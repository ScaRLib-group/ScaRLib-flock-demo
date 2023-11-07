package experiments.cohesioncollision.evaluation

import ch.qos.logback.classic.Level
import experiments.cohesioncollision.{CohesionCollisionActions, CohesionCollisionRF, ExperimentInfo, NNFactory, StateInfo}
import it.unibo.alchemist.{AlchemistEnvironment, NoOutput, ShowEach}
import it.unibo.alchemist.loader.m2m.{JVMConstructor, SimulationModel}
import it.unibo.scarlib.core.system.{CTDEAgent, CTDESystem}
import it.unibo.scarlib.core.model._
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import experiments.cohesioncollision.CohesionCollisionState.encoding
import it.unibo.scarlib.core.neuralnetwork.NeuralNetworkSnapshot

object CohesionCollisionEval extends App {
  val argsMap = args.zipWithIndex.map { case (arg, i) => (i, arg) }.toMap
  val show = argsMap.get(0) match {
    case None => NoOutput
    case Some(steps) => new ShowEach(steps.toInt)
  }
  println(show)
  private val rewardFunction = new CohesionCollisionRF()
  LoggerFactory.getLogger(classOf[SimulationModel]).asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.OFF)
  LoggerFactory.getLogger(classOf[JVMConstructor]).asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.OFF)

  def runEvaluationWith(count: Int, simulations: Int = 16): Unit = {
    (0 until simulations).foreach { case seed =>
      val env = new AlchemistEnvironment(
        envDefinition = s"./src/main/scala/experiments/evaluation/CohesionAndCollisionEval-${count}.yaml",
        rewardFunction = rewardFunction,
        actionSpace = CohesionCollisionActions.toSeq(),
        randomSeed = Some(seed),
        outputStrategy = show
      )
      val datasetSize = 10000
      println(s"------ Simulation ${seed} with ${env.currentNodeCount} agents --------")
      val dataset: ReplayBuffer[State, Action] = ReplayBuffer[State, Action](datasetSize)
      var agents: Seq[CTDEAgent] = Seq.empty
      for (n <- 0 until env.currentNodeCount)
        agents = agents :+ new CTDEAgent(n, env, CohesionCollisionActions.toSeq(), dataset)

      val where = s"./networks/network"
      val learningConfiguration = new LearningConfiguration(dqnFactory = new NNFactory, snapshotPath = "networks/network")

      new CTDESystem(agents, env, dataset, CohesionCollisionActions.toSeq(), learningConfiguration)
        .runTest(ExperimentInfo.episodeLength, NeuralNetworkSnapshot(where, StateInfo.encoding * StateInfo.neighborhood, ExperimentInfo.hiddenSize))
    }
  }
  runEvaluationWith(50)
  runEvaluationWith(100)
  runEvaluationWith(200)
  System.exit(0)
}
