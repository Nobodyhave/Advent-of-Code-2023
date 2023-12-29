class Day19 {
    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_19.txt")!!
        val split = input.split("\n")

        val flows = parseFlows(split)
        val parts = parseParts(split)

        var rating = 0L
        parts.forEach {
            var result: Result = Result.Redirected("in")
            do {
                result = flows[(result as Result.Redirected).flow]!!.process(it)
            } while (result is Result.Redirected)

            if (result is Result.Accepted) {
                rating += it.rating
            }
        }

        println(rating)
    }

    private fun solve2() {
        val input = readFile("day_19.txt")!!
        val split = input.split("\n")

        val flows = parseFlows(split)

        val combinations = inspectWorkflow(flows, "in", Limits())
        println(combinations)
    }

    private fun inspectWorkflow(flows: Map<String, Flow>, flow: String, limits: Limits): Long {
        println("Workflow $flow: Limits: $limits")

        var ruleLimits = limits.copy()
        return flows[flow]!!.rules.sumOf { rule ->
            val result = when (rule.category) {
                Category.X -> {
                    handleOperations(
                        flows,
                        rule,
                        { ruleLimits.copy(xRange = LongRange(ruleLimits.xRange.first, minOf(ruleLimits.xRange.last, rule.value - 1))) },
                        { ruleLimits.copy(xRange = LongRange(maxOf(ruleLimits.xRange.first, rule.value + 1), ruleLimits.xRange.last)) }
                    )
                }
                Category.M -> {
                    handleOperations(
                        flows,
                        rule,
                        { ruleLimits.copy(mRange = LongRange(ruleLimits.mRange.first, minOf(ruleLimits.mRange.last, rule.value - 1))) },
                        { ruleLimits.copy(mRange = LongRange(maxOf(ruleLimits.mRange.first, rule.value + 1), ruleLimits.mRange.last)) }
                    )
                }
                Category.A -> {
                    handleOperations(
                        flows,
                        rule,
                        { ruleLimits.copy(aRange = LongRange(ruleLimits.aRange.first, minOf(ruleLimits.aRange.last, rule.value - 1))) },
                        { ruleLimits.copy(aRange = LongRange(maxOf(ruleLimits.aRange.first, rule.value + 1), ruleLimits.aRange.last)) }
                    )
                }
                Category.S -> {
                    handleOperations(
                        flows,
                        rule,
                        { ruleLimits.copy(sRange = LongRange(ruleLimits.sRange.first, minOf(ruleLimits.sRange.last, rule.value - 1))) },
                        { ruleLimits.copy(sRange = LongRange(maxOf(ruleLimits.sRange.first, rule.value + 1), ruleLimits.sRange.last)) }
                    )
                }
                Category.NONE -> {
                    when (rule.result) {
                        is Result.Accepted -> calculateCombinations(ruleLimits)
                        is Result.Rejected -> 0
                        is Result.Redirected -> inspectWorkflow(flows, rule.result.flow, ruleLimits.copy())
                        else -> throw IllegalStateException("Rules should not be skipped in part 2")
                    }
                }
            }

            if(rule.operation != Operation.NONE) {
                ruleLimits = updateLimits(rule, ruleLimits)
            }

            return@sumOf result
        }
    }

    private fun updateLimits(rule: Rule, limits: Limits): Limits {
        return when(rule.operation) {
            Operation.LESS -> {
                when (rule.category) {
                    Category.X -> {
                        limits.copy(xRange = LongRange(maxOf(limits.xRange.first, rule.value), limits.xRange.last))
                    }
                    Category.M -> {
                        limits.copy(mRange = LongRange(maxOf(limits.mRange.first, rule.value), limits.mRange.last))
                    }
                    Category.A -> {
                        limits.copy(aRange = LongRange(maxOf(limits.aRange.first, rule.value), limits.aRange.last))
                    }
                    Category.S -> {
                        limits.copy(sRange = LongRange(maxOf(limits.sRange.first, rule.value), limits.sRange.last))
                    }
                    Category.NONE -> {
                        throw IllegalStateException("No ops is the last rule, shouldn't be reached here")
                    }
                }
            }
            Operation.MORE -> {
                when (rule.category) {
                    Category.X -> {
                        limits.copy(xRange = LongRange(limits.xRange.first, minOf(limits.xRange.last, rule.value)))
                    }
                    Category.M -> {
                        limits.copy(mRange = LongRange(limits.mRange.first, minOf(limits.mRange.last, rule.value)))
                    }
                    Category.A -> {
                        limits.copy(aRange = LongRange(limits.aRange.first, minOf(limits.aRange.last, rule.value)))
                    }
                    Category.S -> {
                        limits.copy(sRange = LongRange(limits.sRange.first, minOf(limits.sRange.last, rule.value)))
                    }
                    Category.NONE -> {
                        throw IllegalStateException("No ops is the last rule, shouldn't be reached here")
                    }
                }
            }
            Operation.NONE -> {
                throw IllegalStateException("No ops is the last rule, shouldn't be reached here")
            }
        }
    }

    private fun handleOperations(
        flows: Map<String,Flow>,
        rule: Rule,
        lessBlock: (rule: Rule) -> Limits,
        moreBlock: (rule: Rule) -> Limits,
    ): Long {
        return when (rule.operation) {
            Operation.LESS -> {
                when (rule.result) {
                    is Result.Accepted -> calculateCombinations(
                        lessBlock(rule)
                    )
                    is Result.Rejected -> 0
                    is Result.Redirected -> inspectWorkflow(flows, rule.result.flow, lessBlock(rule))
                    else -> throw IllegalStateException("Rules should not be skipped in part 2")
                }
            }
            Operation.MORE -> {
                when (rule.result) {
                    is Result.Accepted -> calculateCombinations(
                        moreBlock(rule)
                    )
                    is Result.Rejected -> 0
                    is Result.Redirected -> inspectWorkflow(flows, rule.result.flow, moreBlock(rule))
                    else -> throw IllegalStateException("Rules should not be skipped in part 2")
                }
            }
            Operation.NONE -> {
                throw IllegalStateException("No ops should be handled in another branch")
            }
        }
    }

    private fun parseFlows(input: List<String>): Map<String, Flow> {
        val flows = input.takeWhile {
            it.isNotBlank() && it[0].isLetter()
        }
            .map {
                val name = it.substring(0, it.indexOf('{'))
                val rulesString = it.substring(it.indexOf('{') + 1, it.indexOf('}')).split(",")
                val rules = rulesString.map {
                    if (it.contains('<') || it.contains('>')) {
                        val category = it.substring(0, 1)
                        val operation = it[1]
                        val value = it.substring(2, it.indexOf(':')).toLong()
                        val result = it.substring(it.indexOf(':') + 1)

                        Rule(
                            Category.valueOf(category.uppercase()),
                            if (operation == '<') Operation.LESS else Operation.MORE,
                            value,
                            if (result == "A") Result.Accepted else if (result == "R") Result.Rejected else Result.Redirected(result)
                        )
                    } else {
                        Rule(
                            Category.NONE,
                            Operation.NONE,
                            0,
                            if (it == "A") Result.Accepted else if (it == "R") Result.Rejected else Result.Redirected(it)
                        )
                    }
                }

                Flow(
                    name,
                    rules
                )
            }

        return flows.associateBy { it.name }
    }

    private fun parseParts(input: List<String>): List<Part> {
        return input.dropWhile { it.isEmpty() || it[0] != '{' }
            .map {
                val partString = it.substring(1, it.indexOf('}')).split(',')
                Part(
                    partString[0].substring(partString[0].indexOf('=') + 1).toLong(),
                    partString[1].substring(partString[1].indexOf('=') + 1).toLong(),
                    partString[2].substring(partString[2].indexOf('=') + 1).toLong(),
                    partString[3].substring(partString[3].indexOf('=') + 1).toLong(),
                )
            }
    }

    private fun calculateCombinations(limits: Limits): Long {
        return (limits.xRange.last - limits.xRange.first + 1) *
                (limits.mRange.last - limits.mRange.first + 1) *
                (limits.aRange.last - limits.aRange.first + 1) *
                (limits.sRange.last - limits.sRange.first + 1)
    }

    private data class Part(
        val x: Long,
        val m: Long,
        val a: Long,
        val s: Long,
        val rating: Long = x + m + a + s
    )

    private data class Rule(
        val category: Category,
        val operation: Operation,
        val value: Long,
        val result: Result
    ) {
        fun apply(part: Part): Result {
            val partValue = when (category) {
                Category.X -> part.x
                Category.M -> part.m
                Category.A -> part.a
                Category.S -> part.s
                Category.NONE -> 0
            }

            return if (category == Category.NONE ||
                (operation == Operation.LESS && partValue < value) ||
                (operation == Operation.MORE && partValue > value)
            ) {
                result
            } else {
                Result.Skipped
            }
        }
    }

    private data class Flow(
        val name: String,
        val rules: List<Rule>
    ) {
        fun process(part: Part): Result {
            rules.forEach {
                val result = it.apply(part)
                if (result != Result.Skipped) return result
            }

            throw IllegalStateException("Flow didn't provide result")
        }
    }

    private sealed class Result {
        object Accepted : Result()
        object Rejected : Result()
        object Skipped : Result()
        class Redirected(val flow: String) : Result()
    }

    private data class Limits(
        var xRange: LongRange = LongRange(1, 4000),
        var mRange: LongRange = LongRange(1, 4000),
        var aRange: LongRange = LongRange(1, 4000),
        var sRange: LongRange = LongRange(1, 4000),
    )

    private enum class Category {
        X, M, A, S, NONE;
    }

    private enum class Operation {
        LESS, MORE, NONE
    }
}