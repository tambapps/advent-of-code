import os { read_lines }

fn main() {
  mut part1_count := 0
  mut part2_count := 0

  for line in read_lines('input.txt')! {
    ranges := line.split(',').map(to_range(it.split('-').map(it.int())))

    if ranges.first().all(ranges.last().contains(it)) || ranges.last().all(ranges.first().contains(it)) {
      part1_count++
    }
    if ranges.first().any(ranges.last().contains(it)) || ranges.last().any(ranges.first().contains(it)) {
      part2_count++
    }
  }
  println('Part 1: ${part1_count} assignments have one range that fully contains the other')
  println('Part 2: ${part2_count} assignments have one range that overlaps the other')
}

fn to_range(bounds []int) []int {
  start := bounds.first()
  end := bounds.last()
  return []int{len: end - start + 1, init: start + it}
}
