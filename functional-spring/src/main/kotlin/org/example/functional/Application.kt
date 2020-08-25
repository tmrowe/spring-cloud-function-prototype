package org.example.functional

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.example.functional.function.*
import org.example.functional.function.model.UpperCaseInput
import org.example.functional.function.model.UppercaseOutput
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.cloud.function.context.FunctionRegistration
import org.springframework.cloud.function.context.FunctionType
import org.springframework.cloud.function.context.FunctionalSpringApplication
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@SpringBootConfiguration
@EnableAutoConfiguration
class Application : ApplicationContextInitializer<GenericApplicationContext> {

    fun getObjectMapper(): ObjectMapper {
        return Jackson2ObjectMapperBuilder()
            .modules(KotlinModule())
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build()
    }

    override fun initialize(context: GenericApplicationContext) {
        context.registerBean("ObjectMapper") {
            getObjectMapper()
        }

        context.registerBean("lowercase") {
            FunctionRegistration(Lowercase())
                .type(
                    FunctionType
                        .from(String::class.java)
                        .to(String::class.java)
                )
        }

        context.registerBean("print") {
            FunctionRegistration(Print())
                .type(FunctionType.consumer(String::class.java))
        }

        context.registerBean("random") {
            FunctionRegistration(Random())
                .type(FunctionType.supplier(Integer::class.java))
        }

        context.registerBean("uppercase") {
            FunctionRegistration(Uppercase())
                .type(
                    FunctionType
                        .from(String::class.java)
                        .to(String::class.java)
                )
        }

        context.registerBean("uppercaseJson") {
            FunctionRegistration(UppercaseJson())
                .type(
                    FunctionType
                        .from(UpperCaseInput::class.java)
                        .to(UppercaseOutput::class.java)
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
