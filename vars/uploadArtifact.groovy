#!/usr/bin/env groovy

def call(Map param) {
    pipeline {
        agent any

        environment {
            NEXUS_VERSION = "nexus3"
            NEXUS_PROTOCOL = "http"
            NEXUS_URL = "192.168.43.45:8081"
            NEXUS_REPOSITORY = "example-repo"
            NEXUS_CREDENTIAL_ID = "nexus-credentials"
        }

        stages {

            stage('Build') {
                agent {
                    docker {
                        image 'maven:3.6.3-openjdk-8'
                        args '-v /var/lib/jenkins/.m2:/root/.m2'
                    }
                }
                steps {
                    sh "mvn -X clean install"
                }
            }

            stage('Publish To Nexus') {
                steps {
                    script {
                        pom = readMavenPom file: "pom.xml";
                        filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
                        echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}";
                        artifactPath = filesByGlob[0].path;
                        artifactExists = fileExists artifactPath;

                        if(artifactExists) {
                            echo "*** File: ${artifactPath}, group: ${pom.groupId}, packaging: ${pom.packaging}, version ${pom.version}";

                            nexusArtifactUploader(
                                nexusVersion: NEXUS_VERSION,
                                protocol: NEXUS_PROTOCOL,
                                nexusUrl: NEXUS_URL,
                                groupId: pom.groupId,
                                version: pom.version,
                                repository: NEXUS_REPOSITORY,
                                credentialsId: NEXUS_CREDENTIAL_ID,
                                artifacts: [
                                    [
                                        artifactId: pom.artifactId,
                                        classifier: '',
                                        file: artifactPath,
                                        type: pom.packaging
                                    ],
                                    [
                                        artifactId: pom.artifactId,
                                        classifier: '',
                                        file: "pom.xml",
                                        type: "pom"
                                    ]
                                ]
                            );
                        } else {
                            error "*** File: ${artifactPath}, could not be found";
                        }
                    }
                }
            }

            stage('Build Docker Image') {
                steps {
                    sh 'echo "building image"'
                }
            }

            stage('Publish Docker Image') {
                steps {
                    sh 'echo "Push image"'
                }
            }
       }
    }
}