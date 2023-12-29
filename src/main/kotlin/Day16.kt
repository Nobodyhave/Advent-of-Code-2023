import java.util.LinkedList
import java.util.Queue

class Day16 {
    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_16.txt")!!
        val split = input.split("\n").map { it.toCharArray() }

        val result = calculateEnergy(split, Cell(0, 0, Direction.RIGHT))
        println(result)
    }

    private fun solve2() {
        val input = readFile("day_16.txt")!!
        val split = input.split("\n").map { it.toCharArray() }

        var result = Int.MIN_VALUE
        for (i in split.indices) {
            result = maxOf(result, calculateEnergy(split, Cell(0, i, Direction.DOWN)))
            result = maxOf(result, calculateEnergy(split, Cell(split.lastIndex, i, Direction.UP)))
        }
        for (i in split[0].indices) {
            result = maxOf(result, calculateEnergy(split, Cell(i, 0, Direction.RIGHT)))
            result = maxOf(result, calculateEnergy(split, Cell(i, split[0].lastIndex, Direction.LEFT)))
        }

        println(result)
    }

    private fun calculateEnergy(grid: List<CharArray>, start: Cell): Int {
        val processed = mutableSetOf<Cell>()
        val queue = LinkedList<Cell>()
        val marked = Array(grid.size) { BooleanArray(grid[0].size) }

        queue.addLast(start)
        while (queue.isNotEmpty()) {
            val cur = queue.pollFirst()
            marked[cur.row][cur.col] = true
            processCell(grid, queue, processed, cur)
        }

        return marked.flatMap { it.asIterable() }.count { it }
    }

    private fun processCell(grid: List<CharArray>, queue: Queue<Cell>, processed: MutableSet<Cell>, cell: Cell) {
        processed.add(cell)

        val nextCells: List<Cell> = when (grid[cell.row][cell.col]) {
            '.' -> {
                when (cell.direction) {
                    Direction.LEFT -> listOf(Cell(cell.row, cell.col - 1, cell.direction))
                    Direction.RIGHT -> listOf(Cell(cell.row, cell.col + 1, cell.direction))
                    Direction.UP -> listOf(Cell(cell.row - 1, cell.col, cell.direction))
                    Direction.DOWN -> listOf(Cell(cell.row + 1, cell.col, cell.direction))
                }
            }
            '\\' -> {
                when (cell.direction) {
                    Direction.LEFT -> listOf(Cell(cell.row - 1, cell.col, Direction.UP))
                    Direction.RIGHT -> listOf(Cell(cell.row + 1, cell.col, Direction.DOWN))
                    Direction.UP -> listOf(Cell(cell.row, cell.col - 1, Direction.LEFT))
                    Direction.DOWN -> listOf(Cell(cell.row, cell.col + 1, Direction.RIGHT))
                }
            }
            '/' -> {
                when (cell.direction) {
                    Direction.LEFT -> listOf(Cell(cell.row + 1, cell.col, Direction.DOWN))
                    Direction.RIGHT -> listOf(Cell(cell.row - 1, cell.col, Direction.UP))
                    Direction.UP -> listOf(Cell(cell.row, cell.col + 1, Direction.RIGHT))
                    Direction.DOWN -> listOf(Cell(cell.row, cell.col - 1, Direction.LEFT))
                }
            }
            '|' -> {
                when (cell.direction) {
                    Direction.LEFT, Direction.RIGHT ->
                        listOf(Cell(cell.row - 1, cell.col, Direction.UP), Cell(cell.row + 1, cell.col, Direction.DOWN))
                    Direction.UP -> listOf(Cell(cell.row - 1, cell.col, cell.direction))
                    Direction.DOWN -> listOf(Cell(cell.row + 1, cell.col, cell.direction))
                }
            }
            '-' -> {
                when (cell.direction) {
                    Direction.LEFT -> listOf(Cell(cell.row, cell.col - 1, cell.direction))
                    Direction.RIGHT -> listOf(Cell(cell.row, cell.col + 1, cell.direction))
                    Direction.UP, Direction.DOWN ->
                        listOf(Cell(cell.row, cell.col - 1, Direction.LEFT), Cell(cell.row, cell.col + 1, Direction.RIGHT))
                }
            }
            else -> throw java.lang.IllegalArgumentException("Unsupported symbol ${grid[cell.row][cell.col]}")
        }
        nextCells.forEach {
            if (isNotOutside(grid, it) && !processed.contains(it)) {
                queue.add(it)
            }
        }
    }

    private fun isNotOutside(grid: List<CharArray>, cell: Cell): Boolean {
        return cell.row >= 0 && cell.row < grid.size && cell.col >= 0 && cell.col < grid[0].size
    }

    private data class Cell(
        val row: Int,
        val col: Int,
        val direction: Direction,
    )

    private enum class Direction {
        LEFT, UP, RIGHT, DOWN
    }
}