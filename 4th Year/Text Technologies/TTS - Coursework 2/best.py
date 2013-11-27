import re
import math
import time
from numpy import *
from operator import itemgetter

#Comment

#Preprocess - figure out average length with and without -, if that improves stats
#Assumptions - corpus is well formed and containing enough information
#            - that the speed of the program isn't required - I create another document to deal with queries and documents

#add hyphens 




def main():
    queryRead = open('qrys.txt', 'r')
    docsRead = open('docs.txt', 'r')

    corpus = readFile('corpus.txt')
    tokens = re.split('[\\s]+', corpus)
    wordBase = set(tokens)
    dictSound = trimDictionary(buildCategories(wordBase), 1)
    dictFirstThree = trimDictionary(buildFirstThree(wordBase), 2)

    numofqueries = 0
    tfquery = 0
    tfdoc = 0
    doclength = 0
    k = 2

    output = open('best.top', 'w')
    numdocavg = findAvgLenAndTotalNumber()
    avgDocLen = numdocavg[0]
    totaldocs = numdocavg[1]

    wordamountlookup = findWordAmountDictionary()
    
    docline = docsRead.readline()
    while docline:
        splitdoc = re.split(" ", re.sub("-", " ", (re.sub("\\n", "", docline))))
        docnum = splitdoc[0]
        doc = splitdoc[2:]

        queryRead = open('qrys.txt', 'r')
        queryline = queryRead.readline()
        while queryline:
            splitquery = re.split(" ", re.sub("-", " ", (re.sub("\\n", "", queryline))))
            querynum = splitquery[0]
            query = splitquery[2:]
            ranking = 0.0
            
            for word in query:
                doc = compareQueryAgainstDocWord(word, wordBase, doc, dictSound, dictFirstThree, wordamountlookup)
                doclength = len(doc)
                tfdoc = doc.count(word)
                tfquery = query.count(word)
                numcontaining = wordamountlookup[word]
                ranking += calcRank(tfdoc, tfquery, k, doclength, avgDocLen, totaldocs, numcontaining)                   

            if(ranking != 0):
                printout = str(querynum) + " 0 "+ str(docnum) + " 0 "+ str(ranking) + " 0 \n"
                output.write(printout)

            queryline = queryRead.readline()

        docline = docsRead.readline()
    output.close()
    queryRead.close()
    docsRead.close()

def compareQueryAgainstDocWord(queryWord, wordBase, doc, dictSound, dictFirstThree, wordamountlookup):
    for word in doc:
        if word not in wordBase:
            soundQuery = SoundEx(queryWord)
            soundDoc = SoundEx(word)
            dictRatings = dict()
            if soundQuery == soundDoc and soundDoc in dictSound:
                soundExCodeMatch = dictSound[soundDoc]
                for x in soundExCodeMatch:
                    dist = calculateDistance(word, x)
                    if dist < 2:
                        dictRatings.setdefault(x, []).append(dist)
            
            if word[0:3] in dictFirstThree and word[0:3] == queryWord[0:3]:
                firstThreeMatch = dictFirstThree[word[0:3]]
                for x in firstThreeMatch:
                    if x not in dictRatings:
                        dist = calculateDistance(word, x)
                        if dist < 2:
                            dictRatings.setdefault(x, []).append(dist)
              
            #if word[len(word)-3:] in dictLastThree and word[len(word)-3:] == queryWord[len(word)-3:]:
            #    lastThreeMatch = dictLastThree[word[len(word)-3:]]
            #    for x in lastThreeMatch:
            #        if x not in dictRatings:
            #            dist = calculateDistance(word, x)
            #            if dist < 2:
            #                dictRatings.setdefault(x, []).append(dist)


            sortedDist = sorted(dictRatings.items(), key=itemgetter(1))
            possiblewords = relWordDistanceFinder(sortedDist)
            if queryWord in possiblewords:
                if queryWord not in doc:
                    wordamountlookup[queryWord] += 1
                doc.remove(word)
                doc.append(queryWord)


    
    return doc

def trimDictionary(dictWork, mode):
    d = dict()
    if mode == 1:
        queryRead = open('qrys.txt', 'r')
        queryline = queryRead.readline()            
        while queryline:
            splitquery = re.split(" ", re.sub("-", " ", (re.sub("\\n", "", queryline))))
            query = splitquery[2:]
            for x in query:
                sound = SoundEx(x)
                if x not in d and sound in dictWork:
                    d[sound] = dictWork[sound]                

            queryline = queryRead.readline()

    elif mode == 2:
        queryRead = open('qrys.txt', 'r')
        queryline = queryRead.readline()            
        while queryline:
            splitquery = re.split(" ", re.sub("-", " ", (re.sub("\\n", "", queryline))))
            query = splitquery[2:]
            for x in query:
                if x not in d and x[0:3] in dictWork:
                    d[x[0:3]] = dictWork[x[0:3]]                

            queryline = queryRead.readline()
    else:
        queryRead = open('qrys.txt', 'r')
        queryline = queryRead.readline()            
        while queryline:
            splitquery = re.split(" ", re.sub("-", " ", (re.sub("\\n", "", queryline))))
            query = splitquery[2:]
            for x in query:
                if x not in d and x[len(x)-3:] in dictWork:
                    d[x[len(x)-3:]] = dictWork[x[len(x)-3:]]                

            queryline = queryRead.readline()
    return d

