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