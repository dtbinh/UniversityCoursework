import re
#TODO
#Comment Code
#Write report


#Toss print states
#Add autorun
#Needs - graph.txt (email file) and roles.txt (roster file)
#Outputs files: hub.txt - Top ten hubs found by HITS
#               auth.txt - Top ten auths found by HITS
#               hub.txt - Top ten nodes found by PageRank
#               duplicates.txt - List of duplicate emails for each main Enron email address
#               graphEmails.dot - Final graph of important flow - emails added in file version
#               importantPersons.dot - Testing graph of important flow - referred to in report

def main():
    output = open("graph.dot", 'w')

    #Parse file according to original specifications
    nodeListName = fileParse(True, [])
           
    #HITS algorithm
    HITS(nodeListName)

    #PageRank algorithm
    PageRank(nodeListName)

    #Locations of interest
    possLocations = outputTens(nodeListName)

    #Add these two - President and CFO of Enron - not present 
    possLocations.append("jeff.skilling@enron.com")
    possLocations.append("andrew.fastow@enron.com")

    #Parse file while finding duplicates
    nodeListName = fileParse(False, possLocations)

    #Tailored list of nodes I want displayed
    customList = ["steven.kean@enron.com", "james.steffes@enron.com", "richard.shapiro@enron.com", "susan.mara@enron.com", "ginger.dernehl@enron.com", "jeff.dasovich@enron.com", "kenneth.lay@enron.com", "alan.comnes@enron.com", "jeff.skilling@enron.com", "andrew.fastow@enron.com"]

    #Count the amount of emails between important locations
    directedGraphCount = {}
    getDirectedNumbers(nodeListName, possLocations, directedGraphCount)

    
    output2 = open("importantPersons.dot", 'w')
    output.write("digraph G { ")
    output2.write("digraph G { ")
    for x in directedGraphCount:
        if x[0] in customList and x[1] in customList:
            if x[0] == "jeff.skilling@enron.com" or x[1] == "jeff.skilling@enron.com" or x[0] == "andrew.fastow@enron.com" or x[1] == "andrew.fastow@enron.com":
                output.write("\""+str(x[0])+"\" -> \""+str(x[1])+"\" [label = \""+str(directedGraphCount[x])+"\"]; ")
            else:
                if directedGraphCount[x] > 100:
                    output.write("\""+str(x[0])+"\" -> \""+str(x[1])+"\" [label = \""+str(directedGraphCount[x])+"\"]; ")
            if directedGraphCount[x] > 50:
                    output.write("\""+str(x[0])+"\" -> \""+str(x[1])+"\" [label = \""+str(directedGraphCount[x])+"\"]; ")

    output.write("}")
    output2.write("}")

    output.close()
    output2.close()

def fileParse(original, possChoices):
    customList = ["steven.kean@enron.com", "james.steffes@enron.com", "richard.shapiro@enron.com", "susan.mara@enron.com", "ginger.dernehl@enron.com", "jeff.dasovich@enron.com", "kenneth.lay@enron.com", "alan.comnes@enron.com", "jeff.skilling@enron.com", "andrew.fastow@enron.com"]
    duplicateoutput = open("duplicates.txt", 'w')
    duplicatedict = {}
    for x in customList:
        duplicatedict[x] = []
    doc = open("graph.txt", 'r')
    docline = doc.readline()
    nodeListName = {}
    if not original:
        duplicates = roster()
        possStrip = {}
        for x in possChoices:
            possStrip[x] = (stripEmail(x, True))
    else:
        dupCheck = ""
        
    while docline:
        splitted = re.split(" ", re.sub("\\n", "", docline))
        if splitted[1] != splitted[2]:
            if splitted[1] not in nodeListName:
                if not original:
                    dupCheck = isDuplicate(duplicates, splitted[1], possStrip)
                    
                if dupCheck == "":
                    nodeListName[splitted[1]] = Node(splitted[1])
                else:
                    if dupCheck in duplicatedict:
                        if splitted[1] not in duplicatedict[dupCheck]:
                            duplicatedict[dupCheck].append(splitted[1])
                        
                    splitted[1] = dupCheck
                    if splitted[1] not in nodeListName and splitted[2] != splitted[1]:
                        nodeListName[splitted[1]] = Node(splitted[1])
                
            if splitted[2] not in nodeListName:
                if not original:
                    dupCheck = isDuplicate(duplicates, splitted[2], possStrip)
                    
                if dupCheck == "":
                    nodeListName[splitted[2]] = Node(splitted[2])
                else:
                    if dupCheck in duplicatedict:
                        if splitted[2] not in duplicatedict[dupCheck]:
                            duplicatedict[dupCheck].append(splitted[2])
                    splitted[2] = dupCheck
                    if splitted[2]  not in nodeListName and splitted[2] != splitted[1]:
                        nodeListName[splitted[2]] = Node(splitted[2])

            nodeListName[splitted[2]].addInLink(nodeListName[splitted[1]])
            nodeListName[splitted[1]].addOutLink(nodeListName[splitted[2]])
        
        docline = doc.readline()

    for x in duplicatedict:
        duplicateoutput.write(str(x)+ " ")
        for y in duplicatedict[x]:
            duplicateoutput.write(str(y)+" ")
        duplicateoutput.write("\n")
    duplicateoutput.close()
    doc.close()
    return nodeListName

