package org.example.functional.function

import java.util.function.Function

class Uppercase: Function<String, String> {
    override fun apply(value: String): String {
        return value.toUpperCase()
    }
}
