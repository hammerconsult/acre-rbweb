/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoDesdobramento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Contábil")
@Audited
@Entity
@Etiqueta("Configuração Contábil")
/*
 *
 * Agrega o conjunto de parâmetros utilizados pelo módulo Contábil. A cada mudança nos valores
 * dos parâmetros deverá ser gerado um novo registro com a data correspondente (desde) e o vínculo
 * com a configuração anterior.
 *
 */
public class ConfiguracaoContabil extends ConfiguracaoModulo implements Serializable {

    private static Integer TAMANHO_LOTE_REPROCESSAMENTO_CONTABIL = 100;
    /**
     * Atributo utilizado para definir o formato do código das Unidades Organiacionais
     * <p/>
     * Exemplo de máscara que permite 5 níveis: 999.999.9.9.999
     */
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Máscara da Unidade Organizacional")
    private String mascaraUnidadeOrganizacional;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Filtro de Despesa Corrente")
    private String filtroDespesaCorrente;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Filtro de Despesa Capital")
    private String filtroDespesaCapital;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Tipo de Desdobramento")
    @Enumerated(EnumType.STRING)
    private TipoDesdobramento tipoDesdobramento;
    @Etiqueta("Pessoa Integração Tributário/Contábil Dívida Ativa")
    @ManyToOne
    private Pessoa pessoaTribContDividaAtiv;
    @Obrigatorio
    @Etiqueta("Classe Credor Integração Tributário/Contábil Dívida Ativa")
    @ManyToOne
    private ClasseCredor classeTribContDividaAtiv;
    @Etiqueta("Pessoa Integração Tributário/Contábil Créditos a Receber")
    @ManyToOne
    private Pessoa pessoaTribContCreditoRec;
    @Obrigatorio
    @Etiqueta("Classe Credor Integração Tributário/Contábil Créditos a Receber")
    @ManyToOne
    private ClasseCredor classeTribContCreditoRec;
    @Etiqueta("Pessoa Integração Tributário/Contábil Receita Realizada")
    @ManyToOne
    private Pessoa pessoaTribContReceitaRea;
    @Obrigatorio
    @Etiqueta("Classe Credor Integração Tributário/Contábil Receita Realizada")
    @ManyToOne
    private ClasseCredor classeTribContReceitaRea;
    @Etiqueta("Classe Credor Integração Tributário/Contábil Restituição")
    @ManyToOne
    private ClasseCredor classeTribContRestituicao;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta Contábil Resultado Diminutivo do Exercício")
    private ContaContabil contaContabilResultadoDimin;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta Contábil Resultado Aumentativo do Exercício")
    private ContaContabil contaContabilResultadoAumen;

    @Etiqueta("Recuperar o Evento do Empenho com valor diferente de Obrigação a Pagar?")
    private Boolean buscarEventoEmpDiferenteObrig;

