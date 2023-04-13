package experiments

import it.unibo.scarlib.core.neuralnetwork.{DQNAbstractFactory, SimpleSequentialDQN}
import me.shadaj.scalapy.py
import StateInfo._

class NNFactory extends DQNAbstractFactory[py.Dynamic] {

  override def createNN(): py.Dynamic = {
    SimpleSequentialDQN(neighborhood * encoding, 64, CohesionCollisionActions.toSeq().size)
  }

}
