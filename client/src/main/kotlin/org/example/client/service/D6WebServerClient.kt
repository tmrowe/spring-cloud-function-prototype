package org.example.client.service

import org.example.client.exception.D6RemoteClientException.WebServerException
import org.example.client.model.SynchronousClientInput.WebServerClientInput
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

/**
 * Internal library client that uses the provided [WebClient] to call one of our remote functions deployed in a web
 * server. Intended for local development.
 *
 * @property webClient An instance of the Spring WebClient passed in by the caller.
 */
class D6WebServerClient(
    private val webClient: WebClient
) {

    /**
     * Makes a single synchronous call to one of our functions deployed in a web server.
     *
     * @param InputType The type of the caller's input.
     * @param ReturnType The type that of the object that will be returned by this call.
     * @param input An instance of [WebServerClientInput] containing the details required to call the targeted function.
     * @param returnType A [Class] wrapping the [ReturnType] that this function will return.
     * @return The response returned by calling the AWS Lambda.
     */
    fun <InputType, ReturnType> call(
        input: WebServerClientInput<InputType, ReturnType>,
        returnType: Class<out ReturnType>
    ): ReturnType {
        println("Attempting to call remote function running in local web server. Call details:")
        println("""
            - Method: ${input.method}
            - URI: ${input.uri}
            - Payload: ${input.payload}
        """.trimIndent())

        return try {
            webClient
                .method(input.method)
                .uri(input.uri)
                .apply {
                    input.payload?.let {
                        body(BodyInserters.fromValue(input.payload))
                    }
                }
                .retrieve()
                .bodyToMono(returnType)
                .block()!!
        } catch(exception: Exception) {
            throw WebServerException(
                input.method,
                input.uri,
                input.payload
            )
        }
    }
}
