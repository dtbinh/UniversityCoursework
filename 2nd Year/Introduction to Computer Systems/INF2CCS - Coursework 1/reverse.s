#This program was created with assistance from Sam Neugbar and Jason Ebbin#
#reverse.s#
#Scott Hofman s0943941#

.data
array:        .space      42    # Creates the 42 character integer array


.text
.globl main
main:   
        # Get number from user
    li   $v0, 8         	#service code
    la   $a0, array  	    #address of buffer
    li   $a1, 42	        #length of 
    syscall

    # Loading the stack address
    la   $s0, array       	#Sets $s0 to store the string
    li $t0, 0       		#This 0 will indicate end of the string
    sub $s0,$s0, 4      	#Moves it 1 byte to account for 0
    sw $t0, ($s0)      		#Saves it
    li $t1, 0         		#Set counter to 0
    
    
push:   
    #Push the numbers onto the array
    lb $t0, array($t1)    	#Loads the byte from the array
    beqz $t0, stringend    	#Checks if it is at the end
        sub $s0,$s0,4       #Moves it 1 byte down
    sw $t0, ($s0)        	#Stores the word 
    addi $t1, 1        		#Move the pointer up one
    j push            		#Loop it back until done

stringend:
    li $t1, 0 			    #reset counter to 0

pop:
    #Pop the numbers off the array
    lw $t0, ($s0)       	#Loads the word
    add $s0, $s0, 4         #Move the pointer down 1 byte
    beqz $t0, done        	#Check to see if at the end
    sb $t0, array($t1)    	#Store the byte from the current 
    addi $t1, 1        		#Move the pointer up one
    j pop            		#Loop it back until done

done: 
							#Prints the array
    li $v0, 4
    la $a1, array
    syscall
	li $a0, 10				#Prints the newline
	li $v0, 11
	syscall		
    li $v0, 10				#Exits the program
    syscall
