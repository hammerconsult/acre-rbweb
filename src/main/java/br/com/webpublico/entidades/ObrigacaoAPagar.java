package br.com.webpublico.entidades;

import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.SubTipoDespesa;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.enums.TipoReconhecimentoObrigacaoPagar;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabilComValor;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by mga on 22/06/2017.
 */
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Obrigação a Pagar")
@Audited
@Entity
public class ObrigacaoAPagar extends SuperEntidade implements EntidadeContabilComValor, IGeraContaAuxiliar {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Etiqueta("Data")
    @Obrigatorio
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataLancamento;

    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    private String numero;

    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;

    @ManyToOne
    @Pesquisavel
    @Etiqueta("Empenho")
    private Empenho empenho;

    @ManyToOne
    @Etiqueta("Elemento de Despesa")
    @Obrigatorio
    private DespesaORC despesaORC;

    @ManyToOne
    private Conta contaDespesa;

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Etiqueta("Tipo de Despesa")
    private TipoContaDespesa tipoContaDespesa;

    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Obrigatorio
    @Etiqueta(value = "Subtipo de Despesa")
    private SubTipoDespesa subTipoDespesa;

    @ManyToOne
    @Tabelavel
    @Etiqueta("Classificação Orçamentária")
    @Obrigatorio
    private FonteDespesaORC fonteDespesaORC;

    @ManyToOne
    private ContaDeDestinacao contaDeDestinacao;

    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;

    @ManyToOne
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Pessoa")
    private Pessoa pessoa;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Classe")
    @Tabelavel
    @Pesquisavel
    private ClasseCredor classeCredor;

    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Monetario
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;

    @Pesquisavel
    @Tabelavel
    @Monetario
    @Etiqueta("Saldo (R$)")
    private BigDecimal saldo;

    @Pesquisavel
    @Monetario
    @Etiqueta("Saldo do Empenho (R$)")
    private BigDecimal saldoEmpenho;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    @Etiqueta("Histórico")
    @Obrigatorio
    private String historico;
    private String historicoNota;
    private String historicoRazao;

    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;

    @Invisivel
    @OneToMany(mappedBy = "obrigacaoAPagar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObrigacaoAPagarDoctoFiscal> documentosFiscais;

    @OneToMany(mappedBy = "obrigacaoAPagar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DesdobramentoObrigacaoPagar> desdobramentos;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Reconhecimento")
    private TipoReconhecimentoObrigacaoPagar tipoReconhecimento;

    @OneToOne
    private ObrigacaoAPagar obrigacaoAPagar;
    @ManyToOne
    private ItemIntegracaoRHContabil itemIntegracaoRHContabil;
    @Invisivel
    private Boolean transportado;

    public ObrigacaoAPagar() {
        valor = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
        saldoEmpenho = BigDecimal.ZERO;
        documentosFiscais = Lists.newArrayList();
        desdobramentos = Lists.newArrayList();
        transportado = false;
    }

    public TipoReconhecimentoObrigacaoPagar getTipoReconhecimento() {
        return tipoReconhecimento;
    }

    public void setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar tipoReconhecimento) {
        this.tipoReconhecimento = tipoReconhecimento;
    }

    public BigDecimal getSaldoEmpenho() {
        if (this.saldoEmpenho == null) {
            return BigDecimal.ZERO;
        }
        return saldoEmpenho;
    }

