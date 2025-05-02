package br.com.webpublico.entidades.rh.creditodesalario.caixa;

import br.com.webpublico.entidades.rh.creditodesalario.CreditoSalario;
import br.com.webpublico.util.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Usuario
 * Date: 18/10/13
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */
public class CreditoSalarioCaixaTrailer implements Serializable {
    private final Integer NUMERO_CARACTERES_POR_LINHA = 240;
    private String codigoBanco;
    private String loteServico;
    private String codigoRegistro;
    private String filler9; //febraban
    private String quantidadeLotes;
    private String quantidadeRegistros;
    private String quantidadeContas;
    private String filler205; //febraban

    public String getCodigoBanco() {
        return codigoBanco;
    }

    public void setCodigoBanco(String codigoBanco) {
        this.codigoBanco = codigoBanco;
    }

    public String getLoteServico() {
        return loteServico;
    }

    public void setLoteServico(String loteServico) {
        this.loteServico = loteServico;
    }

    public String getCodigoRegistro() {
        return codigoRegistro;
    }

    public void setCodigoRegistro(String codigoRegistro) {
        this.codigoRegistro = codigoRegistro;
    }

    public String getFiller9() {
        return filler9;
    }

    public void setFiller9(String filler9) {
        this.filler9 = filler9;
    }

    public String getFiller205() {
        return filler205;
    }

    public void setFiller205(String filler205) {
        this.filler205 = filler205;
    }

    public String getQuantidadeLotes() {
        return quantidadeLotes;
    }

    public void setQuantidadeLotes(String quantidadeLotes) {
        this.quantidadeLotes = quantidadeLotes;
    }

    public String getQuantidadeRegistros() {
        return quantidadeRegistros;
    }

    public void setQuantidadeRegistros(String quantidadeRegistros) {
        this.quantidadeRegistros = quantidadeRegistros;
    }

    public String getQuantidadeContas() {
        return quantidadeContas;
    }

    public void setQuantidadeContas(String quantidadeContas) {
        this.quantidadeContas = quantidadeContas;
    }

    public String getTrailerArquivo() {
        StringBuilder sb = new StringBuilder();

        sb.append(getCodigoBanco());
        sb.append(getLoteServico());
        sb.append(getCodigoRegistro());
        sb.append(getFiller9());
        sb.append(getQuantidadeLotes());
        sb.append(getQuantidadeRegistros());
        sb.append(getQuantidadeContas());
        sb.append(getFiller205());

        String retorno = sb.toString();
        retorno = retorno.toUpperCase();
        retorno = StringUtil.removeCaracteresEspeciais(retorno);
        retorno = retorno.replaceAll("[^\\x00-\\x7F]", " ");

        return retorno;
    }

    public void montaTrailerArquivo(CreditoSalario creditoSalario, Integer quantidadeLotes, Integer totalRegistros, Integer quantidadeContas) {
        setCodigoBanco(StringUtil.cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco(), 3, "0"));
        setLoteServico("9999");
        setCodigoRegistro("9");
        setFiller9("         ");
        setQuantidadeLotes(StringUtil.cortaOuCompletaEsquerda(quantidadeLotes.toString(), 6, "0"));
        Integer totalRegistrosArquivo = totalRegistros + (quantidadeLotes * 2);
        setQuantidadeRegistros(StringUtil.cortaOuCompletaEsquerda(totalRegistrosArquivo + "", 6, "0"));
        setQuantidadeContas(StringUtil.cortaOuCompletaEsquerda(quantidadeContas.toString(), 6, "0"));
        setFiller205(StringUtils.repeat(" ", 205));
    }
}
