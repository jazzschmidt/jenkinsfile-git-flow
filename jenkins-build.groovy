#!groovy

String GRADLE_OPTS = '--stacktrace'

/**
 * Prepares the build environment, e.g. needed toolchains, env vars, etc.
 * @param Closure The next build step
 */
void setup(Closure cl) {
    withEnv("PATH+JAVA_HOME=${tool('OPENJDK-11')}/bin") {
        cl()
    }
}

/** Compiles and assembles the project */
void build() {
    sh "./gradlew ${GRADLE_OPTS} clean"
    sh "./gradlew ${GRADLE_OPTS} assemble"
}

/** Tests the project and publishes the test results */
void test() {
    // Run the test task and eventually fail upon finishing
    sh "./gradlew ${GRADLE_OPTS} test --continue"

    // Report test results
    junit 'build/test-results/**/TEST*.xml'
}

/** Publishes the project artifacts */
def publish() {
    // Publish to nexus, copy via ssh, ...
    echo 'Publishing done 😉'
}

// Required to expose the methods:
return this