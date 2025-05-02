package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.creditodesalario.CreditoSalario;
import br.com.webpublico.util.StringUtil;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Usuario
 * Date: 18/10/13
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */
public class CreditoSalarioHeaderLote implements Serializable {

    private String codigoBanco;
    private String loteServico;
    private String tipoRegistro;
    private String tipoOperacao;
    private String tipoServico;
    private String formaDeLancamento;
    private String numeroVersaoLayout;
    private String cnab1posicao; //febraban
    private String tipoInscricaoEmpresa;
    private String numeroInscricaoEmpresa;
    private String codigoConvenio;
    private String agenciaMantenedoraConta;
    private String digitoVerificadorAgencia;
    private String numeroContaCorrente;
    private String digitoVerificadorContaCorrente;
    private String digitoVerificadorAgenciaContaCorrente;
    private String nomeEmpresa;
    private String mensagem;
    private String logradouro;
    private String numeroLogradouro;
    private String complementoLogradouro;
    private String cidade;
    private String cep;
    private String estado;
    private String cnab6posicoes; //febraban
    private String indicativoFormaPagamento;
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

    public String getTipoInscricaoEmpresa() {
        return tipoInscricaoEmpresa;
    }

    public void setTipoInscricaoEmpresa(String tipoInscricaoEmpresa) {
        this.tipoInscricaoEmpresa = tipoInscricaoEmpresa;
    }

    public String getNumeroInscricaoEmpresa() {
        return numeroInscricaoEmpresa;
    }

    public void setNumeroInscricaoEmpresa(String numeroInscricaoEmpresa) {
        this.numeroInscricaoEmpresa = numeroInscricaoEmpresa;
    }

    public String getCodigoConvenio() {
        return codigoConvenio;
    }

    public void setCodigoConvenio(String codigoConvenio) {
        this.codigoConvenio = codigoConvenio;
    }

    public String getAgenciaMantenedoraConta() {
        return agenciaMantenedoraConta;
    }

    public void setAgenciaMantenedoraConta(String agenciaMantenedoraConta) {
        this.agenciaMantenedoraConta = agenciaMantenedoraConta;
    }

    public String getDigitoVerificadorAgencia() {
        return digitoVerificadorAgencia;
    }

    public void setDigitoVerificadorAgencia(String digitoVerificadorAgencia) {
        this.digitoVerificadorAgencia = digitoVerificadorAgencia;
    }

    public String getNumeroContaCorrente() {
        return numeroContaCorrente;
    }

    public void setNumeroContaCorrente(String numeroContaCorrente) {
        this.numeroContaCorrente = numeroContaCorrente;
    }

    public String getDigitoVerificadorContaCorrente() {
        return digitoVerificadorContaCorrente;
    }

    public void setDigitoVerificadorContaCorrente(String digitoVerificadorContaCorrente) {
        this.digitoVerificadorContaCorrente = digitoVerificadorContaCorrente;
    }

    public String getDigitoVerificadorAgenciaContaCorrente() {
        return digitoVerificadorAgenciaContaCorrente;
    }