def findWordAmountDictionary():
    queryRead = open('qrys.txt', 'r')
    queryline = queryRead.readline()
    amount = {}
    
    while queryline:
        splitquery = re.split(" ", re.sub("-", " ", (re.sub("\\n", "", queryline))))
        query = splitquery[2:]
        for x in query:
            if not x in amount:
                amount[x] = 0
        queryline = queryRead.readline()

    queryRead.close()
    docsRead = open('docs.txt', 'r')
    docline = docsRead.readline()

    while docline:
        for x in amount:
            if x in docline:
                amount[x] += 1
        docline = docsRead.readline()

    docsRead.close()
    return amount    

def calcRank(tfdoc, tfquery, k, doclength, avgDocLen, totaldocs, numcontaining):
    base = float(tfdoc + float((k * float(doclength))/(float(avgDocLen))))
    tf = float(tfdoc) / base
    idf = float(math.log10((float(totaldocs)/float(numcontaining))))
    wordranking = tfquery * tf * idf
    return wordranking

def findAvgLenAndTotalNumber():
    doclength = 0
    numofdocuments = 0
    docsRead = open('docs.txt', 'r')
    docline = docsRead.readline()
    while docline:
        numofdocuments += 1
        splitdoc = re.split(" ", re.sub("-", " ", (re.sub("\\n", "", docline))))
        doclength += len(splitdoc[2:])
        docline = docsRead.readline()

    totalDocs = numofdocuments
    avgDocLen = doclength / totalDocs
    return [avgDocLen, totalDocs]
        
def hyphenremover(hyphenword):
    returnlist = []
    while "-" in hyphenword:
        returnlist.append(hyphenword[0:hyphenword.find("-")])
        hyphenword = hyphenword[hyphenword.find("-")+1:]
    returnlist.append(hyphenword)
    return returnlist

def hyphencombine(hyphenlist):
    output = []
    for x in range(len(hyphenlist)):
        base = ""
        for y in range(len(hyphenlist)-x):
            base = base+hyphenlist[x+y]
            output.append(base)
    return output
     
def SoundEx(word):
    firstLetter = word[0].upper()
    rest = word[1:].lower()
    rest = re.sub('[bfpv]', '1', rest)
    rest = re.sub('[cgjkqsxz]', '2', rest)
    rest = re.sub('[dt]', '3', rest)
    rest = re.sub('[l]', '4', rest)
    rest = re.sub('[mn]', '5', rest)
    rest = re.sub('[r]', '6', rest)
    rest = re.sub('[hw]', '', rest)

    rest = re.sub(r'(\d)\1+',r'\1',rest)
    rest = re.sub('[a-z]+','',rest)
    while len(rest) < 3:
        rest += '0'
    rest = rest[0:3]

    return firstLetter + rest

def readFile(filename):
	f = open(filename, 'r')
	try:
	    content = f.read()
	finally:
	    f.close()
	return content

def calculateDistance(word1, word2):
	x = zeros( (len(word1)+1, len(word2)+1) )
	for i in range(0,len(word1)+1):
		x[i,0] = i
	for i in range(0,len(word2)+1):
		x[0,i] = i

	for j in range(1,len(word2)+1):
		for i in range(1,len(word1)+1):
			if word1[i-1] == word2[j-1]:
				x[i,j] = x[i-1,j-1]
			else:
				minimum = x[i-1, j] + 1
				if minimum > x[i, j-1] + 1:
					minimum = x[i, j-1] + 1
				if minimum > x[i-1, j-1] + 1:
					minimum = x[i-1, j-1] + 1
				x[i,j] = minimum

	return x[len(word1), len(word2)]

def buildCategories(wordBase):
    d = dict()
    for word in wordBase:
        if len(word) != 0:
            code = SoundEx(word) # Now, each word is its own category
            d.setdefault(code, []).append(word)
    return d

def buildLengths(wordBase):
    d = dict()
    for word in wordBase:
        if len(word) != 0:
            d.setdefault(len(word), []).append(word)
    return d

def buildFirstThree(wordBase):
    d = dict()
    for word in wordBase:
        if len(word) != 0:
            d.setdefault(word[0:3], []).append(word)
    return d

def buildLastThree(wordBase):
    d = dict()
    for word in wordBase:
        if len(word)!= 0:
            if len(word) > 2:
                d.setdefault(word[len(word)-3:], []).append(word)
            elif len(word) <= 2:
                d.setdefault(word[len(word)-1], []).append(word)
    return d

def relWordDistanceFinder(sortedDist):
    d = []
    try:
        for x in sortedDist:
            if x[1][0] < 2:
                d.append(x[0])
        return d
    except IndexError:
        return d

if __name__ == '__main__':
    main()
    
