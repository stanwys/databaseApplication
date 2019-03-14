/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rynektransferowy;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Staszek
 */
@Entity
@Table(name = "DOKONANYTRANS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Dokonanytrans.findAll", query = "SELECT d FROM Dokonanytrans d")
    , @NamedQuery(name = "Dokonanytrans.findByIdTransakcji", query = "SELECT d FROM Dokonanytrans d WHERE d.idTransakcji = :idTransakcji")
    , @NamedQuery(name = "Dokonanytrans.findByNazwisko", query = "SELECT d FROM Dokonanytrans d WHERE d.nazwisko = :nazwisko")
    , @NamedQuery(name = "Dokonanytrans.findByKwota", query = "SELECT d FROM Dokonanytrans d WHERE d.kwota = :kwota")
    , @NamedQuery(name = "Dokonanytrans.findByDataTransferu", query = "SELECT d FROM Dokonanytrans d WHERE d.dataTransferu = :dataTransferu")
    , @NamedQuery(name = "Dokonanytrans.filtered0", query = "SELECT d FROM Dokonanytrans d WHERE (?1 IS NULL OR d.nazwisko = ?1) AND (?2 IS NULL OR d.skad.nazwa = ?2) AND (?3 IS NULL OR d.dokad.nazwa = ?3)"
            + "AND (?4 IS NULL OR d.kwota <= ?4)")  
    , @NamedQuery(name = "Dokonanytrans.filtered1", query = "SELECT d FROM Dokonanytrans d WHERE (?1 IS NULL OR d.nazwisko = ?1) AND (?2 IS NULL OR d.skad.nazwa = ?2) AND (?3 IS NULL OR d.dokad.nazwa = ?3)"
            + "AND (?4 IS NULL OR d.kwota = ?4)")
    , @NamedQuery(name = "Dokonanytrans.filtered2",query = "SELECT d FROM Dokonanytrans d WHERE (?1 IS NULL OR d.nazwisko = ?1) AND (?2 IS NULL OR d.skad.nazwa = ?2) AND (?3 IS NULL OR d.dokad.nazwa = ?3)"
            + "AND (?4 IS NULL OR d.kwota >= ?4)")    
})
    
@SequenceGenerator(name="sek5",sequenceName="LICZDT",allocationSize = 1)
public class Dokonanytrans implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="sek5")
    @Basic(optional = false)
    @Column(name = "ID_TRANSAKCJI")
    private Short idTransakcji;
    @Column(name = "NAZWISKO")
    private String nazwisko;
    @Basic(optional = false)
    @Column(name = "KWOTA")
    private int kwota;
    @Basic(optional = false)
    @Column(name = "DATA_TRANSFERU")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataTransferu;
    @JoinColumn(name = "DOKAD", referencedColumnName = "ID_KLUBU")
    @ManyToOne
    private Klub dokad;
    @JoinColumn(name = "SKAD", referencedColumnName = "ID_KLUBU")
    @ManyToOne
    private Klub skad;
    @JoinColumn(name = "ID_ZAWODNIKA", referencedColumnName = "ID_GRACZA")
    @ManyToOne
    private Zawodnik idZawodnika;

    public Dokonanytrans() {
    }

    public Dokonanytrans(Short idTransakcji) {
        this.idTransakcji = idTransakcji;
    }

    public Dokonanytrans(Short idTransakcji, int kwota, Date dataTransferu) {
        this.idTransakcji = idTransakcji;
        this.kwota = kwota;
        this.dataTransferu = dataTransferu;
    }

    public Short getIdTransakcji() {
        return idTransakcji;
    }

    public void setIdTransakcji(Short idTransakcji) {
        this.idTransakcji = idTransakcji;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public int getKwota() {
        return kwota;
    }

    public void setKwota(int kwota) {
        this.kwota = kwota;
    }

    public Date getDataTransferu() {
        return dataTransferu;
    }

    public void setDataTransferu(Date dataTransferu) {
        this.dataTransferu = dataTransferu;
    }

    public Klub getDokad() {
        return dokad;
    }

    public void setDokad(Klub dokad) {
        this.dokad = dokad;
    }

    public Klub getSkad() {
        return skad;
    }

    public void setSkad(Klub skad) {
        this.skad = skad;
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
        hash += (idTransakcji != null ? idTransakcji.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Dokonanytrans)) {
            return false;
        }
        Dokonanytrans other = (Dokonanytrans) object;
        if ((this.idTransakcji == null && other.idTransakcji != null) || (this.idTransakcji != null && !this.idTransakcji.equals(other.idTransakcji))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rynektransferowy.Dokonanytrans[ idTransakcji=" + idTransakcji + " ]";
    }
    
}
