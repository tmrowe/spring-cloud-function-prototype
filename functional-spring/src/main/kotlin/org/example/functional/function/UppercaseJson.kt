package org.example.functional.function

import org.example.functional.function.model.UpperCaseInput
import org.example.functional.function.model.UppercaseOutput
import java.util.function.Function

class UppercaseJson: Function<UpperCaseInput, UppercaseOutput> {

    /**
     * Function demonstrating serializing and de-serializing JSON.
     *
     * @return An instance of [UppercaseOutput] containing the value in [UpperCaseInput.value] uppercased.
     */
    override fun apply(input: UpperCaseInput): UppercaseOutput {
        return UppercaseOutput(
            value = input.value.toUpperCase()
        )
    }
}
