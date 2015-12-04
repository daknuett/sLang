package slang.parser;

public enum Datatype
{
	INT, CHAR, VOID, PTR;
	
	public static Datatype findDatatype(String name)
	{
		switch(name)
		{
			case "int": return INT;
			case "char": return CHAR;
			case "void": return VOID;
			case "ptr": return PTR;
			default: return null;
		}
	}
}
