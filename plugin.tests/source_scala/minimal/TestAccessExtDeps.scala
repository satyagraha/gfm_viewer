package minimal

import org.picocontainer.PicoBuilder

object TestAccessExtDeps {
  
  val x = classOf[PicoBuilder]
  
  def main(args: Array[String]) {
      println("x: " + x)
  }
  
}