package br.com.webpublico.entidades;

import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 * User: Usuario
 * Date: 18/10/13
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */
public class CreditoSalarioTrailerLote implements Serializable {

    private String codigoBanco;
    private String loteServico;
    private String tipoRegistro;
    private String cnab9posicoes; //febraban
    private String quantidadeRegistrosLote;
    private String somatoriaValores;
    private String somatoriaQuantidadeMoeda;
    private String numeroAvisoDevito;
    private String cnab165posicoes; //febraban
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

    public String getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(String tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public String getCnab9posicoes() {
        return cnab9posicoes;
    }

    public void setCnab9posicoes(String cnab9posicoes) {
        this.cnab9posicoes = cnab9posicoes;
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

    public String getCnab165posicoes() {
        return cnab165posicoes;
    }

    public void setCnab165posicoes(String cnab165posicoes) {
        this.cnab165posicoes = cnab165posicoes;
    }

    public String getCodigoOcorrenciasRetorno() {
        return codigoOcorrenciasRetorno;
    }

    public void setCodigoOcorrenciasRetorno(String codigoOcorrenciasRetorno) {
        this.codigoOcorrenciasRetorno = codigoOcorrenciasRetorno;
    }

    public String getTrailerLoteArquivo() {
        StringBuilder sb = new StringBuilder();
        sb.append(getCodigoBanco());
        sb.append(getLoteServico());
        sb.append(getTipoRegistro());
        sb.append(getCnab9posicoes());
        sb.append(getQuantidadeRegistrosLote());
        sb.append(getSomatoriaValores());
        sb.append(getSomatoriaQuantidadeMoeda());
        sb.append(getNumeroAvisoDevito());
        sb.append(getCnab165posicoes());
        sb.append(getCodigoOcorrenciasRetorno());

        if (!validaTrailerLote(sb.toString())) {
            //System.out.println("o TRAILER DO LOTE TEM " + sb.length() + " caracteres de "+NUMERO_CARACTERES_POR_LINHA);
        }

        sb.append("\r\n");

        return sb.toString();

    }


    public void montaTrailerLoteArquivo(Banco banco, Integer seqLote, Integer qtdRegistros, Double somaValor) {
        String strSomaValor = StringUtil.retornaApenasNumeros(Util.formataNumero(somaValor));

        setCodigoBanco(StringUtil.cortaOuCompletaEsquerda(banco.getNumeroBanco(), 3, "0"));
        setLoteServico(StringUtil.cortaOuCompletaEsquerda(seqLote.toString(), 4, "0"));
        setTipoRegistro("5");
        setCnab9posicoes(StringUtils.repeat(" ", 9));
        setQuantidadeRegistrosLote(StringUtil.cortaOuCompletaEsquerda(qtdRegistros.toString(), 6, "0"));
        setSomatoriaValores(StringUtil.cortaOuCompletaEsquerda(strSomaValor, 18, "0"));
        setSomatoriaQuantidadeMoeda(StringUtil.cortaOuCompletaEsquerda("0", 18, "0"));
        setNumeroAvisoDevito(StringUtils.repeat("0", 6));
        setCnab165posicoes(StringUtils.repeat(" ", 165));
        setCodigoOcorrenciasRetorno(StringUtils.repeat(" ", 10));

    }

    public boolean validaTrailerLote(String linha) {
        boolean valido = true;
        if (linha.length() != NUMERO_CARACTERES_POR_LINHA) {
            valido = false;
        }

        return valido;
    }
}
