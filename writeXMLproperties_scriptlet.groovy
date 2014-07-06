/**
 * writeXMLproperties_scriptlet
 
 * Helper utility for Jenkins Display Summary Plugin
 * Reference: https://wiki.jenkins-ci.org/display/JENKINS/Summary+Display+Plugin
 * 
 * Generates the XML file required for rendering a Display Summary Plugin report.
 * The XML file generation is customized from a report configuration properties file
 * Customized for use with the Jenkins Scriptler Plugin
 * 
 * Author: Ioannis K. Moutsatsos
 * Last Update: 7/5/2014
 */

import groovy.xml.*
/*
  Script requires 2 parameters
  Add these parameters in Scriptler-Script Build Step Config so they can be propagated to the script environment

  workspaceVar	: The current Jenkins project wokspace-$WORKSPACE
  configProps	: Local path to the report configuration properties file 
*/
def workspace=workspaceVar
def xmlReportConfig=configProps

def separator = ','
def noPropUse=false // a flag whether properties file will be used
//Create the properties objects, from the file system:
Properties configProps = new Properties()   // report configuration
Properties summaryProps = new Properties()   // report field content
File configFile = new File(xmlReportConfig)
configProps.load(configFile.newDataInputStream())

def reportStyle = configProps.getProperty('report.style')
/*
	Start by reading field content
	summary.properties is a required configuration property pointing to a file containing key value fieed values
	if not used it's value in the cofiguration file must be set to: none
	summary.properties=none
*/
propSource = configProps.getProperty('summary.properties')
if (propSource.startsWith('none')){
    noPropUse=true
    println 'Report does not use properties file'
}else{
    if (propSource.startsWith('http')) {
        propSource.toURL().eachLine {
            if (it.contains('=')) {
                urlprop = it.split('=')
                summaryProps.put(urlprop[0], urlprop[1])
            }
        }
    } else {
        summaryFile = new File("${workspace}/${configProps.getProperty('summary.properties')}")
        summaryProps.load(summaryFile.newDataInputStream())
    }
} //end none else

def columnSet = []
def rowheader = []
def headerIndex = []
def writer = new StringWriter()
def xml = new MarkupBuilder(writer)
/*
Reference: https://wiki.jenkins-ci.org/display/JENKINS/Summary+Display+Plugin
Report style options include
  Section (name)
  Field (name,value, href)
  Table
  Tabs (field, table)
  Accordion (field, table)
 */
