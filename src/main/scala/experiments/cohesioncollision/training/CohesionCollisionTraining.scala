package experiments.cohesioncollision.training

import ch.qos.logback.classic.Level
import experiments.cohesioncollision.{CohesionCollisionActions, CohesionCollisionRF, CohesionCollisionState, ExperimentInfo, NNFactory}
import it.unibo.alchemist.{AlchemistEnvironment, NoOutput, ShowEach}
import it.unibo.alchemist.loader.m2m.{JVMConstructor, SimulationModel}
import it.unibo.scarlib.core.system.{CTDEAgent, CTDESystem}
import it.unibo.scarlib.core.model.{Action, LearningConfiguration, ReplayBuffer, State}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import experiments.cohesioncollision.CohesionCollisionState.encoding

object CohesionCollisionTraining extends App {
  val argsMap = args.zipWithIndex.map { case (arg, i) => (i, arg) }.toMap
  val show = argsMap.get(0) match {
    case None => NoOutput
    case Some(steps) => new ShowEach(steps.toInt)
  }
  private val rewardFunction = new CohesionCollisionRF()
  LoggerFactory.getLogger(classOf[SimulationModel]).asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.OFF)
  LoggerFactory.getLogger(classOf[JVMConstructor]).asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.OFF)

  private val actionSpace = CohesionCollisionActions.toSeq()

  private val env = new AlchemistEnvironment(
    rewardFunction,
    actionSpace,
    "./src/main/scala/experiments/training/CohesionCollisionSimulation.yaml",
    randomSeed = Some(42),
    outputStrategy = show
  )

  private val datasetSize = 10000

  private val dataset: ReplayBuffer[State, Action] = ReplayBuffer[State, Action](datasetSize)

  private var agents: Seq[CTDEAgent] = Seq.empty
  for (n <- 0 until env.currentNodeCount)
    agents = agents :+ new CTDEAgent(n, env, actionSpace, dataset)

  private val learningConfiguration = new LearningConfiguration(dqnFactory = new NNFactory, snapshotPath = "networks/network")

  new CTDESystem(agents, env, dataset, actionSpace, learningConfiguration).learn(ExperimentInfo.episodes, ExperimentInfo.episodeLength)
  System.exit(0)
}
