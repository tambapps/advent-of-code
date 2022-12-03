#!/bin/env groovy
f = new File('input.txt')

list = [0]
for (String line in f.readLines()) {
  if (line) {
    list[-1] += line.toInteger()
  } else {
    list << 0
  }
}
// reverse order
list.sort { - it }
// keeping only the 3 highest numbers
list = list[0..2]
println "The 3 highest numbers of calories transported are\n- " + list.join('\n- ')
println "\nSum: " + list.sum()
