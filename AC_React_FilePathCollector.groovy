/*** BEGIN META {
  "name" : "AC_React_FilePathCollector",
  "comment" : "Creates an extension filtered map of file paths from a selected folder",
  "parameters" : [ 'vfolder','vXtension'],
  "core": "1.596",
  "authors" : [
    { name : "Ioannis K. Moutsatsos" }
  ]
} END META**/
import groovy.io.FileType
path=vFolder
xtnsion=vXtension
prefix=vPrefix
choices=returnFileOptions(path, xtnsion)
/* method to find a file with particular extension */ 

def returnFileOptions(path, xtnsion){
choices=[:]
folder= new File(path)
 // assert folder.exists()
folder.traverse(
        type:FileType.FILES,
        nameFilter:~/$prefix.*\.$xtnsion/,
        maxDepth:0
    ){f->
  		println f
        choices.put(f.canonicalPath, f.name)
        }
return choices
}

  
