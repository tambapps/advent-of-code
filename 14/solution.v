import os { read_lines }
import math { max, min }
import arrays

 const(
   rock = true
 )

fn main() {
  rocks := arrays.flat_map<string, string>(read_lines('input.txt')!, parse)
  highest_y := arrays.max(rocks.map(it.split(',')[1].int()))!
  mut cave1 := map[string]bool{}
  mut cave2 := map[string]bool{}
  for rock_position in rocks {
    cave1[rock_position] = rock
    cave2[rock_position] = rock
  }
  mut count := fall_pt1(mut cave1, highest_y)
  println('Part 1: $count units of sand came to rest')

  count = fall_pt2(mut cave2, highest_y + 2)
  println('Part 2: $count units of sand came to rest')
}

fn fall_pt1(mut cave map[string]bool, highest_y int) int {
  mut x := 500
  mut y := 0
  mut count := 0
  for y < highest_y {
    if str(x, y + 1) !in cave {
      y++
    } else if str(x - 1, y + 1) !in cave {
      x--
      y++
    } else if str(x + 1, y + 1) !in cave {
      x++
      y++
    } else { // couldn't go anywhere
      cave[str(x, y)] = rock
      x = 500
      y = 0
      count++
    }
  }
  return count
}
fn fall_pt2(mut cave map[string]bool, floor_y int) int {
  mut x := 500
  mut y := 0
  mut count := 1 // because we need one more to know this is over
  mut new_drop := true
  for {
    if y + 1 == floor_y {
      cave[str(x, y)] = rock
      // dropping a new sand
      x = 500
      y = 0
      count++
      new_drop = true
    } else if str(x, y + 1) !in cave {
      y++
      new_drop = false
    } else if str(x - 1, y + 1) !in cave {
      x--
      y++
      new_drop = false
    } else if str(x + 1, y + 1) !in cave {
      x++
      y++
      new_drop = false
    } else if new_drop { // new_drop and couldn't go anywhere? this is over
      break
    } else { // couldn't go anywhere
      cave[str(x, y)] = rock
      // dropping a new sand
      x = 500
      y = 0
      count++
      new_drop = true
    }
  }
  return count
}

struct Point {
  x int
  y int
}
fn parse(line string) []string {
  line_ends := line.split(' -> ').map(fn (c string) Point {
    fields := c.split(',').map(it.int())
    return Point {
      x: fields[0]
      y: fields[1]
    }
  })
  mut rocks := []string{}
  for i := 1; i < line_ends.len; i++ {
    start := line_ends[i - 1]
    end := line_ends[i]
    for x in min(start.x, end.x)..(max(start.x, end.x) + 1) {
      for y in min(start.y, end.y)..(max(start.y, end.y) + 1) {
        rocks << str(x, y)
      }
    }
  }
  return rocks
}

[inline]
fn str(x int, y int) string {
  return "$x,$y"
}