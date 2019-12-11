package com.mzdev;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public  class WordXmlEditor {


    private  List<Node> introNodeList = new ArrayList<>();
    private  List<Node> procedureNodeList= new ArrayList<>();
    private  List<Node> objectivesNodeList= new ArrayList<>();
    private  List<Node> apparatusNodeList= new ArrayList<>();
    private  List<Node> analysisNodeList= new ArrayList<>();
    private  List<Node> conclusionNodeList= new ArrayList<>();
    private File inputFile;
    private DocumentBuilderFactory docFactory;
    private DocumentBuilder docBuilder;
    private Document doc;
    private String xmlPath;


    private Node titleNode;
    private Node membersNode;
    private Node dateNode;
    private List <Node> colorNodes = new ArrayList<>();
    private Node groupNumberNode;
    private Node instructorNode;


    public WordXmlEditor(String xmlPath) {
        this.xmlPath = xmlPath;


        //parsing the xml
        try {
            inputFile = new File(xmlPath);
            docFactory = DocumentBuilderFactory.newInstance();
            docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.parse(inputFile);



            NodeList list =  doc.getElementsByTagName("w:t");

            for (int i = 0; i < list.getLength() ; i++) {


                if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) list.item(i);
                    String nodeValue = eElement.getTextContent();

                    switch (nodeValue){

                        case "Module Name" :
                            titleNode = list.item(i);
                            break;
                        case "Madaoui Zakaria" :
                            membersNode = list.item(i);
                            break;
                            case "instructor" :
                                instructorNode = list.item(i);
                            break;
                        case "Groupe: " :
                            groupNumberNode = list.item(i);
                            break;

                        case "Todays Date" :
                            dateNode = list.item(i);

                    }


                    //ORDER: title / space / list / space
                    if (nodeValue.equals("Introduction:") || nodeValue.equals("intro")){

                        introNodeList.add(list.item(i));
                    }


                    if (nodeValue.equals("Objectives:") || nodeValue.equals("sp1") || nodeValue.equals("objective1") ||  nodeValue.equals("sp2")){

                        objectivesNodeList.add(list.item(i));
                    }

                    if (nodeValue.equals("Apparatus:") || nodeValue.equals("sp3") || nodeValue.equals("apparatus1") ||  nodeValue.equals("sp4")){

                        apparatusNodeList.add(list.item(i));
                    }

                    if (nodeValue.equals("Procedure:") || nodeValue.equals("sp5") || nodeValue.equals("procedure1") ||  nodeValue.equals("sp6")){

                        procedureNodeList.add(list.item(i));
                    }

                    if (nodeValue.equals("Analysis:") || nodeValue.equals("sp7") || nodeValue.equals("analysis1") ||  nodeValue.equals("sp8")){

                        analysisNodeList.add(list.item(i));
                    }

                    if (nodeValue.equals("Conclusion:") || nodeValue.equals("By the end of this lab experiment we learned ")){

                        conclusionNodeList.add(list.item(i));
                    }



                }

            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    void setLabTitle(String title){

        titleNode.setTextContent(title);



    }


    void setLabColor(String color){

        Node colorNode1 = doc.getElementsByTagName("v:rect").item(2).getAttributes().item(1);
        Node colorNode2 = doc.getElementsByTagName("v:rect").item(3).getAttributes().item(1);

        color= color.substring(2,8);
        colorNode1.setNodeValue("#"+ color);
        colorNode2.setNodeValue(color);


        NodeList cclist = doc.getElementsByTagName("a:srgbClr");


        for (int i = 0; i < cclist.getLength() ; i++) {

            if ("aadcf7".equals(cclist.item(i).getAttributes().item(0).getNodeValue())) {
                cclist.item(i).getAttributes().item(0).setNodeValue(color);
            }
        }


    }

    void setLabGroupNumber(int groupNumber){

        groupNumberNode.setTextContent("Group: " + groupNumber);

    }

    void setMembersNameAndNumber(List<String>  names){

        membersNode.setTextContent(names.get(0));

        // do this for all names in the list and dont forget to change the names when duplicating
        for (int i = 1; i < names.size() ; i++) {
            duplicateNode(membersNode,names.get(i));
        }
    }

    void setDate(String date){
        dateNode.setTextContent(date);
    }

    void setInstructorNode(String name){
        instructorNode.setTextContent(name);
    }


    void saveChanges(){
        try {

            File temp  = new File("src/com/mzdev/template/word/document.xml");
            temp.delete();

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Result output = new StreamResult(new File(temp.getAbsolutePath()));
            Source input = new DOMSource(doc);

            transformer.transform(input, output);
        } catch (Exception e){
            e.printStackTrace();
        }
    }



    private void duplicateNode(Node node,String name) {

        Element root = (Element) node.getParentNode().getParentNode().getParentNode();
        Element origPerson = (Element) node.getParentNode().getParentNode();
        Element newPerson = (Element) origPerson.cloneNode(true);
        root.insertBefore(newPerson,origPerson);

        node.setTextContent(name);

    }


    private void duplicateElement(Node node) {

        Node root = node.getParentNode().getParentNode().getParentNode();
        Node origin =  node.getParentNode().getParentNode();
        Node neww = origin.cloneNode(true);
        root.insertBefore(neww,origin);

    }
    void enableIntro(boolean enable){


        for (Node item : introNodeList) {
            System.out.println(item.getTextContent());
        }
        for (Node item : objectivesNodeList) {
            System.out.println(item.getTextContent());
        }
        for (Node item : apparatusNodeList) {
            System.out.println(item.getTextContent());
        }
        for (Node item : procedureNodeList) {
            System.out.println(item.getTextContent());
        }
        for (Node item : analysisNodeList) {
            System.out.println(item.getTextContent());
        }
        for (Node item : conclusionNodeList) {
            System.out.println(item.getTextContent());
        }


        
        if (!enable){
            introNodeList.get(0).getParentNode().getParentNode().getParentNode().removeChild(introNodeList.get(0).getParentNode().getParentNode());
            introNodeList.get(1).getParentNode().getParentNode().getParentNode().removeChild(introNodeList.get(1).getParentNode().getParentNode());
        }
        else {
            introNodeList.get(1).setTextContent("");
        }
        
    }
    void enableConclusion(boolean enable){
        
        if (!enable){
            conclusionNodeList.get(0).getParentNode().getParentNode().getParentNode().removeChild(conclusionNodeList.get(0).getParentNode().getParentNode());
            conclusionNodeList.get(1).getParentNode().getParentNode().getParentNode().removeChild(conclusionNodeList.get(1).getParentNode().getParentNode());
        }
    }

    void enableTitle(String title, boolean enable){
        List<Node> list = new ArrayList<>();
        switch (title){

            case "Objectives":
                list = objectivesNodeList ;
                break;
            case "Apparatus":
                list = apparatusNodeList ;
                break;
            case "Procedure":
                list = procedureNodeList ;
                break;
            case "Analysis":
                list = analysisNodeList ;
                break;
        }


        if (!enable){

            list.get(0).getParentNode().getParentNode().getParentNode().removeChild(list.get(0).getParentNode().getParentNode());
            list.get(2).getParentNode().getParentNode().getParentNode().removeChild(list.get(2).getParentNode().getParentNode());
            list.get(1).getParentNode().getParentNode().getParentNode().removeChild(list.get(1).getParentNode().getParentNode());
            list.get(3).getParentNode().getParentNode().getParentNode().removeChild(list.get(3).getParentNode().getParentNode());


        }

        else {


            list.get(2).setTextContent("");
            duplicateElement(list.get(2));
            duplicateElement(list.get(2));
            list.get(1).setTextContent("");
            list.get(3).setTextContent("");

        }


    }

}
