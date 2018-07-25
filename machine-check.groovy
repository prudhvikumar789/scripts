def mac="192.168.1.74"
def linux="192.168.1.6"
def internet="8.8.8.8"
def buildnumber = env.BUILD_NUMBER
def buildstatus="Success";

def emailNotification(buildstatus){
def emailBody="";
def recipentlist="";
    if("${buildstatus}"=='Started'){
       recipentlist="prudhvi.kumar@i-exceed.com" 
       emailBody= """<p>Hello Prudhvi,</p><p>Machine status build triggered.<br>Please find the status details below.</p><p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME}[${env.BUILD_NUMBER}]</a></p><p>Regards,<br>Appzillon DevTeam<br>I-Exceed Technology Solutions</p>"""
    }
    else if("${buildstatus}"=='Success'){
       recipentlist="prudhvi.kumar@i-exceed.com" 
       emailBody= """<p>Hi All,</p><p>Please find the latest build details below.</p><p>Regards,<br>Appzillon DevTeam<br>I-Exceed Technology Solutions</p>""" 
    }
    else{
       recipentlist="prudhvi.kumar@i-exceed.com" 
       emailBody= """<p style="color:red;">Hi All, <br><br> Machine status build ${buildstatus}: Job ${env.JOB_NAME} [${env.BUILD_NUMBER}] <br><br> Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a><br><br>Regards,<br><br>Appzillon IDE Team<br>I-Exceed Technology Solutions</p>"""
    }
    emailext (
            // subject: "Machine status Build ${buildstatus}",
            subject: "Build ${buildstatus}",
            mimeType: 'text/html',
            body: "${emailBody}",
            to: "${recipentlist}",
            replyTo: "jenkinsadmin@i-exceed.com",
            from: 'jenkinsadmin@i-exceed.com',
            recipientProviders: [[$class: 'CulpritsRecipientProvider']]
             )
}

pipeline {
    agent any
    stages {
        
        stage('Status:LINUX') {
            steps {
                script {
                    bat "ping ${linux} -n 1 -w 1000"
                    
            }
        }
        post {
            failure {
                 script { buildstatus="Failed @LINUX" }
                emailNotification("${buildstatus}")
            }
          }
    }
    
    stage('Status:MAC') {
            steps {
                script {
                    bat "ping ${mac} -n 1 -w 1000"
                    
            }
        }
        post {
            failure {
                 script { buildstatus="Failed @MAC" }
                emailNotification("${buildstatus}")
            }
          }
    }


    stage('Status:Internet') {
            steps {
                script {
                    bat "ping ${internet} -n 1 -w 1000"
                    
            }
        }
        post {
            failure {
                 script { buildstatus="Failed @Internet Connectivity Check" }
                emailNotification("${buildstatus}")
            }
          }
    }
    
  }

  post {
    success {
        build(job: "AppzillonPipeLine-Test", parameters: [[$class: 'StringParameterValue', name: 'CheckoutPath', value: "Change Default Path" ],[$class: 'StringParameterValue', name: 'AppzillonVersion', value: "3.6.0" ],[$class: 'StringParameterValue', name: 'CustId', value: "APZCUSTOMERID" ]], propagate: true, wait: false)
        //emailNotification("${buildstatus}","${sysdate}","${params.AppzillonVersion}")
    }
  }

  
//   post {
//             success {
//                  script { buildstatus="Success @Machine-Check"}
//                 emailNotification("${buildstatus}")
//             }
//           }
}