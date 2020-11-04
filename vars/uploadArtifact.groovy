#!/usr/bin/env groovy

def call(Map param) {
    pipeline {
        agent any

        environment {
            // This can be nexus3 or nexus2
            NEXUS_VERSION = "nexus3"
            // This can be http or https
            NEXUS_PROTOCOL = "http"
            // Where your Nexus is running. 'nexus-3' is defined in the docker-compose file
            NEXUS_URL = "192.168.43.45:8081"
            // Repository where we will upload the artifact
            NEXUS_REPOSITORY = "example-repo"
            // Jenkins credential id to authenticate to Nexus OSS
            NEXUS_CREDENTIAL_ID = "nexus-credentials"
        }

        stages {

            stage('Build') {
                agent {
                    docker {
                        image 'maven:3.6.3-openjdk-8'
                        args '-v $HOME/test-tmp:/test-tmp'
                    }
                }
                steps {
                    sh "mvn package -DskipTests=true"
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
                                artifatcs: [
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
    }   }
}