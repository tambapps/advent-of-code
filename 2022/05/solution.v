import os { read_lines }

fn main() {
  lines := read_lines('input.txt')! // unfortunately there is no way (yet?) to read file line per line in V
  mut i := 0
  nb_stacks := (lines[0].len + 1) / 4
  mut stacks_1 := [][]string{len: nb_stacks, init: []string{len: 0}}
  mut stacks_2 := [][]string{len: nb_stacks, init: []string{len: 0}}
  // parsing stack
  for lines[i].len > 0 && lines[i][1].ascii_str() != '1' {
    line := lines[i]
    for j := 0; j < line.len; j += 4 {
      c := line[j + 1].ascii_str()
      if c != ' ' {
        stacks_1[j / 4].prepend(c)
        stacks_2[j / 4].prepend(c)
      }
    }
    i++
  }
  i+= 2  // i is now at the line ' 1   2   3...', and the following line is empty: let's skip them
  for i < lines.len {
    fields := lines[i].split(' ')
    n := fields[1].int()
    from := fields[3].int() - 1 // -1 because we work with indexes starting at 0
    to := fields[5].int() - 1
    i++

    mut part2_move := []string{cap: n}
    for j := 0; j < n; j++ {
      stacks_1[to] << stacks_1[from].pop()
      part2_move.prepend(stacks_2[from].pop())
    }
    for m in part2_move {
      stacks_2[to] << m
    }
  }

  println('Part 1: The top crates are ${stacks_1.map(it.last()).join('')}')
  println('Part 2: The top crates are ${stacks_2.map(it.last()).join('')}')
}
