package br.com.webpublico.entidades;

import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Usuario
 * Date: 24/10/13
 * Time: 09:21
 * To change this template use File | Settings | File Templates.
 */
public class CreditoSalarioDetalheSegmentoB implements Serializable {

    private String codigoBanco;
    private String loteServico;
    private String tipoRegistro;
    private String sequenciaRegistroNoLote;
    private String codigoSegmento;
    private String cnab3posicoes;
    private String tipoInscricaoFavorecido;
    private String numeroInscricaoFavorecido;
    private String logradouroFavorecido;
    private String numeroLogradouroraFavorecido;
    private String complementoFavorecido;
    private String bairroFavorecido;
    private String cidadeFavorecido;
    private String cepFavorecido;
    private String estadoFavorecido;
    private String dataVencimento;
    private String valorDocumento;
    private String valorAbatimento;
    private String valorDesconto;
    private String valorMora;
    private String valorMulta;
    private String codigoDocumentoFavorecido;
    private String avisoAoFavorecido;
    private String codigoUGCentralizadora; //uso exclusivo para o SIAPE
    private String codigoISPB; // 00000000 <- código ISPB do Banco do Brasil
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

    public String getSequenciaRegistroNoLote() {
        return sequenciaRegistroNoLote;
    }

    public void setSequenciaRegistroNoLote(String sequenciaRegistroNoLote) {
        this.sequenciaRegistroNoLote = sequenciaRegistroNoLote;
    }

    public String getCodigoSegmento() {
        return codigoSegmento;
    }

    public void setCodigoSegmento(String codigoSegmento) {
        this.codigoSegmento = codigoSegmento;
    }

    public String getCnab3posicoes() {
        return cnab3posicoes;
    }

    public void setCnab3posicoes(String cnab3posicoes) {
        this.cnab3posicoes = cnab3posicoes;
    }

    public String getTipoInscricaoFavorecido() {
        return tipoInscricaoFavorecido;
    }

    public void setTipoInscricaoFavorecido(String tipoInscricaoFavorecido) {
        this.tipoInscricaoFavorecido = tipoInscricaoFavorecido;
    }

    public String getNumeroInscricaoFavorecido() {
        return numeroInscricaoFavorecido;
    }

    public void setNumeroInscricaoFavorecido(String numeroInscricaoFavorecido) {
        this.numeroInscricaoFavorecido = numeroInscricaoFavorecido;
    }

    public String getLogradouroFavorecido() {
        return logradouroFavorecido;
    }

    public void setLogradouroFavorecido(String logradouroFavorecido) {
        this.logradouroFavorecido = logradouroFavorecido;
    }

    public String getNumeroLogradouroraFavorecido() {
        return numeroLogradouroraFavorecido;
    }

    public void setNumeroLogradouroraFavorecido(String numeroLogradouroraFavorecido) {
        this.numeroLogradouroraFavorecido = numeroLogradouroraFavorecido;
    }

    public String getComplementoFavorecido() {
        return complementoFavorecido;
    }

    public void setComplementoFavorecido(String complementoFavorecido) {
        this.complementoFavorecido = complementoFavorecido;
    }

    public String getBairroFavorecido() {
        return bairroFavorecido;
    }

    public void setBairroFavorecido(String bairroFavorecido) {
        this.bairroFavorecido = bairroFavorecido;
    }

    public String getCidadeFavorecido() {
        return cidadeFavorecido;
    }

    public void setCidadeFavorecido(String cidadeFavorecido) {
        this.cidadeFavorecido = cidadeFavorecido;
    }

    public String getCepFavorecido() {
        return cepFavorecido;
    }

    public void setCepFavorecido(String cepFavorecido) {
        this.cepFavorecido = cepFavorecido;
    }

    public String getEstadoFavorecido() {
        return estadoFavorecido;
    }

    public void setEstadoFavorecido(String estadoFavorecido) {
        this.estadoFavorecido = estadoFavorecido;
    }

