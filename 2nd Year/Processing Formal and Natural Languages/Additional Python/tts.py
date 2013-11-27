from nltk.corpus import inaugural, stopwords
from nltk import FreqDist, ConditionalFreqDist
import re, string



#Section 2, Task 1: Produce a list of overlapping lists of inaugural addresses
def inaug():
	myinaug=inaugural.fileids()
	myaug20=[]
	for x in range(len(myinaug)-4):
		(myaug20.append(myinaug[x:(x+5)]))
	return myaug20
	
def checkwords(args):
	check = True
	notenglish = stopwords.words("english")
	for x in range(0,len(notenglish)-1, 1):
		notenglish[x].encode()
	nottotal = []
	for x in notenglish:
		nottotal.append(x)
	for x in string.punctuation:
		nottotal.append(x)
	nottotal.append('...')
	nottotal.append('--')
	for x in nottotal:
		if x==args:
			check=False
	
	return check

#Section 2, Task 2: Frequency distribution of words, excluding fn words and punct
def word_fdist(inaug_list):
	fixedspeech=map(str.lower, inaugural.words(inaug_list))
	fixedspeech=filter(checkwords, fixedspeech)
	fdist = FreqDist(fixedspeech)	
	return fdist


#Section 2, Task 2: Also compute and print the 20 most common words in each
#of the overlapping 20-year periods. Add script for print_most_common here:
def prints_most_common():
	hold = inaug()
	for x in range(len(hold)):
		print word_fdist(inaug()[x]).keys() [:20]
		print '\n'


	
#Section 2, Task 3: Frequency distribution of sentence lengths,
#excluding stopwords and punctuation. Add script for set_length_fdist here:
def elimpunct(args):
	notwords = []
	check = True
	for x in string.punctuation:
		notwords.append(x)
	notwords.append('--')
	notwords.append('...')
	notwords.remove('.')
	notwords.remove('!')
	notwords.remove('?')
	for x in notwords:
		if x==args:
			check=False
	return check

def sent_length_fdist(inaug_list):
	fixedspeech=filter(elimpunct, inaugural.words(inaug_list))
	count = 0
	listcount = []
	for x in range(len(fixedspeech)):
		if fixedspeech[x]=='.':
			listcount.append(count-1)
			count=0
		if fixedspeech[x]=='!':
			listcount.append(count-1)
			count=0
		if fixedspeech[x]=='?':
			listcount.append(count-1)
			count=0
		count+=1
	fdlist = FreqDist( listcount)
	return fdlist
	


#Section 2, Task 3: Also compute and print the average sentence lengths in each of
#the overlapping 20-year periods. Add script for print_average_lengths here:
#def prints_average_length():
def print_average_lengths():
	hold = inaug()
	out = []
	for x in range(len(hold)):
		print average(sent_length_fdist(hold[x]))

def average(args):
	aver = [float(x) for x in args]
	return sum(aver)/len(args)	
 

#Section 2, Task 4: Conditional freq distribution of words following 'I'/'my' or
#preceding 'me', plus printing samples that occur >1 for each pro-period pair.
#Add your script for build_cond_fdist here:


#Section 2, Task 4: Also compute and print, for each pronoun and each 20-year
#period, the list of words accompanying the pronoun more than once in the
#addresses within the period. Add your script for print_Imyme_words here:


#End of file
