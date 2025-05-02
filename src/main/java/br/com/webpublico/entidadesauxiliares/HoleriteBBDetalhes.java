package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.util.StringUtil;

/**
 * Created with IntelliJ IDEA.
 * User: Carnage
 * Date: 12/11/13
 * Time: 09:10
 * To change this template use File | Settings | File Templates.
 */
public class HoleriteBBDetalhes {

    private String numeroIdentificadorDestinatario;
    private String numeroSequencialLinha;
    private String tipoRegistro;
    private String textoDocumento;
    private String indicadorSaltoLinha;
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

    public String getTextoDocumento() {
        return textoDocumento;
    }

    public void setTextoDocumento(String textoDocumento) {
        this.textoDocumento = textoDocumento;
    }

    public String getIndicadorSaltoLinha() {
        return indicadorSaltoLinha;
    }

    public void setIndicadorSaltoLinha(String indicadorSaltoLinha) {
        this.indicadorSaltoLinha = indicadorSaltoLinha;
    }

    public String getEspacosBrancos() {
        return espacosBrancos;
    }

    public void setEspacosBrancos(String espacosBrancos) {
        this.espacosBrancos = espacosBrancos;
    }

    public void montaDetalhes(HoleriteBBDetalhes detalhes) {


//        3.1 -número identificador do destinatário do documento -Preencher com o número em que o destinatário é identificado na empresa
//        conveniada (contracheque: número de inscrição do funcionário na empresa);
//        3.1 01-20 N 20 identificador do destinatário empresa
        numeroIdentificadorDestinatario = StringUtil.cortaOuCompletaEsquerda(detalhes.getNumeroIdentificadorDestinatario(), 20, "0");

//        3.2 -número seqüencial de identificação da linha do documento - Preencher com o número da linha do texto (seqüencial);
//        3.2 21-22 N 02 seqüencial da linha do texto
        numeroSequencialLinha = StringUtil.cortaOuCompletaEsquerda(detalhes.getNumeroSequencialLinha(), 2, "0");

//        3.3 -número identificador do tipo de registro (documento=2) Preencher obrigatoriamente com o número dois (2);
//        3.3 23-23 N 01 preencher como número 2
        tipoRegistro = StringUtil.cortaOuCompletaEsquerda(detalhes.getTipoRegistro(), 1, "0");

//        3.4 -texto do documento - Preencher com o texto que comporá a linha do documento;
//        3.4 24-71 A 48 preencher com o texto do documento
        textoDocumento = StringUtil.cortaOuCompletaDireita(detalhes.getTextoDocumento(), 48, " ");

//        3.5 -indicador de salto de linha -Preencher zero (0) para linhas corridas (uma abaixo da outra)ou um (1) para saltar uma linha(a linha seguinte à do registro com indicador)
//        obs.: a linha seguinte a do registro com indicador de salto igual a "1" será toda preenchida com brancos;
//        3.5 72-72 A 01 1 -para saltar uma linha de intervalo, 0 -para linhas corridas
        indicadorSaltoLinha = StringUtil.cortaOuCompletaDireita(detalhes.getIndicadorSaltoLinha(), 1, "");

//        3.6 -brancos -Preencher com espaço brancos toda extensão;
//        3.6 73-100 A 28 preencher com brancos
        espacosBrancos = StringUtil.cortaOuCompletaDireita(detalhes.getEspacosBrancos(), 28, " ");

    }

    public String getDetalhes() {

        StringBuilder sb = new StringBuilder();
        sb.append(numeroIdentificadorDestinatario);
        sb.append(numeroSequencialLinha);
        sb.append(tipoRegistro);
        sb.append(textoDocumento);
        sb.append(indicadorSaltoLinha);
        sb.append(espacosBrancos);
        sb.append("\r\n");
        return sb.toString();
    }

}
