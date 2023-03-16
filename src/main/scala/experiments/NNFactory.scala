package experiments

import it.unibo.scarlib.core.neuralnetwork.{DQNAbstractFactory, SimpleSequentialDQN}
import me.shadaj.scalapy.py

class NNFactory extends DQNAbstractFactory[py.Dynamic] {

  override def createNN(): py.Dynamic = SimpleSequentialDQN(0,0,0)

}
