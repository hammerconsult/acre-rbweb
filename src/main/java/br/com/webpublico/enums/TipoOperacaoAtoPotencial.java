package br.com.webpublico.enums;

import com.google.common.collect.Lists;

import java.util.List;

public enum TipoOperacaoAtoPotencial {
    CONVENIO_DE_RECEITA_A_COMPROVAR("Convênio de Receita - A Comprovar"),
    CONVENIO_DE_RECEITA_A_RECEBER("Convênio de Receita - A Receber"),
    CONVENIO_DE_RECEITA_ENCERRAMENTO("Convênio de Receita - Encerramento"),
    CONVENIO_DE_DESPESA_A_COMPROVAR("Convênio de Despesa - A Comprovar"),
    CONVENIO_DE_DESPESA_A_LIBERAR("Convênio de Despesa - A Liberar"),
    CONVENIO_DE_DESPESA_ENCERRAMENTO("Convênio de Despesa - Encerramento"),
    CONTRATO_DE_SERVICO_DESPESA_A_EXERCUTAR("Contrato de Serviço – Despesa - A Executar"),
    CONTRATO_DE_SERVICO_DESPESA_EXECUTADO("Contrato de Serviço – Despesa - Executado"),
    CONTRATO_DE_ALUGUEL_DESPESA_A_EXERCUTAR("Contrato de Aluguel – Despesa - A Executar"),
    CONTRATO_DE_ALUGUEL_DESPESA_EXECUTADO("Contrato de Aluguel – Despesa - Executado"),
    CONTRATO_DE_FORNECIMENTO_DE_BEM_DESPESA_A_EXECUTAR("Contrato de Fornecimento de Bem – Despesa - A Executar"),
    CONTRATO_DE_FORNECIMENTO_DE_BEM_DESPESA_EXECUTADO("Contrato de Fornecimento de Bem – Despesa - Executado");

    private String descricao;

    TipoOperacaoAtoPotencial(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public static List<TipoOperacaoAtoPotencial> buscarOperacoesPorTipoAtoPotencial(TipoAtoPotencial tipoAtoPotencial) {
        List<TipoOperacaoAtoPotencial> retorno = Lists.newArrayList();
        if (tipoAtoPotencial == null) {
            return Lists.newArrayList();
        }
        switch (tipoAtoPotencial) {
            case CONVENIO_DE_RECEITA:
                retorno.add(CONVENIO_DE_RECEITA_A_COMPROVAR);
                retorno.add(CONVENIO_DE_RECEITA_A_RECEBER);
                retorno.add(CONVENIO_DE_RECEITA_ENCERRAMENTO);
                break;
            case CONVENIO_DE_DESPESA:
                retorno.add(CONVENIO_DE_DESPESA_A_COMPROVAR);
                retorno.add(CONVENIO_DE_DESPESA_A_LIBERAR);
                retorno.add(CONVENIO_DE_DESPESA_ENCERRAMENTO);
                break;
            case CONTRATO_DE_SERVICO_DESPESA:
                retorno.add(CONTRATO_DE_SERVICO_DESPESA_A_EXERCUTAR);
                retorno.add(CONTRATO_DE_SERVICO_DESPESA_EXECUTADO);
                break;
            case CONTRATO_DE_ALUGUEL_DESPESA:
                retorno.add(CONTRATO_DE_ALUGUEL_DESPESA_A_EXERCUTAR);
                retorno.add(CONTRATO_DE_ALUGUEL_DESPESA_EXECUTADO);
                break;
            case CONTRATO_DE_FORNECIMENTO_DE_BEM_DESPESA:
                retorno.add(CONTRATO_DE_FORNECIMENTO_DE_BEM_DESPESA_A_EXECUTAR);
                retorno.add(CONTRATO_DE_FORNECIMENTO_DE_BEM_DESPESA_EXECUTADO);
                break;
        }
        return retorno;
    }
}
