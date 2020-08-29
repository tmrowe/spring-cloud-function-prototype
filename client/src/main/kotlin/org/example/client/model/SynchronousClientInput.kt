package org.example.client.model

import org.springframework.http.HttpMethod
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.regions.Region

// TODO: Add Documentation.
sealed class SynchronousClientInput<InputType, out ReturnType> {
    abstract val payload: InputType?

    data class HttpClientInput<InputType, out ReturnType>(
        override val payload: InputType?,
        val method: HttpMethod,
        val uri: String
    ) : SynchronousClientInput<InputType, ReturnType>()

    data class AwsLambdaClientInput<InputType, out ReturnType>(
        override val payload: InputType?,
        val functionName: String,
        val region: Region,
        val credentials: AwsCredentials
    ): SynchronousClientInput<InputType, ReturnType>()
}
