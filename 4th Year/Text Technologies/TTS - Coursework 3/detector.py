import string
import os
import zlib
import re
import hashlib
import math

#Used above modules, code from Lab2 for reading,
#http://www.textfixer.com/resources/common-english-words.txt stopwords
#http://www.ercim.eu/publication/ws-proceedings/DelNoe02/AidanFinn.pdf
#http://stackoverflow.com/questions/265960/best-way-to-strip-punctuation-from-a-string-in-python
#Taken from wikipedia - http://en.wikipedia.org/wiki/Hamming_distance

def finn(fileLoc="0943941"):
    fileList = os.listdir(os.getcwd()+"//"+fileLoc)
    hashduplicates = {}
    setduplicates = {}
    stopWords = getStopWords()
    output = open('finn.txt', 'w')
    count = 0
    for x in fileList:            
        doc = readFile(os.getcwd()+"//"+fileLoc+"//"+x)
        if doc.startswith("This is a transcript of"):
            doc = doc[doc.find(":"):]
        processed = re.split(" ", re.sub("  ", " ", re.sub("(\t) | (\n)", " ", doc.translate(string.maketrans("",""), string.punctuation))))
        abValue = finnAlgorithm(processed)
        if abValue != () and abValue[1]-abValue[0] > 1:
            setHash = simHashFinn2(processed[abValue[0]:abValue[1]], stopWords)
            fingerprint = simHashFinn(processed[abValue[0]:abValue[1]], stopWords)
            splitfinger = fingerprint
            while len(splitfinger) > 0:
                if splitfinger[0:8] not in hashduplicates:
                    hashduplicates[splitfinger[0:8]] = [(str(x), fingerprint)]
                    setduplicates[splitfinger[0:8]] = [(str(x), setHash)]
                else:
                    hashduplicates[splitfinger[0:8]].append((str(x), fingerprint))
                    setduplicates[splitfinger[0:8]].append((str(x), setHash))
                splitfinger = splitfinger[8:]


    show = compareCosineDistance(hashduplicates, setduplicates)
    for x in show:
        output.write(x+"\n")
    output.close()

def finnAlgorithm(processed):
    L = 0
    c = 100
    maxValue = 0
    abValue = ()
    for x in range(len(processed)):
        value = checkReturnValue(processed[x])
        if processed[x] != '':
            L += value
            M = 0
            R = 0
            for i in range(x+1, len(processed)):
                innervalue = checkReturnValue(processed[i])
                if processed[i] != '':
                    R -= innervalue
                    M += 1-innervalue
                    total = L+c*M+R
                    if total > maxValue:
                        maxValue = total
                        abValue = (x, i)
    return abValue


def checkReturnValue(currWord):
    xi = 1 #Is Tag by default
    if str(currWord).isdigit(): #Is Token        
        xi = 0
    return xi
    

def exact(fileLoc="0943941"):
    fileList = os.listdir(os.getcwd()+"//"+fileLoc)
    exactduplicates = {}
    output = open('exact.txt', 'w')
    for x in fileList:
        doc = readFile(os.getcwd()+"//"+fileLoc+"//"+x)
        if doc.startswith("This is a transcript of"):
            doc = doc[doc.find(":"):]
        processed = doc.translate(string.maketrans("",""), string.punctuation)
        mapping = zlib.adler32(processed)
        if mapping not in exactduplicates:
            exactduplicates[mapping] = str(x)
        else:
            output.write(re.sub(".txt", "", exactduplicates[mapping])+"-"+re.sub(".txt", "", str(x))+"\n")
    output.close()

def near(fileLoc="0943941"):
    fileList = os.listdir(os.getcwd()+"//"+fileLoc)
    nearduplicates = {}
    output = open('near.txt', 'w')
    stopWords = getStopWords()
    nearduplicates = {}
    #Fingerprint - create fingerprint of each document, split into chunks, hash groups
    for x in fileList:
        doc = readFile(os.getcwd()+"//"+fileLoc+"//"+x)
        fingerprint = simHash(doc, stopWords)
        splitfinger = fingerprint
        while len(splitfinger) > 0:
            if splitfinger[0:8] not in nearduplicates:
                nearduplicates[splitfinger[0:8]] = [(str(x), fingerprint)]
            else:
                nearduplicates[splitfinger[0:8]].append((str(x), fingerprint))

            splitfinger = splitfinger[8:]


    show = compareDocumentsHash(nearduplicates)
    for x in show:
        output.write(x+"\n")
    output.close()

