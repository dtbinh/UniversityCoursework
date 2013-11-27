import re

def main():
	queryRead = open('qrys.txt', 'r')
	docsRead = open('docs.txt', 'r')
	output = open('overlap.top', 'w')

	docline = docsRead.readline()
	while docline:
		splitdoc = re.split(" ", re.sub("\\n", "", docline))
		docnum = splitdoc[0]
		doc = splitdoc[2:]
		
		queryRead = open('qrys.txt', 'r')
		queryline = queryRead.readline()
		while queryline:
			splitquery = re.split(" ", re.sub("\\n", "", queryline))
			querynum = splitquery[0]
			query = splitquery[2:]
			ranking = 0.0
			
			for x in query:
				if x in doc:
					ranking += 1

			if ranking != 0:
				printout = str(querynum) + " 0 "+ str(docnum) + " 0 "+ str(ranking) + " 0 \n"
				output.write(printout)

			queryline = queryRead.readline()

		docline = docsRead.readline()

	output.close()
	queryRead.close()
	docsRead.close()
	
if __name__ == '__main__':
	main()
