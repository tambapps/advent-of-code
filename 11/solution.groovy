// use RepresentedNumber because in part 2, we have ridiculously enormous worry levels
List<Monkey> monkeys = []
new File('input.txt').newReader().with {
  while (readLine() != null) {
    Monkey monkey = new Monkey()
    monkey.initialItems = readLine().split(/:/)[1].split(/,/).collect { it.trim() }.collect { it.toInteger() }
    monkey.operation = parseOperation(readLine().split(/=/)[1])
    monkey.divider = readLine().split(/\s+/).last().toInteger()
    monkey.trueMonkey = readLine().split(/\s+/).last().toInteger()
    monkey.falseMonkey = readLine().split(/\s+/).last().toInteger()
    monkeys << monkey
    readLine() // empty line
  }
}

RepresentedNumber.DIVIDERS = monkeys*.divider

for (int part in [1, 2]) {
  monkeys*.reset()
  Map<Integer, Long> inspectionCountByMonkey = [:].withDefault { 0L }
  int n = part == 1 ? 20 : 10_000
  n.times { runRound(monkeys, part == 1, inspectionCountByMonkey) }
  Long monkeyBusiness = inspectionCountByMonkey.values()
      .sort { -it }[0..1]
      .inject {i1, i2 -> i1 * i2 }

  println("Part $part")
  inspectionCountByMonkey.each { int index, Long inspectedItemCount ->
    println("Monkey $index inspected items $inspectedItemCount times.")
  }
  println("The level of monkey business after $n rounds is $monkeyBusiness")
  println()
}

static void runRound(List<Monkey> monkeys, boolean divideBy3, Map<Integer, Long> inspectionCountByMonkey) {
  monkeys.eachWithIndex { Monkey monkey, int i ->
    for (def item in monkey.items) {
      def updatedItem = monkey.inspect(item, divideBy3)
      int targetMonkey = monkey.targetMonkey(updatedItem)
      monkeys[targetMonkey].items << updatedItem
      inspectionCountByMonkey[i]++
    }
    monkey.items.clear()
  }
}

static Closure<RepresentedNumber> parseOperation(String operation) {
  Scanner scanner = new Scanner(operation)
  Closure<RepresentedNumber> n1 = numberClosure(scanner.next())
  Closure<RepresentedNumber> operator = switch (scanner.next()) {
    case '*' -> { i1, i2 -> i1 * i2 }
    case '+' -> { i1, i2 -> i1 + i2 }
  }
  Closure<RepresentedNumber> n2 = numberClosure(scanner.next())
  return { item ->
    operator(n1(item), n2(item))
  }
}

static Closure<RepresentedNumber> numberClosure(String input) {
  // can be a number or 'old', which references to the current item being handled
  return input ==~ /\d+/ ? { new RepresentedNumber(input.toInteger()) } : { it }
}

class Monkey {
  List<Integer> initialItems // or rather the worried level of the item
  List<RepresentedNumber> items
  Closure<RepresentedNumber> operation // takes in parameter the item to throw to another monkey
  Integer divider
  Integer trueMonkey
  Integer falseMonkey

  int targetMonkey(RepresentedNumber item) {
    return item % divider == 0 ? trueMonkey : falseMonkey
  }

  RepresentedNumber inspect(RepresentedNumber item, boolean divideBy3) {
    RepresentedNumber result = operation(item)
    return divideBy3 ? result / 3 : result
  }

  void reset() {
    items = initialItems.collect {new RepresentedNumber(it) }
  }
}

/**
 * Represents an euclidean division: value = divider * quotient + rest
 */
class EuclideanDivision {
  final long divider
  final long quotient
  final long rest

  EuclideanDivision(Number divider, Number quotient, Number rest) {
    this.divider = divider.longValue()
    this.quotient = quotient.longValue()
    this.rest = rest.longValue()
  }

  static EuclideanDivision compute(Number n, Number divider) {
    return new EuclideanDivision(divider, n / divider, n % divider)
  }

  EuclideanDivision simplify() {
    long rest = this.rest
    long quotient = this.quotient
    while (rest >= divider) {
      rest -= divider
      quotient++
    }
    return new EuclideanDivision(divider, quotient, rest)
  }

  // the dividend
  BigDecimal getValue() {
    // returning bigDecimal as it may not fit in an int/long
    return (new BigDecimal(quotient) * new BigDecimal(divider)) + new BigDecimal(rest)
  }
}

/**
 * Class representing a number. It doesn't store the number's value but rather several representations of
 * the number's euclidean division by several divider
 */
class RepresentedNumber {
  static List<Integer> DIVIDERS // we will save all Monkey Test: euclidean divisions
  private Map<Number, EuclideanDivision> euclideanDivisions = [:]

  private RepresentedNumber() {}

  RepresentedNumber(Number n) {
    DIVIDERS.each { euclideanDivisions[it] = EuclideanDivision.compute(n, it) }
  }

  BigDecimal getValue() {
    // should all be the same so let's just return the first
    return euclideanDivisions.values().first().value
  }

  // n1 = d * q1 + r1
  // n2 = d * q2 + r2
  // (n1 + n2) = (q1 + q2) * d + (r1 + r2)
  RepresentedNumber plus(RepresentedNumber other) {
    Map<Number, EuclideanDivision> euclideanDivisions = [:]
    DIVIDERS.each { Number d ->
      EuclideanDivision a1 = this.euclideanDivisions[d]
      EuclideanDivision a2 = other.euclideanDivisions[d]
      euclideanDivisions[d] = new EuclideanDivision(d, a1.quotient + a2.quotient, a1.rest + a2.rest).simplify()
    }
    return new RepresentedNumber(euclideanDivisions: euclideanDivisions)
  }

  // n1 = d * q1 + r1
  // n2 = d * q2 + r2
  // (n1 * n2) = (d * q1 + r1) * (d * q2 + r2)
  //           = d * d  * q1 * q2 + d * q1 * r2 + r1 * d * q2 + r1 * r2
  //           = d * ((d * q1 * q2) + (q1 * r2) + (r1 * q2)) + r1 * r2
  RepresentedNumber multiply(RepresentedNumber other) {
    Map<Number, EuclideanDivision> euclideanDivisions = [:]
    DIVIDERS.each { Number d ->
      EuclideanDivision a1 = this.euclideanDivisions[d]
      EuclideanDivision a2 = other.euclideanDivisions[d]
      euclideanDivisions[d] = new EuclideanDivision(d,
          (d * a1.quotient * a2.quotient) + (a1.quotient * a2.rest) + (a2.quotient * a1.rest),
          a1.rest * a2.rest).simplify()
    }
    return new RepresentedNumber(euclideanDivisions: euclideanDivisions)
  }

  RepresentedNumber div(Number other) { // needed because we divide by 3 for part 1
    return new RepresentedNumber((value / other).longValue())
  }

  long mod(Number n) {
    EuclideanDivision number = euclideanDivisions[n]
    if (number == null) {
      throw new RuntimeException("Didn't save representation of euclidean division by $n")
    }
    return number.rest
  }
}