/*** BEGIN META {
  "name" : "JenkinsJobBuildWrapperCopy",
  "comment" : "Copies the source project BuildWrappers to target project",
  "parameters" : [ 'vSourceProject', 'vTargetProject', 'vMode'],
  "core": "2.222.1",
  "authors" : [
    { name : "Ioannis K. Moutsatsos" }
  ]
} END META**/

import org.jenkinsci.plugins.scriptler.config.Parameter;
import org.jenkinsci.plugins.scriptler.config.Script;
import org.jenkinsci.plugins.scriptler.config.ScriptlerConfiguration;
import hudson.model.*
import jenkins.model.*
sourceJob=jenkins.model.Jenkins.instance.getJob(vSourceProject)
newJobName=vTargetProject

println 'Copying BuildWrappers from:\n\t'+sourceJob.name+"\tto:\t$newJobName\n"
jenkins=jenkins.model.Jenkins.instance
cfg=ScriptlerConfiguration.getConfiguration()
try{
jenkins.createProject(FreeStyleProject,newJobName)
} catch (IllegalArgumentException e) {
    println "$newJobName already exists-Reusing existing job"
    reusing=true
}
targetJob=jenkins.model.Jenkins.instance.getJob(newJobName)
sBuildWrappers=sourceJob.getBuildWrappersList()//getBuildWrappersList()
tBuildWrappers=targetJob.getBuildWrappersList()//getBuildWrappersList()
k=0
/* vMode: review, append, overwrite
In 'review' mode we just list vSourceProject BuildWrapper details
In 'append' mode we append source BuildWrappers to those of target
In 'overwrite' mode we overwrite target BuildWrappers with those of source
*/
switch(vMode){
 case"review":
  println "Reviewing BuildWrappers of $vSourceProject"
  sBuildWrappers.each{  
  k++ 
  buildwrapperClass=(it.class as String).tokenize('.')[-1] as String
  println "$buildwrapperClass\tBuildWrapper-$k"
  printBuildWrapperReport(it)
}
 break
case"append":
    println "Appending BuildWrappers to $vSourceProject"
    sBuildWrappers.each{  
  k++ 
  tBuildWrappers.add(it)
  buildwrapperClass=(it.class as String).tokenize('.')[-1] as String
  println "$buildwrapperClass\tAppended-$k"
  printBuildWrapperReport(it)
}
break
case"overwrite":
    println "Overwriting BuildWrappers to $vSourceProject" 
  //remove all target BuildWrappers
    tBuildWrappers.each{
    tBuildWrappers.remove(it)
  }
  //now append source BuildWrappers
  sBuildWrappers.each{  
  k++ 
  tBuildWrappers.add(it)
  buildwrapperClass=(it.class as String).tokenize('.')[-1] as String
  println "$buildwrapperClass\tAppended-$k"
  printBuildWrapperReport(it)
}
break
default:
    println "Unknown Mode. Defaulting to reviewing BuildWrappers of $vSourceProject"
}


/* A method to print simple report 
for a particular class of Jenkins BuildWrapper
*/
def printBuildWrapperReport(hudson.tasks.BuildWrapper buildwrapper) {
 buildwrapperClass=(buildwrapper.class as String).tokenize('.')[-1] as String
  
switch(buildwrapperClass){
 case"ArtifactArchiver":
   println '\tArtifacts:\n\t\t'+buildwrapper.getArtifacts()
break
case"ACIPluginBuildWrapper":
	println '\tSymmaryReportOn:\n\t\t'+buildwrapper.getName()
break   
case"WsCleanup":
	println '\tPerform Workspace Cleanup'
break  
default:
//do default actions
  println 'Uknown case'
}
  print '\n' 
}