# Spring Cloud Function Prototype
This prototype demonstrates basic usage of Spring Cloud Function.

## TODO:
- Update READMEs. 
- Add client documentation.

## Prerequisites
The following need to be installed on your system to run this project:

- [Java Development Kit 8](https://www.oracle.com/technetwork/java/javase/downloads/index.html)
- [Maven](https://maven.apache.org/download.cgi)

## Running The Project
Each of the functions defined in this project can be deployed out to a cloud provider (such as AWS) or run locally in a
web server for local development and isolated testing.

### Local Run
You can now run the API by running the main class under `org.example.Application`.

You can then call each of the functions with an HTTP Client (such as Curl or Postman). The endpoints will all be hosted
on `http://localhost:9101/` with the path being the functions name. I.e:
```
POST http://localhost:9101/uppercase
POST http://localhost:9101/uppercaseJson
POST http://localhost:9101/lowercase
POST http://localhost:9101/print
GET http://localhost:9101/random
GET http://localhost:9101/getVariable
```

### AWS Deployment
-- TODO

## Description
This project defines serverless functions that can be deployed in a variety of ways.

### Function Types
This project deals with three types of function:

#### Supplier
A supplier produces some output that can be read by other processes.

Example Kotlin Signature: `fun example(): () -> Int { ... }`

#### Consumer
A consumer reads some input that it then processes but produces no output.

Example Kotlin Signature: `fun example(): (String) -> Unit { ... }`

#### Function
A function reads some input, processes that input, and then returns some output. 

Example Kotlin Signature: `fun example(): (String) -> String { ... }`
