// execute it with --compile-static for better performance
// weirdly it gives the expectedAnswer minus 1 for the example input, but it worked fine for my input
import groovy.transform.EqualsAndHashCode
import groovy.transform.Field
import groovy.transform.Immutable
import groovy.transform.ToString

import java.lang.Long as MyNumber

@Field final String EMPTY = '.'
@Field final String ROCK = '#'
@Field final String LEFT = '<'
@Field final String RIGHT = '>'
@Field final int WIDTH = 7

@EqualsAndHashCode
@Immutable
class Position {
  MyNumber x
  MyNumber y
}
// for this problem, we'll consider y=0 as the bottom, and positive values to be upper
@Field Map<MyNumber, Map<MyNumber, String>> tetris = [:].withDefault { def oY ->
  MyNumber y = (MyNumber) oY
  [:].withDefault { def oX ->
    MyNumber x = (MyNumber) oX
    y < 0 || x < 0 || x >= WIDTH ? '|' : EMPTY
  }
}
List<String> gasDirections = new File('input.txt').text.collect {it.toString() }

MyNumber rockI
MyNumber gasI
List<MyNumber> highestRockYs
final int startX = 2

void move(List<Position> rp, int offsetX, int offsetY) {
  rp.replaceAll {Position p -> new Position(x: p.x + offsetX, y: p.y + offsetY) }
}

boolean canMove(List<Position> rp, int offsetX, int offsetY) {
  List<Position> rockPositions = rp.collect()
  move(rockPositions, offsetX, offsetY)
  Map<MyNumber, Map<MyNumber, String>> tetris = this.tetris // don't know why it's needed for static compilation
  return rockPositions.every { Position p -> tetris[p.y][p.x] == EMPTY }
}

def reset = { ->
  rockI = 0
  gasI = 0
  highestRockYs = [0L] * WIDTH
}

def printTetris = { List<Position> positionList = [], long startY = 0 -> // for debug
  for (long y in (startY + 2694 + 4)..startY) {
    for (long x in 0..7) {
      print(new Position(x: x, y: y) in positionList ? ROCK : tetris[y][x])
    }
    println()
  }
  println("\n")
}

def fall = {
  MyNumber startY = highestRockYs.max() + 3

  List<Position> rockPositions = switch (rockI) {
    case 0 -> (0..<4).collect { new Position(x: it, y: 0) } // horizontal line
    case 1 -> [new Position(x: 1, y: 2),
        new Position(x: 0, y: 1), new Position(x: 1, y: 1), new Position(x: 2, y: 1),
        new Position(x: 1, y: 0),] // +
    case 2 -> [new Position(x: 2, y: 2), new Position(x: 2, y: 1), new Position(x: 2, y: 0),
                new Position(x: 1, y: 0), new Position(x: 0, y: 0) ] // reversed L
    case 3 -> (0..<4).collect { new Position(x: 0, y: it) } // vertical line
    case 4 -> [new Position(x: 0, y: 0), new Position(x: 1, y: 0), new Position(x: 0, y: 1), new Position(x: 1, y: 1)] // square
    default -> throw new RuntimeException("WTF")
  }
  rockI = (rockI + 1) % 5
  rockPositions = rockPositions.collect { new Position(x: startX + it.x, y: startY + it.y) }

  while (true) {
    int offsetX = switch (gasDirections[gasI]) {
      case LEFT -> -1
      case RIGHT -> 1
      default -> throw new RuntimeException("WTF")
    }
    gasI = (gasI + 1) % gasDirections.size()
    if (canMove(rockPositions, offsetX, 0)) {
      move(rockPositions, offsetX, 0)
    }
    boolean didMoveY
    if (canMove(rockPositions, 0, -1)) {
      move(rockPositions, 0, -1)
    } else {
      break
    }
  }
  Map<MyNumber, Map<MyNumber, String>> t = this.tetris // don't know why it's needed for static compilation
  for (Position p in rockPositions) {
    t[p.y][p.x] = ROCK
  }
  //printTetris(rockPositions)
  for (int x in 0..<WIDTH) {
    highestRockYs[x] = Math.max(rockPositions.findAll { it.x.intValue() == x }
        .collect { it.y + 1}.max() ?: 0L, highestRockYs[x])
  }
}

reset()
2022.times {
  print("\rRun $it out of 2022")
  fall()
}
println("\rPart 1: the tower of rocks is ${highestRockYs.max()} units tall after 2022 runs")

// part 2: need first to search for a cycle
println("Part 2\nLooking for a cycle")
@ToString(includeNames = true)
class Cycle {
  long startY
  long endY // inclusive
  long nbSteps

  long getHeight() {
    return endY - startY
  }
}
reset()
long n = 1000000000000L
long i = 0
long cycleStartY = 0
long cycleStartI = 0
List<Long> cycleConfiguration = [rockI, gasI]
Cycle cycle
while (i < n && cycle == null) {
  fall()
  if (highestRockYs.collect().unique().size() == 1) { // means this is a ground
    def newCycleConf = [rockI, gasI]
    if (cycleConfiguration == newCycleConf) {
      cycle = new Cycle(startY: cycleStartY, endY: highestRockYs.first() - 1, nbSteps: i - cycleStartI - 1)
      println("Cycle found: $cycle")
      break
    }
    cycleStartI = i
    cycleStartY = highestRockYs.first()
    cycleConfiguration = newCycleConf
  }
  i++
}
//printTetris([], cycleStartY - 2)
println("Repeating cycles")
long offset = 0
while (i + cycle.nbSteps < n) {
  i += cycle.nbSteps
  offset += cycle.height
}

long lastCycleHighestY = highestRockYs.max()
while (i < n) {
  fall()
  i++
}
offset += highestRockYs.max() - lastCycleHighestY
long highestRockY = cycle.startY + offset
println("Part 2: $highestRockY")