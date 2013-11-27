// Inf2C Computer Systems, Processor design practical
// Copyright 2004, School of Informatics, The University of Edinburgh


#include "systemc.h"
#include "controlUnit.h"
#include "defines.h"

// Control unit for processor. Combinational logic
void controlUnit::ctrl_regs()
{
    sc_int<3> t_next_cycle;

    while (1) {
        wait(); // wait for reset or clock

        if (reset) {
            wait(1, SC_NS);
            cur_cycle = 0;
            cycle_count = 0;
        } else {
            if (halt.read()) {
                // endSimulation
                std::cout << sc_time_stamp()
                          <<" Halting simulation!"
                          << std::endl
                          << "Cycles = "
                          << cycle_count
                          << std::endl;
                sc_stop();
                // No return instruction needed.
            }
            cycle_count++;
            t_next_cycle = next_cycle;
            wait(1, SC_NS);
            cur_cycle = t_next_cycle;
        }
    }
}


/* 
Combinational Logic for Control Unit State Machine
==================================================

X on an input or output means "don't care". Symbolic names for values,
as suggested in the instructions, are used when appropriate.

Default outputs
---------------
INPUTS            OUTPUTS
cur_cyc subfun    mem_rd  ldPC    ldIR    ldMDLR  wa_mux  ad_mux  b_mux   func    halt;
    opcode  zero      mem_wrt ldMAR   ldMDSR  ldReg   pc_mux  a_mux   rw_mux  nxt_cyc

                  0   0   0   1   0   1   1   0   RD  ALU MAR X   X   ALU X   X   0

All instructions
----------------
cyc0: IF, PC=PC+4 

INPUTS            OUTPUTS
cur_cyc subfun    mem_rd  ldPC    ldIR    ldMDLR  wa_mux  ad_mux  b_mux   func    halt;
    opcode  zero      mem_wrt ldMAR   ldMDSR  ldReg   pc_mux  a_mux   rw_mux  nxt_cyc

0   X   X   X     X   X   1   X   1   X   X   X   X   X   PC  PC  4   X   ADD 1   X

j target   (opcode=02)
--------
cyc1: PC=PC[31:26]^(IR[25:0] << 2)

INPUTS            OUTPUTS
cur_cyc subfun    mem_rd  ldPC    ldIR    ldMDLR  wa_mux  ad_mux  b_mux   func    halt;
    opcode  zero      mem_wrt ldMAR   ldMDSR  ldReg   pc_mux  a_mux   rw_mux  nxt_cyc

1   02  X   X     X   X   1   X   X   X   X   X   X   IMM X   X   X   X   X   0   X

halt   (opcode=00 && subfun=0c)
----
cyc1:  halt=1

INPUTS            OUTPUTS
cur_cyc subfun    mem_rd  ldPC    ldIR    ldMDLR  wa_mux  ad_mux  b_mux   func    halt;
    opcode  zero      mem_wrt ldMAR   ldMDSR  ldReg   pc_mux  a_mux   rw_mux  nxt_cyc

1   00  0c  X     X   X   X   X   X   X   X   X   X   X   X   X   X   X   X   X   1

add rd, rs, rt
---
cur_cyc subfun    mem_rd  ldPC    ldIR    ldMDLR  wa_mux  ad_mux  b_mux   func    halt;
    opcode  zero      mem_wrt ldMAR   ldMDSR  ldReg   pc_mux  a_mux   rw_mux  nxt_cyc

1   0x0 0x20X     X   X   X   X   X   X   X   1   RD  X   X   REG REG ALU ADD 0   X

sub rd, rs, rt
---
cur_cyc subfun     mem_rd  ldPC    ldIR    ldMDLR  wa_mux  ad_mux  b_mux   func    halt;
    opcode   zero      mem_wrt ldMAR   ldMDSR  ldReg   pc_mux  a_mux   rw_mux  nxt_cyc

1   0x0 0x22 X     X   X   X   X   X   X   X   1   RD  X   X   REG REG ALU SUB 0   X

addi rt, rs, n
----
cur_cyc subfun    mem_rd  ldPC    ldIR    ldMDLR  wa_mux  ad_mux  b_mux    func    halt;
    opcode  zero      mem_wrt ldMAR   ldMDSR  ldReg   pc_mux  a_mux    rw_mux  nxt_cyc

1   0x8 X   X     X   X   X   X   X   X   X   1   RT  X   X   REG IR16 ALU ADD 0   X

lw rt, n(rs)
--
cur_cyc  subfun    mem_rd  ldPC    ldIR    ldMDLR  wa_mux  ad_mux  b_mux    func    halt;
    opcode   zero      mem_wrt ldMAR   ldMDSR  ldReg   pc_mux  a_mux    rw_mux  nxt_cyc

1   0x23 X   X     X   X   X   1   X   X   X   X   X   X   X   REG IR16 X   ADD 2   X
2   0x23 X   X     1   X   X   X   X   X   1   X   X   X   MAR X   X    X   X   3   X
3   0x23 X   X     X   X   X   X   X   X   X   1   RT  X   X   X   X    MEM X   0   X

sw rt, n(rs)
--
cur_cyc  subfun    mem_rd  ldPC    ldIR    ldMDLR  wa_mux  ad_mux  b_mux   func    halt;
    opcode   zero      mem_wrt ldMAR   ldMDSR  ldReg   pc_mux  a_mux   rw_mux  nxt_cyc

1   0x2b X   X     X   X   X   1   X   1   X   X   X   X   X   REG IR16X   ADD 2   X
2   0x2b X   X     X   1   X   X   X   X   X   X   X   X   MAR X   X   X   X   0   X

beq rt, rs, label
---
cur_cyc subfun    mem_rd  ldPC    ldIR    ldMDLR  wa_mux  ad_mux  b_mux    func    halt;
    opcode  zero      mem_wrt ldMAR   ldMDSR  ldReg   pc_mux  a_mux    rw_mux  nxt_cyc

1   0x4 X   X     X   X   X   X   X   X   X   X   X   X   X   REG REG  X   SUB 2   X
2   0x4 X   1     X   X   1   X   X   X   X   X   X   ALU X   PC  16X4 X   ADD 0   X

bne rt, rs, label
---
cur_cyc subfun    mem_rd  ldPC    ldIR    ldMDLR  wa_mux  ad_mux  b_mux    func    halt;
    opcode  zero      mem_wrt ldMAR   ldMDSR  ldReg   pc_mux  a_mux    rw_mux  nxt_cyc

1   0x5 X   X     X   X   X   X   X   X   X   X   X   X   X   REG REG  X   SUB 2   X
2   0x5 X   0     X   X   1   X   X   X   X   X   X   ALU X   PC  16X4 X   ADD 0   X

jal target
---
cur_cyc subfun    mem_rd  ldPC    ldIR    ldMDLR  wa_mux  ad_mux  b_mux   func    halt;
    opcode  zero      mem_wrt ldMAR   ldMDSR  ldReg   pc_mux  a_mux   rw_mux  nxt_cyc

1   0x3 X   X     X   X   1   X   X   X   X   1   _31 IMM X   PC  _0  ALU ADD 0   X

jr
--
cur_cyc subfun    mem_rd  ldPC    ldIR    ldMDLR  wa_mux  ad_mux  b_mux   func    halt;
    opcode  zero      mem_wrt ldMAR   ldMDSR  ldReg   pc_mux  a_mux   rw_mux  nxt_cyc

1   0x0 0x8 X     X   X   1   X   X   X   X   X   X   ALU X   REG _0  X   ADD 0   X

Do the opcode and subfunctions
*/


