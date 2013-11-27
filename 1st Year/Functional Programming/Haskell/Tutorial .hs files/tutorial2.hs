-- Informatics 1 - Functional Programming 
-- Tutorial 2
--
-- Week 4 - due: 12/16 Oct.

import Data.Char
import Data.List
import Test.QuickCheck


-- 1.
rotate :: Int -> [Char] -> [Char]
rotate x xs   | (x<0) = error "x<0"
              | (length xs < x) = error "list>x"
              | otherwise = (drop x xs) ++(take x xs)  

-- rotate 0 bs = bs
-- rotate x (a:as) = rotate (x-1) as++[a]

-- 2.
prop_rotate :: Int -> String -> Bool
prop_rotate k str = rotate (l - m) (rotate m str) == str
                        where l = length str
                              m = if l == 0 then 0 else k `mod` l

-- 3. 
makeKey :: Int -> [(Char, Char)]
makeKey x = zip alphabet (rotate x alphabet)
            where alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"


-- 4.
lookUp :: Char -> [(Char, Char)] -> Char
lookUp x [] = x
lookUp x ((xs,y):rest) | x==xs = y
                | otherwise = lookUp x rest    

lookUp2 :: Char -> [(Char, Char)] -> Char
lookUp2 x ys = head ([snd y | y<-ys, x==fst y]++[x])

-- 5.
encipher :: Int -> Char -> Char
encipher x c = lookUp c (makeKey x)

-- 6.
normalize :: String -> String
normalize (xs) = [toUpper x | x <- xs , isDigit x ||  isAlpha x]

-- 7.
encipherStr :: Int -> String -> String
encipherStr n xs = [encipher n x | x<-ys]
                     where ys = (normalize xs)

-- 8.
reverseKey :: [(Char, Char)] -> [(Char, Char)]
reverseKey key = [(snd x, fst x) |  x  <- key  ] 

-- 9.
--decipher :: Int -> Char -> Char
--decipher k ch = lookup ch (reverseKey (makeKey k)) 

--decipherStr :: Int -> String -> String
--decipherStr k str = [decipher k ch | ch <- str]

-- 10.
--prop_cipher :: Int -> String -> Property
--prop_cipher k str = [(0<=k &&k=26)==> decipher k (encipher k str)== normalize str]

-- expr1 ==> expr2

-- 11.
contains :: String -> String -> Bool
contains x y | y `ifPrefixOf` x= True
             | x == [] = False
             | otherwise =  contains (drop 1 x) y 

-- 12.
candidates :: String -> [(Int, String)]
candidates = undefined



-- Optional Material

-- 13.
splitEachFive :: String -> [String]
splitEachFive = undefined

-- 14.
prop_transpose :: String -> Bool
prop_transpose = undefined

-- 15.
encrypt :: Int -> String -> String
encrypt = undefined

-- 16.
decrypt :: Int -> String -> String
decrypt = undefined