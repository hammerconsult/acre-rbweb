package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.util.StringUtil;

/**
 * Created with IntelliJ IDEA.
 * User: Carnage
 * Date: 12/11/13
 * Time: 08:46
 * To change this template use File | Settings | File Templates.
 */
public class HoleriteBBHeader {

    private String numeroIdentificadorDestinatario;
    private String numeroSequencialLinha;
    private String tipoRegistro;
    private String numeroAgencia;
    private String numeroConta;
    private String nomeArquivo;
    private String numeroRemessa;
    private String codigoProduto;
    private String codigoModalidade;
    private String anoFolhaPagamento;
    private String mesFolhaPagamento;
    private String remessaExtra;
    private String dataReferencia;
    private String espacosBrancos;
    private String numeroDoContrato;


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

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getNumeroRemessa() {
        return numeroRemessa;
    }

    public void setNumeroRemessa(String numeroRemessa) {
        this.numeroRemessa = numeroRemessa;
    }

    public String getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(String codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public String getCodigoModalidade() {
        return codigoModalidade;
    }

    public void setCodigoModalidade(String codigoModalidade) {
        this.codigoModalidade = codigoModalidade;
    }

    public String getAnoFolhaPagamento() {
        return anoFolhaPagamento;
    }

    public void setAnoFolhaPagamento(String anoFolhaPagamento) {
        this.anoFolhaPagamento = anoFolhaPagamento;
    }

    public String getMesFolhaPagamento() {
        return mesFolhaPagamento;
    }

    public void setMesFolhaPagamento(String mesFolhaPagamento) {
        this.mesFolhaPagamento = mesFolhaPagamento;
    }

    public String getRemessaExtra() {
        return remessaExtra;
    }

    public void setRemessaExtra(String remessaExtra) {
        this.remessaExtra = remessaExtra;
    }

    public String getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(String dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public String getEspacosBrancos() {
        return espacosBrancos;
    }

    public void setEspacosBrancos(String espacosBrancos) {
        this.espacosBrancos = espacosBrancos;
    }

    public String getNumeroDoContrato() {
        return numeroDoContrato;
    }

    public void setNumeroDoContrato(String numeroDoContrato) {
        this.numeroDoContrato = numeroDoContrato;
    }

    public void montaHeader(HoleriteBBHeader header) {

        //1.1 - número identificador do destinatário do documento - Preencher,   obrigatoriamente, com o número zero, nas vinte posições do campo;
        //1.1 01-20 N 20 preencher com zeros(000:.0)
        numeroIdentificadorDestinatario = StringUtil.cortaOuCompletaEsquerda(header.getNumeroIdentificadorDestinatario(), 20, "0");

        //1.2 - número seqüencial de identificação da linha do documento -       Preencher, obrigatoriamente, com zeros;
        //1.2 21-22 N 02 preencher com zeros (00)
        numeroSequencialLinha = StringUtil.cortaOuCompletaEsquerda(header.getNumeroSequencialLinha(), 2, "0");

        //1.3 - número identificador do tipo de registro (header=0).Preencher,   obrigatoriamente, com zeros;
        //1.3 23-23 N 01 preencher com zero
        tipoRegistro = StringUtil.cortaOuCompletaEsquerda(header.getTipoRegistro(), 1, "0");

        //1.4 - número da agência- Preencher com o número da agência na qual     será cobrada tarifa ou número da agência conveniada que detenha a      conta (sem número verificador);
        //1.4 24-27 N 04 prefixo da agência do BB, sem DV
        numeroAgencia = StringUtil.cortaOuCompletaEsquerda(header.getNumeroAgencia(), 4, "0");

        //1.5 - número da conta- Preencher com o número da conta corrente da     empresa, onde será cobrada a tarifa ou da conta da empresa conveniada, sem DV;
        //1.5 28-38 N 11 num. da conta da empresa no BB, sem DV
        numeroConta = StringUtil.cortaOuCompletaEsquerda(header.getNumeroConta(), 11, "0");

        //1.6 - nome do arquivo - Preencher com o texto "EDO001";
        //1.6 39-44 A 06 preencher com EDO001
        nomeArquivo = StringUtil.cortaOuCompletaDireita(header.getNomeArquivo(), 6, " ");

        //1.7 - número do contrato - Preencher com o número do contrato a ser    informado pelo Banco;
        //1.7 45-56 N 12 número do contrato, atribuído pelo EDO
        numeroDoContrato = StringUtil.cortaOuCompletaEsquerda(header.getNumeroDoContrato(), 12, "0");

        //1.8 - número da remessa - Preencher com o número seqüencial da remessa (primeira igual a 1, segunda a 2, assim por diante);
        //1.8 57-62 N 06 número seqüencial da remessa
        numeroRemessa = StringUtil.cortaOuCompletaEsquerda(header.getNumeroRemessa(), 6, "0");

        //1.9 - código do produto - Preencher com o código do produto a ser      informado pelo Banco (Contracheque=00315);
        //1.9 63-67 N 05 código do produto - 00315
        codigoProduto = StringUtil.cortaOuCompletaEsquerda(header.getCodigoProduto(), 5, "0");

        //1.10 - código da modalidade - Ex: CONTRACHEQUE - 000001;
        //1.10 68-72 N 05 código da modalidade - 00001
        codigoModalidade = StringUtil.cortaOuCompletaEsquerda(header.getCodigoModalidade(), 5, "0");

        //1.11 - ano - Preencher com o ano da folha de pagamento;
        //1.11 73-76 N 04 ano a que se refere o documento
        anoFolhaPagamento = StringUtil.cortaOuCompletaEsquerda(header.getAnoFolhaPagamento(), 4, "0");

        //1.12 - mês - Preencher com o mês da folha de pagamento;
        //1.12 77-78 N 02 mês a que se refere o documento
        mesFolhaPagamento = StringUtil.cortaOuCompletaEsquerda(header.getMesFolhaPagamento(), 2, "0");

        //1.13 - remessa extra - Preencher com conteúdo numérico. Campo disponível para controle por parte do cliente. Poderá ser utilizado para controle da quantidade de remessas relativas ao mês. Por exemplo: em janeiro um cliente enviou três remessas ao Banco, este campo foi    preenchido respectivamente, com os números 01,02 e 03, de forma        a espelhar que para o mês de janeiro foram enviadas três remessas ao   Banco. Caso não haja interesse nesse controle, o cliente poderá        informar "00" no referido campo em todas as remessas.
        //1.13 79-80 N 02 número seqüencial de extraordinariedade
        remessaExtra = StringUtil.cortaOuCompletaEsquerda(header.getRemessaExtra(), 2, "0");

        //1.14 - data de referência - Preencher com a data (DDMMAAAA) do crédito da folha de pagamento;
        //1.14 81-88 N 08 data (ddmmaaaa) referente ao crédito
        dataReferencia = StringUtil.cortaOuCompletaEsquerda(header.getDataReferencia(), 8, "0");

        //1.15 - brancos - Deixar em branco (espaços);
        //1.15 89-100 A 12 preencher com brancos
        espacosBrancos = StringUtil.cortaOuCompletaDireita(header.getEspacosBrancos(), 12, " ");
    }


    public String getHeader() {

        StringBuilder sb = new StringBuilder();
        sb.append(numeroIdentificadorDestinatario);
        sb.append(numeroSequencialLinha);
        sb.append(tipoRegistro);
        sb.append(numeroAgencia);
        sb.append(numeroConta);
        sb.append(nomeArquivo);
        sb.append(numeroDoContrato);
        sb.append(numeroRemessa);
        sb.append(codigoProduto);
        sb.append(codigoModalidade);
        sb.append(anoFolhaPagamento);
        sb.append(mesFolhaPagamento);
        sb.append(remessaExtra);
        sb.append(dataReferencia);
        sb.append(espacosBrancos);
        sb.append("\r\n");

        return sb.toString();

    }
}
