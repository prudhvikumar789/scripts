def defaulcotpath=""
def sysdate =""
def nowdate=new Date().format( 'yyyyMMdd-HH-mm-ss' )
def pass ='"p@ssw0rd"'
def key ='"ab:a9:b8:34:48:0b:a5:0d:e3:56:ae:5e:41:2f:41:9b"'
def buildnumber = env.BUILD_NUMBER
def Macpass ='"madmac"'
def Mackey ='"f8:bc:5c:a6:30:77:0b:61:c8:68:e7:d7:5a:25:ce:08"'
def lip = "192.168.1.6"
def mip = "192.168.1.74"
//Function Notify Email
def buildstatus="Success";
def emailNotification(buildstatus,sysdate,relnum){
    def emailBody="";
     def recipentlist="";
  
    def binaryPath='\\'+'\\192.168.1.6\\appzillontesting\\'+"${relnum}"+'\\'+"${sysdate}"
    if("${buildstatus}"=='Started'){
      recipentlist="preetish.das@i-exceed.com,prudhvi.kumar@i-exceed.com,darshanl.sagar@i-exceed.com,shyam.kumar@i-exceed.com,cc:chandu.balaji@i-exceed.com" 
    // recipentlist="prudhvi.kumar@i-exceed.com"
    //   emailBody= """<p>Hi All,</p><p>AppzillonStudio-3.6.0 build triggered.<br>Please find the latest build details below.</p><p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME}[${env.BUILD_NUMBER}]</a></p><p>Regards,<br>Appzillon DevTeam<br>I-Exceed Technology Solutions</p>"""
    emailBody= """<p>Hi All,</p><p>AppzillonStudio-3.6.0.S1 build <b style="color:#04c98d;"> STARTED </b>.<br><br>Please find the latest build details below.</p><p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME}[${env.BUILD_NUMBER}]</a></p><p>Regards,<br>Appzillon DevTeam<br>I-Exceed Technology Solutions</p>"""
    }
    else if("${buildstatus}"=='Success'){
      recipentlist="preetish.das@i-exceed.com,prudhvi.kumar@i-exceed.com,darshanl.sagar@i-exceed.com,shyam.kumar@i-exceed.com,cc:chandu.balaji@i-exceed.com,cc:nagaraj.hiremath@i-exceed.com,cc:arthanarisamy@i-exceed.com,cc:prabudas.s@i-exceed.com" 
    //   recipentlist="prudhvi.kumar@i-exceed.com"
      emailBody= """<p>Hi All,</p><p>Please find the latest build details below.<ul><ui><b>Path: ${binaryPath}</b></ui></ul></p><p>Regards,<br>Appzillon DevTeam<br>I-Exceed Technology Solutions</p>""" 
    }
    else{
      recipentlist="preetish.das@i-exceed.com,prudhvi.kumar@i-exceed.com,darshanl.sagar@i-exceed.com,shyam.kumar@i-exceed.com,cc:chandu.balaji@i-exceed.com" 
    //   recipentlist="prudhvi.kumar@i-exceed.com"
      emailBody= """<p style="color:red;">Hi All, <br><br> AppzillonStudio-3.6.0.S1 build ${buildstatus}: Job ${env.JOB_NAME} [${env.BUILD_NUMBER}] <br><br> Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a><br><br>Regards,<br><br>Appzillon IDE Team<br>I-Exceed Technology Solutions</p>"""
    }
    emailext (
            subject: "Appzillon-3.6.0.S1 Build ${buildstatus}",
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
    // triggers{cron('H 8 * * *')}
    parameters {
        string(name: 'CheckoutPath', defaultValue: 'Change Default Path', description: 'Path for Appzilon source code checkout!!', trim: true)
        string(name: 'AppzillonVersion', defaultValue: '3.6.0', description: 'Appzillon Version for Release!!',trim: true)
       choice(choices: 'APZCUSTOMERID\nBARCLAYS\nIPPB', description: 'Select Appzillon Customer!!', name: 'CustId')
    //   choice(choices: 'US-EAST-1\nUS-WEST-2', description: 'What AWS region?', name: 'region')
       booleanParam(
         defaultValue: false,
         description: 'For 32 bit Systems',
         name: '32 bit'
       )
       booleanParam(
         defaultValue: false,
         description: 'For 64 bit Systems',
         name: '64 bit'
       )
        booleanParam(
         defaultValue: false,
         description: 'Licence required ?',
         name: 'Licence'
       )
    }
    stages { 
        stage('Checkout') { 
            steps { 
                 withCredentials([string(credentialsId: 'svn-credentials-id', variable: 'TOKEN')]) {
                     script{
                         //def buildnumber = env.BUILD_NUMBER
                         emailNotification("Started","${sysdate}","${params.AppzillonVersion}")
                         echo "${buildnumber}"
                         if("${params.CheckoutPath}"=='Change Default Path'){
                             defaulcotpath="${buildnumber}"
                      //  bat 'svn export --username amar.khan http://192.168.1.1/svn/Appzillon/3.6.0.S1/Studio '+"${buildnumber}"+' --password '+"${TOKEN}" 
                         }else
                         {
                              defaulcotpath="${params.CheckoutPath}"
                        //   bat 'svn export --username amar.khan http://192.168.1.1/svn/Appzillon/3.6.0.S1/Studio '+"${params.CheckoutPath}"+' --password '+"${TOKEN}"   
                         }
                         bat 'svn export --username amar.khan http://192.168.1.1/svn/Appzillon/3.6.0.S1/Studio '+"${defaulcotpath}"+' --password '+"${TOKEN}"   
                     }
                 }
            }
                     post {
            failure {
                 script { buildstatus="Failed @Checkout Stage" }
                emailNotification("${buildstatus}","${sysdate}","${params.AppzillonVersion}")
            }
          }
        }
        stage('Clean') { 
            steps{
             withMaven(maven : 'mvn-3.1.0'){
                bat "cd ${defaulcotpath} && mvn clean"
        }
        }
        }
         stage('Versioning') { 
            steps{
                script {
              def workspace = env.WORKSPACE
              def val1 = "'APZPRODUCTVERSION'"
              def val2 = "'"+ "${params.AppzillonVersion}"+"'"
              def path = "${workspace}\\${defaulcotpath}\\appzillonstudio-core\\src\\main\\java\\com\\iexceed\\apz\\core\\Appzillon.java"
              def winpath = "${workspace}\\${defaulcotpath}\\appzillonstudio-client\\package\\windows\\Appzillon.iss"
              def command = "(gc ${path}) -replace ${val1}, ${val2} | sc ${path}"
              def wincommand = "(gc ${winpath}) -replace ${val1}, ${val2} | sc ${winpath}"
              def com = '"' + "${command}" + '"' 
              def wincom = '"' + "${wincommand}" + '"' 
              
              bat 'powershell -Command ' + "${com}"
              bat 'powershell -Command ' + "${wincom}"
              echo "${command}"
           
           }
        }
         post {
    failure {
         script { buildstatus="Failed @Versioning Stage" }
        emailNotification("${buildstatus}","${sysdate}","${params.AppzillonVersion}")
    }
  }
        }
        stage('Set CustID&Licence') { 
            steps{
           script{
              def val1 = "'APZCUSTOMERID'"
              def val2 = "${params.CustId}"
             if("${params.CustId}"!='APZCUSTOMERID'){
                 def path = "${workspace}\\${defaulcotpath}\\appzillonstudio-core\\src\\main\\java\\com\\iexceed\\apz\\core\\Appzillon.java"
                 def command = "(gc ${path}) -replace ${val1}, '${val2}' | sc ${path}"
                 def com = '"' + "${command}" + '"' 
                 bat 'powershell -Command ' + "${com}"
             }  
             if("${params.Licence}"=="true"){
              def tagstart = "private static String fffbuild"
              def newtagstart = "//private static String fffbuild"
              def tagstartwith4 = "////private static String fffbuild"
              def paths = "${workspace}\\${defaulcotpath}\\appzillonstudio-client\\src\\main\\java\\com\\iexceed\\apz\\ui\\core\\AppzillonUI.java"
              def commands = "(gc ${paths}) -replace '${tagstart}', '${newtagstart}' | sc ${paths};(gc ${paths}) -replace '${tagstartwith4}', '${tagstart}' | sc ${paths}"
              echo "${commands}"
              def coms = '"' + "${commands}" + '"' 
              bat 'powershell -Command ' + "${coms}"
             }
           } 
        }
         post {
    failure {
         script { buildstatus="Failed @Setting CustID" }
        emailNotification("${buildstatus}","${sysdate}","${params.AppzillonVersion}")
    }
  }
        }
        stage('Build') { 
            steps{
            withMaven(maven : 'mvn-3.1.0'){
                bat "cd ${defaulcotpath} && mvn install"
            }
        }
         post {
    failure {
         script { buildstatus="Failed @Build Stage" }
        emailNotification("${buildstatus}","${sysdate}","${params.AppzillonVersion}")
    }
  }
        }
                stage('SonarQube analysis') { 
            steps{
                echo 'sonar check'
            withSonarQubeEnv('sonarcubegate'){
                bat "cd ${defaulcotpath} && mvn sonar:sonar"
            }
        }
         post {
    failure {
         script { buildstatus="Failed @SonarQube analysis" }
        emailNotification("${buildstatus}","${sysdate}","${params.AppzillonVersion}")
    }
  }
        }
        stage("Quality Gate"){
            steps {
                script{
                    echo 'check'
        //   timeout(time: 2, unit: 'MINUTES') {
        //       def qg = waitForQualityGate()
        //       if (qg.status != 'OK') {
        //           error "Pipeline aborted due to quality gate failure: ${qg.status}"
        //       }
        //   }
                    
                }
            }
      }
        stage('SignIn Comment'){
            steps{
                script{
                      def tagstart = "'<exec'"
              def newtagstart = "'<!-- <exec'"
              def tagend = "'</exec>'"
              def newtagend = "'</exec> -->'"
              def path = "${workspace}\\${defaulcotpath}\\appzillonstudio-client\\build.xml"
              def command = "(gc ${path}) -replace ${tagstart}, ${newtagstart} | sc ${path} ;(gc ${path}) -replace ${tagend}, ${newtagend} | sc ${path}"
              def com = '"' + "${command}" + '"' 
              bat 'powershell -Command ' + "${com}"
                }
            }
             post {
    failure {
         script { buildstatus="Failed @SignIn Stage" }
        emailNotification("${buildstatus}","${sysdate}","${params.AppzillonVersion}")
    }
  }
        }
        stage('Jar Distribution'){
            steps{
               parallel(
                  a: {
                    echo "JAR Distribution over linux server"
                     bat 'cd d:\\softs && plink -pw '+"${pass}"+' superuser@192.168.1.6 '+" -batch -hostkey "+"${key}"+" "+"mkdir /AppzillonBuilds/"+"${buildnumber}"
                     bat 'cd d:\\softs && pscp -r -pw '+"${pass}"+" -batch -hostkey "+"${key}"+" "+"${workspace}\\${defaulcotpath}\\appzillonstudio-client"+" "+"${workspace}\\${defaulcotpath}\\ClientJars"+" "+"${workspace}\\${defaulcotpath}\\KeyStore"+" "+"${workspace}\\${defaulcotpath}\\appzillonstudio-commandbuild\\target\\Appzillon"+' superuser@192.168.1.6:/AppzillonBuilds/'+"${buildnumber}"
                  },
                  b: {
                    echo "JAR Distribution over MAC"
                    bat 'cd d:\\softs && plink -pw '+"${Macpass}"+' admin@192.168.1.74 '+" -batch -hostkey "+"${Mackey}"+" "+"mkdir /AppzillonBuilds/"+"${buildnumber}"
                    bat 'cd d:\\softs && pscp -r -pw '+"${Macpass}"+" -batch -hostkey "+"${Mackey}"+" "+"${workspace}\\${defaulcotpath}\\appzillonstudio-client"+" "+"${workspace}\\${defaulcotpath}\\ClientJars"+" "+"${workspace}\\${defaulcotpath}\\KeyStore"+' admin@192.168.1.74:/AppzillonBuilds/'+"${buildnumber}"
                  }
                )
            }
             post {
    failure {
         script { buildstatus="Failed @Jar Distribution Stage" }
        emailNotification("${buildstatus}","${sysdate}","${params.AppzillonVersion}")
    }
    success{
        script{ def psid='"pgrep Appzillon | xargs kill -1"'
             bat 'cd d:\\softs && plink -pw '+"${Macpass}"+' admin@192.168.1.74 '+" -batch -hostkey "+"${Mackey}"+" "+"$psid"
        }
    }
  }
        }
        stage('Packaging') { 
            steps{
                parallel(
                  linux: {
                    echo "deb packaging"
                    bat 'cd d:\\softs && plink -pw '+"${pass}"+' superuser@192.168.1.6 '+" -batch -hostkey "+"${key}"+" "+'cd /AppzillonBuilds/'+"${buildnumber}"+'/appzillonstudio-client;ant'
                  },
                  window: {
                    echo "exe packaging"
                      bat "cd ${defaulcotpath}/appzillonstudio-client && ant"
                  },
                  mac: {
                    echo "mac packaging"
                    //  bat 'cd d:\\softs && plink -pw '+"${Macpass}"+' admin@192.168.1.32 '+" -batch -hostkey "+"${Mackey}"+" "+'/Applications/apache-ant-1.9.4/bin/ant -f /AppzillonBuilds/'+"${buildnumber}"+'/appzillonstudio-client'
                    bat 'cd d:\\softs && plink -pw '+"${Macpass}"+' admin@192.168.1.74 '+" -batch -hostkey "+"${Mackey}"+" "+'cd /AppzillonBuilds/'+"${buildnumber}"+'/appzillonstudio-client;/Applications/apache-ant-1.9.4/bin/ant'
                    // bat 'cd d:\\softs && pscp -r -pw '+"${pass}"+" -batch -hostkey "+"${key}"+" "+"${workspace}\\${defaulcotpath}\\appzillonstudio-client"+" "+"${workspace}\\${defaulcotpath}\\ClientJars"+" "+"${workspace}\\${defaulcotpath}\\KeyStore"+' amar.khan@192.168.1.178:/tmp/test/1'
                  }
                )
             
        }
         post {
             success{
                 script{
                 sysdate="${nowdate}"
                 bat 'cd d:\\softs && plink -pw '+"${pass}"+' superuser@192.168.1.6 '+" -batch -hostkey "+"${key}"+" "+"mkdir -p /AppzillonTesting/"+"${params.AppzillonVersion}/"+"${sysdate}"
                 }
             }
    failure {
         script { buildstatus="Failed @Packaging Stage" }
        emailNotification("${buildstatus}","${sysdate}","${params.AppzillonVersion}")
    }
  }
        }
//          stage('Binary Sharing') { 
//             steps{
//                 parallel(
//                   linux: {
//                     echo "deb sharing"
//                     //appzillonstudio-client/build/bundles
//                     // bat 'cd d:\\softs && plink -pw '+"${pass}"+' superuser@192.168.1.6 '+" -batch -hostkey "+"${key}"+" "+'cd /AppzillonBuilds/'+"${buildnumber}"+'/appzillonstudio-client;ant'
//                  bat 'cd d:\\softs && plink -pw '+"${pass}"+' superuser@192.168.1.6 '+" -batch -hostkey "+"${key}"+" "+"cp -a /AppzillonBuilds/${buildnumber}/appzillonstudio-client/build/bundles/*.deb /AppzillonTesting/${params.AppzillonVersion}/${sysdate};cp -a /AppzillonBuilds/${buildnumber}/Appzillon /AppzillonTesting/${params.AppzillonVersion}/${sysdate}"
//                     // bat 'cd d:\\softs && pscp -r -pw '+"${pass}"+" -batch -hostkey "+"${key}"+" "+"${workspace}\\${defaulcotpath}\\appzillonstudio-client"+" "+"${workspace}\\${defaulcotpath}\\ClientJars"+" "+"${workspace}\\${defaulcotpath}\\KeyStore"+' superuser@192.168.1.6:/AppzillonBuilds/'+"${buildnumber}"
                 
//                   },
//                   window: {
//                     echo "exe sharing"
//                         bat 'cd d:\\softs && pscp -r -pw '+"${pass}"+" -batch -hostkey "+"${key}"+" "+"${workspace}\\${defaulcotpath}\\appzillonstudio-client\\build\\bundles\\*.exe"+" superuser@192.168.1.6:/AppzillonTesting/${params.AppzillonVersion}/${sysdate}/"
//                   },
//                   mac: {
//                     echo "mac sharing"
//                       bat 'cd d:\\softs && plink -pw '+"${Macpass}"+' admin@192.168.1.32 '+" -batch -hostkey "+"${Mackey}"+" "+"/usr/local/bin/sshpass -p "+"${pass}"+" scp -r "+"/AppzillonBuilds/${buildnumber}/appzillonstudio-client/build/bundles/*.dmg superuser@192.168.1.6:/AppzillonTesting/${params.AppzillonVersion}/${sysdate}"
//                     //  bat 'cd d:\\softs && plink -pw '+"${Macpass}"+' admin@192.168.1.32 '+" -batch -hostkey "+"${Mackey}"+" "+'/Applications/apache-ant-1.9.4/bin/ant -f /AppzillonBuilds/'+"${buildnumber}"+'/appzillonstudio-client'
//                   // bat 'cd d:\\softs && plink -pw '+"${Macpass}"+' admin@192.168.1.32 '+" -batch -hostkey "+"${Mackey}"+" "+"sshpass -p "+"${key}"+" scp -r"
//                     // bat 'cd d:\\softs && pscp -r -pw '+"${pass}"+" -batch -hostkey "+"${key}"+" "+"${workspace}\\${defaulcotpath}\\appzillonstudio-client"+" "+"${workspace}\\${defaulcotpath}\\ClientJars"+" "+"${workspace}\\${defaulcotpath}\\KeyStore"+' amar.khan@192.168.1.178:/tmp/test/1'
//                   }
//                 )
             
//         }
//          post {
//     failure {
//          script { buildstatus="Failed @Packaging Stage" }
//         emailNotification("${buildstatus}","${sysdate}","${params.AppzillonVersion}")
//     }
//   }
//         }
 
    }
     post {
    success {
        build(job: "Appzillon-RegressionTesting", parameters: [[$class: 'StringParameterValue', name: 'BuildPipelineNumber', value: "${buildnumber}" ],[$class: 'StringParameterValue', name: 'AppzillonVersion', value: "${params.AppzillonVersion}" ],], propagate: true, wait: false)
        //emailNotification("${buildstatus}","${sysdate}","${params.AppzillonVersion}")
    }
  }
}