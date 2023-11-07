package experiments.followtheleader

import it.unibo.scafi.ScafiProgram
import it.unibo.alchemist.model.scafi.ScafiIncarnationForAlchemist._
import it.unibo.alchemist.model.implementations.molecules.SimpleMolecule
import it.unibo.alchemist.model.implementations.nodes.SimpleNodeManager
import it.unibo.scafi.space.Point3D
import FollowTheLeaderActions._
import it.unibo.scarlib.core.model.State

class FollowTheLeaderScafiAgent extends ScafiProgram with FieldUtils with BlockG {

  private lazy val leader = sense[Int]("leaderId") == mid() //Check if I'm the leader
  private lazy val leaderId = sense[Int]("leaderId")

  override protected def computeState(): State = {
    makeActions()
    node.put("isLeader", leader)
    val potentialToLeader = classicGradient(leader, nbrRange)
    val nearestToLeader = includingSelf.reifyField((nbr(potentialToLeader), nbrVector())).minBy(_._2._1)._2._2
    val positions = excludingSelf
      .reifyField(nbrVector())
      .toList
      .sortBy(_._2.distance(Point3D.Zero))
      .map(_._2)
      .map(point => (point.x, point.y))
      .take(5)

    val leaderNode = alchemistEnvironment.getNodes.get(leaderId)
    val myself = alchemistEnvironment.getNodes.get(mid())
    val distance = alchemistEnvironment.getDistanceBetweenNodes(myself, leaderNode)

    FollowTheLeaderState((nearestToLeader.x, nearestToLeader.y), positions, distance ,mid())
  }

  override protected def makeActions(): Unit = {
    val dt = 0.05
    val agent = node.asInstanceOf[SimpleNodeManager[Any]].node
    val action = agent.getConcentration(new SimpleMolecule("action"))
    if(!leader){
      if (action != null) {
        val target = action match {
          //case NoAction => // do nothing
          case North =>
            alchemistEnvironment.getPosition(agent).plus(Array(0.0, dt))

          case South =>
            alchemistEnvironment.getPosition(agent).plus(Array(0.0, -dt))

          case West =>
            alchemistEnvironment.getPosition(agent).plus(Array(-dt, 0.0))

          case East =>
            alchemistEnvironment.getPosition(agent).plus(Array(dt, 0.0))

          case NorthEast =>
            alchemistEnvironment.getPosition(agent).plus(Array(dt, dt))

          case SouthEast =>
            alchemistEnvironment.getPosition(agent).plus(Array(dt, -dt))

          case NorthWest =>
            alchemistEnvironment.getPosition(agent).plus(Array(-dt, dt))

          case SouthWest =>
            alchemistEnvironment.getPosition(agent).plus(Array(-dt, -dt))

          case StandStill =>
            alchemistEnvironment.getPosition(agent)
        }
        node.put("destination", target)
      }
    } else{
      node.put("destination", alchemistEnvironment.getPosition(agent)) //If I'm the leader I stand still
    }
  }

}