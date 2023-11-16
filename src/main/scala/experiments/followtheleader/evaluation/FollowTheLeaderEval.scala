package experiments.followtheleader.evaluation

import it.unibo.alchemist.loader.m2m.{JVMConstructor, SimulationModel}
import experiments.followtheleader.{ExperimentInfo, FollowTheLeaderActions, FollowTheLeaderNNFactory, FollowTheLeaderRF, StateInfo}
import it.unibo.alchemist.{AlchemistEnvironment, NoOutput, ShowEach}
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.Level
import it.unibo.scarlib.core.model.{Action, Environment, LearningConfiguration, ReplayBuffer, State}
import it.unibo.scarlib.core.neuralnetwork.NeuralNetworkSnapshot
import it.unibo.scarlib.dsl.DSL._

import scala.concurrent.ExecutionContext.Implicits.global
import experiments.followtheleader.FollowTheLeaderState.encoding


object FollowTheLeaderEval extends App {

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
        env.setEnvironmentDefinition(s"./src/main/scala/experiments/followtheleader/evaluation/CohesionAndCollisionEval-${count}.yaml")
      }

      val where = s"./networks/network"

      val system = CTDELearningSystem {
        rewardFunction { new FollowTheLeaderRF() }
        actionSpace { FollowTheLeaderActions.all() }
        dataset { ReplayBuffer[State, Action](10000) }
        agents { count }
        learningConfiguration { LearningConfiguration(dqnFactory = new FollowTheLeaderNNFactory, snapshotPath = where) }
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