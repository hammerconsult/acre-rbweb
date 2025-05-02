package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.CaracterizadorDeBemMovel;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 04/12/13
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta(value = "Efetivação do Levantamento de Bem", genero = "M")
public class EfetivacaoLevantamentoBem extends EventoBem implements CaracterizadorDeBemMovel {

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Levantamento")
    @OneToOne(cascade = CascadeType.ALL)
    private LevantamentoBemPatrimonial levantamento;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Lote")
    @ManyToOne
    private LoteEfetivacaoLevantamentoBem lote;

    private BigDecimal depreciacao;

    public EfetivacaoLevantamentoBem() {
        super(TipoEventoBem.EFETIVACAOLEVANTAMENTOBEM, TipoOperacao.DEBITO);
    }

    public EfetivacaoLevantamentoBem(LoteEfetivacaoLevantamentoBem lote, LevantamentoBemPatrimonial levantamento, Date dataLancamento) {
        super(TipoEventoBem.EFETIVACAOLEVANTAMENTOBEM, TipoOperacao.DEBITO);
        this.levantamento = levantamento;
        this.lote = lote;
        this.setDataLancamento(dataLancamento);
    }

    public LevantamentoBemPatrimonial getLevantamento() {
        return levantamento;
    }

    public void setLevantamento(LevantamentoBemPatrimonial levantamento) {
        this.levantamento = levantamento;
    }

    public LoteEfetivacaoLevantamentoBem getLote() {
        return lote;
    }

    public void setLote(LoteEfetivacaoLevantamentoBem lote) {
        this.lote = lote;
    }

    @Override
    public Date getDataDaOperacao() {
        return new Date();
    }

    @Override
    public ObjetoCompra getObjetoCompra() {
        return levantamento.getObjetoCompra();
    }

    @Override
    public String getRegistroAnterior() {
        return levantamento.getCodigoPatrimonio() != null ?
            levantamento.getCodigoPatrimonio() :
            levantamento.getCodigoAnterior() != null ?
                levantamento.getCodigoAnterior() : null;
    }

    @Override
    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return levantamento.getGrupoObjetoCompra();
    }

    @Override
    public String getObservacao() {
        return levantamento.getObservacao();
    }

    @Override
    public DetentorOrigemRecurso getDetentorOrigemRecurso() {
        return null;
    }

    @Override
    public Pessoa getFornecedor() {
        return levantamento.getFornecedor();
    }

    @Override
    public BigDecimal getValorDoBem() {
        return levantamento.getValorDoBem();
    }

    @Override
    public String getCodigoPatrimonio() {
        return levantamento.getCodigoPatrimonio();
    }

    @Override
    public String getDescricaoDoBem() {
        return levantamento.getDescricaoDoBem();
    }

    @Override
    public EstadoConservacaoBem getEstadoConservacaoBem() {
        return levantamento.getEstadoConservacaoBem();
    }

    @Override
    public TipoAquisicaoBem getTipoAquisicaoBem() {
        return levantamento.getTipoAquisicaoBem();
    }

    @Override
    public String getModelo() {
        return levantamento.getModelo();
    }

    @Override
    public String getMarca() {
        return levantamento.getMarca();
    }

    @Override
    public SituacaoConservacaoBem getSituacaoConservacaoBem() {
        return levantamento.getSituacaoConservacaoBem();
    }

    @Override
    public String getLocalizacao() {
        return levantamento.getLocalizacao();
    }

    @Override
    public UnidadeOrganizacional getUnidadeAdministrativa() {
        return levantamento.getUnidadeAdministrativa();
    }

    @Override
    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return levantamento.getUnidadeOrcamentaria();
    }

    @Override
    public GrupoBem getGrupoBem() {
        return levantamento.getGrupoBem();
    }

    @Override
    public void setGrupoBem(GrupoBem gb) {
        levantamento.setGrupoBem(gb);
    }

    @Override
    public BigDecimal getValorDoLancamento() {
        return getEstadoResultante().getValorOriginal();
    }

    @Override
    public List<BemNotaFiscal> getNotasFiscais() {
        return levantamento.getNotasFiscais();
    }

    public BigDecimal getDepreciacao() {
        return depreciacao;
    }

    public void setDepreciacao(BigDecimal depreciacao) {
        this.depreciacao = depreciacao;
    }
}
