import os { read_lines }
import regex { RE, regex_opt }
import arrays { min, max }
import math { abs }

 const(
   coordinates_separator = ","
 )

struct SensorBeacon {
  xs i64
  ys i64
  xb i64
  yb i64
  distance i64
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
[inline]
fn str(x int, y int) string {
  return "$x$coordinates_separator$y"
}