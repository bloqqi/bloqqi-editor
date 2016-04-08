package org.bloqqi.editor.autolayout;

import java.util.HashMap;

import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.bloqqi.compiler.ast.Anchor;
import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.ComponentParameter;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.InParameter;
import org.bloqqi.compiler.ast.Node;
import org.bloqqi.compiler.ast.OutParameter;
import org.bloqqi.compiler.ast.Parameter;
import org.bloqqi.editor.editparts.ComponentParameterPart;
import org.bloqqi.editor.figures.ComponentFigure;

import de.cau.cs.kieler.core.alg.IKielerProgressMonitor;
import de.cau.cs.kieler.core.alg.BasicProgressMonitor;
import de.cau.cs.kieler.core.kgraph.KEdge;
import de.cau.cs.kieler.core.kgraph.KLabel;
import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.core.kgraph.KPort;
import de.cau.cs.kieler.kiml.AbstractLayoutProvider;
import de.cau.cs.kieler.kiml.klayoutdata.KShapeLayout;
import de.cau.cs.kieler.kiml.options.Direction;
import de.cau.cs.kieler.kiml.options.LayoutOptions;
import de.cau.cs.kieler.kiml.options.PortConstraints;
import de.cau.cs.kieler.kiml.options.PortSide;
import de.cau.cs.kieler.kiml.util.KimlUtil;
import de.cau.cs.kieler.klay.layered.LayeredLayoutProvider;

public class AutoLayoutKlay {
	private static final int EXTRA_SPACE_LITERAL = 16;
	private static final float SPACING = 36f;
	private final DiagramType diagramType;
	private final boolean includeParameters;

	private final KNode graphNode;
	private final Map<Node, KNode> toKNode;
	private final Map<KNode, Node> toNode;
	private final Map<Anchor, KPort> toKPort;
	private final Map<KPort, Anchor> toAnchor;

	private int executionTime;

	public AutoLayoutKlay(DiagramType diagramType, boolean includeParameters) {
			this.diagramType = diagramType;
			this.includeParameters = includeParameters;
			this.toKNode = new HashMap<>();
			this.toNode = new HashMap<>();
			this.toKPort = new HashMap<>();
			this.toAnchor = new HashMap<>();

			graphNode = createGraph();

			addLayoutOptions();
	}
	public AutoLayoutKlay(DiagramType diagramType) {
		this(diagramType, true);
	}
	
	private KNode createGraph() {
		KNode graphNode = KimlUtil.createInitializedNode();
		addParameters(graphNode);
		addComponents(graphNode);
		addConnections();
		return graphNode;
	}

	private void addParameters(KNode graphNode) {
		if (includeParameters) {
			for (InParameter in: diagramType.inParameters()) {
				createParameterKNode(graphNode, in);
			}
			for (OutParameter out: diagramType.outParameters()) {
				createParameterKNode(graphNode, out);
			}
		}
	}
	
	private void addComponents(KNode graphNode) {
		for (Component c: diagramType.components()) {
			KNode node = createKNode(graphNode, c);
			for (ComponentParameter par: c.getInParameterList()) {
				KPort port = KimlUtil.createInitializedPort();
				port.setNode(node);
				toKPort.put(par, port);
				toAnchor.put(port, par);
			}
			for (ComponentParameter par: c.getOutParameterList()) {
				KPort port = KimlUtil.createInitializedPort();
				port.setNode(node);
				toKPort.put(par, port);
				toAnchor.put(port, par);
			}
		}
	}
	
	private void addConnections() {
		for (Connection c: diagramType.connections()) {
			KEdge edge = KimlUtil.createInitializedEdge();

			if (includeParameters || (!c.getSource().node().isParameter() && !c.getTarget().node().isParameter())) {
				KNode source = toKNode.get(c.getSource().node());
				KNode target = toKNode.get(c.getTarget().node());
				KPort sourcePort = toKPort.get(c.getSource().anchor());
				KPort targetPort = toKPort.get(c.getTarget().anchor());

				if (source != null && target != null && sourcePort != null && targetPort != null) {
					edge.setSource(source);
					edge.setTarget(target);
					edge.setSourcePort(sourcePort);
					edge.setTargetPort(targetPort);
					sourcePort.getEdges().add(edge);
					targetPort.getEdges().add(edge);
				}
			}
		}
	}

