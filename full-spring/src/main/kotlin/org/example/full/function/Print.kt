package org.example.full.function

import org.springframework.stereotype.Component
import java.util.function.Consumer

@Component
class Print: Consumer<String> {

    /**
     * Simple Consumer with primitive input and no output.
     */
    override fun accept(value: String) {
        println(value)
    }
}
