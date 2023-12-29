import java.util.LinkedList
import kotlin.math.pow

class Day4 {

    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_4.txt")!!
        val split = input.split("\n")

        val cards = split.map { parseCard(it) }

        var result = 0
        for (card in cards) {
            result += if(card.hits != 0) 2.0.pow(card.hits - 1).toInt() else 0
        }

        println(result)
    }

    private fun solve2() {
        val input = readFile("day_4.txt")!!
        val split = input.split("\n")

        val cards = split.map { parseCard(it) }
        val queue = LinkedList(cards)

        var count = 0
        while (queue.isNotEmpty()) {
            val card = queue.pollFirst()
            for(i in 0 until card.hits) {
                queue.addLast(cards[card.number - 1 + i + 1])
            }

            count++
        }

        println(count)
    }

    private fun parseCard(s: String): Card {
        return Card(
            s.substring(4, s.indexOf(":")).trim().toInt(),
            s.substring(s.indexOf(":") + 1, s.indexOf("|")).trim().split(Regex(" +")).map { it.toInt() }.toSet(),
            s.substring(s.indexOf("|") + 1).trim().split(Regex(" +")).map { it.toInt() }.toSet(),
        )
    }

    private data class Card(
        val number: Int,
        val winning: Set<Int>,
        val candidates: Set<Int>
    ) {
        val hits: Int

        init {
            var count = 0
            for(candidate in candidates) {
                if(winning.contains(candidate)) {
                    count++
                }
            }

            hits = count
        }
    }
}