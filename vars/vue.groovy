#!/usr/bin/env groovy

def call(Map param) {
    pipeline {
        agent any
        stages {

            stage('Build') {
                steps {
                    echo 'Building ...'
                }
            }

            stage('Test') {
                steps {
                    echo 'Testing ...'
                }
            }

            stage('Deploy') {
                steps {
                    echo 'Deploying ...'
                }
            }
        }
    }
}