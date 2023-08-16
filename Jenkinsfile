pipeline {

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        skipDefaultCheckout true
    }

    agent { label 'maven-agent' }

    stages {
//        stage('Run maven') {
//            steps {
//                git(url:'https://github.com/fgibelin/spring-petclinic', branch: 'main')
//                withMaven(
//                          options: [junitPublisher(disabled: true, healthScaleFactor: 1.0)],
//                          publisherStrategy: 'EXPLICIT') {
//                              sh 'mvn clean verify'
//                          }
//            }
//        }
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
        stage('Build Docker image') {
            steps {
                script {
                    pom = readMavenPom file: "pom.xml";
                    filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
                    artifactPath = filesByGlob[0].path;
                    artifactExists = fileExists artifactPath;
                    if (artifactExists) {
                        echo "Building Docker image for spring-petclinic with version ${pom.version}"
                        sh "docker build -t spring-petclinic:${pom.version} --build-arg petclinicArtifact=./target/${pom.artifactId}-${pom.version}.${pom.packaging} ."
                    }
                }
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/**/*.xml'
        }
    }
}
