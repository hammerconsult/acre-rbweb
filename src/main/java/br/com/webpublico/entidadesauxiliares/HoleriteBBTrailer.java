package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.util.StringUtil;

/**
 * Created with IntelliJ IDEA.
 * User: Carnage
 * Date: 12/11/13
 * Time: 09:14
 * To change this template use File | Settings | File Templates.
 */
public class HoleriteBBTrailer {


    private String numeroIdentificadorDestinatario;
    private String numeroSequencialLinha;
    private String tipoRegistro;
    private String AgenciaEConta;
    private String quantidadeDestinatarios;
    private String espacosBrancos;


    public String getNumeroIdentificadorDestinatario() {
        return numeroIdentificadorDestinatario;
    }

    public void setNumeroIdentificadorDestinatario(String numeroIdentificadorDestinatario) {
        this.numeroIdentificadorDestinatario = numeroIdentificadorDestinatario;
    }

    public String getNumeroSequencialLinha() {
        return numeroSequencialLinha;
    }

    public void setNumeroSequencialLinha(String numeroSequencialLinha) {
        this.numeroSequencialLinha = numeroSequencialLinha;
    }

    public String getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(String tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public String getAgenciaEConta() {
        return AgenciaEConta;
    }

    public void setAgenciaEConta(String agenciaEConta) {
        AgenciaEConta = agenciaEConta;
    }

    public String getQuantidadeDestinatarios() {
        return quantidadeDestinatarios;
    }

    public void setQuantidadeDestinatarios(String quantidadeDestinatarios) {
        this.quantidadeDestinatarios = quantidadeDestinatarios;
    }

    public String getEspacosBrancos() {
        return espacosBrancos;
    }

    public void setEspacosBrancos(String espacosBrancos) {
        this.espacosBrancos = espacosBrancos;
    }

    public void montaTrailer(HoleriteBBTrailer trailer) {

//        4.1 -número identificador do destinatário do documento -Preencher obrigatoriamente com noves(9),nas vinte posições;
//        4.1 01-20 N 20 preencher com números Nove (999:.9)
        numeroIdentificadorDestinatario = StringUtil.cortaOuCompletaEsquerda(trailer.getNumeroIdentificadorDestinatario(), 20, "9");

//        4.2 -número seqüencial de identificação da linha do documento - Preencher com números noves;
//        4.2 21-22 N 02  preencher com números Nove (99)
        numeroSequencialLinha = StringUtil.cortaOuCompletaEsquerda(trailer.getNumeroSequencialLinha(), 2, "9");

//        4.3 -número identificador do tipo de registro -Preencher com nove (9);
//        4.3 23-23 N 01 preencher com o número Nove (9)
        tipoRegistro = StringUtil.cortaOuCompletaEsquerda(trailer.getTipoRegistro(), 1, "9");

//        4.4 -agência e conta -Preencher com noves (999:.99);
//        4.4 24-38 N 15 preencher com números Nove (999:.9)
        AgenciaEConta = StringUtil.cortaOuCompletaEsquerda(trailer.getAgenciaEConta(), 15, "9");

//        4.5 -quantidade de destinatários -Preencher com o número de destinatários no arquivo;
//        4.5 39-49 N 11 quantidade de destinatários do arquivo
        quantidadeDestinatarios = StringUtil.cortaOuCompletaEsquerda(trailer.getQuantidadeDestinatarios(), 11, "0");

//        4.6 -brancos -Preencher com brancos (espaços);
//        4.6 50-100 A 51 preencher com brancos
        espacosBrancos = StringUtil.cortaOuCompletaDireita(trailer.getEspacosBrancos(), 51, " ");
    }


    public String getTrailer() {

        StringBuilder sb = new StringBuilder();
        sb.append(numeroIdentificadorDestinatario);
        sb.append(numeroSequencialLinha);
        sb.append(tipoRegistro);
        sb.append(AgenciaEConta);
        sb.append(quantidadeDestinatarios);
        sb.append(espacosBrancos);
        sb.append("\r\n");
        return sb.toString();

    }
}
