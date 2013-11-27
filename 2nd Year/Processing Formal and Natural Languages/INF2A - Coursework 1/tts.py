from nltk.corpus import inaugural, stopwords
from nltk import FreqDist, ConditionalFreqDist
import re, string



#Section 2, Task 1: Produce a list of overlapping lists of inaugural addresses

def inaug20():
	
	#Variables
	myinaug=inaugural.fileids()
	myaug20=[]
	
	#Function
	for x in range(len(myinaug)-4):					#Goes through all ids, -4 for Obama
		(myaug20.append(myinaug[x:(x+5)]))			#Create list from one president to five more

	#Return
	return myaug20
	
#Section 2, Task 2: Frequency distribution of words, excluding fn words and punct
def word_fdist(inaug_list):
	
	fixedspeech=map(str.lower, inaugural.words(inaug_list))		#Applies lower to every element of the list
	fixedspeech=filter(checkwords, fixedspeech)			#Filters false words using helper function checkwords	
	fdist = FreqDist(fixedspeech)					#Assigns it to a frequency disposition
	
	return fdist							#Returns it

def checkwords(args):
	check = True							#Default
	notenglish = stopwords.words("english")				#All the stopwords
	
	for x in range(0,len(notenglish)-1, 1):				#Encodes them
		notenglish[x].encode()
	nottotal = []

	for x in notenglish:						#Take the stopwords and put them in a list
		nottotal.append(x)

	for x in string.punctuation:					#Append all the punctuation 
		nottotal.append(x)

	nottotal.append('...')						#Include these words as well
	nottotal.append('--')							

	for x in nottotal:						#Check to see if args is not allowed
		if x==args:
			check=False					#Not allowed
	
	return check


#Section 2, Task 2: Also compute and print the 20 most common words in each
#of the overlapping 20-year periods. Add script for print_most_common here:

def prints_most_common():
	
	hold = inaug20()						#Takes a list of five incremented presidents

	for x in range(len(hold)):				
		print word_fdist(inaug20()[x]).keys() [:20]		#Prints the frequency distribution of the 20 most frequent words
		print '\n'						#Newline for clarity
	
#Section 2, Task 3: Frequency distribution of sentence lengths,
#excluding stopwords and punctuation. Add script for set_length_fdist here:

def elimpunct(args):

	notwords = []							#Variables
	check = True

	for x in string.punctuation:					#Create list of punctuation forbidden
		notwords.append(x)

	notwords.append('--')						#Eliminate these words
	notwords.append('...')					
	notwords.remove('.')						#Keep these words
	notwords.remove('!')
	notwords.remove('?')

	for x in notwords:						#Eliminates the punctuation
		if x==args:
			check=False

	return check							

def sent_length_fdist(inaug_list):

	fixedspeech=filter(elimpunct, inaugural.words(inaug_list))	#Elimintes the punctuation
	count = 0							#Counter to run alongside for loop
	listcount = []							#List of sentences lengths

	for x in range(len(fixedspeech)):

		if fixedspeech[x]=='.':				
			listcount.append(count-1)			#Appends the counter-1 (for the period) to the list for the sentence length
			count=0						#Resets counter for next sentence

		if fixedspeech[x]=='!':
			listcount.append(count-1)			#Appends the counter-1 (for the exclamation mark) to the list for the sentence length
			count=0		

		if fixedspeech[x]=='?':					#Appends the counter-1 (for the question mark) to the list for the sentence length
			listcount.append(count-1)
			count=0
		count+=1

	fdlist = FreqDist( listcount)					#Makes a distribution

	return fdlist
	


#Section 2, Task 3: Also compute and print the average sentence lengths in each of
#the overlapping 20-year periods. Add script for print_average_lengths here:
#def prints_average_length():
def print_average_lengths():
	
	hold = inaug20()					

	for x in range(len(hold)):					#Displays the average, from the average function
		print average(sent_length_fdist(hold[x]))



def average(args):							#Takes the average of the sentence lengths

	return float(sum([k*args[k] for k in args.keys()]))/float(sum([args[k] for k in args.keys()]))
	 
 

#Section 2, Task 4: Conditional freq distribution of words following 'I'/'my' or
#preceding 'me', plus printing samples that occur >1 for each pro-period pair.
#Add your script for build_cond_fdist here:
def build_cond_fdist():

	cfdist = ConditionalFreqDist()					#Create conditionalFrequency

	for inaug_list in inaug20():					#Go through inaug_list
		period = int(inaug_list[0][0:4])			#Set the period

		for fileid in inaug_list:    				#For all the words in fileid
			words = inaugural.words(fileid)			

			for i in range(len(words)):			#Check all of the words
				pronoun = words[i]			

				if pronoun in ['I', 'my']:		#Print the next word (after I,me)
					cfdist[(pronoun, period)].inc(words[i+1])

				elif pronoun == 'me':                	#Print the previous word (before me)
					cfdist[(pronoun, period)].inc(words[i-1])    

	return cfdist


#Section 2, Task 4: Also compute and print, for eachhttp://start.fedoraproject.org/ pronoun and each 20-year
#period, the list of words accompanying the pronoun more than once in the
#addresses within the period. Add your script for print_Imyme_words here:

def print_Imyme_words(CFD):

	for (pronoun, period) in CFD.conditions():			#List of CFD

		for word in CFD[(pronoun, period)]:			#All words in CFD

			if CFD[(pronoun, period)][word] > 1:		#If the occurence is greater than 1
	
				print period, pronoun, word		#Print it


#End of file
