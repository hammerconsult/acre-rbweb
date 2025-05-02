package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DependenciasDirf;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ArquivoCagedException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Leonardo
 */
@Stateless
public class CagedFacade extends AbstractFacade<Caged> {

    private static final Logger logger = LoggerFactory.getLogger(CagedFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private List<HierarquiaOrganizacional> hos;
    private List<ContratoFP> contratos;
    @EJB
    private ItemEntidadeDPContasFacade itemEntidadeDPContasFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private File arquivo;
    private Integer sequencia;
    private Caged selecionado;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    private EnderecoCorreio ec;
    private Telefone telefone;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;
    @EJB
    private EnderecoFacade enderecoFacade;
    private DependenciasDirf dependenciasDirf;
    @EJB
    private CagedAcompanhamentoFacade cagedAcompanhamentoFacade;
    @EJB
    private EntidadeFacade entidadeFacade;

    public CagedFacade() {
        super(Caged.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public boolean jaExisteCagedPara(Entidade entidade, Mes mes, Exercicio exercicio) {
        String hql = "select caged from Caged caged " +
            " where caged.entidade = :entidade " +
            " and caged.mes = :mes " +
            " and caged.exercicio = :exercicio";
        Query q = this.em.createQuery(hql);
        q.setParameter("entidade", entidade);
        q.setParameter("mes", mes);
        q.setParameter("exercicio", exercicio);
        q.setMaxResults(1);
        try {
            return q.getSingleResult() != null;
        } catch (NoResultException nre) {
            return false;
        }
    }

    public Caged salvarRetornando(Caged caged) {
        try {
            return getEntityManager().merge(caged);
        } catch (Exception ex) {
            logger.error("Problema ao Salvar o Arquivo CAGED", ex);
        }
        return null;
    }

    private List<HierarquiaOrganizacional> buscarUnidadesParaDeclaracaoDoEstabelecimentoSelecionado() {
        return entidadeFacade.buscarHierarquiasDaEntidade(selecionado.getEntidade(), CategoriaDeclaracaoPrestacaoContas.CAGED, selecionado.getPrimeiroDiaDoMes(), selecionado.getUltimoDiaDoMes());
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public void gerarArquivo(Caged selecionado, DependenciasDirf dd) throws ArquivoCagedException {
        this.selecionado = new Caged();
        this.selecionado = selecionado;
        cagedAcompanhamentoFacade.deleteCaged(selecionado);
        dependenciasDirf = dd;
        dependenciasDirf.setTotal(1);
        addLog("PREPARANDO ARQUIVO PARA GERAÇÃO DOS DADOS...");
        carregarTotalDeRegistros();
        StringBuilder conteudo = new StringBuilder();

        try {
            contratos = new ArrayList<>();
            hos = new ArrayList<>();

            addLog("POR FAVOR AGUARDE, O SISTEMA ESTÁ RECUPERANDO OS SERVIDORES PARA GERAÇÃO DO ARQUIVO, ESTE PROCESSO PODE LEVAR ALGUNS INSTANTES...");
            List<HierarquiaOrganizacional> hos = buscarUnidadesParaDeclaracaoDoEstabelecimentoSelecionado();

            for (HierarquiaOrganizacional ho : hos) {
                contratos.addAll(this.contratoFPFacade.recuperaContratosFpExoneradosEAdmitidosPorDataUnidadeOrganizacionalEPrevidenciaINSS(this.selecionado.getMes(), this.selecionado.getExercicio().getAno(), ho));
            }

            if (!contratos.isEmpty()) {
                dependenciasDirf.setTotal(contratos.size());
                addLog("GERANDO REGISTRO A DO ARQUIVO...");
                conteudo.append(criarRegistroAAutorizado());
                addLog("GERANDO REGISTRO B DO ARQUIVO...");
                conteudo.append(criarRegistroBEstabelecimento());
                addLog("GERANDO REGISTROS C DO ARQUIVO...");
                for (ContratoFP contratoFP : contratos) {
                    addLog(this.dependenciasDirf.getCalculados() + 1 + " - GERANDO REGISTROS DE : " + contratoFP.toString().toUpperCase(), "<b>", "</b>");
                    try {
                        String linhaLog = StringUtil.removeCaracteresEspeciais(criarRegistroCMovimentoCaged(contratoFP).toString());
                        conteudo.append(linhaLog);
                    } catch (ArquivoCagedException ex) {
                        addLog("&nbsp;&bull; REGISTRO NÃO GERADO. " + ex.getMessage(), "<font style='color : red;'><i>", "</i></font>");
                        adicionarLogIncosistencia("&nbsp;&bull;" + contratoFP.toString().toUpperCase(), "<b>", "</b>");
                        adicionarLogIncosistencia("&nbsp;&bull; " + ex.getMessage(), "<font style='color : red;'><i>", "</i></font>");
                        continue;
                    } catch (Exception e) {
                        throw e;
                    }
                    addLog("&nbsp;&bull; GERADO COM SUCESSO", "<font style='color : green;'><i>", "</i></font>");
                    dependenciasDirf.incrementarContador();
                }

                arquivo = new File("CAGED-" + this.selecionado.getMes().getNumeroMes() + "" + this.selecionado.getExercicio().getAno() + ".txt");

                BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arquivo), "ISO-8859-1"));
                fw.write(conteudo.toString());
                fw.close();

                this.selecionado.setConteudoArquivo(conteudo.toString());

                try {
                    addLog("SALVANDO ARQUIVO GERADO...");
                    this.selecionado = (Caged) cagedAcompanhamentoFacade.salvar(this.selecionado);

                } catch (Exception e) {
                    this.dependenciasDirf.pararProcessamento();
                    e.printStackTrace();
                    addLog("&nbsp;&bull; NÃO FOI POSSÍVEL GERAR O ARQUIVO. ENTRE EM CONTATO COM O SUPORTE PARA MAIORES INFORMAÇÕES. DETALHES DO ERRO: " + e, "<font style='color : darkred;'><i>", "</i></font>");
                }

                this.dependenciasDirf.setCalculados(Integer.parseInt("" + this.dependenciasDirf.getTotal()));

                if (this.selecionado != null) {
                    addLog("&nbsp;&bull; ARQUIVO GERADO COM SUCESSO", "<font style='color : green;'><i>", "</i></font>");
                    this.dependenciasDirf.setCalculados(Integer.parseInt("" + this.dependenciasDirf.getTotal()));
                    this.dependenciasDirf.pararProcessamento();
                } else {
                    this.dependenciasDirf.pararProcessamento();
                    throw new ArquivoCagedException("NÃO FOI POSSÍVEL GERAR O ARQUIVO. ENTRE EM CONTATO COM O SUPORTE PARA MAIORES INFORMAÇÕES.");
                }

            } else {
                this.dependenciasDirf.pararProcessamento();
                throw new ArquivoCagedException("Nenhuma movimentação para essa competência e entidade.");

            }
        } catch (ArquivoCagedException e) {
            addLog("&nbsp;&bull; Ocorreu um erro ao gerar o arquivo. " + e.getMessage(), "<font style='color : red;'><i>", "</i></font>");
        } catch (IOException ex) {
            addLog("&nbsp;&bull; ERRO GRAVE: " + ex.getMessage(), "<font style='color : darkred;'><i>", "</i></font>");
            ex.printStackTrace();
        }
    }

    public StringBuilder criarRegistroAAutorizado() throws ArquivoCagedException {
        sequencia = 1;
        StringBuilder registroA = new StringBuilder();

        registroA.append("A");
        registroA.append("L2009");
        registroA.append(adicionarFiller(2));
        registroA.append("4");
        registroA.append(recuperaCompetencia(selecionado));
        registroA.append("1");
        registroA.append(calculaSequencia());
        PessoaJuridica pj = recuperaPessoaDaEntidade(selecionado.getEntidade());

        if (pj.getTipoInscricao() != null && pj.getTipoInscricao().equals(TipoInscricao.CNPJ)) {
            registroA.append("1");
            registroA.append(recuperaEFormataCnpj(pj));
        } else {
            registroA.append("2");
            registroA.append(recuperaEFormataCEI(pj));
        }
        registroA.append(StringUtil.cortaOuCompletaDireita(retornaNomeEntidade(), 35, " "));
        registroA.append(retornaEnderecoPessoaJuridicaDaEntidade(pj));
        registroA.append(retornaCepFormatado(pj));
        registroA.append(retornaUf());
        this.retornaTelefoneDaPessoa(pj);
        registroA.append(retornaDDDFormatado());
        registroA.append(retornaTelefoneFormatado());
        registroA.append(recuperaRamal());
        registroA.append(calculaNumeroDeEstabelecimentoRegistroB());
        registroA.append(calculaNumeroRegistroCOuX());
        registroA.append(adicionarFiller(92)).append(System.getProperty("line.separator"));
        return registroA;

    }

    public StringBuilder criarRegistroBEstabelecimento() throws ArquivoCagedException {
        StringBuilder registroB = new StringBuilder();

        registroB.append("B");
        PessoaJuridica pj = recuperaPessoaDaEntidade(selecionado.getEntidade());
        if (pj.getTipoInscricao() != null && pj.getTipoInscricao().equals(TipoInscricao.CNPJ)) {
            registroB.append("1");
            registroB.append(recuperaEFormataCnpj(pj));
        } else {
            registroB.append("2");
            registroB.append(recuperaEFormataCEI(pj));
        }
        registroB.append(calculaSequencia());
        registroB.append(recuperaDeclaracao());
        registroB.append(alteracaoNoCadastro());
        registroB.append(retornaCepFormatado(pj));
        registroB.append(adicionarFiller(5));
        registroB.append(StringUtil.cortaOuCompletaDireita(retornaNomeEntidade(), 40, " "));
        registroB.append(retornaEnderecoPessoaJuridicaDaEntidade(pj));
        registroB.append(retornaBairro());
        registroB.append(retornaUf());
        registroB.append(recuperaNumeroEmpregados());
        registroB.append(retornaPorteEstabelecimento());
        registroB.append(retornaNumeroCnae());
        registroB.append(retornaDDDFormatado());
        registroB.append(retornaTelefoneFormatado());
        registroB.append(retornaEmailResponsavel(selecionado.getPessoaFisica()));
        registroB.append(adicionarFiller(27)).append(System.getProperty("line.separator"));

        return registroB;
    }

    public StringBuilder criarRegistroCMovimentoCaged(ContratoFP contrato) throws ArquivoCagedException {
        StringBuilder registroC = new StringBuilder();
        registroC.append("C");
        PessoaJuridica pj = recuperaPessoaDaEntidade(selecionado.getEntidade());
        if (pj.getTipoInscricao() != null) {
            if (pj.getTipoInscricao().equals(TipoInscricao.CNPJ)) {
                registroC.append("1");
                registroC.append(recuperaEFormataCnpj(pj));
            } else {
                registroC.append("2");
                registroC.append(recuperaEFormataCEI(pj));
            }
        } else {
            this.dependenciasDirf.pararProcessamento();
            throw new ArquivoCagedException("Informe o Tipo de Inscrição da Pessoa Jurídica " + pj.getNome());
        }
        registroC.append(calculaSequencia());
        PessoaFisica pf = recuperaPessoaDoContratoFP(contrato);
        registroC.append(recuperaNumeroPisPasep(pf));
        registroC.append(getSexoPessoaFisica(pf));
        registroC.append(retornaDataNascimentoFormatada(pf));
        registroC.append(StringUtil.cortaOuCompletaDireita(retornaGrauEscolaridadeCaged(pf), 2, " "));
        registroC.append(adicionarFiller(4));
        registroC.append(retornaSalarioMensal(contrato));
        registroC.append(retornaCargaHorariaTrabalho(contrato));
        registroC.append(retornaDataAdmissao(contrato));
        registroC.append(retornaTipoMovimento(contrato, selecionado));
        registroC.append(StringUtil.cortaOuCompletaEsquerda(retornaDiaDesligamento(contrato, selecionado), 2, " "));
        registroC.append(retornaNomePessoa(pf));
        registroC.append(retornaNumeroCarteiraTrabalho(pf));
        registroC.append(adicionarFiller(7));
        registroC.append(retornaRacaCor(pf));
        registroC.append(retornaDeficiencia(pf));
        registroC.append(retornaCBO(contrato));
        registroC.append(isAprendiz());
        registroC.append(retornaUfCarteiraTrabalho(pf));
        registroC.append(retornaTipoDeficiencia(pf));
        registroC.append(retornaCpf(pf));
        registroC.append(retornaCepResidencial(pf));
        registroC.append(adicionarFiller(81)).append(System.getProperty("line.separator"));
        return registroC;
    }

    private void addLog(String valor) {
        try {
            String agora = Util.dateHourToString(new Date());
            this.dependenciasDirf.getLog().add(agora + " - " + valor.concat("<br/>"));
        } catch (NullPointerException npe) {
            if (this.dependenciasDirf == null) {
                this.dependenciasDirf = new DependenciasDirf();
            }
            this.dependenciasDirf.setLog(new ArrayList<String>());
            addLog(valor);
        }
    }

    private void adicionarLogIncosistencia(String valor) {
        try {
            String agora = Util.dateHourToString(new Date());
            this.dependenciasDirf.getLogIncosistencia().add(agora + " - " + valor.concat("<br/>"));
        } catch (NullPointerException npe) {
            if (this.dependenciasDirf == null) {
                this.dependenciasDirf = new DependenciasDirf();
            }
            this.dependenciasDirf.setLogIncosistencia(new ArrayList<String>());
            adicionarLogIncosistencia(valor);
        }
    }

    private void addLog(String valor, String abre, String fecha) {
        addLog(abre + valor + fecha);
    }

    private void adicionarLogIncosistencia(String valor, String abre, String fecha) {
        adicionarLogIncosistencia(abre + valor + fecha);
    }

    private void carregarTotalDeRegistros() {
        this.dependenciasDirf.setTotal(1);
    }

    private String adicionarFiller(Integer numeroEspacos) {
        String retorno = "";
        for (int i = 0; i < numeroEspacos; i++) {
            retorno += " ";
        }
        return retorno;
    }

    private String recuperaCompetencia(Caged caged) {
        String mes = (caged.getMes().getNumeroMes() > 9 ? "" : "0") + (caged.getMes().getNumeroMes());
        String ano = "" + caged.getExercicio().getAno();
        return mes + ano;
    }

    private String calculaSequencia() {
        return StringUtil.cortaOuCompletaEsquerda(sequencia++ + "", 5, "0");
    }

    private PessoaJuridica recuperaPessoaDaEntidade(Entidade e) throws ArquivoCagedException {
        PessoaJuridica pj = this.pessoaFacade.recuperaPessoaJuridicaPorEntidade(e);
        if (pj != null) {
            return pj;
        }
        this.dependenciasDirf.pararProcessamento();
        throw new ArquivoCagedException("Informe a pessoa jurídica na Entidade selecionada.");
    }

    private String recuperaEFormataCnpj(PessoaJuridica pj) throws ArquivoCagedException {
        if (pj.getCnpj() != null) {
            String retorno = StringUtil.removeEspacos(StringUtil.removeCaracteresEspeciaisSemEspaco(pj.getCnpj()));
            if (retorno.length() <= 14) {
                return retorno;
            }
        }
        this.dependenciasDirf.pararProcessamento();
        throw new ArquivoCagedException("Informe o número do CNPJ na pessoa jurídica " + pj.getRazaoSocial() + ".");
    }

    private String recuperaEFormataCEI(PessoaJuridica pj) throws ArquivoCagedException {
        if (pj.getCei() != null) {
            String cei = StringUtil.cortaOuCompletaEsquerda(StringUtil.removeCaracteresEspeciaisSemEspaco(pj.getCei()), 14, "0");
            if (cei.length() <= 14) {
                return cei;
            }
        }
        this.dependenciasDirf.pararProcessamento();
        throw new ArquivoCagedException("Informe o número do CEI na pessoa Jurídica " + pj.getRazaoSocial() + ".");
    }

    private String retornaNomeEntidade() {
        return StringUtil.removeAcentuacao(selecionado.getEntidade().getNome().toUpperCase());
    }

    private String retornaEnderecoPessoaJuridicaDaEntidade(PessoaJuridica pj) throws ArquivoCagedException {
        String endereco = StringUtil.cortaOuCompletaDireita(retornaEnderecoCorreioDaPessoa(pj), 40, " ");
        if (!endereco.trim().isEmpty()) {
            return endereco;
        }
        this.dependenciasDirf.pararProcessamento();
        throw new ArquivoCagedException("Informe um endereço na pessoa jurídica da Entidade.");
    }

    private String retornaCepFormatado(PessoaJuridica pj) throws ArquivoCagedException {
        if (ec != null && ec.getCep() != null && !ec.getCep().trim().isEmpty()) {
            return retiraEspacoEmBranco(StringUtil.removeCaracteresEspeciaisSemEspaco(ec.getCep()));
        }
        this.dependenciasDirf.pararProcessamento();
        throw new ArquivoCagedException("Informe o cep do endereço de: " + pj.getRazaoSocial() + ".");
    }

    private String retornaUf() throws ArquivoCagedException {
        if (ec.getUf().trim().length() > 2) {
            this.dependenciasDirf.pararProcessamento();
            throw new ArquivoCagedException("A unidade federativa (UF) deve ser definida pela sigla e estar com 2 casas. Ex: PR (Paraná), AC (Acre).");
        }
        return ec.getUf();
    }

    private void retornaTelefoneDaPessoa(PessoaJuridica pj) {
        String retorno = "";
        List<Telefone> telefones = new ArrayList<>();
        telefones.addAll(this.pessoaFacade.telefonePorPessoa(pj));
        if (!telefones.isEmpty()) {
            telefone = telefones.get(0);
        }
    }

    private String retornaDDDFormatado() throws ArquivoCagedException {
        if (!telefone.getTelefone().trim().isEmpty()) {
            return StringUtil.cortaOuCompletaEsquerda(retornaDDD(retiraMascaraTelefone(telefone.getTelefone())), 4, "0");
        }
        this.dependenciasDirf.pararProcessamento();
        throw new ArquivoCagedException("Informe o DDD do telefone da pessoa.");
    }

    private String retornaTelefoneFormatado() throws ArquivoCagedException {
        if (!telefone.getTelefone().trim().isEmpty()) {
            return StringUtil.cortaOuCompletaDireita(retornaTelefone(retiraMascaraTelefone(telefone.getTelefone().trim())), 8, " ");
        }
        this.dependenciasDirf.pararProcessamento();
        throw new ArquivoCagedException("Informe o telefone da pessoa.");
    }

    private String recuperaRamal() {
        return "00000";
    }

    private String calculaNumeroDeEstabelecimentoRegistroB() {
        return "00001";
    }

    private String calculaNumeroRegistroCOuX() {
        return StringUtil.cortaOuCompletaEsquerda("" + contratos.size(), 5, "0");
    }

    private String recuperaDeclaracao() {
        if (this.jaExisteCagedPara(this.selecionado.getEntidade(), selecionado.getMes(), selecionado.getExercicio())) {
            return "1";
        } else {
            return "2";
        }
    }

    private String alteracaoNoCadastro() {
        return "1";
    }

    private String retornaBairro() {
        return StringUtil.cortaOuCompletaDireita(retiraEspacoEmBranco(ec.getBairro()), 20, " ");
    }

    private String recuperaNumeroEmpregados() {
        BigDecimal numeroDeContratos = BigDecimal.ZERO;
        for (HierarquiaOrganizacional ho : hos) {
            numeroDeContratos = numeroDeContratos.add(this.contratoFPFacade.recuperaContratosVigentesPorData(ho, montaData(selecionado)));
        }
        return StringUtil.cortaOuCompletaEsquerda(String.valueOf(numeroDeContratos), 5, "0");
    }

    private String retornaPorteEstabelecimento() throws ArquivoCagedException {
        TipoEmpresa tipoEmpresa = selecionado.getEntidade().getPessoaJuridica().getTipoEmpresa();
        if (tipoEmpresa != null) {
            switch (tipoEmpresa) {
                case MICRO:
                    return "1";
                case PEQUENA:
                    return "2";
                default:
                    return "3";
            }
        }
        this.dependenciasDirf.pararProcessamento();
        throw new ArquivoCagedException("Informe o Porte da Empresa " + selecionado.getEntidade().getPessoaJuridica().getRazaoSocial() + " em Pessoa Jurídica.");
    }

    public String retornaNumeroCnae() throws ArquivoCagedException {

        if (selecionado.getEntidade() != null) {
            if (selecionado.getEntidade().getCnae() != null) {
                if (selecionado.getEntidade().getCnae().getCodigoCnae() != null && !selecionado.getEntidade().getCnae().getCodigoCnae().trim().isEmpty()) {
                    return StringUtil.cortaOuCompletaEsquerda(StringUtil.removeCaracteresEspeciaisSemEspaco(selecionado.getEntidade().getCnae().getCodigoCnae()), 7, "0");
                }
            }
        }
        this.dependenciasDirf.pararProcessamento();
        throw new ArquivoCagedException("Informe o código do CNAE da entidade: " + selecionado.getEntidade().getNome() + ".");
    }

    public String retornaEmailResponsavel(Pessoa p) throws ArquivoCagedException {

        if (p.getEmail() != null) {
            if (!p.getEmail().trim().isEmpty()) {
                return StringUtil.cortaOuCompletaDireita(p.getEmail().toUpperCase(), 50, " ");
            }
        }

        this.dependenciasDirf.pararProcessamento();
        throw new ArquivoCagedException("Informe o e-mail do Responsável " + p.getNome());
    }

    private PessoaFisica recuperaPessoaDoContratoFP(ContratoFP contrato) {
        return this.pessoaFacade.recuperarPessoaPeloContratoFP(contrato);
    }

    private String recuperaNumeroPisPasep(PessoaFisica pf) throws ArquivoCagedException {
        CarteiraTrabalho carteiraTrabalho = this.pessoaFacade.recuperarCarteiraTrabalhoPelaPessoa(pf);
        if (carteiraTrabalho != null && carteiraTrabalho.getPisPasep() != null) {
            return StringUtil.removeCaracteresEspeciais(carteiraTrabalho.getPisPasep());
        }
        throw new ArquivoCagedException("Informe o número do PISPASEP de " + pf.getNome() + " CPF: " + pf.getCpf() + ".");
    }

    private String getSexoPessoaFisica(PessoaFisica pf) throws ArquivoCagedException {
        if (pf.getSexo() != null) {
            if (pf.getSexo().equals(Sexo.MASCULINO)) {
                return "1";
            } else {
                return "2";
            }
        }
        throw new ArquivoCagedException("Informe o sexo na pessoa: " + pf.getNome() + " CPF: " + pf.getCpf() + ".");
    }

    public String retornaDataNascimentoFormatada(PessoaFisica pf) throws ArquivoCagedException {
        String retorno = StringUtil.cortaOuCompletaDireita(StringUtil.removeCaracteresEspeciaisSemEspaco(pf.getDataNascimentoFormatada()), 8, " ");
        if (!retorno.trim().isEmpty()) {
            return retorno;
        }
        throw new ArquivoCagedException("Informe a data de nascimento de " + pf.getNome() + " CPF: " + pf.getCpf() + ".");
    }

    public String retornaGrauEscolaridadeCaged(PessoaFisica pf) throws ArquivoCagedException {
        if (pf.getNivelEscolaridade() != null) {
            if (pf.getNivelEscolaridade().getGrauInstrucaoCAGED() != null) {
                switch (pf.getNivelEscolaridade().getGrauInstrucaoCAGED()) {
                    case ANALFABETO:
                        return "1";
                    case QUINTO_ANO_INCOMPLETO:
                        return "2";
                    case QUINTO_ANO_COMPLETO:
                        return "3";
                    case SEXTO_AO_NONO_ANO:
                        return "4";
                    case FUNDAMENTAL_COMPLETO:
                        return "5";
                    case MEDIO_INCOMPLETO:
                        return "6";
                    case MEDIO_COMPLETO:
                        return "7";
                    case SUPERIOR_INCOMPLETO:
                        return "8";
                    case SUPERIOR_COMPLETO:
                        return "9";
                    case MESTRADO:
                        return "10";
                    case DOUTORADO:
                        return "11";
                }
            }
        }
        throw new ArquivoCagedException("Informe o grau de escolaridade do CAGED de " + pf.getNome() + " CPF: " + pf.getCpf() + ".");
    }

    public String retornaSalarioMensal(ContratoFP contratoFP) {

        BigDecimal valor = BigDecimal.ZERO;

        FichaFinanceiraFP ficha = fichaFinanceiraFPFacade.recuperaFichaFinanceiraFPPorVinculoFPMesAno(contratoFP, selecionado.getExercicio().getAno(), selecionado.getMes().getNumeroMes());

        if (ficha != null && ficha.getId() != null) {
            valor = fichaFinanceiraFPFacade.recuperaValorLiquidoFichaFinanceiraFP(ficha);
        } else {

            Date data = DataUtil.montaData(DataUtil.ultimoDiaDoMes(selecionado.getMes().getNumeroMes()), selecionado.getMes().getNumeroMes() - 1, selecionado.getExercicio().getAno()).getTime();
            valor = enquadramentoPCSFacade.salarioBaseContratoServidorAno(contratoFP, data);
        }

        if (valor != null && valor.compareTo(BigDecimal.ZERO) > 0) {
            return StringUtil.cortaOuCompletaEsquerda(StringUtil.removeCaracteresEspeciaisSemEspaco("" + getValorFormatadoParaArquivo(valor)), 8, "0");
        } else {
            return "00000000";
        }
    }

    public String retornaCargaHorariaTrabalho(ContratoFP contrato) {
        return StringUtil.cortaOuCompletaEsquerda("" + contrato.getJornadaDeTrabalho().getHorasSemanal(), 2, "0");
    }

    public String retornaDataAdmissao(ContratoFP contrato) throws ArquivoCagedException {
        if (contrato.getDataAdmissao() != null) {
            return new SimpleDateFormat("dd/MM/yyyy").format(contrato.getDataAdmissao()).replaceAll("/", "");
        }
        throw new ArquivoCagedException("Informe a data de admissão do contrato " + contrato.getNumero() + ".");
    }

    public String retornaTipoMovimento(ContratoFP contrato, Caged caged) throws ArquivoCagedException {
        ExoneracaoRescisao exoneracaoRescisao = this.exoneracaoRescisaoFacade.recuperaContratoExonerado(contrato, caged.getMes(), caged.getExercicio().getAno());
        String isExoneracao = "";
        if (exoneracaoRescisao != null) {
            isExoneracao = " o motivo de Exoneração/Rescisão " + exoneracaoRescisao.getMotivoExoneracaoRescisao() + " do Servidor: ";
            if (exoneracaoRescisao.getMotivoExoneracaoRescisao().getMovimentoCAGED() != null) {
                return exoneracaoRescisao.getMotivoExoneracaoRescisao().getMovimentoCAGED().getCodigo().toString();
            }
        } else {
            isExoneracao = "";
            if (contrato.getMovimentoCAGED() != null) {
                return contrato.getMovimentoCAGED().getCodigo().toString();
            }
        }
        throw new ArquivoCagedException("Informe o movimento do CAGED para " + isExoneracao + contrato.getMatriculaFP() + ".");
    }

    public String retornaDiaDesligamento(ContratoFP contrato, Caged caged) {
        ExoneracaoRescisao exoneracaoRescisao = this.exoneracaoRescisaoFacade.recuperaContratoExonerado(contrato, caged.getMes(), caged.getExercicio().getAno());
        if (exoneracaoRescisao != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(exoneracaoRescisao.getDataRescisao());
            return ("" + c.get(Calendar.DAY_OF_MONTH)).length() == 1 ? "0" + c.get(Calendar.DAY_OF_MONTH) : "" + c.get(Calendar.DAY_OF_MONTH);
        }
        return "";
    }

    public String retornaNomePessoa(Pessoa pessoa) {
        return StringUtil.cortaOuCompletaDireita(pessoa.getNome(), 40, " ");
    }

    public String retornaNumeroCarteiraTrabalho(Pessoa pessoa) throws ArquivoCagedException {
        CarteiraTrabalho ct = pessoaFacade.recuperarCarteiraTrabalhoPelaPessoa((PessoaFisica) pessoa);
//        if (ct.getNumero() == null || ct.getSerie() == null) {
//            this.dependenciasDirf.pararProcessamento();
//            throw new ArquivoCagedException("Verifique o Número e Série da Carteira de Trabalho da pessoa " + pessoa.getNome() + ".");
//
//        }
        return StringUtil.cortaOuCompletaEsquerda(ct.getNumero(), 8, "0") + StringUtil.cortaOuCompletaEsquerda(StringUtil.removeZerosEsquerda(ct.getSerie()), 4, "0");
    }

    public String retornaRacaCor(Pessoa pessoa) {
        if (((PessoaFisica) pessoa).getRacaCor() != null) {
            switch (((PessoaFisica) pessoa).getRacaCor()) {
                case INDIGENA:
                    return "1";
                case BRANCA:
                    return "2";
                case NEGRO:
                    return "4";
                case AMARELA:
                    return "6";
                case PARDA:
                    return "8";
                case NAO_INFORMADA:
                    return "9";
            }
        }
        return "9";
    }

    public String retornaDeficiencia(Pessoa pessoa) {
        if (((PessoaFisica) pessoa).getTipoDeficiencia() != null) {
            if (((PessoaFisica) pessoa).getTipoDeficiencia().equals(TipoDeficiencia.NAO_PORTADOR)) {
                return "2";
            } else {
                return "1";
            }
        } else {
            return "2";
        }
    }

    public String retornaTipoDeficiencia(Pessoa pessoa) {
        if (((PessoaFisica) pessoa).getTipoDeficiencia() != null) {
            if (!((PessoaFisica) pessoa).getTipoDeficiencia().equals(TipoDeficiencia.NAO_PORTADOR)) {
                switch (((PessoaFisica) pessoa).getTipoDeficiencia()) {
                    case FISICA:
                        return "1";
                    case AUDITIVA:
                        return "2";
                    case VISUAL:
                        return "3";
                    case MENTAL:
                        return "4";
                    case MULTIPLA:
                        return "5";
                    case REABILITADO:
                        return "6";
                }
                return "5";
            }
        }
        return "0";
    }

    public String retornaCBO(ContratoFP contrato) throws ArquivoCagedException {
        if (contrato.getCbo() != null) {
            return StringUtil.cortaOuCompletaEsquerda(contrato.getCbo().getCodigo().toString(), 6, "0");
        }
        throw new ArquivoCagedException("Informe o Cadastro Brasileiro de Ocupação (CBO) da pessoa " + contrato.getMatriculaFP().getPessoa().getNome() + ".");
    }

    public String isAprendiz() {
        return "2";
    }

    public String retornaUfCarteiraTrabalho(Pessoa pessoa) throws ArquivoCagedException {
        CarteiraTrabalho ct = this.pessoaFacade.recuperarCarteiraTrabalhoPelaPessoa((PessoaFisica) pessoa);
        if (ct.getUf() != null) {
            return StringUtil.cortaOuCompletaDireita(ct.getUf().getUf(), 2, " ");
        }
        throw new ArquivoCagedException("Informe a UF da Carteira de Trabalho da pessoa " + pessoa.getNome() + " CPF: " + pessoa.getCpf_Cnpj() + ".");
    }

    public String retornaCpf(Pessoa pessoa) throws ArquivoCagedException {
        if (!((PessoaFisica) pessoa).getCpf().trim().isEmpty()) {
            return StringUtil.cortaOuCompletaDireita(StringUtil.removeCaracteresEspeciaisSemEspaco(pessoa.getCpf_Cnpj()), 11, " ");
        }
        throw new ArquivoCagedException("Informe o CPF da pessoa " + pessoa.getNome() + ".");
    }

    public String retornaCepResidencial(Pessoa pessoa) throws ArquivoCagedException {
        List<EnderecoCorreio> enderecos = this.enderecoFacade.retornaEnderecoCorreioDaPessoa(pessoa);
        if (!enderecos.isEmpty()) {
            EnderecoCorreio end = enderecos.get(0);
            if (end.getCep() != null && !end.getCep().trim().isEmpty()) {
                return StringUtil.cortaOuCompletaDireita(StringUtil.removeCaracteresEspeciaisSemEspaco(end.getCep()), 8, "0");
            }
        }
        throw new ArquivoCagedException("Informe o CEP da pessoa " + pessoa.getNome() + ".");
    }

    private String retiraNull(String retorno) {
        return retorno.toLowerCase().replaceAll("null", "");
    }

    private String retornaEnderecoCorreioDaPessoa(PessoaJuridica pj) {
        String retorno = "";
        List<EnderecoCorreio> enderecos = new ArrayList<>();
        enderecos.addAll(this.enderecoFacade.retornaEnderecoCorreioDaPessoa(pj));
        if (!enderecos.isEmpty()) {
            ec = enderecos.get(0);
            retorno = retiraEspacoEmBranco(ec.getLogradouro()) + " "
                + retiraEspacoEmBranco(ec.getNumero()) + " "
                + retiraEspacoEmBranco(ec.getBairro()) + " "
                + retiraEspacoEmBranco(ec.getComplemento());

        }
        return StringUtil.removeAcentuacao(retorno);
    }

    public String retiraEspacoEmBranco(String parte) {
        if (parte == null) {
            return "";
        }
        return parte.trim();
    }


    public String retiraMascaraTelefone(String telefone) {
        return telefone.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\-", "");
    }

    public String retornaDDD(String tel) {
        String retorno = "";
        if (tel.length() >= 3) {
            retorno = tel.substring(0, 2);
        }
        return retorno;
    }

    private String retornaTelefone(String tel) {
        String retorno = "";
        if (tel.length() >= 4) {
            retorno = tel.substring(2, tel.length());
        }
        return retorno;
    }

    public Date montaData(Caged caged) {
        Calendar data = Calendar.getInstance();
        data.set(Calendar.DAY_OF_MONTH, 1);
        data.set(Calendar.YEAR, caged.getExercicio().getAno());
        data.set(Calendar.MONTH, caged.getMes().getNumeroMes() - 1);
        return data.getTime();
    }

    private String getValorFormatadoParaArquivo(BigDecimal valor) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        String r = nf.format(valor);
        r = StringUtil.retornaApenasNumeros(r);
        return r;
    }
}
