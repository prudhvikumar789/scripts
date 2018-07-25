def lnxpass ='"p@ssw0rd"'
def lnxkey ='"ab:a9:b8:34:48:0b:a5:0d:e3:56:ae:5e:41:2f:41:9b"'
def buildnumber = env.BUILD_NUMBER
def Macpass ='"madmac"'
def Mackey ='"f8:bc:5c:a6:30:77:0b:61:c8:68:e7:d7:5a:25:ce:08"'
def sysdate =""
def nowdate=new Date().format( 'yyyyMMdd-HH-mm-ss' )
def buildstatus="Succeeded";
def lip = "192.168.1.6"
def mip = "192.168.1.74"
def TCresult=""

def emailNotification(buildstatus,sysdate,relnum,TCresult){
    def emailBody="";
     def recipentlist="";
  
    def binaryPath='\\'+'\\192.168.1.6\\appzillontesting\\'+"${relnum}"+'\\'+"${sysdate}"
    if("${buildstatus}"=='Succeeded'){
      recipentlist="preetish.das@i-exceed.com,prudhvi.kumar@i-exceed.com,darshanl.sagar@i-exceed.com,shyam.kumar@i-exceed.com,cc:chandu.balaji@i-exceed.com,cc:nagaraj.hiremath@i-exceed.com,cc:arthanarisamy@i-exceed.com,cc:prabudas.s@i-exceed.com" 
      // recipentlist="amar.khan@i-exceed.com" 
       emailBody= """<p style="color:#074207;">Hi All, <br><br> AppzillonStudio-3.6.0.S1 Build with Automation Testing <b style="color:#0487c9;"> SUCCEEDED </b>: <br><br>Click <a href='http://192.168.1.6/Report.html'>here </a>for Testcase Report <br>Click <a href='http://192.168.1.6/AppzillonTest.html'>here </a>for Testcase logs <br>Click <a href='http://192.168.5.254:9000/dashboard?id=com.iexceed.apz%3Aparent-appzillonstudio'>here </a>for Sonarqube Code Analysis Report <br><br>Job ${env.JOB_NAME} [${env.BUILD_NUMBER}] <br></br>Please find the latest build details below.<br><table border="1px solid black"><tr><td>Binary Path</td><td>${binaryPath}</td></tr></table><br><b>TestCase Matrix</b><br>${TCresult}<br> Check console output for testing at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a><br><br>Regards,<br><br>Appzillon IDE Team<br>I-Exceed Technology Solutions</p>""" 
    }
    else{
       recipentlist="preetish.das@i-exceed.com,prudhvi.kumar@i-exceed.com,darshanl.sagar@i-exceed.com,shyam.kumar@i-exceed.com,cc:chandu.balaji@i-exceed.com" 
       emailBody= """<p style="color:red;">Hi All, <br><br> AppzillonStudio-3.6.0.S1 Build with Automation Testing ${buildstatus}: Job ${env.JOB_NAME} [${env.BUILD_NUMBER}] <br><br> Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a><br><br>Regards,<br><br>Appzillon IDE Team<br>I-Exceed Technology Solutions</p>"""
    }
    emailext (
            subject: "Appzillon-3.6.0.S1 Build With Automation Testing ${buildstatus}",
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
     parameters {
        string(name: 'Project Path', defaultValue: 'Change Default Path', description: 'Path for Appzilon Project source code checkout!!')
        string(name: 'BuildPipelineNumber', defaultValue: 'LatestBuildNumber', description: 'Latest Build Number for AppzillonBuildPipline!!')
        string(name: 'AppzillonVersion', defaultValue: '3.6.0', description: 'Appzillon Version for Release!!',trim: true)
     }
    //triggers{cron('46 12 * * *')}
  /* ... unchanged ... */
  stages {
       stage ('Remove old App') {
      steps {
          echo 'hello'
         bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' superuser@192.168.1.6 '+" -batch -hostkey "+"${lnxkey}"+" "+"rm -rf /home/superuser/AppzAutomationTesting/apache-tomcat-8.0.30/webapps/*.war"+";"+"rm -rf /home/superuser/AppzAutomationTesting/apache-tomcat-8.0.30/webapps/AppzillonServer"+";rm -rf /home/superuser/AppzAutomationTesting/apache-tomcat-8.0.30/webapps/Testing" 
      }}
       stage ('HealthCheck Tomcate') {
      steps {
          script{
          echo 'tomcate start'
          def cmd ='"curl -Is http://192.168.1.6:8081|head -n1|grep 200"'
           bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' superuser@192.168.1.6 '+" -batch -hostkey "+"${lnxkey}"+" "+"if(${cmd}); then echo 'Apache is up and running'; else sh /home/superuser/AppzAutomationTesting/apache-tomcat-8.0.30/bin/startup.sh;fi"
          //bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' amar.khan@192.168.5.138 '+" -batch -hostkey "+"${lnxkey}"+" "+"if(${cmd}); then echo 'Apache is up and running'; else echo 'Starting Apache';fi"
    //  bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' amar.khan@192.168.5.138 '+" -batch -hostkey "+"${lnxkey}"+" "+'bash -c pwd'
      
   // bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' amar.khan@192.168.5.138 '+" -batch -hostkey "+"${lnxkey}"+" curl -s -o /dev/null -w %{http_code} http://localhost:8080"
}
      }
}  
    stage ('CommandLineBuild') {
      steps {
          echo 'build app'
          bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' superuser@192.168.1.6 '+" -batch -hostkey "+"${lnxkey}"+" "+"cp -a /AppzillonBuilds/${params.BuildPipelineNumber}/Appzillon /home/superuser/AppzAutomationTesting;cp -a /home/superuser/AppzAutomationTesting/Preferences/*.json /home/superuser/AppzAutomationTesting/Appzillon/Bin/Preferences/"
     bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' superuser@192.168.1.6 '+" -batch -hostkey "+"${lnxkey}"+" "+'cd /home/superuser/AppzAutomationTesting/Appzillon/lib;java -jar  appzillonstudio-commandbuild-ci.jar /home/superuser/AppzAutomationTesting/Appzillon/Bin/Preferences'
      }
}
  stage ('Deploy App') {
      steps {
          script{
          echo 'hi'
          def cmd ='"curl -Is http://192.168.1.6:8081|head -n1|grep 200"'
          def checkapphealth ='"curl -Is http://192.168.1.6:8081/Testing|head -n1|grep 403"'
         //  bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' amar.khan@192.168.1.151 '+" -batch -hostkey "+"${lnxkey}"+" "+"if(${cmd}); then echo 'Apache is up and running'; else sh /opt/apache-tomcat-8.0.30/bin/startup.sh;fi ;while(true); do if(${cmd});then break;else sleep 5;fi; done"
    //bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' amar.khan@192.168.1.151 '+" -batch -hostkey "+"${lnxkey}"+" "+'cp /home/i-exceed.com/amar.khan/Documents/testing/Project/BaseApp/bin/Web/*.war /home/i-exceed.com/amar.khan/Documents/testing/Project/BaseApp/bin/Server/*.war /opt/apache-tomcat-8.0.30/webapps'
      bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' superuser@192.168.1.6 '+" -batch -hostkey "+"${lnxkey}"+" "+"if(${cmd}); then echo 'Apache is up and running'; else sh /home/superuser/AppzAutomationTesting/apache-tomcat-8.0.30/bin/startup.sh;fi ;while(true); do if(${cmd});then break;else sleep 5;fi; done"
      bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' superuser@192.168.1.6 '+" -batch -hostkey "+"${lnxkey}"+" "+'cp /home/superuser/AppzAutomationTesting/Project/BaseApp/bin/Web/*.war /home/superuser/AppzAutomationTesting/Project/BaseApp/bin/Server/*.war /home/superuser/AppzAutomationTesting/apache-tomcat-8.0.30/webapps'
      bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' superuser@192.168.1.6 '+" -batch -hostkey "+"${lnxkey}"+" "+"if(${checkapphealth}); then echo 'Application is up and running'; else echo 'waiting for application to come up....';fi ;while(true); do if(${checkapphealth});then break;else sleep 5;fi; done"
}
      }
}   
stage('Virtual Display'){
    steps{
        script{
            echo 'hi'
            def startvdipcmd='''"echo p@ssw0rd | sudo -S sh /home/superuser/AppzAutomationTesting/Xvfb.sh start"'''
            checkxvfbcmd="pidof /usr/bin/Xvfb"
            def checkvirtualcmd="if(${checkxvfbcmd}); then echo '****Xvfb Server is up and running****'; else ${startvdipcmd};fi ;"
            bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' superuser@192.168.1.6 '+" -batch -hostkey "+"${lnxkey}"+" "+"${checkvirtualcmd}"
        }
       
     //  bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' amar.khan@192.168.5.138 '+" -batch -hostkey "+"${lnxkey}"+" "+'export DISPLAY=:99'
//       chmod +x Xvfb.sh
// sh Xvfb.sh start 
    }
}
stage ('Reg Testing') {
      steps {
          echo 'hi'
    //  bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' amar.khan@192.168.1.151 '+" -batch -hostkey "+"${lnxkey}"+" "+'cd /home/i-exceed.com/amar.khan/Documents/workpace/3.6.0.S1/Studio/appzillonstudio-client/Appzillon/AppzillonDefaults/AppzillonAppTestProject/appzillon-test;export DISPLAY=:99;sleep 5;mvn exec:java'
// bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' amar.khan@192.168.1.151 '+" -batch -hostkey "+"${lnxkey}"+" "+'cd /home/i-exceed.com/amar.khan/Documents/testing;export DISPLAY=:99;sleep 5;pybot login.robot'
bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' superuser@192.168.1.6 '+" -batch -hostkey "+"${lnxkey}"+" "+'''export PATH=$PATH:/home/superuser/AppzAutomationTesting/apache-maven-3.5.2/bin;'''+'export DISPLAY=:99;sleep 5;cd /home/superuser/AppzAutomationTesting/appzillonstudio-client/Appzillon/AppzillonDefaults/AppzillonAppTestProject/appzillon-test;pwd;echo $DISPLAY'+';'+'"ps aux|grep Xvfb"'+';'+'"mvn exec:java 2>&1 | tee /tmp/AppzillonTest.html"'
      }
       post {
             success{
                 script{
                 sysdate="${nowdate}"
                 bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' superuser@192.168.1.6 '+" -batch -hostkey "+"${lnxkey}"+" "+"mkdir -p /AppzillonTesting/"+"${params.AppzillonVersion}/"+"${sysdate}"
                 }
             }
    failure {
         script { buildstatus="Failed @Packaging Stage" }
       emailNotification("${buildstatus}","${sysdate}","${params.AppzillonVersion}")
    }
  }
}
         stage('Binary Sharing') { 
            steps{
                parallel(
                  linux: {
                    echo "deb sharing"
                    //appzillonstudio-client/build/bundles
                    // bat 'cd d:\\softs && plink -pw '+"${pass}"+' superuser@192.168.1.6 '+" -batch -hostkey "+"${key}"+" "+'cd /AppzillonBuilds/'+"${buildnumber}"+'/appzillonstudio-client;ant'
                 bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' superuser@192.168.1.6 '+" -batch -hostkey "+"${lnxkey}"+" "+"cp -a /AppzillonBuilds/${params.BuildPipelineNumber}/appzillonstudio-client/build/bundles/*.deb /AppzillonTesting/${params.AppzillonVersion}/${sysdate};cp -a /AppzillonBuilds/${params.BuildPipelineNumber}/Appzillon /AppzillonTesting/${params.AppzillonVersion}/${sysdate}"
                    // bat 'cd d:\\softs && pscp -r -pw '+"${pass}"+" -batch -hostkey "+"${key}"+" "+"${workspace}\\${defaulcotpath}\\appzillonstudio-client"+" "+"${workspace}\\${defaulcotpath}\\ClientJars"+" "+"${workspace}\\${defaulcotpath}\\KeyStore"+' superuser@192.168.1.6:/AppzillonBuilds/'+"${buildnumber}"
                 
                  },
                  window: {
                    echo "exe sharing"
                        bat 'cd d:\\softs && pscp -r -pw '+"${lnxpass}"+" -batch -hostkey "+"${lnxkey}"+" "+"D:\\jenkins\\workspace\\AppzillonPipeLine-Test\\${params.BuildPipelineNumber}\\appzillonstudio-client\\build\\bundles\\*.exe"+" superuser@192.168.1.6:/AppzillonTesting/${params.AppzillonVersion}/${sysdate}/"
                  },
                  mac: {
                    echo "mac sharing"
                      bat 'cd d:\\softs && plink -pw '+"${Macpass}"+' admin@192.168.1.74 '+" -batch -hostkey "+"${Mackey}"+" "+"/usr/local/bin/sshpass -p "+"${lnxpass}"+" scp -r "+"/AppzillonBuilds/${params.BuildPipelineNumber}/appzillonstudio-client/build/bundles/*.dmg superuser@192.168.1.6:/AppzillonTesting/${params.AppzillonVersion}/${sysdate}"
                    //  bat 'cd d:\\softs && plink -pw '+"${Macpass}"+' admin@192.168.1.32 '+" -batch -hostkey "+"${Mackey}"+" "+'/Applications/apache-ant-1.9.4/bin/ant -f /AppzillonBuilds/'+"${buildnumber}"+'/appzillonstudio-client'
                  // bat 'cd d:\\softs && plink -pw '+"${Macpass}"+' admin@192.168.1.32 '+" -batch -hostkey "+"${Mackey}"+" "+"sshpass -p "+"${key}"+" scp -r"
                    // bat 'cd d:\\softs && pscp -r -pw '+"${pass}"+" -batch -hostkey "+"${key}"+" "+"${workspace}\\${defaulcotpath}\\appzillonstudio-client"+" "+"${workspace}\\${defaulcotpath}\\ClientJars"+" "+"${workspace}\\${defaulcotpath}\\KeyStore"+' amar.khan@192.168.1.178:/tmp/test/1'
                  }
                )
             
        }
         post {
    failure {
         script { buildstatus="Failed @Packaging Stage" }
       // emailNotification("${buildstatus}","${sysdate}","${params.AppzillonVersion}")
    }
  }
        }
stage('Report Generation'){
    steps{
         script{
             def newline='''"sed -e 's/$/<br>/' -i /tmp/AppzillonTest.html"'''
    def suffix='''"echo '</p></body></html>' >> /tmp/AppzillonTest.html"'''
    def exposetestcmd='''"echo p@ssw0rd | sudo -S cp /tmp/AppzillonTest.html /tmp/Report.html /var/www/git"'''
    def prefix='''"echo '<html><head><title>Appzillon Test Logs</title></head><body><h1 style="background-color:Yellow;" align="center" ><font color="SlateBlue">Appzillon Testcase Log</font></h1> <p style="background-color:LightGreen;"><button type="button" onclick="window.location.href='/tmp/test.html'" style="float: right;">Report</button>' | cat - /tmp/AppzillonTest.html > temp && mv temp /tmp/AppzillonTest.html"'''
    // def prefix='''"echo '<html><head><title>Appzillon Test Logs</title></head><body><h1 style="background-color:Yellow;" align="center" ><font color="SlateBlue">Appzillon Testcase Log</font></h1> <p style="background-color:LightGreen;">' | cat - /tmp/AppzillonTest.html > temp && mv temp /tmp/AppzillonTest.html"'''
 bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' superuser@192.168.1.6 '+" -batch -hostkey "+"${lnxkey}"+" "+"cd /home/superuser/AppzAutomationTesting;./reportGenScript.sh"+';'+"${exposetestcmd}"
 bat '>TCresult.txt (cd d:\\softs && plink -pw '+"${lnxpass}"+' superuser@192.168.1.6 '+" -batch -hostkey "+"${lnxkey}"+" "+'cd /home/superuser/AppzAutomationTesting;./emailreport.sh)'

TCresult = readFile "${env.WORKSPACE}/TCresult.txt"
echo "${TCresult}"
//     def workspace = env.WORKSPACE
//     def serverResultPath="${workspace}\\${buildnumber}\\robot\\report";
//     bat "if not exist ${serverResultPath} mkdir ${serverResultPath}"
//     bat 'cd d:\\softs && plink -pw '+"${Macpass}"+' amar.khan@192.168.5.138 '+" -batch -hostkey "+"${Mackey}"+" "+'cat /home/i-exceed.com/amar.khan/Documents/testing/output.xml>'+"${serverResultPath}\\"+'output.xml'
//      bat 'cd d:\\softs && plink -pw '+"${Macpass}"+' amar.khan@192.168.5.138 '+" -batch -hostkey "+"${Mackey}"+" "+'cat /home/i-exceed.com/amar.khan/Documents/testing/log.html>'+"${serverResultPath}\\"+'log.html'
//       bat 'cd d:\\softs && plink -pw '+"${Macpass}"+' amar.khan@192.168.5.138 '+" -batch -hostkey "+"${Mackey}"+" "+'cat /home/i-exceed.com/amar.khan/Documents/testing/report.html>'+"${serverResultPath}\\"+'report.html'
}
}
    
}

stage('Cleanup Action'){
    steps{
        
    script{
        bat 'cd d:\\softs && plink -pw '+"${lnxpass}"+' superuser@192.168.1.6 '+" -batch -hostkey "+"${lnxkey}"+" "+"sh /home/superuser/AppzAutomationTesting/apache-tomcat-8.0.30/bin/shutdown.sh"
    }
      //---  emailNotification('Success')
}
   
}
      
  }
      post {
    success {
        //build(job: "Appzillon-RegressionTesting", parameters: [[$class: 'StringParameterValue', name: 'BuildPipelineNumber', value: "${buildnumber}" ],[$class: 'StringParameterValue', name: 'AppzillonVersion', value: "${params.AppzillonVersion}" ],], propagate: true, wait: false)
        emailNotification("${buildstatus}","${sysdate}","${params.AppzillonVersion}","${TCresult}")
    }
  } 
  }
