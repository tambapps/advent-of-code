import os { read_lines }
import math { abs }

const (
  left = 'L'
  up = 'U'
  right = 'R'
  down = 'D'
)

struct Knot {
  x int
  y int
}

type KnotSet = map[string]Knot

fn main() {
  start_position := Knot {
    x: 0
    y: 0
  }
  mut knots := []Knot{len: 10, init: start_position}
  // V doesn't have sets, so let's use a map as a set
  mut part1_visits := KnotSet(map[string]Knot{})
  mut part2_visits := KnotSet(map[string]Knot{})
  add(mut &part1_visits, start_position)
  add(mut &part2_visits, start_position)

  for line in read_lines('input.txt')! {
    fields := line.split(' ')
    n := fields[1].int()
    for c := 0; c < n; c++ {
      head := knots[0]
      knots[0] = match fields[0] {
        left { Knot {
          x: head.x - 1
          y: head.y
        }}
        up { Knot {
          x: head.x
          y: head.y + 1
        }}
        right { Knot {
          x: head.x + 1
          y: head.y
        }}
        down { Knot {
          x: head.x
          y: head.y - 1
        }}
        else { panic('oh no') }
      }
      for i := 1; i < knots.len; i++ {
        knots[i] = knots[i].follow(knots[i - 1])
      }
      add(mut &part1_visits, knots[1])
      add(mut &part2_visits, knots.last())
    }
  }
  println("Part 1: tail visited ${part1_visits.len} different positions")
  println("Part 2: tail visited ${part2_visits.len} different positions")
}

// needed for the set/map
fn (self Knot) str() string {
  return self.x.str() + "," + self.y.str()
}

fn (self &Knot) follow(head Knot) Knot {
  offset_x := head.x - self.x
  offset_y := head.y - self.y
  if (abs(offset_x) == 2 && offset_y == 0)
      || (offset_x == 0 && abs(offset_y) == 2)
      || (!self.is_next_to(head) && self.x != head.x && self.y != head.y) {
    return Knot {
      x: self.x + sign(offset_x)
      y: self.y + sign(offset_y)
    }
  } else {
    return *self
  }
}

fn (self &Knot) is_next_to(other Knot) bool {
  return other.x != self.x && other.y != self.y && abs(self.x - other.x) <= 1 && abs(self.y - other.y) <= 1
}

fn sign(i int) int {
  if i > 0 { return 1 }
  else if i < 0 { return -1 }
  else { return 0 }
}

[inline]
fn add(mut set &KnotSet, knot Knot) {
  set[knot.str()] = knot
}
