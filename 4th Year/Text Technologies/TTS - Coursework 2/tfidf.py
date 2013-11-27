import re
import math

def main():
    queryRead = open('qrys.txt', 'r')
    docsRead = open('docs.txt', 'r')
    
    numofqueries = 0
    tfquery = 0
    tfdoc = 0
    doclength = 0
    k = 2

    output = open('tfidf.top', 'w')
    #Two pass - find average document length, and number of documents, then proceed
    numdocavg = findAvgLenAndTotalNumber()
    avgDocLen = numdocavg[0]
    totaldocs = numdocavg[1]

    wordamountlookup = findWordAmountDictionary()
    
    docline = docsRead.readline()
    while docline:
        splitdoc = re.split(" ", re.sub("\\n", "", docline))
        docnum = splitdoc[0]
        doc = splitdoc[2:]
        doclength = len(doc)

        queryRead = open('qrys.txt', 'r')
        queryline = queryRead.readline()
        while queryline:
            splitquery = re.split(" ", re.sub("\\n", "", queryline))
            querynum = splitquery[0]
            query = splitquery[2:]
            ranking = 0.0
            
            for x in query:
                
                tfdoc = doc.count(x)
                tfquery = query.count(x)
                numcontaining = wordamountlookup[x]
                ranking += calcRank(tfdoc, tfquery, k, doclength, avgDocLen, totaldocs, numcontaining)                   

            if(ranking != 0):
                printout = str(querynum) + " 0 "+ str(docnum) + " 0 "+ str(ranking) + " 0 \n"
                output.write(printout)

            queryline = queryRead.readline()

        docline = docsRead.readline()
        
    output.close()
    queryRead.close()
    docsRead.close()

def findWordAmountDictionary():
    queryRead = open('qrys.txt', 'r')
    queryline = queryRead.readline()
    amount = {}
    
    while queryline:
        splitquery = re.split(" ", re.sub("\\n", "", queryline))
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
        splitdoc = re.split(" ", re.sub("\\n", "", docline))
        doclength += len(splitdoc[2:])
        docline = docsRead.readline()

    totalDocs = numofdocuments
    avgDocLen = doclength / totalDocs
    return [avgDocLen, totalDocs]

if __name__ == '__main__':
    main()
