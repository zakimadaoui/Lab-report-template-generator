package com.mzdev;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main extends Application {

    //files and paths
    private static String xmlPath = "src/com/mzdev/template/word/document.xml";
    private File template = new File("src/com/mzdev/template");
    static String saveDir = "/home/zaki/Desktop/" ;
    private File originDoc = new File("src/com/mzdev/template/word/document.xml");
    private File backupDoc = new File("src/com/mzdev/temp/document.xml");
    private File myImage = new File("src/com/mzdev/img/header.jpg");


    //Variables
    private static String moduleName ;
    private static int labNumber ;
    private static int groupNumber ;
    private static List<String> membersNames = new ArrayList<>();
    private static String labColor ;
    private static String generatedFileName;
    private static String instructorName;
    private CheckBox intro_checkbox;
    private CheckBox objectives_checkbox;
    private CheckBox apparatus_checkbox;
    private CheckBox procedure_checkbox;
    private CheckBox analysis_checkbox;
    private CheckBox conclusion_checkbox;


    @Override
    public void start(Stage primaryStage) throws Exception{


        File inputFile = new File(xmlPath);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(inputFile);

        Parent root = FXMLLoader.load(getClass().getResource("MainActivity.fxml"));

        //MainActivity
        Scene scene = new Scene(root,600, 500);
        primaryStage.setTitle("Lab Report Template Generator");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);



        TextField moduleNameField = (TextField) scene.lookup("#lab_name_editText");
        TextField labNumberField = (TextField) scene.lookup("#lab_number_editText");
        TextField groupNumberField = (TextField) scene.lookup("#group_number_editText");
        TextField instructorNameField = (TextField) scene.lookup("#instructor_name_editText");
        TextArea membersNamesField = (TextArea) scene.lookup("#members_textfeild");
        ColorPicker colorPicker = (ColorPicker) scene.lookup("#colors_picker");
        Button generateButton = (Button) scene.lookup("#generate_button");


        intro_checkbox = (CheckBox) scene.lookup("#intro_checkbox");
        objectives_checkbox = (CheckBox) scene.lookup("#objectives_checkbox");
        apparatus_checkbox = (CheckBox) scene.lookup("#apparatus_checkbox");
        procedure_checkbox = (CheckBox) scene.lookup("#procedure_checkbox");
        analysis_checkbox = (CheckBox) scene.lookup("#analysis_checkbox");
        conclusion_checkbox = (CheckBox) scene.lookup("#conclusion_checkbox");




        generateButton.setOnAction(actionEvent -> {


            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Choose directory to save generated lab report:");
            File chosenDirectory = chooser.showDialog(scene.getWindow());
            saveDir = chosenDirectory.getAbsolutePath()+"/";

            //save the data
            moduleName = moduleNameField.getCharacters().toString();
            labNumber = Integer.valueOf(labNumberField.getCharacters().toString());
            groupNumber = Integer.valueOf(groupNumberField.getCharacters().toString());
            instructorName = instructorNameField.getCharacters().toString();
            labColor = colorPicker.getValue().toString();
            membersNames = List.of(membersNamesField.getText().split("\n"));


            // generating the file and restoring the original files to the template
            Thread generateThread = new Thread(() -> {
                copy(originDoc,backupDoc);
                generate();
                copy(backupDoc,originDoc);
            });

            generateThread.start();


        });




        ImageView imageView = (ImageView) scene.lookup("#headerImageView");
        Image image = new Image("file://"+myImage.getAbsolutePath());
        imageView.setImage(image);

        primaryStage.show();
    }






    public static void main(String[] args)  {






        launch(args);
    }

    void generate(){
        //generate file name and date
        generatedFileName = moduleName + " " + String.format("lab #%02d",labNumber) ;
        Calendar today = Calendar.getInstance(Locale.getDefault());
        String date = new SimpleDateFormat("dd/MM/yyyy").format(today.getTime());


        //set template attributes here
        // (this one changes the xml directly)

        WordXmlEditor editor = new WordXmlEditor(xmlPath);
        editor.setLabTitle(generatedFileName);
        editor.setLabGroupNumber(groupNumber);
        editor.setMembersNameAndNumber(membersNames);
        editor.setLabColor(labColor);
        editor.setDate(date);
        editor.setInstructorNode(instructorName.toUpperCase(Locale.ENGLISH));


        //checkboxes conditions:
        if (intro_checkbox.isSelected()){editor.enableIntro(true);} else {editor.enableIntro(false);}
        if (objectives_checkbox.isSelected()){editor.enableTitle("Objectives",true);} else {editor.enableTitle("Objectives",false);}
        if (apparatus_checkbox.isSelected()){editor.enableTitle("Apparatus",true);} else {editor.enableTitle("Apparatus",false);}
        if (procedure_checkbox.isSelected()){editor.enableTitle("Procedure",true);} else {editor.enableTitle("Procedure",false);}
        if (analysis_checkbox.isSelected()){editor.enableTitle("Analysis",true);} else {editor.enableTitle("Analysis",false);}
        if (conclusion_checkbox.isSelected()){editor.enableConclusion(true);} else {editor.enableConclusion(false);}



        //save the changes on the file
        editor.saveChanges();


        //generate the docx file here
        FileCompressor generator = new FileCompressor(template.getAbsolutePath(), generatedFileName);
        generator.Compress();

    }



    private static void copy(File file1, File file2){

        try {



            FileInputStream in = new FileInputStream(file1);
            FileOutputStream out = new FileOutputStream(file2);


            BufferedInputStream bufferedIn = new BufferedInputStream(in);

            // backup
            int b;
            while ((b = bufferedIn.read()) != -1){
                out.write(b);
            }

            in.close();
            out.close();

        }catch (IOException e){
            e.printStackTrace();
        }

    }


}
