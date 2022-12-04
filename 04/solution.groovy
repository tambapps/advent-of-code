File f = new File('input.txt')

def (int part1Count, int part2Count) = [0, 0]
for (String line in f.readLines()) {
  // first group is whole match, we don't care about that
  def (_, r1From, r1To, r2From, r2To) = (line =~ /(\d+)-(\d+),(\d+)-(\d+)/)[0]
  IntRange range1 = r1From.toInteger()..r1To.toInteger()
  IntRange range2 = r2From.toInteger()..r2To.toInteger()

  if (range1.containsAll(range2) || range2.containsAll(range1)) part1Count++
  if (range1.any(range2.&contains) || range2.any(range1.&contains)) part2Count++
}

println("Part 1: $part1Count assignements have one range that fully contains the other")
println("Part 2: $part2Count assignements have one range that overlaps the other")