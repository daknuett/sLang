package slang.interpreter;

import java.util.Stack;

import slang.parser.Function;
import slang.parser.Program;
import slang.parser.Statement;
import slang.parser.Variable;
import slang.parser.statements.Block;
import slang.parser.statements.Return;
import slang.parser.statements.VariableDeclaration;
import slang.parser.statements.expressionstats.Assignment;
import slang.parser.statements.expressionstats.Functioncall;
import slang.parser.statements.expressionstats.UnaryOperatorExpression;
import slang.parser.statements.expressionstats.UnaryOperatorExpression.UnaryOperator;
import slang.parser.statements.parts.ArithmeticExpression;
import slang.parser.statements.parts.Expression;
import slang.parser.statements.parts.Expression.BinaryOperator;
import slang.parser.statements.structures.IfStruct;
import slang.parser.statements.structures.WhileStruct;
import slang.parser.literals.Character;
import slang.parser.literals.Number;


public class ToAssembly
{
	private MemoryHandler mem_handle;
	private Stack<MemoryHandler> handle_stack;
	private boolean DEBUG = true;

	public ToAssembly(int ramsize, int start)
	{
		mem_handle = new MemoryHandler(ramsize, start);
		handle_stack = new Stack<MemoryHandler>();
	}

	public String processStaticAssignment(Assignment a) throws RamFullException
	{
		String res = "";
		if(a.getValue() instanceof Number)
		{
			res += "ldi " + String.format("%x", ((Number) a.getValue()).getValue()) + " r0 ";
		}
		else if(a.getValue() instanceof Character)
		{
			res += "ldi '" + ((Character) a.getValue()).getCharacter().charAt(0) + "' r0";
		}
		res += "\n";
		res += "mov r0 " + String.format("%x", mem_handle.getAddress(a.getTarget().getName()));
		res += "\n";
		return res;
	}

	public String startJumpableBlock(String blockname)
	{
		// inherite the current memory layout from the parent block, save the parent block.
		handle_stack.push(mem_handle);
		mem_handle = MemoryHandler.inheriteFrom(mem_handle);

		// now generate a nice blockname
		String res = "J_S_LFB" + blockname + ":\n";
		return res;
	}

	public String startCallableBlock(String blockname)
	{
		handle_stack.push(mem_handle);
		mem_handle = MemoryHandler.inheriteFrom(mem_handle);
		String res = blockname + ":\n";
		return res;
	}

	public String endJumpableBlock(String blockname)
	{
		mem_handle = handle_stack.pop();
		String res = "J_E_LFB" + blockname + ":\n";
		return res;
	}

	public String endCallableBlock(String blockname, boolean ret_done)
	{
		mem_handle = handle_stack.pop();
		String res = "";
		res += blockname + "_end:\n";
		if(!ret_done)
		{
			res += "ret\n";
		}
		return res;
	}

	public String declareVariable(String name) throws RamFullException
	{
		int addr = mem_handle.registerName(name);
		if(DEBUG)
		{ 
			return "; DECLARE " + name + " at " + String.format("%x", addr) + "\n"; 
			}
		return "";
	}

	public String processAssignment(Assignment a) 
	throws RamFullException, InterpretingError	
	{
		if(a.getValue() instanceof Number || a.getValue() instanceof Character)
		{ 
			return processStaticAssignment(a); 
		}
		// handle the "value" first.
		// maybe we are lucky an it is another variable:
		String res = "";
		if(a.getValue() instanceof Variable)
		{
			res += getFromVariable(((Variable) a.getValue()).getName(), "r0");
			res += moveToVariable(((Variable) a.getTarget()).getName(), "r0");
			return res;
		}
		if(a.getValue() instanceof Functioncall)
		{
			// now we have to call the function first.
			res += processFunctionCall((Functioncall) a.getValue());
			res += "mov RAMEND_LOW r0\n";
			res += moveToVariable(((Variable) a.getTarget()).getName(), "r0");
			return res;
		}
		if(a.getValue() instanceof ArithmeticExpression)
		{
			res += processArithmeticExpression((ArithmeticExpression)a.getValue(),"r0");
			res += moveToVariable(((Variable) a.getTarget()).getName(), "r0");
			return res;
		}
		return "";
	}

