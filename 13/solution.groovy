List<Tuple2<Packet, Packet>> pairs = new File('input.txt').newReader().with { BufferedReader reader ->
  List<Tuple2<Packet, Packet>> pairs = []
  String line
  while ((line = readLine())) {
    Packet packet1 = new PacketParser(line).parse()
    Packet packet2 = new PacketParser(readLine()).parse()
    pairs << new Tuple2<>(packet1, packet2)
    readLine() // empty line separator
  }
  pairs
}

int rightIndexSum = 0
pairs.eachWithIndex { Tuple2<Packet, Packet> pair, int i ->
  int number = i + 1
  if (pair.v1 < pair.v2) {
    rightIndexSum += number
  }
}

println("Part 1: sum of indices of well ordered packet is $rightIndexSum")

// flattening packet pairs
List<Packet> allPackets = pairs.collectMany { it }
def divider1 = new Packet([2])
def divider2 = new Packet([6])
allPackets.addAll([divider1, divider2])

allPackets.sort(true)
int index1 = allPackets.indexOf(divider1) + 1
int index2 = allPackets.indexOf(divider2) + 1

println("Part 2: decoder key is " + (index1 * index2))

class Packet extends LinkedList implements Comparable<Packet> {

  Packet() {
  }

  Packet(Collection l) {
    super(l)
  }

  @Override
  int compareTo(Packet p) {
    return compare(this, p)
  }

  static int compare(Packet l1, Packet l2) {
    int i = 0
    while (i < l1.size() && i < l2.size()) {
      def item1 = l1[i]
      def item2 = l2[i]
      if (item1 instanceof Integer && item2 instanceof Integer) {
        int c = item1 <=> item2
        if (c != 0) {
          return c
        }
      }

      if (item1 instanceof Integer && item2 instanceof Packet) {
        int c = compare(new Packet([item1]), item2)
        if (c != 0) {
          return c
        }
      }
      if (item1 instanceof Packet && item2 instanceof Integer) {
        int c = compare(item1, new Packet([item2]))
        if (c != 0) {
          return c
        }
      }
      if (item1 instanceof Packet && item2 instanceof Packet) {
        int c = compare(item1, item2)
        if (c != 0) {
          return c
        }
      }
      i++
    }
    return l1.size() <=> l2.size()
  }
}

class PacketParser {
  String line
  int i = 0
  PacketParser(String line) {
    this.line = line
  }

  Packet parse() {
    Packet packet = new Packet()
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