
from nltk import data, ChartParser

data.clear_cache()
G = data.load('file:mygrammar.cfg')
RDP = ChartParser(G)

def parse (s):
    return RDP.parse (s.split())

def run():
	listofsentences = ['This is the only substantial dispute', 'The Government will not assail you', 'The whole world is at peace', 'We made freedom a birthright', 'I beg your support and encouragement', 'This country believes in prosperity', 'We are reunited', 'This is the heart of our task',"This country reunited the Government"]
	
	for x in listofsentences:
		if(parse (x)):
			print "The sentence - \"" + listofsentences[listofsentences.index(x)] + "\" - successfully parsed!"
		else:
			print "The sentence - \"" + listofsentences[listofsentences.index(x)] + "\" - failed..."

#Sentences selected (from inaugural corpus) and the pos_tag version of it (ignoring the convention of periods and other end statement words)
#This is the heart of our task. 
#[('This', 'DT'), ('is', 'VBZ'), ('the', 'DT'), ('heart', 'NN'), ('of', 'IN'), ('our', 'PRP$'), ('task', 'NN'), ('.', '.')]

# The Government will not assail you. 
#[('The', 'DT'), ('Government', 'NNP'), ('will', 'MD'), ('not', 'RB'), ('assail', 'VB'), ('you', 'PRP'), ('.', '.')]

# The whole world is at peace. 
#[[('The', 'DT'), ('whole', 'JJ'), ('world', 'NN'), ('is', 'VBZ'), ('at', 'IN'), ('peace', 'NN'), ('.', '.')]]

# We made freedom a birthright. 
#[[('We', 'PRP'), ('made', 'VBD'), ('freedom', 'NN'), ('a', 'DT'), ('birthright', 'NN'), ('.', '.')]]

# I beg your support and encouragement. 
#[[('I', 'PRP'), ('beg', 'VBP'), ('your', 'PRP$'), ('support', 'NN'), ('and', 'CC'), ('encouragement', 'NN'), ('.', '.')]]

# This country believes in prosperity. 
#[('This', 'DT'), ('country', 'NN'), ('believes', 'VBZ'), ('in', 'IN'), ('prosperity', 'NN'), ('.', '.')]

# We are reunited.
#[('We', 'PRP'), ('are', 'VBP'), ('reunited', 'VBN'), ('.', '.')]

# This is the only substantial dispute.
 #[('This', 'DT'), ('is', 'VBZ'), ('the', 'DT'), ('only', 'JJ'), ('substantial', 'JJ'), ('dispute', 'NN'), ('.', '.')]

#The final sentence is an example of what happens when the parsing fails, not taken from the corpus