switch (reportStyle) {
    case "tab":
        thead = []; theadTemp=[]
        thead = configProps.getProperty('tab.header').split(',')
        //confirm that content file (CSV) exists for each tab.header and remove those headers for which it does not
        println "Original: $thead"
        thead.each {
            if (configProps.getProperty("content.${it}")=='table'){
                if (configProps.getProperty("table.data.${it}").startsWith('http')) {
                 if(getResponseCode(configProps.getProperty("table.data.${it}"))==200){
                     theadTemp.add(it)
                 }else{
                     println "Could not access content: ${configProps.getProperty("table.data.${it}")}"
                 }
                }else{
                tabContent = "${workspace}/${configProps.getProperty("table.data.${it}")}"
                tabContentFile= new File(tabContent)
                if (tabContentFile.exists()){
                    theadTemp.add(it)
                }else{
                    println "Could not find content: $tabContent"
                }
                    }// end logic check
            }
            if (configProps.getProperty("content.${it}")=='field'&& !noPropUse){
                tabContent = configProps.getProperty("summary.properties")
                theadTemp.add(it)
            }

        }
        thead=theadTemp //replace header with subset where content exists
        println "Adjusted from available content: $thead"
        xml.tabs {
            thead.each {
                //println it
                tabContent = configProps.getProperty("content.${it}")
                if(configProps.getProperty("separator.${it}")!=null){
                    separator=configProps.getProperty("separator.${it}")
                }  else{
                    separator=','
                }
                startOfKey = configProps.getProperty("field.key.${it}")
                valColor = configProps.getProperty('field.value.color')
                tabName = it
                tab(name: "$it") {
                    switch (tabContent) {
                        case "field":
                            summaryProps.sort().each { k, v ->
                                if (k.toString().startsWith(startOfKey)) {
                                    if(v.startsWith('http://')){
                                        field(name: k, value: 'Link', detailcolor: valColor, href:v)
                                    }else{
                                        field(name: k, value: v, detailcolor: valColor)
                                    }
                                }

                            }//end each
                            break
                        case "table":
                            //create a table from referenced file
                            // println "Working with $tabName"
                            def dataTableSource ="${configProps.getProperty("table.data.${tabName}")}" //"${workspace}/${configProps.getProperty("table.data.${tabName}")}"
                            println "$it Table data from: $dataTableSource"
                            propKey="table.header.${tabName}"
                            // if no table.length property is defined we write the entire table
                            def ignoreLineCount=false
                            def rownum=0
                            if(configProps.getProperty("table.length.${tabName}")!=null){
                                rownum = configProps.getProperty("table.length.${tabName}").toInteger()
                            }else{
                                ignoreLineCount=true
                            }

                            if (dataTableSource.startsWith('http')) {
                                dataTableSource.toURL().withReader { reader ->
                                    columnSet = reader.readLine().split(separator)
                                    if (configProps.containsKey("table.header.${tabName}".toString())){
                                        rowheader = configProps.getProperty("table.header.${tabName}").split(',')
                                    }else{
                                        rowheader=columnSet
                                    }
                                    headerIndex = createIndexIntoList(rowheader.toList(), columnSet.toList())
                                }

                            } else {
                                dataTableSource ="${workspace}/${configProps.getProperty("table.data.${tabName}")}"
                                def dataFile = new File(dataTableSource)
                                dataFile.withReader {
                                    reader ->
                                        columnSet = reader.readLine().split(separator)
                                        if (configProps.containsKey("table.header.${tabName}".toString())){
                                            rowheader = configProps.getProperty("table.header.${tabName}").split(',')
                                        }else{
                                            rowheader=columnSet
                                        }
                                        headerIndex = createIndexIntoList(rowheader.toList(), columnSet.toList())
                                }
                                //columnSet //must read CSV file from path
                            }
                            table {
                                //create table header
                                //now add table data
                                if (dataTableSource.startsWith('http')) {
                                    lineCount = 0
                                    dataTableSource.toURL().eachLine { uline ->
                                        columnSet = uline.split(separator)
                                        if (lineCount < rownum + 1) {
                                            columnSet = uline.split(separator)
                                            tr() {
                                                headerIndex.each { h ->
                                                    //if column heading exists we create a table cell, else we skip
                                                    //we also check that the row can be split into enough values or we skip
                                                    if (h != -1&&columnSet.size()==headerIndex.size()) {
                                                        if(columnSet[h].startsWith('http://')){
                                                            td(value: 'Link', bgcolor: 'white', fontcolor: 'black', align: 'left', href:columnSet[h] )
                                                        }else{
                                                            td(value: columnSet[h], bgcolor: 'white', fontcolor: 'black', align: 'left')
                                                        }
                                                    }
                                                }

                                            }
                                            // if we are counting lines we keep track
                                            if(!ignoreLineCount){
                                                lineCount++
                                            }
                                        }
                                    }//end each line
                                } //end if startsWith http
                                else{
                                    //reading from local file system
                                    dataFile = new File(dataTableSource)
                                    lineCount = 0
                                    dataFile.eachLine { uline ->

                                        columnSet = uline.split(separator)
                                        if (lineCount < rownum + 1) {
                                            columnSet = uline.split(separator)
                                            tr() {
                                                headerIndex.each { h ->
                                                    //if column heading exists we create a table cell, else we skip
                                                    //we also check that the row can be split into enough values or we skip
                                                    if (h != -1&&columnSet.size()==headerIndex.size()) {
                                                        if(columnSet[h].startsWith('http://')){
                                                            td(value: 'Link', bgcolor: 'white', fontcolor: 'black', align: 'left', href:columnSet[h] )
                                                        }else{
                                                            td(value: columnSet[h], bgcolor: 'white', fontcolor: 'black', align: 'left')
                                                        }

                                                    }
                                                }

                                            }
                                            // if we are counting lines we keep track
                                            if(!ignoreLineCount){
                                                lineCount++
                                            }
                                        }
                                    }//end each line

                                }

                            }
                            break
                    }//end content
                } //end tab

            }
        }

        break
    case "table":
        println 'TO BE IMPLEMENTED: Table Report'
        break
}

def writer4file = new FileWriter("$workspace/writeXmlSummary.xml")
XmlUtil.serialize(writer.toString(), writer4file)
println "Summary in:"
println new File("$workspace/writeXmlSummary.xml").getCanonicalPath()

/*
Find index of a list members in another list
For example if source[A,D,F] and target[A,B,C,D,E,F,G] we want to return sourceIndex[0,3,5]
 */

def createIndexIntoList(List source, List target) {
    // println 'Getting index'
    def indexList = []
    source.each { s ->
        indexList.add(target.indexOf(s))
    }
    // print indexList
    return indexList
}
/* Method checks if a URL is accessible
 */
def getResponseCode(String urlString) throws MalformedURLException, IOException {
    URL u = new URL(urlString);
    HttpURLConnection huc =  (HttpURLConnection)  u.openConnection();
    huc.setRequestMethod("GET");
    huc.connect();
    return huc.getResponseCode();
}
