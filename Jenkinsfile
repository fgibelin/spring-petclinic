pipeline {

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        skipDefaultCheckout true
    }

    agent { label 'maven-agent' }

    stages {
        stage('Run maven') {
            steps {
                git(url:'https://github.com/fgibelin/spring-petclinic', branch: 'main')
                withMaven(
                          options: [junitPublisher(disabled: true, healthScaleFactor: 1.0)],
                          publisherStrategy: 'EXPLICIT') {
                              sh 'mvn clean verify -Dtest="*,!MySql*,!Postgres*"'
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
                        echo "Building Docker image for spring-petclinic with version ${env.petclinicVersion}"
                        sh "docker build -t spring-petclinic:${env.petclinicVersion} . --build-arg target/spring-petclinic-${env.petclinicVersion}"
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
