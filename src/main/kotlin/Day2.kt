class Day2 {

    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_2.txt")!!
        val split = input.split("\n")

        val result = split
            .map { parseGame(it) }
            .filter { it.isValid() }
            .sumOf { it.gameId }

        println(result)
    }

    private fun solve2() {
        val input = readFile("day_2.txt")!!
        val split = input.split("\n")

        val result = split
            .map { parseGame(it) }
            .map {
                it.counts.foldRight(GameCount()) { game, acc ->
                    acc.apply {
                        redCount = maxOf(redCount, game.redCount)
                        greenCount = maxOf(greenCount, game.greenCount)
                        blueCount = maxOf(blueCount, game.blueCount)
                    }
                }
            }
            .sumOf { it.redCount.toLong() * it.greenCount * it.blueCount }

        println(result)
    }

    private fun parseGame(gameString: String): Game {
        val id = gameString.substring(5, gameString.indexOf(":")).toInt()
        val games = gameString.substring(gameString.indexOf(":") + 2).split(";").map { it.trim() }

        return Game(id).apply {
            games.forEach {
                val balls = it.split(",").map { it.trim() }
                balls.forEach {
                    counts.add(GameCount().apply {
                        val ball = it.split(" ")
                        when (ball[1]) {
                            "red" -> redCount = maxOf(redCount, ball[0].toInt())
                            "green" -> greenCount = maxOf(greenCount, ball[0].toInt())
                            "blue" -> blueCount = maxOf(blueCount, ball[0].toInt())
                        }
                    })
                }
            }
        }
    }

    private fun Game.isValid(): Boolean {
        return counts.all {
            it.redCount <= 12 && it.greenCount <= 13 && it.blueCount <= 14
        }
    }

    private data class Game(
        val gameId: Int,
        val counts: MutableList<GameCount> = mutableListOf()
    )

    private data class GameCount(
        var redCount: Int = 0,
        var greenCount: Int = 0,
        var blueCount: Int = 0,
    )
}