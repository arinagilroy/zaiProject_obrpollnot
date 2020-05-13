package com.arina.gilroy

import java.util.*

object Calc {
    private const val operators = "+-*/"
    private const val delimiters = "() $operators"
    var flag = true
    private fun isOperator(token: String): Boolean {
        if (token == "u-") return true
        for (i in 0 until operators.length) {
            if (token[0] == operators[i]) return true
        }
        return false
    }

    private fun isFunction(token: String): Boolean {
        return if (token == "sqrt" || token == "cube" || token == "pow10") true else false
    }

    private fun isDelimiter(token: String): Boolean {
        if (token.length != 1) return false
        for (i in 0 until delimiters.length) {
            if (token[0] == delimiters[i]) return true
        }
        return false
    }

    private fun priority(token: String): Int {
        if (token == "(") return 1
        if (token == "+" || token == "-") return 2
        return if (token == "*" || token == "/") 3 else 4
    }

    fun parse(infix: String?): List<String> {
        val postfix: MutableList<String> = ArrayList()
        val stack: Deque<String> = ArrayDeque()
        val tokenizer = StringTokenizer(infix, delimiters, true)
        var prev = ""
        var curr = ""
        while (tokenizer.hasMoreTokens()) {
            curr = tokenizer.nextToken()
            if (!tokenizer.hasMoreTokens() && isOperator(curr)) {
                println("Некорректное выражение.")
                flag = false
                return postfix
            }
            if (curr == " ") continue
            if (isFunction(curr)) stack.push(curr) else if (isDelimiter(curr)) {
                if (curr == "(") stack.push(curr) else if (curr == ")") {
                    while (stack.peek() != "(") {
                        postfix.add(stack.pop())
                        if (stack.isEmpty()) {
                            println("Скобки не согласованы.")
                            flag = false
                            return postfix
                        }
                    }
                    stack.pop()
                    if (!stack.isEmpty() && isFunction(stack.peek())) {
                        postfix.add(stack.pop())
                    }
                } else {
                    if (curr == "-" && (prev == "" || isDelimiter(prev) && prev != ")")) {
                        // унарный минус
                        curr = "u-"
                    } else {
                        while (!stack.isEmpty() && priority(curr) <= priority(stack.peek())) {
                            postfix.add(stack.pop())
                        }
                    }
                    stack.push(curr)
                }
            } else {
                postfix.add(curr)
            }
            prev = curr
        }
        while (!stack.isEmpty()) {
            if (isOperator(stack.peek())) postfix.add(stack.pop()) else {
                println("Скобки не согласованы.")
                flag = false
                return postfix
            }
        }
        return postfix
    }
}

private val operationMap = mapOf<String, (Double, Double) -> Double>(
    "+" to { x, y -> x + y },
    "-" to { x, y -> y - x },
    "*" to { x, y -> x * y },
    "/" to { x, y -> y / x }
)

fun Stack<Double>.execute(op: (Double, Double) -> Double) = push(op(pop(), pop()))

fun polishCalculateFunctional(expr: String): Double {
    val stack = Stack<Double>()
    // Extension: for-each
    expr.split(" ").forEach {
        // it = single argument
        // let: receiver --> argument                     // elvis, toDouble()
        operationMap[it]?.let { op -> stack.execute(op) } ?: stack.push( it.toDouble() )
    }
    return stack.pop()
}

fun main() {
    val result = Calc.parse("(1 + 2) * 4 + 3")
    var toPolishCalc: String = ""
    for (s in result){
        print("$s ")
        toPolishCalc = toPolishCalc.plus("$s ")
    }
    println()
    //println(toPolishCalc.substring(0,toPolishCalc.length-1))

    val finalResult = polishCalculateFunctional(toPolishCalc.substring(0,toPolishCalc.length-1))

    println(finalResult)
}