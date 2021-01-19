#!/usr/bin/env groovy

def call(Map param) {
    pipeline {
        agent any

        environment {
            registry = "taufiq12/apps-blimart-backend"
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
                    sh "echo '${registry}:${env.BUILD_NUMBER}' ${WORKSPACE}/Dockerfile > anchore_images"
                    sh "ls ${WORKSPACE}"
                }
            }

            stage('Publish Docker Image') {
                steps {
                    script {
                        docker.withRegistry( '', registryCredential ) {
                            dockerImage.push("${env.BUILD_NUMBER}")
                        }
                    }
                }
            }

            stage('Remove Unused Docker Image') {
                steps {
                    sh "docker rmi ${registry}:${env.BUILD_NUMBER}"
                }
            }
        }
    }
}
