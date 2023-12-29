import java.util.LinkedList

class Day15 {
    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_15.txt")!!
        val split = input.split("\n")

        val commands = input.replace('\n', ' ').split(",")

        var result = 0
        commands.forEach {
            result += hash(it)
        }

        println(result)
    }

    private fun solve2() {
        val input = readFile("day_15.txt")!!
        val commands = input.replace('\n', ' ').split(",")

        val boxes = Array(256) { i-> Box(i) }
        commands.forEach {
            val command = Command(
                it.filter { it.isLetter() },
                if (it.contains("=")) Operation.ADD else Operation.REMOVE,
                if (it.contains("=")) it.filter { it.isDigit() }.toInt() else -1
            )

            val hash = hash(command.label)
            if(command.operation == Operation.ADD) {
                boxes[hash].addLens(Lens(command.label, command.focusLength))
            } else {
                boxes[hash].removeLens(Lens(command.label, command.focusLength))
            }
        }

        var result = 0
        boxes.forEach {
            result += it.focusingPower()
        }

        println(result)
    }

    private fun hash(s: String): Int {
        var hash = 0

        s.forEach {
            hash += it.code
            hash *= 17
            hash %= 256
        }

        return hash
    }

    private class Lens(
        val label: String,
        var focalLength: Int,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Lens

            if (label != other.label) return false

            return true
        }

        override fun hashCode(): Int {
            return label.hashCode()
        }
    }

    private class Box(private val number: Int) {
        private val lenses = LinkedList<Lens>()

        fun addLens(lens: Lens) {
            val boxLens = lenses.firstOrNull { it == lens}
            if (boxLens != null) {
                boxLens.focalLength = lens.focalLength
            } else {
                lenses.add(lens)
            }
        }

        fun removeLens(lens: Lens) {
            lenses.remove(lens)
        }

        fun focusingPower(): Int {
            var result = 0
            var count = 1
            for (lens in lenses) {
                result += (1 + number) * (count++) * lens.focalLength
            }

            return result
        }
    }

    private data class Command(
        val label: String,
        val operation: Operation,
        val focusLength: Int = -1
    )

    private enum class Operation {
        ADD, REMOVE
    }
}