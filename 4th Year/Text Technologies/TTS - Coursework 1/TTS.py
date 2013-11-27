import robotparser
import urllib
import re
import heapq
import time
from bs4 import BeautifulSoup

#Mode specifies priority method - 1 = high numbers, 2 = low numbers, anything else = time
#Boolean Soupmode specifies whether to use beautiful soup or not Soupmode = True -> Use Soup
def crawl(mode, soupmode):
    #Code taken from http://docs.python.org/library/robotparser.html
    #This section attains robot.txt, and allows us to check if we can read a file
    rp = robotparser.RobotFileParser()
    robotstxt = "http://ir.inf.ed.ac.uk/robots.txt"
    rp.set_url(robotstxt)
    rp.read()
    
    #Since the python code lacks functionality to check request_rate/crawl delay, I have implemented it myself
    response = urllib.urlopen(robotstxt)
    robots_source = response.read()
    crawldelay = fetchCrawlDelay(robots_source)

    requestrate = re.search("Request-rate: ([0-9]+)/([0-9]+)", robots_source)
    if(requestrate != None):
        requestratetop = int(requestrate.group(1))    #Top number of the request rate - how many pages we can scroll
        requestratebottom = int(requestrate.group(2)) #Bottom number of the request rate - how many seconds
    else:
        requestratetop = 0
        requestratebottom = 0

    requestratetop = 0
    requestratebottom = 0
    #Set initial pages to be empty
    visitedpages = []
    tovisit = []
    brokenpages = []

    #Variables for respecting robots request rate/crawl delay
    crawledpersecond = 0
    global linkcount
    linkcount = 0
    
    #Load the initial page. Requirement: the largest number takes priority. 
    #In a heapq, the smallest is popped first, take the inverse of the number as priority
    priority = calculatePriority(mode, "0943941.html")
    heapq.heappush(tovisit, ((priority), "http://ir.inf.ed.ac.uk/tts/A1/0943941/0943941.html"))
    beginningruntime = time.time()
    
    while(len(tovisit) != 0):
        starttime = time.time()
        currenturl = heapq.heappop(tovisit)
        response = urllib.urlopen(currenturl[1])
        page_source = response.read()   
        trimmedsource = trimsource(page_source, currenturl[1])
        if(trimmedsource == -1):
            brokenpages.append(currenturl[1])
        else:
            visitedpages.append(currenturl[1])
            if soupmode == True:
                soup = BeautifulSoup(trimmedsource)
                soupParse(soup, visitedpages, tovisit, rp, mode)
            else:
                htmlParse(trimmedsource, visitedpages, tovisit, rp, mode)

        
        endtime = time.time()
        crawledpersecond += 1
        #If we have exceed what the request rate says we can perform, reset the counter and wait the remaining time
        if(crawledpersecond >= requestratetop):
            #Time to yield - request rate minus the amount of times we've waited to finish a single crawl, and the length of however long it took to finish this crawl
            #Could keep track of the length of each of the crawls and take them into account if we wanted it to be faster
            yieldtime = requestratebottom-(crawledpersecond*crawldelay)-(endtime-starttime)
            crawledpersecond = 0
            if(yieldtime < 0):
                yieldtime = 0
            time.sleep(yieldtime)
        
        time.sleep(crawldelay)
        
    endingruntime = time.time()
    print "Size of total links found: "+str(linkcount)+" Size of visited: " + str(len(visitedpages)) + " Size of broken pages: "+str(len(brokenpages))
    print "Total time: "+ str(endingruntime - beginningruntime)

#Gets crawl delay
def fetchCrawlDelay(source):
    crawldelaysource = re.search("Crawl-delay: ([0-9]+)", source)
    crawldelay = 0
    if(crawldelaysource != None):
        crawldelay = int(crawldelaysource.group(1)) #Crawl delay - delay between successful crawls
    return crawldelay

#Checks that there are no duplicates within the URL list (used for debugging)
def checkForDuplicates(tovisit):
    return len(tovisit)!=len(set(tovisit))

#Takes an HTML source page, and removes all the information before and after Content tags. If there are no content tags, return -1
def trimsource(pagesource, currenturl):
    m = pagesource.find("<!-- CONTENT -->")
    if m != -1:
        trimmed_source_top = pagesource[m:]
    else:
        return m

    m = trimmed_source_top.find("<!-- /CONTENT -->")
    if m != -1:
        trimmed_source = trimmed_source_top[:m]
    else:
    	return m

    return trimmed_source


#My defined HTML parser - finds all URLs with href tags (assumed that these will be between <a> </a> for standard purposes
def htmlParse(html, visited, tovisit, rp, mode):
    regular = re.findall("href=\"((http\://(www\.){0,1}[\w]+\.[\w]+/){0,1}([\w]+/)*[\w]+\.html)\"", html)
    for link in regular:
        global linkcount
        linkcount += 1
        #Check if local or another domain
        if "http://ir.inf.ed.ac.uk" in link[0]:
            urltoadd = link[0]
            link[0] = re.findall(".*?/{0,1}([\w]+\.html)", urltoadd)
        elif "http" not in link[0]:
            urltoadd = "http://ir.inf.ed.ac.uk/tts/A1/0943941/"+link[0]
        else:
            return
        canFetch(urltoadd, visited, tovisit, rp, link[0], mode)

#Beautiful soup method of parsing - finds all URLs with href, verifies domain
#This will break because it cannot assign priority for a local one
def soupParse(soup, visited, tovisit, rp, mode):
    for link in soup.find_all('a'):
        global linkcount
        linkcount += 1
        stringlink = str(link.get('href'))
        if "http://ir.inf.ed.ac.uk" in stringlink:
            urltoadd = stringlink
            stringlink = re.findall(".*?/{0,1}([\w]+\.html)", urltoadd)
        elif "http" not in stringlink:
            urltoadd = "http://ir.inf.ed.ac.uk/tts/A1/0943941/"+stringlink
        else:
            return
        canFetch(urltoadd, visited, tovisit, rp, stringlink, mode)

def calculatePriority(mode, link):
    if mode == 1:
        return 1.0/float(re.sub(".html","", re.sub("^0+", "", link)))
    elif mode == 2:
        return float(re.sub(".html","", re.sub("^0+", "", link)))
    else:
        global linkcount
        return linkcount
        
        
#Function that checks if URL can be fetched, parses the URL (.. moves a folder up) and adds it to the priority queue if not already there
def canFetch(urltoadd, visited, tovisit, rp, link, mode):
    if(rp.can_fetch("TTS", urltoadd) and link != "None"):
        while "../" in urltoadd:
            previousurl = urltoadd
            urltoadd = re.sub("/(\w+)/\.\./", "/", urltoadd, 1)
            link = re.sub("\.\./", "", link, 1)
            if previousurl == urltoadd and "../" in urltoadd:
                print "There's something wrong with this URL. Aborting."
                return

        #Strip leading zeros and .html tag, convert to float, and take 1 over to get relative priority for heap    
        priority = calculatePriority(mode, link)
	
	#First two modes - highest priority and lowest priority
        if mode == 1 or mode == 2:
            if not urltoadd in visited and not (priority, urltoadd) in tovisit:
                heapq.heappush(tovisit, (priority, urltoadd))

        #Time sensative mode - does not work with the previous if statement
        else:
            if not urltoadd in visited and not timeModeCheck(urltoadd, tovisit):
                heapq.heappush(tovisit, (priority, urltoadd))
    
def timeModeCheck(urltoadd, tovisit):
    for weburl in tovisit:
        if weburl[1] == urltoadd:
            return True
    return False
