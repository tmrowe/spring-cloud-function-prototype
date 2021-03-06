package org.example.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.client.exception.D6RemoteClientException.RemoteClientException
import org.example.client.model.SynchronousClientInput
import org.example.client.model.SynchronousClientInput.AwsLambdaClientInput
import org.example.client.model.SynchronousClientInput.WebServerClientInput
import org.example.client.service.AwsLambdaClientFactory
import org.example.client.service.D6AwsLambdaClient
import org.example.client.service.D6WebServerClient
import org.springframework.web.reactive.function.client.WebClient

/**
 * A client that abstracts away the requirements of calling our synchronous remote functions.
 *
 * The functions can be deployed and invoked either in AWS Lambda or in a web server for local development.
 *
 * See the [SynchronousClientInput] for supported input types for this library.
 *
 * @property mapper An instance of Jackson [ObjectMapper] used for serializing user input to the structure required by
 * AWS, and the response to the given ReturnType.
 * @property webClient An instance of the Spring [WebClient] that will be used to call functions deployed in a web
 * server.
 */
class D6RemoteFunctionClient(
    private val mapper: ObjectMapper,
    private val webClient: WebClient,
) {

    val internalWebServerClient = D6WebServerClient(
        webClient = webClient
    )

    val internalAwsLambdaClient = D6AwsLambdaClient(
        awsLambdaClientFactory = AwsLambdaClientFactory(),
        objectMapper = mapper
    )

    inline fun <reified InputType, reified ReturnType> call(
        input: SynchronousClientInput<InputType, ReturnType>
    ): ReturnType {
        println("Attempting to call remote function with input type '${input::class}'.")

        val response = try {
            when(input) {
                is WebServerClientInput -> internalWebServerClient.call(input, ReturnType::class.java)
                is AwsLambdaClientInput -> internalAwsLambdaClient.call(input, ReturnType::class.java)
            }
        } catch (exception: Exception) {
            throw RemoteClientException(
                cause = exception
            )
        }

        println("Successfully called remote function. Returning response:")
        println(response)
        return response
    }
}
