/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import java.util.List;

/**
 *
 * @author trishen
 */
public class AccessList 
{
    private String ip;
    private List<String> whiteList;
    private List<String> blackList;
    private Boolean allowAccess;

    public AccessList(String ip, List<String> whiteList, List<String> blackList) {
        this.ip = ip;
        this.whiteList = whiteList;
        this.blackList = blackList;
    }
    
    

    public AccessList(String ip, List<String> whiteList, List<String> blackList, Boolean allowAccess) {
        this.ip = ip;
        this.whiteList = whiteList;
        this.blackList = blackList;
        this.allowAccess = allowAccess;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<String> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }

    public List<String> getBlackList() {
        return blackList;
    }

    public void setBlackList(List<String> blackList) {
        this.blackList = blackList;
    }

    public Boolean getAllowAccess() {
        return allowAccess;
    }

    public void setAllowAccess(Boolean allowAccess) {
        this.allowAccess = allowAccess;
    }
    
    
    
    
    
    
}
