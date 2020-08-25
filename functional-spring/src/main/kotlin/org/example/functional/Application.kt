package org.example.functional

import org.example.functional.function.Lowercase
import org.example.functional.function.Uppercase
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.cloud.function.context.FunctionRegistration
import org.springframework.cloud.function.context.FunctionType
import org.springframework.cloud.function.context.FunctionalSpringApplication
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean

@SpringBootConfiguration
@EnableAutoConfiguration
class Application : ApplicationContextInitializer<GenericApplicationContext> {

    override fun initialize(context: GenericApplicationContext) {
        context.registerBean("uppercase") {
            FunctionRegistration(Uppercase())
                .type(
                    FunctionType
                        .from(String::class.java)
                        .to(String::class.java)
                )
        }

        context.registerBean("lowercase") {
            FunctionRegistration(Lowercase())
                .type(
                    FunctionType
                        .from(String::class.java)
                        .to(String::class.java)
                )
        }
    }

    companion object Main {
        @JvmStatic
        fun main(args: Array<String>) {
            FunctionalSpringApplication.run(Application::class.java, *args)
        }
    }
}
