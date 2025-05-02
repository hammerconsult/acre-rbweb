/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.entidadesauxiliares.manad.ManadRegistro;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoDespesaORC;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IManadRegistro;
import br.com.webpublico.negocios.manad.ManadContabilFacade;
import br.com.webpublico.util.ManadUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author reidocrime
 */
@GrupoDiagrama(nome = "Orcamentario")
@Etiqueta("Anulações")
@Audited
@Entity

public class AnulacaoORC extends SuperEntidadeContabilGerarContaAuxiliar implements IManadRegistro, EntidadeContabil {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Valor")
    @Tabelavel
    @Monetario
    private BigDecimal valor;

    @ManyToOne
    @Etiqueta("Alteração orçamentária")
    @Tabelavel
    private AlteracaoORC alteracaoORC;

    @ManyToOne
    @Etiqueta("Fonte de Recurso")
    @Tabelavel
    private FonteDespesaORC fonteDespesaORC;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Crédito")
    @Tabelavel
    private TipoDespesaORC tipoDespesaORC;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    @Etiqueta("Mês")
    private Integer mes;

    @Monetario
    @Etiqueta("Saldo")
    private BigDecimal saldo;

    private String historicoNota;
    private String historicoRazao;

    private Boolean bloqueado;

    @ManyToOne
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    private EventoContabil eventoContabil;

    @Transient
    @Invisivel
    private Boolean permiteAlteracaoGrupoOrcamentario = null;

    @Transient
    @Etiqueta("Mês")
    private Mes mesAnulacao;

    @Version
    private Long versao;

    public AnulacaoORC() {
        valor = new BigDecimal(BigInteger.ZERO);
        saldo = new BigDecimal(BigInteger.ZERO);
        dataRegistro = new Date();
        mes = new Integer(0);
        bloqueado = true;
    }

    public AnulacaoORC(BigDecimal valor, AlteracaoORC alteracaoORC, FonteDespesaORC fonteDespesaORC, Date dataRegistro, Integer mes, BigDecimal saldo) {
        this.valor = valor;
        this.alteracaoORC = alteracaoORC;
        this.fonteDespesaORC = fonteDespesaORC;
        this.dataRegistro = dataRegistro;
        this.mes = mes;
        this.saldo = saldo;
    }

    public Mes getMesAnulacao() {
        return mesAnulacao;
    }

    public void setMesAnulacao(Mes mesAnulacao) {
        this.mesAnulacao = mesAnulacao;
    }

    public Boolean getPermiteAlteracaoGrupoOrcamentario() {
        return permiteAlteracaoGrupoOrcamentario;
    }

    public void setPermiteAlteracaoGrupoOrcamentario(Boolean permiteAlteracaoGrupoOrcamentario) {
        this.permiteAlteracaoGrupoOrcamentario = permiteAlteracaoGrupoOrcamentario;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AlteracaoORC getAlteracaoORC() {
        return alteracaoORC;
    }

    public void setAlteracaoORC(AlteracaoORC alteracaoORC) {
        this.alteracaoORC = alteracaoORC;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public TipoDespesaORC getTipoDespesaORC() {
        return tipoDespesaORC;
    }

    public void setTipoDespesaORC(TipoDespesaORC tipoDespesaORC) {
        this.tipoDespesaORC = tipoDespesaORC;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Boolean getBloqueado() {
        return bloqueado == null ? false : bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public void gerarHistoricoNota() {
        historicoNota = "";
        if (this.getAlteracaoORC().getCodigo() != null) {
            historicoNota += "N°" + this.getAlteracaoORC().getCodigo() + "/" + Util.getAnoDaData(this.getAlteracaoORC().getDataAlteracao()) + ",";
        }
        if (this.getFonteDespesaORC() != null) {
            historicoNota += " Elemento de Despesa: " + this.getFonteDespesaORC() + ",";
        }
        if (this.getTipoDespesaORC() != null) {
            historicoNota += " Tipo de Despesa: " + this.getTipoDespesaORC().getDescricao() + ",";
        }
        historicoNota = historicoNota + this.getAlteracaoORC().getJustificativaAnulacao();
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil().getClpHistoricoContabil() != null) {
            historicoEvento = this.getEventoContabil().getClpHistoricoContabil().toString();
        }
        this.historicoRazao = historicoEvento + " " + this.historicoNota;
        this.historicoRazao = Util.cortarString(this.historicoRazao, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos() {
        gerarHistoricoNota();
        gerarHistoricoRazao();
    }

    @Override
    public String toString() {
        try {
            return this.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa() + " - " + this.alteracaoORC + " " + Util.formataValor(this.valor) + "(" + new SimpleDateFormat("dd/MM/yyyy").format(this.alteracaoORC.getDataAlteracao()) + ")";
        } catch (NullPointerException e) {
            return this.alteracaoORC + " " + Util.formataValor(this.valor) + "(" + new SimpleDateFormat("dd/MM/yyyy").format(this.alteracaoORC.getDataAlteracao()) + ")";
        }
    }


    @Override
    public ManadRegistro.ManadModulo getModulo() {
        return ManadRegistro.ManadModulo.CONTABIL;
    }

    @Override
    public ManadRegistro.ManadRegistroTipo getTipoRegistro() {
        return ManadRegistro.ManadRegistroTipo.L300;
    }

    @Override
    public void getConteudoManad(ManadContabilFacade facade, StringBuilder conteudo) {
        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.L300.name(), conteudo);
        ManadUtil.criaLinha(2, this.getAlteracaoORC().getAtoLegal().getNumero(), conteudo);
        ManadUtil.criaLinha(3, ManadUtil.getDataSemBarra(this.getAlteracaoORC().getAtoLegal().getDataPublicacao()), conteudo);
        ManadUtil.criaLinha(4, ManadUtil.getValor(BigDecimal.ZERO), conteudo);
        ManadUtil.criaLinha(5, ManadUtil.getValor(this.getValor()), conteudo);
        ManadUtil.criaLinha(6,
            tipoDespesaORC.equals(TipoDespesaORC.SUPLEMENTAR) ? "1"
                : tipoDespesaORC.equals(TipoDespesaORC.ESPECIAL) ? "2"
                : tipoDespesaORC.equals(TipoDespesaORC.EXTRAORDINARIA) ? "3" : "0", conteudo);
        ManadUtil.criaLinha(7, "1", conteudo);
        ManadUtil.quebraLinha(conteudo);
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return alteracaoORC.getCodigo();
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    public String getCodigoExtensaoFonteRecursoAsString() {
        return getFonteDespesaORC().getProvisaoPPAFonte().getExtensaoFonteRecurso().getCodigo().toString();
    }

    public Integer getCodigoEs() {
        return getFonteDespesaORC().getProvisaoPPAFonte().getExtensaoFonteRecurso().getCodigoEs();
    }

    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        AcaoPPA acaoPPA = fonteDespesaORC.getProvisaoPPAFonte().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        return new GeradorContaAuxiliarDTO(fonteDespesaORC.getProvisaoPPAFonte().getProvisaoPPADespesa().getUnidadeOrganizacional(),
            acaoPPA.getCodigoFuncaoSubFuncao(),
            getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao(),
            fonteDespesaORC.getProvisaoPPAFonte().getProvisaoPPADespesa().getContaDeDespesa(),
            getCodigoEs(),
            null, null, alteracaoORC.getExercicio(), null, null, 0, null,
            fonteDespesaORC.getProvisaoPPAFonte().getProvisaoPPADespesa().getContaDeDespesa().getCodigoContaSiconf());
    }
}
