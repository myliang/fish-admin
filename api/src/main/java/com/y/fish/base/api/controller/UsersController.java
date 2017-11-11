package com.y.fish.base.api.controller;

import com.y.fish.base.api.model.Role;
import com.y.fish.base.api.model.User;
import com.y.fish.base.api.repository.RoleRepository;
import com.y.fish.base.api.repository.UserRepository;
import com.y.fish.base.api.sql.Pagination;
import com.y.fish.base.api.sql.SqlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by myliang on 11/8/17.
 */
@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @GetMapping({"", "/"})
    @PreAuthorize("hasPermission('User', 'read')")
    public List index(HttpServletRequest request, HttpServletResponse response) {
        Pagination<User> page = userRepository.where(SqlQuery.build(request).like("userName").eq("state"));
        response.addHeader("x-total-count", page.getTotal() + "");
        return page.getContent().stream().map((item) -> {
            //  get role
            Role role = roleRepository.find(item.getRoleId());
            Map<String, Object> ret = new HashMap();
            ret.put("userName", item.getUserName());
            ret.put("state", item.getState());
            ret.put("roleName", role != null ? role.getName() : "");
            ret.put("createdAt", item.getCreatedAt());
            return ret;
        }).collect(Collectors.toList());
    }

    @GetMapping({"/{id}"})
    @PreAuthorize("hasPermission('User', 'read')")
    public User show(@PathVariable Long id) throws Exception {
        return userRepository.find(id);
    }

    @PostMapping(value = {"", "/"})
    @PreAuthorize("hasPermission('User', 'create')")
    public ResponseEntity create(@Validated @RequestBody User user) throws Exception {
        user.setEncoderPassword("123456");
        userRepository.save(user);
        return ResponseEntity.ok("{}");
    }

    @PatchMapping({"/{id}"})
    @PreAuthorize("hasPermission('User', 'update')")
    public ResponseEntity update(@PathVariable Long id, @RequestBody User user) throws Exception {
        user.setEncoderPassword(user.getPassword());
        userRepository.update(user, id);
        return ResponseEntity.ok("{}");
    }

    @DeleteMapping({"/{id}"})
    @PreAuthorize("hasPermission('User', 'destroy')")
    public ResponseEntity destroy(@PathVariable Long id) throws Exception {
        if (id > 1) userRepository.delete(id);
        return ResponseEntity.ok("{}");
    }
}
