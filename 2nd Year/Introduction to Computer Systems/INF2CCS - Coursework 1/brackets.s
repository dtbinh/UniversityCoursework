#This program was created with assistance from David Eichmann and Craig Innes#
#Brackets.s#
#Scott Hofman s0943941#

.data
at: .asciiz "At "
errorMCB: .asciiz ": mismatching close bracket\n"
errorUCB: .asciiz ": unexpected close bracket\n"
errorUOB: .asciiz ": unclosed open bracket(s)\n"
success: .asciiz ": all brackets matched\n"
stacksize: .space 42
input: .space 42
.text
.globl main

main:
	li $s4,0					#Number of errors = 0
	la $s2,stacksize			#Load stack top
	la $s3,stacksize			#Load stack bottom
		li $v0,8				#Get the input
		la $a0,input		
		li $a1,42
		syscall
	la $s0,input				#Start
	la $s1,input				#Current position

mainloop:						#Starts parsing the string
	lb $v0,0($s1)				#Loads char
	addi $s1,$s1,1				#Move pointer1
	move $s5,$v0				#Moves char to s5
	li $t0,91					#Loads open bracket
	beq $s5,$t0,parseO			#Checks if open, then goes to parse open
	li $t0,93					#Loads close bracket
	beq $s5,$t0,parseBC			#Checks if closed, then goes to parse closed bracket
	li $t0,40					#Loads open round
	beq $s5,$t0,parseO			#Checks if open, then goes to parse open
	li $t0,41					#Loads closed round
	beq $s5,$t0,parseRC			#Checks if closed, then goes to parse closed round
	li $t0,10					#Loads newline
	beq $s5,$t0,end				#If newline, go to the end
	j mainloop					#Loop it around 

parseO:
		move $a0,$s5			#Move character into a0
		addi $s2,$s2,1			#Increment stack pointer
		sb $a0,0($s2)			#Store
		j mainloop				#Loop back
	
parseBC:
		li $s6,91				#Load open bracket
		j parseC				#Goto ParseClose
	
parseRC:
		li $s6,40				#Load open round
		j parseC				#Goto ParseClose
	
parseC:	
		jal stackCount			#Get stackCount
		beqz $v0,unclose		#See if closed accidently
		lb $v0,0($s2)			#Pop if fine
		addi $s2,$s2,-1			
		beq $s6,$v0,mainloop	#Go back to beginning if fine
		la $a0,errorMCB			#If not, load
		jal showMsg				#and show error message
		jal errorcount			#Add 1 to error count
		j mainloop				#Go back to start

unclose:
		la $a0,errorUCB			#Load
		jal showMsg				#And show error message
		jal errorcount			#Add 1 to error count
		j mainloop				#Go back to start

end:
		jal stackCount			#Get stackcount
		beqz $v0,finalmsg		#If parsing is good, show final message
		la $a0,errorUOB			#If not, load
		jal showMsg				#and show error message
		jal errorcount			#Add 1 to errorcount

finalmsg:
	bgtz $s4,exit				#If not successful, go to exit
	la $a0,success				#If it's successful
	jal showMsg					#Show message and progress to exit

exit:
	li $v0,10					#Exit
	syscall

stackCount:
	sub $v0,$s2,$s3				#Figure out the stack by subtracting top from bottom
	jr $ra						#Go back to program

showMsg:
	move $a1,$a0					#shift the address
	la $a0,at					#Display at
	li $v0,4
	syscall
	sub $a0,$s1,$s0				#Display position
	li $v0,1
	syscall
	move $a0,$a1				#Display error/success message
	li $v0,4
    syscall
	jr $ra						#Get back to program

errorcount:
	addi $s4,$s4,1				#Add 1 to amount of errors
	jr $ra						#Return to place in the program
