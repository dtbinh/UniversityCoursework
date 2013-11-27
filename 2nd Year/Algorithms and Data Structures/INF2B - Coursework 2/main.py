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

#s0943941 

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
#Creates lists for the weather events
snow = []
none = []
rain = []
fog = []

#Loops through each day and assigns a list of the days attributes to the weather events list
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
	templist.append(day.visibility)	
	
	if(day.condition=="Fog"):
		fog.append(templist)
	if(day.condition=="Snow"):
		snow.append(templist)
	if(day.condition=="None"):
		none.append(templist)
	if(day.condition=="Rain"):
		rain.append(templist)

#Creates a list for the mean of each weather event
fogMeans = []
snowMeans = []
rainMeans = []
noneMeans = []

#Creates a list for the variance of each weather event
fogVars = []
snowVars = []
rainVars = []
noneVars = []

#Calculates the prior probabilities 
priorfog = float(len(fog))/float(len(trainingData.getMeasurements()))
priorsnow = float(len(snow))/float(len(trainingData.getMeasurements()))
priorrain = float(len(rain))/float(len(trainingData.getMeasurements()))
priornone = float(len(none))/float(len(trainingData.getMeasurements()))

#Set the counter
count = 0

#Iterate over each of the columns
while(count<12):
	#Create variables to hold the sum of columns
	fognum = 0
	snownum = 0
	nonenum = 0
	rainnum = 0

	#Create variables to hold the sum of the square of columns
	fogvar = 0
	snowvar = 0
	nonevar = 0
	rainvar = 0

	#Sum both the ith element of each array of fog and its square
	i = 0
	while(i<len(fog)):
		fognum += fog[i][count]
		fogvar += (fog[i][count]*fog[i][count])
		i+=1

	#Calculate the mean and variance for fog, ensuring floats to prevent loss of accuracy
	fogmean = float(fognum)/float(len(fog))
	fogvariance = float(fogvar - (fognum*fogmean))/float((len(fog)-1))

	#Append means into a list of means and variances into a list of variances
	fogMeans.append(fogmean)
	fogVars.append(fogvariance)
	
	#Sum both the ith element of each array of snow and its square
	i = 0
	while(i<len(snow)):
		snownum += snow[i][count]
		snowvar += (snow[i][count]*snow[i][count])
		i+=1

	#Calculate the mean and variance for snow, and append into list
	snowmean = float(snownum)/float(len(snow))
	snowvariance = float(snowvar - (snownum*snowmean))/float((len(snow)-1))
	snowMeans.append(snowmean)
	snowVars.append(snowvariance)

	#Sum both the ith element of each array of rain and its square
	i = 0
	while(i<len(rain)):
		rainnum += rain[i][count]
		rainvar += (rain[i][count]*rain[i][count])
		i+=1

	#Calculate the mean and variance for rain, and append into list
	rainmean = float(rainnum)/float(len(rain))
	rainvariance = float(rainvar - (rainnum*rainmean))/float((len(rain)-1))
	rainMeans.append(rainmean)
	rainVars.append(rainvariance)

	#Sum both the ith element of each array of none and its square
	i = 0
	while(i<len(none)):
		nonenum += none[i][count]
		nonevar += (none[i][count]*none[i][count])
		i+=1

	#Calculate the mean and variance for none, and append into list
	nonemean = float(nonenum)/float(len(none))
	nonevariance = float(nonevar - (nonenum*nonemean))/float((len(none)-1))
	noneMeans.append(nonemean)
	noneVars.append(nonevariance)
	
	#Move to the next column
	count += 1		
####End of training######

#This function calculates the probability, assuming a Gaussian distribution.
def condprob(value,variance, mean):
	#Error checking for no change in variance
	if variance == 0:
		return 1 	#1, when multiplied, doesn't change the probability.
	#Formula for Gaussian distribution, given a point
	probabilty = float(1)/sqrt((2*pi*variance)) * exp(-((float(value- mean))**2)/(2*variance))

	return probabilty	

#List of predictions
predictions = []

