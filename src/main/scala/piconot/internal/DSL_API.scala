package DSL_API
import picolib._
import picolib.maze._
import picolib.semantics._
import java.io.File

// keywords that will not change and are grouped by type
val directionWords = List("up", "right", "left", "down")
val movingWords = List("go_up", "go_right", "go_left", "go_down")
val nextWords = List("first", "second", "third", "fourth", "fith", "sixth", "seventh", "eighth", "ninth")

// variables for initializing ground states and instances
// variables used for updating states, directions and other instances such as walls
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
var prevState: State = null

// sets the three major componets to null
def setNull(sur: Surroundings, dir: MoveDirection, st: (State,State)) = {
  surroundings = null
  direction = null
  state = (null, null)
}

// updates after an new rule has been made
def update() = {
  nextState = state._2
  prevState = state._1
  prevDirection = direction
  setNull(surroundings, direction, state)
  wallFlag = false
  untilFlag = false
}

// build a rule add it to rule list
def buildRule(state: State, sur: Surroundings, dir: MoveDirection, endState: State) = {
  val newRule = Rule(state, sur, dir, endState)
  ruleList = ruleList :+ newRule
}

// set state with value n
// used for easy incrementation with numbers
def setState(n: Int) = State(n.toString)

// turns the string value of a state into a number
def getState(state: State) = {
  if (state == null) then -1
  else state.toString.toInt
}

// sets states in the instace that first second third ect.. is called
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

// turns normal launguage into a direction
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

// sets wall to a particular direction and all else to anything
def setWall(dir: MoveDirection): Surroundings = dir match {
  case North => Surroundings(Blocked, Anything, Anything, Anything)
  case South => Surroundings(Anything, Anything, Anything, Blocked)
  case West => Surroundings(Anything, Anything, Blocked, Anything)
  case East => Surroundings(Anything, Blocked, Anything, Anything)
  case _ => throw new IllegalArgumentException(s"Invalid input: $dir")
}

// sets wall to a particular direction and all else to what already existed in a surrounding
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
    case _ => throw new IllegalArgumentException(s"Invalid input: $dir")
  }
}
// sets open to a particular direction and all else to Anything
def setOpen(dir: MoveDirection): Surroundings = dir match {
  case North => Surroundings(Open, Anything, Anything, Anything)
  case South => Surroundings(Anything, Anything, Anything, Open)
  case West => Surroundings(Anything, Anything, Open, Anything)
  case East => Surroundings(Anything, Open, Anything, Anything)
  case _ => throw new IllegalArgumentException(s"Invalid input: $dir")
}

// sets open to a particular direction and all else to what already existed in a surrounding
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
    case _ => throw new IllegalArgumentException(s"Invalid input: $dir")
  }
}
// defines how/what happens when a particular keyword is used in the string
def getWords(str: String): Unit = str match {
  case "start" => {
    if nextState == null then state = (setState(0), state._2)
    if (state._2 == null && state._1 != null) then state = (state._1, state._1)
    buildRule(setState(0), surroundings, direction, state._2)
    update()
  }
  case "next" => {
    state = (prevState, state._2)
    if (state._2 == null && state._1 != null) then state = (state._1, state._1)
    buildRule(state._1, surroundings, direction, state._2)
    update()
  }
  case str if nextWords.contains(str) => {
    setWState(str)
    if (state._2 == null && state._1 != null) then state = (state._1, state._1)
    buildRule(state._1, surroundings, direction, state._2)
    update()
  }
  case "until" => untilFlag = true

  case str if movingWords.contains(str) => {
    if (wallFlag) then surroundings = setWall(surroundings, setDirection(str))
    if (untilFlag && wallFlag) then {
      if (state._1 == null) then {
        if (getState(lastState) > getState(nextState)) then state = (lastState, state._2)
        else state = (setState(getState(state._2)-1), state._2)
      }
      else state = (lastState, state._2)
      if (direction == null) then direction = setDirection("stay")
      buildRule(state._1, setWall(surroundings, setDirection(str)),direction,state._2)
      lastState = state._2
      setNull(surroundings, direction, state)
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
      directionsList = Nil
    }
  }
  case "open" => {
    surroundings = setOpen(surroundings, directionsList(0))
    directionsList = directionsList.tail
  }
  case "stayif" => direction = setDirection(str)
  case "and" => // do nothing
  case "+" => // do nothing
  case s"EGTR${nStr}" => state = (state._1, setState(nStr.toInt))
  case _ => throw new IllegalArgumentException(s"Invalid input: $str")
}

// converts a string to a list of rules
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

// allows for a list of strings to be converted into a list of rules
case class RobotState(rules: List[Rule] = List.empty) {
  def +(input: String): RobotState = {
    copy(rules = rules ++ conversion(input))
  }
}
// robot("string") defines a list of rules
object Robot {
  def apply(input: String*): List[Rule] = {
    val state = input.foldLeft(RobotState())((s, input) => s + input)
    state.rules
  }
}
