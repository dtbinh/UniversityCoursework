# Module FSA2a -- methods to manipulate finite-state automata
#         Original version by Oliver Steele
#           This version by Laura Hutchins-Korte

# 20050725 LK: taken from http://osteele.com/software/python/fsa/
# 20050725 LK: fixed bug
# 20050726 LK: fixed bugs
# 20050802 LK: extended
# 20050804 LK: extended
# 20050826 LK: documented
# 20050901 LK: setting up testing framework
# 20050902 LK: setting up testing framework (cont.)
# 20050916 LK: improved view() method + composeLevels
# 20050916 LK: implemented checkQ1-checkQ?
# 20050919 LK: implemented rest of checkQs
# 20050920 LK: implement last behaviour & ...
#  :
#  :
# 20060720 LK: added play(i)
# 20060720 LK: started adding metaData
# 20060721 LK: continued adding metaData


__author__  = "Laura Korte <Laura.Korte@ed.ac.uk>. " + "Earlier version by Oliver Steele <steele@osteele.com>."

from types import InstanceType, ListType, IntType, LongType
IntegerTypes = (IntType, LongType)
from copy import deepcopy

try:
    import NumFSAUtils
except ImportError:
    NumFSAUtils = None

ANY = 'ANY'
EPSILON = None

TRACE_LABEL_MULTIPLICATIONS = 0
# NUMPY_DETERMINIZATION_CUTOFF = 50

class FSA:
    def __init__(self, states, alphabet, transitions, initialState, finalStates, metaData={}):
        if states == None:
            states = self.collectStates(transitions, initialState, finalStates)
        else:
            assert not filter(lambda s, states=states:s not in states, self.collectStates(transitions, initialState, finalStates))
        self.states = states
        self.alphabet = alphabet
        self.transitions = transitions
        self.initialState = initialState
        self.finalStates = finalStates
        self.metaData = metaData
# was: self.setArcMetadata(ArcMetadata)
    
    
    #
    # Initialization
    #

    # 20050802 LK: correct
    def makeStateTable(self, default=None):
        for state in self.states:
            if type(state) != IntType:
                return {}
	# if the minimum is smaller than 0 or the maximum is bigger than 100
	# or twice the amount of states, then fail
        if reduce(min, self.states) < 0: return {}
        if reduce(max, self.states) > max(100, len(self.states) * 2): return {}
	# else return a list of None's of length *highest state + 1*
        return [default] * (reduce(max, self.states) + 1)
    
    def initializeTransitionTables(self):
        self._transitionsFrom = self.makeStateTable()
        for s in self.states:
            self._transitionsFrom[s] = []
        for transition in self.transitions:
            s, _, label = transition
            self._transitionsFrom[s].append(transition)

    # 20050802 LK: adapted from initializeTransitionTables
    #              purpose = transitionsTo
    def initializeTransitionTables2(self):
        self._transitionsTo = self.makeStateTable()
        for s in self.states:
            self._transitionsTo[s] = []
        for transition in self.transitions:
            _, s, label = transition
            self._transitionsTo[s].append(transition)

    def collectStates(self, transitions, initialState, finalStates):
        states = finalStates[:]
        if initialState not in states:
            states.append(initialState)
        for s0, s1, _ in transitions:
            if s0 not in states: states.append(s0)
            if s1 not in states: states.append(s1)
        return states
    
    # 20050802 LK: correct
    def computeEpsilonClosure(self, state):
        states = [state]
        index = 0
        while index < len(states):
            state, index = states[index], index + 1
            for _, s, label in self.transitionsFrom(state):
                if label == EPSILON and s not in states:
                    states.append(s)
        states.sort()
        return states

    # 20050802 LK: correct
    def computeEpsilonClosures(self):
        self._epsilonClosures = self.makeStateTable()
        for s in self.states:
            self._epsilonClosures[s] = self.computeEpsilonClosure(s)
    
    
    #
    # Copying
    #
    def create(self, *args):
        return apply(self.__class__, args)
    
    def copy(self, *args):
        copy = apply(self.__class__, args)
        if hasattr(self, 'label'):
            copy.label = self.label
        if hasattr(self, 'source'):
            copy.source = self.source
        return copy
    
    def creationArgs(self):
        return self.tuple() + (self.getArcMetadata(),)
    
    def coerce(self, klass):
        copy = apply(klass, self.creationArgs())
        if hasattr(self, 'source'):
            copy.source = self.source
        return copy
    
    
    #
    # Accessors
    #
    def epsilonClosure(self, state):
	try: # Q: is this one never used for *old* epsilonClosures?
            return self._epsilonClosures[state]
        except AttributeError:
            self.computeEpsilonClosures()
        return self._epsilonClosures[state]
    
    def labels(self):
        """Returns a list of transition labels."""
        labels = []
        for (_, _, label) in self.transitions:
            if label and label not in labels:
                labels.append(label)
        return labels
    
    def nextAvailableState(self):
        return reduce(max, filter(lambda s:type(s) in IntegerTypes, self.states), -1) + 1
    
    def transitionsFrom(self, state):
        try:
            return self._transitionsFrom[state]
        except AttributeError:
            self.initializeTransitionTables()
        return self._transitionsFrom[state]
    
    # 20050802 LK: created from transitionsFrom
    def transitionsTo(self, state):
        try:
            return self._transitionsTo[state]
        except AttributeError:
            self.initializeTransitionTables2()
        return self._transitionsTo[state]

    def tuple(self):
        return self.states, self.alphabet, self.transitions, self.initialState, self.finalStates
    
    
    #
    # Arc Metadata Accessors
    #
    def hasArcMetadata(self):
        return hasattr(self, '_arcMetadata')
    
    def getArcMetadata(self):
        return getattr(self, '_arcMetadata', {}).items()
    
    def setArcMetadata(self, list):
        arcMetadata = {}
        for (arc, data) in list:
            arcMetadata[arc] = data
        self._arcMetadata = arcMetadata
    
    def addArcMetadata(self, list):
        for (arc, data) in list:
            self.addArcMetadataFor(arc, data)
    
    def addArcMetadataFor(self, transition, data):
        if not hasattr(self, '_arcMetadata'):
            self._arcMetadata = {}
        oldData = self._arcMetadata.get(transition)
        if oldData:
            for item in data:
                if item not in oldData:
                    oldData.append(item)
        else:
            self._arcMetadata[transition] = data
        
    def setArcMetadataFor(self, transition, data):
        if not hasattr(self, '_arcMetadata'):
            self._arcMetadata = {}
        self._arcMetadata[transition] = data
    
    def getArcMetadataFor(self, transition, default=None):
        return getattr(self, '_arcMetadata', {}).get(transition, default)
    
    
    #
    # Predicates
    #
    def isEmpty(self):
        return not self.minimized().finalStates
    
    def isFSA(self):
        return 1
    
    
    #
    # Accepting
    #
    def labelMatches(self, label, input):
        return labelMatches(label, input)
    
    # 20050726 LK: *BUG* added the iStates for-loop
    # 20050802 LK: correct
    def nextStates2(self, state, input):
	iStates = self.epsilonClosure(state)
        states = []
	for iState in iStates:
	    for _, sink, label in self.transitionsFrom(iState):
                if self.labelMatches(label, input) and sink not in states:
                    states.extend(self.epsilonClosure(sink))
        return states


    # 20050901 LK: created
    def previousStates(self, state):
        pStates = []
        for source, sink, label in self.transitionsTo(state):
            if source not in pStates:
                pStates.append(source)
        return pStates

    def nextStates1(self, state):
        nStates = []
        for source, sink, label in self.transitionsFrom(state):
            if sink not in nStates:
                nStates.append(sink)
        return nStates

    # 20050804 LK: created from nextStates
    def nextInputs(self, state):
	iStates = self.epsilonClosure(state)
        labels = []
	for iState in iStates:
	    for _, sink, label in self.transitionsFrom(iState):
                if label not in labels:
                    labels.append(label)
        return labels

    # 20050802 LK: correct, but looks rather useless
    def nextState(self, state, input):
        states = self.nextStates2(state, input)
        if len(states) > 0:
           return states[0]
        else:
           return None

    # 20050802 LK: correct, but looks rather useless
    def nextState1(self, state, input):
        states = self.nextStates2(state, input)
        assert len(states) <= 1
        return states and states[0]
    
    # 20050802 LK: *BUG* added the iStates for-loop
    # 20050802 LK: correct    
    def nextStateSet(self, states, input):
        successors = []
        for state in states:
	    iStates = self.epsilonClosure(state)
	    for iState in iStates:
	        for _, sink, label in self.transitionsFrom(iState):
                    if self.labelMatches(label, input) and sink not in successors:
                        successors.append(sink)
        return successors
   
    # 20050802 LK: correct
    # 20050901 LK: *BUG* fixed (did not work with 'ANY')
    def accepts(self, sequence):
        states = [self.initialState]
        for item in sequence:
            newStates = []
            for state in states:
		n = self.nextStates2(state, item) + self.nextStates2(state, ANY)
		for s1 in n:
                    if s1 not in newStates:
                        newStates.append(s1)
            states = newStates
        return len(filter(lambda s, finals=self.finalStates:s in finals, states)) > 0
    
    
    #
    # FSA operations
    #
    def complement(self):
        states, alpha, transitions, start, finals = completion(self.determinized()).tuple()
#states, alpha, transitions, start, finals = completion(self.minimized()).tuple()
        return self.create(states, alpha, transitions, start, filter(lambda s,f=finals:s not in f, states))#.trimmed()
    
    
    #
    # Reductions
    #
    #20050922 LK: removed '_isSorted' because it was the cause of errors
    def sorted(self, initial=0):
#        if hasattr(self, '_isSorted'):
#            return self
        stateMap = {}
        nextState = initial
        states, index = [self.initialState], 0
        while index < len(states) or len(states) < len(self.states):
            if index >= len(states):
                for state in self.states:
                    if stateMap.get(state) == None:
                        break
                states.append(state)
            state, index = states[index], index + 1
            new, nextState = nextState, nextState + 1
            stateMap[state] = new
            for _, s, _ in self.transitionsFrom(state):
                if s not in states:
                    states.append(s)
        states = stateMap.values()
        newMetaData = buildNewMetaData(self.metaData,stateMap)
        transitions = map(lambda (s0,s1,l),m=stateMap:(m[s0], m[s1], l), self.transitions)
#        arcMetadata = map(lambda ((s0, s1, label), data), m=stateMap: ((m[s0], m[s1], label), data), self.getArcMetadata())
        copy = self.copy(states, self.alphabet, transitions, stateMap[self.initialState], map(stateMap.get, self.finalStates), newMetaData)
#        copy._isSorted = 1
        return copy
    
    
    

    def trimmed(self):
        """Returns an equivalent FSA that doesn't include unreachable states,
        or states that only lead to dead states."""
        if hasattr(self, '_isTrimmed'):
            return self
        states, alpha, transitions, initial, finals = self.tuple()
        reachable, index = [initial], 0
        while index < len(reachable):
            state, index = reachable[index], index + 1
            for (_, s, _) in self.transitionsFrom(state):
                if s not in reachable:
                    reachable.append(s)
        endable, index = list(finals), 0
        while index < len(endable):
            state, index = endable[index], index + 1
            for (s0, s1, _) in transitions:
                if s1 == state and s0 not in endable:
                    endable.append(s0)
        states = []
        for s in reachable:
            if s in endable:
                states.append(s)
        if not states:
            if self.__class__  == FSA:
                return NULL_FSA
            else:
                return NULL_FSA.coerce(self.__class__)
        transitions = filter(lambda (s0, s1, _), states=states:s0 in states and s1 in states, transitions)
#        self.displayTransitions(transitions)
#        newMetadata = filter(lambda ((s0, s1, _), __), states=states: s0 in states and s1 in states, self.metaData)
        result = self.copy(states, alpha, transitions, initial, filter(lambda s, states=states:s in states, finals), self.metaData).sorted()
        result._isTrimmed = 1
        return result
    
    def withoutEpsilons(self):
        # replace each state by its epsilon closure
        states0, alphabet, transitions0, initial0, finals0 = self.tuple()
        initial = self.epsilonClosure(self.initialState)
        initial.sort()
        initial = tuple(initial)
        stateSets, index = [initial], 0
        transitions = []
        while index < len(stateSets):
            stateSet, index = stateSets[index], index + 1
            for (s0, s1, label) in transitions0:
                if s0 in stateSet and label:
                    target = self.epsilonClosure(s1)
                    target.sort()
                    target = tuple(target)
                    transition = (stateSet, target, label)
                    if transition not in transitions:
                        transitions.append(transition)
                    if target not in stateSets:
                        stateSets.append(target)
        finalStates = []
        for stateSet in stateSets:
            if filter(lambda s, finalStates=self.finalStates:s in finalStates, stateSet):
                finalStates.append(stateSet)
        copy = self.copy(stateSets, alphabet, transitions, stateSets[0], finalStates).sorted()
        copy._isTrimmed = 1
        return copy
    
    def determinized(self):
        """Returns a deterministic FSA that accepts the same language."""
        if hasattr(self, '_isDeterminized'):
            return self
