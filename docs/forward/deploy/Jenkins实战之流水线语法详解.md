# Jenkins实战之流水线语法详解

## 一. 简介

> Pipeline支持两种语法： Declarative Pipeline（声明式pipeline，在pipeline2.5中引入，结构化方式）和Scripted Pipeline（脚本式pipeline），两者都支持建立连续输送的Pipeline。

相关资料：

- [jenkins-scripted-pipeline-or-declarative-pipeline](https://stackoverflow.com/questions/43484979/jenkins-scripted-pipeline-or-declarative-pipeline)
- [Declarative-pipelines-vs-scripted](http://jenkins-ci.361315.n4.nabble.com/Declarative-pipelines-vs-scripted-td4891792.html)

声明式Pipeline是后续Open Blue Ocean所支持类型，建议使用声明式Pipeline的方式进行编写，从jenkins社区动向看，很明显这种语法结构会是未来的趋势。

- 声明式pipeline可以内嵌脚本式pipeline
- 声明式pipeline必须包含在固定格式的pipeline{}内
- 块（Block{}）： 只能包含章节Sections，指令Directives，步骤Steps或者赋值语句
- 章节（Sections）： 通常包括一个或多个指令或步骤，如agent，post，stages，steps
- 指令（Directives）： environment，options，parameters，triggers，stage，tools，when
- 步骤（steps）： 执行脚本式pipeline，如script{}

## 二. Declarative Pipeline（声明式pipeline）

### Sections（章节）

#### agent

Pipeline或特定阶段将在Jenkins环境中执行的位置，具体取决于该agent 部分的放置位置；必须在pipeline顶层定义。 参数：

- any: 在任何可用的agent 上执行Pipeline或stage。例如：agent any
- none: 当在pipeline块的顶层使用none时，将不会为整个Pipeline运行分配全局agent ，每个stage部分将需要包含其自己的agent部分。
- label: 使用有label标签的agent，例如：agent { label 'my-defined-label' }
- node: agent { node { label 'labelName' } }，等同于 agent { label 'labelName' }，但node允许其他选项（如customWorkspace）。
- docker: 动态供应一个docker节点去执行pipeline或stage，docker还可以接受一个args，直接传递给docker run调用。

```text
agent {
    docker {
        image 'maven:3-alpine'
        label 'my-defined-label'
        args  '-v /tmp:/tmp'
    }
}
```

- dockerfile: Dockerfile源存储库中包含的容器来构建执行Pipeline或stage。使用此参数，jenkinsfile必须从代码中加载使用“pipeline from SCM”或者“Multibranch
  Pipeline”加载

> 默认是Dockerfile在根目录： agent { dockerfile true } 如果Dockerfile在另一个目录，使用dir参数： agent { dockerfile { dir 'someSubDir' } } 可以使用docker build添加参数： agent { dockerfile { additionalBuildArgs '--build-arg foo=bar' } }

```text
pipeline {
    agent { dockerfile true }
    stages {
        stage('Test') {
            steps {
                sh 'node --version'
                sh 'svn --version'
            }
        }
    }
}
```

常用选项：

- label： 一个字符串，选择哪个特定的label标签，此选项适用于node，docker和dockerfile，并且 node是必需的。
- customWorkspace： 一个字符串， 自定义工作空间，可以使相对路径，也可以是绝对路径。

```text
agent {
    node {
        label 'my-defined-label'
        customWorkspace '/some/other/path'
    }
}
```

- reuseNode： 一个布尔值，默认false，如果为true，在同一工作空间中，适用于docker和dockerfile，并且仅在 单个的stage中使用agent才有效。

```text
pipeline {
//Execute all the steps defined in this Pipeline within a newly created container of the given name and tag (maven:3-alpine).
    agent { docker 'maven:3-alpine' }
    stages {
        stage('Example Build') {
            steps {
                sh 'mvn -B clean verify'
            }
        }
    }
}
```

```text
pipeline {
//使用多个代理，pipeline顶层agent none，每个stage有各自的agent代理
    agent none
    stages {
        stage('Example Build') {
            agent { docker 'maven:3-alpine' }
            steps {
                echoField 'Hello, Maven'
                sh 'mvn --version'
            }
        }
        stage('Example Test') {
            agent { docker 'openjdk:8-jre' }
            steps {
                echoField 'Hello, JDK'
                sh 'java -version'
            }
        }
    }
}
```

#### post

定义Pipeline或stage运行结束时的操作。

- always： 运行，无论Pipeline运行的完成状态如何。
- changed： 只有当前Pipeline运行的状态与先前完成的Pipeline的状态不同时，才能运行。
- failure： 仅当当前Pipeline处于“失败”状态时才运行，通常在Web UI中用红色指示表示。
- success： 仅当当前Pipeline具有“成功”状态时才运行，通常在具有蓝色或绿色指示的Web UI中表示。
- unstable： 只有当前Pipeline具有“不稳定”状态，通常由测试失败，代码违例等引起，才能运行。通常在具有黄色指示的Web UI中表示。
- aborted： 只有当前Pipeline处于“中止”状态时，才会运行，通常是由于Pipeline被手动中止。通常在具有灰色指示的Web UI中表示。

```text
pipeline {
    environment {
        CRDE_EAMIL='xxx@163.com'
    }
    post {
        success {
            script {
                //使用wrap([$class: 'BuildUser'])需要安装user build vars plugin插件
                // JOB_NAME,BUILD_NUMBER,BUILD_USER,env.BUILD_URL是jenkins pipeline内部变量
                wrap([$class: 'BuildUser']) {
                    mail to: "${CRDE_EAMIL}",
                        subject: "pipeline '${JOB_NAME}' (${BUILD_NUMBER}) result",
                        body: "${BUILD_USER}'s pipeline '${JOB_NAME}' (${BUILD_NUMBER}) run success\n请及时前往${env.BUILD_URL}进行查看."
                }
            }
        }
        failure {
            script {
                wrap([$class: 'BuildUser']) {
                    mail to: "${CRDE_EAMIL}",
                        subject: "pipeline '${JOB_NAME}' (${BUILD_NUMBER}) result",
                        body: "${BUILD_USER}'s pipeline '${JOB_NAME}' (${BUILD_NUMBER}) run failure\n请及时前往${env.BUILD_URL}进行查看."
                }
            }
        }
        unstable {
            script {
                wrap([$class: 'BuildUser']) {
                    mail to: "${CRDE_EAMIL}",
                        subject: "pipeline '${JOB_NAME}' (${BUILD_NUMBER}) result",
                        body: "${BUILD_USER}'s pipeline '${JOB_NAME}' (${BUILD_NUMBER}) run unstable\n请及时前往${env.BUILD_URL}进行查看."
                }
            }
        }
    }
}
```

#### stages

> 包含一个或多个stage的序列，Pipeline的大部分工作在此执行。建议stages至少包含至少一个stage指令，用于连接各个交付过程，如构建，测试和部署等。

#### steps

> steps包含一个或多个在stage块中执行的step序列。

### Directives（指令）

#### environment

> environment指令指定一系列键值对，这些键值对将被定义为所有step或stage-specific step的环境变量，具体取决于environment指令在Pipeline中的位置。 该指令支持一种特殊的方法credentials()，可以通过其在Jenkins环境中的标识符来访问预定义的凭据。 对于类型为“Secret Text”的凭据，该 credentials()方法将确保指定的环境变量包含Secret Text内容；对于“标准用户名和密码”类型的凭证，指定的环境变量将被设置为username:password。 每个stage可以有独自的environment块

```text
pipeline {
    agent any
    environment {
        CC = 'clang'
    }
    stages {
        stage('Example') {
            environment {
                AN_ACCESS_KEY = credentials('my-prefined-secret-text')
            }
            steps {
                sh 'printenv'
            }
        }
    }
}
```

#### options

允许在Pipeline本身内配置Pipeline专用选项。Pipeline本身提供了许多选项，例如buildDiscarder，但它们也可能由插件提供，例如 timestamps。

常用选项

- buildDiscarder: pipeline保持构建的最大个数。例如：options { buildDiscarder(logRotator(numToKeepStr: '1')) }
- disableConcurrentBuilds: 不允许并行执行Pipeline,可用于防止同时访问共享资源等。例如：options { disableConcurrentBuilds() }
- skipDefaultCheckout: 默认跳过来自源代码控制的代码。例如：options { skipDefaultCheckout() }
- skipStagesAfterUnstable: 一旦构建状态进入了“Unstable”状态，就跳过此stage。例如：options { skipStagesAfterUnstable() }
- timeout: 设置Pipeline运行的超时时间。例如：options { timeout(time: 1, unit: 'HOURS') }
- retry: 失败后，重试整个Pipeline的次数。例如：options { retry(3) }
- timestamps: 预定义由Pipeline生成的所有控制台输出时间。例如：options { timestamps() }

```text
pipeline {
    agent any
    options {
        timeout(time: 1, unit: 'HOURS')
    }
    stages {
        stage('Example') {
            steps {
                echoField 'Hello World'
            }
        }
    }
}
```

#### parameters

> parameters指令提供用户在触发Pipeline时的参数列表。这些参数值通过该params对象可用于Pipeline步骤。

常用参数 string，choice，booleanParam等

```text
pipeline {
    agent any
    environment {
        CRDE_EAMIL='xxx@163.com'
    }
    parameters {
        string(name: 'PERSON', defaultValue: 'Mr Jenkins', description: 'Who should I say hello to?')
        choice(name: 'server', choices: '192.168.1.1,22,vito,vito111', description: '测试服务器列表选择（IP,SshPort,Name,Passwd）')
        booleanParam(name: 'isCommit', description: '是否邮件通知部署人员', defaultValue: false)
    }
    stages {
        stage('Example') {
            steps {
                echoField "Hello ${params.PERSON}"
                script {
                    def split = ${params.server.split(",")}
                    serverIP = split[0]
                    sshport = split[1]
                    username = split[2]
                    password = split[3]
                    echoField "serverIP:${serverIP},sshport:${sshport},username:${username},password:${password}"
                }
            }
        }
        stage('通知人工验收') {
            steps {
                script {
                    wrap([$class: 'BuildUser']) {
                        if(params.isCommit==false){
                            echoField "不需要通知部署人员人工验收"
                        }
                        else{
                            //邮件通知测试人员人工验收
                            mail to: "${CRDE_EAMIL}",
                                subject: "pipeline '${JOB_NAME}' (${BUILD_NUMBER})人工验收通知",
                                body: "${BUILD_USER}提交的PineLine '${JOB_NAME}' (${BUILD_NUMBER})进入人工验收环节\\n请及时前往${env.BUILD_URL}进行测试验收"
                        }
                    }
                }
            }
        }
    }
}
```

#### triggers

> triggers指令定义了Pipeline自动化触发的方式。对于与源代码集成的Pipeline，如GitHub或BitBucket，triggers可能不需要基于webhook的集成也已经存在。目前只有两个可用的触发器：cron和pollSCM。

- cron: 接受一个cron风格的字符串来定义Pipeline触发的常规间隔，例如： triggers { cron('H 4/* 0 0 1-5') }
- pollSCM: 接受一个cron风格的字符串来定义Jenkins检查SCM源更改的常规间隔。如果存在新的更改，则Pipeline将被重新触发。例如：triggers { pollSCM('H 4/* 0 0 1-5') }

```text
pipeline {
    agent any
    triggers {
        cron('H 4/* 0 0 1-5')
    }
    stages {
        stage('Example') {
            steps {
                echoField 'Hello World'
            }
        }
    }
}
```

#### stage

> stage指令在stages部分中，应包含stop部分，可选agent部分或其他特定于stage的指令。实际上，Pipeline完成的所有实际工作都将包含在一个或多个stage指令中。

```text
pipeline {
    agent any
    stages {
        stage('Example') {
            steps {
                echoField 'Hello World'
            }
        }
    }
}
```

#### tools

> 通过tools可自动安装工具，并放置环境变量到PATH。如果agent none，这将被忽略。

- maven
- jdk
- gradle

```text
pipeline {
    agent any
    tools {
        //工具名称必须在Jenkins 管理Jenkins → 全局工具配置中预配置。
        maven 'apache-maven-3.0.1'
    }
    stages {
        stage('Example') {
            steps {
                sh 'mvn --version'
            }
        }
    }
}
```

#### when

> when指令允许Pipeline根据给定的条件确定是否执行该阶段。该when指令必须至少包含一个条件。如果when指令包含多个条件，则所有子条件必须为stage执行返回true。这与子条件嵌套在一个allOf条件中相同。

内置条件

- branch: 当正在构建的分支与给出的分支模式匹配时执行，例如：when { branch 'master' }。请注意，这仅适用于多分支Pipeline。
- environment: 当指定的环境变量设置为给定值时执行，例如： when { environment name: 'DEPLOY_TO', value: 'production' }
- expression: 当指定的Groovy表达式求值为true时执行，例如： when { expression { return params.DEBUG_BUILD } }
- not: 当嵌套条件为false时执行。必须包含一个条件。例如：when { not { branch 'master' } }
- allOf: 当所有嵌套条件都为真时执行。必须至少包含一个条件。例如：when { allOf { branch 'master'; environment name: 'DEPLOY_TO', value: 'production'
  } }
- anyOf: 当至少一个嵌套条件为真时执行。必须至少包含一个条件。例如：when { anyOf { branch 'master'; branch 'staging' } }

```text
pipeline {
    agent any
    stages {
        stage('Example Build') {
            steps {
                echoField 'Hello World'
            }
        }
        stage('Example Deploy') {
            when {
                allOf {
                    branch 'production'
                    environment name: 'DEPLOY_TO', value: 'production'
                }
            }
            steps {
                echoField 'Deploying'
            }
        }
    }
}
```

#### Parallel（并行）

> Declarative Pipeline近期新增了对并行嵌套stage的支持，对耗时长，相互不存在依赖的stage可以使用此方式提升运行效率。除了parallel stage，单个parallel里的多个step也可以使用并行的方式运行。

```text
pipeline {
    agent any
    stages {
        stage('Non-Parallel Stage') {
            steps {
                echoField 'This stage will be executed first.'
            }
        }
        stage('Parallel Stage') {
            when {
                branch 'master'
            }
            parallel {
                stage('Branch A') {
                    agent {
                        label "for-branch-a"
                    }
                    steps {
                        echoField "On Branch A"
                    }
                }
                stage('Branch B') {
                    agent {
                        label "for-branch-b"
                    }
                    steps {
                        echoField "On Branch B"
                    }
                }
            }
        }
    }
}
```

#### Steps（步骤）

> Declarative Pipeline可以使用 Pipeline Steps reference中的所有可用步骤 ，并附加以下仅在Declarative Pipeline中支持的步骤。

#### script

> script步骤需要一个script Pipeline，并在Declarative Pipeline中执行。对于大多数用例，script在Declarative Pipeline中的步骤不是必须的，但它可以提供一个有用的加强。

```text
pipeline {
    agent any
    stages {
        stage('Example') {
            steps {
                echoField 'Hello World'
                script {
                    def browsers = ['chrome', 'firefox']
                    for (int i = 0; i < browsers.size(); ++i) {
                        echoField "Testing the ${browsers[i]} browser"
                    }
                }
            }
        }
    }
}
```

## 三. Scripted Pipeline（脚本式pipeline）

### 流程控制

> pipeline脚本同其它脚本语言一样，从上至下顺序执行，它的流程控制取决于Groovy表达式，如if/else条件语句，举例如下：

```text
Jenkinsfile (Scripted Pipeline)
node {
    stage('Example') {
        if (env.BRANCH_NAME == 'master') {
            echoField 'I only execute on the master branch'
        } else {
            echoField 'I execute elsewhere'
        }
    }
}
```

pipeline脚本流程控制的另一种方式是Groovy的异常处理机制。当任何一个步骤因各种原因而出现异常时，都必须在Groovy中使用try/catch/finally语句块进行处理，举例如下：

```text
Jenkinsfile (Scripted Pipeline)
node {
    stage('Example') {
        try {
            sh 'exit 1'
        }
        catch (exc) {
            echoField 'Something failed, I should sound the klaxons!'
            throw
        }
    }
}
```

### Steps

> pipeline最核心和基本的部分就是“step”，从根本上来说，steps作为Declarative pipeline和Scripted pipeline语法的最基本的语句构建块来告诉jenkins应该执行什么操作。

### Declarative pipeline和Scripted pipeline的比较

- 共同点： 两者都是pipeline代码的持久实现，都能够使用pipeline内置的插件或者插件提供的steps，两者都可以利用共享库扩展。
- 区别： 两者不同之处在于语法和灵活性。Declarative pipeline对用户来说，语法更严格，有固定的组织结构，更容易生成代码段，使其成为用户更理想的选择。但是Scripted
  pipeline更加灵活，因为Groovy本身只能对结构和语法进行限制，对于更复杂的pipeline来说，用户可以根据自己的业务进行灵活的实现和扩展。

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](https://oss.sparksys.top/sparkzxl-component/wechat-sparkzxl.jpg)
