/*
 * File:         irobot_create.c
 * Date:         21 Dec 2010
 * Description:  Default controller of the iRobot Create robot
 * Author:       fabien.rohrer@cyberbotics.com
 * Modifications:
 */

/* include headers */
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>

#include <webots/differential_wheels.h>
#include <webots/distance_sensor.h>
#include <webots/led.h>
#include <webots/receiver.h>
#include <webots/robot.h>
#include <webots/touch_sensor.h>
#include <webots/camera.h>
#include <webots/compass.h>


//Stucture used for storing x,y coordinates
//Used for odometry, start positions, saving wall coordinates etc.
typedef struct{
  double x;
  double y;
} odomStruct;

/* device stuff */
static WbDeviceTag camera; 
//static WbDeviceTag _camera;   // for displaying camera images

#define BUMPERS_NUMBER 2
#define BUMPER_LEFT 0
#define BUMPER_RIGHT 1
static WbDeviceTag bumpers[BUMPERS_NUMBER];
static const char *bumpers_name[BUMPERS_NUMBER] = {
  "bumper_left",
  "bumper_right"
};
static double get_bearing();

#define CLIFF_SENSORS_NUMBER 4
#define CLIFF_SENSOR_LEFT 0
#define CLIFF_SENSOR_FRONT_LEFT 1
#define CLIFF_SENSOR_FRONT_RIGHT 2
#define CLIFF_SENSOR_RIGHT 3
static WbDeviceTag cliff_sensors[CLIFF_SENSORS_NUMBER];
static const char *cliff_sensors_name[CLIFF_SENSORS_NUMBER] = {
  "cliff_left",
  "cliff_front_left",
  "cliff_front_right",
  "cliff_right"
};

//Added distance sensors to robot

#define DIST_SENSORS_NUMBER 15
#define DIST_SENSOR_FORWARD_1 0
#define DIST_SENSOR_FORWARD_2 1
#define DIST_SENSOR_FORWARD_3 2
#define DIST_SENSOR_FORWARD_4 3
#define DIST_SENSOR_FORWARD_5 4
#define DIST_SENSOR_LEFT_1 5
#define DIST_SENSOR_LEFT_2 6
#define DIST_SENSOR_LEFT_3 7
#define DIST_SENSOR_LEFT_4 8
#define DIST_SENSOR_LEFT_5 9
#define DIST_SENSOR_LEFT_FORWARD 10
#define DIST_SENSOR_LEFT_BACK 11
#define DIST_SENSOR_BACK_RIGHT 12
#define DIST_SENSOR_RIGHT 13
#define DIST_SENSOR_FORWARD_RIGHT 14

static WbDeviceTag dist_sensors[DIST_SENSORS_NUMBER];
static const char *dist_sensors_name[DIST_SENSORS_NUMBER] = {
  "distance_sensor_forward_1",
  "distance_sensor_forward_2",
  "distance_sensor_forward_3",
  "distance_sensor_forward_4",
  "distance_sensor_forward_5",
  "distance_sensor_left_1",
  "distance_sensor_left_2",
  "distance_sensor_left_3",
  "distance_sensor_left_4",
  "distance_sensor_left_5",
  "distance_sensor_left_forward",
  "distance_sensor_left_back",
  "distance_sensor_back_right",
  "distance_sensor_right",
  "distance_sensor_forward_right"
};

#define LEDS_NUMBER 3
#define LED_ON 0
#define LED_PLAY 1
#define LED_STEP 2
static WbDeviceTag leds[LEDS_NUMBER];
static const char *leds_name[LEDS_NUMBER] = {
  "led_on",
  "led_play",
  "led_step"
};

static WbDeviceTag receiver;
static const char *receiver_name = "receiver";

//Added compass to robot for orientation
static WbDeviceTag compass;
static const char *compass_name = "pirate_compass";

/* Misc Stuff */
#define MAX_SPEED 250
#define NULL_SPEED 0
#define HALF_SPEED 125
#define MIN_SPEED -250

#define WHEEL_RADIUS 0.031
#define AXLE_LENGTH 0.271756
#define ENCODER_RESOLUTION 507.9188
#define X_START 0
#define Y_START 0

/* helper functions */
static int get_time_step() {
  static int time_step = -1;
  if (time_step == -1)
    time_step = (int) wb_robot_get_basic_time_step();
  return time_step;
}

