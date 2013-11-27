from Lab1Support import *
import re

def BenfordLaw(filename):
	text = readFile(filename)
	tokens = re.split('[\\s]+', text)
	saveToFile(tokens, 'tokens'+filename+'.txt')
	numbers = [ t for t in tokens if re.match('[0-9]+[\\.\\,0-9]*$', t)]
	saveToFile(numbers, 'numbers'+filename+'.txt')
	digits = [n[0:1] for n in numbers]
	saveToFile(digits, 'digits'+filename+'.txt')
	percentages = calculatePercentages(digits)
	saveToFile(percentages, 'percentages'+filename+'.txt')
	plot(percentages, 1)
