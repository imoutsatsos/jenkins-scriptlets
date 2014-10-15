/* 
  This scriptlet returns a human read-able list of builds of a Jenkins project
  The list can act as a surrogate 'Run-Type' Jenkins parameter, but with the following advantages:
    -it can return build numbers within a user defined range
    -it has a customizable format
    -it can return builds with a certain build status (the default result=SUCCESS)
  The scriptlet is used in combination with a dynamic Jenkins choice control such as uno-choice
  See https://github.com/biouno/uno-choice-plugin for more details
  */
  
//  Scriptlet Parameters j,f,l (job, first, last) must be defined in the scriptlet configuration

options.'j' = j //Jenkins job name
options.'f' = f //first build number
options.'l' = l //last build number-can be left null to return up to the last build

def options = new HashMap()
def fdn = '' //fullDisplayName
def dn = '' //displayName
def jn = '' //jenkins ui name
def buildSet = []
def buildSetHr = [] //a human readble list of builds
def buildURL = "http://localhost:8080/job/${options.j}"
def apiCall = "${buildURL}/api/xml?depth=1"
println apiCall

//Setup a connection to pull data in with REST
def url = new URL("$apiCall")
def connection = url.openConnection()
connection.setRequestMethod("GET")
connection.connect()
def returnMessage = ""

if (connection.responseCode == 200 || connection.responseCode == 201) {
    returnMessage = connection.content.text
    //parse the xml response
    def freeStyleBuild = new XmlSlurper().parseText(returnMessage)
    dn = freeStyleBuild.displayName.toString()
    freeStyleBuild.build.fullDisplayName.each {
        println "${it}"
    }
    println "\n-----The Filtered List------\n"
    if (options.l != '') {
        buildSet = freeStyleBuild.build.findAll {
            it.number.toInteger() >= options.f.toInteger() && it.number.toInteger() < options.l.toInteger() && it.result == 'SUCCESS'
        }
    } else {
        buildSet = freeStyleBuild.build.findAll {
            it.number.toInteger() >= options.f.toInteger() && it.result == 'SUCCESS'
        }
    }

    buildSet.each {
        fdn = it.fullDisplayName.toString()
        jn = fdn.replace(dn, "$dn:->")
        buildSetHr.add("${it.number}:$jn")
    }

} else {
    println "Error Connecting to " + url
}

return buildSetHr
