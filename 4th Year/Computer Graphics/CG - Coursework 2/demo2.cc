#include <iostream>
#include <fstream>
#include <cstring>
#include <GL/glut.h>
#include "demo1.h"
#include <cfloat>

using namespace std;

//window resolution (default 640x480)
int nRows = 640;
int nCols = 480; 

//The mesh global variableglutPostRedisplay
TriangleMesh trig;
vector < vector < float > > zbuffer;

//Point Manipulation
int xtranslate = 0;
int ytranslate = 0;
float scale = 1;
float rotationangley = 0;
float rotationanglex = -0.436332;
//-0.349065
float rotationanglez = 0;

//Light Manipulation
int lightYPos = 2;
int lightXPos = 4;
int specLighting = 100;

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

//Create pixel
void setPixel(int px, int py) 
{
	glBegin(GL_POINTS);
		glVertex2i(px, py);
	glEnd();
}

//Create pixel of a particular color
void setPixelBright(int px, int py, float red, float green, float blue) 
{
	glBegin(GL_POINTS);
		glColor3f(red, green, blue);
		glVertex2i(px, py);
	glEnd();
}

//Create pixel 
void setPixelTransparent(int px, int py, float red, float green, float blue, float alpha) 
{
	glBegin(GL_POINTS);
		glColor4f(red, green, blue, alpha);
		glVertex2i(px, py);
	glEnd();
}

//Line algorithm - ignore
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

//Line algorithm - ignore
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

//Line algorithm - ignore
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
	double intersection = yend + gradient;
	xend = round(x2);
	yend = y2 + gradient * (xend - x2);
	xgap  =1 - modf(x2 + .5, &intpart);
	int xpxl2 = xend;
	holder = modf(yend, &intpart);
	int ypxl2 = intpart;

}

//Calculate the bounding box of a triangle's points (returning the integer values for the pixels)
void boundingBox(float x1, float x2, float x3, float y1, float y2, float y3, int & minx, int & maxx, int & miny, int & maxy)
{
	minx = (int) floor(min(min(x1, x2), x3));
	maxx = (int) ceil(max(max(x1, x2), x3));
	miny = (int) floor(min(min(y1, y2), y3));
	maxy = (int) ceil(max(max(y1, y2), y3));
}

//A check if the barycentric coordinates is between 0 and 1
bool isBetween01(float t)
{
	return (0 <= t) && (t <= 1);
}

//Calculate the barycentric coordinates for a triangle
bool baryCoord(int x, int y, Vector3f v1, Vector3f v2, Vector3f v3, float & lambda1, float & lambda2, float & lambda3) 
{
	//Function taken from Wikipedia
	float det = (v2[1] - v3[1]) * (v1[0] - v3[0]) + (v3[0]-v2[0])*(v1[1]-v3[1]);

	//Calculate weights of the triangle vertices
	lambda1 = float((v2[1] - v3[1]) * (((float)x) - v3[0]) + (v3[0] - v2[0]) * (((float)y) - v3[1])) / det;
	lambda2 = float((v3[1] - v1[1]) * (((float)x) - v3[0]) + (v1[0] - v3[0]) * (((float)y) - v3[1])) / det;
	lambda3 = 1 - lambda1 - lambda2;

	//Only useful if all the points are between 0 and 1 (means its within triangle)
	return isBetween01(lambda1) && isBetween01(lambda2) && isBetween01(lambda3);
}

Vector3f normalCalculate(float lambda1, float lambda2, float lambda3, Vector3f norm1, Vector3f norm2, Vector3f norm3) 
{	
	//Calculate the normal of a series of vectors
	return (norm1 * lambda1) + (norm2 * lambda2) + (norm3 * lambda3);
}

//Depth buffer
void initZbuffer() {
	//Empty it from the previous draw
	zbuffer.clear();

	for (int i=0; i< nRows; i++) 
	{
		vector <float> vecFloat;
		for (int j=0; j<nCols; j++)
		{
			//For each row and column, set the value to be the largest
			vecFloat.push_back(FLT_MAX);
		}
		zbuffer.push_back(vecFloat);	
	}
}

//Calculate the normal vector of a triangle 
Vector3f calculateTriangleNormal(Vector3f triangle1, Vector3f triangle2, Vector3f triangle3) 
{
	Vector3f temp1 = triangle2 - triangle1;
	Vector3f temp2 = triangle3 - triangle1;

	Vector3f normal = Vector3f(	(temp1[1] * temp2[2]) - (temp1[2] * temp2[1]), 
								(temp1[2] * temp2[0]) - (temp1[0] * temp2[2]), 
								(temp1[0] * temp2[1]) - (temp1[1] * temp2[0]));
	normal /= normal.getLength();
	return normal;
}

