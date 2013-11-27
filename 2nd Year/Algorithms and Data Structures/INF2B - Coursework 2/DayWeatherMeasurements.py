############################################################
#	Holds a days worth of weather data
#
#	Author: David Braude
#	Started: 07/02/2011
#	Last Modified: 07/02/2011
#
#	has a print function
#
#############################################################



class DayWeatherMeasurements:
    def __init__(self):
	self.tempMean = 0
	self.tempMax = 0
	self.tempMin = 0
	self.dewPoint = 0
        self.humidMean = 0
	self.humidMax = 0
	self.humidMin = 0
	self.pressure = 0
	self.meanWindSpeed = 0
	self.maxWindSpeed = 0
	self.maxGustSpeed = 0
	self.visibility = 0
	self.condition = 'None'

    def printDay(self):
         print "Temp: Mean = %d, Min = %d, Max = %d   Dew Point = %d, " % (self.tempMean, self.tempMax, self.tempMin, self.dewPoint)
         print "Humidity: Mean = %d, Min = %d, Max = %d    Pressure = %5.2f" % (self.humidMean, self.humidMax, self.humidMin, self.pressure)
         print "Wind Speed: Mean = %d, Max = %d, Gust = %d    Visibility = %5.2f" % (self.meanWindSpeed, self.maxWindSpeed, self.maxGustSpeed, self.visibility)
         print "Condition = %s" % (self.condition)

if __name__ == "__main__":
    day = DayWeatherMeasurements()
	
    day.tempMean = 0
    day.tempMax = 1
    day.tempMin = -2
    day.dewPoint = 3
    day.humidMean = 4
    day.humidMax = 5
    day.humidMin = 6
    day.pressure = 7000.6
    day.meanWindSpeed = 8
    day.maxWindSpeed = 9
    day.maxGustSpeed = 10
    day.visibility = 11.1

    day.printDay()	


