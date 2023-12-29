class Day3 {

    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_3.txt")!!
        val split = input.split("\n")
        val grid = Array(split.size) { i-> split[i] }

        var result = 0

        var i = 0
        while (i < grid.size) {
            var j = 0
            while (j < grid.size) {
                if(grid[i][j].isDigit()) {
                    val start = j
                    while (j < grid[i].length && grid[i][j].isDigit()) j++
                    if(adjacent(grid, start, j - 1, i, ::isSymbol).isNotEmpty()) {
                        val num = grid[i].substring(start, j).toInt()
                        result += num
                    }
                } else {
                    j++
                }
            }
            i++
        }

        println(result)
    }

    private fun solve2() {
        val input = readFile("day_3.txt")!!
        val split = input.split("\n")
        val grid = Array(split.size) { i-> split[i] }

        val gears = mutableMapOf<String, Gear>()
        for (i in grid.indices) {
            for (j in 0 until grid[i].length) {
                if(isGear(grid[i][j])) {
                    gears["${i}-${j}"] = (Gear())
                }
            }
        }

        var result = 0

        var i = 0
        while (i < grid.size) {
            var j = 0
            while (j < grid.size) {
                if(grid[i][j].isDigit()) {
                    val start = j
                    while (j < grid[i].length && grid[i][j].isDigit()) j++
                    val adjacentTo = adjacent(grid, start, j - 1, i, ::isGear)
                    if(adjacentTo.isNotEmpty()) {
                        val num = grid[i].substring(start, j).toInt()
                        updateGear(gears, adjacentTo, num)
                    }
                } else {
                    j++
                }
            }
            i++
        }

        for (gear in gears.values) {
            if(gear.isValid()) {
                result += gear.gearRatio()
            }
        }

        println(result)
    }

    private fun adjacent(grid: Array<String>, start: Int, end: Int, row: Int, predicate: (Char) -> Boolean): String {
         // left column
        if(start != 0){
            if(predicate(grid[row][start - 1])) return "${row}-${start - 1}"
        }
        if(start != 0 && row != 0){
            if(predicate(grid[row - 1][start - 1])) return "${row - 1}-${start - 1}"
        }
        if(start != 0 && row != grid.size - 1){
            if(predicate(grid[row + 1][start - 1])) return "${row + 1}-${start - 1}"
        }

        // right column
        if(end != grid[row].length - 1){
            if(predicate(grid[row][end + 1])) return "${row}-${end + 1}"
        }
        if(end != grid[row].length - 1 && row != 0){
            if(predicate(grid[row - 1][end + 1])) return "${row - 1}-${end + 1}"
        }
        if(end != grid[row].length - 1 && row != grid.size - 1){
            if(predicate(grid[row + 1][end + 1])) return "${row + 1}-${end + 1}"
        }

        // upper row
        if(row != 0) {
            for (i in start..end) {
                if(predicate(grid[row - 1][i])) return "${row - 1}-${i}"
            }
        }

        // bottom row
        if(row != grid.size - 1) {
            for (i in start..end) {
                if(predicate(grid[row + 1][i])) return "${row + 1}-${i}"
            }
        }

        return ""
    }

    private fun isSymbol(char: Char): Boolean {
        return !(char.isDigit() || char == '.')
    }

    private fun isGear(char: Char): Boolean {
        return char == '*'
    }

    private fun updateGear(gears: MutableMap<String, Gear>, gearPosition: String, number: Int) {
        gears[gearPosition]?.apply {
            if(first < 0) {
                first = number
            } else if(second < 0) {
                second = number
            } else {
                throw IllegalStateException("Gear can have only 2 numbers")
            }
        } ?: throw IllegalStateException("Processing unknown gear")
    }

    private data class Gear(
        var first: Int = -1,
        var second: Int = -1,
    ) {
        fun isValid(): Boolean = first >= 0 && second >= 0

        fun gearRatio(): Int = first * second
    }
}