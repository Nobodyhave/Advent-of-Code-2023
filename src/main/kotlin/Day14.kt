class Day14 {
    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_14.txt")!!
        val split = input.split("\n")

        val platform = split.map { it.toCharArray() }
        tiltNorth(platform)

        val result = calculateLoad(platform)
        println(result)
    }

    private fun solve2() {
        val input = readFile("day_14.txt")!!
        val split = input.split("\n")

        var platform = split.map { it.toCharArray() }

        val set = mutableSetOf<String>().apply { add(platformToString(platform)) }
        var cycleStart: String? = null
        var beforeCycle = 0
        while (beforeCycle < 10000) {
            singleCycle(platform)
            val pts = platformToString(platform)
            if (!set.add(platformToString(platform))) {
                cycleStart = pts
                break
            }
            beforeCycle++
        }

        var cycleLength = 0
        while (true) {
            cycleLength++
            singleCycle(platform)
            val pts = platformToString(platform)
            if (cycleStart == pts) {
                break
            }
        }

        val stepsInCycle = if (cycleLength != 0) (1000000000 - beforeCycle) % cycleLength - 1 else 0
        platform = stringToPlatform(cycleStart!!, platform.size, platform[0].size)

        for (i in 0 until stepsInCycle) {
            singleCycle(platform)
        }
        val load = calculateLoad(platform)

        println(load)
    }

    private fun tiltNorth(platform: List<CharArray>) {
        for (j in platform[0].indices) {
            var i = 1
            while (i < platform.size) {
                if (platform[i][j] == 'O') {
                    while (i >= 1 && platform[i - 1][j] == '.') {
                        val temp = platform[i - 1][j]
                        platform[i - 1][j] = platform[i][j]
                        platform[i][j] = temp
                        i--
                    }
                }
                i++
            }
        }
    }

    private fun tiltSouth(platform: List<CharArray>) {
        for (j in platform[0].indices) {
            var i = platform.lastIndex - 1
            while (i >= 0) {
                if (platform[i][j] == 'O') {
                    while (i <= platform.lastIndex - 1 && platform[i + 1][j] == '.') {
                        val temp = platform[i + 1][j]
                        platform[i + 1][j] = platform[i][j]
                        platform[i][j] = temp
                        i++
                    }
                }
                i--
            }
        }
    }

    private fun tiltWest(platform: List<CharArray>) {
        for (i in platform.indices) {
            var j = 1
            while (j < platform[0].size) {
                if (platform[i][j] == 'O') {
                    while (j >= 1 && platform[i][j - 1] == '.') {
                        val temp = platform[i][j - 1]
                        platform[i][j - 1] = platform[i][j]
                        platform[i][j] = temp
                        j--
                    }
                }
                j++
            }
        }
    }

    private fun tiltEast(platform: List<CharArray>) {
        for (i in platform.indices) {
            var j = platform[0].lastIndex - 1
            while (j >= 0) {
                if (platform[i][j] == 'O') {
                    while (j <= platform[0].lastIndex - 1 && platform[i][j + 1] == '.') {
                        val temp = platform[i][j + 1]
                        platform[i][j + 1] = platform[i][j]
                        platform[i][j] = temp
                        j++
                    }
                }
                j--
            }
        }
    }

    private fun singleCycle(platform: List<CharArray>) {
        tiltNorth(platform)
        tiltWest(platform)
        tiltSouth(platform)
        tiltEast(platform)
    }

    private fun platformToString(platform: List<CharArray>): String {
        return buildString {
            platform.forEach {
                append(it)
            }
        }
    }

    private fun stringToPlatform(s: String, rows: Int, cols: Int): List<CharArray> {
        val platform = mutableListOf<String>()
        for (i in 0 until rows) {
            platform.add(s.substring(cols * i, cols * (i + 1)))
        }

        return platform.map { it.toCharArray() }
    }

    private fun calculateLoad(platform: List<CharArray>): Int {
        var load = 0
        for (i in platform.indices) {
            for (j in platform[0].indices) {
                if (platform[i][j] == 'O') {
                    load += platform.size - i
                }
            }
        }

        return load
    }
}