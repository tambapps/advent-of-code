# Advent Of Code

Doing the [advent of code](https://adventofcode.com) using [Marcel](https://tambapps.github.io/marcel/) (my own programming language), 
[Groovy](https://groovy-lang.org/) and [V](https://vlang.io/).

## Marcel
To run these solutions, you'll need to [install Marcel](https://tambapps.github.io/marcel/getting-started/installation.html).

Then run

```shell
marcl solution.mcl
```

## Groovy
Some solutions are to be run using static compilation for better performance. For that add the `--compile-static`
argument like in the below example

```shell
groovy --compile-static solution.groovy
```

`--compile-static` must be **before** the `solution.groovy`, or else it will be treated as an argument for the script

## V

You'll need to [install V](https://github.com/vlang/v#installing-v-from-source) to run such solutions.

Then, use the following command
```shell
v run solution.v
```

## Progress
<details>
  <summary>2022</summary>

| Day | Part 1<br/>Marcel  | Part 2<br/>Marcel  | Part 1<br/>Groovy   | Part 2<br/>Groovy  | Part 1<br/>V       | Part 2<br/>V       |
|-----|--------------------|--------------------|---------------------|--------------------|--------------------|--------------------|
| 1   | :white_check_mark: | :white_check_mark: | :white_check_mark:  | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| 2   | :white_check_mark: | :white_check_mark: | :white_check_mark:  | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| 3   | :white_check_mark: | :white_check_mark: | :white_check_mark:  | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| 4   | :white_check_mark: | :white_check_mark: | :white_check_mark:  | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| 5   | :x:                | :x:                | :white_check_mark:  | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| 6   | :x:                | :x:                | :white_check_mark:  | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| 7   | :x:                | :x:                | :white_check_mark:  | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| 8   | :x:                | :x:                | :white_check_mark:  | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| 9   | :x:                | :x:                | :white_check_mark:  | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| 10  | :x:                | :x:                | :white_check_mark:  | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| 11  | :x:                | :x:                | :white_check_mark:  | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| 12  | :x:                | :x:                | :white_check_mark:  | :white_check_mark: | :x:                | :x:                |
| 13  | :x:                | :x:                | :white_check_mark:  | :white_check_mark: | :x:                | :x:                |
| 14  | :x:                | :x:                | :white_check_mark:  | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| 15  | :x:                | :x:                | :white_check_mark:  | :x:                | :white_check_mark: | :white_check_mark: |
| 16  | :x:                | :x:                | :white_check_mark:  | :white_check_mark: | :x:                | :x:                |
| 17  | :x:                | :x:                | :white_check_mark:  | :x:                | :x:                | :x:                |
| 18  | :x:                | :x:                | :white_check_mark:  | :x:                | :x:                | :x:                |
| 19  | :x:                | :x:                | :white_check_mark:  | :x:                | :x:                | :x:                |
| 20  | :x:                | :x:                | :white_check_mark:  | :x:                | :x:                | :x:                |
| 21  | :x:                | :x:                | :white_check_mark:  | :x:                | :white_check_mark: | :x:                |

</details>