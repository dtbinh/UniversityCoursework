-- Informatics 1 - Functional Programming 
-- Tutorial 4
--
-- Due: the tutorial of week 6 (29/30 Oct)


import System
import IO
import List( nub )
import Char
import Test.QuickCheck

-- <type decls>

type Link = String
type Name = String
type Email = String
type HTML = String
type URL = String

-- </type decls>
-- <sample data>

tutorialURL = "http://www.inf.ed.ac.uk/admin/itodb/mgroups/tuts/inf1-fp.html"
groupURL    = "http://www.inf.ed.ac.uk/admin/itodb/mgroups/stus/inf1-fp.html"
testURL     = "http://www.inf.ed.ac.uk/teaching/courses/inf1/fp/testpage.html"

testHTML :: String
testHTML =    "<html>"
           ++ "<head>"
           ++ "<title>FP: Tutorial 4</title>"
           ++ "</head>"
           ++ "<body>"
           ++ "<h1>A Boring test page</h1>"
           ++ "<h2>for tutorial 4</h2>"
           ++ "<a href=\"http://www.inf.ed.ac.uk/teaching/courses/inf1/fp/\">FP Website</a><br>"
           ++ "<b>Lecturer:</b> <a href=\"mailto:wadler@inf.ed.ac.uk\">Philip Wadler</a><br>"
           ++ "<b>TA:</b> <a href=\"mailto:w.b.heijltjes@sms.ed.ac.uk\">Willem Heijltjes</a>"
           ++ "</body>"
           ++ "</html>"

testLinks :: [Link]
testLinks = [ "http://www.inf.ed.ac.uk/teaching/courses/inf1/fp/\">FP Website</a><br><b>Lecturer:</b> "
            , "mailto:wadler@inf.ed.ac.uk\">Philip Wadler</a><br><b>TA:</b> "
            , "mailto:w.b.heijltjes@sms.ed.ac.uk\">Willem Heijltjes</a></body></html>" ]


testAddrBook :: [(Name,Email)]
testAddrBook = [ ("Philip Wadler","wadler@inf.ed.ac.uk")
               , ("Willem Heijltjes","w.b.heijltjes@sms.ed.ac.uk")]

-- </sample data>
-- <system interaction>

getURL :: String -> IO String
getURL url =
    do username <- getEnv "LOGNAME"
       system ("wget -O /tmp/fp-tmp-" ++ username ++ ".html --quiet " ++ url)
       readFile ("/tmp/fp-tmp-" ++ username ++ ".html")

emailsFromURL :: URL -> IO ()
emailsFromURL url =
  do html <- getURL url
     let emails = (emailsFromHTML html)
     putStr (ppAddrBook emails)

emailsByNameFromURL :: URL -> Name -> IO ()
emailsByNameFromURL url name =
  do html <- getURL url
     let emails = (emailsByNameFromHTML html name)
     putStr (ppAddrBook emails)

-- </system interaction>
-- <exercises>

-- 1.
sameString :: String -> String -> Bool
sameString xs ys | map (toUpper ) xs == map (toUpper) ys  = True
                 | otherwise = False                                         

prop_sameString :: String -> Bool
prop_sameString str  = 
    map toLower str `sameString` map toUpper str

-- 2.
prefix :: String -> String -> Bool
prefix prf str = sameString prf (take (length prf) str) 
                                                 

prop_prefix :: String -> Int -> Bool
prop_prefix str n  =  prefix substr (map toLower str) &&
		      prefix substr (map toUpper str)
                          where
                            substr  =  take n str


-- 3.
contains :: String -> String -> Bool
contains xs ys | prefix xs ys = True
               | ys == [] = False
               | otherwise = contains xs (drop 1 ys)

--prop_contains :: String -> Int -> Int -> Bool
--prop_contains str n = prefix substr (map toLower str) &&
                       --prefix substr (map toUpper str)
                        --      where 
                         --       substr = take n str


-- 4.
takeUntil :: String -> String -> String
takeUntil x [] = []
takeUntil x (a:ys) | prefix x (a:ys)==False = a : takeUntil x ys
                   | prefix x (a:ys)==True = []
  
dropUntil :: String -> String -> String
dropUntil x [] = []
dropUntil x ys = drop (length((takeUntil x ys)++x)) ys 

-- 5.
split :: String -> String -> String
split xs [] = []
split xs ys = (takeUntil xs ys) ++ (split xs (dropUntil (xs) ys))

split2 :: String -> String -> [String]
split2 xs [] = []
split2 xs ys | contains xs ys = (takeUntil xs ys) : (split2 xs (dropUntil (xs) ys))
             | otherwise = [ys] 

reconstruct :: String -> [String] -> String
reconstruct xs [] = []
reconstruct xs (y:ys) | ((length(y:ys))>1) = y ++ xs ++ reconstruct xs ys
                      | otherwise = y     


prop_split :: String -> String -> Property
prop_split sep str  =  sep /= [] ==> reconstruct sep (split2 sep str) `sameString` str


-- 6.
linksFromHTML :: HTML -> [Link]
linksFromHTML html  = split2 "<a href=" html

linksFromHTML2 :: HTML -> [Link]
linksFromHTML2 html = tail (split2 "<a href=" html)

testLinksFromHTML :: Bool
testLinksFromHTML  =  linksFromHTML testHTML == testLinks


-- 7.
takeEmails :: [Link] -> [Link]
takeEmails links = [x| x <- links, prefix "mailto:" x]


-- 8.
link2pair :: Link -> (Name, Email)
link2pair  link = ((takeUntil "</a>" (dropUntil "\">" link)), (takeUntil "\">" (dropUntil "mailto:" link)))


-- 9.
emailsFromHTML :: HTML -> [(Name,Email)]
emailsFromHTML = undefined

testEmailsFromHTML :: Bool
testEmailsFromHTML  =  emailsFromHTML testHTML == testAddrBook


-- 10.
findEmail :: Name -> [(Name, Email)] -> [(Name, Email)]
findEmail = undefined


-- 11.
emailsByNameFromHTML :: HTML -> Name -> [(Name,Email)]
emailsByNameFromHTML = undefined


-- Optional Material

-- 12.
ppAddrBook :: [(Name, Email)] -> String
ppAddrBook addr = unlines [ name ++ ": " ++ email | (name,email) <- addr ]
