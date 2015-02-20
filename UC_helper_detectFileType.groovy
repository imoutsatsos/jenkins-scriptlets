/*** BEGIN META {
  "name" : "UC_helper_detectFileType",
  "comment" : "Identifies a file extension and returns a fileType label",
  "parameters" : [ 'vMyFile'],
  "core": "1.593",
  "authors" : [
    { name : "Ioannis Moutsatsos" }
  ]
} END META**/

/*
Identifies a file extension and returns the fileContent type from 
*/
choices=[]
fileContent='CSV_TABLE,TSV_TABLE,TEXT,HTML,IMAGE,PDF,BINARY'
def select=''

if (!binding.variables.containsKey("vMyFile") || vMyFile=='')
   return ['Choose a file!']

def myFile=vMyFile
def imageExtensions = ['tif', 'tiff', 'png', 'jpeg', 'jpg', 'gif','bmp']
def binaryExtensions=['pdf','exe','doc','xls','xlsx','r','rdata']
def tabularExtensions=['csv','tsv']
def tabularData=['csv':'CSV_TABLE','tsv':'TSV_TABLE']
def textData=['txt':'TEXT','htm':'HTML','html':'HTML']


ext=getFileExtension(myFile).toLowerCase()
println ext

switch(ext){
 case imageExtensions:
   select='IMAGE'
 break
case binaryExtensions:
    select='BINARY'
 break
case tabularData:
    select=tabularData[ext]
 break
case textData:
    select=textData[ext]
 break
default:
    select='UNDEFINED'
}

/* one liner function returning the extension of a file */
def getFileExtension( String fileName){
theExt=fileName.lastIndexOf('.').with {it != -1 ? fileName[(it+1)..(fileName.length()-1)] : 'undefined'}
}

/* Select a value from the provided list based on the detected file type 
   selection is done by appending :selected to the value
*/
fileContent.replace(select,"$select:selected" ).split(',').each{
choices.add(it)
}

return choices
