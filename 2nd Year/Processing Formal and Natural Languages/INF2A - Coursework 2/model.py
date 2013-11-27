import FSA

######################################################
################## THE CHUNNEL FSA ###################
######################################################
#
# This is the FSA model for the chunnel.  There are two
# crash sites: the british_end and the french_end and
# emergency vehicles are dispatched to these sites to help
# at the site.  The model is looping in the sense that
# whenever a vehicle return to its base new problems arise
# for it at both ends of the chunnel.  All accidents require
# all vehicles to attend and there is a requirement that
# fire is extinguished before people are evacuated before
# the wreck at the site is towed away.
#
########## ATOMIC ACTIONS ######################################
# Each atomic action comes in two forms, one labelled with
# propositions for the initial and final states and a second
# unlabelled version with with no propositions.  The unlabelled
# versions are used in languages that restrict the sequencing
# of the actions in the main model via the intersection operator.

rbf = FSA.singleton('return(fire_brigade,to(fire_station))',
                    None,
                    "location(fire_brigade,fire_station)")
rbfu = FSA.singleton('return(fire_brigade,to(fire_station))')

rba = FSA.singleton('return(ambulance,to(hospital))',
                    None,
                    "location(ambulance,hospital)")
rbau = FSA.singleton('return(ambulance,to(hospital))')

rbt = FSA.singleton('return(tow_truck,to(garage))',
                    None,
                    "location(tow_truck,garage)")
rbtu = FSA.singleton('return(tow_truck,to(garage))')
                    
cb1 = FSA.singleton('assign(british_end,fire_brigade)',
                    ["location(fire_brigade,fire_station)",
                     "location(flames,british_end)"],
                    ["location(fire_brigade,british_end)",
                     "location(flames,british_end)"]
                    )
cb1u = FSA.singleton('assign(british_end,fire_brigade)')

ca1 = FSA.singleton('assign(british_end,ambulance)',
                    ["location(ambulance,hospital)",
                     "location(victimsb,british_end)"
                     ],
                    ["location(ambulance,british_end)",
                     "location(victimsb,british_end)"
                     ])
ca1u = FSA.singleton('assign(british_end,ambulance)')

changes = FSA.singleton('changemodes')


ctt1 = FSA.singleton('assign(british_end,tow_truck)',
                     ["location(tow_truck,garage)",
                      "location(wreckb,british_end)"
                      ],
                     ["location(tow_truck,british_end)",
                      "location(wreckb,british_end)"
                      ])
ctt1u = FSA.singleton('assign(british_end,tow_truck)')

fo1 = FSA.singleton('extinguish(flames,from(british_end))',
                    ["location(fire_brigade,british_end)",
                     "location(flames,british_end)"
                     ],
                    "location(fire_brigade,british_end)")
fo1u = FSA.singleton('extinguish(flames,from(british_end))')

pe1 = FSA.singleton('evacuate(victims,from(british_end))',
                    ["location(victimsb,british_end)",
                     "location(ambulance,british_end)"
                     ],
                    "location(victimsb,hospital)")
pe1u = FSA.singleton('evacuate(victims,from(british_end))')

wc1 = FSA.singleton('remove(wreck,from(british_end))',
                    ["location(tow_truck,british_end)",
                     "location(wreckb,british_end)",
                     ],
                    "location(wreckb,garage)")
wc1u = FSA.singleton('remove(wreck,from(british_end))')


cb2 = FSA.singleton('assign(french_end,fire_brigade)',
                    ["location(fire_brigade,fire_station)",
                     "location(flames,french_end)"],
                    ["location(fire_brigade,french_end)",
                     "location(flames,french_end)"])
cb2u = FSA.singleton('assign(french_end,fire_brigade)')

ca2 = FSA.singleton('assign(french_end,ambulance)',
                    ["location(ambulance,hospital)",
                     "location(victimsf,french_end)"
                     ],
                    ["location(ambulance,french_end)",
                     "location(victimsf,french_end)"
                     ])
ca2u = FSA.singleton('assign(french_end,ambulance)')

ctt2 = FSA.singleton('assign(french_end,tow_truck)',
                     ["location(tow_truck,garage)",
                      "location(wreckf,french_end)"
                      ],
                     ["location(tow_truck,french_end)",
                      "location(wreckf,french_end)"
                      ])
ctt2u = FSA.singleton('assign(french_end,tow_truck)')

fo2 = FSA.singleton('extinguish(flames,from(french_end))',
                    ["location(fire_brigade,french_end)",
                     "location(flames,french_end)"
                     ],
                    "location(fire_brigade,french_end)")
