job(name = "Build, run tests, publish", init = {
    container(displayName = "Run publish script", image = "mvn:latest") {
        env["REPOSITORY_URL"] = "https://packages.slne.dev/maven/p/surf/maven"

        shellScript {
            content = """
                echo Build and publish artifacts...
                set -e -x -u
                mvn clean deploy -s settings.xml \
                    -DrepositoryUrl=${'$'}REPOSITORY_URL \
                    -DspaceUsername=${'$'}JB_SPACE_CLIENT_ID \
                    -DspacePassword=${'$'}JB_SPACE_CLIENT_SECRET \
            """
        }
    }
})