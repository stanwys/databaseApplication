/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rynektransferowy;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Staszek
 */
@Entity
@Table(name = "KRAJE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Kraj.findAll", query = "SELECT k FROM Kraj k")
    , @NamedQuery(name = "Kraj.findByIdKraju", query = "SELECT k FROM Kraj k WHERE k.idKraju = :idKraju")
    , @NamedQuery(name = "Kraj.findByNazwa", query = "SELECT k FROM Kraj k WHERE k.nazwa = ?1")
    , @NamedQuery(name = "Kraj.filtered1",query="SELECT k FROM Kraj k WHERE (?1 IS NULL OR k.nazwa = ?1)") 
})

@SequenceGenerator(name="sek3",sequenceName="LICZKR",allocationSize = 1)
public class Kraj implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="sek3")
    @Basic(optional = false)
    @Column(name = "ID_KRAJU")
    private Short idKraju;
    @Basic(optional = false)
    @Column(name = "NAZWA")
    private String nazwa;
    @OneToMany(mappedBy = "idNarod")
    private Collection<Zawodnik> zawodnikCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idPanstwa")
    private Collection<Klub> klubCollection;

    public Kraj() {
    }

    public Kraj(Short idKraju) {
        this.idKraju = idKraju;
    }

    public Kraj(Short idKraju, String nazwa) {
        this.idKraju = idKraju;
        this.nazwa = nazwa;
    }

    public Short getIdKraju() {
        return idKraju;
    }

    public void setIdKraju(Short idKraju) {
        this.idKraju = idKraju;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    @XmlTransient
    public Collection<Zawodnik> getZawodnikCollection() {
        return zawodnikCollection;
    }

    public void setZawodnikCollection(Collection<Zawodnik> zawodnikCollection) {
        this.zawodnikCollection = zawodnikCollection;
    }

    @XmlTransient
    public Collection<Klub> getKlubCollection() {
        return klubCollection;
    }

    public void setKlubCollection(Collection<Klub> klubCollection) {
        this.klubCollection = klubCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idKraju != null ? idKraju.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Kraj)) {
            return false;
        }
        Kraj other = (Kraj) object;
        if ((this.idKraju == null && other.idKraju != null) || (this.idKraju != null && !this.idKraju.equals(other.idKraju))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rynektransferowy.Kraj[ idKraju=" + idKraju + " ]";
    }
    
}
