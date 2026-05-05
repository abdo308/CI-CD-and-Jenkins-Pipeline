pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    triggers {
        githubPush()
        pollSCM('* * * * *')
    }

    stages {
        stage('Clone Repository') {
            steps {
                echo '========== Stage 1: Cloning Repository =========='
                checkout scm
                echo 'Repository cloned successfully!'
            }
        }

        stage('Build Project') {
            steps {
                echo '========== Stage 2: Building Project =========='
                dir('services/calculator') {
                    sh 'mvn clean compile -q'
                }
                echo 'Build completed successfully!'
            }
        }

        stage('Run Unit Tests') {
            steps {
                echo '========== Stage 3: Running Unit Tests =========='
                dir('services/calculator') {
                    sh 'mvn test'
                }
                echo 'All unit tests passed!'
            }
            post {
                always {
                    junit 'services/calculator/target/surefire-reports/*.xml'
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed!'
        }
    }
}
