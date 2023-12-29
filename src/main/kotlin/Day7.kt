import Day7.HandType.Companion.parseHandTypePart1
import Day7.HandType.Companion.parseHandTypePart2

class Day7 {
    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_7.txt")!!
        val split = input.split("\n")

        val hands = parseHands(split, ::parseHandTypePart1).toMutableList()
        radixSort(hands, false)

        var result = 0L
        hands.forEachIndexed { index, hand ->
            result += hand.bid * (index + 1)
        }

        println(result)
    }

    private fun solve2() {
        val input = readFile("day_7.txt")!!
        val split = input.split("\n")

        val hands = parseHands(split, ::parseHandTypePart2).toMutableList()
        radixSort(hands, true)

        var result = 0L
        hands.forEachIndexed { index, hand ->
            result += hand.bid * (index + 1)
        }

        println(result)
    }

    private fun parseHands(split: List<String>, parse: (String) -> HandType): List<Hand> {
        return split
            .map { it.split(" ") }
            .map { (hand, bid) ->
                val handType = parse(hand)
                Hand("${handType.power}$hand", bid.toLong())
            }
    }

    private data class Hand(
        val hand: String,
        val bid: Long,
    )

    private fun radixSort(hands: MutableList<Hand>, isJoker: Boolean) {
        fun getCharIndex(c: Char): Int {
            return when(c) {
                in '1'..'9' -> c.digitToInt()
                'T' -> 10
                'J' -> if(isJoker) 0 else { 11 }
                'Q' -> 12
                'K' -> 13
                'A' -> 14
                else -> throw IllegalArgumentException("Unexpected card: $c")
            }
        }

        val charsPerString = 6
        val size: Int = hands.size
        val alphabetSize = 256

        val aux = arrayOfNulls<Hand>(size)

        for (d in charsPerString - 1 downTo 0) {
            // sort by key-indexed counting on dth character

            // compute frequency counts
            val count = IntArray(alphabetSize + 1)
            for (i in 0 until size) count[getCharIndex(hands[i].hand[d]) + 1]++

            // compute cumulates
            for (r in 0 until alphabetSize) count[r + 1] += count[r]

            // move data
            for (i in 0 until size) aux[count[getCharIndex(hands[i].hand[d])]++] = hands[i]

            // copy back
            for (i in 0 until size) hands[i] = aux[i]!!
        }
    }

    private sealed class HandType(val power: Int) {
        object HighCard : HandType(1)
        object OnePair : HandType(2)
        object TwoPair : HandType(3)
        object ThreeOfAKind : HandType(4)
        object FullHouse : HandType(5)
        object FourOfAKind : HandType(6)
        object FiveOfAKind : HandType(7)

        companion object {
            private const val CARDS = "23456789TJQKA"

            fun parseHandTypePart1(s: String): HandType {
                val counts = s.groupingBy { it }.eachCount().values.toList()

                return when (counts.size) {
                    1 -> {
                        FiveOfAKind
                    }
                    2 -> {
                        if (counts.any { it == 4 }) {
                            FourOfAKind
                        } else {
                            FullHouse
                        }
                    }
                    3 -> {
                        if (counts.any { it == 3 }) {
                            ThreeOfAKind
                        } else {
                            TwoPair
                        }
                    }
                    4 -> {
                        OnePair
                    }
                    else -> {
                        HighCard
                    }
                }
            }

            fun parseHandTypePart2(s: String): HandType {
                val candidates = getHandPermutations(s)

                return candidates.map { parseHandTypePart1(it) }.maxByOrNull { it.power }!!
            }

            private fun getHandPermutations(s: String): List<String> {
                val result = mutableSetOf<String>()

                dfs(result, StringBuilder(s), 0)

                return result.toList()
            }

            private fun dfs(result: MutableSet<String>, sb: StringBuilder,  index: Int) {
                if(index == 5) {
                    result.add(sb.toString())
                    return
                }

                if(sb[index] != 'J') {
                    dfs(result, sb, index + 1)
                } else {
                    for (c in CARDS) {
                        if(c == 'J') continue

                        val oldChar = sb[index]
                        sb[index] = c
                        dfs(result, sb, index + 1)
                        sb[index] = oldChar
                    }
                }
            }
        }
    }
}