class Day6 {

    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_6.txt")!!
        val split = input.split("\n")

        val games = split[0].substring(5).trim().split(Regex(" +"))
            .zip(split[1].substring(9).trim().split(Regex(" +")))
            .map { Game(it.first.toLong(), it.second.toLong()) }

        var result = 1L
        games.forEach { game ->
            val count = simulateGame(game)

            result *= count
        }

        println(result)
    }

    private fun solve2() {
        val input = readFile("day_6.txt")!!
        val split = input.split("\n")

        val time = split[0].substring(5).trim().replace(Regex(" +"), "").toLong()
        val distance = split[1].substring(9).trim().replace(Regex(" +"), "").toLong()
        val count = simulateGame(Game(time, distance))

        println(count)
    }

    private fun simulateGame(game: Game): Long {
        var count = 0L
        for (hold in 0..game.time) {
            val travel = (game.time - hold) * hold
            if(travel > game.distance) count++
        }

        return count
    }

    private data class Game(
        val time: Long,
        val distance: Long,
    )
}