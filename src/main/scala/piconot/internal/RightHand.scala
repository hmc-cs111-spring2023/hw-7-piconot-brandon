import DSL_API._
import picolib._
import picolib.maze._
import picolib.semantics._
import java.io.File

val rulesListMaze = Robot("start go_right EGTR1", 
                            "next go_up until wall right", 
                            "next stayif wall right and up EGTR3", 
                            "first go_down EGTR3", 
                            "next go_right until wall down", 
                            "next stayif wall right and down + open left EGTR2", 
                            "second go_up EGTR0", 
                            "next go_left until wall up", 
                            "next stayif wall left and up and open right EGTR1", 
                            "third go_left EGTR2", 
                            "next go_down until wall left", 
                            "next stayif wall down and left and open up EGTR0"
                        )

val emptyMazeMaze = Maze("resources" + File.separator + "maze.txt")

object EmptyTextMaze extends TextSimulation(emptyMazeMaze, rulesListMaze)