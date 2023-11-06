package experiments

import it.unibo.alchemist.CollectiveAction
import it.unibo.alchemist.model.implementations.molecules.SimpleMolecule
import it.unibo.alchemist.model.interfaces.{Environment, Node, Position, TimeDistribution}

class CohesionCollisionCollectiveAction[T, P <: Position[P]](
    environment: Environment[T, P],
    distribution: TimeDistribution[T])
  extends CollectiveAction[T, P](environment, distribution) {

  override protected def nodeAction(node: Node[T]): Unit = {
    val destination = node.getConcentration(new SimpleMolecule("destination"))
    if(destination != null){
      environment.moveNodeToPosition(node, destination.asInstanceOf[P])
    }
  }
}
