package br.com.webpublico.entidades.rh.creditodesalario.caixa;

import br.com.webpublico.entidades.Banco;
import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.entidades.rh.creditodesalario.CreditoSalario;
import br.com.webpublico.entidadesauxiliares.CreditoSalarioAgrupadorLote;
import br.com.webpublico.util.StringUtil;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Usuario
 * Date: 18/10/13
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */
public class CreditoSalarioCaixaHeaderLote implements Serializable {

    private final Integer NUMERO_CARACTERES_POR_LINHA = 240;
    private String codigoBanco;
    private String loteServico;
    private String tipoRegistro;
    private String tipoOperacao;
    private String tipoServico;
    private String formaDeLancamento;
    private String numeroVersaoLayout;
    private String filler1;
    private String tipoInscricaoEmpresa;
    private String numeroInscricaoEmpresa;
    private String codigoConvenio;
    private String tipoDeCompromisso;
    private String codigoDeCompromisso;
    private String parametroDeTransmissao;
    private String filler6;
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
    private String complementoCEP;
    private String siglaEstado;
    private String usoExclusivoFebraban;
    private String indicativoFormaPagamento;
    private String codigoOcorrenciasRetorno;

    public String getUsoExclusivoFebraban() {
        return usoExclusivoFebraban;
    }

    public void setUsoExclusivoFebraban(String usoExclusivoFebraban) {
        this.usoExclusivoFebraban = usoExclusivoFebraban;
    }

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


    public String getIndicativoFormaPagamento() {
        return indicativoFormaPagamento;
    }

    public void setIndicativoFormaPagamento(String indicativoFormaPagamento) {
        this.indicativoFormaPagamento = indicativoFormaPagamento;
    }

    public String getCodigoOcorrenciasRetorno() {
        return codigoOcorrenciasRetorno;
    }

    public void setCodigoOcorrenciasRetorno(String codigoOcorrenciasRetorno) {
        this.codigoOcorrenciasRetorno = codigoOcorrenciasRetorno;
    }

    public String getHeaderLoteArquivo() {
        StringBuilder sb = new StringBuilder();

        sb.append(getCodigoBanco());
        sb.append(getLoteServico());
        sb.append(getTipoRegistro());
        sb.append(getTipoOperacao());
        sb.append(getTipoServico());
        sb.append(getFormaDeLancamento());
        sb.append(getNumeroVersaoLayout());
        sb.append(getFiller1());
        sb.append(getTipoInscricaoEmpresa());
        sb.append(getNumeroInscricaoEmpresa());
        sb.append(getCodigoConvenio());
        sb.append(getTipoDeCompromisso());
        sb.append(getCodigoDeCompromisso());
        sb.append(getParametroDeTransmissao());
        sb.append(getFiller6());
        sb.append(getAgenciaMantenedoraConta());
        sb.append(getDigitoVerificadorAgencia());
        sb.append(getNumeroContaCorrente());
        sb.append(getDigitoVerificadorContaCorrente());
        sb.append(getDigitoVerificadorAgenciaContaCorrente());
        sb.append(getNomeEmpresa());
        sb.append(getMensagem());
        sb.append(getLogradouro());
        sb.append(getNumeroLogradouro());
        sb.append(getComplementoLogradouro());
        sb.append(getCidade());
        sb.append(getCep());
        sb.append(getSiglaEstado());
        sb.append(getUsoExclusivoFebraban());
        sb.append(getCodigoOcorrenciasRetorno());

        sb.append("\r\n");

        String retorno = sb.toString();
        retorno = retorno.toUpperCase();
        retorno = StringUtil.removeCaracteresEspeciais(retorno);
        retorno = retorno.replaceAll("[^\\x00-\\x7F]", " ");

        return retorno;
    }

