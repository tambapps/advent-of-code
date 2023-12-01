File f = new File('input.txt')

String text = f.text

def (Integer marker1, Integer marker2) = [null, null] 
int i = 0
while (true) {
  Set uniqueChars1 = (i..i + 3).collect(text.&charAt).toSet()
  Set uniqueChars2 = (i..i + 14).collect(text.&charAt).toSet()
  if (marker1 == null && uniqueChars1.size() == 4) {
    marker1 = i + 4
  }
  if (marker2 == null && uniqueChars2.size() == 14) {
    marker2 = i + 15
  }
  if (marker1 && marker2) break;
  i++
}

println "Part 1: marker is at position $marker1"
println "Part 2: marker is at position $marker2"
