/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.rh.esocial.TipoRegimePrevidenciario;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author gustavo
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Tipo Previdência Folha de Pagamento")
public class TipoPrevidenciaFP implements Serializable {

    public static final Long PREVIDENCIA_GERAL = 1L;
    public static final Long RPPS = 3L;
    public static final Long ISENTO = 5L;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @OneToMany(mappedBy = "tipoPrevidenciaFP")
    private List<EventoFP> eventosFP;
    @Enumerated(EnumType.STRING)
    private TipoRegimePrevidenciario tipoRegimePrevidenciario;

    public static boolean isIsento(String tipo) {
        if(tipo == null || tipo.equals("")) return false;
        Long tipoNumber = new Long(tipo);
        return ISENTO.equals(tipoNumber);
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<EventoFP> getEventosFP() {
        return eventosFP;
    }

    public void setEventosFP(List<EventoFP> eventosFP) {
        this.eventosFP = eventosFP;
    }

    public TipoRegimePrevidenciario getTipoRegimePrevidenciario() {
        return tipoRegimePrevidenciario;
    }

    public void setTipoRegimePrevidenciario(TipoRegimePrevidenciario tipoRegimePrevidenciario) {
        this.tipoRegimePrevidenciario = tipoRegimePrevidenciario;
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
        if (!(object instanceof TipoPrevidenciaFP)) {
            return false;
        }
        TipoPrevidenciaFP other = (TipoPrevidenciaFP) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
