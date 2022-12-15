def sensorBeacons = new File('input.txt').readLines().collect { String line ->
  def (_, xs, ys, xb, yb) = (line =~ /Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)/)[0]
  [xs: xs, ys: ys, xb: xb, yb: yb].tap {
    replaceAll { key, value -> value.toLong() }
    len2 = len2(it.xs, it.ys, it.xb, it.yb)
  }
}

println(sensorBeacons)
final long yInterest = 26
final long xMin = sensorBeacons.collect() {it.xs - it.len2  }
    .min()
final long xMax = sensorBeacons.collect() {it.xs + it.len2  }.max()
println("min=$xMin, max=$xMax")
long impossibleBeaconPositions = 0
for (def x in xMin..xMax) {
  if (!sensorBeacons.any { x == it.xb && yInterest == it.yb }
    && sensorBeacons.any { len2(x, yInterest, it.xs, it.ys) <= it.len2 }) {
    impossibleBeaconPositions++
  }
}

println(impossibleBeaconPositions)
static long len2(long x1, long y1, long x2, long y2) {
  def r= Math.abs(x2 - x1) + Math.abs(y2 - y1)
  if (r < 0) throw new RuntimeException("caca")
  r
}