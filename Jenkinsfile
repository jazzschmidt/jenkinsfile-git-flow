#!groovy

node() {
    checkout scm

    def script = load('jenkins-build.groovy') as BuildScript
    def isMainBranch = releaseBranch || masterBranch

    try {
        // Build
        stage('Build') {
            script.setup { script.build() }
        }

        // Test
        stage('Test') {
            script.setup { script.test() }
        }

        currentBuild.result = 'SUCCESS'
    } catch(e) {
        currentBuild.result = 'FAILURE'
        throw e
    } finally {
        if(isBuildBroken) {
            echo "ERROR: ${committer} just broke the build!"
        }

        if(isBuildFixed) {
            echo "INFO: ${committer} just repaired the build. Well done."
        }
    }

    // Further steps on main branches
    if(isMainBranch) {
        def artifact = isMasterBranch() ? 'Snapshot' : 'Release'

        if(releaseBranch) {
            timeout(time: 30, unit: 'MINUTES') {
                input "Please confirm release ${releaseNumberFromBranch}"
            }
        }

        stage("Publish $artifact") {
            script.setup { script.publish() }
        }
    }
}

interface BuildScript {
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
}


/**
 * @return author of the last commit
 */
String getCommitter() {
    sh(returnStdout: true, script: "git log -n1 --pretty='%an'").trim()
}

/**
 * Tells if the current branch is a main branch
 * @return true for main branches
 */
boolean isMasterBranch() {
    ['master', 'main'].contains(BRANCH_NAME)
}

/**
 * Tells if the current branch is a development branch
 * @return true for development branch
 */
boolean isDevelopBranch() {
    ['develop', 'development', 'dev'].contains(BRANCH_NAME)
}

/**
 * Tells if the current branch is a release branch
 * @return true for release branches
 */
boolean isReleaseBranch() {
    BRANCH_NAME.startsWith('release')
}

/**
 * Retrieves the version from the branch name
 *
 * @return String
 */
String getReleaseNumberFromBranch() {
    def matcher = BRANCH_NAME =~ /release\/(.+)$/

    if (!matcher.matches()) {
        error("Release specifier not found in branchname: ${BRANCH_NAME}")
    }

    matcher.group(1)
}

/**
 * Aborts the current build
 * @param message
 */
void abort(String message) {
    currentBuild.result = 'ABORTED'
    throw new hudson.AbortException(message)
}

/**
 * @return last build status or null
 */
String getLastBuildStatus() {
    currentBuild.previousBuiltBuild?.result
}

/**
 * @return true when previous build did not success but this one did
 */
boolean getIsBuildFixed() {
    currentBuild.result == 'SUCCESS' && lastBuildStatus == 'FAILURE'
}

boolean getIsBuildBroken() {
    currentBuild.result == 'FAILURE' && lastBuildStatus == 'SUCCESS'
}