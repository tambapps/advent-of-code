import os { read_lines }

fn main() {
  mut part1_score := 0
  mut part2_score := 0
  mut compartment_group := []string{}
  for line in read_lines('input.txt')! {
  rucksack_size := line.len / 2
  sack1 := line[0..rucksack_size]
  sack2 := line[rucksack_size..line.len]
    common_item := sack1[sack1.index_any(sack2)].ascii_str()
    part1_score+= priority(common_item)

    compartment_group << line
    if compartment_group.len == 3 {
      for c in compartment_group[0].runes().map(it.str()) {
        if compartment_group[1].contains(c) && compartment_group[2].contains(c) {
          part2_score+= priority(c)
          break
        }
      }
      compartment_group.clear()
    }
  }
  println('Part 1: The sum of the priorities of those item types is ${part1_score}')
  println('Part 2: The sum of the priorities of those item types is ${part2_score}')
}

fn priority(s string) int {
  if s.is_upper() { return int(s[0] - `A` + 27) }
  else { return int(s[0] - `a` + 1) }
}
