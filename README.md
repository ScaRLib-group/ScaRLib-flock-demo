# ScaRLib-flock-demo

## Description

This experiment aims to create a flock of drones that moves trying to stay cohesive while avoiding collision with each other, by learning a policy by which wach agent decices how to move based on neighbours relative position. We assumed that agents position themselves in an unlimited 2D environment with a fixed neighbourhood (in this exeperiment the closest five, it is a hyper-parameter). Each agent can move in the 8 directions of a square grid (horizontally, vertically, or diagonally). The environment state, as perceived by the single agent, is the relative distance to che closest neighbours. Thanks to the Scafi integration, we was able to express it as:
```scala
val state = foldhoodPlus(Seq.empty)(_ ++ _)(Set(nbrVector))
``` 
We designed a reward function based on two factors: *collision factor* and *cohesion factor*. We aim to learn a policy by which agents, initially spread in a very sparse way in the environment, moves toward each other until reaching approximate $\delta$ distance without colliding. ultimately forming one or many close groups. The collision factor comes into play when the distance is less than $\delta$, and exponentially weights the distance d relative to its cloesest neighbour: $collision = \exp(-\frac{d}{\delta})$ if $d < \delta$, otherwise $collsion = 0$.

In this way, when the negative factor is taken into account: the system will tend to move nodes away from each other. However, if only this factor were used, the system would be disorganized. This is where the cohesion fcator comes in. Given the neighbour with the maximum distance $D$, it linearly adjusts the distance relative to the node being evaluated by function: $cohesion = -(D-\delta)$ if $d > \delta$, otherwise $cohesion = 0$. The overall reward function is defined as the sum of these two factors.

## Project Structure

All the files needed to describe the experiment are in the `src/main/scala/experiment` folder. As described in ScaRLib, to run a learning the user must define: 
- The action space
- The reward function
- The state
- The neural network used to approximate the Q-function
- The scafi logic
- The alchemist specification

Finally all these elements are merged to create the learning system in the file `CohesionCollisionTraining.scala`.

## How to use it 

In order to launch the learning only one change is needed, you must specify the path on where the snapshots of the policy will be saved. You can do this editing the following line of code in the file `CohesionCollisionTraining.scala`:
```scala
private val learningConfiguration = new LearningConfiguration(dqnFactory = new NNFactory, snapshotPath = "path-to-snapshot-folder")
```
After making this change it is possible to run the learning using a pre-configured Gradle task launching the following command:
```powershell
./gradlew runCohesionAndCollisionTraining
```
Due to the usage of ScalaPy there might be the need for some extra-configuration, all the details can be found [here](https://scalapy.dev/docs/) (sections: `Execution` and `Virtualenv`). Tip: if if you don't want to configure environment variables on your PC you can pass the required arguments directly to the gradle task adding the following code (in `build.gradle.kts` file):
```kotlin
jvmArgs(
  "-Dscalapy.python.library=${pyhtonVersion}",
  //Other required parameters...
)
```