###Testing goes here#####
#Iterate over each day and get a prediction
for day in testingData.getMeasurements():
	#Calculate the probability of fog from each column given the variance and means (selected via their position in the list)
	meantemp = condprob(day.tempMean, fogVars[0], fogMeans[0])
	maxtemp = condprob(day.tempMax, fogVars[1], fogMeans[1])
	mintemp = condprob(day.tempMin, fogVars[2], fogMeans[2])
	dewpoint = condprob(day.dewPoint, fogVars[3], fogMeans[3])
	meanhumid = condprob(day.humidMean, fogVars[4], fogMeans[4])
	maxhumid = condprob(day.humidMax, fogVars[5], fogMeans[5])
	minhumid = condprob(day.humidMin, fogVars[6], fogMeans[6])
	pressure = condprob(day.pressure, fogVars[7], fogMeans[7])
	meanwind = condprob(day.meanWindSpeed, fogVars[8], fogMeans[8])
	maxwind = condprob(day.maxWindSpeed, fogVars[9], fogMeans[9])
	maxgust = condprob(day.maxGustSpeed, fogVars[10], fogMeans[10])
	visibility = condprob(day.visibility, fogVars[11], fogMeans[11])

	#Calculate the entire probability for a fog day
	fogprobability = priorfog*meantemp*maxtemp*mintemp*dewpoint*meanhumid*maxhumid*minhumid*pressure*meanwind*maxwind*maxgust*visibility
	
	#Calculate the probability of snow from each column given the variance and means (selected via their position in the list)
	meantemp = condprob(day.tempMean, snowVars[0], snowMeans[0])
	maxtemp = condprob(day.tempMax, snowVars[1], snowMeans[1])
	mintemp = condprob(day.tempMin, snowVars[2], snowMeans[2])
	dewpoint = condprob(day.dewPoint, snowVars[3], snowMeans[3])
	meanhumid = condprob(day.humidMean, snowVars[4], snowMeans[4])
	maxhumid = condprob(day.humidMax, snowVars[5], snowMeans[5])
	minhumid = condprob(day.humidMin, snowVars[6], snowMeans[6])
	pressure = condprob(day.pressure, snowVars[7], snowMeans[7])
	meanwind = condprob(day.meanWindSpeed, snowVars[8], snowMeans[8])
	maxwind = condprob(day.maxWindSpeed, snowVars[9], snowMeans[9])
	maxgust = condprob(day.maxGustSpeed, snowVars[10], snowMeans[10])
	visibility = condprob(day.visibility, snowVars[11], snowMeans[11])

	#Calculate the entire probability for a snow day
	snowprobability = priorsnow*meantemp*maxtemp*mintemp*dewpoint*meanhumid*maxhumid*minhumid*pressure*meanwind*maxwind*maxgust*visibility

	#Calculate the probability of rain from each column given the variance and means (selected via their position in the list)
	meantemp = condprob(day.tempMean, rainVars[0], rainMeans[0])
	maxtemp = condprob(day.tempMax, rainVars[1], rainMeans[1])
	mintemp = condprob(day.tempMin, rainVars[2], rainMeans[2])
	dewpoint = condprob(day.dewPoint, rainVars[3], rainMeans[3])
	meanhumid = condprob(day.humidMean, rainVars[4], rainMeans[4])
	maxhumid = condprob(day.humidMax, rainVars[5], rainMeans[5])
	minhumid = condprob(day.humidMin, rainVars[6], rainMeans[6])
	pressure = condprob(day.pressure, rainVars[7], rainMeans[7])
	meanwind = condprob(day.meanWindSpeed, rainVars[8], rainMeans[8])
	maxwind = condprob(day.maxWindSpeed, rainVars[9], rainMeans[9])
	maxgust = condprob(day.maxGustSpeed, rainVars[10], rainMeans[10])
	visibility = condprob(day.visibility, rainVars[11], rainMeans[11])

	#Calculate the entire probability for a rain day
	rainprobability = priorrain*meantemp*maxtemp*mintemp*dewpoint*meanhumid*maxhumid*minhumid*pressure*meanwind*maxwind*maxgust*visibility

	#Calculate the probability of none from each column given the variance and means (selected via their position in the list)
	meantemp = condprob(day.tempMean, noneVars[0], noneMeans[0])
	maxtemp = condprob(day.tempMax, noneVars[1], noneMeans[1])
	mintemp = condprob(day.tempMin, noneVars[2], noneMeans[2])
	dewpoint = condprob(day.dewPoint, noneVars[3], noneMeans[3])
	meanhumid = condprob(day.humidMean, noneVars[4], noneMeans[4])
	maxhumid = condprob(day.humidMax, noneVars[5], noneMeans[5])
	minhumid = condprob(day.humidMin, noneVars[6], noneMeans[6])
	pressure = condprob(day.pressure, noneVars[7], noneMeans[7])
	meanwind = condprob(day.meanWindSpeed, noneVars[8], noneMeans[8])
	maxwind = condprob(day.maxWindSpeed, noneVars[9], noneMeans[9])
	maxgust = condprob(day.maxGustSpeed, noneVars[10], noneMeans[10])
	visibility = condprob(day.visibility, noneVars[11], noneMeans[11])

	#Calculate the entire probability for a none day
	noneprobability = priornone*meantemp*maxtemp*mintemp*dewpoint*meanhumid*maxhumid*minhumid*pressure*meanwind*maxwind*maxgust*visibility
	#Take the max value of the probabilities - the highest one is the prediction

	if(max(noneprobability, rainprobability, snowprobability, fogprobability)==noneprobability):
		predictions.append("None")
	elif(max(noneprobability, rainprobability, snowprobability, fogprobability)==rainprobability):
		predictions.append("Rain")
	elif(max(noneprobability, rainprobability, snowprobability, fogprobability)==snowprobability):
		predictions.append("Snow")
	elif(max(noneprobability, rainprobability, snowprobability, fogprobability)==fogprobability):
		predictions.append("Fog")
	


###End of testing#######

# Output
print "\nPrediction accuracy = %d%%\n" % Evaluation.evaluate(testingData, predictions)

