package org.example.client.exception

import java.lang.RuntimeException

sealed class D6RemoteClientException(
    message: String,
    cause: Throwable?
) : RuntimeException(message, cause) {

    class WebCallException(
        endpoint: String,
        message: String = "Failed to call the '$endpoint' endpoint.",
        cause: Throwable? = null
    ) : D6RemoteClientException(message, cause)
}