static void step() {
  if (wb_robot_step(get_time_step()) == -1) {
    wb_robot_cleanup();
    exit(EXIT_SUCCESS);
  }
}

static void init_devices() {
  int i;

  receiver = wb_robot_get_device(receiver_name);
  wb_receiver_enable(receiver, get_time_step());
  
  compass = wb_robot_get_device(compass_name);
  wb_compass_enable(compass, get_time_step());

  for (i=0; i<LEDS_NUMBER; i++) {
    leds[i] = wb_robot_get_device(leds_name[i]);
  }

  for (i=0; i<BUMPERS_NUMBER; i++) {
    bumpers[i] = wb_robot_get_device(bumpers_name[i]);
    wb_touch_sensor_enable(bumpers[i], get_time_step());
  }

  for (i=0; i<CLIFF_SENSORS_NUMBER; i++) {
    cliff_sensors[i] = wb_robot_get_device(cliff_sensors_name[i]);
    wb_distance_sensor_enable(cliff_sensors[i], get_time_step());
  }
  
  for(i=0; i<DIST_SENSORS_NUMBER; i++) {
    dist_sensors[i] = wb_robot_get_device(dist_sensors_name[i]);
    wb_distance_sensor_enable(dist_sensors[i], get_time_step());
  }
}


//####################### Detection Methods ###############################################

static bool is_something_near(){
//Function checks each of the front sensors, and if they distance by any of them is below a certain threshold,
//the robot is about to collide with an obstacle
  return (wb_distance_sensor_get_value(dist_sensors[DIST_SENSOR_FORWARD_1]) < 325 ||
          wb_distance_sensor_get_value(dist_sensors[DIST_SENSOR_FORWARD_2]) < 325 ||
          wb_distance_sensor_get_value(dist_sensors[DIST_SENSOR_FORWARD_3]) < 320 ||
          wb_distance_sensor_get_value(dist_sensors[DIST_SENSOR_FORWARD_4]) < 325 ||
          wb_distance_sensor_get_value(dist_sensors[DIST_SENSOR_FORWARD_5]) < 325);
  }
  
static bool is_it_a_wall(char facingWall, odomStruct odomXY){
//This function, upon encountering an obstacle, decides whether it is a wall or a piece of furniture
  
  double closestSensor = 1000;
  int i;
  //Find the sensor with the smallest distance value
  for (i = DIST_SENSOR_FORWARD_1 ; i <= DIST_SENSOR_FORWARD_5;  i++){
     if(wb_distance_sensor_get_value(dist_sensors[i]) < closestSensor){
      closestSensor = wb_distance_sensor_get_value(dist_sensors[i]);
     }
  }
  
  
  //Sensor values were scaled up by 1000 for readability, so we must scale them back down for measurements
  closestSensor = closestSensor / 1000;
  
  //Decide if we are close enough to a wall for there to be a collision with it
  //e.g if we are in the middle of the room, it is not likely we will be colliding with a wall
  //if we are very close to the wall and encounter something, it is probably the wall we are about to encounter
  switch(facingWall)
  {
    case 't': //If we are facing the top wall, check how close we are from the top of the room (y = 6)
         return(fabs(odomXY.y
               + closestSensor
               - 6) <= 0.3);
    case 'l' : //If we are facing the left wall, check how close we are to the far left of the room (x = 0)
         return(fabs(odomXY.x
               + closestSensor
               ) <= 0.3);
    case 'r' : //If we are facing the right wall, check how close we are to the far right of the room (x = -6);
         return(fabs(odomXY.x
               - closestSensor
               + 6) <= 0.3);
    case 'b' : //If we are facing the bottom wall, check how close we are to the bottom of the room (y = 0)
         return(fabs(odomXY.y
               - closestSensor
               ) <= 0.3);
  }
  
  //Default return value
  return false;
}

static bool is_there_a_collision_at_left() {
  return (wb_touch_sensor_get_value(bumpers[BUMPER_LEFT]) != 0.0);
}

static bool is_there_a_collision_at_right() {
  return (wb_touch_sensor_get_value(bumpers[BUMPER_RIGHT]) != 0.0);
}

static void fflush_ir_receiver() {
  while (wb_receiver_get_queue_length(receiver) > 0)
    wb_receiver_next_packet(receiver);
}

