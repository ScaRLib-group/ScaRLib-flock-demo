# ScaRLib-flock-demo

## Description

This experiment aims to create a flock of drones that moves trying to stay cohesive while avoiding collision with each other, by learning a policy by which wach agent decices how to move based on neighbours relative position. We assumed that agents position themselves in an unlimited 2D environment with a fixed neighbourhood (in this exeperiment the closest five, it is a hyper-parameter). Each agent can move in the 8 directions of a square grid (horizontally, vertically, or diagonally). The environment state, as perceived by the single agent, is the relative distance to che closest neighbours. Thanks to the Scafi integration, we was able to express it as:
```scala
val state = foldhoodPlus(Seq.empty)(_ ++ _)(Set(nbrVector))
``` 
We designed a reward function based on two factors: *collision factor* and *cohesion factor*. We aim to learn a policy by which agents, initially spread in a very sparse way in the environment, moves toward each other until reaching approximate $\delta$ distance without colliding. ultimately forming one or many close groups. The collision factor comes into play when the distance is less than $\delta$, and exponentially weights the distance d relative to its cloesest neighbour: $collision = \exp(-\frac{d}{\delta})$ if $d < \delta$, otherwise $collsion = 0$.

In this way, when the negative factor is taken into account: the system will tend to move nodes away from each other. However, if only this factor were used, the system would be disorganized. This is where the cohesion fcator comes in. Given the neighbour with the maximum distance $D$, it linearly adjusts the distance relative to the node being evaluated by function: $cohesion = -(D-\delta)$ if $d > \delta$, otherwise $cohesion = 0$. The overall reward function is defined as the sum of these two factors.

## Project Structure

## How to use it 

In order to launch the learning only one elements is needed, you must specify the path on where the snapshot of the policy will be saved. You can do this editing the following line of code in the file `CohesionCollisionTraining.scala`:
```scala
private val learningConfiguration = new LearningConfiguration(dqnFactory = new NNFactory, snapshotPath = "path-to-snapshot-folder")
```
