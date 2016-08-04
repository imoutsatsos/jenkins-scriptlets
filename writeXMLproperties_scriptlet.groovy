/*** BEGIN META {"name" : "writeXMLProperties_scriptlet",
 "comment" : "Writes an XML Summary report from a report configuration file. The report configuration acts as a template for report generation Supports table filtering",
 "parameters" : [ 'workspaceVar','configProps'],
 "core": "1.596",
 "authors" : [{ name : "Ioannis Moutsatsos" }]} END META**/

/**
 * Writes a Summary Display Jenkins Plugin XML Template driven by a Report Configuration file.
 * Summary content can be read from files formatted as Java properties or in delimited format
 * Author: Ioannis K. Moutsatsos
 * Last Update: 6/9/2016
 * Required Script Parameters: workspaceVar, configProps
 */

import groovy.xml.*

println "\n---------Write XML Template for Summary Display Plugin (writeXMLProperties_scriptlet.groovy)---------"
def env = System.getenv() //also get the environment
def workspace = workspaceVar//scriptlet parameter:$WORKSPACE
def options = [:]
reportConfigProps = configProps //scriptlet parameter:http://yourJenkinsUrl/userContent/reportConfiguration.properties

def imageExtensions = ['tif', 'tiff', 'png', 'jpeg', 'jpg', 'gif', 'bmp']
operRelational=['>','>=','<','<=', 'in']
operMethod=['startsWith','contains','endsWith', 'matches']

/* Set default size for rendered images.
   May be changed from report configuration on per table basis using the 'imgwidth' table property
 */
def imgWidth = 200
def separator = ','

def noPropUse = false // a flag whether properties file will be used
//Create the properties objects, from the file system:
Properties configProps = new Properties()   // report configuration
Properties summaryProps = new Properties()   // report content
File configFile = new File(reportConfigProps)

