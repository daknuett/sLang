package slang;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

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
