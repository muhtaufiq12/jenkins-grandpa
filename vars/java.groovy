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
                    sh '''
                        docker run -p 5432:5432 -d --name db arminc/clair-db
                        sleep 15
                        docker run -p 6060:6060 --link db:postgres -d --name clair arminc/clair-local-scan
                        sleep 1
                        DOCKER_GATEWAY=$(docker network inspect bridge --format "{{range .IPAM.Config}}{{.Gateway}}{{end}}")
                        wget -qO clair-scanner https://github.com/arminc/clair-scanner/releases/download/v8/clair-scanner_linux_amd64 && chmod +x clair-scanner
                        ./clair-scanner --ip="$DOCKER_GATEWAY" myapp:latest || exit 0
                    '''
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
