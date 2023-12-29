class Day1 {
    private val nums = listOf(
        "one"   to "1",
        "two"   to "2",
        "three" to "3",
        "four"  to "4",
        "five"  to "5",
        "six"   to "6",
        "seven" to "7",
        "eight" to "8",
        "nine"  to "9")

    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_1.txt")!!
        val split = input.split("\n")

        val result = split
            .map { "${it.first { it.isDigit() }}${it.last { it.isDigit() }}" }
            .sumOf { it.toInt() }

        println(result)
    }

    private fun solve2() {
        val input = readFile("day_1.txt")!!
        val split = input.split("\n")

        val result = split
            .map { "${it.getFirstDigit()}${it.getLastDigit()}" }
            .sumOf { it.toInt() }

        println(result)
    }

    private fun String.getFirstDigit(): String {
        val firstDigitIndex = withIndex().firstOrNull { it.value.isDigit() }?.index ?: Int.MAX_VALUE
        val indices = mutableListOf<Int>()
        for (num in nums) {
            indices.add(indexOf(num.first))
        }
        if(indices.all { it < 0 }) return this[firstDigitIndex].toString()

        val minPositive = indices.withIndex().filter { it.value >= 0 }.minByOrNull { it.value }!!

        return if(firstDigitIndex < minPositive.value) {
            this[firstDigitIndex].toString()
        } else {
            nums[minPositive.index].second
        }
    }

    private fun String.getLastDigit(): String {
        val lastDigitIndex = withIndex().lastOrNull { it.value.isDigit() }?.index ?: Int.MIN_VALUE
        val indices = mutableListOf<Int>()
        for (num in nums) {
            indices.add(lastIndexOf(num.first))
        }
        if(indices.all { it < 0 }) return this[lastDigitIndex].toString()

        val maxPositive = indices.withIndex().filter { it.value >= 0 }.maxByOrNull { it.value }!!

        return if(lastDigitIndex > maxPositive.value) {
            this[lastDigitIndex].toString()
        } else {
            nums[maxPositive.index].second
        }
    }
}