import kotlin.math.abs
import kotlin.math.roundToLong

class Day24 {
    private val minCoordinate = 200000000000000.0
    private val maxCoordinate = 400000000000000.0

    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_24.txt")!!
        val split = input.split("\n")

        val hails = parseHails(split)
        var result = 0
        for (i in hails.indices) {
            for (j in (i + 1)..hails.lastIndex) {
                if (isIntersection2d(hails[i], hails[j])) result++
            }
        }

        println(result)
    }

    private fun solve2() {
        val input = readFile("day_24.txt")!!
        val split = input.split("\n")

        val hails = parseHails(split)
        val rockCoordinates = findRockCoordinates(hails)

        println(rockCoordinates.x + rockCoordinates.y + rockCoordinates.z)
    }

    private fun isIntersection2d(hail1: Hail, hail2: Hail): Boolean {
        val a = hail1.start
        val b = hail1.end
        val c = hail2.start
        val d = hail2.end

        val a1: Double = b.y.toDouble() - a.y
        val b1: Double = a.x.toDouble() - b.x
        val c1: Double = a1 * a.x + b1 * a.y

        val a2: Double = d.y.toDouble() - c.y
        val b2: Double = c.x.toDouble() - d.x
        val c2: Double = a2 * c.x + b2 * c.y

        val determinant = a1 * b2 - a2 * b1

        return if (determinant == 0.0) {
            false
        } else {
            val x = (b2 * c1 - b1 * c2) / determinant
            val y = (a1 * c2 - a2 * c1) / determinant

            (x in minCoordinate..maxCoordinate) &&
                    (y in minCoordinate..maxCoordinate) &&
                    hail1.isPointOnTrajectory2d(x, y) &&
                    hail2.isPointOnTrajectory2d(x, y)
        }
    }

    private fun findRockCoordinates(hails: List<Hail>): Point {
        val xyCoordinates = GaussianElimination().solve(
            arrayOf(
                doubleArrayOf((hails[1].yVelocity - hails[0].yVelocity).toDouble(), (hails[0].xVelocity - hails[1].xVelocity).toDouble(), (hails[0].start.y - hails[1].start.y).toDouble(), (hails[1].start.x - hails[0].start.x).toDouble()),
                doubleArrayOf((hails[2].yVelocity - hails[1].yVelocity).toDouble(), (hails[1].xVelocity - hails[2].xVelocity).toDouble(), (hails[1].start.y - hails[2].start.y).toDouble(), (hails[2].start.x - hails[1].start.x).toDouble()),
                doubleArrayOf((hails[3].yVelocity - hails[2].yVelocity).toDouble(), (hails[2].xVelocity - hails[3].xVelocity).toDouble(), (hails[2].start.y - hails[3].start.y).toDouble(), (hails[3].start.x - hails[2].start.x).toDouble()),
                doubleArrayOf((hails[4].yVelocity - hails[3].yVelocity).toDouble(), (hails[3].xVelocity - hails[4].xVelocity).toDouble(), (hails[3].start.y - hails[4].start.y).toDouble(), (hails[4].start.x - hails[3].start.x).toDouble()),
            ),
            doubleArrayOf(
                (hails[1].start.x * hails[1].yVelocity - hails[1].start.y * hails[1].xVelocity - hails[0].start.x * hails[0].yVelocity + hails[0].start.y * hails[0].xVelocity).toDouble(),
                (hails[2].start.x * hails[2].yVelocity - hails[2].start.y * hails[2].xVelocity - hails[1].start.x * hails[1].yVelocity + hails[1].start.y * hails[1].xVelocity).toDouble(),
                (hails[3].start.x * hails[3].yVelocity - hails[3].start.y * hails[3].xVelocity - hails[2].start.x * hails[2].yVelocity + hails[2].start.y * hails[2].xVelocity).toDouble(),
                (hails[4].start.x * hails[4].yVelocity - hails[4].start.y * hails[4].xVelocity - hails[3].start.x * hails[3].yVelocity + hails[3].start.y * hails[3].xVelocity).toDouble(),
            )
        )

        val xzCoordinates = GaussianElimination().solve(
            arrayOf(
                doubleArrayOf((hails[1].zVelocity - hails[0].zVelocity).toDouble(), (hails[0].xVelocity - hails[1].xVelocity).toDouble(), (hails[0].start.z - hails[1].start.z).toDouble(), (hails[1].start.x - hails[0].start.x).toDouble()),
                doubleArrayOf((hails[2].zVelocity - hails[1].zVelocity).toDouble(), (hails[1].xVelocity - hails[2].xVelocity).toDouble(), (hails[1].start.z - hails[2].start.z).toDouble(), (hails[2].start.x - hails[1].start.x).toDouble()),
                doubleArrayOf((hails[3].zVelocity - hails[2].zVelocity).toDouble(), (hails[2].xVelocity - hails[3].xVelocity).toDouble(), (hails[2].start.z - hails[3].start.z).toDouble(), (hails[3].start.x - hails[2].start.x).toDouble()),
                doubleArrayOf((hails[4].zVelocity - hails[3].zVelocity).toDouble(), (hails[3].xVelocity - hails[4].xVelocity).toDouble(), (hails[3].start.z - hails[4].start.z).toDouble(), (hails[4].start.x - hails[3].start.x).toDouble()),
            ),
            doubleArrayOf(
                (hails[1].start.x * hails[1].zVelocity - hails[1].start.z * hails[1].xVelocity - hails[0].start.x * hails[0].zVelocity + hails[0].start.z * hails[0].xVelocity).toDouble(),
                (hails[2].start.x * hails[2].zVelocity - hails[2].start.z * hails[2].xVelocity - hails[1].start.x * hails[1].zVelocity + hails[1].start.z * hails[1].xVelocity).toDouble(),
                (hails[3].start.x * hails[3].zVelocity - hails[3].start.z * hails[3].xVelocity - hails[2].start.x * hails[2].zVelocity + hails[2].start.z * hails[2].xVelocity).toDouble(),
                (hails[4].start.x * hails[4].zVelocity - hails[4].start.z * hails[4].xVelocity - hails[3].start.x * hails[3].zVelocity + hails[3].start.z * hails[3].xVelocity).toDouble(),
            )
        )

        println((xyCoordinates[0] + xyCoordinates[1] + xzCoordinates[1]).toLong())

        return Point(xyCoordinates[0].roundToLong(), xyCoordinates[1].roundToLong(), xzCoordinates[1].roundToLong())
    }

    private fun parseHails(input: List<String>): List<Hail> {
        return input.map {
            val numbers = it.split(Regex("[,@]")).map { it.trim() }.map { it.toLong() }

            Hail(
                Point(numbers[0], numbers[1], numbers[2]),
                numbers[3],
                numbers[4],
                numbers[5]
            )
        }
    }

    private data class Point(
        val x: Long,
        val y: Long,
        val z: Long
    )

    private data class Hail(
        val start: Point,
        val xVelocity: Long,
        val yVelocity: Long,
        val zVelocity: Long,
    ) {
        val end = Point(start.x + xVelocity, start.y + yVelocity, start.z + zVelocity)

        fun isPointOnTrajectory2d(x: Double, y: Double): Boolean {
            return ((x - start.x) / xVelocity > 0) && ((y - start.y) / yVelocity > 0)
        }
    }

    class GaussianElimination {
        private val EPSILON = 1e-10

        // Gaussian elimination with partial pivoting
        fun solve(A: Array<DoubleArray>, b: DoubleArray): DoubleArray {
            val n = b.size
            for (p in 0 until n) {

                // find pivot row and swap
                var max = p
                for (i in p + 1 until n) {
                    if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
                        max = i
                    }
                }
                val temp = A[p]
                A[p] = A[max]
                A[max] = temp
                val t = b[p]
                b[p] = b[max]
                b[max] = t

                // singular or nearly singular
                if (abs(A[p][p]) <= EPSILON) {
                    throw ArithmeticException("Matrix is singular or nearly singular")
                }

                // pivot within A and b
                for (i in p + 1 until n) {
                    val alpha = A[i][p] / A[p][p]
                    b[i] -= alpha * b[p]
                    for (j in p until n) {
                        A[i][j] -= alpha * A[p][j]
                    }
                }
            }

            // back substitution
            val x = DoubleArray(n)
            for (i in n - 1 downTo 0) {
                var sum = 0.0
                for (j in i + 1 until n) {
                    sum += A[i][j] * x[j]
                }
                x[i] = (b[i] - sum) / A[i][i]
            }
            return x
        }
    }
}