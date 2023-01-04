import os { read_lines }
import regex { regex_opt }
import arrays { min, max }
import math { abs }
 const(
  max_y = i64(4000000)
 )

struct SensorBeacon {
  xs i64
  ys i64
  xb i64
  yb i64
  distance i64
}
struct Interval {
  mut:
  from i64
  to   i64
}

fn main() {
  sensor_beacons := parse()
  x_min := min(sensor_beacons.map(it.xs - it.distance))!
  x_max := max(sensor_beacons.map(it.xs + it.distance))!
  println("min=$x_min, max=$x_max")

  mut impossible_beacons := i64(0)
  y_interest := 2000000

  for x := x_min; x <= x_max; x++ {
    if sensor_beacons.any(len(x, y_interest, it.xs, it.ys) <= it.distance)
    && !sensor_beacons.any(x == it.xb && y_interest == it.yb) {
      impossible_beacons++
    }
  }
  println("Part 1: in the row where y=$y_interest, $impossible_beacons positions cannot contain a beacon")

  nb_threads := 8 // 8 because this is my favorite number
  mut threads := []thread{}
  for i := 0; i < nb_threads; i++ {
    start_y := i * max_y / nb_threads
    end_y := (i + 1) * max_y / nb_threads
    threads << spawn search(start_y, end_y, sensor_beacons)
  }
  search(max_y, max_y, sensor_beacons)
  for t in threads { t.wait() }
}

fn search(start_y i64, end_y i64, sensor_beacons []SensorBeacon) {
  for y := start_y; y <= end_y; y++ {
    mut intervals := []Interval{}
    for sb in sensor_beacons {
      min_dst := len(sb.xs, y, sb.xs, sb.ys)
      offset := sb.distance - min_dst
      if min_dst <= sb.distance {
        intervals << Interval{sb.xs - offset, sb.xs + offset}
      }
    }
    intervals.sort(a.from < b.from)

    for i := 1; i < intervals.len; i++ {
      if intervals[i - 1].to >= intervals[i].from {
        intervals[i - 1].to = math.max(intervals[i - 1].to, intervals[i].to)
        intervals.delete(i)
        i--
      }
    }
    // x and y should only be between 0 and max_y
    intervals = intervals.filter(it.from <= max_y && it.to >= 0)
    if intervals.len > 1 {
      // there is a gap in the intervals
      x := intervals.first().to + 1
      r := x * max_y + y
      println("Part 2: The only position possible is at x=$x, y=$y and the answer is $r")
      exit(0) // found the answer. Don't need to run anymore
    }
  }
}
fn len(x1 i64, y1 i64, x2 i64, y2 i64) i64 {
  return abs(x2 - x1) + abs(y2 - y1)
}

fn parse() []SensorBeacon {
  mut regex := regex_opt(r"^-?\d+") or { panic(err) }
  mut sensor_beacons := []SensorBeacon{}
  for line in read_lines('input.txt') or { panic(err) } {
    values := regex.find_all_str(line).map(it.i64())
    distance := len(values[0], values[1], values[2], values[3])
    sb := SensorBeacon {
      xs: values[0]
      ys: values[1]
      xb: values[2]
      yb: values[3]
      distance: distance
    }
    sensor_beacons << sb
  }
  return sensor_beacons
}