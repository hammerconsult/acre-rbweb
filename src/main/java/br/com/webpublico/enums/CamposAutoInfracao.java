/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 *
 * @author user
 */
public enum CamposAutoInfracao {

    ORGAO_AUTUADOR("1. Código do Órgão Autuador"),
    PLACA_VEICULO("2.1. Placa do Veículo"),
    MUNICIPIO_VEICULO("2.2. Município do Veículo"),
    UF_VEICULO("2.3. UF do Veículo"),
    MARCA_MODELO_VEICULO("2.4. Marca/Modelo do Veículo"),
    CATEGORIA_VEICULO("2.5. Categoria do Veículo"),
    NOME_CONDUTOR_INFRATOR("3.1. Nome do Condutor Infrator"),
    CNH_INFRATOR("3.2. CNH do Infrator"),
    CPF_INFRATOR("3.3. CPF do Infrator"),
    ENDERECO_INFRATOR("3.4. Endereço do Infrator"),
    NOME_PERMISSIONARIO("4.1. Nome do Permissionário"),
    ENDERECO_PERMISSIONARIO("4.2. Endereço do Permissionário"),
    MUNICIPIO_PERMISSIONARIO("4.3. Município do Permissionário"),
    UF_PERMISSIONARIO("4.4. UF do Permissionário"),
    CPF_PERMISSIONARIO("4.5. CPF do Permissionário"),
    LOCAL_INFRACAO("5.1. Local da Infração"),
    DATA_INFRACAO("5.2. Data da Infração"),
    HORA_INFRACAO("5.3. Hora da Infração"),
    CODIGO_MUNICIPIO("5.4. Código do Município"),
    LINHA("5.5. Linha"),
    NUMERO_PERMISSIVO("5.6. Número Permissivo"),
    TIPIFICACAO_INFRACAO("6. Tipificação da Infração"),
    INSTRUMENTO_AFERICAO("6.1. Instrumento de Aferição"),
    PERMITIDA("6.2. Permitida"),
    AFERIDA("6.3. Aferida"),
    OBSERVACOES("7. Observações"),
    MATRICULA_AGENTE("8.1. Matrícula do Agente"),
    AGENTE("8.2. Agente"),
    ASSINATURA_AGENTE("8.3. Assinatura do Agente");
    private String descricao;

    private CamposAutoInfracao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
