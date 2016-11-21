package org.bloqqi.editor.commands;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import java.util.HashSet;
import java.util.Set;

import org.bloqqi.compiler.ast.ASTNode;
import org.bloqqi.compiler.ast.Block;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.IdName;
import org.bloqqi.compiler.ast.Pair;
import org.bloqqi.compiler.ast.Program;
import org.bloqqi.compiler.ast.VarUse;
import org.bloqqi.editor.Coordinates;
import org.bloqqi.editor.editparts.PortPart;
import org.bloqqi.editor.figures.BlockFigure;
import org.bloqqi.editor.wizards.specialize.PageParameters.NewInParameter;

public class CreateBlockCommand extends Command {
	private final Point location;
	private Block block;
	private final DiagramType diagramType;
	private final Coordinates coordinates;
	private Set<NewInParameter> newInParameters;

	private boolean hasExecuted;
	
	public CreateBlockCommand(Point location, Block block, DiagramType diagramType, Coordinates coordinates) {
		this.location = location;
		this.block = block;
		this.diagramType = diagramType;
		this.coordinates = coordinates;
		setNewInParameters(new HashSet<>());
		hasExecuted = false;
	}
	
	public void execute() {
		if (block.getName() == null
				|| !Program.isIdValid(block.name())
				|| diagramType.lookup(block.name()) != null) {
			setSimpleName(computeNewName());
		}
		diagramType.addLocalBlock(block);
		diagramType.program().flushAllAttributes();

		if (!hasExecuted) {
			// Some parameters of nested blocks should be exposed as parameters.
			// This is set by the user in the specialization wizard.
			for (NewInParameter in: newInParameters) {
				String parameter = block.name() + "." + in.getPath();
				Pair<Block, VarUse> p = diagramType.addConnectionsParameters(parameter, in.getNewName());
				block = p.first;
			}
		}
		
		diagramType.program().flushAllAttributes();
		coordinates.setRectangle(diagramType, block.accessString(), createRectangle());
		diagramType.notifyObservers();
		
		hasExecuted = true;
	}

	private Rectangle createRectangle() {
		int ports = Math.max(block.getNumInPort(), block.getNumOutPort());
		int height = ports * (PortPart.SIZE + PortPart.PADDING);
		Dimension dim = new Dimension(BlockFigure.WIDTH, Math.max(BlockFigure.MIN_HEIGHT, height));
		return new Rectangle(location, dim);
	}
	
	public void setName(String name) {
		block.setName(new IdName(name));
		block.getModifiers().removeModifier(ASTNode.MODIFIER_SIMPLE);
	}
	
	public void setSimpleName(String name) {
		block.setName(new IdName(name));
		block.getModifiers().addModifier(ASTNode.MODIFIER_SIMPLE);
	}
	
	public String computeNewName() {
		int i = 0;
		String name;
		do {
			i++;
			name = block.getType().name() + "_" + i;
		} while(diagramType.lookup(name) != null);
		return name;
	}

	@Override
	public boolean canUndo() {
		return true;
	}
	
	public void undo() {
		diagramType.getLocalBlockList().removeChild(block);
		diagramType.program().flushAllAttributes();
		diagramType.notifyObservers();
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public void setNewInParameters(Set<NewInParameter> newInParameters) {
		this.newInParameters = newInParameters;
	}
}
