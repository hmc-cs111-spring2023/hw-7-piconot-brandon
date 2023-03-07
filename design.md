# Design

## Who is the target for this design, e.g., are you assuming any knowledge on the part of the language users?
I am designing this language for those who do not speak picobot but wish to be able to provide him with instructions that are fairly natural langauge esc. 

## Why did you choose this design, i.e., why did you think it would be a good idea for users to express the maze-searching computation using this syntax?
I think it would be cool to have specific keywords that allow the user to guide picobot in particular directions and patterns. 

## What behaviors are easier to express in your design than in Picobot’s original design?  If there are no such behaviors, why not?
It should be easier to express what you want the robot to do for example, Go straight then go right might allow the robot to go straight until it cant anymore and then turn right. This example uses the kweywords of go, then, and a directional phrase.
the final behaviors of my language 

## What behaviors are more difficult to express in your design than in Picobot’s original design? If there are no such behaviors, why not?
It may be more complex to design a while loop like fuctionality to a keyword. I was thinking about using a finished? like keyword where lets say "Go straight then go right until finished" where until describes a requirement to be met and finished correlates to a point in which the robot cannot move or finished the program.   

## On a scale of 1–10 (where 10 is “very different”), how different is your syntax from PicoBot’s original design?
This syntax would be very differnet compared to the syntax of picobot so 8

## Is there anything you would improve about your design?


// Robot "start go_right until wall" "next go_up until wall" "next go_down until wall" "next go_up + go_left_1 or go_down + go_left_1 finished?"
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


// 0 xE** -> N 0
// 0 *x** -> E 1    
// 1 *x*S -> E 1
// 1 *ExS -> X 2
// 2 N*x* -> W 2
// 2 x*** -> N 0

// 0 NE** -> X 3
// 3 **Wx -> S 3
// 3 **x* -> W 2
// 2 NxW* -> X 1
// 1 ***x -> S 3    
// 3 x*WS -> X 0


// rework commands for both in terms of functions to be called abouve using buildrules 
// then come up with a new natural language that fits the capabilites of the functionality 

// start next first second thrid fourth... until left right down up then wall open stayif ?and? elsegotorule(n)
// List(0 **x* -> W 0, 0 **B* -> X 1, 1 x*** -> N 1, 1 ***B -> S 2, 2 ***x -> S 2, 2 *B** -> E 3, 3 x*** -> N 3, 3 *B** -> E 2)
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
