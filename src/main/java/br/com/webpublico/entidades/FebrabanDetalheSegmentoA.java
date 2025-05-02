/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author peixe
 */
public class FebrabanDetalheSegmentoA implements Serializable {

    private String tipoRegistro = "3";
    private String segmento = "A";
    private String tipoMovimento = "0";
    private String numeroRegistro;
    private ContratoFP contrato;
    private FichaFinanceiraFP ficha;

    public ContratoFP getContrato() {
        return contrato;
    }

    public void setContrato(ContratoFP contrato) {
        this.contrato = contrato;
    }

    public FichaFinanceiraFP getFicha() {
        return ficha;
    }

    public void setFicha(FichaFinanceiraFP ficha) {
        this.ficha = ficha;
    }

    public FebrabanDetalheSegmentoA(ContratoFP contrato, FichaFinanceiraFP ficha) {
        this.contrato = contrato;
        this.ficha = ficha;
    }

    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public FebrabanDetalheSegmentoA() {
    }

    public String gerarDetalheA(FebrabanHeaderArquivo headerArquivo, Double valorPagemento, Date dataPagamento, Integer sequenciaLote, Integer sequencialRegistro, FolhaDePagamento folha) {
        StringBuilder sb = new StringBuilder();
        //Controle - Banco
        sb.append(StringUtil.cortaOuCompletaEsquerda(contrato.getContaCorrente().getAgencia().getBanco().getNumeroBanco(), 3, "0"));
        //Controle - Lote
        sb.append(StringUtil.cortaOuCompletaEsquerda(sequenciaLote.toString(), 4, "0"));
        //Controle - Registro
        sb.append("3");
        //Serviço - Número do Registro
        sb.append(StringUtil.cortaOuCompletaEsquerda(sequencialRegistro.toString(), 5, "0"));
        //Serviço - Segmento
        sb.append("A");
        //Serviço - Movimento - Tipo
        sb.append("0");
        //Serviço - Movimento - Código
        sb.append("00");
        //Favorecido - Câmara
        sb.append("000");
        //Favorecido - Banco
        sb.append(StringUtil.cortaOuCompletaEsquerda(contrato.getContaCorrente().getAgencia().getBanco().getNumeroBanco(), 3, "0"));
        //Favorecido - Conta Corrente - Agência - Código
        sb.append(StringUtil.cortaOuCompletaEsquerda(contrato.getContaCorrente().getAgencia().getNumeroAgencia().toString(), 5, "0"));
        //Favorecido - Conta Corrente - Agência - DV
        sb.append(StringUtil.cortaOuCompletaEsquerda(contrato.getContaCorrente().getAgencia().getDigitoVerificador().toString(), 1, "0"));
        //Favorecido - Conta Corrente - Conta - Código
        sb.append(StringUtil.cortaOuCompletaEsquerda(contrato.getContaCorrente().getNumeroConta(), 12, "0"));
        //Favorecido - Conta Corrente - Conta - DV
        sb.append(StringUtil.cortaOuCompletaEsquerda(contrato.getContaCorrente().getDigitoVerificador(), 1, "0"));
        //Favorecido - Conta Corrente - DV
        sb.append(StringUtil.cortaOuCompletaEsquerda(contrato.getContaCorrente().getDigitoVerificador(), 1, "0"));
        //Favorecido - Nome
        sb.append(StringUtil.cortaOuCompletaDireita(contrato.getMatriculaFP().getPessoa().getNome(), 30, " "));
        //Crédito - Seu Número
//        Calendar c = Calendar.getInstance();
//        String mes = String.valueOf(folha.getMes().ordinal() + 1);
//        if (mes.length() == 1) {
//            mes = "0" + mes;
//        }
//        String ano = folha.getAno() + "";
//        sb.append(mes).append(ano);
        sb.append(StringUtil.cortaOuCompletaEsquerda(ficha.getId().toString(), 20, "0"));
        //Crédito - Data de Pagamento
        sb.append(Util.dateToString(dataPagamento).replaceAll("/", ""));
        //Crédito - Moeda - Tipo
        sb.append("BRL");
        //Crédito - Moeda - Quantidade
        sb.append(StringUtils.repeat("0", 10));
        //Crédito - Valor Pagamento
        sb.append(StringUtil.cortaOuCompletaEsquerda(String.valueOf(valorPagemento.doubleValue()).replaceAll("\\.", ""), 13, "0"));
        //Crédito - Nosso Número
        sb.append(StringUtil.cortaOuCompletaDireita("", 20, " "));
        //Crédito - Data Real
        sb.append(StringUtils.repeat("0", 8));
        //Crédito - Valor Real
        sb.append(StringUtils.repeat("0", 13));
        //Informação 2
        sb.append(StringUtil.cortaOuCompletaDireita("", 40, " "));
        //Código Finalidade Doc
        sb.append(StringUtil.cortaOuCompletaDireita("", 2, " "));
        //Código Finalidade TED
        sb.append(StringUtil.cortaOuCompletaDireita("", 5, " "));
        //Código Finalidade Complementar
        sb.append(StringUtil.cortaOuCompletaDireita("", 2, " "));
        //CNAB
        sb.append(StringUtil.cortaOuCompletaDireita("", 3, " "));
        //Aviso
        sb.append("0");
        //Ocorrências
        sb.append(StringUtil.cortaOuCompletaDireita("", 10, " "));
        sb.append("\n");

        return sb.toString();
    }
}
