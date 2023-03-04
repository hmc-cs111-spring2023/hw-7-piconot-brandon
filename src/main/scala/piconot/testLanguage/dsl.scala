import picolib._
import picolib.maze._
import picolib.semantics._
import java.io.File

// ex: start go right until wall
// rule(state0, (Anything, Anything, Anything, Anything), East, state0)
// rule(state0, (Anything, Blocked, Anything, Anything), StayHere, state1)

// ex: next go up until wall
// rule(state1, (Anything, Anything, Anything, Anything), North, state1)
// rule(state1, (Blocked, Anything, Anything, Anything), StayHere, state2)

// ex: next go down until wall
// rule(state2, (Anything, Anything, Anything, Anything), South, state2)
// rule(state2, (Anything, Anything, Anything, Blocked), StayHere, state3)

// ex: next (go left 1 + up) or (go left 1 + down) until done

// rule(state3, (Open, Anything, Anything, Anything), North, state3)
// rule(state3, (Blocked, Anything, open, Anything), West, state4)

// rule(state4, (Anything, Anything, Anything, Open), South, state4)
// rule(state4, (Blocked, Anything, open, Blocked), West, state3)

var surroundings: (Surroundings, Surroundings) = (null, null)


val directionWords = List("up", "right", "left", "down")
// val directionRef = List(North, East, West, South)

var direction: (MoveDirection, MoveDirection) = (null, null)


var ruleList: List[Rule] = List.empty

def setDirection(str: String): MoveDirection = str match {
    case "up" => North
    case "right" => East
    case "left" => West
    case "down" => South
    case _ => StayHere
}

def setWall(dir: MoveDirection): Surroundings = dir match {
  case North => Surroundings(Blocked, Anything, Anything, Anything)
  case South => Surroundings(Anything, Anything, Anything, Blocked)
  case West => Surroundings(Anything, Anything, Blocked, Anything)
  case East => Surroundings(Anything, Blocked, Anything, Anything)
  case _ => throw new IllegalArgumentException(s"Invalid input: $dir")
}

var state: ((State,State), (State,State)) = ((null, null), (null, null))

def setState(n: Int) = State(n.toString)

// def getState(state: State) = state.toInt

// Part 1:
val conversion: Conversion[String, List[Rule]] = (str: String) => {
  val parts = str.split(" ")
  // parts 0 = first word
  // parts 1 = second word
  // parts 2 = third word
  /// exc ...
  for (word <- parts) {
    if word == "start" then state = ((setState(0), setState(0)), state._2)
    if word == "next" then state = ((state._1._1, state._1._1), state._2)
    if word == "go" then surroundings = (Surroundings(Anything, Anything, Anything, Anything), surroundings._2)
    if directionWords.contains(word) then direction = (setDirection(word), direction._2)
    if (word == "until") then {
        state = (state._1, (state._1._2, setState(1)))
        direction = (direction._1, setDirection(word)) 
    }
    if word == "wall" then surroundings = (surroundings._1, setWall(direction._1))
  }
  val newRule = Rule(state._1._1, surroundings._1, direction._1, state._1._2)
  val newRule1 = Rule(state._2._1, surroundings._2, direction._2, state._2._2)
  ruleList = ruleList :+ newRule :+ newRule1
  ruleList
}

val emptyMaze1 = Maze("resources" + File.separator + "empty.txt")

object EmptyText1 extends TextSimulation(emptyMaze1, conversion("start go up until wall"))

// // Part 3:
// extension (lang1: RegularLanguage)
//   def go(lang2: RegularLanguage): RegularLanguage = Union(lang1, lang2)

//   def until(lang2: RegularLanguage): RegularLanguage = Concat(lang1, lang2)

//   def right : RegularLanguage = Star(lang1)
 
//   def up : RegularLanguage = Star(lang1)
//   def down : RegularLanguage = Star(lang1)
//   def left : RegularLanguage = Concat(lang1, Star(lang1))

//   def apply(n: Int): RegularLanguage = n match {
//     case 0 => Empty
//     case 1 => lang1
//     case _ => Concat(lang1, lang1(n - 1))
//   }