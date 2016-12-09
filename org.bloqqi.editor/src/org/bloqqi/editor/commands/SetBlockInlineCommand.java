package org.bloqqi.editor.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.bloqqi.compiler.ast.ASTNode;
import org.bloqqi.compiler.ast.Block;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.InheritedBlock;
import org.bloqqi.compiler.ast.Node;
import org.bloqqi.editor.Coordinates;
import org.bloqqi.editor.autolayout.AutoLayoutKlay;

public class SetBlockInlineCommand extends Command {
	private final static int RELATIVE_X_OFFSET = -50;
	private final static int RELATIVE_Y_OFFSET = -30;
	
	private final Coordinates coordinates;
	private final DiagramType diagramType;
	private final Block block;
	private final boolean newInlineValue;
	private final boolean oldInlineValue;
	
	public SetBlockInlineCommand(Coordinates coordinates, Block block, boolean newInlineValue) {
		this.coordinates = coordinates;
		this.diagramType = block.diagramType();
		this.block = block;
		this.newInlineValue = newInlineValue;
		this.oldInlineValue = block.getModifiers().isInline();
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
		block.getModifiers().setModifier("inline", newInlineValue);
		block.program().flushAllAttributes();
		if (newInlineValue) {
			// Compute the coordinates after the change
			autoLayoutInlinedBlocks();
		}
		block.diagramType().notifyObservers();
	}

	private void autoLayoutInlinedBlocks() {
		// Compute auto layout for the inlined blocks and adjust them
		// according to the position of the blocks that is inlined.
		Rectangle blockRectangle = coordinates.getRectangle(diagramType, block.accessString());
		DiagramType dt = (DiagramType) block.type();
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
			String key = block.accessString() + ASTNode.INLINE_SEP + name;
			
			r.x += blockRectangle.x + RELATIVE_X_OFFSET;
			r.y += blockRectangle.y + RELATIVE_Y_OFFSET;
			
			adjustedLayout.put(key, r);
		}

		// Update coordinates of the inlined blocks
		for (InheritedBlock b: diagramType.getBlocks()) {
			if (b.isInlined()
					&& b.inlinedBlock() == block
					&& coordinates.getRectangle(diagramType, b.accessString()) == Coordinates.RECTANGLE_NOT_FOUND) {
				Rectangle r = adjustedLayout.get(b.accessString());
				if (r == null) r = Coordinates.RECTANGLE_NOT_FOUND;
				coordinates.setRectangle(
						diagramType,
						b.accessString(),
						r);
			}
		}
	}

	private void autoLayoutCollapsedBlock() {
		if (coordinates.getRectangle(diagramType, block.accessString()) == Coordinates.RECTANGLE_NOT_FOUND) {
			Rectangle closestToOrigin = null;
			for (InheritedBlock b: diagramType.getBlocks()) {
				if (b.isInlined() && b.inlinedBlock() == block) {
					Rectangle r = coordinates.getRectangle(diagramType, b.accessString());
					if (closestToOrigin == null || distance(r) < distance(closestToOrigin)) {
						closestToOrigin = r;
					}
				}
			}
			if (closestToOrigin != null) {
				coordinates.setRectangle(diagramType, block.accessString(), closestToOrigin);
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
		block.getModifiers().setModifier("inline", oldInlineValue);
		block.program().flushAllAttributes();
		block.diagramType().notifyObservers();
	}
}
