

List<Tuple2<List, List>> pairs = new File('input.txt').newReader().with { BufferedReader reader ->
  List<Tuple2<List, List>> pairs = []
  String line
  while ((line = readLine())) {
    List packet1 = new PacketParser(line).parse()
    List packet2 = new PacketParser(readLine()).parse()
    pairs << new Tuple2<>(packet1, packet2)
    readLine() // empty line separator
  }
  pairs
}

int rightIndexSum = 0

pairs.eachWithIndex { Tuple2<List, List> pair, int i ->
  int number = i + 1
  if (isInRightOrder(pair.v1, pair.v2)) {
    rightIndexSum+= number
  }
}

println("Part 1: sum of indices of well ordered packet is $rightIndexSum")

static boolean isInRightOrder(List l1, List l2) {
  int i = 0
  while (i < l1.size() && i < l2.size()) {
    def item1 = l1[i]
    def item2 = l2[i]
    if (item1 instanceof Integer && item2 instanceof Integer && item1 > item2) {
      return false
    } else if (item1 instanceof Integer && item2 instanceof List) {
      return isInRightOrder([item1], item2)
    } else if (item1 instanceof List && item2 instanceof Integer) {
      return isInRightOrder(item1, [item2])
    } else if (item1 instanceof List && item2 instanceof List) { // both list
      return isInRightOrder(item1, item2)
    }
    i++
  }
  return i >= l1.size()
}

class PacketParser {
  String line
  int i = 0
  PacketParser(String line) {
    this.line = line
  }

  List parse() {
    List packet = []
    i++ // skipping [
    while (i < line.size()) {
      if (current.isInteger()) {
        packet << parseInteger()
      } else if (current == '[') {
        packet << parse()
      } else if (current == ']') {
        i++
        return packet
      } else if (current == ',') {
        i++
      }
    }
    i++
    return packet
  }

  Integer parseInteger() {
    int start = i
    while (current.isInteger()) i++
    return line[start..<i].toInteger()
  }

  String getCurrent() {
    return line[i]
  }
}