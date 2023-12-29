class Day9 {
    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_9.txt")!!
        val split = input.split("\n")

        val history = split.map { it.split(Regex(" +")).map { it.toInt() } }
        val result = history.sumOf { extrapolateForward(it) }

        println(result)
    }

    private fun solve2() {
        val input = readFile("day_9.txt")!!
        val split = input.split("\n")

        val history = split.map { it.split(Regex(" +")).map { it.toInt() } }
        val result = history.sumOf { extrapolateBackward(it) }

        println(result)
    }

    private fun extrapolateForward(measurements: List<Int>): Int {
        val sequences = calculateSequences(measurements)

        var extrapolation = 0
        for (i in sequences.lastIndex - 1 downTo 0) {
            extrapolation += sequences[i].last()
        }

        return extrapolation
    }

    private fun extrapolateBackward(measurements: List<Int>): Int {
        val sequences = calculateSequences(measurements)

        var extrapolation = 0
        for (i in sequences.lastIndex - 1 downTo 0) {
            extrapolation = sequences[i].first() - extrapolation
        }

        return extrapolation
    }

    private fun calculateSequences(measurements: List<Int>): List<List<Int>> {
        var current = mutableListOf<Int>().apply { addAll(measurements) }
        var next = mutableListOf<Int>()
        val sequences = mutableListOf<List<Int>>().apply { add(measurements) }

        while (current.any { it != 0 }) {
            for (i in 1 until current.size) {
                next.add(current[i] - current[i-1])
            }

            sequences.add(next.toList())

            val temp = current
            current = next
            next = temp

            next.clear()
        }

        return sequences
    }

}