def sensorBeacons = new File('input.txt').readLines().collect { String line ->
  def (_, xs, ys, xb, yb) = (line =~ /Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)/)[0]
  [xs: xs, ys: ys, xb: xb, yb: yb].tap {
    replaceAll { key, value -> value.toLong() }
    len2 = len2(it.xs, it.ys, it.xb, it.yb)
  }
}

final long yInterest = 2000000
final long xMin = sensorBeacons.collect() {it.xs - it.len2  }.min()
final long xMax = sensorBeacons.collect() {it.xs + it.len2  }.max()
println("min=$xMin, max=$xMax")
long impossibleBeaconPositions = 0
for (def x in xMin..xMax) {
  if (!sensorBeacons.any { x == it.xb && yInterest == it.yb }
    && sensorBeacons.any { len2(x, yInterest, it.xs, it.ys) <= it.len2 }) {
    impossibleBeaconPositions++
  }
}
println("Part 1: in the row where y=$yInterest, $impossibleBeaconPositions positions cannot contain a beacon")

// part2
def (min, max) = [0, 4000000]
for (def x in min..max) {
  for (def y in min..max) {
    if (!sensorBeacons.any { x == it.xb && y == it.yb }
        && !sensorBeacons.any { len2(x, y, it.xs, it.ys) <= it.len2 }) {
      println("x=$x, y=$y")
    }
  }
}

static long len2(long x1, long y1, long x2, long y2) {
  def r= Math.abs(x2 - x1) + Math.abs(y2 - y1)
  if (r < 0) throw new RuntimeException("use bigger number class")
  r
}