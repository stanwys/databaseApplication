package rynektransferowy;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import rynektransferowy.Dokonanytrans;
import rynektransferowy.Klub;
import rynektransferowy.Kraj;
import rynektransferowy.Listatrans;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2019-01-15T12:51:58")
@StaticMetamodel(Zawodnik.class)
public class Zawodnik_ { 

    public static volatile SingularAttribute<Zawodnik, Kraj> idNarod;
    public static volatile SingularAttribute<Zawodnik, String> nazwisko;
    public static volatile SingularAttribute<Zawodnik, Integer> idGracza;
    public static volatile CollectionAttribute<Zawodnik, Dokonanytrans> dokonanytransCollection;
    public static volatile SingularAttribute<Zawodnik, Klub> idKlubu;
    public static volatile SingularAttribute<Zawodnik, Integer> wartosc;
    public static volatile CollectionAttribute<Zawodnik, Listatrans> listatransCollection;

}