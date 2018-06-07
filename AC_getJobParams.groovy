/*** BEGIN META {
  "name" : "AC_getJobParams",
  "comment" : "Returns a parameter value for a parameter in vBuidTag named vName",
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
job=vJob
//parameter names from LAST SUCCESSFUL BUILD
build=jenkins.model.Jenkins.instance.getItem(job).getLastBuild()
options=build.buildVariables.keySet()
return options