static bool is_there_a_virtual_wall() {
  return (wb_receiver_get_queue_length(receiver) > 0);
}

// #####################################################################################


//################### Actions ##########################################################

static void go_forward() {
  wb_differential_wheels_set_speed(MAX_SPEED, MAX_SPEED);
}

static void go_backward() {
  wb_differential_wheels_set_speed(-HALF_SPEED, -HALF_SPEED);
}

static void stop() {
  wb_differential_wheels_set_speed(-NULL_SPEED, -NULL_SPEED);
}

static void passive_wait(double sec) {
  double start_time = wb_robot_get_time();
  do {
    step();
  } while(start_time + sec > wb_robot_get_time());
}

static void turn_to_wall() {
//This function will turn right to exactly align its left side parallel to the wall.
  stop();
    wb_differential_wheels_set_speed(50, -50);
    //Keep turning until the two sensors on the robot's left hand side are the same distance from the wall
    //i.e the are both lined up with the straight wall
    do{
      step();
      } while(wb_distance_sensor_get_value(dist_sensors[DIST_SENSOR_LEFT_FORWARD]) < wb_distance_sensor_get_value(dist_sensors[DIST_SENSOR_LEFT_BACK]));
  stop();
  step();
}
static void turn(double angle) {
//This modified turn function turns the robot the amount specified as an input parameter
//But also has a fine tuning step to account for slight inaccuracies in turning 
  stop();
  double target_angle = get_bearing() + angle; //Our desired angle is our current orientation plus the amount turned
  
  //If target is greater than 360 degrees or less than 0, cycle round
  if (target_angle > 2 * M_PI){
    target_angle -= 2*M_PI;
  }
  else if(target_angle < 0){
    target_angle += 2*M_PI;
  }

  double neg = (angle < 0.0)? -1.0: 1.0; //Check direction of angle
  double difference = -1; //initial value for difference
  wb_differential_wheels_set_speed(neg*90, -neg*90); //start turning
  
  //Keep turning until we are within 0.02 radians of our desired orientation
  do{
      difference = fabs(target_angle - get_bearing());
      step();
    } while(difference > 0.02);
    
  //Fine tune angle:
  stop();
  step();
  
  //If we have undershot..
  if(target_angle > get_bearing()){
  //Turn slowly a bit more until we are within some threshold
    wb_differential_wheels_set_speed(2, -2);
    do{
      difference = fabs(target_angle - get_bearing());
      step();
    } while(difference <= 0.2 && target_angle > get_bearing());
  }

  //Two situations if target angle is less than bearing...  
  else if(target_angle < get_bearing()) {
    //We have genuinely overshot,
    if(fabs(target_angle - get_bearing()) < 6.0){
    //Turn slowly a bit back the way until we are within some threshold
    wb_differential_wheels_set_speed(-2, 2);
      do{
        difference = fabs(target_angle - get_bearing());
        step();
      } while(difference <= 0.2 && target_angle < get_bearing());
    }
    
    //If theres a difference of 6 between the target and current bearing
    //Theres no way we overshot/undershot by 6 radians!
    //It means we have passed by the 0 radians mark in our turn, and gone to say 6.20
    else{
    //Turn a little bit more to within some threshold
    wb_differential_wheels_set_speed(2, -2);
      do{
      difference = fabs(target_angle - get_bearing());
      step();
      } while(difference >= 6 || target_angle > get_bearing());
    }
    
  }
  stop();
  step();
}

static void home_to(double destX, double destY, double odX, double odY) {
//This function uses odometry to home in on a specified point

  const double *north = wb_compass_get_values(compass); //Get our current orientation vector
  
  //Uses dotproduct rule: cos(alpha) = (a.b) / |a||b|
  //To get angle to turn back to face destination
  double dotproduct = ((destX - odX)*north[2])+((destY -odY)*north[0]);
  double magnitude1 = sqrt(pow(destX - odX, 2) + pow(destY-odY,2));
  double magnitude2 = sqrt(pow(north[0], 2) + pow(north[2], 2));
  double alpha = acos(dotproduct/(magnitude1*magnitude2));
  turn(alpha);
  }


// ########################## Get Values ########################################

