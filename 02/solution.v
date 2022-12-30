import os { read_lines }

const(
  rock = `A`
  paper = `B`
  scisors = `C`
  part1_map = {
    `X`:rock,
    `Y`:paper,
    `Z`:scisors
  }
  weakness = {
    rock:paper,
    paper:scisors,
    scisors:rock
  }
  win = 6
  draw = 3
  loss = 0
)

fn main() {
  mut part1_score := 0
  mut part2_score := 0
  for line in read_lines('input.txt')! {
    against := line[0]
    xyz := line[line.len - 1]
    part1_score+= battle_score(part1_map[xyz], against)
    // for part2 we need to find the rps that will give the provided outcome
    me := match xyz {
      `X` { weakness[weakness[against]] }
      `Y` { against }
      `Z` { weakness[against] }
      else { panic('How could this happen') }
    }
    part2_score+= battle_score(me, against)
  }
  println('Part 1: score is ${part1_score}\nPart 2: score is ${part2_score}')
}

fn outcome(rps rune, against rune) int {
  if rps == weakness[against] {
    return win
  } else if against == weakness[rps] {
    return loss
  } else {
    return draw
  }
}

fn score(rps rune) int {
  return int(rps - `A` + 1)
}

fn battle_score(rps rune, against rune) int {
  return score(rps) + outcome(rps, against)
}
