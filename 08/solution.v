import os { read_lines }
import arrays { reduce, max }

struct ViewResult {
  visible bool
  scenic_score int
}

fn main() {
  lines := read_lines('input.txt')!
  n := lines[0].len
  m := lines.len
  grid := []rune{len: n * m, cap: n * m, init: rune(lines[it / m][it % n])}
  mut candidates := 0
  mut max_scenic_score := 0
  for y := 0; y <m; y++ {
    for x := 0; x < n; x++ {
      result := compute_viewing(grid, x, y, n, m)
      if result.visible {
        candidates++
      }
      max_scenic_score = max([result.scenic_score, max_scenic_score])!
    }
  }
  println('Part 1: There are $candidates candidates')
  println('Part 2: The max scenic score is $max_scenic_score')
}

fn compute_viewing(grid []rune, x int, y int, n int, m int) ViewResult {
  value := grid[index(x, y, n)]
  max := if n > m { n } else { m }
  mut from_left := true
  mut from_top := true
  mut from_right := true
  mut from_bottom := true
  mut left_distance := 0
  mut top_distance := 0
  mut right_distance := 0
  mut bottom_distance := 0
  for i := 1; i < max; i++ {
    // left
    if x - i >= 0 {
      if from_left { left_distance++ }
      if grid[index(x - i, y, n)] >= value { from_left = false }
    }
    // top
    if y - i >= 0 {
      if from_top { top_distance++ }
      if grid[index(x, y - i, n)] >= value { from_top = false }
    }
    // right
    if x + i < n {
      if from_right { right_distance++ }
      if grid[index(x + i, y, n)] >= value { from_right = false }
    }
    // bottom
    if y + i < m {
      if from_bottom { bottom_distance++ }
      if grid[index(x, y + i, n)] >= value { from_bottom = false }
    }
  }
  return ViewResult {
    visible: from_left || from_top || from_right || from_bottom
    scenic_score: left_distance * top_distance * right_distance * bottom_distance
  }
}

fn index(x int, y int, n int) int {
    return y * n + x
}