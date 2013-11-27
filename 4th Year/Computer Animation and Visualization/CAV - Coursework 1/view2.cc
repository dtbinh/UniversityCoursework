#include <iostream>
#include <fstream>
#include <GL/glut.h>
#include <map>
#include <set>
#include <string>
#include <sstream>
#include <math.h>
#include "view.h"

#define _USE_MATH_DEFINES

GLdouble bodyWidth = 1.0;

GLfloat angle = -150;   /* in degrees */
GLfloat xloc = 0, yloc = 0, zloc = 0;
int moving, begin;
int newModel = 1;

void mouse(int button, int state, int x, int y)
{
  if (button == GLUT_LEFT_BUTTON && state == GLUT_DOWN) {
    moving = 1;
    begin = x;
  }
  if (button == GLUT_LEFT_BUTTON && state == GLUT_UP) {
    moving = 0;
  }
}


void motion(int x, int y)
{
  if (moving) {
    angle = angle + (x - begin);
    begin = x;
    newModel = 1;
    glutPostRedisplay();
  }
}

int nRows = 480;
int nCols = 480; 

GLfloat light_ambient[] = {0.5, 0.5, 0.5, 1.0};  /* Red diffuse light. */
GLfloat light_diffuse[] = {0.8, 0.8, 0.8, 1.0};  /* Red diffuse light. */
GLfloat light_specular[] = {0.8, 0.8, 0.8, 1.0};  /* Red diffuse light. */
GLfloat light_position[] = {0.0, 0.0, 1.0, 0.0};  /* Infinite light location. */

static float modelAmb[4] = {0.2, 0.2, 0.2, 1.0};
static float matAmb[4] = {0.2, 0.2, 0.2, 1.0};
static float matDiff[4] = {0.8, 0.8, 0.8, 1.0};
static float matSpec[4] = {0.4, 0.4, 0.4, 1.0};
static float matEmission[4] = {0.0, 0.0, 0.0, 1.0};

static float modelAmb2[4] = {0.5, 0.5, 0.5, 1.0};
static float matAmb2[4] = {0.5, 0.5, 0.5, 1.0};
static float matDiff2[4] = {0.8, 0., 0., 1.0};
static float matSpec2[4] = {0.4, 0., 0., 1.0};
static float matEmission2[4] = {0.0, 0.0, 0.0, 1.0};



TriangleMesh trig;
GLUquadricObj *qobj;

vector<TreeNode> bones;
vector<TreeNode> bonesSave;
vector< vector<float> > weights;

vector < Matrix4f > translations;
vector < Matrix4f> rotations;
vector <Matrix4f> restRotations;
vector < vector < int > > paths;

vector <Matrix4f> chainedM;
vector <Matrix4f> restM;

vector <Vector3f> startPositions;
vector <Vector3f> endPositions;
vector <Vector3f> origin;

int timer = 0;
float animDuration = 30.0;

bool bonesShow = false;
bool bonesStore = false;
bool setup = false;
bool forward = false;
bool anim = false;
bool reseting = false;

bool first = false;
bool second = false;
bool third = false;
bool fourth = false;
bool reset = false;
int sequence = 0;

void printMatrix(Matrix4f test) {
	for (int i=0; i<4; i++) {
		for(int j=0; j<4; j++) {
			cout << test(i,j) << "\t";
		}
		cout << endl;
	}
	cout << endl;
}

void printVector(Vector3f test) {

	for (int i=0; i<3; i++) {
		cout << test[i] << "\t";
	}
	cout << endl;
}

int edgeID(Edge & e, vector < Edge > & list) 
{
	bool inlist = false;

	for (int i = 0; i < list.size(); i++) 
	{
		if ((list[i]._v1 == e._v1 && list[i]._v2 == e._v2) ||
		    (list[i]._v2 == e._v1 && list[i]._v1 == e._v2)) 
		{
			return i;
		}	
	}

	return -1;
}

int find(Edge & e, vector <Edge> list) 
{
	for (int i = 0; i < list.size(); i++) {
		if (list[i] == e) return i;
	}

	return -1;
}


