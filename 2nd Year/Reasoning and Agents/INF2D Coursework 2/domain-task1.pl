% ---------------------------------------------------------------------
%  ----- Informatics 2D - 2010/11 - Second Assignment - Planning -----
% ---------------------------------------------------------------------
%
% Write here you matriculation number (only - your name is not needed)
% Matriculation Number: s0943941
%
%
% ------------------------- Domain Definition -------------------------
% This file describes a planning domain: a set of predicates and
% fluents that describe the state of the system, a set of actions and
% the axioms related to them. More than one problem can use the same
% domain definition, and therefore include this file


% --- Cross-file definitions ------------------------------------------
% marks the predicates whose definition is spread across two or more
% files
%
% :- multifile name/#, name/#, name/#, ...

:- multifile position/2, cleaned/2.



% --- Primitive control actions ---------------------------------------
% this section defines the name and the number of parameters of the
% actions available to the planner
%
% primitive_action( dosomething(_,_) ).	% underscore means `anything'

primitive_action( dust(_) ).
primitive_action( move(_,_) ).
primitive_action( elevator(_,_,_) ).


% --- Precondition for primitive actions ------------------------------
% describe when an action can be carried out, in a generic situation S
%
% poss( doSomething(...), S ) :- preconditions(..., S).

poss( move(Room1, Room2), S) :- 
	position(Room1, S), adjacent(Room1, Room2).

poss( elevator(Lift1, Lift2, Direction), S) :- 
	position(Lift1, S), vertical(Lift1, Lift2, Direction).

poss( dust(Room1), S) :- 
	position(Room1, S), not(cleaned(Room1, S)), not(lift(Room1)).



% --- Successor state axioms ------------------------------------------
% describe the value of fluent based on the previous situation and the
% action chosen for the plan. 
%
% fluent(..., result(A,S)) :- positive; previous-state, not(negative)

position(Room2, result(A,S)) :- 
	(A = move(Room1, Room2) ; A = elevator(Room1,Room2, _)) ;
	position(Room2, S), not(A = move(Room2, _) ; A = elevator(Room2, _,_)).

cleaned(Room1, result(A,S)) :- 
	A = dust(Room1) ;  
	cleaned(Room1, S).


% ---------------------------------------------------------------------
% ---------------------------------------------------------------------