//Perform transformations of the teapot (note: does not return the values to the trig)
void transformation ( Vector3f & vec1, Vector3f & vec2, Vector3f & vec3)
{
//Rotate the values
		vec1[0] = vec1[0] * cos(rotationangley) - vec1[2] * sin(rotationangley);
		vec1[2] = vec1[0] * sin(rotationangley) + vec1[2] * cos(rotationangley);

		vec2[0] = vec2[0] * cos(rotationangley) - vec2[2] * sin(rotationangley);
		vec2[2] = vec2[0] * sin(rotationangley) + vec2[2] * cos(rotationangley);

		vec3[0] = vec3[0] * cos(rotationangley) - vec3[2] * sin(rotationangley);
		vec3[2] = vec3[0] * sin(rotationangley) + vec3[2] * cos(rotationangley);

		vec1[1] = vec1[1] * cos(rotationanglex) - vec1[2] * sin(rotationanglex);
		vec1[2] = vec1[1] * sin(rotationanglex) + vec1[2] * cos(rotationanglex);

		vec2[1] = vec2[1] * cos(rotationanglex) - vec2[2] * sin(rotationanglex);
		vec2[2] = vec2[1] * sin(rotationanglex) + vec2[2] * cos(rotationanglex);

		vec3[1] = vec3[1] * cos(rotationanglex) - vec3[2] * sin(rotationanglex);
		vec3[2] = vec3[1] * sin(rotationanglex) + vec3[2] * cos(rotationanglex);

		
		vec1[0] = vec1[0]*cos(rotationanglez) - vec1[1]*sin(rotationanglez);
		vec1[1] = vec1[0]*sin(rotationanglez) + vec1[1]*cos(rotationanglez);
		
		vec2[0] = vec2[0]*cos(rotationanglez) - vec2[1]*sin(rotationanglez);
		vec2[1] = vec2[0]*sin(rotationanglez) + vec2[1]*cos(rotationanglez);

		vec3[0] = vec3[0]*cos(rotationanglez) - vec3[1]*sin(rotationanglez);
		vec3[1] = vec3[0]*sin(rotationanglez) + vec3[1]*cos(rotationanglez);	

		//Translate the values
		vec1[0] += xtranslate;
		vec2[0] += xtranslate;
		vec3[0] += xtranslate;
		
		vec1[1] += ytranslate;
		vec2[1] += ytranslate;
		vec3[1] += ytranslate;

		//Scale the values
		vec1[0] *= scale;
		vec1[1] *= scale;

		vec2[0] *= scale;
		vec2[1] *= scale;

		vec3[0] *= scale;
		vec3[1] *= scale;
}

//Shading algorithm for a single point - returns the colors for that pixel
Vector3f phong(int j, int k, Vector3f norm, float zvalue, Vector3f light, Vector3f viewvector, Vector3f lightintensity)
{
	//Constant brightness
	float brightness = .9f;
	
	//Specular calculation
	Vector3f lightToPoint = Vector3f(light[0] - j, light[1] - k, light[2]-zvalue);
	Vector3f reflection = lightToPoint.unitVec() - (norm.unitVec() * (2*(norm.unitVec().dot(lightToPoint.unitVec()))));
	float kspecular = .9f;
	float specular = kspecular * pow(reflection.dot(viewvector), specLighting);

	//Individual colors - red
	float reflect = .7f;
	float diffuse = (norm.unitVec()).dot(light.unitVec()) * reflect;
	float red = lightintensity[0] + (brightness * (diffuse + specular));

	//Blue
	reflect = .2f;
	diffuse = (norm.unitVec()).dot(light.unitVec()) * reflect;
	float blue = lightintensity[1] + (brightness * (diffuse + specular));

	//Green
	reflect = .4f;
	diffuse = (norm.unitVec()).dot(light.unitVec()) * reflect;
	float green = lightintensity[2] + (brightness * (diffuse + specular));

	//Return the calculated colors for the j-kth pixel
	return Vector3f(red, green, blue);
}

