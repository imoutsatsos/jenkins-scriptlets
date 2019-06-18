/*** BEGIN META {
  "name" : "AC_getJobContainingValue",
  "comment" : "This script returns job names, where JOB_NAME contains the defined text. Case Insensitive search",
  "parameters" : [ 'nameValue'],
  "core": "2.100",
  "authors" : [
    { name : "Ioannis K. Moutsatsos" }
  ]
} END META**/

//String a Job could contain
contains = nameValue
// Access to the Hudson Singleton
hudsonInstance = hudson.model.Hudson.instance

// Retrieve all Jobs which starts with -jobs-
allItems = hudsonInstance.items
chosenJobs = allItems.findAll{job -> job.name.toUpperCase().contains(contains.toUpperCase())}

// return names as a list of choices
choices=[]
chosenJobs.each { job ->
    choices.add(job.name)
}
return choices
