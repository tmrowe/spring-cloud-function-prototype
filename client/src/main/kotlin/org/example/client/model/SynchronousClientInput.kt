package org.example.client.model

import org.springframework.http.HttpMethod
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.regions.Region

/**
 * Data models encapsulation the data required to call our remote functions.
 * - [AwsLambdaClientInput] encapsulates the necessary information to call a remote function deployed in AWS Lambda.
 * - [WebServerClientInput] encapsulates the necessary information to call a remote function deployed in a web server.
 *
 * @param InputType The type of the payload to pass to the remote function.
 * @param ReturnType The type returned by the remote function.
 */
sealed class SynchronousClientInput<InputType, out ReturnType> {
    abstract val payload: InputType?

    data class AwsLambdaClientInput<InputType, out ReturnType>(
        override val payload: InputType?,
        val functionName: String,
        val region: Region,
        val credentials: AwsCredentials
    ): SynchronousClientInput<InputType, ReturnType>()

    data class WebServerClientInput<InputType, out ReturnType>(
        override val payload: InputType?,
        val method: HttpMethod,
        val uri: String
    ) : SynchronousClientInput<InputType, ReturnType>()
}
