// use TrackedNumber because in part 2, we have ridiculously enormous worry levels
List<Monkey> monkeys = []

new File('input.txt').newReader().with {
  while (readLine() != null) {
    Monkey monkey = new Monkey()
    monkey.initialItems = readLine().split(/:/)[1].split(/,/).collect { it.trim() }.collect { it.toInteger() }
    monkey.operation = parseOperation(readLine().split(/=/)[1])
    monkey.divisible = readLine().split(/\s+/).last().toInteger()
    monkey.trueMonkey = readLine().split(/\s+/).last().toInteger()
    monkey.falseMonkey = readLine().split(/\s+/).last().toInteger()
    monkeys << monkey
    readLine() // empty line
  }
}

TrackedNumber.REPRESENTATIONS_TO_SAVE = monkeys*.divisible

if (true) {
  // TODO delete these tests
  int n = 79 + 23
  TrackedNumber number = new TrackedNumber(79) + new TrackedNumber(23)
  number.savedRepresentations.each { Number d, EuclideanDivision it ->
    println("$n = ${it.d} * ${it.quotient} + ${it.rest}? " + (n == (it.d * it.quotient + it.rest)))
  }

  println()

  n = 79 * 23
  number = new TrackedNumber(79) * new TrackedNumber(23)
  number.savedRepresentations.each { Number d, EuclideanDivision it ->
    println("$n = ${it.d} * ${it.quotient} + ${it.rest}? " + (n == (it.d * it.quotient + it.rest)))
  }
//  return
}


for (int part in [1, 2]) {
  monkeys*.reset()
  Map<Integer, Long> inspectionCountByMonkey = [:].withDefault { 0L }
  int n = part == 1 ? 20 : 10_000//10_000
  n.times { runRound(monkeys, part == 1, inspectionCountByMonkey) }
  Long monkeyBusiness = inspectionCountByMonkey.values()
      .sort { -it }[0..1]
      .inject {i1, i2 -> i1 * i2 }

  inspectionCountByMonkey.each { int index, Long inspectedItemCount ->
    println("Monkey $index inspected items $inspectedItemCount times.")
  }
  println("Part $part: the level of monkey business after $n rounds is $monkeyBusiness")
  println()
}

static void runRound(List<Monkey> monkeys, boolean divideBy3, Map<Integer, Long> inspectionCountByMonkey) {
  monkeys.eachWithIndex { Monkey monkey, int i ->
    for (def item in monkey.items) {
      TrackedNumber updatedItem = monkey.inspect(item, divideBy3)
      int targetMonkey = monkey.targetMonkey(updatedItem)
      monkeys[targetMonkey].items << updatedItem
      inspectionCountByMonkey[i]+= 1
//      println("${monkey.id} threw $updatedItem (previously $item) to monkey $targetMonkey")
    }
    monkey.items.clear()
  }
}

static Closure<TrackedNumber> parseOperation(String operation) {
  Scanner scanner = new Scanner(operation)
  Closure<TrackedNumber> n1 = numberClosure(scanner.next())
  Closure<TrackedNumber> operator = switch (scanner.next()) {
    case '*' -> { i1, i2 -> i1 * i2 }
    default -> { i1, i2 -> i1 + i2 }
  }
  Closure<TrackedNumber> n2 = numberClosure(scanner.next())
  return { item ->
    operator(n1(item), n2(item))
  }
}

static Closure<TrackedNumber> numberClosure(String input) {
  return input ==~ /\d+/ ? { new TrackedNumber(input.toInteger()) } : { it }
}

class Monkey {
  List<Integer> initialItems
  List<TrackedNumber> items
  Closure<TrackedNumber> operation // takes in parameter the item to throw
  Integer divisible
  Integer trueMonkey
  Integer falseMonkey

  int targetMonkey(TrackedNumber item) {
    return item % divisible == 0 ? trueMonkey : falseMonkey
  }

  TrackedNumber inspect(TrackedNumber item, boolean divideBy3) {
    TrackedNumber result = operation(item)
    return divideBy3 ? result / 3 : result
  }

  void reset() {
    items = initialItems.collect {new TrackedNumber(it) }
  }
}

/**
 * Represents an euclidean division: value = d * quotient + rest
 */
class EuclideanDivision {
  final long d
  final long quotient
  final long rest

  EuclideanDivision(Number d, Number quotient, Number rest) {
    this.d = d.longValue()
    this.quotient = quotient.longValue()
    this.rest = rest.longValue()
  }

  EuclideanDivision simplify() {
    long rest = this.rest
    long quotient = this.quotient
    while (rest >= d) {
      rest -= d
      quotient++
    }
    return new EuclideanDivision(d, quotient, rest)
  }

  BigDecimal getValue() {
    // returning bigDecimal as it may not fit in an int/long
    return (new BigDecimal(quotient) * new BigDecimal(d)) + new BigDecimal(rest)
  }
}
class TrackedNumber {
  static List<Integer> REPRESENTATIONS_TO_SAVE // we will save all Monkey Test: euclidean divisions
  private Map<Number, EuclideanDivision> savedRepresentations = [:]

  private TrackedNumber() {}

  TrackedNumber(Long n) {
    REPRESENTATIONS_TO_SAVE.each { savedRepresentations[it] = computeRepresentation(n, it) }
  }

  BigDecimal getValue() {
    return savedRepresentations.values().first().value
  }

  // n1 = d * q1 + r1
  // n2 = d * q2 + r2

  // (n1 + n2) = (q1 + q2) * d + (r1 + r2)
  TrackedNumber plus(TrackedNumber other) {
    Map<Number, EuclideanDivision> savedRepresentations = [:]
    REPRESENTATIONS_TO_SAVE.each { Number d ->
      EuclideanDivision a1 = this.savedRepresentations[d]
      EuclideanDivision a2 = other.savedRepresentations[d]
      savedRepresentations[d] = new EuclideanDivision(d, a1.quotient + a2.quotient, a1.rest + a2.rest).simplify()
    }
    return new TrackedNumber(savedRepresentations: savedRepresentations)
  }

  // n1 = d * q1 + r1
  // n2 = d * q2 + r2
  // (n1 * n2) = (d * q1 + r1) * (d * q2 + r2)
  //           = d * d  * q1 * q2 + d * q1 * r2 + r1 * d * q2 + r1 * r2
  //           = d * ((d * q1 * q2) + (q1 * r2) + (r1 * q2)) + r1 * r2
  TrackedNumber multiply(TrackedNumber other) {
    Map<Number, EuclideanDivision> savedRepresentations = [:]
    REPRESENTATIONS_TO_SAVE.each { Number d ->
      EuclideanDivision a1 = this.savedRepresentations[d]
      EuclideanDivision a2 = other.savedRepresentations[d]
      savedRepresentations[d] = new EuclideanDivision(d,
          (d * a1.quotient * a2.quotient) + (a1.quotient * a2.rest) + (a2.quotient * a1.rest),
          a1.rest * a2.rest).simplify()
    }
    return new TrackedNumber(savedRepresentations: savedRepresentations)
  }

  TrackedNumber div(Integer other) {
    return new TrackedNumber((value / other).longValue())
  }

  int mod(int n) {
    EuclideanDivision number = savedRepresentations[n]
    if (number == null) {
      throw new RuntimeException("Didn't save representation of $n")
    }
    return number.rest
  }

  private static EuclideanDivision computeRepresentation(Number n, Number divider) {
    return new EuclideanDivision(divider, n.longValue() / divider, n.longValue() % divider)
  }
}