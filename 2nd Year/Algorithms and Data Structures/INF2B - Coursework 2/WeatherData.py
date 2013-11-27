############################################################
#	Complete sample set of weather data	
#
#	Author: David Braude
#	Started: 07/02/2011
#	Last Modified: 07/02/2011
#
#	creates a list of DayWeatherMeasurements from a file on decleration
#
#############################################################

import fileinput
import string
import DayWeatherMeasurements as dwm



class WeatherData:
    def __init__(self, filename):
	self.measurements = []
        
	for _dayData in fileinput.input(filename):
            self._day = dwm.DayWeatherMeasurements()
            self._measure = string.split(_dayData, ',')
            self._day.tempMean = int(self._measure[1])
            self._day.tempMax = int(self._measure[2])
            self._day.tempMin = int(self._measure[3])
            self._day.dewPoint = int(self._measure[4])
            self._day.humidMean = int(self._measure[5])
	    self._day.humidMax = int(self._measure[6])
	    self._day.humidMin = int(self._measure[7])
	    self._day.pressure = float(self._measure[8])
	    self._day.meanWindSpeed = int(self._measure[9])
	    self._day.maxWindSpeed = int(self._measure[10])
	    self._day.maxGustSpeed = int(self._measure[11])
	    self._day.visibility = float(self._measure[12])
	    self._day.condition = self._measure[13]
	
            #strips new line character and quotation marks
	    self._day.condition = self._day.condition[1:len(self._day.condition) - 2] 
	
            self.measurements.append(self._day)

    def getMeasurements(self):
        return self.measurements



def unitTest(filename):
	month = WeatherData(filename)
	days = month.getMeasurements()
	for d in days:
            d.printDay()    

if __name__ == "__main__":
	unitTest('training.csv')
