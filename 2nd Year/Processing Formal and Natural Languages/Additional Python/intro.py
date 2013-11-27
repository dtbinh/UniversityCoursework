# intro.py

#load the FSA module
import FSA

#Create Appleton A and Appleton B -- Question 1
a = FSA.singleton('Enter AT')
b = FSA.singleton('Leave AT')

ba = FSA.concatenation(b,a)
ab_star = FSA.closure(ba)
at_door = FSA.concatenation(a,ab_star)

min_at_door = FSA.minimize(at_door)
#min_at_door.view()

show1 = min_at_door.checkQ1()
#print(show1)

#Coffee_and_Notes -- Question 2
c = FSA.singleton('Pick up lecture notes')
d = FSA.singleton('Return lecture notes')
e = FSA.singleton('Have a coffee')

cd = FSA.concatenation(c,d)
cd_star = FSA.closure(cd)
e_star = FSA.closure(e)

coffee = FSA.concatenation(e_star, c)
dcoffee = FSA.concatenation(d,coffee)

notes_and_coffee =  FSA.concatenation(coffee, FSA.closure(dcoffee))
min_notes = FSA.minimize(notes_and_coffee)
#min_notes.view()

show2 = min_notes.checkQ2()
#print(show2)

#Ipod -- Question 3
f = FSA.singleton('Turn iPod on')
g = FSA.singleton('Turn iPod off')

fg = FSA.concatenation(f,g)
fg_star = FSA.closure(fg)

min_fg = FSA.minimize(fg_star)
#min_fg.view()

show3 = min_fg.checkQ3()
#print(show3)

#Interleaving -- Question 4
test = FSA.preInterleave(min_at_door, min_notes)
level2 = FSA.interleave(test,min_fg)
#test2.view()

show4 = level2.checkQ4()
#print(show4)

#Dials -- Question 5

turn1 = FSA.singleton('Turn dial 1')
dial = FSA.minimize(FSA.concatenation(turn1,turn1))
dialloop = FSA.minimize(FSA.concatenation(dial, dial))
dialtest = FSA.minimize(FSA.concatenation(dial,FSA.closure(dialloop)))
#dialtest.view()
show5 = dialtest.checkQ5a()
#print(show5)

turn2 = FSA.singleton('Turn dial 2')
dial2 = FSA.minimize(FSA.concatenation(turn2,turn2))
dialloop2 = FSA.minimize(FSA.concatenation(dial2, dial2))
dialtest2 = FSA.minimize(FSA.concatenation(turn2,FSA.closure(dialloop2)))
#dialtest2.view()


turn3 = FSA.singleton('Turn dial 3')
dial3 = FSA.minimize(FSA.concatenation(turn3,turn3))
dialloop3 = FSA.minimize(FSA.concatenation(dial3, dial3))
dialplus = FSA.minimize(FSA.concatenation(dial3,turn3))
dialtest3 = FSA.minimize(FSA.concatenation(dialplus,FSA.closure(dialloop3)))
#dialtest3.view()


dials = FSA.minimize(FSA.interleave(FSA.interleave(dialtest3,dialtest2),dialtest))
#dials.view()
show8 = dials.checkQ5d()
#print(show8)

swipe = FSA.singleton('Swipe your student card')
level1 = FSA.minimize(FSA.preInterleave(swipe,dials))
#level1.view()
show9 = level1.checkQ5f()
#print(show9)

# Create Levels -- Question 6 
game1 = FSA.composeLevels(level1,level2)
game2 = FSA.concatenation(FSA.concatenation(level1,FSA.singleton('nextLevel')),level2)
show10 = game1.checkQ6()
show11 = game2.checkQ6()
hi = FSA.equivalent(game1,game2)

blah = FSA.singleton( "Enter AT"
, "You are outside Appleton Tower, the ugliest building on the Edinburgh skyline!"
, "You are inside Appleton Tower, much better!" )
blah2 = FSA.singleton( "Leave AT"
, "You are inside Appleton Tower."
, "You are outside Appleton Tower." )

at_door_2 = FSA.union(FSA.minimize(FSA.concatenation(blah,blah2)))
blah3 = FSA.closure(at_door_2)
at_door_3 = FSA.minimize(FSA.concatenation(a,blah3))

at_door_3.view()


