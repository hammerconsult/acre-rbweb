package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.SituacaoSubvencao;
import br.com.webpublico.enums.TipoPassageiro;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 18/12/13
 * Time: 18:09
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Processo de Subvenção")
public class SubvencaoProcesso implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Número do Processo")
    @Pesquisavel
    private Long numeroDoProcesso;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta("Mês de Referência")
    @Pesquisavel
    private Mes mes;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Exercício de Referência")
    @Pesquisavel
    private Exercicio exercicio;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Passageiro")
    @Enumerated(EnumType.STRING)
    private TipoPassageiro tipoPassageiro;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Lançamento")
    @Temporal(TemporalType.DATE)
    private Date dataLancamento;
    private Integer qtdeAlunosTransportados;
    private BigDecimal percentualSubvencao;
    private BigDecimal valorPassagem;
    @Tabelavel
    @Etiqueta("Situação")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private SituacaoSubvencao situacao;
    @OneToMany(mappedBy = "subvencaoProcesso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubvencaoEmpresas> subvencaoEmpresas;
    @Transient
    private Boolean bloqueiaDadosDoProcesso = false;
    private String migracaoChave;
    @Temporal(TemporalType.DATE)
    private Date dataReferenciaParametro;

    public SubvencaoProcesso() {
        subvencaoEmpresas = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoPassageiro getTipoPassageiro() {
        return tipoPassageiro;
    }

    public void setTipoPassageiro(TipoPassageiro tipoPassageiro) {
        this.tipoPassageiro = tipoPassageiro;
    }

    public Boolean getBloqueiaDadosDoProcesso() {
        return bloqueiaDadosDoProcesso;
    }

    public void setBloqueiaDadosDoProcesso(Boolean bloqueiaDadosDoProcesso) {
        this.bloqueiaDadosDoProcesso = bloqueiaDadosDoProcesso;
    }

    public List<SubvencaoEmpresas> getSubvencaoEmpresas() {
        return subvencaoEmpresas;
    }

    public void setSubvencaoEmpresas(List<SubvencaoEmpresas> subvencaoEmpresas) {
        this.subvencaoEmpresas = subvencaoEmpresas;
    }

    public Long getNumeroDoProcesso() {
        return numeroDoProcesso;
    }

    public void setNumeroDoProcesso(Long numeroDoProcesso) {
        this.numeroDoProcesso = numeroDoProcesso;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Integer getQtdeAlunosTransportados() {
        return qtdeAlunosTransportados;
    }

    public void setQtdeAlunosTransportados(Integer qtdeAlunosTransportados) {
        this.qtdeAlunosTransportados = qtdeAlunosTransportados;
    }

    public BigDecimal getPercentualSubvencao() {
        return percentualSubvencao;
    }

    public void setPercentualSubvencao(BigDecimal percentualSubvencao) {
        this.percentualSubvencao = percentualSubvencao;
    }

    public BigDecimal getValorPassagem() {
        return valorPassagem;
    }

    public void setValorPassagem(BigDecimal valorPassagem) {
        this.valorPassagem = valorPassagem;
    }

    public SituacaoSubvencao getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoSubvencao situacao) {
        this.situacao = situacao;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public Date getDataReferenciaParametro() {
        return dataReferenciaParametro;
    }

    public void setDataReferenciaParametro(Date dataReferenciaParametro) {
        this.dataReferenciaParametro = dataReferenciaParametro;
    }
}
