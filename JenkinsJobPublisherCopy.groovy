/*** BEGIN META {
  "name" : "JenkinsJobPublisherCopy",
  "comment" : "Copies the source project Publishers to target project",
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

println 'Copying Publishers from:\n\t'+sourceJob.name+"\tto:\t$newJobName\n"
jenkins=jenkins.model.Jenkins.instance
cfg=ScriptlerConfiguration.getConfiguration()
try{
jenkins.createProject(FreeStyleProject,newJobName)
} catch (IllegalArgumentException e) {
    println "$newJobName already exists-Reusing existing job"
    reusing=true
}
targetJob=jenkins.model.Jenkins.instance.getJob(newJobName)
sPublishers=sourceJob.getPublishersList()//getPublishersList()
tPublishers=targetJob.getPublishersList()//getPublishersList()
k=0
/* vMode: review, append, overwrite
In 'review' mode we just list vSourceProject Publisher details
In 'append' mode we append source Publishers to those of target
In 'overwrite' mode we overwrite target Publishers with those of source
*/
switch(vMode){
 case"review":
  println "Reviewing Publishers of $vSourceProject"
  sPublishers.each{  
  k++ 
  publisherClass=(it.class as String).tokenize('.')[-1] as String
  println "$publisherClass\tPublisher-$k"
  printPublisherReport(it)
}
 break
case"append":
    println "Appending Publishers to $vSourceProject"
    sPublishers.each{  
  k++ 
  tPublishers.add(it)
  publisherClass=(it.class as String).tokenize('.')[-1] as String
  println "$publisherClass\tAppended-$k"
  printPublisherReport(it)
}
break
case"overwrite":
    println "Overwriting Publishers to $vSourceProject" 
  //remove all target Publishers
    tPublishers.each{
    tPublishers.remove(it)
  }
  //now append source Publishers
  sPublishers.each{  
  k++ 
  tPublishers.add(it)
  publisherClass=(it.class as String).tokenize('.')[-1] as String
  println "$publisherClass\tAppended-$k"
  printPublisherReport(it)
}
break
default:
    println "Unknown Mode. Defaulting to reviewing Publishers of $vSourceProject"
}


/* A method to print simple report 
for a particular class of Jenkins Publisher
*/
def printPublisherReport(hudson.tasks.Publisher publisher) {
 publisherClass=(publisher.class as String).tokenize('.')[-1] as String
  
switch(publisherClass){
 case"ArtifactArchiver":
   println '\tArtifacts:\n\t\t'+publisher.getArtifacts()
break
case"ACIPluginPublisher":
	println '\tSymmaryReportOn:\n\t\t'+publisher.getName()
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