void TriangleMesh::loadFile(char * filename)
{
	ifstream f(filename);


	if (f == NULL) {
		cerr << "failed reading polygon data file " << filename << endl;
		exit(1);
	}

	char buf[1024];
	char header[100];
	float x,y,z;
	float xmax,ymax,zmax,xmin,ymin,zmin;
	int v1, v2, v3, n1, n2, n3;

	xmax =-10000; ymax =-10000; zmax =-10000;
	xmin =10000; ymin =10000; zmin =10000;

	while (!f.eof()) {
		    f.getline(buf, sizeof(buf));
		    sscanf(buf, "%s", header);  

		    if (strcmp(header, "v") == 0) {
			sscanf(buf, "%s %f %f %f", header, &x, &y, &z);  
			_v.push_back(Vector3f(x,y,z));

			_vn.push_back(Vector3f(0.f,0.f,1.f));

			Node node;

			node._id = _v.size()-1; 

			_node.push_back(node);
			

			if (x > xmax) xmax = x;
			if (y > ymax) ymax = y;
			if (z > zmax) zmax = z;

			if (x < xmin) xmin = x;
			if (y < ymin) ymin = y;
			if (z < zmin) zmin = z;
		    }
		    else if (strcmp(header, "f") == 0) 
		    {
			sscanf(buf, "%s %d %d %d", header, &v1, &v2, &v3);

			Triangle trig(v1-1, v2-1, v3-1, v1-1, v2-1, v3-1);
			trig._id = _trig.size(); 
			_trig.push_back(trig);

			Edge e1(v1-1, v2-1);
			Edge e2(v2-1, v3-1);
			Edge e3(v3-1, v1-1);

			int id1,id2,id3;

			if ((id1 = edgeID(e1, _edge)) < 0) 
			{
				id1 = _edge.size();
				_edge.push_back(e1);
				_edge[_edge.size()-1] = e1;

				_node[v1-1].edges_to.push_back(v2-1);
				_node[v2-1].edges_to.push_back(v1-1);


				_node[v1-1].edges_cost.push_back(_v[v1-1].distance(_v[v2-1]));
				_node[v2-1].edges_cost.push_back(_v[v1-1].distance(_v[v2-1]));
			}

			if ((id2 = edgeID(e2, _edge)) < 0) 
			{
				id2 = _edge.size();
				e2.setId(id2);
				e2.add_triangle(trig._id);
				_edge.push_back(e2);
				_edge[_edge.size()-1] = e2;

				_node[v2-1].edges_to.push_back(v3-1);
				_node[v3-1].edges_to.push_back(v2-1);


				_node[v2-1].edges_cost.push_back(_v[v2-1].distance(_v[v3-1]));
				_node[v3-1].edges_cost.push_back(_v[v3-1].distance(_v[v2-1]));
			}

			if ((id3 = edgeID(e3, _edge)) < 0) 
			{
				id3 = _edge.size();
				e3.setId(id3);
				e3.add_triangle(trig._id);
				_edge.push_back(e3);

				_node[v3-1].edges_to.push_back(v1-1);
				_node[v1-1].edges_to.push_back(v3-1);

				_node[v3-1].edges_cost.push_back(_v[v3-1].distance(_v[v1-1]));
				_node[v1-1].edges_cost.push_back(_v[v1-1].distance(_v[v3-1]));
			}

			_edge[id1].add_triangle(trig._id);
			_edge[id2].add_triangle(trig._id);
			_edge[id3].add_triangle(trig._id);


			_trig[_trig.size()-1].setEdge(id1,id2,id3); 
		    }
 	}
	vector < vector < int > > facelist (_v.size());
	vector < Vector3f > facenorm (_trig.size());

	/*for (int i = 0; i < _edge.size(); i++) {
		cout << " edge " << i << " trig list " << _edge[i].getTrigList().size()<< endl;
	}*/

	for (int i = 0; i < _trig.size(); i++) 
	{

		Vector3f tmpv = (_v[_trig[i]._vertex[2]] - _v[_trig[i]._vertex[0]]) % 
				(_v[_trig[i]._vertex[1]] - _v[_trig[i]._vertex[0]]) ;

		tmpv.normalize();
		facenorm[i] = tmpv;

		facelist[_trig[i]._vertex[0]].push_back(i);
		facelist[_trig[i]._vertex[1]].push_back(i);
		facelist[_trig[i]._vertex[2]].push_back(i);
	}


	for (int i = 0; i < _v.size(); i++)  
	{
		Vector3f N(0.f,0.f,0.f); 

		float rate1, rate2;

		if (_v[i][1] > 0.5) 
		{
		       rate1 = 1.f ; rate2 = 0.f;	
		}
		else if (_v[i][1] < -0.5) 
		{
		       rate1 = 0.f ; rate2 = 1.f;	
		}
		else 
		{
			rate1 = _v[i][1] + 0.5f; rate2 = 1.f - rate1; 
		}

		for (int j = 0; j < facelist[i].size(); j++) 
		{
			N += facenorm[facelist[i][j]]; 
		}

		N /= (float)facelist[i].size();

		_vn[i] = N;
	}


	_xmin = xmin; _ymin = ymin; _zmin = zmin;
	_xmax = xmax; _ymax = ymax; _zmax = zmax;

	f.close();

};

