File f = new File('input.txt')

def (int part1Count, int part2Count) = [0, 0]

def (List<String> reversedStacksInput, List<String> moves) = f.newReader().with { reader ->
  String line;
  List<String> stacksInput = []
  while (!(line = reader.readLine()).isEmpty()) {
    stacksInput << line
  }
  List<String> moves = []
  while ((line = reader.readLine()) != null) {
    moves << line
  }
  // looks like the last line on the input file was empty
  if (moves.last().isEmpty()) moves.removeLast()
  [stacksInput.reverse(), moves]
}

/*
 * Parsing stack
 */
String numbers = reversedStacksInput.remove(0).trim()

int nbStacks = numbers.trim().substring(numbers.lastIndexOf(' ') + 1).toInteger()

// we use different stacks per part, as the way we move crates are different
Stacks stacks1 = new Stacks(nbStacks)
Stacks stacks2 = new Stacks(nbStacks)

for (line in reversedStacksInput) {
  int i=0;
  stacksLine = []
  for (_ in 0..<nbStacks) {
    if (Character.isWhitespace(line.charAt(i))) {
      stacksLine << null
    } else {
      stacksLine << line.charAt(i + 1)
    }
    i += 4
  }
  stacks1.addLine(stacksLine)
  stacks2.addLine(stacksLine)
}

println 'Stacks initial states'
println stacks1

/*
 * Parsing and executing moves
 */

for (def line in moves) {
  def (_, countStr, fromStr, toStr) = (line =~ /move\s(\d+)\sfrom\s(\d+)\sto\s(\d+)/)[0]
  def (count, from, to) = [countStr, fromStr, toStr].collect(Integer.&parseInt)
  // substracting 1 because we're dealing with indexes which start at 0
  stacks1.move1(count.toInteger(), from - 1, to - 1)
  stacks2.move2(count.toInteger(), from - 1, to - 1)
}

println "Part 1: stacks final state"
println stacks1
println "Top crates: " + (0..<nbStacks).collect(stacks1.&top).join('')

println "\n\nPart 2: stacks final state"
println stacks2
println "Top crates: " + (0..<nbStacks).collect(stacks2.&top).join('')


class Stacks {
  private final List<LinkedList<Character>> stacks = []

  Stacks(int n) {
    for (def i in 0..<n) stacks << new LinkedList<Character>();
  }


  void addLine(List<Character> line) {
    for (i in 0..<line.size()) {
      if (line[i] != null) stacks[i] << line[i]
    }
  }

  // for part 1, which moves crates in a weird way
  void move1(int count, int from, int to) {
    count.times { stacks[to] << stacks[from].removeLast() }
  }

  void move2(int count, int from, int to) {
   def crates = []
   count.times { crates.add(0, stacks[from].removeLast()) }
   crates.each { stacks[to] << it }
  }

  Character top(int i) {
    return stacks[i].last()
  }

  @Override
  String toString() {
    StringBuilder builder = new StringBuilder()
    int i = stacks.collect { it.size() }.max() - 1
    while (i >= 0) {
      for (def stack in stacks) {
        builder.append(stack[i] != null ? "[" + stack[i] + "]" : '   ').append(' ')
      }
      builder.append('\n')
      i--
    }
    return builder.append(" " + (1..stacks.size()).join("   "))
      .toString()
  }
}
