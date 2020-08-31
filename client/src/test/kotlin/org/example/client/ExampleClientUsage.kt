package org.example.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.example.client.model.SynchronousClientInput.AwsLambdaClientInput
import org.example.client.model.SynchronousClientInput.WebServerClientInput
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.reactive.function.client.WebClient
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials
import software.amazon.awssdk.regions.Region

/**
 * This file demonstrates using this library.
 */
class ExampleClientUsage {

    // This is pointing at the full-spring webserver running locally.
    private val webClient = WebClient
        .builder()
        .baseUrl("http://localhost:9101")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()

    private val mapper = Jackson2ObjectMapperBuilder()
        .modules(KotlinModule())
        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .build() as ObjectMapper

    private val remoteFunctionClient = D6RemoteFunctionClient(
        webClient = webClient,
        mapper = mapper
    )

    // Normally we would share these data classes via a library but they are duplicated here for demonstration purposes.
    data class UpperCaseInput(
        val value: String
    )

    data class UppercaseOutput(
        val value: String
    )

    /**
     * For this to run:
     * - The full-spring module's web application needs to be running.
     */
    @Test
    @DisplayName("should respond with the input string uppercased")
    fun exampleUppercaseWebCall() {
        val payload = "EXAMPLE lower case INPUT"
        val actualResponse = remoteFunctionClient.call(
            WebServerClientInput<String, String>(
                payload = payload,
                method = HttpMethod.POST,
                uri = "/uppercase"
            )
        )

        val expectedResponse = payload.toUpperCase()
        assertEquals(
            expectedResponse,
            actualResponse,
            "The input should be uppercased after calling the uppercase function."
        )
    }

    /**
     * For this to run:
     * - The full-spring module's web application needs to be running.
     */
    @Test
    @DisplayName("should respond with the input string uppercased")
    fun exampleUppercaseJsonWebCall() {
        val payload = UpperCaseInput(
            value = "EXAMPLE lower case INPUT"
        )
        val actualResponse = remoteFunctionClient.call(
            WebServerClientInput<UpperCaseInput, UppercaseOutput>(
                payload = payload,
                method = HttpMethod.POST,
                uri = "/uppercaseJson"
            )
        )

        val expectedResponse = UppercaseOutput(
            value = payload.value.toUpperCase()
        )
        assertEquals(
            expectedResponse,
            actualResponse,
            "The input should be uppercased after calling the uppercase function."
        )
    }

    /**
     * For this to run:
     * - The full-spring-lambda-aws.jar will need to be deployed into the `this-is-only-a-test` lambda in the POC
     *   account.
     * - The SPRING_CLOUD_FUNCTION_DEFINITION environment variable should be set to 'uppercase'.
     * - You will need to have Deep 6 AI AWS credentials set up on your system under `/Users/<your-user>>/.aws` for the
     *   `deep6-dev` account.
     * - You will then need to assume role into the POC account, with a command like:
     *   `aws sts assume-role --role-arn arn:aws:iam::002400528852:role/Deep6DevAdmin --role-session-name "some-name"`
     * - Then replace `AccessKeyId`, `SecretAccessKey`, and `SessionToken` with the values returned by the call.
     * - You should then be able to run this test.
     */
    @Test
    @DisplayName("should respond with the input string uppercased")
    fun exampleUppercaseAwsLambdaCall() {
        val input = "example lower case input"
        val actualResponse = remoteFunctionClient.call(
            AwsLambdaClientInput<String, String>(
                payload = "example lower case input",
                functionName = "this-is-only-a-test",
                region = Region.US_EAST_1,
                credentials = AwsSessionCredentials.create(
                    "AccessKeyId",
                    "SecretAccessKey",
                    "SessionToken"
                )
            )
        )

        val expectedResponse = input.toUpperCase()
        assertEquals(
            expectedResponse,
            actualResponse,
            "The input should be uppercased after calling the uppercase function."
        )
    }

    /**
     * For this to run:
     * - The full-spring-lambda-aws.jar will need to be deployed into the `this-is-only-a-test` lambda in the POC
     *   account.
     * - The SPRING_CLOUD_FUNCTION_DEFINITION environment variable should be set to 'uppercaseJson'.
     * - You will need to have Deep 6 AI AWS credentials set up on your system under `/Users/<your-user>>/.aws` for the
     *   `deep6-dev` account.
     * - You will then need to assume role into the POC account, with a command like:
     *   `aws sts assume-role --role-arn arn:aws:iam::002400528852:role/Deep6DevAdmin --role-session-name "some-name"`
     * - Then replace `AccessKeyId`, `SecretAccessKey`, and `SessionToken` with the values returned by the call.
     * - You should then be able to run this test.
     */
    @Test
    @DisplayName("should respond with the input string uppercased")
    fun exampleUppercaseJsonAwsLambdaCall() {
        val payload = UpperCaseInput(
            value = "EXAMPLE lower case INPUT"
        )
        val actualResponse = remoteFunctionClient.call(
            AwsLambdaClientInput<UpperCaseInput, UppercaseOutput>(
                payload = payload,
                functionName = "this-is-only-a-test",
                region = Region.US_EAST_1,
                credentials = AwsSessionCredentials.create(
                    "AccessKeyId",
                    "SecretAccessKey",
                    "SessionToken"
                )
            )
        )

        val expectedResponse = UppercaseOutput(
            value = payload.value.toUpperCase()
        )
        assertEquals(
            expectedResponse,
            actualResponse,
            "The input should be uppercased after calling the uppercase function."
        )
    }
}