#        if len(self.states) > NUMPY_DETERMINIZATION_CUTOFF and NumFSAUtils and not self.getArcMetadata():
#            data = apply(NumFSAUtils.determinize, self.tuple() + (self.epsilonClosure,))
#            result = apply(self.copy, data).sorted()
#            result._isDeterminized = 1
#            return result
        transitions = []
        stateSets, index = [tuple(self.epsilonClosure(self.initialState))], 0
#        arcMetadata = []
        while index < len(stateSets):
            stateSet, index = stateSets[index], index + 1
            # all non-epsilon transitions from one of the current states
            localTransitions = filter(lambda (s0,s1,l), set=stateSet:l and s0 in set, self.transitions)
            if localTransitions:
                localLabels = map(lambda(_,__,label):label, localTransitions)
                labelMap = constructLabelMap(localLabels, self.alphabet)
                labelTargets = {}   # a map from labels to target states
                for transition in localTransitions:
                    _, s1, l1 = transition
                    for label, positives in labelMap:
                        if l1 in positives:
                            successorStates = labelTargets[label] = labelTargets.get(label) or []
                            for s2 in self.epsilonClosure(s1):
                                if s2 not in successorStates:
                                    successorStates.append(s2)
#                            if self.getArcMetadataFor(transition):
#                                arcMetadata.append(((stateSet, successorStates, label), self.getArcMetadataFor(transition)))
                for label, successorStates in labelTargets.items():
                    successorStates.sort()
                    successorStates = tuple(successorStates)
                    transitions.append((stateSet, successorStates, label))
                    if successorStates not in stateSets:
                        stateSets.append(successorStates)
        finalStates = []
        for stateSet in stateSets:
            if filter(lambda s,finalStates=self.finalStates:s in finalStates, stateSet):
                finalStates.append(stateSet)
#        if arcMetadata:
#            def fixArc(pair):
#                (s0, s1, label), data = pair
#                s1.sort()
#                s1 = tuple(s1)
#                return ((s0, s1, label), data)
#            arcMetadata = map(fixArc, arcMetadata)
        result = self.copy(stateSets, self.alphabet, transitions, stateSets[0], finalStates, self.metaData).sorted()
        result._isDeterminized = 1
        result._isTrimmed = 1
        return result
   
    # *BROKEN* 
    def minimized(self):
        """Returns a minimal FSA that accepts the same language."""
        if hasattr(self, '_isMinimized'):
            return self
        s = self.trimmed()
        s = s.determinized()
#        s = self.determinized()
#        s = s.determinized()
#        s.view()
        states0, alpha0, transitions0, initial0, finals0 = s.tuple()
        sinkState = s.nextAvailableState()
        labels = s.labels()
        states = filter(None, [
                tuple(filter(lambda s, finalStates=s.finalStates:s not in finalStates, states0)),
                tuple(filter(lambda s, finalStates=s.finalStates:s in finalStates, states0))])
        labelMap = {}
        for state in states0:
            for label in labels:
                found = 0
                for s0, s1, l in s.transitionsFrom(state):
                    if l == label:
                        assert not found
                        found = 1
                        labelMap[(state, label)] = s1
        changed = 1
        iteration = 0
        while changed:
            changed = 0
            iteration = iteration + 1
            #print 'iteration', iteration
            partitionMap = {sinkState: sinkState}
            for set in states:
                for state in set:
                    partitionMap[state] = set
            #print 'states =', states
            for index in range(len(states)):
                set = states[index]
                if len(set) > 1:
                    for label in labels:
                        destinationMap = {}
                        for state in set:
                            nextSet = partitionMap[labelMap.get((state, label), sinkState)]
                            targets = destinationMap[nextSet] = destinationMap.get(nextSet) or []
                            targets.append(state)
                        #print 'destinationMap from', set, label, ' =', destinationMap
                        if len(destinationMap.values()) > 1:
                            values = destinationMap.values()
                            #print 'splitting', destinationMap.keys()  % IMPORTANT
                            for value in values:
                                value.sort()
                            states[index:index+1] = map(tuple, values)
                            changed = 1
                            break
        transitions = removeDuplicates(map(lambda (s0,s1,label), m=partitionMap:(m[s0], m[s1], label), transitions0))
#        arcMetadata = map(lambda ((s0, s1, label), data), m=partitionMap:((m[s0], m[s1], label), data), s.getArcMetadata())
        if not alpha0:
            newTransitions = consolidateTransitions(transitions)
#            if arcMetadata:
#                newArcMetadata = []
#                for transition, data in arcMetadata:
#                    s0, s1, label = transition
#                    for newTransition in newTransitions:
#                        if newTransition[0] == s0 and newTransition[1] == s1 and labelIntersection(newTransition[2], label):
#                            newArcMetadata.append((newTransition, data))
#                arcMetadata = newArcMetadata
            transitions = newTransitions
        initial = partitionMap[initial0]
        finals = removeDuplicates(map(lambda s, m=partitionMap:m[s], finals0))
        result = s.create(states, s.alphabet, transitions, initial, finals, s.metaData).sorted()
        result._isDeterminized = 1
        result._isMinimized = 1
        result._isTrimmed = 1
        return result
    
    
    #
    # Presentation Methods
    #
    def __repr__(self):
        if hasattr(self, 'label') and self.label:
            return '<%s on %s>' % (self.__class__.__name__, self.label)
        else:
            return '<%s.%s instance>' % (self.__class__.__module__, self.__class__.__name__)
    
    def __str__(self):
        import string
        output = []
        output.append('%s {' % (self.__class__.__name__,))
        output.append('\tinitialState = ' + `self.initialState` + ';')
        if self.finalStates:
            output.append('\tfinalStates = ' + string.join(map(str, self.finalStates), ', ') + ';')
        transitions = list(self.transitions)
        transitions.sort()
        for transition in transitions:
            (s0, s1, label) = transition
            additionalInfo = self.additionalTransitionInfoString(transition)
            output.append('\t%s -> %s %s%s;' % (s0, s1, labelString(label), additionalInfo and ' ' + additionalInfo or ''));
        output.append('}');
        return string.join(output, '\n')
    
    def additionalTransitionInfoString(self, transition):
        if self.getArcMetadataFor(transition):
            import string
            return '<' + string.join(map(str, self.getArcMetadataFor(transition)), ', ') + '>'
    
    def stateLabelString(self, state):
        """A template method for specifying a state's label, for use in dot
        diagrams. If this returns None, the default (the string representation
        of the state) is used."""
        return None
   
    # 20050916 LK: initial state was bold, but now has an incoming arrow
    def toDotString(self):
        """Returns a string that can be printed by the DOT tool at
        http://www.research.att.com/sw/tools/graphviz/ ."""
        import string
	preState = max(self.states) + 1
        output = []
        output.append('digraph finite_state_machine {');
        if self.finalStates:
                output.append('\tnode [shape = doublecircle]; ' + string.join(map(str, self.finalStates), '; ') + ';' );
        output.append('\tnode [shape = circle];');
        output.append('\trankdir=LR;');
        output.append('\t%s [style = invis];' % (preState))
        output.append('\t%s -> %s;' % (preState,self.initialState,))
        for state in self.states:
            if self.stateLabelString(state):
                output.append('\t%s [label = "%s"];' % (state, string.replace(self.stateLabelString(state), '\n', '\\n')))
        transitions = list(self.transitions)
        transitions.sort()
        for (s0, s1, label) in transitions:
            output.append('\t%s -> %s  [label = "%s"];' % (s0, s1, string.replace(labelString(label), '\n', '\\n')));
        output.append('}');
        return string.join(output, '\n')
    
    def view(self):
        view(self.toDotString())
        self.displayStates()

    def displayStates(self):
        import sys
        stateInfo = self.metaData
        for state in stateInfo:
            stateStr = str(state)
            spaces = iter(' ',4 - len(stateStr))
            sys.stdout.write("\n" + str(state) + ":" + spaces)
#            sys.stdout.flush()       
            lines = stateInfo[state]
            sys.stdout.write(lines[0] + "\n")
            i = 1
            while i < len(lines): 
               print ("     " + lines[i])
               i = i + 1

    def displayTransitions(self,trans=None):
        import sys
        if not trans:
           trans = self.transitions
        trans.sort()
        lengths = []
        for t in trans:
          lengths = lengths + [len(t)]
        m = max(lengths)
        for t in trans:
            s0 = str(t[0])
            s1 = str(t[1])
            label = str(t[2])
            spaces1 = iter(' ',m - len(s0))
            spaces2 = iter(' ',m - len(s1))
            sys.stdout.write(spaces1)
            sys.stdout.write(s0)
            sys.stdout.write(" -->" + spaces2)
            sys.stdout.write(s1)
            sys.stdout.write(" : ")
            sys.stdout.write(label)
            sys.stdout.write("\n")


    #####################
    # Testing framework #
    #####################

    # 20050901 LK: created
    # 20050920 LK: added minimization
#    def oldb1(self):
#        fsa = self
#        fsa = minimize(self)
#        transitions14 = [ ( 0,  1, ANY)
#                        , ( 1,  2, ANY)
#                        , ( 2,  3, ANY)
#                        , ( 3,  4, ANY)
#                        , ( 4,  5, ANY)
#                        , ( 5,  6, ANY)
#                        , ( 6,  7, ANY)
#                        , ( 7,  8, ANY)
#                        , ( 8,  9, ANY)
#                        , ( 9, 10, ANY)
#                        , (10, 11, ANY)
#                        , (11, 12, ANY)
#                        , (12, 13, ANY)
#                        , (13, 14, ANY)
#		        ]
#        states14 = [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14]
#        universal14 = FSA(states14, None, transitions14, 0, states14) 
#        r = intersection(fsa, universal14)
#        boolean = r.isEmpty()
#        return boolean

    # 20050921 LK: new, more efficient check
    # 20050922 LK: fixed bug (subs 's' for 'next')
    # DESCRIPTION: It takes the player at least 15 actions to reach the goal.
    def behaviour1(self):
        myStates = [self.initialState]
        index = 0
        stop = False
        while index < 15 and stop == False:
            tempStates = []
            for s in myStates:
                 for next in self.nextStates1(s):
                    if next in self.finalStates:
                        stop = True
                    if next not in tempStates:
                        tempStates = tempStates + [next] 
            myStates = tempStates
            index = index + 1
  	return not stop


    # 20050901 LK: created
    # DESCRIPTION: It is always possible to reach the goal.
    def behaviour2(self):
        goodStates = self.finalStates
	index = 0
	while index < len(goodStates):
	    state = goodStates[index]
	    pStates = self.previousStates(state)
	    for s in pStates:
	        if s not in goodStates:
                    goodStates = goodStates + [s]
	    index = index + 1
	boolean = len(self.states) == len(goodStates)
	return boolean

#    # 20050922 LK: new, more efficient check
#    # DESCRIPTION: Your game contains infinite paths.
#    def behaviour3(self):
#        length = len(self.states)
#        myStates = [self.initialState]
#        index = 0
#        while index < length:
#            tempStates = []
#            for s in myStates:
#                 for next in self.nextStates1(s):
#                     if next not in tempStates:
#                         tempStates = tempStates + [next] 
#            myStates = tempStates
#            index = index + 1
#        if myStates == []:
#            boolean = False
#        else:
#            boolean = True
##        boolean = min(map(lambda x: x in self.finalStates,myStates))
#	return boolean


    # 20050901 LK: started (with Dijksta's algorithm)
    # 20050902 LK: finished
    # DESCRIPTION: Your game contains at least 1 'long' forced cycle (i.e. at least 5 states before a repeat).
    def behaviour3(self,size=5):
        fsa = minimize(self)
        states = fsa.states
        transitions = fsa.transitions
        stop = False
        index = 0
        while index < len(states) and stop == False:
            goalState = states[index]
            startStates = []
	    for _, sink, label in fsa.transitionsFrom(goalState):
                startStates = startStates + [sink]
	    for startState in startStates:
	        path = dijkstra(fsa, startState, goalState)
#                print startState, goalState, path
                if path >= (size):
		    stop = True
	    index = index + 1
        return stop

    # DESCRIPTION: Your game contains at least 1 'long' unforced cycle (i.e. at least 5 states before a repeat).
    def behaviour4(self,size=5):
        fsa = minimize(self)
        states = fsa.states
        transitions = fsa.transitions
        stop = False
        index = 0
        while index < len(states) and not(stop):
            goalState = states[index]
            startStates = []
	    for _, sink, label in fsa.transitionsFrom(goalState):
                startStates = startStates + [sink]
	    for startState in startStates:
                if not(stop):
	           path = dijkstra2(fsa, startState, goalState,size=size)
                   if path >= (size):
		      stop = True
	    index = index + 1
        return stop



    # 20050901 LK: created
    # 20060919 LK: changed behaviour
    # DESCRIPTION: Within at least 1 level it is always possible to reach
    #   the initial state (of the level) with 1 step. Note that it is not
    #   required that the initial state can be reached from the \emph{final}
    #   states of the level.
