package slang.lexer;

public enum TokenType
{
	FUNCTION(true, "function"),
	IF(true, "if"),
	ELSE(true, "else"),
	WHILE(true, "while"),
	RETURN(true, "return"),
	BLOCK_OPEN(false, "{"),
	BLOCK_CLOSE(false, "}"),
	BRACKET_OPEN(false, "("),
	BRACKET_CLOSE(false, ")"),
	KOMMA(false, ","),
	SEMICOLON(false, ";"),
	APOSTROPHE(false, "'"),
	DATATYPE(true, "int", "char", "ptr"),
	VOID(true, "void"),
	BIN_OPERATOR(false, "+", "-", "*", "/", "&", "^", "|", "<", ">", "=="),
	UN_OPERATOR(false, "++", "--"),
	ASSIGN_OPERATOR(false, "="),
	SHORT_ASSIGN_OPERATOR(false, "+=", "-=", "*=", "/="),
	NUMBER(true, "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"),
	NAME(true),
	WHITESPACE(false, "\n", " ", "\t");

	private String[] represents;
	private boolean isCritical;

	TokenType(boolean isCritical, String... represents)
	{
		this.isCritical = isCritical;
		this.represents = represents;
	}

	public boolean isCritical()
	{
		return isCritical;
	}
	
	public String[] getRepresentations()
	{
		return represents;
	}
}
