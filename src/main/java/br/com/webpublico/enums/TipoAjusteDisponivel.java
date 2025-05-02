/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoAjusteDisponivelDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author major
 */
public enum TipoAjusteDisponivel {

    AUMENTATIVO_CONTROLE_INTERNO("Aumentativo Controle Interno", TipoAjusteDisponivelDTO.AUMENTATIVO_CONTROLE_INTERNO),
    AUMENTATIVO_CONTROLE_EXTERNO("Aumentativo Controle Externo", TipoAjusteDisponivelDTO.AUMENTATIVO_CONTROLE_EXTERNO),
    AUMENTATIVO_EMPRESA_PUBLICA("Aumentativo - Empresa Pública", TipoAjusteDisponivelDTO.AUMENTATIVO_EMPRESA_PUBLICA),
    AUMENTATIVO_RPPS("Aumentativo RPPS", TipoAjusteDisponivelDTO.AUMENTATIVO_RPPS),
    DIMINUTIVO_RPPS("Diminutivo RPPS", TipoAjusteDisponivelDTO.DIMINUTIVO_RPPS),
    DIMINUTIVO_CONTROLE_INTERNO("Diminutivo Controle Interno", TipoAjusteDisponivelDTO.DIMINUTIVO_CONTROLE_INTERNO),
    DIMINUTIVO_CONTROLE_EXTERNO("Diminutivo Controle Externo", TipoAjusteDisponivelDTO.DIMINUTIVO_CONTROLE_EXTERNO),
    DIMINUTIVO_EMPRESA_PUBLICA("Diminutivo - Empresa Pública", TipoAjusteDisponivelDTO.DIMINUTIVO_EMPRESA_PUBLICA);

    private String descricao;
    private TipoAjusteDisponivelDTO toDto;


    private TipoAjusteDisponivel(String descricao, TipoAjusteDisponivelDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public static List<TipoAjusteDisponivel> retornarAumentativo() {
        List<TipoAjusteDisponivel> retorno = new ArrayList<>();
        retorno.add(TipoAjusteDisponivel.AUMENTATIVO_CONTROLE_EXTERNO);
        retorno.add(TipoAjusteDisponivel.AUMENTATIVO_CONTROLE_INTERNO);
        retorno.add(TipoAjusteDisponivel.AUMENTATIVO_RPPS);
        retorno.add(TipoAjusteDisponivel.AUMENTATIVO_EMPRESA_PUBLICA);
        return retorno;
    }

    public static List<TipoAjusteDisponivel> retornarDiminutivo() {
        List<TipoAjusteDisponivel> retorno = new ArrayList<>();
        retorno.add(TipoAjusteDisponivel.DIMINUTIVO_CONTROLE_EXTERNO);
        retorno.add(TipoAjusteDisponivel.DIMINUTIVO_CONTROLE_INTERNO);
        retorno.add(TipoAjusteDisponivel.DIMINUTIVO_RPPS);
        retorno.add(TipoAjusteDisponivel.DIMINUTIVO_EMPRESA_PUBLICA);
        return retorno;
    }

    public static List<String> recuperarListaName(List<TipoAjusteDisponivel> tipos) {
        List<String> retorno = new ArrayList<>();
        for (TipoAjusteDisponivel tipo : tipos) {
            retorno.add(tipo.name());
        }
        return retorno;
    }

    public static String montarClausulaIn(List<TipoAjusteDisponivel> tipos) {
        StringBuilder in = new StringBuilder();
        String juncao = "(";
        for (TipoAjusteDisponivel tipo : tipos) {
            in.append(juncao);
            in.append("'");
            in.append(tipo.name());
            in.append("'");
            juncao = ", ";
        }
        in.append(") ");
        return in.toString();
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoAjusteDisponivelDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
