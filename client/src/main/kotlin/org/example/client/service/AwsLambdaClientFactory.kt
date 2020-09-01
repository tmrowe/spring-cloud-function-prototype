package org.example.client.service

import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.lambda.LambdaClient

class AwsLambdaClientFactory {

    /**
     * Build a single AWS [LambdaClient] with the pointing at the given [region] and using the given [credentials].
     *
     * @param region The AWS region the target lambda is deployed in.
     * @param credentials The [AwsCredentials] that will be used to invoke the lambda.
     * @return An AWS [LambdaClient] constructed with the given [region] and using the given [credentials].
     */
    fun build(
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
