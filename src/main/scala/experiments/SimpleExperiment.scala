package experiments

import it.unibo.scarlib.dsl.DSL.*
import it.unibo.scarlib.core.model.{Action, RewardFunction, State, Environment, ReplayBuffer}


object ActionSpace {

  case object North extends Action
  case object South extends Action
  case object East extends Action
  case object West extends Action

  def toSeq() = Seq(North, South, East, West)

}


case class SimpleRewardFunction() extends RewardFunction {

  override def compute(currentState: State, action: Action, newState: State): Double = ???

}

case class SimpleState() extends State {

  override def elements(): Int = ???

  override def toSeq(): Seq[Double] = ???

  override def isEmpty(): Boolean = false

}

case class SimpleEnvironment(rewardFunction: RewardFunction, actionSpace: Seq[Action]) extends Environment(rewardFunction, actionSpace){
  override def step(action: Action, agentId: Int): (Double, State) = ???

  override def observe(agentId: Int): State = ???

  override def reset(): Unit = ???

  override def log(): Unit = ???

  override def logOnFile(): Unit = ???
}


object SimpleExperiment extends App {

  val system = learningSystem {
    rewardFunction { SimpleRewardFunction() }
    actions { ActionSpace.toSeq() }
    dataset { ReplayBuffer[State, Action](10000) }
    agents { 50 }
    environment { "experiments.SimpleEnvironment" }
  }


}
