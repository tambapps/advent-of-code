import os { read_lines }
import math { max, min }
import arrays


struct Point {
  x int
  y int
}
enum Type as u8 {
  rock
  empty
}

fn (self &Point) str() string {
  return str(self.x, self.y)
}
fn str(x int, y int) string {
  return "(x=$x, y=$y)"
}
struct Cave {
mut:
  cave map[string]Type

}

fn (mut self Cave) put(point Point, t Type) {
  self.cave[point.str()] = t
}

fn (self &Cave) get(x int, y int) Type {
  return self.cave[str(x, y)] or { Type.empty }
}

fn main() {
  rocks := arrays.flat_map<string, Point>(read_lines('input.txt')!, parse)
  mut cave := Cave {
    cave: map[string]Type{}
  }
  for rock in rocks {
    cave.put(rock, Type.rock)
  }
  highest_y := arrays.max(rocks.map(it.y))!
  mut count := 0
  for {
    point := cave.fall(highest_y)
    if point.y >= highest_y { break }
    count++
  }
  println('Part 1: $count units of sand came to rest')


}

fn (mut self Cave) fall(highest_y int) Point {
  mut x := 500
  mut y := 0
  // TODO withground ||
  for (false || y < highest_y) && (self.get(x, y + 1) == Type.empty
    || self.get(x - 1, y + 1) == Type.empty || self.get(x + 1, y + 1) == Type.empty) {
    if self.get(x, y + 1) != Type.empty && self.get(x - 1, y + 1) == Type.empty {
      x--
    } else if self.get(x, y + 1) != Type.empty && self.get(x + 1, y + 1) == Type.empty {
      x++
    }
    y++
  }
  p := Point {
    x: x
    y: y
  }
  self.put(p, Type.rock)
  return p
}
// parsing stuff
fn parse(line string) []Point {
  line_ends := line.split(' -> ').map(fn (c string) Point {
    fields := c.split(',').map(it.int())
    return Point {
      x: fields[0]
      y: fields[1]
    }
  })
  mut rocks := []Point{}
  for i := 1; i < line_ends.len; i++ {
    start := line_ends[i - 1]
    end := line_ends[i]
    for x in min(start.x, end.x)..(max(start.x, end.x) + 1) {
      for y in min(start.y, end.y)..(max(start.y, end.y) + 1) {
        rocks << Point {
          x: x
          y: y
        }
      }
    }
  }
  return rocks
}

