#include "systemc.h"
#include "datapath.h"
#include "defines.h"

void datapath::dp_alu()
// Inputs:  pc, ir, a_mux, b_mux, func 
// Outputs: alu, b_reg
{
	sc_int<32> t_ir;
	sc_int<16> tempb;

	while (1) {
		wait(); // wait for any signal on sensitivity list
		wait(5, SC_NS); // delay to produce outputs.

		t_ir = ir;
    	a_addr = t_ir.range(25,21);
    	b_addr = t_ir.range(20,16);

		// Multiplexor A
		switch(a_mux){
		case A_PC:
	    	a = pc;
	   		break;
		case A_REG:
			a = registers[a_addr];
	    	break;
		}

		// Multiplexor B
		switch(b_mux.read()){
		case B_REG:
	    	b = registers[b_addr];
	    	break;
		case B_0:
	    	b = 0;
			break;
		case B_4:
	    	b = 4;
			break;
		// To implement getting bits 0--15 with a sign extend,
		// first shift left by 16 (to get rid of unwanted bits), then
		// shift right by 16 (to put back in position and sign extend).
		case B_IR_16:
	    	tempb = t_ir.range(15,0); // Trick to force sign-extension
			b = tempb;
			break;
		case B_IR_16X4:
			tempb = t_ir.range(15,0);  // Force sign extension
	    	b = tempb << 2; // (ir.read() << 16 >>16) * 4 ;
			break;
		}


		// Compute ALU output
		switch(func.read()){
		case ADD:
	    	alu = a + b;
			break;
		case SUB:
	    	alu = a - b;
			break;
		case AND:
	    	alu = a & b;
			break;
		case OR:
	    	alu = a | b;
			break;
		case EOR:
	    	alu = a ^ b;
			break;
		}

		b_reg = registers[b_addr];

	}
}


void datapath::dp_rf_in()
// Inputs:  alu, mdlr, reg_write_mux
// Outputs: reg_write_data
{
	while (1) {
		wait(); // wait for any signal on sensitivity list
		wait(2, SC_NS); // delay to produce outputs.

		// Get reg_write_data Mux output
		switch(reg_write_mux){
		case RW_ALU:
	    	reg_write_data = alu;
			break;
		case RW_MEM:
	    	reg_write_data = mdlr;
			break;
		}
	}
}

void datapath::dp_pcgen()
// Inputs:  alu, pc, ir
// Outputs: next_pc
{
	sc_int<32> t_pc;
	sc_int<32> t_ir;

	while (1) {
		wait(); // wait for any signal on sensitivity list
		wait(2, SC_NS); // delay to produce outputs.
		t_pc = pc;
		t_ir = ir;

		switch(pc_mux){
		case PC_ALU:
    		next_pc = alu;
			break;
		case PC_IMM:
			// PC[31:28], (IR[25:0] << 2)
    		//pc = (t_pc.range(31,28), t_ir.range(25,0), "0b00");
    		t_pc.range(31,2) = (t_pc.range(31,28), t_ir.range(25,0));
			t_pc.range(1,0) = "0b00";
			next_pc = t_pc;
			break;
		}
	}
}

void datapath::dp_rf_wa()
// Inputs:  ir, wreg_addr_mux
// Outputs: wreg_addr
{
	sc_int<32> t_ir;

	while (1) {
		wait(); // wait for any signal on sensitivity list
		t_ir = ir;

		wait(2, SC_NS); // delay to produce outputs.
		switch (wreg_addr_mux.read()) {
		case WA_RD:
			wreg_addr.write((unsigned)t_ir.range(15,11));
			break;
		case WA_RT:
			wreg_addr.write((unsigned)t_ir.range(20,16));
			break;
		case WA_31:
			wreg_addr.write(31);
			break;
		}
	}
}

// Registers
void datapath::dp_regs()
{
	int        i;
	bool       t_ldPC, t_ldMAR, t_ldMDSR, t_ldIR, t_ldMDLR, t_ldReg;
	sc_int<32> t_next_pc, t_alu, t_b_reg, t_mem_data, t_reg_write_data;
	sc_uint<5> t_wreg_addr;
	while (1) { // forever

		wait(); // Wait for clock edge or reset.

		if (reset) { // Clear to 0
			wait(1, SC_NS);

			pc = 0;
			mar = 0;
			mdsr = 0;
			ir = 0;
			mdlr = 0;
			zero = false;
			// Clear all registers to 0
			for (i=0; i < 32; i++)
				registers[i] = 0;
		}
		else {
			// Sample values at clock edge. Keep at temp variables
			t_ldPC = ldPC;
			t_ldMAR = ldMAR;
			t_ldMDSR = ldMDSR;
			t_ldIR = ldIR;
			t_ldMDLR = ldMDLR;
			t_ldReg = ldReg;
			t_next_pc = next_pc;
			t_alu = alu;
			t_b_reg = b_reg;
			t_mem_data = mem_data;
			t_wreg_addr = wreg_addr;
			t_reg_write_data = reg_write_data;

			wait(1, SC_NS);

			// The zero flag (unconditional load)
			zero = ((int)t_alu == 0x0);
			// PC
			if (t_ldPC) {
				pc = t_next_pc;
			}
			// MAR
			if (t_ldMAR) {
				mar = t_alu;
			}
			// MDSR
			if (t_ldMDSR) {
				mdsr = t_b_reg;
			}
			// IR
			if (t_ldIR) {
				ir = t_mem_data;
			}
			// MDLR
			if (t_ldMDLR) {
				mdlr = t_mem_data;
			}
			// Update register file
			if (t_ldReg) {
	    		if ((unsigned) t_wreg_addr != 0)
					registers[t_wreg_addr] = t_reg_write_data;
	        		// If the register to update is 0, then we actually
                	// just keep it as 0 (register 0 always contains 0).
			}
		}
	}
}

