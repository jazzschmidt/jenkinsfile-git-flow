#!groovy

/*
 * Copyright 2020 Carsten Schmidt <jazzschmidt@icloud.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

String GRADLE_OPTS = '--stacktrace'

/**
 * Prepares the build environment, e.g. needed toolchains, env vars, etc.
 * @param Closure The next build step
 */
void setup(Closure cl) {
    // Usually these are a lot of preparing steps in conjunction
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