/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;


import java.awt.event.FocusListener;
import java.awt.Color;
import java.awt.event.FocusEvent;
import static java.lang.System.identityHashCode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.eclipse.persistence.queries.StoredProcedureCall;
import rynektransferowy.Dokonanytrans;
import rynektransferowy.RynekTransferowy;
import rynektransferowy.Zawodnik;
import rynektransferowy.Klub;
import rynektransferowy.Kraj;
import rynektransferowy.Listatrans;

/**
 *
 * @author Staszek
 */
public class frmRynek extends javax.swing.JFrame {

    /**
     * Creates new form frmRynek
     */
    public int gdziejestem;
    public Java2sAutoTextField[] field = new Java2sAutoTextField[4];
    public Java2sAutoComboBox combo;
    private ArrayList<String>params=new ArrayList<>();
    //listy podpowiadajace tekst
    private List<List<String>> txt = new ArrayList<List<String>>();// -> 0 zawodnicy ; 1 kluby ; 2 kraje
    private static Map<String,Klub> dictK = new HashMap<>();
    private static Map<String,Zawodnik> dictZ = new HashMap<>();
    private static Map<String,Kraj> dictKr = new HashMap<>();
    private static Map<String,Listatrans> dictLt = new HashMap<>();
    private static List<Map>dict = new ArrayList<>();// -> 0 zawodnicy ; 1 kluby ; 2 kraje ; 3 listatrans ; 4 dokonanytrans


//pomocnicze listy 
    private ArrayList<Integer>ktore = new ArrayList<Integer>();
    private DefaultTableModel model = new DefaultTableModel(){
    @Override
    public boolean isCellEditable(int row, int column) {
       //all cells false
       return false;
    }};
    private ArrayList<javax.swing.JTextField>listText = new ArrayList<>();
    //entity manager
    public static EntityManagerFactory emf=null;
    public static EntityManager em=null;
    //wbudowane funkcje i procedury
    public static Query funkc;
    public static StoredProcedureQuery stored;
    //mode of 154 page
    public int mode154;
    public ButtonGroup bg;
    public Short idT;//idTransakcji rekordu z tabeli
    //focus listener for field[i]
    FocusListener lisField;
    public frmRynek() {
        connectToDB();
        initComponents();
        mojInit();
    }
    
