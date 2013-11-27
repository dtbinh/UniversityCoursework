-- Effective Propositional Inference
-- Informatics 2D
-- Assignment 1
-- 2010-2011
--
-- MATRIC NUMBER HERE: s0943941
-- 
-- Please remember to comment ALL your functions.

---------------------------------------------------------------------------------
---------------------------------------------------------------------------------

-- Module declaration and imports
-- DO NOT EDIT THIS PART --

module Assignment where

import System.Random
import Data.Maybe
import Data.List

---------------------------------------------------------------------------------

-- Type declarations 
-- DO NOT EDIT THIS PART --

type Atom = String
type Literal = (Bool,Atom)
type Clause = [Literal]
type Formula = [Clause]
type Model = [(Atom, Bool)]
type Node = (Formula, ([Atom], Model))

---------------------------------------------------------------------------------
---------------------------------------------------------------------------------

-- PART 4.1 

atomsClause :: Clause -> [Atom]
atomsClause [] = []
atomsClause ((x,y):xs) = nub (y : (atomsClause xs))

--recursively takes every second element from the Literal tuple and concats it into a list

atoms :: Formula -> [Atom]
atoms xs = nub (concat (map atomsClause xs))

--applies atomsClause to every clause in the formula

isLiteral :: Literal -> Clause -> Bool
isLiteral x [] = False
isLiteral x (y:ys) = (if(fst(x)==fst(y)&&snd(x)==snd(y)) then True else False) || isLiteral x ys

--Checks to see if the literal compares with the first literal in clause, and ors the entire statement together

flipSymbol :: Model -> Atom -> Model
flipSymbol [] _ = []
flipSymbol (x:xs) a 
		| fst(x)==a = (fst(x),not(snd(x))) : flipSymbol xs a
		| otherwise = x : flipSymbol xs a

--Checks if atom is the part of the model. If so, it changes the Boolean value and concats it with the rest of the list. 

---------------------------------------------------------------------------------

-- PART 4.2 

assign :: (Atom,Bool) -> Formula  -> Formula
assign (a,b) x = map (remove (not(b),a)) (filter (\y -> not(isLiteral (b,a) y)) x)

--First filters from the formula all the nonliterals, then removes the negation with a map.


---------------------------------------------------------------------------------
---------------------------------------------------------------------------------

-- Given functions
-- DO NOT EDIT THIS PART --

-- The WalkSat algorithm


remove :: (Eq a) => a -> [a] -> [a]
remove x list = filter (x /=) list

neg :: Literal -> Literal
neg (x,y) = (not x,y)

checkFormula :: Formula -> Maybe Bool
checkFormula formula = if (formula == [])
                      then Just True
                      else if (elem [] formula)
                       then Just False
                       else Nothing

randomChoice :: RandomGen g => g -> Float -> Float -> (Bool,g)
randomChoice gen probability max = (value <= probability,newgen)
                                where (value,newgen) = randomR (0.0::Float,max) gen

randomElem :: RandomGen g => g -> [a] -> (a,g)
randomElem gen list = (list !! v,newgen)
                where (v,newgen) = randomR (0,(length list) -1) gen

