import kotlin.math.abs
import kotlin.random.Random
import kotlin.random.asJavaRandom

class Day22 {
    private val gridSize = 15

    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_22.txt")!!
        val split = input.split("\n")

//        while (true) {
//            val split = generateInput()
//
//            split.forEach { println(it) }
//            println("---------------------")
//
//            val bricks = parseBricks(split)
//            val fallenBricks = bricks.toMutableList()
//            simulateFall(fallenBricks)
//            val grid = simulateFallInGrid(parseBricks(split).associateBy { it.index })
//            check(fallenBricks, grid)
//        }

        val candidates = getBricksToDisintegrate(split)

        println(candidates.size)
    }

    private fun solve2() {
        val input = readFile("day_22.txt")!!
        val split = input.split("\n")

        var result = 0
        parseBricks(split).forEach { candidate ->
            val bricks = parseBricks(split).toMutableList()
            simulateFall(bricks.sortedBy { it.start.z })

            bricks.remove(candidate)

            result += simulateFall(bricks.sortedBy { it.start.z })
        }

        println(result)
    }

    private fun getBricksToDisintegrate(input: List<String>): List<Brick> {
        val bricks = parseBricks(input)
        simulateFall(bricks.sortedBy { it.start.z })

        val candidates = mutableSetOf<Brick>().apply { addAll(bricks) }
        bricks.forEach { brick ->
            val onTopOf = bricks.filter { brick.isIntersection(it) }.filter { brick.isOnTopOf(it) }
            if (onTopOf.size == 1) {
                candidates.remove(onTopOf.first())
            }
        }

        return candidates.toList()
    }

    private fun simulateFall(bricks: List<Brick>): Int {
        var count = 0
        bricks.forEach {
            if(it.fall(bricks)) count++
        }

        return count
    }

    private fun parseBricks(input: List<String>): List<Brick> {
        var count = 0

        return input.map {
            val coordinatesSplit = it.split('~')
            val startSplit = coordinatesSplit[0].split(',').map { it.toInt() }
            val endSplit = coordinatesSplit[1].split(',').map { it.toInt() }

            count++

            // Make first point lower
            if (startSplit[2] < endSplit[2]) {
                Brick(
                    count,
                    Point(startSplit[0], startSplit[1], startSplit[2]),
                    Point(endSplit[0], endSplit[1], endSplit[2]),
                )
            } else {
                Brick(
                    count,
                    Point(endSplit[0], endSplit[1], endSplit[2]),
                    Point(startSplit[0], startSplit[1], startSplit[2]),
                )
            }
        }
    }

    private data class Brick(
        val index: Int,
        var start: Point,
        var end: Point,
    ) {
        private val xDiff = abs(start.x - end.x)
        private val yDiff = abs(start.y - end.y)
        private val zDiff = abs(start.z - end.z)

        val isX = xDiff != 0
        val isY = yDiff != 0
        val isZ = zDiff != 0 || (xDiff == 0 && yDiff == 0 && zDiff == 0)

        var xRange = IntRange(minOf(start.x, end.x), maxOf(start.x, end.x))
        var yRange = IntRange(minOf(start.y, end.y), maxOf(start.y, end.y))
        var zRange = IntRange(minOf(start.z, end.z), maxOf(start.z, end.z))

        fun fall(list: List<Brick>): Boolean {
            val topBrick = list.filter { start.z > it.end.z && isIntersection(it) }.maxByOrNull { it.end.z }
                ?: Brick(0, Point(0, 0, 0), Point(0, 0, 0))

            if (isOnTopOf(topBrick)) return false

            if (isZ) {
                start = start.copy(z = topBrick.end.z + 1)
                end = end.copy(z = start.z + zDiff)
            } else {
                start = start.copy(z = topBrick.end.z + 1)
                end = end.copy(z = topBrick.end.z + 1)
            }
            updateRanges()

            return true
        }

        fun isIntersection(other: Brick): Boolean {
            val intersects = if (isX && other.isX) {
                (xRange.contains(other.start.x) ||
                        xRange.contains(other.end.x) ||
                        other.xRange.contains(start.x) ||
                        other.xRange.contains(end.x)) && start.y == other.start.y
            } else if (isX && other.isY) {
                xRange.contains(other.start.x) && other.yRange.contains(start.y)
            } else if (isX && other.isZ) {
                xRange.contains(other.start.x) && start.y == other.start.y
            } else if (isY && other.isX) {
                other.xRange.contains(start.x) && yRange.contains(other.start.y)
            } else if (isY && other.isY) {
                (yRange.contains(other.start.y) ||
                        yRange.contains(other.end.y) ||
                        other.yRange.contains(start.y) ||
                        other.yRange.contains(end.y)) && start.x == other.start.x
            } else if (isY && other.isZ) {
                yRange.contains(other.start.y) && start.x == other.start.x
            } else if (isZ && other.isX) {
                other.xRange.contains(start.x) && other.start.y == start.y
            } else if (isZ && other.isY) {
                other.yRange.contains(start.y) && other.start.x == start.x
            } else if (isZ && other.isZ) {
                start.x == other.start.x && start.y == other.start.y
            } else {
                throw IllegalStateException("Missed orientation case")
            }

            return intersects
        }

        fun isOnTopOf(other: Brick): Boolean {
            return start.z == other.end.z + 1
        }

        fun isBelowOf(other: Brick): Boolean {
            return end.z == other.start.z - 1
        }

        private fun updateRanges() {
            xRange = IntRange(minOf(start.x, end.x), maxOf(start.x, end.x))
            yRange = IntRange(minOf(start.y, end.y), maxOf(start.y, end.y))
            zRange = IntRange(minOf(start.z, end.z), maxOf(start.z, end.z))
        }

        override fun toString(): String {
            return "Brick(${index}: start=$start, end=$end)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Brick

            if (index != other.index) return false

            return true
        }

        override fun hashCode(): Int {
            return index
        }
    }

    private data class Point(
        val x: Int,
        val y: Int,
        val z: Int,
    ) {
        override fun toString(): String {
            return "($x, $y, $z)"
        }
    }

    private fun simulateFallInGrid(bricks: Map<Int, Brick>): Array<Array<IntArray>> {
        val grid = Array(gridSize) { Array(gridSize) { IntArray(gridSize) { 0 } } }

        bricks.values.forEach {
            if (it.isX) {
                for (x in it.xRange) {
                    grid[it.start.z][it.start.y][x] = it.index
                }
            } else if (it.isY) {
                for (y in it.yRange) {
                    grid[it.start.z][y][it.start.x] = it.index
                }
            } else {
                for (z in it.zRange) {
                    grid[z][it.start.y][it.start.x] = it.index
                }
            }
        }

        var hasChanged: Boolean
        do {
            hasChanged = false

            for (z in 2 until gridSize) {
                for (x in 0 until gridSize) {
                    for (y in 0 until gridSize) {
                        val index = grid[z][y][x]
                        if (index == 0) continue

                        val brick = bricks[index]!!
                        if (brick.isX) {
                            var canFall = true
                            for (brickX in brick.xRange) {
                                if (grid[z - 1][y][brickX] != 0) {
                                    canFall = false
                                    break
                                }
                            }
                            if (canFall) {
                                for (brickX in brick.xRange) {
                                    grid[z - 1][y][brickX] = grid[z][y][brickX]
                                    grid[z][y][brickX] = 0
                                }

                                hasChanged = true
                            }
                        } else if (brick.isY) {
                            var canFall = true
                            for (brickY in brick.yRange) {
                                if (grid[z - 1][brickY][x] != 0) {
                                    canFall = false
                                    break
                                }
                            }
                            if (canFall) {
                                for (brickY in brick.yRange) {
                                    grid[z - 1][brickY][x] = grid[z][brickY][x]
                                    grid[z][brickY][x] = 0
                                }

                                hasChanged = true
                            }
                        } else {
                            if (grid[z - 1][y][x] == 0) {
                                grid[z - 1][y][x] = grid[z][y][x]
                                grid[z][y][x] = 0

                                hasChanged = true
                            }
                        }
                    }
                }
            }

        } while (hasChanged)

        return grid
    }

    private fun check(bricks: List<Brick>, grid: Array<Array<IntArray>>) {
        for (z in 1 until gridSize) {
            for (x in 0 until gridSize) {
                for (y in 0 until gridSize) {
                    val index = grid[z][y][x]
                    if (index != 0) {
                        val brick = bricks.first { it.index == index }
                        if (!brick.xRange.contains(x) || !brick.yRange.contains(y) || !brick.zRange.contains(z))
                            throw IllegalStateException("Cell[$z][$y][$x] has index but is not in brick")
                    } else {
                        bricks.forEach { brick ->
                            if (brick.xRange.contains(x) && brick.yRange.contains(y) && brick.zRange.contains(z))
                                throw IllegalStateException("Cell[$z][$y][$x] has no index but it is in brick $brick")
                        }
                    }
                }
            }
        }
    }

    private fun generateInput(): List<String> {
        val result = mutableListOf<String>()
        val random = Random.asJavaRandom()

        var z = 1
        while (z < gridSize) {
            val direction = random.nextInt(3)
            if (direction == 0) {
                val length = random.nextInt(gridSize) + 1
                val start = random.nextInt(gridSize + 1 - length)
                val y = random.nextInt(gridSize)
                result.add("$start,$y,$z~${start + length - 1},$y,$z")
                z++
            } else if (direction == 1) {
                val length = random.nextInt(gridSize) + 1
                val start = random.nextInt(gridSize + 1 - length)
                val x = random.nextInt(gridSize)
                result.add("$x,$start,$z~$x,${start + length - 1},$z")
                z++
            } else {
                val length = random.nextInt(minOf(gridSize, gridSize - z)) + 1
                val start = z
                val x = random.nextInt(gridSize)
                val y = random.nextInt(gridSize)
                result.add("$x,$y,$start~$x,$y,${start + length - 1}")
                z += length
            }
        }

        return result
    }
}