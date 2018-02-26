# Kodein-sample-testing ![Build Status](https://api.travis-ci.org/Karumi/kodein-sample-testing.svg?branch=master)

This repository aims to be a small example of how to use Kodein to provide different implementations for production code and testing code

## Description

**The idea is to use Kodein replacing part of our production code using test doubles during the instrumentation tests execution**. This allow us to write instrumentation tests easily.

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

We have created a module that allows us to override the graph and create a different provisioning for those classes. Depends on your objective you can use instance or provision methods. In our case, we use the instance because we are initing Mockito in the test setup.

Last but not least, **we override the production graph using the testing one in our set up method as follows**:

```java
   @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val app = InstrumentationRegistry.getInstrumentation().targetContext.asApp()
        app.resetInjection()
        app.overrideModule = testDependencies
    }
```

First of all, we are instantiating all the mocks we are going to use as part of this test using Mockito. Then, **we use an extension method that we have in the sample Android application to obtain the application instance. Cleaning  the graph we get a clean environment before each test. This helps us to create repeatable scenarios.**

We need to save the module because we don't have control over the injections. Take into account that the order of modules is important. First, we inject the application modules, after that the activity modules and in the end, the testing modules overriding the previous provisions if needed. When we invoke the activity under test, Kodein is going to create the module and inject it into the graph.

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

Based on the previous code we have got an instance of Kodein configured and ready to override modules in a composable way.

You can check how to ``resetInjection`` method clears the graph and restarts the injection of the application dependencies, for each test. This is the key to success if you want to create repeatable scenarios for every test case.

The method ``addModule`` has been called for the Activities to inject their modules, this method checks if we have a Module that should be added to the graph after the activity module. This going to happen when we invoke the tests. Remember that before each test we assign some modules to override the original Kodein configuration from our testing module with our test doubles.

Checking that the UI shows the expected message is the last step. You can find the code in ```MainActivity.kt```.

```java
    override fun onCreate(savedInstanceState: Bundle?) {
        applicationContext.asApp().addModule(activityModules)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTextFromController()
    }
```

**With these simple steps, we can override our dependencies in our tests! We don't have access to the Activities or Services constructors in order to replace production-code dependencies with test doubles.**

License
-------

    Copyright 2018 Karumi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
