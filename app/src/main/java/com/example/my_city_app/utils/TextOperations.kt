package com.example.my_city_app.utils

object TextFormat {
    fun removeUnexpectedSpaces(text: String): String {
        val withoutDuplicates = text.fold("") { acc, e ->
            if (acc.lastOrNull()?.isWhitespace() == true && e.isWhitespace()) acc
            else acc + e
        }
        val withoutLast = if (withoutDuplicates.lastOrNull()?.isWhitespace() == true)
            withoutDuplicates.dropLast(1) else withoutDuplicates
        return if (withoutLast.firstOrNull()?.isWhitespace() == true)
            withoutLast.drop(1) else withoutLast
    }
}