package com.y.fish.base.api.controller;

import com.y.fish.base.api.model.Menu;
import com.y.fish.base.api.model.Resource;
import com.y.fish.base.api.model.Role;
import com.y.fish.base.api.model.User;
import com.y.fish.base.api.repository.MenuRepository;
import com.y.fish.base.api.repository.ResourceRepository;
import com.y.fish.base.api.repository.RoleRepository;
import com.y.fish.base.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by myliang on 11/9/17.
 */
@RestController
@RequestMapping("/api/current")
public class CurrentController {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    MenuRepository menuRepository;

    @GetMapping("/user")
    public Map user() {
        Map retMap = new HashMap();
        String userName = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUserName(userName);
        retMap.put("user", user);

        Map<String, List<String>> ret = new HashMap<>();
        // permissions
        if (user.isAdmin()) {
            List<Resource> resources = resourceRepository.all();
            for (Resource resource : resources) {
                if (!ret.containsKey(resource.getEntityName())) {
                    ret.put(resource.getEntityName(), new ArrayList<>());
                }
                ret.get(resource.getEntityName()).add(resource.getPermission().name());
            }
        } else {
            Role role = roleRepository.find(user.getRoleId());
            if (role != null) ret = role.getPermissionsMap();
        }
        retMap.put("permissions", ret);

        Set<String> keySet = new HashSet<>();
        ret.forEach((k, v) -> keySet.add(k.toLowerCase()));

        retMap.put("menus", menuRepository.findByParentId(0L).stream().map(m -> {
            Stream<Menu> stream = menuRepository.findByParentId(m.getId()).stream();
            List<Menu> menus = stream.filter(s -> keySet.contains(s.getKey())).collect(Collectors.toList());
            if (menus.size() > 0) {
                Map map = new HashMap();
                map.put("key", m.getKey());
                map.put("title", m.getName());
                map.put("children", menus.stream().map(sm -> {
                    Map subMap = new HashMap();
                    subMap.put("key", sm.getKey());
                    subMap.put("title", sm.getName());
                    subMap.put("url", sm.getUrl());
                    return subMap;
                }).toArray());
                return map;
            } else {
                return null;
            }

        }).filter(s -> s != null).toArray());

        return retMap;
    }
}
