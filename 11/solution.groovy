// use long because in part 2, we have ridiculously enormous worry levels
List<Monkey> monkeys = []

new File('input.txt').newReader().with {
  while (readLine() != null) {
    Monkey monkey = new Monkey()
    monkey.initialItems = readLine().split(/:/)[1].split(/,/).collect { it.trim() }.collect { it.toLong() }
    monkey.operation = parseOperation(readLine().split(/=/)[1])
    monkey.divisible = readLine().split(/\s+/).last().toLong()
    monkey.trueMonkey = readLine().split(/\s+/).last().toLong()
    monkey.falseMonkey = readLine().split(/\s+/).last().toLong()
    monkeys << monkey
    readLine() // empty line
  }
}

for (int part in [1, 2]) {
  monkeys*.reset()
  Map<Integer, Long> inspectionCountByMonkey = [:].withDefault { 0L }
  int n = part == 1 ? 20 : 10_000
  n.times { runRound(monkeys, part == 1, inspectionCountByMonkey) }
  long monkeyBusiness = inspectionCountByMonkey.values()
      .sort { -it }[0..1]
      .inject {i1, i2 -> i1 * i2 }

  inspectionCountByMonkey.each { int index, long inspectedItemCount ->
    println("Monkey $index inspected items $inspectedItemCount times.")
  }
  println("Part $part: the level of monkey business after $n rounds is $monkeyBusiness")
  println()
}

static void runRound(List<Monkey> monkeys, boolean divideBy3, Map<Integer, Long> inspectionCountByMonkey) {
  monkeys.eachWithIndex { Monkey monkey, int i ->
    for (def item in monkey.items) {
      long updatedItem = monkey.simplify(monkey.operation(item))
      if (divideBy3) updatedItem /= 3L
      int targetMonkey = monkey.targetMonkey(updatedItem)
      monkeys[targetMonkey].items << updatedItem
      inspectionCountByMonkey[i]+= 1
//      println("${monkey.id} threw $updatedItem (previously $item) to monkey $targetMonkey")
    }
    monkey.items.clear()
  }
}

static Closure<Long> parseOperation(String operation) {
  Scanner scanner = new Scanner(operation)
  Closure<Long> n1 = numberClosure(scanner.next())
  Closure<Long> operator = switch (scanner.next()) {
    case '*' -> { long i1, long i2 -> i1 * i2 }
    default -> Long.&sum
  }
  Closure<Long> n2 = numberClosure(scanner.next())
  return { long item ->
    operator(n1(item), n2(item))
  }
}

static Closure<Long> numberClosure(String input) {
  return input ==~ /\d+/ ? { input.toLong() } : { it }
}

class Monkey {
  List<Long> initialItems
  List<Long> items
  Closure<Long> operation // takes in parameter the item to throw
  long divisible
  long trueMonkey
  long falseMonkey

  int targetMonkey(long item) {
    if (item < 0) throw new RuntimeException("caca" + item)
    return item % divisible == 0 ? trueMonkey : falseMonkey
  }

  long simplify(long item) {
    item % divisible == 0 ? divisible : item
  }

  void reset() {
    items = initialItems.collect()
  }
}