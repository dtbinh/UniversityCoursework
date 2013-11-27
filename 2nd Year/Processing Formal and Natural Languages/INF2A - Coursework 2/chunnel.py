

#######################################
# chunnel2009.py
#
# Author: Srini Janarthanam, Stuart Anderson, Katya Alahverdzhieva
# School of Informatics, Edinburgh
# Date: 12-Nov-2009
# Updated: 14-Nov-2009
# FSA portion adapted from chunnel.py
#######################################


import nltk, re
import FSA

model = None
val = None
assign = None
curr_state = 0

# Your grammar file must be set here...
grammar = nltk.data.load('a03_grammar.fcfg')
print grammar
grammar_file = 'a03_grammar.fcfg'


# #'r1' = get_id('ambulance')
def get_id(label):
	return val[label]

# 'ambulance' = get_label('r1')
def get_label(id):
	for k in val.keys():
		if (id == val[k]):
			return k
 
def eval1(expr):
	lp = nltk.LogicParser()
	
	t = model.evaluate(expr, assign)
	print t

	if (t == True):
		print '>> yes'
	elif (t == False):
		print '>> no'
	else:
		print '>> I dont know'	


def eval2(expr):
	lp = nltk.LogicParser()
	if expr.startswith('exists'):
		expr = expr.replace('exists x.', '')
	l = model.satisfiers(lp.parse(expr), 'x', assign)
	if (l != set([])):
		for i in l:
			print '>>' + get_label(i)
	else:
		print '>> None'	


def replace_sq_brackets(l):
	l2 = []
	for (i,j) in l:
		l3 = []
		for k in j:
			l4 = []
			for m in k:
				l4 = l4 + [get_id(m)]
			l3 = l3 + [tuple(l4)]
		l2 = l2 + [(i, set(l3))]
	return l2



def create_model(cs):
	default = [('ambulance', 'r1'), ('fire_brigade', 'r2'), ('tow_truck', 'r3'),
                   ('victimsb', 'p1'), ('wreckb', 'p2'), ('victimsf', 'p3'), ('wreckf', 'p4'), ('flames', 'p5'),
                   ('hospital', 'l1'), ('fire_station', 'l2'), ('garage', 'l3'), ('british_end', 'l4'),('french_end','l5'),('nowhere','l6'),
                   ('resource', set(['r1','r2','r3'])),
                   ('problem', set(['p1', 'p2', 'p3', 'p4', 'p5']))
		] 
	global model, val, assign
	if not val:
		val = nltk.sem.Valuation(default)
		model = nltk.sem.Model(val.domain, val)
		assign = nltk.sem.Assignment(val.domain, {})        

	if (cs == []):
		return
	else:
		locs =  replace_sq_brackets(chunnel.toModel(cs))
		print chunnel.toModel(cs)
	
	csp = default + locs
	val = nltk.sem.Valuation(csp)
	model = nltk.sem.Model(val.domain, val)
	assign = nltk.sem.Assignment(val.domain, {})
	#print model
	#print val.domain
	#print val
	#print val.keys()
	#return (m,val,g)


def make_transition(expr):
	global curr_state
	next_state = chunnel.nextState(curr_state, expr)
	print 'Making transistion to:' 
	print next_state
	if (next_state != []):	
		create_model(next_state)
		curr_state = next_state
		print '>> ok'
	else:
		print 'Error:::Cannot execute your command'
	

def process(expr):
	print expr
	if (expr.startswith('assign')):
		make_transition(expr)
	elif (expr.startswith('rush_to')):
		make_transition(expr)
	elif (expr.startswith('remove')):
		make_transition(expr)
	elif (expr.startswith('extinguish')):
		make_transition(expr)
	elif (expr.startswith('evacuate')):
		make_transition(expr)
	elif (expr.startswith('return')):
		make_transition(expr)
	elif (expr.startswith('exists')):
		eval2(expr)
	else: 
		eval1(expr)
	
######################################################
################## THE CHUNNEL FSA ###################
######################################################
#
# This is the FSA model for the chunnel.  There are two
# crash sites: the british_end and the french_end and
# emergency vehicles are dispatched to these sites to help
# at the site.  The model is looping in the sense that
# whenever a vehicle return to its base new problems arise
# for it at both ends of the chunnel.  All accidents require
# all vehicles to attend and there is a requirement that
# fire is extinguished before people are evacuated before
# the wreck at the site is towed away.
#
########## ATOMIC ACTIONS ######################################
# Each atomic action comes in two forms, one labelled with
# propositions for the initial and final states and a second
# unlabelled version with with no propositions.  The unlabelled
# versions are used in languages that restrict the sequencing
# of the actions in the main model via the intersection operator.

rbf = FSA.singleton('return(fire_brigade,to(fire_station))',
                    None,
                    "location(fire_brigade,fire_station)")
