import groovy.transform.ToString

@ToString(includePackage = false, includes = ['name', 'rate'], includeNames = true)
class Node {
  static Set<Node> NODES
  String name
  int rate
  private List<String> _adj

  Set<Node> getAdj() {
    return Node.NODES.findAll { it.name in _adj }
  }
}

Node.NODES = new File('input.txt').readLines().collect { String line ->
  List<String> nodeList = (line =~ /[A-Z]{2}/).findAll()
  String node = nodeList.removeAt(0)
  new Node(name: node, rate: (line =~ /rate=(\d+)/)[0][1].toInteger(), _adj: nodeList)
}.toSet()

class Path {
  Set<Node> openedValves = new HashSet<>()
  Node currentPosition
  Node previousPosition
  int score

  List<Path> run() {
    passMinute()
    if (openedValves == Node.NODES) {
      return [this]
    }
    List<Path> paths = []
    if (!(currentPosition in openedValves) && currentPosition.rate) {
      def o = new HashSet<Node>(openedValves)
      o << currentPosition
      paths << new Path(
          openedValves: o,
          currentPosition: currentPosition,
          score: score
      )
    }
    for (def node in currentPosition.adj) {
      if (node == previousPosition) continue
      paths << new Path(
          openedValves: new HashSet<Node>(openedValves),
          currentPosition: node,
          previousPosition: currentPosition,
          score: score
      )
    }

    return paths
  }

  void passMinute() {
    score+= openedValves.collect { it.rate }.sum() ?: 0
  }
}

List<Path> paths = [new Path(currentPosition: Node.NODES.find { it.name == 'AA' })]

30.times {
  println("$it ${paths.size()}")
  paths = paths.collectMany { it.run() }
}

println(paths.size())
println(paths.collect { it.score}.max())
