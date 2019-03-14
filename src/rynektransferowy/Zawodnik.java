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
@Table(name = "ZAWODNICY")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Zawodnik.findAll", query = "SELECT z FROM Zawodnik z")
    , @NamedQuery(name = "Zawodnik.findByIdGracza", query = "SELECT z FROM Zawodnik z WHERE z.idGracza = :idGracza")
    , @NamedQuery(name = "Zawodnik.findByNazwisko", query = "SELECT z FROM Zawodnik z WHERE z.nazwisko = ?1")
    , @NamedQuery(name = "Zawodnik.findByWartosc", query = "SELECT z FROM Zawodnik z WHERE z.wartosc = :wartosc")
    , //@NamedQuery(name = "Zawodnik.filtered", query = "SELECT z FROM Zawodnik z WHERE (:p1 IS NULL OR z.nazwisko = :p1) AND (:p2 IS NULL OR z.idKlubu.nazwa = :p2)")    
    @NamedQuery(name = "Zawodnik.filtered0", query = "SELECT z FROM Zawodnik z WHERE (?1 IS NULL OR z.nazwisko = ?1) AND (?2 IS NULL OR z.idKlubu.nazwa = ?2) AND (?3 IS NULL OR z.idNarod.nazwa = ?3)"
            + "AND (?4 IS NULL OR z.wartosc <= ?4)")    
    , @NamedQuery(name = "Zawodnik.filtered1", query = "SELECT z FROM Zawodnik z WHERE (?1 IS NULL OR z.nazwisko = ?1) AND (?2 IS NULL OR z.idKlubu.nazwa = ?2) AND (?3 IS NULL OR z.idNarod.nazwa = ?3)"
            + "AND (?4 IS NULL OR z.wartosc = ?4)") 
    , @NamedQuery(name = "Zawodnik.filtered2", query = "SELECT z FROM Zawodnik z WHERE (?1 IS NULL OR z.nazwisko = ?1) AND (?2 IS NULL OR z.idKlubu.nazwa = ?2) AND (?3 IS NULL OR z.idNarod.nazwa = ?3)"
            + "AND (?4 IS NULL OR z.wartosc >= ?4)") 
}
)

@SequenceGenerator(name="sek1",sequenceName="LICZZAD",allocationSize = 1)
public class Zawodnik implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="sek1")
    @Basic(optional = false)
    @Column(name = "ID_GRACZA")
    private Integer idGracza;
    @Basic(optional = false)
    @Column(name = "NAZWISKO")
    private String nazwisko;
    @Basic(optional = false)
    @Column(name = "WARTOSC")
    private int wartosc;
    @OneToMany(mappedBy = "idZawodnika")
    private Collection<Dokonanytrans> dokonanytransCollection;
    @OneToMany(mappedBy = "idZawodnika")
    private Collection<Listatrans> listatransCollection;
    @JoinColumn(name = "ID_KLUBU", referencedColumnName = "ID_KLUBU")
    @ManyToOne
    private Klub idKlubu;
    @JoinColumn(name = "ID_NAROD", referencedColumnName = "ID_KRAJU")
    @ManyToOne
    private Kraj idNarod;

    public Zawodnik() {
    }

    public Zawodnik(Integer idGracza) {
        this.idGracza = idGracza;
    }

    public Zawodnik(Integer idGracza, String nazwisko, int wartosc) {
        this.idGracza = idGracza;
        this.nazwisko = nazwisko;
        this.wartosc = wartosc;
    }

    public Integer getIdGracza() {
        return idGracza;
    }

    public void setIdGracza(Integer idGracza) {
        this.idGracza = idGracza;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public int getWartosc() {
        return wartosc;
    }

    public void setWartosc(int wartosc) {
        this.wartosc = wartosc;
    }

    @XmlTransient
    public Collection<Dokonanytrans> getDokonanytransCollection() {
        return dokonanytransCollection;
    }

    public void setDokonanytransCollection(Collection<Dokonanytrans> dokonanytransCollection) {
        this.dokonanytransCollection = dokonanytransCollection;
    }

    @XmlTransient
    public Collection<Listatrans> getListatransCollection() {
        return listatransCollection;
    }

    public void setListatransCollection(Collection<Listatrans> listatransCollection) {
        this.listatransCollection = listatransCollection;
    }

    public Klub getIdKlubu() {
        return idKlubu;
    }

    public void setIdKlubu(Klub idKlubu) {
        this.idKlubu = idKlubu;
    }

    public Kraj getIdNarod() {
        return idNarod;
    }

    public void setIdNarod(Kraj idNarod) {
        this.idNarod = idNarod;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idGracza != null ? idGracza.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Zawodnik)) {
            return false;
        }
        Zawodnik other = (Zawodnik) object;
        if ((this.idGracza == null && other.idGracza != null) || (this.idGracza != null && !this.idGracza.equals(other.idGracza))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rynektransferowy.Zawodnik[ idGracza=" + idGracza + " ]";
    }
    
}
