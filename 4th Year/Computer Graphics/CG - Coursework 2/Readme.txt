s0943941 - Computer Graphics Readme

Runs with commands:
g++ -o demo2 demo2.cc -lglut -lGLU -lGL
./demo2 teapot.obj

Functionality:
Teapot rotation is achieved with i, j, k, l keys
Lighting movement is achieved with 4, 5, 6, 8 keys

Description of implementation:
	Header file:
	First pass through triangle list, calculate normal vectors. Store in a c++ vector.
	Iterate through vertex list. Second pass through triangle list. If vertices match any of the triangles', add to vector list. Average vector list 		for norm. This is stored as another c++ vector, accessible with the function getTriangleNorms().

	Main code:
	Calculate normal list. Initiate/clear the zbuffer for depth analysis (set to the maximum float).
	Iterate through triangles. Transform vertices based on previous assignment. Transform normals as well.
	
	Background Culling: 	Calculate the cross product of the triangle. 
				If the dot product of the cross product and the viewing vector is < 0, draw it. 

	Drawing:	Calculate light intensity constant for phong shading
				Calculate bounding box for rasterization. Loop through each pixel in box.
				Ensure barycentric coordinates for the pixel are within the box.
				Interpolate the vertices for the normal vector of the triangle (Phong Shading)
				Ensure depth is less than the zbuffer before drawing. Updates zbuffer if lower than.
				Calculate phong shading for the pixel and draw it.

	Phong Shading:	Calculate specular light, with Vector from the light source to pixel, the reflection vector of the point, and specular constant.
					Calculate diffuse, with dot product of light and normal of the triangle
					Calculate overall light = light intensity + (specular light + diffuse) * brightness
					Repeat for r,g,b.

	Finish Main Drawing loop

	Floor:		During iteration through triangles, calculate lowest value of teapot without transformations for floor. 
				Create four seperate vectors for triangle vertices of floor (arbitrarily chosen).
				Draw only if z - depth is less than zbuffer value.
				Colors chosen to showcase shadows better.

	Shadows:	Iterate over triangles again. Project each vertex onto the plane of the floor.
				If not culled, draw the shadows onto the floor (drawing works like main drawing function, without any phong shading). 



Sources:
http://www.opengl.org/wiki/Calculating_a_Surface_Normal
http://www.ia.hiof.no/~borres/cgraph/explain/shadow/p-shadow.html
http://content.gpwiki.org/index.php/Backface_culling
http://math.stackexchange.com/questions/13261/how-to-get-a-reflection-vector
http://www.cs.northwestern.edu/~ago820/cs395/Papers/Phong_1975.pdf
http://graphics.wikia.com/wiki/Phong_shading
http://en.wikipedia.org/wiki/Barycentric_coordinate_system_(mathematics)