	public String processFunctionCall(Functioncall f) 
			throws InterpretingError
	{
		// handle inline assembly first
		if(f.getFunction().getName() == "__ASM__"){ return makeInlineAssembly((Character) f.getParameters()[0]); }

		// move the params to the correct locations
		// organize params above RAMEND_LOW
		Expression[] params = f.getParameters();
		String res = "";
		for(int i = 0; i < params.length; i++)
		{
			// need preassembled arithmethics here
			// params are stored right above RAMEND_LOW.

			// supporting variables as arguments only
			// TODO: add other Expressions,too

			if(params[i] instanceof Variable)
			{
				res += getFromVariable(((Variable) params[i]).getName(), "r0");
				res += "mov r0 [int(\" RAMEND_LOW \",16)+" + String.valueOf(i) + "]\n";
			}
			else if(params[i] instanceof Expression)
			{
				res+=parseExpressionTo(params[i],"r0");
				res+="mov r0 [int(\" RAMEND_LOW \",16)+" + String.valueOf(i) + "]\n";
			}
			else
			{
				assert (true);
			}
		}
		res += "call " + f.getFunction().getName() + "\n";

		return res;
	}

	public String makeInlineAssembly(Character asm)
	{
		return "; APP\n" + asm.getCharacter() + "\n; NOAPP\n";
	}

	/**
	 * @param name_to
	 *            has to be a registername, like "r0"
	 * */
	public String getFromVariable(String name_from, String name_to)
	{
		String res = "";
		try
		{
			res += "mov " + String.format("%x", mem_handle.getAddress(name_from)) + " " + name_to + "\n";
		} catch(RamFullException e)
		{
			System.err.println("Something bad happenden, while moving from variable.");
			System.exit(1);
		}
		return res;
	}

	public String moveToVariable(String name_to, String name_from)
	{
		String res = "";
		try
		{
			res += "mov " + name_from + " " + String.format("%x", mem_handle.getAddress(name_to)) + "\n";
		} catch(RamFullException e)
		{
			System.err.println("Something bad happenden, while moving to variable.\n(Is the variable declared?)");
			System.exit(1);
		}
		return res;
	}

