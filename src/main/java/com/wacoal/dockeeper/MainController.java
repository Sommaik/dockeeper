/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wacoal.dockeeper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wacoal.dockeeper.wsdl.EmpClass;
import com.wacoal.dockeeper.wsdl.GetEmpByFilterResponse;
import java.security.Principal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import javax.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author sommaik
 */
@RestController
@RequestMapping("/main")
public class MainController {

    @Autowired
    EmpClient empClient;

    @Autowired
    DataSource datasouce;
            
    @RequestMapping(method = RequestMethod.GET, value = "")
    public ModelAndView index(Model model, Principal principal) {
        GetEmpByFilterResponse resp = empClient.getEmpByFilter();
        List<EmpClass> emps = resp.getGetEmpByFilterResult().getEmpClass();
        EmpClass emp = emps.get(0);
        model.addAttribute("emp", emp);
        System.out.println(" this is a user login "+ principal.getName());
        return new ModelAndView("main");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{name}")
    public String getName (
            @PathVariable String name
    ) throws Exception {
        StringBuilder restUrl = new StringBuilder("http://twcapi.wacoalsampan.com/api/emp/search/v1?empname=");
        restUrl.append(name)
                .append("&api_token=BI8YXJFZmvYGhhx9MjjoLcC8mLMza85oUN9uJtH7uMUEORlqhTNBQ6fMEZXn");

        RestTemplate rest = new RestTemplate();
        String resp = rest.getForObject(restUrl.toString(), String.class);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String>[] map = mapper.readValue(resp, new TypeReference<Map<String, String>[]>(){});
        System.out.println( map[0].get("nameth") );
        return resp;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/some")
    public String getSomeParam(
            @RequestParam(value = "name", required = false, defaultValue = "World") String name
    ) throws Exception {
        Connection con = this.datasouce.getConnection();
        Statement stmt = con.createStatement();
        ResultSet res = stmt.executeQuery("select * from tb_attach");
        while(res.next()){
            System.out.println(res.getString("attach_file"));
        }
        res.close();
        stmt.close();
        
        CallableStatement func = con.prepareCall("");
        
        con.close();
        
        return "Some " + name;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/some")
    public String getSomeParamPost(
            @RequestBody String name
    ) {
        return "Some " + name;
    }
    
    @GetMapping("/reports/{reportname}")
    public ModelAndView viewReports(
           final ModelMap modelMap,
           ModelAndView view,
           @PathParam("reportname") String reportname,
           @RequestParam("format") String format,
           @RequestParam("p_type_id") String typeId
    ){
       
        modelMap.put("datasource", this.datasouce);
        modelMap.put("format", format);
        modelMap.put("p_type_id", typeId);
        view = new ModelAndView(reportname, modelMap);
        
        return view;
    }

}
