# Kodein-sample-testing [![Build Status](https://api.travis-ci.org/Karumi/kodein-sample-testing.svg?branch=master)]
(https://travis-ci.org/Karumi/kodein-sample-testing)

This repository aims to be a small example of how to use Kodein to provide different implementations for production code and testing code

##Description

The idea is to use Kodein replacing part of our production code using test doubles during the instrumentation tests execution. This allow us to write instrumentation tests easily.

For writing those tests we are going to use a property that Kodein give us, the ability to override dependencies in a graph.

As Android does not give us the possibility to provide dependencies by constructor, we need to use Kodein to replace those dependencies. This is the main issue we fix using "override dependencies" Kodein feature.

In this example we can find a simple Activity with two dependencies. The first dependency is just related to the Activity execution context. Having dependencies that only live during the activity lifecycle, like a Presenter, a controller, or any visual dependency is quite common. You can find it in ActivityScopeClass.kt. The second dependency is an Application scope dependency. This dependency lifecycle is linked to the Application lifecycle and not to the Activity lifecycle as the first dependency does. You can find this example in the class ApplicationScopeClass.kt. This class is defined as a Singleton just to show that we can override singletons provision using any type of test dobules.

We wrote a simple test class using Espresso to check if the text shown to the user is the one obtained from the production code dependency or our test double.

You can find it in ```MainActivityTest.kt```. If you check one of the tests you can discover how we are using Kodein to override the dependencies.

```java
@Mock private lateinit var applicationScopeClass: ApplicationScopeClass

@Test
    fun shouldShowInjectedNameProvidedByTheDomainWhenReplaceByAMock() {
        whenever(applicationScopeClass.getText()).thenReturn("Mock Name")
        startActivity()

        onView(withText("Mock Name")).check(matches(isDisplayed()))
    }
```

We are creating a  Mock (we are using Mockito to provide test doubles, this is not mandatory, you 
could use your own test doubles, or using other mocking frameworks). We are replacing the real call
 to getText() for a mock value called 'Mock Name". We launch the activity and we use Expresso to verify that the mocked test has been displayed on the screen.

For this test works perfectly we need to use Kodein to some magic happens. If you continue reading the class at the end of the file you can find the Kodein provisioning dependencies.

```java
    val testDependencies = Module(allowSilentOverride = true) {
        bind<ApplicationScopeClass>() with instance(applicationScopeClass)
        bind<ActivityScopeClass>() with instance(activityScopeClass)
    }
```

we create a Module that allows overriding the graph, and we create provisioning for those classes. Depends on your objective you can use instance or provision method. In our case, we used the instance because we are initing Mockito in the test environment setup.

The final part overrides the production graph for the testing ones if you read the setup method you can find how we are doing that.

```java
   @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val app = InstrumentationRegistry.getInstrumentation().targetContext.asApp()
        app.resetInjection()
        app.overrideModule = testDependencies
    }
```

First of all, we are initiating all the provisioning mocks with Mockito. After that, we use an extension method that we have in the sample Android application to obtain the application. First, we clean the graph, we like have all dependencies clean before each test, to be repeatable. At last, we save our test module to replace the regular module dependencies.

We need to save the module because we don't have control over the injections. the order of modules has been injected is important. We like that first injected the application modules, after that the activity modules and in the end, the testing modules overriding the previous provisions. When we invoke the start activity, Kodein is going to create the module and injected it in the graph, and we need be ready to apply our testing replaces.

If you check ```KodeinSampleApp.kt```

```java
class KodeinSampleApp : Application(), KodeinAware {
    override val kodein = ConfigurableKodein(mutable = true)
    var overrideModule: Module? = null

    override fun onCreate() {
        super.onCreate()
        resetInjection()
    }

    fun addModule(activityModules: Module) {
        kodein.addImport(activityModules, true)
        if (overrideModule != null) {
            kodein.addImport(overrideModule!!, true)
        }
    }

    fun resetInjection() {
        kodein.clear()
        kodein.addImport(appDependencies(), true)
    }

    private fun appDependencies(): Module {
        return Module(allowSilentOverride = true) {
            bind<ApplicationScopeClass>() with singleton {
                ApplicationScopeClass()
            }
        }
    }
}
```

We have an instance of Kodein that allows us to have to mutate the graph and compose in different modules.

You can check how to resetInjection, clear the graph and reinject the application dependencies, for each test, We have everything clear for each invocation.

The method addModule has been called for the Activities to inject their modules, this method checks if we have a Module that should be added to the graph after the activity module. This going to happen when we invoke the tests, remember that before each test we assign to override module our testing module with our mocks.

The last step to check that we need to is in the ```MainActivity.kt```.

```java
    override fun onCreate(savedInstanceState: Bundle?) {
        applicationContext.asApp().addModule(activityModules)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTextFromController()
    }
```

We are accessing the Application for injecting our activity module before any execution in the activity.

With those simple steps, We can override our dependencies in those tests where we don't have access to the Constructor, like activities or services.