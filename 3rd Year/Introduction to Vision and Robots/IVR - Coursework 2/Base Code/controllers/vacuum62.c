/*
 * File:         irobot_create.c
 * Date:         21 Dec 2010
 * Description:  Default controller of the iRobot Create robot
 * Author:       fabien.rohrer@cyberbotics.com
 * Modifications: Do a ctrl-f for Added. 
 * That should have everything I've added - Scott
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

#define DIST_SENSORS_NUMBER 9
#define DIST_SENSOR_FORWARD_1 0
#define DIST_SENSOR_FORWARD_2 1
#define DIST_SENSOR_FORWARD_3 2
#define DIST_SENSOR_FORWARD_LEFT 3
#define DIST_SENSOR_LEFT 4
#define DIST_SENSOR_BACK_LEFT 5
#define DIST_SENSOR_BACK_RIGHT 6
#define DIST_SENSOR_RIGHT 7
#define DIST_SENSOR_FORWARD_RIGHT 8

static WbDeviceTag dist_sensors[DIST_SENSORS_NUMBER];
static const char *dist_sensors_name[DIST_SENSORS_NUMBER] = {
  "distance_sensor_forward_1",
  "distance_sensor_forward_2",
  "distance_sensor_forward_3",
  "distance_sensor_forward_left",
  "distance_sensor_left",
  "distance_sensor_back_left",
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
//Added
#define X_START 0
#define Y_START 0
//Done Added

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

static bool is_there_a_wall_near(){
  return (wb_distance_sensor_get_value(dist_sensors[DIST_SENSOR_FORWARD_1]) < 300 ||
          wb_distance_sensor_get_value(dist_sensors[DIST_SENSOR_FORWARD_2]) < 300 ||
          wb_distance_sensor_get_value(dist_sensors[DIST_SENSOR_FORWARD_3]) < 300);
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



static void turn_to_wall(char turnDir) {
  stop();
  //double lfDist = wb_distance_sensor_get_value(dist_sensors[DIST_SENSOR_FORWARD_LEFT]);
  //double lbDist = wb_distance_sensor_get_value(dist_sensors[DIST_SENSOR_BACK_LEFT]);
  //double rfDist = wb_distance_sensor_get_value(dist_sensors[DIST_SENSOR_FORWARD_RIGHT]);
  //double rbDist = wb_distance_sensor_get_value(dist_sensors[DIST_SENSOR_BACK_RIGHT]);
  if(turnDir == 'r'){
    wb_differential_wheels_set_speed(10, -10);
    do{
      step();
      } while(wb_distance_sensor_get_value(dist_sensors[DIST_SENSOR_FORWARD_LEFT]) < wb_distance_sensor_get_value(dist_sensors[DIST_SENSOR_BACK_LEFT]));
    }
  if(turnDir == 'l'){
    wb_differential_wheels_set_speed(-10, 10);
    do{
      step();
      } while(wb_distance_sensor_get_value(dist_sensors[DIST_SENSOR_FORWARD_RIGHT]) < wb_distance_sensor_get_value(dist_sensors[DIST_SENSOR_BACK_RIGHT]));
  }
  stop();
  step();
}
static void turn(double angle) {  
  stop();
  double target_angle = get_bearing() + angle;
  if (target_angle > 2 * M_PI){
    target_angle -= 2*M_PI;
  }
  else if(target_angle < 0){
    target_angle += 2*M_PI;
  }

  double neg = (angle < 0.0)? -1.0: 1.0;
  double difference = -1;
  wb_differential_wheels_set_speed(neg*90, -neg*90);
  do{
      difference = fabs(target_angle - get_bearing());
      step();
    } while(difference > 0.02);
    
  //fine tune angle:
  stop();
  step();
  
  if(target_angle > get_bearing()){
    wb_differential_wheels_set_speed(5, -5);
    do{
      difference = fabs(target_angle - get_bearing());
      step();
    } while(difference <= 0.2 && target_angle > get_bearing());
  }
  
  else if(target_angle < get_bearing()) {
    wb_differential_wheels_set_speed(-5, 5);
    do{
      difference = fabs(target_angle - get_bearing());
      step();
    } while(difference <= 0.2 && target_angle < get_bearing());
  }
  
  stop();
  step();
}
//Added - This function says return to the starting point.
//Does so by calculating the dot product of the angle facing north[2], north[0]
//And the angle to the origin. It also needs the additional pi/2 to correctly calculate the angle.
//Decently accurate - possibly correct by increasing number of decimal places or adding error checking
static void return_home(double x, double y) {
  const double *north = wb_compass_get_values(compass);
  double dotproduct = ((-x)*north[2])+((-y)*north[0]);
  double magnitude1 = sqrt(pow(x, 2) + pow(y,2));
  double magnitude2 = sqrt(pow(north[0], 2) + pow(north[2], 2));
  double alpha = acos(dotproduct/(magnitude1*magnitude2));
  turn(alpha+1.5708);
  }
//Done Added

// ########################## Get Values ########################################

static double get_bearing() {
  const double *north = wb_compass_get_values(compass);
  double rad = atan2(north[0], north[2]);
  double bearing = (rad - 1.5708);
  if (bearing < 0.0)
    bearing = bearing + 2 * M_PI;
  return bearing;
}

static double randdouble() {
  return rand()/((double)RAND_MAX+1);
}
//Added - Euclidean Distance
static double calc_dist(double x1, double y1, double x2, double y2) {
  double distance  = sqrt(pow((x2 - x1), 2) + pow((y2 - y1), 2));
  return distance;
}
//Done Added

// ##############################################################################

/* main */
int main(int argc, char **argv)
{
  wb_robot_init();
  
  printf("Default controller of the iRobot Create robot started...\n");
  
  init_devices();
  //_camera = wb_robot_get_device("camera");
  //Added
  double xcoord = X_START;
  double ycoord = Y_START;
  
  double phi = get_bearing();
  int counter = 0;
  int returnhome = 0;
  int clean = 1;
  //Done Added
  camera = wb_robot_get_device("camera");
  wb_camera_enable(camera,get_time_step());
  srand(time(NULL));
  
  

  wb_led_set(leds[LED_ON], true);
  passive_wait(0.5);
  
  while (clean) {//Added clean rather than true
    //printf("googelybab %f \n", get_bearing());
  if (is_there_a_virtual_wall()) {
      printf("Virtual wall detected\n");
      turn(M_PI);
    } 
      else if (is_there_a_wall_near()) {
        printf("wall \n");
        turn_to_wall('r');
        passive_wait(0.5);
    }
      else if (is_there_a_collision_at_left()){
        printf("Left obstacle detected\n");
        go_backward();
        passive_wait(0.5);
        turn(M_PI*randdouble());
    } 
      else if (is_there_a_collision_at_right()){
        printf("Right obstacle detected\n");
        go_backward();
        passive_wait(0.5);
        turn(-M_PI*randdouble());
    } else {
    //Added
          wb_differential_wheels_enable_encoders(get_time_step());
          wb_differential_wheels_set_encoders(0.0, 0.0);
          go_forward();
      //calculate odometry
          // printf("xcoord: %f \n", xcoord);
           

        passive_wait(0.1);
        double l = wb_differential_wheels_get_left_encoder();
        double r = wb_differential_wheels_get_right_encoder();
        double dl = l / ENCODER_RESOLUTION * WHEEL_RADIUS; // distance covered by left wheel in meter
        double dr = r / ENCODER_RESOLUTION * WHEEL_RADIUS; // distance covered by right wheel in meter
        phi = get_bearing();
        xcoord = (xcoord + .5*(dl+dr)*cos(phi));
        ycoord = (ycoord + .5*(dl+dr)*sin(phi));
        counter = counter++;
        printf("Counter: %d \n", counter);
        
        //After a certain time, returns home and sets value to true
        if(counter == 200) {
        return_home(xcoord, ycoord);
        returnhome = 1;
        }
        
        //If it is returning home, calculate distance from home. If under threshold, finish cleaning. 
        if(returnhome) {
          if(calc_dist(xcoord, ycoord, X_START, Y_START) < .05) {
          clean = 0;
          }
        }
        
    }
    //Done Added
    wb_camera_get_image(camera);
    fflush_ir_receiver();
    step();
  };
  
  return EXIT_SUCCESS;
}