fo2u = FSA.singleton('extinguish(flames,from(french_end))')

pe2 = FSA.singleton('evacuate(victims,from(french_end))',
                    ["location(victimsf,french_end)",
                     "location(ambulance,french_end)"
                     ],
                    "location(victimsf,hospital)")
pe2u = FSA.singleton('evacuate(victims,from(french_end))')

wc2 = FSA.singleton('remove(wreck,from(french_end))',
                    ["location(tow_truck,french_end)",
                     "location(wreckf,french_end)"
                     ],
                    "location(wreckf,garage)")
wc2u = FSA.singleton('remove(wreck,from(french_end))')

# The rush_to transitions don't require labels to be attached to them
rtf = FSA.singleton('rush_to(french_end)')
rtfu = FSA.singleton('rush_to(french_end)')
rtb = FSA.singleton('rush_to(british_end)')
rtbu = FSA.singleton('rush_to(british_end)')

# These transitions are always used to generate "self-loops" that
# are not used in interaction with the parser but are helpful in
# defining the model.
inf = FSA.minimize(FSA.closure(FSA.singleton('in_France')))
inb = FSA.minimize(FSA.closure(FSA.singleton('in_Britain')))

# This is the model for the firetruck with "rush" actions
# that allow it to go directly from one crash site to the
# other without passing through the fire station.
fo1 = FSA.minimize(FSA.concatenation(fo1,inb))
fo2 = FSA.minimize(FSA.concatenation(fo2,inf))
fbcycle = FSA.minimize(FSA.closure(FSA.concatenation(rtf,fo2,rtb,fo1)))
thrub = FSA.minimize(FSA.concatenation(cb1,fo1,fbcycle,FSA.union(rbf,FSA.concatenation(rtf,fo2,rbf))))
thruf = FSA.minimize(FSA.concatenation(cb2,fo2,FSA.union(rbf,FSA.concatenation(rtb,fo1,fbcycle,FSA.union(rbf,FSA.concatenation(rtf,fo2,rbf))))))
firetruck = FSA.minimize(FSA.closure(FSA.union(thrub,thruf)))

firefrench = FSA.concatenation(cb1,fo1,FSA.closure(FSA.concatenation(rtf,fo2,rtb,fo1)),FSA.union(FSA.concatenation(rtf,fo2,rbf),rbf))
firebritish = FSA.concatenation(cb2,fo2,FSA.closure(FSA.concatenation(rtb,fo1,rtf,fo2)),FSA.union(FSA.concatenation(rtb,fo1,rbf),rbf))

other = FSA.concatenation(FSA.union(cb1,cb2), FSA.union(fo1,fo2) , FSA.union(FSA.closure(FSA.concatenation(rtf,fo2,rtb,fo1)),FSA.closure(FSA.concatenation(rtb,fo1,rtf,fo2))),FSA.union(FSA.union(FSA.concatenation(rtb,fo1,rbf), FSA.concatenation(rtf,fo2,rbf)),rbf))

myfiretruck = FSA.minimize(FSA.closure(FSA.union(firebritish,firefrench)))
myfiretruck2 = FSA.minimize(FSA.closure(other))

#firetruck2 = FSA.minimize(FSA.closure(FSA.union(FSA.concatenation(cb1,fo1,FSA.closure(FSA.concatenation(rtf,fo2,rtb,fo1)),FSA.union(FSA.concatenation(rtf,fo2,rbf),rbf)),FSA.concatenation(cb2,fo2,FSA.closure(FSA.concatenation(rtb,fo1,rtf,fo2)),FSA.union(FSA.concatenation(rtb,fo1,rbf),rbf)))))


#rushcycle = FSA.minimize(FSA.closure(FSA.concatenation(inb,rtf,inf,rtb)))
#thisfiretruck = FSA.minimize(FSA.closure(FSA.union(FSA.union(FSA.concatenation(cb1,fo1,inb,rbf),FSA.concatenation(cb2,fo2,inf,rba)),rushcycle)))
#rushcycle2 = FSA.minimize(FSA.closure(FSA.union(FSA.concatenation(inb,rtf),FSA.concatenation(inf,rtb))))
#firetruck2 = FSA.minimize(FSA.closure(FSA.union(FSA.union(FSA.concatenation(cb1,fo1,inb,rbf),FSA.concatenation(cb2,fo2,inf,rba)),rushcycle2)))
#firetruck3 = FSA.minimize(FSA.closure(FSA.union(FSA.concatenation(cb1,fo1,inb,rbf),FSA.concatenation(cb2,fo2,inf,rba))))
#firetruck4 = FSA.minimize(FSA.closure(FSA.union(FSA.concatenation(cb1,fo1, inb,rbf))))
#raw_input('Please press <enter> to see firetruck')
#firetruck.view()

