package experiments

import it.unibo.scarlib.core.model.{Action, RewardFunction, State}
import it.unibo.scarlib.core.util.AgentGlobalStore
import it.unibo.scafi.space.Point3D

class CohesionCollisionRF extends RewardFunction {

  private val targetDistance = 0.2
  private var ticks: Int = 0

  override def compute(currentState: State, action: Action, newState: State): Double = {
    ticks += 1
    if (currentState.isEmpty()) {
      0.0
    } else {
      val s = newState.asInstanceOf[CohesionCollisionState]
      val distances = computeDistancesFromNeighborhood(s)
      if (distances.isEmpty) {
        0.0
      } else {
        val cohesion = cohesionFactor(distances)
        val collision = collisionFactor(distances)
        AgentGlobalStore().put(s.agentId, "cohesion", cohesion)
        AgentGlobalStore().put(s.agentId, "collision", collision)
        AgentGlobalStore().put(s.agentId, "reward", collision + cohesion)
        cohesion + collision
      }
    }
  }

  private def cohesionFactor(distances: Seq[Double]): Double = {
    val max: Double = distances.max
    if (max > targetDistance) {
      -(max - targetDistance)
    }
    else {
      0.0
    }
  }

  private def collisionFactor(distances: Seq[Double]): Double = {
    val min: Double = distances.min
    if (min < targetDistance) {
      2 * math.log(min / targetDistance)
    }
    else {
      0.0
    }
  }

  private def computeDistancesFromNeighborhood(state: CohesionCollisionState): Seq[Double] =
    state.positions.map(p => Point3D.Zero.distance(Point3D(p._1, p._2, 0)))

}
