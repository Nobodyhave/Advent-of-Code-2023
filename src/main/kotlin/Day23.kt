import kotlin.math.abs
import kotlin.random.Random
import kotlin.random.asJavaRandom

class Day23 {
    var maxPath = Int.MIN_VALUE

    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_23.txt")!!
        val grid = input.split("\n").map { it.toCharArray() }

        val start = findStart(grid)
        val end = findEnd(grid)
        val result = backtrack(grid, Array(grid.size) { BooleanArray(grid[0].size) }, start, end, 0)

        println(result)
    }

    private fun solve2() {
        val input = readFile("day_23.txt")!!
        val grid = input.split("\n").map { it.toCharArray() }

        val start = findStart(grid)
        val end = findEnd(grid)
        val result = backtrackPart2(grid, Array(grid.size) { BooleanArray(grid[0].size) }, start, end, 0)

        println(result)
    }

    private fun findStart(grid: List<CharArray>): Cell {
        return Cell(0, grid.first().withIndex().first { it.value == '.' }.index)
    }

    private fun findEnd(grid: List<CharArray>): Cell {
        return Cell(grid.lastIndex, grid.last().withIndex().first { it.value == '.' }.index)
    }

    private fun backtrack(grid: List<CharArray>, visited: Array<BooleanArray>, cell: Cell, end: Cell, pathLength: Int): Int {
        if(cell == end) return pathLength

        visited[cell.row][cell.col] = true

        var maxPathLength = Int.MIN_VALUE
        when(grid[cell.row][cell.col]) {
            '>' -> {
                val nextCell = Cell(
                    cell.row,
                    cell.col + 1,
                )
                if(!isOutside(grid, nextCell) && !visited[nextCell.row][nextCell.col]) {
                    maxPathLength = maxOf(maxPathLength, backtrack(grid, visited, nextCell, end, pathLength + 1))
                }
            }
            '<' -> {
                val nextCell = Cell(
                    cell.row,
                    cell.col - 1,
                )
                if(!isOutside(grid, nextCell) && !visited[nextCell.row][nextCell.col]) {
                    maxPathLength = maxOf(maxPathLength, backtrack(grid, visited, nextCell, end, pathLength + 1))
                }
            }
            '^' -> {
                val nextCell = Cell(
                    cell.row - 1,
                    cell.col,
                )
                if(!isOutside(grid, nextCell) && !visited[nextCell.row][nextCell.col]) {
                    maxPathLength = maxOf(maxPathLength, backtrack(grid, visited, nextCell, end, pathLength + 1))
                }
            }
            'v' -> {
                val nextCell = Cell(
                    cell.row + 1,
                    cell.col,
                )
                if(!isOutside(grid, nextCell) && !visited[nextCell.row][nextCell.col]) {
                    maxPathLength = maxOf(maxPathLength, backtrack(grid, visited, nextCell, end, pathLength + 1))
                }
            }
            '.' -> {
                for ((i, j) in listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)) {
                    val nextCell = Cell(
                        cell.row + i,
                        cell.col + j,
                    )

                    if(!isOutside(grid, nextCell) && !visited[nextCell.row][nextCell.col]) {
                        maxPathLength = maxOf(maxPathLength, backtrack(grid, visited, nextCell, end, pathLength + 1))
                    }
                }
            }
            '#' -> {

            }
        }

        visited[cell.row][cell.col] = false

        return maxPathLength
    }

    private fun backtrackPart2(grid: List<CharArray>, visited: Array<BooleanArray>, cell: Cell, end: Cell, pathLength: Int): Int {
        if(cell == end){
            if(pathLength > maxPath) {
                maxPath = pathLength
                println(maxPath)
            }
            return pathLength
        }
        if(grid[cell.row][cell.col] == '#') return Int.MIN_VALUE

        visited[cell.row][cell.col] = true

        var maxPathLength = Int.MIN_VALUE
        for ((i, j) in listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)) {
            val nextCell = Cell(
                cell.row + i,
                cell.col + j,
            )

            if(!isOutside(grid, nextCell) && !visited[nextCell.row][nextCell.col]) {
                maxPathLength = maxOf(maxPathLength, backtrackPart2(grid, visited, nextCell, end, pathLength + 1))
            }
        }

        visited[cell.row][cell.col] = false

        return maxPathLength
    }

    private fun isOutside(grid: List<CharArray>, cell: Cell): Boolean {
        return cell.row < 0 || cell.row >= grid.size || cell.col < 0 || cell.col >= grid[0].size
    }

    private data class Cell(
        val row: Int,
        val col: Int,
    )
}