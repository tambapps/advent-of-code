import os { read_lines }
import arrays { sum }

fn main() {
	mut items := [0]

  for line in read_lines('input.txt')! {
    if line.len == 0 {
      items << 0
		} else {
      items[items.len - 1]+= line.int()
		}
	}
	items.sort(a > b) // sort in reverse order
	items = items[0..3] // we only care about the 3 highest numbers (right range bound is exclusive)
	println('The 3 highest numbers of calories transported are\n- ' + items.map(it.str()).join('\n- '))
	println('Sum: ${sum(items)!}')
}
