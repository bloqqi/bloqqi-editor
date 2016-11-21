package org.bloqqi.editor.autolayout;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.bloqqi.compiler.ast.Block;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.Parameter;
import org.bloqqi.editor.Coordinates;
import org.bloqqi.editor.editparts.ComponentParameterPart;
import org.bloqqi.editor.figures.BlockFigure;

public class AutoLayoutDraw2D {
	public static void layout(DiagramType dt, Coordinates coordinates) {
		DirectedGraph graph = new DirectedGraph();
		graph.setDirection(PositionConstants.EAST);
		graph.setDefaultPadding(new Insets(10, 10, 10, 10));
		fromModelToGraph(dt, graph);
		new DirectedGraphLayout().visit(graph);
		updateModel(dt, graph, coordinates);
	}

	private static void updateModel(DiagramType dt, DirectedGraph graph, Coordinates coordinates) {
		for (int i = 0; i < graph.nodes.size(); i++) {
			Node n = graph.nodes.getNode(i);
			org.bloqqi.compiler.ast.Node astNode = (org.bloqqi.compiler.ast.Node) n.data;
			Rectangle r = new Rectangle(n.x, n.y, n.width, n.height);
			coordinates.setRectangle(dt, astNode.accessString(), r);
		}
	}

	@SuppressWarnings("unchecked")
	private static void fromModelToGraph(DiagramType dt, DirectedGraph graph) {
		Map<org.bloqqi.compiler.ast.Node, Node> map = new HashMap<org.bloqqi.compiler.ast.Node, Node>();
		for (Block b: dt.getBlocks()) {
			createGraphNode(graph, map, b);
		}
		Collections.sort(graph.nodes, new Comparator<Node>() {
			public int compare(Node n1, Node n2) {
				return ((Block) n1.data).dfo() - ((Block) n2.data).dfo();
			}
		});
		for (Parameter p: dt.getInParameters()) {
			createGraphNode(graph, map, p);
		}
		for (Parameter p: dt.getOutParameters()) {
			createGraphNode(graph, map, p);
		}
		
		for (Connection c: dt.getConnections()) {
			if (c.getSource().node() != null
					&& c.getTarget().node() != null) {
				Node source = map.get(c.getSource().node());
				Node target = map.get(c.getTarget().node());
				assert(source != null);
				assert(target != null); 
				graph.edges.add(new Edge(source, target));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void createGraphNode(DirectedGraph graph,
			Map<org.bloqqi.compiler.ast.Node, Node> map, org.bloqqi.compiler.ast.Node astNode) {
		Node n = new Node();
		n.data = astNode;
		n.height = ComponentParameterPart.computeNodeMinHeight(astNode);
		n.width = BlockFigure.WIDTH;
		graph.nodes.add(n);
		map.put(astNode, n);
	}
}
