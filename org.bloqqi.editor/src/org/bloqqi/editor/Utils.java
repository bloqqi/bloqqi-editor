package org.bloqqi.editor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.bloqqi.compiler.ast.ASTNode;
import org.bloqqi.compiler.ast.Annotation;
import org.bloqqi.compiler.ast.AutoLayoutConfig;
import org.bloqqi.compiler.ast.CompilationUnit;
import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.Node;
import org.bloqqi.compiler.ast.BloqqiParser;
import org.bloqqi.compiler.ast.BloqqiScanner;
import org.bloqqi.compiler.ast.Program;
import org.bloqqi.compiler.ast.SimpleVarUse;
import org.bloqqi.compiler.ast.TypeDecl;
import org.bloqqi.compiler.ast.TypeUse;
import org.bloqqi.compiler.ast.TypedSimpleVarUse;
import org.bloqqi.compiler.ast.VarDecl;
import org.bloqqi.editor.autolayout.AutoLayoutKlay;

public final class Utils {
	private Utils() { }
	
	public static InputStream programToInputStream(Program program, Coordinates coordinates) {
		CompilationUnit cu = program.getCompilationUnit(0);
		for (TypeDecl td: cu.typeDecls()) {
			if (td.isDiagramType()) {
				DiagramType dt = (DiagramType) td;
				Map<String, Rectangle> map = coordinates.getAllRectangles(dt);
				if (!map.isEmpty()) {
					ArrayList<Annotation> annotations = new ArrayList<Annotation>();
					for (Map.Entry<String, Rectangle> e: map.entrySet()) {
						Rectangle r = e.getValue();
						SimpleVarUse u;
						boolean found = false;
						if (e.getKey().contains(ASTNode.DECLARED_IN_SEP)) {
							String split[] = e.getKey().split(ASTNode.DECLARED_IN_SEP);
							u = new TypedSimpleVarUse(new TypeUse(split[0]), split[1]);
							found = dt.inheritedLookup(split[0], split[1]) != null;
						} else {
							u = new SimpleVarUse(e.getKey());
							found = dt.inheritedLookup(e.getKey()) != null;
						}
						if (found) {
							annotations.add(new Annotation(u, r.x, r.y, r.width, r.height));
						}
					}
					dt.setAnnotations(annotations);
				}
			}
		}
		InputStream is = new ByteArrayInputStream(cu.prettyPrint().getBytes());
		return is;
	}

	public static Program readProgram(IEditorInput editorInput, Coordinates coordinates) {
		Program p = null;
		if (editorInput instanceof IFileEditorInput) {
			IFileEditorInput editor = (IFileEditorInput) editorInput;
			try {
				Reader r = new InputStreamReader(editor.getFile().getContents());
				BloqqiScanner scanner = new BloqqiScanner(r);
				BloqqiParser parser = new BloqqiParser();

				CompilationUnit cu = (CompilationUnit) parser.parse(scanner);
				
				p = new Program();
				p.setStandardLibrary(Program.loadStandardLibrary());
				p.addCompilationUnit(cu);

				readAnnotations(p, coordinates);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return p;
	}
	
	public static void readAnnotations(Program program, Coordinates coordinates) {
		CompilationUnit cu = program.getCompilationUnit(0);
		for (TypeDecl td: cu.typeDecls()) {
			if (td.isDiagramType()) {
				DiagramType dt = (DiagramType) td;
				if (dt.getNumAnnotation() > 0) {
					readAnnotations(dt, coordinates);
				}
			}
		}
	}

	private static void readAnnotations(DiagramType dt, Coordinates coordinates) {
		for (Annotation a: dt.getAnnotations()) {
			VarDecl decl = a.getVarUse().decl();
			if (decl instanceof Node) {
				Rectangle r = new Rectangle(a.getX(), a.getY(), a.getWidth(), a.getHeight());
				coordinates.setRectangle(dt, ((Node) decl).accessString(), r);
			}
		}
	}
	
	public static void possibleAutoLayout(DiagramType dt, Coordinates coordinates) {
		if (coordinates.getAllRectangles(dt).isEmpty()) {
			try {
				coordinates.setAllRectangles(dt, autoLayout(dt));
			} catch (IllegalStateException e) {
				e.printStackTrace();
				System.err.println("Could not do auto layout");
			}
		}
	}
	
	public static Map<String, Rectangle> autoLayout(DiagramType dt) {
		return new AutoLayoutKlay(dt).layout();
	}
	
	public static Map<String, Rectangle> autoLayoutKeepDeclarationOrder(DiagramType dt) {
		final int offsetX = 20;
		
		AutoLayoutConfig c = new AutoLayoutConfig();
		c.height = 28;
		c.width = 60;
		c.verticalPadding = 30;
		c.horizontalPadding = 40;
		c.offsetX = offsetX;
		if (dt.getNumInParameter() > 0) {
			c.offsetX += c.width + c.horizontalPadding;
		}
		c.offsetY = 20;
		
		Map<String, Rectangle> map = new HashMap<String, Rectangle>();
		int maxX = 0;
		for (Component comp: dt.getComponents()) {
			int x = comp.autoLayoutX(c);
			int y = comp.autoLayoutY(c);
			if (maxX < x) maxX = x;
			Rectangle r = new Rectangle(x, y, c.width, c.height);
			map.put(comp.accessString(), r);
		}
		
		for (int i = 0; i < dt.getNumInParameter(); i++) {
			int y = c.offsetY + i * (c.height + c.verticalPadding);
			Rectangle r = new Rectangle(offsetX, y, c.width, c.height);
			map.put(dt.getInParameter(i).accessString(), r);
		}
		
		for (int i = 0; i < dt.getNumOutParameter(); i++) {
			int x = maxX + c.width + c.horizontalPadding;
			int y = c.offsetY + i * (c.height + c.verticalPadding);
			Rectangle r = new Rectangle(x, y, c.width, c.height);
			map.put(dt.getOutParameter(i).accessString(), r);
		}

		return map;
	}
	
	public static boolean isOSWindows() {
		return System.getProperty("os.name").startsWith("Windows");
	}
}
