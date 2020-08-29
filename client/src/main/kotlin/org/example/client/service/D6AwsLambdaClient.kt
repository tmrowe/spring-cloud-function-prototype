package org.example.client.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.client.model.SynchronousClientInput.AwsLambdaClientInput
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.lambda.LambdaClient
import software.amazon.awssdk.services.lambda.model.InvokeRequest

// TODO: Add Documentation.
class D6AwsLambdaClient(
    private val mapper: ObjectMapper
) {

    fun <InputType, ReturnType> call(
        input: AwsLambdaClientInput<InputType, ReturnType>,
        returnType: Class<out ReturnType>
    ): ReturnType {
        val awsClient = buildAwsClient(
            region = input.region,
            credentials = input.credentials
        )

        val payload = SdkBytes.fromUtf8String(mapper.writeValueAsString(input.payload))

        val request = InvokeRequest
            .builder()
            .apply {
                payload ?.let {
                    payload(it)
                }
            }
            .functionName(input.functionName)
            .build()

        val response = awsClient.invoke(request)

        return mapper.readValue(response.payload().asUtf8String(), returnType)
    }

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