    public void setSaldoEmpenho(BigDecimal saldoEmpenho) {
        this.saldoEmpenho = saldoEmpenho;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
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

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<ObrigacaoAPagarDoctoFiscal> getDocumentosFiscais() {
        return documentosFiscais;
    }

    public void setDocumentosFiscais(List<ObrigacaoAPagarDoctoFiscal> documentosFiscais) {
        this.documentosFiscais = documentosFiscais;
    }

    public List<DesdobramentoObrigacaoPagar> getDesdobramentos() {
        return desdobramentos;
    }

    public void setDesdobramentos(List<DesdobramentoObrigacaoPagar> desdobramentos) {
        this.desdobramentos = desdobramentos;
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }

    public SubTipoDespesa getSubTipoDespesa() {
        return subTipoDespesa;
    }

    public void setSubTipoDespesa(SubTipoDespesa subTipoDespesa) {
        this.subTipoDespesa = subTipoDespesa;
    }

    public ObrigacaoAPagar getObrigacaoAPagar() {
        return obrigacaoAPagar;
    }

    public void setObrigacaoAPagar(ObrigacaoAPagar obrigacaoAPagar) {
        this.obrigacaoAPagar = obrigacaoAPagar;
    }

    public Boolean getTransportado() {
        return transportado;
    }

    public void setTransportado(Boolean transportado) {
        this.transportado = transportado;
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    @Override
    public String toString() {
        try {
            return "N° " + numero + " - " + DataUtil.getDataFormatada(dataLancamento) + " - " + Util.formataValor(valor) + ", " + tipoReconhecimento.getDescricao();
        } catch (Exception ex) {
            return "";
        }
    }

    private void gerarHistoricoNota() {
        String historico = "";
        if (this.getNumero() != null) {
            historico += "Obrigação a Pagar: " + this.getNumero() + "/" + this.getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getEmpenho() != null) {
            historico += this.empenho.getNumeroExercicioParaHistorico() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historico += " Dotação: " + this.despesaORC.getHistoricoPadrao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        historico += " Conta de Despesa: " + this.despesaORC.getProvisaoPPADespesa().getContaDeDespesa().getCodigo() + UtilBeanContabil.SEPARADOR_HISTORICO;
        if (this.getDesdobramentos() != null) {
            for (DesdobramentoObrigacaoPagar desdobramento : this.getDesdobramentos()) {
                historico += " Detalhamento: " + desdobramento.getConta().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
        }
        historico += " Conta de Destinação de Recurso: " + this.fonteDespesaORC.getProvisaoPPAFonte().getDestinacaoDeRecursos().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        historico += " Pessoa: " + this.pessoa.getNomeCpfCnpj() + UtilBeanContabil.SEPARADOR_HISTORICO;
        historico += " Classe: " + this.classeCredor.toString() + UtilBeanContabil.SEPARADOR_HISTORICO;

        if (this.getDocumentosFiscais() != null) {
            for (ObrigacaoAPagarDoctoFiscal doc : this.getDocumentosFiscais()) {
                historico += " Tipo: " + doc.getDocumentoFiscal().getTipoDocumentoFiscal() + " -  Nº Documento Comprobatório: " + doc.getDocumentoFiscal().getNumero() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
        }
        if (this.getHistorico() != null) {
            historico += " " + this.getHistorico();
        }
        this.historicoNota = historico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    private void gerarHistoricoRazao() {
        historicoRazao = " ";
        for (DesdobramentoObrigacaoPagar desd : this.getDesdobramentos()) {
            if (desd.getEventoContabil() != null) {
                if (desd.getEventoContabil().getClpHistoricoContabil() != null) {
                    historicoRazao += desd.getEventoContabil().getClpHistoricoContabil().toString() + " ";
                }
            }
        }
        historicoRazao += this.historicoNota;
        historicoRazao = Util.cortarString(this.historicoRazao, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos() {
        gerarHistoricoNota();
        gerarHistoricoRazao();
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        if (empenho != null) {
            return numero + "/" + exercicio.getAno() + " - " + empenho.getNumero();
        }
        return numero + "/" + exercicio.getAno();
    }

    public boolean isObrigacaoPagarDepoisEmpenho() {
        return this.empenho != null;
    }

    public boolean isObrigacaoPagarAntesEmpenho() {
        return this.empenho == null;
    }

    public BigDecimal getValorTotalDesdobramentos() {
        BigDecimal valor = BigDecimal.ZERO;
        if (getDesdobramentos() != null && !getDesdobramentos().isEmpty()) {
            for (DesdobramentoObrigacaoPagar desdobramento : getDesdobramentos()) {
                valor = valor.add(desdobramento.getValor());
            }
        }
        return valor;
    }

    public BigDecimal getSaldoTotalDesdobramentos() {
        BigDecimal saldo = BigDecimal.ZERO;
        if (getDesdobramentos() != null && !getDesdobramentos().isEmpty()) {
            for (DesdobramentoObrigacaoPagar desdobramento : getDesdobramentos()) {
                saldo = saldo.add(desdobramento.getSaldo());
            }
        }
        return saldo;
    }

    public BigDecimal getValorTotalDocumentos() {
        BigDecimal total = BigDecimal.ZERO;
        if (getDocumentosFiscais() != null && !getDocumentosFiscais().isEmpty()) {
            for (ObrigacaoAPagarDoctoFiscal docto : getDocumentosFiscais()) {
                if (docto.getDocumentoFiscal() != null) {
                    total = total.add(docto.getDocumentoFiscal().getValor());
                }
            }
        }
        return total;
    }

    public BigDecimal getSaldoTotalDocumentos() {
        BigDecimal saldo = BigDecimal.ZERO;
        if (getDocumentosFiscais() != null && !getDocumentosFiscais().isEmpty()) {
            for (ObrigacaoAPagarDoctoFiscal desdobramento : getDocumentosFiscais()) {
                saldo = saldo.add(desdobramento.getSaldo());
            }
        }
        return saldo;
    }

    public void validarSaldoDisponivelPorContaDespesa(BigDecimal valor, Conta conta) {
        ValidacaoException ve = new ValidacaoException();
        for (DesdobramentoObrigacaoPagar desdObrigacao : getDesdobramentos()) {
            if (desdObrigacao.getConta().equals(conta) && valor.compareTo(desdObrigacao.getSaldo()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do detalhamento deve ser menor ou igual ao saldo de: " + Util.formataValor(desdObrigacao.getSaldo())
                    + " disponível para a conta: " + conta + ".");
            }
        }
        ve.lancarException();
    }

    public void validarSaldoDisponivelPorDocumentoFiscal(BigDecimal valor, DoctoFiscalLiquidacao documentoFiscal) {
        ValidacaoException ve = new ValidacaoException();
        for (ObrigacaoAPagarDoctoFiscal doctoFiscal : getDocumentosFiscais()) {
            if (doctoFiscal.getDocumentoFiscal().equals(documentoFiscal) && valor.compareTo(doctoFiscal.getSaldo()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor documento deve ser menor ou igual ao saldo de: " + Util.formataValor(doctoFiscal.getSaldo())
                    + " disponível para o documento: " + documentoFiscal + ".");
            }
        }
        ve.lancarException();
    }

    public void validarMesmoDocumentoEmLista(DoctoFiscalLiquidacao documentoFiscal) {
        ValidacaoException ve = new ValidacaoException();
        for (ObrigacaoAPagarDoctoFiscal doctoObrigacao : this.getDocumentosFiscais()) {
            if (!doctoObrigacao.getDocumentoFiscal().equals(documentoFiscal)
                && isMesmoDocumentoFiscal(doctoObrigacao.getDocumentoFiscal(), documentoFiscal)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O documento " + documentoFiscal.getTipoDocumentoFiscal() +
                    ", Nº" + documentoFiscal.getNumero() + "/" + documentoFiscal.getSerie() +
                    ", " + Util.formataValor(documentoFiscal.getValor()) + " já foi adicionado na lista.");
            }
        }
        ve.lancarException();
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public ItemIntegracaoRHContabil getItemIntegracaoRHContabil() {
        return itemIntegracaoRHContabil;
    }

    public void setItemIntegracaoRHContabil(ItemIntegracaoRHContabil itemIntegracaoRHContabil) {
        this.itemIntegracaoRHContabil = itemIntegracaoRHContabil;
    }

    private boolean isMesmoDocumentoFiscal(DoctoFiscalLiquidacao documentoFiscalAdicionado, DoctoFiscalLiquidacao documentoFiscal) {
        return documentoFiscalAdicionado.getTipoDocumentoFiscal().getDescricao().equals(documentoFiscal.getTipoDocumentoFiscal().getDescricao())
            && documentoFiscalAdicionado.getValor().equals(documentoFiscal.getValor())
            && documentoFiscalAdicionado.getNumero().trim().toLowerCase().equals(documentoFiscal.getNumero().trim().toLowerCase());
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    @Override
    public TreeMap getMapContaAuxiliarSistema(TipoContaAuxiliar tipoContaAuxiliar) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        AcaoPPA acaoPPA = getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada2(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada4(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    getContaDeDestinacao(),
                    getExercicio());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada5(getUnidadeOrganizacional(),
                    getContaDeDestinacao(),
                    getExercicio());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada7(getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    getContaDeDestinacao(),
                    getContaDespesa(),
                    (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "99":
                if (empenho != null) {
                    if (CategoriaOrcamentaria.NORMAL.equals(empenho.getCategoriaOrcamentaria())) {
                        return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada9(unidadeOrganizacional,
                            acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                            getContaDeDestinacao(),
                            getContaDespesa(),
                            (empenho.getExtensaoFonteRecurso().getCodigo().toString().startsWith("4") ? 2 :
                                empenho.getExtensaoFonteRecurso().getCodigo().toString().startsWith("1") ||
                                    empenho.getExtensaoFonteRecurso().getCodigo().toString().startsWith("2") ||
                                    empenho.getExtensaoFonteRecurso().getCodigo().toString().startsWith("3") ? 1 : 0),
                            exercicio.getAno(),
                            exercicio,
                            exercicio);
                    }

                    if (CategoriaOrcamentaria.RESTO.equals(empenho.getCategoriaOrcamentaria())) {
                        return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada9(getUnidadeOrganizacional(),
                            acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                            empenho.getContaDeDestinacao(),
                            empenho.getContaDespesa(),
                            (empenho.getExtensaoFonteRecurso().getCodigo().toString().startsWith("4") ? 2 :
                                empenho.getExtensaoFonteRecurso().getCodigo().toString().startsWith("1") ||
                                    empenho.getExtensaoFonteRecurso().getCodigo().toString().startsWith("2") ||
                                    empenho.getExtensaoFonteRecurso().getCodigo().toString().startsWith("3") ? 1 : 0),
                            empenho.getEmpenho().getExercicio().getAno(),
                            empenho.getExercicio(),
                            empenho.getEmpenho().getExercicio());
                    }
                }
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        AcaoPPA acaoPPA = getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar2(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar4(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    getContaDeDestinacao());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar5(getUnidadeOrganizacional(),
                    getContaDeDestinacao());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar7(getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    getContaDeDestinacao(),
                    (getContaDespesa().getCodigoSICONFI() != null ?
                        getContaDespesa().getCodigoSICONFI() :
                        getContaDespesa().getCodigo().replace(".", "")),
                    (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "99":
                if (empenho != null) {
                    if (CategoriaOrcamentaria.NORMAL.equals(empenho.getCategoriaOrcamentaria())) {
                        return UtilGeradorContaAuxiliar.gerarContaAuxiliar9(unidadeOrganizacional,
                            acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                            getContaDeDestinacao(),
                            getContaDespesa(),
                            (empenho.getExtensaoFonteRecurso().getCodigo().toString().startsWith("4") ? 2 :
                                empenho.getExtensaoFonteRecurso().getCodigo().toString().startsWith("1") ||
                                    empenho.getExtensaoFonteRecurso().getCodigo().toString().startsWith("2") ||
                                    empenho.getExtensaoFonteRecurso().getCodigo().toString().startsWith("3") ? 1 : 0),
                            exercicio.getAno(),
                            exercicio);
                    }
                    if (CategoriaOrcamentaria.RESTO.equals(empenho.getCategoriaOrcamentaria())) {
                        return UtilGeradorContaAuxiliar.gerarContaAuxiliar9(getUnidadeOrganizacional(),
                            acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                            empenho.getContaDeDestinacao(),
                            empenho.getContaDespesa(),
                            (empenho.getExtensaoFonteRecurso().getCodigo().toString().startsWith("4") ? 2 :
                                empenho.getExtensaoFonteRecurso().getCodigo().toString().startsWith("1") ||
                                    empenho.getExtensaoFonteRecurso().getCodigo().toString().startsWith("2") ||
                                    empenho.getExtensaoFonteRecurso().getCodigo().toString().startsWith("3") ? 1 : 0),
                            empenho.getEmpenho().getExercicio().getAno(),
                            empenho.getExercicio());
                    }
                }
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    public String getCodigoExtensaoFonteRecursoAsString() {
        return getFonteDespesaORC().getProvisaoPPAFonte().getExtensaoFonteRecurso().getCodigo().toString();
    }
}