#    def behaviour5(self):
#        goodStates = [self.initialState]
#	index = 0
#	while index < len(goodStates):
#	    state = goodStates[index]
#	    pStates = self.previousStates(state)
#	    for s in pStates:
#	        if s not in goodStates:
#                    goodStates.append(s)
#	    index = index + 1
#        boolean = True
#        for state in self.states:
#           newBool = (state in self.finalStates) or (state in goodStates)
#           boolean = boolean and newBool
#	return boolean

    # within at tleast one level it is always possible to reach the initial state of that level
    def behaviour5(self):
        self = self.minimized()
        goodStates = self.previousStates(self.initialState)
        boolean = True
        for state in self.states:
           newBool = (state in self.finalStates) or (state in goodStates)
           boolean = boolean and newBool
        return boolean
                   

    # 20050921 LK: created
    # 20060919 LK: depricated
#    def b6(self):
#        myStopper = False
#        i = 0
#        while i <= 50 and myStopper == False:
#           if self.behaviour6(i):
#              print "B6 succeeded at " + str(i)
#              myStopper = True
#           i = i + 1
#        return myStopper

    def checkQ1(self):
        boolean = equivalent(self,at_door)
        return boolean

    def checkQ2(self):
        boolean = equivalent(self,notes_coffee)
        return boolean

    def checkQ3(self):
        boolean = equivalent(self,iPod)
        return boolean

    def checkQ4(self):
        boolean = equivalent(self,level2)
        return boolean

    def checkQ5a(self):
        boolean = equivalent(self,dial1)
        return boolean

    def checkQ5d(self):
        boolean = equivalent(self,dials)
        return boolean
    
    def checkQ5f(self):
        boolean = equivalent(self,level1)
        return boolean

    def checkQ6(self):
        boolean = equivalent(self,game)
        return boolean


at_door = FSA([0, 1], None, [(0, 1, 'Enter AT'), (1, 0, 'Leave AT')], 0, [1])

notes_coffee = FSA([1, 0], None, [(0, 0, 'Have a coffee'), (0, 1, 'Pick up lecture notes'), (1, 0, 'Return lecture notes')], 0, [1])

iPod= FSA([0, 1], None, [(0, 1, 'Turn iPod on'), (1, 0, 'Turn iPod off')], 0, [0])

level2 = FSA([0, 1, 2, 3, 4, 5, 6, 7], None, [(0, 1, 'Turn iPod on'), (0, 2, 'Enter AT'), (1, 0, 'Turn iPod off'), (1, 3, 'Enter AT'), (2, 0, 'Leave AT'), (2, 2, 'Have a coffee'), (2, 4, 'Pick up lecture notes'), (2, 3, 'Turn iPod on'), (3, 1, 'Leave AT'), (3, 3, 'Have a coffee'), (3, 5, 'Pick up lecture notes'), (3, 2, 'Turn iPod off'), (4, 6, 'Leave AT'), (4, 5, 'Turn iPod on'), (4, 2, 'Return lecture notes'), (5, 7, 'Leave AT'), (5, 4, 'Turn iPod off'), (5, 3, 'Return lecture notes'), (6, 7, 'Turn iPod on'), (6, 4, 'Enter AT'), (7, 6, 'Turn iPod off'), (7, 5, 'Enter AT')], 0, [4, 6])

dial1 = FSA([2, 3, 1, 0], None, [(0, 1, 'Turn dial 1'), (1, 2, 'Turn dial 1'), (2, 3, 'Turn dial 1'), (3, 0, 'Turn dial 1')], 0, [2])


dials = FSA([21, 32, 59, 23, 34, 61, 9, 36, 63, 11, 38, 49, 13, 24, 51, 15, 26, 53, 1, 28, 55, 3, 30, 41, 5, 16, 43, 7, 18, 45, 56, 20, 47, 58, 22, 33, 60, 8, 35, 62, 10, 37, 48, 12, 39, 50, 14, 25, 52, 0, 27, 54, 2, 29, 40, 4, 31, 42, 6, 17, 44, 19, 46, 57], None, [(0, 1, 'Turn dial 2'), (0, 2, 'Turn dial 3'), (0, 3, 'Turn dial 1'), (1, 4, 'Turn dial 2'), (1, 5, 'Turn dial 3'), (1, 6, 'Turn dial 1'), (2, 5, 'Turn dial 2'), (2, 7, 'Turn dial 3'), (2, 8, 'Turn dial 1'), (3, 6, 'Turn dial 2'), (3, 8, 'Turn dial 3'), (3, 9, 'Turn dial 1'), (4, 10, 'Turn dial 2'), (4, 11, 'Turn dial 3'), (4, 12, 'Turn dial 1'), (5, 11, 'Turn dial 2'), (5, 13, 'Turn dial 3'), (5, 14, 'Turn dial 1'), (6, 12, 'Turn dial 2'), (6, 14, 'Turn dial 3'), (6, 15, 'Turn dial 1'), (7, 13, 'Turn dial 2'), (7, 16, 'Turn dial 3'), (7, 17, 'Turn dial 1'), (8, 14, 'Turn dial 2'), (8, 17, 'Turn dial 3'), (8, 18, 'Turn dial 1'), (9, 15, 'Turn dial 2'), (9, 18, 'Turn dial 3'), (9, 19, 'Turn dial 1'), (10, 0, 'Turn dial 2'), (10, 20, 'Turn dial 3'), (10, 21, 'Turn dial 1'), (11, 20, 'Turn dial 2'), (11, 22, 'Turn dial 3'), (11, 23, 'Turn dial 1'), (12, 21, 'Turn dial 2'), (12, 23, 'Turn dial 3'), (12, 24, 'Turn dial 1'), (13, 22, 'Turn dial 2'), (13, 25, 'Turn dial 3'), (13, 26, 'Turn dial 1'), (14, 23, 'Turn dial 2'), (14, 26, 'Turn dial 3'), (14, 27, 'Turn dial 1'), (15, 24, 'Turn dial 2'), (15, 27, 'Turn dial 3'), (15, 28, 'Turn dial 1'), (16, 25, 'Turn dial 2'), (16, 0, 'Turn dial 3'), (16, 29, 'Turn dial 1'), (17, 26, 'Turn dial 2'), (17, 29, 'Turn dial 3'), (17, 30, 'Turn dial 1'), (18, 27, 'Turn dial 2'), (18, 30, 'Turn dial 3'), (18, 31, 'Turn dial 1'), (19, 28, 'Turn dial 2'), (19, 31, 'Turn dial 3'), (19, 0, 'Turn dial 1'), (20, 2, 'Turn dial 2'), (20, 32, 'Turn dial 3'), (20, 33, 'Turn dial 1'), (21, 3, 'Turn dial 2'), (21, 33, 'Turn dial 3'), (21, 34, 'Turn dial 1'), (22, 32, 'Turn dial 2'), (22, 35, 'Turn dial 3'), (22, 36, 'Turn dial 1'), (23, 33, 'Turn dial 2'), (23, 36, 'Turn dial 3'), (23, 37, 'Turn dial 1'), (24, 34, 'Turn dial 2'), (24, 37, 'Turn dial 3'), (24, 38, 'Turn dial 1'), (25, 35, 'Turn dial 2'), (25, 1, 'Turn dial 3'), (25, 39, 'Turn dial 1'), (26, 36, 'Turn dial 2'), (26, 39, 'Turn dial 3'), (26, 40, 'Turn dial 1'), (27, 37, 'Turn dial 2'), (27, 40, 'Turn dial 3'), (27, 41, 'Turn dial 1'), (28, 38, 'Turn dial 2'), (28, 41, 'Turn dial 3'), (28, 1, 'Turn dial 1'), (29, 39, 'Turn dial 2'), (29, 3, 'Turn dial 3'), (29, 42, 'Turn dial 1'), (30, 40, 'Turn dial 2'), (30, 42, 'Turn dial 3'), (30, 43, 'Turn dial 1'), (31, 41, 'Turn dial 2'), (31, 43, 'Turn dial 3'), (31, 2, 'Turn dial 1'), (32, 7, 'Turn dial 2'), (32, 44, 'Turn dial 3'), (32, 45, 'Turn dial 1'), (33, 8, 'Turn dial 2'), (33, 45, 'Turn dial 3'), (33, 46, 'Turn dial 1'), (34, 9, 'Turn dial 2'), (34, 46, 'Turn dial 3'), (34, 47, 'Turn dial 1'), (35, 44, 'Turn dial 2'), (35, 4, 'Turn dial 3'), (35, 48, 'Turn dial 1'), (36, 45, 'Turn dial 2'), (36, 48, 'Turn dial 3'), (36, 49, 'Turn dial 1'), (37, 46, 'Turn dial 2'), (37, 49, 'Turn dial 3'), (37, 50, 'Turn dial 1'), (38, 47, 'Turn dial 2'), (38, 50, 'Turn dial 3'), (38, 4, 'Turn dial 1'), (39, 48, 'Turn dial 2'), (39, 6, 'Turn dial 3'), (39, 51, 'Turn dial 1'), (40, 49, 'Turn dial 2'), (40, 51, 'Turn dial 3'), (40, 52, 'Turn dial 1'), (41, 50, 'Turn dial 2'), (41, 52, 'Turn dial 3'), (41, 5, 'Turn dial 1'), (42, 51, 'Turn dial 2'), (42, 9, 'Turn dial 3'), (42, 53, 'Turn dial 1'), (43, 52, 'Turn dial 2'), (43, 53, 'Turn dial 3'), (43, 7, 'Turn dial 1'), (44, 16, 'Turn dial 2'), (44, 10, 'Turn dial 3'), (44, 54, 'Turn dial 1'), (45, 17, 'Turn dial 2'), (45, 54, 'Turn dial 3'), (45, 55, 'Turn dial 1'), (46, 18, 'Turn dial 2'), (46, 55, 'Turn dial 3'), (46, 56, 'Turn dial 1'), (47, 19, 'Turn dial 2'), (47, 56, 'Turn dial 3'), (47, 10, 'Turn dial 1'), (48, 54, 'Turn dial 2'), (48, 12, 'Turn dial 3'), (48, 57, 'Turn dial 1'), (49, 55, 'Turn dial 2'), (49, 57, 'Turn dial 3'), (49, 58, 'Turn dial 1'), (50, 56, 'Turn dial 2'), (50, 58, 'Turn dial 3'), (50, 11, 'Turn dial 1'), (51, 57, 'Turn dial 2'), (51, 15, 'Turn dial 3'), (51, 59, 'Turn dial 1'), (52, 58, 'Turn dial 2'), (52, 59, 'Turn dial 3'), (52, 13, 'Turn dial 1'), (53, 59, 'Turn dial 2'), (53, 19, 'Turn dial 3'), (53, 16, 'Turn dial 1'), (54, 29, 'Turn dial 2'), (54, 21, 'Turn dial 3'), (54, 60, 'Turn dial 1'), (55, 30, 'Turn dial 2'), (55, 60, 'Turn dial 3'), (55, 61, 'Turn dial 1'), (56, 31, 'Turn dial 2'), (56, 61, 'Turn dial 3'), (56, 20, 'Turn dial 1'), (57, 60, 'Turn dial 2'), (57, 24, 'Turn dial 3'), (57, 62, 'Turn dial 1'), (58, 61, 'Turn dial 2'), (58, 62, 'Turn dial 3'), (58, 22, 'Turn dial 1'), (59, 62, 'Turn dial 2'), (59, 28, 'Turn dial 3'), (59, 25, 'Turn dial 1'), (60, 42, 'Turn dial 2'), (60, 34, 'Turn dial 3'), (60, 63, 'Turn dial 1'), (61, 43, 'Turn dial 2'), (61, 63, 'Turn dial 3'), (61, 32, 'Turn dial 1'), (62, 63, 'Turn dial 2'), (62, 38, 'Turn dial 3'), (62, 35, 'Turn dial 1'), (63, 53, 'Turn dial 2'), (63, 47, 'Turn dial 3'), (63, 44, 'Turn dial 1')], 0, [51])



