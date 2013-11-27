#include <iostream>
#include <fstream>
#include <cstring>
#include <GL/glut.h>
#include "demo1.h"

using namespace std;

//window resolution (default 640x480)
int nRows = 640;
int nCols = 480; 

//The mesh global variableglutPostRedisplay
TriangleMesh trig;

//Point Manipulation
int xtranslate = 0;
int ytranslate = 0;
float scale = 1;
float rotationangley = 0;
float rotationanglex = 0;
float rotationanglez = 0;

//Modes
int mode = 1;
int rotationmode = 1;



//This function loads an obj format file
//This is a utility and should not have to be modified for teapot.obj (the assignment).
void TriangleMesh::loadFile(char * filename)
{
	ifstream f(filename); // the file stream, for reading in a file

	//if the file was unabled to be opened (ifstream pointer is NULL), exit with error message.
	if (f == NULL) {
		cerr << "failed reading polygon data file: " << filename << endl;
		exit(1);
	}

	char buf[1024];
	char header[100];
	float x,y,z;
	float xmax,ymax,zmax,xmin,ymin,zmin;
	int v1, v2, v3, n1, n2, n3;

	xmax =-10000; ymax =-10000; zmax =-10000;
	xmin =10000; ymin =10000; zmin =10000;
	Vector3f av;
	av[0] = av[1] = av[2] = 0.f;

	while (!f.eof()) {
		    f.getline(buf, sizeof(buf));
		    sscanf(buf, "%s", header);  

		    if (strcmp(header, "v") == 0) {
			sscanf(buf, "%s %f %f %f", header, &x, &y, &z);  

			_v.push_back(Vector3f(x,y,z));

			av[0] += x; av[1] += y; av[2] += z;

			if (x > xmax) xmax = x;
			if (y > ymax) ymax = y;
			if (z > zmax) zmax = z;

			if (x < xmin) xmin = x;
			if (y < ymin) ymin = y;
			if (z < zmin) zmin = z;
		    }
		    else if (strcmp(header, "f") == 0) {
			sscanf(buf, "%s %d %d %d", header, &v1, &v2, &v3);
			
			Triangle trig(v1-1, v2-1, v3-1);
			_trig.push_back(trig);
		    }
 	}

	_xmin = xmin; _ymin = ymin; _zmin = zmin;
	_xmax = xmax; _ymax = ymax; _zmax = zmax;

	float range; 
	if (xmax-xmin > ymax-ymin) range = xmax-xmin;
	else range = ymax-ymin;

	for (int j = 0; j < 3; j++) av[j] /= _v.size();

	for (int i = 0; i < _v.size(); i++) 
	{
		for (int j = 0; j < 3; j++) _v[i][j] = (_v[i][j]-av[j])/range*400;  
	}
	cout << "Number of triangles: " << _trig.size() << ", number of vertices: " << _v.size() << endl;
	f.close();
};

void setPixel(int px, int py) 
{
	glBegin(GL_POINTS);
		glVertex2i(px, py);
	glEnd();
}

void setPixelBright(int px, int py, int brightness) 
{
	glBegin(GL_POINTS);
		glColor3f(brightness,brightness,brightness);
		glVertex2i(px, py);
	glEnd();
}

void dda(int x1, int y1, int x2, int y2) 
{
	float x, y;
	int dx = x2 - x1;
	int dy = y2 - y1;
	int n = max(abs(dx), abs(dy));
	float dt = n, dxdt = dx/dt, dydt = dy/dt;
	x = x1;
	y = y1;
	while (n--) 
	{
		setPixel(round(x), round(y));
		x += dxdt;
		y += dydt;
	}
}

void bresenham(int x1, int y1, int x2, int y2)
{
	int deltax = abs(x2 - x1);
	int deltay = abs(y2 - y1);
	int sx;
	int sy;
	if (x1 < x2) {
		sx = 1;
	} else {
		sx = -1;
	}

	if (y1 < y2) {
		sy = 1;
	} else {
		sy = -1;
	}
	int error = deltax - deltay;
	while(true) {
		setPixel(x1, y1);
		if(x1==x2 && y1 == y2) {
			break;
		}
		int e2 = 2*error;
		if (e2 > -deltay) {
			error -= deltay;
			x1 += sx;
		}
		if (e2 < deltax) {
			error += deltax;
			y1 += sy;
		}
	}
}

