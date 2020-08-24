package org.example.full.function

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.function.Supplier

@Component
class GetVariable(
    @Value("\${some.important.variable}") private val veryImportant: String
): Supplier<String> {

    /**
     * Function demonstrating a property being injected in.
     *
     * @return The value of the 'some.important.variable' property.
     */
    override fun get(): String {
        return veryImportant
    }
}