void controlUnit::ctrl_comb()
{
    sc_int<32> t_ir = ir;

    // Extract useful subfields of the instruction
    opcode = t_ir.range(31,26);
    subfunct = t_ir.range(5,0);

    // Default values for various control fields
    //halt = false;
    mem_rd = false;
    mem_wrt = false;

    ldPC = false; 
    ldIR = false; 
    ldReg = false;

    ldMAR = false;
    ldMDLR = false;
    ldMDSR = false;


    pc_mux = PC_ALU;  // Select ALU output by default
    addr_mux = ADDR_MAR;  // MAR by default
    reg_write_mux = RW_ALU;  // write ALU result by default
    wreg_addr_mux = WA_RD;  // into Rd by default

    switch(cur_cycle.read()){

        // The first step fetches the instruction
    case(0):
        // Set address bus and read instruction from memory
        mem_rd = true;
        addr_mux = ADDR_PC;
        // Increment PC
        func = ADD;
        a_mux = A_PC;
        b_mux = B_4;
        ldPC = true;
        // Get the instruction into IR
        ldIR = true;
        next_cycle = 1;  // move to next cycle
        break;
            
        // Now execute the instruction
    case(1):
        if (opcode == 2) {
            // jump instruction
            // Concatenate the lower 26 bits of IR, times 4, 
            //   with the upper 4 bits of the PC
            pc_mux = PC_IMM;
            ldPC = true;  // and load into PC
            next_cycle = 0;  // Instr complete
	} else if (opcode == 0x0 && subfunct == 0x20) {
	     //add instruction
		a_mux = A_REG; 					//Take the two registers into the ALU
		b_mux = B_REG;
		func = ADD; 					//Add the values within the ALU
		reg_write_mux = RW_ALU; 			//Take the result of the ALU and send it back to the register file
		wreg_addr_mux = WA_RD; 				//Take the address of RD from the instruction and set it as the target address
								//Note: wreg_write_mux and wreg_addr_mux are defined already by default as these.
								//	For understanding however, they were coded into the instructions again,
								//	even though they don't need to be there. 
		ldReg = true; 					//Load the register file
		next_cycle = 0; 				//Instruction complete
	} else if (opcode == 0x0 && subfunct == 0x22) {
	    //subtraction instruction
		a_mux = A_REG; 					//Take the two registers into the ALU
		b_mux = B_REG;
		func = SUB; 					//Subtract the two values within the ALU
		reg_write_mux = RW_ALU; 			//Take the result of the ALU and send it back to the register file
		wreg_addr_mux = WA_RD; 				//Take the address of RD from the instruction and set it as the target address
		ldReg = true;  					//Load the register file
		next_cycle = 0;					//Instruction complete
	} else if (opcode == 0x8) {
	     //add immediate instruction
		a_mux = A_REG;					//Take the register provided by rs
		b_mux = B_IR_16; 				//Take the 16 bit 2s complement number immediate value
		func = ADD; 					//Add the two values within the ALU
		reg_write_mux = RW_ALU; 			//Take the result of the ALU and send it back to the register file to be written
		wreg_addr_mux = WA_RT; 				//Take the address of RT from the instruction and set it as the target address
		ldReg = true; 					//Load the register file
		next_cycle = 0;					//Instruction complete
        } else if (opcode == 0x23) {
	      //Load word instruction
		a_mux = A_REG; 					//Take the register for address
		b_mux = B_IR_16; 				//16 bit 2s complement number for offset
		func = ADD; 					//Add the two together to get a target address
		ldMAR = true; 					//Set load into memory to true
		next_cycle = 2; 				//Go to next cycle to use address to take value from memory
	} else if (opcode == 0x2b) {
	      //store word instruction
		a_mux = A_REG; 					//Take register for address
		b_mux = B_IR_16; 				//16 bit 2s complement number for offset
		func = ADD; 					//Add the two together to get target address
		ldMAR = true; 					//Set load into memory to load at the end of cycle
		ldMDSR = true; 					//Set the data-in memory register to load at the end of cycle the data you want stored
		next_cycle = 2; 				//Go to next cycle to take from memory
	} else if (opcode == 0x4 || opcode == 0x5) {
	      //Branch (on Equal and Not Equal) Instruction
		a_mux = A_REG; 					//Take the two comparisons
		b_mux = B_REG;
		func = SUB; 					//Subtract them to check equality
		next_cycle = 2; 				//Go to next cycle to branch
	} else if (opcode == 0x3) {
	      //Jump and Link Instruction
		a_mux = A_PC; 					//You need the value where you are (Program counter) stored into the register.
		b_mux = B_0; 					// To do this, you add a_mux and 0 so that a_mux can be written into the register.
		func = ADD; 					//Add zero so A_PC is unchanged
		reg_write_mux = RW_ALU; 			//Store the PC into the multiplexer
		wreg_addr_mux = WA_31; 				//Address of $31
		ldReg = true; 					//Load the PC into the register
		pc_mux = PC_IMM; 				//Target address concatenated with PC + 4 for target jump
		ldPC = true; 					//Go to the jump
		next_cycle = 0; 				//Instruction complete
	} else if (opcode == 0x0 && subfunct == 0x8) {
	      //Jump Register Instruction
		a_mux = A_REG; 					//Take the jump address provided
		b_mux = B_0; 					//Don't want to change this, but need it to go through the LAU 
		func = ADD; 					//Pass through ALU, changing nothing
		pc_mux = PC_ALU; 				//Value of ALU goes to PC as the address to next go to
		ldPC = true; 					//Load the value as the next PC
		next_cycle = 0;					//Instruction complete
	} else if (opcode == 0 && subfunct == 12) {
            // halt instruction
            printf("Halt instruction\n");
            halt = true;
        } else {
            // unrecognised instruction
            printf("unrecognized instruction, 2nd cycle\n");
            halt = true;
        } break;
   case(2):
	if (opcode == 0x23) {
	      //Load Word Instruction 
		addr_mux = ADDR_MAR; 				//Take the value from MAR as the address into the memory
								//Note: The above line is already predefined. Similarily to before, this is
								//	kept for understanding. 
		mem_rd = true; 					//Read from the memory at the address provided by previous cycle
		ldMDLR = true; 					//Allow the data to go into MDLR next cycle to return to the register file
		next_cycle = 3; 				//Go to final cycle
	} else if (opcode = 0x2b) {
	      //Store Word Instruction
		addr_mux = ADDR_MAR; 				//Take the value assigned from the previous cycle as the address into memory	
		mem_wrt = true; 				//Write (store) into the memory at the address determined by addr_mux
		next_cycle = 0;					//Instruction complete
	} else if (opcode = 0x4) {
	      //Branch on Equal
		if(zero) {					//Checks that the values are zero (equal)
			a_mux = A_PC;				//Take the current location
			b_mux = B_IR_16X4;			//And a 16 bit 2s complement number multiplied by 4
			func = ADD;				//Add them together to get a new target address to branch to
			pc_mux = PC_ALU;			//Set the PC_mux to the value of the ALU (the new address)
			ldPC = true;				//Load the new branch into the PC
			}					//If not zero, do not branch
		next_cycle = 0;					//Instruction complete
	} else if (opcode = 0x5) {
	      //Branch on Non-Equal
		if(!zero) {					//Checks that the values are not zero (not equal)
			a_mux = A_PC;				//Take the current location
			b_mux = B_IR_16X4;			//And a 16 bit 2s complement number multiplied by 4
			func = ADD;				//Add them together to get a new target address to branch to
			pc_mux = PC_ALU;			//Set the PC_mux to the value of the ALU (the new address)
			ldPC = true;				//Load the new branch into the PC
			}					//If zero, do not branch
		next_cycle = 0;					//Instruction complete
	} break;
    case(3):
	if (opcode== 0x23) {
	      //Load Word Instruction
		wreg_addr_mux = WA_RT; 				//Take the address of RR from the instruction and set it as the target address
		reg_write_mux = RW_MEM; 			//The multiplexer chooses the memory as its input so memory can be loaded
		ldReg = true; 					//Allow the register file to be loaded
		next_cycle = 0;					//Instruction complete
	}
	break;
    default:
        printf("Wrong current state\n");
        halt = true;
            
    } // end switch(cur_state)
}
