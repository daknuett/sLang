function void start()
{
	int f=45;
	char chr='c';
	int b;
	__ASM__('nop');
	HW_PR(b);
	f=40;
	foo(f);
	halt();
	return;
}

function int foo(int arg1)
{
	int f = arg1;
	HW_PR(f);
	f=7+6+5;
	HW_PR(f);
	f=5*6*2;
	HW_PR(f);
	return 0;
}


function void HW_PR(int arg)
{
	__ASM__('mov RAMEND_LOW r0');
	__ASM__('ldi 04 SFR');
	return;
}

function void halt()
{
	__ASM__('ldi ff SFR');
	return;
}
