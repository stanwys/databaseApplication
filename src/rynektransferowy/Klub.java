/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rynektransferowy;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "KLUBY")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Klub.findAll", query = "SELECT k FROM Klub k")
    , @NamedQuery(name = "Klub.findByIdKlubu", query = "SELECT k FROM Klub k WHERE k.idKlubu = :idKlubu")
    , @NamedQuery(name = "Klub.findByNazwa", query = "SELECT k FROM Klub k WHERE k.nazwa = ?1")
    , @NamedQuery(name = "Klub.filtered", query = "SELECT k FROM Klub k WHERE (?1 IS NULL OR k.nazwa = ?1) AND (?2 IS NULL OR k.idPanstwa.nazwa = ?2)")    
})


@SequenceGenerator(name="sek2",sequenceName="LICZKL",allocationSize = 1)
public class Klub implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="sek2")
    @Basic(optional = false)
    @Column(name = "ID_KLUBU")
    private Short idKlubu;
    @Basic(optional = false)
    @Column(name = "NAZWA")
    private String nazwa;
    @OneToMany(mappedBy = "dokad")
    private Collection<Dokonanytrans> dokonanytransCollection;
    @OneToMany(mappedBy = "skad")
    private Collection<Dokonanytrans> dokonanytransCollection1;
    @OneToMany(mappedBy = "idKlubu")
    private Collection<Zawodnik> zawodnikCollection;
    @JoinColumn(name = "ID_PANSTWA", referencedColumnName = "ID_KRAJU")
    @ManyToOne(optional = false)
    private Kraj idPanstwa;

    public Klub() {
    }

    public Klub(Short idKlubu) {
        this.idKlubu = idKlubu;
    }

    public Klub(Short idKlubu, String nazwa) {
        this.idKlubu = idKlubu;
        this.nazwa = nazwa;
    }

    public Short getIdKlubu() {
        return idKlubu;
    }

    public void setIdKlubu(Short idKlubu) {
        this.idKlubu = idKlubu;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    @XmlTransient
    public Collection<Dokonanytrans> getDokonanytransCollection() {
        return dokonanytransCollection;
    }

    public void setDokonanytransCollection(Collection<Dokonanytrans> dokonanytransCollection) {
        this.dokonanytransCollection = dokonanytransCollection;
    }

    @XmlTransient
    public Collection<Dokonanytrans> getDokonanytransCollection1() {
        return dokonanytransCollection1;
    }

    public void setDokonanytransCollection1(Collection<Dokonanytrans> dokonanytransCollection1) {
        this.dokonanytransCollection1 = dokonanytransCollection1;
    }

    @XmlTransient
    public Collection<Zawodnik> getZawodnikCollection() {
        return zawodnikCollection;
    }

    public void setZawodnikCollection(Collection<Zawodnik> zawodnikCollection) {
        this.zawodnikCollection = zawodnikCollection;
    }

    public Kraj getIdPanstwa() {
        return idPanstwa;
    }

    public void setIdPanstwa(Kraj idPanstwa) {
        this.idPanstwa = idPanstwa;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idKlubu != null ? idKlubu.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Klub)) {
            return false;
        }
        Klub other = (Klub) object;
        if ((this.idKlubu == null && other.idKlubu != null) || (this.idKlubu != null && !this.idKlubu.equals(other.idKlubu))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rynektransferowy.Klub[ idKlubu=" + idKlubu + " ]";
    }
    
}
