/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rynektransferowy;

import java.io.Serializable;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Staszek
 */
@Entity
@Table(name = "LISTATRANS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Listatrans.findAll", query = "SELECT l FROM Listatrans l")
    , @NamedQuery(name = "Listatrans.findByIdOferty", query = "SELECT l FROM Listatrans l WHERE l.idOferty = :idOferty")
    , @NamedQuery(name = "Listatrans.findByNazwisko", query = "SELECT l FROM Listatrans l WHERE l.nazwisko = :nazwisko")
    , @NamedQuery(name = "Listatrans.findByKwotaOdstepnego", query = "SELECT l FROM Listatrans l WHERE l.kwotaOdstepnego = :kwotaOdstepnego")
    , @NamedQuery(name = "Listatrans.findByTypOferty", query = "SELECT l FROM Listatrans l WHERE l.typOferty = :typOferty")
    , @NamedQuery(name = "Listatrans.filtered0", query = "SELECT l FROM Listatrans l WHERE (?1 IS NULL OR l.nazwisko = ?1) AND (?2 IS NULL OR l.idZawodnika.idKlubu.nazwa = ?2) AND (?3 IS NULL OR l.typOferty = ?3)"
            + "AND (?4 IS NULL OR l.kwotaOdstepnego <= ?4)")
    , @NamedQuery(name = "Listatrans.filtered1", query = "SELECT l FROM Listatrans l WHERE (?1 IS NULL OR l.nazwisko = ?1) AND (?2 IS NULL OR l.idZawodnika.idKlubu.nazwa = ?2) AND (?3 IS NULL OR l.typOferty = ?3)"
            + "AND (?4 IS NULL OR l.kwotaOdstepnego = ?4)")
    , @NamedQuery(name = "Listatrans.filtered2", query = "SELECT l FROM Listatrans l WHERE (?1 IS NULL OR l.nazwisko = ?1) AND (?2 IS NULL OR l.idZawodnika.idKlubu.nazwa = ?2) AND (?3 IS NULL OR l.typOferty = ?3)"
            + "AND (?4 IS NULL OR l.kwotaOdstepnego >= ?4)")
})

@SequenceGenerator(name="sek4",sequenceName="LICZLT",allocationSize = 1)
public class Listatrans implements Serializable {

    @Basic(optional = false)
    @Column(name = "TYP_OFERTY",columnDefinition="VARCHAR2(30) default 'SPRZEDAZ'")
    private String typOferty;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="sek4")
    @Basic(optional = false)
    @Column(name = "ID_OFERTY")
    private Short idOferty;
    @Column(name = "NAZWISKO")
    private String nazwisko;
    @Basic(optional = false)
    @Column(name = "KWOTA_ODSTEPNEGO")
    private int kwotaOdstepnego;
    @JoinColumn(name = "ID_ZAWODNIKA", referencedColumnName = "ID_GRACZA")
    @ManyToOne
    private Zawodnik idZawodnika;

    public Listatrans() {
    }

    public Listatrans(Short idOferty) {
        this.idOferty = idOferty;
    }

    public Listatrans(Short idOferty, int kwotaOdstepnego) {
        this.idOferty = idOferty;
        this.kwotaOdstepnego = kwotaOdstepnego;
    }

    public Short getIdOferty() {
        return idOferty;
    }

    public void setIdOferty(Short idOferty) {
        this.idOferty = idOferty;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public int getKwotaOdstepnego() {
        return kwotaOdstepnego;
    }

    public void setKwotaOdstepnego(int kwotaOdstepnego) {
        this.kwotaOdstepnego = kwotaOdstepnego;
    }

    public Zawodnik getIdZawodnika() {
        return idZawodnika;
    }

    public void setIdZawodnika(Zawodnik idZawodnika) {
        this.idZawodnika = idZawodnika;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idOferty != null ? idOferty.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Listatrans)) {
            return false;
        }
        Listatrans other = (Listatrans) object;
        if ((this.idOferty == null && other.idOferty != null) || (this.idOferty != null && !this.idOferty.equals(other.idOferty))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rynektransferowy.Listatrans[ idOferty=" + idOferty + " ]";
    }

    public String getTypOferty() {
        return typOferty;
    }

    public void setTypOferty(String typOferty) {
        this.typOferty = typOferty;
    }
    
}
