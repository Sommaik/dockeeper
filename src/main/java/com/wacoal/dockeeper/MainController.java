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
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
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

    @RequestMapping(method = RequestMethod.GET, value = "")
    public ModelAndView index(Model model) {
        GetEmpByFilterResponse resp = empClient.getEmpByFilter();
        List<EmpClass> emps = resp.getGetEmpByFilterResult().getEmpClass();
        EmpClass emp = emps.get(0);
        model.addAttribute("emp", emp);
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
    ) {
        return "Some " + name;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/some")
    public String getSomeParamPost(
            @RequestBody String name
    ) {
        return "Some " + name;
    }

}
