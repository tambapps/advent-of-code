// execute it with --compile-static for better performance
// weirdly it gives the expectedAnswer minus 1 for the example input, but it worked fine for my input
import groovy.transform.EqualsAndHashCode
import groovy.transform.Field
import groovy.transform.Immutable

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

def printTetris = { List<Position> positionList = [] -> // for debug
  for (def y in 21..0) { for (def x in 0..7) {print(new Position(x: x, y: y) in positionList ? ROCK : tetris[y][x])} ; println() }
  println("\n")
}

def fall = {
  MyNumber startY = highestRockYs.max() + 3
  List<Position> rockPositions = switch (rockI++ % 5) {
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
  rockPositions = rockPositions.collect { new Position(x: startX + it.x, y: startY + it.y) }

  while (true) {
    int offsetX = switch (gasDirections[gasI++ % gasDirections.size()]) {
      case LEFT -> -1
      case RIGHT -> 1
      default -> throw new RuntimeException("WTF")
    }
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

for (long howMuch in [2022L, 1000000000000L]) {
  rockI = 0
  gasI = 0

  long lowestYInMemory = 0
  highestRockYs = [0L] * WIDTH
  for (long i = 0; i < howMuch; i++) { // n.times {} doesn't seem to work for large numbers
    print("\rRun $i of $howMuch")
    fall()
    // this part is to clean some memory TODO


  }
  println("\nThe tower of rocks is ${highestRockYs.max()} tall after $howMuch runs")
}
