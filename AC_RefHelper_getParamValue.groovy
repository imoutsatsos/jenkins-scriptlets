/*** BEGIN META {
  "name" : "AC_RefHelper_getParamValue",
  "comment" : "Returns the value for parameter vName in build reference vBuidTag",
  "parameters" : [ 'vBuildTag','vName'],
  "core": "1.593",
  "authors" : [
    { name : "Ioannis Moutsatsos" }
  ]
} END META**/

/*
vBuildTag   : Required; JOB_NAME#NNN tag
vName       : Required; The parameter name 
*/
import jenkins.model.*;
jenkinsURL=jenkins.model.Jenkins.instance.getRootUrl()
def options=[] //by default we add the PARAMETER VALUE; this is the parent data set KEY
def htmlRefs=""

buildTag=vBuildTag.split('#')
job=buildTag[0].trim()
buildNumber=buildTag[1] as int
build=jenkins.model.Jenkins.instance.getItem(job).getBuildByNumber(buildNumber)
paramValue=build.buildVariables.getAt(vName)
if(paramValue==null||paramValue==''){
 paramValue='NULL' 
}
return '<input name="value" value="'+paramValue+'" class="setting-input" type="text">'