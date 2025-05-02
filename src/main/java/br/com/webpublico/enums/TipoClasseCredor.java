/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoClasseCredorDTO;

/**
 * @author wiplash
 */
public enum TipoClasseCredor {

    NENHUM("Nenhum", TipoClasseCredorDTO.NENHUM),
    REMUNERACAO_BENEFICIO_PESSOAL("Remuneração e Benefício de Pessoal", TipoClasseCredorDTO.REMUNERACAO_BENEFICIO_PESSOAL),
    BENEFICIO_PREVIDENCIARIO("Benefício Previdenciário", TipoClasseCredorDTO.BENEFICIO_PREVIDENCIARIO),
    BENEFICIO_ASSISTENCIAL("Benefício Assistencial", TipoClasseCredorDTO.BENEFICIO_ASSISTENCIAL),
    ENCARGOS_SOCIAIS_INSS("Encargos Sociais INSS", TipoClasseCredorDTO.ENCARGOS_SOCIAIS_INSS),
    ENCARGOS_SOCIAIS_RPPS("Encargos Sociais RPPS", TipoClasseCredorDTO.ENCARGOS_SOCIAIS_RPPS),
    ENCARGOS_SOCIAIS_FGTS("Encargos Sociais FGTS", TipoClasseCredorDTO.ENCARGOS_SOCIAIS_FGTS),
    FORNECEDOR_NACIONAL("Fornecedor Nacional", TipoClasseCredorDTO.FORNECEDOR_NACIONAL),
    CONTAS_PAGAR_CREDOR("Contas a Pagar Credor Nacional", TipoClasseCredorDTO.CONTAS_PAGAR_CREDOR),
    PRESTADOR_SERVICO("Prestador de Serviço", TipoClasseCredorDTO.PRESTADOR_SERVICO),
    TESOURO_MUNICIPAL("Tesouro Municipal", TipoClasseCredorDTO.TESOURO_MUNICIPAL),
    TESOURO_ESTADUAL("Tesouro Estadual", TipoClasseCredorDTO.TESOURO_ESTADUAL),
    TESOURO_FERERAL("Tesouro Federal", TipoClasseCredorDTO.TESOURO_FERERAL),
    DEPOSITO_CONSIGNACAO("Depósito Consignação", TipoClasseCredorDTO.DEPOSITO_CONSIGNACAO),
    DEPOSITO_GARANTIA("Depósito Garantia", TipoClasseCredorDTO.DEPOSITO_GARANTIA),
    DEPOSITO_JUDICIAL("Depósito Judicial", TipoClasseCredorDTO.DEPOSITO_JUDICIAL),
    DEPOSITO_NAO_JUDICIAL("Depósito não Judicial", TipoClasseCredorDTO.DEPOSITO_NAO_JUDICIAL),
    DEPOSITO_DIVERSO("Depósito Diverso", TipoClasseCredorDTO.DEPOSITO_DIVERSO),
    INDENIZACAO("Indenização", TipoClasseCredorDTO.INDENIZACAO),
    RESTITUICAO("Restituição", TipoClasseCredorDTO.RESTITUICAO),
    DIARIA_CIVIL("Diárias Civil", TipoClasseCredorDTO.DIARIA_CIVIL),
    DIARIA_CAMPO("Diárias de Campo", TipoClasseCredorDTO.DIARIA_CAMPO),
    DIARIA_COLABORADOR("Diárias a Colaborador", TipoClasseCredorDTO.DIARIA_COLABORADOR),
    SUPRIMENTO_FUNDO("Suprimento de Fundos", TipoClasseCredorDTO.SUPRIMENTO_FUNDO),
    SUBVENCAO("Subvenção", TipoClasseCredorDTO.SUBVENCAO),
    CONTRIBUICAO("Contribuição", TipoClasseCredorDTO.CONTRIBUICAO),
    INCENTIVO("Incentivo", TipoClasseCredorDTO.INCENTIVO),
    CONVENIIO_RECEITA("Convênio de Receita", TipoClasseCredorDTO.CONVENIIO_RECEITA),
    CONVENIIO_DESPESA("Convênio de Despesa", TipoClasseCredorDTO.CONVENIIO_DESPESA),
    SENTENCA_JUDICIAL("Sentença Judicial", TipoClasseCredorDTO.SENTENCA_JUDICIAL),
    DIVIDA_PUBLICA("Dívida Pública", TipoClasseCredorDTO.DIVIDA_PUBLICA),
    PRECATORIOS("Precatórios", TipoClasseCredorDTO.PRECATORIOS),
    OUTROS("Outros", TipoClasseCredorDTO.OUTROS),
    AUXILIO("Auxílio", TipoClasseCredorDTO.AUXILIO),
    COMPENSACAO("Compensação", TipoClasseCredorDTO.COMPENSACAO);

    private String descricao;
    private TipoClasseCredorDTO toDto;

    TipoClasseCredor(String descricao, TipoClasseCredorDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoClasseCredorDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
