import java.util.*

class Day21 {
    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_21.txt")!!
        val split = input.split("\n").map { it.toCharArray() }

        val result = dfs(split, mutableSetOf(), 64, 0, findStart(split))

        println(result)
    }

    private fun solve2() {
        val input = readFile("day_21.txt")!!
        val split = input.split("\n").map { it.toCharArray() }

        for(count in listOf(65, 65 + 1*131, 65 + 2*131)) {
            println("Count: $count Plots ${dfs(split, mutableSetOf(), count, 0, findStart(split))}")
        }
    }

    private fun findStart(grid: List<CharArray>): Cell {
        for (i in grid.indices) {
            for (j in grid[0].indices) {
                if (grid[i][j] == 'S') return Cell(i, j, 0)
            }
        }

        throw IllegalArgumentException("Start is not found in the input")
    }

    private fun dfs(grid: List<CharArray>, processed: MutableSet<Cell>, daySteps: Int, currentSteps: Int, cell: Cell): Int {
        processed.add(cell)

        if (currentSteps == daySteps) return 1

        var count = 0
        for ((i, j) in listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)) {
            val nextCell = Cell(
                cell.row + i,
                cell.col + j,
                cell.steps + 1
            )

//            if (isOutside(grid, nextCell)) continue
            if (grid[(nextCell.row + 100 * grid.size) % grid.size][(nextCell.col + 100 * grid[0].size) % grid[0].size] == '#') continue
            if (processed.contains(nextCell)) continue

            count += dfs(grid, processed, daySteps, currentSteps + 1, nextCell)
        }

        return count
    }

    private fun isOutside(grid: List<CharArray>, cell: Cell): Boolean {
        return cell.row < 0 || cell.row >= grid.size || cell.col < 0 || cell.col >= grid[0].size
    }

    private data class Cell(
        val row: Int,
        val col: Int,
        val steps: Int,
    )
}