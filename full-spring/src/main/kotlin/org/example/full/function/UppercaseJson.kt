package org.example.full.function

import org.example.full.function.model.UpperCaseInput
import org.example.full.function.model.UppercaseOutput
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
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
