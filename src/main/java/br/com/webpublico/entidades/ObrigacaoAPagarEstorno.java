package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabilComValor;
import br.com.webpublico.util.DataUtil;
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
 * Created by mga on 26/07/2017.
 */
@Entity
@Audited
@Etiqueta("Estorno de Obrigação a Pagar")
@GrupoDiagrama(nome = "Contabil")
public class ObrigacaoAPagarEstorno extends SuperEntidadeContabilGerarContaAuxiliar implements EntidadeContabilComValor {


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


    public String getCodigoExtensaoFonteRecursoAsString() {
        return obrigacaoAPagar.getFonteDespesaORC().getProvisaoPPAFonte().getExtensaoFonteRecurso().getCodigo().toString();
    }

    public Integer getCodigoEs() {
        return obrigacaoAPagar.getFonteDespesaORC().getProvisaoPPAFonte().getExtensaoFonteRecurso().getCodigoEs();
    }

    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        AcaoPPA acaoPPA = obrigacaoAPagar.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        return new GeradorContaAuxiliarDTO(getUnidadeOrganizacional(),
            acaoPPA.getCodigoFuncaoSubFuncao(),
            obrigacaoAPagar.getContaDeDestinacao(),
            obrigacaoAPagar.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(),
            getCodigoEs(),
            null, null, getExercicio(), null, null, 0, null,
            obrigacaoAPagar.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigoContaSiconf());
    }
}
