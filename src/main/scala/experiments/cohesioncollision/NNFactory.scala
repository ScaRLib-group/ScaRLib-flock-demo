package experiments.cohesioncollision

import experiments.cohesioncollision.StateInfo._
import it.unibo.scarlib.core.neuralnetwork.{DQNAbstractFactory, SimpleSequentialDQN}
import me.shadaj.scalapy.py

class NNFactory extends DQNAbstractFactory[py.Dynamic] {

  override def createNN(): py.Dynamic = {
    SimpleSequentialDQN(neighborhood * encoding, 64, CohesionCollisionActions.toSeq().size)
  }

}
