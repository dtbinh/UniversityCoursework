-- Informatics 1 - Functional Programming 
-- Tutorial 3
--
-- Week 5 - Due: 22/23 Oct.

import Data.Char
import Test.QuickCheck



-- 1. Map
-- a.
uppers :: String -> String
uppers x =  map toUpper x

-- b.
doubles :: [Int] -> [Int]
doubles x =  map (2*) x

-- c.        
penceToPounds :: [Int] -> [Float]
penceToPounds x = map (/100) (map fromIntegral  x)


-- d.
uppers' :: String -> String
uppers' xs = [toUpper x | x<-xs]  

prop_uppers :: String -> Bool
prop_uppers x =  uppers x ==uppers' x



-- 2. Filter
-- a.
alphas :: String -> String
alphas x  =  filter (isAlpha) x

-- b.
rmChar ::  Char -> String -> String
rmChar y x =  filter (\x -> x/=y) x

-- c.
above :: Int -> [Int] -> [Int]
above y x =  filter (>y) x

-- d.
unequals :: [(Int,Int)] -> [(Int,Int)]
unequals ys = filter (\x -> fst(x) /= snd(x)) ys

-- e.
rmCharComp :: Char -> String -> String
rmCharComp y xs = [x | x <- xs, x /= y]

prop_rmChar :: Char -> String -> Bool
prop_rmChar y x = rmChar y x == rmCharComp y x  



-- 3. Comprehensions vs. map & filter
-- a.
upperChars :: String -> String
upperChars s  =  [toUpper c | c <- s, isAlpha c]

upperChars' :: String -> String
upperChars' x  =  filter (isAlpha) (map (toUpper) x)

prop_upperChars :: String -> Bool
prop_upperChars s  =  upperChars s == upperChars' s

-- b.
largeDoubles :: [Int] -> [Int]
largeDoubles xs  =  [2 * x | x <- xs, x > 3]

largeDoubles' :: [Int] -> [Int]
largeDoubles' x  =  map (2*) (filter (>3) x)

prop_largeDoubles :: [Int] -> Bool
prop_largeDoubles xs  =  largeDoubles xs == largeDoubles' xs 

-- c.
reverseEven :: [String] -> [String]
reverseEven strs  =  [reverse s | s <- strs, even (length s)]

reverseEven' :: [String] -> [String]
reverseEven' x  =  map (reverse) (filter (\x -> even(length(x))) x)

prop_reverseEven :: [String] -> Bool
prop_reverseEven strs  =  reverseEven strs == reverseEven' strs



-- 4. Foldr
-- a.
productRec :: [Int] -> Int
productRec xs =  foldr (*) 1 xs

productFold :: [Int] -> Int
productFold [] = 1
productFold (x:xs) = x * productFold(xs) 

prop_product :: [Int] -> Bool
prop_product xs  =  productRec xs == productFold xs

-- b.
andRec :: [Bool] -> Bool
andRec [] = True
andRec (x:xs) | x == True = x && andRec (xs) 
              | otherwise = x && andRec (xs)   

andFold :: [Bool] -> Bool
andFold xs =  foldr (&&) True xs

prop_and :: [Bool] -> Bool
prop_and xs  =  andRec xs == andFold xs 

-- c.
concatRec :: [[Char]] -> [Char]
concatRec []  = ""
concatRec (x:xs) =  x ++ (concatRec xs)

concatFold :: [String] -> String
concatFold xs =  foldr (++) [] xs

prop_concat :: [String] -> Bool
prop_concat strs  =  concatRec strs == concatFold strs

-- d. (optional)
rmCharsRec :: String -> String -> String
rmCharsRec "" ys = ys
rmCharsRec (x:xs) ys = rmCharsRec xs (rmChar x ys) 

rmCharsFold :: String -> String -> String
rmCharsFold xs ys = foldr (rmChar) ys xs

prop_rmChars :: String -> String -> Bool
prop_rmChars chars str  =  rmCharsRec chars str == rmCharsFold chars str



-- Optional material

type Matrix = [[Int]]


-- 5
-- a.
uniform :: [Int] -> Bool
uniform = undefined

-- b.
valid :: Matrix -> Bool
valid = undefined


-- 6.
-- b.
size :: Matrix -> (Int,Int)
size = undefined

-- c.
square :: Matrix -> Bool
square = undefined



-- 8.
plusM :: Matrix -> Matrix -> Matrix
plusM = undefined

-- 9.
timesM :: Matrix -> Matrix -> Matrix
timesM = undefined
