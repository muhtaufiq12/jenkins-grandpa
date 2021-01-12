#!/usr/bin/env groovy

def call(Map param) {
    pipeline {
        agent any

        environment {
            registry = "taufiq12/blimart-backend"
            registryCredential = "dockerhub-credentials"
        }

        stages {
            stage('Build Maven Project') {
                agent {
                    docker {
                        image 'maven:3.6.3-openjdk-8'
                        args '-v /root/.m2:/root/.m2'
                    }
                }
                steps {
                    sh 'mvn -B -DskipTests clean package'
                }
            }

            stage('Copy Maven roject Result') {
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
