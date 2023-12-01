import os { read_lines }

interface Yelling {
  yell(yells &map[string]Yelling) i64
}

struct ConstantYelling {
  value i64
}

 // argument unused but needed for interface
fn (self &ConstantYelling) yell(yells &map[string]Yelling) i64 {
  return self.value
}

type Operand = string | i64

struct OperationYelling {
  operator string
  operand_1 Operand
  operand_2 Operand
}

fn (self &OperationYelling) yell(yells &map[string]Yelling) i64 {
  operand_1 := get_operand_value(yells, self.operand_1)
  operand_2 := get_operand_value(yells, self.operand_2)
  return match self.operator {
    '*' { operand_1 * operand_2 }
    '/' { operand_1 / operand_2 }
    '+' { operand_1 + operand_2 }
    '-' { operand_1 - operand_2 }
    else {
      error("Unknown operator")
      0
    }
  }
}

[inline]
fn get_operand_value(yells &map[string]Yelling, op Operand) i64 {
  return match op {
    string { (*yells)[op].yell(yells) }
    i64 { op }
  }
}

fn main() {
  monkey_map := parse()
  root_monkey := monkey_map['root']
  println(root_monkey.yell(&monkey_map))
}

fn parse() map[string]Yelling {
  mut m := map[string]Yelling {}
  for line in read_lines('input.txt') or { panic(err) } {
    name := line.before(':')
    m[name] = parse_yelling(line.after(': '))
  }
  return m
}

fn parse_yelling(func_string string) Yelling {
  fields := func_string.split(' ')
  if fields.len == 1 {
    return ConstantYelling{value: fields.first().i64()}
  } else {
    return OperationYelling{
      operator: fields[1]
      operand_1: fields[0]
      operand_2: fields[2]
    }
  }
}