//Main drawing function
void drawTeapot(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f norm1, Vector3f norm2, Vector3f norm3, Vector3f light)
{
	//Ambient lighting - constant
	Vector3f lightintensity = Vector3f(.8, .8, .8);
	float reflectance = .2f;
	lightintensity *= reflectance;

	//View vector - where we are
	Vector3f viewvector = Vector3f(0,0,1);
	
	//Bounding box coordinates
	int minx, miny, maxx, maxy;

	//Find the bounding box for the current triangle
	boundingBox(v1[0], v2[0], v3[0], v1[1], v2[1], v3[1], minx, maxx, miny, maxy);
	
	//Iterate through the bounding box 
	for(int j = minx; j <=maxx; j++) 
	{
		for(int k = miny; k<=maxy; k++) 
		{
			float lambda1, lambda2, lambda3; 
			//If our pixel falls within triangle
			if(baryCoord(j, k, v1, v2, v3, lambda1, lambda2, lambda3)) 
			{
				//Calculate the normal vector (for phong shading)
				Vector3f norm = normalCalculate(lambda1, lambda2, lambda3, norm1, norm2, norm3);

				//Calculate the zvalue - make sure that the depth is less than the zbuffer before proceeding (closer to us)
				float zvalue = (v1[2] * lambda1) + (v2[2] * lambda2) + (v3[2] * lambda3);
				if (j+(int)(nRows/2) < zbuffer.size() && k+(int)(nCols/2) < zbuffer[0].size())
				{
					if (zbuffer[j+(int)(nRows/2)][k+(int)(nCols/2)] > zvalue)
					{
						//Use phong shading to find the color, change the zbuffer value, and set the pixel
						Vector3f color = phong(j, k, norm, zvalue, light, viewvector, lightintensity);
						zbuffer[j+(int)(nRows/2)][k+(int)(nCols/2)] = zvalue;
						setPixelBright(j, k, color[0], color[1], color[2]);	
					}
				}
			}
		}
	}
}

//Calculate where the shadow will lie on the plane
//Code adapted from http://www.ia.hiof.no/~borres/cgraph/explain/shadow/p-shadow.html
Vector3f calculateProjection(Vector3f planePoint, Vector3f projectPoint, Vector3f planeNorms, Vector3f light){

   //Calculate t
   float t = (planeNorms[0]*(planePoint[0] - projectPoint[0]) + planeNorms[1]*(planePoint[1] - projectPoint[1]) + planeNorms[2]*(planePoint[2] - projectPoint[2])) / (planeNorms[0]*light[0] + planeNorms[1]*light[1] + planeNorms[2]*light[2]);

   //Puts t into the equation (1)
   float x1 = projectPoint[0] + (t * light[0]);
   float x2 = projectPoint[1] + (t * light[1]);
   float x3 = projectPoint[2] + (t * light[2]);

   return Vector3f(x1, x2, x3);
}

//Draw the floor based on a color
void drawFloor(Vector3f v4, Vector3f v5, Vector3f v6, float r, float g, float b) 
{
	//Find bounding box of triangle
	int minx, maxx, miny, maxy;	
	boundingBox(v4[0], v5[0], v6[0], v4[1], v5[1], v6[1], minx, maxx, miny, maxy);

	for ( int j = minx; j < maxx; j++) 
	{
		for (int k = miny; k <maxy; k++)
		{
			float lambda1, lambda2, lambda3; 
			//Make sure point is in the triangle, and that the zbuffer is lower before drawing
			if(baryCoord(j, k, v4, v5, v6, lambda1, lambda2, lambda3)) {
				float zvalue = (v4[2] * lambda1) + (v5[2] * lambda2) + (v6[2] * lambda3);
				if (j+(int)(nRows/2) < zbuffer.size() && k+(int)(nCols/2) < zbuffer[0].size())
				{
					if (zvalue < zbuffer[j+(int)(nRows/2)][k+(int)(nCols/2)])
					{
						setPixelBright(j, k, r, g, b);
					}
				}
			}
		}
	}

}	

