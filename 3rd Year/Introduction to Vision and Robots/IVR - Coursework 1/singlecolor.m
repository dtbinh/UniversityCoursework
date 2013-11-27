function [xcenter, ycenter, xline, yline, radius, rinverse] = singlecolor(loaded, justColor)

%Error checking - default values
xcenter = -1;
ycenter = -1;
xline = -1;
yline = -1;
radius = -1;


%Perform thresholding
hist = dohist(justColor,0);
filter = fspecial('gaussian', [50 1], 6);
tmp1 = conv(filter,hist);
peak = find(tmp1 == max(tmp1));
bw = justColor > max(peak);
%Eliminate noise
robot = bwareaopen(bw, 50);
rinverse = (~(robot));
[l,w] = size(robot);

%Calculate of robot in this color channel by finding the size of the
%maximum connected object in a black and white image, also calculate a
%bounding box for the robot 
s  = regionprops(robot, {'centroid','area','BoundingBox'});

% check an object has been found
if not(isempty(s))
    %gets the id of the maximum found object hopefully a robot 
    [~, id] = max([s.Area]);
    %varaible to adjst the bounding box 
    boundingboxadjust = 5;
    radius = (s(id).BoundingBox(3)+s(id).BoundingBox(4))/2;

    %Error checking to see if the adjusted box would exceed the edges of
    %the graph 
    if(s(id).BoundingBox(1)+radius-boundingboxadjust>l || s(id).BoundingBox(2)+radius-boundingboxadjust>w)
        boundingboxadjust = 0;
    end
  
    % crop the image to just the boundintg box then convert it to graysacle
    % do a histogram on the grayscale and look for peaks so that the arrow
    % can be filterd out in black and white
    arrow = imcrop(loaded,[s(id).BoundingBox(1)+boundingboxadjust, s(id).BoundingBox(2)+boundingboxadjust, radius-boundingboxadjust, radius-boundingboxadjust]);
    arrow = rgb2gray(arrow);
    hist = dohist(arrow, 0);
    tmp1 = conv(filter, hist);
    peak = find(tmp1 == max(tmp1));

    % find highest peak to left
  xmaxl = -1;
  pkl = -1;
  for i = 2 : peak-1
      if tmp1(i-1) < tmp1(i) & tmp1(i) >= tmp1(i+1)
          if tmp1(i) > xmaxl
          xmaxl = tmp1(i);
          pkl = i;
          end 
      end
  end
  if pkl == -1
      pkl = 1;
      xmaxl = 1;
  end

  %use threshold to convert crop box to black and white so arrow can be
  %identified 
  athresh = arrow < pkl;
  athresh = bwareaopen(athresh, 30);
   
  %identify the arrow and its orentation 
  tri = regionprops(athresh, {'centroid','area','Orientation'});
  if not(isempty(tri))
      [~, id2] = max([tri.Area]);

      xcenter = s(id).Centroid(1);
      ycenter = s(id).Centroid(2);
      % add the arrow's lcoation inside the boudnign box to the bounding
      % box so its location in the overall image is known 
      xtriangle = s(id).BoundingBox(1) + tri(id2).Centroid(1);
      ytriangle = s(id).BoundingBox(2) + tri(id2).Centroid(2);

      % get the angle that the trianlge is on 
      angle = tri(id2).Orientation;
      refpoint = [xcenter,ycenter];
      %refpoint = [ycenter,xcenter];
      
      % roate the triangle angle so its it point in the correct direction 
      alpha = angle+90;

      % get size of the image so that at line can be drawn right up to the
      % edge of it 
      [x y z] = size(loaded);
      %below code calculate 2 point on either side of the overall image at the angle given 
      %through the center of the main robot    
      horizontal_distance = ((refpoint(2)-1)/tand(alpha));
      if ((refpoint(1) + horizontal_distance) > y)
          vertical_distance = ((refpoint(1)-1)/cotd(alpha));
          verctical_distance_reversed = ((y-refpoint(1))/cotd(alpha));
          xline = [(refpoint(2) + vertical_distance), (refpoint(2) - verctical_distance_reversed)];
          yline = [1, y];
      else
          horizontal_distance_reversed = ((x-refpoint(2))/tand(alpha));
          xline = [1, x];
          yline = [(refpoint(1)+ horizontal_distance), (refpoint(1)- horizontal_distance_reversed)];
      end

      %working 2point - direction vector of a line joining the two
      %centroids
      w = [(xcenter-xtriangle),(ycenter-ytriangle)];

      %1 direction vector of 1 half of the region prop orientatation 
      d1 = [xcenter-yline(1),ycenter-xline(1)];
      
      % work out the angle between the orientation line and line between
      % two point centroid's
      angle = (180/pi)*(acos((dot(w,d1))/((norm(w,2))+(norm(d1,2)))));
      
      %choose which half of the orentation line is point forward and pass
      %the point at the edge of the frame back so it can be plotted as a
      %line 
      if (angle <= 90) 
          xline = xline(1);
          yline = yline(1);
      else
          xline = xline(2);
          yline = yline(2);
      end
   end
end