	/**
	 * @param store define the Register to store the result
	 * @throws InterpretingError 
	 * */
	public String processArithmeticExpression(ArithmeticExpression e,String store) 
	throws InterpretingError
	{
		String res = "";
		Expression current=e;
		// load first part
		if(!(((ArithmeticExpression)current).getFirstExpression() instanceof Number)&&( !( ((ArithmeticExpression)current).getFirstExpression()instanceof Variable))&& !(((ArithmeticExpression)current).getFirstExpression() instanceof Functioncall))
		{
			throw new InterpretingError("cannot interpret Expression "+current+" of type "+current.getClass()+" as value");
		}
		Expression working=((ArithmeticExpression)current).getFirstExpression();
		if(working instanceof Variable)
		{
			res+=getFromVariable(((Variable)working).getName(),store);
		}
		if(working instanceof Number)
		{
			res+="ldi "+String.format("%x", ((Number)working).getValue())+" "+store+"\n";
		}
		if(working instanceof Functioncall)
		{
			res+=processFunctionCall(((Functioncall)working));
			res+="mov RAMEND_LOW "+store+ "\n";
		
		}
		System.out.println("first exp done: "+res);
		Expression next=e.getSecondExpression();
		boolean not_done=true;
		while(not_done)
		{
			// handle easiest case: second expression is just a call or value
			if(next instanceof Variable)
			{
				res+=getFromVariable(((Variable)next).getName(),"r1");
				not_done=false;
			}
			if(next instanceof Number)
			{
				res+="ldi "+String.format("%x", ((Number)next).getValue())+" r1\n";
				not_done=false;
			}
			if(next instanceof Functioncall)
			{
				res+=processFunctionCall(((Functioncall)next));
				res+="mov RAMEND_LOW r1\n";
				not_done=false;
				
			}
			// now we are not yet finished
			if(not_done)
			{
				working=((ArithmeticExpression)next).getFirstExpression();
				if(working instanceof Variable)
				{
					res+=getFromVariable(((Variable)working).getName(),"r1");
				}
				if(working instanceof Number)
				{
					res+="ldi "+String.format("%x", ((Number)working).getValue())+" r1\n";
				}
				if(working instanceof Functioncall)
				{
					res+=processFunctionCall(((Functioncall)working));
					res+="mov RAMEND_LOW r1\n";				
				}
				next=((ArithmeticExpression)next).getSecondExpression();
				current=((ArithmeticExpression)current).getSecondExpression();
			}
			BinaryOperator op=((ArithmeticExpression)current).getOperation();
			// std ops
			if(op==BinaryOperator.MINUS)
			{
				res+="sub "+store+" r1\n";
			}
			if(op==BinaryOperator.PLUS)
			{
				res+="add "+store+" r1\n";
			}if(op==BinaryOperator.MULTIPLY)
			{
				res+="mul "+store+" r1\n";
			}
			if(op==BinaryOperator.DIVIDE)
			{
				res+="div "+store+" r1\n";
			}
			// will be compared by 0:
			// a < b => ( a - b ) < 0
			if(op==BinaryOperator.GREATER)
			{
				res+="sub "+store+" r1\n";
			}
			if(op==BinaryOperator.LESS)
			{
				res+="sub "+store+" r1\n";
			}
			if(op==BinaryOperator.EQUALS)
			{
				res+="sub "+store+" r1\n";
			}
			// logic operations
			if(op==BinaryOperator.AND)
			{
				res+="and "+store+" r1\n";
			}
			if(op==BinaryOperator.OR)
			{
				res+="or "+store+" r1\n";
			}
			if(op==BinaryOperator.XOR)
			{
				res+="xor "+store+" r1\n";
			}
			
			res+="mov r1 "+store+"\n";
		}
		return res;
	}

