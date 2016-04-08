package org.bloqqi.editor.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.bloqqi.compiler.ast.ASTNode;
import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.InheritedComponent;
import org.bloqqi.compiler.ast.Node;
import org.bloqqi.editor.Coordinates;
import org.bloqqi.editor.autolayout.AutoLayoutKlay;

public class SetComponentInlineCommand extends Command {
	private final static int RELATIVE_X_OFFSET = -50;
	private final static int RELATIVE_Y_OFFSET = -30;
	
	private final Coordinates coordinates;
	private final DiagramType diagramType;
	private final Component component;
	private final boolean newInlineValue;
	private final boolean oldInlineValue;
	
	public SetComponentInlineCommand(Coordinates coordinates, Component component, boolean newInlineValue) {
		this.coordinates = coordinates;
		this.diagramType = component.diagramType();
		this.component = component;
		this.newInlineValue = newInlineValue;
		this.oldInlineValue = component.getInline();
	}
	
	@Override
	public boolean canExecute() {
		return true;
	}

	@Override
	public void execute() {
		if (!newInlineValue) {
			// Compute the coordinate before the change
			autoLayoutCollapsedBlock();
		}
		component.setInline(newInlineValue);
		component.program().flushAllAttributes();
		if (newInlineValue) {
			// Compute the coordinates after the change
			autoLayoutInlinedBlocks();
		}
		component.diagramType().notifyObservers();
	}

	private void autoLayoutInlinedBlocks() {
		// Compute auto layout for the inlined components and adjust them
		// according to the position of the component that is inlined.
		Rectangle componentRectangle = coordinates.getRectangle(diagramType, component.accessString());
		DiagramType dt = (DiagramType) component.type();
		Map<Node, Rectangle> layout = new AutoLayoutKlay(dt, false).layoutAsNodeMap();
		Map<String, Rectangle> adjustedLayout = new HashMap<>();
		for (Map.Entry<Node, Rectangle> e: layout.entrySet()) {
			Node n = e.getKey();
			Rectangle r = e.getValue();
			
			// TODO: Handle this more properly
			String name = n.accessString();
			if (name.contains(ASTNode.DECLARED_IN_SEP)) {
				name = name.substring(name.indexOf(ASTNode.DECLARED_IN_SEP)+ASTNode.DECLARED_IN_SEP.length());
			}
			String key = component.accessString() + ASTNode.INLINE_SEP + name;
			
			r.x += componentRectangle.x + RELATIVE_X_OFFSET;
			r.y += componentRectangle.y + RELATIVE_Y_OFFSET;
			
			adjustedLayout.put(key, r);
		}

		// Update coordinates of the inlined components
		for (InheritedComponent c: diagramType.getComponents()) {
			if (c.isInlined()
					&& c.inlinedComponent() == component
					&& coordinates.getRectangle(diagramType, c.accessString()) == Coordinates.RECTANGLE_NOT_FOUND) {
				Rectangle r = adjustedLayout.get(c.accessString());
				if (r == null) r = Coordinates.RECTANGLE_NOT_FOUND;
				coordinates.setRectangle(
						diagramType,
						c.accessString(),
						r);
			}
		}
	}

	private void autoLayoutCollapsedBlock() {
		if (coordinates.getRectangle(diagramType, component.accessString()) == Coordinates.RECTANGLE_NOT_FOUND) {
			Rectangle closestToOrigin = null;
			for (InheritedComponent c: diagramType.getComponents()) {
				if (c.isInlined() && c.inlinedComponent() == component) {
					Rectangle r = coordinates.getRectangle(diagramType, c.accessString());
					if (closestToOrigin == null || distance(r) < distance(closestToOrigin)) {
						closestToOrigin = r;
					}
				}
			}
			if (closestToOrigin != null) {
				coordinates.setRectangle(diagramType, component.accessString(), closestToOrigin);
			}
		}
	}
	
	private int distance(Rectangle r) {
		return (int) Math.round(Math.sqrt(r.x*r.x + r.y*r.y));
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void undo() {
		component.setInline(oldInlineValue);
		component.program().flushAllAttributes();
		component.diagramType().notifyObservers();
	}
}
