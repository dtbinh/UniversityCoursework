x=raw_input("Enter a binary sequence : ")
if (x[0] == '0'):
    print "Starts with 0"
elif (x[0] == '1'):
    print "Starts with 1"
else:
    print "Error: Not a binary number!"
if (x[0] == '0' or x[0] == '1'):
    print "Starts with " + x[0]
else: 
    print "Error: Not a binary number!"
