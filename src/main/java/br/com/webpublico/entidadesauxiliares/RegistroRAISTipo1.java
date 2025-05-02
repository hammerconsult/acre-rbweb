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
public class RegistroRAISTipo1 implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(RegistroRAISTipo1.class);

    private String sequenciaRegistro;

    @Etiqueta("Inscrição CNPJ/CEI do estabelecimento")
    @ValidarRegistro(tamanhoDoCampo = 14, campoNumerico = true)
    private String inscricaoCNPJCEI;

    @Etiqueta("Prefixo do estabelecimento")
    @ValidarRegistro(tamanhoDoCampo = 2)
    private String prefixo;

    @Etiqueta("Tipo do registro")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true, valoresPossiveis = {"1"})
    private String tipoRegistro;

    @Etiqueta("Razão Social")
    @ValidarRegistro(tamanhoDoCampo = 52)
    private String razaoSocial;

    @Etiqueta("Logradouro")
    @ValidarRegistro(tamanhoDoCampo = 40)
    private String logradouro;

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

    @Etiqueta("Código do município")
    @ValidarRegistro(tamanhoDoCampo = 7, campoNumerico = true)
    private String codigoMunicipio;

    @Etiqueta("Nome do município")
    @ValidarRegistro(tamanhoDoCampo = 30)
    private String nomeMunicipio;

    @Etiqueta("UF")
    @ValidarRegistro(tamanhoDoCampo = 2)
    private String uf;

    @Etiqueta("DDD")
    @ValidarRegistro(tamanhoDoCampo = 2, campoNumerico = true)
    private String ddd;

    @Etiqueta("Telefone")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String telefone;

    @Etiqueta("E-mail")
    @ValidarRegistro(tamanhoDoCampo = 45)
    private String email;

    @Etiqueta("Atividade Econômica (CNAE)")
    @ValidarRegistro(tamanhoDoCampo = 7, campoNumerico = true)
    private String cnae;

    @Etiqueta("Natureza Jurídica")
    @ValidarRegistro(tamanhoDoCampo = 4, campoNumerico = true)
    private String naturezaJuridica;

    @Etiqueta("Numero de proprietários")
    @ValidarRegistro(tamanhoDoCampo = 4, campoNumerico = true)
    private String numeroProprietarios;

    @Etiqueta("Data-base")
    @ValidarRegistro(tamanhoDoCampo = 2, campoNumerico = true)
    private String dataBase;

    @Etiqueta("Tipo de inscrição do estabelecimento")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true, valoresPossiveis = {"1", "3"})
    private String tipoInscricaoEstabelecimento;

    @Etiqueta("Tipo RAIS")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true, valoresPossiveis = {"0", "1"})
    private String tipoRAIS;

    @Etiqueta("Zeros")
    @ValidarRegistro(tamanhoDoCampo = 2, campoNumerico = true)
    private String zeros;

    @Etiqueta("Matrícula CEI vínculada a uma inscrição CNPJ")
    @ValidarRegistro(tamanhoDoCampo = 12, campoNumerico = true)
    private String matriculaCEI;

    @Etiqueta("Ano")
    @ValidarRegistro(tamanhoDoCampo = 4, campoNumerico = true)
    private String ano;

    @Etiqueta("Ponte da empresa")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true, valoresPossiveis = {"1", "2", "3"})
    private String porteEmpresa;

    @Etiqueta("Indicador de optante pelo simples")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true, valoresPossiveis = {"1", "2"})
    private String indicadorOptantesSimples;

    @Etiqueta("Indicador de participação no PAT")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true, valoresPossiveis = {"1", "2"})
    private String indicadorParticipacaoPAT;

    @Etiqueta("Trabalhadores que recebem até 5 5al.Mínimos")
    @ValidarRegistro(tamanhoDoCampo = 6, campoNumerico = true)
    private String trabalhadoresRecebemAte5Salarios;

    @Etiqueta("Trabalhadores que recebem acima de 5 5al.Mínimos")
    @ValidarRegistro(tamanhoDoCampo = 6, campoNumerico = true)
    private String trabalhadoresRecebemAcima5Salarios;

    @Etiqueta("Porcentagem de serviço próprio (%)")
    @ValidarRegistro(tamanhoDoCampo = 3, campoNumerico = true)
    private String percentualServicoProprio;

    @Etiqueta("Porcentagem de administração de cozinhas (%)")
    @ValidarRegistro(tamanhoDoCampo = 3, campoNumerico = true)
    private String percentualAdministracaoCozinhas;

    @Etiqueta("Porcentagem de refeição-convênio (%)")
    @ValidarRegistro(tamanhoDoCampo = 3, campoNumerico = true)
    private String percentualRefeicaoConvenio;

    @Etiqueta("Porcentagem de refeição transportadas (%)")
    @ValidarRegistro(tamanhoDoCampo = 3, campoNumerico = true)
    private String percentualRefeicaoTransportada;

    @Etiqueta("Porcentagem de cesta alimento (%)")
    @ValidarRegistro(tamanhoDoCampo = 3, campoNumerico = true)
    private String percentualCestaAlimento;

    @Etiqueta("Porcentagem de alimentação-convênio (%)")
    @ValidarRegistro(tamanhoDoCampo = 3, campoNumerico = true)
    private String percentualAlimentacaoConvenio;

    @Etiqueta("Indicador de Encerramento das Atividades")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true, valoresPossiveis = {"1", "2"})
    private String indicadorEncerramentoAtividades;

    @Etiqueta("Data de Encerramento das Atividades")
    @ValidarRegistro(tamanhoDoCampo = 8, campoNumerico = true)
    private String dataEncerramentoAtividades;

    @Etiqueta("CNPJ - contribuição associativa")
    @ValidarRegistro(tamanhoDoCampo = 14, campoNumerico = true)
    private String contribuicaoAssociativaCNPJ;

    @Etiqueta("Valor - contribuição associativa ")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String contribuicaoAssociativaValor;

    @Etiqueta("CNPJ - contribuição sindical")
    @ValidarRegistro(tamanhoDoCampo = 14, campoNumerico = true)
    private String contribuicaoSindicalCNPJ;

    @Etiqueta("Valor - contribuição sindical ")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String contribuicaoSindicalValor;

    @Etiqueta("CNPJ - contribuição assistencial")
    @ValidarRegistro(tamanhoDoCampo = 14, campoNumerico = true)
    private String contribuicaoAssistencialCNPJ;

    @Etiqueta("Valor - contribuição assistencial")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String contribuicaoAssistencialValor;

    @Etiqueta("CNPJ - contribuição confederativa")
    @ValidarRegistro(tamanhoDoCampo = 14, campoNumerico = true)
    private String contribuicaoConfederativaCNPJ;

    @Etiqueta("Valor - contribuição confederativa")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String contribuicaoConfederativaValor;

    @Etiqueta("Esteve em atividade no ano-base")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true, valoresPossiveis = {"1", "2"})
    private String atividadeAnoBase;

    @Etiqueta("Indicador de centralização do pagamento da contribuição sindical")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true, valoresPossiveis = {"1", "2"})
    private String indicadorPagamentoContribuicaoSindical;

    @Etiqueta("CNPJ - estabelecimento centralizador da contribuição sindical")
    @ValidarRegistro(tamanhoDoCampo = 14, campoNumerico = true)
    private String cnpjEstabelecimentoContribuicaoSindical;

    @Etiqueta("Indicador - empresa filiada a sindicato")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true, valoresPossiveis = {"1", "2"})
    private String indicadorEmpresaFiliadaSindicato;

    @Etiqueta("Tipo de sistema de controle de ponto")
    @ValidarRegistro(tamanhoDoCampo = 2, campoNumerico = true, valoresPossiveis = {"00", "01", "02", "03", "04", "05", "06"})
    private String tipoSistemaControlePonto;

    @Etiqueta("Espaços 85")
    @ValidarRegistro(tamanhoDoCampo = 85)
    private String espacosBranco85;

    @Etiqueta("Informação de uso exclusivo da empresa")
    @ValidarRegistro(tamanhoDoCampo = 45)
    private String informacaoUsoExclusivoEmpresa;

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

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = StringUtil.removeCaracteresEspeciais(razaoSocial);
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCnae() {
        return cnae;
    }

    public void setCnae(String cnae) {
        this.cnae = cnae;
    }

    public String getNaturezaJuridica() {
        return naturezaJuridica;
    }

    public void setNaturezaJuridica(String naturezaJuridica) {
        this.naturezaJuridica = naturezaJuridica;
    }

    public String getNumeroProprietarios() {
        return numeroProprietarios;
    }

    public void setNumeroProprietarios(String numeroProprietarios) {
        this.numeroProprietarios = numeroProprietarios;
    }

    public String getDataBase() {
        return dataBase;
    }

    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

    public String getTipoInscricaoEstabelecimento() {
        return tipoInscricaoEstabelecimento;
    }

    public void setTipoInscricaoEstabelecimento(String tipoInscricaoEstabelecimento) {
        this.tipoInscricaoEstabelecimento = tipoInscricaoEstabelecimento;
    }

    public String getTipoRAIS() {
        return tipoRAIS;
    }

    public void setTipoRAIS(String tipoRAIS) {
        this.tipoRAIS = tipoRAIS;
    }

    public String getZeros() {
        return zeros;
    }

    public void setZeros(String zeros) {
        this.zeros = zeros;
    }

    public String getMatriculaCEI() {
        return matriculaCEI;
    }

    public void setMatriculaCEI(String matriculaCEI) {
        this.matriculaCEI = matriculaCEI;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getPorteEmpresa() {
        return porteEmpresa;
    }

    public void setPorteEmpresa(String porteEmpresa) {
        this.porteEmpresa = porteEmpresa;
    }

    public String getIndicadorOptantesSimples() {
        return indicadorOptantesSimples;
    }

    public void setIndicadorOptantesSimples(String indicadorOptantesSimples) {
        this.indicadorOptantesSimples = indicadorOptantesSimples;
    }

    public String getIndicadorParticipacaoPAT() {
        return indicadorParticipacaoPAT;
    }

    public void setIndicadorParticipacaoPAT(String indicadorParticipacaoPAT) {
        this.indicadorParticipacaoPAT = indicadorParticipacaoPAT;
    }

    public String getTrabalhadoresRecebemAte5Salarios() {
        return trabalhadoresRecebemAte5Salarios;
    }

    public void setTrabalhadoresRecebemAte5Salarios(String trabalhadoresRecebemAte5Salarios) {
        this.trabalhadoresRecebemAte5Salarios = trabalhadoresRecebemAte5Salarios;
    }

    public String getTrabalhadoresRecebemAcima5Salarios() {
        return trabalhadoresRecebemAcima5Salarios;
    }

    public void setTrabalhadoresRecebemAcima5Salarios(String trabalhadoresRecebemAcima5Salarios) {
        this.trabalhadoresRecebemAcima5Salarios = trabalhadoresRecebemAcima5Salarios;
    }

    public String getPercentualServicoProprio() {
        return percentualServicoProprio;
    }

    public void setPercentualServicoProprio(String percentualServicoProprio) {
        this.percentualServicoProprio = percentualServicoProprio;
    }

    public String getPercentualAdministracaoCozinhas() {
        return percentualAdministracaoCozinhas;
    }

    public void setPercentualAdministracaoCozinhas(String percentualAdministracaoCozinhas) {
        this.percentualAdministracaoCozinhas = percentualAdministracaoCozinhas;
    }

    public String getPercentualRefeicaoConvenio() {
        return percentualRefeicaoConvenio;
    }

    public void setPercentualRefeicaoConvenio(String percentualRefeicaoConvenio) {
        this.percentualRefeicaoConvenio = percentualRefeicaoConvenio;
    }

    public String getPercentualRefeicaoTransportada() {
        return percentualRefeicaoTransportada;
    }

    public void setPercentualRefeicaoTransportada(String percentualRefeicaoTransportada) {
        this.percentualRefeicaoTransportada = percentualRefeicaoTransportada;
    }

    public String getPercentualCestaAlimento() {
        return percentualCestaAlimento;
    }

    public void setPercentualCestaAlimento(String percentualCestaAlimento) {
        this.percentualCestaAlimento = percentualCestaAlimento;
    }

    public String getPercentualAlimentacaoConvenio() {
        return percentualAlimentacaoConvenio;
    }

    public void setPercentualAlimentacaoConvenio(String percentualAlimentacaoConvenio) {
        this.percentualAlimentacaoConvenio = percentualAlimentacaoConvenio;
    }

    public String getIndicadorEncerramentoAtividades() {
        return indicadorEncerramentoAtividades;
    }

    public void setIndicadorEncerramentoAtividades(String indicadorEncerramentoAtividades) {
        this.indicadorEncerramentoAtividades = indicadorEncerramentoAtividades;
    }

    public String getDataEncerramentoAtividades() {
        return dataEncerramentoAtividades;
    }

    public void setDataEncerramentoAtividades(String dataEncerramentoAtividades) {
        this.dataEncerramentoAtividades = dataEncerramentoAtividades;
    }

    public String getContribuicaoAssociativaCNPJ() {
        return contribuicaoAssociativaCNPJ;
    }

    public void setContribuicaoAssociativaCNPJ(String contribuicaoAssociativaCNPJ) {
        this.contribuicaoAssociativaCNPJ = contribuicaoAssociativaCNPJ;
    }

    public String getContribuicaoAssociativaValor() {
        return contribuicaoAssociativaValor;
    }

    public void setContribuicaoAssociativaValor(String contribuicaoAssociativaValor) {
        this.contribuicaoAssociativaValor = contribuicaoAssociativaValor;
    }

    public String getContribuicaoSindicalCNPJ() {
        return contribuicaoSindicalCNPJ;
    }

    public void setContribuicaoSindicalCNPJ(String contribuicaoSindicalCNPJ) {
        this.contribuicaoSindicalCNPJ = contribuicaoSindicalCNPJ;
    }

    public String getContribuicaoSindicalValor() {
        return contribuicaoSindicalValor;
    }

    public void setContribuicaoSindicalValor(String contribuicaoSindicalValor) {
        this.contribuicaoSindicalValor = contribuicaoSindicalValor;
    }

    public String getContribuicaoAssistencialCNPJ() {
        return contribuicaoAssistencialCNPJ;
    }

    public void setContribuicaoAssistencialCNPJ(String contribuicaoAssistencialCNPJ) {
        this.contribuicaoAssistencialCNPJ = contribuicaoAssistencialCNPJ;
    }

    public String getContribuicaoAssistencialValor() {
        return contribuicaoAssistencialValor;
    }

    public void setContribuicaoAssistencialValor(String contribuicaoAssistencialValor) {
        this.contribuicaoAssistencialValor = contribuicaoAssistencialValor;
    }

    public String getContribuicaoConfederativaCNPJ() {
        return contribuicaoConfederativaCNPJ;
    }

    public void setContribuicaoConfederativaCNPJ(String contribuicaoConfederativaCNPJ) {
        this.contribuicaoConfederativaCNPJ = contribuicaoConfederativaCNPJ;
    }

    public String getContribuicaoConfederativaValor() {
        return contribuicaoConfederativaValor;
    }

    public void setContribuicaoConfederativaValor(String contribuicaoConfederativaValor) {
        this.contribuicaoConfederativaValor = contribuicaoConfederativaValor;
    }

    public String getAtividadeAnoBase() {
        return atividadeAnoBase;
    }

    public void setAtividadeAnoBase(String atividadeAnoBase) {
        this.atividadeAnoBase = atividadeAnoBase;
    }

    public String getIndicadorPagamentoContribuicaoSindical() {
        return indicadorPagamentoContribuicaoSindical;
    }

    public void setIndicadorPagamentoContribuicaoSindical(String indicadorPagamentoContribuicaoSindical) {
        this.indicadorPagamentoContribuicaoSindical = indicadorPagamentoContribuicaoSindical;
    }

    public String getCnpjEstabelecimentoContribuicaoSindical() {
        return cnpjEstabelecimentoContribuicaoSindical;
    }

    public void setCnpjEstabelecimentoContribuicaoSindical(String cnpjEstabelecimentoContribuicaoSindical) {
        this.cnpjEstabelecimentoContribuicaoSindical = cnpjEstabelecimentoContribuicaoSindical;
    }

    public String getIndicadorEmpresaFiliadaSindicato() {
        return indicadorEmpresaFiliadaSindicato;
    }

    public void setIndicadorEmpresaFiliadaSindicato(String indicadorEmpresaFiliadaSindicato) {
        this.indicadorEmpresaFiliadaSindicato = indicadorEmpresaFiliadaSindicato;
    }

    public String getTipoSistemaControlePonto() {
        return tipoSistemaControlePonto;
    }

    public void setTipoSistemaControlePonto(String tipoSistemaControlePonto) {
        this.tipoSistemaControlePonto = tipoSistemaControlePonto;
    }

    public String getEspacosBranco85() {
        return espacosBranco85;
    }

    public void setEspacosBranco85(String espacosBranco85) {
        this.espacosBranco85 = espacosBranco85;
    }

    public String getInformacaoUsoExclusivoEmpresa() {
        return informacaoUsoExclusivoEmpresa;
    }

    public void setInformacaoUsoExclusivoEmpresa(String informacaoUsoExclusivoEmpresa) {
        this.informacaoUsoExclusivoEmpresa = informacaoUsoExclusivoEmpresa;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.setSequenciaRegistro("2");
        sb.append(this.getSequenciaRegistro());
        sb.append(this.getInscricaoCNPJCEI());
        sb.append(this.getPrefixo());
        sb.append(this.getTipoRegistro());
        sb.append(this.getRazaoSocial());
        sb.append(this.getLogradouro());
        sb.append(this.getNumeroLogradouro());
        sb.append(this.getComplemento());
        sb.append(this.getBairro());
        sb.append(this.getCep());
        sb.append(this.getCodigoMunicipio());
        sb.append(this.getNomeMunicipio());
        sb.append(this.getUf());
        sb.append(this.getDdd());
        sb.append(this.getTelefone());
        sb.append(this.getEmail());
        sb.append(this.getCnae());
        sb.append(this.getNaturezaJuridica());
        sb.append(this.getNumeroProprietarios());
        sb.append(this.getDataBase());
        sb.append(this.getTipoInscricaoEstabelecimento());
        sb.append(this.getTipoRAIS());
        sb.append(this.getZeros());
        sb.append(this.getMatriculaCEI());
        sb.append(this.getAno());
        sb.append(this.getPorteEmpresa());
        sb.append(this.getIndicadorOptantesSimples());
        sb.append(this.getIndicadorParticipacaoPAT());
        sb.append(this.getTrabalhadoresRecebemAte5Salarios());
        sb.append(this.getTrabalhadoresRecebemAcima5Salarios());
        sb.append(this.getPercentualServicoProprio());
        sb.append(this.getPercentualAdministracaoCozinhas());
        sb.append(this.getPercentualRefeicaoConvenio());
        sb.append(this.getPercentualRefeicaoTransportada());
        sb.append(this.getPercentualCestaAlimento());
        sb.append(this.getPercentualAlimentacaoConvenio());
        sb.append(this.getIndicadorEncerramentoAtividades());
        sb.append(this.getDataEncerramentoAtividades());
        sb.append(this.getContribuicaoAssociativaCNPJ());
        sb.append(this.getContribuicaoAssociativaValor());
        sb.append(this.getContribuicaoSindicalCNPJ());
        sb.append(this.getContribuicaoSindicalValor());
        sb.append(this.getContribuicaoAssistencialCNPJ());
        sb.append(this.getContribuicaoAssistencialValor());
        sb.append(this.getContribuicaoConfederativaCNPJ());
        sb.append(this.getContribuicaoConfederativaValor());
        sb.append(this.getAtividadeAnoBase());
        sb.append(this.getIndicadorPagamentoContribuicaoSindical());
        sb.append(this.getCnpjEstabelecimentoContribuicaoSindical());
        sb.append(this.getIndicadorEmpresaFiliadaSindicato());
        sb.append(this.getTipoSistemaControlePonto());
        sb.append(this.getEspacosBranco85());
        sb.append(this.getInformacaoUsoExclusivoEmpresa());
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
