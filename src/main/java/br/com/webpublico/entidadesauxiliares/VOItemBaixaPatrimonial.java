package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.EstadoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LeilaoAlienacaoLote;
import br.com.webpublico.enums.EstadoConservacaoBem;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 23/05/2018.
 */
public class VOItemBaixaPatrimonial implements Comparable<VOItemBaixaPatrimonial> {

    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private LeilaoAlienacaoLote leilaoAlienacaoLote;
    private EstadoBem estadoResultante;
    private Bem bemEfetivacao;
    private Long idItem;
    private Long idBem;
    private Long idLote;
    private Long idUnidadeOrc;
    private Long idUnidadeAdm;
    private String descricao;
    private String identificador;
    private String estadoBem;
    private Date dataAquisicao;
    private String unidadeOrcamentaria;
    private String unidadeAdministrativa;
    private String grupoPatrimonial;
    private BigDecimal valorOriginal;
    private BigDecimal valorDepreciacao;
    private BigDecimal valorAmortizacao;
    private BigDecimal valorExaustao;
    private BigDecimal valorAjustePerda;
    private BigDecimal valorAjuste;
    private BigDecimal valorLiquido;
    private BigDecimal valorAvaliado;
    private BigDecimal valorProporcionalArrematado;
    private List<VOItemBaixaPatrimonial> bensAgrupados;

    public VOItemBaixaPatrimonial() {
        valorOriginal = BigDecimal.ZERO;
        valorDepreciacao = BigDecimal.ZERO;
        valorAmortizacao = BigDecimal.ZERO;
        valorExaustao = BigDecimal.ZERO;
        valorAjustePerda = BigDecimal.ZERO;
        valorAjuste = BigDecimal.ZERO;
        valorLiquido = BigDecimal.ZERO;
        valorAvaliado = BigDecimal.ZERO;
        valorProporcionalArrematado = BigDecimal.ZERO;
        bensAgrupados = Lists.newArrayList();
    }

    public static VOItemBaixaPatrimonial criarObjetoVoItemSolicitacaoBaixa(Object[] obj) {
        VOItemBaixaPatrimonial item = new VOItemBaixaPatrimonial();
        item.setIdItem(obj[0] != null ? ((BigDecimal) obj[0]).longValue() : null);
        item.setIdBem(obj[1] != null ? ((BigDecimal) obj[1]).longValue() : null);
        item.setIdLote(obj[2] != null ? ((BigDecimal) obj[2]).longValue() : null);
        item.setIdUnidadeOrc(obj[3] != null ? ((BigDecimal) obj[3]).longValue() : null);
        item.setIdUnidadeAdm(obj[4] != null ? ((BigDecimal) obj[4]).longValue() : null);
        item.setIdentificador((String) obj[5]);
        item.setDescricao((String) obj[6]);
        item.setDataAquisicao(obj[7] != null ? (Date) obj[7] : null);
        item.setEstadoBem((EstadoConservacaoBem.valueOf((String) obj[8]).getDescricao()));
        item.setUnidadeOrcamentaria((String) obj[9]);
        item.setUnidadeAdministrativa((String) obj[10]);
        item.setValorOriginal((BigDecimal) obj[11]);
        item.setValorDepreciacao((BigDecimal) obj[12]);
        item.setValorAmortizacao((BigDecimal) obj[13]);
        item.setValorExaustao((BigDecimal) obj[14]);
        item.setValorAjustePerda((BigDecimal) obj[15]);
        item.setValorAjuste((BigDecimal) obj[16]);
        item.setValorAvaliado((BigDecimal) obj[17]);
        item.setValorProporcionalArrematado((BigDecimal) obj[18]);
        item.setGrupoPatrimonial((String) obj[19]);
        item.setValorLiquido(item.getValorOriginal().subtract(item.getValorAjuste()));
        return item;
    }

    @Override
    public int compareTo(VOItemBaixaPatrimonial o) {
        if (!Strings.isNullOrEmpty(this.identificador) && !Strings.isNullOrEmpty(o.getIdentificador())) {
            Integer registro1 = Integer.parseInt(this.identificador);
            Integer registro2 = Integer.parseInt(o.getIdentificador());
            return registro1.compareTo(registro2);
        }
        return 0;
    }

    public String getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(String unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public Bem getBemEfetivacao() {
        return bemEfetivacao;
    }

    public void setBemEfetivacao(Bem bemEfetivacao) {
        this.bemEfetivacao = bemEfetivacao;
    }

    public EstadoBem getEstadoResultante() {
        return estadoResultante;
    }

    public void setEstadoResultante(EstadoBem estadoResultante) {
        this.estadoResultante = estadoResultante;
    }

    public BigDecimal getValorAvaliado() {
        return valorAvaliado;
    }

    public void setValorAvaliado(BigDecimal valorAvaliado) {
        this.valorAvaliado = valorAvaliado;
    }

    public BigDecimal getValorProporcionalArrematado() {
        return valorProporcionalArrematado;
    }

    public void setValorProporcionalArrematado(BigDecimal valorProporcionalArrematado) {
        this.valorProporcionalArrematado = valorProporcionalArrematado;
    }

    public String getGrupoPatrimonial() {
        return grupoPatrimonial;
    }

    public void setGrupoPatrimonial(String grupoPatrimonial) {
        this.grupoPatrimonial = grupoPatrimonial;
    }

    public BigDecimal getValorAjuste() {
        return valorAjuste;
    }

    public void setValorAjuste(BigDecimal valorAjuste) {
        this.valorAjuste = valorAjuste;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public LeilaoAlienacaoLote getLeilaoAlienacaoLote() {
        return leilaoAlienacaoLote;
    }

    public void setLeilaoAlienacaoLote(LeilaoAlienacaoLote leilaoAlienacaoLote) {
        this.leilaoAlienacaoLote = leilaoAlienacaoLote;
    }

    public List<VOItemBaixaPatrimonial> getBensAgrupados() {
        return bensAgrupados;
    }

    public void setBensAgrupados(List<VOItemBaixaPatrimonial> bensAgrupados) {
        this.bensAgrupados = bensAgrupados;
    }

    public String getBem() {
        return identificador + " - " + descricao;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getEstadoBem() {
        return estadoBem;
    }

    public void setEstadoBem(String estadoBem) {
        this.estadoBem = estadoBem;
    }

    public Date getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(Date dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
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

    public BigDecimal getValorDepreciacao() {
        return valorDepreciacao;
    }

    public void setValorDepreciacao(BigDecimal valorDepreciacao) {
        this.valorDepreciacao = valorDepreciacao;
    }

    public BigDecimal getValorAmortizacao() {
        return valorAmortizacao;
    }

    public void setValorAmortizacao(BigDecimal valorAmortizacao) {
        this.valorAmortizacao = valorAmortizacao;
    }

    public BigDecimal getValorExaustao() {
        return valorExaustao;
    }

    public void setValorExaustao(BigDecimal valorExaustao) {
        this.valorExaustao = valorExaustao;
    }

    public BigDecimal getValorAjustePerda() {
        return valorAjustePerda;
    }

    public void setValorAjustePerda(BigDecimal valorAjustePerda) {
        this.valorAjustePerda = valorAjustePerda;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }
}
