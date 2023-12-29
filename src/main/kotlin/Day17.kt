import java.util.*

class Day17 {
    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_17.txt")!!
        val grid = input.split("\n").map { it.toCharArray().map { it.digitToInt() }.toIntArray() }

        var result = calculateHeatLoss(grid, Cell(0, 0, Direction.DOWN, 1, 0), 1, ::processCellPart1)
        result = minOf(result, calculateHeatLoss(grid, Cell(0, 0, Direction.RIGHT, 1, 0), 1, ::processCellPart1))

        println(result)
    }

    private fun solve2() {
        val input = readFile("day_17.txt")!!
        val grid = input.split("\n").map { it.toCharArray().map { it.digitToInt() }.toIntArray() }

        var result = calculateHeatLoss(grid, Cell(0, 0, Direction.DOWN, 1, 0), 4, ::processCellPart2)
        result = minOf(result, calculateHeatLoss(grid, Cell(0, 0, Direction.RIGHT, 1, 0), 4, ::processCellPart2))

        println(result)
    }

    private fun calculateHeatLoss(
        grid: List<IntArray>,
        start: Cell,
        minSteps: Int,
        processFun: (List<IntArray>, Queue<Cell>, MutableMap<Cell, Int>, MutableMap<Cell, Cell>, Cell) -> Unit
    ): Int {
        val queue = PriorityQueue<Cell>()
        val distTo = mutableMapOf<Cell, Int>()
        val edgeTo = mutableMapOf<Cell, Cell>()
        distTo[start] = 0
        queue.add(start)

        while (!queue.isEmpty()) {
            val cell = queue.poll()
            processFun(grid, queue, distTo, edgeTo, cell)
        }

        var result = Int.MAX_VALUE
        distTo.forEach {
            if (it.key.row == grid.lastIndex && it.key.col == grid[0].lastIndex && it.key.steps >= minSteps) {
                result = minOf(result, it.value)
            }
        }

        return result
    }

    private fun processCellPart1(
        grid: List<IntArray>,
        queue: Queue<Cell>,
        distTo: MutableMap<Cell, Int>,
        edgeTo: MutableMap<Cell, Cell>,
        cell: Cell,
    ) {
        if (cell.steps < 3) {
            goStraight(grid, queue, distTo, edgeTo, cell)
        }
        makeTurn(grid, queue, distTo, edgeTo, cell)
    }

    private fun processCellPart2(
        grid: List<IntArray>, queue: Queue<Cell>,
        distTo: MutableMap<Cell, Int>,
        edgeTo: MutableMap<Cell, Cell>,
        cell: Cell,
    ) {
        if (cell.steps < 4) {
            goStraight(grid, queue, distTo, edgeTo, cell)
        } else if (cell.steps < 10) {
            goStraight(grid, queue, distTo, edgeTo, cell)
            makeTurn(grid, queue, distTo, edgeTo, cell)
        } else {
            makeTurn(grid, queue, distTo, edgeTo, cell)
        }
    }

    private fun goStraight(
        grid: List<IntArray>,
        queue: Queue<Cell>,
        distTo: MutableMap<Cell, Int>,
        edgeTo: MutableMap<Cell, Cell>,
        cell: Cell
    ) {
        when (cell.direction) {
            Direction.LEFT -> {
                if (isNotOutside(grid, cell.row, cell.col - 1)) {
                    val nextCell = cell.copy(
                        col = cell.col - 1,
                        steps = cell.steps + 1,
                        heatLoss = distTo.getOrPut(cell) { 10000 } + grid[cell.row][cell.col - 1],
                    )
                    if (distTo.getOrPut(nextCell) { 10000 } > nextCell.heatLoss) {
                        distTo[nextCell] = nextCell.heatLoss
                        edgeTo[nextCell] = cell
                        queue.add(nextCell)
                    }
                }
            }
            Direction.RIGHT -> {
                if (isNotOutside(grid, cell.row, cell.col + 1)) {
                    val nextCell = cell.copy(
                        col = cell.col + 1,
                        steps = cell.steps + 1,
                        heatLoss = distTo.getOrPut(cell) { 10000 } + grid[cell.row][cell.col + 1],
                    )
                    if (distTo.getOrPut(nextCell) { 10000 } > nextCell.heatLoss) {
                        distTo[nextCell] = nextCell.heatLoss
                        edgeTo[nextCell] = cell
                        queue.add(nextCell)
                    }
                }
            }
            Direction.UP -> {
                if (isNotOutside(grid, cell.row - 1, cell.col)) {
                    val nextCell = cell.copy(
                        row = cell.row - 1,
                        steps = cell.steps + 1,
                        heatLoss = distTo.getOrPut(cell) { 10000 } + grid[cell.row - 1][cell.col],
                    )
                    if (distTo.getOrPut(nextCell) { 10000 } > nextCell.heatLoss) {
                        distTo[nextCell] = nextCell.heatLoss
                        edgeTo[nextCell] = cell
                        queue.add(nextCell)
                    }
                }
            }
            Direction.DOWN -> {
                if (isNotOutside(grid, cell.row + 1, cell.col)) {
                    val nextCell = cell.copy(
                        row = cell.row + 1,
                        steps = cell.steps + 1,
                        heatLoss = distTo.getOrPut(cell) { 10000 } + grid[cell.row + 1][cell.col],
                    )
                    if (distTo.getOrPut(nextCell) { 10000 } > nextCell.heatLoss) {
                        distTo[nextCell] = nextCell.heatLoss
                        edgeTo[nextCell] = cell
                        queue.add(nextCell)
                    }
                }
            }
        }
    }

    private fun makeTurn(
        grid: List<IntArray>,
        queue: Queue<Cell>,
        distTo: MutableMap<Cell, Int>,
        edgeTo: MutableMap<Cell, Cell>,
        cell: Cell
    ) {
        when (cell.direction) {
            Direction.LEFT, Direction.RIGHT -> {
                if (isNotOutside(grid, cell.row + 1, cell.col)) {
                    val nextCell = cell.copy(
                        row = cell.row + 1,
                        steps = 1,
                        direction = Direction.DOWN,
                        heatLoss = distTo.getOrPut(cell) { 10000 } + grid[cell.row + 1][cell.col],
                    )
                    if (distTo.getOrPut(nextCell) { 10000 } > nextCell.heatLoss) {
                        distTo[nextCell] = nextCell.heatLoss
                        edgeTo[nextCell] = cell
                        queue.add(nextCell)
                    }
                }
                if (isNotOutside(grid, cell.row - 1, cell.col)) {
                    val nextCell = cell.copy(
                        row = cell.row - 1,
                        steps = 1,
                        direction = Direction.UP,
                        heatLoss = distTo.getOrPut(cell) { 10000 } + grid[cell.row - 1][cell.col],
                    )
                    if (distTo.getOrPut(nextCell) { 10000 } > nextCell.heatLoss) {
                        distTo[nextCell] = nextCell.heatLoss
                        edgeTo[nextCell] = cell
                        queue.add(nextCell)
                    }
                }
            }
            Direction.UP, Direction.DOWN -> {
                if (isNotOutside(grid, cell.row, cell.col + 1)) {
                    val nextCell = cell.copy(
                        col = cell.col + 1,
                        steps = 1,
                        direction = Direction.RIGHT,
                        heatLoss = distTo.getOrPut(cell) { 10000 } + grid[cell.row][cell.col + 1],
                    )
                    if (distTo.getOrPut(nextCell) { 10000 } > nextCell.heatLoss) {
                        distTo[nextCell] = nextCell.heatLoss
                        edgeTo[nextCell] = cell
                        queue.add(nextCell)
                    }
                }
                if (isNotOutside(grid, cell.row, cell.col - 1)) {
                    val nextCell = cell.copy(
                        col = cell.col - 1,
                        steps = 1,
                        direction = Direction.LEFT,
                        heatLoss = distTo.getOrPut(cell) { 10000 } + grid[cell.row][cell.col - 1],
                    )
                    if (distTo.getOrPut(nextCell) { 10000 } > nextCell.heatLoss) {
                        distTo[nextCell] = nextCell.heatLoss
                        edgeTo[nextCell] = cell
                        queue.add(nextCell)
                    }
                }
            }
        }
    }

    private fun isNotOutside(grid: List<IntArray>, row: Int, col: Int): Boolean {
        return row >= 0 && row < grid.size && col >= 0 && col < grid[0].size
    }

    private data class Cell(
        val row: Int,
        val col: Int,
        val direction: Direction,
        val steps: Int,
        val heatLoss: Int,
    ) : Comparable<Cell> {
        override fun compareTo(other: Cell): Int {
            return heatLoss - other.heatLoss
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Cell

            if (row != other.row) return false
            if (col != other.col) return false
            if (direction != other.direction) return false
            if (steps != other.steps) return false

            return true
        }

        override fun hashCode(): Int {
            var result = row
            result = 31 * result + col
            result = 31 * result + direction.hashCode()
            result = 31 * result + steps
            return result
        }
    }

    private enum class Direction {
        RIGHT, UP, LEFT, DOWN
    }
}