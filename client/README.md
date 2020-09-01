# D6 Remote Client
This library provides a mechanism for calling a remote function deployed either in AWS Lambda or within a web server 
(for local development).

## Using The Library
Projects wishing to use this library should import and construct an instance of 
`org.example.client.D6RemoteFunctionClient`.
 
Then they should construct either an input object dependent on which deployment they wish to call.
- For AWS Lambda: `org.example.client.model.SynchronousClientInput.AwsLambdaClientInput`
- For a local web server: `org.example.client.model.SynchronousClientInput.WebServerClientInput`
  
The top-level client in this library exposes a call function that takes two type arguments and one of the above inputs.
In the form `D6RemoteFunctionClient#call<String, String>(input)`. Where the first argument specifies the type of the 
payload provided in the input that will be passed to the remote function. The second specifying the type that will
be returned by the remote function. So a `String` input and output in this example.

See `.../client/src/test/kotlin/org/example/client/ExampleClientUsage.kt` for examples of library usage both against 
deployed AWS Lambdas and a local webserver. 
