import os { read_lines }
import arrays { sum, min }

struct Directory {
  parent []&Directory // hack because using a simple reference wouldn't work
  name string
mut:
  size int
  children []&Directory = []&Directory{}
}

fn (d Directory) parent() &Directory {
  return d.parent[0]
}

fn (self Directory) total_size() int {
  mut total := self.size
  mut dirs := []&Directory{}
  dirs << self.children
  for dirs.len > 0 {
    mut next_children := []&Directory{}
    for dir in dirs {
      total += dir.size
      next_children << dir.children
    }
    dirs.clear()
    dirs << next_children
  }
  return total
}

fn main() {
  mut root := Directory {
    parent: []
    name: 'root'
  }
  mut all_directories := []&Directory{}
  mut current_dir := &root

  for line in read_lines('input.txt')! {
    if line.starts_with('$ cd') {
      target := line.split(' ').last()
      current_dir = match target {
        '..' { current_dir.parent() }
        '/' { &root }
        else { current_dir.children.filter(it.name == target).first() }
      }
    } else if !line.starts_with('$ ls') { // result of a $ ls
      fields := line.split(' ')
      if fields[0] == 'dir' {
        dir := &Directory {
          parent: [current_dir]
          name: fields[1]
        }
        all_directories << dir
        current_dir.children << dir
      } else {
        current_dir.size+= fields[0].int()
      }
    }
  }
  part1_sum := sum(all_directories.map(it.total_size()).filter(it <= 100000)) or { panic("How could this happen") }
  println('Part 1: the sum of the total size of all directories with a size of at most 100000 is $part1_sum')
  available_space := 70000000 - root.total_size()
  space_needed := 30000000 - available_space
  part2_size := min(all_directories.map(it.total_size()).filter(it >= space_needed)) or { panic("How could this happen") }
  println('Part 2: the size of the directory to delete is $part2_size')
}
