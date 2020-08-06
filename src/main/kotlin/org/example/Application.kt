package org.example

import org.example.service.UppercaseService
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

    // TODO: Move these?

    /**
     * We have three types of function:
     * - Supplier<Output> :: Produces some output.
     * - Function<Input, Output> :: Takes some input and produces some output.
     * - Consumer<Input> :: Takes and process some input.
     *
     * These kotlin type definitions will map to these three types:
     * - Supplier :: () -> Int
     * - Function :: (String) -> String
     * - Consumer :: (String) -> Unit
     *
     * On running the spring application, each of these function types will be deployed to a web server with a path
     * matching the function name.
     */

    data class UpperCaseInput(
        val value: String
    )

    data class UppercaseOutput(
        val value: String
    )

    // Simple function with primitive input and output.
    @Bean
    fun lowercase(): (String) -> String {
        return { it.toLowerCase() }
    }

    // Simple Consumer with primitive input.
    @Bean
    fun print(): (String) -> Unit {
        return { println(it)}
    }

    // Simple Supplier with primitive output.
    @Bean
    fun random(): () -> Int {
        return { (0..10).random() }
    }

    // Injecting a service into a function and using JSON for input and output.
    @Bean
    fun uppercaseJson(
        uppercaseService: UppercaseService
    ): (UpperCaseInput) -> UppercaseOutput {
        return { input ->
            UppercaseOutput(
                value = uppercaseService.uppercase(input.value)
            )
        }
    }

    // Injecting properties.
    @Bean
    fun getVariable(
        @Value("\${some.important.variable}") veryImportant: String
    ): () -> String {
        return { veryImportant }
    }
}
