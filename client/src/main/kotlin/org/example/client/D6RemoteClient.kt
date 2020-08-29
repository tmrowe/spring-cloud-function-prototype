package org.example.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.client.model.SynchronousClientInput
import org.example.client.model.SynchronousClientInput.AwsLambdaClientInput
import org.example.client.model.SynchronousClientInput.HttpClientInput
import org.example.client.service.D6AwsLambdaClient
import org.example.client.service.D6WebClient
import org.springframework.web.reactive.function.client.WebClient

/**
 * TODO: Document
 * Wrapper around ??? that makes HTTP calls to our lambda code deployed as web servers.
 */
class D6RemoteClient(
    private val mapper: ObjectMapper,
    private val webClient: WebClient,
) {

    val internalWebClient = D6WebClient(
        webClient = webClient
    )

    val internalAwsLambdaClient = D6AwsLambdaClient(
        mapper = mapper
    )

    // TODO: Add logging for these calls.
    inline fun <reified InputType, reified ReturnType> call(
        input: SynchronousClientInput<InputType, ReturnType>
    ): ReturnType {
        return when(input) {
            is HttpClientInput -> internalWebClient.call(input, ReturnType::class.java)
            is AwsLambdaClientInput -> internalAwsLambdaClient.call(input, ReturnType::class.java)
        }
    }
}
