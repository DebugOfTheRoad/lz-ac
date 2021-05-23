package com.lckjsoft.gateway.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 一个逗比程序员
 * @Project: lz-gq
 * @Description:
 * @Date: Created in    2021/5/23 18:23
 * @Modified By:
 * @Modified Date:      2021/5/23
 */
public class JwtModel {
    private String userName;

    private List<String> roleIdList;

    public JwtModel(String name, ArrayList<String> _roleIdList) {
        userName = name;
        roleIdList = _roleIdList;
    }

    public JwtModel(){

    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getRoleIdList() {
        return roleIdList;
    }

    public void setRoleIdList(List<String> roleIdList) {
        this.roleIdList = roleIdList;
    }
}
