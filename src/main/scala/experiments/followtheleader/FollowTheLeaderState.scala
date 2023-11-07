package experiments.followtheleader

import StateInfo._
import it.unibo.scarlib.core.model.State
import it.unibo.scarlib.core.neuralnetwork.NeuralNetworkEncoding

case class FollowTheLeaderState(
                                 directionToLeader: (Double, Double),
                                 positions: Seq[(Double, Double)],
                                 distanceFromLeader: Double,
                                 agentId: Int
                               ) extends State {

  override def isEmpty(): Boolean = false

}

object FollowTheLeaderState {
  implicit val encoding: NeuralNetworkEncoding[State] = new NeuralNetworkEncoding[State] {
    override def elements(): Int = (neighborhood * StateInfo.encoding) + 2

    override def toSeq(element: State): Seq[Double] = {
      val elem = element.asInstanceOf[FollowTheLeaderState]
      val fill = List.fill(elements())(0.0)
      (elem.positions.flatMap { case (l, r) => List(l, r) } ++ fill).take(elements() - 2) ++ List(elem.directionToLeader._1, elem.directionToLeader._2)
    }
  }
}