package org.example.client.exception

import org.springframework.http.HttpMethod
import software.amazon.awssdk.regions.Region
import java.lang.RuntimeException

sealed class D6RemoteClientException(
    message: String,
    cause: Throwable?
) : RuntimeException(message, cause) {

    class RemoteClientException(
        message: String = "Failed to call the remote function.",
        cause: Throwable? = null
    ) : D6RemoteClientException(message, cause)

    class WebServerException(
        method: HttpMethod,
        uri: String,
        payload: Any?,
        message: String = """
            Failed to call the function deployed in a web server.
            URI: '$uri' 
            Method: '$method'
            Payload: $payload
        """.trimIndent(),
        cause: Throwable? = null
    ) : D6RemoteClientException(message, cause)

    class AwsLambdaException(
        functionName: String,
        region: Region,
        payload: Any?,
        message: String = """
            Failed to call the function deployed as an AWS Lambda.
            Function Name: '$functionName'
            AWS Region: '$region' 
            Payload: $payload
        """.trimIndent(),
        cause: Throwable? = null
    ) : D6RemoteClientException(message, cause)
}
