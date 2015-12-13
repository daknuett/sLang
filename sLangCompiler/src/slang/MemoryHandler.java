package slang;

import java.util.HashMap;
import java.util.Map;

public class MemoryHandler 
{
	private Map<String,String> mem_map;
	private int mem_max;
	private int current_address;
	public MemoryHandler(int max,int start)
	{
		mem_max=max;
		mem_map=new HashMap<String, String>();
		current_address=start;
	}
	public MemoryHandler(int max,int currentaddress, Map<String,String> m)
	{
		mem_map=new HashMap<String,String>(m);
		mem_max=max;
		current_address=currentaddress;
	}
	
	public int getAddress(String name)
	throws RamFullException
	{
		int result;
		if(! mem_map.containsKey(name))
		{
			result=registerName(name);
		}
		else
		{
			result=Integer.parseInt(mem_map.get(name));
		}
		return result;
	}

	int registerName(String name) 
	throws RamFullException
	{
		/* name not yet registered. 
		 * using block wise handling
		 * */
		mem_map.put(name,String.valueOf(current_address));
		int res=current_address;
		current_address++;
		return res;
	}
	
	/**
	 * @author daniel knuettel
	 * @return Returns a new "inherent" from parent. This allows inherent variables for blocks.
	 * */
	public static MemoryHandler inheriteFrom(MemoryHandler parent)
	{
		return new MemoryHandler(parent.mem_max,parent.current_address,parent.mem_map);
	}
}
