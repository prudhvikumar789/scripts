def mac="192.168.1.74"
def linux="192.168.1.6"
def internet="8.8.8.8"
def buildnumber = env.BUILD_NUMBER
def buildstatus="Success";

def emailNotification(buildstatus){
def emailBody="";
def recipentlist="";
    if("${buildstatus}"=='Failed @LINUX'){
    //   recipentlist="prudhvi.kumar@i-exceed.com"
      recipentlist="preetish.das@i-exceed.com,prudhvi.kumar@i-exceed.com,darshanl.sagar@i-exceed.com,shyam.kumar@i-exceed.com,cc:chandu.balaji@i-exceed.com,cc:nagaraj.hiremath@i-exceed.com,cc:arthanarisamy@i-exceed.com,cc:prabudas.s@i-exceed.com" 
       emailBody= """<p>Hi All, <br><br> Machine status ${buildstatus} Job ${env.JOB_NAME} [${env.BUILD_NUMBER}] <br><br> Linux : <b style="color:#f2620e;"> Host Not Reachable.</b><br><br> <br><br> Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a><br><br>Regards,<br><br>Appzillon IDE Team<br>I-Exceed Technology Solutions</p>"""
    }
    else if("${buildstatus}"=='Failed @MAC'){
    //   recipentlist="prudhvi.kumar@i-exceed.com"
      recipentlist="preetish.das@i-exceed.com,prudhvi.kumar@i-exceed.com,darshanl.sagar@i-exceed.com,shyam.kumar@i-exceed.com,cc:chandu.balaji@i-exceed.com,cc:nagaraj.hiremath@i-exceed.com,cc:arthanarisamy@i-exceed.com,cc:prabudas.s@i-exceed.com" 
       emailBody= """<p>Hi All, <br><br> Machine status ${buildstatus} Job ${env.JOB_NAME} [${env.BUILD_NUMBER}] <br><br> Linux : <b style="color:#0ed800;">OK</b> <br><br> MAC : <b style="color:#f2620e;">Host Not Reachable</b> <br><br> Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a><br><br>Regards,<br><br>Appzillon IDE Team<br>I-Exceed Technology Solutions</p>""" 
    }
    else if("${buildstatus}"=='Failed @Internet Connectivity Check'){
    //   recipentlist="prudhvi.kumar@i-exceed.com"
      recipentlist="preetish.das@i-exceed.com,prudhvi.kumar@i-exceed.com,darshanl.sagar@i-exceed.com,shyam.kumar@i-exceed.com,cc:chandu.balaji@i-exceed.com,cc:nagaraj.hiremath@i-exceed.com,cc:arthanarisamy@i-exceed.com,cc:prabudas.s@i-exceed.com" 
       emailBody= """<p>Hi All, <br><br> Machine status ${buildstatus} Job ${env.JOB_NAME} [${env.BUILD_NUMBER}] <br><br> Linux : <b style="color:#0ed800;">OK</b> <br><br> MAC : <b style="color:#0ed800;">OK</b><br><br> Windows Connectivity : <b style="color:#f2620e;">Host Not Reachable </b> <br><br> Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a><br><br>Regards,<br><br>Appzillon IDE Team<br>I-Exceed Technology Solutions</p>""" 
    }
    else if("${buildstatus}"=='Success @Machine-Status'){
    //   recipentlist="prudhvi.kumar@i-exceed.com"
      recipentlist="preetish.das@i-exceed.com,prudhvi.kumar@i-exceed.com,darshanl.sagar@i-exceed.com,shyam.kumar@i-exceed.com,cc:chandu.balaji@i-exceed.com,cc:nagaraj.hiremath@i-exceed.com,cc:arthanarisamy@i-exceed.com,cc:prabudas.s@i-exceed.com" 
       emailBody= """<p>Hi All, <br><br> Machine status ${buildstatus} Job ${env.JOB_NAME} [${env.BUILD_NUMBER}] <br><br> Linux : <b style="color:#0ed800;">OK </b> <br><br> MAC : <b style="color:#0ed800;">OK</b><br><br> Internet Connectivity : <b style="color:#0ed800;">OK</b><br><br><p>Trigger Appzillon Build :<b style="color:#0494c9;">SUCCESS</b></p><br><br> Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a><br><br>Regards,<br><br>Appzillon IDE Team<br>I-Exceed Technology Solutions</p>""" 
    }
    else{
       recipentlist="prudhvi.kumar@i-exceed.com" 
       emailBody= """<p>Hi All, <br><br> Machine status ${buildstatus} Job ${env.JOB_NAME} [${env.BUILD_NUMBER}] <br><br> Linux : <b style="color:#0ed800;">OK </b> <br><br> MAC : <b style="color:#0ed800;">OK</b><br><br> Internet : <b style="color:#0ed800;">OK</b><br><br> <br><br> Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a><br><br>Regards,<br><br>Appzillon IDE Team<br>I-Exceed Technology Solutions</p>"""
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


    stage('Status:INTERNET') {
            steps {
                script {
                    bat "cd /d C:\\Cygwin64\\bin && lynx.exe -cmd_script=D:\\wifilogin.txt www.yahoo.com"
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

//   post {
//     success {
//         build(job: "AppzillonPipeLine-Test", parameters: [[$class: 'StringParameterValue', name: 'CheckoutPath', value: "Change Default Path" ],[$class: 'StringParameterValue', name: 'AppzillonVersion', value: "3.6.0.S1" ],[$class: 'StringParameterValue', name: 'CustId', value: "APZCUSTOMERID" ]], propagate: true, wait: false)
//         //emailNotification("${buildstatus}","${sysdate}","${params.AppzillonVersion}")
//     }
//   }

  
  post {
            success {
                 script { buildstatus="Success @Machine-Status"}
                emailNotification("${buildstatus}")
                build(job: "AppzillonPipeLine-Test", parameters: [[$class: 'StringParameterValue', name: 'CheckoutPath', value: "Change Default Path" ],[$class: 'StringParameterValue', name: 'AppzillonVersion', value: "3.6.0.S1" ],[$class: 'StringParameterValue', name: 'CustId', value: "APZCUSTOMERID" ]], propagate: true, wait: false)
            }
          }
}