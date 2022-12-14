List<Tuple2<Map<String, Integer>, Map<String, Integer>>> lines = new File('input.txt').readLines().collectMany { String line ->
  def positions = line.split(/->/) // positions are split by an arrow
  return (1..<positions.size()).collect { int i ->
    // XY coordinates are split by a comma
    [positions[i - 1], positions[i]].collect { it.split(/,/)*.toInteger() }
        .collect { [x: it[0], y: it[1]] }
    // grouping line ends into a tuple2
  }.collect { Tuple.tuple(*it) }
}

int highestY = lines.collectMany { it*.y }
    .max()

boolean withGround = false // will be set to true for part 2
// lazy init grid. y -> x -> value
Map<Integer, Map<Integer, String>> grid = [:].withDefault { Integer y ->
  [:].withDefault { Integer x ->
    withGround && y >= highestY + 2
    || lines.any { x in (it.v1.x..it.v2.x) && y in (it.v1.y..it.v2.y) }
        ? '#' : '.'
  }
}

def fall = { ->
  def (int x, int y) = [500, 0]
  while ((withGround || y < highestY) && (grid[y + 1][x] == '.' || grid[y + 1][x - 1] == '.' || grid[y + 1][x + 1] == '.')) {
    if (grid[y + 1][x] != '.' && grid[y + 1][x - 1] == '.') {
      x--
    } else if (grid[y + 1][x] != '.' && grid[y + 1][x + 1] == '.') {
      x++
    }
    y++
  }
  grid[y][x] = 'o'
  return withGround ? x == 500 && y == 0 : y >= highestY // return true if should not fall anymore after
}

int count = 0
while (!fall()) count++

println("Part 1: $count units of sand came to rest")

// resetting everything for part 2
grid.clear()
count = 1 // because we need one more in order to finally know that the source is blocked
withGround = true

while (!fall()) count++
println("Part 2: $count units of sand came to rest")