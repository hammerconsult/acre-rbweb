/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoContagemEspecial;
import br.com.webpublico.enums.TipoFundamentacao;
import br.com.webpublico.enums.TipoPensao;
import br.com.webpublico.enums.TipoReajusteAposentadoria;
import br.com.webpublico.enums.rh.estudoatuarial.TipoDependenciaEstudoAtuarial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
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
 * @author boy
 */
@Entity

@Audited
@Etiqueta(value = "Pensionista", genero = "M")
@GrupoDiagrama(nome = "RecursosHumanos")
public class Pensionista extends VinculoFP implements Serializable {

    @ManyToOne
    @Etiqueta("Pensão")
    private Pensao pensao;
    @OrderBy("inicioVigencia desc")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pensionista")
    private List<ItemValorPensionista> itensValorPensionista;
    @Temporal(TemporalType.TIMESTAMP)
    @Transient
    private Date dataRegistro;
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoFundamentacao tipoFundamentacao;
    @Enumerated(EnumType.STRING)
    private TipoReajusteAposentadoria tipoReajusteAposentadoria;
    @ManyToOne
    private GrauParentescoPensionista grauParentescoPensionista;
    @Etiqueta("Tipo da Pensão")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoPensao tipoPensao;
    @Etiqueta("Invalidez")
    @OrderBy("inicioVigencia desc")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pensionista")
    private List<InvalidezPensao> listaInvalidezPensao;
    @OneToOne
    private ProvimentoFP provimentoFP;
    @Transient
    private LotacaoFuncional lotacaoFuncional;
    @Transient
    private RecursoDoVinculoFP recursoDoVinculoFP;
    @Transient
    private ItemValorPensionista itemValorPensionista;
    @Transient
    private BigDecimal percentual;
    @Transient
    private InvalidezPensao invalidezPensao;
    @Transient
    private int activeIndex = 0;
    @Transient
    private List<CID> cids;
    @Etiqueta("Tipo de Dependente Estudo Atuarial")
    @Enumerated(EnumType.STRING)
    private TipoDependenciaEstudoAtuarial tipoDependEstudoAtuarial;

    public Pensionista() {
        itensValorPensionista = new ArrayList<>();
        listaInvalidezPensao = new ArrayList<>();
        setLotacaoFuncionals(new LinkedList<LotacaoFuncional>());
        setRecursosDoVinculoFP(new LinkedList<RecursoDoVinculoFP>());
        dataRegistro = new Date();
        lotacaoFuncional = null;
        recursoDoVinculoFP = null;
        itemValorPensionista = null;
        invalidezPensao = null;
        percentual = BigDecimal.ZERO;
        activeIndex = 0;
        cids = Lists.newArrayList();
    }

    public TipoReajusteAposentadoria getTipoReajusteAposentadoria() {
        return tipoReajusteAposentadoria;
    }

    public void setTipoReajusteAposentadoria(TipoReajusteAposentadoria tipoReajusteAposentadoria) {
        this.tipoReajusteAposentadoria = tipoReajusteAposentadoria;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public InvalidezPensao getInvalidezPensao() {
        return invalidezPensao;
    }

    public void setInvalidezPensao(InvalidezPensao invalidezPensao) {
        this.invalidezPensao = invalidezPensao;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public ItemValorPensionista getItemValorPensionista() {
        return itemValorPensionista;
    }

    public void setItemValorPensionista(ItemValorPensionista itemValorPensionista) {
        this.itemValorPensionista = itemValorPensionista;
    }

    public RecursoDoVinculoFP getRecursoDoVinculoFP() {
        return recursoDoVinculoFP;
    }

    public void setRecursoDoVinculoFP(RecursoDoVinculoFP recursoDoVinculoFP) {
        this.recursoDoVinculoFP = recursoDoVinculoFP;
    }

    public LotacaoFuncional getLotacaoFuncional() {
        return lotacaoFuncional;
    }

    public void setLotacaoFuncional(LotacaoFuncional lotacaoFuncional) {
        this.lotacaoFuncional = lotacaoFuncional;
    }

    public List<InvalidezPensao> getListaInvalidez() {
        return listaInvalidezPensao;
    }

    public void setListaInvalidez(List<InvalidezPensao> listaInvalidez) {
        listaInvalidezPensao = listaInvalidez;
    }

    @Override
    public Date getDataRegistro() {
        return dataRegistro;
    }

    @Override
    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public TipoFundamentacao getTipoFundamentacao() {
        return tipoFundamentacao;
    }

    public void setTipoFundamentacao(TipoFundamentacao tipoFundamentacao) {
        this.tipoFundamentacao = tipoFundamentacao;
    }

    public Pensao getPensao() {
        return pensao;
    }

    public void setPensao(Pensao pensao) {
        this.pensao = pensao;
    }

    public List<ItemValorPensionista> getItensValorPensionista() {
        return itensValorPensionista;
    }

    public void setItensValorPensionista(List<ItemValorPensionista> itensValorPensionista) {
        this.itensValorPensionista = itensValorPensionista;
    }

    public GrauParentescoPensionista getGrauParentescoPensionista() {
        return grauParentescoPensionista;
    }

    public void setGrauParentescoPensionista(GrauParentescoPensionista grauParentescoPensionista) {
        this.grauParentescoPensionista = grauParentescoPensionista;
    }

    @Override
    public ProvimentoFP getProvimentoFP() {
        return provimentoFP;
    }

    @Override
    public void setProvimentoFP(ProvimentoFP provimentoFP) {
        this.provimentoFP = provimentoFP;
    }

    public TipoPensao getTipoPensao() {
        return tipoPensao;
    }

    public void setTipoPensao(TipoPensao tipoPensao) {
        this.tipoPensao = tipoPensao;
    }

    public List<CID> getCids() {
        return cids;
    }

    public void setCids(List<CID> cids) {
        this.cids = cids;
    }

    public TipoDependenciaEstudoAtuarial getTipoDependEstudoAtuarial() {
        return tipoDependEstudoAtuarial;
    }

    public void setTipoDependEstudoAtuarial(TipoDependenciaEstudoAtuarial tipoDependEstudoAtuarial) {
        this.tipoDependEstudoAtuarial = tipoDependEstudoAtuarial;
    }

    @Override
    public Integer getCarreira() {
        return TipoContagemEspecial.getCodigoAtuarial(getPensao().getContratoFP().getCargo().getTipoContagemSIPREV());
    }

    @Override
    public String toString() {
        return getMatriculaFP().getMatricula() + "/" + getNumero() + " " + getMatriculaFP().getPessoa().getNome() + " - PENSIONISTA";
    }

    @Override
    public ContratoFP getContratoFP() {
        return pensao.getContratoFP();
    }

    public void removerItem(ItemValorPensionista item) {
        if (itensValorPensionista.contains(item)) itensValorPensionista.remove(item);
    }

    public boolean isTipoPensaoTemporaria() {
        return TipoPensao.TEMPORARIA.equals(tipoPensao);
    }

    public boolean isTipoPensaoTemporariaInvalidez() {
        return TipoPensao.TEMPORARIA_INVALIDEZ.equals(tipoPensao);
    }

    public boolean isTipoPensaoVitaliciaInvalidez() {
        return TipoPensao.VITALICIA_INVALIDEZ.equals(tipoPensao);
    }

    public boolean temInvalidezAdicionada() {
        return listaInvalidezPensao != null && !listaInvalidezPensao.isEmpty();
    }
}
