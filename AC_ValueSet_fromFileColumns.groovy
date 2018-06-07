/*** BEGIN META {
  "name" : "AC_ValueSet_fromFileColumns",
  "comment" : "Returns the set of unique values in user-defined columns of tabular artifacts",
  "parameters" : [ 'vSearchSpace','vXtension','vValueColumns','vValueSetPath'],
  "core": "1.596",
  "authors" : [
    { name : "Ioannis K. Moutsatsos" }
  ]
} END META**/

import groovy.io.FileType
artifactEndFilter=vXtension
inputFileUrls=[]
valueColumns=vValueColumns
def artifactUrls=[]
def unoOptions=[]

searchSpace=new File(vSearchSpace)
searchSpace.traverse(
        type:FileType.FILES,
        nameFilter:~/.*\.$artifactEndFilter/,
        maxDepth:0
    ){f-> inputFileUrls.add(f)}

inputFileUrls.each{
 println it.name 
}

/* separator is defined as a regular expression so that we can catch rogue commans inside quotatio marks
*  http://stackoverflow.com/questions/1757065/splitting-a-comma-separated-string-but-ignoring-commas-in-quotes
* */
def separator = ",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*\$)"
def valueList=valueColumns.split(separator)
def csvValues=[] //the list of csv values
def listOnUniq = [] //helper list for identifying unique rows


/*
    Read CSV List and index the positions of interest
    Create lists to hold columns of interest
 */
inputFileUrls.each {

  it.withReader{reader->
    columnSet = reader.readLine().split(separator)
    valueIndex = createIndexIntoList(valueList.toList(), columnSet.toList())

  /*Read the CSV file lines
    construct a multi-value map from each row. Each value is keyed to the header column
 */
lineCount = 0
it.eachLine { l ->
    line = l.split(separator)
   
    if (line.size()==columnSet.size()){ //check for complete record
    uniqHelper = []          
    valueIndex.each {vi->
      uniqHelper = uniqHelper + line[vi]
    }

    if (lineCount >= 1) {
        listOnUniq.add(uniqHelper)
    }

    lineCount++
} //end check for complete record
    else{
        println "Skipping Incomplete record: $line"
    }
}
} //end each reader
}

/* create the unique combinations and assign them to the uno-choice list to be returned to the UI */

  listOnUniq.flatten().unique().each {u->
    unoOptions.add(u)
}
/*
unoOptions.sort().each{
 println it 
}
*/

if(binding.variables.containsKey("vValueSetPath")){
writeValueSet(vValueSetPath,vValueColumns.replace(',','_'), unoOptions)
println 'Saved File'
}

return unoOptions//.sort()


/*
Find index of a list members in another list
For example if source[A,D,F] and target[A,B,C,D,E,F,G] we want to return sourceIndex[0,3,5]
*/

def createIndexIntoList(List source, List target) {
    def indexList = []
    source.each { s ->
        indexList.add(target.indexOf(s))
    }
  //need to remove an index of -1 which means it was not found!
    return indexList-[-1]
}

//writes the unoOptions list to the provided path
def writeValueSet(path='D:/TEMP/valueSet.csv', header, options){
  output=new File("$path")
  outputWriter=output.newWriter(false)
  outputWriter<<header+',NAME'+'\n'
  (options-['null']).eachWithIndex{v,ind->
    outputWriter<<"${ind+1},$v\n"
  }
    outputWriter.flush()
    outputWriter.close()
  }