//Sets the starting point of an animation (based on the loaded vertices) and calculates the ending point of an animation (based on the rotations and translations in ChainedM
void iterateTrig() {
	startPositions = trig.getV();
	vector <Vector3f> returnVec;

	for (int i=0; i<startPositions.size(); i++) {
		Vector3f sum = Vector3f(0,0,0);
		for (int j=1; j< restM.size(); j++) {
			Vector3f temp = chainedM.at(j) * !(restM.at(j)) * startPositions.at(i);
			temp *= weights.at(i).at(j-1);
			sum += temp;
		}
		returnVec.push_back(sum);
	}

	endPositions = returnVec;
}

void recalcModelView(void)
{
	glPopMatrix();
	glPushMatrix();
	glTranslatef(xloc, yloc, zloc);
	glRotatef(angle, 0.0, 1.0, 0.0);
	glTranslatef(0, 0, .0);
	newModel = 0;
}

//Update bones based off changes made to the Big Matrix
//Forward variable used to determine if calculating the forward animation, or returning to the origin
void updateBones() {

	for (int i=0; i<chainedM.size(); i++) {
		Matrix4f updatePos;
		if (forward) updatePos = chainedM.at(i);
		else updatePos = restM.at(i);
		bones.at(i).globalPos = Vector3f(updatePos(0,3),updatePos(1,3), updatePos(2,3));
		
		//Update each of the children		
		for (int j=0; j<bones.at(i).children.size(); j++) {
			Matrix4f updateChild;
			if (forward) updateChild = chainedM.at(bones.at(i).children.at(j).key_value);
			else updateChild = restM.at(bones.at(i).children.at(j).key_value);
			bones.at(i).children.at(j).globalPos = Vector3f(updateChild(0,3), updateChild(1,3), updateChild(2,3));
		}
	}
}

//Load the bones in 
void loadBones(char * filename) 
{

	std::ifstream infile(filename);

	int a, b;
	float x, y, z;
	char buf[1024];

	while (!infile.eof()) {
		infile.getline(buf, sizeof(buf));
		if (bones.size() < 22) 
		{
			sscanf(buf, "%d %f %f %f %d", &a, &x, &y, &z, &b);

			TreeNode currNode;
			currNode.key_value = a;
			currNode.globalPos = Vector3f(x, y, z);
			if (b != -1) {
				bones.at(b).children.push_back(currNode);
			}
			currNode.parent = b;
			bones.push_back(currNode);
		}
	}
}

//Load the weights in
void loadWeights(char * filename)
{
	std::ifstream infile(filename);
	char buf[1024];

	while(!infile.eof()) {
		infile.getline(buf, sizeof(buf));
		if (weights.size() < 3752) {
			vector <float> vecWeight;
			float weight;
			stringstream ss(buf);

			while (ss >> weight) {
				vecWeight.push_back(weight);
			}

			weights.push_back(vecWeight);
		}
	}
}

//Given a bone joint, create a path from that joint to the root
std::vector<int> traverseBonesUp(int start) {
	vector<int> path;
	while(start != -1)
	{
		path.push_back(bones.at(start).key_value);
		start = bones.at(start).parent;
	}
	return path;
}

