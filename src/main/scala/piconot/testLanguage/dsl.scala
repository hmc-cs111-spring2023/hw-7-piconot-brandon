import picolib._
import picolib.maze._
import picolib.semantics._
import java.io.File

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

val directionWords = List("go_up", "go_right", "go_left", "go_down")
val specificWords = List("go_up_1", "go_right_1", "go_left_1", "go_down_1")

var surroundings: (Surroundings, Surroundings) = (null, null)
var direction: (MoveDirection, MoveDirection) = (null, null)
var state: ((State,State), (State,State)) = ((null, null), (null, null))
var ruleList: List[Rule] = List.empty
var numOrs = 0

def setNull(sur: (Surroundings, Surroundings), dir: (MoveDirection, MoveDirection), st: ((State,State), (State,State))) = {
  surroundings = (null,sur._2)
  direction = (null, dir._2)
  state = ((null, null), (st._2._1, st._2._2))
}

def saveTouples(sur: (Surroundings, Surroundings), dir: (MoveDirection, MoveDirection), st: ((State,State), (State,State))) = {
  buildRule(st._1._1, sur._1, dir._1, st._1._2)
  buildRule(st._2._1, sur._2, dir._2, st._2._2)
}

def setDirection(str: String): MoveDirection = str match {
    case "go_up" => North
    case "go_right" => East
    case "go_left" => West
    case "go_down" => South
    case "go_up_1" => North
    case "go_right_1" => East
    case "go_left_1" => West
    case "go_down_1" => South
    case _ => StayHere
}

def setWall(dir: MoveDirection): Surroundings = dir match {
  case North => Surroundings(Blocked, Anything, Anything, Anything)
  case South => Surroundings(Anything, Anything, Anything, Blocked)
  case West => Surroundings(Anything, Anything, Blocked, Anything)
  case East => Surroundings(Anything, Blocked, Anything, Anything)
  case _ => throw new IllegalArgumentException(s"Invalid input: $dir")
}

def setOpen(dir: MoveDirection): Surroundings = dir match {
  case North => Surroundings(Open, Anything, Anything, Anything)
  case South => Surroundings(Anything, Anything, Anything, Open)
  case West => Surroundings(Anything, Anything, Open, Anything)
  case East => Surroundings(Anything, Open, Anything, Anything)
}


def setOpen(sur: Surroundings, dir: MoveDirection): Surroundings = {
  val (n, e, w, s) = sur match {
    case Surroundings(n, e, w, s) => (n, e, w, s)
  }
  dir match {
    case North => Surroundings(Open, e, w, s)
    case South => Surroundings(n, e, w, Open)
    case West => Surroundings(n, e, Open, s)
    case East => Surroundings(n, Open, w, s)
  }
}

def getWords(str: String): Unit = str match {
  case "start" => state = ((setState(0), setState(0)), state._2)
  case "next" => state = ((state._2._2, state._2._2), state._2)
  case str if directionWords.contains(str) => {
    direction = (setDirection(str), direction._2)
    surroundings = (setOpen(direction._1), surroundings._2)
  }
  case str if specificWords.contains(str) => {
    surroundings = (surroundings._1, setOpen(setWall(setDirection(str)), setDirection(str)))
    direction = (direction._1, setDirection(str))
  }
  case "wall" => surroundings = (surroundings._1, setWall(direction._1))
  case "until" => {
    state = (state._1, (state._1._2, setState(getState(state._1._2) + 1)))
    direction = (direction._1, setDirection(str))
  }
  case "+" => state = (state._1, (state._1._2, state._2._2))
  case "or" => {
    numOrs = numOrs + 1
    state = (state._1, (state._2._1, setState(getState(state._1._2) + 1)))
    saveTouples(surroundings, direction, state)
    setNull(surroundings, direction, state)
    state = ((state._2._2, state._2._2), state._1)

  }
  case "finished?" => {
    state = (state._1, (state._2._1, setState(getState(state._2._1) - numOrs)))
    numOrs = 0
  }
  case _ => throw new IllegalArgumentException(s"Invalid input: $str")
}

def buildRule(state: State, sur: Surroundings, dir: MoveDirection, endState: State) = {
  val newRule = Rule(state, sur, dir, endState)
  ruleList = ruleList :+ newRule
}

def setState(n: Int) = State(n.toString)

def getState(state: State) = state.toString.toInt

// Part 1:
val conversion: Conversion[String, List[Rule]] = (str: String) => {
  val parts = str.split(" ")
  // parts 0 = first word
  // parts 1 = second word
  /// exc ...
  for (word <- parts) {
    getWords(word)
  }
  buildRule(state._1._1, surroundings._1, direction._1, state._1._2)
  buildRule(state._2._1, surroundings._2, direction._2, state._2._2)
  
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
val rulesList1 = Robot("start go_right until wall",
                       "next go_up until wall",
                       "next go_down until wall",
                       "next go_up + go_left_1 or go_down + go_left_1 finished?")

val emptyMaze1 = Maze("resources" + File.separator + "empty.txt")

object EmptyText1 extends TextSimulation(emptyMaze1, rulesList1)

// Robot "start go_right until wall" "next go_up until wall" "next go_down until wall" "next go_up + go_left_1 or go_down + go_left_1 finished?"