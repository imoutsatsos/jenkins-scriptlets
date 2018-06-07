/*** BEGIN META {
  "name" : "AC_listDirImages",
  "comment" : "List sorted list of images in target folder",
  "parameters" : [ 'vFolder'],
  "core": "1.596",
  "authors" : [
    { name : "Ioannis K. Moutsatsos" }
  ]
} END META**/
imageFiles=[]
def _folder=new File(vFolder)
/* default filter is by the csv extension */

def filePattern= ~/.*\.(?i)jpg|tif|png|jpeg|tiff|gif|bmp/
def sourceFileList= _folder.listFiles()//.sort{ file -> file.name }
    sourceFileList.each{file->
        if(file.isFile()&&filePattern.matcher(file.name).find())
        {
            imageFiles.add(file.name)
         }
    }
return imageFiles.sort()