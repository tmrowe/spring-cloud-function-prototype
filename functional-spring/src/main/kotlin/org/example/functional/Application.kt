package org.example.functional

import org.example.functional.function.Uppercase
import org.springframework.boot.SpringBootConfiguration
import org.springframework.cloud.function.context.FunctionRegistration
import org.springframework.cloud.function.context.FunctionType
import org.springframework.cloud.function.context.FunctionalSpringApplication
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean

@SpringBootConfiguration
class Application : ApplicationContextInitializer<GenericApplicationContext> {

    override fun initialize(context: GenericApplicationContext) {
        context.registerBean {
            FunctionRegistration(Uppercase())
                .type(
                    FunctionType
                        .from(String::class.java)
                        .to(String::class.java)
                )
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            FunctionalSpringApplication.run(Application::class.java, *args)
        }
    }
}