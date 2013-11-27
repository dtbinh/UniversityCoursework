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

inspectPlane = FSA.singleton("Inspect plane"
, "You need a key to operate this")

askGuard = FSA.singleton("Ask Guard a question"
, "There is a key in the office...snore...")

takeKey = FSA.singleton("Take the key"
, "You thief!")

flyPlane = FSA.singleton("Fly the plane"
, "Whoosh!")

test = FSA.minimize(FSA.closure( 

level1 = FSA.minimize(FSA.closure(FSA.concatenation(FSA.closure(askGuard),guard,(FSA.closure(FSA.concatenation(FSA.closure(FSA.concatenation(office,exitoffice)), office, FSA.concatenation( FSA.closure(FSA.concatenation(seePlane,FSA.closure(inspectPlane), leavePlane, ))), FSA.preInterleave(takeKey, FSA.concatenation( (FSA.closure(FSA.concatenation(seePlane,FSA.closure(inspectPlane), leavePlane, ))), exitoffice))))), leave)))

levelb = FSA.minimize(FSA.closure(FSA.concatenation(FSA.closure(askGuard),guard,(FSA.closure(FSA.concatenation(FSA.closure(FSA.concatenation(office,exitoffice)), office, FSA.preInterleave(takeKey, FSA.concatenation( (FSA.closure(FSA.concatenation(seePlane,FSA.closure(inspectPlane), leavePlane, ))), exitoffice)), FSA.concatenation( FSA.closure(FSA.concatenation(seePlane,FSA.closure(inspectPlane), leavePlane, )))))), leave)))


level1.view()
#levelb.view()
