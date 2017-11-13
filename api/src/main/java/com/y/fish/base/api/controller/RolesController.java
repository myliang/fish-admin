package com.y.fish.base.api.controller;

import com.y.fish.base.api.model.Resource;
import com.y.fish.base.api.model.Role;
import com.y.fish.base.api.repository.MenuRepository;
import com.y.fish.base.api.repository.ResourceRepository;
import com.y.fish.base.api.repository.RoleRepository;
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

/**
 * Created by myliang on 11/7/17.
 */
@RestController
@RequestMapping("/api/roles")
public class RolesController {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    MenuRepository menuRepository;
    @Autowired
    ResourceRepository resourceRepository;

    @GetMapping({"", "/"})
    @PreAuthorize("hasPermission('Role', 'read')")
    public List index(HttpServletRequest request, HttpServletResponse response) {
        Pagination page = roleRepository.where(SqlQuery.build(request).like("name"));
        response.addHeader("x-total-count", page.getTotal() + "");
        return page.getContent();
    }

    @GetMapping("/all")
    public List all() {
        return roleRepository.all();
    }

    @GetMapping("/permissions")
    public Object permissions() {
        List<Resource> resources = resourceRepository.all();
        Map<String, List<String>> ret = new HashMap<>();
        resources.stream().forEach(r -> {
            if (!ret.containsKey(r.getEntityName())) {
                ret.put(r.getEntityName(), new ArrayList<>());
            }
            ret.get(r.getEntityName()).add(r.getPermission().name());
        });
        return ret;
    }

    @GetMapping({"/{id}"})
    @PreAuthorize("hasPermission('Role', 'read')")
    public Role show(@PathVariable Long id) throws Exception {
        return roleRepository.find(id);
    }

    @PostMapping(value = {"", "/"})
    @PreAuthorize("hasPermission('Role', 'create')")
    public ResponseEntity create(@Validated @RequestBody Role role) throws Exception {
        roleRepository.save(role);
        return ResponseEntity.ok("{}");
    }

    @PutMapping({"/{id}"})
    @PreAuthorize("hasPermission('Role', 'update')")
    public ResponseEntity update(@PathVariable Long id, @Validated @RequestBody Role role) throws Exception {
        roleRepository.update(role, id);
        return ResponseEntity.ok("{}");
    }

    @DeleteMapping({"/{id}"})
    @PreAuthorize("hasPermission('Role', 'destroy')")
    public ResponseEntity destroy(@PathVariable Long id) throws Exception {
        if (id > 1) roleRepository.delete(id);
        return ResponseEntity.ok("{}");
    }

}
