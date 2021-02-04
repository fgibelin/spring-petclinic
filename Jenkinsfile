pipeline {
    agent { label 'maven-3.6.3-jdk-11' }
    options {
        skipDefaultCheckout true
    }
    stages {
        stage ("Compile") {
            steps {
                container('maven') {
                    git branch: 'main', url: 'https://github.com/fgibelin/spring-petclinic.git'
                    withMaven(globalMavenSettingsConfig: 'maven-settings',
                              options: [junitPublisher(disabled: true, healthScaleFactor: 1.0)],
                              publisherStrategy: 'EXPLICIT') {
                        sh 'mvn -DskipTests=true clean compile'
                    }
                }
            }
        }
        stage ("Test") {
            steps {
                container('maven') {
                    withMaven(globalMavenSettingsConfig: 'maven-settings',
                              options: [junitPublisher(disabled: true, healthScaleFactor: 1.0)],
                              publisherStrategy: 'EXPLICIT') {
                        sh 'mvn -DskipTests=false test'
                    }
                }
            }
        }
        stage ("SonarQube") {
            steps {
                container('maven') {
                    withSonarQubeEnv('SonarQube') {
                        withMaven(globalMavenSettingsConfig: 'maven-settings',
                              options: [junitPublisher(disabled: true, healthScaleFactor: 1.0)],
                              publisherStrategy: 'EXPLICIT') {
                            sh 'mvn -DskipTests=true -Dsonar.host.url=${SONAR_HOST_URL} sonar:sonar'
                        }
                    }
                }
            }
        }
        stage ("Package") {
            steps {
                container('maven') {
                    withMaven(globalMavenSettingsConfig: 'maven-settings') {
                        sh 'mvn -DskipTests=true package'
                    }
                }
            }
        }
        stage ("Publish to Artifactory") {
            steps {
                container('maven') {
                    script {
                        artifactId = readMavenPom().getArtifactId()
                        artifactVersion = readMavenPom().getVersion()
                    }
                    rtUpload (
                        serverId: 'Artifactory',
                        spec: """{
                                "files": [
                                {
                                    "pattern": "target/spring-petclinic-*.jar",
                                    "target": "maven-local-snapshots/org/springframework/samples/spring-petclinic/${artifactVersion}/"
                                },
                                {
                                    "pattern": "pom.xml",
                                    "target": "maven-local-snapshots/org/springframework/samples/spring-petclinic/${artifactVersion}/spring-petclinic-${artifactVersion}.pom"
                                }
                                ]
                            }"""
                    )
                    rtPublishBuildInfo serverId: 'Artifactory'
                }
            }
        }
    }
}
