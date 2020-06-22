/*** BEGIN META {
  "name" : "jenkinsJobParamBuilder",
  "comment" : "Assembles new job parameters from other jobs",
  "parameters" : [ 'newJobNameString','appendModeBoolean','sourceDataJson'],
  "core": "2.121",
  "authors" : [
    { name : "Ioannis K. Moutsatsos" }
  ]
} END META**/

//LIMITATION: We can't rename the copied parameters -still under investigation

import hudson.model.*
import jenkins.model.*
import org.biouno.unochoice.* 
import groovy.json.*
def jsonSlurper = new JsonSlurper()

newJobName=newJobNameString
appendMode=appendModeBoolean.toBoolean() //in this mode if project exists we append the new parameters, if false we replace them
println 'appendMode:'+appendMode
reusing=false
jenkins=jenkins.model.Jenkins.instance
try{
jenkins.createProject(FreeStyleProject,newJobName)
} catch (IllegalArgumentException e) {
    println "$newJobName already exists-Reusing existing job"
    reusing=true
}
targetJob=jenkins.model.Jenkins.instance.getJob(newJobName)

SOURCEDATA=sourceDataJson
/*
Example sourcedata json
"""
{"SOURCE_DATA":[{"source_job":"CellProfiler_JClustBatch","param_ori":"GENERATED_IMAGE_LIST","param_label":"GENERATED_IMAGE_LIST"},{"source_job":"IMAGELIST_GALLERIES","param_ori":"IMAGELIST_URL","param_label":"IMAGELIST_URL"},{"source_job":"CellProfiler_JClustBatch","param_ori":"PROPERTIES","param_label":"PROPERTIES"},{"source_job":"CellProfiler_JClustBatch","param_ori":"OPERATOR","param_label":"OPERATOR"},{"source_job":"CellProfiler_JClustBatch","param_ori":"PROP_VALUE","param_label":"PROP_VALUE"},{"source_job":"IMAGELIST_GALLERIES","param_ori":"HELPER_DB","param_label":"HELPER_DB"},{"source_job":"IMAGELIST_GALLERIES","param_ori":"PRIMARY_IMAGE_LIST","param_label":"PRIMARY_IMAGE_LIST"},{"source_job":"IMAGELIST_GALLERIES","param_ori":"COLOR_PLAN","param_label":"COLOR_PLAN"},{"source_job":"IMAGELIST_GALLERIES","param_ori":"DBTABLE","param_label":"DBTABLE"},{"source_job":"IMAGELIST_GALLERIES","param_ori":"IMAGE_OBJECTS","param_label":"IMAGE_OBJECTS"}]}
"""

*/
paramAssembly=jsonSlurper.parseText(SOURCEDATA).SOURCE_DATA
newParamDefs=[]


paramAssembly.each{
sourceJob=jenkins.model.Jenkins.instance.getJob(it.source_job)
//UNNAMED parameters are retrieved by index others by their original name
  if (it.param_ori.startsWith('UNNAMED')){
    unnamedIndx=it.param_ori.split('_')[-1] as Integer
	  newParamDef=sourceJob.getProperty('hudson.model.ParametersDefinitionProperty').getParameterDefinitions()[unnamedIndx]    
  }else{
	  newParamDef=sourceJob.getProperty('hudson.model.ParametersDefinitionProperty').getParameterDefinition(it.param_ori)
  }
//newParamDef.remove('name')
//newParamDefs.put('name',it.param_label)
newParamDefs.add(newParamDef)
}//end each_paramAssembly

if (reusing && targetJob.getProperty(ParametersDefinitionProperty.class)!=null && appendMode){
    //Retrieve the ParametersDefinitionProperty that contains the list of parameters.
    ParametersDefinitionProperty currentParams = targetJob.getProperty(ParametersDefinitionProperty.class)
    newParamDefs.each{pdef->
    currentParams.getParameterDefinitions().add(pdef)
      println "\t${pdef.name} (appended)"
    }

}else{
    //we first delete existing parameters if NOT
    if (reusing && targetJob.getProperty(ParametersDefinitionProperty.class)!=null && !appendMode){
        println 'Append Mode is OFF: Overwriting previous job parameters' 
        targetJob.removeProperty(ParametersDefinitionProperty.class)
        targetJob.addProperty(new hudson.model.ParametersDefinitionProperty(newParamDefs))
      println "New Job Parameters:"
      newParamDefs.each{
        println "\t${it.name} "
      }
    }else{
    targetJob.addProperty(new hudson.model.ParametersDefinitionProperty(newParamDefs))
    }
}   

//Save the job-required for BuildWithParameters
    targetJob.save()

//return """<a href="/job/$newJobName/build">Review $newJobName </a>"""
println "try your new job here:"+ "${jenkins.model.Jenkins.instance.getRootUrl()}job/$newJobName/build"