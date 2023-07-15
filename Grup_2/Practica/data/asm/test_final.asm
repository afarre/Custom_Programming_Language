	.data	#declare storage for variables
vara:	.word 0
varb:	.word 0
varc:	.word 0

	.text
	li $t0,5		#load integer: loads the an integer to the specified register
	sw $t0,vara		#store word: store word from source register into RAM destination

	li $t0,10		#load integer: loads the an integer to the specified register
	lw $t1,vara		#load address: loads the memory address from RAM destination to the specified variable
	add $t2,$t0,$t1		#adds two registers and stores the result in the first register
	sw $t2,vart2		#store word: store word from source register into RAM destination

	lw $t0,vart2		#load address: loads the memory address from RAM destination to the specified variable
	sw $t0,varb		#store word: store word from source register into RAM destination

	li $t0,0		#load integer: loads the an integer to the specified register
	sw $t0,varc		#store word: store word from source register into RAM destination

	L1:

	bgt $t0,$t1,L2		#branch if first parameter > second parameter

	#Space for else body (non-usable for while statements)

	b FinL2		#go to flag FinL2 if condition is not met

	L2:

	sw $t0,varb		#store word: store word from source register into RAM destination
	li $t1,2		#load integer: loads the an integer to the specified register
	beq $t0,$t1,I2		#branch if first parameter == second parameter

	#Space for else body (non-usable for while statements)

	b FinI2		#go to flag FinI2 if condition is not met

	I2:

	lw $t0,varb		#load address: loads the memory address from RAM destination to the specified variable
	lw $t1,vara		#load address: loads the memory address from RAM destination to the specified variable
	div $t2,$t0,$t1		#divides two registers and stores the result in the first register
	sw $t2,vart2		#store word: store word from source register into RAM destination

	lw $t0,vart2		#load address: loads the memory address from RAM destination to the specified variable
	sw $t0,varb		#store word: store word from source register into RAM destination

	FinI2:

	b L1

	FinL2:

end: