/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.util.obn350.*;

/**
 *
 * @author reidocrime
 */
public enum ConfiguracaoRegistroTipoDoisOBN600 {

    ID2("Tipo", 1, 0, 1),
    CODIGOAGENCAIABANCARIAUNDGESTORAEMITENTE("Agnecia DV", 5, 1, 6),
    CODIGOUNIDADEGESTORAEMITENTEDASOBS("Código U.G", 11, 6, 17),
    NUMERORELACAOEXTERNAQUECONTASORDEMBANCARIA("N° Bordero", 11, 17, 28),
    NUMEROORDEMBANCARIA("N° Pagamento", 11, 28, 39),
    DATAREFERENCIADARELACAO("Data Bordero", 8, 39, 47),
    BRANCOS("Branco", 4, 47, 51),
    CODIGODAOPERACAO("Código Operação", 2, 51, 53),
    INDICADORTIPOPAGAMENTOPESSOAL("Tipo Pagamento Pessoal", 1, 53, 54),
    ZEROS("Zeros", 9, 54, 63),
    VALORLIQUIDOORDEMBACARIA("R$ Liquida", 17, 63,80 ),
    CODIGOBANCOFAVORECIDO("Banco Favorecido", 3, 80, 83),
    CODIGOAGENCIAFAVORECISASEMDIGITO("Agência Favorecido", 4, 83, 87),
    DIGITOVERIFICADOR("Digito Verificador", 1, 87, 88),
    CODIGOCONTACORRENTAFAVORECIDOSEMDIGITO("Conta Bancaria", 10, 88, 98),
    NOMEFAVORECIDO("Nome Favorecido", 45, 98, 143),
    ENDERECOFAVORECIDO("Endereço Favorecido", 65, 143, 208),
    MUNICIPIOFAVORECIDO("Municipio FAvorecido", 28, 208, 236),
    CODIGOCIAF_GRU("GRU", 17, 236, 253),
    CEPFAVORECIDO("Cep Favorecido", 8, 253, 261),
    UFFAVORECIDO("Uf Favorecido", 2, 261, 263),
    OBSERVACAOOB("Observção", 40, 263, 303),
    INDICADORTIPOPAGAMENTO("Tipo Pagamento", 1, 303, 304),
    TIPOFAVORECIO("Tipo Favorecido", 1, 304, 305),
    CODIGOFAVORECIDO("Codigo Favorecido", 14, 305, 319),
    PREFIXODVAGENCIAPARADEBITO("Prefixo DV", 5, 319, 324),
    NUMEROCONTACONVENIODV("Convenio DV", 10, 324, 334),
    FINALIDADEPAGAMENTOFUNDEB("FUNDEB", 3, 334, 337),
    BRANCO("Branco", 4, 337, 341),
    CODIGODERETORNOOPERACAO("Código de Retorno", 2, 341, 343),
    NUMEROSEQUENCIALNOARQUIVO("N° Sequencial", 7, 343, 350);
    private String descricao;
    private int tamanho;
    private int inicio;
    private int fim;

    private ConfiguracaoRegistroTipoDoisOBN600(String descricao, int tamanho, int inicio, int fim) {
        this.descricao = descricao;
        this.tamanho = tamanho;
        this.inicio = inicio;
        this.fim = fim;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getTamanho() {
        return tamanho;
    }

    public int getInicio() {
        return inicio;
    }

    public int getFim() {
        return fim;
    }
}
