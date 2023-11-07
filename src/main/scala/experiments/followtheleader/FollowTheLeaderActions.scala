package experiments.followtheleader

import it.unibo.scarlib.core.model.Action
import scala.util.Random

object FollowTheLeaderActions {

  final case object North extends Action
  final case object South extends Action
  final case object East extends Action
  final case object West extends Action
  final case object NorthEast extends Action
  final case object NorthWest extends Action
  final case object SouthWest extends Action
  final case object SouthEast extends Action
  final case object StandStill extends Action

  def all(): Seq[Action] =
    Seq(North, South, East, West, NorthEast, NorthWest, SouthEast, SouthWest, StandStill)

  def sample(): Action =
    Random.shuffle(all().take(all().size - 1)).head //Without StandStill

}