# Purpose
Any AWS specific code lives here. 

For instance if we cannot use the default AWS handler provided by Spring 
(org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest) we could add our own here and then 
reference it in AWS lambda.
