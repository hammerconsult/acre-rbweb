package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 18/09/13
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Itens Progressao Automática")
public class ItemProgressaoAuto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProgressaoAuto progressaoAuto;
    @ManyToOne
    private EnquadramentoFuncional enquadramentoAntigo;
    @ManyToOne
    private EnquadramentoFuncional enquadramentoNovo;
    @ManyToOne
    private EnquadramentoFuncional enquadramentoConsidProgAut;
    @ManyToOne
    private ProvimentoFP provimentoProgressao;
    private Boolean ultimaProgressao;
    private Boolean inconsistente;
    private Boolean regularizado;
    private Integer quantidadeMesesProgressao;

    public ItemProgressaoAuto(ProgressaoAuto progressaoAuto, EnquadramentoFuncional enquadramentoAntigo, EnquadramentoFuncional enquadramentoNovo, ProvimentoFP provimentoProgressao, Boolean ultimaProgressao, Boolean inconsistente, Boolean regularizado) {
        this.progressaoAuto = progressaoAuto;
        this.enquadramentoAntigo = enquadramentoAntigo;
        this.enquadramentoNovo = enquadramentoNovo;
        this.provimentoProgressao = provimentoProgressao;
        this.ultimaProgressao = ultimaProgressao;
        this.inconsistente = inconsistente;
        this.regularizado = regularizado;
    }

    public ItemProgressaoAuto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProgressaoAuto getProgressaoAuto() {
        return progressaoAuto;
    }

    public void setProgressaoAuto(ProgressaoAuto progressaoAuto) {
        this.progressaoAuto = progressaoAuto;
    }

    public EnquadramentoFuncional getEnquadramentoAntigo() {
        return enquadramentoAntigo;
    }

    public void setEnquadramentoAntigo(EnquadramentoFuncional enquadramentoAntigo) {
        this.enquadramentoAntigo = enquadramentoAntigo;
    }

    public EnquadramentoFuncional getEnquadramentoNovo() {
        return enquadramentoNovo;
    }

    public void setEnquadramentoNovo(EnquadramentoFuncional enquadramentoNovo) {
        this.enquadramentoNovo = enquadramentoNovo;
    }

    public ProvimentoFP getProvimentoProgressao() {
        return provimentoProgressao;
    }

    public void setProvimentoProgressao(ProvimentoFP provimentoProgressao) {
        this.provimentoProgressao = provimentoProgressao;
    }

    public Boolean getUltimaProgressao() {
        return ultimaProgressao;
    }

    public void setUltimaProgressao(Boolean ultimaProgressao) {
        this.ultimaProgressao = ultimaProgressao;
    }

    public Boolean getInconsistente() {
        return inconsistente;
    }

    public void setInconsistente(Boolean inconsistente) {
        this.inconsistente = inconsistente;
    }

    public Boolean getRegularizado() {
        return regularizado;
    }

    public void setRegularizado(Boolean regularizado) {
        this.regularizado = regularizado;
    }

    public EnquadramentoFuncional getEnquadramentoConsidProgAut() {
        return enquadramentoConsidProgAut;
    }

    public void setEnquadramentoConsidProgAut(EnquadramentoFuncional enquadramentoConsidProgAut) {
        this.enquadramentoConsidProgAut = enquadramentoConsidProgAut;
    }

    public Integer getQuantidadeMesesProgressao() {
        return quantidadeMesesProgressao;
    }

    public void setQuantidadeMesesProgressao(Integer quantidadeMesesProgressao) {
        this.quantidadeMesesProgressao = quantidadeMesesProgressao;
    }

    @Override
    public String toString() {
        String retorno = "";
        if (!getInconsistente()) {
            retorno = (enquadramentoNovo == null ? " " : enquadramentoNovo.getDescricaoCompletaComServidorEValor()) + " - última ref. " + (ultimaProgressao ? "SIM" : "NÃO");
        } else {
            retorno = (enquadramentoAntigo == null ? " " : enquadramentoAntigo.getDescricaoCompletaComServidorEValor()) + " - última ref. " + (ultimaProgressao ? "SIM" : "NÃO");
        }
        return retorno;
    }
}
