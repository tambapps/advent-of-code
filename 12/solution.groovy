import groovy.transform.EqualsAndHashCode
import groovy.transform.Field
import groovy.transform.Immutable

@Field List2d grid

List<String> lines = new File('input.txt').readLines()
def (int n, int m) = [lines[0].size(), lines.size()]
grid = new List2d(n: n, m: m)
lines.join('').each { grid.add(it as char) }

Position start = grid.positionsOf('S' as char).first()
Position end = grid.positionsOf('E' as char).first()
println("Start: $start\nEnd: $end")

int nbSteps = findPaths(start, end).size() - 1 // subtracting one because we included the start position in the path

println("Part 1: $nbSteps steps are required to reach the destination from $start")
print("Part 2: ")

List<Position> shortestPath = grid.positionsOf('a' as char)
    .findResults { // finding (non-null) results because some starts may not be able to reach the end
      findPaths(it, end)
    }
    .min {it.size() }
if (shortestPath.size() - 1 < nbSteps) {
  println("${shortestPath.size() - 1} steps are required to reach the destination from ${shortestPath.first()}")
} else {
  println("Same number of steps")
}

List<Position> findPaths(Position from, Position to) {
  List<Position> path = [to]
  Map<Position, Position> predecessor = bfs(from, to)
  if (!predecessor) return null

  Position crawl = to
  while (predecessor[crawl]) {
    path.add(predecessor[crawl])
    crawl = predecessor[crawl]
  }
  return path.reverse()
}

Map<Position, Position> bfs(Position from, Position to) {
  Map<Position, Position> predecessor = [:]
  Map<Position, Integer> distance = [:].withDefault { Integer.MAX_VALUE }
  Map<Position, Boolean> visited = [:].withDefault { false }

  Queue<Position> queue = new LinkedList<>()

  visited[from] = true
  distance[from] = 0
  queue << from

  while (!queue.isEmpty()) {
    Position p = queue.remove()
    for (Position adj in findReachableNeighbours(p)) {
      if (!visited[adj]) {
        visited[adj] = true
        distance[adj] = distance[p] + 1
        predecessor[adj] = p
        queue << adj
        if (adj == to) return predecessor
      }
    }
  }
  return null
}

List<Position> findReachableNeighbours(Position fromPosition) {
  List<Position> reachableNeighbours = []
  Character from = grid[fromPosition]

  // go through left, top, right and bottom neighbours
  for (def offset in [[-1, 0], [0, 1], [1, 0], [0, -1]]) {
    Position neighbourPosition = fromPosition + offset
    Character neighbour = grid[neighbourPosition]
    if (neighbour == null) continue
    if (height(from) >= height(neighbour) - 1) {
      reachableNeighbours << neighbourPosition
    }
  }
  return reachableNeighbours
}

static int height(Character from) {
  return switch (from) {
    case 'S' -> height('a' as char)
    case 'E' -> height('z' as char)
    default -> (int) (from - ('a' as char))
  }
}

@EqualsAndHashCode
@Immutable
class Position {
  int x
  int y

  Position plus(List<Integer> p) {
    return new Position(x: x + p[0], y: y + p[1])
  }

  @Override
  String toString() {
    return "(x=$x, y=$y)"
  }
}

class List2d {
  int n
  int m
  List<Character> elements = []

  Character getAt(Position p) {
    return getAt(p.x, p.y)
  }

  Character getAt(int i, int j) {
    if (i < 0 || i >= n || j < 0 || j >= m) return null
    int index = getIndex(i, j)
    return elements[index]
  }

  void add(Character e) {
    elements.add(e)
  }


  private Integer getIndex(int i, int j) {
    return j * n + i
  }

  List<Position> positionsOf(Character c) {
    List<Position> pos = []
    for (def j in 0..<m) {
      for (def i in 0..<n) {
        if (this[i, j] == c) {
          pos << new Position(x: i, y: j)
        }
      }
    }
    return pos
  }

  int size() {
    return elements.size()
  }
}