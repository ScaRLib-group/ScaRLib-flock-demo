package experiments

import it.unibo.scarlib.core.model.State
import StateInfo._
import it.unibo.scarlib.core.neuralnetwork.NeuralNetworkEncoding

case class CohesionCollisionState(positions: List[(Double, Double)], agentId: Int) extends State {

  override def isEmpty(): Boolean = false
}

object CohesionCollisionState {
  implicit val encoding: NeuralNetworkEncoding[State] = new NeuralNetworkEncoding[State] {
    override def elements(): Int = neighborhood * StateInfo.encoding

    override def toSeq(element: State): Seq[Double] = {
      val elem = element.asInstanceOf[CohesionCollisionState]
      val fill = List.fill(elements())(0.0)
      (elem.positions.flatMap { case (l, r) => List(l, r) } ++ fill).take(elements())
    }
  }
}