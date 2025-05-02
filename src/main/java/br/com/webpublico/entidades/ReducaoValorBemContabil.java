package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoReducaoValorBem;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Etiqueta("Redução Valor Bem Contábil")
public class ReducaoValorBemContabil extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Data de Lançamento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataLancamento;

    @ManyToOne
    @Etiqueta("Bem")
    private Bem bem;

    @ManyToOne
    @Etiqueta("Bens Móveis")
    private BensMoveis bensMoveis;

    @ManyToOne
    @Etiqueta("Grupo Patrimonial")
    private GrupoBem grupoPatrimonial;

    @ManyToOne
    @Etiqueta("Lote Redução")
    private LoteReducaoValorBem loteReducaoValorBem;

    @Etiqueta("Valor")
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Valor")
    private SituacaoReducaoValorBem situacao;

    public ReducaoValorBemContabil() {
        this.dataLancamento = LocalDateTime.now().toDate();
        this.situacao = SituacaoReducaoValorBem.EM_ELABORACAO;
    }

    public SituacaoReducaoValorBem getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoReducaoValorBem situacao) {
        this.situacao = situacao;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public BensMoveis getBensMoveis() {
        return bensMoveis;
    }

    public void setBensMoveis(BensMoveis bensMoveis) {
        this.bensMoveis = bensMoveis;
    }

    public LoteReducaoValorBem getLoteReducaoValorBem() {
        return loteReducaoValorBem;
    }

    public void setLoteReducaoValorBem(LoteReducaoValorBem loteReducaoValorBem) {
        this.loteReducaoValorBem = loteReducaoValorBem;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public GrupoBem getGrupoPatrimonial() {
        return grupoPatrimonial;
    }

    public void setGrupoPatrimonial(GrupoBem grupoPatrimonial) {
        this.grupoPatrimonial = grupoPatrimonial;
    }

    public boolean isContabilizado() {
        return bensMoveis != null;
    }

    public Boolean isEmElaboracao() {
        return SituacaoReducaoValorBem.EM_ELABORACAO.equals(situacao);
    }

    public Boolean isEmAndamento() {
        return SituacaoReducaoValorBem.EM_ANDAMENTO.equals(situacao);
    }
}
