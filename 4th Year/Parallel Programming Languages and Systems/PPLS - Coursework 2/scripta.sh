/usr/lib64/openmpi/bin/mpicc -o aquadPartA aquadPartA.c stack.h stack.c -lm
/usr/lib64/openmpi/bin/mpirun -c 5 aquadPartA