void wu(int x1, int y1, int x2, int y2)
{
	bool steep = abs(y2-y1) > abs(x2- x1);
	if (steep) {
		swap(x1, y1);
		swap(x2, y2);
	} 
	if (x1 > x2) {
		swap(x1, x2);
		swap(y1, y2);
	}

	int dx = x2 - x1;
	if (dx == 0) {
		return;
	}
	int dy = y2 -y1;
	double gradient = dy/dx;
	double xend = round(x1);
	double yend = y1 + gradient * (xend -x1);
	float intpart;
	double xgap = 1 - modf(x1 + .5, &intpart);
	int xpxl1 = xend;
	double holder = modf(yend, &intpart);
	int ypxl1 = intpart;
	if (steep) {
		setPixelBright(ypxl1, xpxl1, (1 - modf(yend, &intpart)) * xgap);
		setPixelBright(ypxl1+1, xpxl1, modf(yend, &intpart) * xgap);
	} else {
		setPixelBright(xpxl1, ypxl1, (1 - modf(yend, &intpart)) * xgap);	
		setPixelBright(xpxl1, ypxl1+1, modf(yend, &intpart) * xgap);
	}

	double intersection = yend + gradient;
	xend = round(x2);
	yend = y2 + gradient * (xend - x2);
	xgap  =1 - modf(x2 + .5, &intpart);
	int xpxl2 = xend;
	holder = modf(yend, &intpart);
	int ypxl2 = intpart;
 	if (steep) {
		setPixelBright(ypxl2, xpxl2, (1 - modf(yend, &intpart)) * xgap);
		setPixelBright(ypxl2+1, xpxl2, modf(yend, &intpart) * xgap);
	} else {
		setPixelBright(xpxl2, ypxl2, (1 - modf(yend, &intpart)) * xgap);	
		setPixelBright(xpxl2, ypxl2+1, modf(yend, &intpart) * xgap);
	}
	for(int x=xpxl1 +1; x<xpxl2; x++) {
		holder = modf(intersection, &intpart);
		if (steep) {
			setPixelBright(intpart, x, 1- holder);	
			setPixelBright(intpart+1, x, holder);
		} else {
			setPixelBright(x, intpart, 1- holder);	
			setPixelBright(x, intpart+1, holder);
		}
		intersection += gradient;
	}
}	

/*--- Display Function ---*/
//The main display function.
//This allows you to draw pixels onto the display by using GL_POINTS.
//Drawn every time an update is required.
//Students: This is the main file you'll need to modify or replace.
//The idea with this example function is the following:
//1)Clear the screen so we can draw a new frame
//2)Calculate the vertex points for each triangle and draw them (vertices only)
//3)Flush the pipeline so that the instructions we 
void demoDisplay()
{
	glClear(GL_COLOR_BUFFER_BIT); // Clear OpenGL Window

	int trignum = trig.trigNum(); //Number of triangles

	
	Vector3f v1,v2,v3; // Vector objects to hold the returned vertex values

	glColor3f(1,1,1);  // The colour we will be drawing is white (red = 1, green = 1, blue = 1).

	//
	// for each triangle, get the location of the vertices,
	// project them on the xy plane (remove z value), and color the corresponding pixel white
	//
	for (int i = 0 ; i < trignum; i++)  
	{
		/* getting the vertices of the triangle i */
		trig.getTriangleVertices(i, v1,v2,v3); //For the Java programmers, v1,v2 and v3 are the return values here.
		
		//Rotate the values
			v1[0] = v1[0] * cos(rotationangley) - v1[2] * sin(rotationangley);
			v1[2] = v1[0] * sin(rotationangley) + v1[2] * cos(rotationangley);

			v2[0] = v2[0] * cos(rotationangley) - v2[2] * sin(rotationangley);
			v2[2] = v2[0] * sin(rotationangley) + v2[2] * cos(rotationangley);

			v3[0] = v3[0] * cos(rotationangley) - v3[2] * sin(rotationangley);
			v3[2] = v3[0] * sin(rotationangley) + v3[2] * cos(rotationangley);

			v1[1] = v1[1] * cos(rotationanglex) - v1[2] * sin(rotationanglex);
			v1[2] = v1[1] * sin(rotationanglex) + v1[2] * cos(rotationanglex);

			v2[1] = v2[1] * cos(rotationanglex) - v2[2] * sin(rotationanglex);
			v2[2] = v2[1] * sin(rotationanglex) + v2[2] * cos(rotationanglex);

			v3[1] = v3[1] * cos(rotationanglex) - v3[2] * sin(rotationanglex);
			v3[2] = v3[1] * sin(rotationanglex) + v3[2] * cos(rotationanglex);

		
		v1[0] = v1[0]*cos(rotationanglez) - v1[1]*sin(rotationanglez);
		v1[1] = v1[0]*sin(rotationanglez) + v1[1]*cos(rotationanglez);
		
		v2[0] = v2[0]*cos(rotationanglez) - v2[1]*sin(rotationanglez);
		v2[1] = v2[0]*sin(rotationanglez) + v2[1]*cos(rotationanglez);

		v3[0] = v3[0]*cos(rotationanglez) - v3[1]*sin(rotationanglez);
		v3[1] = v3[0]*sin(rotationanglez) + v3[1]*cos(rotationanglez);	

		//Translate the values
		v1[0] += xtranslate;
		v2[0] += xtranslate;
		v3[0] += xtranslate;
		
		v1[1] += ytranslate;
		v2[1] += ytranslate;
		v3[1] += ytranslate;

		//Scale the values
		v1[0] *= scale;
		v1[1] *= scale;

		v2[0] *= scale;
		v2[1] *= scale;

		v3[0] *= scale;
		v3[1] *= scale;
		
		//
		// An example:
		// Colouring the pixels at the vertex location 
		// (just doing parallel projectiion to the xy plane.) 
		// students: only use glBegin(GL_POINTS) for rendering the scene  
		// GL_LINES, GL_POLYGONS and similar may NOT be used in your submission.
		//
		glBegin(GL_POINTS);	
			glVertex2i((int)v1[0],(int)v1[1]);
			glVertex2i((int)v2[0],(int)v2[1]);
			glVertex2i((int)v3[0],(int)v3[1]);
			switch(mode) {
			default:
				dda((int)v1[0],(int)v1[1], (int)v2[0],(int)v2[1]);
				dda((int)v1[0],(int)v1[1], (int)v3[0],(int)v3[1]);
				dda((int)v2[0],(int)v2[1], (int)v3[0],(int)v3[1]);
				break;
			case 2:
				bresenham((int)v1[0],(int)v1[1], (int)v2[0],(int)v2[1]);
				bresenham((int)v1[0],(int)v1[1], (int)v3[0],(int)v3[1]);
				bresenham((int)v2[0],(int)v2[1], (int)v3[0],(int)v3[1]);
				break;
			case 3:
				wu((int)v1[0],(int)v1[1], (int)v2[0],(int)v2[1]);
				wu((int)v1[0],(int)v1[1], (int)v3[0],(int)v3[1]);
				wu((int)v2[0],(int)v2[1], (int)v3[0],(int)v3[1]);
				break;
			}
				
		glEnd();	
	}

	glFlush();// Output everything (write to the screen)
}


