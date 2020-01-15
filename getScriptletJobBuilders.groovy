/*** BEGIN META {
  "name" : "getScriptletJobBuilders",
  "comment" : "Print usage of Groovy SCRIPTLETS in ALL JOB BUILDERS by searching JENKINS model",
  "parameters" : [ 'scriptNameList'],
  "core": "2.121",
  "authors" : [
    { name : "Ioannis K. Moutsatsos" }
  ]
} END META**/

import hudson.model.*
//scriptNameList='copyServerStaticArtifact,Delete_JobSessionWorkspace,dataTableMaker'
scriptMap=[:]

scriptNameList.split(',').each{script->
  scriptJob=[]

  //all job name
jenkins.model.Jenkins.instance.getAllItems(FreeStyleProject.class).each {
  it.getBuilders().each{bld->
    //println bld.getClass()
    if (bld.getClass()==org.jenkinsci.plugins.scriptler.builder.ScriptlerBuilder){
    
    if (bld.getScriptId()-'.groovy'==script){
    bld.getDescriptor()
    scriptJob.add(it.name)
  }
    
  }
  }
  
}
println script
scriptJob.each{j->
  println '\t'+j
}
scriptMap.put(script,scriptJob)
  
} //end each script
return null //scriptMap
