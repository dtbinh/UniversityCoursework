s0943941

Run the program by the commands 
g++ -o view view.cc matrix4f.cc -lglut -lGLU -lGL
./view arma2.obj skeleton2.out attachment2.out

or ./testscript.sh

Commands 
b - show bones
r - reset to starting position
x - speed up animation time
z - slow down animation time
a - loop animation 

1 - Cannonball animation
2 - Inverse Kinematic Reach outward (one hand)
3 - Inverse Kinematic Reach upward (two hand)
4 - Sequence animation (three keyframes)

Note: each animation may require being in the initial position to work properly. If that is the case, simply press the number to reset the program, and press the number again to activate that animation. The animations within 2 and 3 can stack with each other. 


The files were first loaded in, using a function similar to the one given to load the vertices from the character object. The bones were stored in a TreeNode object, which several features: value (given as a number between 0 and 21), position of joint (in global coordinates), parent (which bone came from where) , children (all the possible children that a bone could have). The weights were stored as a vector of vector ints.

Next, I created three vectors to hold the data. One vector holds the translation matrix for the bones:
1  0  0  GlobalPosition of bone - GlobalPosition of bone's parent[x]
0  1  0  GlobalPosition of bone - GlobalPosition of bone's parent[y]
0  0  1  GlobalPosition of bone - GlobalPosition of bone's parent[z]
0  0  0  1.

One vector holds the rotation matrices for the bones at their rest positions (all identity matrices, because at rest, we assume there are no rotations). The final vector holds the rotation matrices for the bones once rotated. This vector is what I manipulate to achieve the proper keyframe animations. Each element of the vector has a corresponding bone.

Next, the restMatrix and the rotated Matrix need to be calculated. To do this, I created a function which creates a stack of bone joints. For example, if I give the function the value 14, it will return 14, 13, 6, 5, 0. If the stack is taken in reverse order,  the changes made on the central bones will propogate toward the later bones if the values are multiplied together (i.e. a rotation to bone 6 should affect bone 14). Each bone has a stack that it multiplies together. For the rest Matrix, the translation matrix is multiplied by the identity rotation matrix (restMatrix) for each element in the stack.

For the rotated Matrix, I manually set the rotations of certain joints. If I wanted to rotate a joint, I set its value within the rotation vector to be a rotation matrix of some angle. Once all the desired angles are set, the M value is calculated in a similar fashion to the rest Matrix - the chain of necessary joints is multiplied together to get a result. Both the rest Matrix and the rotated Matrix are stored as vectors. 

Using the equation given in the slides (v=Mi*M̂^-1*v), we can proceed to calculate each new vertices of the object. The Mi value is the matrix with manually set values, and the M̂^-1 is the inverted restMatrix. We multiply the bone's weight, the matrix with our changes, the inverted restMatrix and the vertex itself to get a weighted position vector. Taking the sum of all the weighted position vectors, we can find the new position for that vertex. This process is done for each vertex in the object. This linear blending allows for adjusting the skin of our object based on the underlying bones. 

Once I have the final positions for each vertex, I store those values as a vector of positions. This serves as the final keyFrame position. With the initial position vectors as the starting keyFrame, I can animate between the two of these using linear interpolation. Animation was done using the glutIdleFunc function. This calls the animation function to the screen whenever the computer is able. With this, I set a flag to indicate when the character is moving. If true, the skin's values is interpolated between the starting KeyFrame and ending KeyFrame (both vectors of type Vector3f). For example, in the cannonball animation, the starting position is defined as the rest position, and the final frame is defined as the result of the rotations applied to the bones for each vertex. The resulting animation is a linear interpolation ( q = q1 * (1-t) + q2 * (t)).

Inverse Kinematics was implemented using a Cyclic Coordinate Descent algorithm. This involves taking a target destination and minimizing each joint angle (up to the root) until it approaches that point. To achieve this, I specify which joint I'm tweaking (starting with the joint just below the effector I'm trying to position) and the effector position. Next, I check to see if the effector is within a threshold of the desired Endpoint. If it is not (too far away to have reached the point), I calculate the rotations for that joint that would minimize the distance for each angle (x, y, and z). Each of these axis results in a rotation matrix, which are multiplied together to achieve the final rotation. This final rotation is multiplied against the previous rotations of the object. The positions of all the bones are updated, and the next joint is selected. If this value is within the threshold, we terminate. If not, we loop again. This process continues until the end effector has reached a position close enough to the point, or has gone a number of iterations (required if the point selected is beyond the reach of the body). 

To tweak the inverse kinematics, use the function 'inverse kinematics'. The function requires a limb (12, 17, 4 or 21 are suggested) and a desired Vector3f to move to. A demonstration of these in action is provided with the keys 2 and 3. 
