| Command        | What it does                                                                          | Runs Tests? | Produces JAR? |
| -------------- | ------------------------------------------------------------------------------------- | ----------- | ------------- |
| `mvn clean`    | Deletes the `target/` directory                                                       | ❌           | ❌             |
| `mvn validate` | Validates the project structure and `pom.xml`                                         | ❌           | ❌             |
| `mvn compile`  | Compiles the main source code (`src/main/java`)                                       | ❌           | ❌             |
| `mvn test`     | Compiles test code and runs unit tests                                                | ✅           | ❌             |
| `mvn package`  | Compiles, tests, and packages into a JAR/WAR                                          | ✅           | ✅             |
| `mvn verify`   | Runs additional verification (e.g., integration tests, JaCoCo checks) after packaging | Usually ✅   | ✅             |
| `mvn install`  | Installs the built artifact into your local Maven repository (`~/.m2`)                | ✅           | ✅             |
| `mvn deploy`   | Uploads the artifact to a remote Maven repository (e.g., Nexus, Artifactory)          | ✅           | ✅             |
