# Jenkins pipeline for Spring Petclinic Application

## Prerequisites
1. A Jenkins Controller
2. Recommended plugins, including
- Git plugin
- Pipeline Maven Integration plugin
- JUnit plugin
3. An agent/node with the following tools installed (agent label: `maven-agent`)
  - JDK 17
  - Maven
  - Docker

Tested on Jenkins 2.401, with Maven 3.6.3 and Docker 24.0.5
 
## Run the pipeline
Create a simple pipeline, with:
- Definition: Pipeline script from SCM
- SCM: Git
- Repository URL: `https://github.com/fgibelin/spring-petclinic`
- Branch to build: `*/main`

The first stage of the pipeline will clone the repository, then use Maven to compile, run the unit tests, and build the jar file in `target/ folder.

The command used is `mvn clean verify` (recap on [Maven Lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#a-build-lifecycle-is-made-up-of-phases))

If you wish to use Artifactory plugin instead, you can replace the first stage with the following block (please replace <artifactory-server-id> with the ID you have set in the Manage Jenkins -> System -> Artifactory).

You will need the following Maven repositories setup in that example:
- `maven-local-releases` (local)
- `maven-local-snapshots` (local)
- `maven` (virtual), which will include [Maven Central repo](https://repo1.maven.org/maven2/), [Spring Milestone repo](https://repo.spring.io/milestone), and the local repositories
```
        stage('Build and deploy to Artifactory') {
            steps {
                script {
                    git(url:'https://github.com/fgibelin/spring-petclinic', branch: 'main')
                    pom = readMavenPom file: "pom.xml";
                    filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
                    artifactPath = filesByGlob[0].path;
                    artifactExists = fileExists artifactPath;
                    if (artifactExists) {
                        env.petclinicVersion = pom.version
                        rtMavenResolver (
                            id: 'maven-resolver',
                            serverId: '<artifactory-server-id>',
                            releaseRepo: 'maven',
                            snapshotRepo: 'maven',
                        )
                        rtMavenDeployer (
                            id: 'maven-deployer',
                            serverId: '<artifactory-server-id>',
                            releaseRepo: 'maven-local-releases',
                            snapshotRepo: 'maven-local-snapshots',
                        )
                        rtMavenRun (
                            useWrapper: true,
                            pom: 'pom.xml',
                            goals: 'verify',
                            resolverId: 'maven-resolver',
                            deployerId: 'maven-deployer',
                            buildName: '${env.JOB_NAME}',
                            buildNumber: '${currentBuild.number}',
                            deployArtifacts: true,
                            project: 'spring-petclinic'
                        )
                    }
                }
            }
        }
```

The second stage will build the Docker image from the Dockerfile with the built jar file, using:
```
docker build -t spring-petclinic:<tag> --build-arg petclinicArtifacto=<path to spring petclinic jar file> .
```


# Run Docker image
You can run the built Docker image using the following command:
```
docker run -p 8080:8080 -d --name spring-petclinic spring-petclinic:3.1.0-SNAPSHOT
```
Or replace the tag `3.1.0-SNAPSHOT` with the version from the application you built.
This will run the container in a detached mode, exposing port 8080
You can then access Petclinic application on `http://localhost:8080`
You can follow the logs using `docker logs spring-petclinic -f`

# Stop application
```
docker stop spring-petclinic
docker rm spring-petclinic
```

# Pull application from a public registry
The built Docker image is available in a public JFrog Container Registry.
This registry is insecured, so please add:
```
{
  "insecure-registries" : ["35.195.23.92:8082"]
}
```
in your /etc/docker/daemon.json, and restart your docker daemon.


You can then pull the image using:
```
docker pull 35.195.23.92:8082/docker-local/spring-petclinic:3.1.0-SNAPSHOT
docker tag 35.195.23.92:8082/docker-local/spring-petclinic:3.1.0-SNAPSHOT spring-petclinic:3.1.0-SNAPSHOT
docker rmi 35.195.23.92:8082/docker-local/spring-petclinic:3.1.0-SNAPSHOT
```
Then run it:
```
docker run -p 8080:8080 -d --name spring-petclinic spring-petclinic:3.1.0-SNAPSHOT
```
