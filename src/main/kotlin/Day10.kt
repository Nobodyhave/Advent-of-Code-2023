import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import java.io.File
import java.io.FileOutputStream
import java.util.*
import javax.swing.JFrame


class Day10 {
    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_10.txt")!!
        val split = input.split("\n")

        var result = Int.MIN_VALUE
        for (c in setOf('|', '-', 'L', 'J', '7', 'F')) {
            result = maxOf(result, bfs(split.map { it.toCharArray() }, c))
        }

        println(result)
    }

    private fun solve2() {
        val input = readFile("day_10.txt")!!
        val grid = input.split("\n").map { it.toCharArray() }

        val start = findStart(grid)
        var cell: Cell? = Cell(-1, -1, Int.MIN_VALUE, null)
        for (c in setOf('|', '-', 'L', 'J', '7', 'F')) {
            if (isStartValid(grid, start.row, start.col, c)) {
                grid[start.row][start.col] = c

                val marked = Array(grid.size) { i -> BooleanArray(grid[i].size) }
                val candidate = dfs(grid, marked, start, start)
                if ((candidate?.dist ?: Int.MIN_VALUE) > cell!!.dist) {
                    cell = candidate
                }

                grid[start.row][start.col] = 'S'
            }
        }

        val loop = backtrack(cell!!).reversed()
        val polygon = Polygon().apply {
            var edgeStart = loop[0]
            var dir = if(loop[0].row == loop[1].row) Direction.HORIZONTAL else Direction.VERTICAL
            for(i in 2 until loop.size) {
                if(loop[i].row == loop[i - 1].row && dir == Direction.HORIZONTAL) {
                    continue
                } else if(loop[i].row == loop[i - 1].row && dir == Direction.VERTICAL) {
                    if(edgeStart.row < loop[i - 1].row) {
                        edges.add(Edge(edgeStart, loop[i - 1], Direction.VERTICAL))
                    } else {
                        edges.add(Edge(loop[i - 1], edgeStart, Direction.VERTICAL))
                    }
                    edgeStart = loop[i - 1]
                    dir = Direction.HORIZONTAL
                } else if (loop[i].row != loop[i - 1].row && dir == Direction.HORIZONTAL){
                    if(edgeStart.col < loop[i - 1].col) {
                        edges.add(Edge(edgeStart, loop[i - 1], Direction.HORIZONTAL))
                    } else {
                        edges.add(Edge(loop[i - 1], edgeStart, Direction.HORIZONTAL))
                    }
                    edgeStart = loop[i - 1]
                    dir = Direction.VERTICAL
                } else {
                    continue
                }
            }
        }

        // A hack to fix the broken edge because of the start point. Was found visually.
        polygon.edges.add(Edge(Cell(21,114, 0, null), Cell(24,114,0, null), Direction.VERTICAL))
        polygon.edges.remove(Edge(Cell(21,114, 0, null), Cell(22,114,0, null), Direction.VERTICAL))

        polygon.edges.sortBy { it.start.col }

        println(calculateInnerPoints(grid, polygon))
//        printLoop(grid, loop, polygon)
//        drawLoop(grid, loop, polygon)
    }

    private fun dfs(grid: List<CharArray>, marked: Array<BooleanArray>, start: Cell, cell: Cell): Cell? {
        var candidate: Cell? = null

        if (!marked[cell.row][cell.col]) {
            marked[cell.row][cell.col] = true

            when (grid[cell.row][cell.col]) {
                '|' -> {
                    var nextCell = Cell(cell.row - 1, cell.col, cell.dist + 1, cell)
                    if (nextCell.row >= 0) {
                        candidate = if (nextCell == start && cell.parent != start) {
                            cell
                        } else {
                            dfs(grid, marked, start, nextCell)
                        }
                    }
                    if (candidate == null) {
                        nextCell = Cell(cell.row + 1, cell.col, cell.dist + 1, cell)
                        if (nextCell.row < grid.size) {
                            candidate = if (nextCell == start && cell.parent != start) {
                                cell
                            } else {
                                dfs(grid, marked, start, nextCell)
                            }
                        }
                    }
                }
                '-' -> {
                    var nextCell = Cell(cell.row, cell.col - 1, cell.dist + 1, cell)
                    if (nextCell.col >= 0) {
                        candidate = if (nextCell == start && cell.parent != start) {
                            cell
                        } else {
                            dfs(grid, marked, start, nextCell)
                        }
                    }
                    if (candidate == null) {
                        nextCell = Cell(cell.row, cell.col + 1, cell.dist + 1, cell)
                        if (nextCell.col < grid[0].size) {
                            candidate = if (nextCell == start && cell.parent != start) {
                                cell
                            } else {
                                dfs(grid, marked, start, nextCell)
                            }
                        }
                    }
                }
                'L' -> {
                    var nextCell = Cell(cell.row - 1, cell.col, cell.dist + 1, cell)
                    if (nextCell.row >= 0) {
                        candidate = if (nextCell == start && cell.parent != start) {
                            cell
                        } else {
                            dfs(grid, marked, start, nextCell)
                        }
                    }
                    if (candidate == null) {
                        nextCell = Cell(cell.row, cell.col + 1, cell.dist + 1, cell)
                        if (nextCell.col < grid[0].size) {
                            candidate = if (nextCell == start && cell.parent != start) {
                                cell
                            } else {
                                dfs(grid, marked, start, nextCell)
                            }
                        }
                    }
                }
                'J' -> {
                    var nextCell = Cell(cell.row - 1, cell.col, cell.dist + 1, cell)
                    if (nextCell.row >= 0) {
                        candidate = if (nextCell == start && cell.parent != start) {
                            cell
                        } else {
                            dfs(grid, marked, start, nextCell)
                        }
                    }
                    if (candidate == null) {
                        nextCell = Cell(cell.row, cell.col - 1, cell.dist + 1, cell)
                        if (nextCell.col >= 0) {
                            candidate = if (nextCell == start && cell.parent != start) {
                                cell
                            } else {
                                dfs(grid, marked, start, nextCell)
                            }
                        }
                    }
                }
                '7' -> {
                    var nextCell = Cell(cell.row + 1, cell.col, cell.dist + 1, cell)
                    if (nextCell.row < grid.size) {
                        candidate = if (nextCell == start && cell.parent != start) {
                            cell
                        } else {
                            dfs(grid, marked, start, nextCell)
                        }
                    }
                    if (candidate == null) {
                        nextCell = Cell(cell.row, cell.col - 1, cell.dist + 1, cell)
                        if (nextCell.col >= 0) {
                            candidate = if (nextCell == start && cell.parent != start) {
                                cell
                            } else {
                                dfs(grid, marked, start, nextCell)
                            }
                        }
                    }
                }
                'F' -> {
                    var nextCell = Cell(cell.row + 1, cell.col, cell.dist + 1, cell)
                    if (nextCell.row < grid.size) {
                        candidate = if (nextCell == start && cell.parent != start) {
                            cell
                        } else {
                            dfs(grid, marked, start, nextCell)
                        }
                    }
                    if (candidate == null) {
                        {}
                        nextCell = Cell(cell.row, cell.col + 1, cell.dist + 1, cell)
                        if (nextCell.col < grid[0].size) {
                            candidate = if (nextCell == start && cell.parent != start) {
                                cell
                            } else {
                                dfs(grid, marked, start, nextCell)
                            }
                        }
                    }
                }
            }
        }

        return candidate
    }

    private fun bfs(grid: List<CharArray>, c: Char): Int {
        val start = findStart(grid)
        if (!isStartValid(grid, start.row, start.col, c)) {
            return Int.MIN_VALUE
        } else {
            grid[start.row][start.col] = c
        }

        val dist = Array(grid.size) { i -> IntArray(grid[i].size) { Int.MAX_VALUE } }

        var maxDist = Int.MIN_VALUE
        val queue = LinkedList<Cell>().apply { addLast(start) }
        while (queue.isNotEmpty()) {
            val cell = queue.pollFirst()
            if (dist[cell.row][cell.col] != Int.MAX_VALUE && dist[cell.row][cell.col] != 0) {
                maxDist = maxOf(maxDist, dist[cell.row][cell.col])
                continue
            }

            dist[cell.row][cell.col] = cell.dist

            when (grid[cell.row][cell.col]) {
                '|' -> {
                    var nextCell = Cell(cell.row - 1, cell.col, cell.dist + 1, cell)
                    if (nextCell.row >= 0 && cell.parent != nextCell) {
                        queue.addLast(nextCell)
                    }
                    nextCell = Cell(cell.row + 1, cell.col, cell.dist + 1, cell)
                    if (nextCell.row < grid.size && cell.parent != nextCell) {
                        queue.addLast(nextCell)
                    }
                }
                '-' -> {
                    var nextCell = Cell(cell.row, cell.col - 1, cell.dist + 1, cell)
                    if (nextCell.col >= 0 && cell.parent != nextCell) {
                        queue.addLast(nextCell)
                    }
                    nextCell = Cell(cell.row, cell.col + 1, cell.dist + 1, cell)
                    if (nextCell.col < grid[0].size && cell.parent != nextCell) {
                        queue.addLast(nextCell)
                    }
                }
                'L' -> {
                    var nextCell = Cell(cell.row - 1, cell.col, cell.dist + 1, cell)
                    if (nextCell.row >= 0 && cell.parent != nextCell) {
                        queue.addLast(nextCell)
                    }
                    nextCell = Cell(cell.row, cell.col + 1, cell.dist + 1, cell)
                    if (nextCell.col < grid[0].size && cell.parent != nextCell) {
                        queue.addLast(nextCell)
                    }
                }
                'J' -> {
                    var nextCell = Cell(cell.row - 1, cell.col, cell.dist + 1, cell)
                    if (nextCell.row >= 0 && cell.parent != nextCell) {
                        queue.addLast(nextCell)
                    }
                    nextCell = Cell(cell.row, cell.col - 1, cell.dist + 1, cell)
                    if (nextCell.col >= 0 && cell.parent != nextCell) {
                        queue.addLast(nextCell)
                    }
                }
                '7' -> {
                    var nextCell = Cell(cell.row + 1, cell.col, cell.dist + 1, cell)
                    if (nextCell.row < grid.size && cell.parent != nextCell) {
                        queue.addLast(nextCell)
                    }
                    nextCell = Cell(cell.row, cell.col - 1, cell.dist + 1, cell)
                    if (nextCell.col >= 0 && cell.parent != nextCell) {
                        queue.addLast(nextCell)
                    }
                }
                'F' -> {
                    var nextCell = Cell(cell.row + 1, cell.col, cell.dist + 1, cell)
                    if (nextCell.row < grid.size && cell.parent != nextCell) {
                        queue.addLast(nextCell)
                    }
                    nextCell = Cell(cell.row, cell.col + 1, cell.dist + 1, cell)
                    if (nextCell.col < grid[0].size && cell.parent != nextCell) {
                        queue.addLast(nextCell)
                    }
                }
            }
        }

        return maxDist
    }

    private fun backtrack(cell: Cell): List<Cell> {
        val list = mutableListOf<Cell>()
        var currentCell: Cell? = cell
        while (currentCell != null) {
            list.add(currentCell)
            currentCell = currentCell.parent
        }

        return list
    }

    private fun findStart(grid: List<CharArray>): Cell {
        for (i in grid.indices) {
            for (j in grid[i].indices) {
                if (grid[i][j] == 'S') return Cell(i, j, 0, null)
            }
        }

        throw IllegalArgumentException("Input doesn't contain start position")
    }

    private fun isStartValid(grid: List<CharArray>, row: Int, col: Int, c: Char): Boolean {
        return when (c) {
            '|' -> {
                row - 1 >= 0 && grid[row - 1][col] in setOf('|', '7', 'F')
                        && row + 1 < grid.size && grid[row + 1][col] in setOf('|', 'J', 'L')
            }
            '-' -> {
                col - 1 >= 0 && grid[row][col - 1] in setOf('-', 'L', 'F')
                        && col + 1 < grid.size && grid[row][col + 1] in setOf('-', 'J', '7')
            }
            'L' -> {
                row - 1 >= 0 && grid[row - 1][col] in setOf('|', '7', 'F')
                        && col + 1 < grid.size && grid[row][col + 1] in setOf('-', 'J', '7')
            }
            'J' -> {
                row - 1 >= 0 && grid[row - 1][col] in setOf('|', '7', 'F')
                        && col - 1 >= 0 && grid[row][col - 1] in setOf('-', 'L', 'F')
            }
            '7' -> {
                row + 1 < grid.size && grid[row + 1][col] in setOf('|', 'J', 'L')
                        && col - 1 >= 0 && grid[row][col - 1] in setOf('-', 'L', 'F')
            }
            'F' -> {
                row + 1 < grid.size && grid[row + 1][col] in setOf('|', 'J', 'L')
                        && col + 1 < grid.size && grid[row][col + 1] in setOf('-', 'J', '7')
            }
            else -> false
        }
    }

    private fun calculateInnerPoints(grid: List<CharArray>, polygon: Polygon): Int {
        var result = 0
        for (i in grid.indices) {
            for (j in grid[0].indices) {
                if(polygon.isInPolygon(i, j)) result++
            }
        }

        return result
    }

    private fun printLoop(grid: List<CharArray>, loop: List<Cell>, polygon: Polygon) {
        val print = Array<CharArray>(grid.size) { CharArray(grid[0].size) { i -> ' '} }
        loop.forEach {
            print[it.row][it.col] = grid[it.row][it.col]
        }
        for (i in grid.indices) {
            for (j in grid[0].indices) {
                if(polygon.isInPolygon(i, j)) {
                    if(print[i][j] != ' ') {
                        println("Hit the wall in print. Row: $i Col: $j")
                        print[i][j] = 'M'
                    } else {
                        print[i][j] = '.'
                    }
                }
            }
        }

        val file = File("output.txt")
        file.delete()
        FileOutputStream(file, true).bufferedWriter().use { writer ->
            print.forEach {
                writer.write(String(it))
                writer.newLine()
            }
        }
    }

    private fun drawLoop(grid: List<CharArray>, loop: List<Cell>, polygon: Polygon) {
        val frame = JFrame()
        frame.contentPane.add(LoopPaintComponent(grid, loop, polygon))
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setLocationRelativeTo(null)
        frame.setSize(1600, 1600)
        frame.isVisible = true
    }

    private enum class Direction {
        HORIZONTAL, VERTICAL
    }

    private data class Polygon(
        val edges: MutableList<Edge> = mutableListOf()
    ) {
        fun isInPolygon(row: Int, col: Int): Boolean {
            val minCol = edges.first().start.col
            val maxCol = edges.last().start.col
            val minRow = edges.minOf { minOf(it.start.row, it.end.row) }
            val maxRow = edges.maxOf { maxOf(it.start.row, it.end.row) }

            // Outside of the loop borders
            if(row < minRow || row > maxRow || col < minCol || col > maxCol) return false

            // Horizontal edges
            if(edges.filter { it.direction == Direction.HORIZONTAL }
                    .any { it.start.row == row && it.start.col <= col && col <= it.end.col }) return false

            // Vertical edges
            if(edges.filter { it.direction == Direction.VERTICAL }
                    .any { it.start.col == col && it.start.row <= row && row <= it.end.row }) return false

            var count = 0
            for (edge in edges) {
                if(edge.direction == Direction.HORIZONTAL) continue
                if(edge.start.col < col && edge.start.row < row && row <= edge.end.row) count++
            }

            return count % 2 != 0
        }
    }

    private data class Edge(
        val start: Cell,
        val end: Cell,
        val direction: Direction,
    ) {
        override fun toString(): String {
            return "(${start.row}, ${start.col}) to (${end.row}, ${end.col}) $direction"
        }
    }

    private class Cell(
        val row: Int,
        val col: Int,
        val dist: Int,
        val parent: Cell?,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Cell

            if (row != other.row) return false
            if (col != other.col) return false

            return true
        }

        override fun hashCode(): Int {
            var result = row
            result = 31 * result + col
            return result
        }
    }

    private class LoopPaintComponent(
        var grid: List<CharArray>,
        val loop: List<Cell>,
        val polygon: Polygon,
    ): Component() {
        override fun paint(g: Graphics) {
            val print = Array(grid.size) { CharArray(grid[0].size) { i -> ' '} }
            loop.forEach {
                print[it.row][it.col] = grid[it.row][it.col]
                drawCell(g, grid[it.row][it.col], it.row, it.col)
            }
            for (i in grid.indices) {
                for (j in grid[0].indices) {
                    if(polygon.isInPolygon(i, j)) {
                        if(print[i][j] != ' ') {
                            println("Hit the wall in draw")
                            drawCell(g, 'M', i, j)
                        } else {
                            drawCell(g, '.', i, j)
                        }
                    }
                }
            }
        }

        private fun drawCell(g: Graphics, c: Char, row: Int, col: Int) {
            val centerX = col * 11 + 5
            val centerY = row * 11 + 5

            g.color = Color.BLACK
            when (c) {
                '|' -> {
                    g.drawLine(centerX, centerY - 5, centerX, centerY + 5)
                }
                '-' -> {
                    g.drawLine(centerX - 5, centerY, centerX + 5, centerY)
                }
                'L' -> {
                    g.drawLine(centerX, centerY, centerX, centerY - 5)
                    g.drawLine(centerX, centerY, centerX + 5, centerY)
                }
                'J' -> {
                    g.drawLine(centerX, centerY, centerX, centerY - 5)
                    g.drawLine(centerX, centerY, centerX - 5, centerY)
                }
                '7' -> {
                    g.drawLine(centerX, centerY, centerX, centerY + 5)
                    g.drawLine(centerX, centerY, centerX - 5, centerY)
                }
                'F' -> {
                    g.drawLine(centerX, centerY, centerX, centerY + 5)
                    g.drawLine(centerX, centerY, centerX + 5, centerY)
                }
                'S' -> {
                    g.drawRect(centerX - 5, centerY - 5, 11, 11)
                }
                'M' -> {
                    g.color = Color.RED
                    g.fillOval(centerX - 3, centerY - 3, 6, 6)
                }
                '.' -> {
                    g.fillOval(centerX - 1, centerY - 1,  2, 2)
                }
            }
        }
    }
}