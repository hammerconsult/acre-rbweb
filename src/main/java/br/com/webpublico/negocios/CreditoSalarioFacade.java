package br.com.webpublico.negocios;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoCreditoSalario;
import br.com.webpublico.entidades.rh.creditodesalario.*;
import br.com.webpublico.entidades.rh.creditodesalario.caixa.*;
import br.com.webpublico.entidadesauxiliares.CreditoSalarioAgrupadorLote;
import br.com.webpublico.entidadesauxiliares.DependenciasDirf;
import br.com.webpublico.entidadesauxiliares.ItemRelatorioConferenciaCreditoSalario;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorioConferenciaCreditoSalario;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.negocios.rh.rotinasanuaismensais.SingletonCreditoSalario;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Claudio
 * Date: 24/10/13
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class CreditoSalarioFacade extends AbstractFacade<CreditoSalario> implements Serializable {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    //    private DependenciasDirf dependenciasCreditoSalario;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private ContaCorrenteBancPessoaFacade contaCorrenteBancPessoaFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaPagamentoFacade;
    @EJB
    private CompetenciaFPFacade competenciaFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private MatriculaFPFacade matriculaFPFacade;
    @EJB
    private RecursoFPFacade recursoFPFacade;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private GrupoRecursoFPFacade grupoRecursoFPFacade;
    @EJB
    private EnderecoFacade enderecoFacade;
    @EJB
    private ContaCorrenteBancariaFacade contaCorrenteBancariaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private CreditoSalarioAcompanhamentoFacade creditoSalarioAcompanhamentoFacade;
    @EJB
    private PensaoAlimenticiaFacade pensaoAlimenticiaFacade;
    @EJB
    private SingletonCreditoSalario singletonCreditoSalario;

    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;

    public CreditoSalarioFacade() {
        super(CreditoSalario.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Object[]> recuperarCreditoSalarioDetalheSegmento(CreditoSalario creditoSalario) {
        StringBuilder sb = new StringBuilder();

        sb.append("select vfp.id, mfp.matricula, pf.nome, sum(case when (iff.tipoEventoFP = :tipo_vantagem) then coalesce(iff.valor, 0) else coalesce(-iff.valor, 0) end) as valor,");
        sb.append(" grupo.nome as nomeGrupo, ff.identificador");
        sb.append(" from FichaFinanceiraFP ff ");
        sb.append(" inner join folhadepagamento fp on fp.id = ff.folhadepagamento_id ");
        sb.append(" inner join itemfichafinanceirafp iff on iff.fichafinanceirafp_id = ff.id ");
        sb.append(" inner join recursofp rec on ff.recursofp_id = rec.id ");
        sb.append(" inner join AgrupamentoRecursoFP agr on agr.recursofp_id = rec.id  ");
        sb.append(" inner join GrupoRecursoFP grupo on agr.grupoRecursofp_ID = grupo.id  ");
        sb.append(" inner join vinculofp vfp on vfp.id = ff.vinculofp_id ");
        sb.append(" inner join matriculafp mfp on mfp.id = vfp.matriculafp_id ");
        sb.append(" inner join pessoafisica pf on pf.id = mfp.pessoa_id ");
        sb.append(" where fp.id = :folha_id and iff.tipoEventoFP in('VANTAGEM', 'DESCONTO')");
        sb.append(" and coalesce(ff.creditoSalarioPago, 0) = 0 ");
        sb.append(" and  valor > 0");
        sb.append(creditoSalario.getMatriculas() != null && !creditoSalario.getMatriculas().isEmpty() ? " and mfp.id in :matriculas" : "");

        sb.append(" group by vfp.id, mfp.matricula, pf.nome, grupo.nome, ff.identificador ");
        sb.append(" order by pf.nome ");

        Query q = em.createNativeQuery(sb.toString());

        q.setParameter("folha_id", creditoSalario.getFolhaDePagamento().getId());
        q.setParameter("tipo_vantagem", TipoEventoFP.VANTAGEM.name());
//        q.setParameter("dataAtual", UtilRH.getDataOperacao());

        if (creditoSalario.getMatriculas() != null && !creditoSalario.getMatriculas().isEmpty()) {
            q.setParameter("matriculas", buscarListaIdMatriculas(creditoSalario));
        }

//        List<Long> resultado = q.getResultList();
//        resultado = new ArrayList(new HashSet(resultado));

//        for (CreditoSalarioCaixaDetalheSegmentoA creditoSalarioBancoBrasilDetalheSegmentoA : resultado) {
//            creditoSalarioBancoBrasilDetalheSegmentoA.getVinculoFP().getMatriculaFP().getPessoa().getContaCorrenteBancPessoas().size();
//        }
        return q.getResultList();
    }

    private List<Long> buscarListaIdMatriculas(CreditoSalario creditoSalario) {
        List<Long> matriculas = new ArrayList<>();
        for (MatriculaFP matriculaFP : creditoSalario.getMatriculas()) {
            matriculas.add(matriculaFP.getId());
        }
        return matriculas;
    }

    public List<Object[]> recuperarCreditoSalarioPensionistaDetalheSegmento(CreditoSalario creditoSalario, DependenciasDirf dependenciasCreditoSalario) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select vfp.numero, mfp.matricula, vfp.id as contratoId , ");
        sb.append(" grupo.nome as nomeGrupo, ");
        sb.append(" vfp.id, ff.identificador ");
        sb.append(" from FichaFinanceiraFP ff ");
        sb.append(" inner join folhadepagamento fp on fp.id = ff.folhadepagamento_id ");
        sb.append(" inner join itemfichafinanceirafp iff on iff.fichafinanceirafp_id = ff.id ");
        sb.append(" inner join recursofp rec on ff.recursofp_id = rec.id ");
        sb.append(" inner join AgrupamentoRecursoFP agr on agr.recursofp_id = rec.id  ");
        sb.append(" inner join GrupoRecursoFP grupo on agr.grupoRecursofp_ID = grupo.id  ");
        sb.append(" inner join vinculofp vfp on vfp.id = ff.vinculofp_id ");
        sb.append(" inner join matriculafp mfp on mfp.id = vfp.matriculafp_id ");
        sb.append(" where fp.id = :folha_id ");
        sb.append(" and coalesce(ff.creditoSalarioPago, 0) = 0 ");
        sb.append(" and vfp.id in (select pensao.vinculofp_id from pensaoalimenticia pensao  ");
        sb.append("                     inner join BeneficioPensaoAlimenticia bpa on pensao.id = bpa.PensaoAlimenticia_id  ");
        sb.append("                     where to_date(:dataReferencia, 'dd/MM/yyyy') between bpa.inicioVigencia and coalesce(bpa.finalVigencia, to_date(:dataReferencia, 'dd/MM/yyyy')) ");
        sb.append("                     and pensao.vinculofp_id = vfp.id) ");
        sb.append(creditoSalario.getMatriculas() != null && !creditoSalario.getMatriculas().isEmpty() ? " and mfp.id in :matriculas" : "");
        sb.append(" group by vfp.numero, mfp.matricula, vfp.id, grupo.nome, ff.identificador ");

        Query q = em.createNativeQuery(sb.toString());

        q.setParameter("folha_id", creditoSalario.getFolhaDePagamento().getId());
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(creditoSalario.getDataCredito()));

        if (creditoSalario.getMatriculas() != null && !creditoSalario.getMatriculas().isEmpty()) {
            q.setParameter("matriculas", buscarListaIdMatriculas(creditoSalario));
        }
        if (!q.getResultList().isEmpty()) {
            List<Object[]> toReturn = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                VinculoFP vinculo = vinculoFPFacade.recuperar((((BigDecimal) obj[2]).longValue()));
                for (BeneficioPensaoAlimenticia beneficioPensaoAlimenticia : pensaoAlimenticiaFacade.buscarBeneficiarioPensaoAlimenticiaVigentePorVinculo(vinculo, creditoSalario.getCompetenciaFP())) {
                    PessoaFisica pessoaFisica = buscarPessoaFisicaDoBeneficio(dependenciasCreditoSalario, beneficioPensaoAlimenticia, creditoSalario);
                    if (pessoaFisica != null) {
                        MatriculaFP matriculaFP = new MatriculaFP((String) obj[1], pessoaFisica);
                        VinculoFP vinculoFP = new VinculoFP(((BigDecimal) obj[4]).longValue(), matriculaFP, (String) obj[0]);
                        Object[] objetoRetorno = new Object[6];
                        objetoRetorno[0] = vinculoFP;
                        objetoRetorno[1] = matriculaFP.getMatricula();
                        objetoRetorno[2] = obj[3];
                        objetoRetorno[3] = buscarValorDaPensao(vinculo, beneficioPensaoAlimenticia, creditoSalario);
                        objetoRetorno[5] = obj[5];
                        toReturn.add(objetoRetorno);
                    }
                }
            }
            return toReturn;
        }

        return new ArrayList<>();
    }

    private BigDecimal buscarValorDaPensao(VinculoFP vinculo, BeneficioPensaoAlimenticia beneficioPensaoAlimenticia, CreditoSalario creditoSalario) {
        BigDecimal valorPensao = BigDecimal.ZERO;
        if (verificaEventosPensao(beneficioPensaoAlimenticia)) {
            valorPensao = fichaFinanceiraFPFacade.buscarItemFichaFinanceiraFPPorVinculoFPEventoAndFolha(vinculo, beneficioPensaoAlimenticia.getEventoFP(), creditoSalario.getFolhaDePagamento());
            if (valorPensao == null) {
                logger.error("Valor da Pensão está nullo.");
                valorPensao = BigDecimal.ZERO;
            }
        }

        return valorPensao;
    }

    private boolean verificaEventosPensao(BeneficioPensaoAlimenticia beneficioPensaoAlimenticia) {
        int codigoEvento = Integer.parseInt(beneficioPensaoAlimenticia.getEventoFP().getCodigo());
        if (codigoEvento >= 603 && codigoEvento < 660) {
            return true;
        } else {
            return false;
        }
    }

    private PessoaFisica buscarPessoaFisicaDoBeneficio(DependenciasDirf dependenciasCreditoSalario, BeneficioPensaoAlimenticia beneficioPensaoAlimenticia, CreditoSalario creditoSalario) {
        PessoaFisica pessoaFisica = null;
        try {
            if (buscarContaCorrenteDaPessoa(beneficioPensaoAlimenticia.getPessoaFisicaBeneficiario(), creditoSalario) != null) {
                pessoaFisica = beneficioPensaoAlimenticia.getPessoaFisicaBeneficiario();
            } else if (buscarContaCorrenteDaPessoa(beneficioPensaoAlimenticia.getPessoaFisicaResponsavel(), creditoSalario) != null) {
                pessoaFisica = beneficioPensaoAlimenticia.getPessoaFisicaResponsavel();
            }
        } catch (NullPointerException ex) {
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ERRO, "Beneficiário...: " + beneficioPensaoAlimenticia.getPessoaFisicaBeneficiario());
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ERRO, "Problema.: BANCO DA CONTA CORRENTE NÃO CONFERE - O banco Origem da conta corrente informada para receber o benefício não é a Caixa Economica Federal.<br />");
        }
        return pessoaFisica;
    }

    private void addLog(DependenciasDirf dependenciasDirf, DependenciasDirf.TipoLog tipoLog, String valor) {
        dependenciasDirf.adicionarLog(tipoLog, valor);
    }

    private String montaHeader(CreditoSalario cs, ItemCreditoSalario itemCreditoSalario) {
        CreditoSalarioCaixaHeader creditoSalarioHeader = new CreditoSalarioCaixaHeader();
        creditoSalarioHeader.montaHeader(cs, itemCreditoSalario);
        return creditoSalarioHeader.getHeaderArquivo();
    }

    private String montaHeaderLote(CreditoSalario creditoSalario, Integer sequenciaLote, CreditoSalarioAgrupadorLote agrupador) {
        CreditoSalarioCaixaHeaderLote creditoSalarioHeaderLote = new CreditoSalarioCaixaHeaderLote();
        creditoSalarioHeaderLote.montaHeaderLote(creditoSalario, sequenciaLote, agrupador);
        return creditoSalarioHeaderLote.getHeaderLoteArquivo();
    }

    private String montaTrailerLote(CreditoSalario cs, Integer sequenciaLote, Integer totalRegistrosLote, Double valorTotalLote) {
        CreditoSalarioCaixaTrailerLote creditoSalarioTrailerLote = new CreditoSalarioCaixaTrailerLote();
        creditoSalarioTrailerLote.montaTrailerLoteArquivo(cs.getContaBancariaEntidade().getAgencia().getBanco(), sequenciaLote, totalRegistrosLote, valorTotalLote);
        return creditoSalarioTrailerLote.getTrailerLoteArquivo();
    }

    private String montaTrailer(CreditoSalario cs, Integer quantidadeLotes, Integer totalRegistros, Integer quantidadeContas) {
        CreditoSalarioCaixaTrailer creditoSalarioTrailer = new CreditoSalarioCaixaTrailer();
        creditoSalarioTrailer.montaTrailerArquivo(cs, quantidadeLotes, totalRegistros, quantidadeContas);
        return creditoSalarioTrailer.getTrailerArquivo();
    }

    public BigDecimal recuperaValorLiquidoFichaFinanceiraFP(Long idFolhaDePagamento, Long idVinculo) {
        StringBuilder sql = new StringBuilder();
        sql.append("     select sum(case when (item.tipoEventoFP = '").append(TipoEventoFP.VANTAGEM.name()).append("') then coalesce(item.valor, 0) else coalesce(-item.valor, 0) end)");
        sql.append("       from itemfichafinanceirafp item");
        sql.append(" inner join fichafinanceirafp ficha on ficha.id = item.fichafinanceirafp_id");
        sql.append(" inner join folhadepagamento fp on fp.id = ficha.folhadepagamento_id");
        sql.append("      where fp.id = :folhapagamento_id ");
        sql.append("        and ficha.vinculofp_id = :vinculofp_id ");
        sql.append("        and item.tipoEventoFP in ('").append(TipoEventoFP.VANTAGEM.name()).append("', '").append(TipoEventoFP.DESCONTO.name()).append("')");

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("folhapagamento_id", idFolhaDePagamento);
        q.setParameter("vinculofp_id", idVinculo);
        q.setMaxResults(1);

        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    private ContaCorrenteBancaria buscarContaCorrenteDaPessoaEVinculo(VinculoFP vinculo, CreditoSalario selecionado) {
        if (vinculo.getContaCorrente() != null) {
            return vinculo.getContaCorrente();
        }
        if (vinculo instanceof ContratoFP && selecionado.getConfiguracaoCreditoSalario().getValidarContaCorrenteContrato()) {
            if (vinculo.getContaCorrente() != null) {
                return vinculo.getContaCorrente();
            } else {
                return null;
            }
        }

        if (!isTipoGeracaoServidor(selecionado)) {
            ContaCorrenteBancaria contaBeneficiario = getContaCorrenteDaPensaoAlimenticia(vinculo, selecionado);
            if (contaBeneficiario != null) {
                return contaBeneficiario;
            }
        }

        List<ContaCorrenteBancPessoa> contasDaPessoa = contaCorrenteBancPessoaFacade.buscarContasCorrenteBancPorPessoaFisica(vinculo.getMatriculaFP().getPessoa());
        if (contasDaPessoa == null) {
            return null;
        }

        // Busca conta salário ativa e no mesmo banco como prioridade
        for (ContaCorrenteBancPessoa contaCorrenteBancPessoa : contasDaPessoa) {
            if (!selecionado.isTipoArquivoServidor() && SituacaoConta.ATIVO.equals(contaCorrenteBancPessoa.getSituacao())) {
                return contaCorrenteBancPessoa.getContaCorrenteBancaria();
            }
            if (contaCorrenteBancPessoa.getAgencia().getBanco().equals(selecionado.getContaBancariaEntidade().getAgencia().getBanco())
                && SituacaoConta.ATIVO.equals(contaCorrenteBancPessoa.getSituacao())
                && ModalidadeConta.CONTA_SALARIO.equals(contaCorrenteBancPessoa.getModalidade())) {
                return contaCorrenteBancPessoa.getContaCorrenteBancaria();
            }
        }

        // Busca ativa e no mesmo banco
        for (ContaCorrenteBancPessoa contaCorrenteBancPessoa : contasDaPessoa) {
            if (!selecionado.isTipoArquivoServidor() && SituacaoConta.ATIVO.equals(contaCorrenteBancPessoa.getSituacao())) {
                return contaCorrenteBancPessoa.getContaCorrenteBancaria();
            }
            if (contaCorrenteBancPessoa.getAgencia().getBanco().equals(selecionado.getContaBancariaEntidade().getAgencia().getBanco())
                && SituacaoConta.ATIVO.equals(contaCorrenteBancPessoa.getSituacao())) {
                return contaCorrenteBancPessoa.getContaCorrenteBancaria();
            }
        }
        return null;
    }

    private ContaCorrenteBancaria buscarContaCorrenteDaPessoa(PessoaFisica pessoa, CreditoSalario selecionado) {
        pessoa = contaCorrenteBancariaFacade.getPessoaFisicaFacade().recuperar(pessoa.getId());
        List<ContaCorrenteBancPessoa> contasDaPessoa = pessoa.getContaCorrenteBancPessoas();
        if (contasDaPessoa == null) {
            return null;
        }

        // Busca conta salário ativa e no mesmo banco como prioridade
        for (ContaCorrenteBancPessoa contaCorrenteBancPessoa : contasDaPessoa) {
            if (!selecionado.isTipoArquivoServidor() && SituacaoConta.ATIVO.equals(contaCorrenteBancPessoa.getSituacao())) {
                return contaCorrenteBancPessoa.getContaCorrenteBancaria();
            }
            if (contaCorrenteBancPessoa.getAgencia().getBanco().equals(selecionado.getContaBancariaEntidade().getAgencia().getBanco()) && SituacaoConta.ATIVO.equals(contaCorrenteBancPessoa.getSituacao()) && ModalidadeConta.CONTA_SALARIO.equals(contaCorrenteBancPessoa.getModalidade())) {
                return contaCorrenteBancPessoa.getContaCorrenteBancaria();
            }
        }

        // Busca ativa e no mesmo banco
        for (ContaCorrenteBancPessoa contaCorrenteBancPessoa : contasDaPessoa) {
            if (!selecionado.isTipoArquivoServidor() && SituacaoConta.ATIVO.equals(contaCorrenteBancPessoa.getSituacao())) {
                return contaCorrenteBancPessoa.getContaCorrenteBancaria();
            }
            if (contaCorrenteBancPessoa.getAgencia().getBanco().equals(selecionado.getContaBancariaEntidade().getAgencia().getBanco()) && SituacaoConta.ATIVO.equals(contaCorrenteBancPessoa.getSituacao())) {
                return contaCorrenteBancPessoa.getContaCorrenteBancaria();
            }
        }
        return null;
    }

    private EnderecoCorreio recuperarEndereco(Pessoa pessoa) {
        return enderecoFacade.retornaPrimeiroEnderecoCorreioValido(pessoa);
    }

    private ParametrosRelatorioConferenciaCreditoSalario atribuirDadosParametrosRelatorio(CreditoSalario creditoSalario, GrupoRecursoFP grupoRecurso) {
        ParametrosRelatorioConferenciaCreditoSalario parametrosRelatorio = new ParametrosRelatorioConferenciaCreditoSalario();
        HierarquiaOrganizacional hierarquiaOrganizacional = grupoRecurso.getHierarquiaOrganizacional();
        String codigoHO = hierarquiaOrganizacional.getCodigo();

        parametrosRelatorio.setOrgao(codigoHO + " - " + grupoRecurso.getNome());
        parametrosRelatorio.setBanco(creditoSalario.getContaBancariaEntidade().getAgencia().getBanco().toString());
        parametrosRelatorio.setAgencia(creditoSalario.getContaBancariaEntidade().getAgencia().agenciaEDigitoVerificador());
        parametrosRelatorio.setConta(creditoSalario.getContaBancariaEntidade().getNumeroCompleto());
        return parametrosRelatorio;
    }

    private ItemRelatorioConferenciaCreditoSalario criarItemRelatorioConferenciaComBaseEmSegmento(VinculoFP vinculoFP) {
        ItemRelatorioConferenciaCreditoSalario itemConferencia = new ItemRelatorioConferenciaCreditoSalario();

        itemConferencia.setMatriculaServidor(vinculoFP.getMatriculaFP().getMatricula());
        itemConferencia.setNumeroContrato(vinculoFP.getNumero());
        itemConferencia.setNomeServidor(vinculoFP.getMatriculaFP().getPessoa().toString());

        return itemConferencia;
    }

    private void apagarCreditoSalarioExistente(CreditoSalario selecionado) {
        try {
            creditoSalarioAcompanhamentoFacade.jaExisteCreditoSalarioGeradoMesmosParametros(selecionado);
        } catch (RuntimeException re) {
            singletonCreditoSalario.delete(selecionado);
        }
    }

    private void salvarNovoCreditoSalario(CreditoSalario selecionado) {
        if (selecionado.getId() == null) {
            creditoSalarioAcompanhamentoFacade.salvarNovo(selecionado);
        }
    }

    private boolean isTipoGeracaoServidor(CreditoSalario selecionado) {
        return TipoGeracaoCreditoSalario.SERVIDORES.name().equals(selecionado.getTipoGeracaoCreditoSalario().name());
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void gerarArquivo(CreditoSalario selecionado, DependenciasDirf dependenciasCreditoSalario) throws Exception {
        apagarCreditoSalarioExistente(selecionado);
        salvarNovoCreditoSalario(selecionado);
        ConfiguracaoCreditoSalario configuracaoCreditoSalario = configuracaoRHFacade.buscarConfiguracaoCreditoDeSalario();
        selecionado.setConfiguracaoCreditoSalario(configuracaoCreditoSalario);

        addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ALERTA, "POR FAVOR AGUARDE, O SISTEMA ESTÁ RECUPERANDO OS SERVIDORES PARA GERAÇÃO DO ARQUIVO, ESTE PROCESSO PODE LEVAR ALGUNS INSTANTES...");
        dependenciasCreditoSalario.setTotalCadastros(0);
        List<Object[]> lista = new ArrayList<>();
        if (isTipoGeracaoServidor(selecionado)) {
            lista = recuperarCreditoSalarioDetalheSegmento(selecionado);
        } else {
            lista = recuperarCreditoSalarioPensionistaDetalheSegmento(selecionado, dependenciasCreditoSalario);
            Collections.sort(lista, getOrdenador());
        }
        if (lista.isEmpty() || lista == null) {
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ALERTA, "NÃO FORAM LOCALIZADOS SERVIDORES COM RENDIMENTOS NO ANO INFORMADO.");
            dependenciasCreditoSalario.pararProcessamento();
            throw new ExcecaoNegocioGenerica("Não foram localizados servidores com rendimentos no período informado informado.");
        }
        Map<GrupoRecursoFP, List<Object[]>> vinculosGrupo = Maps.newLinkedHashMap();
        filtrarPensionistas(selecionado, lista);
        filtrarVinculosPorGrupo(lista, selecionado, dependenciasCreditoSalario, vinculosGrupo);
        vinculosGrupo = ordenarGrupos(vinculosGrupo);
        preencherTotalDeRegistros(dependenciasCreditoSalario, vinculosGrupo);
        for (Map.Entry<GrupoRecursoFP, List<Object[]>> grupoRecursoFPListEntry : vinculosGrupo.entrySet()) {
            logger.debug("Grupo {} total {}", grupoRecursoFPListEntry.getKey(), grupoRecursoFPListEntry.getValue().size());
            Integer sequencialRemessa = getSequencial();
            gerarArquivoInvidual(selecionado, dependenciasCreditoSalario, grupoRecursoFPListEntry.getValue(), grupoRecursoFPListEntry.getKey(), sequencialRemessa);
        }
        dependenciasCreditoSalario.pararProcessamento();
        creditoSalarioAcompanhamentoFacade.salvar(selecionado);
    }

    private void filtrarPensionistas(CreditoSalario selecionado, List<Object[]> lista) {
        if (!selecionado.isTipoArquivoServidor() && isPossuiBeneficiarios(selecionado)) {
            for (Iterator<Object[]> iterator = lista.iterator(); iterator.hasNext(); ) {
                Object[] objeto = iterator.next();
                VinculoFP v = (VinculoFP) objeto[0];
                if (naoPossuiNaLista(selecionado, v)) {
                    iterator.remove();
                }
            }
        }
    }

    private boolean isPossuiBeneficiarios(CreditoSalario selecionado) {
        return selecionado.getBeneficiarios() != null && selecionado.getBeneficiarios().length > 0;
    }

    private boolean naoPossuiNaLista(CreditoSalario selecionado, VinculoFP v) {
        if (selecionado.getBeneficiarios() != null) {
            for (BeneficioPensaoAlimenticia beneficioPensaoAlimenticia : selecionado.getBeneficiarios()) {
                if (beneficioPensaoAlimenticia.getPessoaFisicaBeneficiario().equals(v.getMatriculaFP().getPessoa())) {
                    return false;
                }
            }
        }
        return true;
    }

    private Map<GrupoRecursoFP, List<Object[]>> ordenarGrupos(Map<GrupoRecursoFP, List<Object[]>> unsortMap) {
        // Convert Map to List
        List<Map.Entry<GrupoRecursoFP, List<Object[]>>> list =
            new LinkedList<>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<GrupoRecursoFP, List<Object[]>>>() {
            @Override
            public int compare(Map.Entry<GrupoRecursoFP, List<Object[]>> o1,
                               Map.Entry<GrupoRecursoFP, List<Object[]>> o2) {
                return (o1.getKey().getHierarquiaOrganizacional().getCodigo()).compareTo(o2.getKey().getHierarquiaOrganizacional().getCodigo());
            }
        });

        // Convert sorted map back to a Map
        Map<GrupoRecursoFP, List<Object[]>> sortedMap = new LinkedHashMap<>();
        for (Iterator<Map.Entry<GrupoRecursoFP, List<Object[]>>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<GrupoRecursoFP, List<Object[]>> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;


        /*Comparator<GrupoRecursoFP, List<Object[]>> comparator = new Comparator<GrupoRecursoFP>() {
            @Override
            public int compare(GrupoRecursoFP o1, GrupoRecursoFP o2) {
                return 0;
            }
        };*/

      /*  SortedMap<GrupoRecursoFP, List<Object[]>> ordenado = Maps.newTreeMap();
        ordenado.putAll(vinculosGrupo);*/
        //vinculosGrupo.clear();
        //vinculosGrupo.putAll(ordenado);
    }

    private void preencherTotalDeRegistros(DependenciasDirf dependenciasCreditoSalario, Map<GrupoRecursoFP, List<Object[]>> vinculosGrupo) {
        Integer total = 0;
        for (Map.Entry<GrupoRecursoFP, List<Object[]>> grupoRecursoFPListEntry : vinculosGrupo.entrySet()) {
            total += grupoRecursoFPListEntry.getValue().size();
        }
        dependenciasCreditoSalario.setTotalCadastros(total);
    }

    private void criarVinculoFPCreditoSalario(ItemCreditoSalario itemCreditoSalario, VinculoFP vinculo) {
        VinculoFPCreditoSalario vinculoFPCreditoSalario = new VinculoFPCreditoSalario();
        vinculoFPCreditoSalario.setVinculoFP(vinculo);
        vinculoFPCreditoSalario.setItemCreditoSalario(itemCreditoSalario);
        itemCreditoSalario.getItensVinculoCreditoSalario().add(vinculoFPCreditoSalario);
    }


    public void gerarArquivoInvidual(CreditoSalario selecionado, DependenciasDirf dependenciasCreditoSalario, List<Object[]> lista, GrupoRecursoFP grupoRecurso, Integer sequencialRemessa) throws Exception {
        Integer ano = selecionado.getFolhaDePagamento().getAno();
        String mes = selecionado.getFolhaDePagamento().getMes().getNumeroMes().toString();
        mes = StringUtil.cortarOuCompletarEsquerda(mes, 2, "0");
        String codigoFolha = selecionado.getFolhaDePagamento().getTipoFolhaDePagamento().getCodigo().toString();
        codigoFolha = StringUtil.cortarOuCompletarEsquerda(codigoFolha, 2, "0");

        String identificador = ano.toString().concat(mes).concat(codigoFolha);
        identificador = identificador.concat(RandomStringUtils.randomAlphanumeric(12));
        ItemCreditoSalario itemCreditoSalario = new ItemCreditoSalario();
        itemCreditoSalario.setCreditoSalario(selecionado);
        itemCreditoSalario.setGrupoRecursoFP(grupoRecurso);
        itemCreditoSalario.setSequencial(sequencialRemessa);
        itemCreditoSalario.setIdentificador(identificador.toUpperCase());
        ParametrosRelatorioConferenciaCreditoSalario parametrosRelatorio = atribuirDadosParametrosRelatorio(selecionado, grupoRecurso);
        selecionado.setConteudoArquivo(montaHeader(selecionado, itemCreditoSalario));
        Integer sequencialNossoNumero = 1;
        Integer totalRegistros = 1;
        Integer totalRegistrosLote = 0;
        Integer quantidadeLotes = 1;
        Double valorTotalLote = 0d;
        BigDecimal liquidoGrupo = BigDecimal.ZERO;

        StringBuilder lote = new StringBuilder();
        Map<CreditoSalarioAgrupadorLote, List<Object[]>> lotes = Maps.newHashMap();
        criarLotes(lotes, lista, selecionado);
        Integer sequenciaLote = 1;

        for (Map.Entry<CreditoSalarioAgrupadorLote, List<Object[]>> creditoSalarioAgrupadorLoteListEntry : lotes.entrySet()) {
            if (creditoSalarioAgrupadorLoteListEntry.getValue().isEmpty()) {
                continue;
            }
            lote.append(StringUtil.removeCaracteresEspeciais(montaHeaderLote(selecionado, sequenciaLote, creditoSalarioAgrupadorLoteListEntry.getKey())));

            for (Object[] resultado : creditoSalarioAgrupadorLoteListEntry.getValue()) {
                if (dependenciasCreditoSalario.getParado()) {
                    throw new ExcecaoNegocioGenerica("Processo Cancelado pelo Usuário");
                }

                VinculoFP vfp = getVinculo(resultado, selecionado);
                criarVinculoFPCreditoSalario(itemCreditoSalario, vfp);
                int contadorAtual = dependenciasCreditoSalario.getContadorProcessos() + 1;

                CreditoSalarioCaixaDetalheSegmentoA detalheSegmento = new CreditoSalarioCaixaDetalheSegmentoA(vfp, "" + resultado[1]);
                BigDecimal valorLiquido = (BigDecimal) resultado[3];
                detalheSegmento.setValorPagamento("" + valorLiquido);

                carregarContasCorrenteDaPessoa(detalheSegmento);
                ContaCorrenteBancaria cc = buscarContaCorrenteDaPessoaEVinculo(vfp, selecionado);
                try {
                    validarValorDaFichaFinanceira(cc, selecionado, valorLiquido, detalheSegmento, dependenciasCreditoSalario, contadorAtual, grupoRecurso);
                    validarContaCorrenteBancaria(cc, selecionado, detalheSegmento, dependenciasCreditoSalario, contadorAtual, grupoRecurso);
                } catch (ExcecaoNegocioGenerica eng) {
                    dependenciasCreditoSalario.incrementarContador();
                    continue;
                }

                // Relatório Conferência (Dados do Servidore)
                ItemRelatorioConferenciaCreditoSalario itemConferencia = criarItemRelatorioConferenciaComBaseEmSegmento(vfp);
                itemConferencia.setNumeroBanco(cc.getBanco().getNumeroBanco());
                itemConferencia.setNumeroAgencia(cc.getAgencia().getNumeroAgencia());
                itemConferencia.setDigitoAgencia(cc.getAgencia().getDigitoVerificador());
                itemConferencia.setNumeroConta(cc.getNumeroConta());
                itemConferencia.setDigitoConta(cc.getDigitoVerificador());
                itemConferencia.setValor(valorLiquido);
                liquidoGrupo = liquidoGrupo.add(valorLiquido);
                // Relatório Conferência

                detalheSegmento.getVinculoFP().setContaCorrente(cc);

                totalRegistrosLote++;
                valorTotalLote += valorLiquido.doubleValue();

                detalheSegmento.montaDetalheSegmentoA(selecionado, sequenciaLote, totalRegistrosLote, sequencialNossoNumero, resultado[5] != null ? resultado[5].toString() : null);
                lote.append(detalheSegmento.getDetalheSegmentoA());
                totalRegistros++;

                totalRegistrosLote++;
                CreditoSalarioCaixaDetalheSegmentoB detalheSegmentoB = new CreditoSalarioCaixaDetalheSegmentoB();
                EnderecoCorreio ec = recuperarEndereco(detalheSegmento.getVinculoFP().getMatriculaFP().getPessoa());
                detalheSegmentoB.montaDetalheSegmentoB(selecionado, detalheSegmento.getVinculoFP(), ec, sequenciaLote, totalRegistrosLote);
                lote.append(StringUtil.removeCaracteresEspeciais(detalheSegmentoB.getDetalheSegmentoB()));
                totalRegistros++;

                addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.SUCESSO, "<b>" + StringUtil.cortaOuCompletaEsquerda(contadorAtual + "", 5, "0") + " - " + StringUtil.cortaOuCompletaDireita(vfp.getMatriculaFP().getPessoa().getNome().toUpperCase(), 50, " ") + " ✔ GERADO COM SUCESSO</b>");
                dependenciasCreditoSalario.incrementarContador();
                parametrosRelatorio.setItens(Util.adicionarObjetoEmLista(parametrosRelatorio.getItens(), itemConferencia));
                sequencialNossoNumero++;
            }
            lote.append(montaTrailerLote(selecionado, sequenciaLote, totalRegistrosLote + 2, valorTotalLote)); //totalRegistrosLote+2 porque soma a linha header do lote e a linha do trailer do lote
            sequenciaLote++;
        }
        selecionado.setConteudoArquivo(selecionado.getConteudoArquivo() + lote.toString());
        selecionado.setConteudoArquivo(selecionado.getConteudoArquivo() + montaTrailer(selecionado, quantidadeLotes, ++totalRegistros, 0));
        selecionado.setParametrosRelatorioConferenciaCreditoSalario(parametrosRelatorio);


        selecionado = gerarEntidadeArquivo(selecionado, itemCreditoSalario, sequencialRemessa);

        ByteArrayOutputStream bytes = getRelatorioEmBytes(selecionado, dependenciasCreditoSalario);
        gerarArquivoDoRelatorio(bytes, selecionado, itemCreditoSalario);
        itemCreditoSalario.setValorLiquido(liquidoGrupo);
        selecionado.getItensCreditoSalario().add(itemCreditoSalario);
        creditoSalarioAcompanhamentoFacade.salvar(selecionado);

    }

    private void filtrarVinculosPorGrupo(List<Object[]> lista, CreditoSalario selecionado, DependenciasDirf dependenciasCreditoSalario, Map<GrupoRecursoFP, List<Object[]>> vinculosGrupo) {
        if (selecionado.getGrupos().isEmpty()) {

        }
        for (Object[] vinculo : lista) {
            String nomeGrupo = getNomeGrupo(vinculo, selecionado);
            if (nomeGrupo == null || nomeGrupo.equals("")) {
                adicionarInconsistencia(selecionado, dependenciasCreditoSalario, vinculo);
                continue;
            }
            classificarVinculo(nomeGrupo, vinculo, vinculosGrupo, selecionado.getGrupos());
        }
    }

    private void adicionarInconsistencia(CreditoSalario selecionado, DependenciasDirf dependenciasCreditoSalario, Object[] vinculo) {
        String mensagem = "O Servidor não possui agrupamento de Recurso FP ";
        if (isTipoGeracaoServidor(selecionado)) {
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ERRO, mensagem + vinculo[1] + " " + vinculo[2]);
        } else {
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ERRO, mensagem + vinculo[0]);
        }
    }

    private String getNomeGrupo(Object[] vinculo, CreditoSalario selecionado) {
        try {
            if (isTipoGeracaoServidor(selecionado)) {
                return (String) vinculo[4];
            } else {
                return (String) vinculo[2];
            }
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Não foi possível recuperar a sub folha do vinculo " + vinculo[0], e);
        }
    }

    private void classificarVinculo(String nomeGrupo, Object[] vinculo, Map<GrupoRecursoFP, List<Object[]>> mapaGrupoServidor, List<GrupoRecursoFP> grupoRecursoFPs) {
        for (GrupoRecursoFP grupoRecursoFP : grupoRecursoFPs) {
            if (nomeGrupo.equals(grupoRecursoFP.getNome())) {
                if (mapaGrupoServidor.containsKey(grupoRecursoFP)) {
                    mapaGrupoServidor.get(grupoRecursoFP).add(vinculo);
                } else {
                    List<Object[]> l = Lists.newLinkedList();
                    l.add(vinculo);
                    mapaGrupoServidor.put(grupoRecursoFP, l);
                }
            }
        }
    }

    private boolean vinculoEstaNoGrupo(VinculoFP v, GrupoRecursoFP grupoRecursoFP) {
        return grupoRecursoFPFacade.vinculoFPEstaNoGrupo(v, grupoRecursoFP);
    }

    private VinculoFP getVinculo(Object[] resultado, CreditoSalario selecionado) {
        if (isTipoGeracaoServidor(selecionado)) {
            return vinculoFPFacade.recuperar(Long.parseLong("" + resultado[0]));
        } else {
            return (VinculoFP) resultado[0];
        }
    }

    private Map<CreditoSalarioAgrupadorLote, List<Object[]>> criarLotes(Map<CreditoSalarioAgrupadorLote, List<Object[]>> lotes, List<Object[]> lista, CreditoSalario selecionado) {
        if (selecionado.isTipoArquivoServidor()) {
            CreditoSalarioAgrupadorLote agrupador = new CreditoSalarioAgrupadorLote();
            agrupador.setBanco(selecionado.getContaBancariaEntidade().getAgencia().getBanco());
            agrupador.setModalidadeConta(selecionado.getContaBancariaEntidade().getModalidadeConta());
            lotes.put(agrupador, lista);
            return lotes;
        }
        preencherMapKey(lotes, lista, selecionado);
        for (Object[] objeto : lista) {
            VinculoFP vinculo = getVinculo(objeto, selecionado);
            ContaCorrenteBancaria conta = buscarContaCorrenteDaPessoaEVinculo(vinculo, selecionado);

            if (conta != null) {
                CreditoSalarioAgrupadorLote agrupador = new CreditoSalarioAgrupadorLote();
                agrupador.setBanco(conta.getAgencia().getBanco());
                agrupador.setModalidadeConta(conta.getModalidadeConta());
                lotes.get(agrupador).add(objeto);
            }
        }
        return lotes;
    }

    private void preencherMapKey(Map<CreditoSalarioAgrupadorLote, List<Object[]>> lotes, List<Object[]> lista, CreditoSalario creditoSalario) {
        for (Object[] objeto : lista) {
            VinculoFP vinculoFP = getVinculo(objeto, creditoSalario);
            ContaCorrenteBancaria conta = buscarContaCorrenteDaPessoaEVinculo(vinculoFP, creditoSalario);
            if (conta != null) {
                CreditoSalarioAgrupadorLote agrupador = new CreditoSalarioAgrupadorLote();
                agrupador.setBanco(conta.getAgencia().getBanco());
                agrupador.setModalidadeConta(conta.getModalidadeConta());
                lotes.put(agrupador, new ArrayList<Object[]>());
            }
        }
    }

    private CreditoSalario gerarEntidadeArquivo(CreditoSalario creditoSalario, ItemCreditoSalario itemCreditoSalario, Integer sequencial) {
        InputStream is = new ByteArrayInputStream(creditoSalario.getConteudoArquivo().getBytes());
        Arquivo a = new Arquivo();
        a.setNome(getNomeArquivo(creditoSalario, sequencial));
        a.setMimeType("text/plain");
        a.setInputStream(is);
        try {
            arquivoFacade.novoArquivo(a, a.getInputStream());
        } catch (Exception ex) {
            logger.error("Erro:", ex);
        }
        itemCreditoSalario.getArquivos().add(criarItemArquivo(TipoArquivoCreditoSalario.REMESSA, itemCreditoSalario, a));
        return creditoSalario;
    }

    private ItemArquivoCreditoSalario criarItemArquivo(TipoArquivoCreditoSalario tipoArquivo, ItemCreditoSalario itemCreditoSalario, Arquivo a) {
        ItemArquivoCreditoSalario itemArquivo = new ItemArquivoCreditoSalario();
        itemArquivo.setArquivo(a);
        itemArquivo.setTipoArquivo(tipoArquivo);
        itemArquivo.setItemCreditoSalario(itemCreditoSalario);
        return itemArquivo;
    }

    private String getNomeArquivo(CreditoSalario creditoSalario, Integer sequencial) {
        String dataGeracao = new SimpleDateFormat("ddMMyyyy").format(new Date());
        String codigoConvenio = creditoSalario.getContaBancariaEntidade().getCodigoDoConvenio();
        String numeroRemessa = StringUtil.cortaOuCompletaEsquerda(sequencial + "", 6, "0");
        String nomeArquivo = "ACC." + dataGeracao + "." + codigoConvenio + "." + numeroRemessa + ".REM";
        return nomeArquivo;
    }


    private void gerarArquivoDoRelatorio(ByteArrayOutputStream bytes, CreditoSalario selecionado, ItemCreditoSalario itemCreditoSalario) throws Exception {
        Arquivo arquivo = new Arquivo();
        arquivo.setDescricao("CONFERÊNCIA INDIVIDUAL DE CRÉDITO");
        arquivo.setNome("Relatório Conferência " + itemCreditoSalario.getGrupoRecursoFP().getNome() + ".pdf");
        arquivo.setMimeType("application/pdf");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes.toByteArray());
        arquivo.setTamanho(Long.valueOf(byteArrayInputStream.available()));

        arquivo = arquivoFacade.retornaArquivoSalvo(arquivo, byteArrayInputStream);
        itemCreditoSalario.getArquivos().add(criarItemArquivo(TipoArquivoCreditoSalario.RELATORIO, itemCreditoSalario, arquivo));
        selecionado.setArquivoRelatorio(arquivo);
    }

    private List<ParametrosRelatorioConferenciaCreditoSalario> getRelatorio(CreditoSalario selecionado) {
        return Lists.newArrayList(selecionado.getParametrosRelatorioConferenciaCreditoSalario());
    }

    private ByteArrayOutputStream getRelatorioEmBytes(CreditoSalario selecionado, DependenciasDirf dependenciasCreditoSalario) throws JRException, IOException {
        HashMap parameter = new HashMap();
        AbstractReport abstractReport = AbstractReport.getAbstractReport();
        abstractReport.setGeraNoDialog(true);
        String jasper = "RelatorioConferenciaCreditoSalario.jasper";
        String nomeRelatorio = "Conferência do Arquivo Crédito Salário";
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(getRelatorio(selecionado));

        parameter.put("IMAGEM", dependenciasCreditoSalario.getCaminhoImagem());
        parameter.put("MUNICIPIO", selecionado.getContaBancariaEntidade().getEntidade().getNome());
        parameter.put("MODULO", "Recursos Humanos");
        parameter.put("SECRETARIA", "Departamento de Recursos Humano");
        parameter.put("NOMERELATORIO", nomeRelatorio);
        parameter.put("USUARIO", dependenciasCreditoSalario.getUsuarioSistema().getNome());
        parameter.put("SUBREPORT_DIR", dependenciasCreditoSalario.getCaminhoRelatorio());
        parameter.put("COMPETENCIA", "Competência: " + selecionado.getCompetenciaFP().toString());
        parameter.put("FOLHADEPAGAMENTO", selecionado.getFolhaDePagamento().toString());
        return abstractReport.gerarBytesEmPdfDeRelatorioComDadosEmCollectionView(dependenciasCreditoSalario.getCaminhoRelatorio(), jasper, parameter, dataSource);
    }

    private void carregarContasCorrenteDaPessoa(CreditoSalarioCaixaDetalheSegmentoA detalheSegmento) {
        detalheSegmento.getVinculoFP().getMatriculaFP().getPessoa().setContaCorrenteBancPessoas(contaCorrenteBancPessoaFacade.buscarContasCorrenteBancPorPessoaFisica(detalheSegmento.getVinculoFP().getMatriculaFP().getPessoa()));
    }

    private void validarContaCorrenteBancaria(ContaCorrenteBancaria cc, CreditoSalario selecionado, CreditoSalarioCaixaDetalheSegmentoA detalheSegmento, DependenciasDirf dependenciasCreditoSalario, int contadorAtual, GrupoRecursoFP grupoRecurso) throws ExcecaoNegocioGenerica {
        LogCreditoSalario log = new LogCreditoSalario();
        HierarquiaOrganizacional hierarquiaOrganizacional = grupoRecurso.getHierarquiaOrganizacional();
        String codigoHO = hierarquiaOrganizacional.getCodigo();
        log.setOrgao(codigoHO + " - " + grupoRecurso.getNome());

        PessoaFisica pessoa = detalheSegmento.getVinculoFP().getMatriculaFP().getPessoa();
        if (pessoa != null && pessoa.getCpf() == null) {
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ERRO, "<b>" + StringUtil.cortaOuCompletaEsquerda(contadorAtual + "", 5, "0") + " - Vinculo...: " + detalheSegmento.getVinculoFP() + "</b>");
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ERRO, StringUtil.cortaOuCompletaEsquerda(contadorAtual + "", 5, "0") + " - Pessoa....: " + detalheSegmento.getVinculoFP().getMatriculaFP().getPessoa());
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ERRO, StringUtil.cortaOuCompletaEsquerda(contadorAtual + "", 5, "0") + " - Problema..: CPF -  CPF da pessoa está vazio.");

            log.setCreditoSalario(selecionado);
            log.setVinculoFP(em.find(VinculoFP.class, detalheSegmento.getVinculoFP().getId()));
            log.setValorAReceber(new BigDecimal(detalheSegmento.getValorPagamento()));
            log.setLog("CPF -  Não foi possível encontrar o CPF da pessoa.");
            selecionado.getItemLogCreditoSalario().add(log);
            throw new ExcecaoNegocioGenerica("CPF inválido, verifique os detalhes no log da geração do arquivo");
        }

        if (cc == null) {
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ERRO, "<b>" + StringUtil.cortaOuCompletaEsquerda(contadorAtual + "", 5, "0") + " - Vinculo...: " + detalheSegmento.getVinculoFP() + "</b>");
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ERRO, StringUtil.cortaOuCompletaEsquerda(contadorAtual + "", 5, "0") + " - Pessoa....: " + detalheSegmento.getVinculoFP().getMatriculaFP().getPessoa());
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ERRO, StringUtil.cortaOuCompletaEsquerda(contadorAtual + "", 5, "0") + " - Problema..: CONTA CORRENTE -  Não foi possível localizar conta corrente tanto no cadastro do ContratoFP como no cadastro de Pessoa.");
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ERRO, StringUtil.cortaOuCompletaEsquerda(contadorAtual + "", 5, "0") + " - Detalhes..: " + "O sistema busca na seguinte ordem: ContaCorrente informada no vinculo, qualquer conta corrente ativa no cadastro de pessoal e que seja do mesmo banco selecionado no crédito de salário.<br />");

            log.setCreditoSalario(selecionado);
            log.setVinculoFP(em.find(VinculoFP.class, detalheSegmento.getVinculoFP().getId()));
            log.setValorAReceber(new BigDecimal(detalheSegmento.getValorPagamento()));
            log.setLog("CONTA CORRENTE -  Não foi possível localizar conta corrente tanto no cadastro do ContratoFP como no cadastro de Pessoa");
            selecionado.getItemLogCreditoSalario().add(log);
            throw new ExcecaoNegocioGenerica("Conta corrente bancária inválida, verifique os detalhes no log da geração do arquivo");
        }

        if (selecionado.isTipoArquivoServidor() && hasMesmoTipoDeConta(cc, selecionado)) {
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ERRO, "<b>" + StringUtil.cortaOuCompletaEsquerda(contadorAtual + "", 5, "0") + " - Vinculo..: " + detalheSegmento.getVinculoFP() + "</b>");
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ERRO, StringUtil.cortaOuCompletaEsquerda(contadorAtual + "", 5, "0") + " - Pessoa...: " + detalheSegmento.getVinculoFP().getMatriculaFP().getPessoa());
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ERRO, StringUtil.cortaOuCompletaEsquerda(contadorAtual + "", 5, "0") + " - Problema.: O tipo de Conta não é equivalente ao " + selecionado.getConfiguracaoCreditoSalario().getModalidadeConta() + ".<br />");

            log.setCreditoSalario(selecionado);
            log.setVinculoFP(em.find(VinculoFP.class, detalheSegmento.getVinculoFP().getId()));
            log.setContaCorrenteBancaria(cc);
            log.setValorAReceber(new BigDecimal(detalheSegmento.getValorPagamento()));
            log.setLog("O tipo de Conta não é equivalente ao " + selecionado.getConfiguracaoCreditoSalario().getModalidadeConta());
            selecionado.getItemLogCreditoSalario().add(log);

            throw new ExcecaoNegocioGenerica("Conta corrente bancária inválida, verifique os detalhes no log da geração do arquivo");
        }
    }

    private boolean hasMesmoTipoDeConta(ContaCorrenteBancaria cc, CreditoSalario selecionado) {
        if (selecionado.getConfiguracaoCreditoSalario().getModalidadeConta() == null) {
            return false;
        }
        if (!selecionado.getConfiguracaoCreditoSalario().getModalidadeConta().equals(cc.getModalidadeConta())) {
            return true;
        }
        return false;
    }

    private void validarValorDaFichaFinanceira(ContaCorrenteBancaria cc, CreditoSalario selecionado, BigDecimal valor, CreditoSalarioCaixaDetalheSegmentoA detalheSegmento, DependenciasDirf dependenciasCreditoSalario, int contadorAtual, GrupoRecursoFP grupoRecurso) throws ExcecaoNegocioGenerica {
        LogCreditoSalario log = new LogCreditoSalario();
        HierarquiaOrganizacional hierarquiaOrganizacional = grupoRecurso.getHierarquiaOrganizacional();
        String codigoHO = hierarquiaOrganizacional.getCodigo();
        log.setOrgao(codigoHO + " - " + grupoRecurso.getNome());

        if (valor == null) {
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ERRO, "<b>" + StringUtil.cortaOuCompletaEsquerda(contadorAtual + "", 5, "0") + " - Vinculo..: " + detalheSegmento.getVinculoFP() + "</b>");
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ERRO, StringUtil.cortaOuCompletaEsquerda(contadorAtual + "", 5, "0") + " - Pessoa...: " + detalheSegmento.getVinculoFP().getMatriculaFP().getPessoa());
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ERRO, StringUtil.cortaOuCompletaEsquerda(contadorAtual + "", 5, "0") + " - Problema..: " + "Valor liquido da ficha financeira está nulo, verifique as operações e tente novamente.<br />");

            log.setCreditoSalario(selecionado);
            log.setVinculoFP(em.find(VinculoFP.class, detalheSegmento.getVinculoFP().getId()));
            log.setContaCorrenteBancaria(cc);
            log.setValorAReceber(new BigDecimal(detalheSegmento.getValorPagamento()));
            log.setLog("Valor liquido da ficha financeira está nulo, verifique as operações e tente novamente");
            selecionado.getItemLogCreditoSalario().add(log);

            throw new ExcecaoNegocioGenerica("Conta corrente bancária inválida, verifique os detalhes no log da geração do arquivo");
        }

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ERRO, "<b>" + StringUtil.cortaOuCompletaEsquerda(contadorAtual + "", 5, "0") + " - Vinculo..: " + detalheSegmento.getVinculoFP() + "</b>");
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ERRO, StringUtil.cortaOuCompletaEsquerda(contadorAtual + "", 5, "0") + " - Pessoa...: " + detalheSegmento.getVinculoFP().getMatriculaFP().getPessoa());
            addLog(dependenciasCreditoSalario, DependenciasDirf.TipoLog.ERRO, StringUtil.cortaOuCompletaEsquerda(contadorAtual + "", 5, "0") + " - Problema..: " + "Valor liquido da ficha financeira está zerado/negativo (R$ " + Util.reaisToString(valor) + "), verifique as operações e tente novamente.<br />");

            log.setCreditoSalario(selecionado);
            log.setVinculoFP(em.find(VinculoFP.class, detalheSegmento.getVinculoFP().getId()));
            log.setContaCorrenteBancaria(cc);
            log.setValorAReceber(new BigDecimal(detalheSegmento.getValorPagamento()));
            log.setLog("Valor liquido da ficha financeira está zerado/negativo (R$ " + Util.reaisToString(valor) + "), verifique as operações e tente novamente");
            selecionado.getItemLogCreditoSalario().add(log);

            throw new ExcecaoNegocioGenerica("Conta corrente bancária inválida, verifique os detalhes no log da geração do arquivo");
        }
    }

    public String getOperacaoDaConta(ContaCorrenteBancaria contaCorrenteBancaria, CreditoSalario creditoSalario) {
        switch (contaCorrenteBancaria.getModalidadeConta()) {

            case CONTA_CORRENTE:
                return creditoSalario.isTipoArquivoServidor() ? "037" : "001";
            case CONTA_POUPANCA:
                return "013";
            case CONTA_SALARIO:
                return "037";
            case CONTA_SALARIO_NSGD:
                return "";
            default:
                return "037";
        }
    }

    public VinculoFPFacade getVinculoFPFacade() {
        return vinculoFPFacade;
    }

    public ContaCorrenteBancPessoaFacade getContaCorrenteBancPessoaFacade() {
        return contaCorrenteBancPessoaFacade;
    }

    public FichaFinanceiraFPFacade getFichaFinanceiraFPFacade() {
        return fichaFinanceiraFPFacade;
    }

    public FolhaDePagamentoFacade getFolhaPagamentoFacade() {
        return folhaPagamentoFacade;
    }

    public CompetenciaFPFacade getCompetenciaFPFacade() {
        return competenciaFPFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public MatriculaFPFacade getMatriculaFPFacade() {
        return matriculaFPFacade;
    }

    public RecursoFPFacade getRecursoFPFacade() {
        return recursoFPFacade;
    }

    public ContaBancariaEntidadeFacade getContaBancariaEntidadeFacade() {
        return contaBancariaEntidadeFacade;
    }

    public GrupoRecursoFPFacade getGrupoRecursoFPFacade() {
        return grupoRecursoFPFacade;
    }

    public EnderecoFacade getEnderecoFacade() {
        return enderecoFacade;
    }

    public ContaCorrenteBancariaFacade getContaCorrenteBancariaFacade() {
        return contaCorrenteBancariaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public CreditoSalarioAcompanhamentoFacade getCreditoSalarioAcompanhamentoFacade() {
        return creditoSalarioAcompanhamentoFacade;
    }

    public Comparator<? super Object[]> getOrdenador() {
        return new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Object[] s1 = (Object[]) o1;
                Object[] s2 = (Object[]) o2;
                if (((VinculoFP) s1[0]).getMatriculaFP().getPessoa().getNome() != null && ((VinculoFP) s2[0]).getMatriculaFP().getPessoa().getNome() != null) {
                    return ((VinculoFP) s1[0]).getMatriculaFP().getPessoa().getNome().compareTo(((VinculoFP) s2[0]).getMatriculaFP().getPessoa().getNome());
                } else {
                    return 0;
                }
            }
        };
    }

    @Override
    public CreditoSalario recuperar(Object id) {
        CreditoSalario creditoSalario = em.find(CreditoSalario.class, id);
        creditoSalario.getItemLogCreditoSalario().size();
        for (ItemCreditoSalario itemCreditoSalario : creditoSalario.getItensCreditoSalario()) {
            itemCreditoSalario.getArquivos().size();
        }
        return creditoSalario;
    }


    public Integer getSequencial() {
        return singletonCreditoSalario.getProximoCodigo();
    }

    public ContaCorrenteBancaria getContaCorrenteDaPensaoAlimenticia(VinculoFP vinculo, CreditoSalario selecionado) {
        List<BeneficioPensaoAlimenticia> beneficioPensaoAlimenticias = pensaoAlimenticiaFacade.buscarBeneficioPensaoAlimenticiaVigentePorPessoa(vinculo.getMatriculaFP().getPessoa(), selecionado.getDataCredito());
        if (beneficioPensaoAlimenticias != null && !beneficioPensaoAlimenticias.isEmpty()) {
            BeneficioPensaoAlimenticia beneficiario = beneficioPensaoAlimenticias.get(0);
            if (beneficiario.getContaCorrenteBancaria() != null) {
                return beneficiario.getContaCorrenteBancaria();
            }
        }
        return null;
    }

    public List<LogCreditoSalario> getInconsistencias(CreditoSalario creditoSalario) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT DISTINCT mat.MATRICULA || '/' || vinculo.numero AS matricula,  ");
        sql.append(" pf.nome AS nome, ");
        sql.append(" banco.NUMEROBANCO as banco, ");
        sql.append(" agencia.NUMEROAGENCIA || '-' || agencia.DIGITOVERIFICADOR as agencia, ");
        sql.append(" conta.NUMEROCONTA || '-' || conta.DIGITOVERIFICADOR as numeroconta, ");
        sql.append(" logcredito.LOG as log, ");
        sql.append(" logcredito.orgao, ");
        sql.append(" logcredito.valorAReceber as valor");
        sql.append(" from LOGCREDITOSALARIO logcredito ");
        sql.append(" inner join vinculofp vinculo on logcredito.VINCULOFP_ID = vinculo.id ");
        sql.append(" inner join matriculafp mat on vinculo.MATRICULAFP_ID = mat.id ");
        sql.append(" inner join pessoafisica pf on mat.PESSOA_ID = pf.id ");
        sql.append(" left join ContaCorrenteBancaria conta on logcredito.CONTACORRENTEBANCARIA_ID = conta.ID ");
        sql.append(" left join agencia on conta.AGENCIA_ID = agencia.id ");
        sql.append(" LEFT JOIN banco ON agencia.BANCO_ID = banco.id ");
        sql.append(" LEFT JOIN CREDITOSALARIO cs ON logcredito.CREDITOSALARIO_ID = cs.ID ");
        sql.append(" LEFT JOIN ITEMCREDITOSALARIO ic ON cs.ID = ic.CREDITOSALARIO_ID ");
        sql.append(" where logcredito.CREDITOSALARIO_ID = :idCreditoSalario order by pf.nome ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idCreditoSalario", creditoSalario.getId());
        List<Object[]> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            List<LogCreditoSalario> itemLog = Lists.newArrayList();
            for (Object[] obj : resultado) {
                LogCreditoSalario log = new LogCreditoSalario();
                log.setMatricula((String) obj[0]);
                log.setNome((String) obj[1]);
                log.setBanco((String) obj[2]);
                log.setAgencia((String) obj[3]);
                log.setConta((String) obj[4]);
                log.setLog((String) obj[5]);
                log.setOrgao((String) obj[6]);
                log.setValorAReceber((BigDecimal) obj[7]);
                itemLog.add(log);
            }
            return itemLog;
        }
        return null;
    }

    public Date buscarDataPagamentoAtravesDoCreditoSalario(Integer mes, Integer ano, TipoFolhaDePagamento tipo) {
        String sql = " select credito.datacredito " +
            "from creditosalario credito " +
            "         inner join FOLHADEPAGAMENTO folha on credito.FOLHADEPAGAMENTO_ID = folha.ID " +
            "         inner join COMPETENCIAFP comp on credito.COMPETENCIAFP_ID = comp.ID AND folha.COMPETENCIAFP_ID = comp.id " +
            "         inner join exercicio ex on comp.EXERCICIO_ID = ex.ID " +
            "where comp.mes = :mes " +
            "  and ex.ano = :ano " +
            "  and credito.TIPOGERACAOCREDITOSALARIO = :tipoCredito " +
            "  and folha.TIPOFOLHADEPAGAMENTO = :tipoFolha ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("mes", Mes.getMesToInt(mes).getNumeroMesIniciandoEmZero());
        q.setParameter("ano", ano);
        q.setParameter("tipoFolha", tipo.name());
        q.setParameter("tipoCredito", TipoGeracaoCreditoSalario.SERVIDORES.name());

        List<Date> datas = q.getResultList();

        if (datas != null && !datas.isEmpty()) {
            return datas.get(0);
        }
        return null;
    }

    public List<CreditoSalario> buscarCreditosSalarioPorVinculoFP(VinculoFP vinculoFP) {
        String sql = " select distinct credito.* " +
            " from CreditoSalario credito " +
            "         inner join ItemCreditoSalario itemCredito on credito.ID = itemCredito.CREDITOSALARIO_ID " +
            "         inner join VinculoFPCreditoSalario itemVinculo on itemCredito.ID = itemVinculo.ITEMCREDITOSALARIO_ID " +
            " where itemVinculo.VINCULOFP_ID = :vinculo ";
        Query q = em.createNativeQuery(sql, CreditoSalario.class);
        q.setParameter("vinculo", vinculoFP.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public boolean existeCreditoSalarioPorFolhaDePagamento(FolhaDePagamento folhaDePagamento) {
        Long count = (Long) em.createQuery(
            "select count(credito) from CreditoSalario credito where credito.folhaDePagamento = :folha"
        ).setParameter("folha", folhaDePagamento).getSingleResult();
        return count > 0;
    }

}
