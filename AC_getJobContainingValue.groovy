/*** BEGIN META {
  "name" : "AC_getJobContainingValue",
  "comment" : "This script displays jobs, containing the defined value",
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
chosenJobs = allItems.findAll{job -> job.name.contains(contains)}

// return names as a list of choices
choices=[]
chosenJobs.each { job ->
    choices.add(job.name)
}
return choices