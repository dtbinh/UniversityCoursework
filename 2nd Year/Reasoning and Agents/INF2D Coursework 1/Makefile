#
# Effective Propositional Inference
# Informatics 2D
# Assignment 1
# 2010-2011
#
# Makefile
#

OBJS 	= Assignment.o SatParser.o 
INTERS  = Assignment.hi SatParser.hi 
SOURCE	= Assignment.hs SatParser.hs 
CC	= ghc
FLAGS   = -package parsec -c
OFLAGS  = -package parsec

%.o: %.hs
	$(CC) $(FLAGS) $<

%.hi: %.hs
	$(CC) $(FLAGS) $<

walksat: $(OBJS) $(INTERS) WalkSat.o WalkSat.hi
	$(CC) $(OFLAGS) $(OBJS) WalkSat.o -o walksat

dpll: $(OBJS) $(INTERS) Dpll.o Dpll.hi
	$(CC) $(OFLAGS) $(OBJS) Dpll.o -o dpll

dpll2: $(OBJS) $(INTERS) Dpll2.o Dpll2.hi
	$(CC) $(OFLAGS) $(OBJS) Dpll2.o -o dpll2

clean:
	rm -f $(OBJS) $(INTERS) WalkSat.o WalkSat.hi Dpll.o Dpll.hi Dpll2.o Dpll2.hi

cleanall: 
	rm -f $(OBJS) $(INTERS) WalkSat.o WalkSat.hi Dpll.o Dpll.hi Dpll2.o Dpll2.hi
	rm -f walksat dpll dpll2

count:
	wc $(SOURCE)

