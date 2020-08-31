package org.example.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.example.client.exception.D6RemoteClientException.*
import org.example.client.model.SynchronousClientInput.AwsLambdaClientInput
import org.example.client.model.SynchronousClientInput.WebServerClientInput
import org.example.client.service.D6AwsLambdaClient
import org.example.client.service.D6WebServerClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.reactive.function.client.WebClient

class TestD6RemoteFunctionClient {
//te
    private val mockWebClient = mock<WebClient>()
    private val mockObjectMapper = mock<ObjectMapper>()
    private val mockWebServerClient = mock<D6WebServerClient>()
    private val mockAwsLambdaClient = mock<D6AwsLambdaClient>()

    private val remoteFunctionClient = D6RemoteFunctionClient(
        webClient = mockWebClient,
        mapper = mockObjectMapper
    )

    init {
        // Replace the instance of the D6WebServerClient built by D6RemoteFunctionClient with a mock.
        val internalWebServerClient = remoteFunctionClient.javaClass.getDeclaredField("internalWebServerClient")
        internalWebServerClient.isAccessible = true
        internalWebServerClient.set(remoteFunctionClient, mockWebServerClient)

        // Replace the instance of the D6AwsLambdaClient built by D6RemoteFunctionClient with a mock.
        val internalAwsLambdaClient = remoteFunctionClient.javaClass.getDeclaredField("internalAwsLambdaClient")
        internalAwsLambdaClient.isAccessible = true
        internalAwsLambdaClient.set(remoteFunctionClient, mockAwsLambdaClient)
    }

    @Nested
    @DisplayName("AWS Lambda Client")
    inner class AwsLambdaClient {

        private val mockAwsLambdaClientInput = mock<AwsLambdaClientInput<String, String>>()

        @Test
        @DisplayName("should call the WebServerClient when passed a WebServerClientInput")
        fun callsCorrectClient() {
            remoteFunctionClient.call(mockAwsLambdaClientInput)
            verify(mockAwsLambdaClient).call(mockAwsLambdaClientInput, String::class.java)
        }

        @Test
        @DisplayName("should return the value returned by the client")
        fun correctReturn() {
            whenever(mockAwsLambdaClient.call<String, String>(any(), any()))
                .thenReturn("foo")

            val response = remoteFunctionClient.call(mockAwsLambdaClientInput)
            assertEquals("foo", response)
            assertEquals(response::class, String::class)
        }

        @Test
        @DisplayName("should throw expected exception if the internal client throws")
        fun errorHandling() {
            whenever(mockAwsLambdaClient.call<String, String>(any(), any()))
                .thenThrow(WebServerException::class.java)

            assertThrows<RemoteClientException> {
                remoteFunctionClient.call(mockAwsLambdaClientInput)
            }
        }
    }

    @Nested
    @DisplayName("Web Server Client")
    inner class WebServerClient {

        private val mockWebServerClientInput = mock<WebServerClientInput<String, String>>()

        @Test
        @DisplayName("should call the WebServerClient when passed a WebServerClientInput")
        fun callsCorrectClient() {
            remoteFunctionClient.call(mockWebServerClientInput)
            verify(mockWebServerClient).call(mockWebServerClientInput, String::class.java)
        }

        @Test
        @DisplayName("should return the value returned by the client")
        fun correctReturn() {
            whenever(mockWebServerClient.call<String, String>(any(), any()))
                .thenReturn("foo")

            val response = remoteFunctionClient.call(mockWebServerClientInput)
            assertEquals("foo", response)
            assertEquals(response::class, String::class)
        }

        @Test
        @DisplayName("should throw expected exception if the internal client throws")
        fun errorHandling() {
            whenever(mockWebServerClient.call<String, String>(any(), any()))
                .thenThrow(AwsLambdaException::class.java)

            assertThrows<RemoteClientException> {
                remoteFunctionClient.call(mockWebServerClientInput)
            }
        }
    }
}
