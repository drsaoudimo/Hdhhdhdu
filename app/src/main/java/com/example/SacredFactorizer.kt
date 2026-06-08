package com.example

import java.math.BigInteger

object SacredFactorizer {

    val A1 = arrayOf(
        longArrayOf(7, 286, 200, 176, 120, 165),
        longArrayOf(206, 75, 129, 109, 123, 111),
        longArrayOf(43, 52, 99, 128, 111, 110),
        longArrayOf(98, 135, 112, 78, 118, 64),
        longArrayOf(77, 227, 93, 88, 69, 60),
        longArrayOf(34, 30, 73, 54, 45, 83),
        longArrayOf(182, 88, 75, 85, 54, 53),
        longArrayOf(89, 59, 37, 35, 38, 29),
        longArrayOf(18, 45, 60, 49, 62, 55),
        longArrayOf(78, 96, 29, 22, 24, 13),
        longArrayOf(14, 11, 11, 18, 12, 12),
        longArrayOf(30, 52, 52, 44, 28, 28),
        longArrayOf(20, 56, 40, 31, 50, 40),
        longArrayOf(46, 42, 29, 19, 36, 25),
        longArrayOf(22, 17, 19, 26, 30, 20),
        longArrayOf(15, 21, 11, 8, 8, 19),
        longArrayOf(5, 8, 8, 11, 11, 8),
        longArrayOf(3, 9, 5, 4, 7, 3),
        longArrayOf(6, 3, 5, 4, 5, 6)
    )

    val A_NT = arrayOf(
        longArrayOf(5, 218, 112, 136, 175, 166),
        longArrayOf(175, 85, 161, 141, 112, 164),
        longArrayOf(73, 62, 127, 111, 91, 114),
        longArrayOf(86, 152, 111, 48, 165, 59),
        longArrayOf(88, 151, 77, 179, 25, 80),
        longArrayOf(28, 22, 84, 68, 59, 74),
        longArrayOf(197, 111, 64, 137, 54, 36),
        longArrayOf(208, 102, 46, 48, 45, 42),
        longArrayOf(26, 84, 122, 64, 102, 96),
        longArrayOf(80, 99, 36, 29, 35, 14),
        longArrayOf(18, 10, 13, 8, 12, 7),
        longArrayOf(29, 79, 45, 64, 22, 30),
        longArrayOf(16, 80, 59, 15, 36, 25),
        longArrayOf(34, 28, 17, 14, 23, 30),
        longArrayOf(15, 12, 14, 17, 26, 14),
        longArrayOf(10, 9, 8, 3, 8, 11),
        longArrayOf(3, 4, 7, 12, 9, 8),
        longArrayOf(2, 5, 2, 3, 7, 3),
        longArrayOf(4, 2, 3, 3, 3, 5)
    )

    val M_corrected = arrayOf(
        doubleArrayOf(8.87, 264.85, 462.24, 545.53, 337.34, 220.07, 406.48, 159.03, 325.79, 331.59, 141.21, 147.85, 405.12),
        doubleArrayOf(53.02, 42.75, 84.66, 55.93, 76.23, 50.61, 12.81, 46.08, 17.17, 8.58, 38.43, 34.58, 5.22),
        doubleArrayOf(6.32, 1.75, 0.65, 0.93, 0.13, 0.41, 1.73, 0.41, 0.71, 0.84, 0.75, 0.40, 0.86)
    )

    data class Result(val p: BigInteger, val q: BigInteger, val strategy: String)

    fun factorize(n: BigInteger): Result? {
        if (n <= BigInteger.ONE) return null
        if (n.mod(BigInteger.valueOf(2)) == BigInteger.ZERO) return Result(BigInteger.valueOf(2), n.divide(BigInteger.valueOf(2)), "Even parity check")

        // Strategy 1: Differences
        for (i in A1.indices) {
            for (j in A1[i].indices) {
                val diff = BigInteger.valueOf(Math.abs(A1[i][j] - A_NT[i][j]))
                if (diff > BigInteger.ZERO) {
                    val g = n.gcd(diff)
                    if (g > BigInteger.ONE && g < n) return Result(g, n.divide(g), "Strategy 1: Matrix Difference at [$i,$j]")
                }
            }
        }

        // Strategy 2: Sums
        for (i in A1.indices) {
            val sumA1 = A1[i].sum()
            val sumANT = A_NT[i].sum()
            val diff = BigInteger.valueOf(Math.abs(sumA1 - sumANT))
            val g = n.gcd(diff)
            if (g > BigInteger.ONE && g < n) return Result(g, n.divide(g), "Strategy 2: Row Sum Difference at Row $i")
            
            val gA1 = n.gcd(BigInteger.valueOf(sumA1))
            if (gA1 > BigInteger.ONE && gA1 < n) return Result(gA1, n.divide(gA1), "Strategy 2: Row Sum A1 at Row $i")
            
            val gANT = n.gcd(BigInteger.valueOf(sumANT))
            if (gANT > BigInteger.ONE && gANT < n) return Result(gANT, n.divide(gANT), "Strategy 2: Row Sum ANT at Row $i")
        }

        // Strategy 3: Column Sums
        for (j in 0 until 6) {
            var sumA1 = 0L
            var sumANT = 0L
            for (i in A1.indices) {
                sumA1 += A1[i][j]
                sumANT += A_NT[i][j]
            }
            val diff = BigInteger.valueOf(Math.abs(sumA1 - sumANT))
            val g = n.gcd(diff)
            if (g > BigInteger.ONE && g < n) return Result(g, n.divide(g), "Strategy 3: Column Sum Difference at Col $j")
        }

        // Strategy 4: Gram Matrix Elements (Approximate)
        for (i in 0 until 6) {
            for (j in 0 until 6) {
                var g1Val = BigInteger.ZERO
                var gNTVal = BigInteger.ZERO
                for (k in A1.indices) {
                    g1Val = g1Val.add(BigInteger.valueOf(A1[k][i] * A1[k][j]))
                    gNTVal = gNTVal.add(BigInteger.valueOf(A_NT[k][i] * A_NT[k][j]))
                }
                val diff = g1Val.subtract(gNTVal).abs()
                if (diff > BigInteger.ZERO) {
                    val g = n.gcd(diff)
                    if (g > BigInteger.ONE && g < n) return Result(g, n.divide(g), "Strategy 4: Gram Matrix Difference at [$i,$j]")
                }
            }
        }
        
        // M Matrix Based (Multiplied by 100 as per prompt suggestion)
        for (i in M_corrected.indices) {
            for (j in M_corrected[i].indices) {
                val valM = BigInteger.valueOf((M_corrected[i][j] * 100).toLong())
                if (valM > BigInteger.ZERO) {
                    val g = n.gcd(valM)
                    if (g > BigInteger.ONE && g < n) return Result(g, n.divide(g), "Strategy 5: Sacred Corrected Matrix M at [$i,$j]")
                }
            }
        }

        return null
    }
}
