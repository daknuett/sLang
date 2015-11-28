/* startfunction */
#define __ASM__NOP  __ASM__('NOP')
#define __local
function void start()
{
	__local int f=45;
	__local int b=foo(f);
	__local char chr='c';
	__ASM__NOP ;
	HW_PR(b);
	halt();
	return;
}

function int foo(int arg1)
{
	int bar=10;
	if(arg1>5)
	{
		bar += arg1;
	}
	else
	{
		bar -= arg1;
	}
	int i=0;
	while(i<(bar/2))
	{
		bar += i;
		i++;
	}
	return bar;
}


function void HW_PR(int arg)
{
	/* inline assembler muss wegen hardware access sein */
	__ASM__('mov arg r0');
	__ASM__('ldi 04 SFR');
	return;
}

function void halt()
{
	__ASM__('ldi ff SFR');
	return;
}