def compareDocumentsHash(nearduplicates):
    detector = []
    show = []
    #Compare documents in same hash against each other
    for x in nearduplicates:
        for y in nearduplicates[x]:
            for z in nearduplicates[x]:
                if y[0] != z[0]:
                    hdist = hamming_distance(y[1], z[1])
                    if hdist < 10:
                        if (y[0], z[0]) not in detector and (z[0], y[0]) not in detector:
                            detector.append((y[0], z[0]))
                            detector.append((z[0], y[0]))
                            show.append(re.sub(".txt", "", y[0])+"-"+re.sub(".txt", "", z[0]))
    return show

def compareCosineDistance(hashduplicates,setduplicates):
    detector = []
    show = []
    for x in setduplicates:
        for y in setduplicates[x]:
            for z in setduplicates[x]:
                if y[0] != z[0]:
                    cdist = len(y[1].intersection(z[1])) / (math.sqrt(len(y[1]) * len(z[1])))
                    if cdist > .5:
                        if (y[0], z[0]) not in detector and (z[0], y[0]) not in detector:
                            detector.append((y[0], z[0]))
                            detector.append((z[0], y[0]))
                            show.append(re.sub(".txt", "", y[0])+"-"+re.sub(".txt", "", z[0]))

    for x in hashduplicates:
        for y in hashduplicates[x]:
            for z in hashduplicates[x]:
                if y[0] != z[0]:
                    hdist = hamming_distance(y[1], z[1])
                    if hdist < 5:
                        if (y[0], z[0]) not in detector and (z[0], y[0]) not in detector:
                            detector.append((y[0], z[0]))
                            detector.append((z[0], y[0]))
                            show.append(re.sub(".txt", "", y[0])+"-"+re.sub(".txt", "", z[0]))
    return show


def simHash(doc, stopwords):
    speech = processSpeech(doc, stopwords)
    freq = calcFrequencies(speech)
    binaryWords = findBinaryWords(freq)
    vector = calcVector(binaryWords)
    fingerprint = calcFingerprint(vector)
    return fingerprint

def simHashFinn(doclist, stopwords):
    speech=[]
    for word in doclist:
        if word.lower() not in stopwords and word != '':
            speech.append(word.lower())
    freq = calcFrequencies(speech)
    binaryWords = findBinaryWords(freq)
    vector = calcVector(binaryWords)
    fingerprint = calcFingerprint(vector)
    return fingerprint

def simHashFinn2(doclist, stopwords):
    speech = []
    for word in doclist:
        if word.lower() not in stopwords and word != '':
            speech.append(word.lower())
    return set(speech)
    
def calcVector(binaryWords):
    vector = []
    for x in range(128):
        totalcol = 0
        for word in binaryWords:
            value = 1
            if binaryWords[word][1][x] == '0':
                value = -1
            totalcol += binaryWords[word][0] * value
        vector.append(totalcol)
    return vector

def calcFingerprint(vector):
    fingerprint = ""
    for x in vector:
        fingerprint += bin(x > 0)[2:]
    return fingerprint


def getStopWords():
    stopWords = "a,able,about,across,after,all,almost,also,am,among,an,and,any,are,as,at,be,because,been,but,by,can,cannot,could,dear,did,do,does,either,else,ever,every,for,from,get,got,had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,it,its,just,least,let,like,likely,may,me,might,most,must,my,neither,no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,say,says,she,should,since,so,some,than,that,the,their,them,then,there,these,they,this,tis,to,too,twas,us,wants,was,we,were,what,when,where,which,while,who,whom,why,will,with,would,yet,you,your"
    return re.split(",", stopWords)

def processSpeech(doc, stopwords):
    if doc.startswith("This is a transcript of"):
            doc = doc[doc.find(":"):]
    out = re.split(" ", re.sub("  ", " ", (re.sub("(\n)|(\t)", " ", doc.translate(string.maketrans("",""), string.punctuation)))))
    goodWords=[]
    for word in out:
        if word.lower() not in stopwords and word != '':
            goodWords.append(word.lower())
    return goodWords

def calcFrequencies(speech):
    freqAmount = {}
    for x in speech:
        if x not in freqAmount:
            freqAmount[x] = 1
        else:
            freqAmount[x] += 1
    return freqAmount

def findBinaryWords(frequency):
    binaryWords = {}
    for x in frequency:
        m = hashlib.md5()
        m.update(x)
        mapping = m.hexdigest()
        #mapping = zlib.adler32(x)
        binary = str(bin(int(mapping, 16))[2:]).zfill(128)
        binaryWords[x] = (frequency[x], binary)
    return binaryWords

#Taken from wikipedia - http://en.wikipedia.org/wiki/Hamming_distance
def hamming_distance(s1, s2):
    assert len(s1) == len(s2)
    return sum(ch1 != ch2 for ch1, ch2 in zip(s1, s2))

def readFile(filename):
    f = open(filename, 'r')
    try:
        content = f.read()
    finally:
        f.close()
    return content

def main():
    exact()
    near()
    finn()

if __name__ == '__main__':
    main()
