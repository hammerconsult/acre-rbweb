package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.LeilaoAlienacaoLote;
import br.com.webpublico.entidades.LoteAvaliacaoAlienacao;
import com.google.common.base.Strings;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mga on 23/05/2018.
 */
public class VOItemSolicitacaoAlienacao implements Comparable<VOItemSolicitacaoAlienacao> {

    private Long idItem;
    private Long idBem;
    private Long idLote;
    private Long idItemLeilao;
    private Long idUnidadeOrc;
    private Long idUnidadeAdm;
    private Long idGrupoBem;
    private String registroPatrimonial;
    private String registroAnterior;
    private Date dataOperacao;
    private String descricao;
    private String unidadeOrcamentaria;
    private BigDecimal valorOriginal;
    private BigDecimal valorAjuste;
    private BigDecimal valorLiquido;
    private BigDecimal valorAvaliado;
    private BigDecimal valorArrematado;
    private LoteAvaliacaoAlienacao loteAvaliacaoAlienacao;
    private Date dataAquisicao;
    private String fonteRecurso;
    private Bem bem;

    public VOItemSolicitacaoAlienacao() {
        valorAjuste = BigDecimal.ZERO;
        valorAvaliado = BigDecimal.ZERO;
        valorLiquido = BigDecimal.ZERO;
        valorOriginal = BigDecimal.ZERO;
        valorArrematado = BigDecimal.ZERO;
    }

    public static VOItemSolicitacaoAlienacao criarObjetoVoItemSolicitacaoAlienacao(Object[] obj) {
        VOItemSolicitacaoAlienacao item = new VOItemSolicitacaoAlienacao();
        item.setIdItem(((BigDecimal) obj[0]).longValue());
        item.setIdBem(((BigDecimal) obj[1]).longValue());
        item.setIdLote(obj[2] != null ? ((BigDecimal) obj[2]).longValue() : null);
        item.setIdItemLeilao(obj[3] != null ? ((BigDecimal) obj[3]).longValue() : null);
        item.setIdUnidadeOrc(((BigDecimal) obj[4]).longValue());
        item.setIdUnidadeAdm(((BigDecimal) obj[5]).longValue());
        item.setIdGrupoBem(((BigDecimal) obj[6]).longValue());
        item.setRegistroPatrimonial((String) obj[7]);
        item.setRegistroAnterior((String) obj[8]);
        item.setDescricao((String) obj[9]);
        item.setDataOperacao((Date) obj[10]);
        item.setUnidadeOrcamentaria((String) obj[11]);
        item.setValorOriginal((BigDecimal) obj[12]);
        item.setValorAjuste((BigDecimal) obj[13]);
        item.setValorAvaliado((BigDecimal) obj[14]);
        item.setValorArrematado((BigDecimal) obj[15]);
        item.setValorLiquido(item.getValorOriginal().subtract(item.getValorAjuste()));
        item.setDataAquisicao((Date) obj[16]);
        return item;
    }


    public Date getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(Date dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }

    public String getFonteRecurso() {
        return fonteRecurso;
    }

    public void setFonteRecurso(String fonteRecurso) {
        this.fonteRecurso = fonteRecurso;
    }

    public Long getIdItemLeilao() {
        return idItemLeilao;
    }

    public void setIdItemLeilao(Long idItemLeilao) {
        this.idItemLeilao = idItemLeilao;
    }

    public Long getIdLote() {
        return idLote;
    }

    public void setIdLote(Long idLote) {
        this.idLote = idLote;
    }


    public Long getIdUnidadeOrc() {
        return idUnidadeOrc;
    }

    public void setIdUnidadeOrc(Long idUnidadeOrc) {
        this.idUnidadeOrc = idUnidadeOrc;
    }

    public Long getIdUnidadeAdm() {
        return idUnidadeAdm;
    }

    public void setIdUnidadeAdm(Long idUnidadeAdm) {
        this.idUnidadeAdm = idUnidadeAdm;
    }

    public Long getIdGrupoBem() {
        return idGrupoBem;
    }

    public void setIdGrupoBem(Long idGrupoBem) {
        this.idGrupoBem = idGrupoBem;
    }

    public LoteAvaliacaoAlienacao getLoteAvaliacaoAlienacao() {
        return loteAvaliacaoAlienacao;
    }

    public void setLoteAvaliacaoAlienacao(LoteAvaliacaoAlienacao loteAvaliacaoAlienacao) {
        this.loteAvaliacaoAlienacao = loteAvaliacaoAlienacao;
    }

    public Long getIdItem() {
        return idItem;
    }

    public void setIdItem(Long idItem) {
        this.idItem = idItem;
    }

    public Long getIdBem() {
        return idBem;
    }

    public void setIdBem(Long idBem) {
        this.idBem = idBem;
    }

    public String getRegistroPatrimonial() {
        return registroPatrimonial;
    }

    public void setRegistroPatrimonial(String registroPatrimonial) {
        this.registroPatrimonial = registroPatrimonial;
    }

    public String getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(String unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public BigDecimal getValorAjuste() {
        return valorAjuste;
    }

    public void setValorAjuste(BigDecimal valorAjuste) {
        this.valorAjuste = valorAjuste;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public BigDecimal getValorAvaliado() {
        return valorAvaliado;
    }

    public void setValorAvaliado(BigDecimal valorAvaliado) {
        this.valorAvaliado = valorAvaliado;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public BigDecimal getValorArrematado() {
        return valorArrematado;
    }

    public void setValorArrematado(BigDecimal valorArrematado) {
        this.valorArrematado = valorArrematado;
    }

    public String getRegistroAnterior() {
        return registroAnterior;
    }

    public void setRegistroAnterior(String registroAnterior) {
        this.registroAnterior = registroAnterior;
    }

    public String toStringBem() {
        return registroPatrimonial + " - " + descricao;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    @Override
    public int compareTo(VOItemSolicitacaoAlienacao o) {
        if (!Strings.isNullOrEmpty(this.registroPatrimonial) && !Strings.isNullOrEmpty(o.getRegistroPatrimonial())) {
            Integer registro1 = Integer.parseInt(this.registroPatrimonial);
            Integer registro2 = Integer.parseInt(o.getRegistroPatrimonial());
            return registro1.compareTo(registro2);
        }
        return 0;
    }
}
