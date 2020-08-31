package org.example.client.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.client.exception.D6RemoteClientException.AwsLambdaException
import org.example.client.model.SynchronousClientInput.AwsLambdaClientInput
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.lambda.LambdaClient
import software.amazon.awssdk.services.lambda.model.InvokeRequest

/**
 * Internal library client that constructs an AWS Client and calls one of our functions deployed in an AWS lambda.
 *
 * @property mapper An instance of Jackson [ObjectMapper] passed in by the caller for serializing the user input to
 * the structure required by AWS, and the response to the given ReturnType.
 */
class D6AwsLambdaClient(
    private val mapper: ObjectMapper
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
            val response = buildAwsClient(
                region = input.region,
                credentials = input.credentials
            ).invoke(
                InvokeRequest
                    .builder()
                    .functionName(input.functionName)
                    .apply {
                        input.payload ?.let { payload ->
                            val awsPayload = SdkBytes.fromUtf8String(mapper.writeValueAsString(payload))
                            payload(awsPayload)
                        }
                    }
                    .build()
            )

            mapper.readValue(response.payload().asUtf8String(), returnType)
        } catch(exception: Exception) {
            throw AwsLambdaException(
                functionName = input.functionName,
                region = input.region,
                payload = input.payload
            )
        }
    }

    /**
     * Build a single AWS [LambdaClient] with the pointing at the given [region] and using the given [credentials].
     *
     * @param region The AWS region the target lambda is deployed in.
     * @param credentials The [AwsCredentials] that will be used to invoke the lambda.
     * @return An AWS [LambdaClient] constructed with the given [region] and using the given [credentials].
     */
    private fun buildAwsClient(
        region: Region,
        credentials: AwsCredentials
    ): LambdaClient {
        return LambdaClient.builder()
            .region(region)
            .credentialsProvider {
                credentials
            }
            .build()
    }
}
