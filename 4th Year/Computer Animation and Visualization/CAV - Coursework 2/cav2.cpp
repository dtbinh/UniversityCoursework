#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <math.h>
using namespace std;

#include <GL/glut.h>
#define GLUT_KEY_ESCAPE 27
#ifndef GLUT_WHEEL_UP
    #define GLUT_WHEEL_UP 3
    #define GLUT_WHEEL_DOWN 4
#endif

#include "Vector.h"
#include "Matrix.h"
#include "Volume.h"

#define WIDTH 256
#define HEIGHT 256

float scale = 0.1f;
float rotation = 0.785398f;

static Volume* head = NULL;

int empty = 50;
int skin = 60;
int bone = 90;


Vector3 black = Vector3(0,0,0);
Vector3 skinColor = black;


Vector3 findColor(float density) {
	if (density <= scale * empty) {
		//Empty space/noise
		return Vector3(0,0,0);
	} 
	else if (density <= scale * skin) {
		//Skin
		return skinColor;
	} else if (density <= scale * bone) {
		//Bone lower bound
		return Vector3(0, 0, 1);
	} else {
		//Teeth
		return Vector3(1, 1, 1);
	} 
}

void Update() {
	glutPostRedisplay();
}

void Draw() {
  glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glBegin(GL_POINTS);
	//Iterate over each pixel on the screen
	for (int j=0; j<head->GetHeight(); j++) {
		for (int k=0; k < head -> GetDepth(); k++) {
			//Create values for ray cast
			Vector3 viewpoint = Vector3(512, HEIGHT/2, 100/2);
			Vector3 pixel = Vector3(0,j,k);

			//Setup display buffer
			float accumulatedAlpha = 0;
			Vector3 render = Vector3(0,0,0);
			for (int i=0; i < head -> GetWidth()-50; i++) {

				//Ray cast
				Vector3 planePoint = Vector3(i,2,7);
				Vector3 planeNormal = Vector3(-1,0,0);
				Vector3 direction = pixel - viewpoint;
				
				//Find intersection of ray and plane
				float d = Vector3::Dot((pixel - planePoint), planeNormal) / Vector3::Dot(direction, planeNormal);
				Vector3 intersection = (direction * d) + pixel;

				//Rotate intersected points based on user data
				Matrix_3x3 rotate = Matrix_3x3::RotationZ(rotation);
				Vector3 temp = Vector3(-intersection[0],intersection[1],intersection[2]);

				//Shift points from middle of screen to origin, rotate about origin, then shift points back
				Vector3 shift = Vector3(128, 128, 0);
				temp -= shift;
				temp = rotate * temp;
				temp += shift;

				//Use rotated points
				float x = temp[0];
				float y = temp[1];
				float z = temp[2];
				
				//Determine if value is within data
				float val = 0;
				if (x >= 0  && y >=0 && z >= 0 && x < WIDTH && y < HEIGHT && z < 100) {
					val = scale * head->Get(x,y, z);
				} 

				//Calculate accumulated alpha and render buffer based off simple light (one color, adjusted by alpha) 
				float alpha = (val/255.0);
				accumulatedAlpha = alpha + ((1 - alpha) * accumulatedAlpha);
				Vector3 light = findColor(val);
				light *= (1 - accumulatedAlpha)*alpha;
				render += light;

				//Early termination
				if (accumulatedAlpha >= .95) {
					break;
				}
			}

			//Color and drawn render buffer
			glColor3f(render.r(), render.g(), render.b());
			glVertex3f(j,k,0);
		}	
  	}	
	glEnd();
	glFlush();
	glutSwapBuffers();
}


void KeyEvent(unsigned char key, int x, int y) {
	switch(key) {
	    case GLUT_KEY_ESCAPE:
	      exit(EXIT_SUCCESS);
	      break;
	    case '1':
		skin ++;
		cout << skin << endl;
		if (skin > 255) skin =0;
		break;
	    case '2':
		skin --;
		cout << skin << endl;
		if (skin < 0) skin = 255;
		break;
	    case '3':
		bone ++;
		cout << bone << endl;
		if (bone > 255) bone = 0;
		break;
	    case '4':
		bone --;
		cout << bone << endl;
		if (bone<0) bone = 255;
		break;
	    case '9':
		cout << "Just Skin" << endl;
		skinColor = Vector3(0,0,1);
		scale = .25f;
		skin = 230;
		bone = 60;
		break;
	    case '8':
		cout << "Just skull" << endl;
		skinColor = black;
		skin = 90;
		bone = 90;
		scale = 0.1f;
		break;
	    case '0':
		cout << "Skull through bones" << endl;
		skinColor = black;
		scale = 0.1f;
		skin = 60;
		bone = 90;
		break;
	    case 'z':
		scale += .01f;
		cout << "Increasing scale to: " << scale << endl;
		break;
	    case 'x':
		scale -= .01f;
		cout << "Decreasing scale to: " << scale << endl;
		break;
	    case 'o':
		rotation += 3.14159/8;
		cout << "Rotation to: " << rotation << endl;
		break;
	    case 'p':
		rotation -= 3.14159/8;
		cout << "Rotation to: " << rotation << endl;
		break;

	}

	
}

int main(int argc, char **argv) {

	head = new Volume("head");
	
	
	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_RGB|GLUT_DOUBLE|GLUT_DEPTH|GLUT_MULTISAMPLE);
	glutInitWindowSize(WIDTH, HEIGHT);
	glutCreateWindow("cav");

	glClearColor(0.5, 0.5, 0.5, 1.0);
	
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	glOrtho(0, WIDTH, HEIGHT, 0, -512, 512);
	
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
	
	glDisable(GL_DEPTH_TEST);
	
	glutKeyboardFunc(KeyEvent);
	glutDisplayFunc(Draw);
	glutIdleFunc(Update);
	
	glutMainLoop();
	
	delete head;
};