    private void initFocus(){
        lisField=new FocusListener(){
          @Override
            public void focusLost(FocusEvent e) {
                //wpisanie do jLabel7 klubu zawodnika
                if(gdziejestem==113 || gdziejestem==14){
                    String gracz="",klub="";
                    JTextField src = (JTextField)e.getSource();
                    try{
                        //pobierz nazwisko z pola
                        gracz=src.getText().toUpperCase();
                        //znajdz rekord dla gracza w hashmapie i zwroc klub
                        Zawodnik z=(Zawodnik)dict.get(0).get(gracz);
                        klub=z.getIdKlubu().getNazwa();
                    }
                    catch(Exception ex){
                        klub="";
                    }
                    //ustaw jLabel7
                    jLabel7.setText(klub);
                }
            }
            @Override
            public void focusGained(FocusEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }
    
    private void setPageMode()
    {
        if (jRadioButton1.isSelected())mode154=1;
        else mode154=0;
    }
    
    private void groupButton(){
        bg=new ButtonGroup();
        bg.add(jRadioButton1);
        bg.add(jRadioButton2);
    }
    
    private void initQueries()
    {
        stored=em.createStoredProcedureQuery("transferZaw");
        //set parameters of stored
        stored.registerStoredProcedureParameter("nazwisko1",String.class, ParameterMode.IN);
        stored.registerStoredProcedureParameter("kwota1", Integer.class, ParameterMode.IN);
        stored.registerStoredProcedureParameter("skad1", Integer.class, ParameterMode.IN);
        stored.registerStoredProcedureParameter("dokad1", Integer.class, ParameterMode.IN);
        //set parameters of funkc
        funkc=em.createNativeQuery("SELECT NVL(czyMoge(?),0) from sys.dual");
    }
    
    
    private void textSuccess(javax.swing.JLabel ob)//green color
    {
        ob.setForeground(new java.awt.Color(51, 204, 0));
        ob.setText("Success!");
    }
    
    private void textFailure(javax.swing.JLabel ob)//red color
    {
        ob.setForeground(new java.awt.Color(255,51,51));
        ob.setText("Failure!");
    }
    
    
    private void addModelColumns(DefaultTableModel mod,String[] nazwy)
    {
        for(String ob:nazwy)mod.addColumn(ob);
    }
    
    private void setModelColumnsLength(javax.swing.JTable table,int[] dlugosc)
    {
        for(int i=0;i<dlugosc.length;i++)table.getColumnModel().getColumn(i).setPreferredWidth(dlugosc[i]);
    }
    
    private Query setQuery(String[] array,String op)
    {
        Query q=null;
        switch(op)
            {
                case "<=":
                    System.out.println("0");
                    q=em.createNamedQuery(array[0]);
                    break;
                case "=":
                    System.out.println("1");
                    q=em.createNamedQuery(array[1]);
                    break;
                case ">=":
                    System.out.println("2");
                    q=em.createNamedQuery(array[2]);
                    break;
            }
        return q;
    }
    
    private void setQueryParams(Query q,int ile)
    {
        //setting parameters from List params
        for(int i=0;i<ile;i++)
        {
            if(params.get(i)=="")
                q.setParameter(i+1,null );
            else q.setParameter(i+1,params.get(i).toUpperCase()); /// na razie wszystkie sa stringami
        }
        //4-ty parametr to integer (ale nie zawsze)
        try{
            if(params.get(3).length()==0)q.setParameter(4,null );
            else q.setParameter(4,Integer.parseInt(params.get(3)));
        }
        catch(Exception ex)
        {
            System.out.println("nie moge sparsować");
        }
    }
    
    private void loadTxtFields()//do editu, bo nie trzeba za kazdym razem siegac do bazy danych
    {
        for(int i=0;i<4;i++)
        {
             field[i].setText("");
             listText.get(i).setText("");
        }
        switch(gdziejestem)
        {
            case 111:
              // kluby i kraje
                field[1].setDataList(txt.get(1));
                field[2].setDataList(txt.get(2));
                break;
            case 112:
                //zawodnicy
                field[0].setDataList(txt.get(0));
                break;
            case 113:
                //zawodnicy
                field[0].setDataList(txt.get(0));
                break;
            case 121:
                //kraje
                field[1].setDataList(txt.get(2));
                break;
            case 122:
                //kluby i kraje
                field[0].setDataList(txt.get(1));
                field[2].setDataList(txt.get(2));
                break;
            case 123:
                //kluby
                field[0].setDataList(txt.get(1));
                break;
            //case 124:
              //  break;
            case 125:
                //kraje
                field[0].setDataList(txt.get(2));
                break;    
            case 131:
                //zawodnik
                field[0].setDataList(txt.get(0));
                break;
            case 132:
                //zawodnik
                field[0].setDataList(txt.get(0));
                break;
            case 133:
                //zawodnik
                field[0].setDataList(txt.get(0));
                break;
            case 14:
                //zawodnik i klub
                field[0].setDataList(txt.get(0));
                field[2].setDataList(txt.get(1));
                break;
            case 151:
                //zawodnik, klub i kraj
                for(int i=0;i<3;i++)
                {
                    field[i].setDataList(txt.get(i));
                }
                break;
            case 152:
              //  klub i kraj
                for(int i=0;i<2;i++)
                {
                    field[i].setDataList(txt.get(i+1));
                }
                break;
            case 153:
              //  nazwisko i klub
                field[0].setDataList(txt.get(0));
                field[1].setDataList(txt.get(1));
                break;
            case 154:
             //    nazwisko i klub x2
                field[0].setDataList(txt.get(0));
                field[1].setDataList(txt.get(1));
                field[2].setDataList(txt.get(1));
                break;
        }
    }
    
    private void readDataFromTxtFields()
    {
        int i;
        //jesli jakies smieci zostaly
        if(!params.isEmpty())params.clear();
        //iteruje po polach tekstowych
        for(i=0;i<4;i++)//maks 4 pola tekstowe
        {
            if(field[i].isVisible())params.add(field[i].getText().toUpperCase());
            else if (listText.get(i).isVisible())params.add(listText.get(i).getText().toUpperCase());
        }
    }
    
    private <T>void addToHashData(T ob)//y -> okresla jakiego typu jest dodawany obiekt
    {
        if (ob instanceof Zawodnik)
        {
            if(dict.get(0).isEmpty())
                dict.get(0).put(((Zawodnik) ob).getNazwisko(), (Zawodnik)ob);
            else if(!dict.get(0).containsKey(((Zawodnik) ob).getNazwisko()))
                dict.get(0).put(((Zawodnik) ob).getNazwisko(), (Zawodnik)ob);
        }
        else if (ob instanceof Klub)
        {
            if(dict.get(1).isEmpty())
                dict.get(1).put(((Klub) ob).getNazwa(), (Klub)ob);
            else if(!dict.get(0).containsKey(((Klub) ob).getNazwa()))
                dict.get(1).put(((Klub) ob).getNazwa(), (Klub)ob);
        }
        else if (ob instanceof Kraj)
        {
            if(dict.get(2).isEmpty())
                dict.get(2).put(((Kraj) ob).getNazwa(), (Kraj)ob);
            else if(!dict.get(0).containsKey(((Kraj) ob).getNazwa()))
                dict.get(2).put(((Kraj) ob).getNazwa(), (Kraj)ob);
        }
    }
    
    private void removeFromHashData(int y,String param)//usuwa według klucza 
    {
        if(y>=0 && y<=2)
        {
            if(!dict.get(y).isEmpty())
            {
                dict.get(y).remove(param.toUpperCase());
            }
        } 
    }
    
    private Object getLastAdded(int y,String param)
    {
        Query q;
        if (y==0){   
            q=em.createNamedQuery("Zawodnik.findByNazwisko");
            q.setParameter(1, param);
            Zawodnik z=(Zawodnik)q.getSingleResult();
            return z;
        }
        else if (y== 1){   
            q=em.createNamedQuery("Klub.findByNazwa");
            q.setParameter(1, param);
            Klub k=(Klub)q.getSingleResult();
            return k;
        }
        else if (y==2){
            q=em.createNamedQuery("Kraj.findByNazwa");
            q.setParameter(1, param);
            Kraj kr=(Kraj)q.getSingleResult();
            return kr;
        }
        return null;
    }
    
    private static void loadDataHash(int y,Query qu)
    {
        //mam zaladowane do listy        
        //czas zaladowac do slownika
        if(y ==0)
        {
            List<Zawodnik>x=qu.getResultList();
            //wypelnij slownik tym co w liscie
            for(Zawodnik z : x)
            {
                if(dict.get(y).isEmpty())dict.get(y).put(z.getNazwisko(), z);
                else if(!dict.get(y).containsKey(z.getNazwisko()))
                {
                    dict.get(y).put(z.getNazwisko(), z);
                }
            }
        }
        else if (y == 1)
        {
            List<Klub>x=qu.getResultList();
            //wypelnij slownik tym co w liscie
            for(Klub z : x)
            {
                if(dict.get(y).isEmpty())dict.get(y).put(z.getNazwa(), z);
                else if(!dict.get(y).containsKey(z.getNazwa()))
                {
                    dict.get(y).put(z.getNazwa(), z);
                }
            }
        }
        else if (y ==2)
        {
            List<Kraj>x=qu.getResultList();
            //wypelnij slownik tym co w liscie
            for(Kraj z : x)
            {
                if(dict.get(y).isEmpty())dict.get(y).put(z.getNazwa(), z);
                else if(!dict.get(y).containsKey(z.getNazwa()))
                {
                     dict.get(y).put(z.getNazwa(), z);
                }
            }
        }
        else if (y ==3)
        {
            List<Listatrans>x=qu.getResultList();
            //wypelnij slownik tym co w liscie
            for(Listatrans z : x)
            {
                if(dict.get(y).isEmpty())dict.get(y).put(z.getNazwisko(), z);
                else if(!dict.get(y).containsKey(z.getNazwisko()))
                {
                    dict.get(y).put(z.getNazwisko(), z);
                }
            }
        }
     /*   else if (y instanceof Dokonanytrans)
        {
            List<Dokonanytrans>x=qu.getResultList();
            //wypelnij slownik tym co w liscie
            for(Dokonanytrans z : x)
            {
                dict.get(y).put(z.getNazwisko(), z);              
            }
        }*/
    }
    
    private void connectToDB()
    {
        if(emf==null)
        {
             try{
                 emf=Persistence.createEntityManagerFactory("RynekTransferowyPU");
             }
             catch(Exception ex){
                 throw ex;
             }
        }
        //EntityManager 
        if(em==null)
        {
            try{
                 em = emf.createEntityManager();
             }
             catch(Exception ex){
                 throw ex;
             }
        }
        if(em!=null && emf!=null)System.out.println("Połączyłeś się!");
    }
    
    public static void disconnectFromDB()
    {
        try{
            em.close();
            emf.close();
            System.out.println("You have disconnected from the database");
        }
        catch(Exception ex)
        {
            throw ex;
        }
    }
    
    private void printHash(int x)
    {
        Set<String>keys=null;
        if(!dict.get(x).isEmpty())keys = dict.get(x).keySet();
        for(String key: keys)
            {//przechodze po wszystkich kluczach
                System.out.println(x+" "+key);
            }
    }
    
    private void initTxtList(){
        //funkcja ladujaca do list pomocniczych stringi
        int i;
        for(i=0;i<3;i++)
        {
            List<String>inner=new ArrayList();
            inner.add("");
            txt.add(inner);
        }
    }
    
    //ta funkcja jest potrzebna, ponieważ relacje mają włączone opcje delete on cascade
    private int removeManyFromHashData(int ktora,int kryt,String param) //ktora-> 0,1, zawodnicy,klub //kryt 1,R\{1} klub,kraj //param nazwa
    {
        int wyn=0;//ile usunieto
        Iterator<String> it=null;
        switch(ktora)
        {
            case 0://usuwam z hashmapy zawodnicy
                //przejdz po wszystkich obiektach w hashmapie
                it = dict.get(0).keySet().iterator(); //entrySet lub keySet
                if(it!=null){
                while (it.hasNext())
                {
                    //rzutuje obiekt 
                    String key=it.next();
                    Zawodnik z =(Zawodnik)dict.get(0).get(key);
                    if(kryt==1)//klub
                    {
                        if(z.getIdKlubu().getNazwa().equals(param)){it.remove();wyn++;
                    System.out.println(z.getNazwisko()+" "+z.getIdKlubu().getNazwa());}
                    }
                    else if(kryt==2)//narodowość
                    {
                        if(z.getIdNarod().getNazwa().equals(param)){it.remove();wyn++;
                        System.out.println(z.getNazwisko()+" "+z.getIdKlubu().getNazwa());}
                    }
                    else if(kryt==3) //usun graczy, ktorzy graja w kraju ktory jest usuwany
                    {
                        if(z.getIdKlubu().getIdPanstwa().getNazwa().equals(param)){it.remove();wyn++;
                        System.out.println(z.getNazwisko()+" "+z.getIdKlubu().getNazwa());}
                    }
                }
                }
                break;
            case 1://usuwam z hashmapy klub
                it = dict.get(1).keySet().iterator(); //entrySet lub keySet
                if(it!=null){
                while (it.hasNext())
                {
                    //rzutuje obiekt 
                    //rzutuje obiekt 
                    String key=it.next();
                    Klub z =(Klub)dict.get(1).get(key);
                    //usuwam klub z danego kraju
                    if(z.getIdPanstwa().getNazwa().equals(param)){it.remove();wyn++;
                    System.out.println(z.getNazwa()+" "+z.getIdPanstwa().getNazwa());}
                }      
                }
                break;
            default:break;
        }
        return wyn;
    }
    
    private void updateTxtList(int x) // x -> ktoralista : 0 zawodnicy ; 1 kluby ; 2 kraje
    {
        int i;
        Set<String>keys=null;
        if(!dict.get(x).isEmpty())keys = dict.get(x).keySet();
        if(x>=0 && x <=2 && keys!=null){
            for(String key: keys)
            {//przechodze po wszystkich kluczach
                if(!txt.get(x).contains(key))
                {//jesli nie mam w pomocniczej liscie , to dodaje
                    txt.get(x).add(key);
                }   
            }
            Iterator iter=txt.get(x).listIterator();
           // for(String str:txt.get(x))
            while(iter.hasNext())
            {//jesli w liscie mam cos czego juz nie ma w hashtable
                String str=(String)iter.next();
                if(null==dict.get(x).get(str) && str!="")
                {
                    iter.remove();
                }
            }
        }
    }
    
    private void mojInit()
    {
        int i;
        initTxtList();
        
        //inicjalizuje 
        dictK = new HashMap<String,Klub>();
        dictZ = new HashMap<String,Zawodnik>();
        dictKr = new HashMap<String,Kraj>();
        dictLt = new HashMap<String,Listatrans>();
        
        dict.add(dictZ);
        dict.add(dictK);
        dict.add(dictKr);
        dict.add(dictLt);
       
        //pobierz rekordy i wpisz dane do hashmap
        Query q0,q1,q2;
        q0=em.createNamedQuery("Zawodnik.findAll");
        loadDataHash(0,q0); 
        q1=em.createNamedQuery("Klub.findAll");
        loadDataHash(1,q1);
        q2=em.createNamedQuery("Kraj.findAll");
        loadDataHash(2,q2);
        updateTxtList(0);
        updateTxtList(1);
        updateTxtList(2);
        
        
        System.out.println(dict.get(0).size()+" "+dict.get(1).size()+" "+dict.get(2).size());
        for(i=0;i<4;i++)
        {
            //na poczatku puste
        field[i]=new Java2sAutoTextField(txt.get(0));//,combo);
        getContentPane().add(field[i], new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 60+i*40, 100, -1));
        field[i].setVisible(false); 
        }
        //dodaj moj listener dla pola zerowego
        initFocus();
        field[0].addFocusLis(lisField);
        listText.add(jTextField1);listText.add(jTextField2);listText.add(jTextField3);listText.add(jTextField4);
        //ustawienia jTable
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        //deklaruje procedure i funkcje
        initQueries();
        //ustawiam tryb strony 154
        mode154=1;
        //ustawiam grupe radio button
        groupButton();
        //ustawiam idT
        idT=-1;
        //ustawiam na poczatek gdziejestem
        gdziejestem=1;
    }
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jButton7.setVisible(false);
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Rynek Transferowy");
        setAutoRequestFocus(false);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(800, 400));
        setSize(new java.awt.Dimension(800, 400));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setText("Search Information");
        jButton1.setPreferredSize(new java.awt.Dimension(140, 30));
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(309, 304, 160, -1));
        jButton1.getAccessibleContext().setAccessibleName("");

        jButton2.setText("Make deal");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton2.setPreferredSize(new java.awt.Dimension(140, 30));
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(309, 237, 160, -1));
        jButton2.getAccessibleContext().setAccessibleName("");

        jButton3.setText("Modify transfer list");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton3.setPreferredSize(new java.awt.Dimension(140, 30));
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(309, 165, 160, -1));
        jButton3.getAccessibleContext().setAccessibleName("");

        jButton4.setText("Modify club/country");
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton4.setPreferredSize(new java.awt.Dimension(140, 30));
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton4MouseClicked(evt);
            }
        });
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(309, 92, 160, -1));
        jButton4.getAccessibleContext().setAccessibleName("");

        jButton5.setText("Modify player");
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton5.setPreferredSize(new java.awt.Dimension(140, 30));
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton5MouseClicked(evt);
            }
        });
        getContentPane().add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(309, 26, 160, -1));
        jButton5.getAccessibleContext().setAccessibleName("");

        jButton6.setText("Quit");
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setMaximumSize(new java.awt.Dimension(53, 30));
        jButton6.setPreferredSize(new java.awt.Dimension(50, 30));
        jButton6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton6MouseClicked(evt);
            }
        });
        getContentPane().add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(665, 304, 72, 30));

        jLabel1.setText("nazwisko:");
        jLabel1.setVisible(false);
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, 80, 20));

        jLabel2.setText("klub:");
        jLabel2.setVisible(false);
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 80, 20));

        jLabel3.setText("kraj:");
        jLabel3.setVisible(false);
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 140, 80, 20));

        jLabel4.setText("wartość:");
        jLabel4.setVisible(false);
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 180, 80, 20));

        jButton7.setText("Confirm");
        jButton7.setMaximumSize(new java.awt.Dimension(73, 30));
        jButton7.setPreferredSize(new java.awt.Dimension(73, 30));
        jButton7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton7MouseClicked(evt);
            }
        });
        getContentPane().add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 304, 90, 30));

        jScrollPane1.setVisible(false);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTable1.setCellSelectionEnabled(true);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 20, 500, 270));

        jTextField1.setBackground(new java.awt.Color(239, 219, 145));
        jTextField1.setVisible(false);
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 60, 100, 20));

        jTextField2.setBackground(new java.awt.Color(239, 219, 145));
        jTextField2.setVisible(false);
        getContentPane().add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, 100, 20));

        jTextField3.setBackground(new java.awt.Color(239, 219, 145));
        jTextField3.setVisible(false);
        getContentPane().add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 140, 100, 20));

        jTextField4.setBackground(new java.awt.Color(239, 219, 145));
        jTextField4.setVisible(false);
        getContentPane().add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 180, 100, 20));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<=", "=", ">=" }));
        jComboBox1.setVisible(false);
        getContentPane().add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 220, -1, -1));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 204, 0));

        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 290, 70, 17));

        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Find");
        jRadioButton1.setVisible(false);
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jRadioButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 310, -1, -1));

        jRadioButton2.setText("Delete");
        jRadioButton2.setVisible(false);
        getContentPane().add(jRadioButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 310, -1, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 190, 40));

        jLabel7.setVisible(false);
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 100, 130, 20));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton5MouseClicked
        //przycisk pierwszy od góry
        switch(gdziejestem)
        {
            case 1:
                gdziejestem=11;
                loadScene11();
                jLabel6.setText("Modify player");
                System.out.println(gdziejestem);
                break;
            case 11:
                gdziejestem=111;
                jLabel6.setText("Add player");
                loadTxtFields();
                loadScene11X();
                jButton7.setVisible(true);
                //dodaje i ustawiam teksty
                jLabel2.setText("klub:");
                jLabel3.setText("kraj:");
                jLabel4.setText("wartosc:");
                jLabel3.setVisible(true);
                jLabel4.setVisible(true);
                //pokazuje text boxy ze wsparciem
                ktore.clear();ktore.add(1);ktore.add(2);
                showTextBoxes(ktore);
                //pokazuje zwykle text fields
                ktore.set(0,0);ktore.set(1,3);
                showTextFields(ktore);
                System.out.println(gdziejestem);
                break;         
            case 12:
                loadScene15X();//4 przyciski znikają
                jScrollPane1.setVisible(false);
                jButton1.setVisible(false);
               // jButton7.setVisible(true);
                jButton6.setText("Return");
                // strona 1.2
                jLabel1.setText("nazwa klubu:");
                jLabel2.setText("państwo:");
                jLabel1.setVisible(true);
                jLabel2.setVisible(true);
                ktore.clear();ktore.add(1);
                showTextBoxes(ktore);ktore.set(0,0);
                showTextFields(ktore);
                gdziejestem=121;
                jLabel6.setText("Add club");
                loadTxtFields();
                System.out.println(gdziejestem);
                break;
            case 13:
                loadScene15X();//4 przyciski znikają
                jScrollPane1.setVisible(false);
                jButton1.setVisible(false);
              //  jButton7.setVisible(true);
                //jButton6.setText("Return");
                //strona 1.3
                jLabel1.setText("nazwisko:");jLabel2.setText("typ oferty:");
                jLabel3.setText("cena:");
                jLabel1.setVisible(true);jLabel2.setVisible(true);jLabel3.setVisible(true);
                ktore.clear();ktore.add(0);
                showTextBoxes(ktore);ktore.set(0,1);ktore.add(2);
                showTextFields(ktore);
                gdziejestem=131;
                jLabel6.setText("Place in list");
                loadTxtFields();
                System.out.println(gdziejestem);
                break;
            case 15:
                gdziejestem=151;
                jLabel6.setText("Find player");
                loadTxtFields();
                loadScene15X();
                //strona 1.5.1
                jComboBox1.setVisible(true);
                jLabel1.setText("nazwisko:");jLabel2.setText("klub:");jLabel3.setText("kraj:");jLabel4.setText("wartosc:");
                jLabel1.setVisible(true);jLabel2.setVisible(true);jLabel3.setVisible(true);jLabel4.setVisible(true);
                ktore.clear();ktore.add(1);ktore.add(2);ktore.add(0);
                showTextBoxes(ktore);ktore.clear();ktore.add(3);
                showTextFields(ktore);
                System.out.println(gdziejestem);
                break;      
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5MouseClicked

    private void jButton6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton6MouseClicked
        //przycisk powrotu bądź zamkniecia programu
        jButton7.setVisible(false);
        jComboBox1.setVisible(false);
        jLabel5.setText("");    //text oznaczajacy sukces badz porazke confirma
        model.setRowCount(0);
        model.setColumnCount(0);
        // label oznaczajacy z jakiego klubu jest gracz
        if(gdziejestem==14 || gdziejestem==113)jLabel7.setVisible(false);
        //combox
        if(gdziejestem >=11 && gdziejestem <=15){
            gdziejestem=1;loadScene1();
            jLabel6.setText("");//text oznaczajacy gdzie sie jest
        }
        else if(gdziejestem >=111 && gdziejestem <=113){gdziejestem=11;loadScene11();jLabel6.setText("Modify player");}
        else if(gdziejestem >=121 && gdziejestem<=125){gdziejestem=12;loadScene12();jLabel6.setText("Modify club/country");}
        else if(gdziejestem >=131 && gdziejestem <=133){gdziejestem=13;loadScene13();jLabel6.setText("Modify transfer list");}
        else if(gdziejestem >= 151 && gdziejestem <=154){
            if(gdziejestem==154 || gdziejestem==152){jRadioButton1.setVisible(false);jRadioButton2.setVisible(false);}
            gdziejestem=15;loadScene15();jScrollPane1.setVisible(false);jLabel6.setText("Search information");}
        else if(gdziejestem==1){disconnectFromDB();System.exit(0);} 
        
        System.out.println(gdziejestem);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6MouseClicked

    private void jButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseClicked
        //przycisk drugi od góry
        switch(gdziejestem){
            case 1:
                loadScene12();
                gdziejestem=12;
                jLabel6.setText("Modify club/country");
                System.out.println(gdziejestem);
                break;
            case 11:
                loadScene11X();
                gdziejestem=112;
                loadTxtFields();
                jLabel1.setText("kogo:");
                jLabel2.setText("nazwisko:");
                jLabel3.setText("wartosc:");
                jLabel3.setVisible(true);
                jLabel6.setText("Change player's name");
                ktore.clear();ktore.add(0);
                showTextBoxes(ktore); //field autofill
                ktore.set(0,1);ktore.add(2);
                showTextFields(ktore);// jTextField
                System.out.println(gdziejestem);
                break;
            case 12://modify club (update)
                loadScene15X();
                gdziejestem=122;
                jLabel6.setText("Modify club");
                loadTxtFields();
                jScrollPane1.setVisible(false);//chowam tabele
                jButton1.setVisible(false);//chowam button na samym dole
                jLabel1.setText("klub:");
                jLabel2.setText("nowa nazwa:");
                jLabel3.setText("państwo:");
                jLabel1.setVisible(true);jLabel2.setVisible(true);jLabel3.setVisible(true);
                ktore.clear();ktore.add(0);ktore.add(2);
                showTextBoxes(ktore); //field autofill
                ktore.clear();ktore.add(1);
                showTextFields(ktore); // jTextField
                break;
            case 13:
                loadScene15X();//4 przyciski znikają
                jScrollPane1.setVisible(false);
                jButton1.setVisible(false);
                //strona 1.3
                jLabel1.setText("nazwisko:");jLabel2.setText("typ oferty:");
                jLabel3.setText("cena:");
                jLabel1.setVisible(true);jLabel2.setVisible(true);jLabel3.setVisible(true);
                ktore.clear();ktore.add(0);
                showTextBoxes(ktore);ktore.set(0,1);ktore.add(2);
                showTextFields(ktore);
                gdziejestem=132;
                jLabel6.setText("Update offer");
                loadTxtFields();
                System.out.println(gdziejestem);
                break;
            case 15:
                loadScene15X();
                gdziejestem=152;
                jLabel6.setText("Find club/country");
                loadTxtFields();
                //strona 1.5.2
                jLabel1.setText("klub:");jLabel2.setText("kraj:");
                jLabel1.setVisible(true);jLabel2.setVisible(true);
                ktore.clear();ktore.add(1);ktore.add(0);
                showTextBoxes(ktore);
                jRadioButton1.setText("Club");
                jRadioButton2.setText("Country");
                jRadioButton1.setVisible(true);
                jRadioButton2.setVisible(true);
                System.out.println(gdziejestem);
                break;
        }
    }//GEN-LAST:event_jButton4MouseClicked

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked
        //przycisk 3 od góry
        switch(gdziejestem){
            case 1:
                loadScene13();//4 przyciski znikają
                jScrollPane1.setVisible(false);
                jButton1.setVisible(false);
              //  jButton7.setVisible(true);
               // jButton6.setText("Return");
                
                gdziejestem=13;
                jLabel6.setText("Modify transfer list");
                System.out.println(gdziejestem);
                break;
            case 11:
                loadScene11X();
                jLabel2.setText("skad:");
                ktore.clear();ktore.add(0);
                showTextBoxes(ktore);
                gdziejestem=113;
                loadTxtFields();
                jLabel6.setText("Delete player");
                jLabel7.setText("");jLabel7.setVisible(true);
                System.out.println(gdziejestem);
                break;      
            case 12:
                loadScene15X();
                jScrollPane1.setVisible(false);//chowam tabele
                jButton1.setVisible(false);//chowam button na samym dole
                jLabel1.setText("nazwa:");
                jLabel1.setVisible(true);
                ktore.clear();ktore.add(0);
                showTextBoxes(ktore);
                gdziejestem=123;
                jLabel6.setText("Delete club");
                loadTxtFields();
                System.out.println(gdziejestem);
                break;
            case 13:
                loadScene15X();
                jScrollPane1.setVisible(false);//chowam tabele
                jButton1.setVisible(false);//chowam button na samym dole
                jLabel1.setText("zawodnik:");
                jLabel1.setVisible(true);
                ktore.clear();ktore.add(0);
                showTextBoxes(ktore);
                gdziejestem=133;
                jLabel6.setText("Withdraw from list");
                loadTxtFields();
                System.out.println(gdziejestem);
                break;
            case 15:
                loadScene15X();
                //strona 1.5.3
                jComboBox1.setVisible(true);
                jLabel1.setText("nazwisko:");jLabel2.setText("skad:");jLabel3.setText("typ oferty:");
                jLabel4.setText("cena:");
                jLabel1.setVisible(true);jLabel2.setVisible(true);jLabel3.setVisible(true);jLabel4.setVisible(true);
                ktore.clear();ktore.add(1);ktore.add(0);
                showTextBoxes(ktore);ktore.set(0,3);ktore.set(1,2);
                showTextFields(ktore);
                gdziejestem=153;
                jLabel6.setText("Search transfer list");
                loadTxtFields();
                System.out.println(gdziejestem);
        }
    }//GEN-LAST:event_jButton3MouseClicked

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        //przycisk 4 od góry
        //to samo dla strony 1.4 i 1.5.4
        if(gdziejestem==1 || gdziejestem==15){
        jLabel1.setText("nazwisko:");jLabel2.setText("skad:");jLabel3.setText("dokad:");
        jLabel1.setVisible(true);jLabel2.setVisible(true);jLabel3.setVisible(true);jLabel4.setVisible(true);}
        switch(gdziejestem){
            case 1:
                loadScene15X();//4 przyciski znikają, tablica pojawia sie 
                jScrollPane1.setVisible(false);//tablica znika
                jButton1.setVisible(false);//znika przycisk na samym dole
                jButton6.setText("Return");
                jButton7.setVisible(true);//pojawia sie confirm
                //strona 1.4
                jLabel4.setText("kwota:");
                ktore.clear();ktore.add(0);ktore.add(2);
                showTextBoxes(ktore);ktore.remove(1);ktore.set(0,3);
                showTextFields(ktore);
                gdziejestem=14;
                loadTxtFields();
                jLabel6.setText("Make a deal");
                jLabel7.setText("");jLabel7.setVisible(true);
                System.out.println(gdziejestem);
                break;
            case 12:
                loadScene15X();
                jScrollPane1.setVisible(false);//chowam tabele
                jButton1.setVisible(false);//chowam button na samym dole
                jLabel1.setText("nowy kraj:");
                jLabel1.setVisible(true);
                ktore.clear();ktore.add(0);
                showTextFields(ktore);
                gdziejestem=124;
                loadTxtFields();
                jLabel6.setText("Add country");
                System.out.println(gdziejestem);
                break;
            case 15:
                loadScene15X();
                //strona 1.5.4
                jComboBox1.setVisible(true);
                jLabel4.setText("kwota zap.:");
                ktore.clear();ktore.add(1);ktore.add(2);ktore.add(0);
                showTextBoxes(ktore);ktore.clear();ktore.add(3);
                showTextFields(ktore);
                gdziejestem=154;
                loadTxtFields();
                jLabel6.setText("Search history");
                //pokaz radio buttony
                jRadioButton1.setText("Find");
                jRadioButton2.setText("Delete");
                jRadioButton1.setVisible(true);
                jRadioButton2.setVisible(true);
                idT=-1;
                System.out.println(gdziejestem);
                break;
        }       
        
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        // TODO add your handling code here:
        switch(gdziejestem)
        {
            case 1:
                loadScene15();
                gdziejestem=15;
                jLabel6.setText("Search information");
                break;
            case 12:
                loadScene15X();
                jScrollPane1.setVisible(false);//chowam tabele
                jButton1.setVisible(false);//chowam button na samym dole
                jLabel1.setText("nazwa kraju:");
                jLabel1.setVisible(true);
                ktore.clear();ktore.add(0);
                showTextBoxes(ktore);
                gdziejestem=125;
                loadTxtFields();
                jLabel6.setText("Delete country");
                System.out.println(gdziejestem);
                break;
        }
    }//GEN-LAST:event_jButton1MouseClicked

    private void jButton7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton7MouseClicked
        // Confirm Button
        Query q;
        readDataFromTxtFields();
        model.setRowCount(0);
        model.setColumnCount(0);
        if(gdziejestem ==111) //insert player into database
        {
            Zawodnik z=new Zawodnik();
            if(params.get(0).length()>0 && params.get(3).length()<=9 && params.get(3).length()>0
                    && params.get(1).length()>0 && params.get(2).length()>0)
            {try
            {
            
        //    if(params.get(0).length()>0)
                z.setNazwisko(params.get(0).toUpperCase());
        //    else
        //        {System.out.println("Nie podales nazwiska!");
        //            textFailure(jLabel5); 
       //         }
        //    if(params.get(3).length()<=9 && params.get(3).length()>0)    
        //    {    
              /*  try
                {*/
                    z.setWartosc(Integer.parseInt(params.get(3)));
             /*   }
                catch(Exception ex)
                {
                    System.out.print("Cannot convert to int");
                    textFailure(jLabel5); 
                    //throw ex;
                }*/
       //     }        
            //użycie map 
            // -> 0 zawodnicy ; 1 kluby ; 2 kraje ; 3 listatrans ; 4 dokonanytrans
          //  try
         //   {
                z.setIdKlubu((Klub)dict.get(1).get(params.get(1)));
         //   }
    /*        catch(Exception ex)
            {
                System.out.print("Podano nieprawidłowy klub");
                textFailure(jLabel5); 
               // throw ex;
            }
*/  //          try
     //       {
                z.setIdNarod((Kraj)dict.get(2).get(params.get(2)));
      //      }
       //     catch(Exception ex)
      //      {
       //         System.out.print("Podano nieprawidłowy kraj");
       //         textFailure(jLabel5); 
               // throw ex;
        //    }
        //    try
       //     {
                em.getTransaction().begin();
                em.persist(z);
                em.getTransaction().commit();
                addToHashData(z);
                updateTxtList(0);
                textSuccess(jLabel5);    
            }
            catch(Exception ex)
            {
                System.out.println("Coś się, coś się popsuło...");
                textFailure(jLabel5); 
            }
            }
            else {
                System.out.println("Nieprawidłowe dane..");
                textFailure(jLabel5);
                
            }
            
        //    }
         //   catch(Exception ex)
         //   {
          //      System.out.println("Coś się, coś się popsuło 22...");
           //     textFailure(jLabel5);
            
          //  }
        } 
        else if(gdziejestem == 112)
        {
            //pobieram nazwisko z params.get(0) i to jest moj klucz
            //szukam w dict.get(0) obiektu z kluczem == params.get(0)
            if (params.get(1).length() > 0 && params.get(1).length()<=30 && 
                    params.get(2).length() > 0 && params.get(2).length()<=9)
            {
                try
                {
                    Zawodnik pom=(Zawodnik)dict.get(0).get(params.get(0).toUpperCase());
                    Zawodnik z=em.find(Zawodnik.class,pom.getIdGracza());
                    em.getTransaction().begin();
                    z.setNazwisko(params.get(1).toUpperCase());
                    z.setWartosc(Integer.parseInt(params.get(2)));
                    em.getTransaction().commit();
                    System.out.print(z.getNazwisko()+" "+z.getWartosc());
                    //update key in hashmap
                    removeFromHashData(0,params.get(0));
                    addToHashData(z);
                    updateTxtList(0);
                    textSuccess(jLabel5); 
                }
                catch(Exception ex)
                {
                    System.out.println("Puste nazwisko wybrano lub nie integer");
                    textFailure(jLabel5); 
                }
            }
            else {
                System.out.println("Zla dlugosc");
                textFailure(jLabel5);
            }
        
        }
        else if(gdziejestem == 113)
        {
            try{
                Zawodnik pom=(Zawodnik)dict.get(0).get(params.get(0).toUpperCase());
                Zawodnik z=em.find(Zawodnik.class,pom.getIdGracza());
                em.getTransaction().begin();
                em.remove(z);
                em.getTransaction().commit();
                removeFromHashData(0,z.getNazwisko());
                updateTxtList(0);
                textSuccess(jLabel5); 
            }
            catch(Exception ex)
            {
                 System.out.println("coś poszlo nie tak ...");
                 textFailure(jLabel5);
            }
        }
        else if(gdziejestem ==121)//dodaj nowy klub
        {
            Klub k=new Klub();
            if(params.get(0).length()>0 && params.get(0).length()<=30  && params.get(1).length()>0)
            {
                k.setNazwa(params.get(0).toUpperCase());
                k.setIdPanstwa((Kraj)dict.get(2).get(params.get(1)));
            }
            else {
                System.out.println("nie ma podanej nazwy klubu");
                textFailure(jLabel5);
            }
            try
            {
                em.getTransaction().begin();
                em.persist(k);
                em.getTransaction().commit();
                addToHashData(k);
                updateTxtList(1);
                textSuccess(jLabel5);
            }
            catch(Exception ex)
            {
                System.out.println("Nie moglem dodac klubu");
                textFailure(jLabel5);
            }
        }
        else if(gdziejestem == 122){//update existing club
            //sprawdzam poprawnosc podanych danych
            if (params.get(0).length()>0 && params.get(1).length() > 0 && params.get(1).length()<=30 && 
                    params.get(2).length() > 0)
            {
                try{
                    Klub pom=(Klub)dict.get(1).get(params.get(0).toUpperCase());
                    Klub z=em.find(Klub.class,pom.getIdKlubu());
                    Kraj pom2=(Kraj)dict.get(2).get(params.get(2).toUpperCase());
                    em.getTransaction().begin();
                    z.setNazwa(params.get(1).toUpperCase());
                    z.setIdPanstwa(pom2);
                    em.getTransaction().commit();
                    System.out.print(z.getNazwa()+" "+z.getIdPanstwa().getNazwa());
                    //update key in hashmap
                    removeFromHashData(1,params.get(0));
                    addToHashData(z);
                    updateTxtList(1);
                    textSuccess(jLabel5);
                    //update hash mape dla zawodnikow ie. ich nowy klub
                    
                    
                }
                catch(Exception ex){
                    System.out.println("Nie moglem zaktualizowac klubu");
                    textFailure(jLabel5); 
                }
            }
            else {
                System.out.println("Niepoprawne dane");
                textFailure(jLabel5); 
            }
        }
        else if(gdziejestem == 123){//delete club from database
            try{
                Klub pom=(Klub)dict.get(1).get(params.get(0).toUpperCase());
                Klub z=em.find(Klub.class,pom.getIdKlubu());
                em.getTransaction().begin();
                em.remove(z);
                em.getTransaction().commit();
                removeFromHashData(1,z.getNazwa());
                updateTxtList(1);
                textSuccess(jLabel5); 
                //usun z hash mapy graczy, ktorzy nalezeli do tego klubu
                removeManyFromHashData(0,1,z.getNazwa()); // 0-> zawodnicy | 1-> według klubu
                updateTxtList(0);
            }
            catch(Exception ex)
            {
                 System.out.println("Nie moglem usunac klubu");
                 textFailure(jLabel5);
            }
        }
        else if(gdziejestem == 124){//add country
            Kraj k=new Kraj();
            if(params.get(0).length()>0 && params.get(0).length()<=30 )
            {
                k.setNazwa(params.get(0).toUpperCase());
            }
            else {
                System.out.println("nie ma podanej nazwy kraju");
                textFailure(jLabel5);
            }
            try
            {
                em.getTransaction().begin();
                em.persist(k);
                em.getTransaction().commit();
                addToHashData(k);
                updateTxtList(2);
                textSuccess(jLabel5);
            }
            catch(Exception ex)
            {
                System.out.println("Nie moglem dodac kraju");
                textFailure(jLabel5);
            }
        }
        else if(gdziejestem == 125){//delete country
            if(params.get(0).length()>0)
            {
                try{
                    Kraj pom=(Kraj)dict.get(2).get(params.get(0).toUpperCase());
                    Kraj z=em.find(Kraj.class,pom.getIdKraju());
                    em.getTransaction().begin();
                    em.remove(z);
                    em.getTransaction().commit();
                    removeFromHashData(2,z.getNazwa());
                    updateTxtList(2); 
                    //usun z hash mapy
                    //usun graczy majacych narodowosc = params.get(0)
                    removeManyFromHashData(0,2,params.get(0).toUpperCase()); // 0-> zawodnicy
                    System.out.println("przeszlo 1");
                    //usun graczy z klubow ulokowanych w params.get(0)
                    removeManyFromHashData(0,3,params.get(0).toUpperCase()); // 0 ->zawodnicy
                    System.out.println("przeszlo 2");
                    //usun kluby bedace ulokowane w params.get(0)
                    removeManyFromHashData(1,0,params.get(0).toUpperCase());//1 -> kluby
                    System.out.println("przeszlo 3");
                    updateTxtList(0);
                    updateTxtList(1);
                    textSuccess(jLabel5);
                }
                catch(Exception ex)
                {
                     System.out.println("Nie moglem usunac kraju");
                     textFailure(jLabel5);
                }
            }
            else {
                System.out.println("Nie podano kraju");
                 textFailure(jLabel5);
            }
        }
        else if(gdziejestem ==131)
        {
            Listatrans lt=new Listatrans();
            if(params.get(0).length()>0 && params.get(1).length()<=30 && params.get(1).length()>0 &&
                    params.get(2).length()>0 && params.get(2).length()<=9) // params.get(1). is default 'SPRZEDAZ'
            {
               try{
                   Zawodnik pom=(Zawodnik)dict.get(0).get(params.get(0));
                   lt.setNazwisko(pom.getNazwisko());
                   lt.setIdZawodnika(pom);
                   lt.setKwotaOdstepnego(Integer.parseInt(params.get(2)));
                  // if(params.get(1).length()>0)
                   lt.setTypOferty(params.get(1));
                   em.getTransaction().begin();
                   em.persist(lt);
                   em.getTransaction().commit();
                   textSuccess(jLabel5);
               }
               catch(Exception ex)
               {
                   System.out.println("Blad 13");
                   textFailure(jLabel5);
               }         
            }
            else {System.out.println("nie ma podanego typu oferty");textFailure(jLabel5);}
        }
        else if(gdziejestem == 132){//update offer
            if (params.get(0).length()>0 && params.get(1).length() > 0 && params.get(1).length()<=30 && 
                    params.get(2).length() > 0 && params.get(2).length()<=9)
            {
                try
                {
                    //znajdz rekord z danym zawodnikiem
                    q=em.createNamedQuery("Listatrans.findByNazwisko");
                    q.setParameter("nazwisko",params.get(0).toUpperCase());
                    //wykonaj zapytanie
                    Listatrans pom=(Listatrans)q.getSingleResult();
                    Listatrans z=em.find(Listatrans.class,pom.getIdOferty());
                    em.getTransaction().begin();
                    z.setTypOferty(params.get(1).toUpperCase());
                    z.setKwotaOdstepnego(Integer.parseInt(params.get(2)));
                    em.getTransaction().commit();
                    //wyswietl zmiany
                    System.out.println(z.getTypOferty()+" "+z.getKwotaOdstepnego());
                    textSuccess(jLabel5); 
                }
                catch(Exception ex)
                {
                    System.out.println("Nie mozna aktualizowac oferty");
                    textFailure(jLabel5); 
                }
            }
            else {
                System.out.println("Zla dlugosc argumentow");
                textFailure(jLabel5);
            }
        }
        else if(gdziejestem == 133){
            if(params.get(0).length()>0)
            {
                try{
                    //znajdz rekord z danym zawodnikiem
                    q=em.createNamedQuery("Listatrans.findByNazwisko");
                    q.setParameter("nazwisko",params.get(0).toUpperCase());
                    //usun gracza z listy transferowej
                    Listatrans pom=(Listatrans)q.getSingleResult();
                    Listatrans z=em.find(Listatrans.class,pom.getIdOferty());
                    em.getTransaction().begin();
                    em.remove(z);
                    em.getTransaction().commit();
                    textSuccess(jLabel5); 
                }
                catch(Exception ex){
                    System.out.println("Nie mozna usunac oferty");
                    textFailure(jLabel5);
                }
            }
            else{
                System.out.println("Puste nazwisko");
                textFailure(jLabel5);
            }
        }
        else if(gdziejestem == 14) //dokonujemy deala
        {
            //mamy parametry: params.get(0) = imie_nazwisko | params.get(1) = dokad | params.get(2) = kwota
            //sprawdz najpierw, czy zawodnik jest na liście transferowej    
            //sprawdz , czy obecny klub zawodnika jest rozny od "dokad"
            Dokonanytrans dt = new Dokonanytrans();
            if (params.get(0).length()>0 && params.get(1).length()>0 &&
                    params.get(2).length()>0 && params.get(2).length()<=9)
            {
                //sprawdz czy zawodnik jest w ogóle na liscie transferowej
                //ustaw parametr funkcji
                funkc.setParameter(1,params.get(0));
                BigDecimal wynik=(BigDecimal)funkc.getSingleResult();
                
                if(wynik.intValue()==1)
                {    
                try{
                   Zawodnik pom=(Zawodnik)dict.get(0).get(params.get(0));
                   //sprawdz czy skad != dokad
                   if(pom.getIdKlubu().getNazwa()!=params.get(1))
                   {
                       //dokonaj transferu
                       Klub k=new Klub();
                       k=(Klub)dict.get(1).get(params.get(1));
                       int id1=(int)pom.getIdKlubu().getIdKlubu();
                       int id2=(int)k.getIdKlubu();
                       //nazwisko1 IN VARCHAR2,kwota1 IN Number, skad1 IN Number,dokad1 IN Number 
                       stored.setParameter("nazwisko1", params.get(0));
                       stored.setParameter("kwota1", Integer.parseInt(params.get(2)));
                       stored.setParameter("skad1",id1);
                       stored.setParameter("dokad1",id2);
                       //execute stored procedure
                       stored.execute();
                       //zaktualizuj hashMape dict
                       //usun zawodnika ze starym klubem
                       removeFromHashData(0,pom.getNazwisko());
                       pom.setIdKlubu(k);
                       System.out.println("nowy klub:"+pom.getIdKlubu().getNazwa());
                       //dodaj zawodnika z nowym klubem
                       addToHashData(pom);
                       //w sumie to nie jest chyba konieczne
                       updateTxtList(0);
                       textSuccess(jLabel5);
                   }   
                   else {
                       System.out.println("Nie mozna sie przeniesc do tego samego klubu");
                        textFailure(jLabel5);
                   }
               }
               catch(Exception ex)
               {
                   System.out.println("Blad 14");
                   textFailure(jLabel5);
               }   
               }
               else {//wynik !=1
                    System.out.println("Nie ma na liscie transferowej");
                   textFailure(jLabel5);
                }
            }
            else textFailure(jLabel5);
        }
        else if (gdziejestem==151)
        {
        
            //jTable1.addColumn("nazwisko");
            //DefaultTableModel model = new DefaultTableModel();
            jTable1.setModel(model);
            String[] nazwy={"nazwisko","narodowość","klub","kraj klubu","wartość"};
            int[] dlugosci={120,120,140,140,75};
            addModelColumns(model,nazwy);
            setModelColumnsLength(jTable1,dlugosci);
            
            String op=(String)jComboBox1.getSelectedItem();
            String[] array={"Zawodnik.filtered0","Zawodnik.filtered1","Zawodnik.filtered2"};
            q=setQuery(array,op);
            setQueryParams(q,3);
            
            List<Zawodnik> listaGraczy = q.getResultList();
            for(Zawodnik z : listaGraczy)
                //System.out.println(z.getNazwisko() +" kraj gracza: "+z.getIdNarod().getNazwa() +" Klub:"+z.getIdKlubu().getNazwa() + "Kraj klubu:"+z.getIdKlubu().getIdPanstwa().getNazwa());
                 model.addRow(new Object[]{z.getNazwisko(),z.getIdNarod().getNazwa(),z.getIdKlubu().getNazwa(),
                     z.getIdKlubu().getIdPanstwa().getNazwa(),z.getWartosc()}); 
        }
        else if (gdziejestem ==152)
        {
            //ustaw mode154 jesli 1 to club, 0 to country
            setPageMode();
            jTable1.setModel(model);
            
            if(mode154==1)
            {
                String[] nazwy={"klub","kraj klubu"};
                int[] dlugosci={140,140};
                addModelColumns(model,nazwy);
                setModelColumnsLength(jTable1,dlugosci);
            
                q=em.createNamedQuery("Klub.filtered");
                setQueryParams(q,2);
                List<Klub> listaKlubow = q.getResultList();
                for(Klub k : listaKlubow)
                    //System.out.println(z.getNazwisko() +" kraj gracza: "+z.getIdNarod().getNazwa() +" Klub:"+z.getIdKlubu().getNazwa() + "Kraj klubu:"+z.getIdKlubu().getIdPanstwa().getNazwa());
                     model.addRow(new Object[]{k.getNazwa(),k.getIdPanstwa().getNazwa()});
            }
            else // mode154==0 ,czyli country
            {
                String[] nazwy={"kraj"};
                int[] dlugosci={140};
                addModelColumns(model,nazwy);
                setModelColumnsLength(jTable1,dlugosci);
                q=em.createNamedQuery("Kraj.filtered1");
                q.setParameter(1,params.get(1).toUpperCase());
                List<Kraj> listaKlubow = q.getResultList();
                for(Kraj k : listaKlubow)
                    model.addRow(new Object[]{k.getNazwa()});
            }
        }
        else if(gdziejestem==153)
        {
            jTable1.setModel(model);
            String[] nazwy={"nazwisko","narodowość","klub","typ oferty","cena"};
            int[] dlugosci={120,120,140,100,75};
            //dodaj kolumny do modelu
            addModelColumns(model,nazwy);
            //ustawszerokosc kolumn
            setModelColumnsLength(jTable1,dlugosci);
            String op=(String)jComboBox1.getSelectedItem();            
            String[] array={"Listatrans.filtered0","Listatrans.filtered1","Listatrans.filtered2"};
            q=setQuery(array,op);
            setQueryParams(q,3);
            
            List<Listatrans> listaGraczy = q.getResultList();
            for(Listatrans z : listaGraczy)
                model.addRow(new Object[]{z.getNazwisko(),z.getIdZawodnika().getIdNarod().getNazwa(),
                     z.getIdZawodnika().getIdKlubu().getNazwa(),z.getTypOferty(),z.getKwotaOdstepnego()});
        }
        else if (gdziejestem==154)
        {
            //odczytuje tryb strony -> find albo delete
            setPageMode();//ustawiam zmienna mode154
            System.out.println("mode:"+mode154);
           
            if(mode154==0) //mode154==0 tryb delete
            {
                if(idT>=0)//jesli wybrano jakis rekord; domyślnie idT =-1; idT to id_transakcji
                {
                     //usuwam
                    try{
                    //znajdz rekord z danym idTransakcji
                    Dokonanytrans z=em.find(Dokonanytrans.class,idT);
                    //usun rekord z DokonanychTransferow
                    em.getTransaction().begin();
                    em.remove(z);
                    em.getTransaction().commit();
                    textSuccess(jLabel5); 
                    }
                    catch(Exception ex){
                        System.out.println("Nie mozna usunac umowy z historii");
                        textFailure(jLabel5);
                }           
                } 
                else {
                    System.out.println("nie wybrano rekordu");
                    textFailure(jLabel5);
                }
            }
            else//tryb find
            {
                idT=-1;            
            }
            jTable1.setModel(model);
            String[] nazwy={"nazwisko","skąd","dokąd","kwota","data",""};
            int[] dlugosci={120,140,140,75,200,0};
            //dodaj kolumny do modelu
            addModelColumns(model,nazwy);
            //ustawszerokosc kolumn
            setModelColumnsLength(jTable1,dlugosci);
            //ukryj kolumne z idTransakcji
            jTable1.removeColumn(jTable1.getColumnModel().getColumn(5));
            String op=(String)jComboBox1.getSelectedItem();            
            String[] array={"Dokonanytrans.filtered0","Dokonanytrans.filtered1","Dokonanytrans.filtered2"};
            q=setQuery(array,op);
            setQueryParams(q,3);
                
            List<Dokonanytrans> listaGraczy = q.getResultList();
            for(Dokonanytrans d : listaGraczy)
                model.addRow(new Object[]{d.getNazwisko(),d.getSkad().getNazwa(),
                    d.getDokad().getNazwa(),d.getKwota(),d.getDataTransferu(),d.getIdTransakcji()}); 
        }
    }//GEN-LAST:event_jButton7MouseClicked
    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
         disconnectFromDB();
    }//GEN-LAST:event_formWindowClosing

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if(gdziejestem==154)
        {
            int selectedRowIndex;
            selectedRowIndex = jTable1.getSelectedRow();
            idT=(Short)model.getValueAt(selectedRowIndex, 5);
            System.out.println("SelectRow : "+selectedRowIndex+" idTrans:"+idT);
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void loadScene1(){
         jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
         jButton5.setText("Modify player");
         jButton4.setText("Modify club/country");
         jButton3.setText("Modify transfer list");
         jButton2.setText("Make deal");
         jButton1.setText("Search information");
         jButton6.setText("Quit");
         jButton5.setVisible(true);
         jButton4.setVisible(true);
         jButton3.setVisible(true);
         jButton2.setVisible(true);
         jButton1.setVisible(true);
         jLabel1.setVisible(false);
         jLabel2.setVisible(false);
         jLabel3.setVisible(false);
         jLabel4.setVisible(false);
         hideTextBoxes();
         hideTextFields();
    }
    
    private void loadScene12(){
         jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
         jButton5.setText("Add club");
         jButton4.setText("Modify club");
         jButton3.setText("Delete club");
         jButton2.setText("Add country");
         jButton1.setText("Delete country");
         jButton6.setText("Return");
         jButton5.setVisible(true);
         jButton4.setVisible(true);
         jButton3.setVisible(true);
         jButton2.setVisible(true);
         jButton1.setVisible(true);
         jLabel1.setVisible(false);
         jLabel2.setVisible(false);
         jLabel3.setVisible(false);
         jLabel4.setVisible(false);
         hideTextBoxes();
         hideTextFields();  
    }
    
    private void loadScene11(){
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton5.setText("Add player");
        jButton4.setText("Change player's name");
        jButton3.setText("Delete player");
        jButton6.setText("Return");
        jButton5.setVisible(true);
        jButton4.setVisible(true);
        jButton3.setVisible(true);
        jButton2.setVisible(false);
        jButton1.setVisible(false);
        jLabel1.setVisible(false);
        jLabel2.setVisible(false);
        jLabel3.setVisible(false);
        jLabel4.setVisible(false);
        hideTextFields();
        hideTextBoxes();
    }
    private void loadScene11X(){
        jButton5.setVisible(false);
        jButton4.setVisible(false);
        jButton3.setVisible(false);
        jLabel1.setVisible(true);
        jLabel2.setVisible(true);
        jButton7.setVisible(true);
        jLabel1.setText("nazwisko:");
    }
    private void loadScene13(){
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton5.setText("Place in list");
        jButton4.setText("Update offer");
        jButton3.setText("Withdraw from list");
        jButton6.setText("Return");
        jButton5.setVisible(true);
        jButton4.setVisible(true);
        jButton3.setVisible(true);
        jButton2.setVisible(false);
        jButton1.setVisible(false);
        jLabel1.setVisible(false);
        jLabel2.setVisible(false);
        jLabel3.setVisible(false);
        jLabel4.setVisible(false);
        hideTextFields();
        hideTextBoxes();
    }
    private void loadScene15(){
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton5.setText("Find player");
        jButton4.setText("Find club/country");
        jButton3.setText("Search transfer list");
        jButton2.setText("Search history");
        jButton6.setText("Return");   
        jButton5.setVisible(true);
        jButton4.setVisible(true);
        jButton3.setVisible(true);
        jButton2.setVisible(true);
        jButton1.setVisible(false);
        jLabel1.setVisible(false);
        jLabel2.setVisible(false);
        jLabel3.setVisible(false);
        jLabel4.setVisible(false);
        hideTextBoxes();
        hideTextFields();
    }
    private void loadScene15X(){
        jButton5.setVisible(false);
        jButton4.setVisible(false);
        jButton3.setVisible(false);
        jButton2.setVisible(false);
        jScrollPane1.setVisible(true);
        jButton7.setVisible(true);
    }
    
    private void hideTextBoxes()
    {
        for(int i=0;i<4;i++)field[i].setVisible(false);
    }
    
    private void hideTextFields()
    {
        for(int i=0;i<4;i++)listText.get(i).setVisible(false);
    }
    private void showTextFields(ArrayList<Integer>kt)
    {
        for(int i=0;i<4;i++)
        {
            if(kt.contains(i))listText.get(i).setVisible(true);
        }
    }
    
    private void showTextBoxes(ArrayList<Integer>kt)
    {
        for(int i=0;i<4;i++)
        {
            if(kt.contains(i))field[i].setVisible(true);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frmRynek.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmRynek.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmRynek.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmRynek.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmRynek().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    // End of variables declaration//GEN-END:variables
}
