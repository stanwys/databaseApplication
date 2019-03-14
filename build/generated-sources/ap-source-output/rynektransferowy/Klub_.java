package rynektransferowy;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import rynektransferowy.Dokonanytrans;
import rynektransferowy.Kraj;
import rynektransferowy.Zawodnik;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2019-01-15T12:51:58")
@StaticMetamodel(Klub.class)
public class Klub_ { 

    public static volatile CollectionAttribute<Klub, Zawodnik> zawodnikCollection;
    public static volatile CollectionAttribute<Klub, Dokonanytrans> dokonanytransCollection;
    public static volatile SingularAttribute<Klub, Short> idKlubu;
    public static volatile CollectionAttribute<Klub, Dokonanytrans> dokonanytransCollection1;
    public static volatile SingularAttribute<Klub, String> nazwa;
    public static volatile SingularAttribute<Klub, Kraj> idPanstwa;

}