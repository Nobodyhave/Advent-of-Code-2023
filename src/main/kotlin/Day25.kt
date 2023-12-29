import java.util.Random

class Day25 {
    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_25.txt")!!
        val split = input.split("\n")

        val graph = parseGraph(split)

        while (true) {
            val ka = KargersAlgorithm()
            val minCut = ka.minCut(graph)
            if (minCut.first == 3) {
                println(minCut.second)
                break
            }
        }
    }

    private fun parseGraph(input: List<String>): Graph {
        var count = 0
        val vertices = mutableMapOf<String, Int>()
        val edges = mutableSetOf<Edge>()

        input.forEach {
            val v = it.substring(0, it.indexOf(":"))
            if (!vertices.containsKey(v)) vertices[v] = count++

            val u = it.substring(it.indexOf(":") + 2).split(" ").map { it.trim() }
            u.forEach {
                if (!vertices.containsKey(it)) vertices[it] = count++
                edges.add(Edge(vertices[v]!!, vertices[it]!!))
            }
        }

        val graph = Graph(vertices.size, edges.size)
        edges.forEachIndexed { index, edge ->
            graph.edge[index] = edge
        }

        return graph
    }

    private fun solve2() {

    }

    private class KargersAlgorithm {
        fun minCut(graph: Graph): Pair<Int, Int> {
            // Get data of given graph
            val vCount = graph.V
            val eCount = graph.E
            val edges = graph.edge

            // Allocate memory for creating V subsets.
            val subsets = Array<Subset?>(vCount) { v -> Subset(v, 1) }

            // Initially there are V vertices in
            // contracted graph
            var vertices = vCount
            val random = Random()

            // Keep contracting vertices until there are
            // 2 vertices.
            while (vertices > 2) {
                // Pick a random edge
                val i = random.nextInt(eCount)

                // Find vertices (or sets) of two corners
                // of current edge
                val subset1 = find(subsets, edges[i]!!.src)
                val subset2 = find(subsets, edges[i]!!.dest)

                // If two corners belong to same subset,
                // then no point considering this edge
                if (subset1 == subset2) {
                    continue
                } else {
//                    println("Contracting edge " + edges[i]!!.src + "-" + edges[i]!!.dest)
                    vertices--
                    union(subsets, subset1, subset2)
                }
            }
//            for (edge in edges) {
//                // Find vertices (or sets) of two corners
//                // of current edge
//                val subset1 = find(subsets, edge!!.src)
//                val subset2 = find(subsets, edge!!.dest)
//
//                // If two corners belong to same subset,
//                // then no point considering this edge
//                if (subset1 != subset2) {
//                    println("Contracting edge " + edge!!.src + "-" + edge!!.dest)
//                    vertices--
//                    union(subsets, subset1, subset2)
//                }
//
//                if (vertices == 2) break
//            }

            // Now we have two vertices (or subsets) left in
            // the contracted graph, so count the edges between
            // two components and return the count.
            var cutEdges = 0
            var rankProduct = 0
            for (i in 0 until eCount) {
                val subset1 = find(subsets, edges[i]!!.src)
                val subset2 = find(subsets, edges[i]!!.dest)
                if (subset1 != subset2) {
                    cutEdges++
                    rankProduct = subsets[subset1]!!.rank * subsets[subset2]!!.rank
                }
            }
            return cutEdges to (rankProduct)
        }

        // A utility function to find set of an element i
        // (uses path compression technique)
        private fun find(subsets: Array<Subset?>, i: Int): Int {
            // find root and make root as parent of i
            // (path compression)
            if (subsets[i]!!.parent != i) {
                subsets[i]!!.parent = find(subsets, subsets[i]!!.parent)
            }
            return subsets[i]!!.parent
        }

        // A function that does union of two sets of x and y
        // (uses union by rank)
        private fun union(subsets: Array<Subset?>, x: Int, y: Int) {
            val xRoot = find(subsets, x)
            val yRoot = find(subsets, y)
            if (xRoot == yRoot) return

            // Attach smaller rank tree under root of high
            // rank tree (Union by Rank)
            if (subsets[xRoot]!!.rank < subsets[yRoot]!!.rank) {
                subsets[xRoot]!!.parent = yRoot
                subsets[yRoot]!!.rank += subsets[xRoot]!!.rank
            } else {
                subsets[yRoot]!!.parent = xRoot
                subsets[xRoot]!!.rank += subsets[yRoot]!!.rank
            }
        }
    }

    private data class Edge(var src: Int, var dest: Int)

    private data class Graph(var V: Int, var E: Int) {
        val edge: Array<Edge?> = arrayOfNulls(E)
    }

    private data class Subset(var parent: Int, var rank: Int)
}