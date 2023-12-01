// execute it with --compile-static for better performance
class SensorBeacon { long xs; long ys; long xb; long yb; long len }
List<SensorBeacon> sensorBeacons = new File('input.txt').readLines().collect { String line ->
  List<Long> coordinates = (line =~ /-?\d+/).findAll().collect { it.toString().toLong() }
  new SensorBeacon(xs: coordinates[0], ys: coordinates[1], xb: coordinates[2], yb: coordinates[3]).tap {
    len = len(it.xs, it.ys, it.xb, it.yb)
  }
}

final long yInterest = 2000000
final long xMin = sensorBeacons.collect() {it.xs - it.len  }.min()
final long xMax = sensorBeacons.collect() {it.xs + it.len  }.max()
println("min=$xMin, max=$xMax")
long impossibleBeaconPositions = 0
for (def x in xMin..xMax) {
  if (!sensorBeacons.any { x == it.xb && yInterest == it.yb }
    && sensorBeacons.any { len(x, yInterest, it.xs, it.ys) <= it.len }) {
    impossibleBeaconPositions++
  }
}

println("Part 1: in the row where y=$yInterest, $impossibleBeaconPositions positions cannot contain a beacon")
static long len(long x1, long y1, long x2, long y2) {
  def r= Math.abs(x2 - x1) + Math.abs(y2 - y1) // Manhattan distance
  if (r < 0) throw new RuntimeException("use bigger number class")
  r
}