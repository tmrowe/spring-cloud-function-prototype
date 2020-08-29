package org.example.client.service

import org.example.client.exception.D6RemoteClientException.WebCallException
import org.example.client.model.SynchronousClientInput.HttpClientInput
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

// TODO: Add documentation.
class D6WebClient(
    private val webClient: WebClient
) {

    fun <InputType, ReturnType> call(
        input: HttpClientInput<InputType, ReturnType>,
        returnType: Class<out ReturnType>
    ): ReturnType {
        return webClient
            .method(input.method)
            .uri(input.uri)
            .apply {
                input.payload?.let {
                    body(BodyInserters.fromValue(input.payload))
                }
            }
            .retrieve()
            .bodyToMono(returnType)
            .block()
            ?: throw WebCallException(input.uri)
    }
}