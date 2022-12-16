import groovy.transform.ToString

import java.util.concurrent.Executors
class SensorBeacon {
  long xs; long ys; long xb; long yb; long len2
}
@ToString(includePackage = false, includeNames = true)
class Interval {long start; long end}
def sensorBeacons = new File('input.txt').readLines().collect { String line ->
  List<Long> list = (line =~ /\d+/).findAll().collect { it.toString().toLong() }
  new SensorBeacon(xs: list[0], ys: list[1], xb: list[2], yb: list[3]).tap {
    len2 = len2(it.xs, it.ys, it.xb, it.yb)
  }
}

// part2
def (minX, maxX) = [0, 4000000]
def (minY, maxY) = [0, 4000000]


def executor = Executors.newFixedThreadPool(8)
for (def y in 0..4000000) {
  executor.submit {
    def l = System.currentTimeMillis()

    List<Interval> intervals = merge(sensorBeacons.findResults {
      long minDst = len2(it.xs, y, it.xs, it.ys)
      long offset = it.len2 - minDst
      if (minDst > it.len2) return null

      return new Interval(start: Long.valueOf(it.xs - offset), end: Long.valueOf(it.xs + offset))
    } as List<Interval>)

    Iterator<Long> iterator = new OutsideIntervalIterator(intervals: intervals)
    while (iterator.hasNext()) {
      long x = iterator.next()
      println("x=$x, y=$y")
    }
  }
}

List<Interval> merge(List<Interval> intervals) {
  if (intervals.size() <= 1)
    return intervals;

  // Sort by ascending starting point using an anonymous Comparator
  intervals.sort((i1, i2) -> Long.compare(i1.start, i2.start));

  List<Interval> result = new LinkedList<Interval>();
  long start = intervals.get(0).start;
  long end = intervals.get(0).end;

  for (Interval interval : intervals) {
    if (interval.start <= end) // Overlapping intervals, move the end if needed
      end = Math.max(end, interval.end);
    else {                     // Disjoint intervals, add the previous one and reset bounds
      result.add(new Interval(start:  start,end:  end));
      start = interval.start;
      end = interval.end;
    }
  }

  // Add the last interval
  result.add(new Interval(start: start, end: end));
  return result;
}

class OutsideIntervalIterator implements Iterator<Long> {

  long current = 0
  int i = 0
  private List<Interval> intervals
  @Override
  boolean hasNext() {
    if (i >= intervals.size() || current > 4000000L) return false
    Interval interval = intervals[i]
    if (current < interval.start) return true
    current = interval.end + 1
    return i++ < intervals.size()
  }

  @Override
  Long next() {
    return current++
  }

  private void update() {
    Interval interval = intervals[i]
  }
}

static long len2(long x1, long y1, long x2, long y2) {
  def r= Math.abs(x2 - x1) + Math.abs(y2 - y1)
  if (r < 0) throw new RuntimeException("use bigger number class")
  r
}