configProps.load(configFile.newDataInputStream())
propSource = configProps.getProperty('summary.properties')
if (propSource.startsWith('none')) {
    noPropUse = true
    println 'Report does not use properties file'
} else {
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
/* Maps for column selection criteria */
exactSelect = [:]
relationalSelect = [:]
isFiltered = false //will be set appropriately if the table data is filtered with a table.select property


def reportStyle = configProps.getProperty('report.style')

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
        thead = []; theadTemp = []
        thead = configProps.getProperty('tab.header').split(',')
        //confirm that content file (CSV) exists for each tab.header and remove those headers for which it does not
        println "TABS-Original: $thead"
        thead.each {
            if (configProps.getProperty("content.${it}") == 'table') {
                if (configProps.getProperty("table.data.${it}").startsWith('http')) {
                    if (getResponseCode(configProps.getProperty("table.data.${it}")) == 200) {
                        theadTemp.add(it)
                    } else {
                        println "\tCould not access content: ${configProps.getProperty("table.data.${it}")}"
                    }
                } else {
                    tabContent = "${workspace}/${configProps.getProperty("table.data.${it}")}"
                    tabContentFile = new File(tabContent)
                    if (tabContentFile.exists()) {
                        theadTemp.add(it)
                    } else {
                        println "\tCould not find content: $tabContent"
                    }
                }// end logic check
            }
            if (configProps.getProperty("content.${it}") == 'field' && !noPropUse) {
                tabContent = configProps.getProperty("summary.properties")
                theadTemp.add(it)
            }

        }
        thead = theadTemp //replace header with subset where content exists
        println "TABS-Adjusted: $thead (from available content)"
        xml.tabs {
            thead.each {
                //println it
                tabContent = configProps.getProperty("content.${it}")
                if (configProps.getProperty("separator.${it}") != null) {
                    separator = configProps.getProperty("separator.${it}")
                } else {
                    separator = ','
                }
                startOfKey = configProps.getProperty("field.key.${it}")
                valColor = configProps.getProperty('field.value.color')
                tabName = it
                tab(name: "$it") {
                    switch (tabContent) {
                        case "field":
                            summaryProps.sort().each { k, v ->
                                if (k.toString().startsWith(startOfKey) && v != null) {
                                    if (v.startsWith('http')) {
                                        field(name: k, value: 'Link', detailcolor: valColor, href: '/' + v.split(':/')[1])
                                    } else {
                                        field(name: k, value: v, detailcolor: valColor)
                                    }
                                }

                            }//end each
                            break
                        case "table":
                            //create a table from referenced file
                            // println "Working with $tabName"
                            def dataTableSource = "${configProps.getProperty("table.data.${tabName}")}" //"${workspace}/${configProps.getProperty("table.data.${tabName}")}"
                            println "\n$tabName : Table data from: $dataTableSource"
                            propKey = "table.header.${tabName}"
                            // if no table.length property is defined we write the entire table
                            def ignoreLineCount = false
                            def rownum = 0
                            if (configProps.getProperty("table.select.${tabName}") != null) {
                                exactSelect = [:]
                                relationalSelect = [:]
                                isFiltered = true
                                parseSelectCriteria(configProps.getProperty("table.select.${tabName}"))
                            } else {
                                isFiltered = false
                            }
                            if (configProps.getProperty("table.length.${tabName}") != null) {
                                rownum = configProps.getProperty("table.length.${tabName}").toInteger()
                            } else {
                                ignoreLineCount = true
                            }
                            if (configProps.getProperty("table.imgwidth.${tabName}") != null) {
                                imgWidth = configProps.getProperty("table.imgwidth.${tabName}").toInteger()
                            }

                            if (dataTableSource.startsWith('http')) {
                                dataTableSource.toURL().withReader { reader ->
                                    columnSet = reader.readLine().split(separator)
                                    if (configProps.containsKey("table.header.${tabName}".toString())) {
                                        rowheader = configProps.getProperty("table.header.${tabName}").split(',')
                                    } else {
                                        rowheader = columnSet
                                    }
                                    headerIndex = createIndexIntoList(rowheader.toList(), columnSet.toList())
                                }

                            } else {
                                dataTableSource = "${workspace}/${configProps.getProperty("table.data.${tabName}")}"
                                def dataFile = new File(dataTableSource)
                                dataFile.withReader {
                                    reader ->
                                        columnSet = reader.readLine().split(separator)
                                        if (configProps.containsKey("table.header.${tabName}".toString())) {
                                            rowheader = configProps.getProperty("table.header.${tabName}").split(',')
                                        } else {
                                            rowheader = columnSet
                                        }
                                        headerIndex = createIndexIntoList(rowheader.toList(), columnSet.toList())
                                }
                                //columnSet //must read CSV file from path
                            }
                            // DMPQM-298 make table sort-able
                            table(sorttable: "yes") {
                                //create table header
                                //now add table data
                                if (dataTableSource.startsWith('http')) {
                                    lineCount = 0
                                    dataTableSource.toURL().eachLine { uline, rowIndex ->
                                        if (isSelected(rowheader, uline, exactSelect, relationalSelect, rowIndex)) {
                                            headerIndex.removeAll([-1])
                                            lineValueSet = uline.split(separator)
                                            columnValueMatch=lineValueSet.size()==columnSet.size()
                                            if (lineCount < rownum + 1 && columnValueMatch) {
                                                lineValueSet = uline.split(separator)[headerIndex]
                                                tr() {
                                                    lineValueSet.each { h ->
                                                        //if column heading exists we create a table cell, else we skip
                                                        //we also check that the row can be split into enough values or we skip
                                                        isImage = false //default flag for image file-values
                                                        if (h != null) {
                                                            //check if cell value is an image
                                                            imageExtensions.each {
                                                                if (h.endsWith(it)) {
                                                                    isImage = true
                                                                }
                                                            }
                                                            if (h.startsWith('http') && isImage) {
                                                                def imgUrl = (h.split(':/')[1]).replaceAll('\\\\', '')
                                                                xml.mkp.yieldUnescaped("<td><![CDATA[<a href=\"${'/' + imgUrl}\"/><img width=$imgWidth src=\"${'/' + imgUrl}\"/></a>]]></td>")
                                                            } else if (h.startsWith('http')) {
                                                                td(value: 'Link', bgcolor: 'white', fontcolor: 'black', align: 'left', href: '/' + h.split(':/')[1])
                                                            } else {
                                                                td(value: h, bgcolor: 'white', fontcolor: 'black', align: 'left')
                                                            }
                                                        }
                                                    }

                                                }
                                                // if we are counting lines we keep track
                                                if (!ignoreLineCount) {
                                                    lineCount++
                                                }
                                            }
                                        }//end if isSelected
                                    }//end each line
                                } //end if startsWith http
                                else {
                                    //reading from local file system
                                    dataFile = new File(dataTableSource)
                                    lineCount = 0
                                    dataFile.eachLine { uline, rowIndex ->
                                        if (isSelected(rowheader, uline, exactSelect, relationalSelect, rowIndex)) {
                                            headerIndex.removeAll([-1])
                                            lineValueSet = uline.split(separator)
                                            columnValueMatch=lineValueSet.size()==columnSet.size()
//                                            println "${lineValueSet.size()}==${columnSet.size()}"
                                            if (lineCount < rownum + 1 && columnValueMatch) {
//                                                columnSet = uline.split(separator)
//                                                thisHeaderIndex=headerIndex.intersect((0..(columnSet.size()-1)))
                                                lineValueSet = uline.split(separator)[headerIndex]
                                                tr() {
                                                    lineValueSet.each { h ->
                                                        //if column heading exists we create a table cell, else we skip
                                                        //we also check that the row can be split into enough values or we skip
                                                        //set logic flag if cell value is an image
                                                        isImage = false //default flag for image file-values

                                                        if (h != null) {
                                                            imageExtensions.each {
                                                                if (h.endsWith(it)) {
                                                                    isImage = true
                                                                }
                                                            }
                                                            if (h.startsWith('http') && isImage) {
                                                                def imgUrl = (h.split(':/')[1]).replaceAll('\\\\', '')
                                                                xml.mkp.yieldUnescaped("<td><![CDATA[<a href=\"${'/' + imgUrl}\"/><img width=$imgWidth src=\"${'/' + imgUrl}\"/></a>]]></td>")
                                                            } else if (h.startsWith('http')) {
                                                                td(value: 'Link', bgcolor: 'white', fontcolor: 'black', align: 'left', href: '/' + h.split(':/')[1])
                                                            } else {
                                                                td(value: h, bgcolor: 'white', fontcolor: 'black', align: 'left')
                                                            }
                                                        }
                                                    }

                                                }
                                                // if we are counting lines we keep track
                                                if (!ignoreLineCount) {
                                                    lineCount++
                                                }
                                            }
                                        }//end if isSelected
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
        println 'Table reports are not currently supported'
        break
}

def writer4file = new FileWriter("$workspace/writeXmlSummary.xml")
XmlUtil.serialize(writer.toString(), writer4file)
println "\nSummary Display XML Template: ${new File("$workspace/writeXmlSummary.xml").getCanonicalPath()}"
writer4file.close() //close the file

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
    return indexList
}
/* Method checks if a URL is accessible
 */

def getResponseCode(String urlString) throws MalformedURLException, IOException {
    URL u = new URL(urlString);
    HttpURLConnection huc = (HttpURLConnection) u.openConnection();
    huc.setRequestMethod("GET");
    huc.connect();
    return huc.getResponseCode();
}

/*Parses a groovy map string with column selection criteria
* modifies global vars exactSelect, relationalSelect
* */

def parseSelectCriteria(selectMapString) {
    selectClauses = evaluate("$selectMapString")
    /*parse and assign exact and relational selection criteria*/
    selectClauses.each { k, v ->
        if (v.getClass() == LinkedHashMap) {
    println "\tSELECT '$k' WHERE: $v"
            relationalSelect.put(k, v)
        } else {
    println "\tSELECT '$k' WHERE: $v"
            exactSelect.put(k, v)
        }

    }
}

/*Method to determine if a row should be included
* We always include header row and do not filter when the table.select is not set
* */

def isSelected(headList, rowText, exactSelect, relationalSelect, rowCount) {
    if (isFiltered && rowCount > 1) {
        line = rowText.split(',')
        criteria = []
        if (exactSelect != [:]) {
//            println "Exact select $exactSelect"
            criteria.add(isExactRowMatch(headList, line, exactSelect))
        }
        if (relationalSelect != [:]) {
//            println "Method select $relationalSelect"
            criteria.add(isRelationalRowMatch(headList, line, relationalSelect))
        }

        return criteria.contains(false) == false

    }else{
        //unless isFiltered we select all rows
        return true
    }
}




/*method determines whether a row should be included by the matching criteria
 Matches by exact values using AND between clauses
*/

def isExactRowMatch(headlist, line, selClauses) {
    columnIndex = selClauses.keySet()
    ci = headlist.findIndexValues { it in columnIndex }
    testVals = line[ci]
    selectCombinations = GroovyCollections.combinations(selClauses.values())
    return selectCombinations.findResult { (it as String) == (testVals as String) ? it : null } != null
}

/*
 Method creates appropriate assertions that check whether a row
 contains data that match the relational operators and methods
 Note that we rely on a special grammar for that.
*/
def isRelationalRowMatch(headlist,line,selClauses) {
    testClause = [] //a list to keep result of tests
    columnIndex = selClauses.keySet()
    columnIndex.each {
        ci = headlist.findIndexValues { ind -> ind in it }
        testValue = line[ci]
        testValue.each { tv ->
            /* assert that only supported operators are used */
            assert selClauses[it].operator in operRelational.plus(operMethod)

            if (selClauses[it].operator in operRelational ){
                assertion = "$tv ${selClauses[it].operator} ${selClauses[it].value}"
//                println "$tv ${selClauses[it].operator} ${selClauses[it].value}"
                if (selClauses[it].negate==true){
//              println 'Negating assertion'
                    assertion= "$tv ${selClauses[it].operator} ${selClauses[it].value}==false"
                }else{
                    assertion = "$tv ${selClauses[it].operator} ${selClauses[it].value}".replace('\\','/')
                }
                testResult = evaluate(assertion)
                testClause.add(testResult)
            }//end in operRelational operator

            if (selClauses[it].operator in operMethod ){
                methodTest=[] //a list to keep test from methods
                selClauses[it].value.each{op->
//                    println "${selClauses[it]}: ${selClauses[it].negate}"
                    if (selClauses[it].negate==true){
//              println 'Negating assertion'
                        assertion= "\'${tv}\'.${selClauses[it].operator}(\'$op\')==false".replace('\\','/')
                    }else{
                        assertion= "\'${tv}\'.${selClauses[it].operator}(\'$op\')".replace('\\','/')
                    }
//              println 'Asserting:'+assertion
                    methodTest.add(evaluate(assertion))
                }
                testClause.add(methodTest.contains(true))
            }//end in operMethod operator


        }

//        println testClause
    }
    return testClause.contains(false) == false
}
