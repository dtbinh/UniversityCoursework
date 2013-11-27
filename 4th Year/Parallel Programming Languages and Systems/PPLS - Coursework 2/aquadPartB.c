/* Program by s0943941
Program used to approximate the area between two points (A and B), using MPI_Send and MPI_Recieve instead of using recursive calls. Models parallel procressing. 
	This program uses the stack given without modifications. 
	Farmer: First subdivides the values into equal subranges, and sends these values to each worker in turn using MPI_Send. Waits until it has received something from each process, and then terminates. When it receives something (via MPI_Recv), it will have two values - the amount that the worker has calculated, and the number of times that the worker has performed its calculations. Because the worker finishes after returning its value, the farmer does not need to send a command to terminate the worker. 
	Worker: Creates a count pointer to keep track of how many times the function performs its work through its various iterations. Could have used a global variable for this, but felt this reflected the scope more accurately. Waits to receive something from the farmer (MPI_Recv), then uses the recursive quad function to calculate the area (incrementing the count whenever it successfully returns). This value, once calculated, is stored in a double and sent along with the count to the farmer. Because this is deterministic (each run will result in the same values for the code), the call usleep(SLEEPTIME) was deemed unnecessary (the order in which the workers finish does not matter).The worker, once it has returned its value, automatically finishes. 
	MPI: I used MPI_Send and MPI_Recv here for simplicity (since it was similar to the other code). I could have used scatter to send the values to each of the processes (this would have to tweaked slightly so that the scatter did not send any value to the farmer) and could have used gather to collect the values (the resulting code would have been around the same length and complexity). 
	The results are consistent with the values given in the assignment:
	Area=7583461.801486
	Tasks Per Process
	0	1	2	3	4	
	0	39	195	1059	5271	

*/

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <mpi.h>
#include "stack.h"

#define EPSILON 1e-3
#define F(arg)  cosh(arg)*cosh(arg)*cosh(arg)*cosh(arg)
#define A 0.0
#define B 5.0
#define FARMER 0
#define SUM 1
#define SPLIT 2

int *tasks_per_process;
double farmer(int);
void worker(int);

int main(int argc, char **argv ) {
  int i, myid, numprocs;
  double area, a, b;

  MPI_Init(&argc, &argv);
  MPI_Comm_size(MPI_COMM_WORLD,&numprocs);
  MPI_Comm_rank(MPI_COMM_WORLD,&myid);

  if(numprocs < 2) {
    fprintf(stderr, "ERROR: Must have at least 2 processes to run\n");
    MPI_Finalize();
    exit(1);
  }

  if (myid == 0) { // Farmer
    // init counters
    tasks_per_process = (int *) malloc(sizeof(int)*(numprocs));
    for (i=0; i<numprocs; i++) {
      tasks_per_process[i]=0;
    }
  }

  if (myid == 0) { // Farmer
    area = farmer(numprocs);
  } else { //Workers
    worker(myid);
  }

  if(myid == 0) {
    fprintf(stdout, "Area=%lf\n", area);
    fprintf(stdout, "\nTasks Per Process\n");
    for (i=0; i<numprocs; i++) {
      fprintf(stdout, "%d\t", i);
    }
    fprintf(stdout, "\n");
    for (i=0; i<numprocs; i++) {
      fprintf(stdout, "%d\t", tasks_per_process[i]);
    }
    fprintf(stdout, "\n");
    free(tasks_per_process);
  }
  MPI_Finalize();
  return 0;
}

double farmer(int numprocs) {
	//Setup receiving and sending variables
	double initial[2] = {A, B};	
	double receive[2];
	
	//Status and source for sending/receiving, as well as final area 
	MPI_Status status;
	int source;
	double areaSum = 0;
	int i;
	
	//Subdivide the region into groups based on amount of workers
	double subdivide = (initial[1] - initial[0])/(numprocs-1);

	//Counts how many the farmer is waiting to receive
	int toReceive = numprocs-1;
	
	//Sends each worker its subdivision
	for (i = 0; i<numprocs-1; i++) {
		double sendRange[2] = {i*subdivide, (i+1)*subdivide};
		MPI_Send(sendRange, 2, MPI_DOUBLE, i+1, SPLIT, MPI_COMM_WORLD);
	}

	//Receive from each in turn, decrease amount needed to finish, increase area from first value, and set the work done by the second value
	while(toReceive > 0) {
		MPI_Recv(&receive, 2, MPI_DOUBLE, MPI_ANY_SOURCE, MPI_ANY_TAG, MPI_COMM_WORLD, &status);
		source = status.MPI_SOURCE;
		areaSum += receive[0];
		tasks_per_process[source] = (int) receive[1];
		toReceive--;
	}

	return areaSum;
}

double quad(double left, double right, double fleft, double fright, double lrarea, int *count) {
	//Perform the quad recursive call (identical to the sequential quad function, except for count pointer) 
	double mid, fmid, larea, rarea;
	mid = (left + right) / 2;
	fmid = F(mid);
	larea = (fleft + fmid) * (mid - left) / 2;
	rarea = (fmid + fright) * (right - mid) / 2;
	if( fabs((larea + rarea) - lrarea) > EPSILON ) {
		larea = quad(left, mid, fleft, fmid, larea, count);
		rarea = quad(mid, right, fmid, fright, rarea, count);
	}
  	//If we are returning something, increase the count by one (needs to be dereferenced)
  	(*count)++;
	return (larea + rarea);
}

void worker(int mypid) {
	//Task to receive the initial data from the farmer
	double task[2];

	//Create count pointer that is incremented each time a recursive call is made
	int* count;
	count = malloc(sizeof(int));
	*count = 0;

	//Wait for farmer to send a task (doesn't need to check for tag, since this automatically terminates)
	MPI_Status status;
	MPI_Recv(&task, 2, MPI_DOUBLE, FARMER, MPI_ANY_TAG, MPI_COMM_WORLD, &status);

	//Get area by quad function, based on what we've received from farmer
	double area = quad(task[0], task[1], F(task[0]), F(task[1]), (F(task[0])+F(task[1])) * (task[1]-task[0])/2, count);

	//Send back the area calculated with the amount of calls done
	double result[2] = {area, (double) *count};
	MPI_Send(&result, 2, MPI_DOUBLE, FARMER, SUM, MPI_COMM_WORLD);	
	free(count);
}


