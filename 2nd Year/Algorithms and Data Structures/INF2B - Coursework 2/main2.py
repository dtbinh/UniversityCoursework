############################################################
#	Main program for the Naive Bayes coursework
#
#	Author: David Braude
#	Started: 07/02/2011
#	Last Modified: 08/02/2011
#
#	This should be modified to run your script
#
#	it is invoked with the following command:
#		python main.py training.csv test.csv
#
#############################################################

import WeatherData as wd
import Evaluation
import sys
import string
from math import *

####################### Main Program #########################

# Check it has 2 .csv files as the input
if len(sys.argv) != 3:
    sys.exit("Incorrect amount of arugements") 

trainFileName = string.split(sys.argv[1], '.')
testFileName = string.split(sys.argv[2], '.')

if (trainFileName[len(trainFileName) -1] != "csv") or (testFileName[len(testFileName) - 1] != "csv"):
    sys.exit("Incorrect file type");


# Handles input
trainingData = wd.WeatherData(sys.argv[1])
testingData = wd.WeatherData(sys.argv[2])


### your training goes here
snow = []
none = []
rain = []
fog = []
snowcount = 0
raincount = 0
nonecount = 0
fogcount = 0

for day in trainingData.getMeasurements():
	templist = []
	templist.append(day.tempMean)
	templist.append(day.tempMax)
	templist.append(day.tempMin)
	templist.append(day.dewPoint)
	templist.append(day.humidMean)
	templist.append(day.humidMax)
	templist.append(day.humidMin)
	templist.append(day.pressure)
	templist.append(day.meanWindSpeed)
	templist.append(day.maxWindSpeed)
	templist.append(day.maxGustSpeed)

	
	if(day.condition=="Fog"):
		fog.append(templist)
	if(day.condition=="Snow"):
		snow.append(templist)
	if(day.condition=="None"):
		none.append(templist)
	if(day.condition=="Rain"):
		rain.append(templist)
fogMeans = []
snowMeans = []
rainMeans = []
noneMeans = []
fogVars = []
snowVars = []
rainVars = []
noneVars = []

count = 0

while(count<11):
	fognum = 0
	snownum = 0
	nonenum = 0
	rainnum = 0
	fogvar = 0
	snowvar = 0
	nonevar = 0
	rainvar = 0

	i = 0
	while(i<len(fog)):
		fognum += fog[i][count]
		fogvar += (fog[i][count]*fog[i][count])
		i+=1
	fogmean = float(fognum)/float(len(fog))
	fogvariance = float(fogvar - (fognum*fogmean))/float((len(fog)-1))
	fogMeans.append(fogmean)
	fogVars.append(fogvariance)
	
	i = 0
	while(i<len(snow)):
		snownum += snow[i][count]
		snowvar += (snow[i][count]*snow[i][count])
		i+=1
	snowmean = float(snownum)/float(len(snow))
	snowvariance = float(snowvar - (snownum*snowmean))/float((len(snow)-1))
	snowMeans.append(snowmean)
	snowVars.append(snowvariance)

	i = 0
	while(i<len(rain)):
		rainnum += rain[i][count]
		rainvar += (rain[i][count]*rain[i][count])
		i+=1
	rainmean = float(rainnum)/float(len(rain))
	rainvariance = float(rainvar - (rainnum*rainmean))/float((len(rain)-1))
	rainMeans.append(rainmean)
	rainVars.append(rainvariance)

	i = 0
	while(i<len(none)):
		nonenum += none[i][count]
		nonevar += (none[i][count]*none[i][count])
		i+=1
	nonemean = float(nonenum)/float(len(none))
	nonevariance = float(nonevar - (nonenum*nonemean))/float((len(none)-1))
	noneMeans.append(nonemean)
	noneVars.append(nonevariance)
	
	count += 1		


def condprob(value,variance, mean):
	exponent = 0	
	constant = 0
		
	if variance == 0:
		if value == mean:
			return 1
		else:
			return 0.5
	exponent = -((float(value- mean))**2)/(2*variance)
	constant = float(1)/sqrt((2*pi*variance))
	probabilty = constant * exp(exponent)

	return probabilty	



