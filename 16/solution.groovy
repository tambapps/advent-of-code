// execute it with --compile-static for better performance
// weirdly it gives the expectedAnswer minus 1 for the example input, but it worked fine for my input
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
    it.group(1).toInteger()
  })): nodeList]
}
final Map<String, Node> nodes = adjMap.keySet().collectEntries { [(it.name): it] }
adjMap.each { node, adjNames ->
  node.adj = adjNames.collect { String nodeName -> nodes[nodeName] }
}

class Move { // move of one participant
  Node currentPosition
  Node previousPosition
  Set<Node> openedValves = new HashSet<>()
}

class ValveApproach {
  Set<Node> openedValves = new HashSet<>()
  List<Node> participantsCurrentPosition
  List<Node> participantsPreviousPosition
  int score

  List<ValveApproach> next() {
    List<List<Move>> participantMoves = [] // the list of all moves of all participants
    participantsCurrentPosition.size().times { participantMoves << [] }
    for (i in 0..<participantsCurrentPosition.size()) {
      Node currentPosition = participantsCurrentPosition[i]
      Node previousPosition = participantsPreviousPosition[i]

      if (!(currentPosition in openedValves) && currentPosition.rate) {
        // taking this minute to open the valve
        def o = new HashSet<Node>(openedValves)
        o << currentPosition
        // previousPosition not set on purpose
        participantMoves[i] << new Move(openedValves: o, currentPosition: currentPosition)
      } else {
        for (def node in currentPosition.adj) {
          if (node == previousPosition) continue
          participantMoves[i] << new Move(
              openedValves: new HashSet<Node>(openedValves),
              currentPosition: node,
              previousPosition: currentPosition,
          )
        }
      }
    }
    // finding all combination possible of moves made by all participant (useless if only one participant)
    return participantMoves.combinations()
        .collect {(List<Move>) it } // needed for static compilation
        .collect { mergedWith(it) }
  }

  ValveApproach mergedWith(List<Move> moves) {
    Set<Node> openedValves = new HashSet<>(this.openedValves)
    List<Node> participantsCurrentPosition = []
    List<Node> participantsPreviousPosition = []
    moves.each { Move move ->
      openedValves.addAll(move.openedValves)
      participantsCurrentPosition << move.currentPosition
      participantsPreviousPosition << move.previousPosition
    }
    return new ValveApproach(
        openedValves: openedValves,
        participantsCurrentPosition: participantsCurrentPosition,
        participantsPreviousPosition: participantsPreviousPosition,
        score: score
    )
  }

  void passMinute() {
    if (openedValves) {
      score+= (int) openedValves.collect { it.rate }.sum()
    }
  }
}

List<ValveApproach> approaches = [new ValveApproach(participantsCurrentPosition: [nodes['AA']], participantsPreviousPosition: [null])]
(30..1).each {int remaining ->
  print("\rPart 1: remaining: $remaining minute(s) ")
  approaches.each {it.passMinute() }
  approaches = approaches.collectMany { it.next() }
}
println("\rPart 1: the most pressure we can release is " + approaches.collect { it.score }.max())

// part 2. using 2 positions as there is the elephant and me
approaches = [new ValveApproach(participantsCurrentPosition: [nodes['AA'], nodes['AA']], participantsPreviousPosition: [null, null])]
(26..1).each { int remaining ->
  print("\rPart 2: remaining: $remaining minute(s) ")
  approaches.each {it.passMinute() }
  approaches = approaches.collectMany { it.next() }

  int max = approaches.collect { it.score }.max()
  if (remaining <= 13) { // hack to filter results and not having enormous number of paths to handle
    approaches = approaches.findAll { it.score >= max * 0.88f }
  }
}
println("\rPart 2: the most pressure we can release is " + approaches.collect { it.score }.max())