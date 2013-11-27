s0943941 - Computer Graphics Readme

Press a,s,w,d to move the teapot about the screen.
Press 1 and 2 to switch between DDA and Bresenham's Midpoint Algorithm for easy comparison. 
Press i and k to rotate the teapot about the x axis
Press j and l to rotate the teapot about the y axis
Press r and f to scale the teapot towards and away from the camera

DDA algorithm taken from Lecture slides
Bresenham's Algorithm taken from pseudocode on Wikipedia

run with the commands: 
g++ -o demo1 demo1.cc -lglut -lGLU -lGL
./demo1 teapot.obj
