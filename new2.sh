stage('check server') {
            steps {
                statuscode=$(sh ping 192.168.1.6 | grep '64 bytes from 192.168.1.6:')
                if ["$statuscode" == "64 bytes from 192.168.1.6:"]
                    then
                        echo "Tomact is up and Running"
                    else
                        echo "Tomact is not up and Running"
                    fi 
                  
            }
        }




        ping -c 1 172.17.0.3 &> /dev/null && echo Container is Running. || echo Container NOT Running.












pipeline {
    agent any
    stages {
        stage('Param') {
            steps {
                echo "${version}"



                
            }
        }
    }
    post { 
        always { 
            echo 'I will always say Hello again!'
        }
    }
}