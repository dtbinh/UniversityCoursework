-- Informatics 1 - Functional Programming 
-- Tutorial 7
--
-- Week 9 - Due: 19/20 Nov.


import LSystem
import Test.QuickCheck

-- Exercise 1

-- 1a. split
split :: Command -> [Command]
split Sit = []
split (a :#: b) = split a ++ split b
split a = [a]


-- 1b. join
join :: [Command] -> Command
join [] = Sit
join (x : xs) = x :#: join(xs)


-- 1c  equivalent
equivalent :: Command -> Command -> Bool
equivalent x y = split(x) == split (y) 
               

-- 1d. testing join and split
prop_split_join :: Command -> Bool
prop_split_join x = equivalent x (join (split x))

prop_split :: Command -> Bool
prop_split c = all psh (split c)
               
psh :: Command -> Bool
psh (_:#:_) = False
psh sit = False
psh _ = True


-- Exercise 2
-- 2a. copy
copy :: Int -> Command -> Command
copy 1 c = c
copy x c = c :#: copy (x-1) c

-- 2b. pentagon
pentagon :: Distance -> Command
pentagon x = copy 5 (Go x :#: Turn 72.0)

-- 2c. polygon
polygon :: Distance -> Int -> Command
polygon x n= copy n (Go x :#: Turn p)
	where p = 360 / (fromIntegral n)



-- Exercise 3
-- spiral
spiral :: Distance -> Int -> Distance -> Angle -> Command
spiral side n step angle | n==0 = Sit
                         | side >0 = (Go side :#: Turn angle) :#: spiral (side+step) (n-1) step angle



-- Exercise 4
-- optimise
optimise :: Command -> Command
optimise comm = join ( f (split comm))
                where f xs | g xs == xs = xs
                           | otherwise = f(g xs)
                           where g [] = []
                                 g (Sit:xs) = g xs
                                 g (Go 0:xs)= g xs
                                 g (Turn 0: xs) = g xs
                                 g (Go x:Go y:xs) = ((Go (x+y)):g xs)
                                 g (Turn x:Turn y: xs) = (Turn (x+y) :g xs)
                                 g (cmd:xs) = cmd : g xs

--optional
--Excerise  5. arrowhead
arrowhead :: Int -> Command
arrowhead = undefined

-- 6. snowflake
snowflake :: Int -> Command
snowflake = undefined

-- 7. hilbert
hilbert :: Int -> Command
hilbert = undefined

