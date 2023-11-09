package experiments.followtheleader

import it.unibo.scafi.space.Point3D
import it.unibo.scarlib.core.model.{Action, RewardFunction, State}
import it.unibo.scarlib.core.util.AgentGlobalStore

class FollowTheLeaderRF extends RewardFunction {

  private val targetDistance = 0.2

  override def compute(currentState: State, action: Action, newState: State): Double = {
    if(currentState.isEmpty()){
      0.0
    } else {
      val s = newState.asInstanceOf[FollowTheLeaderState]
      val distances = computeDistancesFromNeighborhood(s)
      if(distances.isEmpty){
        0.0
      } else{
        val collision = collisionFactor(distances)
        val distanceFromLeader = - s.distanceFromLeader
        val reward = collision + distanceFromLeader
        AgentGlobalStore().put(s.agentId, "distanceFromLeader", distanceFromLeader)
        AgentGlobalStore().put(s.agentId, "collision", collision)
        AgentGlobalStore().put(s.agentId, "reward", reward)
        reward
      }
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

  private def computeDistancesFromNeighborhood(state: FollowTheLeaderState): Seq[Double] =
    state.positions.map(p => Point3D.Zero.distance(Point3D(p._1, p._2, 0)))
}