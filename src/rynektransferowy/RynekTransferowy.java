/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rynektransferowy;

import gui.frmRynek;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author Staszek
 */
public class RynekTransferowy {

    /**
     * @param args the command line arguments
     */ 
        
    public static void main(String[] args) {
        // TODO code application logic here
         
        frmRynek cos=new frmRynek();
        cos.setVisible(true);
    }
    
}