### your prediction generation goes here, prediction is a list of strings
predictions = []
####################
# this is dummy code to generate predictions delete it in your final version
for day in trainingData.getMeasurements():

	meantemp = 0
	maxtemp = 0
	mintemp = 0
	dewpoint = 0
	meanhumid = 0
	maxhumid = 0
	minhumid = 0
	pressure = 0
	meanwind = 0
	maxwind = 0
	maxgust = 0
	visibility = 0

	x = 0
	while(x<11):
		if(x==0):	
			meantemp = condprob(day.tempMean, fogVars[x], fogMeans[x])
		elif(x==1):
			maxtemp = condprob(day.tempMax, fogVars[x], fogMeans[x])
		elif(x==2):
			mintemp = condprob(day.tempMin, fogVars[x], fogMeans[x])
		elif(x==3):
			dewpoint = condprob(day.dewPoint, fogVars[x], fogMeans[x])
		elif(x==4):
			meanhumid = condprob(day.humidMean, fogVars[x], fogMeans[x])
		elif(x==5):
			maxhumid = condprob(day.humidMax, fogVars[x], fogMeans[x])
		elif(x==6):
			minhumid = condprob(day.humidMin, fogVars[x], fogMeans[x])
		elif(x==7):
			pressure = condprob(day.pressure, fogVars[x], fogMeans[x])
		elif(x==8):
			meanwind = condprob(day.meanWindSpeed, fogVars[x], fogMeans[x])
		elif(x==9):
			maxwind = condprob(day.maxWindSpeed, fogVars[x], fogMeans[x])
		elif(x==10):
			maxgust = condprob(day.maxGustSpeed, fogVars[x], fogMeans[x])
		elif(x==11):
			visibility = condprob(day.visibility, fogVars[x], fogMeans[x])
		x+=1

	fogprobability = .25*meantemp*maxtemp*mintemp*dewpoint*meanhumid*maxhumid*minhumid*pressure*meanwind*maxwind*maxgust
	
	x = 0
	while(x<11):
		if(x==0):	
			meantemp = condprob(day.tempMean, snowVars[x], snowMeans[x])
		elif(x==1):
			maxtemp = condprob(day.tempMax, snowVars[x], snowMeans[x])
		elif(x==2):
			mintemp = condprob(day.tempMin, snowVars[x], snowMeans[x])
		elif(x==3):
			dewpoint = condprob(day.dewPoint, snowVars[x], snowMeans[x])
		elif(x==4):
			meanhumid = condprob(day.humidMean, snowVars[x], snowMeans[x])
		elif(x==5):
			maxhumid = condprob(day.humidMax, snowVars[x], snowMeans[x])
		elif(x==6):
			minhumid = condprob(day.humidMin, snowVars[x], snowMeans[x])
		elif(x==7):
			pressure = condprob(day.pressure, snowVars[x], snowMeans[x])
		elif(x==8):
			meanwind = condprob(day.meanWindSpeed, snowVars[x], snowMeans[x])
		elif(x==9):
			maxwind = condprob(day.maxWindSpeed, snowVars[x], snowMeans[x])
		elif(x==10):
			maxgust = condprob(day.maxGustSpeed, snowVars[x], snowMeans[x])
		elif(x==11):
			visibility = condprob(day.visibility, snowVars[x], snowMeans[x])
		x+=1

	snowprobability = .25*meantemp*maxtemp*mintemp*dewpoint*meanhumid*maxhumid*minhumid*pressure*meanwind*maxwind*maxgust

	x = 0
	while(x<11):
		if(x==0):	
			meantemp = condprob(day.tempMean, rainVars[x], rainMeans[x])
		elif(x==1):
			maxtemp = condprob(day.tempMax, rainVars[x], rainMeans[x])
		elif(x==2):
			mintemp = condprob(day.tempMin, rainVars[x], rainMeans[x])
		elif(x==3):
			dewpoint = condprob(day.dewPoint, rainVars[x], rainMeans[x])
		elif(x==4):
			meanhumid = condprob(day.humidMean, rainVars[x], rainMeans[x])
		elif(x==5):
			maxhumid = condprob(day.humidMax, rainVars[x], rainMeans[x])
		elif(x==6):
			minhumid = condprob(day.humidMin, rainVars[x], rainMeans[x])
		elif(x==7):
			pressure = condprob(day.pressure, rainVars[x], rainMeans[x])
		elif(x==8):
			meanwind = condprob(day.meanWindSpeed, rainVars[x], rainMeans[x])
		elif(x==9):
			maxwind = condprob(day.maxWindSpeed, rainVars[x], rainMeans[x])
		elif(x==10):
			maxgust = condprob(day.maxGustSpeed, rainVars[x], rainMeans[x])
		elif(x==11):
			visibility = condprob(day.visibility, rainVars[x], rainMeans[x])
		x+=1

	rainprobability = .25*meantemp*maxtemp*mintemp*dewpoint*meanhumid*maxhumid*minhumid*pressure*meanwind*maxwind*maxgust

	x = 0
	while(x<11):
		if(x==0):	
			meantemp = condprob(day.tempMean, noneVars[x], noneMeans[x])
		elif(x==1):
			maxtemp = condprob(day.tempMax, noneVars[x], noneMeans[x])
		elif(x==2):
			mintemp = condprob(day.tempMin, noneVars[x], noneMeans[x])
		elif(x==3):
			dewpoint = condprob(day.dewPoint, noneVars[x], noneMeans[x])
		elif(x==4):
			meanhumid = condprob(day.humidMean, noneVars[x], noneMeans[x])
		elif(x==5):
			maxhumid = condprob(day.humidMax, noneVars[x], noneMeans[x])
		elif(x==6):
			minhumid = condprob(day.humidMin, noneVars[x], noneMeans[x])
		elif(x==7):
			pressure = condprob(day.pressure, noneVars[x], noneMeans[x])
		elif(x==8):
			meanwind = condprob(day.meanWindSpeed, noneVars[x], noneMeans[x])
		elif(x==9):
			maxwind = condprob(day.maxWindSpeed, noneVars[x], noneMeans[x])
		elif(x==10):
			maxgust = condprob(day.maxGustSpeed, noneVars[x], noneMeans[x])
		elif(x==11):
			visibility = condprob(day.visibility, noneVars[x], noneMeans[x])
		x+=1

	noneprobability = .25*meantemp*maxtemp*mintemp*dewpoint*meanhumid*maxhumid*minhumid*pressure*meanwind*maxwind*maxgust

	if(max(noneprobability, rainprobability, snowprobability, fogprobability)==noneprobability):
		predictions.append("None")
	elif(max(noneprobability, rainprobability, snowprobability, fogprobability)==rainprobability):
		predictions.append("Rain")
	elif(max(noneprobability, rainprobability, snowprobability, fogprobability)==snowprobability):
		predictions.append("Snow")
	elif(max(noneprobability, rainprobability, snowprobability, fogprobability)==fogprobability):
		predictions.append("Fog")
print predictions

		
"""
for day in testingData.getMeasurements():
	predictions.append(day.condition)	
"""	
####################



# Output
print "\nPrediction accuracy = %f%%\n" % Evaluation.evaluate(trainingData, predictions)





