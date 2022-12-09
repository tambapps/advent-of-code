// we don't need a grid as we don't care about the position itself for the answer
// we just need to number of unique positions encountered
Knot startPosition = new Knot(x: 0, y: 0) // start position doesn't really matter either. let's start at (0, 0)
List<Knot> knots = (0..<10).collect { startPosition.copy() } // initializing 10 new knots

Set<Knot> part1TailVisits = [startPosition.copy()]
Set<Knot> part2TailVisits = [startPosition.copy()]

for (def line in new File('input.txt').readLines()) {
  def (String d, String numberString) = line.split("\\s")
  def (Direction direction, int n) = [Direction.values().find { it.name().startsWith(d) }, numberString.toInteger()]
  n.times {
    // first knot is head
    knots.first().move(direction)
    for (i in 1..<knots.size()) {
      knots[i].follow(knots[i - 1])
    }
    part1TailVisits << knots[1].copy()
    part2TailVisits << knots.last().copy()
  }
}
println("Part 1: tail visited ${part1TailVisits.size()} different position")
println("Part 2: tail visited ${part2TailVisits.size()} different position")

enum Direction {
  LEFT(-1, 0), UP(0, 1), RIGHT(1, 0), DOWN(0, -1);

  final int moveX
  final int moveY

  Direction(int moveX, int moveY) {
    this.moveX = moveX
    this.moveY = moveY
  }
}

@groovy.transform.EqualsAndHashCode
class Knot {
  int x
  int y

  void move(int offsetX, int offsetY) {
    x += offsetX
    y += offsetY
  }

  void move(Direction direction) {
    move(direction.moveX, direction.moveY)
  }

  boolean isNextTo(Knot other) {
    return other != this && Math.abs(x - other.x) <= 1 && Math.abs(y - other.y) <= 1
  }

  Knot copy() {
    return new Knot(x: x, y: y) // useful, as this object is not immutable
  }

  void follow(Knot head) {
    def (int offsetX, int offsetY) = [head.x - x, head.y - y]
    if (Math.abs(offsetX) == 2 && offsetY == 0
        || offsetX == 0 && Math.abs(offsetY) == 2
        || !isNextTo(head) && x != head.x && y != head.y) {
      // signum to make sure we always move by 1 or 0
      move(Math.signum(offsetX) as int, Math.signum(offsetY) as int)
    }
  }
}


