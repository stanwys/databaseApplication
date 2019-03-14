package rynektransferowy;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import rynektransferowy.Klub;
import rynektransferowy.Zawodnik;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2019-01-15T12:51:58")
@StaticMetamodel(Kraj.class)
public class Kraj_ { 

    public static volatile CollectionAttribute<Kraj, Klub> klubCollection;
    public static volatile SingularAttribute<Kraj, Short> idKraju;
    public static volatile CollectionAttribute<Kraj, Zawodnik> zawodnikCollection;
    public static volatile SingularAttribute<Kraj, String> nazwa;

}