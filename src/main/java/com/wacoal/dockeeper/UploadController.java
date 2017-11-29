/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wacoal.dockeeper;

/**
 *
 * @author sommaik
 */
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class UploadController {
    
    @Autowired
    DataSource datasource;
    
    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "/Users/sommaik/Workspace/test/";

    @GetMapping("/upload")
    public String index() {
        return "upload";
    }

    @PostMapping("/doupload") // //new annotation since 4.3
    public String singleFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("typeId") String typeId,
            @RequestParam("ownerId") String ownerId,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:uploadStatus";
        }

        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);
            
            Connection con = this.datasource.getConnection();
            Statement stmt = con.createStatement();
            StringBuilder sb = new StringBuilder();
            sb.append(" insert into tb_attach(owner_id, filename, type_id, create_by, create_date) ")
                    .append(" values(")
                    .append("'").append(ownerId).append("'").append(",")
                    .append("'").append(file.getOriginalFilename()).append("'").append(",")
                    .append("'").append(typeId).append("'").append(",")
                    .append("'").append(principal.getName()).append("'").append(",")
                    .append("getdate()")
                    .append(")")
                    ;
            System.out.println("===============================");
            System.out.println(sb.toString());
            System.out.println("===============================");
//            stmt.executeUpdate(sb.toString());
            stmt.close();
            con.close();
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");
            

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException se){
            se.printStackTrace();
        }

        return "redirect:/uploadStatus";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

}
