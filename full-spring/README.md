# Spring Cloud Function Prototype
This prototype demonstrates basic usage of Spring Cloud Function.

## Module Specifics
This module demonstrates using Spring Cloud Function out of the box with no significant modification.

### Limitations
I don't know how we can define the functions outside of the Application class due to : 
```
@Bean method KotlinLambdaToFunctionAutoConfiguration.kotlinToFunctionTransformer is non-static and returns an object 
assignable to Spring's BeanFactoryPostProcessor interface. This will result in a failure to process annotations such as 
@Autowired, @Resource and @PostConstruct within the method's declaring @Configuration class. Add the 'static' modifier 
to this method to avoid these container lifecycle issues; see @Bean javadoc for complete details.
```
