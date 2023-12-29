import java.awt.Component
import java.awt.Graphics
import javax.swing.JFrame
import kotlin.math.abs

class Day18 {
    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_18.txt")!!
        val split = input.split("\n")

        val instructions = parseInstructions(split)
        val points = findPoints(instructions)
        val polygon = createPolygon(points)
        val result = polygon.calculateArea()

        println(result)
    }

    private fun solve2() {
        val input = readFile("day_18.txt")!!
        val split = input.split("\n")

        val instructions = parseInstructions(split)
        val fixedInstructions = fixInstructions(instructions)

        val points = findPoints(fixedInstructions)
        val polygon = createPolygon(points)
        val result = polygon.calculateArea()

        println(result)
    }

    private fun createPolygon(points: List<Point>): Polygon {
        return Polygon().apply {
            points.windowed(2, 1).forEach {
                edges.add(Edge(it[0], it[1], if (it[0].x == it[1].x) EdgeDirection.VERTICAL else EdgeDirection.HORIZONTAL))
            }
        }
    }

    private fun findPoints(instructions: List<Instruction>): List<Point> {
        val startPoint = Point(0, 0)
        val scale = 1L

        val points = mutableListOf<Point>().apply { add(startPoint) }

        instructions.forEach {
            when (it.direction) {
                Direction.UP -> {
                    points.add(Point(points.last().x, points.last().y - it.length * scale))
                }
                Direction.DOWN -> {
                    points.add(Point(points.last().x, points.last().y + it.length * scale))
                }
                Direction.LEFT -> {
                    points.add(Point(points.last().x - it.length * scale, points.last().y))
                }
                Direction.RIGHT -> {
                    points.add(Point(points.last().x + it.length * scale, points.last().y))
                }
            }
        }

        return points
    }

    private fun parseInstructions(input: List<String>): List<Instruction> {
        return input.map {
            val direction = when (it[0]) {
                'U' -> Direction.UP
                'R' -> Direction.RIGHT
                'D' -> Direction.DOWN
                'L' -> Direction.LEFT
                else -> throw IllegalArgumentException("Not supported direction: ${it[0]}")
            }

            val length = it.substring(2, it.indexOf('(')).trim().toLong()
            val color = it.substring(it.indexOf('#') + 1, it.indexOf(')'))

            Instruction(direction, length, color)
        }
    }

    private fun fixInstructions(instructions: List<Instruction>): List<Instruction> {
        return instructions.map {
            val direction = when (it.color.last()) {
                '0' -> Direction.RIGHT
                '1' -> Direction.DOWN
                '2' -> Direction.LEFT
                '3' -> Direction.UP
                else -> throw IllegalArgumentException("Not supported direction: ${it.color.last()}")
            }
            val length = it.color.substring(0, it.color.length - 1).toLong(16)

            Instruction(direction, length, it.color)
        }
    }

    private fun drawTrench(points: List<Point>, polygon: Polygon) {
        val frame = JFrame()
        frame.contentPane.add(TrenchPaintComponent(points, polygon))
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setLocationRelativeTo(null)
        frame.setSize(1600, 1600)
        frame.isVisible = true
    }

    private data class Instruction(
        val direction: Direction,
        val length: Long,
        val color: String,
    )

    private data class Point(
        val x: Long,
        val y: Long,
    ) {
        override fun toString(): String {
            return "($x, $y)"
        }
    }

    private enum class Direction {
        UP, RIGHT, DOWN, LEFT
    }

    private enum class EdgeDirection {
        HORIZONTAL, VERTICAL
    }

    private data class Edge(
        val start: Point,
        val end: Point,
        val direction: EdgeDirection,
    )

    private data class Polygon(
        val edges: MutableList<Edge> = mutableListOf()
    ) {
        fun calculateArea(): Long {
            var area = 0L
            edges.forEach {
                area += ((it.start.x) + (it.end.x)) * (it.start.y - it.end.y)
            }
            area = abs(area / 2)

            val boundaryPoints = edges.sumOf { abs(it.start.x - it.end.x) + abs(it.start.y - it.end.y) }
            val internalPoints = area - boundaryPoints / 2 + 1

            return boundaryPoints + internalPoints
        }
    }

    private class TrenchPaintComponent(
        val points: List<Point>,
        val polygon: Polygon,
    ) : Component() {
        override fun paint(g: Graphics) {
            val xOffset = 400
            val yOffset = 500
            val pointScale = 0.0004
            val ovalScale = 2
            polygon.edges.forEach {
                g.drawLine(
                    (xOffset + it.start.x * pointScale).toInt(),
                    (yOffset + it.start.y * pointScale).toInt(),
                    (xOffset + it.end.x * pointScale).toInt(),
                    (yOffset + it.end.y * pointScale).toInt()
                )
            }
            points.forEach {
                g.fillOval(it.x.toInt() - ovalScale, it.y.toInt() - ovalScale, 2 * ovalScale, 2 * ovalScale)
            }
        }
    }
}