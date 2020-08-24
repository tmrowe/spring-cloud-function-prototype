package org.example.functional.function

import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class Uppercase: Function<String, String> {
    override fun apply(value: String): String {
        return value.toUpperCase()
    }
}
