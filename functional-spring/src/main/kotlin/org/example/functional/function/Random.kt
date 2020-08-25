package org.example.functional.function

import java.util.function.Supplier

class Random: Supplier<Int> {

    /**
     * Simple Supplier with primitive output and no input.
     *
     * @return A random Integer between 0 and 10.
     */
    override fun get(): Int {
        return (0..10).random()
    }
}
