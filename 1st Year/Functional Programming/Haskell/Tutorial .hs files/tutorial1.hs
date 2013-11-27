-- Informatics 1 - Functional Programming 
-- Tutorial 1
--
-- Due: the tutorial of week 3 (8/9 Oct.)

import Data.Char
import Data.List
import Test.QuickCheck



-- 1. halveEvens

-- List-comprehension version
halveEvens :: [Int] -> [Int]
halveEvens xs = [ x `div` 2 | x <- xs, even x ]

-- Recursive version
halveEvensRec :: [Int] -> [Int]
halveEvensRec []                    = []
halveEvensRec (x:xs) | even x       = x `div` 2 : halveEvensRec xs
                     | otherwise    = halveEvensRec xs                 

-- Mutual test
prop_halveEvens :: [Int] -> Bool
prop_halveEvens xs = halveEvens xs == halveEvensRec xs



-- 2. inRange

-- List-comprehension version
inRange :: Int -> Int -> [Int] -> [Int]
inRange lo hi xs = [x | x <- xs, lo <= x, x <= hi]

-- Recursive version
inRangeRec :: Int -> Int -> [Int] -> [Int]
inRangeRec lo hi []                   = []
inRangeRec lo hi (x:xs) | (lo <= x &&  x <= hi) = x : inRangeRec lo hi xs
                        | otherwise      = inRangeRec lo hi xs

-- Mutual test
prop_inRange :: Int -> Int -> [Int] -> Bool
prop_inRange lo hi xs = inRange lo hi xs == inRangeRec lo hi xs



-- 3. sumPositives: sum up all the positive numbers in a list

-- List-comprehension version
countPositives :: [Int] -> Int
countPositives xs = length ([ x | x <- xs, 0 < x])

-- Recursive version
countPositivesRec :: [Int] -> Int
countPositivesRec [] = 0
countPositivesRec (x:xs) | x>0 = 1 + countPositivesRec xs
                         | otherwise = countPositivesRec xs        

-- Mutual test
prop_countPositives :: [Int] -> Bool
prop_countPositives xs = countPositives xs ==countPositivesRec xs



-- 4. pennypincher

-- Helper function
discount :: Int -> Int
discount x = round((90/100)* (fromIntegral(x)))

-- List-comprehension version
pennypincher :: [Int] -> Int
pennypincher xs = sum([discount(x) | x <- xs, discount(x) <= 19900])

-- Recursive version
pennypincherRec :: [Int] -> Int
pennypincherRec [] = 0
pennypincherRec (x:xs)  | discount(x)<=19900 = discount(x)+pennypincherRec (xs)
                        | otherwise          = pennypincherRec(xs)

-- Mutual test
prop_pennypincher :: [Int] -> Bool
prop_pennypincher xs = pennypincher xs == pennypincherRec xs



-- 5. sumDigits



-- List-comprehension version
multDigits :: String -> Int
multDigits xs = product( [digitToInt x | x <- xs, isDigit x])

-- Recursive version
multDigitsRec :: String -> Int
multDigitsRec [] = 1
multDigitsRec (x:xs)  | isDigit x = digitToInt(x)*multDigitsRec(xs)
                      | otherwise =  multDigitsRec (xs)

-- Mutual test
prop_multDigits :: String -> Bool
prop_multDigits xs = multDigitsRec xs == multDigits xs



-- 6. capitalized

-- List-comprehension version
capitalized :: String -> String
capitalized "" = ""
capitalized xs = toUpper (head xs) : tail([toLower(x) | x<-xs])

 

-- Recursive version
capitalizedRec :: String -> String
capitalizedRec [] = []
capitalizedRec (x:xs)  | isUpper x = toLower(x) : capitalizedRec(xs) 
                       | otherwise  = x : capitalizedRec (xs)

capitalFull :: String -> String
capitalFull [] = []
capitalFull  xs = toUpper (head xs) : tail(capitalizedRec xs)

-- Mutual test
prop_capitalized :: String -> Bool
prop_capitalized xs = capitalized xs == capitalFull xs



-- 7. title

-- List-comprehension version
title :: [String] -> [String]
title (x:xs) = capitalized x : [if (length x >3) then (capitalized x) else (makeLower x) | x<-xs]

               
-- Recursive version
titleRec :: [String] -> [String]
titleRec [] = []
titleRec (x:xs) | length(x)>3 = capitalized(x) : titleRec(xs)
                | otherwise   = makeLower(x) : titleRec(xs)

titleFix :: [String] -> [String]
titleFix xs = drop 1 (titleRec(xs))

steal :: [String] -> [String]
steal xs = take 1 (titleRec(xs))

bigFirst :: [String] -> [String]
bigFirst xs = checker (steal xs)

combine :: [String] -> [String]
combine xs = bigFirst xs ++ titleFix xs

checker :: [String] -> [String] 
checker xs = [capitalized x | x<-xs]

correction :: [String] -> [String]
correction xs = [capitalized x | x<-xs]

makeLower :: String -> String
makeLower xs = [toLower(x) | x<-xs]



-- mutual test
prop_title :: [String] -> Bool
prop_title = undefined

squaresRec :: [Integer] -> [Integer]
squaresRec [] = []
squaresRec (x:xs) = x*x : squaresRec xs
                     



-- Optional Material

-- 8. crosswordFind

-- List-comprehension version
crosswordFind :: Char -> Int -> Int -> [String] -> [String]
crosswordFind = undefined

-- Recursive version
crosswordFindRec :: Char -> Int -> Int -> [String] -> [String]
crosswordFindRec = undefined

-- Mutual test
prop_crosswordFind :: Char -> Int -> Int -> [String] -> Bool
prop_crosswordFind = undefined 



-- 9. search

-- List-comprehension version

search :: String -> Char -> [Int]
search = undefined

-- Recursive version
searchRec :: String -> Char -> [Int]
searchRec = undefined

-- Mutual test
prop_search :: String -> Char -> Bool
prop_search = undefined


-- 10. contains

-- List-comprehension version
contains :: String -> String -> Bool
contains = undefined

-- Recursive version
containsRec :: String -> String -> Bool
containsRec = undefined

-- Mutual test
prop_contains :: String -> String -> Bool
prop_contains = undefined

--Random Shit
sign :: Int -> Char
sign x | 1<=x && x<=9 = '+'
       | x==0 = 'O'
       | -9<=x && x<=(-1) = '-'
       | otherwise = error("Poop")

signs :: [Int] -> String
signs xs = [sign x| x<-xs, -9<=x && x<=9]

signs' :: [Int] -> String
signs' [] = ""
signs' (x:xs) | -9 <= x && x <= 9 = sign x : (signs' xs)
              | otherwise = signs' xs

prop_signs :: [Int] -> Bool
prop_signs xs = signs' xs == signs xs

f :: Char -> [Char]
f x = [i| (y,i)<-zip ['A'..'Z'] (reverse ['Z'..'A']), x==y]

thing :: String -> String
thing xs = [i| (i,y) <- zip xs [1..n], odd y]
           where n = length xs

things :: String -> String
things [] = ""
things (x:xs) | y`mod`2==0 = x : things xs 
              | otherwise = things xs