([21, 32, 59, 23, 34, 61, 9, 36, 63, 11, 38, 49, 13, 24, 51, 15, 26, 53, 1, 28, 55, 3, 30, 41, 5, 16, 43, 7, 18, 45, 56, 20, 47, 58, 22, 33, 60, 8, 35, 62, 10, 37, 48, 12, 39, 50, 14, 25, 52, 0, 27, 54, 2, 29, 40, 4, 31, 42, 6, 17, 44, 19, 46, 57], None, [(0, 1, 'Turn dial 2'), (0, 2, 'Turn dial 3'), (0, 3, 'Turn dial 1'), (1, 4, 'Turn dial 2'), (1, 5, 'Turn dial 3'), (1, 6, 'Turn dial 1'), (2, 5, 'Turn dial 2'), (2, 7, 'Turn dial 3'), (2, 8, 'Turn dial 1'), (3, 6, 'Turn dial 2'), (3, 8, 'Turn dial 3'), (3, 9, 'Turn dial 1'), (4, 10, 'Turn dial 2'), (4, 11, 'Turn dial 3'), (4, 12, 'Turn dial 1'), (5, 11, 'Turn dial 2'), (5, 13, 'Turn dial 3'), (5, 14, 'Turn dial 1'), (6, 12, 'Turn dial 2'), (6, 14, 'Turn dial 3'), (6, 15, 'Turn dial 1'), (7, 13, 'Turn dial 2'), (7, 16, 'Turn dial 3'), (7, 17, 'Turn dial 1'), (8, 14, 'Turn dial 2'), (8, 17, 'Turn dial 3'), (8, 18, 'Turn dial 1'), (9, 15, 'Turn dial 2'), (9, 18, 'Turn dial 3'), (9, 19, 'Turn dial 1'), (10, 0, 'Turn dial 2'), (10, 20, 'Turn dial 3'), (10, 21, 'Turn dial 1'), (11, 20, 'Turn dial 2'), (11, 22, 'Turn dial 3'), (11, 23, 'Turn dial 1'), (12, 21, 'Turn dial 2'), (12, 23, 'Turn dial 3'), (12, 24, 'Turn dial 1'), (13, 22, 'Turn dial 2'), (13, 25, 'Turn dial 3'), (13, 26, 'Turn dial 1'), (14, 23, 'Turn dial 2'), (14, 26, 'Turn dial 3'), (14, 27, 'Turn dial 1'), (15, 24, 'Turn dial 2'), (15, 27, 'Turn dial 3'), (15, 28, 'Turn dial 1'), (16, 25, 'Turn dial 2'), (16, 0, 'Turn dial 3'), (16, 29, 'Turn dial 1'), (17, 26, 'Turn dial 2'), (17, 29, 'Turn dial 3'), (17, 30, 'Turn dial 1'), (18, 27, 'Turn dial 2'), (18, 30, 'Turn dial 3'), (18, 31, 'Turn dial 1'), (19, 28, 'Turn dial 2'), (19, 31, 'Turn dial 3'), (19, 0, 'Turn dial 1'), (20, 2, 'Turn dial 2'), (20, 32, 'Turn dial 3'), (20, 33, 'Turn dial 1'), (21, 3, 'Turn dial 2'), (21, 33, 'Turn dial 3'), (21, 34, 'Turn dial 1'), (22, 32, 'Turn dial 2'), (22, 35, 'Turn dial 3'), (22, 36, 'Turn dial 1'), (23, 33, 'Turn dial 2'), (23, 36, 'Turn dial 3'), (23, 37, 'Turn dial 1'), (24, 34, 'Turn dial 2'), (24, 37, 'Turn dial 3'), (24, 38, 'Turn dial 1'), (25, 35, 'Turn dial 2'), (25, 1, 'Turn dial 3'), (25, 39, 'Turn dial 1'), (26, 36, 'Turn dial 2'), (26, 39, 'Turn dial 3'), (26, 40, 'Turn dial 1'), (27, 37, 'Turn dial 2'), (27, 40, 'Turn dial 3'), (27, 41, 'Turn dial 1'), (28, 38, 'Turn dial 2'), (28, 41, 'Turn dial 3'), (28, 1, 'Turn dial 1'), (29, 39, 'Turn dial 2'), (29, 3, 'Turn dial 3'), (29, 42, 'Turn dial 1'), (30, 40, 'Turn dial 2'), (30, 42, 'Turn dial 3'), (30, 43, 'Turn dial 1'), (31, 41, 'Turn dial 2'), (31, 43, 'Turn dial 3'), (31, 2, 'Turn dial 1'), (32, 7, 'Turn dial 2'), (32, 44, 'Turn dial 3'), (32, 45, 'Turn dial 1'), (33, 8, 'Turn dial 2'), (33, 45, 'Turn dial 3'), (33, 46, 'Turn dial 1'), (34, 9, 'Turn dial 2'), (34, 46, 'Turn dial 3'), (34, 47, 'Turn dial 1'), (35, 44, 'Turn dial 2'), (35, 4, 'Turn dial 3'), (35, 48, 'Turn dial 1'), (36, 45, 'Turn dial 2'), (36, 48, 'Turn dial 3'), (36, 49, 'Turn dial 1'), (37, 46, 'Turn dial 2'), (37, 49, 'Turn dial 3'), (37, 50, 'Turn dial 1'), (38, 47, 'Turn dial 2'), (38, 50, 'Turn dial 3'), (38, 4, 'Turn dial 1'), (39, 48, 'Turn dial 2'), (39, 6, 'Turn dial 3'), (39, 51, 'Turn dial 1'), (40, 49, 'Turn dial 2'), (40, 51, 'Turn dial 3'), (40, 52, 'Turn dial 1'), (41, 50, 'Turn dial 2'), (41, 52, 'Turn dial 3'), (41, 5, 'Turn dial 1'), (42, 51, 'Turn dial 2'), (42, 9, 'Turn dial 3'), (42, 53, 'Turn dial 1'), (43, 52, 'Turn dial 2'), (43, 53, 'Turn dial 3'), (43, 7, 'Turn dial 1'), (44, 16, 'Turn dial 2'), (44, 10, 'Turn dial 3'), (44, 54, 'Turn dial 1'), (45, 17, 'Turn dial 2'), (45, 54, 'Turn dial 3'), (45, 55, 'Turn dial 1'), (46, 18, 'Turn dial 2'), (46, 55, 'Turn dial 3'), (46, 56, 'Turn dial 1'), (47, 19, 'Turn dial 2'), (47, 56, 'Turn dial 3'), (47, 10, 'Turn dial 1'), (48, 54, 'Turn dial 2'), (48, 12, 'Turn dial 3'), (48, 57, 'Turn dial 1'), (49, 55, 'Turn dial 2'), (49, 57, 'Turn dial 3'), (49, 58, 'Turn dial 1'), (50, 56, 'Turn dial 2'), (50, 58, 'Turn dial 3'), (50, 11, 'Turn dial 1'), (51, 57, 'Turn dial 2'), (51, 15, 'Turn dial 3'), (51, 59, 'Turn dial 1'), (52, 58, 'Turn dial 2'), (52, 59, 'Turn dial 3'), (52, 13, 'Turn dial 1'), (53, 59, 'Turn dial 2'), (53, 19, 'Turn dial 3'), (53, 16, 'Turn dial 1'), (54, 29, 'Turn dial 2'), (54, 21, 'Turn dial 3'), (54, 60, 'Turn dial 1'), (55, 30, 'Turn dial 2'), (55, 60, 'Turn dial 3'), (55, 61, 'Turn dial 1'), (56, 31, 'Turn dial 2'), (56, 61, 'Turn dial 3'), (56, 20, 'Turn dial 1'), (57, 60, 'Turn dial 2'), (57, 24, 'Turn dial 3'), (57, 62, 'Turn dial 1'), (58, 61, 'Turn dial 2'), (58, 62, 'Turn dial 3'), (58, 22, 'Turn dial 1'), (59, 62, 'Turn dial 2'), (59, 28, 'Turn dial 3'), (59, 25, 'Turn dial 1'), (60, 42, 'Turn dial 2'), (60, 34, 'Turn dial 3'), (60, 63, 'Turn dial 1'), (61, 43, 'Turn dial 2'), (61, 63, 'Turn dial 3'), (61, 32, 'Turn dial 1'), (62, 63, 'Turn dial 2'), (62, 38, 'Turn dial 3'), (62, 35, 'Turn dial 1'), (63, 53, 'Turn dial 2'), (63, 47, 'Turn dial 3'), (63, 44, 'Turn dial 1')], 0, [51])




level1 = FSA([1, 31, 58, 22, 33, 60, 9, 35, 62, 11, 37, 48, 13, 23, 50, 15, 25, 52, 63, 27, 54, 3, 29, 40, 5, 16, 42, 7, 18, 44, 55, 20, 46, 57, 21, 32, 59, 8, 34, 61, 10, 36, 47, 12, 38, 49, 14, 24, 51, 0, 26, 53, 64, 2, 28, 39, 4, 30, 41, 6, 17, 43, 19, 45, 56], None, [(0, 1, 'Swipe your student card'), (1, 2, 'Turn dial 2'), (1, 3, 'Turn dial 3'), (1, 4, 'Turn dial 1'), (2, 5, 'Turn dial 2'), (2, 6, 'Turn dial 3'), (2, 7, 'Turn dial 1'), (3, 6, 'Turn dial 2'), (3, 8, 'Turn dial 3'), (3, 9, 'Turn dial 1'), (4, 7, 'Turn dial 2'), (4, 9, 'Turn dial 3'), (4, 10, 'Turn dial 1'), (5, 11, 'Turn dial 2'), (5, 12, 'Turn dial 3'), (5, 13, 'Turn dial 1'), (6, 12, 'Turn dial 2'), (6, 14, 'Turn dial 3'), (6, 15, 'Turn dial 1'), (7, 13, 'Turn dial 2'), (7, 15, 'Turn dial 3'), (7, 16, 'Turn dial 1'), (8, 14, 'Turn dial 2'), (8, 17, 'Turn dial 3'), (8, 18, 'Turn dial 1'), (9, 15, 'Turn dial 2'), (9, 18, 'Turn dial 3'), (9, 19, 'Turn dial 1'), (10, 16, 'Turn dial 2'), (10, 19, 'Turn dial 3'), (10, 20, 'Turn dial 1'), (11, 1, 'Turn dial 2'), (11, 21, 'Turn dial 3'), (11, 22, 'Turn dial 1'), (12, 21, 'Turn dial 2'), (12, 23, 'Turn dial 3'), (12, 24, 'Turn dial 1'), (13, 22, 'Turn dial 2'), (13, 24, 'Turn dial 3'), (13, 25, 'Turn dial 1'), (14, 23, 'Turn dial 2'), (14, 26, 'Turn dial 3'), (14, 27, 'Turn dial 1'), (15, 24, 'Turn dial 2'), (15, 27, 'Turn dial 3'), (15, 28, 'Turn dial 1'), (16, 25, 'Turn dial 2'), (16, 28, 'Turn dial 3'), (16, 29, 'Turn dial 1'), (17, 26, 'Turn dial 2'), (17, 1, 'Turn dial 3'), (17, 30, 'Turn dial 1'), (18, 27, 'Turn dial 2'), (18, 30, 'Turn dial 3'), (18, 31, 'Turn dial 1'), (19, 28, 'Turn dial 2'), (19, 31, 'Turn dial 3'), (19, 32, 'Turn dial 1'), (20, 29, 'Turn dial 2'), (20, 32, 'Turn dial 3'), (20, 1, 'Turn dial 1'), (21, 3, 'Turn dial 2'), (21, 33, 'Turn dial 3'), (21, 34, 'Turn dial 1'), (22, 4, 'Turn dial 2'), (22, 34, 'Turn dial 3'), (22, 35, 'Turn dial 1'), (23, 33, 'Turn dial 2'), (23, 36, 'Turn dial 3'), (23, 37, 'Turn dial 1'), (24, 34, 'Turn dial 2'), (24, 37, 'Turn dial 3'), (24, 38, 'Turn dial 1'), (25, 35, 'Turn dial 2'), (25, 38, 'Turn dial 3'), (25, 39, 'Turn dial 1'), (26, 36, 'Turn dial 2'), (26, 2, 'Turn dial 3'), (26, 40, 'Turn dial 1'), (27, 37, 'Turn dial 2'), (27, 40, 'Turn dial 3'), (27, 41, 'Turn dial 1'), (28, 38, 'Turn dial 2'), (28, 41, 'Turn dial 3'), (28, 42, 'Turn dial 1'), (29, 39, 'Turn dial 2'), (29, 42, 'Turn dial 3'), (29, 2, 'Turn dial 1'), (30, 40, 'Turn dial 2'), (30, 4, 'Turn dial 3'), (30, 43, 'Turn dial 1'), (31, 41, 'Turn dial 2'), (31, 43, 'Turn dial 3'), (31, 44, 'Turn dial 1'), (32, 42, 'Turn dial 2'), (32, 44, 'Turn dial 3'), (32, 3, 'Turn dial 1'), (33, 8, 'Turn dial 2'), (33, 45, 'Turn dial 3'), (33, 46, 'Turn dial 1'), (34, 9, 'Turn dial 2'), (34, 46, 'Turn dial 3'), (34, 47, 'Turn dial 1'), (35, 10, 'Turn dial 2'), (35, 47, 'Turn dial 3'), (35, 48, 'Turn dial 1'), (36, 45, 'Turn dial 2'), (36, 5, 'Turn dial 3'), (36, 49, 'Turn dial 1'), (37, 46, 'Turn dial 2'), (37, 49, 'Turn dial 3'), (37, 50, 'Turn dial 1'), (38, 47, 'Turn dial 2'), (38, 50, 'Turn dial 3'), (38, 51, 'Turn dial 1'), (39, 48, 'Turn dial 2'), (39, 51, 'Turn dial 3'), (39, 5, 'Turn dial 1'), (40, 49, 'Turn dial 2'), (40, 7, 'Turn dial 3'), (40, 52, 'Turn dial 1'), (41, 50, 'Turn dial 2'), (41, 52, 'Turn dial 3'), (41, 53, 'Turn dial 1'), (42, 51, 'Turn dial 2'), (42, 53, 'Turn dial 3'), (42, 6, 'Turn dial 1'), (43, 52, 'Turn dial 2'), (43, 10, 'Turn dial 3'), (43, 54, 'Turn dial 1'), (44, 53, 'Turn dial 2'), (44, 54, 'Turn dial 3'), (44, 8, 'Turn dial 1'), (45, 17, 'Turn dial 2'), (45, 11, 'Turn dial 3'), (45, 55, 'Turn dial 1'), (46, 18, 'Turn dial 2'), (46, 55, 'Turn dial 3'), (46, 56, 'Turn dial 1'), (47, 19, 'Turn dial 2'), (47, 56, 'Turn dial 3'), (47, 57, 'Turn dial 1'), (48, 20, 'Turn dial 2'), (48, 57, 'Turn dial 3'), (48, 11, 'Turn dial 1'), (49, 55, 'Turn dial 2'), (49, 13, 'Turn dial 3'), (49, 58, 'Turn dial 1'), (50, 56, 'Turn dial 2'), (50, 58, 'Turn dial 3'), (50, 59, 'Turn dial 1'), (51, 57, 'Turn dial 2'), (51, 59, 'Turn dial 3'), (51, 12, 'Turn dial 1'), (52, 58, 'Turn dial 2'), (52, 16, 'Turn dial 3'), (52, 60, 'Turn dial 1'), (53, 59, 'Turn dial 2'), (53, 60, 'Turn dial 3'), (53, 14, 'Turn dial 1'), (54, 60, 'Turn dial 2'), (54, 20, 'Turn dial 3'), (54, 17, 'Turn dial 1'), (55, 30, 'Turn dial 2'), (55, 22, 'Turn dial 3'), (55, 61, 'Turn dial 1'), (56, 31, 'Turn dial 2'), (56, 61, 'Turn dial 3'), (56, 62, 'Turn dial 1'), (57, 32, 'Turn dial 2'), (57, 62, 'Turn dial 3'), (57, 21, 'Turn dial 1'), (58, 61, 'Turn dial 2'), (58, 25, 'Turn dial 3'), (58, 63, 'Turn dial 1'), (59, 62, 'Turn dial 2'), (59, 63, 'Turn dial 3'), (59, 23, 'Turn dial 1'), (60, 63, 'Turn dial 2'), (60, 29, 'Turn dial 3'), (60, 26, 'Turn dial 1'), (61, 43, 'Turn dial 2'), (61, 35, 'Turn dial 3'), (61, 64, 'Turn dial 1'), (62, 44, 'Turn dial 2'), (62, 64, 'Turn dial 3'), (62, 33, 'Turn dial 1'), (63, 64, 'Turn dial 2'), (63, 39, 'Turn dial 3'), (63, 36, 'Turn dial 1'), (64, 54, 'Turn dial 2'), (64, 48, 'Turn dial 3'), (64, 45, 'Turn dial 1')], 0, [52])






