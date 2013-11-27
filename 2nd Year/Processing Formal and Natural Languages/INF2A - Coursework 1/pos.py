
from nltk import pos_tag
import re
from nonVBG import *


#Section 3, Task 1: Use pos_tag to tag each word in every sentence in a list.

def tag_sentences (sent_list):
	
	out = []								#List for output
	
	for i in sent_list:							#Checks every element of list
		out.append(pos_tag(i))						#Applies pos_tag to each

	return out								#Returns list of pos_tagged elements

#Section3, Task 1: Given a list of tagged sentences, build and return
#a dictionary whose keys are words ending in 'ing', where the value
#associated with a w is a *list* of all pos-tags assigned to w anywhere
#in the given sentences.

def build_ing_dict(tag_sent_list):

	dictionary = {} 							#Creates dictionary

	for x in tag_sent_list:							#Goes through tagged list

		for y,t in x:							#Selects word tuple (word, pos)
			lcase = y.lower()					#Makes everything lowercase
			hold = []						#temporary holder

			if(re.search("ing$", lcase)):				#Looks for -ing words at the end of lcase

				hold.append(t)					#Places -ing in hold

				if(dictionary.has_key(lcase)):			#Checks for multiple tags

					hold.append(dictionary[lcase][0])	#Stores multiple ones in one list

				hold = list(set(hold))				#Gets rid of duplicates in list
				dictionary[lcase] = hold			#Stores as dictionary

	return dictionary							#Returns dictionary

#Section 3, Task 2: Re-tag 'ing' words that follow a preposition
#and aren't in nonVBG. Add script for retag_sentence here:
def retag_sentence (tag_sent):
	
	count = 0								#Creates counter to run alongside forloop

	for x,t in tag_sent:							#For every tuple 

		if(len(tag_sent)-1!=count):					#Ensures for loop doesn't extend index

			(nextword,temp) = tag_sent[count+1]			#Creates variable for the next word

			if(t=='IN'):						#Sees if word if a preposition

				if(re.search("ing$",nextword)):			#Checks if nextword ends in -ing

					for y in nonVBG:			#For all the elements in nonVBG,

						if(nextword!=y):		#Make sure nextword isn't one
							tag_sent[(count+1)] = (nextword,'VBG')  
										
										#Retags the successfully identified word

			count+=1						#Increments counter

	return tag_sent								#Returns updated word
