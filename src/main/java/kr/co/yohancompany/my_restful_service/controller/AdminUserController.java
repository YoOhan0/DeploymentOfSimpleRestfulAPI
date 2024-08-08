package kr.co.yohancompany.my_restful_service.controller;


import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import jakarta.validation.Valid;
import kr.co.yohancompany.my_restful_service.bean.AdminUser;
import kr.co.yohancompany.my_restful_service.bean.AdminUserV2;
import kr.co.yohancompany.my_restful_service.bean.User;
import kr.co.yohancompany.my_restful_service.dao.UserDaoService;
import kr.co.yohancompany.my_restful_service.exception.UserNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminUserController {
    private UserDaoService service;

    public AdminUserController(UserDaoService service) {
        this.service = service;
    }

//    @GetMapping("/v1/users/{id}")
//    @GetMapping(value = "/users/{id}",headers = "X-API-VERSION=1")
    @GetMapping(value = "/users/{id}",produces="application/vnd.company.appv1+json")
    public MappingJacksonValue retrieveUser4Admin(@PathVariable int id) {
        User user= service.findOne(id);


        AdminUser adminUser=new AdminUser();
        if (user == null) {
            throw new UserNotFoundException(String.format("ID[%s] not found", id));
        } else {
            BeanUtils.copyProperties(user,adminUser);
        }

        SimpleBeanPropertyFilter filter=SimpleBeanPropertyFilter.filterOutAllExcept("id","name","joinDate","ssn");
        FilterProvider filters= new SimpleFilterProvider().addFilter("UserInfo",filter);

        MappingJacksonValue mapping=new MappingJacksonValue(adminUser);
        mapping.setFilters(filters);

        return mapping;
    }


    // --> /admin/v2/users/{id}
//    @GetMapping(value = "/users/{id}",headers="X-API-VERSION=2")
    @GetMapping(value = "/users/{id}",produces="application/vnd.company.appv2+json")
    public MappingJacksonValue retrieveUser4AdminV2(@PathVariable int id) {
        User user= service.findOne(id);


        AdminUserV2 adminUser=new AdminUserV2();
        if (user == null) {
            throw new UserNotFoundException(String.format("ID[%s] not found", id));
        } else {
            BeanUtils.copyProperties(user,adminUser);
            adminUser.setGrade("VIP"); //grade
        }

        SimpleBeanPropertyFilter filter=SimpleBeanPropertyFilter.filterOutAllExcept("id","name","joinDate","grade");
        FilterProvider filters= new SimpleFilterProvider().addFilter("UserInfoV2",filter);

        MappingJacksonValue mapping=new MappingJacksonValue(adminUser);
        mapping.setFilters(filters);

        return mapping;
    }

    // --> /admin/users
    @GetMapping("/users")
    public MappingJacksonValue retrieveAllUsers4Admin() {
        List<User> users= service.findAll();

        List<AdminUser> adminUsers =new ArrayList<>();
        AdminUser adminUser=null;

        for (User user : users) {
            adminUser=new AdminUser();
            BeanUtils.copyProperties(user,adminUser);

            adminUsers.add(adminUser);
        }

        SimpleBeanPropertyFilter filter=SimpleBeanPropertyFilter.filterOutAllExcept("id","name","joinDate","ssn");
        FilterProvider filters= new SimpleFilterProvider().addFilter("UserInfo",filter);

        MappingJacksonValue mapping=new MappingJacksonValue(adminUsers);
        mapping.setFilters(filters);

        return mapping;
    }


}