rbfu = FSA.singleton('return(fire_brigade,to(fire_station))')

rba = FSA.singleton('return(ambulance,to(hospital))',
                    None,
                    "location(ambulance,hospital)")
rbau = FSA.singleton('return(ambulance,to(hospital))')

rbt = FSA.singleton('return(tow_truck,to(garage))',
                    None,
                    "location(tow_truck,garage)")
rbtu = FSA.singleton('return(tow_truck,to(garage))')
                    
cb1 = FSA.singleton('assign(british_end,fire_brigade)',
                    ["location(fire_brigade,fire_station)",
                     "location(flames,british_end)"],
                    ["location(fire_brigade,british_end)",
                     "location(flames,british_end)"]
                    )
cb1u = FSA.singleton('assign(british_end,fire_brigade)')

ca1 = FSA.singleton('assign(british_end,ambulance)',
                    ["location(ambulance,hospital)",
                     "location(victimsb,british_end)"
                     ],
                    ["location(ambulance,british_end)",
                     "location(victimsb,british_end)"
                     ])
ca1u = FSA.singleton('assign(british_end,ambulance)')

ctt1 = FSA.singleton('assign(british_end,tow_truck)',
                     ["location(tow_truck,garage)",
                      "location(wreckb,british_end)"
                      ],
                     ["location(tow_truck,british_end)",
                      "location(wreckb,british_end)"
                      ])
ctt1u = FSA.singleton('assign(british_end,tow_truck)')

fo1 = FSA.singleton('extinguish(flames,from(british_end))',
                    ["location(fire_brigade,british_end)",
                     "location(flames,british_end)"
                     ],
                    "location(fire_brigade,british_end)")
fo1u = FSA.singleton('extinguish(flames,from(british_end))')

pe1 = FSA.singleton('evacuate(victims,from(british_end))',
                    ["location(victimsb,british_end)",
                     "location(ambulance,british_end)"
                     ],
                    ["location(victimsb,hospital)",
                    "location(ambulance,nowhere)"])
pe1u = FSA.singleton('evacuate(victims,from(british_end))')

wc1 = FSA.singleton('remove(wreck,from(british_end))',
                    ["location(tow_truck,british_end)",
                     "location(wreckb,british_end)",
                     ],
                    ["location(wreckb,garage)",
                    "location(tow_truck,nowhere)"])
wc1u = FSA.singleton('remove(wreck,from(british_end))')


cb2 = FSA.singleton('assign(french_end,fire_brigade)',
                    ["location(fire_brigade,fire_station)",
                     "location(flames,french_end)"],
                    ["location(fire_brigade,french_end)",
                     "location(flames,french_end)"])
cb2u = FSA.singleton('assign(french_end,fire_brigade)')

ca2 = FSA.singleton('assign(french_end,ambulance)',
                    ["location(ambulance,hospital)",
                     "location(victimsf,french_end)"
                     ],
                    ["location(ambulance,french_end)",
                     "location(victimsf,french_end)"
                     ])
ca2u = FSA.singleton('assign(french_end,ambulance)')

ctt2 = FSA.singleton('assign(french_end,tow_truck)',
                     ["location(tow_truck,garage)",
                      "location(wreckf,french_end)"
                      ],
                     ["location(tow_truck,french_end)",
                      "location(wreckf,french_end)"
                      ])
ctt2u = FSA.singleton('assign(french_end,tow_truck)')

fo2 = FSA.singleton('extinguish(flames,from(french_end))',
                    ["location(fire_brigade,french_end)",
                     "location(flames,french_end)"
                     ],
                    "location(fire_brigade,french_end)")
fo2u = FSA.singleton('extinguish(flames,from(french_end))')

pe2 = FSA.singleton('evacuate(victims,from(french_end))',
                    ["location(victimsf,french_end)",
                     "location(ambulance,french_end)"
                     ],
                    ["location(victimsf,hospital)",
                    "location(ambulance,nowhere)"])
pe2u = FSA.singleton('evacuate(victims,from(french_end))')

wc2 = FSA.singleton('remove(wreck,from(french_end))',
                    ["location(tow_truck,french_end)",
                     "location(wreckf,french_end)"
                     ],
                    ["location(wreckf,garage)",
                     "location(tow_truck,nowhere)"])
wc2u = FSA.singleton('remove(wreck,from(french_end))')

# The rush_to transitions don't require propositions to be attached to them
rtf = FSA.singleton('rush_to(french_end)')
rtfu = FSA.singleton('rush_to(french_end)')
rtb = FSA.singleton('rush_to(british_end)')
rtbu = FSA.singleton('rush_to(british_end)')

