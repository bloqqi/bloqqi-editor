package org.bloqqi.editor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.WeakHashMap;

import org.eclipse.draw2d.geometry.Rectangle;
import org.bloqqi.compiler.ast.ASTNode;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.Node;
import org.bloqqi.editor.figures.ComponentFigure;

public class Coordinates {
	public final static Rectangle RECTANGLE_NOT_FOUND
		= new Rectangle(10, 10, ComponentFigure.WIDTH, ComponentFigure.MIN_HEIGHT);
	
	private WeakHashMap<DiagramType, Map<String, Rectangle>> coordinates;
	
	public Coordinates() {
		coordinates = new WeakHashMap<DiagramType, Map<String, Rectangle>>();
	}

	
	public Rectangle getRectangle(DiagramType dt, String name) {
		Map<String, Rectangle> map = coordinates.get(dt);
		if (map == null) {
			return RECTANGLE_NOT_FOUND;
		}
		Rectangle r = map.get(name);
		return r == null ? RECTANGLE_NOT_FOUND : r.getCopy();
	}

	public Rectangle getRectangleNearestSuperType(DiagramType dt, Node node) {
		for (DiagramType superDt: dt.superTypesLinearizedReversed()) {
			String key = superDt == node.declaredInDiagramType()
				? node.name()
				: node.accessString();
			Rectangle r = getRectangle(superDt, key);
			if (r != RECTANGLE_NOT_FOUND) {
				if (superDt != dt) {
					setRectangle(dt, superDt.name()+ASTNode.DECLARED_IN_SEP+node.name(), r);
				}
				return r;
			}
		}
		return RECTANGLE_NOT_FOUND;
	}
	
	public void setRectangle(DiagramType dt, String name, Rectangle r) {
		Map<String, Rectangle> map = coordinates.get(dt);
		if (map == null) {
			map = new HashMap<String, Rectangle>();
			coordinates.put(dt, map);
		}
		map.put(name, r.getCopy());
	}
	
	public SortedMap<String, Rectangle> getAllRectangles(DiagramType dt) {
		Map<String, Rectangle> map = coordinates.get(dt);
		SortedMap<String, Rectangle> newMap = new TreeMap<String, Rectangle>();
		if (map != null) {
			for (Map.Entry<String, Rectangle> e: map.entrySet()) {
				newMap.put(e.getKey(), e.getValue().getCopy());
			}
		}
		return newMap;
	}
	
	public void setAllRectangles(DiagramType dt, Map<String, Rectangle> map) {
		coordinates.put(dt, map);
	}

	public boolean removeAllRectangles(DiagramType dt) {
		return coordinates.remove(dt) != null;
	}
	
	public boolean removeRectangle(DiagramType dt, String name) {
		Map<String, Rectangle> map = coordinates.get(dt);
		if (map != null) {
			return map.remove(name) != null;
		}
		return false;
	}

	public Set<DiagramType> getDiagramTypes() {
		return coordinates.keySet();
	}

	public void printAll() {
		for (Map.Entry<DiagramType, Map<String, Rectangle>> e: coordinates.entrySet()) {
			System.out.println(e.getKey().name() + ": " + e.getValue());
		}
	}
}
