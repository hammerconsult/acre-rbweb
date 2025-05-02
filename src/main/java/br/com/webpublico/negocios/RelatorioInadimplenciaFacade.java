package br.com.webpublico.negocios;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by HardRock on 12/05/2017.
 */
@Stateless
public class RelatorioInadimplenciaFacade extends AbstractFacade<Inadimplencia> implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(RelatorioInadimplenciaFacade.class);
    private static BigDecimal ZERO_VIRGULA_ZERO_UM = new BigDecimal("0.01");
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private TipoAutonomoFacade tipoAutonomoFacade;
    @EJB
    private NaturezaJuridicaFacade naturezaJuridicaFacade;
    @EJB
    private ProcessoParcelamentoFacade processoParcelamentoFacade;

    public RelatorioInadimplenciaFacade() {
        super(Inadimplencia.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    private String montarFromBase() {
        return " from ParcelaValorDivida pvd " +
            " inner join SituacaoParcelaValorDivida spvd on spvd.id = pvd.situacaoAtual_id " +
            " inner join ValorDivida vd on vd.id = pvd.valorDivida_id " +
            " inner join Calculo calc on calc.id = vd.calculo_id ";
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteRelatorioInadimplencia> buscarInadimplenciasContribuinte(FiltroRelatorioInadimplencia filtro, String condicao) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct ")
            .append("  p.id as id,  ")
            .append("  pvd.id as idParcela, ")
            .append("  coalesce(pf.nome, pj.razaoSocial) as nome, ")
            .append("  coalesce(formatacpfcnpj(coalesce(pf.cpf,pj.cnpj)), ' ') as inscricao, ")
            .append("  case when pf.id is not null then 'F' else 'J' end as tipopessoa, ")
            .append("  pvd.valor as valor, ")
            .append("  vd.id as idValorDivida ")
            .append(montarFromBase())
            .append(" inner join CalculoPessoa cp on cp.calculo_id = calc.id and cp.id = (select max(cpes.id) from CalculoPessoa cpes where cpes.calculo_id = calc.id) ")
            .append(" inner join Pessoa p on p.id = cp.pessoa_id  ");
        if (filtro.getTipoPessoa() == null) {
            sql.append(" left join PessoaFisica pf on pf.id = p.id ");
            sql.append(" left join PessoaJuridica pj on pj.id = p.id ");
        } else if (filtro.isPessoaFisica()) {
            sql.append(" inner join PessoaFisica pf on pf.id = p.id ");
            sql.append(" left join PessoaJuridica pj on pj.id = p.id ");
        } else if (filtro.isPessoaJuridica()) {
            sql.append(" left join PessoaFisica pf on pf.id = p.id ");
            sql.append(" inner join PessoaJuridica pj on pj.id = p.id ");
        }
        sql.append(condicao);
        sql.append(" order by p.id, vd.id, pvd.id");

        Query q = em.createNativeQuery(sql.toString());
        List<Object[]> lista = q.getResultList();
        List<Inadimplencia> retorno = Lists.newArrayList();

        for (Object[] registro : lista) {
            Inadimplencia md = new Inadimplencia();
            md.setIdCadastro(((BigDecimal) registro[0]).longValue());
            if (!retorno.contains(md)) {
                md.setNome((String) registro[2]);
                md.setInscricaoCadastral((String) registro[3]);
                md.setTipoPessoa(registro[4].toString());
                md.setOrdenacao(filtro.getOrdenacao());
                md.setOrdenacaoInadimplencia(filtro.getOrdenacaoInadimplencia());
                retorno.add(md);
            } else {
                md = retorno.get(retorno.indexOf(md));
            }
            InadimplenciaParcelas parcela = new InadimplenciaParcelas();
            parcela.setIdParcela(((BigDecimal) registro[1]).longValue());
            parcela.setValor((BigDecimal) registro[5]);
            parcela.setInadimplencia(md);
            md.getParcelas().add(parcela);
        }
        somarParcelas(retorno);
        Collections.sort(retorno);
        if (retorno.size() > filtro.getQuantidadeInadimplencias()) {
            retorno = retorno.subList(0, filtro.getQuantidadeInadimplencias());
        }
        return new AsyncResult<>(new AssistenteRelatorioInadimplencia(retorno));
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteRelatorioInadimplencia> buscarInadimplenciasCadastroImobiliario(FiltroRelatorioInadimplencia filtro, String condicao) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select imo.id as id, ")
            .append("  pvd.id as idParcela, ")
            .append("  coalesce(pf.nome, pj.razaoSocial) as nome, ")
            .append("  imo.inscricaoCadastral as inscricao, ")
            .append("  case when pf.id is not null then 'F' else 'J' end as tipopessoa, ")
            .append("  pvd.valor as valor, ")
            .append("  vd.id as idValorDivida ")
            .append(montarFromBase())
            .append(" inner join CadastroImobiliario imo on calc.cadastro_id = imo.id and coalesce(imo.ativo,0) = 1 ")
            .append(" inner join Propriedade prop on prop.imovel_id = imo.id and prop.id = (select max(p.id) from Propriedade p where p.imovel_id = imo.id and current_date between p.inicioVigencia and coalesce(p.finalVigencia, current_date)) ")
            .append(" inner join Lote lote on lote.id = imo.lote_id")
            .append(" inner join Setor setor on setor.id = lote.setor_id")
            .append(" inner join Quadra quadra on quadra.id = lote.quadra_id")
            .append(" inner join Testada test on test.lote_id = lote.id and coalesce(test.principal,0) = 1")
            .append(" inner join Face face on face.id = test.face_id")
            .append(" left join LogradouroBairro logra on logra.id = face.logradouroBairro_id")
            .append(" left join Logradouro log on log.id = logra.logradouro_id ")
            .append(" left join Bairro bairro on bairro.id = logra.bairro_id")
            .append(" inner join Pessoa p on p.id = prop.pessoa_id  ")
            .append(" left join PessoaFisica pf on pf.id = p.id ")
            .append(" left join PessoaJuridica pj on pj.id = p.id ");
        sql.append(condicao);
        sql.append(" order by imo.id, vd.id, pvd.id");

        Query q = em.createNativeQuery(sql.toString());
        List<Object[]> lista = q.getResultList();
        List<Inadimplencia> retorno = Lists.newArrayList();

        for (Object[] registro : lista) {
            Inadimplencia md = new Inadimplencia();
            md.setIdCadastro(((BigDecimal) registro[0]).longValue());
            if (!retorno.contains(md)) {
                md.setNome((String) registro[2]);
                md.setInscricaoCadastral((String) registro[3]);
                md.setTipoPessoa(registro[4].toString());
                md.setOrdenacao(filtro.getOrdenacao());
                md.setOrdenacaoInadimplencia(filtro.getOrdenacaoInadimplencia());
                retorno.add(md);
            } else {
                md = retorno.get(retorno.indexOf(md));
            }
            InadimplenciaParcelas parcela = new InadimplenciaParcelas();
            parcela.setIdParcela(((BigDecimal) registro[1]).longValue());
            parcela.setValor((BigDecimal) registro[5]);
            parcela.setInadimplencia(md);
            md.getParcelas().add(parcela);
        }
        somarParcelas(retorno);
        Collections.sort(retorno);
        if (retorno.size() > filtro.getQuantidadeInadimplencias()) {
            retorno = retorno.subList(0, filtro.getQuantidadeInadimplencias());
        }
        return new AsyncResult<>(new AssistenteRelatorioInadimplencia(retorno));
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteRelatorioInadimplencia> buscarInadimplenciasCadastroEconomico(FiltroRelatorioInadimplencia filtro, String condicao) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select cmc.id as id, ")
            .append("  pvd.id as idParcela, ")
            .append("  coalesce(pf.nome, pj.razaoSocial) as nome, ")
            .append("  cmc.inscricaoCadastral as inscricao, ")
            .append("  case when pf.id is not null then 'F' else 'J' end as tipopessoa, ")
            .append("  pvd.valor as valor, ")
            .append("  vd.id as idValorDivida ")
            .append(montarFromBase())
            .append(" inner join cadastroeconomico cmc on calc.cadastro_id = cmc.id ")
            .append(" left join  EconomicoCNAE eCnae on eCnae.cadastroEconomico_id = cmc.id ")
            .append(" left join  CNAE cnae on cnae.ID = eCnae.cnae_id ")
            .append(" inner join Pessoa p on p.id = cmc.pessoa_id  ")
            .append(" left join PessoaFisica pf on pf.id = p.id ")
            .append(" left join PessoaJuridica pj on pj.id = p.id ");
        sql.append(condicao);
        sql.append(" order by cmc.id, vd.id, pvd.id");

        Query q = em.createNativeQuery(sql.toString());
        List<Object[]> lista = q.getResultList();
        List<Inadimplencia> retorno = Lists.newArrayList();

        for (Object[] registro : lista) {
            Inadimplencia md = new Inadimplencia();
            md.setIdCadastro(((BigDecimal) registro[0]).longValue());
            if (!retorno.contains(md)) {
                md.setNome((String) registro[2]);
                md.setInscricaoCadastral((String) registro[3]);
                md.setTipoPessoa(registro[4].toString());
                md.setOrdenacao(filtro.getOrdenacao());
                md.setOrdenacaoInadimplencia(filtro.getOrdenacaoInadimplencia());
                retorno.add(md);
            } else {
                md = retorno.get(retorno.indexOf(md));
            }
            InadimplenciaParcelas parcela = new InadimplenciaParcelas();
            parcela.setIdParcela(((BigDecimal) registro[1]).longValue());
            parcela.setValor((BigDecimal) registro[5]);
            parcela.setInadimplencia(md);
            md.getParcelas().add(parcela);
        }
        somarParcelas(retorno);
        Collections.sort(retorno);
        if (retorno.size() > filtro.getQuantidadeInadimplencias()) {
            retorno = retorno.subList(0, filtro.getQuantidadeInadimplencias());
        }
        return new AsyncResult<>(new AssistenteRelatorioInadimplencia(retorno));
    }


    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteRelatorioInadimplencia> buscarInadimplenciasCadastroRural(FiltroRelatorioInadimplencia filtro, String condicao) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select rural.id as id, ")
            .append("  pvd.id as idParcela, ")
            .append("  coalesce(pf.nome, pj.razaoSocial) as nome, ")
            .append("  rural.codigo as inscricao, ")
            .append("  case when pf.id is not null then 'F' else 'J' end as tipopessoa, ")
            .append("  pvd.valor as valor, ")
            .append("  vd.id as idValorDivida ")
            .append(montarFromBase())
            .append(" inner join CadastroRural rural on calc.cadastro_id = rural.id ")
            .append(" inner join PropriedadeRural prop on prop.imovel_id = rural.id ")
            .append(" inner join Pessoa p on p.id = prop.pessoa_id  ")
            .append(" left join PessoaFisica pf on pf.id = p.id ")
            .append(" left join PessoaJuridica pj on pj.id = p.id ");
        sql.append(condicao);
        sql.append(" order by rural.id, vd.id, pvd.id");

        Query q = em.createNativeQuery(sql.toString());
        List<Object[]> lista = q.getResultList();
        List<Inadimplencia> retorno = Lists.newArrayList();

        for (Object[] registro : lista) {
            Inadimplencia md = new Inadimplencia();
            md.setIdCadastro(((BigDecimal) registro[0]).longValue());
            if (!retorno.contains(md)) {
                md.setNome((String) registro[2]);
                md.setInscricaoCadastral(((BigDecimal) registro[3]).toString());
                md.setTipoPessoa(registro[4].toString());
                md.setOrdenacao(filtro.getOrdenacao());
                md.setOrdenacaoInadimplencia(filtro.getOrdenacaoInadimplencia());
                retorno.add(md);
            } else {
                md = retorno.get(retorno.indexOf(md));
            }
            InadimplenciaParcelas parcela = new InadimplenciaParcelas();
            parcela.setIdParcela(((BigDecimal) registro[1]).longValue());
            parcela.setValor((BigDecimal) registro[5]);
            parcela.setInadimplencia(md);
            md.getParcelas().add(parcela);
        }
        somarParcelas(retorno);
        Collections.sort(retorno);
        if (retorno.size() > filtro.getQuantidadeInadimplencias()) {
            retorno = retorno.subList(1, filtro.getQuantidadeInadimplencias());
        }
        return new AsyncResult<>(new AssistenteRelatorioInadimplencia(retorno));
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<TotalizadorInadimplenciaPorDivida> calcularTotalizadorDivida(List<Inadimplencia> inadimplencias) {
        List<TotalizadorInadimplenciaPorDivida> totalizador = Lists.newArrayList();
        if (inadimplencias != null && !inadimplencias.isEmpty()) {
            for (Inadimplencia inadimplencia : inadimplencias) {
                for (InadimplenciaParcelas parcela : inadimplencia.getParcelas()) {
                    TotalizadorInadimplenciaPorDivida total = new TotalizadorInadimplenciaPorDivida(parcela.getDescricaoDivida(), parcela.getValor());
                    if (totalizador.contains(total)) {
                        totalizador.get(totalizador.indexOf(total)).somar(parcela.getValor());
                    } else {
                        totalizador.add(total);
                    }
                }
            }
        }
        return totalizador;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 12)
    public Future<AssistenteRelatorioInadimplencia> atualizarValoresDevidos(FiltroRelatorioInadimplencia filtro, AssistenteBarraProgresso assistente, List<Inadimplencia> inadimplencias) {
        List<Inadimplencia> listaRetorno = Lists.newArrayList();
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (Inadimplencia inadimplencia : inadimplencias) {
            if (filtro.getValoresAtualizados()) {
                listaRetorno.add(atualizarValor(inadimplencia, filtro.getTipoDebito()));
            }
            assistente.conta();
            valorTotal = valorTotal.add(inadimplencia.getValor());
        }
        Collections.sort(listaRetorno);
        return new AsyncResult<>(new AssistenteRelatorioInadimplencia(listaRetorno));
    }

    private List<ResultadoParcela> pesquisarParcelamentoDividaAtiva(List<ResultadoParcela> parcelas) {
        List<Long> listaIdsCalculoParcelamento = recuperarParcelamentosDasParcelasFiltradas(parcelas);
        if (!listaIdsCalculoParcelamento.isEmpty()) {
            return adicionarParcelamentos(listaIdsCalculoParcelamento, parcelas);
        }
        return Lists.newArrayList();
    }

    private List<Long> recuperarParcelamentosDasParcelasFiltradas(List<ResultadoParcela> parcelas) {
        List<Long> listaIdsCalculoParcelamento = Lists.newArrayList();
        for (ResultadoParcela resultado : parcelas) {
            List<Long> parcelamentos = processoParcelamentoFacade.recuperarIDDoParcelamentoDaParcelaOriginal(resultado.getIdParcela());
            if (!parcelamentos.isEmpty()) {
                for (Long id : parcelamentos) {
                    if (!listaIdsCalculoParcelamento.contains(id)) {
                        listaIdsCalculoParcelamento.add(id);
                    }
                }
            }
        }
        return listaIdsCalculoParcelamento;
    }

    private List<ResultadoParcela> adicionarParcelamentos(List<Long> listaIdsCalculoParcelamento, List<ResultadoParcela> parcelas) {
        List<ResultadoParcela> parcelasDoParcelamentoOriginado = Lists.newArrayList();
        if (!listaIdsCalculoParcelamento.isEmpty()) {
            ConsultaParcela cp = new ConsultaParcela();
            List<List<Long>> dividido = Lists.partition(listaIdsCalculoParcelamento, 1000);
            for (List<Long> novosIds : dividido) {
                cp.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, novosIds);
            }
            cp.executaConsulta();
            for (ResultadoParcela rp : cp.getResultados()) {
                if (!parcelas.contains(rp) && !parcelasDoParcelamentoOriginado.contains(rp)) {
                    parcelasDoParcelamentoOriginado.add(rp);

                    LinkedList<ResultadoParcela> listaRp = Lists.newLinkedList();
                    listaRp.add(rp);
                    parcelasDoParcelamentoOriginado.addAll(pesquisarParcelamentoDividaAtiva(listaRp));
                }
            }
        }
        return parcelasDoParcelamentoOriginado;
    }

    private void ordenarParcelas(List<ResultadoParcela> parcelas) {
        Comparator<ResultadoParcela> comparator = new Comparator<ResultadoParcela>() {
            @Override
            public int compare(ResultadoParcela um, ResultadoParcela dois) {
                int i = um.getTipoCadastro().compareTo(dois.getTipoCadastro());
                if (i != 0) {
                    return i;
                }
                i = um.getCadastro().compareTo(dois.getCadastro());
                if (i != 0) {
                    return i;
                }
                i = um.getExercicio().compareTo(dois.getExercicio());
                if (i != 0) {
                    return i;
                }
                i = um.getIdValorDivida().compareTo(dois.getIdValorDivida());
                if (i != 0) {
                    return i;
                }
                i = um.getSequenciaParcela().compareTo(dois.getSequenciaParcela());
                if (i != 0) {
                    return i;
                }
                i = um.getVencimento().compareTo(dois.getVencimento());
                if (i != 0) {
                    return i;
                }
                i = um.getSd().compareTo(dois.getSd());
                if (i != 0) {
                    return i;
                }
                i = um.getIdParcela().compareTo(dois.getIdParcela());
                if (i != 0) {
                    return i;
                }
                return 0;
            }
        };
        Collections.sort(parcelas, comparator);
    }

    private Inadimplencia atualizarValor(Inadimplencia inadimplente, FiltroRelatorioInadimplencia.TipoDebito tipoDebito) {
        List<ResultadoParcela> parcelasDoInadimplente = Lists.newArrayList();
        for (InadimplenciaParcelas parcela : inadimplente.getParcelas()) {
            ConsultaParcela consulta = new ConsultaParcela();
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, parcela.getIdParcela());
            consulta.executaConsulta();

            ResultadoParcela resultadoParcela = consulta.getResultados().get(0);
            if (resultadoParcela != null) {
                parcela.setImposto(resultadoParcela.getValorImposto());
                parcela.setTaxa(resultadoParcela.getValorTaxa());
                parcela.setJuros(resultadoParcela.getValorJuros());
                parcela.setMulta(resultadoParcela.getValorMulta());
                parcela.setCorrecao(resultadoParcela.getValorCorrecao());
                parcela.setHonorarios(resultadoParcela.getValorHonorarios());
                parcela.setDesconto(resultadoParcela.getValorDesconto());
                parcela.setValor(resultadoParcela.getValorTotal());

                parcelasDoInadimplente.add(resultadoParcela);
            }
        }

        if (FiltroRelatorioInadimplencia.TipoDebito.PARCELAMENTO_DIVIDA_ATIVA.equals(tipoDebito)) {
            parcelasDoInadimplente.addAll(pesquisarParcelamentoDividaAtiva(parcelasDoInadimplente));
        }

        ordenarParcelas(parcelasDoInadimplente);
        inadimplente.zeraValores();

        BigDecimal valorVencido = BigDecimal.ZERO;
        List<ResultadoParcela> parcelasAgrupadas = agruparParcelasPorValorDivida(parcelasDoInadimplente);
        for (ResultadoParcela parcela : parcelasAgrupadas) {
            if (parcela.isVencido(new Date())) {
                valorVencido = valorVencido.add(parcela.getValorTotal());
            }
            inadimplente.setValor(inadimplente.getValor().add(parcela.getValorTotal()));
            inadimplente.setImposto(inadimplente.getImposto().add(parcela.getValorImposto()));
            inadimplente.setTaxa(inadimplente.getTaxa().add(parcela.getValorTaxa()));
            inadimplente.setDesconto(inadimplente.getDesconto().add(parcela.getValorDesconto()));
            inadimplente.setJuros(inadimplente.getJuros().add(parcela.getValorJuros()));
            inadimplente.setMulta(inadimplente.getMulta().add(parcela.getValorMulta()));
            inadimplente.setCorrecao(inadimplente.getCorrecao().add(parcela.getValorCorrecao()));
            inadimplente.setHonorarios(inadimplente.getHonorarios().add(parcela.getValorHonorarios()));

            inadimplente.getParcelas().add(new InadimplenciaParcelas(inadimplente, parcela));
        }
        inadimplente.setPercentual(valorVencido.multiply(BigDecimal.valueOf(100)).divide(inadimplente.getValor(), BigDecimal.ROUND_HALF_UP));
        return inadimplente;
    }

    private Inadimplencia atualizarValor(Inadimplencia inadimplente, FiltroRelatorioInadimplencia filtro) {
        try {
            ConsultaParcela consulta = new ConsultaParcela();
            if (!filtro.isCadastroContribuinte()) {
                consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, inadimplente.getIdCadastro());
            } else {
                consulta.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, inadimplente.getIdCadastro());
            }
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MAIOR_IGUAL, filtro.getVencimentoInicial());
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR_IGUAL, filtro.getVencimentoFinal());
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
            if (!filtro.getListaDividas().isEmpty()) {
                consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IN, filtro.getListaIdDividas());
            }
            if (FiltroRelatorioInadimplencia.TipoDebito.DO_EXERCICIO.equals(filtro.getTipoDebito())) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, false);
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, false);
            }
            if (FiltroRelatorioInadimplencia.TipoDebito.DIVIDA_ATIVA.equals(filtro.getTipoDebito())) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, true);
            }
            if (FiltroRelatorioInadimplencia.TipoDebito.DIVIDA_ATIVA_AJUIZADA.equals(filtro.getTipoDebito())) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, true);
            }
            consulta.executaConsulta();
            inadimplente.zeraValores();

            List<ResultadoParcela> parcelasAgrupadas = agruparParcelasPorValorDivida(consulta.getResultados());
            BigDecimal valorVencido = BigDecimal.ZERO;
            for (ResultadoParcela resultado : parcelasAgrupadas) {
                if (resultado.isVencido(new Date())) {
                    valorVencido = valorVencido.add(resultado.getValorTotal());
                }
                inadimplente.setValor(inadimplente.getValor().add(resultado.getValorTotal()));
                if (filtro.getDetalhado()) {
                    inadimplente.setImposto(inadimplente.getImposto().add(resultado.getValorImposto()));
                    inadimplente.setTaxa(inadimplente.getTaxa().add(resultado.getValorTaxa()));
                    inadimplente.setDesconto(inadimplente.getDesconto().add(resultado.getValorDesconto()));
                    inadimplente.setJuros(inadimplente.getJuros().add(resultado.getValorJuros()));
                    inadimplente.setMulta(inadimplente.getMulta().add(resultado.getValorMulta()));
                    inadimplente.setCorrecao(inadimplente.getCorrecao().add(resultado.getValorCorrecao()));
                    inadimplente.setHonorarios(inadimplente.getHonorarios().add(resultado.getValorHonorarios()));
                }
            }
            inadimplente.setPercentual(valorVencido.multiply(BigDecimal.valueOf(100)).divide(inadimplente.getValor(), BigDecimal.ROUND_HALF_UP));
        } catch (Exception e) {
            log.error("Não foi possível calcular os acréscimos  " + e.getMessage());
        }
        return inadimplente;
    }


    public List<ResultadoParcela> agruparParcelasPorValorDivida(List<ResultadoParcela> parcelas) {

        Map<Long, List<ResultadoParcela>> parcelaPorVd = Maps.newHashMap();

        for (ResultadoParcela resultado : parcelas) {
            if (!parcelaPorVd.containsKey(resultado.getIdValorDivida())) {
                parcelaPorVd.put(resultado.getIdValorDivida(), new ArrayList<ResultadoParcela>());
            }
            parcelaPorVd.get(resultado.getIdValorDivida()).add(resultado);
        }
        List<ResultadoParcela> parcelasSoma = Lists.newArrayList();
        for (Long idValorDivida : parcelaPorVd.keySet()) {
            List<ResultadoParcela> parcelasDaDivida = Lists.newArrayList();
            for (ResultadoParcela resultadoParcela : parcelaPorVd.get(idValorDivida)) {
                if (resultadoParcela.getCotaUnica()
                    && !resultadoParcela.getVencido()
                    && (resultadoParcela.getSituacaoNameEnum().equals(SituacaoParcela.EM_ABERTO.name()))) {
                    parcelasDaDivida.clear();
                    parcelasDaDivida.add(resultadoParcela);
                    break;
                } else if (!resultadoParcela.getCotaUnica()) {
                    parcelasDaDivida.add(resultadoParcela);
                }
            }
            parcelasSoma.addAll(parcelasDaDivida);
        }
        return parcelasSoma;
    }

    public void gerarRelatorio(FiltroRelatorioInadimplencia filtro, List<Inadimplencia> inadimplencias, List<TotalizadorInadimplenciaPorDivida> totalizadorDivida, String tituloRelatorio) {
        AbstractReport report = AbstractReport.getAbstractReport();

        Collections.sort(totalizadorDivida);

        HashMap parameters = new HashMap();
        parameters.put("NOME_PREFEITURA", "PREFEITURA MUNICIPAL DE RIO BRANCO");
        parameters.put("NOME_ESTADO", "ESTADO DO ACRE");
        parameters.put("NOME_ORGAO", "Secretaria Municipal de Desenvolvimento Econômico e Finanças");
        parameters.put("BRASAO", report.getCaminhoImagem());
        parameters.put("USUARIO", SistemaFacade.obtemLogin());
        parameters.put("SUBREPORT_DIR", report.getCaminho());
        parameters.put("IP", SistemaFacade.obtemIp());
        parameters.put("FILTROS", filtro.getFiltros());
        parameters.put("NOME_RELATORIO", tituloRelatorio);
        parameters.put("DETALHADO", filtro.getDetalhado());
        parameters.put("TIPO_CADASTRO", filtro.getTipoCadastroTributario().name());
        parameters.put("TOTALIZADORDIVIDA", totalizadorDivida);

        Collections.sort(inadimplencias);
        try {
            report.gerarRelatorioComDadosEmCollection("RelatorioInadimplencia.jasper", parameters, new JRBeanCollectionDataSource(inadimplencias));
        } catch (JRException e) {
            log.error("Erro ao emitir o relatorio: ", e);
        } catch (IOException e) {
            log.error("Erro ao emitir o relatorio: ", e);
        }
    }

    private void somarParcelas(List<Inadimplencia> inadimplencias) {
        for (Inadimplencia inadimplencia : inadimplencias) {
            BigDecimal valorTotal = BigDecimal.ZERO;
            for (InadimplenciaParcelas parcela : inadimplencia.getParcelas()) {
                valorTotal = valorTotal.add(parcela.getValor());
            }
            inadimplencia.setValor(valorTotal);
        }
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public LogradouroFacade getLogradouroFacade() {
        return logradouroFacade;
    }

    public BairroFacade getBairroFacade() {
        return bairroFacade;
    }

    public TipoAutonomoFacade getTipoAutonomoFacade() {
        return tipoAutonomoFacade;
    }

    public NaturezaJuridicaFacade getNaturezaJuridicaFacade() {
        return naturezaJuridicaFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

}
