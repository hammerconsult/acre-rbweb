package br.com.webpublico.entidadesauxiliares.administrativo;

import br.com.webpublico.enums.TipoSituacaoLicitacao;
import com.google.common.collect.Lists;

import java.util.List;

public class AgendaLicitacaoVO {
    private Integer dia;
    private String diaDaSemana;
    private Integer posicao;
    private List<AgendaLicitacaoVO> licitacoes;

    private Long idLicitacao;
    private String licitacao;
    private String resumoObjeto;
    private TipoSituacaoLicitacao tipoSituacaoLicitacao;

    public AgendaLicitacaoVO() {
        licitacoes = Lists.newArrayList();
    }

    public Integer getDia() {
        return dia;
    }

    public void setDia(Integer dia) {
        this.dia = dia;
    }

    public String getDiaDaSemana() {
        return diaDaSemana;
    }

    public void setDiaDaSemana(String diaDaSemana) {
        this.diaDaSemana = diaDaSemana;
    }

    public Integer getPosicao() {
        return posicao;
    }

    public void setPosicao(Integer posicao) {
        this.posicao = posicao;
    }

    public String getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(String licitacao) {
        this.licitacao = licitacao;
    }

    public Long getIdLicitacao() {
        return idLicitacao;
    }

    public void setIdLicitacao(Long idLicitacao) {
        this.idLicitacao = idLicitacao;
    }

    public List<AgendaLicitacaoVO> getLicitacoes() {
        return licitacoes;
    }

    public void setLicitacoes(List<AgendaLicitacaoVO> licitacoes) {
        this.licitacoes = licitacoes;
    }

    public String getResumoObjeto() {
        return resumoObjeto;
    }

    public void setResumoObjeto(String resumoObjeto) {
        this.resumoObjeto = resumoObjeto;
    }

    public TipoSituacaoLicitacao getTipoSituacaoLicitacao() {
        return tipoSituacaoLicitacao;
    }

    public void setTipoSituacaoLicitacao(TipoSituacaoLicitacao tipoSituacaoLicitacao) {
        this.tipoSituacaoLicitacao = tipoSituacaoLicitacao;
    }
}
