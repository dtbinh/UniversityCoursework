-- Effective Propositional Inference
-- Informatics 2D
-- Assignment 1
-- 2010-2011
--
-- Main function for DPLL with heuristic
-- DO NOT EDIT THIS FILE --

module Main where

import System.Environment
import Assignment
import SatParser

main = do
        args <- getArgs
        sat <- getExample (args!!0)
        case (map (\x -> (x!!0)) (filter (\x -> x /= [[]]) sat)) of
         [] -> putStrLn "Not a valid Sat problem"
         formula -> test formula


test formula = do 
                 putStrLn "Using DPLL with heuristic ..." 
                 let dpll_heuristic = dpllHeuristic formula
                 putStrLn ("dpll_heuristic: Formula is " ++ (show (fst dpll_heuristic)) ++ " and number of backtrack calls made is " ++ (show (snd dpll_heuristic)))

