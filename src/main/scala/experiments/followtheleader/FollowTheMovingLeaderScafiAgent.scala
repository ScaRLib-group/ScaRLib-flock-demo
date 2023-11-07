package experiments.followtheleader

import it.unibo.scafi.ScafiProgram
import it.unibo.alchemist.model.scafi.ScafiIncarnationForAlchemist._
import it.unibo.alchemist.model.implementations.molecules.SimpleMolecule
import it.unibo.alchemist.model.implementations.nodes.SimpleNodeManager
import it.unibo.scafi.space.{Point2D, Point3D}
import FollowTheLeaderActions._
import it.unibo.scarlib.core.model.State

class FollowTheMovingLeaderScafiAgent extends ScafiProgram with FieldUtils with BlockG {

  private lazy val leader = sense[Int]("leaderId") == mid() //Check if I'm the leader
  private lazy val leaderId = sense[Int]("leaderId")
  private val consecutiveMoves = 100
  private var actualMoves = 0
  private var leaderMove = FollowTheLeaderActions.sample()

  override protected def computeState(): State = {
    makeActions()
    //node.put("isLeader", leader)
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

    if(leader){
      node.put("isLeader", 1)
    }

    node.put(
      "distances",
      positions
        .map(x => Point2D(x._1, x._2))
        .map(Point3D.Zero.distance(_))
        .sum / positions.size
    ) // for data exporting

    node.put(
      "distanceToLeader",
      distance
    ) // for data exporting

    FollowTheLeaderState((nearestToLeader.x, nearestToLeader.y), positions, distance, mid())
  }

  override protected def makeActions(): Unit = {
    val dt = 0.05
    val agent = node.asInstanceOf[SimpleNodeManager[Any]].node
    val action = agent.getConcentration(new SimpleMolecule("action"))

    if (action != null) {
      val target = action match {
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

    if(leader) {
      if(actualMoves < consecutiveMoves) {
        actualMoves += 1
        node.put("action", leaderMove)
      } else {
        actualMoves = 0
        leaderMove = FollowTheLeaderActions.sample()
        node.put("action", leaderMove)
      }
    }

  }
}