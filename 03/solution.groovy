File f = new File('input.txt')
int part1Score = 0
int part2Score = 0

// going through the lines in batches of 3 (because a group is a set of 3 lines)
for (List<String> compartmentGroup in f.readLines().collate(3)) {
  for (String line in compartmentGroup) {
    int compartmentSize = line.size() / 2
    def (compartment1, compartment2) = [line.substring(0, compartmentSize), line.substring(compartmentSize)]
    // part 1
    Character commonItem = compartment1.find { compartment2.contains(it) }.charAt(0)
    part1Score += priority(commonItem)
  }
  // part 2
  Character badge = compartmentGroup[0].find { compartmentGroup[1].contains(it)
      && compartmentGroup[2].contains(it) }.charAt(0)
  part2Score += priority(badge)
}
println("Part 1: The sum of the priorities of those item types is $part1Score")
println("Part 2: The sum of the priorities of those item types $part2Score")

static int priority(Character c) {
  return c.isUpperCase() ? c - 'A'.charAt(0) + 27
      : c - 'a'.charAt(0) + 1
}