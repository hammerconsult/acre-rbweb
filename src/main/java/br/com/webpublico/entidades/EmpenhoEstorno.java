/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.entidadesauxiliares.manad.ManadRegistro;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.TipoEmpenhoEstorno;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.EntidadeContabilComValor;
import br.com.webpublico.interfaces.IManadRegistro;
import br.com.webpublico.negocios.manad.ManadContabilFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.ManadUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author venon
 */
@GrupoDiagrama(nome = "Contabil")
@Entity
@Audited
@Etiqueta("Estorno de Empenho")
public class EmpenhoEstorno extends SuperEntidadeContabilGerarContaAuxiliar implements EntidadeContabilComValor, IManadRegistro {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private String numero;

    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data")
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataEstorno;

    @ErroReprocessamentoContabil
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Empenho")
    @ManyToOne
    private Empenho empenho;

    @ErroReprocessamentoContabil
    @Tabelavel
    @Pesquisavel
    @Monetario
    @Obrigatorio
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;

    @Etiqueta("Histórico Contábil")
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @ErroReprocessamentoContabil
    private HistoricoContabil historicoContabil;

    @Etiqueta("Histórico")
    @Obrigatorio
    private String complementoHistorico;

    @Invisivel
    @ManyToOne
    private MovimentoDespesaORC movimentoDespesaORC;

    @Enumerated(EnumType.STRING)
    @ReprocessamentoContabil
    private CategoriaOrcamentaria categoriaOrcamentaria;

    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Etiqueta("Unidade Organizacional")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    @Etiqueta("Unidade Organizacional Administrativa")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacionalAdm;

    @ManyToOne
    @Etiqueta("Evento Contábil")
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Obrigatorio
    private EventoContabil eventoContabil;

    @ManyToOne
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Etiqueta("Exercício")
    private Exercicio exercicio;

    @Enumerated(EnumType.STRING)
    private TipoEmpenhoEstorno tipoEmpenhoEstorno;

    @Etiqueta("Saldo Disponível (R$)")
    @Monetario
    private BigDecimal saldoDisponivel;

