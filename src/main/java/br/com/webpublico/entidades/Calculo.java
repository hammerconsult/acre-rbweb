package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.interfaces.GeradorReferenciaParcela;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.arrecadacao.CalculoExecutorDepoisDePagar;
import br.com.webpublico.negocios.tributario.geradoresreferencaparcela.*;
import br.com.webpublico.nfse.facades.DeclaracaoMensalServicoFacade;
import br.com.webpublico.tributario.consultadebitos.dtos.DTOConsultaDebitos;
import br.com.webpublico.tributario.consultadebitos.enums.EnumConsultaDebitos;
import br.com.webpublico.tributario.consultadebitos.enums.TipoCalculoDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Numerico;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@Etiqueta("Cálculo")
@GrupoDiagrama(nome = "Tributario")
public abstract class Calculo extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data do Cálculo")
    @Tabelavel
    private Date dataCalculo;
    private Boolean simulacao;
    @Etiqueta("Valor Real")
    @Tabelavel(campoSelecionado = false)
    @Numerico
    private BigDecimal valorReal;
    private BigDecimal valorEfetivo;
    private Boolean isento;
    @ManyToOne
    private Cadastro cadastro;
    @OneToMany(mappedBy = "calculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoPessoa> pessoas;
    private Long subDivida;
    @Enumerated(EnumType.STRING)
    private TipoCalculo tipoCalculo;
    private Boolean consistente;
    @ManyToOne
    private ProcessoCalculo processoCalculo;
    private String referencia;
    //Informações Adicionais
    private String observacao;
    private boolean isentaAcrescimos;
    private Boolean geradoPortalContribuinte;

    public Calculo() {
        valorEfetivo = BigDecimal.ZERO;
        valorReal = BigDecimal.ZERO;
        isento = Boolean.FALSE;
        simulacao = Boolean.FALSE;
        pessoas = Lists.newArrayList();
        consistente = Boolean.TRUE;
        subDivida = Long.valueOf("1");
        dataCalculo = new Date();
        geradoPortalContribuinte = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoCalculo getTipoCalculo() {
        return tipoCalculo;
    }

    public void setTipoCalculo(TipoCalculo tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getDataCalculo() {
        return dataCalculo;
    }

    public void setDataCalculo(Date dataCalculo) {
        this.dataCalculo = dataCalculo;
    }

    public Boolean getSimulacao() {
        return simulacao;
    }

    public void setSimulacao(Boolean simulacao) {
        this.simulacao = simulacao;
    }

    public BigDecimal getValorEfetivo() {
        return valorEfetivo != null ? valorEfetivo.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorEfetivo(BigDecimal valorEfetivo) {
        this.valorEfetivo = valorEfetivo;
    }

    public BigDecimal getValorReal() {
        return valorReal;
    }

    public void setValorReal(BigDecimal valorReal) {
        this.valorReal = valorReal;
    }

    public boolean isInadimplente() {
        //verificar se existe algum débito (confirmar se débito apenas em dívida ativa)
        return false;
    }

    public Boolean getIsento() {
        return isento == null ? false : isento;
    }

    public void setIsento(Boolean isento) {
        this.isento = isento;
    }

    public List<CalculoPessoa> getPessoas() {
        return pessoas;
    }

    public void setPessoas(List<CalculoPessoa> pessoas) {
        this.pessoas = pessoas;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public Long getSubDivida() {
        return subDivida;
    }

    public void setSubDivida(Long subDivida) {
        this.subDivida = subDivida;
    }

    public Boolean getConsistente() {
        return consistente;
    }

    public void setConsistente(Boolean consistente) {
        this.consistente = consistente;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Boolean getGeradoPortalContribuinte() {
        return geradoPortalContribuinte;
    }

    public void setGeradoPortalContribuinte(Boolean geradoPortalContribuinte) {
        this.geradoPortalContribuinte = geradoPortalContribuinte;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.Calculo[ id=" + id + " ]";
    }

    public ProcessoCalculo getProcessoCalculo() {
        return processoCalculo;
    }

    public void setProcessoCalculo(ProcessoCalculo processoCalculo) {
        this.processoCalculo = processoCalculo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public boolean isIsentaAcrescimos() {
        return isentaAcrescimos;
    }

    public void setIsentaAcrescimos(boolean isentaAcrescimos) {
        this.isentaAcrescimos = isentaAcrescimos;
    }

    public Mes getMesReferencia() {
        Integer mes = null;
        if (this instanceof CalculoISS) {
            mes = ((CalculoISS) this).getProcessoCalculoISS().getMesReferencia();
        } else if (this instanceof CalculoLancamentoOutorga) {
            return ((CalculoLancamentoOutorga) this).getProcCalcLancamentoOutorga().getLancamentoOutorga().getMes();
        } else if (this instanceof CalculoMultaAcessoria) {
            mes = ((CalculoMultaAcessoria) this).getProcessoCalculoMultaAcessoria().getCalculoISS().getProcessoCalculoISS().getMesReferencia();
        } else if (this instanceof CalculoNfse) {
            mes = ((CalculoNfse) this).getMesDeReferencia();
        }
        if (mes != null) {
            return Mes.getMesToInt(mes);
        }
        return null;
    }

    public static enum TipoCalculo implements EnumConsultaDebitos, EnumComDescricao {

        ALVARA_FUNCIONAMENTO(TipoCalculoDTO.ALVARA_FUNCIONAMENTO, GeraReferenciaAlvara.class, 1, "Alvará de Funcionamento", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        ALVARA_LOCALIZACAO(TipoCalculoDTO.ALVARA_LOCALIZACAO, GeraReferenciaAlvara.class, 1, "Alvará de Localização", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        ALVARA_SANITARIO(TipoCalculoDTO.ALVARA_SANITARIO, GeraReferenciaAlvara.class, 1, "Alvará de Sanitário", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        CEASA(TipoCalculoDTO.CEASA, GeraReferenciaCeasa.class, 1, "Contrato de Ceasa", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        DIVIDA_DIVERSA(TipoCalculoDTO.DIVIDA_DIVERSA, GeraReferenciaDividaDiversa.class, 1, "Dividas Diversas", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        DOCTO_OFICIAL(TipoCalculoDTO.DOCTO_OFICIAL, GeraReferenciaCertidao.class, 1, "Documentos Oficiais", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        FISCALIZACAO(TipoCalculoDTO.FISCALIZACAO, GeraReferenciaFiscalizacaoISS.class, 1, "Fiscalização Tributária", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        IPTU(TipoCalculoDTO.IPTU, GeraReferenciaIPTU.class, 1, "Cálculo de I.P.T.U.", DescontoItemParcela.Origem.IPTU, CalculoFacade.class),
        ISS(TipoCalculoDTO.ISS, GeraReferenciaISS.class, 1, "Declaração de I.S.S.Q.N.", DescontoItemParcela.Origem.OUTRO, DeclaracaoMensalServicoFacade.class),
        ITBI(TipoCalculoDTO.ITBI, GeraReferenciaITBI.class, 1, "Cálculo de I.T.B.I.", DescontoItemParcela.Origem.OUTRO, CalculoITBIFacade.class),
        LANCAMENTO_OUTORGA(TipoCalculoDTO.LANCAMENTO_OUTORGA, GeraReferenciaOutorga.class, 1, "Lançamento de Outorga", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        LICITACAO_CEASA(TipoCalculoDTO.LICITACAO_CEASA, GeraReferenciaCeasa.class, 1, "Licitação do CEASA", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        MULTA_FISCALIZACAO(TipoCalculoDTO.MULTA_FISCALIZACAO, GeraReferenciaFiscalizacaoISS.class, 1, "Multa de Fiscalização Tributária", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        PROC_FISCALIZACAO(TipoCalculoDTO.PROC_FISCALIZACAO, GeraReferenciaFiscalizacaoSecretarias.class, 1, "Fiscalização de Secretarias", DescontoItemParcela.Origem.OUTRO, ProcessoFiscalizacaoFacade.class),
        RENDAS(TipoCalculoDTO.RENDAS, GeraReferenciaRendas.class, 1, "Contrato de Rendas Patrimoniais", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        TAXAS_DIVERSAS(TipoCalculoDTO.TAXAS_DIVERSAS, GeraReferenciaTaxaDiversa.class, 1, "Lançamento de Taxas Diversas", DescontoItemParcela.Origem.OUTRO, CalculoTaxasDiversasFacade.class),
        INSCRICAO_DA(TipoCalculoDTO.INSCRICAO_DA, GeraReferenciaDividaAtiva.class, 2, "Inscrição em D.A.", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        NFS_AVULSA(TipoCalculoDTO.NFS_AVULSA, GeraReferenciaNFSAvulsa.class, 1, "Lançamento de Nota Fiscal Avulsa", DescontoItemParcela.Origem.OUTRO, NFSAvulsaFacade.class),
        PARCELAMENTO(TipoCalculoDTO.PARCELAMENTO, GeraReferenciaParcelamento.class, 3, "Parcelamento", DescontoItemParcela.Origem.PARCELAMENTO, ProcessoParcelamentoFacade.class),
        MULTA_ACESSORIA(TipoCalculoDTO.MULTA_ACESSORIA, GeraReferenciaMultaAcessoria.class, 1, "Multa de Declaração de I.S.S.Q.N.", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        NFSE(TipoCalculoDTO.NFSE, GeraReferenciaNFSE.class, 1, "Nota Fiscal Eletrônica", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        FISCALIZACAO_RBTRANS(TipoCalculoDTO.FISCALIZACAO_RBTRANS, GeraReferenciaFiscalizacaoRBTrans.class, 1, "Fiscalização de RBTrans", DescontoItemParcela.Origem.OUTRO, FiscalizacaoRBTransFacade.class),
        RB_TRANS(TipoCalculoDTO.RB_TRANS, GeraReferenciaRBTrans.class, 1, "Transito e Transporte", DescontoItemParcela.Origem.OUTRO, PermissaoTransporteFacade.class),
        AGRUPADO(TipoCalculoDTO.AGRUPADO, GeraReferenciaIPTU.class, 1, "Agrupado", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        INCONSISTENCIA(TipoCalculoDTO.INCONSISTENCIA, GeraReferenciaInconsistencia.class, 1, "Inconsistências", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        CANCELAMENTO_PARCELAMENTO(TipoCalculoDTO.CANCELAMENTO_PARCELAMENTO, GeraReferenciaCancelamentoParcelamento.class, 1, "Cancelamento de Parcelamento", DescontoItemParcela.Origem.PARCELAMENTO, CalculoFacade.class),
        PROCESSO_COMPENSACAO(TipoCalculoDTO.PROCESSO_COMPENSACAO, GeraReferenciaCompensacao.class, 1, "Processo de Compensação", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        CONTRIBUICAO_MELHORIA(TipoCalculoDTO.CONTRIBUICAO_MELHORIA, GeraReferenciaContribuicaoMelhoria.class, 1, "Contribuição de Melhoria", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        SUBVENCAO(TipoCalculoDTO.SUBVENCAO, GeraReferenciaSubvencao.class, 1, "Subvenção", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        PAGAMENTO_SUBVENCAO(TipoCalculoDTO.PAGAMENTO_SUBVENCAO, GeraReferenciaSubvencao.class, 1, "Pagamento por Subvenção", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        CONTA_CORRENTE(TipoCalculoDTO.CONTA_CORRENTE, GeraReferenciaContaCorrente.class, 1, "Conta Corrente", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        COMPENSACAO(TipoCalculoDTO.COMPENSACAO, GeraReferenciaProcessoCompensacao.class, 1, "Compensacao", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        ALVARA(TipoCalculoDTO.ALVARA, GeraReferenciaNovoAlvara.class, 1, "Alvará de Localização e Funcionamento", DescontoItemParcela.Origem.OUTRO, CalculoAlvaraFacade.class),
        BLOQUEIO_JUDICIAL(TipoCalculoDTO.BLOQUEIO_JUDICIAL, GeraReferenciaBloqueioJudicial.class, 1, "Bloqueio Judicial", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        ALVARA_CONSTRUCAO_HABITESE(TipoCalculoDTO.ALVARA_CONSTRUCAO_HABITESE, GeraReferenciaAlvaraConstrucaoHabitese.class, 1, "Alvará de Construção e Habite-se", DescontoItemParcela.Origem.OUTRO, AlvaraConstrucaoFacade.class),
        CONTRATO_CONCESSAO(TipoCalculoDTO.CONTRATO_CONCESSAO, GeraReferenciaContrato.class, 1, "Contrato de Concessão", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        SOLICITACAO_CADASTRO_CREDOR(TipoCalculoDTO.SOLICITACAO_CADASTRO_CREDOR, GeraReferenciaSolicitacaoCadastroCredor.class, 1, "Solicitação de Cadastro de Credor", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class),
        LICENCIAMENTO_AMBIENTAL(TipoCalculoDTO.LICENCIAMENTO_AMBIENTAL, GeraReferenciaLicenciamentoAmbiental.class, 1, "Licenciamento Ambiental", DescontoItemParcela.Origem.OUTRO, CalculoFacade.class);

        private TipoCalculoDTO dto;
        private Class<GeradorReferenciaParcela> geradorReferenciaParcela;
        private Integer ordemApresentacao;
        private String descricao;
        private DescontoItemParcela.Origem origem;
        private Class<? extends CalculoExecutorDepoisDePagar> executorDepoisDePagar;

        private TipoCalculo(TipoCalculoDTO dto, Class geradorReferenciaParcela,
                            Integer ordemApresentacao, String descricao,
                            DescontoItemParcela.Origem origem, Class<? extends CalculoExecutorDepoisDePagar> executorDepoisDePagar) {
            this.dto = dto;
            this.geradorReferenciaParcela = geradorReferenciaParcela;
            this.ordemApresentacao = ordemApresentacao;
            this.descricao = descricao;
            this.origem = origem;
            this.executorDepoisDePagar = executorDepoisDePagar;
        }

        public boolean isDependenteDeOutroValorDivida() {
            if (this.equals(PROCESSO_COMPENSACAO)
                || this.equals(CANCELAMENTO_PARCELAMENTO)) {
                return true;
            }
            return false;
        }

        public Class<? extends CalculoExecutorDepoisDePagar> getExecutorDepoisDePagar() {
            return executorDepoisDePagar;
        }

        public boolean isCancelamentoParcelamento() {
            return this.equals(CANCELAMENTO_PARCELAMENTO);
        }

        public boolean isContaCorrente() {
            return this.equals(CONTA_CORRENTE);
        }

        public void geraReferencia(SituacaoParcelaValorDivida spvd) throws IllegalAccessException, InstantiationException {
            getGeradorReferenciaParcela().geraReferencia(spvd);
        }

        public GeradorReferenciaParcela getGeradorReferenciaParcela() throws InstantiationException, IllegalAccessException {
            return GerenciadorDeInstancias.getINSTANCE().getReferencia(geradorReferenciaParcela);
        }

        public Integer getOrdemApresentacao() {
            return ordemApresentacao;
        }

        public String getDescricao() {
            return descricao;
        }

        public boolean isInscricaoDividaAtiva() {
            return this.equals(INSCRICAO_DA);
        }


        public static TipoCalculo fromDto(TipoCalculoDTO dto) {
            for (TipoCalculo value : values()) {
                if (value.toDto().equals(dto)) {
                    return value;
                }
            }
            return null;
        }

        @Override
        public DTOConsultaDebitos toDto() {
            return dto;
        }

        public boolean equals(TipoCalculoDTO dto) {
            return this.equals(TipoCalculo.fromDto(dto));
        }


        public DescontoItemParcela.Origem getOrigem() {
            return origem;
        }
    }
}
