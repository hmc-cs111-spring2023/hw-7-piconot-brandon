import picolib._
import picolib.maze._
import picolib.semantics._
import java.io.File

val directionWords = List("up", "right", "left", "down")
val movingWords = List("go_up", "go_right", "go_left", "go_down")
val nextWords = List("first", "second", "third", "fourth", "fith", "sixth", "seventh", "eighth", "ninth")

var surroundings: Surroundings = null
var direction: MoveDirection = null
var state: (State,State) = (null, null)
var nextState: State = null
var prevDirection: MoveDirection = null
var ruleList: List[Rule] = List.empty
var untilFlag: Boolean = false
var wallFlag: Boolean = false
var directionsList: List[MoveDirection] = Nil
var lastState: State = null 
def setNull(sur: Surroundings, dir: MoveDirection, st: (State,State)) = {
  surroundings = null
  direction = null
  state = (null, null)
}

def setWState(str: String): Unit = str match {
  case "first" => state = (setState(1), state._2)
  case "second" => state = (setState(2), state._2)
  case "third" => state = (setState(3), state._2)
  case "fourth"=> state = (setState(4), state._2)
  case "fith"  => state = (setState(5), state._2)
  case "sixth" => state = (setState(6), state._2)
  case "seventh" => state = (setState(7), state._2)
  case "eighth"  => state = (setState(8), state._2)
  case "ninth" => state = (setState(9), state._2)
  case _ => throw new IllegalArgumentException("Unsupported number of arguments") // throw an exception for any other number of arguments
}


def setDirection(str: String): MoveDirection = str match {
    case "up" => North
    case "right" => East
    case "left" => West
    case "down" => South
    case "go_up" => North
    case "go_right" => East
    case "go_left" => West
    case "go_down" => South
    case _ => StayHere
}

def setWall(dir: MoveDirection): Surroundings = dir match {
  case North => Surroundings(Blocked, Anything, Anything, Anything)
  case South => Surroundings(Anything, Anything, Anything, Blocked)
  case West => Surroundings(Anything, Anything, Blocked, Anything)
  case East => Surroundings(Anything, Blocked, Anything, Anything)
  case _ => throw new IllegalArgumentException(s"Invalid input: $dir")
}

def setWall(sur: Surroundings, dir: MoveDirection): Surroundings = {
  val (n, e, w, s) = sur match {
    case Surroundings(n, e, w, s) => (n, e, w, s)
    case null => (Anything, Anything, Anything, Anything)
  }
  dir match {
    case North => Surroundings(Blocked, e, w, s)
    case South => Surroundings(n, e, w, Blocked)
    case West => Surroundings(n, e, Blocked, s)
    case East => Surroundings(n, Blocked, w, s)
  }
}

def setOpen(dir: MoveDirection): Surroundings = dir match {
  case North => Surroundings(Open, Anything, Anything, Anything)
  case South => Surroundings(Anything, Anything, Anything, Open)
  case West => Surroundings(Anything, Anything, Open, Anything)
  case East => Surroundings(Anything, Open, Anything, Anything)
  case _ => throw new IllegalArgumentException(s"Invalid input: $dir")
}


def setOpen(sur: Surroundings, dir: MoveDirection): Surroundings = {
  val (n, e, w, s) = sur match {
    case Surroundings(n, e, w, s) => (n, e, w, s)
    case null => (Anything, Anything, Anything, Anything)
  }
  dir match {
    case North => Surroundings(Open, e, w, s)
    case South => Surroundings(n, e, w, Open)
    case West => Surroundings(n, e, Open, s)
    case East => Surroundings(n, Open, w, s)
  }
}

def getWords(str: String): Unit = str match {
  case "start" => {
    if nextState == null then state = (setState(0), state._2)
    if (state._2 == null && state._1 != null) then state = (state._1, state._1)
    buildRule(setState(0), surroundings, direction, state._2)
    nextState = state._2
    prevDirection = direction
    setNull(surroundings, direction, state)
    wallFlag = false
    untilFlag = false
  }
  case "next" => {
    state = (nextState, state._2)
    if (state._2 == null && state._1 != null) then state = (state._1, state._1)
    buildRule(state._1, surroundings, direction, state._2)
    nextState = state._2
    prevDirection = direction
    setNull(surroundings, direction, state)
    wallFlag = false
    untilFlag = false
  }
  case str if nextWords.contains(str) => {
    setWState(str)
    if (state._2 == null && state._1 != null) then state = (state._1, state._1)
    buildRule(state._1, surroundings, direction, state._2)
    nextState = state._2
    prevDirection = direction
    setNull(surroundings, direction, state)
    wallFlag = false
    untilFlag = false
  }
  case "until" => untilFlag = true

  case str if movingWords.contains(str) => {
    if (wallFlag) then surroundings = setWall(surroundings, setDirection(str))
    if (untilFlag && wallFlag) then {
      if (state._1 == null) then {
        println(lastState)
        println(nextState)
        if (getState(lastState) > getState(nextState)) then state = (lastState, state._2)
        else state = (setState(getState(state._2)-1), state._2)
      }
      else state = (lastState, state._2)
      if (direction == null) then direction = setDirection("stay")
      buildRule(state._1, setWall(surroundings, setDirection(str)),direction,state._2)
      lastState = state._2
      state = (null, null)
      surroundings = null
      direction = null
    }
    
    direction = setDirection(str)
    if (surroundings == null) then surroundings = setOpen(setDirection(str))
    else surroundings = setOpen(surroundings, setDirection(str))
  }
  case str if directionWords.contains(str) => {
    directionsList = directionsList :+ setDirection(str)
  }
  case "then" => surroundings = setOpen(surroundings, direction)
  case "wall" => {
    if (directionsList.length == 0) then wallFlag = true
    else{
      for (i <- 0 to directionsList.length-1) {
          surroundings = setWall(surroundings, directionsList(i))
      }
    }
  }
  case "open" => {
    surroundings = setOpen(surroundings, directionsList(1))
    directionsList = directionsList.tail
  }
  case "stayif" => direction = setDirection(str)
  case "and" => surroundings = setWall(surroundings, direction)
  case "+" => // do nothing
  case s"EGTR${nStr}" => state = (state._1, setState(nStr.toInt))

  case _ => throw new IllegalArgumentException(s"Invalid input: $str")
}

def buildRule(state: State, sur: Surroundings, dir: MoveDirection, endState: State) = {
  val newRule = Rule(state, sur, dir, endState)
  ruleList = ruleList :+ newRule
}

def setState(n: Int) = State(n.toString)

def getState(state: State) = {
  if (state == null) then -1
  else state.toString.toInt
}

val conversion: Conversion[String, List[Rule]] = (str: String) => {
  val parts = str.split(" ").reverse.mkString(" ").split(" ")
  // parts 0 = first word
  // parts 1 = second word
  /// exc ...
  for (word <- parts) {
    getWords(word)
  }
  println(ruleList)
  ruleList
}

case class RobotState(rules: List[Rule] = List.empty) {
  def +(input: String): RobotState = {
    copy(rules = rules ++ conversion(input))
  }
}

object Robot {
  def apply(input: String*): List[Rule] = {
    val state = input.foldLeft(RobotState())((s, input) => s + input)
    state.rules
  }
}
val rulesList1 = Robot("start go_left until wall EGTR1", "first go_up until wall then go_down EGTR2", "second go_down until wall then go_right EGTR3", "third go_up until wall then go_right EGTR2")

val emptyMaze1 = Maze("resources" + File.separator + "empty.txt")

object EmptyText1 extends TextSimulation(emptyMaze1, rulesList1)

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
    

