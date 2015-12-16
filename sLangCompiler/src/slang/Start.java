package slang;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

import slang.interpreter.ToAssembly;
import slang.lexer.Lexer;
import slang.lexer.Token;
import slang.parser.Program;
import slang.parser.exceptions.SyntaxErrorException;


public class Start
{	
	public static void main(String... args)
	{
		String program = readFile();
		Lexer lexer = new Lexer(program);
		LinkedList<Token> tokens = lexer.lex();
		try
		{
			Program p = Program.build(tokens.listIterator());
			System.out.println("\n\n" + p);
			//Function f =  p.getFunctions()[0];
			//System.out.println(f.getBody().toString());
			ToAssembly t = new ToAssembly(1000,30);
			System.out.println("\n\n\n");
			try
			{
				System.out.println(t.processProgram(p));
			}
			catch(Exception e)
			{
				System.err.println(e);
				e.printStackTrace();
			}
			
		} catch(SyntaxErrorException e)
		{
			e.printStackTrace();
			System.err.println(e.getMessage() + " at " + e.getPos());
		}
	}

	private static String readFile()
	{
		StringBuilder program = new StringBuilder();

		try(BufferedReader reader = Files.newBufferedReader(Paths.get(Start.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.resolve("samples" + System.getProperty("file.separator") + "sampleRaw.sLang")))
		{
			String line = reader.readLine();
			while(line != null)
			{
				program.append(line);
				program.append('\n');
				line = reader.readLine();
			}
		} catch(IOException | URISyntaxException e)
		{
			e.printStackTrace();
		}

		return program.toString();
	}
}
