
struct datapath : sc_module {
    sc_out<bool> zero;
	sc_out<sc_int<32> >  ir;
	sc_out<sc_int<32> >  mar;
	sc_out<sc_int<32> >  pc;
	sc_out<sc_int<32> >  mdsr;

	sc_in<sc_int<32> >  mem_data;

	sc_in<sc_uint<2> >  wreg_addr_mux;
	sc_in<bool>  reg_write_mux;
	sc_in<bool>  a_mux;
	sc_in<sc_uint<3> >  b_mux;
	sc_in<sc_uint<3> >  func;
	sc_in<bool>  pc_mux;

	sc_in<bool>  ldReg;
	sc_in<bool>  ldPC;
	sc_in<bool>  ldMAR;
	sc_in<bool>  ldMDSR;
	sc_in<bool>  ldIR;
	sc_in<bool>  ldMDLR;

	sc_in<bool> clk;
	sc_in<bool> reset;

    // method to write values to the output ports
    void dp_regs();
    void dp_alu();
    void dp_rf_in();
    void dp_pcgen();
    void dp_rf_wa();
    
	sc_int<32> registers[32];  // The register file


	// Local signals
	//   declared here to be visible in the waveforms.
	sc_uint<5> a_addr, b_addr;
	sc_int<32> a, b;
	sc_signal<sc_int<32> > mdlr;
	sc_signal<sc_uint<5> > wreg_addr;
	sc_signal<sc_int<32> > alu;
	sc_signal<sc_int<32> > b_reg;
	sc_signal<sc_int<32> > next_pc;
	sc_signal<sc_int<32> > reg_write_data;

    //Constructor
    SC_CTOR( datapath ) {
	SC_THREAD( dp_regs); 
        sensitive_pos << reset;
		sensitive_pos << clk;

	SC_THREAD( dp_alu); 
 		sensitive << ir;
		sensitive << pc;
		sensitive << a_mux;
		sensitive << b_mux;
		sensitive << func;
		//sensitive << registers;
		// also sensitive to registers

	SC_THREAD( dp_rf_in); 
		sensitive << mdlr;
		sensitive << alu;
		sensitive << reg_write_mux;

	SC_THREAD( dp_pcgen); 
 		sensitive << ir;
		sensitive << pc;
		sensitive << alu;
		sensitive << pc_mux;

	SC_THREAD( dp_rf_wa); 
 		sensitive << ir;
		sensitive << wreg_addr_mux;

    }
};

