package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum OrigemSolicitacaoEmpenho implements EnumComDescricao {

    RESTITUICAO("Restituicao"),
    CONTRATO("Contrato"),
    ATA_REGISTRO_PRECO("Ata Registro de Preço"),
    DISPENSA_LICITACAO_INEXIGIBILIDADE("Dispensa de Licitação/Inexigibilidade"),
    RECONHECIMENTO_DIVIDA_EXERCICIO("Reconhecimento da Dívida do Exercício"),
    CONVENIO_DESPESA("Convênio Despesa"),
    VALOR_PARCELA("Valor Parcela"),
    OUTROS_ENCARGOS("Outros Encargos"),
    JUROS("Juros"),
    DIARIA("Diária");

    String descricao;

    OrigemSolicitacaoEmpenho(String descricao) {
        this.descricao = descricao;
    }


    public boolean isOrigemExecucaoProcesso(){
        return this.equals(ATA_REGISTRO_PRECO) || this.equals(DISPENSA_LICITACAO_INEXIGIBILIDADE);
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