	public String processBlock(Block b) 
			throws RamFullException, InterpretingError
	{
		String res = "";
		res += startJumpableBlock(String.valueOf(b.getBlockNumber()));
		Statement b_statements[] = b.getStatements();

		for(int i = 0; i < b_statements.length; i++)
		{
			// some output to detect something
			//System.out.println(b_statements[i]);
			//System.out.println(b_statements[i].getClass());
			if(b_statements[i] instanceof VariableDeclaration)
			{
				res += declareVariable(((VariableDeclaration) b_statements[i]).getName());
			}
			if(b_statements[i] instanceof Assignment)
			{
				res += processAssignment((Assignment) b_statements[i]);
			}
			if(b_statements[i] instanceof Functioncall)
			{
				res += processFunctionCall((Functioncall) b_statements[i]);
			}
			if(b_statements[i] instanceof Block)
			{
				res += processBlock((Block) b_statements[i]);
			}
			if(b_statements[i] instanceof IfStruct)
			{
				res += processIfStruct((IfStruct) b_statements[i]);
			}
			if(b_statements[i] instanceof WhileStruct)
			{
				res += processWhileStruct((WhileStruct) b_statements[i]);
			}
			if(b_statements[i] instanceof Return)
			{
				res+=parseExpressionTo(((Return)b_statements[i]).getRetValue(),"r0");
				res+="mov r0 RAMEND_LOW\n";
				res+="ret\n";
			}
		}
		res += endJumpableBlock(String.valueOf(b.getBlockNumber()));

		return res;
	}
	public String processBlock(Block b,int skip) 
			throws RamFullException, InterpretingError
	{
		String res = "";
		res += startJumpableBlock(String.valueOf(b.getBlockNumber()));
		Statement b_statements[] = b.getStatements();

		for(int i = skip; i < b_statements.length; i++)
		{
			// some output to detect something
						//System.out.println(b_statements[i]);
						//System.out.println(b_statements[i].getClass());
			if(b_statements[i] instanceof VariableDeclaration)
			{
				res += declareVariable(((VariableDeclaration) b_statements[i]).getName());
			}
			if(b_statements[i] instanceof Assignment)
			{
				res += processAssignment((Assignment) b_statements[i]);
			}
			if(b_statements[i] instanceof Functioncall)
			{
				res += processFunctionCall((Functioncall) b_statements[i]);
			}
			if(b_statements[i] instanceof Block)
			{
				res += processBlock((Block) b_statements[i]);
			}
			if(b_statements[i] instanceof IfStruct)
			{
				res += processIfStruct((IfStruct) b_statements[i]);
			}
			if(b_statements[i] instanceof WhileStruct)
			{
				res += processWhileStruct((WhileStruct) b_statements[i]);
			}
			if(b_statements[i] instanceof Return)
			{
				res+=parseExpressionTo(((Return)b_statements[i]).getRetValue(),"r0");
				res+="mov r0 RAMEND_LOW\n";
				res+="ret\n";
			}
		}
		res += endJumpableBlock(String.valueOf(b.getBlockNumber()));

		return res;
	}
	public String processBlock(Block b,int skip,String append) 
			throws RamFullException, InterpretingError
	{
		String res = "";
		res += startJumpableBlock(String.valueOf(b.getBlockNumber()));
		Statement b_statements[] = b.getStatements();

		for(int i = skip; i < b_statements.length; i++)
		{
			if(b_statements[i] instanceof VariableDeclaration)
			{
				res += declareVariable(((VariableDeclaration) b_statements[i]).getName());
			}
			if(b_statements[i] instanceof Assignment)
			{
				res += processAssignment((Assignment) b_statements[i]);
			}
			if(b_statements[i] instanceof Functioncall)
			{
				res += processFunctionCall((Functioncall) b_statements[i]);
			}
			if(b_statements[i] instanceof Block)
			{
				res += processBlock((Block) b_statements[i]);
			}
			if(b_statements[i] instanceof IfStruct)
			{
				res += processIfStruct((IfStruct) b_statements[i]);
			}
			if(b_statements[i] instanceof WhileStruct)
			{
				res += processWhileStruct((WhileStruct) b_statements[i]);
			}
			if(b_statements[i] instanceof Return)
			{
				res+=parseExpressionTo(((Return)b_statements[i]).getRetValue(),"r0");
				res+="mov r0 RAMEND_LOW\n";
				res+="ret\n";
			}
			if(b_statements[i] instanceof UnaryOperatorExpression)
			{
				res+=getFromVariable(((UnaryOperatorExpression)b_statements[i]).getVariable().getName(),"r0");
				if(((UnaryOperatorExpression)b_statements[i]).getOperator()==UnaryOperator.INCREMENT_POST)
				{
					
					res+="inc r0\n";
				}
				if(((UnaryOperatorExpression)b_statements[i]).getOperator()==UnaryOperator.INCREMENT_PRE)
				{
					
					res+="inc r0\n";
				}
				if(((UnaryOperatorExpression)b_statements[i]).getOperator()==UnaryOperator.DECREMENT_POST)
				{
					
					res+="dec r0\n";
				}if(((UnaryOperatorExpression)b_statements[i]).getOperator()==UnaryOperator.DECREMENT_PRE)
				{
					
					res+="dec r0\n";
				}
				res+=moveToVariable(((UnaryOperatorExpression)b_statements[i]).getVariable().getName(),"r0");
				
			}
		}
		res+=append;
		res += endJumpableBlock(String.valueOf(b.getBlockNumber()));

		return res;
	}