//Initialize the following vectors (each corresponding to a joint)
//translations = global coordinate of each of the joints, relative to the previous joint
//restRotations = identity matrix - in rest position, all joints are not rotated
//rotations = identity matrix initially - these matrices will be changed to manipulate the image
//paths = list of joints needed to pass through to get to the root
void setupStartMatrices(){
	vector<Matrix4f> translationsList;
	vector<Matrix4f> rotationsList;
	vector<Matrix4f> restRotationsList;
	vector < vector < int > > pathsList;

	for (int i=0; i<bones.size(); i++) {
		Matrix4f translate;
		translate.setIdentity();
		Vector3f trans;
		if (i==0) trans = bones.at(i).globalPos;
		else	  trans = bones.at(i).globalPos - bones.at(bones.at(i).parent).globalPos;
		
		translate.setTranslation(trans);
		translationsList.push_back(translate);
		
		Matrix4f rotate;
		rotate.setIdentity();
		rotationsList.push_back(rotate);
		restRotationsList.push_back(rotate);
		pathsList.push_back(traverseBonesUp(bones.at(i).key_value));
		
	}
	translations = translationsList; 
	restRotations = restRotationsList;
	rotations = rotationsList;
	paths = pathsList;
}

void resetRotations() {
	Matrix4f rot;
	rot.setIdentity();
	for (int i=0; i<bones.size(); i++) {
		rotations.at(i) = rot;
	}
}

//Create a simple keyframe by rotating certain joints 
void setKeyFrameAnim1() {
	Matrix4f rot;
	rot = rotY(-M_PI/3);
	rotations.at(10) = rot;
	rot = rotY(M_PI/3);
	rotations.at(15) = rot;
	rot = rotX(-M_PI/1.5);
	rotations.at(1) = rot;
	rot = rotX(-M_PI/1.5);
	rotations.at(18) = rot;
	rot = rotX(M_PI/3);
	rotations.at(3) = rot;
	rotations.at(20) = rot;
	rot = rotX(M_PI/4);
	rotations.at(0) = rot;
}

//Create another simple keyframe by rotating certain joints
void setKeyFrameAnim2() {
	sequence = 0;
	Matrix4f rot;
	rot = rotX(M_PI/4);
	rotations.at(8) = rot;
	rotations.at(13) = rot;
	rot = rotX(M_PI/6);
	rotations.at(18) = rot;
	rot = rotX(M_PI/4);
	rotations.at(2) = rot;
}

void setKeyFrameAnim3() {
	sequence = 1;
	Matrix4f rot;
	rot = rotX(-M_PI/6);
	rotations.at(1) = rot;
	rot.setIdentity();
	rotations.at(18) = rot;
	rotations.at(8) = rot;
	rot = rotX(-M_PI/4);
	rotations.at(2) = rot;
}

void setKeyFrameAnim4() {
	sequence = 2;
	Matrix4f rot;
	rot = rotZ(M_PI/4);

	rotations.at(8) = rot;
	rot.setIdentity();
	rotations.at(1) = rot;
	rotations.at(18) = rot;
	rot = rotX(-M_PI/2);
	rotations.at(15) = rot;
	rotations.at(10) = rot;
	rot = rotY(M_PI/4);

	rotations.at(5) = rot;
}


//When the paths are calculated, they are popped off the stack. This function resets the paths
void resetPath() {
	vector < vector < int > > pathsList;
	for (int i=0; i<bones.size(); i++) {
		pathsList.push_back(traverseBonesUp(bones.at(i).key_value));
	}
	paths = pathsList;
}

//For each bone, calculate the rest Matrix (R1, T1, R2, T2....RN, TN for n = 21)
//For each bone, calculate the transformations (initially same as rest, are changed in setKeyFrameAnim1 and inverseKinematics) 
//This uses the path to walk down the body - path is 21, 20, 19, 18, 0. Pop off the back, and use that value. 
void calcPathMatrix() {
	vector <Matrix4f> chainedMList;
	vector <Matrix4f> restMList;

	for (int i=0; i<paths.size(); i++) {
		Matrix4f bigM;
		Matrix4f restBigM;
		bigM.setIdentity();
		restBigM.setIdentity();
		while (!paths.at(i).empty()) {
			bigM *= rotations.at(paths.at(i).back());
			bigM *= translations.at(paths.at(i).back());
			restBigM *= restRotations.at(paths.at(i).back());
			restBigM *= translations.at(paths.at(i).back());
			paths.at(i).pop_back();
		}
		chainedMList.push_back(bigM);
		restMList.push_back(restBigM);
	}
	restM = restMList;
	chainedM = chainedMList;
}



