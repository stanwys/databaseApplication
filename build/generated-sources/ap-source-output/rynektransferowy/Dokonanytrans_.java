package rynektransferowy;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import rynektransferowy.Klub;
import rynektransferowy.Zawodnik;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2019-01-15T12:51:58")
@StaticMetamodel(Dokonanytrans.class)
public class Dokonanytrans_ { 

    public static volatile SingularAttribute<Dokonanytrans, Short> idTransakcji;
    public static volatile SingularAttribute<Dokonanytrans, Date> dataTransferu;
    public static volatile SingularAttribute<Dokonanytrans, String> nazwisko;
    public static volatile SingularAttribute<Dokonanytrans, Klub> dokad;
    public static volatile SingularAttribute<Dokonanytrans, Zawodnik> idZawodnika;
    public static volatile SingularAttribute<Dokonanytrans, Integer> kwota;
    public static volatile SingularAttribute<Dokonanytrans, Klub> skad;

}