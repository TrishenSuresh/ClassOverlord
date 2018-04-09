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
public class Lab 
{
    private String labNo;
    private String subnet;
    private String masterIp;
    private List<AccessList> accessList;
    private boolean Blocked;

    public Lab(String labNo, String subnet, String masterIp, boolean blocked) {
        this.labNo = labNo;
        this.subnet = subnet;
        this.masterIp = masterIp;
        this.Blocked = blocked;
    }
    
    public void initializeAccessList(List<AccessList> accessList)
    {
        this.accessList = accessList;
    }

    public String getLabNo() {
        return labNo;
    }

    public void setLabNo(String labNo) {
        this.labNo = labNo;
    }

    public String getSubnet() {
        return subnet;
    }

    public void setSubnet(String subnet) {
        this.subnet = subnet;
    }

    public String getMasterIp() {
        return masterIp;
    }

    public void setMasterIp(String masterIp) {
        this.masterIp = masterIp;
    }
    
    public List<AccessList> getAccessList()
    {
        return accessList;
    }

    public boolean isBlocked() 
    {
        return Blocked;
    }

    public void setBlocked(boolean isBlocked) 
    {
        this.Blocked = isBlocked;
    }
    
    
    
    
    
}
