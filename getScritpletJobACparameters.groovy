/*** BEGIN META {
  "name" : "getScritpletJobACparameters",
  "comment" : "Prints usage of Groovy SCRIPTLETS in ALL JOB Active Choice PARAMETERS by searching JENKINS model",
  "parameters" : [ 'scriptNameList'],
  "core": "2.121",
  "authors" : [
    { name : "Ioannis K. Moutsatsos" }
  ]
} END META**/

import hudson.model.*
//scriptNameListExample='UC_helper_GetBuildsByNumAsMap,H2_inMem_ParameterDB,AC_RefHelper_getParamValue'
scriptMap=[:]

scriptNameList.split(',').each{script->
  scriptJob=[]

  //all job name
jenkins.model.Jenkins.instance.getAllItems(FreeStyleProject.class).each {
  it.properties.each{p->

  if( p.value.class==hudson.model.ParametersDefinitionProperty){
    p.value.getParameterDefinitions().each{pdn->
       if (pdn  in org.biouno.unochoice.UnoChoiceParameter && pdn.getScript().getClass()==org.biouno.unochoice.model.ScriptlerScript ){
         if (pdn.getScript().scriptlerScriptId-'.groovy'==script){
          scriptJob.add(it.name)
          }
        
      }
    }
  } //end each Parameter Definition Property
  }
  
}
  println "$script : Used in ${scriptJob.size} Job Build forms"
scriptJob.each{j->
  println '\t'+j
}
scriptMap.put(script,scriptJob)
  
} //end each script
return null//scriptMap
