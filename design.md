# Design

## Who is the target for this design, e.g., are you assuming any knowledge on the part of the language users?
I am designing this language for those who do not speak picobot but wish to be able to provide him with instructions that are fairly natural langauge esc. 

## Why did you choose this design, i.e., why did you think it would be a good idea for users to express the maze-searching computation using this syntax?
I think it would be cool to have specific keywords that allow the user to guide picobot in particular directions and patterns. 

## What behaviors are easier to express in your design than in Picobot’s original design?  If there are no such behaviors, why not?
It should be easier to express what you want the robot to do for example, Go straight then go right might allow the robot to go straight until it cant anymore and then turn right. This example uses the kweywords of go, then, and a directional phrase.
the final behaviors of my language looks roughly like this "start go_right EGTR1" which allows for you to start in state0 and go right which sets your direction and surroundings and the keyword EGTRn where n is the state you want to end up in. this syntax is fairly simpler then picobot but also requires the user to understand what possible combinations of keywords work good together. 

## What behaviors are more difficult to express in your design than in Picobot’s original design? If there are no such behaviors, why not?
It may be more complex to design a while loop like fuctionality to a keyword. I was thinking about using a finished? like keyword where lets say "Go straight then go right until finished" where until describes a requirement to be met and finished correlates to a point in which the robot cannot move or finished the program.   

In my final implementation I did not have a loop keyword instead I found that tring to use keywords for loops were too hard and this resulte din the use of the keyword EGTRn where where n is the state you want to end up in. Now I can express the following :
"start go_right EGTR1", 
"next go_up until wall right", 
"next stayif wall right and up EGTR3",

where start and next allow for the start state to be 0 and using EGTRn allows for me to define new transitions to states. Notice in line 2 I do not use EGTRn which tells the code that it should stay in the same state that it is currently in in this case state 0.

## On a scale of 1–10 (where 10 is “very different”), how different is your syntax from PicoBot’s original design?
This syntax would be very differnet compared to the syntax of picobot so 8

I would still say that my syntax is pretty different. It may be true that my syntax ordering is roughly the same as picobot for instance start next first are keywords that describe what state its in, and EGTRn sets the transition state. Other than that my syntax Is completly String based and uses a specific set of keywords. 

## Is there anything you would improve about your design?
Yes eventaully instead of writing code like the following in order to run thr program: 

import DSL_API._
import picolib._
import picolib.maze._
import picolib.semantics._
import java.io.File

val rulesListEmpty = Robot("start go_left until wall EGTR1", 
                            "first go_up until wall then go_down EGTR2", 
                            "second go_down until wall then go_right EGTR3", 
                            "third go_up until wall then go_right EGTR2"
                        )

val emptyMazeEmpty = Maze("resources" + File.separator + "empty.txt")

object EmptyTextEmpty extends TextSimulation(emptyMazeEmpty, rulesListEmpty)

I would eventually like to have it so that I can write a txt file with my commands one on each line without having them in string qoutes and then just use that file in place of rulesListEmpty. I feel like that would be the best impliamention for my language. 
