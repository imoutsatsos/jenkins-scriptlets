/*** BEGIN META {
  "name" : "AC_React_ArtifactCollector",
  "comment" : "Creates an extension filtered map of artifacts from a selected job build",
  "parameters" : [ 'vBuildRef','vXtension'],
  "core": "1.596",
  "authors" : [
    { name : "Ioannis K. Moutsatsos" }
  ]
} END META**/
import hudson.model.*
  xtension=vXtension
  j_project=vBuildRef.split('#')[0]
  j_build_no=vBuildRef.split('#')[1]
def choices=[:]
def job = hudson.model.Hudson.instance.getItem(j_project) 
def build=job.getBuildByNumber(j_build_no.toInteger())
buildURL="${jenkins.model.Jenkins.instance.getRootUrl()}job/$j_project/$j_build_no"
artifact= build.getArtifacts()
   artifact.each{
    choices.put("${buildURL}/artifact/$it" as String,"$it" as String)
   }
return choices.findAll{key,value->value.endsWith(".$xtension")}