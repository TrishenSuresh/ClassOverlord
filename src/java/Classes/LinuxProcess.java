/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.net.util.SubnetUtils;

/**
 *
 * @author trishen
 */
public class LinuxProcess {

    private Process process = null;

    public void makeLab(Lab lab) {
        try {
            SubnetUtils utils = new SubnetUtils(lab.getSubnet());
            String[] allIps = utils.getInfo().getAllAddresses();

            for (String ip : allIps) {
                process = Runtime.getRuntime().exec("mkdir -p /etc/squid/configurations/" + lab.getLabNo() + "/" + ip);
                process = Runtime.getRuntime().exec("touch /etc/squid/configurations/" + lab.getLabNo() + "/" + ip + "/whitelist");
                process = Runtime.getRuntime().exec("touch /etc/squid/configurations/" + lab.getLabNo() + "/" + ip + "/blacklist");
            }

        } catch (Exception e) {
            out.println(e.toString());
        }

    }

    public String deleteLab(Lab lab) {
        try {
            process = Runtime.getRuntime().exec("rm -rf /etc/squid/configurations/" + lab.getLabNo() + "/");
        } catch (Exception e) {
            return e.toString();
        }

        return "";
    }

    public Lab getLabAccessList(Lab lab) {
        SubnetUtils utils = new SubnetUtils(lab.getSubnet());
        String[] allIps = utils.getInfo().getAllAddresses();
        List<AccessList> accessList = new ArrayList<AccessList>();

        for (String ip : allIps) {

            List<String> whiteList = new ArrayList<String>();
            List<String> blackList = new ArrayList<String>();

            try {
                //WhiteList
                process = Runtime.getRuntime().exec("cat /etc/squid/configurations/" + lab.getLabNo() + "/" + ip + "/whitelist"); // for Linux
                //Process process = Runtime.getRuntime().exec("cmd /c dir"); //for Windows

                process.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    whiteList.add(line);
                }

                //BlackList
                process = Runtime.getRuntime().exec("cat /etc/squid/configurations/" + lab.getLabNo() + "/" + ip + "/blacklist"); // for Linux
                //Process process = Runtime.getRuntime().exec("cmd /c dir"); //for Windows

                process.waitFor();
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                while ((line = reader.readLine()) != null) {
                    blackList.add(line);
                }

                accessList.add(new AccessList(ip, whiteList, blackList));

            } catch (Exception e) {
                System.out.println(e);
            }
        }

        lab.initializeAccessList(accessList);

        return lab;
    }

    public String applyAccessList(Lab l, String ip) {
        String result = "Access List Saved";

        try {

            
            process = Runtime.getRuntime().exec("cat /dev/null > /etc/squid/configurations/"+l.getLabNo()+"/"+ip+"/whitelist "); // for Linux
            process = Runtime.getRuntime().exec("echo \"test2\" > /etc/squid/configurations/"+l.getLabNo()+"/"+ip+"/whitelist "); // for Linux
            process.waitFor();
            process = Runtime.getRuntime().exec("cat /dev/null > /etc/squid/configurations/" + l.getLabNo() + "/" + ip + "/blacklist"); // for Linux
            process.waitFor();
            for (AccessList acl : l.getAccessList()) {
                if (acl.getIp().equals(ip)) {

                    for (String wl : acl.getWhiteList()) 
                    {
                        process = Runtime.getRuntime().exec("echo \""+wl+"\" >> /etc/squid/configurations/" + l.getLabNo() + "/" + ip + "/whitelist"); // for Linux
                    }
                    
                    for(String bl : acl.getBlackList())
                    {
                        process = Runtime.getRuntime().exec("echo \""+bl+"\" >> /etc/squid/configurations/" + l.getLabNo() + "/" + ip + "/blacklist"); // for Linux
                    }

                    break;
                }
            }

        } catch (Exception e) {
            return e.getMessage();
        }

        return result;
    }
    


}
