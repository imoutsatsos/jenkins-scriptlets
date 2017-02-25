// Get a list of all the Jenkins jobs from all CloudBees folders
def choices=[:]

jenkins.model.Jenkins.instance?.getAllItems(com.cloudbees.hudson.plugins.folder.Folder).each { folder ->
  println "Folder - ${folder}"
  folder.getItems().each {
    println "${it.name}"
    choices.put(folder.name+'/'+it.name, it.name)
  } 
}
return choices
