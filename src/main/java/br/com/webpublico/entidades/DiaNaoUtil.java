/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoDiaNaoUtil;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author reidocrime
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Dias Não Úteis")
public class DiaNaoUtil implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Nome")
    private String nomeFeriado;
    @Tabelavel
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    @Etiqueta("Data do Feriado")
    private Date dataNaoUtil;
    @Tabelavel
    @Etiqueta("Válido todos os Anos")
    private Boolean validoTodosAnos;
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Etiqueta("Tipo do Feriado")
    private TipoDiaNaoUtil tipoDiaNaoUtil;

    public DiaNaoUtil() {
    }

    public DiaNaoUtil(String nomeFeriado, Date dataNaoUtil, Boolean validoTodosAnos, TipoDiaNaoUtil tipoDiaNaoUtil) {
        this.nomeFeriado = nomeFeriado;
        this.dataNaoUtil = dataNaoUtil;
        this.validoTodosAnos = validoTodosAnos;
        this.tipoDiaNaoUtil = tipoDiaNaoUtil;
    }

    public Date getDataNaoUtil() {
        return dataNaoUtil;
    }

    public void setDataNaoUtil(Date dataNaoUtil) {
        this.dataNaoUtil = dataNaoUtil;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeFeriado() {
        return nomeFeriado;
    }

    public void setNomeFeriado(String nomeFeriado) {
        this.nomeFeriado = nomeFeriado;
    }

    public Boolean isValidoTodosAnos() {
        return validoTodosAnos;
    }

    public Boolean getValidoTodosAnos() {
        return validoTodosAnos;
    }

    public void setValidoTodosAnos(Boolean validoTodosAnos) {
        this.validoTodosAnos = validoTodosAnos;
    }

    public TipoDiaNaoUtil getTipoDiaNaoUtil() {
        return tipoDiaNaoUtil;
    }

    public void setTipoDiaNaoUtil(TipoDiaNaoUtil tipoDiaNaoUtil) {
        this.tipoDiaNaoUtil = tipoDiaNaoUtil;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DiaNaoUtil)) {
            return false;
        }
        DiaNaoUtil other = (DiaNaoUtil) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nomeFeriado;
    }
}
