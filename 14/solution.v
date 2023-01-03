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

[inline]
fn str(x int, y int) string {
  return "(x=$x, y=$y)"
}

 // TODO get rid of this struct
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
  mut cave1 := map[string]Type{}
  mut cave2 := map[string]Type{}
  for rock in rocks {
    cave1[str(rock.x, rock.y)] = Type.rock
    cave2[str(rock.x, rock.y)] = Type.rock
  }
  mut count := 0
  for !fall_pt1(mut cave1, highest_y) {
    count++
  }
  println('Part 1: $count units of sand came to rest')
  count = 1 // because we need one more in order to finally know that the source is blocked
  for !fall_pt2(mut cave2, highest_y + 2) {
    count++
  }
  println('Part 2: $count units of sand came to rest')
}

// return true if should not fall anymore after
fn fall_pt1(mut cave map[string]Type, highest_y int) bool {
  // TODO try to optimize conditions
  mut x := 500
  mut y := 0
  for (y < highest_y) && (str(x, y + 1) !in cave
    || str(x - 1, y + 1) !in cave || str(x + 1, y + 1) !in cave) {
    if str(x, y + 1) in cave && str(x - 1, y + 1) !in cave {
      x--
    } else if str(x, y + 1) in cave && str(x + 1, y + 1) !in cave {
      x++
    }
    y++
  }
  cave[str(x, y)] = Type.rock
  return y >= highest_y
}
fn fall_pt2(mut cave map[string]Type, highest_y int) bool {
    // TODO not handling ground (and therefore not working) and try to optimize conditions
  mut x := 500
  mut y := 0
  for (y < highest_y) && (str(x, y + 1) !in cave
  || str(x - 1, y + 1) !in cave || str(x + 1, y + 1) !in cave) {
    if str(x, y + 1) in cave && str(x - 1, y + 1) !in cave {
      x--
    } else if str(x, y + 1) in cave && str(x + 1, y + 1) !in cave {
      x++
    }
    y++
  }
  cave[str(x, y)] = Type.rock
  return x == 500 && y == 0
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

