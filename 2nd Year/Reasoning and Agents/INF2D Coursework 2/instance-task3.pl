% ---------------------------------------------------------------------
%  ----- Informatics 2D - 2010/11 - Second Assignment - Planning -----
% ---------------------------------------------------------------------
%
% Write here you matriculation number (only - your name is not needed)
% Matriculation Number: s0943941
%
%
% ------------------------- Problem Instance --------------------------
% This file is a template for a problem instance: the definition of an
% initial state and of a goal. 

% debug(on).	% need additional debug information at runtime?



% --- Load domain definitions from an external file -------------------

:- [domain-task1].		% Replace with the domain for this problem


% --- Definition of the initial state ---------------------------------
% Define the rooms
room(r1).
room(r2).
room(r3).
room(r4).
room(r5).
room(r6).
room(r7).

%Define directions for lifts
direction(up).
direction(down).

%Define the lifts
lift(lg).
lift(l1).
lift(l2).

%Create paths between rooms that function both ways
adjacent(r1,l2).
adjacent(l2, r1).

adjacent(r1,r2).
adjacent(r2,r1).

adjacent(r3,r5).
adjacent(r5,r3).

adjacent(r4,r5).
adjacent(r5,r4).

adjacent(l1,r5).
adjacent(r5,l1).

adjacent(r6,r7).
adjacent(r7,r6).

adjacent(r6,lg).
adjacent(lg,r6).

adjacent(lg,r7).
adjacent(r7,lg).

%Create elevevators between rooms that function one way
vertical(lg,l1,up).
vertical(l1,lg,down).
vertical(l1,l2,up).
vertical(l2,l1,down).

%Initial state
position(r1, s0).


% --- Goal condition that the planner will try to reach ---------------

goal(S) :- cleaned(r5, S), position(r1, S).				


% ---------------------------------------------------------------------
% ---------------------------------------------------------------------
