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
vector< vector<float> > weights;

vector < Matrix4f > translations;
vector < Matrix4f> rotations;
vector <Matrix4f> restRotations;
vector < vector < int > > paths;

vector <Matrix4f> chainedM;
vector <Matrix4f> restM;

vector <Vector3f> startPositions;
vector <Vector3f> endPositions;

int timer = 0;
float animDuration = 20.0;

bool bonesShow = false;
bool bonesStore = false;
bool setup = false;
bool forward = false;

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

void setTrig() {
	startPositions = trig.getV();
	vector<Vector3f> returnVec;
	for (int i=0; i<startPositions.size(); i++) {
		Vector3f sum = Vector3f(0,0,0);
		for (int j=1; j<restM.size(); j++) {
			Vector3f temp = chainedM.at(j) * !(restM.at(j)) * startPositions.at(i);
			temp *= weights.at(i).at(j-1);
			sum += temp;
		}
		returnVec.push_back(sum);
	}
	trig.setV(returnVec);
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


void fillBones() 
{

std::ifstream infile("skeleton2.out");

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


std::vector<int> traverseBonesUp(int start) {
	vector<int> path;
	while(start != -1)
	{
		path.push_back(bones.at(start).key_value);
		//cout << bones.at(start).key_value << endl;
		start = bones.at(start).parent;
	}
	return path;
}

void setupStartMatrices(){
	vector<Matrix4f> translationsList;
	vector<Matrix4f> rotationsList;
	vector<Matrix4f> restRotationsList;
	vector < vector < int > > pathsList;

	for (int i=0; i<bones.size(); i++) {
		Matrix4f translate;
		translate.setIdentity();
		Vector3f trans;
		if (i==0) {
			trans = bones.at(i).globalPos;
		} else {
			trans = bones.at(i).globalPos - bones.at(bones.at(i).parent).globalPos;
		}
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

void setKeyFrameAnim1() {
	Matrix4f rot;
	rot = rotY(-M_PI/3);
	rotations.at(10) = rot;
	/*rot = rotY(M_PI/3);
	rotations.at(15) = rot;
	rot = rotX(-M_PI/1.5);
	rotations.at(1) = rot;
	rot = rotX(-M_PI/1.5);
	rotations.at(18) = rot;
	rot = rotX(M_PI/3);
	rotations.at(3) = rot;
	rotations.at(20) = rot;
	rot = rotX(M_PI/4);
	rotations.at(0) = rot;*/
}

void resetPath() {
	vector < vector < int > > pathsList;
	for (int i=0; i<bones.size(); i++) {
		pathsList.push_back(traverseBonesUp(bones.at(i).key_value));
	}
	paths = pathsList;
}

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




void traverseBonesDown() {
	setupStartMatrices();
	setKeyFrameAnim1();
	calcPathMatrix();
}

void fillWeights()
{
std::ifstream infile("attachment2.out");

bool temp = false;

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

void moveBone(int boneNum, Vector3f pos)
{
	bones.at(boneNum).globalPos = pos;
}

void printBones() {
	for (int i =0; i< bones.size(); i++) {
		cout << i << " ";
		printVector(bones.at(i).globalPos);
	}	
}


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
				
				/*skinColor[1] = m1; skinColor[0] = 1-m1;
				glMaterialfv(GL_FRONT, GL_DIFFUSE, skinColor); 
				glNormal3f(-n1[0],-n1[1],-n1[2]);
				glVertex3f(v1[0],v1[1],v1[2]); */
				

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

float squareDist(Vector3f one, Vector3f two)
{
	return pow((one[0]-two[0]), 2) + pow((one[1]-two[1]), 2) + pow((one[2] - two[2]), 2);
}

float dist(Vector3f one, Vector3f two)
{
	return sqrt(pow((one[0]-two[0]), 2) + pow((one[1]-two[1]), 2) + pow((one[2] - two[2]), 2));
}

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
void inverseKinematics(Vector3f endPos, int limb) 
{
	//Limb should be 12, 17, 4 or 21
	int maxTries = 100;
	int tries = 0;
	int link = bones.at(limb).parent;
	float threshold = .2f;
	float turnAngle;
	float cosAngle;
	float turnDeg;

	forward = true;

	Vector3f rootPos;
	Vector3f endEffector;
	Vector3f currVector;
	Vector3f targetVector;

	do {

		rootPos = bones.at(link).globalPos;
		endEffector = bones.at(limb).globalPos;

		if (dist(endEffector, endPos) > threshold) {
			findMinimizingRotations(endPos, endEffector, rootPos, Vector3f(0,1,1), link);
			findMinimizingRotations(endPos, endEffector, rootPos, Vector3f(1,0,1), link);
			findMinimizingRotations(endPos, endEffector, rootPos, Vector3f(1,1,0), link);
/*

			float cosAngle = targetVector.dot(currVector);
			if (cosAngle < 0.99999) {
				Vector3f crossResult = targetVector.cross(currVector);
				Matrix4f rot;
				if (crossResult[2] > 0.0f) { //Rotate clockwise
					turnAngle = acos((float)cosAngle);
					turnDeg = (turnAngle * M_PI)/180.0;
					rot = rotZ(-turnDeg);
				

				} else if (crossResult[2] < 0.0f) {
					turnAngle = acos((float)cosAngle);
					turnDeg = (turnAngle*M_PI)/180.0;
					rot = rotZ(turnDeg);
				}
				rotations.at(link) = rotations.at(link) * rot;

			} 

			currVector = Vector3f(endEffector[0] - rootPos[0], endEffector[1] - rootPos[1], endEffector[2] - rootPos[2]); 
			targetVector = Vector3f(endPos[0] - rootPos[0], 0, endPos[2] - rootPos[2]);

			currVector.normalize();
			targetVector.normalize();

			cosAngle = targetVector.dot(currVector);
			if (cosAngle < 0.99999) {
				Vector3f crossResult = targetVector.cross(currVector);
				Matrix4f rot;
				if (crossResult[1] > 0.0f) { //Rotate clockwise
					turnAngle = acos((float)cosAngle);
					turnDeg = (turnAngle * M_PI)/180.0;
					rot = rotY(-turnDeg);
				

				} else if (crossResult[1] < 0.0f) {
					turnAngle = acos((float)cosAngle);
					turnDeg = (turnAngle*M_PI)/180.0;
					rot = rotY(turnDeg);
				}
				rotations.at(link) = rotations.at(link) * rot;

			} 
			currVector = Vector3f(endEffector[0] - rootPos[0], endEffector[1] - rootPos[1], endEffector[2] - rootPos[2]); 
			targetVector = Vector3f(0, endPos[1] - rootPos[1], endPos[2] - rootPos[2]);

			currVector.normalize();
			targetVector.normalize();

			cosAngle = targetVector.dot(currVector);
			if (cosAngle < 0.99999) {
				Vector3f crossResult = targetVector.cross(currVector);
				Matrix4f rot;
				if (crossResult[0] > 0.0f) { //Rotate clockwise
					turnAngle = acos((float)cosAngle);
					turnDeg = (turnAngle * M_PI)/180.0;
					rot = rotX(-turnDeg);
				

				} else if (crossResult[0] < 0.0f) {
					turnAngle = acos((float)cosAngle);
					turnDeg = (turnAngle*M_PI)/180.0;
					rot = rotX(turnDeg);
				}
				rotations.at(link) = rotations.at(link) * rot;

			} 
*/
			resetPath();
			calcPathMatrix();
			updateBones();


			link = bones.at(link).parent;
			if (link < 0) link = bones.at(limb).parent;
		

		}

	} while(tries++ < maxTries && dist(endEffector, endPos) > threshold);
	//TODO Try with addresses for the square distance (and other places where they may be needed)
	updateBones();
	setTrig();
//	iterateTrig();			
	//setup = true;
	//printBones();
}

/*
#define EFFECTOR_POS 5 // THIS CHAIN HAS 5 LINKS
#define MAX_IK_TRIES 100 // TIMES THROUGH THE CCD LOOP
#define IK_POS_THRESH 1.0f // THRESHOLD FOR SUCCESS
///////////////////////////////////////////////////////////////////////////////
// Procedure: ComputeCCDLink
// Purpose: Compute an IK Solution to an end effector position
// Arguments: End Target (x,y)
// Returns: TRUE if a solution exists, FALSE if the position isn't in reach
///////////////////////////////////////////////////////////////////////////////
BOOL COGLView::ComputeCCDLink(CPoint endPos)
{
/// Local Variables ///////////////////////////////////////////////////////////
tVector rootPos,curEnd,desiredEnd,targetVector,curVector,crossResult;
double cosAngle,turnAngle,turnDeg;
int link,tries;
///////////////////////////////////////////////////////////////////////////////
// START AT THE LAST LINK IN THE CHAIN
link = EFFECTOR_POS - 1;
tries = 0; // LOOP COUNTER SO I KNOW WHEN TO QUIT
do
{
// THE COORDS OF THE X,Y,Z POSITION OF THE ROOT OF THIS BONE IS IN THE MATRIX
// TRANSLATION PART WHICH IS IN THE 12,13,14 POSITION OF THE MATRIX
rootPos.x = m_Link[link].matrix.m[12];
rootPos.y = m_Link[link].matrix.m[13];
rootPos.z = m_Link[link].matrix.m[14];
// POSITION OF THE END EFFECTOR
curEnd.x = m_Link[EFFECTOR_POS].matrix.m[12];
curEnd.y = m_Link[EFFECTOR_POS].matrix.m[13];
curEnd.z = m_Link[EFFECTOR_POS].matrix.m[14];
// DESIRED END EFFECTOR POSITION
desiredEnd.x = endPos.x;
desiredEnd.y = endPos.y;
desiredEnd.z = 0.0f; // ONLY DOING 2D NOW
// SEE IF I AM ALREADY CLOSE ENOUGH
if (VectorSquaredDistance(&curEnd, &desiredEnd) > IK_POS_THRESH)
{
// CREATE THE VECTOR TO THE CURRENT EFFECTOR POS
curVector.x = curEnd.x - rootPos.x;
curVector.y = curEnd.y - rootPos.y;
curVector.z = curEnd.z - rootPos.z;
// CREATE THE DESIRED EFFECTOR POSITION VECTOR
targetVector.x = endPos.x - rootPos.x;
targetVector.y = endPos.y - rootPos.y;
targetVector.z = 0.0f; // ONLY DOING 2D NOW
// NORMALIZE THE VECTORS (EXPENSIVE, REQUIRES A SQRT)
NormalizeVector(&curVector);
NormalizeVector(&targetVector);
// THE DOT PRODUCT GIVES ME THE COSINE OF THE DESIRED ANGLE
cosAngle = DotProduct(&targetVector,&curVector);
// IF THE DOT PRODUCT RETURNS 1.0, I DON'T NEED TO ROTATE AS IT IS 0 DEGREES
if (cosAngle < 0.99999)
{
// USE THE CROSS PRODUCT TO CHECK WHICH WAY TO ROTATE
CrossProduct(&targetVector, &curVector, &crossResult);


if (crossResult.z > 0.0f) // IF THE Z ELEMENT IS POSITIVE, ROTATE CLOCKWISE
{
turnAngle = acos((float)cosAngle); // GET THE ANGLE
turnDeg = RADTODEG(turnAngle); // COVERT TO DEGREES
// DAMPING
if (m_Damping && turnDeg > m_Link[link].damp_width) 
turnDeg = m_Link[link].damp_width;
m_Link[link].rot.z -= (float)turnDeg; // ACTUALLY TURN THE LINK
// DOF RESTRICTIONS
if (m_DOF_Restrict &&
m_Link[link].rot.z < (float)m_Link[link].min_rz) 
m_Link[link].rot.z = (float)m_Link[link].min_rz;
}
else if (crossResult.z < 0.0f) // ROTATE COUNTER CLOCKWISE
{
turnAngle = acos((float)cosAngle);
turnDeg = RADTODEG(turnAngle);
// DAMPING
if (m_Damping && turnDeg > m_Link[link].damp_width) 
turnDeg = m_Link[link].damp_width;
m_Link[link].rot.z += (float)turnDeg; // ACTUALLY TURN THE LINK
// DOF RESTRICTIONS
if (m_DOF_Restrict &&
m_Link[link].rot.z > (float)m_Link[link].max_rz) 
m_Link[link].rot.z = (float)m_Link[link].max_rz;
}
// RECALC ALL THE MATRICES WITHOUT DRAWING ANYTHING
drawScene(FALSE); // CHANGE THIS TO TRUE IF YOU WANT TO SEE THE ITERATION
}
if (--link < 0) link = EFFECTOR_POS - 1; // START OF THE CHAIN, RESTART
}
// QUIT IF I AM CLOSE ENOUGH OR BEEN RUNNING LONG ENOUGH
} while (tries++ < MAX_IK_TRIES && 
VectorSquaredDistance(&curEnd, &desiredEnd) > IK_POS_THRESH);
return TRUE;
}



*/


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
		startPositions.swap(endPositions);
		updateBones();
		bonesShow = bonesStore;	
		glutDisplayFunc(myDisplay);
		glutPostRedisplay();	
	}
}

void demoKeyboardHandler(unsigned char key, int x, int y)
{
	if(key == '1')
	{
		setup = true;
		bonesStore = bonesShow;
	}
	
	if (key == '2')
	{
		setupStartMatrices();
		Vector3f test = bones.at(12).globalPos;
		inverseKinematics(Vector3f(.183606, .700675, .300024), 12);
		updateBones();
	}

	if (key == '3')
	{
		for (int i=0; i<rotations.size(); i++) {
			printMatrix(rotations.at(i));
		}
	}
	if(key == 'p') 
	{
		printBones();
	}

	if (key == 'b')
	{
		cout << "Showing bones" << endl;
		bonesShow = !bonesShow;
	}
	if(key == 'm')
	{
        	cout << "Mouse location: " << x << " " << y << endl;
	}

	glutDisplayFunc(myDisplay);// Callback function
	glutPostRedisplay();

    cout << "Key pressed: " << key << endl;

}

int main(int argc, char **argv)
{
	if (argc >  1)  {
		trig.loadFile(argv[1]);
		fillBones();
		fillWeights();
		traverseBonesDown();
		iterateTrig();
	//Matrix4f temp;
	//temp.setIdentity();

	//cout << temp(0,3) << endl;
	//temp.setTranslation(Vector3f(4,1,1));
	//!(temp);
	//printMatrix(!(temp));
/*	cout << temp(0,3) << endl;*/
	
	
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
