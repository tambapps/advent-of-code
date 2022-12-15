def sensorBeacons = new File('input.txt').readLines().collect { String line ->
  def (_, xs, ys, xb, yb) = (line =~ /Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)/)[0]
  [xs: xs, ys: ys, xb: xb, yb: yb].tap {
    replaceAll { key, value -> value.toInteger() }
    len2 = len2(it.xs, it.ys, it.xb, it.yb)
  }
}

println(sensorBeacons)
final int yInterest = 10
List<Integer> xList = sensorBeacons.collectMany { [it.xs, it.xb] }
final int xMin = xList.min()
final int xMax = xList.max()
println(xMax)

int impossibleBeaconPositions = 0
for (def x in xMin..xMax) {
  if (sensorBeacons.any {x != it.xb && yInterest != it.yb && len2(x, yInterest, it.xs, it.ys) <= it.len2 }) {
    impossibleBeaconPositions++
  }
}

println(impossibleBeaconPositions)
static int len2(int x1, int y1, int x2, int y2) {
  return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)
}