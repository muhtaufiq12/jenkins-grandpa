#!/usr/bin/env groovy

def call(Map param) {
    pipeline {
        agent any

        environment {
            registry = "taufiq12/blimart-backend"
            registryCredential = "dockerhub-credentials"
        }

        stages {
            stage('Build and Dockerized Maven Project') {
                steps {
                    script {
                        docker.build registry +"-${env.BUILD_ID}" + "--build-arg jar_file_path=${WORKSPACE}"
                    }
                }
            }

            stage('Copy Maven Project Result') {
                steps {
                    echo "workspace directory is ${WORKSPACE}"
                }
            }

            stage('Build Docker Image') {
                steps {
                    // script {
                    //     docker.build registry + ":$BUILD_NUMBER"
                    // }
                    echo 'Building Image'
                }
            }

            stage('Analyze Docker Image') {
                steps {
                    echo 'Analyze Image ..'
                }
            }

            stage('Publish Docker Image') {
                steps {
                    echo 'Publish Docker Image ..'
                }
            }
        }
    }
}
