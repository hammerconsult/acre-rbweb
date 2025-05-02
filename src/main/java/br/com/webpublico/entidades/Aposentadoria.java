/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.esocial.TipoBeneficioEsocial;
import br.com.webpublico.entidades.rh.esocial.TipoBeneficioPrevidenciario;
import br.com.webpublico.enums.TipoContagemEspecial;
import br.com.webpublico.enums.TipoReajusteAposentadoria;
import br.com.webpublico.enums.rh.esocial.TipoPlanoSegregacaoMassa;
import br.com.webpublico.enums.rh.estudoatuarial.TipoBeneficioEstudoAtuarial;
import br.com.webpublico.enums.rh.sig.TipoAposentadoriaEspecialSIG;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author peixe
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta(value = "Aposentadoria", genero = "F")
public class Aposentadoria extends VinculoFP implements Serializable {

    public static final BigDecimal PERCENTUAL_MINIMO = BigDecimal.valueOf(70);
    public static final BigDecimal PERCENTUAL_MAXIMO = BigDecimal.valueOf(100);
    @Obrigatorio
    @Etiqueta("Aposentado")
    @ManyToOne
    @Tabelavel
    private ContratoFP contratoFP;
    @Etiqueta("Tipo Aposentadoria")
    @Obrigatorio
    @Tabelavel
    @ManyToOne
    @Pesquisavel
    private TipoAposentadoria tipoAposentadoria;
    @Etiqueta("Regra de Aposentadoria")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private RegraAposentadoria regraAposentadoria;
    @OneToMany(mappedBy = "aposentadoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AtoLegalAposentadoria> atosLegais;
    @Etiqueta("Valor Percentual")
    private BigDecimal percentual;
    @OneToMany(mappedBy = "aposentadoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemAposentadoria> itemAposentadorias;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Reajuste Aposentadoria")
    @Tabelavel
    @Pesquisavel
    private TipoReajusteAposentadoria tipoReajusteAposentadoria;
    @OneToMany(mappedBy = "aposentadoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Invalidez do Aposentado")
    @OrderBy("inicioVigencia")
    private List<InvalidezAposentado> invalidezAposentados;
    @OneToOne
    private ProvimentoFP provimentoFP;
    @Etiqueta("Unidade Organizacional Orçamentária")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacionalOrc;
    @Etiqueta("Data de Publicação")
    @Temporal(TemporalType.DATE)
    private Date dataPublicacao;
    @Transient
    private FichaFinanceiraFP fichaFinanceiraFP;
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @Etiqueta("Processo")
    @ManyToOne
    private Processo processo;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo De Cálculo")
    @Enumerated(EnumType.STRING)
    private TipoCalculoAposentadoria tipoCalculoAposentadoria;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de plano de segregação da massa")
    private TipoPlanoSegregacaoMassa tipoPlanoSegregacaoMassa;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Tipo de Benefício Previdenciário")
    private TipoBeneficioPrevidenciario tipoBenefPrevidenciario;
    @Obrigatorio
    @Etiqueta("Número do benefício previdenciário")
    private String numeroBeneficioPrevidenciario;
    @Etiqueta("Tipo de Aposentadoria Especial SIG")
    @Enumerated(EnumType.STRING)
    private TipoAposentadoriaEspecialSIG tipoAposentadoriaEspecialSIG;
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Benefício Estudo Atuarial")
    private TipoBeneficioEstudoAtuarial tipoBeneficioEstudoAtuarial;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Tipo de Benefício Esocial")
    private TipoBeneficioEsocial tipoBeneficioEsocial;

    public Aposentadoria() {
        invalidezAposentados = new ArrayList<>();
        itemAposentadorias = new ArrayList<>();
        atosLegais = Lists.newArrayList();
        setLotacaoFuncionals(new LinkedList<LotacaoFuncional>());
        setRecursosDoVinculoFP(new LinkedList<RecursoDoVinculoFP>());
        percentual = BigDecimal.ZERO;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public List<AtoLegalAposentadoria> getAtosLegais() {
        return atosLegais;
    }

    public void setAtosLegais(List<AtoLegalAposentadoria> atosLegais) {
        this.atosLegais = atosLegais;
    }

    public String getNumeroBeneficioPrevidenciario() {
        return numeroBeneficioPrevidenciario;
    }

    public void setNumeroBeneficioPrevidenciario(String numeroBeneficioPrevidenciario) {
        this.numeroBeneficioPrevidenciario = numeroBeneficioPrevidenciario;
    }

    @Override
    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public List<ItemAposentadoria> getItemAposentadorias() {
        return itemAposentadorias;
    }

    public void setItemAposentadorias(List<ItemAposentadoria> itemAposentadorias) {
        this.itemAposentadorias = itemAposentadorias;
    }

    public RegraAposentadoria getRegraAposentadoria() {
        return regraAposentadoria;
    }

    public void setRegraAposentadoria(RegraAposentadoria regraAposentadoria) {
        this.regraAposentadoria = regraAposentadoria;
    }

    public TipoAposentadoria getTipoAposentadoria() {
        return tipoAposentadoria;
    }

    public void setTipoAposentadoria(TipoAposentadoria tipoAposentadoria) {
        this.tipoAposentadoria = tipoAposentadoria;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public TipoCalculoAposentadoria getTipoCalculoAposentadoria() {
        return tipoCalculoAposentadoria;
    }

    public void setTipoCalculoAposentadoria(TipoCalculoAposentadoria tipoCalculoAposentadoria) {
        this.tipoCalculoAposentadoria = tipoCalculoAposentadoria;
    }

    public TipoReajusteAposentadoria getTipoReajusteAposentadoria() {
        return tipoReajusteAposentadoria;
    }

    public void setTipoReajusteAposentadoria(TipoReajusteAposentadoria tipoReajusteAposentadoria) {
        this.tipoReajusteAposentadoria = tipoReajusteAposentadoria;
    }

    public List<InvalidezAposentado> getInvalidezAposentados() {
        return invalidezAposentados;
    }

    public void setInvalidezAposentados(List<InvalidezAposentado> invalidezAposentados) {
        this.invalidezAposentados = invalidezAposentados;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalOrc() {
        return unidadeOrganizacionalOrc;
    }

    public void setUnidadeOrganizacionalOrc(UnidadeOrganizacional unidadeOrganizacionalOrc) {
        this.unidadeOrganizacionalOrc = unidadeOrganizacionalOrc;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public TipoPlanoSegregacaoMassa getTipoPlanoSegregacaoMassa() {
        return tipoPlanoSegregacaoMassa;
    }

    public void setTipoPlanoSegregacaoMassa(TipoPlanoSegregacaoMassa tipoPlanoSegregacaoMassa) {
        this.tipoPlanoSegregacaoMassa = tipoPlanoSegregacaoMassa;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public TipoBeneficioPrevidenciario getTipoBenefPrevidenciario() {
        return tipoBenefPrevidenciario;
    }

    public void setTipoBenefPrevidenciario(TipoBeneficioPrevidenciario tipoBenefPrevidenciario) {
        this.tipoBenefPrevidenciario = tipoBenefPrevidenciario;
    }

    public TipoBeneficioEstudoAtuarial getTipoBeneficioEstudoAtuarial() {
        return tipoBeneficioEstudoAtuarial;
    }

    public void setTipoBeneficioEstudoAtuarial(TipoBeneficioEstudoAtuarial tipoBeneficioEstudoAtuarial) {
        this.tipoBeneficioEstudoAtuarial = tipoBeneficioEstudoAtuarial;
    }

    public TipoAposentadoriaEspecialSIG getTipoAposentadoriaEspecialSIG() {
        return tipoAposentadoriaEspecialSIG;
    }

    public void setTipoAposentadoriaEspecialSIG(TipoAposentadoriaEspecialSIG tipoAposentadoriaEspecialSIG) {
        this.tipoAposentadoriaEspecialSIG = tipoAposentadoriaEspecialSIG;
    }


    @Override
    public ProvimentoFP getProvimentoFP() {
        return provimentoFP;
    }

    @Override
    public void setProvimentoFP(ProvimentoFP provimentoFP) {
        this.provimentoFP = provimentoFP;
    }

    public FichaFinanceiraFP getFichaFinanceiraFP() {
        return fichaFinanceiraFP;
    }

    public void setFichaFinanceiraFP(FichaFinanceiraFP fichaFinanceiraFP) {
        this.fichaFinanceiraFP = fichaFinanceiraFP;
    }

    public TipoBeneficioEsocial getTipoBeneficioEsocial() {
        return tipoBeneficioEsocial;
    }

    public void setTipoBeneficioEsocial(TipoBeneficioEsocial tipoBeneficioEsocial) {
        this.tipoBeneficioEsocial = tipoBeneficioEsocial;
    }

    @Override
    public Integer getCarreira() {
        return TipoContagemEspecial.getCodigoAtuarial(getContratoFP().getCargo().getTipoContagemSIPREV());
    }

    @Override
    public Long getIdCalculo() {
        if (tipoReajusteAposentadoria == TipoReajusteAposentadoria.PARIDADE) {
            return contratoFP.getId();
        } else {
            return getId();
        }
    }

    @Override
    public String toString() {
        String nome = getMatriculaFP().getPessoa() != null ? getMatriculaFP().getPessoa().getNome() : " Nome não encontrado ";
        return getMatriculaFP().getMatricula() + "/" + getNumero() + " " + nome + " (" + getMatriculaFP().getPessoa().getNomeTratamento() + ")" + " - APOSENTADO(A)";
    }

    public void removerItem(ItemAposentadoria item) {
        if (itemAposentadorias.contains(item)) itemAposentadorias.remove(item);
    }
}
