#!/usr/bin/env groovy

def call(Map param) {
    pipeline {
        agent any

        environment {
            registry = "taufiq12/blimart-backend"
            registryCredential = "dockerhub-credentials"
            dockerImage = ''
        }

        stages {
            stage('Build and Dockerized Maven Project') {
                steps {
                    script {
                        dockerImage = docker.build registry 
                    }
                }
            }

            stage('Analyze Docker Image') {
                steps {
                    echo 'Analyze Image ..'
                }
            }

            stage('Publish Docker Image') {
                steps {
                    script {
                        docker.withRegistry( 'https://registry.hub.docker.com', 'registryCredential' ) {
                            dockerImage.push("${env.BUILD_NUMBER}")
                            dockerImage.push("latest")
                        }
                    }
                }
            }
        }
    }
}