game = FSA([21, 32, 59, 23, 34, 61, 71, 9, 36, 63, 11, 38, 49, 13, 24, 51, 15, 26, 53, 64, 1, 28, 55, 66, 3, 30, 41, 5, 16, 43, 69, 7, 18, 45, 56, 20, 47, 58, 60, 22, 33, 8, 35, 62, 72, 10, 37, 48, 12, 39, 50, 14, 25, 52, 0, 27, 54, 65, 2, 29, 40, 67, 4, 31, 42, 68, 6, 17, 44, 70, 19, 46, 57], None, [(0, 1, 'Swipe your student card'), (1, 2, 'Turn dial 2'), (1, 3, 'Turn dial 3'), (1, 4, 'Turn dial 1'), (2, 5, 'Turn dial 2'), (2, 6, 'Turn dial 3'), (2, 7, 'Turn dial 1'), (3, 6, 'Turn dial 2'), (3, 8, 'Turn dial 3'), (3, 9, 'Turn dial 1'), (4, 7, 'Turn dial 2'), (4, 9, 'Turn dial 3'), (4, 10, 'Turn dial 1'), (5, 11, 'Turn dial 2'), (5, 12, 'Turn dial 3'), (5, 13, 'Turn dial 1'), (6, 12, 'Turn dial 2'), (6, 14, 'Turn dial 3'), (6, 15, 'Turn dial 1'), (7, 13, 'Turn dial 2'), (7, 15, 'Turn dial 3'), (7, 16, 'Turn dial 1'), (8, 14, 'Turn dial 2'), (8, 17, 'Turn dial 3'), (8, 18, 'Turn dial 1'), (9, 15, 'Turn dial 2'), (9, 18, 'Turn dial 3'), (9, 19, 'Turn dial 1'), (10, 16, 'Turn dial 2'), (10, 19, 'Turn dial 3'), (10, 20, 'Turn dial 1'), (11, 1, 'Turn dial 2'), (11, 21, 'Turn dial 3'), (11, 22, 'Turn dial 1'), (12, 21, 'Turn dial 2'), (12, 23, 'Turn dial 3'), (12, 24, 'Turn dial 1'), (13, 22, 'Turn dial 2'), (13, 24, 'Turn dial 3'), (13, 25, 'Turn dial 1'), (14, 23, 'Turn dial 2'), (14, 26, 'Turn dial 3'), (14, 27, 'Turn dial 1'), (15, 24, 'Turn dial 2'), (15, 27, 'Turn dial 3'), (15, 28, 'Turn dial 1'), (16, 25, 'Turn dial 2'), (16, 28, 'Turn dial 3'), (16, 29, 'Turn dial 1'), (17, 26, 'Turn dial 2'), (17, 1, 'Turn dial 3'), (17, 30, 'Turn dial 1'), (18, 27, 'Turn dial 2'), (18, 30, 'Turn dial 3'), (18, 31, 'Turn dial 1'), (19, 28, 'Turn dial 2'), (19, 31, 'Turn dial 3'), (19, 32, 'Turn dial 1'), (20, 29, 'Turn dial 2'), (20, 32, 'Turn dial 3'), (20, 1, 'Turn dial 1'), (21, 3, 'Turn dial 2'), (21, 33, 'Turn dial 3'), (21, 34, 'Turn dial 1'), (22, 4, 'Turn dial 2'), (22, 34, 'Turn dial 3'), (22, 35, 'Turn dial 1'), (23, 33, 'Turn dial 2'), (23, 36, 'Turn dial 3'), (23, 37, 'Turn dial 1'), (24, 34, 'Turn dial 2'), (24, 37, 'Turn dial 3'), (24, 38, 'Turn dial 1'), (25, 35, 'Turn dial 2'), (25, 38, 'Turn dial 3'), (25, 39, 'Turn dial 1'), (26, 36, 'Turn dial 2'), (26, 2, 'Turn dial 3'), (26, 40, 'Turn dial 1'), (27, 37, 'Turn dial 2'), (27, 40, 'Turn dial 3'), (27, 41, 'Turn dial 1'), (28, 38, 'Turn dial 2'), (28, 41, 'Turn dial 3'), (28, 42, 'Turn dial 1'), (29, 39, 'Turn dial 2'), (29, 42, 'Turn dial 3'), (29, 2, 'Turn dial 1'), (30, 40, 'Turn dial 2'), (30, 4, 'Turn dial 3'), (30, 43, 'Turn dial 1'), (31, 41, 'Turn dial 2'), (31, 43, 'Turn dial 3'), (31, 44, 'Turn dial 1'), (32, 42, 'Turn dial 2'), (32, 44, 'Turn dial 3'), (32, 3, 'Turn dial 1'), (33, 8, 'Turn dial 2'), (33, 45, 'Turn dial 3'), (33, 46, 'Turn dial 1'), (34, 9, 'Turn dial 2'), (34, 46, 'Turn dial 3'), (34, 47, 'Turn dial 1'), (35, 10, 'Turn dial 2'), (35, 47, 'Turn dial 3'), (35, 48, 'Turn dial 1'), (36, 45, 'Turn dial 2'), (36, 5, 'Turn dial 3'), (36, 49, 'Turn dial 1'), (37, 46, 'Turn dial 2'), (37, 49, 'Turn dial 3'), (37, 50, 'Turn dial 1'), (38, 47, 'Turn dial 2'), (38, 50, 'Turn dial 3'), (38, 51, 'Turn dial 1'), (39, 48, 'Turn dial 2'), (39, 51, 'Turn dial 3'), (39, 5, 'Turn dial 1'), (40, 49, 'Turn dial 2'), (40, 7, 'Turn dial 3'), (40, 52, 'Turn dial 1'), (41, 50, 'Turn dial 2'), (41, 52, 'Turn dial 3'), (41, 53, 'Turn dial 1'), (42, 51, 'Turn dial 2'), (42, 53, 'Turn dial 3'), (42, 6, 'Turn dial 1'), (43, 52, 'Turn dial 2'), (43, 10, 'Turn dial 3'), (43, 54, 'Turn dial 1'), (44, 53, 'Turn dial 2'), (44, 54, 'Turn dial 3'), (44, 8, 'Turn dial 1'), (45, 17, 'Turn dial 2'), (45, 11, 'Turn dial 3'), (45, 55, 'Turn dial 1'), (46, 18, 'Turn dial 2'), (46, 55, 'Turn dial 3'), (46, 56, 'Turn dial 1'), (47, 19, 'Turn dial 2'), (47, 56, 'Turn dial 3'), (47, 57, 'Turn dial 1'), (48, 20, 'Turn dial 2'), (48, 57, 'Turn dial 3'), (48, 11, 'Turn dial 1'), (49, 55, 'Turn dial 2'), (49, 13, 'Turn dial 3'), (49, 58, 'Turn dial 1'), (50, 56, 'Turn dial 2'), (50, 58, 'Turn dial 3'), (50, 59, 'Turn dial 1'), (51, 57, 'Turn dial 2'), (51, 59, 'Turn dial 3'), (51, 12, 'Turn dial 1'), (52, 58, 'Turn dial 2'), (52, 16, 'Turn dial 3'), (52, 60, 'nextLevel'), (52, 61, 'Turn dial 1'), (53, 59, 'Turn dial 2'), (53, 61, 'Turn dial 3'), (53, 14, 'Turn dial 1'), (54, 61, 'Turn dial 2'), (54, 20, 'Turn dial 3'), (54, 17, 'Turn dial 1'), (55, 30, 'Turn dial 2'), (55, 22, 'Turn dial 3'), (55, 62, 'Turn dial 1'), (56, 31, 'Turn dial 2'), (56, 62, 'Turn dial 3'), (56, 63, 'Turn dial 1'), (57, 32, 'Turn dial 2'), (57, 63, 'Turn dial 3'), (57, 21, 'Turn dial 1'), (58, 62, 'Turn dial 2'), (58, 25, 'Turn dial 3'), (58, 64, 'Turn dial 1'), (59, 63, 'Turn dial 2'), (59, 64, 'Turn dial 3'), (59, 23, 'Turn dial 1'), (60, 65, 'Turn iPod on'), (60, 66, 'Enter AT'), (61, 64, 'Turn dial 2'), (61, 29, 'Turn dial 3'), (61, 26, 'Turn dial 1'), (62, 43, 'Turn dial 2'), (62, 35, 'Turn dial 3'), (62, 67, 'Turn dial 1'), (63, 44, 'Turn dial 2'), (63, 67, 'Turn dial 3'), (63, 33, 'Turn dial 1'), (64, 67, 'Turn dial 2'), (64, 39, 'Turn dial 3'), (64, 36, 'Turn dial 1'), (65, 60, 'Turn iPod off'), (65, 68, 'Enter AT'), (66, 60, 'Leave AT'), (66, 66, 'Have a coffee'), (66, 69, 'Pick up lecture notes'), (66, 68, 'Turn iPod on'), (67, 54, 'Turn dial 2'), (67, 48, 'Turn dial 3'), (67, 45, 'Turn dial 1'), (68, 65, 'Leave AT'), (68, 68, 'Have a coffee'), (68, 70, 'Pick up lecture notes'), (68, 66, 'Turn iPod off'), (69, 71, 'Leave AT'), (69, 66, 'Return lecture notes'), (69, 70, 'Turn iPod on'), (70, 72, 'Leave AT'), (70, 68, 'Return lecture notes'), (70, 69, 'Turn iPod off'), (71, 72, 'Turn iPod on'), (71, 69, 'Enter AT'), (72, 71, 'Turn iPod off'), (72, 70, 'Enter AT')], 0, [69, 71])