static double get_bearing() {
//This function converts the compass vector into a usable radians value

  const double *north = wb_compass_get_values(compass);
  double rad = atan2(north[0], north[2]);
  double bearing = (rad);
  if (bearing < 0.0)
    bearing = bearing + 2 * M_PI;
  return bearing;
}


static double calc_dist(double x1, double y1, double x2, double y2) {
//This function calculates the euclidean distance between two points 
  double distance  = sqrt(pow((x2 - x1), 2) + pow((y2 - y1), 2));
  return distance;
}

static odomStruct get_odom(double xcoord, double ycoord) {
//This function measures the amount the wheels have turned in order to update our measurement of odometry
        double l = wb_differential_wheels_get_left_encoder();
        double r = wb_differential_wheels_get_right_encoder();
        double dl = l / ENCODER_RESOLUTION * WHEEL_RADIUS; // distance covered by left wheel in meter
        double dr = r / ENCODER_RESOLUTION * WHEEL_RADIUS; // distance covered by right wheel in meter
        odomStruct XY;
//In our implementation, we simply use the compass to get our current orientation
//It gives more accurate results, but if required, we could just change this part to calculate phi by:
// phi = phi + - 0.5*(dl - dr)/(AXLE_LENGTH) 
        double phi = get_bearing();
        XY.x = (xcoord + .5*(dl+dr)*cos(phi));
        XY.y = (ycoord + .5*(dl+dr)*sin(phi));
        return XY;
}

// ##############################################################################

