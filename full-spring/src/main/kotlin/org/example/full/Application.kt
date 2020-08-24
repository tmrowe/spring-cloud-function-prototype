package org.example.full

import org.example.full.function.model.UpperCaseInput
import org.example.full.function.model.UppercaseOutput
import org.example.full.function.service.UppercaseService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application {

    companion object Main {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }

    /**
     * Simple function with primitive input and output.
     *
     * @return The given String after it is lower cased.
     */
    @Bean
    fun lowercase(): (String) -> String {
        return { it.toLowerCase() }
    }

    /**
     * Simple Consumer with primitive input and no output.
     */
    @Bean
    fun print(): (String) -> Unit {
        return { println(it)}
    }

    /**
     * Simple Supplier with primitive output and no input.
     *
     * @return A random Integer between 0 and 10.
     */
    @Bean
    fun random(): () -> Int {
        return { (0..10).random() }
    }

    /**
     * Function demonstrating the injection of a service containing it's functionality.
     *
     * @param uppercaseService Service containing the functionality this lambda invokes.
     * @return The given string upper cased.
     */
    @Bean
    fun uppercase(
        uppercaseService: UppercaseService
    ): (String) -> String {
        return { input ->
            uppercaseService.uppercase(input)
        }
    }

    /**
     * Function demonstrating a property being injected in.
     *
     * @param veryImportant The injected property
     * @return The property.
     */
    @Bean
    fun getVariable(
        @Value("\${some.important.variable}") veryImportant: String
    ): () -> String {
        return { veryImportant }
    }

    /**
     * Function demonstrating serializing and de-serializing JSON.
     *
     * @return An instance of  [UppercaseOutput] containing the value in [UpperCaseInput.value] uppercased.
     */
    @Bean
    fun uppercaseJson(): (UpperCaseInput) -> UppercaseOutput {
        return { input ->
            UppercaseOutput(
                value = input.value.toUpperCase()
            )
        }
    }
}
