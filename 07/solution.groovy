import groovy.io.FileType

final FileNode root = new FileNode(type: FileType.DIRECTORIES, name: 'ROOT')
FileNode currentDir = root
DIRECTORIES = [root]

for (String line in new File('input.txt').readLines()) {
  if (line.startsWith('$ cd')) {
    def (_, dir) = (line =~ /\$\scd\s(\\/|\w+|\.{2})/)[0]
    currentDir = switch (dir) {
      case '/' -> root
      case '..' -> currentDir.parent
      default -> currentDir.children.find { it.name == dir }
    }
  } else if (!line.startsWith('$ ls')) {
    def (String dirOrSize, String name) = line.split(/\s/)
    if (dirOrSize.isInteger()) {
      currentDir.addChild(new FileNode(type: FileType.FILES, name: name, size: dirOrSize.toInteger()))
    } else {
      FileNode newNode = new FileNode(type: FileType.DIRECTORIES, name: name)
      currentDir.addChild(newNode)
      DIRECTORIES.add(newNode)
    }
  }
}

int sum = DIRECTORIES.collect { it.size }
    .findAll { it <= 100000 }
    .sum()
println "Part 1: the sum of the total size of all directories with a size of at most 100000 is " + sum

int availableSpace = 70000000 - root.size
int spaceNeeded = 30000000 - availableSpace

println "Part 2: the size of the directory to delete is "+ DIRECTORIES.collect { it.size }
    .findAll { it >= spaceNeeded }
    .min()

class FileNode {
  FileNode parent
  String name
  FileType type
  int size
  List<FileNode> children = []

  void addChild(FileNode fileNode) {
    fileNode.parent = this
    children << fileNode
    if (fileNode.size > 0) {
      FileNode node = fileNode
      while ((node = node.parent)) {
        node.size += fileNode.size
      }
    }
  }
}