node {
    def GIT_URL = 'https://github.com/sdaschner/hello-openshift.git'
    def PROJECT_NUMBER = '15'

    timestamps {
        stage('checkout') {
            gitCheckout(GIT_URL)
        }

        stage('package') {
            withEnv(["JAVA_HOME=${tool "jdk8"}", "PATH+MVN=${tool name: "Maven3", type: 'maven'}/bin"]) {
                sh "mvn clean package"
            }
        }

        stage('archive results') {
            junit '**/target/surefire-reports/TEST-*.xml'
        }

        stage('build image') {
            openShiftStartBuild(env.BUILD_NUMBER, "training${PROJECT_NUMBER}")
        }

        stage('deploy staging') {
            String imageReference = "172.30.1.1:5000/training${PROJECT_NUMBER}/hello-openshift:build-${env.BUILD_NUMBER}"
            echo "updating OpenShift deployment to ${imageReference}"

            sh "sed -i 's#172.30.1.1:5000/training" + PROJECT_NUMBER + "/hello-openshift:build-[0-9]\\+#$imageReference#' deployment/01_deployment.yaml"
            echo "updated deployment/01_deployment.yaml"
            sh "cat deployment/01_deployment.yaml"

            openShiftDeploy("training${PROJECT_NUMBER}")
        }

    }

}


//////////////////////

def gitCheckout(String url, String credentialsId = 'github-enterprise', String branch = '*/main', boolean doGenerateSubmoduleConfigurations = false) {
    checkout([
        $class                           : 'GitSCM',
        branches                         : [[name: branch]],
        doGenerateSubmoduleConfigurations: doGenerateSubmoduleConfigurations,
        extensions                       : [],
        submoduleCfg                     : [],
        userRemoteConfigs                : [[
                credentialsId : credentialsId,
                url           : url
        ]]
    ])
}

def openShiftStartBuild(String tag, String project) {
    withEnv(["KUBECONFIG=${env.WORKSPACE}/.kube/config.${env.BUILD_NUMBER}"]) {
        String apiUrl = 'https://158.177.141.244:8443/'
        try {
            withCredentials([[
                                     $class          : 'UsernamePasswordMultiBinding',
                                     credentialsId   : "openshift",
                                     usernameVariable: 'USERNAME',
                                     passwordVariable: 'PASSWORD'
                             ]]) {
                sh "oc login ${apiUrl} -u \"${USERNAME}\" -p \"${PASSWORD}\" --insecure-skip-tls-verify"
            }

            sh "oc project ${project}"
            sh "oc process hello-openshift-build IMAGE_TAG=${tag}| oc apply -f -"
            sh "oc start-build hello-openshift-build-${tag} --from-dir='.' --request-timeout='3m' --wait --follow"

        } finally {
            sh "if [ -e '${KUBECONFIG}' ]; then rm '${KUBECONFIG}'; fi"
        }
    }
}

def openShiftDeploy(String project) {
    withEnv(["KUBECONFIG=${env.WORKSPACE}/.kube/config.${env.BUILD_NUMBER}"]) {
        String apiUrl = 'https://158.177.141.244:8443/'
        try {
            withCredentials([[
                                     $class          : 'UsernamePasswordMultiBinding',
                                     credentialsId   : "openshift",
                                     usernameVariable: 'USERNAME',
                                     passwordVariable: 'PASSWORD'
                             ]]) {
                sh "oc login ${apiUrl} -u \"${USERNAME}\" -p \"${PASSWORD}\" --insecure-skip-tls-verify"
            }

            sh "oc project ${project}"
            sh "oc apply -f deployment/"
            sh "oc rollout status deployments hello-openshift"

        } finally {
            sh "if [ -e '${KUBECONFIG}' ]; then rm '${KUBECONFIG}'; fi"
        }
    }
}
