package com.mzdev;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileCompressor {

    private String SOURCE_FOLDER ; // template path
    private String OUTPUT_ZIP_FILE ;
    private List<String> fileList = new ArrayList<>();

    public FileCompressor(String SOURCE_FOLDER, String fileName) {

        this.SOURCE_FOLDER = SOURCE_FOLDER;

        // generated file name and location here
        OUTPUT_ZIP_FILE = Main.saveDir + fileName + ".docx";


    }



    // this method must run in a background thread
    public void Compress() {

            //todo: show loading dialog
            System.out.println("Generating....");

            try {

                GenerateFilesList(new File(SOURCE_FOLDER));

                byte[] buffer = new byte[1024];

                FileInputStream in;

                //this creates an empty zip file
                FileOutputStream fos = new FileOutputStream(OUTPUT_ZIP_FILE);

                //this is responsible for filling that file with data
                ZipOutputStream zipOut = new ZipOutputStream(fos);

                for (String file : fileList) {
                    in = new FileInputStream(SOURCE_FOLDER + File.separator +file);
                    ZipEntry zipEntry = new ZipEntry(file);
                    zipOut.putNextEntry(zipEntry);

                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        zipOut.write(buffer, 0, len);
                    }


                    in.close();

                }

                zipOut.closeEntry();
                zipOut.close();


                //todo: make a popup here to show that every thing is done
                System.out.println("Done");
            }

            catch (IOException e) {
                e.printStackTrace();
            }


    }




    //this function generates all files list in a directory
    private void GenerateFilesList(File node){

         if (node.isFile()){
             String file = node.getAbsolutePath();
             fileList.add(file.substring(SOURCE_FOLDER.length()+1));
         }
         if (node.isDirectory()){
             if(node.isDirectory()){
                 String[] subNote = node.list();
                 for(String filename : subNote){
                     GenerateFilesList(new File(node, filename));
                 }
             }
         }

    }
}
