package experiments.cohesioncollision.training

import ch.qos.logback.classic.Level
import experiments.cohesioncollision.{CohesionCollisionActions, CohesionCollisionRF, ExperimentInfo, NNFactory}
import it.unibo.alchemist.{AlchemistEnvironment, NoOutput, ShowEach}
import it.unibo.alchemist.loader.m2m.{JVMConstructor, SimulationModel}
import it.unibo.scarlib.core.model.Environment
import it.unibo.scarlib.core.model.{Action, LearningConfiguration, ReplayBuffer, State}
import org.slf4j.LoggerFactory
import it.unibo.scarlib.dsl.DSL._

import scala.concurrent.ExecutionContext.Implicits.global
import experiments.cohesioncollision.CohesionCollisionState.encoding

object CohesionCollisionTraining extends App {

  val argsMap = args.zipWithIndex.map { case (arg, i) => (i, arg) }.toMap
  val show = argsMap.get(0) match {
    case None => NoOutput
    case Some(steps) => new ShowEach(steps.toInt)
  }

  LoggerFactory.getLogger(classOf[SimulationModel]).asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.OFF)
  LoggerFactory.getLogger(classOf[JVMConstructor]).asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.OFF)

  private implicit val configuration: Environment => Unit = (e: Environment) => {
    val env = e.asInstanceOf[AlchemistEnvironment]
    env.setOutputStrategy(show)
    env.setRandomSeed(Some(42))
    env.setEnvironmentDefinition("./src/main/scala/experiments/cohesioncollision/training/CohesionCollisionSimulation.yaml")
  }

  val system = CTDELearningSystem {
    rewardFunction { new CohesionCollisionRF() }
    actionSpace { CohesionCollisionActions.toSeq() }
    dataset { ReplayBuffer[State, Action](10000) }
    agents { 50 }
    learningConfiguration { LearningConfiguration(dqnFactory = new NNFactory, snapshotPath = "networks/network") }
    environment { "it.unibo.alchemist.AlchemistEnvironment" }
  }

  system.learn(ExperimentInfo.episodes, ExperimentInfo.episodeLength)

  System.exit(0)
}