"""

Algorithms - PageRank and HITS

"""


def PageRank(nodeListName):
    #PageRank - initialize the ranking
    for x in nodeListName:
        nodeListName[x].pagerank = (1.0/len(nodeListName))

    lambdaVar = .8

    for t in range(0, 10):

        #Calculate the sum of all the sink values pageRank
        sinkValue = 0
        for x in nodeListName:
            if len(nodeListName[x].outgoing) == 0:
                sinkValue += nodeListName[x].pagerank        

        #Iterate over the entire list and update the pagerank
        for x in nodeListName:
            firstTerm = (1-lambdaVar + (lambdaVar * sinkValue))/len(nodeListName)
            count = 0
            for y in nodeListName[x].incoming:
                count += (y.pagerank/len(y.outgoing))
            nodeListName[x].pagerankhold = firstTerm + (lambdaVar * count)

        #Transfer from holder array to actual array, so that the sums total 1
        for x in nodeListName:
            nodeListName[x].pagerank = nodeListName[x].pagerankhold


def HITS(nodeListName):
    #Initialize
    for x in nodeListName:
        #nodeListName[x].auth = len(nodeListName) ** (-.5)
        #nodeListName[x].hub = len(nodeListName) ** (-.5)
        nodeListName[x].hub = 1
        nodeListName[x].auth = 1
    #Run for 10 iterations
    for y in range(0, 10):
        normAuth = 0.0
        normHub = 0.0

        #Calculate the authority (summing incoming hubs)
        for x in nodeListName:
            nodeListName[x].calcAuth()
            normAuth += (float(nodeListName[x].auth) ** 2)
        normAuth = float(normAuth) ** (.5)

        #Calculate the hubs (summing outgoing authority)
        for x in nodeListName:
            nodeListName[x].calcHub()
            normHub += (float(nodeListName[x].hub) ** 2)
        normHub = float(normHub) ** (.5)

        #Normalize each node
        for x in nodeListName:
            nodeListName[x].auth /= normAuth
            nodeListName[x].hub /= normHub

"""

Email correction and duplicate detection

"""

def compareEmails(first, second):
    if first != second:
        if first[0] == second[0] or first[1] == second[0] or first[0] == second[1]:
            if len(stripEmail(first, False)) > len(stripEmail(second, False)):
                if len(set(re.sub("[0-9]|[\.]", "", stripEmail(second, False)) ) - set(re.sub("\.", "", stripEmail(first, False)) )) == 0:
                    return True
            else:
                if len(set(re.sub("\.", "", stripEmail(first, False)) ) - set(re.sub("[0-9]|[\.]", "", stripEmail(second, False)) )) == 0:
                    return True
    return False

#Either removes everything after @ (if dot) or finds the surname of the email      
def stripEmail(email, dot):
    index = email.find("@")
    if index >= 0:
        if not dot:
            return email[:index]
        else:
            index2 = email[:index].find(".")
            if index2 >=0:
                return email[:index][index2+1:]
            else:
                return email[:index]    
        
    else:
        return email

#Checks if an email is a duplicate of an entry in duplicate or possStripped
def isDuplicate(duplicates, email, possStripped):
    #Iterate through the duplicates from roster file, and check if duplicate
    for x in duplicates:
        for y in duplicates[x]:
            if y == email:
                return max(duplicates[x], key=len)
    #Iterate through the important nodes, and check if possible duplicate
    stripped = stripEmail(email, False)
    for x in possStripped:
        if possStripped[x] in stripped:
            if compareEmails(x, email):
                return x
    return ""

