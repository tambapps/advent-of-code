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

fn str(x int, y int) string {
  return "(x=$x, y=$y)"
}

struct Cave {
  highest_y int [required]
  with_ground bool
mut:
  cave map[string]Type = map[string]Type{}
}

fn (mut self Cave) put(x int, y int, t Type) {
  self.cave[str(x, y)] = t
}

fn (self &Cave) get(x int, y int) Type {
  return self.cave[str(x, y)] or {
    if self.with_ground && y >= self.highest_y + 2 {
      Type.rock
    } else {
      Type.empty
    }
  }
}

fn main() {
  rocks := arrays.flat_map<string, Point>(read_lines('input.txt')!, parse)
  highest_y := arrays.max(rocks.map(it.y))!
  mut cave1 := Cave {
    highest_y: highest_y
  }
  mut cave2 := Cave {
    highest_y: highest_y
    with_ground: true
  }
  for rock in rocks {
    cave1.put(rock.x, rock.y, Type.rock)
    cave2.put(rock.x, rock.y, Type.rock)
  }
  mut count := 0
  for !cave1.fall() {
    count++
  }
  println('Part 1: $count units of sand came to rest')
  count = 1 // because we need one more in order to finally know that the source is blocked
  for !cave2.fall() {
    count++
  }
  println('Part 2: $count units of sand came to rest')
}

// return true if should not fall anymore after
fn (mut self Cave) fall() bool {
  mut x := 500
  mut y := 0
  for (self.with_ground || y < self.highest_y) && (self.get(x, y + 1) == Type.empty
    || self.get(x - 1, y + 1) == Type.empty || self.get(x + 1, y + 1) == Type.empty) {
    if self.get(x, y + 1) != Type.empty && self.get(x - 1, y + 1) == Type.empty {
      x--
    } else if self.get(x, y + 1) != Type.empty && self.get(x + 1, y + 1) == Type.empty {
      x++
    }
    y++
  }
  self.put(x, y, Type.rock)
  return if self.with_ground { x == 500 && y == 0 } else { y >= self.highest_y }
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

