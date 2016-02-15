package slang;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

import slang.interpreter.ToAssembly;
import slang.lexer.Lexer;
import slang.lexer.Token;
import slang.parser.Program;
import slang.parser.exceptions.SyntaxErrorException;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String... args)
	{
		if(args.length < 2)
		{
			System.err.println("FATAL: need input & outputfile\n\nusage: sLangCompiler <input_file> <output_file> [options]\n");
			System.exit(1);
		}
		String program = readFile(args[0]);
		Lexer lexer = new Lexer(program);
		LinkedList<Token> tokens = lexer.lex();
		try
		{
			Program p = Program.build(tokens.listIterator());
			//System.out.println("\n\n" + p);
			//Function f =  p.getFunctions()[0];
			//System.out.println(f.getBody().toString());
			ToAssembly t = new ToAssembly(1000,30);
			System.out.println("\n\n\n");
			try
			{
				//System.out.println(t.processProgram(p));
				BufferedWriter reader = Files.newBufferedWriter(Paths.get(args[1]));
				reader.write(t.processProgram(p));
				reader.close();
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

	private static String readFile(String filename)
	{
		StringBuilder program = new StringBuilder();

		try(BufferedReader reader = Files.newBufferedReader(Paths.get(filename)))
		{
			String line = reader.readLine();
			while(line != null)
			{
				program.append(line);
				program.append('\n');
				line = reader.readLine();
			}
		} catch(IOException e)
		{
			e.printStackTrace();
		}

		return program.toString();
	}

}
