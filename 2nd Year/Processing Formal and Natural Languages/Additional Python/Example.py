# Example.py
# 20070920 LHK: created

# load the FSA module
import FSA

# Create two basic FSAs with labels 'a' and 'b' ...
a = FSA.singleton('a')
b = FSA.singleton('b')

# ... combine them through concatenation ...
ab = FSA.concatenation(a,b)

# ... and add a Kleene star!
ab_star = FSA.closure(ab)

#########################################
##   Please note that ab_star models   ##
##   the following regular expression: ##
##                                     ##
##          (a b)*                     ##
#########################################

# Now show 'a' ...
raw_input('Please press <enter> to see "a"')
a.view()

# ... show 'b' ...
raw_input('Please press <enter> to see "b"')
b.view()

# ... show 'ab' ... 
raw_input('Please press <enter> to see "ab"')
ab.view()

# ... show 'ab_star' ...
raw_input('Please press <enter> to see "ab_star"')
ab_star.view()

# ... minimize 'ab_star' and show it!
raw_input('Please press <enter> to see a minimize version of "ab_star"')
ab_star = FSA.minimize(ab_star)
ab_star.view()
