function [rxc, ryc, gxc, gyc, bxc, byc, background] = FINDrobots(image)
%this function takes in an image and attempts to find all 3 robots and
%there direction. as well as computing the image minus the robots for the
%background 

% loads the image from the file this is passed in it will be in the from of
% d#/00000###.jpg
loaded = importdata(image, 'jpg');

%below is matrix normalazation by bob fishcer as a replacment for out for
%loop preforming the same functions this greatly acclerates our code 
[R,C,D]=size(double(loaded));
NM = sum(double(loaded)*2,3);
NM3 = zeros(R,C,3); 
NM3(:,:,1) = NM;
NM3(:,:,2) = NM;
NM3(:,:,3) = NM;
IN = imdivide(double(loaded)*2,NM3);

%this sperates the image channels into thre respective color spaces
r = double(IN(:,:,1));
g = double(IN(:,:,2));
b = double(IN(:,:,3));

% this computes a greysale image of e.g. color space bye subtracting the other 
%color  spaces to remove the diffrent robots from the diffrent channels the channels
% are also weighted when doing this becuase some are stronger than others 
rnew = r - g/2 - b/2;
gnew = 1.4*g - r/2 - b/2;
bnew = 1.4*b - r/2 - g/2;

%convert back to an intgeral from a partial fraction and convert to a unit
%so image can be displayed 
justRed = uint8 (rnew*255);
justGreen = uint8 (gnew*255);
justBlue = uint8 (bnew*255);

%Call the function single color which actually preforms the calulations for
%finding the center of the object and the point to which a line will be
%drawn and the radius for the circle to hightlight the the object 
%single color also produces a background with the the background filterd
%from the robot
[rcentx,rcenty,rxline, ryline, rradius, backr] = singlecolor(loaded, justRed);
[gcentx,gcenty,gxline, gyline, gradius, backg] = singlecolor(loaded, justGreen);
[bcentx,bcenty,bxline, byline, bradius, backb] = singlecolor(loaded, justBlue);

%Combines the black robot images with multiplication identity
darkenrobots = (backr);
darkenrobots = immultiply((backg), darkenrobots);
darkenrobots = uint8(immultiply((backb), darkenrobots));

%Multiplies the boolean darkenrobots with the color channels and sets them
%into a background without the robots.
background(:,:,1)=immultiply(darkenrobots, loaded(:,:,1));
background(:,:,2)=immultiply(darkenrobots, loaded(:,:,2));
background(:,:,3)=immultiply(darkenrobots, loaded(:,:,3));


%send centers to be caputerd in centarray 
rxc = rcentx;
ryc = rcenty;
gxc = gcentx;
gyc = gcenty;
bxc = bcentx;
byc = bcenty;

%show the image with the robot circled and a line shwoing its direction 

imshow(loaded);
hold on

rc = [rcentx,rcenty];
gc = [gcentx, gcenty];
bc = [bcentx, bcenty];

%Circle angle
a = [0:2*pi/30:2*pi];


title(strcat({'File name: '}, image));
%Plot the centers and direction the robots
% -1 protection if the robot is not found 
if(rc(1) ~= -1 && rc(2) ~= -1 && rradius~= -1)
    plot(rc(1), rc(2), 'r*');
    plot((rradius/1.5)*cos(a)+rc(1), (rradius/1.5)*sin(a)+rc(2), 'r');
    if(rxline~= -1 && ryline~= -1)
        plot([rc(1), ryline], [rc(2), rxline], 'r','LineWidth',2); 
    end
end

if(gc(2) ~= -1 && gc(2)~= -1 && gradius~= -1)
    plot(gc(1), gc(2), 'g*');
    plot((gradius/1.5)*cos(a)+gc(1), (gradius/1.5)*sin(a)+gc(2), 'g');
    if (gxline~= -1 && gyline~= -1)
        plot([gc(1), gyline], [gc(2), gxline], 'g','LineWidth',2);
    end
end

if(bc(1)~= -1 && bc(2)~= -1 && bradius~= -1)
    plot(bc(1), bc(2), 'b*');
    plot((bradius/1.5)*cos(a)+bc(1), (bradius/1.5)*sin(a)+bc(2), 'b');
    if (bxline~= -1 && byline~= -1)
        plot([bc(1), byline], [bc(2), bxline], 'b','LineWidth',2); 
    end
end

hold off
pause(.01);

end

