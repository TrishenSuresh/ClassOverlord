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
import java.util.stream.Collectors;
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
        List<String> denyFile = readDenyFile();

        for (String ip : allIps) {

            List<String> whiteList = new ArrayList<String>();
            List<String> blackList = new ArrayList<String>();
            boolean isAllowed = true;

            try {
                //WhiteList
                process = Runtime.getRuntime().exec("cat /etc/squid/configurations/" + lab.getLabNo() + "/" + ip + "/whitelist"); // for Linux

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

                //Deny File
                for (String s : denyFile) {
                    if (s.equals(ip)) {
                        isAllowed = false;
                        break;
                    }
                }

                accessList.add(new AccessList(ip, whiteList, blackList, isAllowed));

            } catch (Exception e) {
                System.out.println(e);
            }
        }

        lab.initializeAccessList(accessList);

        return lab;
    }

    public String applyAccessList(Lab l, String ip) {
        String result = "Access List Saved";
        AccessList AccessL = null;

        try {

            String[] eraseWhiteList = {"bash", "-c", "cat /dev/null > /etc/squid/configurations/" + l.getLabNo() + "/" + ip + "/whitelist"};
            String[] eraseBlackList = {"bash", "-c", "cat /dev/null > /etc/squid/configurations/" + l.getLabNo() + "/" + ip + "/blacklist"};

            process = Runtime.getRuntime().exec(eraseWhiteList);
            process.waitFor();

            process = Runtime.getRuntime().exec(eraseBlackList); // for Linux
            process.waitFor();
            
            for(AccessList al : l.getAccessList())
            {
                if(al.getIp().equals(ip))
                {
                    AccessL = al;
                    break;
                }
            }

                if (AccessL.getIp().equals(ip)) 
                {

                    for (String wl : AccessL.getWhiteList()) 
                    {
                        String[] insertWhiteList = {"bash", "-c", "echo \"" + wl + "\" >> /etc/squid/configurations/" + l.getLabNo() + "/" + ip + "/whitelist"};
                        process = Runtime.getRuntime().exec(insertWhiteList);
                    }

                    for (String bl : AccessL.getBlackList()) 
                    {
                        String[] insertBlackList = {"bash", "-c", "echo \"" + bl + "\" >> /etc/squid/configurations/" + l.getLabNo() + "/" + ip + "/blacklist"};
                        process = Runtime.getRuntime().exec(insertBlackList);
                    }
                    
                    List<String> denyFile = readDenyFile();
                    
                    if(AccessL.isIsAllowed())
                    {
                        for (int i = 0; i < denyFile.size(); i++) 
                        {
                            if (denyFile.get(i).equals(AccessL.getIp())) 
                            {
                                denyFile.remove(i);
                            }
                        }
                    }
                    
                    Boolean found = false;
                    
                    if(!AccessL.isIsAllowed())
                    {
                        if(denyFile.size() > 0)
                        {
                            for (int i = 0; i < denyFile.size(); i++) 
                            {
                                if (denyFile.get(i).equals(AccessL.getIp())) 
                                {
                                    found = true;
                                    break;
                                }
                            }
                            
                            if(!found)
                            {        
                                denyFile.add(AccessL.getIp());
                            }
                        }
                        else
                        {
                            denyFile.add(AccessL.getIp());
                        }
                        
                    }
                    
                    writeDenyFile(denyFile);
                }
            

        } catch (Exception e) {
            return e.getMessage();
        }

        return result;
    }

    public List<String> applyConfiguration() {

        try {
            MySQL sql = new MySQL();

            List<Lab> allLabs = sql.getAllLabs();

            List<String> configFile = new ArrayList<String>();

            String newline = System.getProperty("line.separator");

            int sourceStartLine = 0;
            int blockingStartLine = 0;

            //read config file
            configFile = readDefaultConfigFile();

            //Locate the starting source point
            for (int counter = 0; counter < configFile.size(); counter++) //Source Location
            {
                String s = configFile.get(counter);

                if (s.contains("# Example rule")) {
                    sourceStartLine = counter;
                    break;
                }
            }

            //Get labs 
            for (Lab l : allLabs) {
                l = getLabAccessList(l);
            }
            
            
            configFile.add(1,"acl blockAccess src \\\"/etc/squid/configurations/deny_access\\\"");

            //source
            for (Lab l : allLabs) {

                List<AccessList> accessList = l.getAccessList();

                for (AccessList acl : accessList) {
                    String replacedIP = acl.getIp().replace('.', '_');

                    //Source
                    for (int sourcecounter = sourceStartLine; sourcecounter < configFile.size(); sourcecounter++) {
                        if (configFile.get(sourcecounter).length() == 0) {
                            configFile.add(sourcecounter, "acl " + replacedIP + " src " + acl.getIp());
                            configFile.add(sourcecounter + 1, "acl " + replacedIP + "_whitelist dstdomain \\\"/etc/squid/configurations/" + l.getLabNo() + "/" + acl.getIp() + "/whitelist\\\"");
                            configFile.add(sourcecounter + 2, "acl " + replacedIP + "_blacklist dstdomain \\\"/etc/squid/configurations/" + l.getLabNo() + "/" + acl.getIp() + "/blacklist\\\"");
                            break;
                        }
                    }

                }

            }

            //Locate the starting block point
            for (int counter = 0; counter < configFile.size(); counter++) //Source Location
            {
                String s = configFile.get(counter);

                if (s.contains("INSERT YOUR OWN RULE(S)")) {
                    blockingStartLine = counter;
                    break;
                }
            }

            for (Lab l : allLabs) {

                List<AccessList> accessList = l.getAccessList();

                for (AccessList acl : accessList) {
                    String replacedIP = acl.getIp().replace('.', '_');

                    //Block
                    for (int blockcounter = blockingStartLine; blockcounter < configFile.size(); blockcounter++) {
                        if (configFile.get(blockcounter).length() == 0) {
                            configFile.add(blockcounter, "http_access allow " + replacedIP + " " + replacedIP + "_whitelist");
                            configFile.add(blockcounter + 1, "http_access deny " + replacedIP + " " + replacedIP + "_blacklist");
                            break;
                        }
                    }

                }

            }

            String[] removeConfiguration = {"bash", "-c", "cat /dev/null > /etc/squid/squid.conf"};
            process = Runtime.getRuntime().exec(removeConfiguration);

            for (String s : configFile) {
                String[] addConfiguration = {"bash", "-c", "echo \"" + s + "\" >> /etc/squid/squid.conf"};
                process = Runtime.getRuntime().exec(addConfiguration);
                process.waitFor();
            }
            sql.closeConnection();
            return configFile;

        } catch (Exception e) {
            //return e.getMessage();
        }

        return null;
    }

    public void restartSquid() {
        try {
            String[] cmd = {"bash", "-c", "echo momo960406| sudo -S systemctl restart squid"};
            process = Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {

        }

    }

    public List<String> readConfigFile() {
        List<String> configFile = new ArrayList<String>();

        try {
            process = Runtime.getRuntime().exec("cat /etc/squid/squid.conf");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                configFile.add(line);
            }
        } catch (Exception e) {

        }

        return configFile;
    }

    public List<String> readDefaultConfigFile() {
        List<String> configFile = new ArrayList<String>();

        try {
            process = Runtime.getRuntime().exec("cat /etc/squid/squid.conf.default");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                configFile.add(line);
            }
        } catch (Exception e) {

        }

        return configFile;
    }

    public List<String> readDenyFile() {
        List<String> denyFile = new ArrayList<String>();

        try {
            process = Runtime.getRuntime().exec("cat /etc/squid/configurations/deny_access");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                denyFile.add(line);
            }
        } catch (Exception e) {

        }

        return denyFile;
    }
    
    public void writeDenyFile(List<String> denyFile)
    {
        try
        {
            
            denyFile = denyFile.stream().distinct().collect(Collectors.toList());
            
            String[] eraseDenyFile = {"bash", "-c", "cat /dev/null > /etc/squid/configurations/deny_access"};
            process = Runtime.getRuntime().exec(eraseDenyFile);
            
            for (String s : denyFile) {
                String[] insertDenyList = {"bash", "-c", "echo \"" + s + "\" >> /etc/squid/configurations/deny_access"};
                process = Runtime.getRuntime().exec(insertDenyList);
                process.waitFor();
            }
        }
        catch(Exception e)
        {
            
        }
        
    }
    
    public void removeBlock(Lab l)
    {
        SubnetUtils utils = new SubnetUtils(l.getSubnet());
        String[] allIps = utils.getInfo().getAllAddresses();
        
        List<String> denyFile = readDenyFile();
        
        for(String s: allIps)
        {
            denyFile.remove(s);
        }
        
        writeDenyFile(denyFile);
    }
    
    public void addBlock(Lab l)
    {
        SubnetUtils utils = new SubnetUtils(l.getSubnet());
        String[] allIps = utils.getInfo().getAllAddresses();
        
        List<String> denyFile = readDenyFile();
        
        for(String s: allIps)
        {
            denyFile.add(s);
        }
        
        writeDenyFile(denyFile);
    }
    

}
