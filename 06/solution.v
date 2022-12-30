import os { read_file }

fn main() {
  text := read_file('input.txt')!
  println('Part 1: marker is at position ' + search_marker_position(text, 4).str())
  println('Part 2: marker is at position ' + search_marker_position(text, 14).str())
}

fn search_marker_position(text string, window_size int) int {
  for i := window_size; i < text.len; i++ {
    chunk := text[(i-window_size)..(i)]
    if are_all_unique(chunk) {
      return i
    }
  }
  panic("Shouldn't happen")
}

fn are_all_unique(s string) bool {
  mut m := map[u8]bool{}
  for e in s {
    if m[e] {
      return false
    }
    m[e] = true
  }
  return true
}