package br.com.webpublico.entidades.rh.administracaodepagamento;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import br.com.webpublico.enums.rh.administracaopagamento.TipoTercoFeriasAutomatico;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Audited
@Table(name = "REPROCLANCTERCOFERIASAUT")
public class ReprocessamentoLancamentoTercoFeriasAutomatico extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer mesVerificacao;
    private Integer anoVerificacao;
    private Integer mesLancamento;
    private Integer anoLancamento;
    @Enumerated(EnumType.STRING)
    private TipoTercoFeriasAutomatico criterioBusca;
    @OneToMany(mappedBy = "reprocLancTerCoferiasAut", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemReprocessamentoLancamentoTercoFeriasAutomatico> itensReprocessamento;

    public ReprocessamentoLancamentoTercoFeriasAutomatico() {
        itensReprocessamento = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMesVerificacao() {
        return mesVerificacao;
    }

    public void setMesVerificacao(Integer mesVerificacao) {
        this.mesVerificacao = mesVerificacao;
    }

    public Integer getAnoVerificacao() {
        return anoVerificacao;
    }

    public void setAnoVerificacao(Integer anoVerificacao) {
        this.anoVerificacao = anoVerificacao;
    }

    public Integer getMesLancamento() {
        return mesLancamento;
    }

    public void setMesLancamento(Integer mesLancamento) {
        this.mesLancamento = mesLancamento;
    }

    public Integer getAnoLancamento() {
        return anoLancamento;
    }

    public void setAnoLancamento(Integer anoLancamento) {
        this.anoLancamento = anoLancamento;
    }

    public TipoTercoFeriasAutomatico getCriterioBusca() {
        return criterioBusca;
    }

    public void setCriterioBusca(TipoTercoFeriasAutomatico criterioBusca) {
        this.criterioBusca = criterioBusca;
    }

    public List<ItemReprocessamentoLancamentoTercoFeriasAutomatico> getItensReprocessamento() {
        return itensReprocessamento;
    }

    public void setItensReprocessamento(List<ItemReprocessamentoLancamentoTercoFeriasAutomatico> itensReprocessamento) {
        this.itensReprocessamento = itensReprocessamento;
    }
}
