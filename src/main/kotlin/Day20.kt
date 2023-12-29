import java.util.LinkedList

class Day20 {
    fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        val input = readFile("day_20.txt")!!
        val split = input.split("\n")

        val modules = parseModules(split)

        var lowCount = 0L
        var highCount = 0L

        repeat(1000) {
            val queue = LinkedList<Signal>()
            queue.add(Signal(SignalType.LOW, "hand", "button"))
            while (queue.isNotEmpty()) {
                val signal = queue.pollFirst()

                if (signal.type == SignalType.LOW && signal.source != "hand") lowCount++
                if (signal.type == SignalType.HIGH) highCount++

                modules[signal.destination]?.processSignal(signal)?.forEach { queue.addLast(it) }
            }
        }

        val result = lowCount * highCount
        println(result)
    }

    private fun solve2() {
        val input = readFile("day_20.txt")!!
        val split = input.split("\n")

        val modules = parseModules(split)

        var count = 0
        while (true) {
            count++

            val queue = LinkedList<Signal>()
            queue.add(Signal(SignalType.LOW, "hand", "button"))
            while (queue.isNotEmpty()) {
                val signal = queue.pollFirst()

                modules[signal.destination]?.processSignal(signal)?.forEach { queue.addLast(it) }

                // Code to manually find cycles per input source
//                if(signal.source == "rk" && signal.type == SignalType.HIGH) {
//                    println("Count: $count")
//                }
//                if(signal.source == "cd" && signal.type == SignalType.HIGH) {
//                    println("Count: $count")
//                }
//                if(signal.source == "zf" && signal.type == SignalType.HIGH) {
//                    println("Count: $count")
//                }
//                if(signal.source == "qx" && signal.type == SignalType.HIGH) {
//                    println("Count: $count")
//                }
            }
        }
    }

    private fun parseModules(input: List<String>): Map<String, Module> {
        val modules = (input.map {
            val output = it.substring(it.indexOf('>') + 1).trim().split(", ")

            if (it.startsWith("broadcaster")) {
                Module.Broadcast("broadcaster", output)
            } else if (it.startsWith("%")) {
                Module.FlipFlop(it.substring(1, it.indexOf(" ")), output)
            } else {
                Module.Conjunction(it.substring(1, it.indexOf(" ")), output)
            }
        } + Module.Button("button", listOf("broadcaster"))).associateBy { it.name }

        modules.forEach {
            it.value.output.forEach { output ->
                val destModule = modules[output]
                if (destModule is Module.Conjunction) {
                    destModule.initForInput(it.key)
                }
            }
        }

        return modules
    }

    private fun gcd(a: Long, b: Long): Long {
        return if (a == 0L) b else gcd(b % a, a)
    }

    private fun lcm(a: Long, b: Long): Long {
        return a / gcd(a, b) * b
    }

    private sealed class Module(val name: String, val output: List<String>) {
        abstract fun processSignal(signal: Signal): List<Signal>

        class Button(name: String, output: List<String>) : Module(name, output) {
            override fun processSignal(signal: Signal): List<Signal> {
                return output.map { Signal(SignalType.LOW, name, it) }
            }
        }

        class Broadcast(name: String, output: List<String>) : Module(name, output) {
            override fun processSignal(signal: Signal): List<Signal> {
                return output.map {
                    Signal(signal.type, name, it)
                }
            }
        }

        class FlipFlop(name: String, output: List<String>) : Module(name, output) {
            private var state: SignalType = SignalType.LOW

            override fun processSignal(signal: Signal): List<Signal> {
                return if (signal.type == SignalType.HIGH) {
                    emptyList()
                } else {
                    state = if (state == SignalType.LOW) SignalType.HIGH else SignalType.LOW
                    output.map {
                        Signal(state, name, it)
                    }
                }
            }
        }

        class Conjunction(name: String, output: List<String>) : Module(name, output) {
            private val inputState = mutableMapOf<String, SignalType>()

            override fun processSignal(signal: Signal): List<Signal> {
                inputState[signal.source] = signal.type

                val signalTypeToSend = if (inputState.values.all { it == SignalType.HIGH }) SignalType.LOW else SignalType.HIGH
                return output.map {
                    Signal(signalTypeToSend, name, it)
                }
            }

            fun initForInput(input: String) {
                inputState[input] = SignalType.LOW
            }
        }
    }

    private data class Signal(
        val type: SignalType,
        val source: String,
        val destination: String,
    )


    private enum class SignalType {
        LOW, HIGH
    }
}