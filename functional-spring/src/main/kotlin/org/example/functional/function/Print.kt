package org.example.functional.function

import java.util.function.Consumer

class Print: Consumer<String> {

    /**
     * Simple Consumer with primitive input and no output.
     */
    override fun accept(value: String) {
        println(value)
    }
}
