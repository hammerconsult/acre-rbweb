package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.nfse.enums.VersaoDesif;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.UFM;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Fabio on 24/01/2017.
 */
@GrupoDiagrama(nome = "CadastroEconomico")
@Entity
@Audited
@Etiqueta("Enquadramento Fiscal")
public class EnquadramentoFiscal extends SuperEntidade implements ValidadorVigencia {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @Enumerated(EnumType.STRING)
    private TipoPorte porte;
    @Enumerated(EnumType.STRING)
    private TipoContribuinte tipoContribuinte;
    @Enumerated(EnumType.STRING)
    private RegimeTributario regimeTributario;
    @Enumerated(EnumType.STRING)
    private TipoIssqn tipoIssqn;
    @Enumerated(EnumType.STRING)
    private TipoPeriodoValorEstimado tipoPeriodoValorEstimado;
    @UFM
    private BigDecimal issEstimado;
    private Boolean substitutoTributario;
    @Enumerated(EnumType.STRING)
    private TipoNotaFiscalServico tipoNotaFiscalServico;
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    private Date fimVigencia;
    @ManyToOne
    private EscritorioContabil escritorioContabil;
    @Enumerated(EnumType.STRING)
    private RegimeEspecialTributacao regimeEspecialTributacao;
    @Enumerated(EnumType.STRING)
    private VersaoDesif versaoDesif;
    @ManyToOne
    private Banco banco;

    public EnquadramentoFiscal() {
        this.criadoEm = System.nanoTime();
        this.regimeEspecialTributacao = RegimeEspecialTributacao.PADRAO;
    }

    public EnquadramentoFiscal(CadastroEconomico cadastroEconomico) {
        super();
        this.cadastroEconomico = cadastroEconomico;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoPorte getPorte() {
        return porte;
    }

    public void setPorte(TipoPorte porte) {
        this.porte = porte;
    }

    public TipoContribuinte getTipoContribuinte() {
        return tipoContribuinte;
    }

    public void setTipoContribuinte(TipoContribuinte tipoContribuinte) {
        this.tipoContribuinte = tipoContribuinte;
    }

    public TipoIssqn getTipoIssqn() {
        return tipoIssqn;
    }

    public void setTipoIssqn(TipoIssqn tipoIssqn) {
        this.tipoIssqn = tipoIssqn;
    }

    public RegimeTributario getRegimeTributario() {
        return regimeTributario;
    }

    public void setRegimeTributario(RegimeTributario regimeTributario) {
        this.regimeTributario = regimeTributario;
    }

    public TipoNotaFiscalServico getTipoNotaFiscalServico() {
        return tipoNotaFiscalServico;
    }

    public void setTipoNotaFiscalServico(TipoNotaFiscalServico tipoNotaFiscalServico) {
        this.tipoNotaFiscalServico = tipoNotaFiscalServico;
    }

    public Boolean getSubstitutoTributario() {
        return substitutoTributario != null ? substitutoTributario : false;
    }

    public void setSubstitutoTributario(Boolean substitutoTributario) {
        this.substitutoTributario = substitutoTributario;
    }

    public EscritorioContabil getEscritorioContabil() {
        return escritorioContabil;
    }

    public void setEscritorioContabil(EscritorioContabil escritorioContabil) {
        this.escritorioContabil = escritorioContabil;
    }

    public TipoPeriodoValorEstimado getTipoPeriodoValorEstimado() {
        return tipoPeriodoValorEstimado;
    }

    public void setTipoPeriodoValorEstimado(TipoPeriodoValorEstimado tipoPeriodoValorEstimado) {
        this.tipoPeriodoValorEstimado = tipoPeriodoValorEstimado;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public BigDecimal getIssEstimado() {
        return issEstimado;
    }

    public void setIssEstimado(BigDecimal issEstimado) {
        this.issEstimado = issEstimado;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public VersaoDesif getVersaoDesif() {
        return versaoDesif;
    }

    public void setVersaoDesif(VersaoDesif versaoDesif) {
        this.versaoDesif = versaoDesif;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public boolean isMei() {
        return TipoIssqn.MEI.equals(getTipoIssqn());
    }

    public boolean isIsento() {
        return TipoIssqn.ISENTO.equals(getTipoIssqn());
    }

    public Boolean isEmitenteNotaEletronica() {
        return TipoNotaFiscalServico.ELETRONICA.equals(this.tipoNotaFiscalServico);
    }

    public boolean isInstituicaoFinanceira() {
        return regimeEspecialTributacao != null && RegimeEspecialTributacao.INSTITUICAO_FINANCEIRA.equals(this.regimeEspecialTributacao);
    }

    public RegimeEspecialTributacao getRegimeEspecialTributacao() {
        return regimeEspecialTributacao;
    }

    public void setRegimeEspecialTributacao(RegimeEspecialTributacao regimeEspecialTributacao) {
        this.regimeEspecialTributacao = regimeEspecialTributacao;
    }

    public boolean getSuperSimples() {
        return TipoIssqn.SIMPLES_NACIONAL.equals(getTipoIssqn());
    }

    public boolean getMei() {
        return TipoIssqn.MEI.equals(getTipoIssqn());
    }

}
