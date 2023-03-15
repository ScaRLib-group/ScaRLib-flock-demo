package experiments

import it.unibo.scarlib.core.model.State

case class CohesionCollisionState(positions: List[(Double, Double)], agentId: Int) extends State {
  override def elements(): Int = 5 * 2

  override def toSeq(): Seq[Double] = {
    val fill = List.fill(elements())(0.0)
    (positions.flatMap { case (l, r) => List(l, r) } ++ fill).take(elements())
  }

  override def isEmpty(): Boolean = false
}