    public void montaHeaderLote(CreditoSalario creditoSalario, Integer sequenciaLote, CreditoSalarioAgrupadorLote agrupadorLote) {
        PessoaJuridica pj = creditoSalario.getHierarquiaOrganizacional().getSubordinada().getEntidade().getPessoaJuridica();

        String tipoServico = creditoSalario.isTipoArquivoServidor() ? "30" : "20";
        String formaLancamento = isMesmoBanco(creditoSalario, agrupadorLote.getBanco()) ? "01" : "41";
        String tipoDeCompromisso = creditoSalario.isTipoArquivoServidor() ? "02" : "01";

        EnderecoCorreio ec = creditoSalario.getHierarquiaOrganizacional().getSubordinada().getEntidade().getPessoaJuridica().getEnderecoPrincipal();
        ajustarEndereco(ec);


        setCodigoBanco(StringUtil.cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco(), 3, "0"));
        setLoteServico(StringUtil.cortaOuCompletaEsquerda(sequenciaLote.toString(), 4, "0"));
        setTipoRegistro("1");
        setTipoOperacao("C");
        setTipoServico(tipoServico);
        setFormaDeLancamento(StringUtil.cortaOuCompletaEsquerda(formaLancamento, 2, "0"));
        setNumeroVersaoLayout("041");
        setFiller1(" ");
        setTipoInscricaoEmpresa("2");
        setNumeroInscricaoEmpresa(StringUtil.cortaOuCompletaEsquerda(pj.getCnpj().replaceAll("/", "").replaceAll("\\.", "").replaceAll("-", ""), 14, "0"));
        setCodigoConvenio(StringUtil.cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getCodigoDoConvenio(), 6, "0"));
        setTipoDeCompromisso(tipoDeCompromisso);
        setCodigoDeCompromisso("0001");
        setParametroDeTransmissao("01");
        setFiller6("      ");
        setAgenciaMantenedoraConta(StringUtil.cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getAgencia().getNumeroAgencia(), 5, "0"));
        setDigitoVerificadorAgencia(StringUtil.cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getAgencia().getDigitoVerificador(), 1, "0"));
        setNumeroContaCorrente(StringUtil.cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getNumeroConta().trim(), 12, "0"));
        setDigitoVerificadorContaCorrente(StringUtil.cortaOuCompletaDireita(creditoSalario.getContaBancariaEntidade().getDigitoVerificador(), 1, " "));
        setDigitoVerificadorAgenciaContaCorrente(" ");
        setNomeEmpresa(StringUtil.cortaOuCompletaDireita(StringUtil.removeCaracteresEspeciais(creditoSalario.getContaBancariaEntidade().getEntidade().getNome()), 30, " "));
        getNomeDaEmpresa(creditoSalario, agrupadorLote);
        setLogradouro(StringUtil.cortaOuCompletaDireita(ec.getLogradouro(), 30, " "));
        setNumeroLogradouro(StringUtil.cortaOuCompletaEsquerda(ec.getNumero(), 5, "0"));
        setComplementoLogradouro(StringUtil.cortaOuCompletaDireita(ec.getComplemento(), 15, " "));
        setCidade(StringUtil.cortaOuCompletaDireita(ec.getLocalidade(), 20, " "));
        setCep(StringUtil.cortaOuCompletaEsquerda(ec.getCep().replaceAll("-", ""), 8, "0"));
        setSiglaEstado(StringUtil.cortaOuCompletaDireita(ec.getUf(), 2, " "));
        setUsoExclusivoFebraban("        ");
        setCodigoOcorrenciasRetorno(StringUtil.cortaOuCompletaDireita(" ", 10, " "));
    }

    private void getNomeDaEmpresa(CreditoSalario creditoSalario, CreditoSalarioAgrupadorLote agrupadorLote) {
        if (!creditoSalario.isTipoArquivoServidor()) {
            if (isMesmoBanco(creditoSalario, agrupadorLote.getBanco())) {
                switch (agrupadorLote.getModalidadeConta()) {
                    case CONTA_CAIXA_FACIL:
                        setMensagem(StringUtil.cortaOuCompletaDireita("CREDITO EM CONTA CORRENTE", 40, " "));
                        break;
                    case CONTA_CORRENTE:
                        setMensagem(StringUtil.cortaOuCompletaDireita("CREDITO EM CONTA CORRENTE", 40, " "));
                        break;
                    case CONTA_POUPANCA:
                        setMensagem(StringUtil.cortaOuCompletaDireita("DEPOSITO EM POUPANCA", 40, " "));
                        break;
                    case CONTA_SALARIO:
                        setMensagem(StringUtil.cortaOuCompletaDireita("CREDITO EM CONTA SALARIO", 40, " "));
                        break;
                    case CONTA_SALARIO_NSGD:
                        setMensagem(StringUtil.cortaOuCompletaDireita("CREDITO EM CONTA SALARIO", 40, " "));
                        break;
                    default:
                        setMensagem(StringUtil.cortaOuCompletaDireita("CREDITO EM CONTA CORRENTE", 40, " "));
                }
            } else {
                setMensagem(StringUtil.cortaOuCompletaDireita("CREDITO EM OUTROS BANCOS - DOC/TED", 40, " "));
            }
        } else {
            setMensagem(StringUtil.cortaOuCompletaDireita(" ", 40, " "));
        }
    }

    private void ajustarEndereco(EnderecoCorreio endereco) {
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

    private boolean isMesmoBanco(CreditoSalario creditoSalario, Banco banco) {
        if (creditoSalario.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco().equals(banco.getNumeroBanco())) {
            return true;
        }
        return false;
    }

    public String getFiller1() {
        return filler1;
    }

    public void setFiller1(String filler1) {
        this.filler1 = filler1;
    }

    public String getTipoDeCompromisso() {
        return tipoDeCompromisso;
    }

    public void setTipoDeCompromisso(String tipoDeCompromisso) {
        this.tipoDeCompromisso = tipoDeCompromisso;
    }

    public String getCodigoDeCompromisso() {
        return codigoDeCompromisso;
    }

    public void setCodigoDeCompromisso(String codigoDeCompromisso) {
        this.codigoDeCompromisso = codigoDeCompromisso;
    }

    public String getParametroDeTransmissao() {
        return parametroDeTransmissao;
    }

    public void setParametroDeTransmissao(String parametroDeTransmissao) {
        this.parametroDeTransmissao = parametroDeTransmissao;
    }

    public String getFiller6() {
        return filler6;
    }

    public void setFiller6(String filler6) {
        this.filler6 = filler6;
    }

    public String getComplementoCEP() {
        return complementoCEP;
    }

    public void setComplementoCEP(String complementoCEP) {
        this.complementoCEP = complementoCEP;
    }

    public String getSiglaEstado() {
        return siglaEstado;
    }

    public void setSiglaEstado(String siglaEstado) {
        this.siglaEstado = siglaEstado;
    }
}
