package com.lambao.cctg.model

data class InterestPeriod(
    val startDay: Int?,
    val endDay: Int?,
    val rate: Double?
)

val fakeInterestPeriods = listOf(
    InterestPeriod(0, 6, 2.7),
    InterestPeriod(7, 13, 2.7),
    InterestPeriod(14, 20, 3.0),
    InterestPeriod(21, 29, 3.0),
    InterestPeriod(30, 59, 3.6),
    InterestPeriod(60, 89, 3.6),
    InterestPeriod(90, null, 4.3),
)
