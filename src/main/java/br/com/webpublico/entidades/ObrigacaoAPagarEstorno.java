package br.com.webpublico.entidades;

import br.com.webpublico.enums.CategoriaOrcamentaria;
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
 * Created by mga on 26/07/2017.
 */
@Entity
@Audited
@Etiqueta("Estorno de Obrigação a Pagar")
@GrupoDiagrama(nome = "Contabil")
public class ObrigacaoAPagarEstorno extends SuperEntidade implements EntidadeContabilComValor, IGeraContaAuxiliar {


    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private String numero;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data")
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataEstorno;

    @Etiqueta("Obrigação a Pagar")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    private ObrigacaoAPagar obrigacaoAPagar;

    @Etiqueta("Unidade Organizacional")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    @Etiqueta("Usuário Sistema")
    @ManyToOne
    private UsuarioSistema usuarioSistema;

    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;

    @Tabelavel
    @Pesquisavel
    @Monetario
    @Obrigatorio
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;

    @Etiqueta("Histórico")
    @Obrigatorio
    private String historico;
    private String historicoNota;
    private String historicoRazao;

    @OneToMany(mappedBy = "obrigacaoAPagarEstorno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DesdobramentoObrigacaoAPagarEstorno> desdobramentos;

    @OneToMany(mappedBy = "obrigacaoAPagarEstorno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObrigacaoPagarEstornoDoctoFiscal> documentosFiscais;

    public ObrigacaoAPagarEstorno() {
        valor = BigDecimal.ZERO;
        desdobramentos = Lists.newArrayList();
        documentosFiscais = Lists.newArrayList();
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public List<DesdobramentoObrigacaoAPagarEstorno> getDesdobramentos() {
        return desdobramentos;
    }

    public void setDesdobramentos(List<DesdobramentoObrigacaoAPagarEstorno> desdobramentos) {
        this.desdobramentos = desdobramentos;
    }

    public List<ObrigacaoPagarEstornoDoctoFiscal> getDocumentosFiscais() {
        return documentosFiscais;
    }

    public void setDocumentosFiscais(List<ObrigacaoPagarEstornoDoctoFiscal> documentosFiscais) {
        this.documentosFiscais = documentosFiscais;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public ObrigacaoAPagar getObrigacaoAPagar() {
        return obrigacaoAPagar;
    }

    public void setObrigacaoAPagar(ObrigacaoAPagar obrigacaoAPagar) {
        this.obrigacaoAPagar = obrigacaoAPagar;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
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

    @Override
    public BigDecimal getValor() {
        return valor;
    }

    @Override
    public String toString() {
        try {
            return "N° " + numero + " - " + DataUtil.getDataFormatada(dataEstorno) + " - " + Util.formataValor(valor);
        } catch (Exception ex) {
            return "";
        }
    }

    private void gerarHistoricoNota() {
        String historico = "";
        if (this.getNumero() != null) {
            historico += "Estorno Obrigação a Pagar: " + this.getNumero() + "/" + this.getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getObrigacaoAPagar().getEmpenho() != null) {
            historico += this.getObrigacaoAPagar().getEmpenho().getNumeroExercicioParaHistorico() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historico += " Dotação: " + this.getObrigacaoAPagar().getDespesaORC().getHistoricoPadrao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        historico += " Conta de Despesa: " + this.getObrigacaoAPagar().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo() + UtilBeanContabil.SEPARADOR_HISTORICO;
        if (this.getDesdobramentos() != null) {
            for (DesdobramentoObrigacaoAPagarEstorno desdobramento : this.getDesdobramentos()) {
                historico += " Detalhamento: " + desdobramento.getConta().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
        }
        historico += " Fonte de Recursos: " + ((ContaDeDestinacao) this.getObrigacaoAPagar().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        historico += " Pessoa: " + this.getObrigacaoAPagar().getPessoa().getNomeCpfCnpj() + UtilBeanContabil.SEPARADOR_HISTORICO;
        historico += " Classe: " + this.getObrigacaoAPagar().getClasseCredor().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;

        if (this.getDocumentosFiscais() != null) {
            for (ObrigacaoPagarEstornoDoctoFiscal doc : this.getDocumentosFiscais()) {
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
        for (DesdobramentoObrigacaoAPagarEstorno desd : this.getDesdobramentos()) {
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
        if (getObrigacaoAPagar().getEmpenho() != null) {
            return numero + "/" + exercicio.getAno() + " - " + getObrigacaoAPagar().getEmpenho().getNumero();
        }
        return numero + "/" + exercicio.getAno();
    }

    public BigDecimal getValorTotalDetalhamentos() {
        BigDecimal valor = BigDecimal.ZERO;
        if (getDesdobramentos() != null && !getDesdobramentos().isEmpty()) {
            for (DesdobramentoObrigacaoAPagarEstorno desdobramento : getDesdobramentos()) {
                valor = valor.add(desdobramento.getValor());
            }
        }
        return valor;
    }

    public BigDecimal getValorTotalDocumentos() {
        BigDecimal total = BigDecimal.ZERO;
        if (getDocumentosFiscais() != null && !getDocumentosFiscais().isEmpty()) {
            for (ObrigacaoPagarEstornoDoctoFiscal docto : getDocumentosFiscais()) {
                if (docto.getDocumentoFiscal() != null) {
                    total = total.add(docto.getValor());
                }
            }
        }
        return total;
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
        AcaoPPA acaoPPA = obrigacaoAPagar.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada2(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada4(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    obrigacaoAPagar.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao(),
                    getExercicio());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada5(getUnidadeOrganizacional(),
                    obrigacaoAPagar.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao(),
                    getExercicio());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada7(getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    obrigacaoAPagar.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao(),
                    obrigacaoAPagar.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(),
                    (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));

            case "99":
                if (obrigacaoAPagar != null && obrigacaoAPagar.getEmpenho() != null) {
                    if (CategoriaOrcamentaria.NORMAL.equals(obrigacaoAPagar.getEmpenho().getCategoriaOrcamentaria())) {
                        return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada9(unidadeOrganizacional,
                            acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                            obrigacaoAPagar.getContaDeDestinacao(),
                            obrigacaoAPagar.getContaDespesa(),
                            (obrigacaoAPagar.getEmpenho().getExtensaoFonteRecurso().getCodigo().toString().startsWith("4") ? 2 :
                                obrigacaoAPagar.getEmpenho().getExtensaoFonteRecurso().getCodigo().toString().startsWith("1") ||
                                    obrigacaoAPagar.getEmpenho().getExtensaoFonteRecurso().getCodigo().toString().startsWith("2") ||
                                    obrigacaoAPagar.getEmpenho().getExtensaoFonteRecurso().getCodigo().toString().startsWith("3") ? 1 : 0),
                            exercicio.getAno(),
                            exercicio,
                            exercicio);
                    }

                    if (CategoriaOrcamentaria.RESTO.equals(obrigacaoAPagar.getEmpenho().getCategoriaOrcamentaria())) {
                        return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada9(getUnidadeOrganizacional(),
                            acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                            obrigacaoAPagar.getEmpenho().getContaDeDestinacao(),
                            obrigacaoAPagar.getEmpenho().getContaDespesa(),
                            (obrigacaoAPagar.getEmpenho().getExtensaoFonteRecurso().getCodigo().toString().startsWith("4") ? 2 :
                                obrigacaoAPagar.getEmpenho().getExtensaoFonteRecurso().getCodigo().toString().startsWith("1") ||
                                    obrigacaoAPagar.getEmpenho().getExtensaoFonteRecurso().getCodigo().toString().startsWith("2") ||
                                    obrigacaoAPagar.getEmpenho().getExtensaoFonteRecurso().getCodigo().toString().startsWith("3") ? 1 : 0),
                            obrigacaoAPagar.getEmpenho().getEmpenho().getExercicio().getAno(),
                            obrigacaoAPagar.getEmpenho().getExercicio(),
                            obrigacaoAPagar.getEmpenho().getEmpenho().getExercicio());
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
        AcaoPPA acaoPPA = obrigacaoAPagar.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar2(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar4(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    obrigacaoAPagar.getContaDeDestinacao());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar5(getUnidadeOrganizacional(),
                    obrigacaoAPagar.getContaDeDestinacao());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar7(getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    obrigacaoAPagar.getContaDeDestinacao(),
                    (obrigacaoAPagar.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigoSICONFI() != null ?
                        obrigacaoAPagar.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigoSICONFI() :
                        obrigacaoAPagar.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo().replace(".", "")),
                    (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "99":
                if (obrigacaoAPagar != null && obrigacaoAPagar.getEmpenho() != null) {
                    if (CategoriaOrcamentaria.NORMAL.equals(obrigacaoAPagar.getEmpenho().getCategoriaOrcamentaria())) {
                        return UtilGeradorContaAuxiliar.gerarContaAuxiliar9(unidadeOrganizacional,
                            acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                            obrigacaoAPagar.getContaDeDestinacao(),
                            obrigacaoAPagar.getContaDespesa(),
                            (obrigacaoAPagar.getEmpenho().getExtensaoFonteRecurso().getCodigo().toString().startsWith("4") ? 2 :
                                obrigacaoAPagar.getEmpenho().getExtensaoFonteRecurso().getCodigo().toString().startsWith("1") ||
                                    obrigacaoAPagar.getEmpenho().getExtensaoFonteRecurso().getCodigo().toString().startsWith("2") ||
                                    obrigacaoAPagar.getEmpenho().getExtensaoFonteRecurso().getCodigo().toString().startsWith("3") ? 1 : 0),
                            exercicio.getAno(),
                            exercicio);
                    }
                    if (CategoriaOrcamentaria.RESTO.equals(obrigacaoAPagar.getEmpenho().getCategoriaOrcamentaria())) {
                        return UtilGeradorContaAuxiliar.gerarContaAuxiliar9(getUnidadeOrganizacional(),
                            acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                            obrigacaoAPagar.getEmpenho().getContaDeDestinacao(),
                            obrigacaoAPagar.getEmpenho().getContaDespesa(),
                            (obrigacaoAPagar.getEmpenho().getExtensaoFonteRecurso().getCodigo().toString().startsWith("4") ? 2 :
                                obrigacaoAPagar.getEmpenho().getExtensaoFonteRecurso().getCodigo().toString().startsWith("1") ||
                                    obrigacaoAPagar.getEmpenho().getExtensaoFonteRecurso().getCodigo().toString().startsWith("2") ||
                                    obrigacaoAPagar.getEmpenho().getExtensaoFonteRecurso().getCodigo().toString().startsWith("3") ? 1 : 0),
                            obrigacaoAPagar.getEmpenho().getEmpenho().getExercicio().getAno(),
                            obrigacaoAPagar.getEmpenho().getExercicio());
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
        return obrigacaoAPagar.getFonteDespesaORC().getProvisaoPPAFonte().getExtensaoFonteRecurso().getCodigo().toString();
    }
}