#    # 20050919: created
#    def checkQ5b(self):
#        boolean = equivalent(self,passport_in_box_plus_info_naive)
#        return boolean

#    # 20050919: created
#    def checkQ8a(self):
#        boolean = equivalent(self,passport_in_box_plus_info_proper)
#        return boolean


# 20060720 LK: created
def buildNewMetaData(oldMetaData,stateMap):
    newMetaData = {}
    for key in stateMap.keys():
        newIndex = stateMap[key]
        newInfo = []
        if type(key) == type((1,2)):
            for pieceOfState in key:
                i = oldMetaData.get(pieceOfState)
		if i:
		   for item in i:
	               bool = True
		       if item in newInfo:
		          bool = False
		       if bool:
                          newInfo = newInfo + [item]
        else:
            newInfo = oldMetaData.get(key)
        if newInfo:
           newMetaData[newIndex] = newInfo
    return newMetaData




def iter(char,n):
    i = 0
    string = ''
    while i < n:
       string = string + char
       i = i + 1
    return string
    

# 20050902  LK: created
# 20070925 LHK: forced dijkstra
# 20070925 LHK: expects a minimized FSA!
def dijkstra(inputFSA, startState, goalState):
    fsa = inputFSA
    costArray = len(fsa.states) * [None]
    costArray[startState] = 0
    visitedStates = [startState]
    index = 0
    while index < len(visitedStates) and costArray[goalState] == None:
        workingState = visitedStates[index]
        cost = costArray[workingState] + 1	
        for _, sink, label in fsa.transitionsFrom(workingState):
	    if costArray[sink] == None:
	        costArray[sink] = cost
                visitedStates = visitedStates + [sink]
        index = index + 1
    return costArray[goalState]
    

# HERE WAS I
# 20070925 LHK: unforced dijkstra
def dijkstra2(inputFSA, startState, goalState,size=None):
    fsa = inputFSA
    if not(size):
       size = len(fsa.states) - 1
#    print "SIZE: ", size
    sizes = []
    paths = [[startState]]
    stop = False
    while len(paths) > 0 and not(stop):
#       print "PATHS: ", paths
       newpaths = []
       for path in paths:
           if not(stop):
              for _, sink, label in fsa.transitionsFrom(path[-1]):
                  if not(stop):
                     if sink == goalState:
                        sizes = sizes + [len(path)]
#                        print "SOLUTION: ", path, sink 
                        if len(path) >= size:
                           stop = True
                     elif not(sink in path):
#                        print "PATH, SINK: ", path, sink 
                        newpaths = newpaths + [path + [sink]]
       paths = newpaths
    if sizes <> []:
       answer = max(sizes)
    else:
       answer = 0
    return answer 

           
    
    costArray[startState] = 0
    visitedStates = [startState]
    index = 0
    while index < len(visitedStates) and costArray[goalState] == None:
        workingState = visitedStates[index]
        cost = costArray[workingState] + 1	
        for _, sink, label in fsa.transitionsFrom(workingState):
	    if costArray[sink] == None:
	        costArray[sink] = cost
                visitedStates = visitedStates + [sink]
        index = index + 1
    return costArray[goalState]


#
# Recognizers for special-case languages
#

NULL_FSA = FSA([0], None, [], 0, [])
EMPTY_STRING_FSA = FSA([0], None, [], 0, [0])
UNIVERSAL_FSA = FSA([0], None, [(0, 0, ANY)], 0, [0])
#LFSA1 = FSA([0,1], None, [(0,1,'open'), (1,0,'close')], 0, [1])
#transitions14 = [ ( 0,  1, ANY)
#                , ( 1,  2, ANY)
#                , ( 2,  3, ANY)
#                , ( 3,  4, ANY)
#                , ( 4,  5, ANY)
#                , ( 5,  6, ANY)
#                , ( 6,  7, ANY)
#                , ( 7,  8, ANY)
#                , ( 8,  9, ANY)
#                , ( 9, 10, ANY)
#                , (10, 11, ANY)
#                , (11, 12, ANY)
#                , (12, 13, ANY)
#                , (13, 14, ANY)
#	        ]
#states14 = [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14]
#universal14 = FSA(states14, None, transitions14, 0, states14) 
#transitions16 = [ ( 0,  1, 'a')
#                , ( 1,  2, 'b')
#                , ( 2,  3, 'c')
#                , ( 3,  4, 'd')
#                , ( 4,  5, 'e')
#                , ( 5,  6, 'f')
#                , ( 6,  7, 'g')
#                , ( 7,  8, 'h')
#                , ( 8,  9, 'i')
#                , ( 9, 10, 'j')
#                , (10, 11, 'k')
#                , (11, 12, 'l')
#                , (12, 13, 'm')
#                , (13, 14, 'n')
#                , (14, 15, 'o')
#                , (15, 16, 'p')
#	        ]
#states16 = [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]
#alphabet16 = FSA(states16, None, transitions16, 0, [16]) 
#transitions15 = [ ( 0,  1, 'a')
#                , ( 1,  2, 'b')
#                , ( 2,  3, 'c')
#                , ( 3,  4, 'd')
#                , ( 4,  5, 'e')
#                , ( 5,  6, 'f')
#                , ( 6,  7, 'g')
#                , ( 7,  8, 'h')
#                , ( 8,  9, 'i')
#                , ( 9, 10, 'j')
#                , (10, 11, 'k')
#                , (11, 12, 'l')
#                , (12, 13, 'm')
#                , (13, 14, 'n')
#                , (14, 15, 'o')
#                , (13, 2, 'p')
#	        ]
#states15 = [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]
#sequence15 = FSA(states15, None, transitions15, 0, [5]) 
#LFSA2 = FSA([0,1],None,[(0,1,'open')],0,[1])
#LFSA3 = FSA([0,1,2],None,[(0,1,'open'),(0,2,'close')],0,[1])


#
# Utility functions
#

def removeDuplicates(sequence):
    result = []
    for x in sequence:
        if x not in result:
            result.append(x)
    return result

def toFSA(arg):
    if hasattr(arg, 'isFSA') and arg.isFSA:
        return arg
    else:
        return singleton(arg)

def view(str):
    import os, tempfile
    dotfile = tempfile.mktemp()
    psfile = tempfile.mktemp()
    open(dotfile, 'w').write(str)
    dotter = 'dot'
    psviewer = 'gv'
#    psoptions = '-antialias'
    psoptions = ''
    os.system("%s -Tps %s -o %s" % (dotter, dotfile, psfile))
    os.system("%s %s %s&" % (psviewer, psoptions, psfile))


#
# Operations on languages (via their recognizers)
# These generally return nondeterministic FSAs.
#



def closure2(arg):
    fsa = toFSA(arg)
    states, alpha, transitions, initial, finals = fsa.tuple()
    final = fsa.nextAvailableState()
#    transitions = transitions[:]
    for s in finals:
        transitions.append((s, final, None))
    transitions.append((initial, final, None))
    transitions.append((final, initial, None))
    return fsa.create(states + [final], alpha, transitions, initial, [final], fsa.metaData)


def closure(arg):
    return closure2(deepcopy(arg))

# 20060721 LK: broken?
def complement(arg):
    """Returns an FSA that accepts exactly those strings that the argument does
    not."""
    return toFSA(arg).complement()

# 20050802 LK: correct
def concatenation(a, *args):
    """Returns an FSA that accepts the language consisting of the concatenation
    of strings recognized by the arguments."""
    a = toFSA(a)
    for b in args:
        b = toFSA(b).sorted(a.nextAvailableState())
        states0, alpha0, transitions0, initial0, finals0 = a.tuple()
        states1, alpha1, transitions1, initial1, finals1 = b.tuple()
	newMetaData = deepcopy(a.metaData)
        newMetaData.update(b.metaData)
        a = a.create(states0 + states1, alpha0, transitions0 + transitions1 + map(lambda  s0, s1=initial1:(s0, s1, EPSILON), finals0), initial0, finals1,newMetaData)
    return a

#def containment(arg, occurrences=1):
#    """Returns an FSA that matches sequences containing at least _count_
#    occurrences
#    of _symbol_."""
#    arg = toFSA(arg)
#    fsa = closure(singleton(ANY))
#    for i in range(occurrences):
#        fsa = concatenation(fsa, concatenation(arg, closure(singleton(ANY))))
#    return fsa

# 20050802 LK: correct
def difference(a, b):
    """Returns an FSA that accepts those strings accepted by the first
    argument, but not the second."""
    boolean = intersection(a, complement(b))
    return boolean

# 20050802 LK: correct
def equivalent(a, b):
    """Return true ifff a and b accept the same language."""
    boolean = difference(a, b).isEmpty() and difference(b, a).isEmpty()
    return boolean

# 20050802 LK: depricated
# def oldIntersection(a, b):
#    """Returns the intersection of two FSAs"""
#    a, b = completion(a.determinized()), completion(b.determinized())
#    states0, alpha0, transitions0, start0, finals0 = a.tuple()
#    states1, alpha1, transitions1, start1, finals1 = b.tuple()
#    states = [(start0, start1)]
#    index = 0
#    transitions = []
#    arcMetadata = []
#    buildArcMetadata = a.hasArcMetadata() or b.hasArcMetadata()
#    while index < len(states):
#        state, index = states[index], index + 1
#        for sa0, sa1, la in a.transitionsFrom(state[0]):
#            for sb0, sb1, lb in b.transitionsFrom(state[1]):
#                label = labelIntersection(la, lb)
#                if label:
#                    s = (sa1, sb1)
#                    transition = (state, s, label)
#                    transitions.append(transition)
#                    if s not in states:
#                        states.append(s)
#                    if buildArcMetadata:
#                        if a.getArcMetadataFor((sa0, sa1, la)):
#                            arcMetadata.append((transition, a.getArcMetadataFor((sa0, sa1, la))))
#                        if b.getArcMetadataFor((sa0, sa1, la)):
#                            arcMetadata.append((transition, b.getArcMetadataFor((sa0, sa1, la))))
#    finals = filter(lambda (s0, s1), f0=finals0, f1=finals1:s0 in f0 and s1 in f1, states)
#    return a.create(states, alpha0, transitions, states[0], finals, arcMetadata).sorted()

# work in progress
# 20050802 LK: created
# 20050802 LK: *NOTE* test and extend to *labels1 and *labels2
def sync((fsa1,label1),(fsa2,label2)):
    """Returns the synchonisation of two FSAs on the specified labels"""
    a, b = a.determinized(), b.determinized()
    states0, alpha0, transitions0, start0, finals0 = a.tuple()
    states1, alpha1, transitions1, start1, finals1 = b.tuple()
    states = [(start0, start1)]
    index = 0
    transitions = []
    while index < len(states):
        state, index = states[index], index + 1
        for sa0, sa1, la in a.transitionsFrom(state[0]):
	    if la == label1:
	        for sb0, sb1, lb in b.transitionsTo(state[1]):
                    if lb == label2:
                        s = (sa1, sb0)
                        transition = (state, s, la)
                        transitions.append(transition)
                        if s not in states:
                            states.append(s)
 	    else:
                s = (sa1,state[1])
		transition = (state,s,la)
                transitions.append(transition)
                if s not in states:
                    states.append(s)
        for sb0, sb1, lb in a.transitionsFrom(state[1]):
	    if lb == label2:
	        for sa0, sa1, la in a.transitionsTo(state[0]):
                    if la == label1:
                        s = (sb1, sa0)
                        transition = (state, s, la)
                        transitions.append(transition)
                        if s not in states:
                            states.append(s)
 	    else:
                s = (sb1,state[0])
		transition = (state,s,lb)
                transitions.append(transition)
                if s not in states:
                    states.append(s)
    finals = filter(lambda (s0, s1), f0=finals0, f1=finals1:s0 in f0 and s1 in f1, states)
    return a.create(states, alpha0, transitions, states[0], finals).sorted()

# 20050802 LK: adapted from oldIntersection
# 20050901 LK: can deal with ANY now
# 20060720 LK: started adding stateInfo: TODO
def intersection(fsa_a, fsa_b):
    """Returns the intersection of two FSAs"""
    a, b = fsa_a.determinized(), fsa_b.determinized()
    states0, alpha0, transitions0, start0, finals0 = a.tuple()
    b = b.sorted(reduce(max, states0)+1)
    states1, alpha1, transitions1, start1, finals1 = b.tuple()
    states = [(start0, start1)]
    index = 0
    transitions = []
    while index < len(states):
        state, index = states[index], index + 1
        for sa0, sa1, la in a.transitionsFrom(state[0]):
            for sb0, sb1, lb in b.transitionsFrom(state[1]):
                label = labelIntersection(la,lb)
                if label:
                    s = (sa1, sb1)
                    transition = (state, s, label)
                    transitions.append(transition)
                    if s not in states:
                        states.append(s)
