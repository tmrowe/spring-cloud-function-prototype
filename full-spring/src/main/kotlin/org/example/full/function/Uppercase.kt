package org.example.full.function

import org.example.full.function.service.UppercaseService
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class Uppercase(
    val uppercaseService: UppercaseService
) : Function<String, String> {

    /**
     * Function demonstrating the injection of a service containing it's functionality.
     *
     * @return The given string upper cased.
     */
    override fun apply(value: String): String {
        return uppercaseService.uppercase(value)
    }
}
