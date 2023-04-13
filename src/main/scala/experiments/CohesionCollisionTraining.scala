package experiments

import it.unibo.alchemist.AlchemistEnvironment
import it.unibo.alchemist.loader.m2m.{JVMConstructor, SimulationModel}
import it.unibo.scarlib.core.deepRL.{CTDESystem, IndependentAgent}
import it.unibo.scarlib.core.model.{Action, LearningConfiguration, ReplayBuffer, State}
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.Level
import scala.concurrent.ExecutionContext.Implicits.global

object CohesionCollisionTraining extends App {

  private val rewardFunction = new CohesionCollisionRF()
  LoggerFactory.getLogger(classOf[SimulationModel]).asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.OFF)
  LoggerFactory.getLogger(classOf[JVMConstructor]).asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.OFF)

  private val actionSpace = CohesionCollisionActions.toSeq()

  private val env = new AlchemistEnvironment(
    rewardFunction,
    actionSpace,
    "./src/main/scala/experiments/CohesionCollisionSimulation.yaml",
    randomSeed = Some(42)
  )

  private val datasetSize = 10000

  private val dataset: ReplayBuffer[State, Action] = ReplayBuffer[State, Action](datasetSize)

  private var agents: Seq[IndependentAgent] = Seq.empty
  for (n <- 0 to env.currentNodeCount)
    agents = agents :+ new IndependentAgent(n, env, actionSpace, dataset)

  private val learningConfiguration = new LearningConfiguration(dqnFactory = new NNFactory, snapshotPath = "path-to-snapshot-folder")

  new CTDESystem(agents, env, dataset, actionSpace, learningConfiguration).learn(1000, 100)

}