# This is the ambulance behaviour, it evacuates victims 
# either from the british end or the french end
ambulance = FSA.minimize(FSA.closure(FSA.union(FSA.concatenation(ca1,pe1,inb,rba),FSA.concatenation(ca2,pe2,inf,rba))))

ambfrench = FSA.concatenation(ca1,pe1,FSA.closure(FSA.concatenation(ca2,pe2,ca1,pe1)),FSA.union(FSA.concatenation(ca2,pe2,rba),rba))
ambbritish = FSA.concatenation(ca2,pe2,FSA.closure(FSA.concatenation(ca1,pe1,ca2,pe2)),FSA.union(FSA.concatenation(ca1,pe1,rba),rba))

ambb2 = FSA.closure(FSA.concatenation(ca1,pe1, FSA.union(rba,FSA.concatenation(ca2,pe2,rba))))
ambf2 = FSA.closure(FSA.concatenation(ca2,pe2, FSA.union(rba,FSA.concatenation(ca1,pe1,rba))))

smode = FSA.minimize(FSA.closure(FSA.union(FSA.concatenation(ca1,pe1,rba),FSA.concatenation(ca2,pe2,rba))))
rmode = FSA.minimize(FSA.closure(FSA.union(ambfrench, ambbritish)))
rmode2 = FSA.minimize(FSA.closure(FSA.union(ambf2,ambb2)))

cmode = FSA.minimize(FSA.closure(FSA.concatenation(changes,changes)))

ambu1 =FSA.minimize(FSA.closure(FSA.concatenation(changes,rmode2,changes)))
ambu2 =FSA.minimize(FSA.concatenation(changes,rmode2,cmode))

end = FSA.minimize(FSA.closure(FSA.concatenation(cmode,smode, ambu1)))

myambulance = FSA.minimize(FSA.union(FSA.concatenation(end, ambu2),end))

#raw_input('Please press <enter> to see ambulance')
#ambulance.view()

# This is the tow truck behaviour, it tows wrecks away either
# from the british end or the french end.
towtruck = FSA.minimize(FSA.closure(FSA.union(FSA.concatenation(ctt1,wc1,inb,rbt),FSA.concatenation(ctt2,wc2,inf,rbt))))
#raw_input('Please press <enter> to see towtruck')
#towtruck.view()

# These define all actions and all those we do not want to restrict by intersection in the
# main model.  "oastar" is just any sequence of those actions we do not want to restrict.
allactions = FSA.minimize(FSA.union(inb,inf,rtfu,rtbu,rbfu,rbau,rbtu,cb1u,ca1u,ctt1u,fo1u,pe1u,wc1u,cb2u,ca2u,ctt2u,fo2u,pe2u,wc2u))
otheractions = FSA.minimize(FSA.union(inb,inf,rtfu,rtbu,rbfu,rbau,rbtu,cb1u,ca1u,ctt1u,cb2u,ca2u,ctt2u))
oastar = FSA.minimize(FSA.closure(otheractions))
#raw_input('Please press <enter> to see oastar')
#oastar.view()

# "synchservices" is just the language that ensures the fire must be extinguished before the
# victims are evacuated before the wreck is cleared at either end.
syncservices1 = FSA.closure(FSA.concatenation(oastar,fo1u,oastar,pe1u,oastar,wc1u,oastar))
showsync = FSA.minimize(syncservices1)
syncservices2 = FSA.closure(FSA.concatenation(oastar,fo2u,oastar,pe2u,oastar,wc2u,oastar))
syncservices = FSA.minimize(FSA.interleave(syncservices1,syncservices2))
#raw_input('Please press <enter> to see syncservices')
#syncservices.view()

# The combined fire engine and ambulance behaviour
fireamb = FSA.minimize(FSA.interleave(firetruck,ambulance))
#raw_input('Please press <enter> to see fireamb')
#fireamb.view()

# The combined fire engine, ambulance and tow truck behaviour
fireambtow = FSA.minimize(FSA.interleave(fireamb,towtruck))
#raw_input('Please press <enter> to see fireambtow')
#fireambtow.view()

# Constrain the combined vehicles to observe the constraints on the order of activities.
chunnel = FSA.minimize(FSA.intersection(fireambtow,syncservices))
#raw_input('Please press <enter> to see chunnel')
#chunnel.view()
