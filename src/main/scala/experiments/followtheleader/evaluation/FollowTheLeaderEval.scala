package experiments.followtheleader.evaluation

import it.unibo.alchemist.loader.m2m.{JVMConstructor, SimulationModel}
import experiments.followtheleader.{FollowTheLeaderActions, FollowTheLeaderNNFactory, FollowTheLeaderRF}
import it.unibo.alchemist.{AlchemistEnvironment, NoOutput, ShowEach}
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.Level
import experiments.followtheleader.{ExperimentInfo, StateInfo}
import it.unibo.scarlib.core.system.{CTDESystem, CTDEAgent}
import it.unibo.scarlib.core.model.{Action, LearningConfiguration, ReplayBuffer, State}
import it.unibo.scarlib.core.neuralnetwork.NeuralNetworkSnapshot

import scala.concurrent.ExecutionContext.Implicits.global
import experiments.followtheleader.FollowTheLeaderState.encoding

object FollowTheLeaderEval extends App {
  val argsMap = args.zipWithIndex.map { case (arg, i) => (i, arg) }.toMap
  val show = argsMap.get(0) match {
    case None => NoOutput
    case Some(steps) => new ShowEach(steps.toInt)
  }

  private val rewardFunction = new FollowTheLeaderRF()
  LoggerFactory.getLogger(classOf[SimulationModel]).asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.OFF)
  LoggerFactory.getLogger(classOf[JVMConstructor]).asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.OFF)

  def runEvaluationWith(count: Int, simulations: Int = 16): Unit = {
    (0 until simulations).foreach { case seed =>
      val actionSpace = FollowTheLeaderActions.all()
      val env = new AlchemistEnvironment(
        envDefinition = s"./src/main/scala/experiments/FollowTheLeader/evaluation/FollowTheLeaderEval-${count}.yaml",
        rewardFunction = rewardFunction,
        actionSpace = actionSpace,
        randomSeed = Some(seed),
        outputStrategy = show
      )
      val datasetSize = 10000
      println(s"------ Simulation ${seed} with ${env.currentNodeCount} agents --------")
      val dataset: ReplayBuffer[State, Action] = ReplayBuffer[State, Action](datasetSize)
      var agents: Seq[CTDEAgent] = Seq.empty
      for (n <- 0 until env.currentNodeCount)
        agents = agents :+ new CTDEAgent(n, env, actionSpace, dataset)

      val where = s"./networks/network-followtheleader"
      val learningConfiguration = new LearningConfiguration(dqnFactory = new FollowTheLeaderNNFactory, snapshotPath = "networks/network")

      new CTDESystem(agents, env, dataset, actionSpace, learningConfiguration)
        .runTest(ExperimentInfo.episodeLength, NeuralNetworkSnapshot(where, (StateInfo.encoding * StateInfo.neighborhood) + 2, ExperimentInfo.hiddenSize))
    }
  }

  runEvaluationWith(50)
  runEvaluationWith(100)
  runEvaluationWith(200)
  System.exit(0)
}