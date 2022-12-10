import groovy.transform.Field

@Field final int WIDTH = 40
@Field final int HEIGHT = 6
@Field final char DARK_PIXEL = '.'
@Field final char LIT_PIXEL = '#'

@Field int cycle = 1
@Field int x = 1
@Field int sum = 0 // part 1
@Field List<Character> crt = [] // part 2

for (String line in new File('input.txt').readLines()) {
  if (line.startsWith('addx ')) {
    int n = line.split(/\s/).last().toInteger()
    nextCycle() // takes 2 cycles to finish
    nextCycle(n)
  } else { // noop instruction
    nextCycle()
  }
}

void nextCycle(int value = 0) {
  // handling current cycle
  int crtPosition = (cycle - 1) % WIDTH
  IntRange spriteRange = (x - 1)..(x + 1) // sprite of width 3 and centered at x -> [x-1, x, x+1]
  crt.add((crtPosition % WIDTH) in spriteRange ? LIT_PIXEL : DARK_PIXEL)

  // handling next cycle
  cycle++
  x += value
  if (cycle in [20, 60, 100, 140, 180, 220]) {
    println("$cycle * $x = ${cycle * x}")
    sum += cycle * x
  }
}

println("Part 1: the sum of these six signal strengths is $sum")
println("Part 2: CRT output")
for (def j in 0..<HEIGHT) {
  for (def i in 0..<WIDTH) {
    print(crt[j * WIDTH + i])
  }
  println()
}