//sets up the variables needed to work
void setupAnimation() {
	setupStartMatrices();
	setKeyFrameAnim1();
	calcPathMatrix();
}

void setupAnimation2() {
	setupStartMatrices();
	setKeyFrameAnim2();
	calcPathMatrix();
}

void setupAnimation3() {
	setKeyFrameAnim3();
	resetPath();
	calcPathMatrix();
}

void setupAnimation4() {
	setKeyFrameAnim4();
	resetPath();
	calcPathMatrix();
}


//Display function - modified to display bones underneath 
void myDisplay()
{
	if (newModel)
		recalcModelView();


	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear OpenGL Window
	int trignum = trig.trigNum();
	Vector3f v1,v2,v3,n1,n2,n3;
	for (int i = 0 ; i < trignum; i++)  
	{

		float m1,m2,m3,min,max;
		trig.getTriangleVertices(i,v1,v2,v3);
		trig.getTriangleNormals(i,n1,n2,n3);
		trig.getMorseValue(i, m1, m2, m3);
		m1 = m2 = m3 = trig.color(i);

		GLfloat skinColor[] = {0.1, 1., 0.1, 1.0};

		if (max >= 0) {
			glBegin(GL_TRIANGLES);

				skinColor[1] = m1; skinColor[0] = 1-m1;
				glMaterialfv(GL_FRONT, GL_DIFFUSE, skinColor); 
				glNormal3f(-n1[0],-n1[1],-n1[2]);
				glVertex3f(v1[0],v1[1],v1[2]);

				skinColor[1] = m2; skinColor[0] = 1-m2;
				glMaterialfv(GL_FRONT, GL_DIFFUSE, skinColor); 
				glNormal3f(-n2[0],-n2[1],-n2[2]);
				glVertex3f(v2[0],v2[1],v2[2]);

				skinColor[1] = m3; skinColor[0] = 1-m3;
				glMaterialfv(GL_FRONT, GL_DIFFUSE, skinColor); 
				glNormal3f(-n3[0],-n3[1],-n3[2]);
				glVertex3f(v3[0],v3[1],v3[2]);			

			glEnd();
		}
	}
	if(bonesShow) {
		glClear(GL_DEPTH_BUFFER_BIT);
	
		glDisable(GL_LIGHT0);
		glDisable(GL_LIGHTING);
	

		glLineWidth(5.5); 
		glColor3f(1, 1, 1);
		for (int i=0; i<bones.size(); i++) {
			for (int j=0; j<bones.at(i).children.size(); j++) {
				glBegin(GL_LINES);
				Vector3f origin = bones.at(i).globalPos;
				Vector3f dest = bones.at(i).children.at(j).globalPos;
				glVertex3f(origin[0], origin[1], origin[2]);
				glVertex3f(dest[0], dest[1], dest[2]);
				glEnd();	
			}		
		}

		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
	}	
	

	 glutSwapBuffers();

}

//Distance function squared
float squareDist(Vector3f one, Vector3f two)
{
	return pow((one[0]-two[0]), 2) + pow((one[1]-two[1]), 2) + pow((one[2] - two[2]), 2);
}

//Normal distance function
float dist(Vector3f one, Vector3f two)
{
	return sqrt(pow((one[0]-two[0]), 2) + pow((one[1]-two[1]), 2) + pow((one[2] - two[2]), 2));
}

