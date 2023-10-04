pipeline {
    agent any

    stages{
    stage('Build') {
        steps {
                echo 'Building....'
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/umibana/Tingeso-Monolitico']])
                script {gradle 'build'}
            }
        }
        stage('SonarQube analysis'){
            steps{
                echo 'SonarQube analysis....'
                sh """./gradlew sonar \
                        -Dsonar.projectKey=MonolithicWebapp \
                        -Dsonar.projectName='MonolithicWebapp' \
                        -Dsonar.host.url=http://localhost:9000 \
                        -Dsonar.token=sqp_973532bb05d5d19caf378c79d739abb9b86b572a"""
            }
        }
        stage('Build Docker image'){
        steps {
            echo 'Building Docker image....'
            sh 'docker buildx build --platform linux/amd64 -t umibana/monolithicwebapp .'
            }
        }
        stage('Push Docker image'){
         steps{
             echo 'Pushing Docker image...'
             withCredentials([string(credentialsId: 'dckrhubpassword', variable: 'dckpass')]) {
                 sh "docker login -u umibana -p ${dckpass}"
             }
             sh "docker push umibana/monolithicwebapp"

         }
        }
    }
        post {
            always {
                echo 'Build finished'
                sh "docker logout"
            }
            success {
                echo 'Build success'
            }
            failure {
                echo 'Build failed'
            }
            unstable {
                echo 'Build unstable'
            }
            changed {
                echo 'Build changed'
            }
      
        }
    }