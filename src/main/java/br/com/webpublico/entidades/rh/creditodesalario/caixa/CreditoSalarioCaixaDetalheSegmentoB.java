package br.com.webpublico.entidades.rh.creditodesalario.caixa;

import br.com.webpublico.entidades.ContaCorrenteBancaria;
import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.creditodesalario.CreditoSalario;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Usuario
 * Date: 24/10/13
 * Time: 09:21
 * To change this template use File | Settings | File Templates.
 */
public class CreditoSalarioCaixaDetalheSegmentoB implements Serializable {

    private final Integer NUMERO_CARACTERES_POR_LINHA = 240;
    private String codigoBanco;
    private String loteServico;
    private String codigoRegistro;
    private String sequenciaRegistroNoLote;
    private String codigoSegmento;
    private String filler3;
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
    private String filler15;

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

    public String getFiller3() {
        return filler3;
    }

    public void setFiller3(String filler3) {
        this.filler3 = filler3;
    }

    public String getFiller15() {
        return filler15;
    }

    public void setFiller15(String filler15) {
        this.filler15 = filler15;
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

    public String getDetalheSegmentoB() {
        StringBuilder sb = new StringBuilder();
        sb.append(getCodigoBanco());
        sb.append(getLoteServico());
        sb.append(getCodigoRegistro());
        sb.append(getSequenciaRegistroNoLote());
        sb.append(getCodigoSegmento());
        sb.append(getFiller3());
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
        sb.append(getFiller15());

        sb.append("\r\n");
        String retorno = sb.toString();
        retorno = retorno.toUpperCase();
        retorno = StringUtil.removeCaracteresEspeciais(retorno);
        retorno = retorno.replaceAll("[^\\x00-\\x7F]", " ");

        return retorno;
    }

    public void montaDetalheSegmentoB(CreditoSalario selecionado, VinculoFP vinculoFP, EnderecoCorreio enderecoCorreio, Integer sequenciaLote, Integer registroNoLote) {

        PessoaFisica p = vinculoFP.getMatriculaFP().getPessoa();

        ContaCorrenteBancaria cc = null;

        try {
            cc = vinculoFP.getContaCorrente() != null ? vinculoFP.getContaCorrente() : vinculoFP.getMatriculaFP().getPessoa().getContaCorrentePrincipal().getContaCorrenteBancaria();
        } catch (NullPointerException npe) {
            return;
        }

        if (enderecoCorreio == null) {
            enderecoCorreio = new EnderecoCorreio();
        }
        validaEndereco(selecionado, vinculoFP, enderecoCorreio);

        setCodigoBanco(StringUtil.cortaOuCompletaEsquerda(selecionado.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco(), 3, "0"));
        setLoteServico(StringUtil.cortaOuCompletaEsquerda(sequenciaLote.toString(), 4, "0"));
        setCodigoRegistro("3");
        setSequenciaRegistroNoLote(StringUtil.cortaOuCompletaEsquerda(registroNoLote.toString(), 5, "0"));
        setCodigoSegmento("B");
        setFiller3("   ");
        setTipoInscricaoFavorecido("1");
        setNumeroInscricaoFavorecido(StringUtil.cortaOuCompletaEsquerda(vinculoFP.getMatriculaFP().getPessoa().getCpf().replaceAll("\\.", "").replaceAll("-", ""), 14, "0"));
        setLogradouroFavorecido(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getLogradouro(), 30, " "));
        setNumeroLogradouroraFavorecido(StringUtil.cortaOuCompletaEsquerda(enderecoCorreio.getNumero(), 5, "0"));
        setComplementoFavorecido(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getComplemento(), 15, " "));
        setBairroFavorecido(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getBairro(), 15, " "));
        setCidadeFavorecido(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getLocalidade(), 20, " "));
        setCepFavorecido(StringUtil.cortaOuCompletaEsquerda(enderecoCorreio.getCep().replaceAll("-", ""), 8, "0"));
        setEstadoFavorecido(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getUf(), 2, " "));
        setDataVencimento(StringUtil.cortaOuCompletaEsquerda(Util.dateToString(selecionado.getDataCredito()).replaceAll("/", ""), 8, "0"));
        setValorDocumento(StringUtil.cortaOuCompletaEsquerda("", 15, "0"));
        setValorAbatimento(StringUtil.cortaOuCompletaEsquerda("", 15, "0"));
        setValorDesconto(StringUtil.cortaOuCompletaEsquerda("", 15, "0"));
        setValorMora(StringUtil.cortaOuCompletaEsquerda("", 15, "0"));
        setValorMulta(StringUtil.cortaOuCompletaEsquerda("", 15, "0"));
        setCodigoDocumentoFavorecido("               ");
        setFiller15("               ");
    }

    private void validaEndereco(CreditoSalario selecionado, VinculoFP vinculoFP, EnderecoCorreio endereco) {
        PessoaFisica p = vinculoFP.getMatriculaFP().getPessoa();
        boolean adicionouAlgoNoLog = false;
        if (endereco.getBairro() == null) {
            endereco.setBairro("");
//            selecionado.addConsoleLog(selecionado.getLogAzul(), "Falta informar o Bairro no endereço da pessoa: " + p.getNome() + ", pode ser que o arquivo não passe no validador do banco.");
            adicionouAlgoNoLog = true;
        }
        if (endereco.getCep() == null) {
            endereco.setCep("-");
//            selecionado.addConsoleLog(selecionado.getLogAzul(), "Falta informar o CEP no endereço da pessoa: " + p.getNome() + ", pode ser que o arquivo não passe no validador do banco.");
            adicionouAlgoNoLog = true;
        }
        if (endereco.getComplemento() == null) {
            endereco.setComplemento("");
//            selecionado.addConsoleLog(selecionado.getLogAzul(), "Falta informar o Complemento no endereço da pessoa: " + p.getNome() + ", pode ser que o arquivo não passe no validador do banco.");
            adicionouAlgoNoLog = true;
        }
        if (endereco.getLocalidade() == null) {
            endereco.setLocalidade("");
//            selecionado.addConsoleLog(selecionado.getLogAzul(), "Falta informar a Localidade no endereço da pessoa: " + p.getNome() + ", pode ser que o arquivo não passe no validador do banco.");
            adicionouAlgoNoLog = true;
        }
        if (endereco.getNumero() == null) {
            endereco.setNumero("");
//            selecionado.addConsoleLog(selecionado.getLogAzul(), "Falta informar o número no endereço da pessoa: " + p.getNome() + ", pode ser que o arquivo não passe no validador do banco.");
            adicionouAlgoNoLog = true;
        }
        if (endereco.getUf() == null) {
            endereco.setUf("");
//            selecionado.addConsoleLog(selecionado.getLogAzul(), "Falta informar o Estado(UF) no endereço da pessoa: " + p.getNome() + ", pode ser que o arquivo não passe no validador do banco.");
            adicionouAlgoNoLog = true;
        }
        if (endereco.getLogradouro() == null) {
            endereco.setLogradouro("");
//            selecionado.addConsoleLog(selecionado.getLogAzul(), "Falta informar o Logradouro no endereço da pessoa: " + p.getNome() + ", pode ser que o arquivo não passe no validador do banco.");
            adicionouAlgoNoLog = true;
        }

        if (adicionouAlgoNoLog) {
//            selecionado.addConsoleLog(selecionado.getLogAzul(), "*****************************************************");
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
