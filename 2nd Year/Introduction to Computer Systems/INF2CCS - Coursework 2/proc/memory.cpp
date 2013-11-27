#include "systemc.h"
#include "defines.h"
#include "memory.h"
#include <stdio.h>


sc_int<32> memory::addrSel()
{
	// Memory address mutliplexor
	switch(addr_mux.read()){
	case ADDR_PC:
	    return pc;
	case ADDR_MAR:
	    return mar;
	}
	printf("Memory address mux out of range\n");
	exit(1);
}

void memory::memRd()
{
	FILE *fp;
	int i = 0;
	int mem_word;
	char line[256];

	addr = addrSel();

	if (reset) {
		// LOAD MEMORY
		fp = fopen(fname, "r");
		while (fscanf(fp, "%s", line) != EOF) {
			if (line[0] == '%') {
				if (sscanf(line, "%%%x", &mem_word) == 1)
					mem[i++] = mem_word;
			}
		}
		fclose(fp);
		mem_data = 0;
	} else if (mem_rd) {
		if ((int) addr < 0 || (int) addr > 1024) {
			printf("Memory read out of range at address %x\n", (int) addr);
			//sc_stop();
		}
		mem_data = mem[addr.range(9,2)];
	}
}

void memory::memWr()
{
	addr = addrSel();
	if (mem_wrt) {
		if ((int) addr < 0 || (int) addr > 1024) {
			printf("Memory access out of range at address %x\n", (int) addr);
			//sc_stop();
		}
		mem[addr.range(9,2)] = data_in;
	}
}

