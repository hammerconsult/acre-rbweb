/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.TipoReconhecimentoObrigacaoPagar;
import br.com.webpublico.enums.TipoRestosProcessado;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabilComValor;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author venon
 */
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Liquidação")
@Audited
@Entity
public class Liquidacao extends SuperEntidade implements Serializable, EntidadeContabilComValor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    @ErroReprocessamentoContabil
    private String numero;
    @Tabelavel
    @Etiqueta("Data")
    @Obrigatorio
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    private Date dataLiquidacao;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Empenho")
    @ErroReprocessamentoContabil
    private Empenho empenho;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Monetario
    @Etiqueta("Valor (R$)")
    @ErroReprocessamentoContabil
    private BigDecimal valor;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Monetario
    @Etiqueta("Saldo (R$)")
    private BigDecimal saldo;
    @Obrigatorio
    @Etiqueta("Usuário")
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Invisivel
    @ManyToOne
    private MovimentoDespesaORC movimentoDespesaORC;
    @Invisivel
    @OneToMany(mappedBy = "liquidacao")
    private List<Pagamento> pagamentos;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    private String numeroOriginal;
    @Invisivel
    @OneToMany(mappedBy = "liquidacao")
    private List<LiquidacaoEstorno> liquidacaoEstornos;
    @Etiqueta("Histórico")
    @Obrigatorio
    private String complemento;
    @Enumerated(EnumType.STRING)
    @ReprocessamentoContabil
    private CategoriaOrcamentaria categoriaOrcamentaria;
    @OneToOne
    private Liquidacao liquidacao;
    @ManyToOne
    @ErroReprocessamentoContabil
    private Exercicio exercicio;
    @ManyToOne
    private Exercicio exercicioOriginal;
    @OneToMany(mappedBy = "liquidacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private List<LiquidacaoDoctoFiscal> doctoFiscais;
    @OneToMany(mappedBy = "liquidacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private List<LiquidacaoObrigacaoPagar> obrigacoesPagar;
    @OneToMany(mappedBy = "liquidacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    private List<Desdobramento> desdobramentos;
    @Etiqueta("Unidade Organizacional")
    @ManyToOne
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;
    @Etiqueta("Unidade Organizacional Administrativa")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacionalAdm;
    private String historicoNota;
    private String historicoRazao;
    @Invisivel
    private Boolean transportado;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Reconhecimento")
    private TipoReconhecimentoObrigacaoPagar tipoReconhecimento;
    @Version
    private Long versao;

    public Liquidacao() {
        valor = new BigDecimal(BigInteger.ZERO);
        saldo = new BigDecimal(BigInteger.ZERO);
        liquidacaoEstornos = new ArrayList<LiquidacaoEstorno>();
        doctoFiscais = new ArrayList<LiquidacaoDoctoFiscal>();
        desdobramentos = new ArrayList<Desdobramento>();
        obrigacoesPagar = new ArrayList<>();
        transportado = false;
    }

    public Liquidacao(Date dataLiquidacao, BigDecimal valor, BigDecimal saldo, UsuarioSistema usuarioSistema, Empenho empenho, MovimentoDespesaORC movimentoDespesaORC, List<Pagamento> pagamentos, Date dataRegistro, String numero, String numeroOriginal, List<LiquidacaoEstorno> liquidacaoEstornos, String complemento, CategoriaOrcamentaria categoriaOrcamentaria, Liquidacao liquidacao, Exercicio exercicio, Exercicio exercicioOriginal, List<LiquidacaoDoctoFiscal> doctoFiscais, List<Desdobramento> desdobramentos, String historicoNota, String historicoRazao) {
        this.dataLiquidacao = dataLiquidacao;
        this.valor = valor;
        this.saldo = saldo;
        this.usuarioSistema = usuarioSistema;
        this.empenho = empenho;
        this.movimentoDespesaORC = movimentoDespesaORC;
        this.pagamentos = pagamentos;
        this.dataRegistro = dataRegistro;
        this.numero = numero;
        this.numeroOriginal = numeroOriginal;
        this.liquidacaoEstornos = liquidacaoEstornos;
        this.complemento = complemento;
        this.categoriaOrcamentaria = categoriaOrcamentaria;
        this.liquidacao = liquidacao;
        this.exercicio = exercicio;
        this.exercicioOriginal = exercicioOriginal;
        this.doctoFiscais = doctoFiscais;
        this.desdobramentos = desdobramentos;
        this.historicoNota = historicoNota;
        this.historicoRazao = historicoRazao;
    }

    public Liquidacao(Long id, String numero, Date dataLiquidacao, Long empenho, BigDecimal valor, BigDecimal saldo) {
        this.id = id;
        this.numero = numero;
        this.dataLiquidacao = dataLiquidacao;
        this.empenho = new Empenho();
        this.empenho.setId(empenho);
        this.valor = valor;
        this.saldo = saldo;
    }

    public TipoReconhecimentoObrigacaoPagar getTipoReconhecimento() {
        return tipoReconhecimento;
    }

    public void setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar tipoReconhecimento) {
        this.tipoReconhecimento = tipoReconhecimento;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<LiquidacaoObrigacaoPagar> getObrigacoesPagar() {
        return obrigacoesPagar;
    }

    public void setObrigacoesPagar(List<LiquidacaoObrigacaoPagar> obrigacoesPagar) {
        this.obrigacoesPagar = obrigacoesPagar;
    }

    public Date getDataLiquidacao() {
        return dataLiquidacao;
    }

    public void setDataLiquidacao(Date dataLiquidacao) {
        this.dataLiquidacao = dataLiquidacao;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public MovimentoDespesaORC getMovimentoDespesaORC() {
        return movimentoDespesaORC;
    }

    public void setMovimentoDespesaORC(MovimentoDespesaORC movimentoDespesaORC) {
        this.movimentoDespesaORC = movimentoDespesaORC;
    }

    public List<Pagamento> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<Pagamento> pagamentos) {
        this.pagamentos = pagamentos;
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public List<LiquidacaoEstorno> getLiquidacaoEstornos() {
        return liquidacaoEstornos;
    }

    public void setLiquidacaoEstornos(List<LiquidacaoEstorno> liquidacaoEstornos) {
        this.liquidacaoEstornos = liquidacaoEstornos;
    }

    public CategoriaOrcamentaria getCategoriaOrcamentaria() {
        return categoriaOrcamentaria;
    }

    public void setCategoriaOrcamentaria(CategoriaOrcamentaria categoriaOrcamentaria) {
        this.categoriaOrcamentaria = categoriaOrcamentaria;
    }

    public Liquidacao getLiquidacao() {
        return liquidacao;
    }

    public void setLiquidacao(Liquidacao liquidacao) {
        this.liquidacao = liquidacao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Exercicio getExercicioOriginal() {
        return exercicioOriginal;
    }

    public void setExercicioOriginal(Exercicio exercicioOriginal) {
        this.exercicioOriginal = exercicioOriginal;
    }

    public String getNumeroOriginal() {
        return numeroOriginal;
    }

    public void setNumeroOriginal(String numeroOriginal) {
        this.numeroOriginal = numeroOriginal;
    }

    public List<LiquidacaoDoctoFiscal> getDoctoFiscais() {
        return doctoFiscais;
    }

    public void setDoctoFiscais(List<LiquidacaoDoctoFiscal> doctoFiscais) {
        this.doctoFiscais = doctoFiscais;
    }

    public List<Desdobramento> getDesdobramentos() {
        return desdobramentos;
    }

    public void setDesdobramentos(List<Desdobramento> desdobramentos) {
        this.desdobramentos = desdobramentos;
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

    public Boolean getTransportado() {
        return transportado;
    }

    public void setTransportado(Boolean transportado) {
        this.transportado = transportado;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Liquidacao)) {
            return false;
        }
        Liquidacao other = (Liquidacao) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public String getComplementoContabil() {
        String toReturn = "";
        if (this.numero != null) {
            toReturn = toReturn + this.numero + ", ";
        }
        if (this.dataLiquidacao != null) {
            toReturn = toReturn + this.dataLiquidacao + ", ";
        }
        if (this.empenho.getNumero() != null) {
            toReturn = toReturn + " Empenho: " + this.empenho.getNumero() + ", ";
        }
        if (this.empenho.getDataEmpenho() != null) {
            toReturn = toReturn + " Data Empenho " + this.empenho.getNumero() + ", ";
        }
        if (this.getEmpenho().getDespesaORC() != null) {
            toReturn = toReturn + this.getEmpenho().getDespesaORC().getCodigoReduzido() + ", ";
        }
        if (this.getEmpenho().getFonteDespesaORC() != null) {
            toReturn = toReturn + this.getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos().getCodigo() + " - " + this.getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos().getDescricao() + ".";
        }
        return toReturn;
    }

    @Override
    public String toString() {
        if (empenho != null) {
            return "N° " + numero + " - " + empenho.getFornecedor().getNome() + " - " + new SimpleDateFormat("dd/MM/yyyy").format(dataLiquidacao) + " - " + Util.formataValor(valor);
        }
        return "N° " + numero + " - " + new SimpleDateFormat("dd/MM/yyyy").format(dataLiquidacao) + " - " + Util.formataValor(valor);
    }

    private void gerarHistoricoNota() {
        String historico = "";
        if (this.getNumero() != null) {
            historico += "Liquidação: " + this.getNumero() + "/" + this.getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getEmpenho() != null) {
            historico += this.empenho.getNumeroExercicioParaHistorico() + UtilBeanContabil.SEPARADOR_HISTORICO;
            historico += " Dotação: " + this.getEmpenho().getDespesaORC().getHistoricoPadrao() + UtilBeanContabil.SEPARADOR_HISTORICO;
            historico += " Conta de Despesa: " + this.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo() + "." + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getDesdobramentos() != null) {
            for (Desdobramento desdobramento : this.getDesdobramentos()) {
                historico += " Detalhamento: " + desdobramento.getConta().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
        }
        if (this.getEmpenho().getFonteDespesaORC() != null) {
            historico += " Fonte de Recursos: " + ((ContaDeDestinacao) this.getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getEmpenho() != null) {
            historico += " Pessoa: " + this.getEmpenho().getFornecedor().getNomeCpfCnpj() + UtilBeanContabil.SEPARADOR_HISTORICO;
            historico += " Classe: " + this.getEmpenho().getClasseCredor().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getDoctoFiscais() != null) {
            for (LiquidacaoDoctoFiscal doc : this.getDoctoFiscais()) {
                historico += " Tipo: " + doc.getDoctoFiscalLiquidacao().getTipoDocumentoFiscal() + " -  Nº Documento Comprobatório: " + doc.getDoctoFiscalLiquidacao().getNumero() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
        }
        if (this.getComplemento() != null) {
            historico += " " + this.getComplemento();
        }
        this.historicoNota = historico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    private void gerarHistoricoRazao() {
        historicoRazao = " ";
        for (Desdobramento desd : this.getDesdobramentos()) {
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
        return numero + "/" + exercicio.getAno() + " - " + empenho.getNumero();
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        String retorno = toString();
        if (empenho.isEmpenhoRestoPagar()) {
            retorno = "Liquidação de Restos a Pagar: nº ";
            if (liquidacao != null) {
                retorno += liquidacao.getNumero() + " - " + DataUtil.getDataFormatada(liquidacao.getDataLiquidacao()) + " - ";
            } else {
                retorno += numero + " - " + DataUtil.getDataFormatada(dataLiquidacao) + " - ";
            }
            retorno += "Restos a Pagar: nº " + empenho.getNumero() + " - " + DataUtil.getDataFormatada(empenho.getDataEmpenho()) + " - " +
                "Empenho: nº " + empenho.getEmpenho().getNumero() + " - " + DataUtil.getDataFormatada(empenho.getEmpenho().getDataEmpenho()) + " - " +
                "Função " + empenho.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getFuncao().getCodigo() + " - " + empenho.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getFuncao().getDescricao() + " - " +
                "Subfunção " + empenho.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getSubFuncao().getCodigo() + " - " + empenho.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getSubFuncao().getDescricao() + " - " +
                "Conta de Despesa " + empenho.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo() + " - " + empenho.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getDescricao() + " - " +
                "Fonte de Recurso " + empenho.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getCodigo() + " - " + empenho.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getDescricao() + " - " +
                "Pessoa " + empenho.getFornecedor().getNome() + " - " +
                "Valor " + Util.formataValor(valor);
        }
        return retorno;
    }

    public boolean isIntegracaoDocumentoFiscal() {
        return empenho != null
            && (empenho.getTipoContaDespesa().isBemMovel()
            || empenho.getTipoContaDespesa().isEstoque());
    }

    public boolean isLiquidacaoPossuiObrigacoesPagar() {
        return this.getObrigacoesPagar() != null && !this.getObrigacoesPagar().isEmpty();
    }

    public boolean hasEmpenhoComDesdobramento() {
        return empenho != null && !empenho.getDesdobramentos().isEmpty();
    }

    public boolean isEmpenhoIntegracaoDiaria() {
        return this.getEmpenho() != null && this.getEmpenho().isEmpenhoIntegracaoDiaria();
    }

    public boolean isEmpenhoIntegracaoContrato() {
        return this.getEmpenho() != null && this.getEmpenho().isEmpenhoIntegracaoContrato();
    }

    public boolean isEmpenhoIntegracaoContratoOrReconhecimentoDividaOrAta() {
        return this.getEmpenho() != null &&
            (this.getEmpenho().isEmpenhoIntegracaoContrato()
            || this.getEmpenho().isEmpenhoIntegracaoReconhecimentoDivida()
            || this.getEmpenho().isEmpenhoIntegracaoExecucaoProcesso());
    }

    public boolean isLiquidacaoRestoPagar() {
        return this.categoriaOrcamentaria != null && CategoriaOrcamentaria.RESTO.equals(this.categoriaOrcamentaria);
    }

    public boolean isLiquidacaoNormal() {
        return this.categoriaOrcamentaria != null && CategoriaOrcamentaria.NORMAL.equals(this.categoriaOrcamentaria);
    }

    public BigDecimal getTotalDocumentos() {
        BigDecimal total = BigDecimal.ZERO;
        for (LiquidacaoDoctoFiscal dfl : this.getDoctoFiscais()) {
            total = total.add(dfl.getValorLiquidado());
        }
        return total;
    }

    public BigDecimal getTotalBaseCalculo() {
        BigDecimal total = BigDecimal.ZERO;
        for (LiquidacaoDoctoFiscal dfl : this.getDoctoFiscais()) {
            total = total.add(dfl.getValorBaseCalculo());
        }
        return total;
    }

    public BigDecimal getSaldoTotalDocumentos() {
        BigDecimal total = BigDecimal.ZERO;
        for (LiquidacaoDoctoFiscal dfl : this.getDoctoFiscais()) {
            total = total.add(dfl.getSaldo());
        }
        return total;
    }

    public BigDecimal getTotalDesdobramentos() {
        BigDecimal total = BigDecimal.ZERO;
        for (Desdobramento d : this.getDesdobramentos()) {
            total = total.add(d.getValor());
        }
        return total;
    }

    public BigDecimal getSaldoTotalDesdobramentos() {
        BigDecimal total = BigDecimal.ZERO;
        for (Desdobramento desd : this.getDesdobramentos()) {
            total = total.add(desd.getSaldo());
        }
        return total;
    }

    public BigDecimal getTotalObrigacaoPagar() {
        BigDecimal total = BigDecimal.ZERO;
        for (LiquidacaoObrigacaoPagar op : this.getObrigacoesPagar()) {
            total = total.add(op.getObrigacaoAPagar().getValor());
        }
        return total;
    }

    public BigDecimal getSaldoTotalObrigacaoPagar() {
        BigDecimal total = BigDecimal.ZERO;
        for (LiquidacaoObrigacaoPagar op : this.getObrigacoesPagar()) {
            total = total.add(op.getObrigacaoAPagar().getSaldo());
        }
        return total;
    }

    public void validarMesmoDocumentoEmLista(DoctoFiscalLiquidacao documentoFiscal) {
        ValidacaoException ve = new ValidacaoException();
        for (LiquidacaoDoctoFiscal doctoLiquidacao : this.getDoctoFiscais()) {
            if (!doctoLiquidacao.getDoctoFiscalLiquidacao().equals(documentoFiscal)
                && isMesmoDocumentoFiscal(doctoLiquidacao.getDoctoFiscalLiquidacao(), documentoFiscal)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O documento " + documentoFiscal.getTipoDocumentoFiscal() +
                    ", Nº" + documentoFiscal.getNumero() + "/" + documentoFiscal.getSerie() +
                    ", " + Util.formataValor(documentoFiscal.getValor()) + " já foi adicionado na lista.");
            }
        }
        ve.lancarException();
    }

    public boolean isMesmoDocumentoFiscal(DoctoFiscalLiquidacao documentoFiscalAdicionado, DoctoFiscalLiquidacao documentoFiscal) {
        return documentoFiscalAdicionado.getTipoDocumentoFiscal().getDescricao().equals(documentoFiscal.getTipoDocumentoFiscal().getDescricao())
            && documentoFiscalAdicionado.getValor().equals(documentoFiscal.getValor())
            && documentoFiscalAdicionado.getNumero().trim().toLowerCase().equals(documentoFiscal.getNumero().trim().toLowerCase());
    }

    public void validarMesmoDetalhamentoEmLista(Desdobramento desdSelecionado) {
        ValidacaoException ve = new ValidacaoException();
        for (Desdobramento desdEmLista : this.getDesdobramentos()) {
            if (!desdEmLista.equals(desdSelecionado)) {
                isMesmoDetalhamentoDespesa(desdSelecionado.getConta(), ve, desdEmLista);
            }
        }
        ve.lancarException();
    }

    public void validarMesmoDetalhamentoEmListaPoObrigacaoPagar(Desdobramento desdSelecionado, ObrigacaoAPagar obrigacaoAPagar) {
        ValidacaoException ve = new ValidacaoException();
        for (Desdobramento desdEmLista : this.getDesdobramentos()) {
            if (!desdEmLista.equals(desdSelecionado)
                && desdEmLista.getDesdobramentoObrigacaoPagar().getObrigacaoAPagar().equals(obrigacaoAPagar)) {
                isMesmoDetalhamentoDespesa(desdSelecionado.getConta(), ve, desdEmLista);
            }
        }
        ve.lancarException();
    }

    private void isMesmoDetalhamentoDespesa(Conta conta, ValidacaoException ve, Desdobramento desdEmLista) {
        if (desdEmLista.getConta().equals(conta)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" A conta " + conta + " já foi adicionada na lista.");
        }
    }

    public void validarSaldoDisponivelPorContaDespesa(BigDecimal valor, Conta conta, ObrigacaoAPagar obrigacaoAPagar) {
        ValidacaoException ve = new ValidacaoException();
        for (Desdobramento desd : getDesdobramentos()) {
            if (isLiquidacaoPossuiObrigacoesPagar()) {
                if (desd.getDesdobramentoObrigacaoPagar().getObrigacaoAPagar().equals(obrigacaoAPagar)) {
                    validarSaldoDetalhamentoPorContaDespesa(valor, ve, desd, conta);
                }
            } else {
                validarSaldoDetalhamentoPorContaDespesa(valor, ve, desd, conta);
            }
        }
        ve.lancarException();
    }

    private void validarSaldoDetalhamentoPorContaDespesa(BigDecimal valor, ValidacaoException ve, Desdobramento desdEmLista, Conta conta) {
        if (desdEmLista.getConta().equals(conta) && valor.compareTo(desdEmLista.getSaldo()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do detalhamento deve ser menor ou igual ao saldo de: " + Util.formataValor(desdEmLista.getSaldo())
                + " disponível para a conta: " + conta + ".");
        }
    }


    public void validarSaldoDisponivelPorDocumentoFiscal(BigDecimal valor, DoctoFiscalLiquidacao documentoFiscal) {
        ValidacaoException ve = new ValidacaoException();
        for (LiquidacaoDoctoFiscal doctoFiscal : getDoctoFiscais()) {
            if (doctoFiscal.getDoctoFiscalLiquidacao().equals(documentoFiscal) && valor.compareTo(doctoFiscal.getSaldo()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor documento deve ser menor ou igual ao saldo de: " + Util.formataValor(doctoFiscal.getSaldo())
                    + " disponível para o documento: " + documentoFiscal + ".");
            }
        }
        ve.lancarException();
    }

    public boolean isLiquidacaoRestoNaoProcessada() {
        return isLiquidacaoRestoPagar()
            && this.empenho != null
            && TipoRestosProcessado.NAO_PROCESSADOS.equals(this.empenho.getTipoRestosProcessados());
    }
}
