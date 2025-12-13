pipeline {
    agent { 
        label 'build_agents'
    }

    stages {
        stage('init') {
            steps {
                bat '''
                    echo Hello World
                '''
            }
        }
    }
}