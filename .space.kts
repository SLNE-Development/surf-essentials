job("Build and Deploy Essentials") {
	container(displayName = "Essentials Builder", image = "maven:latest") {
		shellScript {
			content = """
				mvn clean install package
   			"""
        }
    }
}