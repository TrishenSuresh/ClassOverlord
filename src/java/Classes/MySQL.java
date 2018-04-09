/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

/**
 *
 * @author trishen
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MySQL
{
    Connection connection = null;
    public MySQL() throws ClassNotFoundException, SQLException
    {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/classoverlord","root", "1234");
    }
   
    
    public void closeConnection() throws SQLException
    {
        connection.close();
    }
    
    public User login(String email, String password) throws SQLException
    {
        String query = "SELECT * FROM users WHERE emailAddress = ? ";
        PreparedStatement st = connection.prepareStatement(query);
        st.setString(1, email);
        
        ResultSet rs = st.executeQuery();
        
        if(rs.next())
        {
            String firstName = rs.getString("firstName");
            String lastName = rs.getString("lastName");
            Boolean isAdmin = rs.getBoolean("isAdmin");
            
            User user = new User(email, firstName, lastName, password, isAdmin);
            
            return user;
            
        }
        else
        {
            return null;
        }
       
        
    }
    
    public Lab getLab(String ipAddress) throws SQLException
    {
        String query = "SELECT * FROM labs where masterIp = ?";
        PreparedStatement st = connection.prepareStatement(query);
        st.setString(1, ipAddress);
        
        ResultSet rs = st.executeQuery();
        
        if(rs.next())
        {
            String labNo = rs.getString("labNo");
            String subnet = rs.getString("subnet");
            String masterIp = rs.getString("masterIp");
            boolean blocked = rs.getBoolean("isBlocked");
            
            Lab currentLab = new Lab(labNo, subnet, masterIp,blocked);
            
            return currentLab;
        }
        else
        {
            return null;
        }
    }
    
    public List<Lab> getAllLabs() throws SQLException
    {
        String query = "SELECT * FROM labs";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(query);
        
        List<Lab> allLabs = new ArrayList<Lab>();
        
        while(rs.next())
        {
            allLabs.add(new Lab(rs.getString("labNo"), rs.getString("subnet"), rs.getString("masterIp"),rs.getBoolean("isBlocked")));
        }
        
        return allLabs;
    }
    
    public String editLab(Lab newLab,Lab oldLab)
    {
        String result = "Succesfully Editted";
        
        try
        {
            String insert = "UPDATE labs SET labNo = ?, subnet = ?, masterIp = ? WHERE labNo = ?";
            PreparedStatement st = connection.prepareStatement(insert);
           
            st.setString(1, newLab.getLabNo());
            st.setString(2, newLab.getSubnet());
            st.setString(3, newLab.getMasterIp());
            st.setString(4, oldLab.getLabNo());
            
            st.executeUpdate();
            
            LinuxProcess proc = new LinuxProcess();
            proc.deleteLab(oldLab);
            proc.makeLab(newLab);
            proc.applyConfiguration();
            
            
            
        }
        catch(Exception e)
        {
            return e.getMessage();
        }
        
        return result;
        
    }
    
    public String deletelab(Lab selectedLab)
    {
        String result = "Succesfully Deleted";
        
        try
        {
            String delete = "DELETE FROM labs WHERE labNo= ?";
            PreparedStatement st = connection.prepareStatement(delete);
            st.setString(1, selectedLab.getLabNo());
            
            st.executeUpdate();
            
           LinuxProcess proc = new LinuxProcess();
           proc.deleteLab(selectedLab);

            
        }
        catch(Exception e)
        {
            return e.toString();
        }
        
        LinuxProcess proc = new LinuxProcess();
        proc.applyConfiguration();
        
        return result;

    }
    
    public String addLab(Lab newLab)
    {
        String result = "Succesfully Add";
        
        try
        {
            String insert = "INSERT INTO labs (labNo, subnet, masterIp, isBlocked) VALUES (?, ?, ?, ?)";
            PreparedStatement st = connection.prepareStatement(insert);
            
            st.setString(1, newLab.getLabNo());
            st.setString(2, newLab.getSubnet());
            st.setString(3, newLab.getMasterIp());
            st.setBoolean(4, false);
            
            st.executeUpdate();
            
            LinuxProcess proc = new LinuxProcess();
            proc.makeLab(newLab);
            proc.applyConfiguration();
 
            
        }
        catch(Exception e)
        {
            return e.getMessage();
        }
        
        return result;
    }
    
    public void toggleBlock(Lab l)
    {
        boolean isBlocked = false;
        
        try
        {
            if(!l.isBlocked())  
            {
                isBlocked = true;
            }
            
            String insert = "UPDATE labs SET isBlocked = ? WHERE labNo = ?";
            PreparedStatement st = connection.prepareStatement(insert);
            
            st.setBoolean(1, isBlocked);
            st.setString(2, l.getLabNo());
            
            st.executeUpdate();
            
        }
        catch(Exception e)
        {
         
        }
        
        
    }

    public String test()
    {
        try
        {
            String query = "select * from users";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            
            rs.next();
            
            return rs.getString(1);
        }
        catch(Exception e)
        {
            return e.toString();
        }
    }
}
