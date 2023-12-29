class Day12 {
    private var count = 0

    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_12.txt")!!
        val split = input.split("\n")

        var result = 0
        split.forEach {
            val resultSet = mutableSetOf<String>()
            val sb = StringBuilder(it.substring(0, it.indexOf(" ")))
            val counts = it.substring(it.indexOf(" ") + 1).split(",").map { it.toInt() }

//            println("---------- $it ----------")
            count = 0
            dfs(resultSet, sb, counts, 0)
            result += resultSet.size
        }

        println(result)
    }

    private fun solve2() {
        val input = readFile("day_12.txt")!!
        val split = input.split("\n")

        var result = 0L
        split.forEach {
            val pattern = it.substring(0, it.indexOf(" "))
            val counts = it.substring(it.indexOf(" ") + 1).split(",").map { it.toInt() }

            result += countArrangements(unfoldPattern(pattern), unfoldCounts(counts))
        }

        println(result)
    }

    private fun unfoldPattern(pattern: String): String {
        val list = mutableListOf<String>()
        repeat(5) { list.add(pattern) }


        return ".${list.joinToString(separator = "?")}."
    }

    private fun unfoldCounts(counts: List<Int>): String {
        val list = mutableListOf<Int>()
        repeat(5) { list.addAll(counts) }

        return "F${
            list.joinToString(separator = "F") {
                buildString {
                    repeat(it) { append("T") }
                }
            }
        }F"
    }

    private fun countArrangements(chars: String, springs: String): Long {
        val n = chars.length
        val m = springs.length
        val dp = Array(n + 1) { LongArray(m + 1) }
        dp[n][m] = 1
        for (i in n - 1 downTo 0) {
            for (j in m - 1 downTo 0) {
                var damaged = false
                var operational = false
                when (chars[i]) {
                    '#' -> {
                        damaged = true
                    }
                    '.' -> {
                        operational = true
                    }
                    else -> {
                        operational = true
                        damaged = true
                    }
                }
                var sum: Long = 0
                if (damaged && springs[j] == 'T') {
                    sum += dp[i + 1][j + 1]
                } else if (operational && springs[j] == 'F') {
                    sum += dp[i + 1][j + 1] + dp[i + 1][j]
                }
                dp[i][j] = sum
            }
        }
        return dp[0][0]
    }

    private fun dfs(result: MutableSet<String>, sb: StringBuilder, counts: List<Int>, index: Int) {
        if (index == sb.length) {
            if (isValid(sb, counts)) {
                result.add(sb.toString())
            }
            return
        }

        if (sb[index] != '?') {
            dfs(result, sb, counts, index + 1)
        } else {
            sb[index] = '.'
            dfs(result, sb, counts, index + 1)
            sb[index] = '#'
            dfs(result, sb, counts, index + 1)
            sb[index] = '?'
        }
    }

    private fun isValid(sb: StringBuilder, counts: List<Int>): Boolean {
        val candidateCounts = sb.split(Regex("[^#]+")).filter { it.isNotEmpty() }.map { it.length }.toList()

        return counts.size == candidateCounts.size && candidateCounts.zip(counts).all { it.first == it.second }
    }
}