#                if la[0] == '~' and not lb == la[1:len(la)]:
#                    s = (sa1, sb1)
#                    transition = (state, s, lb)
#                    transitions.append(transition)
#                    if s not in states:
#                        states.append(s)
#                if lb[0] == '~' and not la == lb[1:len(lb)]:
#                    s = (sa1, sb1)
#                    transition = (state, s, la)
#                    transitions.append(transition)
#                    if s not in states:
#                        states.append(s)
#                if la == lb:
#                    s = (sa1, sb1)
#                    transition = (state, s, la)
#                    transitions.append(transition)
#                    if s not in states:
#                        states.append(s)
#                if la == ANY:
#                    s = (sa1, sb1)
#                    transition = (state, s, lb)
#                    transitions.append(transition)
#                    if s not in states:
#                        states.append(s)
#                if lb == ANY:
#                    s = (sa1, sb1)
#                    transition = (state, s, la)
#                    transitions.append(transition)
#                    if s not in states:
#                        states.append(s)
    finals = filter(lambda (s0, s1), f0=finals0, f1=finals1:s0 in f0 and s1 in f1, states)
    newMetaData = deepcopy(a.metaData)
    newMetaData.update(b.metaData)
    return a.create(states, alpha0, transitions, states[0], finals,newMetaData).sorted()

# 20050802 LK: adapted from intersection
#def interleave(a, b):
#    """Returns the interleave of two FSAs"""
#    a, b = a.determinized(), b.determinized()
#    states0, alpha0, transitions0, start0, finals0 = a.tuple()
#    b = b.sorted(reduce(max, states0)+1)
#    states1, alpha1, transitions1, start1, finals1 = b.tuple()
#    states = [(start0, start1)]
#    index = 0
#    transitions = []
#    while index < len(states):
#        state, index = states[index], index + 1
#        for sa0, sa1, la in a.transitionsFrom(state[0]):
#  	    s = (sa1, state[1])
#            transition = (state, s, la)
#            transitions.append(transition)
#            if s not in states:
#                states.append(s)
#        for sb0, sb1, lb in b.transitionsFrom(state[1]):
#            s = (state[0], sb1)
#            transition = (state, s, lb)
#            transitions.append(transition)
#            if s not in states:
#                states.append(s)
#    finals = filter(lambda (s0, s1), f0=finals0, f1=finals1:s0 in f0 and s1 in f1, states)
#    newMetaData = deepcopy(a.metaData)
#    newMetaData.update(b.metaData)
#    return a.create(states, alpha0, transitions, states[0], finals,newMetaData).sorted()

# 20050802 LK: adapted from intersection
def single_interleave(a, b):
    """Returns the interleave of two FSAs"""
    a, b = a.determinized(), b.determinized()
    states0, alpha0, transitions0, start0, finals0 = a.tuple()
    b = b.sorted(reduce(max, states0)+1)
    states1, alpha1, transitions1, start1, finals1 = b.tuple()
    states = [(start0, start1)]
    index = 0
    transitions = []
    while index < len(states):
        state, index = states[index], index + 1
        for sa0, sa1, la in a.transitionsFrom(state[0]):
  	    s = (sa1, state[1])
            transition = (state, s, la)
            transitions.append(transition)
            if s not in states:
                states.append(s)
        for sb0, sb1, lb in b.transitionsFrom(state[1]):
            s = (state[0], sb1)
            transition = (state, s, lb)
            transitions.append(transition)
            if s not in states:
                states.append(s)
    finals = filter(lambda (s0, s1), f0=finals0, f1=finals1:s0 in f0 and s1 in f1, states)
    newMetaData = deepcopy(a.metaData)
    newMetaData.update(b.metaData)
    return a.create(states, alpha0, transitions, states[0], finals,newMetaData).sorted()


# 20050802 LK: adapted from intersection
# 20060822 LK: multiple arguments
def interleave(arg1, *args):
    """Returns the interleave of two FSAs"""
    new = arg1.determinized()
    for b in args:
       new = single_interleave(new,b)
       new = new.minimized()
    return new


# 20050802 LK: adapted from intersection
# 20050804 LK: *BUG* finals1 ==> finals0
def preInterleave(a, b):
    """Returns the precondition interleave of two FSAs"""
    a, b = a.determinized(), b.determinized()
    states0, alpha0, transitions0, start0, finals0 = a.tuple()
    b = b.sorted(reduce(max, states0)+1)
    states1, alpha1, transitions1, start1, finals1 = b.tuple()
    states = [(start0, start1)]
    index = 0
    transitions = []
    while index < len(states):
        state, index = states[index], index + 1
        for sa0, sa1, la in a.transitionsFrom(state[0]):
  	    s = (sa1, state[1])
            transition = (state, s, la)
            transitions.append(transition)
            if s not in states:
                states.append(s)
        for sb0, sb1, lb in b.transitionsFrom(state[1]):
	    if state[0] in finals0:
                s = (state[0], sb1)
                transition = (state, s, lb)
                transitions.append(transition)
                if s not in states:
                    states.append(s)
    finals = filter(lambda (s0, s1), f0=finals0, f1=finals1:s1 in f1, states)
    newMetaData = deepcopy(a.metaData)
    newMetaData.update(b.metaData)
    return a.create(states, alpha0, transitions, states[0], finals,newMetaData).sorted()

def firmPreInterleave(a, b):
    """Returns the firm precondition interleave of two FSAs"""
    a, b = a.determinized(), b.determinized()
    states0, alpha0, transitions0, start0, finals0 = a.tuple()
    b = b.sorted(reduce(max, states0)+1)
    states1, alpha1, transitions1, start1, finals1 = b.tuple()
    states = [(start0, start1)]
    index = 0
    transitions = []
    while index < len(states):
        state, index = states[index], index + 1
        for sa0, sa1, la in a.transitionsFrom(state[0]):
  	    s = (sa1, state[1])
            transition = (state, s, la)
            transitions.append(transition)
            if s not in states:
                states.append(s)
        for sb0, sb1, lb in b.transitionsFrom(state[1]):
	    if state[0] in finals0:
                s = (state[0], sb1)
                transition = (state, s, lb)
                transitions.append(transition)
                if s not in states:
                    states.append(s)
    finals = filter(lambda (s0, s1), f0=finals0, f1=finals1:s1 in f1 and s0 in f0, states)
    newMetaData = deepcopy(a.metaData)
    newMetaData.update(b.metaData)
    return a.create(states, alpha0, transitions, states[0], finals,newMetaData).sorted()




# 20040802 LK: correct
def option(fsa):
    return union(fsa, EMPTY_STRING_FSA)


def reverse(fsa):
    states, alpha, transitions, initial, finals = fsa.tuple()
    newInitial = fsa.nextAvailableState()
    return fsa.create(states + [newInitial], alpha, map(lambda (s0, s1, l):(s1, s0, l), transitions) + map(lambda s1, s0=newInitial:(s0, s1, EPSILON), finals), [initial])


#20050916 LK: created
def composeLevels(*args):
   new = toFSA(args[0])
   connectingFSA = singleton('nextLevel')
   i = 1
   while i < len(args):
      fsa = concatenation(new, connectingFSA, toFSA(args[i]))
      tuple = fsa.tuple()
#     a.create(states, alpha0, transitions, states[0], finals,newMetaData)
      new = new.create(tuple[0],tuple[1],tuple[2],tuple[3],tuple[4],fsa.metaData).sorted()
#      new.states.sort()
      i = i + 1
   return new


def union(*args):
    initial, final = 1, 2
    states, transitions = [initial, final], []
#    arcMetadata = []
    metaData = {}
    for arg in args:
#        arg = toFSA(arg).sorted(reduce(max, states) + 1)
        arg = arg.sorted(reduce(max, states) + 1)
        states1, alpha1, transitions1, initial1, finals1 = arg.tuple()
        metaData.update(arg.metaData)
        states.extend(states1)
        transitions.extend(list(transitions1))
        transitions.append((initial, initial1, None))
        for s in finals1:
            transitions.append((s, final, None))
#        arcMetadata.extend(arg.getArcMetadata())
    if len(args):
        return toFSA(args[0]).create(states, alpha1, transitions, initial, [final], metaData)
    else:
        return FSA(states, alpha1, transitions, initial, [final])


#
# FSA Functions
#

def completion(fsa):
    """Returns an FSA that accepts the same language as the argument, but that
    lands in a defined state for every input."""
    states, alphabet, transitions, start, finals = fsa.tuple()
    transitions = transitions[:]
    sinkState = fsa.nextAvailableState()
    for state in states:
            labels = map(lambda (_, __, label):label, fsa.transitionsFrom(state))
            for label in complementLabelSet(labels, alphabet):
		    # 20050725 LK: *BUG* added extra set of brackets
                transitions.append((state, sinkState, label))
    if alphabet:
        transitions.extend(map(lambda symbol, s=sinkState:(s, s, symbol), alphabet))
    else:
        transitions.append((sinkState, sinkState, ANY))
    return fsa.copy(states + [sinkState], alphabet, transitions, start, finals, fsa.getArcMetadata())

def determinize(fsa):
    return fsa.determinized()

def minimize(fsa):
    return fsa.minimized()

def sort(fsa):
    return fsa.sorted()

def trim(fsa):
    return fsa.trimmed()


#
# Label operations
#

TRACE_LABEL_OPERATIONS = 0

def labelComplements(label, alphabet):
    complement = labelComplement(label, alphabet) or []
    if TRACE_LABEL_OPERATIONS:
        print 'complement(%s) = %s' % (label, complement)
    if  type(complement) != ListType:
        complement = [complement]
    return complement

def labelComplement(label, alphabet):
#    if type(label) == InstanceType:
#        return label.complement()
    if alphabet:
        return filter(lambda s, s1=label:s != s1, alphabet)
    elif label == ANY:
        return None
    else:
        return symbolComplement(label)

def labelIntersection(l1, l2):
    intersection = _labelIntersection(l1, l2)
    if TRACE_LABEL_OPERATIONS:
            print 'intersection(%s, %s) = %s' % (l1, l2, intersection)
    return intersection

# 20050922 LK: testing
def _labelIntersection(l1, l2):
    if l1 == l2:
        return l1
    #todo: is the following ever true
    elif not l1 or not l2:
        return None
    elif l1 == ANY:
        return l2
    elif l2 == ANY:
        return l1
#    elif type(l1) == InstanceType:
#        return l1.intersection(l2)
#    elif type(l2) == InstanceType:
#        return l2.intersection(l1)
    else:
        return symbolIntersection(l1, l2)

def labelString(label):
    return str(label)

def labelMatches(label, input):
#    print type(label), type(input)
    if type(label) == InstanceType and hasattr(label, 'matches'):
        return label.matches(input)
    else:
        return label == input


#
# Label set operations
#

TRACE_LABEL_SET_OPERATIONS = 0

def complementLabelSet(labels, alphabet=None):
    if not labels:
        return alphabet or [ANY]
    result = labelComplements(labels[0], alphabet)
    for label in labels[1:]:
        result = intersectLabelSets(labelComplements(label, alphabet), result)
    if TRACE_LABEL_SET_OPERATIONS:
        print 'complement(%s) = %s' % (labels, result)
    return result

def intersectLabelSets(alist, blist):
    clist = []
    for a in alist:
        for b in blist:
            c = labelIntersection(a, b)
            if c:
                clist.append(c)
    if TRACE_LABEL_SET_OPERATIONS:
        print 'intersection%s = %s' % ((alist, blist), clist)
    return clist

def unionLabelSets(alist, blist, alphabet=None):
    result = complementLabelSet(intersectLabelSets(complementLabelSet(alist, alphabet), complementLabelSet(blist, alphabet)), alphabet)
    if TRACE_LABEL_SET_OPERATIONS:
        print 'union%s = %s' % ((alist, blist), result)
    return result


#
# Transition and Label utility operations
#

TRACE_CONSOLIDATE_TRANSITIONS = 0
TRACE_CONSTRUCT_LABEL_MAP = 0

def consolidateTransitions(transitions):
    result = []
    for s0, s1 in removeDuplicates(map(lambda (s0, s1, _):(s0,s1), transitions)):
        labels = []
        for ss0, ss1, label in transitions:
            if ss0 == s0 and ss1 == s1:
                labels.append(label)
        if len(labels) > 1:
            reduced = reduce(unionLabelSets, map(lambda label:[label], labels))
            if TRACE_LABEL_OPERATIONS or TRACE_CONSOLIDATE_TRANSITIONS:
                print 'consolidateTransitions(%s) -> %s' % (labels, reduced)
            labels = reduced
        for label in labels:
            result.append((s0, s1, label))
    return result

def constructLabelMap(labels, alphabet, includeComplements=0):
    """Return a list of (newLabel, positives), where newLabel is an
    intersection of elements from labels and their complemens, and positives is
    a list of labels that have non-empty intersections with newLabel."""
    label = labels[0]
    #if hasattr(label, 'constructLabelMap'):
    #   return label.constructLabelMap(labels)
    complements = labelComplements(label, alphabet)
    if len(labels) == 1:
        results = [(label, [label])]
        if includeComplements:
            for complement in complements:
                results.append((complement, []))
        return results
    results = []
    for newLabel, positives in constructLabelMap(labels[1:], alphabet, includeComplements=1):
        newPositive = labelIntersection(label, newLabel)
        if newPositive:
            results.append((newPositive, [label] + positives))
        for complement in complements:
            if positives or includeComplements:
                newNegative = labelIntersection(complement, newLabel)
                if newNegative:
                    results.append((newNegative, positives))
    if TRACE_CONSTRUCT_LABEL_MAP:
        print 'consolidateTransitions(%s) -> %s' % (labels, results)
    return results


