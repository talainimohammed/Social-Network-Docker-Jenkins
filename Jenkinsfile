pipeline{
  agent any
tools {
        // Install the Maven version configured as "M3" and add it to the path.
        maven "Maven"
    }

  stages{
        stage('Build'){
            steps{
                sh 'mvn -Dmaven.test.skip=true clean install'
            }
        }
        stage('Build Docker image'){
          steps{
            sh 'docker-compose build'
          }
        }
        stage('Push Docker image'){
          steps{
            withCredentials([usernamePassword(credentialsId: 'dockerHubCredentials', usernameVariable: 'mttech', passwordVariable: 'talainimd658')]) {
              sh "docker login -u $DOCKERHUB_USERNAME -p $DOCKERHUB_PASSWORD"
              sh 'docker-compose push'
            }
          }
        }
  }
  
}