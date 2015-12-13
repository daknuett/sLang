function void start()
{
	int f=45;
	int b=foo(f);
	char chr='c';
	__ASM__('nop');
	HW_PR(b);
	halt();
	return;
}

function int foo(int arg1)
{
	int bar=10;
	if(arg1>5)
	{
		bar = arg1 + bar;
	}
	else
	{
		bar = bar - arg1 ;
	}
	int i=0;
	while(i<(bar/2))
	{
		bar = bar + i;
		i++;
	}
	return bar;
}


function void HW_PR(int arg)
{
	__ASM__('mov arg r0');
	__ASM__('ldi 04 SFR');
	return;
}

function void halt()
{
	__ASM__('ldi ff SFR');
	return;
}
