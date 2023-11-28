import kotlin.math.pow
import kotlin.system.measureTimeMillis

val values2 = listOf(12, 1, 14, 3, 5, 11, 10, 2, 8, 9, 7, 15, 0, 13, 6, 4)
val values4 = listOf(11, 0, 13, 5, 10, 7, 15, 6, 3, 8, 4, 12, 9, 1, 2, 14)
val podstanovka2 = values2.withIndex().associate { it.index to it.value }
val podstanovka4 = values4.withIndex().associate { it.index to it.value }

fun toBin(dex: Int, amount: Int): List<Int> {
    val binDefault = dex.toString(2)
    return binDefault.padStart(amount, '0').map { it.toString().toInt() }
}

fun scalarProd(bin41: List<Int>, bin42: List<Int>): Int {
    var result = 0
    for (i in 0 until 4) {
        result = result xor (bin41[i] * bin42[i])
    }
    return result
}

fun createMatrix(number: Int): List<List<Int>> {
    return toBin(number, 16).chunked(4)
}

fun createVector(number: Int): List<Int> {
    return toBin(number, 4)
}

fun mult(matrix: List<List<Int>>, vector: List<Int>): List<Int> {
    return matrix.map { scalarProd(it, vector) }
}

fun add(vec1: List<Int>, vec2: List<Int>): List<Int> {
    return vec1.zip(vec2).map { (a, b) -> a xor b }
}

fun toDex(vec: List<Int>): Int {
    return vec.joinToString("").toInt(2)
}

fun invertPodstanovka(podstanovka: Map<Int, Int>): Map<Int, Int> {
    return podstanovka.entries.associate{(k,v) -> v to k}
}

fun calculateDeterminant(matrix: List<List<Int>>): Int {
    return matrix[0][0] * (matrix[1][1] * matrix[2][2] * matrix[3][3] +
            matrix[1][2] * matrix[2][3] * matrix[3][1] +
            matrix[1][3] * matrix[2][1] * matrix[3][2] -
            matrix[1][1] * matrix[2][3] * matrix[3][2] -
            matrix[1][2] * matrix[2][1] * matrix[3][3] -
            matrix[1][3] * matrix[2][2] * matrix[3][1]) -
            matrix[0][1] * (matrix[1][0] * matrix[2][2] * matrix[3][3] +
            matrix[1][2] * matrix[2][3] * matrix[3][0] +
            matrix[1][3] * matrix[2][0] * matrix[3][2] -
            matrix[1][0] * matrix[2][3] * matrix[3][2] -
            matrix[1][2] * matrix[2][0] * matrix[3][3] -
            matrix[1][3] * matrix[2][2] * matrix[3][0]) +
            matrix[0][2] * (matrix[1][0] * matrix[2][1] * matrix[3][3] +
            matrix[1][1] * matrix[2][3] * matrix[3][0] +
            matrix[1][3] * matrix[2][0] * matrix[3][1] -
            matrix[1][0] * matrix[2][3] * matrix[3][1] -
            matrix[1][1] * matrix[2][0] * matrix[3][3] -
            matrix[1][3] * matrix[2][1] * matrix[3][0]) -
            matrix[0][3] * (matrix[1][0] * matrix[2][1] * matrix[3][2] +
            matrix[1][1] * matrix[2][2] * matrix[3][0] +
            matrix[1][2] * matrix[2][0] * matrix[3][1] -
            matrix[1][0] * matrix[2][2] * matrix[3][1] -
            matrix[1][1] * matrix[2][0] * matrix[3][2] -
            matrix[1][2] * matrix[2][1] * matrix[3][0])
}

fun main() {
    val time = measureTimeMillis {
        val finResM = mutableListOf<List<List<Int>>>()
        val finResV = mutableListOf<List<Int>>()

        val matrixes = mutableListOf<List<List<Int>>>()

        for (numbA in 0 until 2.0.pow(16).toInt()) {
            val matrix = createMatrix(numbA)
            if (calculateDeterminant(matrix) % 2 != 0) {
                matrixes.add(matrix)
            }
        }

        val vectors = mutableListOf<List<Int>>()

        for (numbV in 0 until 16) {
            vectors.add(createVector(numbV))
        }

        println(vectors)

        var start = System.currentTimeMillis()

        for (m2 in matrixes) {
            for (v2 in vectors) {
                for (m1 in matrixes) {
                    for (v1 in vectors) {
                        var counter = 0
                        for (x in 0 until 16) {
                            if (System.currentTimeMillis() - start > 5_000 * 60) {
                                println(matrixes.indexOf(m2))
                                println(matrixes.indexOf(m1))
                                println(vectors.indexOf(v2))
                                println(vectors.indexOf(v1))
                                start = System.currentTimeMillis()
                            }
                            val xVect = vectors[x]
                            val y = createVector(podstanovka2[toDex(add(mult(m1, xVect), v1))]!!)
                            val fin = toDex(add(mult(m2, y), v2))
                            if (podstanovka4[x] == fin) {
                                counter++
                            } else {
                                break
                            }
                        }
                        if (counter == 16) {
                            println("$m1 $m2 $v1 $v2")
                            finResM.add(m1)
                            finResV.add(v1)
                            finResM.add(m2)
                            finResV.add(v2)
                        }
                    }
                }
            }
        }
        println(finResM.size)
        println(finResV.size)
        println(finResM)
        println(finResV)
    }
    println("time = $time")
}
