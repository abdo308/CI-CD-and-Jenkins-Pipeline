pipeline {
    agent any

    environment {
        CALCULATOR_DIR = 'services/calculator'
        FRONTEND_DIR   = 'frontend'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Backend Unit Tests') {
            steps {
                echo 'Running Java Unit Tests...'
                dir("${CALCULATOR_DIR}") {
                    // Running inside a docker container to ensure Maven is available
                    sh 'docker run --rm -v "$(pwd)":/app -w /app maven:3.9.9-eclipse-temurin-17 mvn test'
                }
            }
            post {
                success {
                    echo 'All tests passed successfully!'
                }
                failure {
                    error 'Unit tests failed. Pipeline stopping.'
                }
            }
        }

        stage('Build Backend') {
            steps {
                echo 'Packaging Calculator Service...'
                dir("${CALCULATOR_DIR}") {
                    sh 'docker run --rm -v "$(pwd)":/app -w /app maven:3.9.9-eclipse-temurin-17 mvn package -DskipTests'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                echo 'Building Docker Compose images...'
                sh 'docker compose build'
            }
        }

        stage('Health Check') {
            steps {
                echo 'Starting containers for verification...'
                sh 'docker compose up -d'
                // Wait for the service to be ready
                sh 'sleep 10'
                sh 'curl -f http://localhost:8080/api/health'
                echo 'CI Pipeline completed successfully!'
            }
            post {
                always {
                    echo 'Cleaning up containers...'
                    sh 'docker compose down'
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
