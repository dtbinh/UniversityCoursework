/*****************************************************************************
 
  main.cpp -- This is the top level file instantiating the modules and
              binding ports to signals.
 
 *****************************************************************************/

#include "systemc.h"
#include "memory.h"
#include "datapath.h"
#include "controlUnit.h"

#define NS * 1e-9

int sc_main(int ac, char *av[])
{
        sc_report_handler::set_actions("/IEEE_Std_1666/deprecated", SC_DO_NOTHING);
	int i;

	sc_signal<sc_uint<2> >  wreg_addr_mux;
	sc_signal<sc_uint<3> >  b_mux;
	sc_signal<sc_uint<3> >  func;
	sc_signal<bool>  reg_write_mux;
	sc_signal<bool>  a_mux;
   	sc_signal<bool>  pc_mux;
	sc_signal<bool>  addr_mux;

	sc_signal<bool>  ldReg;
	sc_signal<bool>  ldPC;
	sc_signal<bool>  ldMAR;
	sc_signal<bool>  ldMDSR;
	sc_signal<bool>  ldIR;
	sc_signal<bool>  ldMDLR;

	sc_signal<bool>  mem_rd;
	sc_signal<bool>  mem_wrt;

	sc_signal<sc_int<32> > ir;
	sc_signal<sc_int<32> > pc;
	sc_signal<sc_int<32> > mar;
	sc_signal<bool>       zero;
	sc_signal<sc_int<32> > addr;
	sc_signal<sc_int<32> > mdsr;
	sc_signal<sc_int<32> > mem_data;


	sc_signal<bool> reset;
	//sc_signal<bool> clk;

	sc_clock clk ("ID", 10, SC_NS, 0.5, 10, SC_NS, true);

	controlUnit cu("controlUnit");
	cu.wreg_addr_mux(wreg_addr_mux);
	cu.reg_write_mux(reg_write_mux);
	cu.a_mux(a_mux);
	cu.b_mux(b_mux);
	cu.func(func);
	//cu.halt(halt);
	cu.pc_mux(pc_mux);
	cu.addr_mux(addr_mux);
	cu.ldReg(ldReg);
	cu.ldPC(ldPC);
	cu.ldMAR(ldMAR);
	cu.ldMDSR(ldMDSR);
	cu.ldIR(ldIR);
	cu.ldMDLR(ldMDLR);
	cu.mem_rd(mem_rd);
	cu.mem_wrt(mem_wrt);
	cu.ir(ir);
	cu.zero(zero);
	cu.clk(clk);
	cu.reset(reset);
	
	datapath dp("datapath");
	// Named port binding
	dp.zero(zero);
	dp.ir(ir);
	dp.pc(pc);
	dp.mar(mar);
	dp.mdsr(mdsr);
	dp.mem_data(mem_data);
	dp.wreg_addr_mux(wreg_addr_mux);
	dp.reg_write_mux(reg_write_mux);
	dp.a_mux(a_mux);
	dp.b_mux(b_mux);
	dp.func(func);
	dp.pc_mux(pc_mux);
	dp.ldReg(ldReg);
	dp.ldPC(ldPC);
	dp.ldMAR(ldMAR);
	dp.ldMDSR(ldMDSR);
	dp.ldIR(ldIR);
	dp.ldMDLR(ldMDLR);
	dp.clk(clk);
	dp.reset(reset);

	memory mem("memory");
	mem.mem_data(mem_data);
	//mem.addr(addr);
	mem.pc(pc);
	mem.mar(mar);
	mem.addr_mux(addr_mux);
	mem.mem_rd(mem_rd);
	mem.mem_wrt(mem_wrt);
	mem.data_in(mdsr);
	mem.clk(clk);
	mem.reset(reset);

	/*
	amux amx("addressMux");
	amx.addr(addr);
	amx.pc(pc);
	amx.mar(mar);
	amx.addr_mux(addr_mux);
	*/
	
	if (ac < 1) {
		printf("Test program file expected\n");
		return 1;
	} else {
		mem.fname = av[1];
	}
	
	// Waves:
	cu.tf = sc_create_vcd_trace_file("waves");
        cu.tf->set_time_unit(1, SC_NS);

	sc_trace(cu.tf, clk, "clock");
	sc_trace(cu.tf, reset, "reset");
	sc_trace(cu.tf, wreg_addr_mux, "wreg_addr_mux"); 
	sc_trace(cu.tf, b_mux, "b_mux");
	sc_trace(cu.tf, func, "func");
	sc_trace(cu.tf, reg_write_mux, "reg_write_mux");
	sc_trace(cu.tf, a_mux, "a_mux");
   	sc_trace(cu.tf, pc_mux, "pc_mux");
	sc_trace(cu.tf, addr_mux, "addr_mux");
	sc_trace(cu.tf, ldReg, "ldReg");
	sc_trace(cu.tf, ldPC, "ldPC");
	sc_trace(cu.tf, ldMAR, "ldMAR");
	sc_trace(cu.tf, ldMDSR, "ldMDSR");
	sc_trace(cu.tf, ldIR, "ldIR");
	sc_trace(cu.tf, ldMDLR, "ldMDLR");
	sc_trace(cu.tf, mem_rd, "mem_rd");
	sc_trace(cu.tf, mem_wrt, "mem_wrt");
	sc_trace(cu.tf, ir, "ir");
	sc_trace(cu.tf, zero, "zero");
	sc_trace(cu.tf, mdsr, "mdsr");
	sc_trace(cu.tf, mem_data, "mem_data");
	sc_trace(cu.tf, pc, "pc");
	sc_trace(cu.tf, mar, "mar");

	sc_trace(cu.tf, dp.next_pc, "next_pc");

	sc_trace(cu.tf, cu.cycle_count, "cycleCount");
	sc_trace(cu.tf, cu.halt, "halt");
	sc_trace(cu.tf, cu.cur_cycle, "cur_cycle");
	sc_trace(cu.tf, cu.opcode, "opcode");
	sc_trace(cu.tf, cu.subfunct, "subfunct");
	sc_trace(cu.tf, cu.next_cycle, "next_cycle");
	sc_trace(cu.tf, dp.mdlr, "mdlr");
	sc_trace(cu.tf, dp.a_addr, "a_addr");
	sc_trace(cu.tf, dp.b_addr, "b_addr");
	sc_trace(cu.tf, dp.wreg_addr, "wreg_addr");
	sc_trace(cu.tf, dp.a, "a_bus");
	sc_trace(cu.tf, dp.b, "b_bus");
	sc_trace(cu.tf, dp.b_reg, "b_reg");
	sc_trace(cu.tf, dp.alu, "alu");
	sc_trace(cu.tf, dp.reg_write_data, "reg_write_data");
	sc_trace(cu.tf, dp.registers[0], "R0");
	sc_trace(cu.tf, dp.registers[1], "R1");
	sc_trace(cu.tf, dp.registers[2], "R2");
	sc_trace(cu.tf, dp.registers[3], "R3");
	sc_trace(cu.tf, dp.registers[4], "R4");
	sc_trace(cu.tf, dp.registers[5], "R5");
	sc_trace(cu.tf, dp.registers[6], "R6");
	sc_trace(cu.tf, dp.registers[7], "R7");
	sc_trace(cu.tf, dp.registers[8], "R8");
	sc_trace(cu.tf, dp.registers[9], "R9");
	sc_trace(cu.tf, dp.registers[10], "R10");
	sc_trace(cu.tf, dp.registers[11], "R11");
	sc_trace(cu.tf, dp.registers[12], "R12");
	sc_trace(cu.tf, dp.registers[13], "R13");
	sc_trace(cu.tf, dp.registers[14], "R14");
	sc_trace(cu.tf, dp.registers[15], "R15");
	sc_trace(cu.tf, dp.registers[16], "R16");
	sc_trace(cu.tf, dp.registers[17], "R17");
	sc_trace(cu.tf, dp.registers[18], "R18");
	sc_trace(cu.tf, dp.registers[19], "R19");
	sc_trace(cu.tf, dp.registers[20], "R20");
	sc_trace(cu.tf, dp.registers[21], "R21");
	sc_trace(cu.tf, dp.registers[22], "R22");
	sc_trace(cu.tf, dp.registers[23], "R23");
	sc_trace(cu.tf, dp.registers[24], "R24");
	sc_trace(cu.tf, dp.registers[25], "R25");
	sc_trace(cu.tf, dp.registers[26], "R26");
	sc_trace(cu.tf, dp.registers[27], "R27");
	sc_trace(cu.tf, dp.registers[28], "R28");
	sc_trace(cu.tf, dp.registers[29], "R29");
	sc_trace(cu.tf, dp.registers[30], "R30");
	sc_trace(cu.tf, dp.registers[31], "R31");

        // Using "mem(xx)" not "mem[xx]" as SystemC complains about
        // latter possibly being interpreted by waveform viewer in
        // unexpected ways

	sc_trace(cu.tf, mem.mem[0], "mem(0)");
	sc_trace(cu.tf, mem.mem[1], "mem(4)");
	sc_trace(cu.tf, mem.mem[2], "mem(8)");
	sc_trace(cu.tf, mem.mem[3], "mem(12)");
	sc_trace(cu.tf, mem.mem[4], "mem(16)");
	sc_trace(cu.tf, mem.mem[5], "mem(20)");
	sc_trace(cu.tf, mem.mem[6], "mem(24)");
	sc_trace(cu.tf, mem.mem[7], "mem(28)");
	sc_trace(cu.tf, mem.mem[8], "mem(32)");
	sc_trace(cu.tf, mem.mem[9], "mem(36)");
	sc_trace(cu.tf, mem.mem[10], "mem(40)");
	sc_trace(cu.tf, mem.mem[11], "mem(44)");
	sc_trace(cu.tf, mem.mem[12], "mem(48)");
	sc_trace(cu.tf, mem.mem[13], "mem(52)");
	sc_trace(cu.tf, mem.mem[14], "mem(56)");
	sc_trace(cu.tf, mem.mem[15], "mem(60)");
	sc_trace(cu.tf, mem.mem[16], "mem(64)");
	sc_trace(cu.tf, mem.mem[17], "mem(68)");
	sc_trace(cu.tf, mem.mem[18], "mem(72)");
	sc_trace(cu.tf, mem.mem[19], "mem(76)");
	sc_trace(cu.tf, mem.addr, "mem_addr");


	//clk = 0;
	reset = 0;
	sc_start(3);
	reset = 1;
	sc_start(4);
	reset = 0;
	sc_start(-1);

	sc_close_vcd_trace_file(cu.tf);

	return 0;
}

