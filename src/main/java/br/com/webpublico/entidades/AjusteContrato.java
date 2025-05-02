package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Contratos")
@Etiqueta("Ajuste de Contrato")
public class AjusteContrato extends SuperEntidade implements PossuidorArquivo, Comparable<AjusteContrato> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número do Ajuste")
    private String numeroAjuste;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Lançamento")
    private Date dataLancamento;

    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Etiqueta("Data de Aprovação")
    private Date dataAprovacao;

    @ManyToOne
    @Etiqueta("Responsável")
    private PessoaFisica responsavel;

    @Length(maximo = 3000)
    @Etiqueta("Histórico")
    private String historico;

    @Invisivel
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoAjusteContrato situacao;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Contrato")
    private Contrato contrato;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Ajuste")
    private TipoAjusteContrato tipoAjuste;

    @Invisivel
    @NotAudited
    @OneToMany(mappedBy = "ajusteContrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AjusteContratoDadosCadastrais> ajustesDadosCadastrais;

    public AjusteContrato() {
        situacao = SituacaoAjusteContrato.EM_ELABORACAO;
        tipoAjuste = TipoAjusteContrato.CONTRATO;
    }

    public List<AjusteContratoDadosCadastrais> getAjustesDadosCadastrais() {
        return ajustesDadosCadastrais;
    }

    public void setAjustesDadosCadastrais(List<AjusteContratoDadosCadastrais> ajustesDadosCadastrais) {
        this.ajustesDadosCadastrais = ajustesDadosCadastrais;
    }

    public String getNumeroAjuste() {
        return numeroAjuste;
    }

    public void setNumeroAjuste(String numeroAjuste) {
        this.numeroAjuste = numeroAjuste;
    }

    public TipoAjusteContrato getTipoAjuste() {
        return tipoAjuste;
    }

    public void setTipoAjuste(TipoAjusteContrato tipoAjuste) {
        this.tipoAjuste = tipoAjuste;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Date getDataAprovacao() {
        return dataAprovacao;
    }

    public void setDataAprovacao(Date dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public PessoaFisica getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(PessoaFisica responsavel) {
        this.responsavel = responsavel;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public SituacaoAjusteContrato getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoAjusteContrato situacao) {
        this.situacao = situacao;
    }

    public boolean isAjusteContrato() {
        return tipoAjuste != null && tipoAjuste.isContrato();
    }

    public boolean isAjusteAditivoOrApostilamento() {
        return isAjusteAditivo() || isAjusteApostilamento();
    }

    public boolean isAjusteAditivo() {
        return tipoAjuste != null && tipoAjuste.isAditivo();
    }

    public boolean isAjusteOperacaoAditivo() {
        return tipoAjuste != null && tipoAjuste.isOperacaoAditivo();
    }

    public boolean isAjusteApostilamento() {
        return tipoAjuste != null && tipoAjuste.isApostilamento();
    }

    public boolean isAjusteDadosCadastrais(){
        return isAjusteContrato() || isAjusteAditivoOrApostilamento();
    }

    public boolean isSubstituicaoObjetoCompra() {
        return tipoAjuste != null && tipoAjuste.isSubstituicaoObjetoCompra();
    }

    public boolean isAjusteControleExecucao() {
        return  tipoAjuste != null && tipoAjuste.isControleExecucao();
    }

    public AjusteContratoDadosCadastrais getAjusteContratoDadosCadastraisDe(TipoAjusteDadosCadastrais tipo) {
        for (AjusteContratoDadosCadastrais aj : getAjustesDadosCadastrais()) {
            if (aj.getTipoAjuste().equals(tipo)) {
                return aj;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        try {
            return "" + getNumeroAjuste() + "/" + DataUtil.getDataFormatada(getDataAprovacao()) + " - " + getContrato().getContratado();
        } catch (NullPointerException ex) {
            return "";
        }
    }

    @Override
    public int compareTo(AjusteContrato o) {
        if (getDataLancamento() != null && o.getDataLancamento() != null
            && getNumeroAjuste() != null && o.getNumeroAjuste() != null) {
            return ComparisonChain.start()
                .compare(getNumeroAjuste(), o.getNumeroAjuste())
                .compare(getDataLancamento(), o.getDataLancamento())
                .result();
        }
        return 0;
    }
}
