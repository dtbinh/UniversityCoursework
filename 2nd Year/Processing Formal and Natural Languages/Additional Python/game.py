#Welcome to the Game
#Importing FSA
import FSA

guard = FSA.singleton("Pass Guard" 
, "There is a guard blocking your entrance")

leave = FSA.singleton("Leave Main Area"
, "Go past the guard, who's asleep")

office = FSA.singleton("To office"
, "There's a door to an office")

exitoffice = FSA.singleton("Leave office"
, "Back to the entrance")

seePlane = FSA.singleton("To plane"
, "In the office, you see a plane")

leavePlane = FSA.singleton("Leave plane"
, "Back to the office")

test = FSA.minimize((FSA.closure (FSA.concatenation (seePlane,leavePlane) ) ))
test2 = FSA.minimize(FSA.preInterleave(everything,test))

entrance = FSA.minimize(FSA.concatenation(leave, guard))
everything = FSA.minimize(FSA.closure(FSA.concatenation(guard,(FSA.closure(FSA.preinterleave(test, (FSA.concatenation(office, exitoffice)))), leave)))


#everything.view()
test2.view()