/* Main */
int main(int argc, char **argv)
{
//This function is the main controller for the robot and is responsible for most of its behaviour

  bool following_wall = false; //The robot is not following along a wall to start with
  bool cleaning = true; //Should the robot still be cleaning the room? Yes at the beginning (obviously)
  bool returning_to_wall = false; //Is robot moving out from wall or coming back?
  bool homing = false; //Is robot homing to a specified position
  int corner_counter = 0; //How many corners have i approached so far
  
  //odometry of robot relative to room
  odomStruct odomXY;
  //initial start position of robot
  odomStruct startPos;
  //how far along the wall it has got
  odomStruct lastPos;
  //position at wall
  odomStruct wallPos;
  //Walls i could currently face: Top, Left, Bottom or Right
  char wallFacing[4] = {'l', 't', 'r', 'b'};
  int currentWall = 0;   //Curently facing the top wall
  
  wb_robot_init();
  
  printf("Default controller of the iRobot Create robot started...\n");
  
  init_devices();

  double phi = get_bearing();
  
  

  //_camera = wb_robot_get_device("camera");
  camera = wb_robot_get_device("camera");
  wb_camera_enable(camera,get_time_step());
  
  srand(time(NULL));
  
  wb_led_set(leds[LED_ON], true);
  passive_wait(0.5);
  
  //Align self with left wall to start off
  turn(-get_bearing());
  currentWall = 0;
  
  passive_wait(0.5);
  
  //Odometry assumes that start position is (0,0). However, we can get the global position of robot in room by measuring
  //how far it is from the bottom corner with the distance sensors
  odomXY.x = -wb_distance_sensor_get_value(dist_sensors[DIST_SENSOR_FORWARD_3])/1000;
  odomXY.y = wb_distance_sensor_get_value(dist_sensors[DIST_SENSOR_LEFT_3])/1000;
  
  startPos.x = odomXY.x;
  startPos.y = odomXY.y;
  
  wallPos = odomXY;
  
  //Main cleaning loop
  while (cleaning) {
  
    wb_differential_wheels_enable_encoders(get_time_step());
    
    //Check for possible collisions
      if (is_something_near()) {
      
        if(is_it_a_wall(wallFacing[currentWall], odomXY)){ //Is possible collision with a wall or a piece of furniture?

          if(!following_wall){
            //If not currently following wall, turn to follow wall
            wb_differential_wheels_set_encoders(0.0, 0.0);
            turn_to_wall();
            currentWall = (currentWall + 1) % 4; //Update wall we are currently facing
            passive_wait(0.5);
          
            odomXY = get_odom(odomXY.x, odomXY.y); //Update odometry
            following_wall = true;
            lastPos = odomXY; //Save position on wall to we can move along a specified amount
          }
          else{
            //Following a wall and hit another wall? Its a corner, navigate corner
            wb_differential_wheels_set_encoders(0.0, 0.0);
            turn(M_PI/2);
            odomXY = get_odom(odomXY.x, odomXY.y);
            currentWall = (currentWall + 1) % 4;
            corner_counter++;
          }
      }
      
        else{
          //If you encounter furniture, turn 180 degrees and head back to wall
          wb_differential_wheels_set_encoders(0.0, 0.0);
          stop();
          passive_wait(0.1);
          turn(M_PI);
          odomXY = get_odom(odomXY.x, odomXY.y);
          currentWall = (currentWall + 2) % 4;
          passive_wait(0.1);
          following_wall = false;
        }
    }
      else if (is_there_a_collision_at_left()){
        //If for some reason distance sensors havent caught an object, use the same behaviour
        //if you just bump into it
        printf("Left obstacle detected\n");
        wb_differential_wheels_set_encoders(0.0, 0.0);
        stop();
        passive_wait(0.1);
        go_backward();
        passive_wait(0.5);
        turn(M_PI);
        odomXY = get_odom(odomXY.x, odomXY.y);
        currentWall = (currentWall + 2) % 4;
        //printf("%c \n", wallFacing[currentWall]);
        passive_wait(0.1);
        following_wall = false;
        
    } 
      else if (is_there_a_collision_at_right()){
        //Same as for left collision
        printf("Right obstacle detected\n");
        wb_differential_wheels_set_encoders(0.0, 0.0);
        stop();
        passive_wait(0.1);
        go_backward();
        passive_wait(0.5);
        turn(M_PI);
        odomXY = get_odom(odomXY.x, odomXY.y);
        currentWall = (currentWall + 2) % 4;
        passive_wait(0.1);
        following_wall = false;
    }

      else if(following_wall){
          wb_differential_wheels_set_encoders(0.0, 0.0);
          //Follow the wall along a set interval
          if(calc_dist(odomXY.x, odomXY.y, lastPos.x, lastPos.y) < 0.3){
             go_forward();
             passive_wait(0.1);
             odomXY = get_odom(odomXY.x, odomXY.y);
          }
          else{
          //Then turn off into the room
            turn(M_PI/2);
            passive_wait(0.1);
            odomXY = get_odom(odomXY.x, odomXY.y);
            following_wall = false;
            currentWall = (currentWall + 1) % 4;
            //printf("%c \n", wallFacing[currentWall]);
            wallPos = odomXY;
            returning_to_wall = false;
          }
          
          
      //calculate odometry
          // printf("xcoord: %f \n", xcoord);
      }
      else {
          wb_differential_wheels_set_encoders(0.0, 0.0);
          //Default: Go forward
          go_forward();
          
          if(!returning_to_wall && calc_dist(odomXY.x, odomXY.y, wallPos.x, wallPos.y) >= 3.2){
          //If you have gone far enough into the room, return to the wall
             wb_differential_wheels_set_encoders(0.0, 0.0);
             stop();
             passive_wait(0.1);
             turn(M_PI);
             odomXY = get_odom(odomXY.x, odomXY.y);
             currentWall = (currentWall + 2) % 4;
             passive_wait(0.1);
             following_wall = false;
             returning_to_wall = true;
          }
          
        passive_wait(0.1);
        odomXY = get_odom(odomXY.x, odomXY.y);
      }
      //Return to start position if you are finished cleaning
      if(corner_counter == 4){
         stop();
         cleaning = false;
         printf("homing");
      }
    
  }
  
  bool finalnav = true;
  
  while(finalnav){
  //This loop handles navigation back to starting position
    wb_differential_wheels_set_encoders(0.0, 0.0);
    if(!homing){
    //turn to direction of starting position
    home_to(startPos.x, startPos.y, odomXY.x, odomXY.y);
    passive_wait(0.1);
    odomXY = get_odom(odomXY.x, odomXY.y);
    homing = true;
    }
    
    if(calc_dist(odomXY.x, odomXY.y, startPos.x, startPos.y) > 0.1){
    //Keep moving towards start position until you are within some distance threshold of it
      go_forward();
      passive_wait(0.1);
      odomXY = get_odom(odomXY.x, odomXY.y);
    }
    else{
      printf("finished"); //Finish program
      stop();
      step();
      finalnav = false;
    }
    
  }  
    wb_camera_get_image(camera);
    fflush_ir_receiver();
    step();
  
  return EXIT_SUCCESS;
}



