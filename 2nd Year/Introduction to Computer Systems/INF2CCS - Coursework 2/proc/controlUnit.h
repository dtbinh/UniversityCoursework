
struct controlUnit : sc_module {
	sc_out<sc_uint<2> >  wreg_addr_mux;
	sc_out<sc_uint<3> >  b_mux;
	sc_out<sc_uint<3> >  func;
	sc_out<bool>  reg_write_mux;
	sc_out<bool>  a_mux;
	sc_out<bool>  pc_mux;
	sc_out<bool>  addr_mux;

	sc_out<bool>  ldReg;
	sc_out<bool>  ldPC;
	sc_out<bool>  ldMAR;
	sc_out<bool>  ldMDSR;
	sc_out<bool>  ldIR;
	sc_out<bool>  ldMDLR;

	sc_out<bool>  mem_rd;
	sc_out<bool>  mem_wrt;

	sc_in<sc_int<32> > ir;
	sc_in<bool>       zero;

	sc_in<bool> reset;
	sc_in<bool> clk;

	void ctrl_comb();
	void ctrl_regs();

	// Local signals
	//   declared here to be visible in the waveforms.
	int opcode, subfunct;

	sc_signal<sc_int<3> > cur_cycle, next_cycle;
	sc_signal<bool>  halt;

	sc_trace_file *tf;

	// Cycle counter (for reporting at end of simulation)
	long cycle_count;

	SC_CTOR( controlUnit ) {
		SC_METHOD( ctrl_comb );
		sensitive << cur_cycle << ir << zero;

		SC_THREAD( ctrl_regs );
		sensitive_pos << clk << reset;

		halt = false;
	}
};