	private KNode createParameterKNode(KNode graphNode, Parameter parameter) {
		KNode node = createKNode(graphNode, parameter);
		KPort port = KimlUtil.createInitializedPort();
		port.setNode(node);
		toKPort.put(parameter, port);
		toAnchor.put(port, parameter);
		return node;
	}

	private KNode createKNode(KNode graphNode, Node node) {
		KNode knode = KimlUtil.createInitializedNode();
		knode.setParent(graphNode);
		KLabel nodeLabel1 = KimlUtil.createInitializedLabel(knode);
		nodeLabel1.setText(node.name());
		toKNode.put(node, knode);
		toNode.put(knode, node);
		return knode;
	}
	
	private void addLayoutOptions() {
		KShapeLayout layout = graphNode.getData(KShapeLayout.class);
		layout.setProperty(LayoutOptions.DIRECTION, Direction.RIGHT);
		layout.setProperty(LayoutOptions.SPACING, SPACING);

		for (KNode knode : graphNode.getChildren()) {
			Node node = toNode.get(knode);
			
			KShapeLayout nodeLayout = knode.getData(KShapeLayout.class);
//			final int width = node.isInlined() ? ComponentFigure.WIDTH+30 : ComponentFigure.WIDTH;
			int width = ComponentFigure.WIDTH;
			if (node.hasIncomingLiterals()) {
				width += EXTRA_SPACE_LITERAL;
			}

			final int height = ComponentParameterPart.computeNodeMinHeight(node);
			nodeLayout.setWidth(width);
			nodeLayout.setHeight(height);

			// set fixed size for the child
			//           childLayout.setProperty(LayoutOptions.FIXED_SIZE, Boolean.TRUE);
			// set port constraints to fixed port positions
			nodeLayout.setProperty(LayoutOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_POS);
			for (KPort port : knode.getPorts()) {
				KShapeLayout portLayout = port.getData(KShapeLayout.class);
				Anchor anchor = toAnchor.get(port);

				if (node.isInParameter()) {
					portLayout.setXpos(width);
					portLayout.setYpos(height/2);
					portLayout.setProperty(LayoutOptions.PORT_SIDE, PortSide.EAST);
				} else if (node.isOutParameter()) {
					portLayout.setXpos(0.0f);
					portLayout.setYpos(height/2);
					portLayout.setProperty(LayoutOptions.PORT_SIDE, PortSide.WEST);
				} else if (node.isComponent()) {
					ComponentParameter compPar = (ComponentParameter) anchor;
					if (anchor.isInParameter()) {
						portLayout.setXpos(0.0f);
						portLayout.setProperty(LayoutOptions.PORT_SIDE, PortSide.WEST);
					} else {
						portLayout.setXpos(width);
						portLayout.setProperty(LayoutOptions.PORT_SIDE, PortSide.EAST);
					}
					portLayout.setYpos(ComponentParameterPart.getMidYPos(compPar));
				}
			}
		}
	}
	
	/**
	 * Do auto layout and return the result
	 * @return
	 */
	public Map<String, Rectangle> layout() {
		Map<String, Rectangle> coordinates = new HashMap<>();
		for (Map.Entry<Node, Rectangle> e: layoutAsNodeMap().entrySet()) {
			coordinates.put(e.getKey().accessString(), e.getValue());
		}
		return coordinates;
	}
	
	public Map<Node, Rectangle> layoutAsNodeMap() {
		IKielerProgressMonitor progressMonitor = new BasicProgressMonitor();
		AbstractLayoutProvider layoutProvider = new LayeredLayoutProvider();
		layoutProvider.doLayout(graphNode, progressMonitor);

		executionTime = (int) Math.round(progressMonitor.getExecutionTime() * 1000);

		Map<Node, Rectangle> coordinates = new HashMap<>();
		for (KNode knode : graphNode.getChildren()) {
			KShapeLayout layout = knode.getData(KShapeLayout.class);
			int x = (int) Math.round(layout.getXpos());
			int y = (int) Math.round(layout.getYpos());
			int width = (int) Math.round(layout.getWidth());
			int height = (int) Math.round(layout.getHeight());
			if (toNode.get(knode).hasIncomingLiterals()) {
				x += EXTRA_SPACE_LITERAL;
				width -= EXTRA_SPACE_LITERAL;
			}
			Rectangle r = new Rectangle(x, y, width, height);
			coordinates.put(toNode.get(knode), r);
		}
		return coordinates;
	}


	/**
	 * Returns the execution time of the layout algorithm in milliseconds
	 * @return
	 */
	public int getExectionTime() {
		return executionTime;
	}
}