# These transitions are always used to generate "self-loops" that
# are not used in interaction with the parser but are helpful in
# defining the model.
inf = FSA.minimize(FSA.closure(FSA.singleton('in_France')))
inb = FSA.minimize(FSA.closure(FSA.singleton('in_Britain')))

# This is the model for the firetruck with "rush" actions
# that allow it to go directly from one crash site to the
# other without passing through the fire station.
fo1 = FSA.minimize(FSA.concatenation(fo1,inb))
fo2 = FSA.minimize(FSA.concatenation(fo2,inf))
fbcycle = FSA.minimize(FSA.closure(FSA.concatenation(rtf,fo2,rtb,fo1)))
thrub = FSA.minimize(FSA.concatenation(cb1,fo1,fbcycle,FSA.union(rbf,FSA.concatenation(rtf,fo2,rbf))))
thruf = FSA.minimize(FSA.concatenation(cb2,fo2,FSA.union(rbf,FSA.concatenation(rtb,fo1,fbcycle,FSA.union(rbf,FSA.concatenation(rtf,fo2,rbf))))))
firetruck = FSA.minimize(FSA.closure(FSA.union(thrub,thruf)))
#raw_input('Please press <enter> to see firetruck')
#firetruck.view()

# This is the ambulance behaviour, it evacuates victims are
# evacuated either from the british end or the french end
ambulance = FSA.minimize(FSA.closure(FSA.union(FSA.concatenation(ca1,pe1,inb,rba),FSA.concatenation(ca2,pe2,inf,rba))))
#raw_input('Please press <enter> to see ambulance')
#ambulance.view()

# This is the tow truck behaviour, it tows wrecks away either
# from the british end or the french end.
towtruck = FSA.minimize(FSA.closure(FSA.union(FSA.concatenation(ctt1,wc1,inb,rbt),FSA.concatenation(ctt2,wc2,inf,rbt))))
#raw_input('Please press <enter> to see towtruck')
#towtruck.view()

# These define all actions and all those we do not want to restrict by intersection in the
# main model.  "oastar" is just any sequence of those actions we do not want to restrict.
allactions = FSA.minimize(FSA.union(inb,inf,rtfu,rtbu,rbfu,rbau,rbtu,cb1u,ca1u,ctt1u,fo1u,pe1u,wc1u,cb2u,ca2u,ctt2u,fo2u,pe2u,wc2u))
otheractions = FSA.minimize(FSA.union(inb,inf,rtfu,rtbu,rbfu,rbau,rbtu,cb1u,ca1u,ctt1u,cb2u,ca2u,ctt2u))
oastar = FSA.minimize(FSA.closure(otheractions))
#raw_input('Please press <enter> to see oastar')
#oastar.view()

# "synchservices" is just the language that ensures the fire must be extinguished before the
# victims are evacuated before the wreck is cleared at either end.
syncservices1 = FSA.closure(FSA.concatenation(oastar,fo1u,oastar,pe1u,oastar,wc1u,oastar))
syncservices2 = FSA.closure(FSA.concatenation(oastar,fo2u,oastar,pe2u,oastar,wc2u,oastar))
syncservices = FSA.minimize(FSA.interleave(syncservices1,syncservices2))
#raw_input('Please press <enter> to see syncservices')
#syncservices.view()

# The combined fire engine and ambulance behaviour
fireamb = FSA.minimize(FSA.interleave(firetruck,ambulance))
#raw_input('Please press <enter> to see fireamb')
#fireamb.view()

# The combined fire engine, ambulance and tow truck behaviour
fireambtow = FSA.minimize(FSA.interleave(fireamb,towtruck))
#raw_input('Please press <enter> to see fireambtow')
#fireambtow.view()

# Constrain the combined vehicles to observe the constraints on the order of activities.
chunnel = FSA.minimize(FSA.intersection(fireambtow,syncservices))
#raw_input('Please press <enter> to see chunnel')
#chunnel.view()


## Interpreter

def preprocess(s):
	s = s.strip()
	if s.endswith('?'):
		s = s.replace('?', ' ?')
	if s.endswith('.'):
		s = s.replace('.', ' .')
	
	return s


def translate(s):
	s = preprocess(s)
	result = nltk.sem.batch_interpret([s], grammar_file)
	print 'Parsing... ' + s
	print result
	if (result[0] != []):
		(syn, sema) = result[0][0]
		return sema.simplify()
	else:
		return 'Error::: Cannot parse the sentence'



#########################################
### Main Program

fsa = chunnel
curr_state = fsa.initialState
create_model(curr_state)

try:
	while(input <> "EOF"):	
		input = raw_input(">>>>>")
		if (input != ''):
			fopl_query = '' + translate(input).str()
			print 'fopl_query = '+ fopl_query
			process(fopl_query)
except EOFError:
    print '\nBye!'



###########################################################################



