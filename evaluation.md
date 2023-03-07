# Evaluation: running commentary

## Internal DSL

_Describe each change from your ideal syntax to the syntax you implemented, and
describe_ why _you made the change._

**On a scale of 1–10 (where 10 is "a lot"), how much did you have to change your syntax?**
My syntax did not have to change that much but the nuber of keywords and the names of the keywords that I endedup using mostly changed I would say about 5.

**On a scale of 1–10 (where 10 is "very difficult"), how difficult was it to map your syntax to the provided API?**
It was kind more dificult then expected I would say around an 8. I had high ambitions for the type of natural language stings that I wanted to use and eventually had to revert to a set of words that almost go 1 to 1 for that number of characters in the picobot language. My syntax still has some better usage. 

The following is the process of how I came to use my final language:

First I started out with this idea that worked well for empty txt but not so well for the maze. 

Robot "start go_right until wall" "next go_up until wall" "next go_down until wall" "next go_up + go_left_1 or go_down + go_left_1 finished?"

In the following represention bellow you can see how I origonally maped out my syntax to a command: 
// ex: start go_right until wall
// rule(state0, (Anything, Anything, Anything, Anything), East, state0)
// rule(state0, (Anything, Blocked, Anything, Anything), StayHere, state1)

// ex: next go_up until wall
// rule(state1, (Anything, Anything, Anything, Anything), North, state1)
// rule(state1, (Blocked, Anything, Anything, Anything), StayHere, state2)

// ex: next go_down until wall
//       next             go_down                         go_down  next
// rule(state2, (Anything, Anything, Anything, Open), South, state2)
//       until             wall                            until  until
// rule(state2, (Anything, Anything, Anything, Blocked), StayHere, state3)

// ex: next go_up + go_left_1 or go_down + go_left_1 finished?

//.     next          go_up                          go_up    next
// rule(state3, (Open, Anything, Anything, Anything), North, state3)
//         +              go_left_1                   go_left_1  or
// rule(state3, (Blocked, Anything, open, Anything), West, state4)

//.     or          go_down                          go_down    or
// rule(state4, (Anything, Anything, Anything, Open), South, state4)
//         +              go_left_1                 go_left_1  finished?
// rule(state4, (Anything, Anything, open, Blocked), West, state3)

// start go_right update 
// 0 *x** -> E 1  
// or go_up_1 
// 0 xE** -> N 0
// or stop update
// 0 NE** -> X 3

// next go_down update
// 1 ***x -> S 3
// or go_right_1
// 1 *x*S -> E 1
// or Stop update
// 1 *ExS -> X 2

// next go_up update
// 2 x*** -> N 0 
// or go_up + go_left_1
// 2 N*x* -> W 2
// or go_up + go_left + go_right_1 update
// 2 NxW* -> X 1

//
// 3 **x* -> W 2
//
// 3 **Wx -> S 3
//
// 3 x*WS -> X 0


Then after I had code that seemed to work for empty but not maze I took another look at my code and saw that it was using a selct few types of functions and that I could rework my code in a diffrent order. This being that my keywords are read in each string from back to front. The following kinda shows how I thought through the creation of this by defining parts of my lagauge in terms of definitions that I created which could be used to write one line of picobot code.

// rework commands for both in terms of functions to be called abouve using buildrules 
// then come up with a new natural language that fits the capabilites of the functionality 

// new set of 
// start next first second thrid fourth... until left right down up go_left go_right go_down go_up then wall open stayif and ElseGoToRule(n)

// start go_left
// buildRule()
// setState(0)
// setOpen(West)
// setDirection(left)
// setState(0)
// 0 **x* -> W 0 

// until wall increment
// buildRule()
// setState(0)
// setWall(West)
// setDirection(stay)
// setState(1)
// 0 **W* -> X 1  

// first go_up
// buildRule()
// setState(1)
// setOpen(North)
// setDirection(up)
// setState(1)
// 1 x*** -> N 1 

// until wall then go_down increment
// buildRule()
// setState(1)
// setWall(setOpen(South), North)
// setDirection(down)
// setState(2)
// 1 N**x -> S 2  

// second go_down 
// buildRule()
// setState(2)
// setOpen(South)
// setDirection(down)
// setState(2)
// 2 ***x -> S 2  

// until wall then go_right increment
// buildRule()
// setState(2)
// setWall(setOpen(East), South)
// setDirection(Right)
// setState(3)
// 2 *x*S -> E 3 

// third go_up 
// buildRule()
// setState(3)
// setOpen(North)
// setDirection(up)
// setState(3)
// 3 x*** -> N 3

// until wall then go_right decrement(repeat)
// buildRule()
// setState(3)
// setWall(setOpen(East), North)
// setDirection(Right)
// setState(2)
// 3 Nx** -> E 2  

/////////////////////////

// start right incremnt
// buildRule()
// setState(0)
// setOpen(East)
// setDirection(right)
// setState(1)
// 0 *x** -> E 1  

// next go_up until wall right
// buildRule()
// setState(0)
// setWall(setOpen(north), East)
// setDirection(up)
// setState(0)
// 0 xE** -> N 0

// next stayif wall right and up incremnet(3)
// buildRule()
// setState(0)
// setWall(setWall(north), East)
// setDirection(stay)
// setState(3)
// 0 NE** -> X 3

// first go_down increment(3)
// buildRule()
// setState(1)
// setOpen(South)
// setDirection(down)
// setState(3)
// 1 ***x -> S 3

// next go_right until wall down
// buildRule()
// setState(1)
// setWall(setOpen(East), South)
// setDirection(right)
// setState(1)
// 1 *x*S -> E 1

// next stayif ((wall right and down) + (open left)) incremnet(2)
// buildRule()
// setState(1)
// SetWall(setWall(setOpen(West), South), East)
// setDirection(stay)
// setState(2)
// 1 *ExS -> X 2

// second go_up incremnet(0)
// buildRule()
// setState(2)
// setOpen(North)
// setDirection(up)
// setState(0)
// 2 x*** -> N 0 

// next go_right until wall up
// buildRule()
// setState(2)
// setWall(setOpen(West), North)
// setDirection(right)
// setState(2)
// 2 N*x* -> W 2

// next stayif ((wall left and up) and (open right)) incremnet(1)
// buildRule()
// setState(2)
// SetWall(setWall(setOpen(East), West), North)
// setDirection(stay)
// setState(1)
// 2 NxW* -> X 1

// third go_right incremnet(2)
// buildRule()
// setState(3)
// setOpen(West)
// setDirection(right)
// setState(2)
// 3 **x* -> W 2

// next go_down until wall left
// buildRule()
// setState(3)
// setWall(setOpen(South), West)
// setDirection(down)
// setState(3)
// 3 **Wx -> S 3

// next stayif ((wall down and left) and (open up)) incremnet(0)
// buildRule()
// setState(3)
// setWall(setWall(setOpen(north), South), West)
// setDirection(stay)
// setState(0)
// 3 x*WS -> X 0
