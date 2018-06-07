/*** BEGIN META {
  "name" : "AC_RefHelper_getSelectArtifactUrl",
  "comment" : "Returns a comma separated list of artifact URLs that contains/startsWith/endsWith the 'selector' string as input HTML",
  "parameters" : [ 'vBuildRef','vSelector','vSelectLogic'],
  "core": "1.593",
  "authors" : [
    { name : "Ioannis Moutsatsos" }
  ]
} END META**/
import hudson.model.*

/*
 for backward compatibility we set 'contains' as the default 
 if selectLogic parameter is not provided
*/
  String selectLogic
  if (binding.variables.containsKey("vSelectLogic")){
    selectLogic=vSelectLogic
  }

if(selectLogic==null || selectLogic==''){
 selectLogic='contains' 
}
def choices=[]
def project=vBuildRef.split('#')[0]
def buildNo=vBuildRef.split('#')[1] 
  def job = hudson.model.Hudson.instance.getItem(project) 
def build=job.getBuildByNumber(buildNo.toInteger())
  selector=vSelector
env=[:] 
env= build.getEnvironment(TaskListener.NULL);
buildURL=env['BUILD_URL']
artifact=[]
   artifact= build.getArtifacts()
   artifact.each{
 switch(selectLogic){
 case"contains":
	 if((it as String).contains(selector)){
     choices.add("${buildURL}artifact/$it")
     }//end if contains
 break
case"startsWith":
	     if((it as String).startsWith(selector)){
     choices.add("${buildURL}artifact/$it")
     }//end if startsWith
break
case"endsWith":
		     if((it as String).endsWith(selector)){
     choices.add("${buildURL}artifact/$it")
     }//end if endsWith
break
}

       }  
theValue=choices.join(',')

return '<input name="value" value="'+theValue+'" class="setting-input" type="text">'