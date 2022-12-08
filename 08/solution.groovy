// note that in this script I considered the top to be y=0 (and bottom to be y=m-1)
List<String> lines = new File('input.txt').readLines()
def (int n, int m) = [lines[0].size(), lines.size()]
List2d list = new List2d(n: n, m: m)
lines.join('').each { list.add((int) (it.charAt(0) - '0'.charAt(0))) }

List<Integer> candidates = [] // part 1
int maxScenicScore = 0 // part 2
for(i in 0..<n) {
  for (j in 0..<m) {
    if (isVisible(list, i, j, Direction.LEFT) || isVisible(list, i, j, Direction.TOP) 
        || isVisible(list, i, j, Direction.RIGHT) || isVisible(list, i, j, Direction.BOTTOM)) {
      candidates << list[i, j]
    }
    maxScenicScore = Math.max(maxScenicScore, scenicScore(list, i, j))
  }
}

println "Part 1: There are ${candidates.size()} candidates"
println "Part 2: The max scenic score is $maxScenicScore"

boolean isVisible(List2d list, int posX, int posY, Direction direction) {
  // we want to look from the direction edge, to (posX, posY)
  def (rangeX, rangeY) = switch(direction) {
    case Direction.LEFT -> [0..(list.n - 1), posY..posY]
    case Direction.TOP ->  [posX..posX, 0..(list.m - 1)]
    case Direction.RIGHT -> [(list.n - 1)..0, posY..posY]
    case Direction.BOTTOM -> [posX..posX, (list.m - 1)..0]
  }

  int value = list[posX, posY]
  for (def i in rangeX) {
    for (def j in rangeY) {
      if (i == posX && j == posY) return true
      if (list[i, j] >= value) return false
    }
  }
  throw new RuntimeException("Shouldn't happen")
}

int scenicScore(List2d list, int i, int j) {
  return directionScenicScore(list, i, j, Direction.LEFT) * directionScenicScore(list, i, j, Direction.TOP)
    * directionScenicScore(list, i, j, Direction.RIGHT) * directionScenicScore(list, i, j, Direction.BOTTOM)
}

int directionScenicScore(List2d list, int posX, int posY, Direction direction) {
  // we want to look from (posX, posY), to the pointed direction
  def (rangeX, rangeY) = switch(direction) {
    case Direction.LEFT -> [(posX - 1)..0, posY..posY]
    case Direction.TOP ->  [posX..posX, (posY + 1)..(list.m - 1)]
    case Direction.RIGHT -> [(posX + 1)..(list.n - 1), posY..posY]
    case Direction.BOTTOM -> [posX..posX, (posY - 1)..0]
  }

  if (posX == 0 && direction == Direction.LEFT || posX == list.n - 1 && direction == Direction.RIGHT
    || posY == list.m - 1 && direction == Direction.TOP || posY == 0 && direction == Direction.BOTTOM) {
    return 0 // if is on edge and checking the direction pointing further the edge, viewing distance is 0
  }

  int value = list[posX, posY]
  int viewingDistance = 0
  for (def i in rangeX) {
    for (def j in rangeY) {
      viewingDistance++
      if (list[i, j] >= value) return viewingDistance
    }
  }
  return viewingDistance
}

enum Direction { LEFT, TOP, RIGHT, BOTTOM }
class List2d {
  int n
  int m
  List<Integer> elements = []


  int getAt(int i, int j) {
    return elements[getIndex(i, j)]
  }

  void add(int e) {
    elements.add(e)
  }

  void putAt(int i, int value) {
    elements[i] = value
  }


  int getIndex(int i, int j) {
    return j * n + i
  }
}
