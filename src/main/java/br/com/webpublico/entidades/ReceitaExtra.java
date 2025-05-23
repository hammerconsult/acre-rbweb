/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.SituacaoReceitaExtra;
import br.com.webpublico.enums.TipoConsignacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author venon
 */
@Entity
@GrupoDiagrama(nome = "Contabil")
@Audited
@Etiqueta("Receita Extraorçamentária")
public class ReceitaExtra extends SuperEntidade implements EntidadeContabil, IGeraContaAuxiliar {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Data")
    @Obrigatorio
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ReprocessamentoContabil
    private Date dataReceita;
    @Etiqueta("Número")
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    private String numero;
    @Etiqueta("Data da Concilição")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ErroReprocessamentoContabil
    @Tabelavel
    private Date dataConciliacao;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Conta Financeira")
    @ManyToOne
    private SubConta subConta;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;
    @Obrigatorio
    @Etiqueta("Conta de Destinação de Recurso")
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.ESQUERDA)
    @Pesquisavel
    @ManyToOne
    private ContaDeDestinacao contaDeDestinacao;
    @Obrigatorio
    @Etiqueta("Conta Extraorçamentária")
    @ManyToOne
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.ESQUERDA)
    @Pesquisavel
    private Conta contaExtraorcamentaria;
    @ManyToOne
    @ReprocessamentoContabil
    @Etiqueta(value = "Evento Contábil")
    @Tabelavel
    @Pesquisavel
    private EventoContabil eventoContabil;
    @ManyToOne
    @Obrigatorio
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.ESQUERDA)
    @Pesquisavel
    @Etiqueta("Pessoa")
    private Pessoa pessoa;
    @ManyToOne
    @Obrigatorio
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.ESQUERDA)
    @Pesquisavel
    @Etiqueta("Classe")
    private ClasseCredor classeCredor;
    @Obrigatorio
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    @Etiqueta("Valor (R$)")
    @Monetario
    private BigDecimal valor;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Pesquisavel
    @Etiqueta("Saldo (R$)")
    private BigDecimal saldo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Transportada")
    private Boolean transportado;
    @Obrigatorio
    @Etiqueta("Histórico")
    private String complementoHistorico;
    @Obrigatorio
    @Etiqueta("Usuário do Sistema")
    @ManyToOne
    @Pesquisavel
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;
    @OneToOne
    private RetencaoPgto retencaoPgto;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Situação")
    @Tabelavel
    private SituacaoReceitaExtra situacaoReceitaExtra;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Tipo de Consignação")
    @Tabelavel
    private TipoConsignacao tipoConsignacao;
    @ManyToOne
    private Exercicio exercicio;
    @Etiqueta("Unidade Organizacional Administrativa")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacionalAdm;
    private String migracaoChave;
    //coluna adicionada para trabalhar com a receita extra na depesa extra e estorno despesa extra.
    @Monetario
    private BigDecimal valorEstornado;
    private String historicoNota;
    private String historicoRazao;
    @Transient
    private String codigoUnidade;
    private String uuid;
    @ManyToOne
    private Identificador identificador;
    @Version
    private Long versao;

    @Pesquisavel
    @Etiqueta("Receita de Ajuste em Depósito")
    private Boolean receitaDeAjusteDeposito;

    public ReceitaExtra() {
        valor = new BigDecimal(BigInteger.ZERO);
        saldo = new BigDecimal(BigInteger.ZERO);
        valorEstornado = new BigDecimal(BigInteger.ZERO);
        transportado = false;
        receitaDeAjusteDeposito = false;
        uuid = UUID.randomUUID().toString();
    }

    public Boolean getReceitaDeAjusteDeposito() {
        return receitaDeAjusteDeposito;
    }

    public void setReceitaDeAjusteDeposito(Boolean receitaDeAjusteDeposito) {
        this.receitaDeAjusteDeposito = receitaDeAjusteDeposito;
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

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public RetencaoPgto getRetencaoPgto() {
        return retencaoPgto;
    }

    public void setRetencaoPgto(RetencaoPgto retencaoPgto) {
        this.retencaoPgto = retencaoPgto;
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

    public Conta getContaExtraorcamentaria() {
        return contaExtraorcamentaria;
    }

    public void setContaExtraorcamentaria(Conta contaExtraorcamentaria) {
        this.contaExtraorcamentaria = contaExtraorcamentaria;
    }

    public Date getDataReceita() {
        return dataReceita;
    }

    public void setDataReceita(Date dataReceita) {
        this.dataReceita = dataReceita;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public SituacaoReceitaExtra getSituacaoReceitaExtra() {
        return situacaoReceitaExtra;
    }

    public void setSituacaoReceitaExtra(SituacaoReceitaExtra situacaoReceitaExtra) {
        this.situacaoReceitaExtra = situacaoReceitaExtra;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalAdm() {
        return unidadeOrganizacionalAdm;
    }

    public void setUnidadeOrganizacionalAdm(UnidadeOrganizacional unidadeOrganizacionalAdm) {
        this.unidadeOrganizacionalAdm = unidadeOrganizacionalAdm;
    }

    public Date getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(Date dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
    }

    public TipoConsignacao getTipoConsignacao() {
        return tipoConsignacao;
    }

    public void setTipoConsignacao(TipoConsignacao tipoConsignacao) {
        this.tipoConsignacao = tipoConsignacao;
    }

    public BigDecimal getValorEstornado() {
        return valorEstornado;
    }

    public void setValorEstornado(BigDecimal valorEstornado) {
        this.valorEstornado = valorEstornado;
    }

    public Boolean getTransportado() {
        return transportado;
    }

    public void setTransportado(Boolean transportado) {
        this.transportado = transportado;
    }


    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Identificador getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Identificador identificador) {
        this.identificador = identificador;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public void gerarHistoricoNota() {
        historicoNota = " ";
        if (this.getNumero() != null) {
            historicoNota += "N: " + this.getNumero() + "/" + this.getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getRetencaoPgto() != null) {
            if (this.getRetencaoPgto().getPagamento() != null) {
                Pagamento pagamento = this.getRetencaoPgto().getPagamento();
                historicoNota += " Pagamento: " + pagamento.getNumeroPagamento() + "/" + pagamento.getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
                historicoNota += " Liquidação: " + pagamento.getLiquidacao().getNumero() + "/" + pagamento.getLiquidacao().getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
                historicoNota += pagamento.getLiquidacao().getEmpenho().getNumeroExercicioParaHistorico() + UtilBeanContabil.SEPARADOR_HISTORICO;
                historicoNota += " Dotação: " + pagamento.getLiquidacao().getEmpenho().getDespesaORC().getHistoricoPadrao() + UtilBeanContabil.SEPARADOR_HISTORICO;
                historicoNota += " Conta de Despesa: " + pagamento.getLiquidacao().getEmpenho().getDespesaORC().getDescricaoContaDespesaPPA() + UtilBeanContabil.SEPARADOR_HISTORICO;
                if (pagamento.getLiquidacao().getDesdobramentos() != null) {
                    for (Desdobramento desdobramento : pagamento.getLiquidacao().getDesdobramentos()) {
                        historicoNota += " Detalhamento: " + desdobramento.getConta().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
                    }
                }
            }
        }

        if (this.getSubConta() != null) {
            historicoNota += "Conta Financeira: " + this.getSubConta() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getContaDeDestinacao() != null) {
            historicoNota += "Conta de Destinação de Recurso: " + this.getContaDeDestinacao().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getContaExtraorcamentaria() != null) {
            historicoNota += "Conta Extraorçamentária: " + this.getContaExtraorcamentaria().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getPessoa() != null) {
            historicoNota += "Pessoa: " + this.getPessoa().getNomeCpfCnpj() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getClasseCredor() != null) {
            historicoNota += "Classe: " + this.getClasseCredor().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historicoNota = historicoNota + complementoHistorico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil() != null) {
            if (this.getEventoContabil().getClpHistoricoContabil() != null) {
                historicoEvento = this.getEventoContabil().getClpHistoricoContabil().toString();
            }
        }
        this.historicoRazao = historicoEvento + this.historicoNota;
        this.historicoRazao = Util.cortarString(this.historicoRazao, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos() {
        gerarHistoricoNota();
        gerarHistoricoRazao();
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
        if (!(object instanceof ReceitaExtra)) {
            return false;
        }
        ReceitaExtra other = (ReceitaExtra) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (numero != null) {
            return "N° " + numero + " - " + new SimpleDateFormat("dd/MM/yyyy").format(dataReceita) + " - " + Util.formataValor(valor);
        }
        return " N° -- " + new SimpleDateFormat("dd/MM/yyyy").format(dataReceita) + " - " + Util.formataValor(valor);
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        try {
            return numero + "/" + exercicio.getAno() + " - " + retencaoPgto.getPagamento().getNumeroPagamento();
        } catch (Exception e) {
            return numero + "/" + exercicio.getAno();
        }
    }

    public boolean isReceitaAberta() {
        return this.getSituacaoReceitaExtra() != null && SituacaoReceitaExtra.ABERTO.equals(this.situacaoReceitaExtra);
    }

    public boolean isReceitaEfetuada() {
        return this.getSituacaoReceitaExtra() != null && SituacaoReceitaExtra.EFETUADO.equals(this.situacaoReceitaExtra);
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
        AcaoPPA acaoPPA = null;
        if (retencaoPgto != null) {
            acaoPPA = getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(getUnidadeOrganizacional());
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
                    getEmpenho().getContaDespesa(),
                    (getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "99":
                if (CategoriaOrcamentaria.RESTO.equals(retencaoPgto.getPagamento().getCategoriaOrcamentaria())) {
                    return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada9(getUnidadeOrganizacional(),
                        acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                        getContaDeDestinacao(),
                        getEmpenho().getContaDespesa(),
                        (getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                            getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                                getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                                getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                        getEmpenho().getEmpenho().getExercicio().getAno(),
                        getEmpenho().getExercicio(),
                        getEmpenho().getEmpenho().getExercicio());
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
        AcaoPPA acaoPPA = null;
        if (retencaoPgto != null) {
            acaoPPA = getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(getUnidadeOrganizacional());
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
                    (getEmpenho().getContaDespesa().getCodigoSICONFI() != null ?
                        getEmpenho().getContaDespesa().getCodigoSICONFI() :
                        getEmpenho().getContaDespesa().getCodigo().replace(".", "")),
                    (getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "99":
                if (CategoriaOrcamentaria.RESTO.equals(retencaoPgto.getPagamento().getCategoriaOrcamentaria())) {
                    return UtilGeradorContaAuxiliar.gerarContaAuxiliar9(unidadeOrganizacional,
                        acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                        getContaDeDestinacao(),
                        getEmpenho().getContaDespesa(),
                        (getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                            getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                                getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                                getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                        getEmpenho().getEmpenho().getExercicio().getAno(),
                        getEmpenho().getExercicio());
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

    private Empenho getEmpenho() {
        return retencaoPgto.getPagamento().getLiquidacao().getEmpenho();
    }
}

