package org.example.full.function

import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class Lowercase: Function<String, String> {

    /**
     * Simple function with primitive input and output.
     *
     * @return The given String after it is lower cased.
     */
    override fun apply(value: String): String {
        return value.toLowerCase()
    }
}
