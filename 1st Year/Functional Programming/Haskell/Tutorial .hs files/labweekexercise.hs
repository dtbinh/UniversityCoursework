-- Informatics 1 - Functional Programming
-- Lab Week Exercise
--
-- Week 2 - due: Friday, Oct. 2, 5pm
--
-- Insert your name and matriculation number here:
-- Name: Scott Hofman
-- Nr. : s0943941


import Test.QuickCheck


-- Exercise 3:

double :: Int -> Int
double x = x + x

square :: Int -> Int
square x = x * x

-- Exercise 4:

isTriple :: Int -> Int -> Int -> Bool
isTriple a b c = (a^2 + b^2 == c^2)


-- Exercise 5:

leg1 :: Int -> Int -> Int
leg1 x y = (x^2 - y^2)

leg2 :: Int -> Int -> Int
leg2 x y = (2*y*x)

hyp :: Int -> Int -> Int
hyp x y = x^2+y^2


-- Exercise 6:

prop_triple :: Int -> Int -> Bool
prop_triple x y = isTriple (leg1 x y) (leg2 x y) (hyp x y)

