package experiments.followtheleader

import it.unibo.scarlib.core.neuralnetwork.{DQNAbstractFactory, SimpleSequentialDQN}
import me.shadaj.scalapy.py
import StateInfo._

class FollowTheLeaderNNFactory extends DQNAbstractFactory[py.Dynamic]{
  override def createNN(): py.Dynamic =
    SimpleSequentialDQN((neighborhood * encoding)+2, 64, FollowTheLeaderActions.all().size)
}