//This function is called when a (normal) key is pressed
//x and y give the mouse coordinates when a keyboard key is pressed
void demoKeyboardHandler(unsigned char key, int x, int y)
{
	if(key == 'm')
	{
        	cout << "Mouse location: " << x << " " << y << endl;
	}
	if(key == 'a')
	{
		xtranslate -= 5;
	}
	if (key == 'd') 
	{
		xtranslate += 5;
	}
	if (key == 'w')
	{
		ytranslate += 5;
	}
	if (key == 's') 
	{
		ytranslate -= 5;
	
	}
	if (key == 'j')
	{
		rotationmode = 1;
		rotationangley += ((5 * 3.14159265) / 180);
	}
	if (key == 'l')
	{
		rotationmode = 1;
		rotationangley -= ((5 * 3.14159265) / 180);
	}
	if (key == 'i')
	{
		rotationmode = 2;
		rotationanglex += ((5 * 3.14159265) /180);
	}
	if (key == 'k')
	{
		rotationmode = 2;
		rotationanglex -= ((5 * 3.14159265) /180);
	}
	if (key == 'q')
	{
		rotationmode = 3;
		rotationanglez += ((5 * 3.14159265) / 180);
	}
	if (key == 'e')
	{
		rotationmode = 3;
		rotationanglez -= ((5 * 3.14159265) / 180);
	}

	if (key == 'r')
	{
		scale *= 1.1;
	}
	if (key == 'f')
	{
		scale *= .9;
	}
	if (key == '1')	//DDA Algorithm
	{
		mode = 1;
	}
	if (key == '2') //Bresenham Algorithm
	{
		mode = 2;
	}

	glutDisplayFunc(demoDisplay);// Callback function
	glutPostRedisplay();

    cout << "Key pressed: " << key << endl;

}



//Program entry point.
//argc is a count of the number of arguments (including the filename of the program).
//argv is a pointer to each c-style string.
int main(int argc, char **argv)
{

    cout << "Computer Graphics Assignment 1 Demo Program" << endl;

	if (argc >  1)  
	{
		trig.loadFile(argv[1]);
	}
	else 
	{
        cerr << "Usage:" << endl;
		cerr << argv[0] << " <filename> " << endl;
		exit(1);
	}

	//initialise OpenGL
	glutInit(&argc, argv);
	//Define the window size with the size specifed at the top of this file
	glutInitWindowSize(nRows, nCols);
	//Create the window for drawing with the title "SimpleExample"
	glutCreateWindow("CG-CW1");
	//Apply a 2D orthographic projection matrix, allowing you to draw
	//directly to pixels
	gluOrtho2D(-nRows/2, nRows/2, -(float)nCols/2,  (float)nCols/2);

	//Set the function demoDisplay (defined above) as the function that
	//is called when the window must display
	glutDisplayFunc(demoDisplay);// Callback function
    	//similarly for keyboard input
	glutKeyboardFunc(demoKeyboardHandler);	

	//Run the GLUT internal loop
	glutMainLoop();// Display everything and wait
}
