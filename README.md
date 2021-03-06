# Git Flow Jenkinsfile

This project demonstrates how to separate actual build details from
its logical structure.

The `Jenkinsfile` contains a template of a _Git Flow_ Continuous
Integration & Deployment workflow, while the implementation itself
resides in the `jenkins-build.groovy` script.

I often use this template on heavyweight projects, that shall be
extended to include semantic versioning verification of branches,
behave different in some special branches, publish or deploy the
project with Jenkins steps and not directly with the build tool,
send fancy messages to Slack/Teams/whatever or all kind of things,
that would otherwise mess up the build logic.

Since I prefer to focus on _how to build this sh*t_, I also
extracted the environment setup for the builds into a single setup
method, that will be used to prepare all stages.

## Build Structure

The build uses the following stages:

- __Build__ - compiles the project
- __Test__ - starts the tests and exposes reports
- on _master_ branch:
    - __Publish Snapshot__ - publishes a snapshot version
- on _release_ branches;
    - __Publish Release__ publishes a release version after manual confirmation

## The Build Script

The actual build script - `jenkins-build.groovy` - must implement
those methods and must be finalised with a `return this` statement:

```groovy
/**
 * Prepares the build environment, e.g. needed toolchains, env vars, etc.
 * @param Closure The next build step
 */
void setup(Closure cl)

/** Compiles and assembles the project */
void build()

/** Tests the project and publishes the test results */
void test()

/** Publishes the project artifacts */
void publish()
```

### Example
````groovy
void setup(Closure cl) {
    // Usually these are a lot of preparing steps in conjunction
    withEnv("PATH+JAVA_HOME=${tool('OPENJDK-11')}/bin") {
        // And this must not be forgotten at the very heart of it
        cl()
    }
}

void build() {
    // Please marvel at the simplicity and obviousness of the steps,
    // rather than having endless intendations of messy setup steps
    sh "./gradlew clean"
    sh "./gradlew assemble"
}

void test() {
    try {
        // Run the test task and eventually fail upon finishing
        sh "./gradlew test --continue"
    } finally {
        // Report test results
        junit 'build/test-results/**/TEST*.xml'
    }
}

/** Publishes the project artifacts */
def publish() {
    // Publish to nexus, copy via ssh, ... 😉
    sh './gradlew publish'
}


return this
````
