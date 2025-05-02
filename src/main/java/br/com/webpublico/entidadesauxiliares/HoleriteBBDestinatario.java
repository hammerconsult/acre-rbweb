package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.util.StringUtil;

/**
 * Created with IntelliJ IDEA.
 * User: Carnage
 * Date: 12/11/13
 * Time: 09:14
 * To change this template use File | Settings | File Templates.
 */
public class HoleriteBBDestinatario {

    private String numeroIdentificadorDestinatario;
    private String numeroSequencialLinha;
    private String tipoRegistro;
    private String numeroAgencia;
    private String numeroConta;
    private String quantidadeLinhas;
    private String nome;
    private String cpf;
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

    public String getNumeroAgencia() {
        return numeroAgencia;
    }

    public void setNumeroAgencia(String numeroAgencia) {
        this.numeroAgencia = numeroAgencia;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public String getQuantidadeLinhas() {
        return quantidadeLinhas;
    }

    public void setQuantidadeLinhas(String quantidadeLinhas) {
        this.quantidadeLinhas = quantidadeLinhas;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEspacosBrancos() {
        return espacosBrancos;
    }

    public void setEspacosBrancos(String espacosBrancos) {
        this.espacosBrancos = espacosBrancos;
    }

    public void montaDestinatario(HoleriteBBDestinatario destinatario) {


//        2.1 - número identificador do destinatário do documento - Preencher o  número em que o destinatário é identificado na empresa conveniada(contracheque: número de inscrição do funcionário na empresa)
//        Obs.: Este número será solicitado do destinatário para emissão do documento, nos terminais do Banco;
//        2.1  01-20   N    20    identificador do destinatário empresa
        numeroIdentificadorDestinatario = StringUtil.cortaOuCompletaEsquerda(destinatario.getNumeroIdentificadorDestinatario(), 20, "0");

//        2.2 - número seqüencial de identificação da linha do documento - Preencher com zeros;
//        2.2  21-22   N    02    preencher com números zeros (00)
        numeroSequencialLinha = StringUtil.cortaOuCompletaEsquerda(destinatario.getNumeroSequencialLinha(), 2, "0");

//        2.3 - número identificador do tipo de registro- Preencher com o número 1;
//        2.3  23-23   N    01    preencher com o número 1
        tipoRegistro = StringUtil.cortaOuCompletaEsquerda(destinatario.getTipoRegistro(), 1, "1");

//        2.4 - número da agência do destinatário - Preencher com o prefixo da   agência onde o destinatário do documento possui conta corrente (sem    dígito verificador);
//        2.4  24-27   N    04    prefixo da agência do BB, sem DV
        numeroAgencia = StringUtil.cortaOuCompletaEsquerda(destinatario.getNumeroAgencia(), 4, "0");

//        2.5 - número da conta do destinatário - (sem DV);
//        2.5  28-38   N    11    conta BB do destinatário no BB, sem DV
        numeroConta = StringUtil.cortaOuCompletaEsquerda(destinatario.getNumeroConta(), 11, "0");

//        2.6 - quantidade de linhas do documento - Preencher com a quantidade   de linhas do documento para o destinatário desse registro (indicador   de salto de linha não entra no somatório);
//        2.6  39-40   N    02    quantidade de linhas do texto
        quantidadeLinhas = StringUtil.cortaOuCompletaEsquerda(destinatario.getQuantidadeLinhas(), 2, "0");

//        2.7 - nome do destinatário;
//        2.7  41-80   A    40    nome do destinatário
        nome = StringUtil.cortaOuCompletaDireita(destinatario.getNome(), 40, " ");

//        2.8 - CPF - Preencher com o número do CPF do destinatário do documento a ser emitido, para conferência com os números agência e conta informados nos campos 2.4 e 2.5;
//        2.8  81-91   N    11    CPF do destinatário
        cpf = StringUtil.cortaOuCompletaEsquerda(destinatario.getCpf(), 11, "0");

//        2.9 - brancos - Preencher com espaços brancos toda a extensão.
//        2.9  92-100  A    09    preencher com brancos
        espacosBrancos = StringUtil.cortaOuCompletaDireita(destinatario.getEspacosBrancos(), 9, " ");
    }


    public String getDestinatario() {

        StringBuilder sb = new StringBuilder();
        sb.append(numeroIdentificadorDestinatario);
        sb.append(numeroSequencialLinha);
        sb.append(tipoRegistro);
        sb.append(numeroAgencia);
        sb.append(numeroConta);
        sb.append(quantidadeLinhas);
        sb.append(nome);
        sb.append(cpf);
        sb.append(espacosBrancos);
        sb.append("\r\n");
        return sb.toString();

    }
}