    private String historicoNota;
    private String historicoRazao;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "empenhoEstorno", orphanRemoval = true)
    private List<DesdobramentoEmpenhoEstorno> desdobramentos;

    public EmpenhoEstorno() {
        desdobramentos = Lists.newArrayList();
        valor = BigDecimal.ZERO;
        saldoDisponivel = BigDecimal.ZERO;
    }

    public EmpenhoEstorno(Date dataEstorno, BigDecimal valor, String numero, Empenho empenho, HistoricoContabil historicoContabil, String complementoHistorico, MovimentoDespesaORC movimentoDespesaORC, CategoriaOrcamentaria categoriaOrcamentaria) {
        this.dataEstorno = dataEstorno;
        this.valor = valor;
        this.numero = numero;
        this.empenho = empenho;
        this.historicoContabil = historicoContabil;
        this.complementoHistorico = complementoHistorico;
        this.movimentoDespesaORC = movimentoDespesaORC;
        this.categoriaOrcamentaria = categoriaOrcamentaria;
    }

    public EmpenhoEstorno(Long id, Date dataEstorno, BigDecimal valor, String numero, Long empenho) {
        this.id = id;
        this.dataEstorno = dataEstorno;
        this.valor = valor;
        this.numero = numero;
        this.empenho = new Empenho();
        this.empenho.setId(empenho);
    }

    public List<DesdobramentoEmpenhoEstorno> getDesdobramentos() {
        return desdobramentos;
    }

    public void setDesdobramentos(List<DesdobramentoEmpenhoEstorno> desdobramentos) {
        this.desdobramentos = desdobramentos;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComplementoHistorico() {
        return complementoHistorico;
    }

    public void setComplementoHistorico(String complementoHistorico) {
        this.complementoHistorico = complementoHistorico;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public HistoricoContabil getHistoricoContabil() {
        return historicoContabil;
    }

    public void setHistoricoContabil(HistoricoContabil historicoContabil) {
        this.historicoContabil = historicoContabil;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public MovimentoDespesaORC getMovimentoDespesaORC() {
        return movimentoDespesaORC;
    }

    public void setMovimentoDespesaORC(MovimentoDespesaORC movimentoDespesaORC) {
        this.movimentoDespesaORC = movimentoDespesaORC;
    }

    public CategoriaOrcamentaria getCategoriaOrcamentaria() {
        return categoriaOrcamentaria;
    }

    public void setCategoriaOrcamentaria(CategoriaOrcamentaria categoriaOrcamentaria) {
        this.categoriaOrcamentaria = categoriaOrcamentaria;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalAdm() {
        return unidadeOrganizacionalAdm;
    }

    public void setUnidadeOrganizacionalAdm(UnidadeOrganizacional unidadeOrganizacionalAdm) {
        this.unidadeOrganizacionalAdm = unidadeOrganizacionalAdm;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public String getHistoricoNota() {
        return historicoNota;
    }

    public void setHistoricoNota(String historicoNota) {
        this.historicoNota = historicoNota;
    }

    public String getHistoricoRazao() {
        return historicoRazao;
    }

    public void setHistoricoRazao(String historicoRazao) {
        this.historicoRazao = historicoRazao;
    }

    public TipoEmpenhoEstorno getTipoEmpenhoEstorno() {
        return tipoEmpenhoEstorno;
    }

    public void setTipoEmpenhoEstorno(TipoEmpenhoEstorno tipoEmpenhoEstorno) {
        this.tipoEmpenhoEstorno = tipoEmpenhoEstorno;
    }

    public String getNumeroAnoCategoria() {
        try {
            return numero + "/" + exercicio.getAno() + " - " + DataUtil.getDataFormatada(dataEstorno) + " - " + categoriaOrcamentaria.getDescricao();
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String getNumeroAnoCategoriaValor() {
        try {
            return numero + "/" + exercicio.getAno() + " - " + DataUtil.getDataFormatada(dataEstorno) + " - " + categoriaOrcamentaria.getDescricao() + " - " + Util.formataValor(valor);
        } catch (NullPointerException e) {
            return "";
        }
    }

    @Override
    public String toString() {
        return "N° " + numero + " - Empenho: " + empenho;
    }

    public void gerarHistoricoNota() {
        historicoNota = "";
        if (this.getNumero() != null) {
            historicoNota += "Nº " + this.getNumero() + "/" + Util.getAnoDaData(this.getDataEstorno()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (empenho.getTransportado()) {
            historicoNota += " Restos a Pagar: " + empenho.getNumero() + "/" + Util.getAnoDaData(empenho.getDataEmpenho()) + UtilBeanContabil.SEPARADOR_HISTORICO;
            historicoNota += " Empenho de Despesa: " + empenho.getEmpenho().getNumero() + "/" + Util.getAnoDaData(empenho.getEmpenho().getDataEmpenho()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        } else if (this.getEmpenho().getNumero() != null) {
            historicoNota += this.empenho.getNumeroExercicioParaHistorico() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getEmpenho().getDespesaORC() != null) {
            historicoNota += " Dotação: " + this.getEmpenho().getDespesaORC().getHistoricoPadrao() + UtilBeanContabil.SEPARADOR_HISTORICO;
            historicoNota += " Conta de Despesa: " + this.getEmpenho().getDespesaORC().getDescricaoContaDespesaPPA() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getEmpenho().getFonteDespesaORC() != null) {
            historicoNota += " Fonte de Recurso: " + this.getEmpenho().getFonteDespesaORC().getDescricaoFonteDeRecurso().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getEmpenho().getFornecedor() != null) {
            historicoNota += " Pessoa: " + this.getEmpenho().getFornecedor().getNomeCpfCnpj() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getEmpenho().getClasseCredor() != null) {
            historicoNota += " Classe: " + this.getEmpenho().getClasseCredor().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getEmpenho().getTipoEmpenho() != null) {
            historicoNota += " Tipo de Empenho: " + this.getEmpenho().getTipoEmpenho().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getEmpenho().getPropostaConcessaoDiaria() != null) {
            historicoNota += " Concessão de Diária: " + this.getEmpenho().getPropostaConcessaoDiaria().getTipoProposta().getDescricao() + ", " + this.getEmpenho().getPropostaConcessaoDiaria().getCodigo() + "/" + Util.getAnoDaData(this.getEmpenho().getPropostaConcessaoDiaria().getDataLancamento()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getEmpenho().getDividaPublica() != null) {
            historicoNota += " Dívida Pública: " + this.getEmpenho().getDividaPublica().getNumeroProtocolo() + "/" + Util.getAnoDaData(this.getEmpenho().getDividaPublica().getDataHomologacao()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getEmpenho().getConvenioDespesa() != null) {
            historicoNota += " Convênio de Despesa: " + this.getEmpenho().getConvenioDespesa().getNumConvenio() + "/" + Util.getAnoDaData(this.getEmpenho().getConvenioDespesa().getDataVigenciaInicial()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getEmpenho().getConvenioReceita() != null) {
            historicoNota += " Convênio de Receita: " + this.getEmpenho().getConvenioReceita().getNomeConvenio() + "/" + Util.getAnoDaData(this.getEmpenho().getConvenioReceita().getVigenciaInicial()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getEmpenho().getDividaPublica() != null) {
            historicoNota += " " + this.getEmpenho().getDividaPublica().getNaturezaDividaPublica().getDescricao() + ": " + this.getEmpenho().getDividaPublica().getNumero() + "/" + Util.getAnoDaData(this.getEmpenho().getDividaPublica().getDataInicio() == null ? this.getEmpenho().getDividaPublica().getDataHomologacao() : this.getEmpenho().getDividaPublica().getDataInicio()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getEmpenho().getContrato() != null) {
            if (this.getEmpenho().getContrato().getLicitacao() != null) {
                historicoNota += " Contrato: " + this.getEmpenho().getContrato().getNumeroProcesso() + "/" + this.getEmpenho().getContrato().getLicitacao().getExercicio() + UtilBeanContabil.SEPARADOR_HISTORICO;
            } else {
                historicoNota += " Contrato: " + this.getEmpenho().getContrato().getNumeroProcesso() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
        }
        historicoNota = historicoNota + " " + complementoHistorico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil().getClpHistoricoContabil() != null) {
            historicoEvento = this.getEventoContabil().getClpHistoricoContabil().toString();
        }
        this.historicoRazao = historicoEvento.toString() + " " + this.historicoNota;
        this.historicoRazao = Util.cortarString(this.historicoRazao, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos() {
        gerarHistoricoNota();
        gerarHistoricoRazao();
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero + " - " + ((EntidadeContabil) empenho).getReferenciaArquivoPrestacaoDeContas();
    }

    @Override
    public ManadRegistro.ManadModulo getModulo() {
        return ManadRegistro.ManadModulo.CONTABIL;
    }

    @Override
    public ManadRegistro.ManadRegistroTipo getTipoRegistro() {
        return ManadRegistro.ManadRegistroTipo.L050;
    }

    @Override
    public void getConteudoManad(ManadContabilFacade facade, StringBuilder conteudo) {
        HierarquiaOrganizacional unidade = ((ManadContabilFacade) facade).getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(this.getDataEstorno(), this.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        HierarquiaOrganizacional orgao = ((ManadContabilFacade) facade).getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(this.getDataEstorno(), unidade.getSuperior(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        AcaoPPA acaoPPA = this.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.L050.name(), conteudo);
        ManadUtil.criaLinha(2, orgao.getCodigoNo(), conteudo);
        ManadUtil.criaLinha(3, unidade.getCodigoNo(), conteudo);
        ManadUtil.criaLinha(4, acaoPPA.getAcaoPrincipal().getFuncao().getCodigo(), conteudo);
        ManadUtil.criaLinha(5, acaoPPA.getAcaoPrincipal().getSubFuncao().getCodigo(), conteudo);
        ManadUtil.criaLinha(6, acaoPPA.getAcaoPrincipal().getPrograma().getCodigo(), conteudo);
        ManadUtil.criaLinha(7, acaoPPA.getAcaoPrincipal().getCodigo(), conteudo);
        ManadUtil.criaLinha(8, acaoPPA.getCodigo(), conteudo);
        ManadUtil.criaLinha(9, "", conteudo);
        ManadUtil.criaLinha(10, "", conteudo);
        ManadUtil.criaLinha(11, "", conteudo);
        ManadUtil.criaLinha(12, this.numero, conteudo);
        ManadUtil.criaLinha(13, ManadUtil.getDataSemBarra(this.dataEstorno), conteudo);
        ManadUtil.criaLinha(14, ManadUtil.getValor(valor), conteudo);
        ManadUtil.criaLinha(15, "C", conteudo);
        ManadUtil.criaLinha(16, "", conteudo);
        ManadUtil.criaLinhaSemPipe(17, this.complementoHistorico, conteudo);
    }

    public boolean isEstornoEmpenhoDeObrigacaoPagar() {
        return this.empenho != null && this.empenho.getObrigacoesPagar() != null && !this.empenho.getObrigacoesPagar().isEmpty();
    }

    public boolean isEmpenhoEstornoResto() {
        return CategoriaOrcamentaria.RESTO.equals(this.categoriaOrcamentaria);
    }

    public boolean isEmpenhoEstornoNormal() {
        return CategoriaOrcamentaria.NORMAL.equals(this.categoriaOrcamentaria);
    }

    public BigDecimal getSaldoDisponivel() {
        return saldoDisponivel;
    }

    public void setSaldoDisponivel(BigDecimal saldoDisponivel) {
        this.saldoDisponivel = saldoDisponivel;
    }

    public BigDecimal getValorTotalDetalhamento() {
        BigDecimal valor = BigDecimal.ZERO;
        for (DesdobramentoEmpenhoEstorno desdobramento : desdobramentos) {
            valor = valor.add(desdobramento.getValor());
        }
        return valor;
    }

    public BigDecimal getValorEmpenhoComObrigacaoPagar() {
        BigDecimal valor = BigDecimal.ZERO;
        if (this.valor != null) {
            valor = this.valor.subtract(getValorTotalDetalhamento());
        }
        return valor;
    }

    public void validarMesmoDetalhamentoEmListaPorObrigacaoPagar(DesdobramentoEmpenhoEstorno desdSelecionado, ObrigacaoAPagar obrigacaoAPagar) {
        ValidacaoException ve = new ValidacaoException();
        for (DesdobramentoEmpenhoEstorno desdEmLista : this.getDesdobramentos()) {
            if (!desdEmLista.equals(desdSelecionado)
                && desdEmLista.getDesdobramentoObrigacaoPagar().getObrigacaoAPagar().equals(obrigacaoAPagar)) {
                isMesmoDetalhamentoDespesa(desdSelecionado.getConta(), ve, desdEmLista);
            }
        }
        ve.lancarException();
    }

    public void validarMesmoDetalhamentoEmListaPorEmpenho(DesdobramentoEmpenhoEstorno desdSelecionado, Empenho empenho) {
        ValidacaoException ve = new ValidacaoException();
        for (DesdobramentoEmpenhoEstorno desdEmLista : this.getDesdobramentos()) {
            if (!desdEmLista.equals(desdSelecionado)
                && desdEmLista.getDesdobramentoEmpenho().getEmpenho().equals(empenho)) {
                isMesmoDetalhamentoDespesa(desdSelecionado.getConta(), ve, desdEmLista);
            }
        }
        ve.lancarException();
    }

    private void isMesmoDetalhamentoDespesa(Conta conta, ValidacaoException ve, DesdobramentoEmpenhoEstorno desdEmLista) {
        if (desdEmLista.getConta().equals(conta)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" A conta " + conta + " já foi adicionada na lista.");
        }
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        String retorno = toString();
        if (empenho.isEmpenhoRestoPagar()) {
            retorno = "Cancelamento de Restos a Pagar: nº " + numero + " - " + DataUtil.getDataFormatada(dataEstorno) + " - " +
                "Restos a Pagar: nº " + empenho.getNumero() + " - " + DataUtil.getDataFormatada(empenho.getDataEmpenho()) + " - " +
                "Empenho: nº " + empenho.getEmpenho().getNumero() + " - " +
                DataUtil.getDataFormatada(empenho.getEmpenho().getDataEmpenho()) + " - " +
                "Função " + empenho.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getFuncao().getCodigo() + " - " +
                empenho.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getFuncao().getDescricao() + " - " +
                "Subfunção " + empenho.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getSubFuncao().getCodigo() + " - " +
                empenho.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getSubFuncao().getDescricao() + " - " +
                "Conta de Despesa " + empenho.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo() + " - " +
                empenho.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getDescricao() + " - " +
                "Fonte de Recurso " + empenho.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getCodigo() + " - " +
                empenho.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getDescricao() + " - " +
                "Pessoa " + empenho.getFornecedor().getNome() + " - " +
                "Valor " + Util.formataValor(valor);
        }
        return retorno;
    }

    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        AcaoPPA acaoPPA = this.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        if (CategoriaOrcamentaria.NORMAL.equals(categoriaOrcamentaria)) {
            return new GeradorContaAuxiliarDTO(unidadeOrganizacional,
                acaoPPA.getCodigoFuncaoSubFuncao(),
                this.getEmpenho().getContaDeDestinacao(),
                this.getEmpenho().getContaDespesa(),
                this.getEmpenho().getCodigoEs(),
                exercicio.getAno(),
                this.getEmpenho().getContaDespesa(),
                exercicio,
                exercicio,
                null,
                0,
                null,
                (this.getEmpenho().getContaDespesa().getCodigoContaSiconf()));
        }
        if (CategoriaOrcamentaria.RESTO.equals(categoriaOrcamentaria)) {
            return new GeradorContaAuxiliarDTO(getUnidadeOrganizacional(),
                acaoPPA.getCodigoFuncaoSubFuncao(),
                this.getEmpenho().getContaDeDestinacao(),
                this.getEmpenho().getContaDespesa(),
                this.getEmpenho().getCodigoEs(),
                empenho.getExercicio().getAno(),
                this.getEmpenho().getContaDespesa(),
                exercicio,
                empenho.getExercicio(),
                null,
                0,
                null,
                (this.getEmpenho().getContaDespesa().getCodigoContaSiconf()));
        }
        return null;
    }
}
