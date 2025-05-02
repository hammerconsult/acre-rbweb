package br.com.webpublico.negocios.rh.previdencia;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.previdencia.ExportacaoPrevidenciaComplementarBBPrev;
import br.com.webpublico.entidades.rh.previdencia.ItemPrevidenciaComplementar;
import br.com.webpublico.entidadesauxiliares.rh.previdencia.PercentualContribuicaoPrevidenciaComplementarVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.previdencia.TipoEnvioBBPrev;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Stateless
public class ExportacaoPrevidenciaComplementarBBPrevFacade extends AbstractFacade<ExportacaoPrevidenciaComplementarBBPrev> {
    private static final String CODIGO_DO_PLANO = "0141";
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;
    @EJB
    private ReintegracaoFacade reintegracaoFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private PrevidenciaComplementarFacade previdenciaComplementarFacade;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private DependenteFacade dependenteFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;


    public ExportacaoPrevidenciaComplementarBBPrevFacade() {
        super(ExportacaoPrevidenciaComplementarBBPrev.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public ExportacaoPrevidenciaComplementarBBPrev gerarArquivo(ExportacaoPrevidenciaComplementarBBPrev exportacao, List<ContratoFP> contratos, AssistenteBarraProgresso assistenteBarraProgresso) throws IOException {
        ByteArrayOutputStream arquivoRetorno = new ByteArrayOutputStream();
        Arquivo arquivo = new Arquivo();
        arquivo.montarImputStream();
        InputStreamReader streamReader = new InputStreamReader(arquivo.getInputStream());
        arquivoRetorno.write(montarArquivo(exportacao, contratos, assistenteBarraProgresso).getBytes());
        exportacao = salvarArquivoGerado(exportacao, arquivoRetorno);
        arquivoRetorno.close();
        streamReader.close();
        arquivo.getInputStream().close();
        return exportacao;
    }

    private ExportacaoPrevidenciaComplementarBBPrev salvarArquivoGerado(ExportacaoPrevidenciaComplementarBBPrev exportacao, ByteArrayOutputStream outputStream) {
        try {
            InputStream is = new ByteArrayInputStream(outputStream.toByteArray());

            Arquivo arquivo = new Arquivo();
            arquivo.setDescricao(montarNomeArquivo(exportacao) + ".dat");
            arquivo.setMimeType("text/plain");
            arquivo.setNome(montarNomeArquivo(exportacao) + ".dat");

            exportacao.setArquivo(arquivoFacade.retornaArquivoSalvo(arquivo, is));
            is.close();
            exportacao.setDataGeracao(new Date());
            return salvarRetornando(exportacao);
        } catch (Exception e) {
            logger.error("Erro ao gerar arquivo da previdência complementar  {}", e);
        }
        return null;
    }

    private String montarNomeArquivo(ExportacaoPrevidenciaComplementarBBPrev exportacao) {
        StringBuilder sb = new StringBuilder();
        sb.append(exportacao.getTipoEnvio().getCodigo());
        sb.append(StringUtil.cortarOuCompletarEsquerda(exportacao.getPatrocinador().getEntidade().getCodigoPatrocinadora(), 5, "0"));
        sb.append(StringUtil.cortarOuCompletarEsquerda(exportacao.getPatrocinador().getEntidade().getCnpb(), 10, "0"));
        sb.append(exportacao.getAno()).append(StringUtil.cortarOuCompletarEsquerda(exportacao.getMes() + "", 2, "0"));
        return sb.toString();
    }

    private String montarArquivo(ExportacaoPrevidenciaComplementarBBPrev exportacao, List<ContratoFP> contratos, AssistenteBarraProgresso assistenteBarraProgresso) {
        Integer totalRegistroDadosCadastrais = 0;
        Integer totalRegistroPlanosVinculados = 0;
        Integer totalRegistroPercentuaisContribuicoesIndividuais = 0;
        Integer totalRegistroDependente = 0;
        Date dataUltimoDiaCompetencia = DataUtil.criarDataUltimoDiaMes(exportacao.getMes(), exportacao.getAno()).toDate();
        StringBuilder sb = new StringBuilder();
        sb.append(montarHeader(exportacao));

        StringBuilder registroDadosCadastrais = new StringBuilder();
        StringBuilder registroPlanosVinculados = new StringBuilder();
        StringBuilder registroPercentuaisContribuicoesIndividuais = new StringBuilder();
        StringBuilder registroDependente = new StringBuilder();
        for (ContratoFP contrato : contratos) {
            registroDadosCadastrais.append(montarDadosCadastrais(exportacao, contrato, dataUltimoDiaCompetencia));
            registroPlanosVinculados.append(montarPlanosVinculados(exportacao, contrato));

            List<PercentualContribuicaoPrevidenciaComplementarVO> percentuais = buscarPercentuaisContribuicaoParaServidor(contrato, exportacao.getMes(), exportacao.getAno());
            totalRegistroPercentuaisContribuicoesIndividuais += percentuais.size();
            registroPercentuaisContribuicoesIndividuais.append(montarPercentuaisContribuicaoIndividual(exportacao, contrato, percentuais));
            List<Dependente> dependentes = dependenteFacade.dependentesPorResponsavel(contrato.getMatriculaFP().getPessoa());
            if (dependentes != null && !dependentes.isEmpty()) {
                registroDependente.append(montarDependentes(contrato, dependentes, dataUltimoDiaCompetencia));
                totalRegistroDependente += dependentes.size();
            }
            totalRegistroDadosCadastrais++;
            totalRegistroPlanosVinculados++;
            assistenteBarraProgresso.conta();
        }
        sb.append(registroDadosCadastrais);
        sb.append(registroPlanosVinculados);
        sb.append(registroPercentuaisContribuicoesIndividuais);
        sb.append(registroDependente);
        sb.append(montarTotalizadores(totalRegistroDadosCadastrais, totalRegistroPlanosVinculados, totalRegistroPercentuaisContribuicoesIndividuais, totalRegistroDependente));
        return sb.toString();
    }

    private String montarHeader(ExportacaoPrevidenciaComplementarBBPrev exportacao) {
        StringBuilder sb = new StringBuilder();
        sb.append("1");
        sb.append(StringUtil.cortarOuCompletarEsquerda(exportacao.getPatrocinador().getEntidade().getCodigoPatrocinadora(), 5, "0"));
        sb.append(exportacao.getAno()).append(StringUtil.cortarOuCompletarEsquerda(exportacao.getMes() + "", 2, "0"));
        sb.append(StringUtil.cortarOuCompletarEsquerda("0", 588, "0")).append("\n");
        return sb.toString();
    }

    private String montarDadosCadastrais(ExportacaoPrevidenciaComplementarBBPrev exportacao, ContratoFP contratoFP, Date dataUltimoDiaCompetencia) {
        PessoaFisica pf = pessoaFisicaFacade.recuperar(contratoFP.getMatriculaFP().getPessoa().getId());
        StringBuilder sb = new StringBuilder();
        //TIPO DE REGISTRO
        sb.append("2");
        //MATRÍCULA DO PARTICIPANTE
        sb.append(StringUtil.cortarOuCompletarEsquerda(contratoFP.getMatriculaFP().getMatricula(), 7, "0"));
        sb.append(StringUtil.cortarOuCompletarEsquerda(contratoFP.getNumero(), 2, "0"));
        //NOME
        sb.append(StringUtil.cortaOuCompletaDireita(pf.getNome(), 40, " "));
        //CÓDIGO DA LOTAÇÃO
        HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(new Date(), contratoFP.getLotacaoFuncionalVigente().getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        sb.append(StringUtil.cortaOuCompletaDireita((ho != null ? StringUtil.removeCaracteresEspeciaisSemEspaco(ho.getCodigo()) : ""), 10, "0"));
        //CÓDIGO DO CARGO
        sb.append(StringUtil.cortarOuCompletarEsquerda("0", 8, "0"));
        //CÓDIGO DA FUNÇÃO EXERCIDA
        sb.append(StringUtil.cortarOuCompletarEsquerda(contratoFP.getCargo().getCodigoDoCargo().trim(), 5, "0"));
        //DATA DE ADMISSÃO
        sb.append(DataUtil.getDataFormatada(contratoFP.getInicioVigencia(), "yyyyMMdd"));
        //DATA DE DEMISSÃO E MOTIVO DE DEMISSÃO
        ExoneracaoRescisao exoneracaoRescisao = exoneracaoRescisaoFacade.buscarExoneracaoRescisaoContratoNotReintegracao(contratoFP.getId());
        if (exoneracaoRescisao != null) {
            sb.append(DataUtil.getDataFormatada(exoneracaoRescisao.getDataRescisao(), "yyyyMMdd"));
            sb.append(StringUtil.cortarOuCompletarEsquerda(exoneracaoRescisao.getMotivoExoneracaoRescisao().getTipoMotivoDemissaoBBPrev() != null ? exoneracaoRescisao.getMotivoExoneracaoRescisao().getTipoMotivoDemissaoBBPrev().getCodigo() : "", 2, " "));
        } else {
            sb.append(StringUtil.cortarOuCompletarEsquerda("", 8, "0"));
            sb.append(StringUtil.cortarOuCompletarEsquerda("", 2, " "));
        }
        //SITUAÇÃO NA EMPRESA E DATA DA SITUAÇÃO NA EMPRESA
        if (TipoEnvioBBPrev.PROPOSTA_ADESAO.equals(exportacao.getTipoEnvio())) {
            sb.append("01");
            sb.append(StringUtil.cortarOuCompletarEsquerda("", 8, "0"));
        } else {
            sb.append(buscarSituacaoNaEmpresa(exportacao, contratoFP));
        }
        //SEXO
        sb.append(pf.getSexo() != null ? pf.getSexo().getSigla() : " ");
        //ESTADO CIVIL
        sb.append(pf.getEstadoCivil() != null ? pf.getEstadoCivil().getCodigoSiprev() : "0");
        //GRAU DE INSTRUÇÃO
        sb.append(pf.getNivelEscolaridade() != null && pf.getNivelEscolaridade().getGrauInstrucaoBBPrev() != null ? pf.getNivelEscolaridade().getGrauInstrucaoBBPrev().getCodigo() : "0");

        EnderecoCorreio endereco = pf.getEnderecoPrincipal() != null ? pf.getEnderecoPrincipal() : (pf.getEnderecos() != null && !pf.getEnderecos().isEmpty() ? pf.getEnderecos().get(0) : null);
        if (endereco != null) {
            //ENDEREÇO RESIDENCIAL
            sb.append(StringUtil.cortaOuCompletaDireita(endereco.getLogradouro() != null ? endereco.getLogradouro() : "", 40, " "));
            //BAIRRO
            sb.append(StringUtil.cortaOuCompletaDireita(endereco.getNumero() != null ? endereco.getNumero() : "", 6, " "));
            //COMPLEMENTO DO ENDEREÇO
            sb.append(StringUtil.cortaOuCompletaDireita(endereco.getComplemento() != null ? endereco.getComplemento() : "", 30, " "));
            //BAIRRO
            sb.append(StringUtil.cortaOuCompletaDireita(endereco.getBairro() != null ? endereco.getBairro() : "", 20, " "));
            //CIDADE
            sb.append(StringUtil.cortaOuCompletaDireita(endereco.getLocalidade() != null ? endereco.getLocalidade() : "", 20, " "));
            //UF
            sb.append(StringUtil.cortaOuCompletaDireita(endereco.getUf() != null ? endereco.getUf() : "", 2, " "));
            //CEP
            sb.append(StringUtil.cortaOuCompletaDireita(endereco.getCep() != null ? StringUtil.removeCaracteresEspeciaisSemEspaco(endereco.getCep()) : "", 8, " "));
        } else {
            sb.append(StringUtil.cortaOuCompletaDireita("", 126, " "));
        }
        //TELEFONES
        sb.append(buscarTelefones(pf));
        //CIDADE DE NASCIMENTO
        sb.append(StringUtil.cortaOuCompletaDireita(pf.getNaturalidade() != null ? pf.getNaturalidade().getNome() : "", 20, " "));
        //UF DE NASCIMENTO
        sb.append(StringUtil.cortaOuCompletaDireita(pf.getNaturalidade() != null ? pf.getNaturalidade().getUf().getUf() : "", 2, " "));
        //CÓDIGO DA NACIONALIDADE
        sb.append(StringUtil.cortaOuCompletaDireita(pf.getNacionalidade() != null && pf.getNacionalidade().getNacionalidadeBBPrev() != null ? pf.getNacionalidade().getNacionalidadeBBPrev().getCodigo() : "", 2, " "));
        //DATA DE NASCIMENTO
        sb.append(DataUtil.getDataFormatada(pf.getDataNascimento(), "yyyyMMdd"));
        //NÚMERO DO CPF
        sb.append(StringUtil.cortarOuCompletarEsquerda(StringUtil.retornaApenasNumeros(pf.getCpf()), 11, "0"));

        RG rg = pf.getRg();
        if (rg != null) {
            //NÚMERO DA CARTEIRA DE IDENTIDADE
            sb.append(StringUtil.cortaOuCompletaDireita(rg.getNumero() != null ? StringUtil.retornaApenasNumeros(rg.getNumero()) : "", 12, " "));
            //ÓRGÃO DE EMISSÃO
            sb.append(StringUtil.cortaOuCompletaDireita(rg.getOrgaoEmissao() != null ? rg.getOrgaoEmissao() : "", 8, " "));
            //UF DO ÓRGÃO DE EMISSÃO
            sb.append(StringUtil.cortaOuCompletaDireita(rg.getUf() != null ? rg.getUf().getUf() : "", 2, " "));
            //DATA DE EMISSÃO
            sb.append(StringUtil.cortaOuCompletaDireita(rg.getDataemissao() != null ? DataUtil.getDataFormatada(rg.getDataemissao(), "yyyyMMdd") : "", 8, " "));
        } else {
            sb.append(StringUtil.cortaOuCompletaDireita("", 30, " "));
        }
        //FORMA DE RECOLHIMENTO DAS CONTRIBUIÇÕES
        sb.append("0");
        //NÚMERO DO BANCO
        sb.append(StringUtil.cortaOuCompletaDireita(contratoFP.getContaCorrente() != null ? contratoFP.getContaCorrente().getBanco().getNumeroBanco() : "", 3, " "));
        //CÓDIGO DA AGÊNCIA
        sb.append(StringUtil.cortaOuCompletaDireita(contratoFP.getContaCorrente() != null ? contratoFP.getContaCorrente().getAgencia().getNumeroAgencia() : "", 5, " "));
        //DÍGITO VERIFICADOR DA AGÊNCIA
        sb.append(StringUtil.cortaOuCompletaDireita(contratoFP.getContaCorrente() != null ? contratoFP.getContaCorrente().getAgencia().getDigitoVerificador() : "", 1, " "));
        //CONTA CORRENTE
        sb.append(StringUtil.cortaOuCompletaDireita(contratoFP.getContaCorrente() != null ? contratoFP.getContaCorrente().getNumeroConta() : "", 11, " "));
        //DÍGITO VERIFICADOR DA CONTA CORRENTE
        sb.append(StringUtil.cortaOuCompletaDireita(contratoFP.getContaCorrente() != null ? contratoFP.getContaCorrente().getDigitoVerificador() : "", 1, " "));
        //NOME DO PAI
        sb.append(StringUtil.cortaOuCompletaDireita(pf.getPai() != null ? pf.getPai() : "", 30, " "));
        //NOME DA MÃE
        sb.append(StringUtil.cortaOuCompletaDireita(pf.getMae() != null ? pf.getMae() : "", 30, " "));
        //E-MAIL
        sb.append(StringUtil.cortaOuCompletaDireita(pf.getEmail() != null ? pf.getEmail() : "", 60, " "));
        //OPÇÃO PELO REGIME DE TRIBUTAÇÃO
        List<ItemPrevidenciaComplementar> itensPrevidenciaComplementar = previdenciaComplementarFacade.buscarItemPrevidenciaComplementarPorContrato(contratoFP, dataUltimoDiaCompetencia);
        sb.append(itensPrevidenciaComplementar != null && itensPrevidenciaComplementar.get(0).getRegimeTributacaoBBPrev() != null ? itensPrevidenciaComplementar.get(0).getRegimeTributacaoBBPrev().getCodigo() : " ");
        //OPÇÃO DE INVESTIMENTO
        sb.append("0");
        //SITUAÇÃO NA ADMISSÃO
        sb.append(contratoFP.getSituacaoAdmissaoBBPrev().getCodigo());

        //VALOR DOS RENDIMENTOS MENSAIS
        BigDecimal valorRendimentos = buscarValorRendimentosMensais(exportacao.getMes(), exportacao.getAno(), contratoFP);
        if (valorRendimentos != null && valorRendimentos.compareTo(BigDecimal.ZERO) > 0) {
            String[] valor = valorRendimentos.toString().split("\\.");
            sb.append(StringUtil.cortarOuCompletarEsquerda(valor.length >= 1 ? valor[0] : valorRendimentos.toString(), 9, "0"));
            sb.append(StringUtil.cortaOuCompletaDireita(valor.length > 1 ? valor[1] : "0", 2, "0"));
        } else {
            sb.append(StringUtil.cortarOuCompletarEsquerda("", 11, "0"));
        }
        //VALOR DO PATRIMÔNIO BRUTO
        sb.append(StringUtil.cortarOuCompletarEsquerda("", 11, "0"));
        //DATA DE PROTOCOLO
        sb.append(StringUtil.cortarOuCompletarEsquerda("", 8, "0"));
        //INDICADOR DE TIPO DE FUNCIONÁRIO
        sb.append("P");
        //TIPO DE ADESÃO
        sb.append("AA");
        //RESERVADO
        sb.append(StringUtil.cortarOuCompletarEsquerda("", 59, "0"));
        return sb.append("\n").toString();
    }

    private String montarPlanosVinculados(ExportacaoPrevidenciaComplementarBBPrev exportacao, ContratoFP contratoFP) {
        StringBuilder sb = new StringBuilder();
        //TIPO DE REGISTRO
        sb.append("3");
        //MATRÍCULA DO PARTICIPANTE
        sb.append(StringUtil.cortarOuCompletarEsquerda(contratoFP.getMatriculaFP().getMatricula(), 7, "0"));
        sb.append(StringUtil.cortarOuCompletarEsquerda(contratoFP.getNumero(), 2, "0"));
        //CÓDIGO DO PLANO
        sb.append(CODIGO_DO_PLANO);
        //DATA DE INSCRIÇÃO NO PLANO
        sb.append("00000000");
        //SITUAÇÃO NO PLANO
        sb.append(TipoEnvioBBPrev.PROPOSTA_ADESAO.equals(exportacao.getTipoEnvio()) ? "01" : "00");
        //DATA DE SITUAÇÃO NO PLANO
        sb.append("00000000");
        //RESERVADO
        sb.append(StringUtil.cortarOuCompletarEsquerda("0", 568, "0"));
        return sb.append("\n").toString();
    }

    private String montarPercentuaisContribuicaoIndividual(ExportacaoPrevidenciaComplementarBBPrev exportacao, ContratoFP contratoFP, List<PercentualContribuicaoPrevidenciaComplementarVO> percentuais) {
        StringBuilder sb = new StringBuilder();

        for (PercentualContribuicaoPrevidenciaComplementarVO percentual : percentuais) {
            //TIPO DE REGISTRO
            sb.append("4");
            //ANO DE REFERÊNCIA
            sb.append(exportacao.getAno());
            //MÊS DE REFERÊNCIA
            sb.append(StringUtil.cortarOuCompletarEsquerda(exportacao.getMes() + "", 2, "0"));
            //MATRÍCULA DO PARTICIPANTE
            sb.append(StringUtil.cortarOuCompletarEsquerda(contratoFP.getMatriculaFP().getMatricula(), 7, "0"));
            sb.append(StringUtil.cortarOuCompletarEsquerda(contratoFP.getNumero(), 2, "0"));
            //CÓDIGO DO PLANO
            sb.append(CODIGO_DO_PLANO);
            //CÓDIGO DA CONTRIBUIÇÃO
            sb.append(StringUtil.cortarOuCompletarEsquerda(percentual.getCodigo() != null ? percentual.getCodigo() : "00", 2, "0"));
            //PERCENTUAL DE CONTRIBUIÇÃO DO PARTICIPANTE
            //PERCENTUAL DE CONTRIBUIÇÃO DA PATROCINADORA
            sb.append(buscarPercentuaisContribuicao(percentual));
            //RESERVADO
            sb.append(StringUtil.cortarOuCompletarEsquerda("0", 11, "0"));
            //PERCENTUAL DA SEGUNDA FAIXA DE CONTRIBUIÇÃO DO PARTICIPANTE
            sb.append("00000000");
            //RESERVADO
            sb.append(StringUtil.cortarOuCompletarEsquerda("0", 543, "0"));
            sb.append("\n");
        }
        return sb.toString();
    }

    private String montarDependentes(ContratoFP contratoFP, List<Dependente> dependentes, Date dataUltimoDiaCompetencia) {
        StringBuilder sb = new StringBuilder();
        for (Dependente dependente : dependentes) {
            //TIPO DE REGISTRO
            sb.append("5");
            //MATRÍCULA DO PARTICIPANTE
            sb.append(StringUtil.cortarOuCompletarEsquerda(contratoFP.getMatriculaFP().getMatricula(), 7, "0"));
            sb.append(StringUtil.cortarOuCompletarEsquerda(contratoFP.getNumero(), 2, "0"));
            //NOME DO DEPENDENTE
            sb.append(StringUtil.cortaOuCompletaDireita(dependente.getDependente().getNome(), 60, " "));
            //GRAU DE PARENTESCO
            sb.append(StringUtil.cortaOuCompletaDireita(dependente.getGrauDeParentesco() != null && dependente.getGrauDeParentesco().getGrauParentescoBBPrev() != null ? dependente.getGrauDeParentesco().getGrauParentescoBBPrev().getCodigo() : "", 2, " "));
            //SEXO DO DEPENDENTE
            sb.append(StringUtil.cortaOuCompletaDireita(dependente.getDependente().getSexo() != null ? dependente.getDependente().getSexo().getSigla() : "", 1, " "));
            //DATA DE NASCIMENTO DO DEPENDENTE
            sb.append(dependente.getDependente().getDataNascimento() != null ? DataUtil.getDataFormatada(dependente.getDependente().getDataNascimento(), "yyyyMMdd") : "00000000");
            //INVALIDO?
            sb.append(dependenteInvalido(dependente, dataUltimoDiaCompetencia) ? "S" : "N");
            //DEPENDENTE PARA IRRF?
            DependenteVinculoFP dependenteVinculoFP = dependenteFacade.buscarDependenteVinculoFPPorTipo(dependente, "2", dataUltimoDiaCompetencia);
            sb.append(dependenteVinculoFP != null ? "S" : "N");
            //RESERVADO
            sb.append(StringUtil.cortarOuCompletarEsquerda("", 517, "0")).append("\n");
        }
        return sb.toString();
    }

    private String montarTotalizadores(Integer totalRegistroDadosCadastrais, Integer totalRegistroPlanosVinculados, Integer totalRegistroPercentuaisContribuicoesIndividuais, Integer totalRegistroDependente) {
        StringBuilder sb = new StringBuilder();
        //TIPO DE REGISTRO
        sb.append("7");
        //QUANTIDADE DE REGISTRO TIPO 2
        sb.append(StringUtil.cortarOuCompletarEsquerda(totalRegistroDadosCadastrais + "", 6, "0"));
        //QUANTIDADE DE REGISTRO TIPO 3
        sb.append(StringUtil.cortarOuCompletarEsquerda(totalRegistroPlanosVinculados + "", 6, "0"));
        //QUANTIDADE DE REGISTRO TIPO 4
        sb.append(StringUtil.cortarOuCompletarEsquerda(totalRegistroPercentuaisContribuicoesIndividuais + "", 6, "0"));
        //QUANTIDADE DE REGISTRO TIPO 5
        sb.append(StringUtil.cortarOuCompletarEsquerda(totalRegistroDependente + "", 6, "0"));
        //QUANTIDADE DE REGISTRO TIPO 6
        sb.append("000000");
        //RESERVADO
        sb.append(StringUtil.cortarOuCompletarEsquerda("", 569, "0")).append("\n");
        return sb.toString();
    }

    private String buscarPercentuaisContribuicao(PercentualContribuicaoPrevidenciaComplementarVO percentual) {
        StringBuilder sb = new StringBuilder();
        BigDecimal valorServidor = BigDecimal.ZERO;
        BigDecimal valorPatrocinador = BigDecimal.ZERO;
        if (percentual.getValorReferenciaServidor().compareTo(BigDecimal.ZERO) > 0 && percentual.getQuantidadeOcorrenciasServidor() > 0) {
            valorServidor = percentual.getValorReferenciaServidor().divide(BigDecimal.valueOf(percentual.getQuantidadeOcorrenciasServidor()));
        }
        if (percentual.getValorReferenciaPatrocinador().compareTo(BigDecimal.ZERO) > 0 && percentual.getQuantidadeOcorrenciasPatrocinador() > 0) {
            valorPatrocinador = percentual.getValorReferenciaPatrocinador().divide(BigDecimal.valueOf(percentual.getQuantidadeOcorrenciasPatrocinador()));
        }

        if (valorServidor.compareTo(BigDecimal.ZERO) > 0) {
            String[] servidor = valorServidor.toString().split("\\.");
            sb.append(StringUtil.cortarOuCompletarEsquerda(servidor.length >= 1 ? servidor[0] : valorServidor.toString(), 5, "0"));
            sb.append(StringUtil.cortaOuCompletaDireita(servidor.length > 1 ? servidor[1] : "0", 3, "0"));
        } else {
            sb.append("00000000");
        }

        if (valorPatrocinador.compareTo(BigDecimal.ZERO) > 0) {
            String[] patrocinador = valorPatrocinador.toString().split("\\.");
            sb.append(StringUtil.cortarOuCompletarEsquerda(patrocinador.length >= 1 ? patrocinador[0] : valorPatrocinador.toString(), 5, "0"));
            sb.append(StringUtil.cortaOuCompletaDireita(patrocinador.length > 1 ? patrocinador[1] : "0", 3, "0"));
        } else {
            sb.append("00000000");
        }

        return sb.toString();
    }

    private boolean dependenteInvalido(Dependente dependente, Date dataOperacao) {
        PessoaFisicaCid cid = dependente.getDependente().getPessoaFisicaCid();
        return cid != null && cid.getInicioVigencia() != null && cid.getInicioVigencia().compareTo(dataOperacao) <= 0 && (cid.getFinalVigencia() == null || cid.getFinalVigencia().compareTo(dataOperacao) >= 0);
    }

    private String buscarSituacaoNaEmpresa(ExportacaoPrevidenciaComplementarBBPrev exportacao, ContratoFP contratoFP) {
        StringBuilder sb = new StringBuilder();
        ExoneracaoRescisao exoneracao = exoneracaoRescisaoFacade.recuperaContratoExonerado(contratoFP, Mes.getMesToInt(exportacao.getMes()), exportacao.getAno());
        if (exoneracao != null) {
            sb.append("05");
            sb.append(StringUtil.cortarOuCompletarEsquerda(DataUtil.getDataFormatada(exoneracao.getDataRescisao(), "yyyyMMdd"), 8, "0"));
            return sb.toString();
        }

        CedenciaContratoFP cedencia = buscarCedenciaNaCompetenciaParaServidor(contratoFP, exportacao.getMes(), exportacao.getAno());
        if (cedencia != null) {
            sb.append("09");
            sb.append(StringUtil.cortarOuCompletarEsquerda(DataUtil.getDataFormatada(cedencia.getDataCessao(), "yyyyMMdd"), 8, "0"));
            return sb.toString();
        }

        List<Afastamento> afastamentos = afastamentoFacade.listaAfastamentosInicioCompetenciaSEFIP(contratoFP, exportacao.getMes(), exportacao.getAno());
        if (afastamentos != null && !afastamentos.isEmpty()) {
            Afastamento afastamento = afastamentos.get(0);
            if (afastamento.getTipoAfastamento().getTipoSituacaoBBPrev() != null) {
                sb.append(afastamento.getTipoAfastamento().getTipoSituacaoBBPrev().getCodigo());
                sb.append(StringUtil.cortarOuCompletarEsquerda(DataUtil.getDataFormatada(afastamento.getInicio(), "yyyyMMdd"), 8, "0"));
                return sb.toString();
            }
        }

        Reintegracao reintegracao = reintegracaoFacade.buscarReintegracaoParaContratoNaCompetencia(contratoFP.getId(), exportacao.getMes(), exportacao.getAno());
        if (reintegracao != null) {
            sb.append("01");
            sb.append(StringUtil.cortarOuCompletarEsquerda(DataUtil.getDataFormatada(reintegracao.getDataReintegracao(), "yyyyMMdd"), 8, "0"));
            return sb.toString();
        }

        RetornoCedenciaContratoFP retornoCedenciaContratoFP = buscarRetornoCedenciaNaCompetenciaParaServidor(contratoFP, exportacao.getMes(), exportacao.getAno());
        if (retornoCedenciaContratoFP != null) {
            sb.append("01");
            sb.append(StringUtil.cortarOuCompletarEsquerda(DataUtil.getDataFormatada(retornoCedenciaContratoFP.getDataRetorno(), "yyyyMMdd"), 8, "0"));
            return sb.toString();
        }

        List<Afastamento> afastamentosRetorno = afastamentoFacade.listaAfastamentosFinalCompetenciaSEFIP(contratoFP, exportacao.getMes(), exportacao.getAno());
        if (afastamentosRetorno != null && !afastamentosRetorno.isEmpty()) {
            Afastamento afastamento = afastamentosRetorno.get(0);
            sb.append("01");
            sb.append(StringUtil.cortarOuCompletarEsquerda(DataUtil.getDataFormatada(afastamento.getTermino(), "yyyyMMdd"), 8, "0"));
            return sb.toString();
        }
        sb.append("01");
        sb.append(exportacao.getAno()).append(StringUtil.cortarOuCompletarEsquerda(exportacao.getMes().toString(), 2, "0")).append("01");
        return sb.toString();
    }

    private String buscarTelefones(PessoaFisica pf) {
        StringBuilder sb = new StringBuilder();
        Map<TipoTelefone, Telefone> matFones = new HashMap<>();
        if (pf.getTelefones() != null) {
            for (Telefone telefone : pf.getTelefones()) {
                matFones.put(telefone.getTipoFone(), telefone);
            }
        }
        Telefone telefoneResidencial = matFones.get(TipoTelefone.RESIDENCIAL);
        Telefone telefoneComercial = matFones.get(TipoTelefone.COMERCIAL);
        Telefone telefoneCelular = matFones.get(TipoTelefone.CELULAR);

        if (telefoneResidencial != null) {
            String fone = StringUtil.retornaApenasNumeros(telefoneResidencial.getTelefone());
            sb.append(StringUtil.cortarOuCompletarEsquerda(fone.length() >= 10 ? fone.substring(0, 2) : "0", 3, "0"));
            sb.append(StringUtil.cortarOuCompletarEsquerda(fone.length() >= 10 ? fone.substring(2, fone.length()) : "0", 17, "0"));
        } else {
            sb.append(StringUtil.cortarOuCompletarEsquerda("0", 20, "0"));
        }

        if (telefoneComercial != null) {
            String fone = StringUtil.retornaApenasNumeros(telefoneComercial.getTelefone());
            sb.append(StringUtil.cortarOuCompletarEsquerda(fone.length() >= 10 ? fone.substring(0, 2) : "0", 3, "0"));
            sb.append(StringUtil.cortarOuCompletarEsquerda(fone.length() >= 10 ? fone.substring(2, fone.length()) : "0", 17, "0"));
        } else {
            sb.append(StringUtil.cortarOuCompletarEsquerda("0", 20, "0"));
        }

        if (telefoneCelular != null) {
            String fone = StringUtil.retornaApenasNumeros(telefoneCelular.getTelefone());
            sb.append(StringUtil.cortarOuCompletarEsquerda(fone.length() >= 10 ? fone.substring(0, 2) : "0", 3, "0"));
            sb.append(StringUtil.cortarOuCompletarEsquerda(fone.length() >= 10 ? fone.substring(2, fone.length()) : "0", 17, "0"));
        } else {
            sb.append(StringUtil.cortarOuCompletarEsquerda("0", 20, "0"));
        }

        return sb.toString();
    }

    public List<ContratoFP> buscarContratosComPrevidenciaComplementarVigenteFiltrandoMatriculaOrNomeOrCPF(String s, Integer mes, Integer ano, List<Long> idsUnidades) {
        String hql = " select distinct new ContratoFP(contrato.id, matricula.matricula||'/'||contrato.numero||' - '||pf.nome||' ('||pf.nomeTratamento||')'||' - '||formatacpfcnpj(pf.cpf)) " +
            " from ItemPrevidenciaComplementar itemPrev " +
            " inner join itemPrev.previdenciaComplementar prev " +
            " inner join prev.contratoFP contrato " +
            " inner join contrato.matriculaFP matricula " +
            " inner join matricula.pessoa pf " +
            " inner join contrato.unidadeOrganizacional un " +
            " where ((lower(pf.nome) like :filtro) or" +
            "           (lower(pf.cpf) like :filtro) or" +
            "           (lower(matricula.matricula) like :filtro)) " +
            " and un.id in (:unidades) " +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(contrato.inicioVigencia,'mm/yyyy'),'mm/yyyy') " +
            "                                                                     and to_date(to_char(coalesce(contrato.finalVigencia, :dataVigencia),'mm/yyyy'),'mm/yyyy') " +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(itemPrev.inicioVigencia,'mm/yyyy'),'mm/yyyy') " +
            "                                                                     and to_date(to_char(coalesce(itemPrev.finalVigencia, :dataVigencia),'mm/yyyy'),'mm/yyyy') ";

        Query q = em.createQuery(hql);
        q.setMaxResults(50);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataVigencia", DataUtil.criarDataPrimeiroDiaMes(mes, ano));
        q.setParameter("unidades", idsUnidades);
        return q.getResultList();
    }

    public CedenciaContratoFP buscarCedenciaNaCompetenciaParaServidor(ContratoFP contratoFP, Integer mes, Integer ano) {
        String sql = "select distinct cedencia.* " +
            " from CedenciaContratoFP cedencia " +
            " where to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') = to_date(to_char(cedencia.dataCessao,'mm/yyyy'),'mm/yyyy')  " +
            "  and cedencia.CONTRATOFP_ID = :contrato  " +
            "  and cedencia.tipoContratoCedenciaFP = :tipoCedencia " +
            "  and ((cedencia.UNIDADEORGANIZACIONAL_ID is not null and not exists(select 1 " +
            "                                                                     from Entidade ent " +
            "                                                                              inner join unidadeOrganizacional ho on ent.ID = ho.ENTIDADE_ID " +
            "                                                                     where (ent.codigoPatrocinadora is not null or " +
            "                                                                            TRIM(ent.codigoPatrocinadora) <> '') " +
            "                                                                       and ho.id = cedencia.UNIDADEORGANIZACIONAL_ID)) or " +
            "       (cedencia.CESSIONARIO_ID is not null and not exists(select 1 " +
            "                                                           from UnidadeExterna cessionario " +
            "                                                                    inner join PESSOAJURIDICA pj on cessionario.PESSOAJURIDICA_ID = pj.ID " +
            "                                                                    inner join ENTIDADE ent on pj.ID = ent.PESSOAJURIDICA_ID " +
            "                                                           where (ent.codigoPatrocinadora is not null or " +
            "                                                                  TRIM(ent.codigoPatrocinadora) <> '') " +
            "                                                             and cessionario.id = cedencia.CESSIONARIO_ID))) " +
            "order by cedencia.dataCessao desc";

        Query q = em.createNativeQuery(sql, CedenciaContratoFP.class);
        q.setParameter("dataVigencia", DataUtil.criarDataPrimeiroDiaMes(mes, ano));
        q.setParameter("contrato", contratoFP);
        q.setParameter("tipoCedencia", TipoCedenciaContratoFP.COM_ONUS.name());
        List resultList = q.getResultList();

        if (resultList != null && !resultList.isEmpty()) {
            return (CedenciaContratoFP) resultList.get(0);
        }
        return null;
    }

    public List<PercentualContribuicaoPrevidenciaComplementarVO> buscarPercentuaisContribuicaoParaServidor(ContratoFP contratoFP, Integer mes, Integer ano) {
        String sql = "select case when ev.TIPOCONTRIBUICAOBBPREV = 'SERVIDOR' then sum(coalesce(itemFicha.VALORREFERENCIA, 0)) end valorReferenciaServidor, " +
            "       case when ev.TIPOCONTRIBUICAOBBPREV = 'PATROCINADOR' " +
            "               then sum(coalesce(itemFicha.VALORREFERENCIA, 0)) end                                          valorReferenciaPatrocinador, " +
            "       case when ev.TIPOCONTRIBUICAOBBPREV = 'SERVIDOR' then count(ev.id) end                   quantidadeOcorrenciasServidor, " +
            "       case when ev.TIPOCONTRIBUICAOBBPREV = 'PATROCINADOR' then count(ev.id) end               quantidadeOcorrenciasPatrocinador," +
            "       ev.CODIGOCONTRIBUICAOBBPREV                                                              codigo " +
            "from ITEMFICHAFINANCEIRAFP itemFicha " +
            "         inner join FICHAFINANCEIRAFP ficha " +
            "                    on itemFicha.FICHAFINANCEIRAFP_ID = ficha.ID " +
            "         inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            "         inner join eventofp ev on itemFicha.EVENTOFP_ID = ev.ID " +
            "where folha.ano = :ano " +
            "  and folha.mes = :mes " +
            "  and ficha.VINCULOFP_ID = :contrato " +
            "  and ev.TIPOCONTRIBUICAOBBPREV is not null " +
            "group by ev.CODIGOCONTRIBUICAOBBPREV, ev.TIPOCONTRIBUICAOBBPREV ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("mes", Mes.getMesToInt(mes).getNumeroMesIniciandoEmZero());
        q.setParameter("ano", ano);
        q.setParameter("contrato", contratoFP.getId());
        List<Object[]> resultado = q.getResultList();

        if (resultado != null && !resultado.isEmpty()) {
            List<PercentualContribuicaoPrevidenciaComplementarVO> percentuais = Lists.newArrayList();
            for (Object[] obj : resultado) {
                PercentualContribuicaoPrevidenciaComplementarVO percentual = new PercentualContribuicaoPrevidenciaComplementarVO();
                percentual.setValorReferenciaServidor(obj[0] != null ? (BigDecimal) obj[0] : BigDecimal.ZERO);
                percentual.setValorReferenciaPatrocinador(obj[1] != null ? (BigDecimal) obj[1] : BigDecimal.ZERO);
                percentual.setQuantidadeOcorrenciasServidor(obj[2] != null ? ((BigDecimal) obj[2]).intValue() : 0);
                percentual.setQuantidadeOcorrenciasPatrocinador(obj[3] != null ? ((BigDecimal) obj[3]).intValue() : 0);
                percentual.setCodigo(obj[4] != null ? (String) obj[4] : "");
                percentuais.add(percentual);
            }
            Map<String, PercentualContribuicaoPrevidenciaComplementarVO> mapPercentuais = new HashMap<>();
            for (PercentualContribuicaoPrevidenciaComplementarVO p : percentuais) {
                PercentualContribuicaoPrevidenciaComplementarVO perc = mapPercentuais.get(p.getCodigo());
                if (perc != null) {
                    perc.setValorReferenciaServidor(perc.getValorReferenciaServidor().add(p.getValorReferenciaServidor()));
                    perc.setValorReferenciaPatrocinador(perc.getValorReferenciaPatrocinador().add(p.getValorReferenciaPatrocinador()));
                    perc.setQuantidadeOcorrenciasServidor(perc.getQuantidadeOcorrenciasServidor() + p.getQuantidadeOcorrenciasServidor());
                    perc.setQuantidadeOcorrenciasPatrocinador(perc.getQuantidadeOcorrenciasPatrocinador() + p.getQuantidadeOcorrenciasPatrocinador());
                    mapPercentuais.put(perc.getCodigo(), perc);
                } else {
                    mapPercentuais.put(p.getCodigo(), p);
                }
            }
            return Lists.newArrayList(mapPercentuais.values());
        }
        return Lists.newArrayList();
    }

    public RetornoCedenciaContratoFP buscarRetornoCedenciaNaCompetenciaParaServidor(ContratoFP contratoFP, Integer mes, Integer ano) {
        String hql = " select distinct retorno from RetornoCedenciaContratoFP retorno " +
            " inner join retorno.cedenciaContratoFP cedencia " +
            " inner join cedencia.unidadeOrganizacional ho " +
            " inner join ho.entidade enti " +
            " where to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') = to_date(to_char(retorno.dataRetorno,'mm/yyyy'),'mm/yyyy') " +
            " and retorno.contratoFP = :contrato " +
            " and cedencia.tipoContratoCedenciaFP = :tipoCedencia " +
            " and (enti.codigoPatrocinadora is null or TRIM(enti.codigoPatrocinadora) = '' ) " +
            " order by retorno.dataRetorno desc ";

        Query q = em.createQuery(hql);
        q.setParameter("dataVigencia", DataUtil.criarDataPrimeiroDiaMes(mes, ano));
        q.setParameter("contrato", contratoFP);
        q.setParameter("tipoCedencia", TipoCedenciaContratoFP.COM_ONUS);
        List resultList = q.getResultList();

        if (resultList != null && !resultList.isEmpty()) {
            return (RetornoCedenciaContratoFP) resultList.get(0);
        }
        return null;
    }

    public List<ContratoFP> buscarContratosParaProposta(Integer mes, Integer ano, List<Long> idsUnidades) {
        String hql = " select distinct contrato " +
            " from ItemPrevidenciaComplementar itemPrev " +
            " inner join itemPrev.previdenciaComplementar prev " +
            " inner join prev.contratoFP contrato " +
            " inner join contrato.unidadeOrganizacional un " +
            " where to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') = to_date(to_char(itemPrev.inicioVigencia,'mm/yyyy'),'mm/yyyy') " +
            "   and un.id in (:unidades) ";

        Query q = em.createQuery(hql);
        q.setParameter("dataVigencia", DataUtil.criarDataPrimeiroDiaMes(mes, ano));
        q.setParameter("unidades", idsUnidades);
        List<ContratoFP> contratosFPs = Lists.newArrayList();
        List<ContratoFP> contratos = q.getResultList();
        for (ContratoFP c : contratos) {
            contratosFPs.add(contratoFPFacade.recuperarContratoComLotacaoFuncional(c.getId()));
        }
        return contratosFPs;
    }

    public List<ContratoFP> buscarContratosParaManutencao(Integer mes, Integer ano, List<Long> idsUnidades) {
        String sql = "select distinct contrato.id from ITEMPREVCOMPLEMENTAR itemPrev " +
            "         inner join previdenciaComplementar prev on itemPrev.PREVIDENCIACOMPLEMENTAR_ID = prev.ID " +
            "         inner join contratoFP contrato on prev.CONTRATOFP_ID = contrato.ID " +
            "         inner join vinculofp v on contrato.ID = v.ID " +
            "         inner join unidadeorganizacional un on v.UNIDADEORGANIZACIONAL_ID = un.ID " +
            " where to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(itemPrev.inicioVigencia,'mm/yyyy'),'mm/yyyy') " +
            "                                                                     and to_date(to_char(coalesce(itemPrev.finalVigencia, :dataVigencia),'mm/yyyy'),'mm/yyyy') " +
            " and un.id in (:unidades)" +
            " and (exists(select er.id " +
            "             from ExoneracaoRescisao er " +
            "             where er.VINCULOFP_ID = contrato.ID " +
            "               and to_date(to_char(:dataVigencia, 'mm/yyyy'), 'mm/yyyy') = to_date(to_char(er.dataRescisao, 'mm/yyyy'), 'mm/yyyy')) " +
            "   or exists(select 2 " +
            "             from CedenciaContratoFP cedencia " +
            "             where to_date(to_char(:dataVigencia, 'mm/yyyy'), 'mm/yyyy') = to_date(to_char(cedencia.dataCessao, 'mm/yyyy'), 'mm/yyyy') " +
            "               and cedencia.CONTRATOFP_ID = contrato.id " +
            "               and cedencia.TIPOCONTRATOCEDENCIAFP = :tipoCedencia " +
            "               and ((cedencia.UNIDADEORGANIZACIONAL_ID is not null and not exists(select 1 " +
            "                                                                       from Entidade ent " +
            "                                                                                inner join unidadeOrganizacional ho on ent.ID = ho.ENTIDADE_ID " +
            "                                                                       where (ent.codigoPatrocinadora is not null or " +
            "                                                                              TRIM(ent.codigoPatrocinadora) <> '') " +
            "                                                                         and ho.id = cedencia.UNIDADEORGANIZACIONAL_ID)) or " +
            "                                                        (cedencia.CESSIONARIO_ID is not null and not exists(select 1 " +
            "                                                             from UnidadeExterna cessionario " +
            "                                                                      inner join PESSOAJURIDICA pj on cessionario.PESSOAJURIDICA_ID = pj.ID " +
            "                                                                      inner join ENTIDADE ent on pj.ID = ent.PESSOAJURIDICA_ID " +
            "                                                             where (ent.codigoPatrocinadora is not null or " +
            "                                                                    TRIM(ent.codigoPatrocinadora) <> '') " +
            "                                                               and cessionario.id = cedencia.CESSIONARIO_ID)))) " +
            "   or exists(select a.id from Afastamento a " +
            "                         inner join TIPOAFASTAMENTO tipo on a.TIPOAFASTAMENTO_ID = tipo.ID " +
            "                         where a.CONTRATOFP_ID = contrato.ID " +
            "                                and tipo.TIPOSITUACAOBBPREV is not null " +
            "                                and (to_date(to_char(:dataVigencia, 'mm/yyyy'), 'mm/yyyy') = to_date(to_char(a.inicio, 'mm/yyyy'), 'mm/yyyy') " +
            "                                            or to_date(to_char(:dataVigencia, 'mm/yyyy'), 'mm/yyyy') = to_date(to_char(a.TERMINO, 'mm/yyyy'), 'mm/yyyy'))) " +
            "   or exists(select r.id from Reintegracao r " +
            "                         where r.CONTRATOFP_ID = contrato.ID " +
            "                                and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') = to_date(to_char(r.dataReintegracao,'mm/yyyy'),'mm/yyyy')) " +
            "   or exists(select retorno.id from RetornoCedenciaContratoFP retorno " +
            "                                               inner join cedenciaContratoFP cedencia on retorno.CEDENCIACONTRATOFP_ID = cedencia.ID " +
            "                                               inner join unidadeOrganizacional ho on cedencia.UNIDADEORGANIZACIONAL_ID = ho.ID " +
            "                                               inner join entidade enti on ho.ENTIDADE_ID = enti.ID " +
            "                          where to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') = to_date(to_char(retorno.dataRetorno,'mm/yyyy'),'mm/yyyy') " +
            "                            and retorno.CONTRATOFP_ID = contrato.ID " +
            "                            and cedencia.tipoContratoCedenciaFP = :tipoCedencia " +
            "                            and (enti.codigoPatrocinadora is null or TRIM(enti.codigoPatrocinadora) = '' ))) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataVigencia", DataUtil.criarDataPrimeiroDiaMes(mes, ano));
        q.setParameter("tipoCedencia", TipoCedenciaContratoFP.COM_ONUS.name());
        q.setParameter("unidades", idsUnidades);
        List<ContratoFP> contratosFPs = Lists.newArrayList();
        for (BigDecimal id : (List<BigDecimal>) q.getResultList()) {
            contratosFPs.add(contratoFPFacade.recuperarContratoComLotacaoFuncional(id.longValue()));
        }
        return contratosFPs;
    }

    public BigDecimal buscarValorRendimentosMensais(Integer mes, Integer ano, ContratoFP contratofp) {
        String sql = " select sum(coalesce(item.valor, 0)) " +
            " from ItemFichaFinanceiraFP item " +
            "         inner join fichaFinanceiraFP ficha on item.FICHAFINANCEIRAFP_ID = ficha.ID " +
            "         inner join folhaDePagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            " where folha.ano = :ano " +
            "  and folha.mes = :mes " +
            "  and ficha.VINCULOFP_ID = :idContratofp " +
            "  and item.tipoEventoFP = :tipoEvento " +
            "  and folha.efetivadaEm is not null ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("mes", Mes.getMesToInt(mes).getNumeroMesIniciandoEmZero());
        q.setParameter("ano", ano);
        q.setParameter("idContratofp", contratofp.getId());
        q.setParameter("tipoEvento", TipoEventoFP.VANTAGEM.name());

        List resultList = q.getResultList();
        if (resultList == null || resultList.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) resultList.get(0);
    }
}
