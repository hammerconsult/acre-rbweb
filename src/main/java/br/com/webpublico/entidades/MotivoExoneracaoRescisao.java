/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.rh.esocial.TipoMotivoDesligamentoESocial;
import br.com.webpublico.enums.rh.previdencia.TipoMotivoDemissaoBBPrev;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Motivos de Exonerações/Rescisões")
public class MotivoExoneracaoRescisao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Integer codigo;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Movimento CAGED")
    @ManyToOne
    private MovimentoCAGED movimentoCAGED;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Motivo de Desligamento do FGTS")
    @ManyToOne
    private MotivoDesligamentoFGTS motivoDesligamentoFGTS;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Motivo de Desligamento da RAIS")
    @ManyToOne
    private MotivoDesligamentoRAIS motivoDesligamentoRAIS;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Tipo de Saque")
    @ManyToOne
    private TipoSaque tipoSaque;
    @Tabelavel
    @Etiqueta("Alterar Vacânica dos Cargos")
    private Boolean vacancia;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Motivo da Exoneração/Rescisão E-social")
    private TipoMotivoDesligamentoESocial tipoMotivoDesligamentoESocial;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Motivo de Demissão BBPrev")
    private TipoMotivoDemissaoBBPrev tipoMotivoDemissaoBBPrev;

    public TipoMotivoDesligamentoESocial getTipoMotivoDesligamentoESocial() {
        return tipoMotivoDesligamentoESocial;
    }

    public void setTipoMotivoDesligamentoESocial(TipoMotivoDesligamentoESocial tipoMotivoDesligamentoESocial) {
        this.tipoMotivoDesligamentoESocial = tipoMotivoDesligamentoESocial;
    }

    public MotivoExoneracaoRescisao() {
        vacancia = false;
    }

    public Long getId() {
        return id;
    }

    public Boolean getVacancia() {
        return vacancia;
    }

    public void setVacancia(Boolean vacancia) {
        this.vacancia = vacancia;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public MotivoDesligamentoFGTS getMotivoDesligamentoFGTS() {
        return motivoDesligamentoFGTS;
    }

    public void setMotivoDesligamentoFGTS(MotivoDesligamentoFGTS motivoDesligamentoFGTS) {
        this.motivoDesligamentoFGTS = motivoDesligamentoFGTS;
    }

    public MotivoDesligamentoRAIS getMotivoDesligamentoRAIS() {
        return motivoDesligamentoRAIS;
    }

    public void setMotivoDesligamentoRAIS(MotivoDesligamentoRAIS motivoDesligamentoRAIS) {
        this.motivoDesligamentoRAIS = motivoDesligamentoRAIS;
    }

    public MovimentoCAGED getMovimentoCAGED() {
        return movimentoCAGED;
    }

    public void setMovimentoCAGED(MovimentoCAGED movimentoCAGED) {
        this.movimentoCAGED = movimentoCAGED;
    }

    public TipoSaque getTipoSaque() {
        return tipoSaque;
    }

    public void setTipoSaque(TipoSaque tipoSaque) {
        this.tipoSaque = tipoSaque;
    }

    public TipoMotivoDemissaoBBPrev getTipoMotivoDemissaoBBPrev() {
        return tipoMotivoDemissaoBBPrev;
    }

    public void setTipoMotivoDemissaoBBPrev(TipoMotivoDemissaoBBPrev tipoMotivoDemissaoBBPrev) {
        this.tipoMotivoDemissaoBBPrev = tipoMotivoDemissaoBBPrev;
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
        if (!(object instanceof MotivoExoneracaoRescisao)) {
            return false;
        }
        MotivoExoneracaoRescisao other = (MotivoExoneracaoRescisao) object;
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
