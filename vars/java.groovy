#!/usr/bin/env groovy

def call(Map param) {
    pipeline {
        agent none
        stages {
            stage('Build') {
                agent {
                    docker {
                        image 'maven:3.6.3-openjdk-8'
                        args '-v /root/.m2:/root/.m2'
                    }
                }
                steps {
                    sh 'mvn package -DskipTests=true'
                }
            }

            stage('Build Docker Image') {
                steps {
                    echo 'Build Docker Image'
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