    public String getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(String dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getValorDocumento() {
        return valorDocumento;
    }

    public void setValorDocumento(String valorDocumento) {
        this.valorDocumento = valorDocumento;
    }

    public String getValorAbatimento() {
        return valorAbatimento;
    }

    public void setValorAbatimento(String valorAbatimento) {
        this.valorAbatimento = valorAbatimento;
    }

    public String getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(String valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public String getValorMora() {
        return valorMora;
    }

    public void setValorMora(String valorMora) {
        this.valorMora = valorMora;
    }

    public String getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(String valorMulta) {
        this.valorMulta = valorMulta;
    }

    public String getCodigoDocumentoFavorecido() {
        return codigoDocumentoFavorecido;
    }

    public void setCodigoDocumentoFavorecido(String codigoDocumentoFavorecido) {
        this.codigoDocumentoFavorecido = codigoDocumentoFavorecido;
    }

    public String getAvisoAoFavorecido() {
        return avisoAoFavorecido;
    }

    public void setAvisoAoFavorecido(String avisoAoFavorecido) {
        this.avisoAoFavorecido = avisoAoFavorecido;
    }

    public String getCodigoUGCentralizadora() {
        return codigoUGCentralizadora;
    }

    public void setCodigoUGCentralizadora(String codigoUGCentralizadora) {
        this.codigoUGCentralizadora = codigoUGCentralizadora;
    }

    public String getCodigoISPB() {
        return codigoISPB;
    }

    public void setCodigoISPB(String codigoISPB) {
        this.codigoISPB = codigoISPB;
    }

    public String getDetalheSegmentoB() {
        StringBuilder sb = new StringBuilder();
        sb.append(getCodigoBanco());
        sb.append(getLoteServico());
        sb.append(getTipoRegistro());
        sb.append(getSequenciaRegistroNoLote());
        sb.append(getCodigoSegmento());
        sb.append(getCnab3posicoes());
        sb.append(getTipoInscricaoFavorecido());
        sb.append(getNumeroInscricaoFavorecido());
        sb.append(getLogradouroFavorecido());
        sb.append(getNumeroLogradouroraFavorecido());
        sb.append(getComplementoFavorecido());
        sb.append(getBairroFavorecido());
        sb.append(getCidadeFavorecido());
        sb.append(getCepFavorecido());
        sb.append(getEstadoFavorecido());
        sb.append(getDataVencimento());
        sb.append(getValorDocumento());
        sb.append(getValorAbatimento());
        sb.append(getValorDesconto());
        sb.append(getValorMora());
        sb.append(getValorMulta());
        sb.append(getCodigoDocumentoFavorecido());
        sb.append(getAvisoAoFavorecido());
        sb.append(getCodigoUGCentralizadora());
        sb.append(getCodigoISPB());

        if(!validaDetalheSegmentoB(sb.toString())) {
            //System.out.println("o DETALHE DO SEGMENTO 'B' TEM "+sb.length()+" caracteres de "+NUMERO_CARACTERES_POR_LINHA);
        }

        sb.append("\r\n");

        return sb.toString();
    }

    public void montaDetalheSegmentoB(VinculoFP vinculoFP, EnderecoCorreio enderecoCorreio, Integer sequenciaLote, Integer registroNoLote) {

        PessoaFisica p = vinculoFP.getMatriculaFP().getPessoa();

        if (enderecoCorreio == null) {
            enderecoCorreio = new EnderecoCorreio();
        }
        validaEndereco(enderecoCorreio);

        setCodigoBanco(StringUtil.cortaOuCompletaEsquerda(vinculoFP.getContaCorrente().getAgencia().getBanco().getNumeroBanco(), 3, "0"));
        setLoteServico(StringUtil.cortaOuCompletaEsquerda(sequenciaLote.toString(), 4, "0"));
        setTipoRegistro("3");
        setSequenciaRegistroNoLote(StringUtil.cortaOuCompletaEsquerda(registroNoLote.toString(), 5, "0"));
        setCodigoSegmento("B");

        //CNAB - FEBRABAN/CNAB
        setCnab3posicoes(StringUtil.cortaOuCompletaEsquerda(" ", 3, " "));
        //Favorecido - Inscrição - Tipo de Inscrição do favorecido
        setTipoInscricaoFavorecido("1");
        //Favorecido - Inscrição - Número de Inscrição do favorecido
        setNumeroInscricaoFavorecido(StringUtil.cortaOuCompletaEsquerda(vinculoFP.getMatriculaFP().getPessoa().getCpf().replaceAll("\\.", "").replaceAll("-", ""), 14, "0"));
        //Favorecido - Logradouro
        setLogradouroFavorecido(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getLogradouro(), 30, " "));
        //Favorecido - Numero
        setNumeroLogradouroraFavorecido(StringUtil.cortaOuCompletaEsquerda(enderecoCorreio.getNumero(), 5, "0"));
        //Favorecido - Complemento
        setComplementoFavorecido(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getComplemento(), 15, " "));
        //Favorecido - Bairro
        setBairroFavorecido(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getBairro(), 15, " "));
        //Favorecido - Cidade
        setCidadeFavorecido(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getLocalidade(), 20, " "));
        //Favorecido - CEP
        setCepFavorecido(StringUtil.cortaOuCompletaEsquerda(enderecoCorreio.getCep().replaceAll("-", ""), 8, "0"));
        //Favorecido - Estado
        setEstadoFavorecido(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getUf(), 2, " "));
        //Pagamento - Vencimento
        setDataVencimento(StringUtils.repeat("0", 8));
        //Pagamento - Valor Docum.
        setValorDocumento(StringUtils.repeat("0", 15));
        //Pagamento - Valor Abatimento.
        setValorAbatimento(StringUtils.repeat("0", 15));
        //Pagamento - Valor Desconto
        setValorDesconto(StringUtils.repeat("0", 15));
        //Pagamento - Valor da Mora
        setValorMora(StringUtils.repeat("0", 15));
        //Pagamento - Valor da Multa
        setValorMulta(StringUtils.repeat("0", 15));
        //Pagamento - Cód/Doc. Favorec.
        setCodigoDocumentoFavorecido(StringUtils.repeat(" ", 15));
        //Aviso
        setAvisoAoFavorecido("0");
        //Código UG Centralizadora
        setCodigoUGCentralizadora(StringUtils.repeat("0", 6));
        //ISPB 00000000 <- código ISPB do Banco do Brasil
        setCodigoISPB(StringUtils.repeat("0", 8));

    }

    private void validaEndereco(EnderecoCorreio endereco) {
        if (endereco.getBairro() == null) {
            endereco.setBairro("");
        }
        if (endereco.getCep() == null) {
            endereco.setCep("-");
        }
        if (endereco.getComplemento() == null) {
            endereco.setComplemento("");
        }
        if (endereco.getLocalidade() == null) {
            endereco.setLocalidade("");
        }
        if (endereco.getNumero() == null) {
            endereco.setNumero("");
        }
        if (endereco.getUf() == null) {
            endereco.setUf("");
        }
        if (endereco.getLogradouro() == null) {
            endereco.setLogradouro("");
        }
    }

    public boolean validaDetalheSegmentoB(String linha) {
        boolean valido = true;
        if (linha.length() != NUMERO_CARACTERES_POR_LINHA) {
            valido = false;
        }

        return valido;
    }
}
