import os { read_lines }
import arrays { copy, reduce }

const (
  old = -1
)

struct Operation {
  operand_1 int
  operand_2 int
  operator string
}

struct Monkey {
  id int
  initial_items []u64
mut:
  items []u64 = []u64{}
  operation Operation
  divider u64
  true_monkey int
  false_monkey int
}

// TODO part 2 gives wrong result
fn main() {
  mut monkeys := parse_monkeys()
  for part in [1, 2] {
     mut inspection_count_by_monkey := map[int]u64{}
     // resetting monkeys
     for mut m in monkeys.values() {
      m.items.clear()
      m.items << m.initial_items
     }
     n := if part == 1 { 20 } else { 10_000 }
     println('Part $part')
     for c := 0; c < n; c++ {
       run_round(mut monkeys, part == 1, mut inspection_count_by_monkey)
     }
     for id, count in inspection_count_by_monkey {
       println("Monkey $id inspected items $count times.")
     }
     mut inspection_counts := inspection_count_by_monkey.values()
     inspection_counts.sort(b < a) // sort by descending order
     monkey_business := reduce(inspection_counts[0..2], fn (t1 u64, t2 u64) u64 { return t1 * t2 })!
     println("The level of monkey business after $n rounds is $monkey_business\n")
  }
}

fn run_round(mut monkeys map[int]&Monkey, divide_by_3 bool, mut inspection_count_by_monkey &map[int]u64) {
  for mut monkey in monkeys.values() {
    for item in monkey.items {
      mut updated_item := monkey.operation.operate(item)
      if divide_by_3 { updated_item = updated_item / 3 }
      monkeys[monkey.target_monkey(updated_item)].items << updated_item
      if _ := inspection_count_by_monkey[monkey.id] { inspection_count_by_monkey[monkey.id]++ }
      else { inspection_count_by_monkey[monkey.id] = u64(1) }
    }
    monkey.items.clear()
  }
}

fn parse_monkeys() map[int]&Monkey {
  lines := read_lines('input.txt') or { panic('File not found') }
  mut i := 0
  mut monkeys := map[int]&Monkey{}
  for i < lines.len {
    m := Monkey {
      id: lines[i].all_before(':').split(' ').last().int()
      initial_items: lines[i + 1].split(':').last().split(',').map(it.trim(' ').u64())
      operation: parse_operation(lines[i + 2].split('=')[1].trim(' '))
      divider: lines[i + 3].split(' ').last().u64()
      true_monkey: lines[i + 4].split(' ').last().int()
      false_monkey: lines[i + 5].split(' ').last().int()
    }
    monkeys[m.id] = &m
    i += 7
  }
  return monkeys
}

fn (self &Monkey) target_monkey(item u64) int {
  return if item % self.divider == 0 { self.true_monkey } else { self.false_monkey }
}

fn (self &Operation) operate(item u64) u64 {
  operand_1 := if self.operand_1 == old { item } else { u64(self.operand_1) }
  operand_2 := if self.operand_2 == old { item } else { u64(self.operand_2) }
  return match self.operator {
    '*' { operand_1 * operand_2 }
    '+' { operand_1 + operand_2 }
    else { panic('Unknown operator ${self.operator}') }
  }
}

fn parse_operation(op string) Operation {
  fields := op.split(' ')
  return Operation {
    operand_1: if fields[0] == 'old' { old } else { fields[0].int() }
    operand_2: if fields[2] == 'old' { old } else { fields[2].int() }
    operator: fields[1]
  }
}