//Finds and sets the angle that minimizes the distance between the current Joint and the target point
//Note: rotation is used as binary vector, with 0 being the axis you want to rotate about
void findMinimizingRotations(Vector3f end, Vector3f effector, Vector3f root, Vector3f rotation, int link) {
	Vector3f currVector = Vector3f(effector[0] - root[0], effector[1] - root[1], effector[2] - root[2]); 
	Vector3f targetVector = Vector3f((end[0] - root[0]) * rotation[0], (end[1] - root[1]) * rotation[1], (end[2] - root[2]) * rotation[2]);

	currVector.normalize();
	targetVector.normalize();

	float cosAngle = targetVector.dot(currVector);
	if (cosAngle < 0.99999) {
		Vector3f crossResult = targetVector.cross(currVector);
		Matrix4f rot;
		float turnAngle = acos((float)cosAngle);
		float turnDeg = (turnAngle * M_PI)/180.0;
		if (rotation[0] == 0) {
			if (crossResult[0] > 0.0f)	rot = rotX(-turnDeg);
			else if (crossResult[0] < 0.0f) rot = rotX(turnDeg);
		} else if (rotation[1] == 0) {
			if (crossResult[1] > 0.0f)	rot = rotY(-turnDeg);
			else if (crossResult[1] < 0.0f) rot = rotY(turnDeg);
		} else if (rotation[2] == 0) {
			if (crossResult[2] > 0.0f)	rot = rotZ(-turnDeg);
			else if (crossResult[2] < 0.0f) rot = rotZ(turnDeg);
		}
		rotations.at(link) = rotations.at(link) * rot;
	} 

}

//Function adapted from code at http://graphics.cs.cmu.edu/nsp/course/15-464/Fall09/assignments/asst2/jlander_gamedev_nov98.pdf
//Given a limb (two hands or legs), and a desired positition, updates the bigM to reach this point iteratively
bool inverseKinematics(Vector3f endPos, int limb) 
{
	//Limb should be 12, 17, 4 or 21
	int maxTries = 100;
	int tries = 0;
	int link = bones.at(limb).parent;
	float threshold = .2f;

	Vector3f endEffector;

	do {

		Vector3f rootPos = bones.at(link).globalPos;
		endEffector = bones.at(limb).globalPos;	

		if (dist(endEffector, endPos) > threshold) {

			findMinimizingRotations(endPos, endEffector, rootPos, Vector3f(0,1,1), link);	//x rotation
			findMinimizingRotations(endPos, endEffector, rootPos, Vector3f(1,0,1), link);	//y rotation
			findMinimizingRotations(endPos, endEffector, rootPos, Vector3f(1,1,0), link);	//z rotation

			resetPath();
			calcPathMatrix();
			forward = true;
			updateBones();


			link = bones.at(link).parent;
			if (link < 0) link = bones.at(limb).parent;
		

		}

	} while(tries++ < maxTries && squareDist(endEffector, endPos) > threshold);
	iterateTrig();	
	forward = false;		
	updateBones();
	return true;
}


//Returns everything to the start (using an animation)
void returntoStart() {	
	anim = false;
	first = false;
	second = false;
	third = false;
	forward = false;
	startPositions = trig.getV();
	endPositions = origin;
	setup = true;
	reseting = true;
	timer = 0;
}

//Function for animating. Uses custom timer that increments if setup is true. Interpolates between beginning keyframe and ending keyframe
void animate() {
	if (timer < animDuration and setup) {
		bonesShow = false;
		timer += 1;
		vector <Vector3f> transit;
		for(int j=0; j<startPositions.size(); j++) {
			Vector3f temp1 = startPositions.at(j);
			temp1 *= float(1.0-float(timer/animDuration));
			Vector3f temp2 = endPositions.at(j);
			temp2 *= float(timer/animDuration);
			transit.push_back((temp1 + temp2));
		}
		trig.setV(transit);
		glutDisplayFunc(myDisplay);
		glutPostRedisplay(); 
	} else if (setup) {
		timer = 0;
		setup = false;
		forward = !(forward);
		if (!reseting) startPositions.swap(endPositions);
		updateBones();
		bonesShow = bonesStore;	
		if (anim) setup = true;	
		if (fourth) {
			if (sequence == 0) {
				setupAnimation3();
				iterateTrig();
				updateBones();
				setup = true;
			} else if (sequence == 1) {
				setupAnimation4();
				iterateTrig();
				updateBones();
				setup = true;
			} else {
				fourth = false;
				sequence = 0;
				reset = true;
			}

		}
		if (reseting) {
			forward = false;
			iterateTrig();	
			updateBones();
			bones = bonesSave;
			setup = false;
			reseting = false;
		}

		glutDisplayFunc(myDisplay);
		glutPostRedisplay();


	}
}


