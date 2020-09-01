package org.example.client.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.client.exception.D6RemoteClientException.AwsLambdaException
import org.example.client.model.SynchronousClientInput.AwsLambdaClientInput
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.lambda.LambdaClient
import software.amazon.awssdk.services.lambda.model.InvokeRequest

/**
 * Internal library client that constructs an AWS Client and calls one of our functions deployed in an AWS lambda.
 *
 * @property awsLambdaClientFactory A factory capable of constructing instances of [LambdaClient].
 * @property objectMapper An instance of Jackson [ObjectMapper] passed in by the caller for serializing the user input
 * to the structure required by AWS, and the response to the given ReturnType.
 */
class D6AwsLambdaClient(
    private val awsLambdaClientFactory: AwsLambdaClientFactory,
    private val objectMapper: ObjectMapper
) {

    /**
     * Makes a single synchronous call to one of our functions deployed as an AWS Lambda.
     *
     * @param InputType The type of the caller's input.
     * @param ReturnType The type that of the object that will be returned by this call.
     * @param input An instance of [AwsLambdaClientInput] containing the details required to call the targeted AWS
     * lambda.
     * @param returnType A [Class] wrapping the [ReturnType] that this function will return.
     * @return The response returned by calling the AWS Lambda.
     */
    fun <InputType, ReturnType> call(
        input: AwsLambdaClientInput<InputType, ReturnType>,
        returnType: Class<out ReturnType>
    ): ReturnType {
        println("Attempting to call remote function running on AWS Lambda. Call details:")
        println("""
            - Function Name: ${input.functionName}
            - AWS Region: ${input.region}
            - Payload: ${input.payload}
        """.trimIndent())

        return try {
            val awsClient = awsLambdaClientFactory.build(
                region = input.region,
                credentials = input.credentials
            )

            val response = awsClient.invoke(
                InvokeRequest
                    .builder()
                    .functionName(input.functionName)
                    .apply {
                        input.payload ?.let { payload ->
                            val awsPayload = SdkBytes.fromUtf8String(objectMapper.writeValueAsString(payload))
                            payload(awsPayload)
                        }
                    }
                    .build()
            )

            objectMapper.readValue(response.payload().asUtf8String(), returnType)
        } catch(exception: Exception) {
            throw AwsLambdaException(
                functionName = input.functionName,
                region = input.region,
                payload = input.payload,
                cause = exception
            )
        }
    }
}
