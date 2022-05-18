package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

fun main() {
    while (true) {
        print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ")
        val response = readln()
        val (sourceBase, targetBase) =  if (response == "/exit") break else response.split(" ").map { it.toInt() }
        while (true) {
            print("Enter number in base $sourceBase to convert to base $targetBase (To go back type /back) ")
            when (val number = readln()) {
                "/back" -> break
                else -> {
                    val res = if (sourceBase == 10 && targetBase == 10) number else {
                        if (sourceBase == 10) number.convertFrom(targetBase) else {
                            if (targetBase == 10) number.convertTo(sourceBase) else
                            number.convertTo(sourceBase).convertFrom(targetBase)
                        }
                    }
                    println("Conversion result: $res")
                }
            }
            println()
        }
    }
}

fun String.convertTo(base: Int): String {
    val map = (('0'..'9') + ('a'..'z')).mapIndexed { i, c -> c to i }.toMap()
    val (num, numrem) = if (this.contains(".")) {
        this.substringBefore(".") to this.substringAfter(".")
    } else { this to "" }
    var res = BigInteger.ZERO
    for (i in 0..num.lastIndex) {
        res += map[num[i]]!!.toBigInteger() * base.pow(num.lastIndex - i, 0).toBigInteger()
    }
    var result = res.toString()
    if (numrem.isNotEmpty()) {
        var resrem = BigDecimal.ZERO
        for (i in 0..numrem.lastIndex) {
            if (numrem[i] == '0') { resrem += BigDecimal.ZERO; continue }
            val a = map[numrem[i]]!!.toBigDecimal()
            val b = base.pow(-(i + 1), numrem.length)
            resrem += a * b
        }
        result += ".${resrem.setScale(5, RoundingMode.CEILING).toString().substringAfter(".")}"
    }
    return result
}

private fun <T : Number> T.pow(i: Int, n: Int): BigDecimal {
    val res = this.toString().toBigDecimal().pow(kotlin.math.abs(i)).setScale(5, RoundingMode.FLOOR)
    val result = if (i < 0) {
        BigDecimal.ONE.divide(res, n, RoundingMode.CEILING)
    } else res
    return result
}

fun String.convertFrom(base: Int): String {
    val map = (('0'..'9') + ('a'..'z')).mapIndexed { i, c -> i.toBigInteger() to c.toString() }.toMap()
    val (num, numrem) = if (this.contains(".")) {
        this.substringBefore(".") to this.substringAfter(".")
    } else { this to "" }
    var result = ""
    if (num != "0") {
        var res = ""
        var a = num.toBigInteger()
        val baseBiga = base.toBigInteger()
        if (a < baseBiga) res = "${map[a]}$res" else {
            while (a >= baseBiga) {
                res = "${map[(a % baseBiga)]}$res"
                a /= baseBiga
                if (a < baseBiga) res = "${map[a]}$res"
            }
        }
        result = res
    } else result = num
    if (numrem.isNotEmpty() && numrem.toBigDecimal() != BigDecimal.ZERO) {
        var resrem = ""
        var r = ".$numrem".toBigDecimal()
        val baseBigb = base.toBigDecimal()
        while (resrem.length < 5 && r != BigDecimal.ZERO) {
            val resurs = (r * baseBigb).toString().substringBefore(".").toBigInteger()
            resrem += "${map[resurs]}"
            r = baseBigb * r - resurs.toBigDecimal()
        }
        result += ".$resrem"
    } else if (numrem.isNotEmpty() && numrem.toBigDecimal() == BigDecimal.ZERO) result += ".00000"
    return result
}




