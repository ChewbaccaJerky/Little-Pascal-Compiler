	#TEXT SEGMENT
	.text
	.globl main
main:
	move	 $fp, $sp

	#New Line
	la	 $a0, newline
	syscall

	#Print String
	li	 $v0, 4
	la	 $a0, String0
	syscall

	#GREATER THAN
	lw	 $t0, -4($fp)
	lw	 $t1, 0($fp)
	slt	 $t0, $t1, $t0
	sw	 $t0, -8($fp)

	#IF STATEMENT
	lw	 $t0, -8($fp)
	beq	 $t0, $0, ELSE0

	#New Line
	la	 $a0, newline
	syscall

	#Print String
	li	 $v0, 4
	la	 $a0, String1
	syscall

	#GENERATE LABEL
ELSE0:
	#LESS THAN
	lw	 $t0, -4($fp)
	lw	 $t1, 0($fp)
	slt	 $t0, $t0, $t1
	sw	 $t0, -8($fp)

	#IF STATEMENT
	lw	 $t0, -8($fp)
	beq	 $t0, $0, ELSE1

	#Store Immediate
	li	 $t0, 2
	sw	 $t0, -12($fp)

	#Store Immediate
	li	 $t0, 2
	sw	 $t0, -16($fp)

	#MULTIPLY
	lw	 $t0, -12($fp)
	lw	 $t1, -16($fp)
	mult	 $t0, $t1
	sw	 $t1, -16($fp)

	#Move to variable location.
	lw	 $t0, -16($fp)
	sw	 $t0, 0($fp)

	#Print String
	li	 $v0, 4
	la	 $a0, String2
	syscall

	#New Line
	la	 $a0, newline
	syscall

	#Print Expression
	lw	 $t0, -16($fp)
	move	 $a0, $t0
	li	 $v0, 1
	syscall

	#Print String
	li	 $v0, 4
	la	 $a0, String3
	syscall

	#New Line
	la	 $a0, newline
	syscall

	#Print Expression
	lw	 $t0, -16($fp)
	move	 $a0, $t0
	li	 $v0, 1
	syscall

	#GENERATE LABEL
ELSE1:
	#Store Immediate
	li	 $t0, 3
	sw	 $t0, -28($fp)

	#Move to variable location.
	lw	 $t0, -28($fp)
	sw	 $t0, -20($fp)

	#Store Immediate
	li	 $t0, 3
	sw	 $t0, -32($fp)

	#AddOp
	lw	 $t0, -20($fp)
	lw	 $t1, -32($fp)
	add	 $t0, $t0, $t1
	sw	 $t0, -32($fp)

	#Move to variable location.
	lw	 $t0, -32($fp)
	sw	 $t0, -24($fp)

	#New Line
	la	 $a0, newline
	syscall

	#Print Expression
	lw	 $t0, -32($fp)
	move	 $a0, $t0
	li	 $v0, 1
	syscall

	#New Line
	la	 $a0, newline
	syscall

	#Print Expression
	lw	 $t0, -32($fp)
	move	 $a0, $t0
	li	 $v0, 1
	syscall

	#New Line
	la	 $a0, newline
	syscall

	#Print Expression
	lw	 $t0, -32($fp)
	move	 $a0, $t0
	li	 $v0, 1
	syscall

	#EXIT SYSTEM
	li	 $v0, 10
	syscall

	#DATA SEGMENT
	.data
String0: .asciiz "Enter a number"
String1: .asciiz "TOO BIGG!!!"
String2: .asciiz "You Entered"
String3: .asciiz "Multiply by 2 and you get"
newline: .asciiz "\n"