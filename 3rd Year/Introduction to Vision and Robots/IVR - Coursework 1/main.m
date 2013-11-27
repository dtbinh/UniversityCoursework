function main(passeddir)
% this is the controlling function for a complete image set
% it will load all images in a directory which has been passed to it 
% the directory name must start with a d and be followed by and int 
% e.g. 'd1' or 'd2' where 1 or 2 may be specified 

% concats to open dir 
passeddir = strcat('d',int2str(passeddir),'/');
%loads all images from the dir into a flat array 1x100
files = dir(strcat(passeddir,'*.jpg'));
%centarry is a matrix 100x6 which stores the centers of all 3
%objects in x,y format so they can be plotted later 
centarry = double(zeros([(length(files)) 6]));

%below code makes a background based upon weighted movment 
%if little-to-no movement is detcted in background 
%it produces a background based upon average over all the images 

% weighted background generating variables 
%total number of background images
total = 1;
%number of images in weighted background
counter = 1;
%if weighted background is used e.g. movment threshold has been reached 
found = 0;
%the amount of movment that is being used to weight this background 
weight = 0;
H = fspecial('disk', 10);
pause on;

% below code generates background and fills the center array and runs find
% robots which generates the data to be used below
for i = 1:length(files)
    string = strcat(passeddir,files(i).name);
    load = double(importdata(string, 'jpg'));
    %this line goes into a function which computers robots and there
    %direction as well as computing the image minus the robots
    [a,b,c,d,e,f,g] = FINDrobots(string);
    %saves centers into centarry 
    centarry(i, 1) = a;
    centarry(i, 2) = b;
    centarry(i, 3) = c;
    centarry(i, 4) = d;
    centarry(i, 5) = e;
    centarry(i, 6) = f;
 
    %averages image and image minus robots from find robots 
    avgback = (imadd(load,double(g)))/2;
 
    %amount of movment to look for variables
    mindist = 1.5;
    maxdist = 80;

    if(i==1)
        im = double(avgback);
        im2 = double(avgback);
    else
        if(i>5)
           %-1 is used to indicate that the robot or object was not found
           %this value is used throughtout the code fro protection purposes
           if((a && b && c && d && e && f)~=-1)
               % looks for movment to compute background
               rxdist = centarry(i, 1) - centarry(i-4, 1);
               rydist = centarry(i, 2) - centarry(i-4, 2);
               gxdist = centarry(i, 3) - centarry(i-4, 3);
               gydist = centarry(i, 4) - centarry(i-4, 4);
               bxdist = centarry(i, 5) - centarry(i-4, 5);
               bydist = centarry(i, 6) - centarry(i-4, 6);
               rdist = sqrt(rxdist^2 + rydist^2);
               gdist = sqrt(gxdist^2 + gydist^2);
               bdist = sqrt(bxdist^2 + bydist^2);
               total = total + 1;
               %adds and image to the background if enough movement is found  
               if(rdist > mindist && bdist > mindist && gdist > mindist && rdist < maxdist && bdist < maxdist && gdist< maxdist) 
                  lowest = rdist;
                  if(rdist>bdist)
                    lowest = bdist;
                  end
                    if(lowest>gdist)
                        lowest = gdist;
                    end          
                    counter = counter + 1;
                    weight = lowest + weight;
                    found = 1;
                    avgback = avgback * lowest;
                    im = imadd(im,avgback);
               else
                  im2 = imadd(im2,avgback);
               end
            end
         end
    end
end

%choose wether to use movment based background or to use a total average
%background based upon wether enough movment based images where selected or
%a complete avearge is liekly to be better
if(found && counter>20)
   im = uint8(im/(counter+weight));
    
else
    im = uint8(im2/total);
end

% this filter blurs the image slightly producing a nicer background
% reducing robot ghosting
im = imfilter(im, H, 'replicate');

%show background and waits
imshow(im);
title('This is the background');
pause;
hold on;

%this code draws the trace ontop of the background with a filter to stop
%the trace from jumping erraticly when impossible movment is deteced
%between frames
jumptoll = 40;
for i = 1:length(files)
    % ignors the first frame as there is no movement from a previous frame
    if(i==1)
        ;
    else
        %check for -1 protection and plots the line beween frames 
        if ((centarry((i-1),1)||centarry((i),1)||centarry((i-1),2)||centarry((i),2))==(-1)||abs(centarry((i-1),1)-centarry((i),1))>=jumptoll||abs(centarry((i-1),2)-centarry((i),2))>=jumptoll)
            ;
        else
        plot([centarry((i-1),1) centarry((i),1)], [centarry((i-1),2) centarry((i),2)], 'r','LineWidth',2); 
        end
        if ((centarry((i-1),3)||centarry((i),3)||centarry((i-1),4)||centarry((i),4))==(-1)||abs(centarry((i-1),3)-centarry((i),3))>=jumptoll||abs(centarry((i-1),4)-centarry((i),4))>=jumptoll)
            ;
        else
        plot([centarry((i-1),3) centarry((i),3)], [centarry((i-1),4) centarry((i),4)], 'g','LineWidth',2); 
        end
        if ((centarry((i-1),5)||centarry((i),5)||centarry((i-1),6)||centarry((i),6))==(-1)||abs(centarry((i-1),5)-centarry((i),5))>=jumptoll||abs(centarry((i-1),6)-centarry((i),6))>=jumptoll)
            ;
        else
        plot([centarry((i-1),5) centarry((i),5)], [centarry((i-1),6) centarry((i),6)], 'b','LineWidth',2); 
        end
    end
end
title('This is the Trace');
pause;
end

