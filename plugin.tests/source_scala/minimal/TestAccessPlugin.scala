package minimal

import code.satyagraha.gfm.viewer.model.impl.ViewerModelDefault

object TestAccessPlugin {
  
  val x = new ViewerModelDefault(null, null, null, null, null)
  
  def main(args: Array[String]) {
      println("x: " + x)
  }
  
}