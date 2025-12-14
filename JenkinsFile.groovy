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
    tools {
        maven 'Maven-3.9.11'
        jdk 'Java-21'
        git 'git'
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
                git([
                    branch: env.GIT_REF, 
                    changelog: false, 
                    credentialsId: 'Git-SSH-Key', 
                    poll: false, 
                    url: 'git@github.com:L00196713/SimpleJavaProject.git'
                ])
            }
        }
        stage('Build') {
            steps {
                bat '''
                    mvn clean package -DskipTests
                '''
            }
        }
        stage('Test') {
            steps {
                bat '''
                    mvn test
                '''
                junit 'target/surefire-reports/*.xml'
            }
        }
        stage('Security Scans') {
            steps {
                script {
                    def status = "success"
                    try {
                        withCredentials([
                            string(credentialsId: 'nvd_api_key', variable: 'NVD_API_KEY'),
                            file(credentialsId: 'mvn_settings', variable: 'MAVEN_SETTINGS_FILE')
                        ]){
                            bat """
                                mvn -s ${MAVEN_SETTINGS_FILE} org.owasp:dependency-check-maven:check -DnvdApiKey=${NVD_API_KEY}
                            """
                        }
                    } catch (err) {
                        echo "Dependency Check failed with error: ${err}"
                        status = "failure"
                    } finally {
                        dependencyCheckPublisher pattern: 'target/dependency-check-report.xml', failedTotalCritical: 1
                        if (status == 'failure') {
                            currentBuild.result = 'UNSTABLE'
                            throw new Exception("Dependency Check failed.") 
                        }
                    }
                }
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
            deleteDir()
        }
        failure {
            echo "I could put something like slack notifications here"
        }
    }
}