#
# Symbol operations
#

def symbolComplement(symbol):
    if '&' in symbol:
        import string
        return map(symbolComplement, string.split(symbol, '&'))
    elif symbol[0] == '~':
        return symbol[1:]
    else:
        return '~' + symbol

def symbolIntersection(s1, s2):
    import string
    set1 = string.split(s1, '&')
    set2 = string.split(s2, '&')
    for symbol in set1:
        if symbolComplement(symbol) in set2:
            return None
    for symbol in set2:
        if symbol not in set1:
            set1.append(symbol)
    nonNegatedSymbols = filter(lambda s:s[0] != '~', set1)
    if len(nonNegatedSymbols) > 1:
        return None
    if nonNegatedSymbols:
        return nonNegatedSymbols[0]
    set1.sort()
    return string.join(set1, '&')


#
# Construction from labels
#

# 20060720 LK: added stateInfo
class InvalidLabelException(Exception): pass
def singleton(symbol, info0=None, info1=None):
    alphabet = None
    stateInfo = {}
    if info0:
       stateInfo[0] = [info0]
    if info1:
       stateInfo[1] = [info1]
#    arcMetadata = None
    if type(symbol == str) and not ('~' in symbol or '&' in symbol):
        fsa = FSA([0,1], alphabet, [(0, 1, symbol)], 0, [1],stateInfo)
#        if arcMetadata:
#            fsa.setArcMetadataFor((0, 1, symbol), arcMetadata)
        fsa.label = `symbol`
        return fsa
    else:
        raise InvalidLabelException

def sequence(sequence, alphabet=None):
    fsa = reduce(concatenation, map(lambda label, alphabet=alphabet:singleton(label, alphabet), sequence), EMPTY_STRING_FSA)
    fsa.label = `sequence`
    return fsa


def deterministic(fsa):
    states = fsa.states
    bool = True
    for s in states:
       labels = []
       for _, sink, label in fsa.transitionsFrom(s):
          if (label in labels) or (label == None):
             bool = False
          else:
             labels.append(label)
        ### HERE WAS I !!!
    return bool

# @(*(+(0,@(1,0))),*1)

#
# Compiling Regular Expressions
#

#class parseError(Exception): pass
#def re2fsa(re):
#    fsa,rest = parseRE(re)
#    if rest == "":
#       return fsa
#    else: raise parseError

def bigUnion(fsas):
    fsa = fsas[0]
    i = 1
    while i < len(fsas):
       fsa = union(fsa,fsas[i])
       i = i + 1 
    return fsa

def bigConcat(fsas):
    fsa = fsas[0]
    i = 1
    while i < len(fsas):
       fsa = concatenation(fsa,fsas[i])
       i = i + 1 
    return fsa

#def parseRE(reg):
#    print "parseRE:"
#    print reg
#    import re
#    if reg[0] == '(':
#       fsa1,rest = parseRE(reg[1:])
#       fsa = closure(fsa1)
#    elif reg[0] == '+':
#       fsas,rest = parseREs(reg[1:])
#       fsa =  bigUnion(fsas)
#    elif reg[0] == '@':
#       fsas,rest = parseREs(reg[1:])
#       fsa = bigConcat(fsas)
#    elif reg[0] == '(':
#       if reg[len(reg)-1] == ')':
#          fsa,rest = parseRE(reg[:len(reg)-1][1:])
#       else: raise parseError       
#    else:
#       fsa,rest = parseLit(reg)
#    return fsa,rest

#def parseREs(reg):
#    print "parseREs:"
#    print reg
#    import re
#    if (reg[0] == '(' and reg[len(reg)-1] == ')'):
#       res = reg[:len(reg)-1][1:].split(',')
#       i = 0
#       fsas = []
#       rests = ""
#       while i < len(res):
#          fsa,rest = parseRE(res[i])
#          fsas = fsas + [fsa]
#          rests = rests + rest
#          i = i + 1
#    else: raise parseError
#    return fsas,rest

#def parseLit(reg):
#    print "parseLit:"
#    print reg
#    import re
#    if re.match("\w",reg) <> None:
#       fsa = singleton(reg[0])
#       rest = reg[1:]
#    else: raise parseError
#    return fsa,rest
    
def newLabel(s1,s2):
    string = ""
    for s in s1,s2:
       if s == None:
          s = "$"
       if len(s) <> 1:
          s = "(" + s + ")"
       string = string + s
    return s


def label2string(label):
    if label == None:
       label = '$'
    return label

# NB. this only works for cancatenation labels!
def label2string_empty(label):
    if label == None:
       label = ""
    return label



def myTransitionsTo(fsa,state):
    trans = []
    for source, sink, label in fsa.transitions:
       if sink == state:
          trans.append((source, sink, label))
    return trans
          
def myTransitionsFrom(fsa,state):
    trans = []
    for source, sink, label in fsa.transitions:
       if source == state:
          trans.append((source, sink, label))
    return trans
          

def fsa2re(fsa):
#    print "ORIGINAL: " + str(fsa.tuple())
#    fsa.view()
    # create a new initial state
    newInitialState = fsa.nextAvailableState()
    fsa.states.append(newInitialState)
    # create a new final state
    newFinalState = fsa.nextAvailableState()
    fsa.states.append(newFinalState)
    # connect the new initial state    
    fsa.transitions.append((newInitialState,fsa.initialState,None))
    fsa.initialState = newInitialState
    # connect the new final state
    for state in fsa.finalStates:
       fsa.transitions.append((state,newFinalState,None))
    fsa.finalStates = [newFinalState]
#    print fsa.tuple()
    # replace all duplicate arrows by a single one
    for source1, sink1, label1 in fsa.transitions:
       for source2, sink2, label2 in fsa.transitions:
           if source1 == source2 and sink1 == sink2 and label1 <> label2:
              fsa.transitions.remove((source1, sink1, label1))
              fsa.transitions.remove((source2, sink2, label2))
              label = newLabel(label1,label2)
              fsa.transitions.append((source1, sink1, label))
#    print "AFTER INITIALISATION: " + str(fsa.tuple())
    # remove states 1-by-1
    i1 = 0
    while i1 < len(fsa.states):
       state = fsa.states[i1]
#       print state
       if state <> newFinalState and state <> newInitialState:
          qis = []
          qrips = []
          qjs = []
          qos = []
          transitionsTo = myTransitionsTo(fsa,state)
#          print fsa.tuple()
#          print transitionsTo
          i2 = 0
          while i2 < len(transitionsTo): 
              source, sink, label = transitionsTo[i2]
#              print source
#              print sink
#              print label
              if source == sink:
                 qrips.append(label)
              else:
                 qis.append((source, label))
#              print fsa.transitions
              fsa.transitions.remove((source, sink, label))
#              print fsa.tuple()
              i2 = i2 + 1
#          print "QRIPS: " + str(qrips)
          for source, sink, label in myTransitionsFrom(fsa,state):
              if source <> sink:
                 qjs.append((sink, label))
              fsa.transitions.remove((source, sink, label))
#              print fsa.tuple()
          for source, l1 in qis:
             for sink, l2 in qjs:
                (bool,original) = findLabel(fsa.transitions,source,sink)
#                print "ORIGINAL: " + str(original)
                label = makeLabel(l1,qrips,l2,(bool,original))
#                print "LABEL: " + str(label)
                if bool:
                   fsa.transitions.remove((source,sink,original))
                fsa.transitions.append((source,sink,label))
#                print fsa.tuple()
#       print fsa.tuple()
#       fsa.view()
       i1 = i1 + 1
    fsa.states = [newInitialState,newFinalState]
    if len(fsa.transitions) == 1:
       [(_,_,re)] = fsa.transitions
       re = str(re)
    else:
       re = None
    return re

def makeLabel(l1,qrips,l2,(bool,original)):
    if bool and original == None:
       original = '$'
    if qrips == []:
       qrip = ""
    else:
       qrip = "(" + label2string(qrips[0]) + ")*"
    middle = label2string(l1) + qrip + label2string(l2)
    if len(middle) == 0:
       main = ""
    elif len(middle) == 1:
       main = middle
    else:
#       main = "(" + middle + ")"
       main = middle
    if original == None and main == "":
       label = None
    elif original == None and main <> "":
       label = main
    elif main == "":
       label = original
    else:
       label = "(" + original + "+" + main + ")"
    return label

    
def findLabel(trans,source,sink):
    label = (False,None)
    for s1, s2, l in trans:
       if source == s1 and sink == s2:
          label = (True,l)
    return label



def tokenize(re):
    tokens = []
    i = 0
    depth = 0
    while i < len(re):
       if re[i] == '(':
          if depth <> 0:
             lastIndex = len(tokens) - 1
             lastToken = tokens[lastIndex] + "("
             tokens[lastIndex] = lastToken
          else:
             tokens = tokens + [""]
          depth = depth + 1
       elif re[i] == ')':
          if depth <> 1 and depth <> 0:
             lastIndex = len(tokens) - 1
             lastToken = tokens[lastIndex] + ")"
             tokens[lastIndex] = lastToken
          depth = depth - 1
       else:
          if depth == 0:
             tokens = tokens + [re[i]]
          else:
             lastIndex = len(tokens) - 1
             lastToken = tokens[lastIndex] + re[i]
             tokens[lastIndex] = lastToken
       i = i + 1 
    return tokens

def parseRE(re):
    import string
    if re <> None:
       s = re
       s = string.replace(s, ' ', '')
       fsa = token2fsa(s)
    else:
       fsa = EMPTY_STRING_FSA
    return fsa

def token2fsa(token):
    epsilon = '$'
    tokens = tokenize(token)
    if len(tokens) == 1:
       if len(tokens[0]) == 1:
          if tokens[0] == epsilon:
             fsa = EMPTY_STRING_FSA
          else:
             fsa = singleton(tokens[0])
       else:
          fsa = token2fsa(tokens[0])
    else:
       fsa = build(tokens)
    return fsa
    
def build(tokens):
    fsas = [EMPTY_STRING_FSA]
    fsaIndex = 0
    i = len(tokens) - 1
    while i >= 0:
       if tokens[i] == '*':
          fsa = closure(token2fsa(tokens[i-1]))
          fsas[fsaIndex] = concatenation(fsa,fsas[fsaIndex])
          i = i - 1
       elif tokens[i] == '}':
          fsa = FSA([0],None,[],0,[])
          fsas[fsaIndex] = concatenation(fsa,fsas[fsaIndex])
          i = i - 1
       elif tokens[i] == '+':
          fsaIndex = fsaIndex + 1
          fsas = fsas + [EMPTY_STRING_FSA]
       elif tokens[i] == '|':
          fsaIndex = fsaIndex + 1
          fsas = fsas + [EMPTY_STRING_FSA]
       else:
          fsa = token2fsa(tokens[i])
          fsas[fsaIndex] = concatenation(fsa,fsas[fsaIndex])    
       i = i - 1
    fsa = bigUnion(fsas)
    return fsa



# game = (fsa,intro,end,prompt)
def play(game):
    try: import readline
    except: None
#    global state
    gameFSA = minimize(game[0])
    stateInfo = gameFSA.metaData
#    print stateInfo
    intro = game[1]
    end = game[2]
    prompt = game[3]
    print intro
    state = gameFSA.initialState
    stop = False
    while (not state in gameFSA.finalStates) and (stop == False):
        print "\n========== INFORMATION ==========\n"
        if state in stateInfo.keys():
           for line in stateInfo[state]:
              print line
        print "\n======= AVAILABLE ACTIONS =======\n"
        actions = gameFSA.nextInputs(state);
        for action in actions:
            print action          
        command = raw_input('\n' + prompt)
        if command == "Goodbye!":
	    stop = True
        # show the info of the current state
#        elif command == "info":
#            lines = stateInfo[state]
#            for line in lines:
#               print line
        # show the available actions
#        elif command == "help":
#            actions = gameFSA.nextInputs(state);
#            for action in actions:
#                print action          
        else:    
	    stateOption = gameFSA.nextState1(state,command)
            if not(stateOption):
                print "I don't understand."
	    else:
	        state = stateOption
                print "OK  :)"
    if stop == False:
	print end
    else: print "Goodbye!"

# [v] interleave
# [v] preInterleave
# [v] concatenation
# [v] closure
# [v] composeLevels(levels)
# [v] union(fsas)
# [v] option(a)
# [delete?] partialPreInterleave(a,b,s)
# [delete?,broken?] reverse(a)
# [v] intersection(a, b)
# [delete?] difference(a, b)
# [delete?] complement(a)
	
