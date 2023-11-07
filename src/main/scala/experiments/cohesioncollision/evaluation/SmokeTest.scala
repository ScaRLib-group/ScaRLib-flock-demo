package experiments.cohesioncollision.evaluation
/*
import ch.qos.logback.classic.Level
import experiments._
import it.unibo.alchemist.loader.m2m.{JVMConstructor, SimulationModel}
import it.unibo.alchemist.{AlchemistEnvironment, NoOutput, ShowEach}
import it.unibo.scarlib.core.deepRL.{CTDESystem, IndependentAgent}
import it.unibo.scarlib.core.model._
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global


object SmokeTest extends App {
  private val rewardFunction = new CohesionCollisionRF()
  LoggerFactory.getLogger(classOf[SimulationModel]).asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.OFF)
  LoggerFactory.getLogger(classOf[JVMConstructor]).asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.OFF)

  val env = new AlchemistEnvironment(
    envDefinition = s"./src/main/scala/experiments/evaluation/Smoke.yaml",
    rewardFunction = rewardFunction,
    actionSpace = CohesionCollisionActions.toSeq(),
    randomSeed = Some(42),
    outputStrategy = new ShowEach(1)
  )
  val datasetSize = 10000
  println(s"------ Simulation ${42} with ${env.currentNodeCount} agents --------")
  val dataset: ReplayBuffer[State, Action] = ReplayBuffer[State, Action](datasetSize)
  var agents: Seq[IndependentAgent] = Seq.empty
  for (n <- 0 until env.currentNodeCount)
    agents = agents :+ new IndependentAgent(n, env, CohesionCollisionActions.toSeq(), dataset)

  val where = s"./networks/network"
  val learningConfiguration = new LearningConfiguration(dqnFactory = new NNFactory, snapshotPath = "networks/network")

  new CTDESystem(agents, env, dataset, CohesionCollisionActions.toSeq(), learningConfiguration)
    .runTest(ExperimentInfo.episodeLength, PolicyNN.apply(where, StateInfo.encoding * StateInfo.neighborhood, ExperimentInfo.hiddenSize))

  System.exit(0)
}
*/