// compile static to improve performances
// weirdly it didn't work for the example input, but it worked for my real input.

class Node {
  String name
  int rate
  List<Node> adj
}

Map<Node, List<String>> adjMap = new File('input.txt').readLines().collectEntries { String line ->
  List<String> nodeList = (line =~ /[A-Z]{2}/).findAll()
  String node = nodeList.removeAt(0)
  [(new Node(name: node, rate: (line =~ /rate=(\d+)/).with {
    it.find()
    it.group(1).toLong()
  })): nodeList]
}
final Map<String, Node> nodes = adjMap.keySet().collectEntries { [(it.name): it] }
adjMap.each { node, adjNames ->
  node.adj = adjNames.collect { String nodeName -> nodes[nodeName] }
}

class ValveApproach {
  Set<Node> openedValves = new HashSet<>()
  Node currentPosition
  Node previousPosition
  long score

  List<ValveApproach> next() {
    List<ValveApproach> paths = []
    if (!(currentPosition in openedValves) && currentPosition.rate) {
      // taking this minute to open the valve
      def o = new HashSet<Node>(openedValves)
      o << currentPosition
      paths << new ValveApproach(
          openedValves: o,
          currentPosition: currentPosition,
          // previousPosition not set on purpose
          score: score
      )
    } else {
      for (def node in currentPosition.adj) {
        if (node == previousPosition) continue
        paths << new ValveApproach(
            openedValves: new HashSet<Node>(openedValves),
            currentPosition: node,
            previousPosition: currentPosition,
            score: score
        )
      }
    }
    return paths
  }

  void passMinute() {
    if (openedValves) {
      score+= (long) openedValves.collect { it.rate }.sum()
    }
  }
}

List<ValveApproach> approaches = [new ValveApproach(currentPosition: nodes['AA'])]
30.times {
  approaches.each {it.passMinute() }
  approaches = approaches.collectMany { it.next() }
}

println("The most pressure we can release is " + approaches.collect { it.score }.max())