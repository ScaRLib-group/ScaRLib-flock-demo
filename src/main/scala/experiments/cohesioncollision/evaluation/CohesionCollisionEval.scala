package experiments.cohesioncollision.evaluation

import ch.qos.logback.classic.Level
import experiments.cohesioncollision.{CohesionCollisionActions, CohesionCollisionRF, ExperimentInfo, NNFactory, StateInfo}
import it.unibo.alchemist.{AlchemistEnvironment, NoOutput, ShowEach}
import it.unibo.alchemist.loader.m2m.{JVMConstructor, SimulationModel}
import it.unibo.scarlib.core.model._
import org.slf4j.LoggerFactory
import it.unibo.scarlib.dsl.DSL._
import it.unibo.scarlib.core.model.Environment

import scala.concurrent.ExecutionContext.Implicits.global
import experiments.cohesioncollision.CohesionCollisionState.encoding
import it.unibo.scarlib.core.neuralnetwork.NeuralNetworkSnapshot

object CohesionCollisionEval extends App {

  val argsMap = args.zipWithIndex.map { case (arg, i) => (i, arg) }.toMap
  val show = argsMap.get(0) match {
    case None => NoOutput
    case Some(steps) => new ShowEach(steps.toInt)
  }

  LoggerFactory.getLogger(classOf[SimulationModel]).asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.OFF)
  LoggerFactory.getLogger(classOf[JVMConstructor]).asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.OFF)

  def runEvaluationWith(count: Int, simulations: Int = 16): Unit = {
    (0 until simulations).foreach { case seed =>

      println(s"------ Simulation ${seed} with ${count} agents --------")

      implicit val configuration: Environment => Unit = (e: Environment) => {
        val env = e.asInstanceOf[AlchemistEnvironment]
        env.setOutputStrategy(show)
        env.setRandomSeed(Some(42))
        env.setEnvironmentDefinition(s"./src/main/scala/experiments/cohesioncollision/evaluation/CohesionAndCollisionEval-${count}.yaml")
      }

      val where = s"./networks/network"

      val system = CTDELearningSystem {
        rewardFunction { new CohesionCollisionRF() }
        actionSpace { CohesionCollisionActions.toSeq() }
        dataset { ReplayBuffer[State, Action](10000) }
        agents { count }
        learningConfiguration { LearningConfiguration(dqnFactory = new NNFactory, snapshotPath = where) }
        environment { "it.unibo.alchemist.AlchemistEnvironment" }
      }

      system
        .runTest(ExperimentInfo.episodeLength, NeuralNetworkSnapshot(where, StateInfo.encoding * StateInfo.neighborhood, ExperimentInfo.hiddenSize))
    }
  }

  runEvaluationWith(50)
  runEvaluationWith(100)
  runEvaluationWith(200)
  System.exit(0)
}
