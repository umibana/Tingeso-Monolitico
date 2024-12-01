pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Building....'
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/umibana/Tingeso-Monolitico']])
                withGradle {
                    sh './gradlew build'
                }
            }
        }
        
        stage('SonarQube analysis') {
            steps {
                echo 'SonarQube analysis....'
                sh """ ./gradlew sonar \
                          -Dsonar.projectKey=Devsecops \
                          -Dsonar.host.url=http://localhost:9001 \
                          -Dsonar.token=sqp_295d33e50702c684444b9841381219259151a52c"""
            }
        }

        stage('OWASP Dependency Check') {
            steps {
                echo 'Running OWASP Dependency Check...'
                dependencyCheck additionalArguments: '''
                    --scan ./ 
                    --format HTML 
                    --format XML 
                    --suppressionFile suppress.xml
                    --failOnCVSS 7
                    --out dependency-check-report''', 
                odcInstallation: 'OWASP-Dependency-Check'
                
                dependencyCheckPublisher pattern: 'dependency-check-report.xml'
            }
        }

        stage('OWASP ZAP Security Tests') {
            steps {
                echo 'Running OWASP ZAP Security Tests...'
                script {
                    // Make sure your application is running before scanning
                    sh '''
                        docker run -v $(pwd)/zap-report:/zap/wrk/:rw -t owasp/zap2docker-stable zap-baseline.py \
                        -t http://localhost:8090 \
                        -r zap-report.html \
                        -I \
                        -j \
                        -m 1 \
                        --auto
                    '''
                }
                // Publish ZAP report
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'zap-report',
                    reportFiles: 'zap-report.html',
                    reportName: 'ZAP Security Report'
                ])
            }
        }
    }

    post {
        always {
            echo 'Build finished'
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
