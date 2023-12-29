import java.lang.Long.max
import java.util.LinkedList
import kotlin.math.min

private const val SEED_TO_SOIL = "seed-to-soil"
private const val SOIL_TO_FERTILIZER = "soil-to-fertilizer"
private const val FERTILIZER_TO_WATER = "fertilizer-to-water"
private const val WATER_TO_LIGHT = "water-to-light"
private const val LIGHT_TO_TEMPERATURE = "light-to-temperature"
private const val TEMPERATURE_TO_HUMIDITY = "temperature-to-humidity"
private const val HUMIDITY_TO_LOCATION = "humidity-to-location"

class Day5 {

    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_5.txt")!!
        val split = input.split("\n")

        val almanac = parseAlmanac(split).apply {
            seeds.addAll(split[0].substring(7).trim().split(Regex(" +")).map { LongRange(it.toLong(), it.toLong()) })
        }

        var result = Long.MAX_VALUE
        with(almanac) {
            seeds.forEach { seed ->
                result = min(result, getLocation(seed.first))
            }
        }

        println(result)
    }

    private fun solve2() {
        val input = readFile("day_5.txt")!!
        val split = input.split("\n")

        val almanac = parseAlmanac(split).apply {
            seeds.addAll(split[0].substring(7).trim().split(Regex(" +"))
                .map { it.toLong() }
                .windowed(2, 2)
                .map { LongRange(it[0], it[0] + it[1] - 1) })
        }

        var result = Long.MAX_VALUE
        with(almanac) {
            seeds.forEach { seedRange ->
                val soils = intersectRanges(listOf(seedRange).sortedBy { it.first }, SEED_TO_SOIL)
                val fertilizers = intersectRanges(soils.sortedBy { it.first }, SOIL_TO_FERTILIZER)
                val waters = intersectRanges(fertilizers.sortedBy { it.first }, FERTILIZER_TO_WATER)
                val lights = intersectRanges(waters.sortedBy { it.first }, WATER_TO_LIGHT)
                val temperatures = intersectRanges(lights.sortedBy { it.first }, LIGHT_TO_TEMPERATURE)
                val humidities = intersectRanges(temperatures.sortedBy { it.first }, TEMPERATURE_TO_HUMIDITY)
                val locations = intersectRanges(humidities.sortedBy { it.first }, HUMIDITY_TO_LOCATION)

                result = min(result, locations.minByOrNull { it.first }?.first ?: throw IllegalStateException("Empty locations"))
            }
        }

        println(result)
    }

    private fun parseAlmanac(split: List<String>): Almanac {
        val mapSections = mutableMapOf<String, Int>()

        split.forEachIndexed { index, line ->
            when {
                line.startsWith(SEED_TO_SOIL) -> mapSections[SEED_TO_SOIL] = index + 1
                line.startsWith(SOIL_TO_FERTILIZER) -> mapSections[SOIL_TO_FERTILIZER] = index + 1
                line.startsWith(FERTILIZER_TO_WATER) -> mapSections[FERTILIZER_TO_WATER] = index + 1
                line.startsWith(WATER_TO_LIGHT) -> mapSections[WATER_TO_LIGHT] = index + 1
                line.startsWith(LIGHT_TO_TEMPERATURE) -> mapSections[LIGHT_TO_TEMPERATURE] = index + 1
                line.startsWith(TEMPERATURE_TO_HUMIDITY) -> mapSections[TEMPERATURE_TO_HUMIDITY] = index + 1
                line.startsWith(HUMIDITY_TO_LOCATION) -> mapSections[HUMIDITY_TO_LOCATION] = index + 1
            }
        }

        return Almanac().apply {
            for (i in mapSections[SEED_TO_SOIL]!!..mapSections[SOIL_TO_FERTILIZER]!! - 3) {
                addEntry(split[i], SEED_TO_SOIL)
            }

            for (i in mapSections[SOIL_TO_FERTILIZER]!!..mapSections[FERTILIZER_TO_WATER]!! - 3) {
                addEntry(split[i], SOIL_TO_FERTILIZER)
            }

            for (i in mapSections[FERTILIZER_TO_WATER]!!..mapSections[WATER_TO_LIGHT]!! - 3) {
                addEntry(split[i], FERTILIZER_TO_WATER)
            }

            for (i in mapSections[WATER_TO_LIGHT]!!..mapSections[LIGHT_TO_TEMPERATURE]!! - 3) {
                addEntry(split[i], WATER_TO_LIGHT)
            }

            for (i in mapSections[LIGHT_TO_TEMPERATURE]!!..mapSections[TEMPERATURE_TO_HUMIDITY]!! - 3) {
                addEntry(split[i], LIGHT_TO_TEMPERATURE)
            }

            for (i in mapSections[TEMPERATURE_TO_HUMIDITY]!!..mapSections[HUMIDITY_TO_LOCATION]!! - 3) {
                addEntry(split[i], TEMPERATURE_TO_HUMIDITY)
            }

            for (i in mapSections[HUMIDITY_TO_LOCATION]!!..split.lastIndex) {
                addEntry(split[i], HUMIDITY_TO_LOCATION)
            }
        }
    }

    private data class Almanac(
        val seeds: MutableList<LongRange> = mutableListOf(),
        val maps: MutableMap<String, MutableSet<AlmanacEntry>> = mutableMapOf()
    ) {
        fun addEntry(s: String, key: String) {
            val entry = s.trim().split(Regex(" +")).map { it.toLong() }
            maps.getOrPut(key) { mutableSetOf() }.add(
                AlmanacEntry(
                    destinationRange = LongRange(entry[0], entry[0] + entry[2] - 1),
                    sourceRange = LongRange(entry[1], entry[1] + entry[2] - 1),
                    offset = entry[0] - entry[1]
                )
            )
        }

        private fun getDestination(mapKey: String, source: Long): Long {
            val entry = maps[mapKey]?.firstOrNull { it.sourceRange.contains(source) }

            return if (entry != null) {
                source + entry.offset
            } else {
                source
            }
        }

        fun getLocation(seed: Long): Long {
            val soil = getDestination(SEED_TO_SOIL, seed)
            val fertilizer = getDestination(SOIL_TO_FERTILIZER, soil)
            val water = getDestination(FERTILIZER_TO_WATER, fertilizer)
            val light = getDestination(WATER_TO_LIGHT, water)
            val temperature = getDestination(LIGHT_TO_TEMPERATURE, light)
            val humidity = getDestination(TEMPERATURE_TO_HUMIDITY, temperature)

            return getDestination(HUMIDITY_TO_LOCATION, humidity)
        }

        fun intersectRanges(sourceRange: List<LongRange>, mapKey: String): List<LongRange> {
            val destinations = mutableListOf<LongRange>()
            val intersections = mutableListOf<LongRange>()

            sourceRange.forEach { source ->
                maps[mapKey]!!.forEach { entry ->
                    if (entry.sourceRange.contains(source.first) || entry.sourceRange.contains(source.last)) {
                        val intersection = LongRange(
                            max(source.first, entry.sourceRange.first),
                            min(source.last, entry.sourceRange.last)
                        )
                        intersections.add(intersection)

                        destinations.add(
                            LongRange(
                                getDestination(mapKey, intersection.first),
                                getDestination(mapKey, intersection.last)
                            )
                        )
                    }
                }
            }

            intersections.add(LongRange(Long.MAX_VALUE, Long.MAX_VALUE))
            mergeRanges(intersections)

            sourceRange.forEach { source ->
                var start = source.first
                for (range in intersections) {
                    if(start > source.last) {
                        break
                    } else if (range.last < start) {
                        continue
                    } else if(range.first > source.last) {
                        destinations.add(LongRange(start, source.last))
                        break
                    } else if(start < range.first) {
                        destinations.add(LongRange(start, range.first - 1))
                        start = range.last + 1
                    } else {
                        start = range.last + 1
                    }
                }
            }

            return mergeRanges(destinations)
        }

        private fun mergeRanges(ranges: List<LongRange>): List<LongRange> {
            if (ranges.isEmpty()) return listOf()

            val stack = LinkedList<LongRange>()

            val sortedRanges = ranges.sortedBy { it.first }
            stack.addLast(sortedRanges[0])

            for (i in 1 until sortedRanges.size) {
                val top = stack.peekLast()

                if (top.last < sortedRanges[i].first - 1) {
                    stack.addLast(sortedRanges[i])
                } else if (top.last < sortedRanges[i].last) {
                    stack.pollLast()
                    stack.addLast(LongRange(top.first, sortedRanges[i].last))
                }
            }

            return stack
        }
    }

    private data class AlmanacEntry(
        val destinationRange: LongRange,
        val sourceRange: LongRange,
        val offset: Long
    )
}