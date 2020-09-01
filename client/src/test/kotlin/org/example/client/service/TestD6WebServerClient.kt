package org.example.client.service

import com.nhaarman.mockitokotlin2.*
import org.example.client.exception.D6RemoteClientException.WebServerException
import org.example.client.model.SynchronousClientInput.WebServerClientInput
import org.junit.jupiter.api.*
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec
import reactor.core.publisher.Mono
import java.lang.RuntimeException

class TestD6WebServerClient {

    private val mockResponse = mock<ResponseSpec>()
    private val mockRequestBodyUriSpec = mock<RequestBodyUriSpec>().also{ requestBodyUriSpec ->
        whenever(requestBodyUriSpec.uri(any<String>())).thenReturn(requestBodyUriSpec)
        whenever(requestBodyUriSpec.retrieve()).thenReturn(mockResponse)
    }
    private val mockWebClient = mock<WebClient>().also {
        whenever(it.method(any())).thenReturn(mockRequestBodyUriSpec)
    }
    private val webServerClient = D6WebServerClient(
        webClient = mockWebClient
    )

    private val webServerClientInput = WebServerClientInput<String, String>(
        payload = "some example payload",
        method = HttpMethod.POST,
        uri = "/some-endpoint"
    )

    @BeforeEach
    fun before() {
        whenever(mockResponse.bodyToMono(any<Class<*>>()))
            .thenReturn(Mono.just("foo"))
    }

    @Nested
    @DisplayName("Simple Input & Output")
    inner class SimpleInputOutput {

        @Test
        @DisplayName("should return the response from the Web Client")
        fun returnsExpectedResult() {
            val response = webServerClient.call(webServerClientInput, String::class.java)
            Assertions.assertEquals("foo", response)
        }
    }

    @Nested
    @DisplayName("Complex Input & Output")
    inner class ComplexInputOutput {

        private val complexWebServerClientInput = WebServerClientInput<ExampleInput, ExampleOutput>(
            payload = ExampleInput(value = "some example payload"),
            method = HttpMethod.POST,
            uri = "/some-endpoint"
        )

        @Test
        @DisplayName("should return the response from the Web Client")
        fun returnsExpectedResult() {
            val webClientResponse = ExampleOutput(value = "some example payload")

            whenever(mockResponse.bodyToMono(any<Class<*>>()))
                .thenReturn(Mono.just(webClientResponse))

            val response = webServerClient.call(complexWebServerClientInput, ExampleOutput::class.java)
            Assertions.assertEquals(webClientResponse, response)
        }
    }

    @Nested
    @DisplayName("Web Client Usage")
    inner class WebClientUsage {

        @Test
        @DisplayName("Should pass to the web client the HTTP method given in the input")
        fun correctMethod() {
            webServerClient.call(webServerClientInput, String::class.java)
            verify(mockWebClient).method(webServerClientInput.method)
        }

        @Test
        @DisplayName("Should pass to the web client the URI given in the input")
        fun correctUri() {
            webServerClient.call(webServerClientInput, String::class.java)
            verify(mockRequestBodyUriSpec).uri(webServerClientInput.uri)
        }

        @Nested
        @DisplayName("Null Payload")
        inner class NullPayload {

            @Test
            @DisplayName("Should pass to the web client the payload given in the input if it is non-null")
            fun handleNonNullBody() {
                webServerClient.call(webServerClientInput, String::class.java)
                verify(mockRequestBodyUriSpec).body(any())
            }

            @Test
            @DisplayName("Should not pass to the web client the payload given in the input if it is null")
            fun handleNullBody() {
                val webServerClientInputWithNullPayload = webServerClientInput.copy(payload = null)
                webServerClient.call(webServerClientInputWithNullPayload, String::class.java)

                verify(mockRequestBodyUriSpec, times(0)).body(any())
            }
        }
    }

    @Nested
    @DisplayName("Error Handling")
    inner class ErrorHandling {

        @Test
        @DisplayName("should throw wrapped exception if the WebClient throws")
        fun awsLambdaClientFactoryErrorHandling() {
            whenever(mockWebClient.method(any())).thenThrow(RuntimeException::class.java)
            assertThrows<WebServerException> {
                webServerClient.call(webServerClientInput, String::class.java)
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
