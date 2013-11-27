struct memory : sc_module {
	sc_out<sc_int<32> > mem_data;

	sc_in<sc_int<32> >  mar;
	sc_in<sc_int<32> >  pc;
	sc_in<bool>  addr_mux;

	sc_in<sc_int<32> >  data_in;

	sc_in<bool>  mem_rd;
	sc_in<bool>  mem_wrt;

	sc_in<bool> reset;
	sc_in<bool> clk;

	char *fname; // filename to load into memory.

    sc_int<32> addrSel();
	void memRd();
	void memWr();

	sc_int<32> mem[256];
	sc_int<32> addr;

	SC_CTOR( memory ) {
		SC_METHOD( memRd );
		sensitive << reset << mar << pc << addr_mux << mem_rd;

		SC_METHOD( memWr );
		sensitive_pos << clk;
	}
};