randomAssign :: RandomGen g => g -> [Atom] -> ([(Atom,Bool)],g)
randomAssign gen [] = ([],gen)
randomAssign gen (x:xs) = (((x,y):ys),newgen)
                where (y,gen') = randomR (False,True) gen
                      (ys,newgen) = randomAssign gen' xs

assignModel :: Model -> Formula -> Formula
assignModel [] formula = formula
assignModel (x:xs) formula = assign x (assignModel xs formula)

satisfies :: Model -> Formula -> Bool
satisfies _ []  = True
satisfies model formula = case res of
                        Nothing -> False
                        Just x -> x
                   where res = checkFormula (assignModel model formula)

satisfiesClause :: Model -> Clause -> Bool
satisfiesClause model clause = satisfies model [clause]

falseClause :: Model -> Clause -> Bool
falseClause assignments clause = not $ satisfiesClause assignments clause

falseClauses :: Formula -> Model -> [Clause]
falseClauses formula assignments = filter (falseClause assignments) formula

satisfiedClausesCount :: Formula -> Model -> Int
satisfiedClausesCount clauses model = length (filter (satisfiesClause model) clauses)

maxSatClauses :: Model -> Formula -> (Atom,Int) -> [Atom] -> Atom
maxSatClauses _ _ (maxatom,max) [] = maxatom
maxSatClauses model formula (maxatom,max) (x:xs) = if (sats > max)
					then maxSatClauses model formula (x,sats) xs
				 	else maxSatClauses model formula (maxatom,max) xs
					where sats = satisfiedClausesCount formula (flipSymbol model x)

walkSatRecursion :: RandomGen g => g -> Formula -> Model -> Float -> Int -> (Maybe (Model,Int),g)
walkSatRecursion gen formula model _ 0 = if (satisfies model formula) 
					then (Just (model,0),gen)
	                                else (Nothing,gen)
walkSatRecursion gen formula model prob n = if (satisfies model formula)
                                            then (Just (model,n),gen)
                                            else if (rch)
                                            then (walkSatRecursion gen3 formula flipRandom prob (n-1))
                                            else (walkSatRecursion gen3 formula flipMaxSat prob (n-1))
                           where (rch,gen1) = randomChoice gen prob (1.0::Float)
                                 (clause,gen2) = randomElem gen1 (falseClauses formula model)
                                 atms = atomsClause clause
                                 (ratom,gen3) = randomElem gen2 atms
                                 matom = maxSatClauses model formula (ratom,satisfiedClausesCount formula flipRandom) atms
                                 flipRandom = flipSymbol model ratom
                                 flipMaxSat = flipSymbol model matom

walkSat :: Formula -> Float -> Int -> IO (Maybe (Model,Int))
walkSat formula prob n = do
			gen <- getStdGen
			let (rassign,gen') = (randomAssign gen (atoms formula))
			let (res,gen'') = walkSatRecursion gen' formula rassign prob n
			setStdGen (gen'')
			putStrLn $ show res
			return res


---------------------------------------------------------------------------------
---------------------------------------------------------------------------------

-- PART 5.1 

-- PART 5.1.1

removeTautologies :: Formula -> Formula
removeTautologies x = filter (checkTautologies) x

--Filters the tautologies using checkTautologies. 

checkTautologies :: Clause -> Bool
checkTautologies x 	| (intersect (negateClause x) x /= []) = False
			| otherwise = True

-- The intersection of a negation with a clause will give only the elements that appear in both (P,¬P,Q) intersected with its negation (¬P,P,¬Q) gives (¬P,P) -> The tautologies. If the intersection between a clause and its negation is not empty, then it has a tautology within it.

negateClause :: Clause -> Clause
negateClause [] = []
negateClause (x:xs) = (not(fst(x)),snd(x)) : negateClause xs

--negates the clause (could potentially use map flip if you feel like changing things) This allows you to compare the opposites. (such as if its True P and False P, the negation is False P and True P => The intersection of those reveals the tautologies).

-- PART 5.1.2

pureLiteralDeletion :: Formula -> Formula
pureLiteralDeletion x = filter (removeClause (isolateLiteral x)) x

--Applies the functions below to each clause of the formula, checking each against the pure literals of the entire formula. 

isolateLiteral :: Formula -> Clause
isolateLiteral x = (nub (concat x)) \\ (intersect (nub (concat x)) (negateClause(nub(concat x))))

--Finds Literals by taking every literal and making it into a single clause, and then eliminating the ones that are tautologies throughout the entire clause (ones that appear more than once throughout)

removeClause :: Clause -> Clause -> Bool
removeClause [] y = True
removeClause (x:xs) y	| x `elem` y = False
			| otherwise = removeClause xs y

--Takes a list of literals (in this case, pure literals) and compares each of them to another list of literals. Gives [] if the pure literal is found to be within the function and leaving it alone if not. 

-- PART 5.1.3

propagateUnits :: Formula -> Formula
propagateUnits x | checkLength(x) == [] = x
		 | otherwise = propagateUnits[removeUnit(negateClause (head(checkLength x))) y | y <- filter (removeClause (head(checkLength x))) x]

--Similar to literal deletion above, this function first checks to see if there are any individual units. If so (/=[]), it first filters each of the literals from the clauses they contain, and then sends that into removeUnit which removes the negation of the literals from their clauses (without affecting the rest of the clause). Then it calls propagateUnits on it again to make sure that the function is clear of Units. 

checkLength :: Formula -> Formula
checkLength x = [xs | xs<-x, length xs==1]

--This gives a list of Unit Clauses within a Formula (where the length is 1)

removeUnit :: Clause -> Clause -> Clause
removeUnit [] y = y
removeUnit (x:xs) y 	| x `elem` y = remove x y
			| otherwise = removeUnit xs y

--Checks a clause to see if an element is in it, and if it is, removes it from the clause, unlike removeClause, which eliminates the entire Clause from the formula.

---------------------------------------------------------------------------------

-- PART 5.2

update :: Node -> [Node]
update (f,(a,m)) = [((assign (chooseAtom(a),True) f), ((tail(a)),(m ++ [(chooseAtom(a),True)])))] ++ [((assign (chooseAtom(a),False) f), ((tail(a)),(m ++ [(chooseAtom(a),False)])))]

--Assigns the first atom from the list to True on the formula, with the assign function. It takes the tail of the atoms list (having dealt with the first one) and updates the model with (first atom, True). The false version of this is then concatenated to the end of it to form a list. 

search :: (Node -> [Node]) -> [Node] -> Int -> (Bool, Int)
search updateFunction [] count = (False, count)
search updateFunction ((f,(a,m)):nodes) count 	| checkFormula form == Just True = (True,count)
						| checkFormula form == Just False = search updateFunction nodes count
						| otherwise = search updateFunction (updateFunction(form,(a,m)) ++ nodes) (count+1)
	where form = pureLiteralDeletion(removeTautologies(propagateUnits(f)))

--form provides the simplification stage of the dpll.
--updateFunction is a recursive function which checks the simplified formula against the Maybe Monad. If True, then the formula has been satisfied. If False, it ignores that node and moves onto the next node. If it is neither, it applies updateFunction to the current node and adds 1 to the count. If it reaches the end, and its an empty list, then the entire formula cannot be solved and thus returns false. 

chooseAtom :: [Atom] -> Atom
chooseAtom list = (head list)

dpll :: Formula -> (Bool, Int)
dpll formula = search update [(formula, (atoms formula, []))] 0


----------------------------------------------------------------------

-- PART 6

atoms2 :: Formula -> [Atom]
atoms2 xs = concat (map atomsClause xs)

--Gives list of atoms without removing duplicates (so they can be counted) and cross referenced against the list of atoms appearing only once.

countAtoms :: [Atom] -> [Atom] -> [(Int, Atom)]
countAtoms x y = [(length(intersect (x) [ys]),ys) |  ys <- y]

--Counts the number of atoms in the formula, and gives them as a tuple where int is the number of times a give atom (Atom) appears. This is done by taking the list from atoms2(Formula) and intersecting it with the first element of atoms(Formula). This only shows a single atom, which is then counted by length. This result is paired with the atom. The process is repeated for all the atoms in the formula.

findlength :: [Atom] -> [Atom] -> Int
findlength x y = maximum[length(intersect (x) [ys]) |  ys <- y]

--Finds the occurance of the most occuring atom in the list (by taking the max of a list of integers of how often it appears)

findMostAtom :: Int -> [(Int,Atom)] -> Atom
findMostAtom x (y:ys) 	| x == fst(y) = snd(y)
			| otherwise = findMostAtom x ys

-- Compares countAtoms against findLength to give the Atom that occurs the most frequently

formulaMostAtom :: Formula -> Atom
formulaMostAtom x = findMostAtom (findlength (atoms2 x) (atoms x)) (countAtoms (atoms2 x) (atoms x))

-- Combines everything above into a single step to find the Atom that appears the most. Therefore, when the update function assigns True, it will assign it to the atom that appears the most, and therefore will have the most impact. 

update2 :: Node -> [Node]
update2 (f,(a,m)) = [((assign (formulaMostAtom(f),True) f), ((remove (formulaMostAtom(f)) a),(m ++ [(formulaMostAtom(f),True)])))] ++ [((assign (formulaMostAtom(f),False) f), ((remove (formulaMostAtom(f)) a),(m ++ [(formulaMostAtom(f),False)])))]

-- Updated function which assigns the most appeared atom in the formula True, then removes it from the list of atoms and adds it to the model. This is concatenated to the result of false. 

dpllHeuristic :: Formula -> (Bool, Int)
dpllHeuristic formula = search update2 [(formula, (atoms formula, []))] 0


---------------------------------------------------------------------------------

-- Examples of type Formula which you can use to test the functions you develop

x = [[(True,"p"), (True,"q")], [(True,"p"), (False,"p")], [(True,"q")]]
y = [[(True,"p"), (True,"q")], [(True,"p"), (True,"q"), (True,"z")], [(False,"z"), (False,"w"), (True,"k")], [(False,"z"), (False,"w"), (True,"s")], [(True,"p"), (False,"q")]]
z = [[(True,"k"), (False,"g"), (True,"t")], [(False,"k"), (True,"w"), (True,"z")], [(True,"t"), (True,"p")], [(False,"p")], [(True,"z"), (True,"k"), (False,"w")], [(False,"z"), (False,"k"), (False,"w")], [(False,"z"), (True,"k"), (True,"w")]]
w = [[(True,"p")], [(False,"p")]]
v = [[(True,"p"), (False,"q")], [(True,"p"), (True,"q")]]
q = [[(True,"p"), (False,"q")], [(True,"q")]]
mytest = [(True,"p"), (True,"q"),(True,"r"),(False,"s"),(False,"r")]
mytest2 = [("p", True),("q", False)]
mytest3 = [[(True,"p"),(True,"q"),(True,"r"),(False,"t")],[(True,"p"),(True,"q"),(False,"r")],[(False,"q"),(True,"r"),(True,"k")],[(True,"t")],[(False,"k")]]
mytest4 = [(True,"p"), (True,"q"),(True,"t"),(False,"s"),(False,"r")]
mytest5 = [[(True,"t"),(False,"s")],[(True,"w"),(False,"w"),(False,"r"),(False,"p")],[(False,"t"),(True,"r")],[(False,"q"),(True,"r"),(True,"p"),(True,"s")],[(True,"s")]]
mytest6 = [[(False,"p")],[(True,"p")]]
mytest7 = [[(True,"q")],[(True,"r"),(True,"s")],[(False,"q")],[(True,"k"),(True,"r"),(True,"s")],[(True,"k"),(True,"r"),(True,"s"),(True,"q")]]
mytest8 = [[(True,"p")],[(True,"q"),(True,"p")],[(True,"t")]]
---------------------------------------------------------------------------------

-- PART 7

-- TABLE 1 : WalkSat
{-
         |  p=0  | p=0.5 |  p=1  |
---------+-------+-------+-------+
Sat1.cnf |   70  |  111  |  316  |
Sat2.cnf |   58  |   93  |  315  |
Sat3.cnf |  Fail |  Fail |  Fail |
Sat4.cnf |   61  |  136  |  257  |
Sat5.cnf |  Fail |  Fail |  Fail |
Sat6.cnf |   19  |  Fail |  Fail |
Sat7.cnf |  253  |  581  |  Fail |
Say8.cnf |   54  |  108  |  244  |
---------+-------+-------+-------+

-- TABLE 2 : DPLL and DPLL2

         | DPLL  | DPLL2 |
---------+-------+-------+
Sat1.cnf |   65  |   30  |
Sat2.cnf |   54  |   31  |
Sat3.cnf |  538  |   54  |
Sat4.cnf |   51  |   26  |
Sat5.cnf |  ---  |   47  |
Sat6.cnf |  662  |   55  |
Sat7.cnf | 35848 |   97  |
Say8.cnf |   55  |   27  |
---------+-------+-------+
-}
---------------------------------------------------------------------------------

-- REPORT

-- Do NOT exceed 1.5 A4 pages of printed plain text.
-- Write your report HERE:
{-

1)

	(a) All formulas were considered solvable by DPLL2. Sat5 caused DPLL to fail (30 minute runtime will be considered failure), and WalkSat failed Sat3, Sat5, and some of Sat6 and Sat7. Sat6 and Sat7's successes were abnormalties amongst the failures received. Sat3 and Sat5 are unsatisfiable, with the rest determined satisfiable by both DPLL and DPLL2.

	(b) Several instances occurred when WalkSat worked once and then didn't. This stems as a result of WalkSat using probability to compute a solution. Occasionally, the computer will be "unlucky" and make a series of flips that result in unsatisfiability. Conversely, as demonstrated by the table, the computer can get lucky and record a successful result when the other 9 trials result in failure (Sat6, p=0 for instance). A function based on probability will always have a probability of success and a probability of failure - no guarantees about either. 

	(c) There are two parameters in WalkSat: probability and maxflips. Maxflips sets the amount of flips the computer can make (thus limiting the time the computer has to compute a successful answer). A longer Maxflips means longer computation times, especially with functions that cannot be solved by WalkSat (such as Sat3). Setting WalkSat to a higher number should increase the chances of success. The probability function affects the performace more strongly - when the probability is zero, WalkSat continuously flips the truth value that maximizes the number of satisfied clauses. This result works the best, achieving success in the least number of flips. A probability of .5 effectively means the computer will alternate between randomly flipping and maximizing. This, on average, doubles the number of flips required for satisifing the formula. Finally, a probability of 1 means randomly flipping every time, again doubling the amount of flips required for success. 

	(d) Given that success has been found 6 out of 8 times with the probability set to zero, the ideal probability appears to be zero. This is furthered by the fact that in each instance where success was found amongst other probabilities, 0 gives the lowest number of flips. The total number of flips allowed by the computer should be high, if you do not mind waiting for an answer. This maximizes the chances of success by giving the computer more options to check before returning failure. 

	(e) WalkSat and DPLL both deal with logic in conjunctive normal form. WalkSat finds a solution via flipping values based on probability. DPLL checks satisfiability by following a branch along a tree, assigning values, backtracking in the wrong case, and simplifying as it goes.  Given the sheer differences between WalkSat and DPLL, there are few benefits that they could receive from one another. A possibility stems from WalkSat’s choice of which literal to solve. This value could be used as the heuristic for DPLL. Also, a combination of probability and the assignment of truth values, an amalgamation of the two searches, could prove beneficial. 


2)

	(a) The University of Saint Andrews had a few slides available on the internet about Heuristics and DPLL. From these notes, combined with my intuition with what would have the best effect, I decided to use an approach that would use the most appearing atom to determine which atom should be selected. This way, when they are assigned true, they have the greatest impact on the tree and would therefore reduce the amount of calculations the computer needed. The URL is here: http://www.cs.st-andrews.ac.uk/~ipg/AI/Lectures/Search4/sld015.htm
	
	(b) I created atoms2 to return a list of all the atoms in a formula (I didn't need atomsClause2 because atoms of the same value within a clause do not matter when assigned True/False). I then took the atoms2 list and intersected it with the first element from atoms. The unique atom left is counted by length and paired with itself. The process is repeated for all the atoms in the formula. Then, the atom appearing the most is found, by taking the highest number and comparing it to the tuple (using the functions findLength and findMostAtom respectively). FormulaMostAtom then takes the formula and returns the atom needed. Update2 uses formulaMostAtom whenever chooseAtom appeared, and the tail function is replaced by a removal of the most appearing atom. 

	(c) As evidenced by the table, there was a marked improvement. This is evidenced from Sat5, which I ran for 30 minutes without sign of stopping with DPLL. Using DPLL2, the result was calculated within seconds. Sat7 was another dramatic revaluation - 35848 to 97. This clearly demonstrates heuristics value. 
-}

---------------------------------------------------------------------------------
---------------------------------------------------------------------------------
