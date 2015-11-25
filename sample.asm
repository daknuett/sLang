#include<stddef.inc>
call start
ldi ff SFR
.set funct_start_begin a
.set funct_start_size 16
.set var_f 0
.set var_b 0
.set var_chr 0
start:
ldi 2d var_f
mov var_f RAMEND_LOW
call foo
mov RAMEND_LOW var_b

ldi 'c' var_chr
mov var_b RAMEND_LOW
call HW_PR
call halt
ret

.set funct_foo_begin 24
.set funct_foo_size 51
.set var_bar 0
.set var_i 0
foo:
ldi a var_bar
mov RAMEND_LOW r0
subi 5 r0
jlt r0 funct_foo_if1
jmp funct_foo_else1
funct_foo_if1:
mov var_bar r0
add RAMEND_LOW r0
mov r0 RAMEND_LOW
jmp funct_foo_if1_end
funct_foo_else1:
mov var_bar r0
mov RAMEND_LOW r1
sub r0 r1
mov r1 var_bar
jmp funct_foo_if1_end
funct_foo_if1_end:
ldi 0 var_i

ldi 2 r0
mov var_i r1
mov var_bar r2
div r2 r0
sub r1 r0
jge r0 funct_foo_while1_end
jmp funct_foo_while1_start
funct_foo_while1_start:
mov var_bar r0
mov var_i r1
add r1 r0
mov r0 var_bar
mov var_i r0
inc r0
mov r0 var_i
funct_foo_while1_end:
mov var_bar RAMEND_LOW
ret

.set funct_HW_PR_begin 75
.set funct_HW_PR_size 7

HW_PR:
mov RAMEND_LOW r0
ldi 04 SFR
ret

.set funct_halt_begin 7e
.set funct_halt_size 4
halt:
ldi ff SFR
ret
