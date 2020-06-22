/*** BEGIN META {
  "name" : "JenkinsJobBuilderCopy",
  "comment" : "Copies the source project builders to target project",
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

println 'Copying builders from:\n\t'+sourceJob.name+"\tto:\t$newJobName\n"
jenkins=jenkins.model.Jenkins.instance
cfg=ScriptlerConfiguration.getConfiguration()
try{
jenkins.createProject(FreeStyleProject,newJobName)
} catch (IllegalArgumentException e) {
    println "$newJobName already exists-Reusing existing job"
    reusing=true
}
targetJob=jenkins.model.Jenkins.instance.getJob(newJobName)
sBuilders=sourceJob.getBuildersList()
tBuilders=targetJob.getBuildersList()
k=0
/* vMode: review, append, overwrite
In 'review' mode we just list vSourceProject builder details
In 'append' mode we append source builders to those of target
In 'overwrite' mode we overwrite target builders with those of source
*/
switch(vMode){
 case"review":
  println "Reviewing builders of $vSourceProject"
  sBuilders.each{  
  k++ 
  builderClass=(it.class as String).tokenize('.')[-1] as String
  println "$builderClass\tBuilder-$k"
  printBuilderReport(it)
}
 break
case"append":
    println "Appending builders to $vSourceProject"
    sBuilders.each{  
  k++ 
  tBuilders.add(it)
  builderClass=(it.class as String).tokenize('.')[-1] as String
  println "$builderClass\tAppended-$k"
  printBuilderReport(it)
}
break
case"overwrite":
    println "Overwriting builders to $vSourceProject" 
  //remove all target builders
    tBuilders.each{
    tBuilders.remove(it)
  }
  //now append source builders
  sBuilders.each{  
  k++ 
  tBuilders.add(it)
  builderClass=(it.class as String).tokenize('.')[-1] as String
  println "$builderClass\tAppended-$k"
  printBuilderReport(it)
}
break
default:
    println "Unknown Mode. Defaulting to reviewing builders of $vSourceProject"
}


/* A method to print simple report 
for a particular class of Jenkins Builder
*/
def printBuilderReport(hudson.tasks.Builder builder) {
 builderClass=(builder.class as String).tokenize('.')[-1] as String
  
 switch(builderClass){
 case"ScriptlerBuilder":
   println '\tScript:\n\t\t'+builder.getScriptId()
   Script s = ScriptlerConfiguration.getConfiguration().getScriptById(builder.getScriptId());
   println '\tAbout:\n\t\t'+s.comment
   println '\tParameters:'
	builder.getParameters().each{pm->
    println '\t\t'+ pm.getName()+':\n\t\t\t'+pm.getValue()
  }
 break
case"EnvInjectBuilder":
	propertiesFile=builder.getInfo().getPropertiesFilePath()
    println '\t'+propertiesFile
    println '\tMore Env Props:'+builder.getInfo().getPropertiesContent().replace('\n','\n\t')
break   
case"Groovy":
	scriptFile=builder.getScriptSource().getScriptFile()
    println '\t'+scriptFile
    println '\tScriptParameters:'+builder.getScriptParameters()
break
case"SystemGroovy":
	scriptFile=builder.getSource().getScriptFile()
    println '\t'+scriptFile
    println '\tScriptBindings:'+builder.getBindings()
break
case"R":
    println '\t Embedded R Command'
break
case"SSHBuilder":
    println '\t'+builder.getSiteName()
break
case"ConditionalBuilder":
    println '\t'+(builder.getRunCondition().class as String).tokenize('.')[-1] as String
	builder.getConditionalbuilders().each{
    println '\t'+it.class
    
    printBuilderReport(it)   
    }
break    
default:
//do default actions
  println 'Uknown case'
}
  print '\n' 
}