
job("Build, Test and Publish") {
    container(displayName = "Build an publish", image = "maven:latest") {
        env["REPOSITORY_URL"] = "{{ project:REPOSITORY_URL }}"

        shellScript {
            content = """
                mvn -s settings.xml clean deploy \
                    -DrepositoryUrl=${'$'}REPOSITORY_URL \
                    -DspaceUsername=${'$'}JB_SPACE_CLIENT_ID \
                    -DspacePassword=${'$'}JB_SPACE_CLIENT_SECRET \
            """
        }
    }
}