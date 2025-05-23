/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.manad.ManadRegistro;
import br.com.webpublico.enums.CategoriaConta;
import br.com.webpublico.enums.EsferaOrcamentaria;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.interfaces.IManadRegistro;
import br.com.webpublico.negocios.manad.ManadContabilFacade;
import br.com.webpublico.util.*;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.TreeMap;

/**
 * @author peixe
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Audited
public class ReceitaLOAFonte implements Serializable, EntidadeContabil, IManadRegistro, IGeraContaAuxiliar {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Receita Loa")
    private ReceitaLOA receitaLOA;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Conta de Destinação")
    private ContaDeDestinacao destinacaoDeRecursos;
    @Tabelavel
    @Etiqueta("Valor (R$)")
    @Monetario
    private BigDecimal valor;
    private Boolean rounding;
    private BigDecimal percentual;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Transient
    private Long criadoEm;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta("Esfera Orçamentária")
    private EsferaOrcamentaria esferaOrcamentaria;
    private String historicoNota;
    private String historicoRazao;
    @ManyToOne
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    private EventoContabil eventoContabil;
    private Long item;

    public ReceitaLOAFonte() {
        criadoEm = System.nanoTime();
        dataRegistro = new Date();
        rounding = false;
        valor = new BigDecimal(BigInteger.ZERO);
        percentual = new BigDecimal(BigInteger.ZERO);
        item = 1l;
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

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public Date getDateRegistro() {
        return dataRegistro;
    }

    public void setDateRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public ContaDeDestinacao getDestinacaoDeRecursos() {
        return destinacaoDeRecursos;
    }

    public void setDestinacaoDeRecursos(ContaDeDestinacao destinacaoDeRecursos) {
        this.destinacaoDeRecursos = destinacaoDeRecursos;
    }

    public ReceitaLOA getReceitaLOA() {
        return receitaLOA;
    }

    public void setReceitaLOA(ReceitaLOA receitaLOA) {
        this.receitaLOA = receitaLOA;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getRounding() {
        return rounding;
    }

    public void setRounding(Boolean rounding) {
        this.rounding = rounding;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public EsferaOrcamentaria getEsferaOrcamentaria() {
        return esferaOrcamentaria;
    }

    public void setEsferaOrcamentaria(EsferaOrcamentaria esferaOrcamentaria) {
        this.esferaOrcamentaria = esferaOrcamentaria;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Long getItem() {
        return item;
    }

    public void setItem(Long item) {
        this.item = item;
    }

    public void gerarHistoricoNota() {
        historicoNota = " ";
        if (this.getReceitaLOA() != null) {
            historicoNota += this.getReceitaLOA().getLoa().getLdo().getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getReceitaLOA().getContaDeReceita() != null) {
            historicoNota += " Conta de Receita: " + this.getReceitaLOA().getContaDeReceita().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getDestinacaoDeRecursos().getFonteDeRecursos() != null) {
            historicoNota += " Fonte de Recursos: " + this.getDestinacaoDeRecursos().getFonteDeRecursos().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historicoNota = historicoNota + " " + this.getReceitaLOA().getComplementoContabil();
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

    public FonteDeRecursos getFonteRecurso() {
        return ((ContaDeDestinacao) destinacaoDeRecursos).getFonteDeRecursos();
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "Receita: " + receitaLOA + " Destinação: " + destinacaoDeRecursos + " Valor: " + Util.formataValor(valor) + "(Conjunto: " + item + ")";
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return receitaLOA.getContaDeReceita().getCodigo() + " - " + receitaLOA.getContaDeReceita().getDescricao() + " - FR: " + destinacaoDeRecursos.getFonteDeRecursos().getCodigo() + " - " +
            destinacaoDeRecursos.getFonteDeRecursos().getDescricao();
    }

    @Override
    public ManadRegistro.ManadModulo getModulo() {
        return ManadRegistro.ManadModulo.CONTABIL;
    }

    @Override
    public ManadRegistro.ManadRegistroTipo getTipoRegistro() {
        return ManadRegistro.ManadRegistroTipo.L200;
    }

    @Override
    public void getConteudoManad(ManadContabilFacade facade, StringBuilder conteudo) {
        Date data = this.getReceitaLOA().getLoa().getDataEfetivacao();
        UnidadeOrganizacional unidadeOrganizacional = getReceitaLOA().getEntidade();

        HierarquiaOrganizacional unidade = ((ManadContabilFacade) facade).getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(data, unidadeOrganizacional, TipoHierarquiaOrganizacional.ORCAMENTARIA);
        HierarquiaOrganizacional orgao = ((ManadContabilFacade) facade).getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(data, unidade.getSuperior(), TipoHierarquiaOrganizacional.ORCAMENTARIA);

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.L200.name(), conteudo);
        ManadUtil.criaLinha(2, getReceitaLOA().getLoa().getLdo().getExercicio().getAno().toString(), conteudo);
        ManadUtil.criaLinha(3, getReceitaLOA().getContaDeReceita().getCodigo(), conteudo);
        ManadUtil.criaLinha(4, orgao.getCodigoNo() + unidade.getCodigoNo(), conteudo);
        ManadUtil.criaLinha(5, ManadUtil.getValor(this.getValor()), conteudo);
        ManadUtil.criaLinha(6, ManadUtil.getValor(BigDecimal.ZERO), conteudo);
        ManadUtil.criaLinha(7, "", conteudo);
        ManadUtil.criaLinha(8, getReceitaLOA().getContaDeReceita().getDescricao(), conteudo);
        ManadUtil.criaLinha(9, getReceitaLOA().getContaDeReceita().getCategoria().equals(CategoriaConta.ANALITICA) ? "A" : "S", conteudo);
        ManadUtil.criaLinha(10, getReceitaLOA().getContaDeReceita().getNivel() + "", conteudo);
        ManadUtil.quebraLinha(conteudo);
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
        switch (tipoContaAuxiliar.getCodigo()) {
            case "96":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada6(receitaLOA.getEntidade(),
                    destinacaoDeRecursos,
                    receitaLOA.getContaDeReceita(),
                    receitaLOA.getLoa().getLdo().getExercicio());
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
        switch (tipoContaAuxiliar.getCodigo()) {
            case "96":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar6(receitaLOA.getEntidade(),
                    destinacaoDeRecursos,
                    (!Strings.isNullOrEmpty(receitaLOA.getContaDeReceita().getCodigoSICONFI()) ?
                        receitaLOA.getContaDeReceita().getCodigoSICONFI() :
                        receitaLOA.getContaDeReceita().getCodigo()).replace(".", ""));
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
}
