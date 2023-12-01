import os { read_lines }
import strings { new_builder }

const (
  width = 40
  height = 6
  dark_pixel = `.`
  lit_pixel = `#`
  critical_cycles = [20, 60, 100, 140, 180, 220]
)

struct Crt {
mut:
  image []rune = []rune{}
  cycle int = 1
  x int = 1
  sum int
}

fn main() {
  mut crt := Crt {}
  for line in read_lines('input.txt')! {
    if line.starts_with('addx ') {
      n := line.split(' ').last().int()
      crt.next_cycle()
      crt.next_cycle(value: n)
    } else {
      crt.next_cycle()
    }
  }
  println("Part 1: the sum of these six signal strengths is ${crt.sum}")
  println("Part 2: CRT output")
  for j := 0; j < height; j++ {
    println(crt.image[(j * width)..((j + 1) * width)].string())
  }
}

fn (mut self Crt) next_cycle(params NextCycleParams) {
  // handling current cycle
  position := (self.cycle - 1) % width // sprite of width 3 and centered at x -> [x-1, x, x+1]
  r := if position >= self.x - 1 && position <= self.x + 1 { lit_pixel } else { dark_pixel }
  self.image << r

  // handling next cycle
  self.cycle++
  self.x += params.value
  if self.cycle in critical_cycles {
    self.sum += self.cycle * self.x
  }
}

[params]
struct NextCycleParams {
  value int
}