#
def outputTens(nodeListName):
    outputHub = open('hubs.txt', 'w')
    outputAuth = open('auth.txt', 'w')
    outputPage = open('pr.txt', 'w')
    
    tophub = []
    topauth = []
    toppage = []
    topLenHubs = []    
    topLenAuth = []
    
    for x in nodeListName:
        tophub.append((nodeListName[x].hub, nodeListName[x].name))
        topauth.append((nodeListName[x].auth, nodeListName[x].name))
        toppage.append((nodeListName[x].pagerank, nodeListName[x].name))
        topLenHubs.append((len(nodeListName[x].incoming), nodeListName[x].name))
        topLenAuth.append((len(nodeListName[x].outgoing), nodeListName[x].name))

    tenHub = getTopTen(tophub)
    tenAuth = getTopTen(topauth)
    tenPage = getTopTen(toppage)
    tenLenHub = getTopTen(topLenHubs)
    tenLenAuth = getTopTen(topLenAuth)

    possLocations = []

    for x in tenHub:
        outputHub.write(str(round(float(x[0]), 8)) + " " + str(x[1]) + "\n")
        possLocations.append(x[1])
    for x in tenAuth:
        outputAuth.write(str(round(float(x[0]), 8)) + " " + str(x[1]) + "\n")
        possLocations.append(x[1])
    for x in tenPage:
        outputPage.write(str(round(float(x[0]), 8)) + " " + str(x[1]) + "\n")
        possLocations.append(x[1])
    for x in tenLenHub:
        possLocations.append(x[1])
    for x in tenLenAuth:
        possLocations.append(x[1])

    outputHub.close()
    outputAuth.close()
    outputPage.close()
    
    return possLocations

#Creates a mapping between the emails sent between important individuals
#and their amount (over all email accounts)
def getDirectedNumbers(nodeListName, possLocations, directedGraphCount):
    for x in set(possLocations):
        for y in nodeListName[x].outgoing:
            if y.name in possLocations and nodeListName[x].name in possLocations:
                if (nodeListName[x].name, y.name) not in directedGraphCount:
                    directedGraphCount[(nodeListName[x].name, y.name)] = 1
                else:
                    directedGraphCount[(nodeListName[x].name, y.name)] += 1
  

#Finds the top ten results of a list
def getTopTen(topList):
    topList.sort()
    topList.reverse()
    return topList[:10]




"""

Parsing Roster File

"""
def roster():
    docRead = open("roles.txt", 'r')
    docline = docRead.readline()
    parsedList = []
    while docline:
        naRemove = re.sub("N/A", " ", docline)
        xxxRemove = re.sub("xxx", " ", naRemove)
        newLineRemove = re.sub("\n", " ", xxxRemove)
        parsedList.append(parseLine(newLineRemove))

        docline = docRead.readline()
    duplicates = {}
    for roster in parsedList:
        if len(roster) > 1:
            initial = []
            initial.append(roster[0])
            if roster[1] not in duplicates:
                duplicates[roster[1]] = [roster[0]+"@enron.com"]
            for altroster in parsedList:
                if len(altroster) > 1:
                    if (roster[1] == altroster[1]) and (roster[0] != altroster[0]):
                        if roster[0] not in duplicates[roster[1]]:
                            duplicates[roster[1]].append(roster[0]+"@enron.com")
            
    docRead.close()
    return duplicates

def showRoster(duplicates):
    for x in duplicates:
        if len(duplicates[x]) > 1:
            print str(x) + " " + str(duplicates[x])

#Takes each line of the roster file and finds the email, name, and position
def parseLine(line):
    tabRemove = re.split("\t", line, 1)
    email = []
    email.append(tabRemove[0])
    count = 0
    letters = []
    words = []
    for x in tabRemove[1]: 
        if (x == ' ' or repr(x) == repr('\t')):
            #If we have a collection of multiple spaces,
            #then we have finished a word
            count += 1
            if count > 3:
                if letters:
                    string = ''.join(letters)
                    string = string.rstrip().lstrip()
                    if string:
                        words.append(string)
                    letters = []
                    
            else:
                letters.append(x)
        else:
            letters.append(x)
            count = 0

    #Add the final word from the buffer (typically the position if there is one)
    if letters:
        string = ''.join(letters)
        string = string.rstrip().lstrip()
        if string:
            words.append(string)
    
    return email + words 

"""

Node Class

"""

class Node:
    def __init__(self, name):
        self.name = name
        self.auth = 0.0
        self.hub = 0.0
        self.incoming = []
        self.outgoing = []
        self.pagerank = 0.0
        self.pagerankhold = 0.0

    #Add an outLink to the node
    def addOutLink(self, link):
        self.outgoing.append(link)

    #Add an inLink to the node
    def addInLink(self, link):
        self.incoming.append(link)

    #Sum the values of all the incoming hub scores
    def calcAuth(self):
        self.auth = 0.0
        for x in self.incoming:
            self.auth += x.getHub()

    #Sum the values of all the outgoing authority scores
    def calcHub(self):
        self.hub = 0.0
        for x in self.outgoing:
            self.hub += x.getAuth()

    def getHub(self):
        return self.hub

    def getAuth(self):
        return self.auth




if __name__ == '__main__':
	main()
                    
