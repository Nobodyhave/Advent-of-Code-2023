class Day13 {
    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_13.txt")!!
        val split = input.split("\n\n")

        var result = 0
        split.forEach {
            val pattern = Pattern(it.split("\n"))
            result += pattern.getNotes(0)
        }

        println(result)
    }

    private fun solve2() {
        val input = readFile("day_13.txt")!!
        val split = input.split("\n\n")

        var result = 0
        split.forEach {
            val pattern = Pattern(it.split("\n"))
            result += pattern.getNotes(1)
        }

        println(result)
    }

    private data class Pattern(
        private val input: List<String>
    ) {
        private val pattern: List<CharArray> = input.map { it.toCharArray() }

        private operator fun get(row: Int, col: Int): Char {
            return pattern[row][col]
        }

        private fun compareRows(row1: Int, row2: Int): Int {
            var count = 0

            for (i in pattern[0].indices) {
                if(get(row1, i) != get(row2, i)){
                    count++
                }
            }

            return count
        }

        private fun compareColumns(col1: Int, col2: Int): Int {
            var count = 0

            for (i in pattern.indices) {
                if(get(i, col1) != get(i, col2)) {
                    count++
                }
            }

            return count
        }

        fun getNotes(errorsTarget: Int): Int {
            for (i in 1..pattern.lastIndex) {
                var errors = 0
                var left = i - 1
                var right = i
                do {
                    errors += compareRows(left, right)
                    left--
                    right++
                } while (left >= 0 && right < pattern.size)

                if(errors == errorsTarget) return i * 100
            }

            for (i in 1..pattern[0].lastIndex) {
                var errors = 0
                var left = i - 1
                var right = i
                do {
                    errors += compareColumns(left, right)
                    left--
                    right++
                } while (left >= 0 && right < pattern[0].size)

                if(errors == errorsTarget) return i
            }

            return -1
        }
    }
}