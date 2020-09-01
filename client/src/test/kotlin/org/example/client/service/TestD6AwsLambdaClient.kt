package org.example.client.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.nhaarman.mockitokotlin2.*
import org.example.client.exception.D6RemoteClientException.AwsLambdaException
import org.example.client.model.SynchronousClientInput.AwsLambdaClientInput
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.lambda.LambdaClient
import software.amazon.awssdk.services.lambda.model.InvokeRequest
import software.amazon.awssdk.services.lambda.model.InvokeResponse
import org.junit.jupiter.api.assertThrows
import software.amazon.awssdk.services.lambda.model.LambdaException
import java.lang.RuntimeException

class TestD6AwsLambdaClient {

    private val mockLambdaClient = mock<LambdaClient>()
    private val mockAwsLambdaClientFactory = mock<AwsLambdaClientFactory>().also {
        whenever(it.build(any(), any())).thenReturn(mockLambdaClient)
    }

    private val objectMapper = Jackson2ObjectMapperBuilder()
        .modules(KotlinModule())
        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .build() as ObjectMapper

    private val awsLambdaClient = D6AwsLambdaClient(
        awsLambdaClientFactory = mockAwsLambdaClientFactory,
        objectMapper = objectMapper
    )

    private val awsLambdaClientInput = AwsLambdaClientInput<String, String>(
        payload = "some example payload",
        functionName = "some-aws-lambda-function-name",
        region = Region.US_EAST_1,
        credentials = mock()
    )

    @BeforeEach
    fun before() {
        whenever(mockLambdaClient.invoke(any<InvokeRequest>()))
            .thenReturn(
                InvokeResponse
                    .builder()
                    .payload(SdkBytes.fromUtf8String("\"foo\""))
                    .build()
            )
    }

    @Nested
    @DisplayName("Simple Input & Output")
    inner class SimpleInputOutput {

        @Test
        @DisplayName("should deserialize the response from the Lambda Client")
        fun returnsExpectedResult() {
            val response = awsLambdaClient.call(awsLambdaClientInput, String::class.java)
            assertEquals("foo", response)
        }
    }

    @Nested
    @DisplayName("Complex Input & Output")
    inner class ComplexInputOutput {

        private val complexAwsLambdaClientInput = AwsLambdaClientInput<ExampleInput, ExampleOutput>(
            payload = ExampleInput(value = "some example payload"),
            functionName = "some-aws-lambda-function-name",
            region = Region.US_EAST_1,
            credentials = mock()
        )

        @Test
        @DisplayName("should deserialize the response from the Lambda Client")
        fun returnsExpectedResult() {
            whenever(mockLambdaClient.invoke(any<InvokeRequest>()))
                .thenReturn(
                    InvokeResponse
                        .builder()
                        .payload(SdkBytes.fromUtf8String("{\"value\": \"some example payload\"}"
                        .trimIndent()))
                        .build()
                )

            val response = awsLambdaClient.call(complexAwsLambdaClientInput, ExampleOutput::class.java)
            assertEquals(
                ExampleOutput("some example payload"),
                response
            )
        }
    }

    @Nested
    @DisplayName("Null Payload")
    inner class NullPayload {

        @Test
        @DisplayName("should set the payload of the InvokeRequest if an input with a non-null payload is passed")
        fun payloadShouldNotBeNull() {
            awsLambdaClient.call(awsLambdaClientInput, String::class.java)
            verify(mockLambdaClient).invoke(check<InvokeRequest> {
                assertNotNull(it.payload())
            })
        }

        @Test
        @DisplayName("should not set the payload of the InvokeRequest if an input with a null payload is passed")
        fun payloadShouldBeNull() {
            val awsLambdaClientInputWithNullPayload = awsLambdaClientInput.copy(payload = null)
            awsLambdaClient.call(awsLambdaClientInputWithNullPayload, String::class.java)

            verify(mockLambdaClient).invoke(check<InvokeRequest> {
                assertNull(it.payload())
            })
        }
    }

    @Nested
    @DisplayName("AWS Client Construction")
    inner class AwsClientConstruction {

        @Test
        @DisplayName("Should build a LambdaClient with the given region and credentials")
        fun buildAwsClient() {
            awsLambdaClient.call(awsLambdaClientInput, String::class.java)
            verify(mockAwsLambdaClientFactory).build(
                region = awsLambdaClientInput.region,
                credentials = awsLambdaClientInput.credentials
            )
        }

        @Test
        @DisplayName("Should build a new LambdaClient each time the client is called")
        fun newAwsClientForEachCall() {
            awsLambdaClient.call(awsLambdaClientInput, String::class.java)
            awsLambdaClient.call(awsLambdaClientInput, String::class.java)
            awsLambdaClient.call(awsLambdaClientInput, String::class.java)

            verify(mockAwsLambdaClientFactory, times(3)).build(any(), any())
        }
    }

    @Nested
    @DisplayName("Error Handling")
    inner class ErrorHandling {

        @Test
        @DisplayName("should throw wrapped exception if the AwsLambdaClientFactory throws")
        fun awsLambdaClientFactoryErrorHandling() {
            whenever(mockAwsLambdaClientFactory.build(any(), any()))
                .thenThrow(RuntimeException::class.java)

            assertThrows<AwsLambdaException> {
                awsLambdaClient.call(awsLambdaClientInput, String::class.java)
            }
        }

        @Test
        @DisplayName("should throw wrapped exception if the LambdaClient throws")
        fun lambdaClientErrorHandling() {
            whenever(mockLambdaClient.invoke(any<InvokeRequest>()))
                .thenThrow(LambdaException::class.java)

            assertThrows<AwsLambdaException> {
                awsLambdaClient.call(awsLambdaClientInput, Int::class.java)
            }
        }

        @Test
        @DisplayName("should throw wrapped exception if the Jackson ObjectMapper throws")
        fun jacksonErrorHandling() {
            whenever(mockLambdaClient.invoke(any<InvokeRequest>()))
                .thenReturn(null)

            assertThrows<AwsLambdaException> {
                awsLambdaClient.call(awsLambdaClientInput, String::class.java)
            }
        }
    }

    data class ExampleInput(
        val value: String
    )

    data class ExampleOutput(
        val value: String
    )
}