#!/usr/bin/env groovy

def call(Map param) {
    pipeline {
        agent none
        stages {
            stage('Build') {
                agent {
                    docker {
                        image 'maven:3.6.3-openjdk-8'
                        args '-v $HOME/test-tmp:/test-tmp'
                    }
                }
                steps {
                    sh 'mvn -q package'
                    sh 'mvn test'
                }
            }
        }
    }
}
