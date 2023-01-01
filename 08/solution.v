import os { read_lines }

enum Direction as u8 {
  left
  top
  right
  bottom
}

fn main() {
  lines := read_lines('input.txt')!
  n := lines[0].len
  m := lines.len
  grid := []rune{len: n * m, cap: n * m, init: rune(lines[it / m][it % n])}
  mut candidates := 0
  for j := 0; j <m; j++ {
    for i := 0; i < n; i++ {
      if is_visible(grid, i, j, n, m) {
        candidates++
      }
    }
  }
  println('Part 1: There are $candidates candidates')
}

fn is_visible(grid []rune, x int, y int, n int, m int) bool {
    value := grid[index(x, y, n)]
    max := if n > m { n } else { m }
    mut from_left := true
    mut from_top := true
    mut from_right := true
    mut from_bottom := true
    for i := 1; i < max; i++ {
        // left
        if x - i >= 0 && grid[index(x - i, y, n)] >= value {
            from_left = false
        }
        // top
        if y - i >= 0 && grid[index(x, y - i, n)] >= value {
            from_top = false
        }
        // right
        if x + i < n && grid[index(x + i, y, n)] >= value {
            from_right = false
        }
        // bottom
        if y + i < m && grid[index(x, y + i, n)] >= value {
            from_bottom = false
        }
    }
    return from_left || from_top || from_right || from_bottom
}

fn index(x int, y int, n int) int {
    return y * n + x
}