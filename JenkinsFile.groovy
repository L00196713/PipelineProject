pipeline {
    agent { 
        label 'build_agents'
    }
    parameters {
        string(
            name: 'Git Reference', 
            defaultValue: 'main', 
            description: 'The branch/tag/commit from the Application repository you wish to build.'
        )
    }
    environment {
        GIT_REF = "${params['Git Reference']}"
    }
    stages {
        stage('Initialize') {
            steps {
                bat '''
                    echo I will put any intialization that must be done at runtime in here
                '''
            }
        }
        stage('Source Control Management') {
            steps {
                bat '''
                    echo Clone required repositories (build source, test code, terraform scripts, etc)
                '''
            }
        }
        stage('Build') {
            steps {
                bat '''
                    echo Build the application
                '''
            }
        }
        stage('Test') {
            steps {
                bat '''
                    echo Run unit tests on the newly built package
                '''
            }
        }
        stage('Security Scans') {
            steps {
                bat '''
                    echo Run security scans (static code analysis and vulnerability checker)
                '''
            }
        }
        stage('Create Docker Image') {
            steps {
                bat '''
                    echo Build docker image
                '''
            }
        }
        stage('Deploy') {
            steps {
                bat '''
                    echo Deploy to cloud, terraform for infra, ansible for app/container installation
                    echo also include monitoring/logging in the deployment
                '''
            }
        }
    }
    post {
        always {
            echo "I can add any cleanup or job summary's here"
        }
        failure {
            echo "I could put something like slack notifications here"
        }
    }
}