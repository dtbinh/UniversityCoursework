#ifndef _rt_H
#define _rt_H

//These include basic math and vector support, as well as
//a helper library for OpenGL.
//For the Java developers amongst you, this is slightly similar
//to an import statement in Java.
#include <cmath>
#include <vector>
#include <GL/glut.h>

//this allows you to not have to write the std namespace before each
//function from the std library.
//for instance: std::vector becomes vector.
using namespace std; 

// -- forward declarations of the classes --
//Vector3f is a class to store 3 values, often used for x,y,z coordinates 
class Vector3f;

//the Triangle class stores an index to 3 vertices. These are internally stored
//as indices to a vector of vertices (which are Vector3f).
class Triangle;

//The TriangleMesh class actually stores the geometry of an object.
//It stores the vertices and a list of triangles (indices into the vertex list).
class TriangleMesh;

//--- Definitions for the classes ---
class Vector3f {

	float _item[3];//item internally stores the 3 floating point values

	public:

	//A Vector3f is indexed like an array, with a range of 0 to 2.
	//When we are using these for cartesian coordinates, index 0 is
	//the x coordinate, 1 is y, and 2 is z.
	float & operator [] (int i) {
		return _item[i];
    	}

	//a constructor for the Vector3f class. 
	Vector3f(float x, float y, float z) 
	{  _item[0] = x ; _item[1] = y ; _item[2] = z; };

	//Another constructor, initialising to 0,0,0
	Vector3f() 
	{
		_item[0] = 0.0f ; _item[1] = 0.0f ; _item[2] = 0.0f; 
	}


	Vector3f & operator = (Vector3f & obj) 
	{
		_item[0] = obj[0];
		_item[1] = obj[1];
		_item[2] = obj[2];

		return *this;
	}

	Vector3f & operator += (Vector3f & obj) 
	{
		_item[0] += obj[0];
		_item[1] += obj[1];
		_item[2] += obj[2];

		return *this;
	}
};

//An output stream, useful for debugging.
//Allows you to stream Vector3f objects to the terminal.
ostream & operator << (ostream & stream, Vector3f & obj) 
{
	stream << obj[0] << ' ' << obj[1] << ' ' << obj[2] << ' ';
}




class Triangle {
friend class TriangleMesh;

	int _vertex[3];//indices to the 3 vertices of the triangle
public:

	Triangle(int v1, int v2, int v3) 
	{
		_vertex[0] = v1;  _vertex[1] = v2;  _vertex[2] = v3;  
	}
};

//Utilities
//fmax returns the maximum of 3 floats
float fmax(float f1,float f2, float f3) {
	float f = f1;

	if (f < f2) f = f2;
	if (f < f3) f = f3;

	return f;
}

//fmin returns the minimum of 3 floats.
float fmin(float f1,float f2, float f3) {
	float f = f1;

	if (f > f2) f = f2;
	if (f > f3) f = f3;

	return f;
}


class TriangleMesh 
{
	vector <Vector3f> _v; //_v stores the vertices as Vector3f
	vector <Triangle> _trig; //_trig stores the triangles as instances of the Triangle class
	float _xmax, _xmin, _ymax, _ymin, _zmin, _zmax;//These store the bounding values for each axis.

public: 
	TriangleMesh(char * filename) { loadFile(filename) ;};
	TriangleMesh() {};
	void loadFile(char * filename);

	int trigNum() { return _trig.size() ;};
	int vNum() { return _v.size();};
	Vector3f v(int i) { return _v[i];};

	//v1, v2 and v3 are return values for the vertices. i is the input index.
	void getTriangleVertices(int i, Vector3f & v1, Vector3f & v2, Vector3f & v3)
	{
		v1 = _v[_trig[i]._vertex[0]]; 
		v2 = _v[_trig[i]._vertex[1]]; 
		v3 = _v[_trig[i]._vertex[2]]; 
	}
	void setTriangle(int i, Vector3f & v1, Vector3f & v2, Vector3f & v3)
	{
		_v[_trig[i]._vertex[0]] = v1;
		_v[_trig[i]._vertex[1]] = v2;
		_v[_trig[i]._vertex[2]] = v3;
	}

			
};

#endif //_rt_H
