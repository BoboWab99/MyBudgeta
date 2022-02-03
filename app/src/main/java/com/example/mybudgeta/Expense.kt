package com.example.mybudgeta

data class Expense(
    var title: String,
    var cost: Number,
    var category: String,
    var uid: String? = null,
    var date: String? = null // date added
)