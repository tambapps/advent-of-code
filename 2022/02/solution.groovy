File f = new File('input.txt')
int part1Score = 0
int part2Score = 0

for (String line in f.readLines()) {
  // first group is whole match, we don't care about that
  def (_, s1, s2) = (line =~ /([A|B|C])\s([X|Y|Z])/)[0]
  RPS opponent = RPS.values().find { it.opponent == s1 }
  part1Score += RPS.values().find { it.you == s2 }.score(opponent)
  part2Score += opponent.getForOutput(s2).score(opponent)
}


println "Part 1: score is $part1Score"
println "Part 2: score is $part2Score"

enum RPS {
  ROCK('A', 'X', 1) {
    @Override
    RPS getWeakness() { return PAPER; }
  },
  PAPER('B', 'Y', 2) {
    @Override
    RPS getWeakness() { return SCISSORS; }
  },
  SCISSORS('C', 'Z', 3) {
    @Override
    RPS getWeakness() { return ROCK; }
  };

  final String opponent;
  // for part1
  final String you;
  final int selectionScore;

  RPS(String opponent, String you, int selectionScore) {
    this.opponent = opponent;
    this.you = you;
    this.selectionScore = selectionScore;
  }

  int battleScore(RPS other) {
    if (this == other) return 3;
    return other == weakness ? 0 : 6;
  }

  // for part2
  RPS getForOutput(String x) {
    return switch (x) {
      case 'X' -> values().find { it != this && it != weakness }
      case 'Y' -> this
      case 'Z' -> weakness
    }
  }

  int score(RPS opponent) {
    return selectionScore + battleScore(opponent)
  }
  
  abstract RPS getWeakness()
}
