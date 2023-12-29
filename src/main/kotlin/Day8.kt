
class Day8 {
    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_8.txt")!!
        val split = input.split("\n")

        val instructions = split[0].trim()
        val desertMap = parseMap(split.drop(2))

        var count = 0
        var currentNode = "AAA"
        while (true) {
            if(currentNode == "ZZZ") break

            currentNode = if(instructions[count % instructions.length] == 'L') {
                desertMap[currentNode]!!.left
            } else {
                desertMap[currentNode]!!.right
            }

            count++
        }

        println(count)
    }

    private fun solve2() {
        val input = readFile("day_8.txt")!!
        val split = input.split("\n")

        val instructions = split[0].trim()
        val desertMap = parseMap(split.drop(2))

        var count = 0L
        var currentNodes = desertMap.keys.filter { it.endsWith("A") }.toMutableList()
        val endNodes = desertMap.keys.filter { it.endsWith("Z") }.toMutableList()
        var destinationNodes = mutableListOf<String>()
        val cycleLengths = mutableListOf<Long>()

        while (true) {
            val zNodes = currentNodes.filter { it.endsWith("Z") }
            zNodes.forEach {
                cycleLengths.add(count)
                endNodes.remove(it)
            }

            if(endNodes.isEmpty()) break

            if(instructions[(count % instructions.length).toInt()] == 'L') {
                currentNodes.forEach {
                    destinationNodes.add(desertMap[it]!!.left)
                }
            } else {
                currentNodes.forEach {
                    destinationNodes.add(desertMap[it]!!.right)
                }
            }

            val temp = currentNodes
            currentNodes = destinationNodes
            destinationNodes = temp

            destinationNodes.clear()

            count++
        }

        println(cycleLengths.reduce{ acc, i -> lcm(acc, i) })
    }

    private fun parseMap(split: List<String>): Map<String, Node> {
        val desertMap = mutableMapOf<String, Node>()

        split.forEach {
            val current = it.substring(0, it.indexOf("=")).trim()
            val nodes = it.substring(it.indexOf("(") + 1, it.indexOf(")")).trim().split(", ")

            desertMap[current] = Node(nodes[0], nodes[1])
        }

        return desertMap
    }

    private fun gcd(a: Long, b: Long): Long {
        return if (a == 0L) b else gcd(b % a, a)
    }

    private fun lcm(a: Long, b: Long): Long {
        return a / gcd(a, b) * b
    }

    private data class Node(
        val left: String,
        val right: String,
    )
}