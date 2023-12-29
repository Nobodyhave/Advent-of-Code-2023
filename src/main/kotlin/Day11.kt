import kotlin.math.abs

class Day11 {
    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_11.txt")!!
        val split = input.split("\n").map { it.toCharArray() }

        val expandedUniverse = expand(split, 2)
        expandedUniverse.calculateGalaxies()
        val result = expandedUniverse.calculateDistance()

        println(result)
    }

    private fun solve2() {
        val input = readFile("day_11.txt")!!
        val split = input.split("\n").map { it.toCharArray() }

        val expandedUniverse = expand(split, 1000000)
        expandedUniverse.calculateGalaxies()
        val result = expandedUniverse.calculateDistance()

        println(result)
    }

    private fun expand(universe: List<CharArray>, scale: Int): Expansion {
        val rowsToExpand = mutableListOf<Int>()
        val colsToExpand = mutableListOf<Int>()

        rowsToExpand.addAll(universe.withIndex().filter { it.value.all { it == '.' } }.map { it.index })
        for (col in universe[0].indices) {
            var expandable = true
            for (row in universe.indices) {
                if(universe[row][col] != '.') {
                    expandable = false
                    break
                }
            }
            if(expandable) {
                colsToExpand.add(col)
            }
        }

        return Expansion(universe, rowsToExpand, colsToExpand, scale)
    }

    private data class Expansion(
        val universe: List<CharArray>,
        val rows: List<Int>,
        val cols: List<Int>,
        val scale: Int,
        val galaxies: MutableList<Cell> = mutableListOf(),
    ) {
        fun calculateGalaxies() {
            val sortedRows = rows.sorted()
            val sortedCols = cols.sorted()

            universe.forEachIndexed { row, array ->
                array.forEachIndexed { col, char ->
                    val countRows = sortedRows.indexOfLast { it < row } + 1
                    val countCols = sortedCols.indexOfLast { it < col } + 1

                    if(char == '#') {
                        galaxies.add(Cell(countRows.toLong() * (scale - 1) + row, countCols.toLong() * (scale - 1) + col))
                    }
                }
            }
        }

        fun calculateDistance(): Long {
            var distance = 0L
            for (i in galaxies.indices) {
                for (j in i + 1 until galaxies.size) {
                    distance += abs(galaxies[i].row - galaxies[j].row) + abs(galaxies[i].col - galaxies[j].col)
                }
            }

            return distance
        }
    }

    private data class Cell(
        val row: Long,
        val col: Long,
    )
}