//Deal with keys
void demoKeyboardHandler(unsigned char key, int x, int y)
{
	if(key == '1')
	{

		if (!first) {
			first = true;
			if(second || fourth || third || reset) {
				returntoStart();
				reset = false;
				first =  false;
			} else {
			setupAnimation();
			iterateTrig();				
			
			setup = true;
			second = false;
			fourth =false;
			third = false;
			}
		} else {
			returntoStart();
		}
	}
	
	if (key == '2')
	{
		setupStartMatrices();
		if(!second) {
			if (reset) {
				returntoStart();
				reset = false;
				second = false;
			} else {
			second = inverseKinematics(Vector3f(0.283606, 0.700675, 1.600024), 12);
			first = false;
			third = false;
			fourth = false;
			setup = true;
			}
		} else {
			returntoStart();
		}
	}

	if (key == '3')
	{
		setupStartMatrices();
		if(!third) {
			if (reset) {
				returntoStart();
				reset = false;
				third = false;
			} else {
			third = inverseKinematics(Vector3f(-1.0333, 1.9, .0545), 17);
			third = inverseKinematics(Vector3f(2.0333, 1.9, .0545), 12);
			first = false;
			second = false;
			fourth = false;
			setup = true;
			}
		} else {
			returntoStart();
		}
	}

	if (key == '4')
	{
		
		if (!fourth) {
			fourth = true;
			if(second || first || third || reset) {
				returntoStart();
				reset = false;
				fourth =  false;
			} else {
			setupAnimation2();
			iterateTrig();				
			setup = true;
			first = false;
			second = false;
			third = false;
			}
		} else {
			returntoStart();
		}
	}
	if (key == 'a')
	{
		anim = !anim;
	}

	if (key == 'z')
	{
		animDuration ++;
	}
	if (key == 'x')
	{
		animDuration --;
	}

	if (key == 'b')
	{
		cout << "Showing bones" << endl;
		bonesShow = !bonesShow;
	}

	if (key == 'r')
	{
		returntoStart();
	}

	glutDisplayFunc(myDisplay);// Callback function
	glutPostRedisplay();

    cout << "Key pressed: " << key << endl;

}

//Save the original state
void saveOriginal() {
	origin = trig.getV();
	bonesSave = bones;
}


int main(int argc, char **argv)
{
	if (argc >  1)  {
		trig.loadFile(argv[1]);
		loadBones(argv[2]);
		saveOriginal();
		loadWeights(argv[3]);
		setupAnimation();
		iterateTrig();	
	}
	else {
		cerr << argv[0] << " <filename> " << endl;
		exit(1);
	}

	int width, height;
	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_RGB | GLUT_DOUBLE | GLUT_DEPTH | GLUT_MULTISAMPLE);

	glutInitWindowSize(nRows, nCols);
	glutCreateWindow("Computer Animation");

	glLightfv(GL_LIGHT0, GL_AMBIENT, light_ambient);
	glLightfv(GL_LIGHT0, GL_DIFFUSE, light_diffuse);
	glLightfv(GL_LIGHT0, GL_SPECULAR, light_diffuse);
	glLightfv(GL_LIGHT0, GL_POSITION, light_position);
	glEnable(GL_LIGHT0);
	glEnable(GL_LIGHTING);


	/* Use depth buffering for hidden surface elimination. */
	glEnable(GL_DEPTH_TEST);

	/* Setup the view of the cube. */
	glMatrixMode(GL_PROJECTION);
	gluPerspective( /* field of view in degree */ 40.0, 
	/* aspect ratio */ 1., /* Z near */ 1.0, /* Z far */ 1000.0);

	glMatrixMode(GL_MODELVIEW);

	gluLookAt(0.0, 0.0, 7.0,  /* eye is at (0,0,5) */
		  0.0, 0.0, 0.0,      /* center is at (0,0,0) */
		  0.0, 1.0, 0.0);      /* up is in positive Y direction */
	glPushMatrix();       /* dummy push so we can pop on model recalc */


	glutDisplayFunc(myDisplay);// Callback function

	glutMouseFunc(mouse);
	glutMotionFunc(motion);
	glutKeyboardFunc(demoKeyboardHandler);

	glutIdleFunc(animate);

	glutMainLoop();// Display everything and wait
}
