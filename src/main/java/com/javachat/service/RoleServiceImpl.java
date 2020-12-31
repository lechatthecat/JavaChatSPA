package com.javachat.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.javachat.model.Role;
import com.javachat.repository.RoleRepository;

@Service
public class RoleServiceImpl implements RoleService{
    @Autowired
    private RoleRepository roleRepository;

    @Transactional(rollbackFor=Exception.class)
    public Role findById(long id){
        return roleRepository.getOne(id);
    };
}
