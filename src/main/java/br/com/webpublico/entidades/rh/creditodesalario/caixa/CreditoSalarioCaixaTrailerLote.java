package br.com.webpublico.entidades.rh.creditodesalario.caixa;

import br.com.webpublico.entidades.Banco;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Usuario
 * Date: 18/10/13
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */
public class CreditoSalarioCaixaTrailerLote implements Serializable {

    private String codigoBanco;
    private String loteServico;
    private String codigoRegistro;
    private String filler9;
    private String quantidadeRegistrosLote;
    private String somatoriaValores;
    private String somatoriaQuantidadeMoeda;
    private String numeroAvisoDevito;
    private String filler165; //febraban
    private String codigoOcorrenciasRetorno;
    private final Integer NUMERO_CARACTERES_POR_LINHA = 240;

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

    public String getQuantidadeRegistrosLote() {
        return quantidadeRegistrosLote;
    }

    public void setQuantidadeRegistrosLote(String quantidadeRegistrosLote) {
        this.quantidadeRegistrosLote = quantidadeRegistrosLote;
    }

    public String getSomatoriaValores() {
        return somatoriaValores;
    }

    public void setSomatoriaValores(String somatoriaValores) {
        this.somatoriaValores = somatoriaValores;
    }

    public String getSomatoriaQuantidadeMoeda() {
        return somatoriaQuantidadeMoeda;
    }

    public void setSomatoriaQuantidadeMoeda(String somatoriaQuantidadeMoeda) {
        this.somatoriaQuantidadeMoeda = somatoriaQuantidadeMoeda;
    }

    public String getNumeroAvisoDevito() {
        return numeroAvisoDevito;
    }

    public void setNumeroAvisoDevito(String numeroAvisoDevito) {
        this.numeroAvisoDevito = numeroAvisoDevito;
    }

    public String getCodigoOcorrenciasRetorno() {
        return codigoOcorrenciasRetorno;
    }

    public void setCodigoOcorrenciasRetorno(String codigoOcorrenciasRetorno) {
        this.codigoOcorrenciasRetorno = codigoOcorrenciasRetorno;
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

    public String getFiller165() {
        return filler165;
    }

    public void setFiller165(String filler165) {
        this.filler165 = filler165;
    }

    public String getTrailerLoteArquivo() {
        StringBuilder sb = new StringBuilder();
        sb.append(getCodigoBanco());
        sb.append(getLoteServico());
        sb.append(getCodigoRegistro());
        sb.append(getFiller9());
        sb.append(getQuantidadeRegistrosLote());
        sb.append(getSomatoriaValores());
        sb.append(getSomatoriaQuantidadeMoeda());
        sb.append(getNumeroAvisoDevito());
        sb.append(getFiller165());
        sb.append(getCodigoOcorrenciasRetorno());

        sb.append("\r\n");
        String retorno = sb.toString();
        retorno = retorno.toUpperCase();
        retorno = StringUtil.removeCaracteresEspeciais(retorno);
        retorno = retorno.replaceAll("[^\\x00-\\x7F]", " ");

        return retorno;
    }


    public void montaTrailerLoteArquivo(Banco banco, Integer seqLote, Integer qtdRegistros, Double somaValor) {
        String strSomaValor = StringUtil.retornaApenasNumeros(Util.formataNumero(somaValor));

        setCodigoBanco(StringUtil.cortaOuCompletaEsquerda(banco.getNumeroBanco(), 3, "0"));
        setLoteServico(StringUtil.cortaOuCompletaEsquerda(seqLote.toString(), 4, "0"));
        setCodigoRegistro("5");
        setFiller9("         ");
        setQuantidadeRegistrosLote(StringUtil.cortaOuCompletaEsquerda(qtdRegistros.toString(), 6, "0"));
        setSomatoriaValores(StringUtil.cortaOuCompletaEsquerda(strSomaValor, 18, "0"));
        setSomatoriaQuantidadeMoeda(StringUtil.cortaOuCompletaEsquerda("0", 18, "0"));
        setNumeroAvisoDevito("000000");
        setFiller165("                                                                                                                                                                     ");
        setCodigoOcorrenciasRetorno("          ");

    }
}