    @OneToMany(mappedBy = "configuracaoContabil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConfiguracaoContabilContaContabil> contasContabeisTransferencia;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "configuracaoContabil")
    private List<ConfiguracaoContabilContaReceita> contasReceita;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "configuracaoContabil")
    private List<ConfiguracaoContabilContaDespesa> contasDespesa;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "configuracaoContabil")
    private List<ConfiguracaoContabilFonte> fontesTesouro;

    private Boolean gerarContaAuxiliarSiconfi;

    @Etiqueta("Tamanho do lote do reprocessamento contábil")
    @Obrigatorio
    private Integer tamanhoLoteReprocessamentoContabil;

    @Obrigatorio
    @Etiqueta("Código do Cabeçalho do arquivo da MSC")
    private String codigoCabecalhoArquivoMSC;

    @Etiqueta("Quantidade de Diárias de Campo")
    @Obrigatorio
    private Integer quantidadeDiariasDeCampo;
    @Etiqueta("Dias Corridos da Primeira Notificação")
    @Obrigatorio
    private Integer diasPrimeiraNotificacao;
    @Etiqueta("Dias Úteis da Segunda Notificação")
    @Obrigatorio
    private Integer diasSegundaNotificacao;

    @Etiqueta("Bloquear para ter apenas um desdobramento na Liquidação e Obrigação a Pagar?")
    private Boolean bloquearUmDesdobramento;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "configuracaoContabil")
    private List<ConfiguracaoContabilUsuario> usuariosGestores;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "configuracaoContabil")
    private List<ConfiguracaoContabilContaReinf> contasReinf;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "configuracaoContabil")
    private List<ConfiguracaoContabilUnidadeDocumentoBloqueado> unidadesDocumentosBloqueados;

    @Etiqueta("Bloquear para ter apenas um desdobramento na Liquidação e Obrigação a Pagar?")
    private Boolean bloquearSaldoNegativoBemMovel;

    @Etiqueta("Bloquear para ter apenas um desdobramento na Liquidação e Obrigação a Pagar?")
    private Boolean bloquearSaldoNegativoBemImovel;

    @Column(name = "BLOQUEARSALDONEGBEMINTANGIVEL")
    @Etiqueta("Bloquear saldo negativo para Bem Intangível?")
    private Boolean bloquearSaldoNegativoBemIntangivel;

    @Column(name = "BLOQUEARSALDONEGATBEMESTOQUE")
    @Etiqueta("Bloquear saldo negativo para Bem de Estoque?")
    private Boolean bloquearSaldoNegativoBemEstoque;
    @Etiqueta("Porcentagem mínima calculo base documento comprobatorio liquidação.")
    private BigDecimal porcentagemMinimaCalculoBase;
    @Etiqueta("Contabilizar utilizando micro-service?")
    private Boolean contabilizarMicroService;
    @Column(name = "SALDOCREDITORECEBERMICROSERVIC")
    @Etiqueta("Gerar saldo crédito a receber utilizando micro-service?")
    private Boolean saldoCreditoReceberMicroService;
    @Column(name = "SALDODIVIDAATIVACONTABILMICROS")
    @Etiqueta("Gerar saldo dívida ativa utilizando micro-service?")
    private Boolean saldoDividaAtivaContabilMicroService;
    @Etiqueta("Gerar saldo dívida pública utilizando micro-service?")
    private Boolean saldoDividaPublicaMicroService;
    @Column(name = "SALDOEXTRAORCMICROSERVICE")
    @Etiqueta("Gerar saldo extraorçamentário utilizando micro-service?")
    private Boolean saldoExtraorcamentarioMicroService;
    @Column(name = "SALDOFONTEDESPESAORCMICROS")
    @Etiqueta("Gerar saldo fonte despesa orçamentária utilizando micro-service?")
    private Boolean saldoFonteDespesaOrcMicroService;
    @Column(name = "SALDOGRUPOBEMIMOVEISMICROS")
    @Etiqueta("Gerar saldo bens imóveis utilizando micro-service?")
    private Boolean saldoGrupoBemImoveisMicroService;
    @Column(name = "SALDOGRUPOBEMINTANGIVEISMICROS")
    @Etiqueta("Gerar saldo bens intangíveis utilizando micro-service?")
    private Boolean saldoGrupoBemIntangiveisMicroService;
    @Etiqueta("Gerar saldo bens móveis utilizando micro-service?")
    private Boolean saldoGrupoBemMicroService;
    @Etiqueta("Gerar saldo grupo material utilizando micro-service?")
    private Boolean saldoGrupoMaterialMicroService;
    @Etiqueta("Gerar saldo conta financeira utilizando micro-service?")
    private Boolean saldoSubContaMicroService;
    @ManyToOne
    private Conta contaExtraInssPadraoDocLiq;
    @ManyToOne
    private Conta contaExtraIrrfPadraoDocLiq;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "configuracaoContabil")
    private List<ConfiguracaoContabilTipoContaDespesaReinf> tiposContasDespesasReinf;
    @ManyToOne
    private Pessoa pessoaInssPadraoDocLiq;
    @ManyToOne
    private ClasseCredor classeCredInssPadraoDocLiq;
    @ManyToOne
    private Pessoa pessoaIrrfPadraoDocLiq;
    @ManyToOne
    private ClasseCredor classeCredIrrfPadraoDocLiq;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "configuracaoContabil")
    private List<ConfiguracaoContabilArquivoLayout> arquivosLayouts;
    @Etiqueta("Dotação Padrão do Reconhecimento de Dívida de Exercícios Anteriores.")
    private String dotacaoPadraoReconhecimentoDiv;

    @Column(name = "PERMITIRTIPOLANCAMENTOMANUAL")
    @Etiqueta("Permitir selecionar tipo de contabilização no Lancamento Manual?")
    private Boolean permitirTipoLancamentoManual;

    public ConfiguracaoContabil() {
        contasDespesa = Lists.newArrayList();
        contasReceita = Lists.newArrayList();
        fontesTesouro = Lists.newArrayList();
        buscarEventoEmpDiferenteObrig = false;
        permitirTipoLancamentoManual = false;
        gerarContaAuxiliarSiconfi = false;
        usuariosGestores = Lists.newArrayList();
        contasReinf = Lists.newArrayList();
        unidadesDocumentosBloqueados = Lists.newArrayList();
        tiposContasDespesasReinf = Lists.newArrayList();
        arquivosLayouts = Lists.newArrayList();
    }

    public ConfiguracaoContabil(Date desde, String mascaraUnidadeOrganizacional, String filtroDespesaCorrente, String filtroDespesaCapital) {
        super(desde);
        this.mascaraUnidadeOrganizacional = mascaraUnidadeOrganizacional;
        this.filtroDespesaCapital = filtroDespesaCapital;
        this.filtroDespesaCorrente = filtroDespesaCorrente;
        contasDespesa = Lists.newArrayList();
        contasReceita = Lists.newArrayList();
        fontesTesouro = Lists.newArrayList();
        buscarEventoEmpDiferenteObrig = false;
        gerarContaAuxiliarSiconfi = false;
        usuariosGestores = Lists.newArrayList();
        tiposContasDespesasReinf = Lists.newArrayList();
        arquivosLayouts = Lists.newArrayList();
    }

    public String getMascaraUnidadeOrganizacional() {
        return mascaraUnidadeOrganizacional;
    }

    public void setMascaraUnidadeOrganizacional(String mascaraUnidadeOrganizacional) {
        this.mascaraUnidadeOrganizacional = mascaraUnidadeOrganizacional;
    }

    public String getFiltroDespesaCapital() {
        return filtroDespesaCapital;
    }

    public void setFiltroDespesaCapital(String filtroDespesaCapital) {
        this.filtroDespesaCapital = filtroDespesaCapital;
    }

    public String getFiltroDespesaCorrente() {
        return filtroDespesaCorrente;
    }

    public void setFiltroDespesaCorrente(String filtroDespesaCorrente) {
        this.filtroDespesaCorrente = filtroDespesaCorrente;
    }

    public TipoDesdobramento getTipoDesdobramento() {
        return tipoDesdobramento;
    }

    public void setTipoDesdobramento(TipoDesdobramento tipoDesdobramento) {
        this.tipoDesdobramento = tipoDesdobramento;
    }

    public Pessoa getPessoaTribContDividaAtiv() {
        return pessoaTribContDividaAtiv;
    }

    public void setPessoaTribContDividaAtiv(Pessoa pessoaTribContDividaAtiv) {
        this.pessoaTribContDividaAtiv = pessoaTribContDividaAtiv;
    }

    public ClasseCredor getClasseTribContDividaAtiv() {
        return classeTribContDividaAtiv;
    }

    public void setClasseTribContDividaAtiv(ClasseCredor classeTribContDividaAtiv) {
        this.classeTribContDividaAtiv = classeTribContDividaAtiv;
    }

    public Pessoa getPessoaTribContCreditoRec() {
        return pessoaTribContCreditoRec;
    }

    public void setPessoaTribContCreditoRec(Pessoa pessoaTribContCreditoRec) {
        this.pessoaTribContCreditoRec = pessoaTribContCreditoRec;
    }

    public ClasseCredor getClasseTribContCreditoRec() {
        return classeTribContCreditoRec;
    }

    public void setClasseTribContCreditoRec(ClasseCredor classeTribContCreditoRec) {
        this.classeTribContCreditoRec = classeTribContCreditoRec;
    }

    public Pessoa getPessoaTribContReceitaRea() {
        return pessoaTribContReceitaRea;
    }

    public void setPessoaTribContReceitaRea(Pessoa pessoaTribContReceitaRea) {
        this.pessoaTribContReceitaRea = pessoaTribContReceitaRea;
    }

    public ClasseCredor getClasseTribContReceitaRea() {
        return classeTribContReceitaRea;
    }

    public void setClasseTribContReceitaRea(ClasseCredor classeTribContReceitaRea) {
        this.classeTribContReceitaRea = classeTribContReceitaRea;
    }

    public List<ConfiguracaoContabilContaReceita> getContasReceita() {
        return contasReceita;
    }

    public void setContasReceita(List<ConfiguracaoContabilContaReceita> contasReceita) {
        this.contasReceita = contasReceita;
    }

    public List<ConfiguracaoContabilContaDespesa> getContasDespesa() {
        return contasDespesa;
    }

    public void setContasDespesa(List<ConfiguracaoContabilContaDespesa> contasDespesa) {
        this.contasDespesa = contasDespesa;
    }

    public List<ConfiguracaoContabilContaContabil> getContasContabeisTransferencia() {
        return contasContabeisTransferencia;
    }

    public void setContasContabeisTransferencia(List<ConfiguracaoContabilContaContabil> contasContabeisTransferencia) {
        this.contasContabeisTransferencia = contasContabeisTransferencia;
    }

    public List<ConfiguracaoContabilContaContabil> getContasContabeisTransferenciaAjustes() {
        List<ConfiguracaoContabilContaContabil> contasContabeisTransferenciaAjustes = Lists.newArrayList();
        for (ConfiguracaoContabilContaContabil configuracaoContabilContaContabil : contasContabeisTransferencia) {
            if (configuracaoContabilContaContabil.isAjuste()) {
                Util.adicionarObjetoEmLista(contasContabeisTransferenciaAjustes, configuracaoContabilContaContabil);
            }
        }
        return contasContabeisTransferenciaAjustes;
    }

    public List<ConfiguracaoContabilContaContabil> getContasContabeisTransferenciaResultado() {
        List<ConfiguracaoContabilContaContabil> contasContabeisTransferenciaResultado = Lists.newArrayList();
        for (ConfiguracaoContabilContaContabil configuracaoContabilContaContabil : contasContabeisTransferencia) {
            if (configuracaoContabilContaContabil.isResultado()) {
                Util.adicionarObjetoEmLista(contasContabeisTransferenciaResultado, configuracaoContabilContaContabil);
            }
        }
        return contasContabeisTransferenciaResultado;
    }

    public List<Conta> getContasContabeisTransferenciaAjustesPublico() {
        return buscarContasContabeisPublicos(getContasContabeisTransferenciaAjustes());
    }

    public List<Conta> getContasContabeisTransferenciaAjustesPrivado() {
        return buscarContasContabeisPrivados(getContasContabeisTransferenciaAjustes());
    }

    public List<Conta> getContasContabeisTransferenciaResultadoPublico() {
        return buscarContasContabeisPublicos(getContasContabeisTransferenciaResultado());
    }

    public List<Conta> getContasContabeisTransferenciaResultadoPrivado() {
        return buscarContasContabeisPrivados(getContasContabeisTransferenciaResultado());
    }

    public Boolean getBloquearUmDesdobramento() {
        return bloquearUmDesdobramento != null ? bloquearUmDesdobramento : Boolean.FALSE;
    }

    public void setBloquearUmDesdobramento(Boolean bloquearUmDesdobramento) {
        this.bloquearUmDesdobramento = bloquearUmDesdobramento;
    }

    private List<Conta> buscarContasContabeisPublicos(List<ConfiguracaoContabilContaContabil> contasContabeis) {
        List<Conta> contasPublico = Lists.newArrayList();
        for (ConfiguracaoContabilContaContabil configuracaoContabilContaContabil : contasContabeis) {
            if (configuracaoContabilContaContabil.getPatrimonioLiquido().isPublico()) {
                Util.adicionarObjetoEmLista(contasPublico, configuracaoContabilContaContabil.getContaContabil());
            }
        }
        return contasPublico;
    }

    private List<Conta> buscarContasContabeisPrivados(List<ConfiguracaoContabilContaContabil> contasContabeis) {
        List<Conta> contasPrivado = Lists.newArrayList();
        for (ConfiguracaoContabilContaContabil configuracaoContabilContaContabil : contasContabeis) {
            if (configuracaoContabilContaContabil.getPatrimonioLiquido().isPrivado()) {
                Util.adicionarObjetoEmLista(contasPrivado, configuracaoContabilContaContabil.getContaContabil());
            }
        }
        return contasPrivado;
    }

    public ContaContabil getContaContabilResultadoDimin() {
        return contaContabilResultadoDimin;
    }

    public void setContaContabilResultadoDimin(ContaContabil contaContabilResultadoDimin) {
        this.contaContabilResultadoDimin = contaContabilResultadoDimin;
    }

    public ContaContabil getContaContabilResultadoAumen() {
        return contaContabilResultadoAumen;
    }

    public void setContaContabilResultadoAumen(ContaContabil contaContabilResultadoAumen) {
        this.contaContabilResultadoAumen = contaContabilResultadoAumen;
    }

    public Boolean getBuscarEventoEmpDiferenteObrig() {
        return buscarEventoEmpDiferenteObrig;
    }

    public void setBuscarEventoEmpDiferenteObrig(Boolean buscarEventoEmpDifereteObrig) {
        this.buscarEventoEmpDiferenteObrig = buscarEventoEmpDifereteObrig;
    }

    public Boolean getGerarContaAuxiliarSiconfi() {
        return gerarContaAuxiliarSiconfi;
    }

    public void setGerarContaAuxiliarSiconfi(Boolean gerarContaAuxiliarSiconfi) {
        this.gerarContaAuxiliarSiconfi = gerarContaAuxiliarSiconfi;
    }

    public String getCodigoCabecalhoArquivoMSC() {
        return codigoCabecalhoArquivoMSC;
    }

    public void setCodigoCabecalhoArquivoMSC(String codigoCabecalhoArquivoMSC) {
        this.codigoCabecalhoArquivoMSC = codigoCabecalhoArquivoMSC;
    }

    public Integer getQuantidadeDiariasDeCampo() {
        return quantidadeDiariasDeCampo == null ? 0 : quantidadeDiariasDeCampo;
    }

    public void setQuantidadeDiariasDeCampo(Integer quantidadeDiariasDeCampo) {
        this.quantidadeDiariasDeCampo = quantidadeDiariasDeCampo;
    }

    public Integer getDiasPrimeiraNotificacao() {
        return diasPrimeiraNotificacao;
    }

    public void setDiasPrimeiraNotificacao(Integer diasPrimeiraNotificacao) {
        this.diasPrimeiraNotificacao = diasPrimeiraNotificacao;
    }

    public Integer getDiasSegundaNotificacao() {
        return diasSegundaNotificacao;
    }

    public void setDiasSegundaNotificacao(Integer diasSegundaNotificacao) {
        this.diasSegundaNotificacao = diasSegundaNotificacao;
    }

    public List<ConfiguracaoContabilUsuario> getUsuariosGestores() {
        return usuariosGestores;
    }

    public void setUsuariosGestores(List<ConfiguracaoContabilUsuario> usuariosGestores) {
        this.usuariosGestores = usuariosGestores;
    }

    public ClasseCredor getClasseTribContRestituicao() {
        return classeTribContRestituicao;
    }

    public void setClasseTribContRestituicao(ClasseCredor classeTribContRestituicao) {
        this.classeTribContRestituicao = classeTribContRestituicao;
    }

    public List<ConfiguracaoContabilUsuario> getUsuariosGestoresContabil() {
        List<ConfiguracaoContabilUsuario> gestoresContabil = Lists.newArrayList();
        for (ConfiguracaoContabilUsuario usuarioGestor : usuariosGestores) {
            if (usuarioGestor.getTipoUsuarioGestor().isGestorContabil()) {
                Util.adicionarObjetoEmLista(gestoresContabil, usuarioGestor);
            }
        }
        return gestoresContabil;
    }

    public List<ConfiguracaoContabilUsuario> getUsuariosGestoresControladoria() {
        List<ConfiguracaoContabilUsuario> gestoresControladoria = Lists.newArrayList();
        for (ConfiguracaoContabilUsuario usuarioGestor : usuariosGestores) {
            if (usuarioGestor.getTipoUsuarioGestor().isGestorControladoria()) {
                Util.adicionarObjetoEmLista(gestoresControladoria, usuarioGestor);
            }
        }
        return gestoresControladoria;
    }

    public List<ConfiguracaoContabilContaReinf> getContasReinf() {
        return contasReinf;
    }

    public void setContasReinf(List<ConfiguracaoContabilContaReinf> contasReinf) {
        this.contasReinf = contasReinf;
    }

    public List<Conta> getContasReinfAsContaExtra() {
        List<Conta> retorno = Lists.newArrayList();
        for (ConfiguracaoContabilContaReinf configuracaoContabilContaReinf : getContasReinf()) {
            retorno.add(configuracaoContabilContaReinf.getContaExtra());
        }
        return retorno;
    }

    public List<ConfiguracaoContabilUnidadeDocumentoBloqueado> getUnidadesDocumentosBloqueados() {
        return unidadesDocumentosBloqueados;
    }

    public void setUnidadesDocumentosBloqueados(List<ConfiguracaoContabilUnidadeDocumentoBloqueado> unidadesDocumentosBloqueados) {
        this.unidadesDocumentosBloqueados = unidadesDocumentosBloqueados;
    }

    public Boolean getBloquearSaldoNegativoBemMovel() {
        return bloquearSaldoNegativoBemMovel != null ? bloquearSaldoNegativoBemMovel : Boolean.FALSE;
    }

    public void setBloquearSaldoNegativoBemMovel(Boolean bloquearSaldoNegativoBemMovel) {
        this.bloquearSaldoNegativoBemMovel = bloquearSaldoNegativoBemMovel;
    }

    public Boolean getBloquearSaldoNegativoBemImovel() {
        return bloquearSaldoNegativoBemImovel != null ? bloquearSaldoNegativoBemImovel : Boolean.FALSE;
    }

    public void setBloquearSaldoNegativoBemImovel(Boolean bloquearSaldoNegativoBemImovel) {
        this.bloquearSaldoNegativoBemImovel = bloquearSaldoNegativoBemImovel;
    }

    public Boolean getBloquearSaldoNegativoBemIntangivel() {
        return bloquearSaldoNegativoBemIntangivel != null ? bloquearSaldoNegativoBemIntangivel : Boolean.FALSE;
    }

    public void setBloquearSaldoNegativoBemIntangivel(Boolean bloquearSaldoNegativoBemIntangivel) {
        this.bloquearSaldoNegativoBemIntangivel = bloquearSaldoNegativoBemIntangivel;
    }

    public Boolean getBloquearSaldoNegativoBemEstoque() {
        return bloquearSaldoNegativoBemEstoque != null ? bloquearSaldoNegativoBemEstoque : Boolean.FALSE;
    }

    public void setBloquearSaldoNegativoBemEstoque(Boolean bloquearSaldoNegativoBemEstoque) {
        this.bloquearSaldoNegativoBemEstoque = bloquearSaldoNegativoBemEstoque;
    }

    public Integer getTamanhoLoteReprocessamentoContabil() {
        if (tamanhoLoteReprocessamentoContabil == null) {
            tamanhoLoteReprocessamentoContabil = TAMANHO_LOTE_REPROCESSAMENTO_CONTABIL;
        }
        return tamanhoLoteReprocessamentoContabil;
    }

    public void setTamanhoLoteReprocessamentoContabil(Integer tamanhoLoteReprocessamentoContabil) {
        this.tamanhoLoteReprocessamentoContabil = tamanhoLoteReprocessamentoContabil;
    }

    public BigDecimal getPorcentagemMinimaCalculoBase() {
        return porcentagemMinimaCalculoBase;
    }

    public void setPorcentagemMinimaCalculoBase(BigDecimal porcentagemMinimaCalculoBase) {
        this.porcentagemMinimaCalculoBase = porcentagemMinimaCalculoBase;
    }

    public List<ConfiguracaoContabilFonte> getFontesTesouro() {
        return fontesTesouro;
    }

    public void setFontesTesouro(List<ConfiguracaoContabilFonte> fontesTesouro) {
        this.fontesTesouro = fontesTesouro;
    }

    public Boolean getContabilizarMicroService() {
        return contabilizarMicroService == null ? Boolean.FALSE : contabilizarMicroService;
    }

    public void setContabilizarMicroService(Boolean contabilizarMicroService) {
        this.contabilizarMicroService = contabilizarMicroService;
    }

    public Boolean getSaldoCreditoReceberMicroService() {
        return saldoCreditoReceberMicroService == null ? Boolean.FALSE : saldoCreditoReceberMicroService;
    }

    public void setSaldoCreditoReceberMicroService(Boolean saldoCreditoReceberMicroService) {
        this.saldoCreditoReceberMicroService = saldoCreditoReceberMicroService;
    }

    public Boolean getSaldoDividaAtivaContabilMicroService() {
        return saldoDividaAtivaContabilMicroService == null ? Boolean.FALSE : saldoDividaAtivaContabilMicroService;
    }

    public void setSaldoDividaAtivaContabilMicroService(Boolean saldoDividaAtivaContabilMicroService) {
        this.saldoDividaAtivaContabilMicroService = saldoDividaAtivaContabilMicroService;
    }

    public Boolean getSaldoDividaPublicaMicroService() {
        return saldoDividaPublicaMicroService == null ? Boolean.FALSE : saldoDividaPublicaMicroService;
    }

    public void setSaldoDividaPublicaMicroService(Boolean saldoDividaPublicaMicroService) {
        this.saldoDividaPublicaMicroService = saldoDividaPublicaMicroService;
    }

    public Boolean getSaldoExtraorcamentarioMicroService() {
        return saldoExtraorcamentarioMicroService == null ? Boolean.FALSE : saldoExtraorcamentarioMicroService;
    }

    public void setSaldoExtraorcamentarioMicroService(Boolean saldoExtraorcamentarioMicroService) {
        this.saldoExtraorcamentarioMicroService = saldoExtraorcamentarioMicroService;
    }

    public Boolean getSaldoFonteDespesaOrcMicroService() {
        return saldoFonteDespesaOrcMicroService == null ? Boolean.FALSE : saldoFonteDespesaOrcMicroService;
    }

    public void setSaldoFonteDespesaOrcMicroService(Boolean saldoFonteDespesaOrcMicroService) {
        this.saldoFonteDespesaOrcMicroService = saldoFonteDespesaOrcMicroService;
    }

    public Boolean getSaldoGrupoBemImoveisMicroService() {
        return saldoGrupoBemImoveisMicroService == null ? Boolean.FALSE : saldoGrupoBemImoveisMicroService;
    }

    public void setSaldoGrupoBemImoveisMicroService(Boolean saldoGrupoBemImoveisMicroService) {
        this.saldoGrupoBemImoveisMicroService = saldoGrupoBemImoveisMicroService;
    }

    public Boolean getSaldoGrupoBemIntangiveisMicroService() {
        return saldoGrupoBemIntangiveisMicroService == null ? Boolean.FALSE : saldoGrupoBemIntangiveisMicroService;
    }

    public void setSaldoGrupoBemIntangiveisMicroService(Boolean saldoGrupoBemIntangiveisMicroService) {
        this.saldoGrupoBemIntangiveisMicroService = saldoGrupoBemIntangiveisMicroService;
    }

    public Boolean getSaldoGrupoBemMicroService() {
        return saldoGrupoBemMicroService == null ? Boolean.FALSE : saldoGrupoBemMicroService;
    }

    public void setSaldoGrupoBemMicroService(Boolean saldoGrupoBemMicroService) {
        this.saldoGrupoBemMicroService = saldoGrupoBemMicroService;
    }

    public Boolean getSaldoGrupoMaterialMicroService() {
        return saldoGrupoMaterialMicroService == null ? Boolean.FALSE : saldoGrupoMaterialMicroService;
    }

    public void setSaldoGrupoMaterialMicroService(Boolean saldoGrupoMaterialMicroService) {
        this.saldoGrupoMaterialMicroService = saldoGrupoMaterialMicroService;
    }

    public Boolean getSaldoSubContaMicroService() {
        return saldoSubContaMicroService == null ? Boolean.FALSE : saldoSubContaMicroService;
    }

    public void setSaldoSubContaMicroService(Boolean saldoSubContaMicroService) {
        this.saldoSubContaMicroService = saldoSubContaMicroService;
    }

    public Conta getContaExtraInssPadraoDocLiq() {
        return contaExtraInssPadraoDocLiq;
    }

    public void setContaExtraInssPadraoDocLiq(Conta contaExtraInssPadraoDocLiq) {
        this.contaExtraInssPadraoDocLiq = contaExtraInssPadraoDocLiq;
    }

    public Conta getContaExtraIrrfPadraoDocLiq() {
        return contaExtraIrrfPadraoDocLiq;
    }

    public void setContaExtraIrrfPadraoDocLiq(Conta contaExtraIrrfPadraoDocLiq) {
        this.contaExtraIrrfPadraoDocLiq = contaExtraIrrfPadraoDocLiq;
    }

    public List<ConfiguracaoContabilTipoContaDespesaReinf> getTiposContasDespesasReinf() {
        return tiposContasDespesasReinf;
    }

    public void setTiposContasDespesasReinf(List<ConfiguracaoContabilTipoContaDespesaReinf> tiposContasDespesasReinf) {
        this.tiposContasDespesasReinf = tiposContasDespesasReinf;
    }

    public Pessoa getPessoaInssPadraoDocLiq() {
        return pessoaInssPadraoDocLiq;
    }

    public void setPessoaInssPadraoDocLiq(Pessoa pessoaInssPadraoDocLiq) {
        this.pessoaInssPadraoDocLiq = pessoaInssPadraoDocLiq;
    }

    public ClasseCredor getClasseCredInssPadraoDocLiq() {
        return classeCredInssPadraoDocLiq;
    }

    public void setClasseCredInssPadraoDocLiq(ClasseCredor classeCredInssPadraoDocLiq) {
        this.classeCredInssPadraoDocLiq = classeCredInssPadraoDocLiq;
    }

    public Pessoa getPessoaIrrfPadraoDocLiq() {
        return pessoaIrrfPadraoDocLiq;
    }

    public void setPessoaIrrfPadraoDocLiq(Pessoa pessoaIrrfPadraoDocLiq) {
        this.pessoaIrrfPadraoDocLiq = pessoaIrrfPadraoDocLiq;
    }

    public ClasseCredor getClasseCredIrrfPadraoDocLiq() {
        return classeCredIrrfPadraoDocLiq;
    }

    public void setClasseCredIrrfPadraoDocLiq(ClasseCredor classeCredIrrfPadraoDocLiq) {
        this.classeCredIrrfPadraoDocLiq = classeCredIrrfPadraoDocLiq;
    }

    public List<ConfiguracaoContabilArquivoLayout> getArquivosLayouts() {
        return arquivosLayouts;
    }

    public void setArquivosLayouts(List<ConfiguracaoContabilArquivoLayout> arquivosLayouts) {
        this.arquivosLayouts = arquivosLayouts;
    }

    public String getDotacaoPadraoReconhecimentoDiv() {
        return dotacaoPadraoReconhecimentoDiv;
    }

    public void setDotacaoPadraoReconhecimentoDiv(String dotacaoPadraoReconhecimentoDiv) {
        this.dotacaoPadraoReconhecimentoDiv = dotacaoPadraoReconhecimentoDiv;
    }

    public Boolean getPermitirTipoLancamentoManual() {
        if (permitirTipoLancamentoManual == null) {
            permitirTipoLancamentoManual = Boolean.FALSE;
        }
        return permitirTipoLancamentoManual;
    }

    public void setPermitirTipoLancamentoManual(Boolean permitirTipoLancamentoManual) {
        this.permitirTipoLancamentoManual = permitirTipoLancamentoManual;
    }
}
