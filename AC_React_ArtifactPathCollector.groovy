/*** BEGIN META {
  "name" : "AC_React_ArtifactPathCollector",
  "comment" : "Creates an extension filtered map of artifact paths from a selected job build",
  "parameters" : [ 'vBuildRef','vXtension'],
  "core": "1.596",
  "authors" : [
    { name : "Ioannis K. Moutsatsos" }
  ]
} END META**/
import hudson.model.*
def hudson = hudson.model.Hudson.instance
def globalProps = hudson.globalNodeProperties
def props = globalProps.getAll(hudson.slaves.EnvironmentVariablesNodeProperty.class)
// println hudson.model.Hudson.instance.globalNodeProperties


for (nodeproperty in props)
{ 
   //println(nodeproperty.getEnvVars());
   globalEnvVars=nodeproperty.getEnvVars()
}

//Example use
vRoot=globalEnvVars.getAt('BUILD_RECORD_ROOT')

xtension=vXtension
  j_project=vBuildRef.split('#')[0]
  j_build_no=vBuildRef.split('#')[1]
def choices=[:]
def allArtifacts=[:]
def job = hudson.model.Hudson.instance.getItem(j_project) 
def build=job.getBuildByNumber(j_build_no.toInteger())
buildPath=vRoot+"/$j_project/builds/$j_build_no"

artifact= build.getArtifacts()
   artifact.each{
    allArtifacts.put("${buildPath}/archive/$it" as String,"$it" as String)
   }

xtension.tokenize(',').each{
choices=choices+ allArtifacts.findAll{key,value->value.endsWith(it)}
}
  return choices
  
