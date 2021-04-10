pipeline {
    agent any
    parameters {
            choice(choices: ['PATCH', 'MINOR', 'MAJOR'], name: 'VERSION_BUMP')
        }
    stages {
        stage('build') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'aliyun_maven_repo', passwordVariable: 'password', usernameVariable: 'username')]) {
                    sh './gradlew clean build -Pusername=$username -Ppassword=$password'
                }
            }
        }
        stage('setVersion') {
            steps {
                script {
                    def version = sh (script: "./gradlew properties -q | grep \"version:\" | awk '{ print \$2}'",
                        returnStdout: true).trim()
                    version = version.replace("-SNAPSHOT", '')
                    def versionBump = params.VERSION_BUMP
                    sh('./gradlew task increment -PversionBump=' + params.VERSION_BUMP)
                    version = sh (script: "./gradlew properties -q | grep \"version:\" | awk '{ print \$2}'",
                        returnStdout: true).trim()
                }
            }
        }
        stage('uploadArtifacts') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'aliyun_maven_repo', passwordVariable: 'password', usernameVariable: 'username')]) {
                    sh './gradlew task uploadArchives -Pusername=$username -Ppassword=$password'
                }
             }
        }
        stage('tag') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'Chris_github', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                    script {
                        def version = sh (script: "./gradlew properties -q | grep \"version:\" | awk '{ print \$2}'",
                                           returnStdout: true).trim()

                        def url = scm.userRemoteConfigs[0].url.replace('https://','')
                        sh "git tag -f $version"
                        sh("git commit -am \"chore(gradle): Published version " + version + "\"")
                        sh('git push --atomic https://${GIT_USERNAME}:${GIT_PASSWORD}@' + url + ' HEAD:master ' + version)
                        sh './gradlew task prepareSnapshot'
                        sh("git commit -am \"chore(gradle): Prepare for next release\"")
                        sh('git push https://${GIT_USERNAME}:${GIT_PASSWORD}@' + url + ' HEAD:master')
                    }
                }
            }
        }
    }
}