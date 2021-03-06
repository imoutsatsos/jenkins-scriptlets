/*** BEGIN META {
  "name" : "AC_RefHelper_findBuildsOnRunParamLike",
  "comment" : "Returns a formatted HTML list of build href_links related by a common run type build parameter whose value is defined by the user.",
  "parameters" : [ 'vName','vValue', 'vSearchSpace','vShow'],
  "core": "1.593",
  "authors" : [
    { name : "Ioannis Moutsatsos" }
  ]
} END META**/

/*
vSearchSpace: Required; A comma delimited list of Job names
vName       : Required; A comma delimited list of the common build ref parameter name 
vValue      : Required; parameter value to match
vShow       : Flag (on/off) indicating whether to display links
*/
import jenkins.model.*;
jenkinsURL=jenkins.model.Jenkins.instance.getRootUrl()
def options=[] //by default we add the PARAMETER VALUE; this is the parent data set KEY
def htmlRefs=""

if(vShow=='on'){
jobNames=vSearchSpace.split(',')
jobNames.each{ job->
  job=job.trim()
  if (jenkins.model.Jenkins.instance.getItem(job)!=null){
    htmlRefs=htmlRefs+"""<h2>$job</h2>"""
jenkins.model.Jenkins.instance.getItem(job).getBuilds().each{
  vName.split(',').each{param->
  param=param.trim()
  commonParam=it.getEnvironment(null).getAt(param)
    
  if (commonParam!=null && (commonParam==vValue || commonParam.toUpperCase().contains(vValue.toUpperCase())) && it.result.toString()=='SUCCESS'){
    uc_key=job+'#'+it.number
    uc_value="(${it.id})_${it.getDisplayName()}:${it.getDescription()!=null?it.getDescription():''}"
    htmlRefs=htmlRefs+"<a href=\"${jenkinsURL}job/$job/${it.number}\">$uc_value</a> <p>"
    
  }
  }//end each param
}
  }
  
} //end each JobNames

return htmlRefs
}   
else{
  return '<hr/>'
}