	public String processFunction(Function f) 
			throws RamFullException, InterpretingError
	{
		String res = "";
		res += startCallableBlock(f.getName());
		if(DEBUG)
		{
			res+="; setting up environment\n";
		}
		Statement [] b_statements=f.getBody().getStatements();
		for(int i =0;i<f.getParamCount();i++)
		{
			res+=declareVariable(((VariableDeclaration) b_statements[i]).getName());
			String name=((VariableDeclaration) b_statements[i]).getName();
			res+="mov [int(\" RAMEND_LOW \",16)+"+i+"] "+String.format("%x",mem_handle.getAddress(name))+"\n";
		}
		if(DEBUG)
		{
			res+="; setup done\n";
		}
		res += processBlock(f.getBody(),f.getParamCount());
		res += endCallableBlock(f.getName(), false);
		return res;
	}
	
	/**
	 * @param to should be a register name
	 * @throws InterpretingError 
	 * */
	public String parseExpressionTo(Expression e,String to) 
		throws InterpretingError
	{
		String res="";
		if(e instanceof Number )
		{ 
			res+="ldi "+((Number)e).getValue() +" "+to+"\n" ; 
		}
		if(e instanceof Character )
		{
			res+="ldi '"+((Character)e).getCharacter().charAt(0) +"' "+to+"\n";
		}
		
		// handle the "value" first.
		// maybe we are lucky an it is another variable:
		if(e instanceof Variable)
		{
			res += getFromVariable(((Variable) e).getName(), "r0");
			return res;
		}
		if(e instanceof Functioncall)
		{
			// now we have to call the function first.
			res += processFunctionCall((Functioncall) e);
			res += "mov RAMEND_LOW r0\n";
			return res;
		}
		if(e instanceof ArithmeticExpression)
		{
			res += processArithmeticExpression((ArithmeticExpression)e,"r0");
			return res;
		}
		return res;
	}
	
	public String processWhileStruct(WhileStruct w) 
			throws RamFullException, InterpretingError
	{
		String res ="";
		if(DEBUG)
		{
			res+="; while struct \n";
			res+="; initial check\n";
		}
		res+=parseExpressionTo(w.getCondition(),"r0");
		res+="jne r0 J_S_LFB"+((Block)w.getBody()).getBlockNumber()+"\n";
		res+="jmp J_E_LFB"+((Block)w.getBody()).getBlockNumber()+"\n";
		if(DEBUG)
		{
			res+="; initial check done\n";
		}
		//String append="";
		// TODO: why did I added this variable?
		res+=processBlock((Block)w.getBody(),
				0,
				parseExpressionTo(w.getCondition(),"r0")+
						"jne r0 J_S_LFB"+
						String.valueOf(((Block)w.getBody()).getBlockNumber())+"\n");
		
		if(DEBUG)
		{
			res+="; while done \n";
		}
		return res;
	}
	
	public String processIfStruct(IfStruct i) 
			throws InterpretingError, RamFullException
	{
		String res="";
		if(DEBUG)
		{
			res+="; if\n";
			res+="; checking expression\n";
		}
		res+=parseExpressionTo(i.getCondition(),"r0");
		res+="jne r0 J_S_LFB"+((Block)i.getTrueBody()).getBlockNumber()+"\n";
		res+="jmp J_S_LFB"+((Block)i.getFalseBody()).getBlockNumber()+"\n";
		res+=processBlock((Block)i.getTrueBody());
		res+=processBlock((Block)i.getFalseBody());
		return res;
	}
	
	public String processProgram(Program p) 
			throws RamFullException, InterpretingError
	{
		String res="";
		// head: std includes + prevent bad memory
		// function start has to be provided
		res+="#include<stddef.inc>\n";
		res+="call start\nldi ff SFR\n";
		
		for (Function f: p.getFunctions())
		{
			res+=processFunction(f);
		}
		
		return res;
	}
}
