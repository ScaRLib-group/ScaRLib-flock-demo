# ScaRLib-flock-demo

## Description

The objective of this experiment is to develop a group of drones that have the ability to move together while avoiding collisions with each other. This is achieved through the implementation of a policy that enables each drone to decide its movement based on the relative position of its neighbors. The experiment is conducted in a 2D environment with unlimited space, where each drone's neighborhood is limited to the five closest neighbors (a hyper-parameter). The drones are capable of moving in eight different directions in a square grid, including horizontal, vertical, and diagonal movements. The environment state is determined by the relative distance between the drones and their closest neighbors, as perceived by the individual drone. By integrating Scafi, we are able to express this concept effectively.
```scala
val state = foldhoodPlus(Seq.empty)(_ ++ _)(Set(nbrVector))
``` 
We designed a reward function based on two factors: *collision factor* and *cohesion factor*. We aim to learn a policy by which agents, initially spread in a very sparse way in the environment, move toward each other until reaching approximate $\delta$ distance without colliding. Ultimately forming one or many close groups. The collision factor comes into play when the distance is less than $\delta$, and exponentially weights the distance d relative to its closest neighbour: $collision = \exp(-\frac{d}{\delta})$ if $d < \delta$, otherwise $collision = 0$.

In this way, when the negative factor is taken into account: the system will tend to move nodes away from each other. However, if only this factor were used, the system would be disorganized. This is where the cohesion facator comes in. Given the neighbour with the maximum distance $D$, it linearly adjusts the distance relative to the node being evaluated by function: $cohesion = -(D-\delta)$ if $d > \delta$, otherwise $cohesion = 0$. The overall reward function is defined as the sum of these two factors.

## Project Structure

All the files needed to describe the experiment are in the `src/main/scala/experiment` folder. As described in ScaRLib, to run a learning the user must define: 
- The action space
- The reward function
- The state
- The neural network used to approximate the Q-function
- The scafi logic
- The alchemist specification

Finally, all these elements are merged to create the learning system in the file `CohesionCollisionTraining.scala`.

## Preliminaries 

Due to the usage of ScalaPy there might be the need for some extra-configuration, all the details can be found [here](https://scalapy.dev/docs/) (sections: `Execution` and `Virtualenv`). Tip: if if you don't want to configure environment variables on your PC you can pass the required arguments directly to the gradle task adding the following code (in `build.gradle.kts` file):
```kotlin
jvmArgs(
    "-Dscalapy.python.library=${pyhtonVersion}",
    //Other required parameters...
)
```
Before running the learning you must install the following dependencies:
```shell
pip install -r requirements.txt
```

## How to use it 

In order to launch the learning only one change is needed, you must specify the path on where the snapshots of the policy will be saved. You can do this editing the following line of code in the file `CohesionCollisionTraining.scala`:
```scala
private val learningConfiguration = new LearningConfiguration(dqnFactory = new NNFactory, snapshotPath = "path-to-snapshot-folder")
```
After making this change it is possible to run the learning using a pre-configured Gradle task launching the following command:
```powershell
./gradlew runCohesionAndCollisionTraining
```
If you want to see the dynamics of the system during the learning you can use the following command:
```powershell
./gradlew runCohesionAndCollisionTraining
```
You can follow the progess of learning using `Tensorboard` by running the following command:
```powershell
tensorboard --logdir=runs
```
This could take a while (hours in a modern machine). So we already upload the last network snapshot in the folder `network`.

To verify the performance of the policy you can run the following command the `evaluation` tasks with:
```aidl
./gradlew runCohesionAndCollisionEvaluation
```
If you want to see the GUI you can run the following command:
```aidl
./gradlew runCohesionAndCollisionEvaluationGui
```
This will produce the data needed to plot the graphs in the `data` folder. 
To plot the graphs you can run the following command:
```aidl
python plotter.py
```
This will create the graphs in the `charts` folder.