//Separate function for drawing the shadows - nearly identical to drawfrom Bounding Box
void drawShadow(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f light)
{
	//View vector
	Vector3f viewvector = Vector3f(0,0,1);
	
	//Bounding box coordinates
	int minx, miny, maxx, maxy;

	//Find the bounding box for the current triangle
	boundingBox(v1[0], v2[0], v3[0], v1[1], v2[1], v3[1], minx, maxx, miny, maxy);
	
	//Iterate through the bounding box 
	for(int j = minx; j <=maxx; j++) 
	{
		for(int k = miny; k<=maxy; k++) 
		{
			float lambda1, lambda2, lambda3; 
			if(baryCoord(j, k, v1, v2, v3, lambda1, lambda2, lambda3)) 
			{
				float zvalue = (v1[2] * lambda1) + (v2[2] * lambda2) + (v3[2] * lambda3);
				//Make sure point is in the triangle, and that the zbuffer is lower before drawing
				if (j+(int)(nRows/2) < zbuffer.size() && k+(int)(nCols/2) < zbuffer[0].size())
				{
					if (zbuffer[j+(int)(nRows/2)][k+(int)(nCols/2)] > zvalue)
					{
						zbuffer[j+(int)(nRows/2)][k+(int)(nCols/2)] = zvalue;
						setPixelTransparent(j, k, 0, 0, 0, 1);
					}
				}
			}
		}
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
	
	//Light vector
	Vector3f light = Vector3f(lightXPos, lightYPos, -10);

	Vector3f v1,v2,v3; // Vector objects to hold the returned vertex values
	Vector3f norm1, norm2, norm3; //Vector objects to hold the vertices norm values

	//Clear the depth buffer on the z-axis
	initZbuffer();

	//Lowest y-point on the untransformed teapot
	float lowesty = FLT_MAX;

	//Create teapot from triangles
	for (int i = 0 ; i < trignum; i++)  
	{

		/* getting the vertices of the triangle i */
		trig.getTriangleVertices(i, v1,v2,v3); //For the Java programmers, v1,v2 and v3 are the return values here.

		//Find the corresponding normals of the selected vertices 
		trig.getTriangleNorms(i, norm1, norm2, norm3);

		//Calculate lowest point on the teapot - used for the floor		
		float low = min(min(v2[1], v1[1]), v3[1]);
		if (low < lowesty) 
		{
			lowesty = low;
		}

		//Adjust vertices for transformations
		transformation(v1,v2, v3);
		transformation(norm1, norm2, norm3);

		//Backface culling calculation
		Vector3f edge1 = v3 - v1;
		Vector3f edge2 = v3 - v2;
		Vector3f cull = edge1.cross(edge2);
	
		//if you don't need to cull, draw the teapot
		if (cull.dot(Vector3f(0,0,1)) < 0)
		{
			drawTeapot(v1, v2, v3, norm1, norm2, norm3, light);
		}
	}
	
	//Vectors for the triangle - v4 and v5 are the same as v8 and v9, but I need to perform the transformation function on three vertices
	Vector3f v4 = Vector3f(-200, lowesty-2, 200);
	Vector3f v5 = Vector3f(200, lowesty-2, -200);
	Vector3f v6 = Vector3f(-200, lowesty-2, -200);
	Vector3f v7 = Vector3f(200, lowesty-2, 200);
	Vector3f v8 = Vector3f(-200, lowesty-2, 200);
	Vector3f v9 = Vector3f(200, lowesty-2, -200);



	//Adjust for current view
	transformation(v4,v5, v6);
	transformation(v7, v8, v9);
	
	drawFloor(v4, v5, v6, .9f, .9f, .9f);
	drawFloor(v8, v9, v7, .1f, .1f, .5f);

	Vector3f planeNorm = calculateTriangleNormal(v4, v5, v6);


	//Shadow pass
	for (int i = 0 ; i < trignum; i++)  
	{

		/* getting the vertices of the triangle i */
		trig.getTriangleVertices(i, v1,v2,v3); //For the Java programmers, v1,v2 and v3 are the return values here.
		transformation(v1,v2,v3);

		//Calculate projection of shadow onto plane (v4) using the triangle vertices, light vector, and normal vector for plane
		Vector3f shadow1 = calculateProjection(v4, v1, planeNorm, light);
		Vector3f shadow2 = calculateProjection(v4, v2, planeNorm, light);
		Vector3f shadow3 = calculateProjection(v4, v3, planeNorm, light);

		//Backface culling
		Vector3f edge1 = shadow3 - shadow1;
		Vector3f edge2 = shadow3 - shadow2;
		Vector3f cull = edge1.cross(edge2);

		//Draw the teapot's shadow (similar to how the teapot was drawn)
		if (cull.dot(Vector3f(0,0,1)) < 0)
		{
			drawShadow(shadow1, shadow2, shadow3, light);
		}
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
		cout << rotationanglex << endl;
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

	if (key == '8')
	{
		lightYPos += 1;
		cout << lightXPos << " " << lightYPos << endl;
	}

	if (key == '6')
	{
		lightXPos += 1;
		cout << lightXPos << " " << lightYPos << endl;
	}

	if (key == '4')
	{
		lightXPos -= 1;
		cout << lightXPos << " " << lightYPos << endl;
	}

	if (key == '5')
	{
		lightYPos -= 1;
		cout << lightXPos << " " << lightYPos << endl;
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

	trig.normalCalc();
	
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
