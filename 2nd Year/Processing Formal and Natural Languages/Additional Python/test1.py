L = ['how','why','however','where','never']
for x in L:
    if(x[0:2] == "wh"):
        print "*" + " " + x[0:2] + " " + x
    else:
        print "-" + " " + x[0:2] + " " + x
