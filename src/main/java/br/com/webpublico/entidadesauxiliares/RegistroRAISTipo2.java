/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.ValidarRegistro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Id;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Claudio
 */
public class RegistroRAISTipo2 extends SuperEntidade implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(RegistroRAISTipo2.class);
    @Id
    private Long id;
    private String sequenciaRegistro;

    @Etiqueta("Inscrição CNPJ/CEI do estabelecimento")
    @ValidarRegistro(tamanhoDoCampo = 14, campoNumerico = true)
    private String inscricaoCNPJCEI;

    @Etiqueta("Prefixo do estabelecimento")
    @ValidarRegistro(tamanhoDoCampo = 2)
    private String prefixo;

    @Etiqueta("Tipo do registro")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true, valoresPossiveis = {"2"})
    private String tipoRegistro;

    @Etiqueta("Código PIS/PASEP")
    @ValidarRegistro(tamanhoDoCampo = 11, campoNumerico = true)
    private String codigoPisPasep;

    @Etiqueta("Nome do empregado")
    @ValidarRegistro(tamanhoDoCampo = 52)
    private String nomeEmpregado;

    @Etiqueta("Data de nascimento")
    @ValidarRegistro(tamanhoDoCampo = 8, campoNumerico = true)
    private String dataNascimento;

    @Etiqueta("Nacionalidade")
    @ValidarRegistro(tamanhoDoCampo = 2)
    private String nacionalidade;

    @Etiqueta("Ano de Chegada ao país")
    @ValidarRegistro(tamanhoDoCampo = 4, campoNumerico = true)
    private String anoChegadaAoPais;

    @Etiqueta("Grau de Instrução")
    @ValidarRegistro(tamanhoDoCampo = 2, campoNumerico = true)
    private String grauInstrucao;

    @Etiqueta("CPF")
    @ValidarRegistro(tamanhoDoCampo = 11, campoNumerico = true)
    private String cpf;

    @Etiqueta("CTPS número")
    @ValidarRegistro(tamanhoDoCampo = 8, campoNumerico = true)
    private String ctpsNumero;

    @Etiqueta("CTPS série")
    @ValidarRegistro(tamanhoDoCampo = 5)
    private String ctpsSerie;

    @Etiqueta("Data de admissão/Data de Transferência")
    @ValidarRegistro(tamanhoDoCampo = 8, campoNumerico = true)
    private String dataAdmissaoTransferencia;

    @Etiqueta("Tipo de Admissão")
    @ValidarRegistro(tamanhoDoCampo = 2, campoNumerico = true)
    private String tipoAdmissao;

    @Etiqueta("Salário Contratual")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String salarioContratual;

    @Etiqueta("Tipo de Salário Contratual")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true)
    private String tipoSalarioContratual;

    @Etiqueta("Horas Semanais")
    @ValidarRegistro(tamanhoDoCampo = 2, campoNumerico = true)
    private String horasSemanais;

    @Etiqueta("CBO")
    @ValidarRegistro(tamanhoDoCampo = 6, campoNumerico = true)
    private String cbo;

    @Etiqueta("Vínculo empregatício")
    @ValidarRegistro(tamanhoDoCampo = 2, campoNumerico = true)
    private String vinculoEmpregaticio;

    @Etiqueta("Código do desligamento")
    @ValidarRegistro(tamanhoDoCampo = 2, campoNumerico = true)
    private String codigoDesligamento;

    @Etiqueta("Data do desligamento")
    @ValidarRegistro(tamanhoDoCampo = 4, campoNumerico = true)
    private String dataDesligamento;

    @Etiqueta("Remuneração de janeiro")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String remuneracaoJaneiro;

    @Etiqueta("Remuneração de fevereiro")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String remuneracaoFevereiro;

    @Etiqueta("Remuneração de marco")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String remuneracaoMarco;

    @Etiqueta("Remuneração de abril")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String remuneracaoAbril;

    @Etiqueta("Remuneração de maio")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String remuneracaoMaio;

    @Etiqueta("Remuneração de abril")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String remuneracaoJunho;

    @Etiqueta("Remuneração de julho")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String remuneracaoJulho;

    @Etiqueta("Remuneração de agosto")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String remuneracaoAgosto;

    @Etiqueta("Remuneração de setembro")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String remuneracaoSetembro;

    @Etiqueta("Remuneração de outubro")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String remuneracaoOutubro;

    @Etiqueta("Remuneração de novembro")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String remuneracaoNovembro;

    @Etiqueta("Remuneração de dezembro")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String remuneracaoDezembro;

    @Etiqueta("Remuneração de 13° Salário Adiantamento")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String remuneracao13SalarioAdiantamento;

    @Etiqueta("Mês de Pagamento do 13° Salário Adiantamento")
    @ValidarRegistro(tamanhoDoCampo = 2, campoNumerico = true)
    private String mesPagamento13SalarioAdiantamento;

    @Etiqueta("Remuneração do 13° Salário Final")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String remuneracao13SalarioFinal;

    @Etiqueta("Mês do 13° Salário Final")
    @ValidarRegistro(tamanhoDoCampo = 2, campoNumerico = true)
    private String mesPagamento13SalarioFinal;

    @Etiqueta("Raça/Cor")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true)
    private String racaCor;

    @Etiqueta("Indicador de Deficiência")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true)
    private String indicadorDeficiencia;

    @Etiqueta("Tipo de Deficiência")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true)
    private String tipoDeficiencia;

    @Etiqueta("Indicador de Alvará")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true)
    private String indicadorAlvara;

    @Etiqueta("Aviso Prévio Indenizado")
    @ValidarRegistro(tamanhoDoCampo = 9, campoNumerico = true)
    private String avisoPrevioIndenizado;

    @Etiqueta("Sexo")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true)
    private String sexo;

    @Etiqueta("Motivo do primeiro afastamento")
    @ValidarRegistro(tamanhoDoCampo = 2, campoNumerico = true)
    private String motivoPrimeiroAfastamento;

    @Etiqueta("Data início do primeiro afastamento")
    @ValidarRegistro(tamanhoDoCampo = 4, campoNumerico = true)
    private String dataInicioPrimeiroAfastamento;

    @Etiqueta("Data final do primeiro afastamento")
    @ValidarRegistro(tamanhoDoCampo = 4, campoNumerico = true)
    private String dataFinalPrimeiroAfastamento;

    @Etiqueta("Motivo do segundo afastamento")
    @ValidarRegistro(tamanhoDoCampo = 2, campoNumerico = true)
    private String motivoSegundoAfastamento;

    @Etiqueta("Data início do segundo afastamento")
    @ValidarRegistro(tamanhoDoCampo = 4, campoNumerico = true)
    private String dataInicioSegundoAfastamento;

    @Etiqueta("Data final do segundo afastamento")
    @ValidarRegistro(tamanhoDoCampo = 4, campoNumerico = true)
    private String dataFinalSegundoAfastamento;

    @Etiqueta("Motivo do terceiro afastamento")
    @ValidarRegistro(tamanhoDoCampo = 2, campoNumerico = true)
    private String motivoTerceiroAfastamento;

    @Etiqueta("Data início do terceiro afastamento")
    @ValidarRegistro(tamanhoDoCampo = 4, campoNumerico = true)
    private String dataInicioTerceiroAfastamento;

    @Etiqueta("Data final do terceiro afastamento")
    @ValidarRegistro(tamanhoDoCampo = 4, campoNumerico = true)
    private String dataFinalTerceiroAfastamento;

    @Etiqueta("Quantidade dias afastamento")
    @ValidarRegistro(tamanhoDoCampo = 3, campoNumerico = true)
    private String quantidadeDiasAfastamento;

    @Etiqueta("Valor - férias indenizadas")
    @ValidarRegistro(tamanhoDoCampo = 8, campoNumerico = true)
    private String valorFeriasIndenizadas;

    @Etiqueta("Valor - banco de horas")
    @ValidarRegistro(tamanhoDoCampo = 8, campoNumerico = true)
    private String valorBancoDeHoras;

    @Etiqueta("Quantidade de meses - banco de horas ")
    @ValidarRegistro(tamanhoDoCampo = 2, campoNumerico = true)
    private String quantidadeMesesBancoDeHoras;

    @Etiqueta("Valor - dissídio coletivo ")
    @ValidarRegistro(tamanhoDoCampo = 8, campoNumerico = true)
    private String valorDissidioColetivo;

    @Etiqueta("Quantidades de meses - dissídio coletivo ")
    @ValidarRegistro(tamanhoDoCampo = 2, campoNumerico = true)
    private String quantidadeMesesDissidioColetivo;

    @Etiqueta("Valor - gratificações")
    @ValidarRegistro(tamanhoDoCampo = 8, campoNumerico = true)
    private String valorGratificacoes;

    @Etiqueta("Quantidade de meses gratificações")
    @ValidarRegistro(tamanhoDoCampo = 2, campoNumerico = true)
    private String quantidadeMesesGratificacoes;

    @Etiqueta("Valor - multa por rescisão sem justa causa")
    @ValidarRegistro(tamanhoDoCampo = 8, campoNumerico = true)
    private String valorMultaRescisaoSemJustaCausa;

    @Etiqueta("CNPJ - contribuição associativa (1° ocorrência)")
    @ValidarRegistro(tamanhoDoCampo = 14, campoNumerico = true)
    private String contribuicaoAssociativa1OcorrenciaCNPJ;

    @Etiqueta("Valor - contribuição associativa (1° ocorrência)")
    @ValidarRegistro(tamanhoDoCampo = 8, campoNumerico = true)
    private String contribuicaoAssociativa1OcorrenciaValor;

    @Etiqueta("CNPJ - contribuição associativa (2° ocorrência)")
    @ValidarRegistro(tamanhoDoCampo = 14, campoNumerico = true)
    private String contribuicaoAssociativa2OcorrenciaCNPJ;

    @Etiqueta("Valor - contribuição associativa (2° ocorrência)")
    @ValidarRegistro(tamanhoDoCampo = 8, campoNumerico = true)
    private String contribuicaoAssociativa2OcorrenciaValor;

    @Etiqueta("CNPJ - contribuição (tributo) sindical")
    @ValidarRegistro(tamanhoDoCampo = 14, campoNumerico = true)
    private String contribuicaoSindicalCNPJ;

    @Etiqueta("Valor - contribuição (tributo) sindical")
    @ValidarRegistro(tamanhoDoCampo = 8, campoNumerico = true)
    private String contribuicaoSindicalValor;

    @Etiqueta("CNPJ - contribuição assistencial")
    @ValidarRegistro(tamanhoDoCampo = 14, campoNumerico = true)
    private String contribuicaoAssistencialCNPJ;

    @Etiqueta("Valor - contribuição assistencial")
    @ValidarRegistro(tamanhoDoCampo = 8, campoNumerico = true)
    private String contribuicaoAssistencialValor;

    @Etiqueta("CNPJ - contribuição confederativa")
    @ValidarRegistro(tamanhoDoCampo = 14, campoNumerico = true)
    private String contribuicaoConfederativaCNPJ;

    @Etiqueta("Valor - contribuição confederativa")
    @ValidarRegistro(tamanhoDoCampo = 8, campoNumerico = true)
    private String contribuicaoConfederativaValor;

    @Etiqueta("Município - local de trabalho")
    @ValidarRegistro(tamanhoDoCampo = 7, campoNumerico = true)
    private String municipioTrabalho;

    @Etiqueta("Horas extras trabalhadas - janeiro")
    @ValidarRegistro(tamanhoDoCampo = 3, campoNumerico = true)
    private String horasExtrasJaneiro;

    @Etiqueta("Horas extras trabalhadas - fevereiro")
    @ValidarRegistro(tamanhoDoCampo = 3, campoNumerico = true)
    private String horasExtrasFevereiro;

    @Etiqueta("Horas extras trabalhadas - março")
    @ValidarRegistro(tamanhoDoCampo = 3, campoNumerico = true)
    private String horasExtrasMarco;

    @Etiqueta("Horas extras trabalhadas - abril")
    @ValidarRegistro(tamanhoDoCampo = 3, campoNumerico = true)
    private String horasExtrasAbril;

    @Etiqueta("Horas extras trabalhadas - maio")
    @ValidarRegistro(tamanhoDoCampo = 3, campoNumerico = true)
    private String horasExtrasMaio;

    @Etiqueta("Horas extras trabalhadas - junho")
    @ValidarRegistro(tamanhoDoCampo = 3, campoNumerico = true)
    private String horasExtrasJunho;

    @Etiqueta("Horas extras trabalhadas - julho")
    @ValidarRegistro(tamanhoDoCampo = 3, campoNumerico = true)
    private String horasExtrasJulho;

    @Etiqueta("Horas extras trabalhadas - agosto")
    @ValidarRegistro(tamanhoDoCampo = 3, campoNumerico = true)
    private String horasExtrasAgosto;

    @Etiqueta("Horas extras trabalhadas - setembro")
    @ValidarRegistro(tamanhoDoCampo = 3, campoNumerico = true)
    private String horasExtrasSetembro;

    @Etiqueta("Horas extras trabalhadas - outubro")
    @ValidarRegistro(tamanhoDoCampo = 3, campoNumerico = true)
    private String horasExtrasOutubro;

    @Etiqueta("Horas extras trabalhadas - novembro")
    @ValidarRegistro(tamanhoDoCampo = 3, campoNumerico = true)
    private String horasExtrasNovembro;

    @Etiqueta("Horas extras trabalhadas - dezembro")
    @ValidarRegistro(tamanhoDoCampo = 3, campoNumerico = true)
    private String horasExtrasDezembro;

    @Etiqueta("Indicador - empregado filiado a sindicato")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true)
    private String indicadorEmpregadoFiliadoSindicato;

    @Etiqueta("Identificador Vínculo Aprendiz Gravida")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true)
    private String identificadorAprendizGravida;

    @Etiqueta("Identificador Trabalho Parcial")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true)
    private String idenficadorTrabalhoParcial;

    @Etiqueta("Identificador Tele Trabalho")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true)
    private String identificadorTeleTrabalho;

    @Etiqueta("Identificador Trabalho Intermitente")
    @ValidarRegistro(tamanhoDoCampo = 1, campoNumerico = true)
    private String identificadorTrabalhoIntermitente;


    @Etiqueta("Informação de uso exclusivo da empresa")
    @ValidarRegistro(tamanhoDoCampo = 8)
    private String informacaoUsoExclusivoEmpresa;

    @Etiqueta("Matrícula")
    @ValidarRegistro(tamanhoDoCampo = 30)
    private String matriculaContrato;

    @Etiqueta("Categoria")
    @ValidarRegistro(tamanhoDoCampo = 3)
    private String categoriaTrabalhador;

    public RegistroRAISTipo2(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getCodigoPisPasep() {
        return codigoPisPasep;
    }

    public void setCodigoPisPasep(String codigoPisPasep) {
        this.codigoPisPasep = codigoPisPasep;
    }

    public String getNomeEmpregado() {
        return nomeEmpregado;
    }

    public void setNomeEmpregado(String nomeEmpregado) {
        this.nomeEmpregado = StringUtil.removeCaracteresEspeciais(nomeEmpregado);
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public String getAnoChegadaAoPais() {
        return anoChegadaAoPais;
    }

    public void setAnoChegadaAoPais(String anoChegadaAoPais) {
        this.anoChegadaAoPais = anoChegadaAoPais;
    }

    public String getGrauInstrucao() {
        return grauInstrucao;
    }

    public void setGrauInstrucao(String grauInstrucao) {
        this.grauInstrucao = grauInstrucao;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCtpsNumero() {
        return ctpsNumero;
    }

    public void setCtpsNumero(String ctpsNumero) {
        this.ctpsNumero = ctpsNumero;
    }

    public String getCtpsSerie() {
        return ctpsSerie;
    }

    public void setCtpsSerie(String ctpsSerie) {
        this.ctpsSerie = ctpsSerie;
    }

    public String getDataAdmissaoTransferencia() {
        return dataAdmissaoTransferencia;
    }

    public void setDataAdmissaoTransferencia(String dataAdmissaoTransferencia) {
        this.dataAdmissaoTransferencia = dataAdmissaoTransferencia;
    }

    public String getTipoAdmissao() {
        return tipoAdmissao;
    }

    public void setTipoAdmissao(String tipoAdmissao) {
        this.tipoAdmissao = tipoAdmissao;
    }

    public String getSalarioContratual() {
        return salarioContratual;
    }

    public void setSalarioContratual(String salarioContratual) {
        this.salarioContratual = salarioContratual;
    }

    public String getTipoSalarioContratual() {
        return tipoSalarioContratual;
    }

    public void setTipoSalarioContratual(String tipoSalarioContratual) {
        this.tipoSalarioContratual = tipoSalarioContratual;
    }

    public String getHorasSemanais() {
        return horasSemanais;
    }

    public void setHorasSemanais(String horasSemanais) {
        this.horasSemanais = horasSemanais;
    }

    public String getCbo() {
        return cbo;
    }

    public void setCbo(String cbo) {
        this.cbo = cbo;
    }

    public String getVinculoEmpregaticio() {
        return vinculoEmpregaticio;
    }

    public void setVinculoEmpregaticio(String vinculoEmpregaticio) {
        this.vinculoEmpregaticio = vinculoEmpregaticio;
    }

    public String getCodigoDesligamento() {
        return codigoDesligamento;
    }

    public void setCodigoDesligamento(String codigoDesligamento) {
        this.codigoDesligamento = codigoDesligamento;
    }

    public String getDataDesligamento() {
        return dataDesligamento;
    }

    public void setDataDesligamento(String dataDesligamento) {
        this.dataDesligamento = dataDesligamento;
    }

    public String getRemuneracaoJaneiro() {
        return remuneracaoJaneiro;
    }

    public void setRemuneracaoJaneiro(String remuneracaoJaneiro) {
        this.remuneracaoJaneiro = remuneracaoJaneiro;
    }

    public String getRemuneracaoFevereiro() {
        return remuneracaoFevereiro;
    }

    public void setRemuneracaoFevereiro(String remuneracaoFevereiro) {
        this.remuneracaoFevereiro = remuneracaoFevereiro;
    }

    public String getRemuneracaoMarco() {
        return remuneracaoMarco;
    }

    public void setRemuneracaoMarco(String remuneracaoMarco) {
        this.remuneracaoMarco = remuneracaoMarco;
    }

    public String getRemuneracaoAbril() {
        return remuneracaoAbril;
    }

    public void setRemuneracaoAbril(String remuneracaoAbril) {
        this.remuneracaoAbril = remuneracaoAbril;
    }

    public String getRemuneracaoMaio() {
        return remuneracaoMaio;
    }

    public void setRemuneracaoMaio(String remuneracaoMaio) {
        this.remuneracaoMaio = remuneracaoMaio;
    }

    public String getRemuneracaoJunho() {
        return remuneracaoJunho;
    }

    public void setRemuneracaoJunho(String remuneracaoJunho) {
        this.remuneracaoJunho = remuneracaoJunho;
    }

    public String getRemuneracaoJulho() {
        return remuneracaoJulho;
    }

    public void setRemuneracaoJulho(String remuneracaoJulho) {
        this.remuneracaoJulho = remuneracaoJulho;
    }

    public String getRemuneracaoAgosto() {
        return remuneracaoAgosto;
    }

    public void setRemuneracaoAgosto(String remuneracaoAgosto) {
        this.remuneracaoAgosto = remuneracaoAgosto;
    }

    public String getRemuneracaoSetembro() {
        return remuneracaoSetembro;
    }

    public void setRemuneracaoSetembro(String remuneracaoSetembro) {
        this.remuneracaoSetembro = remuneracaoSetembro;
    }

    public String getRemuneracaoOutubro() {
        return remuneracaoOutubro;
    }

    public void setRemuneracaoOutubro(String remuneracaoOutubro) {
        this.remuneracaoOutubro = remuneracaoOutubro;
    }

    public String getRemuneracaoNovembro() {
        return remuneracaoNovembro;
    }

    public void setRemuneracaoNovembro(String remuneracaoNovembro) {
        this.remuneracaoNovembro = remuneracaoNovembro;
    }

    public String getRemuneracaoDezembro() {
        return remuneracaoDezembro;
    }

    public void setRemuneracaoDezembro(String remuneracaoDezembro) {
        this.remuneracaoDezembro = remuneracaoDezembro;
    }

    public String getRemuneracao13SalarioAdiantamento() {
        return remuneracao13SalarioAdiantamento;
    }

    public void setRemuneracao13SalarioAdiantamento(String remuneracao13SalarioAdiantamento) {
        this.remuneracao13SalarioAdiantamento = remuneracao13SalarioAdiantamento;
    }

    public String getMesPagamento13SalarioAdiantamento() {
        return mesPagamento13SalarioAdiantamento;
    }

    public void setMesPagamento13SalarioAdiantamento(String mesPagamento13SalarioAdiantamento) {
        this.mesPagamento13SalarioAdiantamento = mesPagamento13SalarioAdiantamento;
    }

    public String getRemuneracao13SalarioFinal() {
        return remuneracao13SalarioFinal;
    }

    public void setRemuneracao13SalarioFinal(String remuneracao13SalarioFinal) {
        this.remuneracao13SalarioFinal = remuneracao13SalarioFinal;
    }

    public String getMesPagamento13SalarioFinal() {
        return mesPagamento13SalarioFinal;
    }

    public void setMesPagamento13SalarioFinal(String mesPagamento13SalarioFinal) {
        this.mesPagamento13SalarioFinal = mesPagamento13SalarioFinal;
    }

    public String getRacaCor() {
        return racaCor;
    }

    public void setRacaCor(String racaCor) {
        this.racaCor = racaCor;
    }

    public String getIndicadorDeficiencia() {
        return indicadorDeficiencia;
    }

    public void setIndicadorDeficiencia(String indicadorDeficiencia) {
        this.indicadorDeficiencia = indicadorDeficiencia;
    }

    public String getTipoDeficiencia() {
        return tipoDeficiencia;
    }

    public void setTipoDeficiencia(String tipoDeficiencia) {
        this.tipoDeficiencia = tipoDeficiencia;
    }

    public String getIndicadorAlvara() {
        return indicadorAlvara;
    }

    public void setIndicadorAlvara(String indicadorAlvara) {
        this.indicadorAlvara = indicadorAlvara;
    }

    public String getAvisoPrevioIndenizado() {
        return avisoPrevioIndenizado;
    }

    public void setAvisoPrevioIndenizado(String avisoPrevioIndenizado) {
        this.avisoPrevioIndenizado = avisoPrevioIndenizado;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getMotivoPrimeiroAfastamento() {
        return motivoPrimeiroAfastamento;
    }

    public void setMotivoPrimeiroAfastamento(String motivoPrimeiroAfastamento) {
        this.motivoPrimeiroAfastamento = motivoPrimeiroAfastamento;
    }

    public String getDataInicioPrimeiroAfastamento() {
        return dataInicioPrimeiroAfastamento;
    }

    public void setDataInicioPrimeiroAfastamento(String dataInicioPrimeiroAfastamento) {
        this.dataInicioPrimeiroAfastamento = dataInicioPrimeiroAfastamento;
    }

    public String getDataFinalPrimeiroAfastamento() {
        return dataFinalPrimeiroAfastamento;
    }

    public void setDataFinalPrimeiroAfastamento(String dataFinalPrimeiroAfastamento) {
        this.dataFinalPrimeiroAfastamento = dataFinalPrimeiroAfastamento;
    }

    public String getMotivoSegundoAfastamento() {
        return motivoSegundoAfastamento;
    }

    public void setMotivoSegundoAfastamento(String motivoSegundoAfastamento) {
        this.motivoSegundoAfastamento = motivoSegundoAfastamento;
    }

    public String getDataInicioSegundoAfastamento() {
        return dataInicioSegundoAfastamento;
    }

    public void setDataInicioSegundoAfastamento(String dataInicioSegundoAfastamento) {
        this.dataInicioSegundoAfastamento = dataInicioSegundoAfastamento;
    }

    public String getDataFinalSegundoAfastamento() {
        return dataFinalSegundoAfastamento;
    }

    public void setDataFinalSegundoAfastamento(String dataFinalSegundoAfastamento) {
        this.dataFinalSegundoAfastamento = dataFinalSegundoAfastamento;
    }

    public String getMotivoTerceiroAfastamento() {
        return motivoTerceiroAfastamento;
    }

    public void setMotivoTerceiroAfastamento(String motivoTerceiroAfastamento) {
        this.motivoTerceiroAfastamento = motivoTerceiroAfastamento;
    }

    public String getDataInicioTerceiroAfastamento() {
        return dataInicioTerceiroAfastamento;
    }

    public void setDataInicioTerceiroAfastamento(String dataInicioTerceiroAfastamento) {
        this.dataInicioTerceiroAfastamento = dataInicioTerceiroAfastamento;
    }

    public String getDataFinalTerceiroAfastamento() {
        return dataFinalTerceiroAfastamento;
    }

    public void setDataFinalTerceiroAfastamento(String dataFinalTerceiroAfastamento) {
        this.dataFinalTerceiroAfastamento = dataFinalTerceiroAfastamento;
    }

    public String getQuantidadeDiasAfastamento() {
        return quantidadeDiasAfastamento;
    }

    public void setQuantidadeDiasAfastamento(String quantidadeDiasAfastamento) {
        this.quantidadeDiasAfastamento = quantidadeDiasAfastamento;
    }

    public String getValorFeriasIndenizadas() {
        return valorFeriasIndenizadas;
    }

    public void setValorFeriasIndenizadas(String valorFeriasIndenizadas) {
        this.valorFeriasIndenizadas = valorFeriasIndenizadas;
    }

    public String getValorBancoDeHoras() {
        return valorBancoDeHoras;
    }

    public void setValorBancoDeHoras(String valorBancoDeHoras) {
        this.valorBancoDeHoras = valorBancoDeHoras;
    }

    public String getQuantidadeMesesBancoDeHoras() {
        return quantidadeMesesBancoDeHoras;
    }

    public void setQuantidadeMesesBancoDeHoras(String quantidadeMesesBancoDeHoras) {
        this.quantidadeMesesBancoDeHoras = quantidadeMesesBancoDeHoras;
    }

    public String getValorDissidioColetivo() {
        return valorDissidioColetivo;
    }

    public void setValorDissidioColetivo(String valorDissidioColetivo) {
        this.valorDissidioColetivo = valorDissidioColetivo;
    }

    public String getQuantidadeMesesDissidioColetivo() {
        return quantidadeMesesDissidioColetivo;
    }

    public void setQuantidadeMesesDissidioColetivo(String quantidadeMesesDissidioColetivo) {
        this.quantidadeMesesDissidioColetivo = quantidadeMesesDissidioColetivo;
    }

    public String getValorGratificacoes() {
        return valorGratificacoes;
    }

    public void setValorGratificacoes(String valorGratificacoes) {
        this.valorGratificacoes = valorGratificacoes;
    }

    public String getQuantidadeMesesGratificacoes() {
        return quantidadeMesesGratificacoes;
    }

    public void setQuantidadeMesesGratificacoes(String quantidadeMesesGratificacoes) {
        this.quantidadeMesesGratificacoes = quantidadeMesesGratificacoes;
    }

    public String getValorMultaRescisaoSemJustaCausa() {
        return valorMultaRescisaoSemJustaCausa;
    }

    public void setValorMultaRescisaoSemJustaCausa(String valorMultaRescisaoSemJustaCausa) {
        this.valorMultaRescisaoSemJustaCausa = valorMultaRescisaoSemJustaCausa;
    }

    public String getContribuicaoAssociativa1OcorrenciaCNPJ() {
        return contribuicaoAssociativa1OcorrenciaCNPJ;
    }

    public void setContribuicaoAssociativa1OcorrenciaCNPJ(String contribuicaoAssociativa1OcorrenciaCNPJ) {
        this.contribuicaoAssociativa1OcorrenciaCNPJ = contribuicaoAssociativa1OcorrenciaCNPJ;
    }

    public String getContribuicaoAssociativa1OcorrenciaValor() {
        return contribuicaoAssociativa1OcorrenciaValor;
    }

    public void setContribuicaoAssociativa1OcorrenciaValor(String contribuicaoAssociativa1OcorrenciaValor) {
        this.contribuicaoAssociativa1OcorrenciaValor = contribuicaoAssociativa1OcorrenciaValor;
    }

    public String getContribuicaoAssociativa2OcorrenciaCNPJ() {
        return contribuicaoAssociativa2OcorrenciaCNPJ;
    }

    public void setContribuicaoAssociativa2OcorrenciaCNPJ(String contribuicaoAssociativa2OcorrenciaCNPJ) {
        this.contribuicaoAssociativa2OcorrenciaCNPJ = contribuicaoAssociativa2OcorrenciaCNPJ;
    }

    public String getContribuicaoAssociativa2OcorrenciaValor() {
        return contribuicaoAssociativa2OcorrenciaValor;
    }

    public void setContribuicaoAssociativa2OcorrenciaValor(String contribuicaoAssociativa2OcorrenciaValor) {
        this.contribuicaoAssociativa2OcorrenciaValor = contribuicaoAssociativa2OcorrenciaValor;
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

    public String getMunicipioTrabalho() {
        return municipioTrabalho;
    }

    public void setMunicipioTrabalho(String municipioTrabalho) {
        this.municipioTrabalho = municipioTrabalho;
    }

    public String getHorasExtrasJaneiro() {
        return horasExtrasJaneiro;
    }

    public void setHorasExtrasJaneiro(String horasExtrasJaneiro) {
        this.horasExtrasJaneiro = horasExtrasJaneiro;
    }

    public String getHorasExtrasFevereiro() {
        return horasExtrasFevereiro;
    }

    public void setHorasExtrasFevereiro(String horasExtrasFevereiro) {
        this.horasExtrasFevereiro = horasExtrasFevereiro;
    }

    public String getHorasExtrasMarco() {
        return horasExtrasMarco;
    }

    public void setHorasExtrasMarco(String horasExtrasMarco) {
        this.horasExtrasMarco = horasExtrasMarco;
    }

    public String getHorasExtrasAbril() {
        return horasExtrasAbril;
    }

    public void setHorasExtrasAbril(String horasExtrasAbril) {
        this.horasExtrasAbril = horasExtrasAbril;
    }

    public String getHorasExtrasMaio() {
        return horasExtrasMaio;
    }

    public void setHorasExtrasMaio(String horasExtrasMaio) {
        this.horasExtrasMaio = horasExtrasMaio;
    }

    public String getHorasExtrasJunho() {
        return horasExtrasJunho;
    }

    public void setHorasExtrasJunho(String horasExtrasJunho) {
        this.horasExtrasJunho = horasExtrasJunho;
    }

    public String getHorasExtrasJulho() {
        return horasExtrasJulho;
    }

    public void setHorasExtrasJulho(String horasExtrasJulho) {
        this.horasExtrasJulho = horasExtrasJulho;
    }

    public String getHorasExtrasAgosto() {
        return horasExtrasAgosto;
    }

    public void setHorasExtrasAgosto(String horasExtrasAgosto) {
        this.horasExtrasAgosto = horasExtrasAgosto;
    }

    public String getHorasExtrasSetembro() {
        return horasExtrasSetembro;
    }

    public void setHorasExtrasSetembro(String horasExtrasSetembro) {
        this.horasExtrasSetembro = horasExtrasSetembro;
    }

    public String getHorasExtrasOutubro() {
        return horasExtrasOutubro;
    }

    public void setHorasExtrasOutubro(String horasExtrasOutubro) {
        this.horasExtrasOutubro = horasExtrasOutubro;
    }

    public String getHorasExtrasNovembro() {
        return horasExtrasNovembro;
    }

    public void setHorasExtrasNovembro(String horasExtrasNovembro) {
        this.horasExtrasNovembro = horasExtrasNovembro;
    }

    public String getHorasExtrasDezembro() {
        return horasExtrasDezembro;
    }

    public void setHorasExtrasDezembro(String horasExtrasDezembro) {
        this.horasExtrasDezembro = horasExtrasDezembro;
    }

    public String getIndicadorEmpregadoFiliadoSindicato() {
        return indicadorEmpregadoFiliadoSindicato;
    }

    public void setIndicadorEmpregadoFiliadoSindicato(String indicadorEmpregadoFiliadoSindicato) {
        this.indicadorEmpregadoFiliadoSindicato = indicadorEmpregadoFiliadoSindicato;
    }

    public String getInformacaoUsoExclusivoEmpresa() {
        return informacaoUsoExclusivoEmpresa;
    }

    public void setInformacaoUsoExclusivoEmpresa(String informacaoUsoExclusivoEmpresa) {
        this.informacaoUsoExclusivoEmpresa = informacaoUsoExclusivoEmpresa;
    }

    public String getIdentificadorAprendizGravida() {
        return identificadorAprendizGravida;
    }

    public void setIdentificadorAprendizGravida(String identificadorAprendizGravida) {
        this.identificadorAprendizGravida = identificadorAprendizGravida;
    }

    public String getIdenficadorTrabalhoParcial() {
        return idenficadorTrabalhoParcial;
    }

    public void setIdenficadorTrabalhoParcial(String idenficadorTrabalhoParcial) {
        this.idenficadorTrabalhoParcial = idenficadorTrabalhoParcial;
    }

    public String getIdentificadorTeleTrabalho() {
        return identificadorTeleTrabalho;
    }

    public void setIdentificadorTeleTrabalho(String identificadorTeleTrabalho) {
        this.identificadorTeleTrabalho = identificadorTeleTrabalho;
    }

    public String getIdentificadorTrabalhoIntermitente() {
        return identificadorTrabalhoIntermitente;
    }

    public void setIdentificadorTrabalhoIntermitente(String identificadorTrabalhoIntermitente) {
        this.identificadorTrabalhoIntermitente = identificadorTrabalhoIntermitente;
    }

    public String getMatriculaContrato() {
        return matriculaContrato;
    }

    public void setMatriculaContrato(String matriculaContrato) {
        this.matriculaContrato = matriculaContrato;
    }

    public String getCategoriaTrabalhador() {
        return categoriaTrabalhador;
    }

    public void setCategoriaTrabalhador(String categoriaTrabalhador) {
        this.categoriaTrabalhador = categoriaTrabalhador;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getSequenciaRegistro());
        sb.append(this.getInscricaoCNPJCEI());
        sb.append(this.getPrefixo());
        sb.append(this.getTipoRegistro());
        sb.append(this.getCodigoPisPasep());
        sb.append(this.getNomeEmpregado());
        sb.append(this.getDataNascimento());
        sb.append(this.getNacionalidade());
        sb.append(this.getAnoChegadaAoPais());
        sb.append(this.getGrauInstrucao());
        sb.append(this.getCpf());
        sb.append(this.getCtpsNumero());
        sb.append(this.getCtpsSerie());
        sb.append(this.getDataAdmissaoTransferencia());
        sb.append(this.getTipoAdmissao());
        sb.append(this.getSalarioContratual());
        sb.append(this.getTipoSalarioContratual());
        sb.append(this.getHorasSemanais());
        sb.append(this.getCbo());
        sb.append(this.getVinculoEmpregaticio());
        sb.append(this.getCodigoDesligamento());
        sb.append(this.getDataDesligamento());
        sb.append(this.getRemuneracaoJaneiro());
        sb.append(this.getRemuneracaoFevereiro());
        sb.append(this.getRemuneracaoMarco());
        sb.append(this.getRemuneracaoAbril());
        sb.append(this.getRemuneracaoMaio());
        sb.append(this.getRemuneracaoJunho());
        sb.append(this.getRemuneracaoJulho());
        sb.append(this.getRemuneracaoAgosto());
        sb.append(this.getRemuneracaoSetembro());
        sb.append(this.getRemuneracaoOutubro());
        sb.append(this.getRemuneracaoNovembro());
        sb.append(this.getRemuneracaoDezembro());
        sb.append(this.getRemuneracao13SalarioAdiantamento());
        sb.append(this.getMesPagamento13SalarioAdiantamento());
        sb.append(this.getRemuneracao13SalarioFinal());
        sb.append(this.getMesPagamento13SalarioFinal());
        sb.append(this.getRacaCor());
        sb.append(this.getIndicadorDeficiencia());
        sb.append(this.getTipoDeficiencia());
        sb.append(this.getIndicadorAlvara());
        sb.append(this.getAvisoPrevioIndenizado());
        sb.append(this.getSexo());
        sb.append(this.getMotivoPrimeiroAfastamento());
        sb.append(this.getDataInicioPrimeiroAfastamento());
        sb.append(this.getDataFinalPrimeiroAfastamento());
        sb.append(this.getMotivoSegundoAfastamento());
        sb.append(this.getDataInicioSegundoAfastamento());
        sb.append(this.getDataFinalSegundoAfastamento());
        sb.append(this.getMotivoTerceiroAfastamento());
        sb.append(this.getDataInicioTerceiroAfastamento());
        sb.append(this.getDataFinalTerceiroAfastamento());
        sb.append(this.getQuantidadeDiasAfastamento());
        sb.append(this.getValorFeriasIndenizadas());
        sb.append(this.getValorBancoDeHoras());
        sb.append(this.getQuantidadeMesesBancoDeHoras());
        sb.append(this.getValorDissidioColetivo());
        sb.append(this.getQuantidadeMesesDissidioColetivo());
        sb.append(this.getValorGratificacoes());
        sb.append(this.getQuantidadeMesesGratificacoes());
        sb.append(this.getValorMultaRescisaoSemJustaCausa());
        sb.append(this.getContribuicaoAssociativa1OcorrenciaCNPJ());
        sb.append(this.getContribuicaoAssociativa1OcorrenciaValor());
        sb.append(this.getContribuicaoAssociativa2OcorrenciaCNPJ());
        sb.append(this.getContribuicaoAssociativa2OcorrenciaValor());
        sb.append(this.getContribuicaoSindicalCNPJ());
        sb.append(this.getContribuicaoSindicalValor());
        sb.append(this.getContribuicaoAssistencialCNPJ());
        sb.append(this.getContribuicaoAssistencialValor());
        sb.append(this.getContribuicaoConfederativaCNPJ());
        sb.append(this.getContribuicaoConfederativaValor());
        sb.append(this.getMunicipioTrabalho());
        sb.append(this.getHorasExtrasJaneiro());
        sb.append(this.getHorasExtrasFevereiro());
        sb.append(this.getHorasExtrasMarco());
        sb.append(this.getHorasExtrasAbril());
        sb.append(this.getHorasExtrasMaio());
        sb.append(this.getHorasExtrasJunho());
        sb.append(this.getHorasExtrasJulho());
        sb.append(this.getHorasExtrasAgosto());
        sb.append(this.getHorasExtrasSetembro());
        sb.append(this.getHorasExtrasOutubro());
        sb.append(this.getHorasExtrasNovembro());
        sb.append(this.getHorasExtrasDezembro());
        sb.append(this.getIndicadorEmpregadoFiliadoSindicato());
        sb.append(this.getIdentificadorAprendizGravida());
        sb.append(this.getIdenficadorTrabalhoParcial());
        sb.append(this.getIdentificadorTeleTrabalho());
        sb.append(this.getIdentificadorTrabalhoIntermitente());
        sb.append(this.getMatriculaContrato());
        sb.append(this.getCategoriaTrabalhador());
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
