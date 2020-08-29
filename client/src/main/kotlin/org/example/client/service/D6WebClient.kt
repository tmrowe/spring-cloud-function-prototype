package org.example.client.service

import org.example.client.exception.D6RemoteClientException.WebCallException
import org.example.client.model.SynchronousClientInput.HttpClientInput
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec

// TODO: Add documentation.
class D6WebClient(
    private val webClient: WebClient
) {

    fun <InputType, ReturnType> call(
        input: HttpClientInput<InputType, ReturnType>,
        returnType: Class<out ReturnType>
    ): ReturnType {
        val request = if(input.payload != null)
            withBody(input)
        else
            withoutBody(input)

        return request
            .retrieve()
            .bodyToMono(returnType)
            .block()
            ?: throw WebCallException(input.uri)
    }

    private fun <InputType, ReturnType> withBody(
        input: HttpClientInput<InputType, ReturnType>
    ): RequestHeadersSpec<*> {
        return webClient
            .method(input.method)
            .uri(input.uri)
            .body(BodyInserters.fromValue(input.payload!!))
    }

    private fun <InputType, ReturnType> withoutBody(
        input: HttpClientInput<InputType, ReturnType>
    ): RequestBodySpec {
        return webClient
            .method(input.method)
            .uri(input.uri)
    }
}