package slang.lexer;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.SortedSet;
import java.util.TreeSet;


public class Lexer
{
	public static final char FILLER = (char) 0;

	private SortedSet<Token> tokens = new TreeSet<Token>();
	private StringBuilder program;

	public Lexer(String program)
	{
		this.program = new StringBuilder(program);
	}

	public LinkedList<Token> lex()
	{
		System.out.println(program);

		filterToken(TokenType.FUNCTION);
		filterToken(TokenType.IF);
		filterToken(TokenType.ELSE);
		filterToken(TokenType.WHILE);
		filterToken(TokenType.DATATYPE);
		filterToken(TokenType.RETURN);
		filterToken(TokenType.VOID);
		filterToken(TokenType.APOSTROPHE);
		filterToken(TokenType.ASSIGN_OPERATOR);
		filterToken(TokenType.BIN_OPERATOR);
		filterToken(TokenType.BLOCK_CLOSE);
		filterToken(TokenType.BLOCK_OPEN);
		filterToken(TokenType.BRACKET_CLOSE);
		filterToken(TokenType.BRACKET_OPEN);
		filterToken(TokenType.KOMMA);
		filterToken(TokenType.SEMICOLON);
		filterToken(TokenType.SHORT_ASSIGN_OPERATOR);
		filterToken(TokenType.UN_OPERATOR);
		filterToken(TokenType.NUMBER);

		filterWhitespaces();
		filterNames();

		System.out.println(program + "\n\n");

		for(Token t : tokens)
		{
			System.out.println(t);
		}

		
		System.out.println("\n\n");
		
		LinkedList<Token> tmp = mergeTokenParts();
		
		for(Token t : tmp)
		{
			System.out.println(t);
		}
		
		return tmp;
	}
	
	private LinkedList<Token> mergeTokenParts()
	{
		LinkedList<Token> tokens = new LinkedList<Token>(this.tokens);
		
		Token current;
		Token last = new Token("", TokenType.APOSTROPHE, -1);	//just some irrelevant type
		ListIterator<Token> iterator = tokens.listIterator();
		while(iterator.hasNext())
		{
			current = iterator.next();
			if(current.getType().isCritical() && last.getType().isCritical())
			{
				current = replace(last, current, iterator, current.getType() == TokenType.NUMBER && last.getType() == TokenType.NUMBER ? TokenType.NUMBER : TokenType.NAME);
			}			
			last = current;
		}
		
		return tokens;
	}
	
	private Token replace(Token first, Token second, ListIterator<Token> iterator, TokenType type)
	{
		iterator.previous();
		iterator.previous();
		iterator.remove();
		iterator.next();
		Token newToken = new Token(first.getRepresentation() + second.getRepresentation(), type, first.getPos());
		iterator.set(newToken);
		return newToken;
	}

	private void filterNames()
	{
		int first = -1;
		for(int i = 0; i < program.length(); i++)
		{
			char character = program.charAt(i);
			if(character != FILLER)
			{
				if(first == -1)
					first = i;
			}
			else
			{
				if(first != -1)
				{
					tokens.add(new Token(program.substring(first, i), TokenType.NAME, first));
					program.replace(first, i, getFiller(i - first));
					first = -1;
				}
			}
		}
	}

	private void filterWhitespaces()
	{
		for(int i = 0; i < program.length(); i++)
		{
			char character = program.charAt(i);
			if(Character.isWhitespace(character))
			{
				tokens.add(new Token(Character.toString(character), TokenType.WHITESPACE, i));
				program.replace(i, i + 1, getFiller(1));
			}
		}
	}

	private void filterToken(TokenType token)
	{
		String[] reps = token.getRepresentations();

		for(String rep : reps)
		{
			filterString(rep, token);
		}
	}

	private void filterString(String rep, TokenType type)
	{
		int currentIndex = 0;

		while(currentIndex < program.length())
		{
			currentIndex = program.indexOf(rep, currentIndex);
			if(currentIndex == -1) break;

			tokens.add(new Token(rep, type, currentIndex));

			program.replace(currentIndex, currentIndex + rep.length(), getFiller(rep.length()));
		}
	}

	private static String getFiller(int length)
	{
		StringBuilder s = new StringBuilder();
		for(int i = 0; i < length; i++)
			s.append(FILLER);
		return s.toString();
	}
}
