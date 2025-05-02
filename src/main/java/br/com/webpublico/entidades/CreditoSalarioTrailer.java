package br.com.webpublico.entidades;

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
public class CreditoSalarioTrailer implements Serializable {
    private String codigoBanco;
    private String loteServico;
    private String tipoRegistro;
    private String cnab9posicoes; //febraban
    private String quantidadeLotes;
    private String quantidadeRegistros;
    private String quantidadeContas;
    private String cnab205posicoes; //febraban
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

    public String getCnab205posicoes() {
        return cnab205posicoes;
    }

    public void setCnab205posicoes(String cnab205posicoes) {
        this.cnab205posicoes = cnab205posicoes;
    }

    public String getTrailerArquivo() {
        StringBuilder sb = new StringBuilder();

        sb.append(getCodigoBanco());
        sb.append(getLoteServico());
        sb.append(getTipoRegistro());
        sb.append(getCnab9posicoes());
        sb.append(getQuantidadeLotes());
        sb.append(getQuantidadeRegistros());
        sb.append(getQuantidadeContas());
        sb.append(getCnab205posicoes());

        if (!validaTrailer(sb.toString())) {
            //System.out.println("O TRAILER TEM "+sb.length()+" caracteres de "+NUMERO_CARACTERES_POR_LINHA);
        }

        return sb.toString();
    }

    public void montaTrailerArquivo(GrupoRecursoFP grupo, CreditoSalario creditoSalario, Integer quantidadeLotes, Integer totalRegistros, Integer quantidadeContas) {
        PessoaJuridica pj = grupo.getHierarquiaOrganizacional().getSubordinada().getEntidade().getPessoaJuridica();
        setCodigoBanco(StringUtil.cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco(), 3, "0"));
        setLoteServico("9999");
        setTipoRegistro("9");
        setCnab9posicoes(StringUtils.repeat(" ", 9));
        setQuantidadeLotes(StringUtil.cortaOuCompletaEsquerda(quantidadeLotes.toString(), 6, "0"));
        setQuantidadeRegistros(StringUtil.cortaOuCompletaEsquerda(totalRegistros.toString(), 6, "0"));
        setQuantidadeContas(StringUtil.cortaOuCompletaEsquerda(quantidadeContas.toString(), 6, "0"));
        setCnab205posicoes(StringUtils.repeat(" ", 205));
    }

    public boolean validaTrailer(String linha) {
        boolean valido = true;
        if (linha.length() != NUMERO_CARACTERES_POR_LINHA) {
            valido = false;
        }

        return valido;
    }

}
