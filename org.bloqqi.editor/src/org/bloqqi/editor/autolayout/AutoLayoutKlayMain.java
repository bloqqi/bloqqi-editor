package org.bloqqi.editor.autolayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.bloqqi.compiler.ast.CompilationUnit;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.BloqqiParser;
import org.bloqqi.compiler.ast.BloqqiScanner;
import org.bloqqi.compiler.ast.Program;

public class AutoLayoutKlayMain {
	private static CompilationUnit parse(Reader reader) throws IOException, beaver.Parser.Exception {
		BloqqiScanner scanner = new BloqqiScanner(reader);
		BloqqiParser parser = new BloqqiParser();
		return (CompilationUnit) parser.parse(scanner);
	}
	
	public static Program parseProgram(String program) {
		Reader reader = new StringReader(program);
		
		PrintStream err = System.err;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);

		try {
			System.setErr(ps);
			CompilationUnit cu = parse(reader);
			if (os.size() > 0) {
				throw new RuntimeException("Parser recovery:\n" + os.toString());
			}
			Program p = new Program();
			p.setStandardLibrary(new CompilationUnit());
			p.addCompilationUnit(cu);
			return p;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage() + "\n" + os.toString());
		} finally {
			System.setErr(err);
		}
	}
	
	public static void main(final String[] args) throws Exception {
		String s = ""
				+ "diagramtype Main(Int in, Int in2 => Int out) {"
				+ "		Block block1;"
				+ "		Block block2;"
				+ "		Block block3;"
				+ "		connect(in, block1.in1);"
				+ "		connect(in, block1.in2);"
				+ "		connect(in2, block2.in1);"
				+ "		connect(in2, block2.in2);"
				+ "		connect(block1.out, block3.in1);"
				+ " 	connect(block2.out, block3.in2);"
				+ "		connect(block3.out, out);"
				+ "}"
				+ "diagramtype Block(Int in1, Int in2 => Int out) { }";
		Program p = parseProgram(s);
		System.out.println(p.getCompilationUnit(0).prettyPrint());
		DiagramType dt = (DiagramType) p.getCompilationUnit(0).typeDecls().get(0);

		AutoLayoutKlay autoLayout = new AutoLayoutKlay(dt);
		for (Map.Entry<String, Rectangle> e: autoLayout.layout().entrySet()) {
			System.out.println(e.getKey() + ": " + e.getValue());
		}
		System.out.println("Execution time: " + autoLayout.getExectionTime() + " ms");
	}
}
