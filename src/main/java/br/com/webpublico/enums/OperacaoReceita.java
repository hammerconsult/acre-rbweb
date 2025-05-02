package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.OperacaoReceitaDTO;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edi on 25/04/2016.
 */
public enum OperacaoReceita implements EnumComDescricao {

    RECEITA_DIRETAMENTE_ARRECADADA_BRUTA("Receita Diretamente Arrecadada Bruta", OperacaoReceitaDTO.RECEITA_DIRETAMENTE_ARRECADADA_BRUTA),
    RECEITA_CREDITOS_RECEBER_BRUTA("Receita de Créditos a Receber Bruta", OperacaoReceitaDTO.RECEITA_CREDITOS_RECEBER_BRUTA),
    RECEITA_DIVIDA_ATIVA_BRUTA("Receita de Dívida Ativa Bruta", OperacaoReceitaDTO.RECEITA_DIVIDA_ATIVA_BRUTA),
    DEDUCAO_RECEITA_RENUNCIA("Dedução de Receita - Renúncia", OperacaoReceitaDTO.DEDUCAO_RECEITA_RENUNCIA),
    DEDUCAO_RECEITA_RESTITUICAO("Dedução de Receita - Restituição", OperacaoReceitaDTO.DEDUCAO_RECEITA_RESTITUICAO),
    DEDUCAO_RECEITA_DESCONTO("Dedução de Receita - Desconto", OperacaoReceitaDTO.DEDUCAO_RECEITA_DESCONTO),
    DEDUCAO_RECEITA_FUNDEB("Dedução de Receita - FUNDEB", OperacaoReceitaDTO.DEDUCAO_RECEITA_FUNDEB),
    DEDUCAO_RECEITA_OUTRAS("Dedução de Receita - Outras", OperacaoReceitaDTO.DEDUCAO_RECEITA_OUTRAS);

    private String descricao;
    private OperacaoReceitaDTO toDto;

    OperacaoReceita(String descricao, OperacaoReceitaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public static List<OperacaoReceita> retornarOperacoesReceitaNormal() {
        List<OperacaoReceita> retorno = Lists.newArrayList();
        retorno.add(OperacaoReceita.RECEITA_CREDITOS_RECEBER_BRUTA);
        retorno.add(OperacaoReceita.RECEITA_DIRETAMENTE_ARRECADADA_BRUTA);
        retorno.add(OperacaoReceita.RECEITA_DIVIDA_ATIVA_BRUTA);
        return retorno;
    }

    public static List<OperacaoReceita> retornarOperacoesReceitaDeducao() {
        List<OperacaoReceita> retorno = Lists.newArrayList();
        retorno.add(OperacaoReceita.DEDUCAO_RECEITA_RENUNCIA);
        retorno.add(OperacaoReceita.DEDUCAO_RECEITA_RESTITUICAO);
        retorno.add(OperacaoReceita.DEDUCAO_RECEITA_DESCONTO);
        retorno.add(OperacaoReceita.DEDUCAO_RECEITA_FUNDEB);
        retorno.add(OperacaoReceita.DEDUCAO_RECEITA_OUTRAS);
        return retorno;
    }

    public static List<String> recuperarListaOperacaoReceitaName(List<OperacaoReceita> operacoes) {
        List<String> retorno = new ArrayList<>();
        for (OperacaoReceita or : operacoes) {
            retorno.add(or.name());
        }
        return retorno;
    }

    public static String montarClausulaIn(List<OperacaoReceita> registros) {
        StringBuilder in = new StringBuilder();
        String juncao = "(";
        for (OperacaoReceita operacaoReceita : registros) {
            in.append(juncao);
            in.append("'");
            in.append(operacaoReceita.name());
            in.append("'");
            juncao = ", ";
        }
        in.append(") ");
        return in.toString();
    }

    public boolean isReceitaBruta() {
        return RECEITA_DIVIDA_ATIVA_BRUTA.equals(this) || RECEITA_CREDITOS_RECEBER_BRUTA.equals(this);
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public OperacaoReceitaDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
