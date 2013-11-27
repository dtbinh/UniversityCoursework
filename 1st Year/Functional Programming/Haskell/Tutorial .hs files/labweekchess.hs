-- Informatics 1 - Functional Programming 
-- Lab week tutorial part II
--
--

import ChessPieces
import Test.QuickCheck



-- Exercise 9:

pic1 :: Picture
pic1 = beside( above (knight)(invert knight))(above (invert knight) knight)

pic2 :: Picture
pic2 = beside (above (knight)(invert( flipV knight )))(above (invert knight)(flipV (knight)))




-- Exercise 10:
-- a)

emptyRow :: Picture
emptyRow = repeatH 4 (beside (whiteSquare) blackSquare) 

-- b)

otherEmptyRow :: Picture
otherEmptyRow = repeatH 4 (beside (blackSquare) whiteSquare)

-- c)

middleBoard :: Picture
middleBoard = repeatV 2 (above(emptyRow)otherEmptyRow)

-- d)

whiteRow :: Picture
whiteRow =  beside( beside (beside (beside (beside (beside (beside rook  knight) bishop) queen) king) bishop) knight) rook 

whiteRowPieces :: Picture
whiteRowPieces = superimpose whiteRow otherEmptyRow


blackRow :: Picture
blackRow = invert whiteRow 

blackRowPieces :: Picture
blackRowPieces = superimpose blackRow emptyRow

-- e)

populatedBoard :: Picture
populatedBoard = above ( above allBlack middleBoard) allWhite

blackPawns :: Picture
blackPawns = superimpose (repeatH 8 (invert pawn)) otherEmptyRow

whitePawns :: Picture
whitePawns = superimpose (repeatH 8 (pawn)) emptyRow

allBlack :: Picture
allBlack = above blackRowPieces blackPawns

allWhite :: Picture
allWhite = above whitePawns whiteRowPieces



-- Functions --

twoBeside :: Picture -> Picture
twoBeside x = beside x (invert x)


-- Exercise 11:

twoAbove :: Picture -> Picture
twoAbove x = above x (invert x)

fourPictures :: Picture -> Picture
fourPictures x = twoAbove( twoBeside x)



-- Optional material -------------------------

-- Exercise 12:

prop_assoc_above :: Picture -> Picture -> Picture -> Bool
prop_assoc_above p q r = p `above` (q `above` r) == (p `above` q) `above` r

prop_2x_invert :: Picture -> Bool
prop_2x_invert p = invert (invert p) == p

prop_assoc_beside :: Picture -> Picture -> Picture -> Bool
prop_assoc_beside = undefined
