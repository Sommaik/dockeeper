/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wacoal.dockeeper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author sommaik
 */
@RestController
@RequestMapping("/ajax")
public class AjaxController {
    
    @Autowired
    DataSource datasource;
    
    @GetMapping("/findName")
    public String findName (
            @RequestParam("term") String name
    ) throws Exception {
        StringBuilder restUrl = new StringBuilder("http://twcapi.wacoalsampan.com/api/emp/search/v1?empname=");
        restUrl.append(name)
                .append("&api_token=BI8YXJFZmvYGhhx9MjjoLcC8mLMza85oUN9uJtH7uMUEORlqhTNBQ6fMEZXn");

        RestTemplate rest = new RestTemplate();
        String resp = rest.getForObject(restUrl.toString(), String.class);
        return resp;
    }
    
    
    @GetMapping("/mstype")
    public ArrayList<Map<String, Object>> getMsType() throws Exception{
        Connection con = this.datasource.getConnection();
        Statement stmt = con.createStatement();
        ResultSet res = stmt.executeQuery("select * from ms_type");
        ArrayList<Map<String, Object>> datas = new ArrayList<>();
        while(res.next()){
            Map<String, Object> data = new HashMap<>();
            data.put("typeId", res.getString("type_id"));
            data.put("typeName", res.getString("type_name"));
            datas.add(data);
        }
        res.close();
        stmt.close();
        return datas;
    }
    
    
}
