package experiments.followtheleader.training

import it.unibo.alchemist.loader.m2m.{JVMConstructor, SimulationModel}
import it.unibo.alchemist.{AlchemistEnvironment, NoOutput, ShowEach}
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.Level
import experiments.followtheleader.{ExperimentInfo, FollowTheLeaderActions, FollowTheLeaderNNFactory, FollowTheLeaderRF}
import it.unibo.scarlib.core.system.{CTDEAgent, CTDESystem}
import it.unibo.scarlib.core.model.{Action, LearningConfiguration, ReplayBuffer, State}

import scala.concurrent.ExecutionContext.Implicits.global
import experiments.followtheleader.FollowTheLeaderState.encoding


object FollowTheLeaderTraining extends App {
  val argsMap = args.zipWithIndex.map { case (arg, i) => (i, arg) }.toMap
  val show = argsMap.get(0) match {
    case None => NoOutput
    case Some(steps) => new ShowEach(steps.toInt)
  }

  private val rewardFunction = new FollowTheLeaderRF()
  LoggerFactory.getLogger(classOf[SimulationModel]).asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.OFF)
  LoggerFactory.getLogger(classOf[JVMConstructor]).asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.OFF)

  private val actionSpace = FollowTheLeaderActions.all()

  private val env = new AlchemistEnvironment(
    rewardFunction,
    actionSpace,
    "./src/main/scala/experiments/followtheleader/FollowTheLeaderSimulation.yaml",
    randomSeed = Some(42),
    outputStrategy = show
  )

  private val datasetSize = 10000

  private val dataset: ReplayBuffer[State, Action] = ReplayBuffer[State, Action](datasetSize)

  private var agents: Seq[CTDEAgent] = Seq.empty

  for (n <- 0 until env.currentNodeCount)
    agents = agents :+ new CTDEAgent(n, env, actionSpace, dataset)

  private val learningConfiguration = new LearningConfiguration(dqnFactory = new FollowTheLeaderNNFactory(), snapshotPath = "networks/network")

  new CTDESystem(agents, env, dataset, actionSpace, learningConfiguration).learn(ExperimentInfo.episodes, ExperimentInfo.episodeLength)
  System.exit(0)
}