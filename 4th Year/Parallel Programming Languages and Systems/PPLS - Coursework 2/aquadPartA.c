/* Program by s0943941
	Program used to approximate the area between two points (A and B), using MPI_Rsend and MPI_Recieve instead of using recursive calls. Models parallel procressing. 
	This program uses the stack given without modifications. 
	The farmer consists of an array to keep track of which processes are working, a stack to hold the data needed to calculate the area, and a do-while loop to perform the work. The worker will assign tasks (and update the working array) as long as there is a free worker and information on the stack. The sending of messages is done in process order, which has the drawback that, during the first few calls, the first process will do the majority of the work. This process uses MPI_Rsend, which requires that there is an active receiver. Then, once there's nothing on the stack or there's no free workers, the farmer will wait to receive information back using MPI_Recv. It checks the tag of the received message - if it indicates that its a function call, then its recursive and needs to be pushed onto the stack. This received message will have three data values - left, mid and right, which are split into two tuples and pushed back onto the stack. If the tag received in a sum tag, then it knows it is the calculated value and can add this to its area variable. After receiving a message, the farmer knows this process isn't working, so it changes the working array to indicate this, and decreases the amount of workers working. The loop will continue until no process is working and the stack is empty. Once all the workers have returned values, the farmer finishes the process by sending a 'finish' message to each of the workers outside of the loop, and returns the area. 	
	One improvement to the code base would have the farmer immediately send back the received data to the worker it received its information from. However, this improvement seemed too similar to the explicitly forbidden 'do not allow the worker to keep a single task', so it was not implemented. 
	The worker consists of a loop that will continue to receive until told to terminate via message. It waits to receive a message using MPI_Recv, and enters the while loop if that tag is not the terminating tag. It then computes the quad function, using an adapted quad function to calculate all the necessary values (it only receives two values - left and right - from the farmer, from which it can compute everything else). If the computed values are too large (bigger than epsilon), then it returns to the farmer three values (left, mid, and right) with the function tag (to indicate to the farmer to place it back on the stack). If it is less than epsilon, then it has successfully approximately a sum for that region, and returns a single value with the sum tag (to indicate to the farmer to add it to the area calculation). It then waits to receive a new task. This process continues checking indefinitely until the received tag indicates NO_MORE_TASKS, which means the area has been calculated and the worker can stop. After each receive call, the process usleep(SlEEPTIME) is called to emulate parallel processes (although it does not seem to make a difference on the number of calls if removed on DICE). 
	MPI: I chose to implement the farmer's send as an Rsend. This was done because it sends synchronously only if there's an awaiting receiver. Given how the program has been structured, there must be an awaiting receiver if it is able to send, so it makes sense to send it with a fast sender. Conversely, on the worker's side, when the message is ready to be sent, it is best to ensure that the message will be received using a different send command, because this can occur at any time. In this case, I used MPI_Ssend (as an extra precaution with synchronicity), but I could have used MPI_Send to the same effect. The program also works if the farmer implements MPI_Send. 
	For receiving, I used MPI_Recv for both its simplicity and to guarantee that the message would be fully received before continuing. I probably would be able to use a combination of Iprobe/Probe to continuously check for messages while receiving, but with this code, there's nothing else that needs doing, so it would continuously be waiting. If the program had other values to compute, or another task to accomplish, it might be better to use the Probe functionality to check for a message. 
	The results are evenly distributed, and the resulting area is correct according to the sequential program. 
	Area=7583461.801486

	Tasks Per Process
	0	1	2	3	4	
	0	1646	1640	1640	1641
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
#define FUNCTION 2
#define NOMORETASKS 3
#define QUAD 4

#define SLEEPTIME 1

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
	//Create a new stack and push the starting data onto it
	stack *stack = new_stack();
	double initial[2] = {A, B};
	push(initial, stack);

	//Variables to place in sending and receiving messages
	double *data;	
	double receive[3];
	
	//Keeping track of how many workers are working (initially none) and boolean array indicating if a particular worker is working
	int working [numprocs-2];
	int procsWorking = 0;
	int i;
	for (i=0; i<numprocs-2; i++) 
		working[i] = 0;
	
	//Message status information
	MPI_Status status;
	int tag;

	//Area to be approximated
	double areaSum = 0;
	
	//Main loop
	do {
		//Loop through workers (i=1). If there's work to do on the stack, and that worker isn't working, send it work
		for (i=1; i<numprocs && !(is_empty(stack)); i++) {
			if(!working[i-1]) {

				//Fetch data (needs to be freed) and send a message with QUAD tag, indicating it needs to calculate approximation
				data = pop(stack);
				MPI_Rsend(data, 2, MPI_DOUBLE, i, QUAD, MPI_COMM_WORLD);
				free(data);

				//Increment amount of work done, and update the worker's status as working
				tasks_per_process[i]++;
				procsWorking++;
				working[i-1] = 1;
			}
		}

		//Wait for a receive call, update status and tag
		MPI_Recv(&receive, 3, MPI_DOUBLE, MPI_ANY_SOURCE, MPI_ANY_TAG, MPI_COMM_WORLD, &status);
		i = status.MPI_SOURCE;
		tag = status.MPI_TAG;

		//If tag indicates that its a recursive call, push it back onto stack (only three values returned)
		if(tag == FUNCTION) {
			//Mid = receive[1] => Needed in both
			double pushleft[2] = {receive[0], receive[1]};
			push(pushleft, stack);

			double pushright[2] = {receive[1], receive[2]};
			push(pushright, stack);
		} 

		//If tag indicates that its a sum call, add it onto the area (only one value returned)
		else if (tag == SUM) {
			areaSum += receive[0];
		}
	
		//We've received something - decrease amount working, and set the worker from who we received the message to be idle
		procsWorking--;
		working[i-1] = 0;

	} while(procsWorking > 0 || !(is_empty(stack)));
	//Loop until there is nothing on the stack and no more processes are working

	//Send each workers a tag to finish (data meaningless)
	for (i = 1; i<numprocs; i++) {
		MPI_Rsend(&data, 0, MPI_DOUBLE, i, NOMORETASKS, MPI_COMM_WORLD);
	}
	
	//Return area
	return areaSum;
}



void worker(int mypid) {
	//Variables to hold incoming data - message status, and task information
	double task[2];
	MPI_Status status;

	//Waits to receive something from the farmer
	MPI_Recv(&task, 2, MPI_DOUBLE, FARMER, MPI_ANY_TAG, MPI_COMM_WORLD, &status);

	//Needed to emulate parallel processes
	usleep(SLEEPTIME);

	//Response tag is given by status of incoming message
	int tag = status.MPI_TAG;

	//Repeat until received message indicates 'no more tasks'
	while (tag != NOMORETASKS) {
		//Recreate quad function within worker. Left and right are given by the farmer
		double left = task[0];
		double right = task[1];
		double fleft = F(left);
		double fright = F(right);
		double lrarea = (fleft+fright) * (right-left)/2;
		double mid, fmid, larea, rarea;
	
		mid = (left + right) / 2;
		fmid = F(mid);
		larea = (fleft + fmid) * (mid - left) / 2;
		rarea = (fmid + fright) * (right - mid) / 2;

		//If the area is too large, send back left, right and mid to be put back on stack (aka recursive FUNCTION call)
		//Else, send back the sum
  		if( fabs((larea + rarea) - lrarea) > EPSILON ) {
			double result[3] = {left, mid, right};
			MPI_Ssend(&result, 3, MPI_DOUBLE, FARMER, FUNCTION, MPI_COMM_WORLD);
		} else {
			double result[1] = {larea+rarea};
			MPI_Ssend(&result, 1, MPI_DOUBLE, FARMER, SUM, MPI_COMM_WORLD);
		}

		//Waits for something from the farmer, waits, then checks the tag. Either terminates or repeats process
		MPI_Recv(&task, 2, MPI_DOUBLE, FARMER, MPI_ANY_TAG, MPI_COMM_WORLD, &status);
		usleep(SLEEPTIME);
		tag = status.MPI_TAG;
	}
	
}
