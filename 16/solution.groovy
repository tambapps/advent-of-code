import groovy.transform.Memoized
import groovy.transform.ToString
import groovyx.gpars.GParsPool

@ToString(includePackage = false, includes = ['name', 'rate'], includeNames = true)
class Node {
  static Set<Node> NODES
  static Set<Node> NODES_WITH_RATE
  String name
  int rate
  private List<String> _adj

  @Memoized
  Set<Node> getAdj() {
    return Node.NODES.findAll { it.name in _adj }
  }
}

Node.NODES = new File('input.txt').readLines().collect { String line ->
  List<String> nodeList = (line =~ /[A-Z]{2}/).findAll()
  String node = nodeList.removeAt(0)
  new Node(name: node, rate: (line =~ /rate=(\d+)/)[0][1].toInteger(), _adj: nodeList)
}.toSet()
Node.NODES_WITH_RATE = Node.NODES.findAll { it.rate }

class Path {
  Set<Node> openedValves = new HashSet<>()
  Node currentPosition
  Node previousPosition
  int score

  boolean isMaximumGrowth() {
    return openedValves.containsAll(Node.NODES_WITH_RATE)
  }

  List<Path> run() {
    passMinute()
    if (maximumGrowth) {
      return [this]
    }
    List<Path> paths = []
    if (!(currentPosition in openedValves) && currentPosition.rate) {
      def o = new HashSet<Node>(openedValves)
      o << currentPosition
      paths << new Path(
          openedValves: o,
          currentPosition: currentPosition,
          // previousPosition not set in purpose
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

  int max = paths.collect { it.score }.max()
  paths = paths.findAll { !(it.maximumGrowth && it.score < max) }
  paths = paths.collectMany { it.run() }
}

println(paths.size())
println(paths.collect { it.score}.max())
