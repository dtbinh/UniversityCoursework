############################################################
#	Compares a set of weather data to a prediction
#
#	Author: David Braude
#	Started: 07/02/2011
#	Last Modified: 07/02/2011
#
#	Returns as a percentage or error
#
#############################################################

import WeatherData as wd

def evaluate(weatherData, predictions):

        if len(predictions) != len(weatherData.getMeasurements()):
              raise LenError('Length of prediction array does not match the amount of days in weather data')

        _right = 0
  
        _days = weatherData.getMeasurements()
     
        for i in range(len(_days)):
            if _days[i].condition == predictions[i]: _right += 1
	
	return float(_right)/len(predictions)*100



if __name__ == "__main__": 
   
    data = wd.WeatherData('training.csv')

    predictions = []
    for d in data.getMeasurements():
        predictions.append(d.condition)
    x = evaluate(data, predictions)
    print str(x) + '\n'
    
    predictions = []
    for d in data.getMeasurements():
        predictions.append("None")
    x = evaluate(data, predictions)
    print str(x) + '\n'
