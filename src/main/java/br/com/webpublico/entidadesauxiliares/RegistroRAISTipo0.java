/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.ValidarRegistro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Claudio
 */
public class RegistroRAISTipo0 implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(RegistroRAISTipo0.class);
    private String sequenciaRegistro;

    @Etiqueta("Inscrição CNPJ/CEI do 1º estabelecimento do arquivo")
    @ValidarRegistro(tamanhoDoCampo = 14, campoNumerico = true)
    private String inscricaoCNPJCEI;

    @Etiqueta("Prefixo do 1º estabelecimento do arquivo")
    @ValidarRegistro(tamanhoDoCampo = 2)
    private String prefixo;

    @Etiqueta("Tipo do registro")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true, valoresPossiveis = {"0"})
    private String tipoRegistro;

    @Etiqueta("Constante")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true, valoresPossiveis = {"1"})
    private String constante;

    @Etiqueta("CNPJ/CEI/CPF do responsável")
    @ValidarRegistro(tamanhoDoCampo = 14, campoNumerico = true)
    private String cnpjCEIResponsavel;

    @Etiqueta("Tipo de Inscrição do responsável")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true, valoresPossiveis = {"1", "3", "4"})
    private String tipoInscricaoResponsavel;

    @Etiqueta("Razão Social/ Nome do responsável")
    @ValidarRegistro(tamanhoDoCampo = 40)
    private String razaoSocialNomeResponsavel;

    @Etiqueta("Logradouro do responsável")
    @ValidarRegistro(tamanhoDoCampo = 40)
    private String logradouroResponsavel;

    @Etiqueta("Número")
    @ValidarRegistro(tamanhoDoCampo = 6, campoNumerico = true)
    private String numeroLogradouro;

    @Etiqueta("Complemento")
    @ValidarRegistro(tamanhoDoCampo = 21)
    private String complemento;

    @Etiqueta("Bairro")
    @ValidarRegistro(tamanhoDoCampo = 19)
    private String bairro;

    @Etiqueta("CEP")
    @ValidarRegistro(tamanhoDoCampo = 8, campoNumerico = true)
    private String cep;

    @Etiqueta("Código do Município")
    @ValidarRegistro(tamanhoDoCampo = 7, campoNumerico = true)
    private String codigoMunicipio;

    @Etiqueta("Nome do Município")
    @ValidarRegistro(tamanhoDoCampo = 30)
    private String nomeMunicipio;

    @Etiqueta("Sigla da UF")
    @ValidarRegistro(tamanhoDoCampo = 2)
    private String uf;

    @Etiqueta("Telefone para contato (Código DDD)")
    @ValidarRegistro(tamanhoDoCampo = 2, campoNumerico = true)
    private String ddd;

    @Etiqueta("Telefone para contato (Número)")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String telefone;

    @Etiqueta("Indicador de retificação da declaração")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true)
    private String indicadorRetificacaoDeclaracao;

    @Etiqueta("Data da retificação dos estabelecimentos")
    @ValidarRegistro(tamanhoDoCampo = 8, campoNumerico = true)
    private String dataRetificacao;

    @Etiqueta("Data de geração do arquivo")
    @ValidarRegistro(tamanhoDoCampo = 8, campoNumerico = true)
    private String dataGeracaoArquivo;

    @Etiqueta("E-mail do responsável")
    @ValidarRegistro(tamanhoDoCampo = 45)
    private String emailResponsavel;

    @Etiqueta("Nome do Responsável")
    @ValidarRegistro(tamanhoDoCampo = 52)
    private String nomeResponsavel;

    @Etiqueta("Espaços 24")
    @ValidarRegistro(tamanhoDoCampo = 24)
    private String espacosBranco24;

    @Etiqueta("CPF do responsável")
    @ValidarRegistro(tamanhoDoCampo = 11, campoNumerico = true)
    private String cpfResponsavel;

    @Etiqueta("CREA a ser retificado")
    @ValidarRegistro(tamanhoDoCampo = 12, campoNumerico = true)
    private String creaResponsavel;

    @Etiqueta("Data de nascimento do responsável")
    @ValidarRegistro(tamanhoDoCampo = 8, campoNumerico = true)
    private String dataNascimentoResponsavel;

    @Etiqueta("Espaços 192")
    @ValidarRegistro(tamanhoDoCampo = 192)
    private String espacosBranco192;

    public String getSequenciaRegistro() {
        return sequenciaRegistro;
    }

    public void setSequenciaRegistro(String sequenciaRegistro) {
        this.sequenciaRegistro = StringUtil.cortaOuCompletaEsquerda(sequenciaRegistro, 6, "0");
    }

    public String getInscricaoCNPJCEI() {
        return inscricaoCNPJCEI;
    }

    public void setInscricaoCNPJCEI(String inscricaoCNPJCEI) {
        this.inscricaoCNPJCEI = inscricaoCNPJCEI;
    }

    public String getPrefixo() {
        return prefixo;
    }

    public void setPrefixo(String prefixo) {
        this.prefixo = prefixo;
    }

    public String getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(String tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public String getConstante() {
        return constante;
    }

    public void setConstante(String constante) {
        this.constante = constante;
    }

    public String getCnpjCEIResponsavel() {
        return cnpjCEIResponsavel;
    }

    public void setCnpjCEIResponsavel(String cnpjCEIResponsavel) {
        this.cnpjCEIResponsavel = cnpjCEIResponsavel;
    }

    public String getTipoInscricaoResponsavel() {
        return tipoInscricaoResponsavel;
    }

    public void setTipoInscricaoResponsavel(String tipoInscricaoResponsavel) {
        this.tipoInscricaoResponsavel = tipoInscricaoResponsavel;
    }

    public String getRazaoSocialNomeResponsavel() {
        return razaoSocialNomeResponsavel;
    }

    public void setRazaoSocialNomeResponsavel(String razaoSocialNomeResponsavel) {
        this.razaoSocialNomeResponsavel = StringUtil.removeCaracteresEspeciais(razaoSocialNomeResponsavel);
    }

    public String getLogradouroResponsavel() {
        return logradouroResponsavel;
    }

    public void setLogradouroResponsavel(String logradouroResponsavel) {
        this.logradouroResponsavel = logradouroResponsavel;
    }

    public String getNumeroLogradouro() {
        return numeroLogradouro;
    }

    public void setNumeroLogradouro(String numeroLogradouro) {
        this.numeroLogradouro = numeroLogradouro;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(String codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    public String getNomeMunicipio() {
        return nomeMunicipio;
    }

    public void setNomeMunicipio(String nomeMunicipio) {
        this.nomeMunicipio = nomeMunicipio;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getDdd() {
        return ddd;
    }

    public void setDdd(String ddd) {
        this.ddd = ddd;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getIndicadorRetificacaoDeclaracao() {
        return indicadorRetificacaoDeclaracao;
    }

    public void setIndicadorRetificacaoDeclaracao(String indicadorRetificacaoDeclaracao) {
        this.indicadorRetificacaoDeclaracao = indicadorRetificacaoDeclaracao;
    }

    public String getDataRetificacao() {
        return dataRetificacao;
    }

    public void setDataRetificacao(String dataRetificacao) {
        this.dataRetificacao = dataRetificacao;
    }

    public String getDataGeracaoArquivo() {
        return dataGeracaoArquivo;
    }

    public void setDataGeracaoArquivo(String dataGeracaoArquivo) {
        this.dataGeracaoArquivo = dataGeracaoArquivo;
    }

    public String getEmailResponsavel() {
        return emailResponsavel;
    }

    public void setEmailResponsavel(String emailResponsavel) {
        this.emailResponsavel = emailResponsavel;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public void setNomeResponsavel(String nomeResponsavel) {
        this.nomeResponsavel = nomeResponsavel;
    }

    public String getEspacosBranco24() {
        return espacosBranco24;
    }

    public void setEspacosBranco24(String espacosBranco24) {
        this.espacosBranco24 = espacosBranco24;
    }

    public String getCpfResponsavel() {
        return cpfResponsavel;
    }

    public void setCpfResponsavel(String cpfResponsavel) {
        this.cpfResponsavel = cpfResponsavel;
    }

    public String getCreaResponsavel() {
        return creaResponsavel;
    }

    public void setCreaResponsavel(String creaResponsavel) {
        this.creaResponsavel = creaResponsavel;
    }

    public String getDataNascimentoResponsavel() {
        return dataNascimentoResponsavel;
    }

    public void setDataNascimentoResponsavel(String dataNascimentoResponsavel) {
        this.dataNascimentoResponsavel = dataNascimentoResponsavel;
    }

    public String getEspacosBranco192() {
        return espacosBranco192;
    }

    public void setEspacosBranco192(String espacosBranco192) {
        this.espacosBranco192 = espacosBranco192;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.setSequenciaRegistro("1");
        sb.append(this.getSequenciaRegistro());
        sb.append(this.getInscricaoCNPJCEI());
        sb.append(this.getPrefixo());
        sb.append(this.getTipoRegistro());
        sb.append(this.getConstante());
        sb.append(this.getCnpjCEIResponsavel());
        sb.append(this.getTipoInscricaoResponsavel());
        sb.append(this.getRazaoSocialNomeResponsavel());
        sb.append(this.getLogradouroResponsavel());
        sb.append(this.getNumeroLogradouro());
        sb.append(this.getComplemento());
        sb.append(this.getBairro());
        sb.append(this.getCep());
        sb.append(this.getCodigoMunicipio());
        sb.append(this.getNomeMunicipio());
        sb.append(this.getUf());
        sb.append(this.getDdd());
        sb.append(this.getTelefone());
        sb.append(this.getIndicadorRetificacaoDeclaracao());
        sb.append(this.getDataRetificacao());
        sb.append(this.getDataGeracaoArquivo());
        sb.append(this.getEmailResponsavel());
        sb.append(this.getNomeResponsavel());
        sb.append(this.getEspacosBranco24());
        sb.append(this.getCpfResponsavel());
        sb.append(this.getCreaResponsavel());
        sb.append(this.getDataNascimentoResponsavel());
        sb.append(this.getEspacosBranco192());
        return sb.toString();
    }

    public void validarLayout(AuxiliarAndamentoRais aar) {
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                ValidarRegistro validarRegistro = null;
                Etiqueta etiqueta = null;
                for (Annotation annotation : field.getAnnotations()) {
                    if (annotation instanceof Etiqueta) {
                        etiqueta = (Etiqueta) annotation;
                    }
                    if (annotation instanceof ValidarRegistro) {
                        validarRegistro = (ValidarRegistro) annotation;
                    }
                }
                if (validarRegistro == null) {
                    continue;
                }
                try {
                    String valorAtributo = field.get(this).toString();
                    if ("".equals(valorAtributo) || "null".equals(valorAtributo) || valorAtributo.equals(null)) {
                        aar.getLog().add(Util.dateHourToString(new Date()) + "<font style='color : red;'><i> - &nbsp;&bull; " + etiqueta.value() + " está vazio." + "</i></font><br/>");
                    } else {
                        int tamanhoCampo = validarRegistro.tamanhoDoCampo();
                        if (valorAtributo.length() > tamanhoCampo) {
                            aar.getLog().add(Util.dateHourToString(new Date()) + "<font style='color : red;'><i> - &nbsp;&bull; " + etiqueta.value() + " valor = " + valorAtributo + " quantidade de caracteres maior que" + tamanhoCampo + "</i></font><br/>");
                        }
                        if (valorAtributo.length() < tamanhoCampo) {
                            aar.getLog().add(Util.dateHourToString(new Date()) + "<font style='color : red;'><i> - &nbsp;&bull; " + etiqueta.value() + " valor = " + valorAtributo + " quantidade de caracteres menor que" + tamanhoCampo + "</i></font><br/>");
                        }
                        if (validarRegistro.campoNumerico()) {
                            try {
                                BigDecimal.valueOf(Long.parseLong(field.get(this).toString()));
                            } catch (NumberFormatException nu) {
                                aar.getLog().add(Util.dateHourToString(new Date()) + "<font style='color : red;'><i> - &nbsp;&bull; " + etiqueta.value() + " caracteres não numéricos. Valor " + valorAtributo + "</i></font><br/>");
                            }
                            boolean possivel = false;
                            for (String s : validarRegistro.valoresPossiveis()) {
                                if (s.equals("") || s.equals(valorAtributo)) {
                                    possivel = true;
                                }
                            }
                            if (!possivel) {
                                aar.getLog().add(Util.dateHourToString(new Date()) + "<font style='color : red;'><i> - &nbsp;&bull; " + etiqueta.value() + " o valor " + valorAtributo + " é inválido, dever estar entre os valores " + validarRegistro.valoresPossiveis() + " </i></font><br/>");
                            }
                        }
                    }
                } catch (NullPointerException ne) {
                    aar.getLog().add(Util.dateHourToString(new Date()) + "<font style='color : red;'><i> - &nbsp;&bull; " + etiqueta.value() + " está vazio." + "</i></font><br/>");
                }
            }
        } catch (IllegalAccessException e) {
            logger.debug(e.getMessage());
        }
    }
}
