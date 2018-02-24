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

            String[] eraseWhiteList = {"bash", "-c", "cat /dev/null > /etc/squid/configurations/" + l.getLabNo() + "/" + ip + "/whitelist"};
            String[] eraseBlackList = {"bash", "-c", "cat /dev/null > /etc/squid/configurations/" + l.getLabNo() + "/" + ip + "/blacklist"};

            process = Runtime.getRuntime().exec(eraseWhiteList);
            process.waitFor();

            process = Runtime.getRuntime().exec(eraseBlackList); // for Linux
            process.waitFor();

            for (AccessList acl : l.getAccessList()) {

                if (acl.getIp().equals(ip)) {

                    for (String wl : acl.getWhiteList()) {
                        String[] insertWhiteList = {"bash", "-c", "echo \"" + wl + "\" >> /etc/squid/configurations/" + l.getLabNo() + "/" + ip + "/whitelist"};
                        process = Runtime.getRuntime().exec(insertWhiteList);
                    }

                    for (String bl : acl.getBlackList()) {
                        String[] insertBlackList = {"bash", "-c", "echo \"" + bl + "\" >> /etc/squid/configurations/" + l.getLabNo() + "/" + ip + "/blacklist"};
                        process = Runtime.getRuntime().exec(insertBlackList);
                    }

                    break;
                }
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
            process = Runtime.getRuntime().exec("cat /etc/squid/squid.conf.default");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                configFile.add(line);
            }

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

    public void restartSquid() 
    {
        try {
            String[] cmd = {"/bin/bash", "-c", "echo momo960406| sudo -S systemctl restart squid"};
            process = Runtime.getRuntime().exec(cmd);
        }
        catch(Exception e)
        {
            
        }

    }

}