    public void setDigitoVerificadorAgenciaContaCorrente(String digitoVerificadorAgenciaContaCorrente) {
        this.digitoVerificadorAgenciaContaCorrente = digitoVerificadorAgenciaContaCorrente;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getNumeroVersaoLayout() {
        return numeroVersaoLayout;
    }

    public void setNumeroVersaoLayout(String numeroVersaoLayout) {
        this.numeroVersaoLayout = numeroVersaoLayout;
    }

    public String getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(String tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public String getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(String tipoServico) {
        this.tipoServico = tipoServico;
    }

    public String getFormaDeLancamento() {
        return formaDeLancamento;
    }

    public void setFormaDeLancamento(String formaDeLancamento) {
        this.formaDeLancamento = formaDeLancamento;
    }

    public String getCnab1posicao() {
        return cnab1posicao;
    }

    public void setCnab1posicao(String cnab1posicao) {
        this.cnab1posicao = cnab1posicao;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumeroLogradouro() {
        return numeroLogradouro;
    }

    public void setNumeroLogradouro(String numeroLogradouro) {
        this.numeroLogradouro = numeroLogradouro;
    }

    public String getComplementoLogradouro() {
        return complementoLogradouro;
    }

    public void setComplementoLogradouro(String complementoLogradouro) {
        this.complementoLogradouro = complementoLogradouro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getIndicativoFormaPagamento() {
        return indicativoFormaPagamento;
    }

    public void setIndicativoFormaPagamento(String indicativoFormaPagamento) {
        this.indicativoFormaPagamento = indicativoFormaPagamento;
    }

    public String getCnab6posicoes() {
        return cnab6posicoes;
    }

    public void setCnab6posicoes(String cnab6posicoes) {
        this.cnab6posicoes = cnab6posicoes;
    }

    public String getCodigoOcorrenciasRetorno() {
        return codigoOcorrenciasRetorno;
    }

    public void setCodigoOcorrenciasRetorno(String codigoOcorrenciasRetorno) {
        this.codigoOcorrenciasRetorno = codigoOcorrenciasRetorno;
    }

    public String getHeaderLoteArquivo() {
        StringBuilder sb = new StringBuilder();

        sb.append(StringUtil.cortaOuCompletaEsquerda(getCodigoBanco(), 3, "0"));
        sb.append(StringUtil.cortaOuCompletaEsquerda(getLoteServico(), 4, "0"));
        sb.append(getTipoRegistro());
        sb.append(getTipoOperacao());
        sb.append(StringUtil.cortaOuCompletaEsquerda(getTipoServico(), 2, "0"));
        sb.append(StringUtil.cortaOuCompletaEsquerda(getFormaDeLancamento(), 2, "0"));
        sb.append(getNumeroVersaoLayout());
        sb.append(getCnab1posicao());
        sb.append(getTipoInscricaoEmpresa());
        sb.append(StringUtil.cortaOuCompletaEsquerda(getNumeroInscricaoEmpresa().replaceAll("/", "").replaceAll("\\.", "").replaceAll("-", ""), 14, "0"));
        sb.append(StringUtil.cortaOuCompletaDireita(getCodigoConvenio(), 20, " "));
        sb.append(StringUtil.cortaOuCompletaEsquerda(getAgenciaMantenedoraConta(), 5, "0"));
        sb.append(getDigitoVerificadorAgencia());
        sb.append(StringUtil.cortaOuCompletaDireita(getNumeroContaCorrente(), 12, "0"));
        sb.append(getDigitoVerificadorContaCorrente());
        sb.append(getDigitoVerificadorAgenciaContaCorrente());
        sb.append(StringUtil.cortaOuCompletaDireita(getNomeEmpresa(), 30, " "));
        sb.append(StringUtil.cortaOuCompletaDireita(getMensagem(), 40, " "));
        sb.append(StringUtil.cortaOuCompletaDireita(getLogradouro(), 30, " "));
        sb.append(StringUtil.cortaOuCompletaEsquerda(getNumeroLogradouro(), 5, "0"));
        sb.append(StringUtil.cortaOuCompletaDireita(getComplementoLogradouro(), 15, " "));
        sb.append(StringUtil.cortaOuCompletaDireita(getCidade(), 20, " "));
        sb.append(StringUtil.cortaOuCompletaEsquerda(getCep().replaceAll("-", ""), 8, "0"));
        sb.append(getEstado());
        sb.append(getIndicativoFormaPagamento());
        sb.append(getCnab6posicoes());
        sb.append(StringUtil.cortaOuCompletaEsquerda(getCodigoOcorrenciasRetorno(), 10, " "));

        if (!validaHeaderLote(sb.toString())) {
            //System.out.println("o HEADER DO LOTE TEM "+sb.length()+" caracteres de "+NUMERO_CARACTERES_POR_LINHA);
        }

        sb.append("\r\n");

        return sb.toString();
    }

    public void montaHeaderLote(CreditoSalario creditoSalario, HierarquiaOrganizacional ho, EnderecoCorreio enderecoCorreio, GrupoRecursoFP grupo, Banco banco, Integer sequenciaLote) {
        PessoaJuridica pj = ho.getSubordinada().getEntidade().getPessoaJuridica();

        //mesmo banco = 01 (credito em conta corrente) -- outros bancos = 03 (DOC/TED)
        String formaLancamento = isMesmoBanco(creditoSalario, banco) ? "01" : "03";

        validaEndereco(enderecoCorreio);

        setCodigoBanco(StringUtil.cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco(), 3, "0"));
        setLoteServico(StringUtil.cortaOuCompletaEsquerda(sequenciaLote.toString(), 4, "0"));
        setTipoRegistro("1");
        setTipoOperacao("C");
        setTipoServico("30"); //30 ou 98 verificar de onde vem estas informações
        setFormaDeLancamento(StringUtil.cortaOuCompletaEsquerda(formaLancamento, 2, "0")); //mesmo banco = 01 (credito em conta corrente) -- outros bancos = 03 (DOC/TED)
        setNumeroVersaoLayout("045");
        setCnab1posicao(" ");
        setTipoInscricaoEmpresa("2");
        setNumeroInscricaoEmpresa(StringUtil.cortaOuCompletaEsquerda(pj.getCnpj().replaceAll("/", "").replaceAll("\\.", "").replaceAll("-", ""), 14, "0"));

        String codigoConvenio = StringUtil.cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getCodigoDoConvenio(), 9, "0");
        String codigoPagamentoConvenio = "0126";
        setCodigoConvenio(StringUtil.cortaOuCompletaDireita(codigoConvenio + codigoPagamentoConvenio, 20, " "));

        setAgenciaMantenedoraConta(StringUtil.cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getAgencia().getNumeroAgencia(), 5, "0"));
        setDigitoVerificadorAgencia(creditoSalario.getContaBancariaEntidade().getAgencia().getDigitoVerificador() != null ? creditoSalario.getContaBancariaEntidade().getAgencia().getDigitoVerificador().toUpperCase() : " ");
        setNumeroContaCorrente(StringUtil.cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getNumeroConta(), 12, "0"));
        setDigitoVerificadorContaCorrente(creditoSalario.getContaBancariaEntidade().getDigitoVerificador() != null ? creditoSalario.getContaBancariaEntidade().getDigitoVerificador().toUpperCase() : " ");
        setDigitoVerificadorAgenciaContaCorrente(" ");
        setNomeEmpresa(StringUtil.cortaOuCompletaDireita(StringUtil.removeCaracteresEspeciais(grupo.getNomeEmpresa().toUpperCase()), 30, " "));

        if (getFormaDeLancamento().equals("01")) {
            setMensagem(StringUtil.cortaOuCompletaDireita("CREDITO EM CONTA CORRENTE", 40, " "));
        } else if (getFormaDeLancamento().equals("03")) {
            setMensagem(StringUtil.cortaOuCompletaDireita("CREDITO EM OUTROS BANCOS - DOC/TED", 40, " "));
        } else {
            setMensagem(StringUtil.cortaOuCompletaDireita(" ", 40, " "));
        }

        setLogradouro(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getLogradouro(), 30, " "));
        setNumeroLogradouro(StringUtil.cortaOuCompletaEsquerda(enderecoCorreio.getNumero(), 5, "0"));
        setComplementoLogradouro(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getComplemento(), 15, " "));
        setCidade(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getLocalidade(), 20, " "));
        setCep(enderecoCorreio.getCep().replaceAll("-", ""));
        setEstado(enderecoCorreio.getUf());
        setIndicativoFormaPagamento("01");
        setCnab6posicoes(StringUtil.cortaOuCompletaDireita(" ", 6, " "));
        setCodigoOcorrenciasRetorno(StringUtil.cortaOuCompletaDireita(" ", 10, " "));
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

    public boolean validaHeaderLote(String linha) {
        boolean valido = true;
        if (linha.length() != NUMERO_CARACTERES_POR_LINHA) {
            valido = false;
        }

        return valido;
    }

    private boolean isMesmoBanco(CreditoSalario creditoSalario, Banco banco) {
        if (creditoSalario.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco().equals(banco.getNumeroBanco())) {
            return true;
        }
        return false;
    }
}
