#!/usr/bin/env groovy
def call(Map params){
    def prepareDeliver = libraryResource 'prepareDeliver.sh'
    def deliver = libraryResource 'deliver.sh'
    def packageAndShip = libraryResource 'packageAndShip.sh'
    pipeline {
        agent any
        stages {
            stage('Package Application with HELM') {
                agent {
                    docker {
                        image 'kmdr7/helm-kubectl:latest'
                    }
                }
                steps {
                    withEnv([
                        'CONTAINER_REGISTRY='+params.containerRegistry,
                        'CONTAINER_IMAGE='+params.containerImage,
                        'CONTAINER_VERSION='+params.containerVersion,
                    ]){
                        sh(packageAndShip)
                    }
                }
            }

            stage('Deploy Application to Kubernetes') {
                agent {
                    docker {
                        image 'kmdr7/helm-kubectl:latest'
                    }
                }
                steps {
                    withCredentials([
                        usernamePassword(
                            credentialsId: 'cred-kubernetes',
                            usernameVariable: 'KUBE_ENDPOINT',
                            passwordVariable: 'KUBE_TOKEN'
                        )
                    ]) {
                        withEnv([
                            'CONTAINER_REGISTRY='+params.containerRegistry,
                            'CONTAINER_IMAGE='+params.containerImage,
                            'CONTAINER_VERSION='+params.containerVersion,
                            'NAMESPACE='+params.namespace,
                        ]){
                            sh(prepareDeliver)
                            sh(deliver)
                        }
                    }
                }
            }
        }
    }
}
