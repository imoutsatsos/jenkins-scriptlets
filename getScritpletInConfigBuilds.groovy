/*** BEGIN META {
  "name" : "getScritpletInConfigBuilds",
  "comment" : "Report usage of Groovy scriptler by searching JOB/GIT_configuration artifacts",
  "parameters" : [ 'vBuildRef','vArtifactName', 'vArtifactExtension'],
  "core": "1.596",
  "authors" : [
    { name : "Ioannis K. Moutsatsos" }
  ]
} END META**/
import hudson.model.*
  
  artifactName=vArtifactName.tokenize(',').collect{it.trim()+".$vArtifactExtension"}
  println "looking for $artifactName"
  j_project=vBuildRef.split('#')[0]
  j_build_no=vBuildRef.split('#')[1]
  build_no_range=j_build_no.split('-')
  filteredArtifacts=[]


def job = hudson.model.Hudson.instance.getItem(j_project) 

if (build_no_range.size()>1){
  //find in build no range
  println 'Searching '+job.name+' Build Range:'+"${build_no_range[0]}..${build_no_range[1]}"+'\n'
  range=evaluate ("${build_no_range[0]}..${build_no_range[1]}")
  //println 'Searching Build Range:'+range
  range.each{
  //println it
  filteredArtifacts.add(getArtifactEndsWith(job, it, artifactName ))
  }
  }else{
  //find in single build no
  println 'Searching Single Build No:'+j_build_no
  filteredArtifacts.add(getArtifactEndsWith(job, j_build_no, artifactName ))
  }
/*
filteredArtifacts.each{
 println it.getClass() 
}
*/

grouped= filteredArtifacts.findAll{it.getClass()==java.util.ArrayList}.flatten().groupBy{el->el.artifact}


grouped.each{k,v->
  buildsWithTarget=[]
  println k
  buildsWithTarget.add(v.build)
  buildsWithTarget.flatten().unique().sort().each{
 println '\t'+it 
}

}//end grouped each
return null




/* a method to retrieve artifacts
ending in any of the searchText list options
@searchText a List text values to search for in build artifact names

*/

def getArtifactEndsWith(job, jobBuildNo, List searchText ){
	def choices=[]
	def foundList=[]
	build=job.getBuildByNumber(jobBuildNo.toInteger())
        if (build!=null){              
		buildURL="${jenkins.model.Jenkins.instance.getRootUrl()}job/$job.name/$jobBuildNo"
		artifact= build.getArtifacts()		
		   artifact.each{
		    //choices.add("${buildURL}/artifact/$it" as String)
		     choices.add("$it.name" as String)
		   }
		
		//Now iterate all of the artifactNames to search
			searchText.each{artName->
			itemsFound=choices.findAll{it.endsWith("$artName")} 
			
			//only return a map if you find the artifact
			if (itemsFound.size()>0){
			theArtifact=[:]
			//theArtifact.put('build',build.getDisplayName())
            theArtifact.put('build',build.buildVariables.getAt('PROJECT_NAME'))
			theArtifact.put('artifact',choices.findAll{it.endsWith("$artName")}) 
			foundList.add(theArtifact)
			}
 
			}
			
		} //end if not null build
return (foundList.size()>0)?foundList:null//.flatten()

} //end getArtifactEndsWith



