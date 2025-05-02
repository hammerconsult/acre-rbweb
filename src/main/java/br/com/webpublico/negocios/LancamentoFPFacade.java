/*
 * Codigo gerado automaticamente em Fri Sep 02 15:57:15 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoFP;
import br.com.webpublico.entidadesauxiliares.ImportaMovimentoFinanceiro;
import br.com.webpublico.entidadesauxiliares.ItemConsignacao;
import br.com.webpublico.entidadesauxiliares.rh.portal.ConsignacaoServidor;
import br.com.webpublico.entidadesauxiliares.rh.portal.ConsignacaoServidorDTO;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.ItemValorPrevidencia;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.YearMonth;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
public class LancamentoFPFacade extends AbstractFacade<LancamentoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ImportaArquivoEconsigAsyncFacade importaArquivoEconsigAsyncFacade;
    @EJB
    private LoteProcessamentoFacade loteProcessamentoFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private ArquivoEconsigFacade arquivoEconsigFacade;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    @EJB
    private PensionistaFacade pensionistaFacade;
    @EJB
    private BaseFPFacade baseFPFacade;


    public LancamentoFPFacade() {
        super(LancamentoFP.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean validaLancamento(LancamentoFP lancamentoFP) {
        List<LancamentoFP> lancamentoFPs = new ArrayList<>();
        Query q = em.createQuery("from LancamentoFP l where l.vinculoFP = :vinculo "
            + " and l.eventoFP = :evento and l.tipoFolhaDePagamento = :tipo ");
        q.setParameter("vinculo", lancamentoFP.getVinculoFP());
        q.setParameter("evento", lancamentoFP.getEventoFP());
        q.setParameter("tipo", lancamentoFP.getTipoFolhaDePagamento());
        lancamentoFPs = q.getResultList();
        for (LancamentoFP l : lancamentoFPs) {

            Date cadastroInicio = Util.getDataHoraMinutoSegundoZerado(lancamentoFP.getMesAnoInicial());
            Date cadastroFinal = Util.getDataHoraMinutoSegundoZerado(lancamentoFP.getMesAnoFinal());
            Date recuperadoInicio = Util.getDataHoraMinutoSegundoZerado(l.getMesAnoInicial());
            Date recuperadoFinal = Util.getDataHoraMinutoSegundoZerado(l.getMesAnoFinal());

            if ((recuperadoInicio.compareTo(cadastroInicio) == 0) || (recuperadoInicio.compareTo(cadastroFinal) == 0)) {
                if (!l.getId().equals(lancamentoFP.getId())) {
                    return true;
                }

            } else if ((recuperadoFinal.compareTo(cadastroInicio) == 0) || (recuperadoFinal.compareTo(cadastroFinal) == 0)) {
                if (!l.getId().equals(lancamentoFP.getId())) {
                    return true;
                }
            }

            // verifica se o novo período está dentro de um período já existente
            if ((lancamentoFP.getMesAnoInicial().after(l.getMesAnoInicial())) && (lancamentoFP.getMesAnoFinal().before(l.getMesAnoFinal()))) {
                return true;
            }
            // verifica se o novo período contém um período já existente
            if ((l.getMesAnoInicial().after(lancamentoFP.getMesAnoInicial())) && (l.getMesAnoFinal().before(lancamentoFP.getMesAnoFinal()))) {
                return true;
            }
        }
        return false;
    }

    public boolean validarLancamentoReajuste(LancamentoFP novo, LancamentoFP original) {
        List<LancamentoFP> lancamentoFPs = new ArrayList<>();
        Query q = em.createQuery("from LancamentoFP l where l.vinculoFP = :vinculo "
            + " and l.eventoFP = :evento and l.tipoFolhaDePagamento = :tipo ");
        q.setParameter("vinculo", novo.getVinculoFP());
        q.setParameter("evento", novo.getEventoFP());
        q.setParameter("tipo", novo.getTipoFolhaDePagamento());
        lancamentoFPs = q.getResultList();
        for (LancamentoFP l : lancamentoFPs) {
            if (l.equals(original)) {
                continue;
            }
            DateTime cadastroInicio = new DateTime(Util.getDataHoraMinutoSegundoZerado(novo.getMesAnoInicial()));
            DateTime cadastroFinal = new DateTime(Util.getDataHoraMinutoSegundoZerado(novo.getMesAnoFinal()));
            DateTime recuperadoInicio = new DateTime(Util.getDataHoraMinutoSegundoZerado(l.getMesAnoInicial()));
            DateTime recuperadoFinal = new DateTime(Util.getDataHoraMinutoSegundoZerado(l.getMesAnoFinal()));

            if ((recuperadoInicio.compareTo(cadastroInicio) == 0) || (recuperadoInicio.compareTo(cadastroFinal) == 0)) {
                if (!l.getId().equals(novo.getId())) {
                    return true;
                }

            } else if ((recuperadoFinal.compareTo(cadastroInicio) == 0) || (recuperadoFinal.compareTo(cadastroFinal) == 0)) {
                if (!l.getId().equals(novo.getId())) {
                    return true;
                }
            }

            if (new Interval(cadastroInicio, cadastroFinal).contains(recuperadoInicio) || new Interval(cadastroInicio, cadastroFinal).contains(recuperadoFinal)) {
                return true;
            }

            if (new Interval(recuperadoInicio, recuperadoFinal).contains(cadastroInicio) || new Interval(recuperadoInicio, recuperadoFinal).contains(cadastroFinal)) {
                return true;
            }

        }
        return false;
    }

    public void definirDatasNovoLancamento(LancamentoFP lancamentoFP) {
        List<LancamentoFP> lancamentoFPs = new ArrayList<>();
        Query q = em.createQuery("from LancamentoFP l where l.vinculoFP = :vinculo "
            + " and l.eventoFP = :evento order by l.mesAnoFinal desc");
        q.setParameter("vinculo", lancamentoFP.getVinculoFP());
        q.setParameter("evento", lancamentoFP.getEventoFP());
        lancamentoFPs = q.getResultList();
        if (lancamentoFPs.size() > 0) {
            LancamentoFP recuperado = lancamentoFPs.get(0);
            Calendar c = Calendar.getInstance();
            c.setTime(recuperado.getMesAnoFinal());
            c.add(Calendar.MONTH, 1);
            lancamentoFP.setMesAnoInicial(c.getTime());
            c.add(Calendar.MONTH, 1);
            lancamentoFP.setMesAnoFinal(c.getTime());
            lancamentoFP.setMesAnoInicioCalculo(lancamentoFP.getMesAnoInicial());
        }
    }

    public boolean podeSalvarLancamento(LancamentoFP c) {
        Query q = em.createQuery("from LancamentoFP c where "
            + "((:dataInicio "
            + "between c.mesAnoInicial and coalesce(c.mesAnoFinal,:dataInicio)) "
            + "or  (:dataFim between c.mesAnoInicial and coalesce(c.mesAnoFinal,:dataFim))) and c.vinculoFP = :vinculoFP");
        q.setParameter("vinculoFP", c.getVinculoFP());
        q.setParameter("dataInicio", c.getMesAnoInicial());
        q.setParameter("dataFim", retornaData(c.getMesAnoFinal()));
        return q.getResultList().isEmpty();
    }

    public Date retornaData(Date c) {
        if (c != null) {
            return c;
        } else {
            return Util.getDataHoraMinutoSegundoZerado(new Date());
        }
    }

    public Date getDataFinalLancamento(VinculoFP vinculo, Integer mes, Integer ano) {
        Query createQuery = em.createQuery("select max(lancamento.mesAnoFinal) from LancamentoFP lancamento"
            + " where lancamento.vinculoFP = :vinculo and "
            + " ((extract(month from :dataParam)) >= (extract(month from lancamento.mesAnoInicial)) and  (extract(year from :dataParam)) >= (extract(year from (lancamento.mesAnoInicial))))"
            + " and ((extract(month from :dataParam)) <= (extract(month from lancamento.mesAnoFinal)) and (extract(year from :dataParam)) <=  (extract(year from (coalesce(lancamento.mesAnoFinal, :dataParam)))))");
//        mes--;
        Date dataCalculo = Util.criaDataPrimeiroDiaDoMes(mes, ano);
        createQuery.setParameter("vinculo", vinculo);
        createQuery.setParameter("dataParam", dataCalculo);
        if (createQuery.getResultList().isEmpty()) {
            return null;
        }
        return (Date) createQuery.getSingleResult();
    }

    public List<LancamentoFP> listaFiltrandoX(String s, int inicio, int max, ContratoFP contratoFP, Date mesAno, EventoFP eventoFP) {
        String sql = " SELECT lancamento.* FROM lancamentoFP lancamento "
            + " INNER JOIN vinculoFP vinculo ON (vinculo.id = lancamento.vinculofp_id) "
            + " INNER JOIN matriculaFP matricula ON (matricula.id = vinculo.matriculafp_id) "
            + " INNER JOIN pessoaFisica pessoa ON (pessoa.id = matricula.pessoa_id) "
            + " INNER JOIN eventoFP evento ON (evento.id = lancamento.eventofp_id) "
            + " WHERE rownum < :registros ";

        String where = "";

        if (contratoFP != null) {
            where += " and vinculo.id = :contratoFP ";
        }

        if (eventoFP != null) {
//            if (!where.equals("")) {
            where += " and evento.id = :eventoFP ";
//            }

        }

        if (mesAno != null) {
//            if (!where.equals("")) {
            where += " and to_date(to_char(:mesAno,'mm/yyyy'),'mm/yyyy') between "
                + " to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') "
                + " and to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') ";
//            }

        }

//        where = " AND " + where;

        if (where.equals(" and ")) {
            where = " AND (((lower(pessoa.nome) LIKE :filtro) OR "
                + " (lower(pessoa.cpf) LIKE :filtro) OR "
                + " (lower(matricula.matricula) LIKE :filtro)) OR "
                + " ((lower(evento.codigo) LIKE :filtro) OR "
                + " (lower(evento.descricao) LIKE :filtro)) OR "
                + " (and to_date(to_char(:mesAno,'mm/yyyy'),'mm/yyyy') between " +
                "                 to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') " +
                "                 and to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') ) ) ";
        }

        String ordem = " ORDER BY pessoa.nome, lancamento.mesAnoInicial DESC, lancamento.mesAnoFinal DESC, evento.codigo ";
        Query q = getEntityManager().createNativeQuery(sql + where + ordem, LancamentoFP.class);

        if (where.contains(":filtro")) {
            q.setParameter("filtro", "%" + s.trim().toLowerCase() + "%");
        } else {

            if (mesAno != null) {
                q.setParameter("mesAno", mesAno);
            }

            if (eventoFP != null) {
                q.setParameter("eventoFP", eventoFP.getId());
            }

            if (contratoFP != null) {
                q.setParameter("contratoFP", contratoFP.getId());
            }
        }
        q.setParameter("registros", (max + inicio + 1));
        q.setMaxResults(max + inicio + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public boolean isLancamentoExist(LancamentoFP lancamento) {
        Query createQuery = em.createQuery("select lancamento from LancamentoFP lancamento "
            + "where lancamento.eventoFP.codigo = :codigoEvento "
            + "and lancamento.vinculoFP.id = :vinculo "
            + " and :dataParam >= lancamento.mesAnoInicial "
            + " and :dataParam <= coalesce(lancamento.mesAnoFinal,:dataParam)");
        createQuery.setParameter("codigo", lancamento.getEventoFP().getCodigo());
        createQuery.setParameter("vinculo", lancamento.getId());
        createQuery.setParameter("dataParam", lancamento.getMesAnoInicial());
        return !createQuery.getResultList().isEmpty();
    }


    public List<LancamentoFP> buscarLancamentosArquivoRetorno(ExportarArquivoRetorno exportarArquivoRetorno) {
        Query createQuery = em.createQuery("select lancamento from LancamentoFP lancamento inner join lancamento.vinculoFP vinculo "
            + "where lancamento.tipoFolhaDePagamento = :tipoFolha and lancamento.tipoImportacao = :tipo and to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between " +
            "                 to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') " +
            "                 and to_date(to_char(coalesce(lancamento.mesAnoFinal,:data),'mm/yyyy'),'mm/yyyy') order by lancamento.eventoFP.codigo, lancamento.vinculoFP.matriculaFP.pessoa.nome ");
        createQuery.setParameter("data", Util.criaDataPrimeiroDiaMesJoda(exportarArquivoRetorno.getMes(), exportarArquivoRetorno.getAno()).toDate());
        createQuery.setParameter("tipo", TipoImportacao.ECONSIG);
        createQuery.setParameter("tipoFolha", exportarArquivoRetorno.getTipoFolhaDePagamento());
        return createQuery.getResultList();
    }

    public List<LancamentoFP> buscarLancamentosPorVersao(ExportarArquivoRetorno exportarArquivoRetorno) {

        Query createQuery = em.createQuery("select lancamento from LancamentoFP lancamento inner join lancamento.vinculoFP vinculo "
            + "where lancamento.tipoFolhaDePagamento = :tipoFolha and lancamento.tipoImportacao = :tipo and to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between " +
            "                 to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') " +
            "                 and to_date(to_char(coalesce(lancamento.mesAnoFinal,:data),'mm/yyyy'),'mm/yyyy') " +
            "    and vinculo.id in(select distinct ficha.vinculoFP.id from ItemFichaFinanceiraFP item " +
            "                       inner join item.fichaFinanceiraFP ficha " +
            "                       inner join ficha.folhaDePagamento folha" +
            "                       inner join item.eventoFP e " +
            "                       where folha.ano = :ano " +
            "                         and folha.mes = :mes " +
            "                         and folha.tipoFolhaDePagamento = :tipoFolha " +
            "                         and folha.versao = :versao " +
            "                         and e.tipoDeConsignacao is not null " +
            "                        )" +
            "order by lancamento.eventoFP.codigo, lancamento.vinculoFP.matriculaFP.pessoa.nome ");
        createQuery.setParameter("data", Util.criaDataPrimeiroDiaMesJoda(exportarArquivoRetorno.getMes(), exportarArquivoRetorno.getAno()).toDate());
        createQuery.setParameter("tipo", TipoImportacao.ECONSIG);
        createQuery.setParameter("tipoFolha", exportarArquivoRetorno.getTipoFolhaDePagamento());
        createQuery.setParameter("mes", Mes.getMesToInt(exportarArquivoRetorno.getMes()));
        createQuery.setParameter("ano", exportarArquivoRetorno.getAno());
        createQuery.setParameter("versao", exportarArquivoRetorno.getVersao());

        return createQuery.getResultList();
    }

    public List<LancamentoFP> listaLancamentos(VinculoFP vinculo, int ano, int mes) {
        Query q = em.createQuery("from LancamentoFP lancamento "
            + " where lancamento.vinculoFP = :vinculo "
            + " and to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between "
            + " to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') "
            + " and to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') ");
        q.setParameter("vinculo", vinculo);
        q.setParameter("dataParam", Util.criaDataPrimeiroDiaDoMesFP(Mes.getMesToInt(mes).ordinal(), ano));
        List<LancamentoFP> lista = q.getResultList();
        return lista;
    }

    public List<LancamentoFP> buscarLancamentosPorTipo(VinculoFP vinculo, int ano, int mes, TipoFolhaDePagamento tipo,
                                                       Date dataCalculo, boolean recisaoComEstoValor) {
        //data do calculo para caso o lançamento tenha sido lançado depois do ultimo dia de processamento da folha, não calcular :D
        Query q = em.createQuery("from LancamentoFP lancamento "
            + " where lancamento.vinculoFP = :vinculo and lancamento.tipoFolhaDePagamento in :tipo and lancamento.eventoFP.ativo = true"
            + " and (to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between "
            + " to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') "
            + " and to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy')) order by lancamento.dataCadastro asc");
        q.setParameter("vinculo", vinculo);
        q.setParameter("tipo", getTipoCorreto(tipo, recisaoComEstoValor));
        q.setParameter("dataParam", Util.criaDataPrimeiroDiaDoMesFP(Mes.getMesToInt(mes).ordinal(), ano));
        List<LancamentoFP> lista = q.getResultList();
        try {
            buscarLancamentosPrevidencia(lista, vinculo, mes, ano, tipo, dataCalculo, recisaoComEstoValor);
        } catch (Exception e) {
            logger.error("Erro ao tentar buscar lançamentos de previdências ", e);
        }
        return lista;
    }

    private void buscarLancamentosPrevidencia(List<LancamentoFP> lista, VinculoFP vinculo, int mes, int ano, TipoFolhaDePagamento tipo, Date dataCalculo, boolean recisaoComEstoValor) {
        List<ItemValorPrevidencia> itensPrevidencia = new ArrayList<>();
        if (vinculo instanceof Aposentadoria) {
            List<ItemValorPrevidencia> itensValorAposentadoria = aposentadoriaFacade.buscarLancamentosVigentesAtivosENaoAutomaticos(vinculo, mes, ano);
            itensPrevidencia.addAll(itensValorAposentadoria);
        }
        if (vinculo instanceof Pensionista) {
            List<ItemValorPrevidencia> itensValorPensionista = pensionistaFacade.buscarLancamentosVigentesAtivosENaoAutomaticos(vinculo, mes, ano);
            itensPrevidencia.addAll(itensValorPensionista);
        }
        if (!itensPrevidencia.isEmpty()) {
            DateTime ultimoDiaMes = Util.criaDataPrimeiroDiaMesJoda(Mes.getMesToInt(mes).getNumeroMes(), ano);
            criarLancamentoVirtual(itensPrevidencia, lista, tipo, ultimoDiaMes.dayOfMonth().withMaximumValue());
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void criarLancamentoVirtual(List<ItemValorPrevidencia> itensAposentadoria, List<LancamentoFP> lista, TipoFolhaDePagamento tipo, DateTime dateTime) {
        for (ItemValorPrevidencia itemValorPrevidencia : itensAposentadoria) {
            if (naoContemEventoNoLancamento(lista, itemValorPrevidencia)) {
                LancamentoFP novoLancamento = criarLancamento(itemValorPrevidencia, tipo, dateTime);
                lista.add(novoLancamento);
            }
        }

    }

    private LancamentoFP criarLancamento(ItemValorPrevidencia itemValorPrevidencia, TipoFolhaDePagamento tipo, DateTime dateTime) {
        LancamentoFP lancamento = new LancamentoFP();
        lancamento.setVinculoFP(itemValorPrevidencia.getVinculoFP());
        lancamento.setDataCadastro(itemValorPrevidencia.getDataRegistro());
        lancamento.setEventoFP(itemValorPrevidencia.getEventoFP());
        lancamento.setTipoLancamentoFP(TipoLancamentoFP.VALOR);
        lancamento.setMesAnoInicial(itemValorPrevidencia.getInicioVigencia());
        lancamento.setMesAnoFinal(itemValorPrevidencia.getFinalVigencia() != null ? itemValorPrevidencia.getFinalVigencia() : dateTime.toDate());
        lancamento.setQuantificacao(itemValorPrevidencia.getValor());
        lancamento.setTipoFolhaDePagamento(tipo);
        return lancamento;
    }

    private boolean naoContemEventoNoLancamento(List<LancamentoFP> lista, ItemValorPrevidencia itemValorPrevidencia) {
        for (LancamentoFP lancamentoFP : lista) {
            if (lancamentoFP.getEventoFP().equals(itemValorPrevidencia.getEventoFP())) {
                return false;
            }
        }
        return true;
    }

    private List<TipoFolhaDePagamento> getTipoCorreto(TipoFolhaDePagamento tipo, boolean rescisaoComEstornoValor) {
        List<TipoFolhaDePagamento> tipos = new ArrayList<>();
        if (tipo.equals(TipoFolhaDePagamento.NORMAL)) {
            tipos.add(TipoFolhaDePagamento.NORMAL);
//            tipos.add(TipoFolhaDePagamento.COMPLEMENTAR);
        }
        if (tipo.equals(TipoFolhaDePagamento.COMPLEMENTAR)) {
//            tipos.add(TipoFolhaDePagamento.NORMAL);
            tipos.add(TipoFolhaDePagamento.COMPLEMENTAR);
        }
        if ((tipo.equals(TipoFolhaDePagamento.RESCISAO) || tipo.equals(TipoFolhaDePagamento.RESCISAO_COMPLEMENTAR)) && rescisaoComEstornoValor) {
            tipos.add(TipoFolhaDePagamento.RESCISAO);
            tipos.add(TipoFolhaDePagamento.RESCISAO_COMPLEMENTAR);
        }
        if ((tipo.equals(TipoFolhaDePagamento.RESCISAO) || tipo.equals(TipoFolhaDePagamento.RESCISAO_COMPLEMENTAR)) && !rescisaoComEstornoValor) {
            tipos.add(TipoFolhaDePagamento.NORMAL);
//            tipos.add(TipoFolhaDePagamento.COMPLEMENTAR);
            tipos.add(TipoFolhaDePagamento.RESCISAO);
            tipos.add(TipoFolhaDePagamento.RESCISAO_COMPLEMENTAR);
        }
        if (tipo.equals(TipoFolhaDePagamento.SALARIO_13)) {
            tipos.add(TipoFolhaDePagamento.SALARIO_13);
            tipos.add(TipoFolhaDePagamento.NORMAL);
        }
        if (tipo.equals(TipoFolhaDePagamento.ADIANTAMENTO_13_SALARIO)) {
            tipos.add(TipoFolhaDePagamento.ADIANTAMENTO_13_SALARIO);
        }
        if (tipos.isEmpty()) {
            tipos.add(tipo);
        }
        return tipos;
    }

    public List<LancamentoFP> listaLancamentosEmprestimos(VinculoFP vinculo, int ano, int mes) {
//        Query q = em.createQuery("from LancamentoFP lancamento "
//                + " where lancamento.vinculoFP = :vinculo and lancamento.tipoFolhaDePagamento = :tipo"
//                + " and to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between "
//                + " to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') "
//                + " and to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') and lancamento.eventoFP.codigo in " +
//                " (671,692,706,734,735,736,737,740,741,742,743,744,745,746,747,748,749,771,772,773,774,775,776,777,782,806) order by lancamento.eventoFP.ordemProcessamento ");
//        q.setParameter("vinculo", vinculo);
//        q.setParameter("tipo", TipoFolhaDePagamento.NORMAL);
//        q.setParameter("dataParam", Util.criaDataPrimeiroDiaDoMesFP(Mes.getMesToInt(mes).ordinal(), ano));
//        List<LancamentoFP> lista = q.getResultList();
        Query q = em.createQuery("from LancamentoFP lancamento "
            + " where lancamento.vinculoFP = :vinculo and lancamento.tipoFolhaDePagamento = :tipo"
            + " and to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between "
            + " to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') "
            + " and to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') and lancamento.eventoFP.tipoDeConsignacao = :tipoConsignacao order by lancamento.eventoFP.ordemProcessamento ");
        q.setParameter("vinculo", vinculo);
        q.setParameter("tipo", TipoFolhaDePagamento.NORMAL);
        q.setParameter("tipoConsignacao", TipoDeConsignacao.EMPRESTIMO);
        q.setParameter("dataParam", Util.criaDataPrimeiroDiaDoMesFP(Mes.getMesToInt(mes).ordinal(), ano));
        List<LancamentoFP> lista = q.getResultList();
        return lista;
    }

    public List<LancamentoFP> listaLancamentosConvenio(VinculoFP vinculo, int ano, int mes) {
//        Query q = em.createQuery("from LancamentoFP lancamento "
//                + " where lancamento.vinculoFP = :vinculo and lancamento.tipoFolhaDePagamento = :tipo"
//                + " and to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between "
//                + " to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') "
//                + " and to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') and lancamento.eventoFP.codigo in " +
//                " (605,611,612,614,615,619,626,631,674,691,694,712,715,718,738,739,751,752,753,754,755,756,757,758,759,760,770,780,831) order by lancamento.eventoFP.ordemProcessamento");
//        q.setParameter("vinculo", vinculo);
//        q.setParameter("tipo", TipoFolhaDePagamento.NORMAL);
//        q.setParameter("dataParam", Util.criaDataPrimeiroDiaDoMesFP(Mes.getMesToInt(mes).ordinal(), ano));
//        List<LancamentoFP> lista = q.getResultList();
        Query q = em.createQuery("from LancamentoFP lancamento "
            + " where lancamento.vinculoFP = :vinculo and lancamento.tipoFolhaDePagamento = :tipo"
            + " and to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between "
            + " to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') "
            + " and to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') and lancamento.eventoFP.tipoDeConsignacao = :tipoConsignacao order by lancamento.eventoFP.ordemProcessamento");
        q.setParameter("vinculo", vinculo);
        q.setParameter("tipo", TipoFolhaDePagamento.NORMAL);
        q.setParameter("tipoConsignacao", TipoDeConsignacao.CONVENIO);
        q.setParameter("dataParam", Util.criaDataPrimeiroDiaDoMesFP(Mes.getMesToInt(mes).ordinal(), ano));
        List<LancamentoFP> lista = q.getResultList();
        return lista;
    }

    public List<LancamentoFP> listaLancamentosDuplicados(int mes, int ano) {
        Query q = em.createQuery("from LancamentoFP lancamento "
            + " where lancamento.tipoFolhaDePagamento = :tipo"
            + " and to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy')  and to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') "
            + " and lancamento.vinculoFP in( select lanc.vinculoFP from LancamentoFP lanc where lanc.vinculoFP.id = lancamento.vinculoFP.id and lanc.tipoFolhaDePagamento = :tipo and to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between to_date(to_char(lanc.mesAnoInicial,'mm/yyyy'),'mm/yyyy')  and to_date(to_char(lanc.mesAnoFinal,'mm/yyyy'),'mm/yyyy') "
            + "  group by lanc.vinculoFP,lanc.eventoFP having count(*) > 1) "
            + " and lancamento.eventoFP in( select lanc.eventoFP from LancamentoFP lanc where lanc.vinculoFP.id = lancamento.vinculoFP.id and lanc.tipoFolhaDePagamento = :tipo and to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between to_date(to_char(lanc.mesAnoInicial,'mm/yyyy'),'mm/yyyy')  and to_date(to_char(lanc.mesAnoFinal,'mm/yyyy'),'mm/yyyy') "
            + "  group by lanc.vinculoFP,lanc.eventoFP having count(*) > 1) order by lancamento.vinculoFP");

        q.setParameter("tipo", TipoFolhaDePagamento.NORMAL);

        q.setParameter("dataParam", Util.criaDataPrimeiroDiaDoMesFP(Mes.getMesToInt(mes).ordinal(), ano));
        List<LancamentoFP> lista = q.getResultList();
        return lista;
    }


    public List<LancamentoFP> listaLancamentosPorTipoConsignacao(VinculoFP vinculo, int ano, int mes, TipoDeConsignacao tipo) {
        Query q = em.createQuery("from LancamentoFP lancamento "
            + " where lancamento.vinculoFP = :vinculo and lancamento.tipoFolhaDePagamento = :tipo"
            + " and to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between "
            + " to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') "
            + " and to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') and lancamento.eventoFP.tipoDeConsignacao = :tipoConsignacao order by lancamento.dataCadastroEconsig");
        q.setParameter("vinculo", vinculo);
        q.setParameter("tipo", TipoFolhaDePagamento.NORMAL);
        q.setParameter("tipoConsignacao", tipo);
        q.setParameter("dataParam", Util.criaDataPrimeiroDiaDoMesFP(Mes.getMesToInt(mes).ordinal(), ano));
        List<LancamentoFP> lista = q.getResultList();
        return lista;
    }

    public List<LancamentoFP> listaLancamentosCartao(VinculoFP vinculo, int ano, int mes) {
//        Query q = em.createQuery("from LancamentoFP lancamento "
//                + " where lancamento.vinculoFP = :vinculo and lancamento.tipoFolhaDePagamento = :tipo"
//                + " and to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between "
//                + " to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') "
//                + " and to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') and lancamento.eventoFP.codigo in (781,683) order by lancamento.eventoFP.ordemProcessamento ");
//        q.setParameter("vinculo", vinculo);
//        q.setParameter("tipo", TipoFolhaDePagamento.NORMAL);
//        q.setParameter("dataParam", Util.criaDataPrimeiroDiaDoMesFP(Mes.getMesToInt(mes).ordinal(), ano));
//        List<LancamentoFP> lista = q.getResultList();
        Query q = em.createQuery("from LancamentoFP lancamento "
            + " where lancamento.vinculoFP = :vinculo and lancamento.tipoFolhaDePagamento = :tipo"
            + " and to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between "
            + " to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') "
            + " and to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') and lancamento.eventoFP.tipoDeConsignacao = :tipoConsignacao order by lancamento.eventoFP.ordemProcessamento ");
        q.setParameter("vinculo", vinculo);
        q.setParameter("tipo", TipoFolhaDePagamento.NORMAL);
        q.setParameter("tipoConsignacao", TipoDeConsignacao.CARTAO);
        q.setParameter("dataParam", Util.criaDataPrimeiroDiaDoMesFP(Mes.getMesToInt(mes).ordinal(), ano));
        List<LancamentoFP> lista = q.getResultList();
        return lista;
    }


    public List<LancamentoFP> buscarLancamentosPorConsignacao(VinculoFP vinculo, int ano, int mes, TipoDeConsignacao tipo){
        Query q = em.createQuery("from LancamentoFP lancamento "
            + " where lancamento.vinculoFP = :vinculo and lancamento.tipoFolhaDePagamento = :tipo"
            + " and to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between "
            + " to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') "
            + " and to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') and lancamento.eventoFP.tipoDeConsignacao = :tipoConsignacao order by lancamento.eventoFP.ordemProcessamento ");
        q.setParameter("vinculo", vinculo);
        q.setParameter("tipo", TipoFolhaDePagamento.NORMAL);
        q.setParameter("tipoConsignacao", tipo);
        q.setParameter("dataParam", Util.criaDataPrimeiroDiaDoMesFP(Mes.getMesToInt(mes).ordinal(), ano));
        return q.getResultList();
    }

    public void salvarAlteracaoLancamentoFp(LancamentoFP orignal, LancamentoFP novo) throws Exception {
        DateTime encerramentoAntigo = new DateTime(novo.getMesAnoInicial());
        DateTime dataFinalOriginal = new DateTime(orignal.getMesAnoFinal());
        if (dataFinalOriginal.isAfter(encerramentoAntigo)) {
            encerramentoAntigo = encerramentoAntigo.minusDays(1);
            orignal.setMesAnoFinal(encerramentoAntigo.toDate());
        }
        novo.setDataCadastro(UtilRH.getDataOperacao());
        novo.setId(null);
        salvar(orignal);
        salvarNovo(novo);

    }

    public MotivoRejeicao getMotivoRejeicao(List<MotivoRejeicao> lista, Integer codigo) {
        for (MotivoRejeicao mr : lista) {
            if (mr.getCodigo().equals(codigo)) {
                return mr;
            }
        }
        return null;
    }

    public boolean validarLancamento(ImportaMovimentoFinanceiro arquivo, LancamentoFP lancamentos, String linha, String... args) {
        boolean retorno = true;
        String qualoDefeito = "";
        if (lancamentos.getVinculoFP() == null) {
            qualoDefeito += " || contrato " + args[0] + " não encontrado ||";
            retorno = false;
        }
        if (lancamentos.getTipoLancamentoFP() == null) {
            qualoDefeito += "|| tipo lançamento " + args[1] + " não encontrado ||";
            retorno = false;
        }
        if (lancamentos.getMesAnoInicial() == null) {
            qualoDefeito += "|| data inicial " + args[2] + " não encontrada ||";
            retorno = false;
        }
        if (lancamentos.getMesAnoFinal() == null) {
            qualoDefeito += "|| data final " + args[3] + " não encontrada ||";
            retorno = false;
        }
        if (lancamentos.getEventoFP() == null) {
            qualoDefeito += "|| evento " + args[4] + " não encontrado ||";
            retorno = false;
        }
        if (retorno == false) {
            linha += " Problemas Encontrados: " + qualoDefeito;
            arquivo.getLinhaProblema().add(linha);
        }
        return retorno;
    }

    public boolean naoExisteLancamento(LancamentoFP lancamento) {
        String consulta = "from LancamentoFP lancamento where "
            + " lancamento.mesAnoInicial = :mesAnoInicial"
            + " and lancamento.mesAnoFinal = :mesAnoFinal"
            + " and lancamento.eventoFP  = :evento"
            + " and lancamento.tipoLancamentoFP = :tipo"
            + " and lancamento.vinculoFP = :vinculo";
        Query q = em.createQuery(consulta);
        q.setParameter("mesAnoInicial", lancamento.getMesAnoInicial());
        q.setParameter("mesAnoFinal", lancamento.getMesAnoFinal());
        q.setParameter("evento", lancamento.getEventoFP());
        q.setParameter("tipo", lancamento.getTipoLancamentoFP());
        q.setParameter("vinculo", lancamento.getVinculoFP());

        return q.getResultList().isEmpty();

    }

    public LancamentoFP getLancamentoExistenteEconsig(LancamentoFP lancamento) {
        String consulta = "from LancamentoFP lancamento where "
            + " lancamento.mesAnoInicial = :mesAnoInicial"
            + " and lancamento.mesAnoFinal = :mesAnoFinal"
            + " and lancamento.eventoFP  = :evento"
            + " and lancamento.tipoLancamentoFP = :tipo"
            + " and lancamento.vinculoFP = :vinculo";
        Query q = em.createQuery(consulta);
        q.setParameter("mesAnoInicial", lancamento.getMesAnoInicial());
        q.setParameter("mesAnoFinal", lancamento.getMesAnoFinal());
        q.setParameter("evento", lancamento.getEventoFP());
        q.setParameter("tipo", lancamento.getTipoLancamentoFP());
        q.setParameter("vinculo", lancamento.getVinculoFP());
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) return null;
        return (LancamentoFP) q.getSingleResult();

    }

    @Override
    public void salvarNovo(LancamentoFP entity) {
        if (entity.getAtoLegal() != null && entity.getAtoLegal().getId() == null) {
            getEntityManager().persist(entity.getAtoLegal());
        }
        entity.setLancamentoFPHist(null);
        getEntityManager().persist(entity);
    }

    @Override
    public void salvar(LancamentoFP entity) {
        try {
            entity = getLancamentoFPComHistorico(entity);
            if (entity.getAtoLegal() != null && entity.getAtoLegal().getId() == null) {
                getEntityManager().persist(entity.getAtoLegal());
            }
            getEntityManager().merge(entity);
        } catch (ExcecaoNegocioGenerica eng) {
            throw eng;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void salvarSemHistorico(LancamentoFP entity) {
        try {
            if (entity.getAtoLegal() != null && entity.getAtoLegal().getId() == null) {
                getEntityManager().persist(entity.getAtoLegal());
            }
            getEntityManager().merge(entity);
        } catch (ExcecaoNegocioGenerica eng) {
            throw eng;
        } catch (Exception ex) {
            throw ex;
        }
    }


    public void salvarLancamentoVinculoDaImportacao(List<LancamentoFP> lancamentosImportados) {
        for (LancamentoFP lancamento : lancamentosImportados) {
            if (!validaLancamento(lancamento)) {
                getEntityManager().persist(lancamento);
            }
        }
    }

    public LancamentoFP getLancamentoFPComHistorico(LancamentoFP entity) {
        LancamentoFP lancamentoFPAtualmentePersistido = getLancamentoFPAtualmentePersistido(entity);
        entity.criarOrAtualizarAndAssociarHistorico(lancamentoFPAtualmentePersistido);
        return entity;
    }

    private LancamentoFP getLancamentoFPAtualmentePersistido(LancamentoFP entity) {
        if (entity.getId() != null) {
            return recuperar(entity.getId());
        }
        return entity;
    }

    public List<LancamentoFP> lancamentosArquivosImportados(int mes, int ano) {
        Query createQuery = em.createQuery("select lancamento from LancamentoFP lancamento "
            + "where lancamento.tipoImportacao = :tipo and to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between " +
            "                 to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') " +
            "                 and to_date(to_char(coalesce(lancamento.mesAnoFinal,:data),'mm/yyyy'),'mm/yyyy') order by lancamento.eventoFP.codigo, lancamento.vinculoFP.matriculaFP.pessoa.nome ");
        createQuery.setParameter("data", Util.criaDataPrimeiroDiaMesJoda(mes, ano).toDate());
        createQuery.setParameter("tipo", TipoImportacao.ARQUIVO);
        return createQuery.getResultList();
    }


    public List<LancamentoFP> buscarLancamentosEconsigRejeitadosPorMesAndAno(int mesInicial, int anoInicial, int mesFinal, int anoFinal) {
        Query createQuery = em.createQuery("select lancamento from LancamentoFP lancamento "
            + " where lancamento.motivoRejeicao is not null " +
            "     and (to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') between to_date(to_char(:dataInicial,'mm/yyyy'),'mm/yyyy')  " +
            "     and to_date(to_char(:dataFinal,'mm/yyyy'),'mm/yyyy') " +
            "      or to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') between to_date(to_char(:dataInicial,'mm/yyyy'),'mm/yyyy') " +
            "     and to_date(to_char(:dataFinal,'mm/yyyy'),'mm/yyyy')) " +
            "   order by cast(lancamento.vinculoFP.matriculaFP.matricula as integer) ");
        createQuery.setParameter("dataInicial", Util.criaDataPrimeiroDiaMesJoda(mesInicial, anoInicial).toDate());
        createQuery.setParameter("dataFinal", Util.criaDataPrimeiroDiaMesJoda(mesFinal, anoFinal).toDate());
        return createQuery.getResultList();
    }

    public List<LancamentoFP> buscarLancamentosEconsigPorPeriodo(YearMonth inicial, YearMonth fim, Boolean somenteRejeitados) {
        String sql = "select " +
            "    new br.com.webpublico.entidades.LancamentoFP(lancamento.id, lancamento.vinculoFP, lancamento.eventoFP, lancamento.tipoLancamentoFP, " +
            "   coalesce((select sum(item.valor) from FichaFinanceiraFP ficha join ficha.folhaDePagamento folha join ficha.itemFichaFinanceiraFP item" +
            " where folha.ano = extract(year from lancamento.mesAnoInicial) and folha.mes+1 = extract(month from lancamento.mesAnoInicial)" +
            "   and ficha.vinculoFP = lancamento.vinculoFP and folha.tipoFolhaDePagamento = lancamento.tipoFolhaDePagamento and lancamento.eventoFP = item.eventoFP ),0) , " +
            "  " +
            "   lancamento.mesAnoInicial, lancamento.mesAnoFinal, motivo) from LancamentoFP lancamento left join lancamento.motivoRejeicao motivo "
            + "    where lancamento.eventoFP.tipoDeConsignacao is not null " +
            "      and (to_date(to_char(:inicial,'mm/yyyy'),'mm/yyyy') between to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(lancamento.mesAnoFinal,:inicial),'mm/yyyy'),'mm/yyyy')" +
            "                 or " +
            "           to_date(to_char(:fim,'mm/yyyy'),'mm/yyyy') between  to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy')  and to_date(to_char(coalesce(lancamento.mesAnoFinal,:fim),'mm/yyyy'),'mm/yyyy')" +
            "                 or " +
            "           to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') between " +
            "                 to_date(to_char(:inicial,'mm/yyyy'),'mm/yyyy') " +
            "                 and to_date(to_char(:fim,'mm/yyyy'),'mm/yyyy')" +
            "                 or " +
            "           to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') between " +
            "                 to_date(to_char(:inicial,'mm/yyyy'),'mm/yyyy') " +
            "                 and to_date(to_char(:fim,'mm/yyyy'),'mm/yyyy')" +
            ")";
        if (somenteRejeitados) {
            sql += " and lancamento.motivoRejeicao is not null ";
        }
        sql += " order by cast(lancamento.vinculoFP.matriculaFP.matricula as integer) ";
        Query query = em.createQuery(sql);

        query.setParameter("inicial", Util.criaDataPrimeiroDiaMesJoda(inicial.getMonthOfYear(), inicial.getYear()).toDate());
        query.setParameter("fim", Util.criaDataPrimeiroDiaMesJoda(fim.getMonthOfYear(), fim.getYear()).toDate());
        return query.getResultList();
    }


    public List<LancamentoFP> buscarLancamentosEconsigPorMesAndAnoAndVinculoFP(YearMonth inicial, YearMonth fim, Boolean somenteRejeitados, VinculoFP vinculoFP) {
        String sql = "select " +
            "    new br.com.webpublico.entidades.LancamentoFP(lancamento.id, lancamento.vinculoFP, lancamento.eventoFP, lancamento.tipoLancamentoFP, " +
            "   coalesce((select sum(item.valor) from FichaFinanceiraFP ficha join ficha.folhaDePagamento folha join ficha.itemFichaFinanceiraFP item" +
            " where folha.ano = extract(year from lancamento.mesAnoInicial) and folha.mes+1 = extract(month from lancamento.mesAnoInicial)" +
            "   and ficha.vinculoFP = lancamento.vinculoFP and folha.tipoFolhaDePagamento = lancamento.tipoFolhaDePagamento and lancamento.eventoFP = item.eventoFP ),0) , " +
            "  " +
            "   lancamento.mesAnoInicial, lancamento.mesAnoFinal, motivo) from LancamentoFP lancamento left join lancamento.motivoRejeicao motivo "
            + "where lancamento.vinculoFP = :vinculo and " +
            "    (to_date(to_char(:inicial,'mm/yyyy'),'mm/yyyy') between to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(lancamento.mesAnoFinal,:inicial),'mm/yyyy'),'mm/yyyy')" +
            "                 or " +
            "           to_date(to_char(:fim,'mm/yyyy'),'mm/yyyy') between  to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy')  and to_date(to_char(coalesce(lancamento.mesAnoFinal,:fim),'mm/yyyy'),'mm/yyyy')" +
            "                 or " +
            "           to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') between " +
            "                 to_date(to_char(:inicial,'mm/yyyy'),'mm/yyyy') " +
            "                 and to_date(to_char(:fim,'mm/yyyy'),'mm/yyyy')" +
            "                 or " +
            "           to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') between " +
            "                 to_date(to_char(:inicial,'mm/yyyy'),'mm/yyyy') " +
            "                 and to_date(to_char(:fim,'mm/yyyy'),'mm/yyyy'))" +
            "                 and lancamento.eventoFP.tipoDeConsignacao is not null ";

        if (somenteRejeitados) {
            sql += " and lancamento.motivoRejeicao is not null ";
        }
        sql += "   order by cast(lancamento.vinculoFP.matriculaFP.matricula as integer), lancamento.eventoFP.codigo ";
        Query query = em.createQuery(sql);

        query.setParameter("inicial", Util.criaDataPrimeiroDiaMesJoda(inicial.getMonthOfYear(), inicial.getYear()).toDate());
        query.setParameter("fim", Util.criaDataPrimeiroDiaMesJoda(fim.getMonthOfYear(), fim.getYear()).toDate());
        query.setParameter("vinculo", vinculoFP);
        return query.getResultList();
    }

    public List<LancamentoFP> buscarLancamentosEconsigRejeitadosPorMesAndAnoAndVinculoFP(int mesInicial, int anoInicial, int mesFinal, int anoFinal, VinculoFP vinculoFP) {
        Query createQuery = em.createQuery("select lancamento from LancamentoFP lancamento "
            + "where lancamento.vinculoFP = :vinculo and  lancamento.motivoRejeicao is not null " +
            "     and (to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') between to_date(to_char(:dataInicial,'mm/yyyy'),'mm/yyyy')  " +
            "     and to_date(to_char(:dataFinal,'mm/yyyy'),'mm/yyyy') " +
            "      or to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') between to_date(to_char(:dataInicial,'mm/yyyy'),'mm/yyyy') " +
            "     and to_date(to_char(:dataFinal,'mm/yyyy'),'mm/yyyy')) " +
            "  order by cast(lancamento.vinculoFP.matriculaFP.matricula as integer) ");
        createQuery.setParameter("dataInicial", Util.criaDataPrimeiroDiaMesJoda(mesInicial, anoInicial).toDate());
        createQuery.setParameter("dataFinal", Util.criaDataPrimeiroDiaMesJoda(mesFinal, anoFinal).toDate());
        createQuery.setParameter("vinculo", vinculoFP);
        return createQuery.getResultList();
    }


    public List<LancamentoFP> buscarLancamentosEconsigPorMesAndAnoAndOrgao(YearMonth inicial, YearMonth fim, Boolean somenteRejeitados, HierarquiaOrganizacional orgao) {
        String sql = "select distinct " +
            "    new br.com.webpublico.entidades.LancamentoFP(lancamento.id, lancamento.vinculoFP, lancamento.eventoFP, lancamento.tipoLancamentoFP, " +
            "   coalesce((select sum(item.valor) from FichaFinanceiraFP ficha join ficha.folhaDePagamento folha join ficha.itemFichaFinanceiraFP item" +
            " where folha.ano = extract(year from lancamento.mesAnoInicial) and folha.mes+1 = extract(month from lancamento.mesAnoInicial)" +
            "   and ficha.vinculoFP = lancamento.vinculoFP and folha.tipoFolhaDePagamento = lancamento.tipoFolhaDePagamento and lancamento.eventoFP = item.eventoFP ),0) , " +
            "  " +
            "   lancamento.mesAnoInicial, lancamento.mesAnoFinal, motivo) from LancamentoFP lancamento left join lancamento.motivoRejeicao motivo " +
            "                 inner join lancamento.vinculoFP.lotacaoFuncionals lotacao" +
            "                 inner join lotacao.unidadeOrganizacional uo" +
            "                 inner join uo.hierarquiasOrganizacionais ho" +
            "                 where " +

            "              (to_date(to_char(:inicial,'mm/yyyy'),'mm/yyyy') between to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(lancamento.mesAnoFinal,:inicial),'mm/yyyy'),'mm/yyyy')" +
            "                 or " +
            "           to_date(to_char(:fim,'mm/yyyy'),'mm/yyyy') between  to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy')  and to_date(to_char(coalesce(lancamento.mesAnoFinal,:fim),'mm/yyyy'),'mm/yyyy')" +
            "                 or " +
            "           to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') between " +
            "                 to_date(to_char(:inicial,'mm/yyyy'),'mm/yyyy') " +
            "                 and to_date(to_char(:fim,'mm/yyyy'),'mm/yyyy')" +
            "                 or " +
            "           to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') between " +
            "                 to_date(to_char(:inicial,'mm/yyyy'),'mm/yyyy') " +
            "                 and to_date(to_char(:fim,'mm/yyyy'),'mm/yyyy')" +
            "                                                                            )" +

            "                 and to_date(to_char(:inicial,'mm/yyyy'),'mm/yyyy') between to_date(to_char(lotacao.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(lotacao.finalVigencia,:inicial),'mm/yyyy'),'mm/yyyy') " +
            "                 and ho.codigo like :codigo " +
            "                 and lancamento.eventoFP.tipoDeConsignacao is not null  ";
        if (somenteRejeitados) {
            sql += " and lancamento.motivoRejeicao is not null ";
        }

        Query query = em.createQuery(sql);
        query.setParameter("inicial", Util.criaDataPrimeiroDiaMesJoda(inicial.getMonthOfYear(), inicial.getYear()).toDate());
        query.setParameter("fim", Util.criaDataPrimeiroDiaMesJoda(fim.getMonthOfYear(), fim.getYear()).toDate());
        query.setParameter("codigo", "" + orgao.getCodigoSemZerosFinais() + "%");
        return query.getResultList();
    }


    public List<LancamentoFP> buscarLancamentosEconsigRejeitadosPorMesAndAnoAndOrgao(int mesInicial, int anoInicial, int mesFinal, int anoFinal, HierarquiaOrganizacional orgao) {
        Query createQuery = em.createQuery("select distinct lancamento from LancamentoFP lancamento " +
            "                 inner join lancamento.vinculoFP.lotacaoFuncionals lotacao" +
            "                 inner join lotacao.unidadeOrganizacional uo" +
            "                 inner join uo.hierarquiasOrganizacionais ho" +
            "                 where lancamento.motivoRejeicao is not null " +
            "                  and (to_date(to_char(lancamento.mesAnoInicial,'mm/yyyy'),'mm/yyyy') between to_date(to_char(:dataInicial,'mm/yyyy'),'mm/yyyy')  " +
            "                  and to_date(to_char(:dataFinal,'mm/yyyy'),'mm/yyyy') " +
            "                   or to_date(to_char(lancamento.mesAnoFinal,'mm/yyyy'),'mm/yyyy') between to_date(to_char(:dataInicial,'mm/yyyy'),'mm/yyyy') " +
            "                  and to_date(to_char(:dataFinal,'mm/yyyy'),'mm/yyyy')) " +

            "                   and (to_date(to_char(:dataInicial,'mm/yyyy'),'mm/yyyy') between to_date(to_char(lotacao.inicioVigencia,'mm/yyyy'),'mm/yyyy') " +
            "                   and to_date(to_char(coalesce(lotacao.finalVigencia,:dataInicial),'mm/yyyy'),'mm/yyyy') " +
            "                    or to_date(to_char(:dataFinal,'mm/yyyy'),'mm/yyyy') between to_date(to_char(lotacao.inicioVigencia,'mm/yyyy'),'mm/yyyy') " +
            "                   and to_date(to_char(coalesce(lotacao.finalVigencia,:dataFinal),'mm/yyyy'),'mm/yyyy') )" +

            "                   and (to_date(to_char(:dataInicial,'mm/yyyy'),'mm/yyyy') between to_date(to_char(ho.inicioVigencia,'mm/yyyy'),'mm/yyyy') " +
            "                   and to_date(to_char(coalesce(ho.fimVigencia,:dataInicial),'mm/yyyy'),'mm/yyyy') " +
            "                    or to_date(to_char(:dataFinal,'mm/yyyy'),'mm/yyyy') between to_date(to_char(ho.inicioVigencia,'mm/yyyy'),'mm/yyyy') " +
            "                   and to_date(to_char(coalesce(ho.fimVigencia,:dataFinal),'mm/yyyy'),'mm/yyyy') )" +
            "                   and ho.codigo like :codigo " +
            "                  ");
        createQuery.setParameter("dataInicial", Util.criaDataPrimeiroDiaMesJoda(mesInicial, anoInicial).toDate());
        createQuery.setParameter("dataFinal", Util.criaDataPrimeiroDiaMesJoda(mesFinal, anoFinal).toDate());
        createQuery.setParameter("codigo", "" + orgao.getCodigoSemZerosFinais() + "%");
        return createQuery.getResultList();
    }


    public void salvar(LancamentoFP lfp, LoteProcessamento loteProcessamento, List<MensagemValidacao> mensagensRetorno) throws ExcecaoNegocioGenerica {
        loteProcessamento = loteProcessamentoFacade.recuperar(loteProcessamento.getId());
        String consulta = loteProcessamentoFacade.gerarQueryConsulta(loteProcessamento);

        if (consulta.contains("where ( )")) {
            throw new ExcecaoNegocioGenerica("O lote selecionado não possui os parâmetros necessários para consultar servidores. Verifique seu cadastro e tente novamente.");
        }
        List<Long> idsVinculo = vinculoFPFacade.findVinculosByConsultDinamicaSql(consulta);
        List<VinculoFP> vinculos = Lists.newArrayList();
        for (Long id : idsVinculo) {
            vinculos.add(vinculoFPFacade.recuperar(id));
        }

        try {
            for (VinculoFP vinculo : vinculos) {
                lfp.setVinculoFP(vinculo);
                if (validaLancamento(lfp)) {
                    mensagensRetorno.add(new MensagemValidacao(TipoMensagemValidacao.ALERTA, "", "", "O(a) servidor(a) " + vinculo + " já possui lançameto da verba " + lfp.getEventoFP() + " em " + Util.dateToString(lfp.getMesAnoInicial())));
                    continue;
                }
                LancamentoFP novoLancamento = (LancamentoFP) Util.clonarObjeto(lfp);
                novoLancamento.setVinculoFP(vinculo);
                salvar(novoLancamento);
            }
        } catch (ExcecaoNegocioGenerica eng) {
            throw eng;
        }
    }


    public void excluirLancamentosSelecionadosDeImportacao(List<LancamentoFP> lancamentosMarcadosParaRemocao) {
        for (LancamentoFP lancamento : lancamentosMarcadosParaRemocao) {
            lancamento = getEntityManager().merge(lancamento);
            getEntityManager().remove(lancamento);
        }
    }

    public void removerItensLancamento(ArquivoEconsigItens itemArquivo) {


//        itemArquivo.setArquivoEconsig(null);
//        itemArquivo.setLancamentoFP(null);
//        itemArquivo = getEntityManager().merge(itemArquivo);
        getEntityManager().remove(itemArquivo);
    }

    public void removerItemFichaFinanceira(ItemFichaFinanceiraFP item) {
        item = getEntityManager().merge(item);
        getEntityManager().remove(item);
    }

    public void remover(LancamentoFP lancamento, Integer mes, Integer ano) {
        removeItemFichaFinanceira(lancamento, mes, ano);
        removeItemArquivoEconsig(lancamento);
        Object chave = Persistencia.getId(lancamento);
        Object obj = recuperar(chave);
        if (obj != null) {
            getEntityManager().remove(obj);
        }
    }

    public boolean isPermiteExcluirLancamento(LancamentoFP lancamentoFP) {
        Query q = this.em.createQuery("select ficha from FichaFinanceiraFP ficha join ficha.itemFichaFinanceiraFP itens "
            + "                         where ficha.vinculoFP = :vinculoFP "
            + "                           and ficha.folhaDePagamento.efetivadaEm >= :dataCadastro "
            + "                           and to_date(lpad(itens.mes,2,'0') ||'/'||itens.ano,'MM/yyyy') between :inicio and :fim "
            + "                           and ficha.folhaDePagamento.tipoFolhaDePagamento = :tipofolha"
            + "                           and itens.eventoFP = :evento ");
        q.setParameter("vinculoFP", lancamentoFP.getVinculoFP());
        q.setParameter("dataCadastro", lancamentoFP.getDataCadastro());
        q.setParameter("vinculoFP", lancamentoFP.getVinculoFP());
        q.setParameter("inicio", lancamentoFP.getMesAnoInicial());
        q.setParameter("fim", lancamentoFP.getMesAnoFinal());
        q.setParameter("tipofolha", lancamentoFP.getTipoFolhaDePagamento());
        q.setParameter("evento", lancamentoFP.getEventoFP());
        return !q.getResultList().isEmpty();
    }

    public void removeItemFichaFinanceira(LancamentoFP lancamento, Integer mes, Integer ano) {
        try {
            ItemFichaFinanceiraFP item = fichaFinanceiraFPFacade.recuperaItemFichaFinanceiraFPPorVinculoFPMesAnoEvento(lancamento.getVinculoFP(), ano, mes, lancamento.getEventoFP());
            if (item != null && !item.getFichaFinanceiraFP().getFolhaDePagamento().folhaEfetivada()) {
                removerItemFichaFinanceira(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addOperacaoNaoRealizada("Não foi possível completar a operação solicitada. Verifique os passos executados e tente novamente. Se o problema persistir entre em contato com o suporte técnico.");
        }
    }

    public void removeItemArquivoEconsig(LancamentoFP lancamento) {
        try {
            ArquivoEconsigItens itemArquivo = arquivoEconsigFacade.recuperaEconsigItensPorLancamentoFP(lancamento);
            if (itemArquivo != null) {
                removerItensLancamento(itemArquivo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addOperacaoNaoRealizada("Não foi possível completar a operação solicitada. Verifique os passos executados e tente novamente. Se o problema persistir entre em contato com o suporte técnico.");
        }
    }


    public void excluirLancamentos(List<LancamentoFP> excluidos) {
        for (LancamentoFP excluido : excluidos) {
            remover(excluido);
        }
    }

    public Integer buscarQuantidadeDeLancamentosPorData(Date inicio, boolean isEconsig) {
        Integer total = 0;
        String hql = "select count(lancamento) from LancamentoFP lancamento where lancamento.dataCadastro = :data ";
        if (!isEconsig) {
            hql += " and lancamento.tipoImportacao is null ";
        }
        Query q = em.createQuery(hql);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(inicio));
        if (q.getResultList().isEmpty()) {
            return total;
        }
        Long bg = (Long) q.getSingleResult();
        total = bg.intValue();
        return total;
    }

    public Integer buscarQuantidadeDeServidoresComLancamentosPorData(Date inicio, boolean isEconsig) {
        Integer total = 0;
        String hql = "select count(distinct lancamento.vinculoFP_id) from LancamentoFP lancamento where to_date(to_char(lancamento.dataCadastro,'dd/MM/yyyy'),'dd/MM/yyyy') = :data ";
        if (!isEconsig) {
            hql += " and lancamento.tipoImportacao is null ";
        }
        Query q = em.createNativeQuery(hql);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(inicio));
        if (q.getResultList().isEmpty()) {
            return total;
        }
        BigDecimal bg = (BigDecimal) q.getResultList().get(0);
        total = bg.intValue();
        return total;
    }

    public void salvarLancamentos(List<LancamentoFP> lancamentos) {
        for (LancamentoFP lancamento : lancamentos) {
            if (!validaLancamento(lancamento)) {
                em.persist(lancamento);
            } else {
                logger.debug("Já existe um lançamento " + lancamento.getVinculoFP() + " - " + lancamento.getEventoFP() + " - " + lancamento.getMesAnoInicial() + " - " + lancamento.getMesAnoFinal());
            }
        }

    }

    public BigDecimal verificarConvenio(List<ItemConsignacao> itemConsignacoes, BigDecimal totalBase20, BigDecimal totalBase10, BigDecimal cambioEntreConvenioEmprestimo, BigDecimal totalConvenio) {
        BigDecimal valorEmprestimo = totalBase20.add(totalBase10);
        cambioEntreConvenioEmprestimo = totalBase20;
        for (ItemConsignacao itemConsignacao : itemConsignacoes) {
            if (TipoDeConsignacao.CONVENIO.equals(itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao())) {
                atribuirNaoPagoItemConsignacao(valorEmprestimo, itemConsignacao);
            } else if (itemConsignacao.getItemFichaFinanceiraFP() != null) {
                valorEmprestimo = valorEmprestimo.subtract(itemConsignacao.getItemFichaFinanceiraFP().getValor());
                if (itemConsignacao.getItemFichaFinanceiraFP().getValor().compareTo(BigDecimal.ZERO) <= 0) {
                    itemConsignacao.setNaoPago(true);
                } else if (valorEmprestimo.compareTo(BigDecimal.ZERO) <= 0) {
                    itemConsignacao.setNaoPago(true);
                }
            } else {
                itemConsignacao.setNaoPago(true);
            }
        }

        if (totalConvenio.doubleValue() > totalBase10.doubleValue()) {
            cambioEntreConvenioEmprestimo = valorEmprestimo;
        }
        return cambioEntreConvenioEmprestimo;
    }

    public void verificarEmprestimo(List<ItemConsignacao> itemConsignacoes, BigDecimal cambioEntreConvenioEmprestimo) {
        BigDecimal valor = cambioEntreConvenioEmprestimo;
        for (ItemConsignacao itemConsignacao : itemConsignacoes) {
            if (TipoDeConsignacao.EMPRESTIMO.equals(itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao())) {
                verificarTipoLancamentoEAtribuirNaoPago(valor, itemConsignacao);
            }
        }
    }

    public void verificarCartao(List<ItemConsignacao> itemConsignacoes, BigDecimal totalBase5) {
        BigDecimal valor = totalBase5;
        for (ItemConsignacao itemConsignacao : itemConsignacoes) {
            if (TipoDeConsignacao.CARTAO.equals(itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao())) {
                verificarTipoLancamentoEAtribuirNaoPago(valor, itemConsignacao);
            }
        }
    }


    private void atribuirNaoPagoItemConsignacao(BigDecimal valor, ItemConsignacao itemConsignacao) {
        try {
            if (itemConsignacao.getLancamentoFP().getQuantificacao().compareTo(itemConsignacao.getItemFichaFinanceiraFP().getValor()) == 0) {
                return;
            }
        } catch (Exception e) {
            logger.debug("Não foi possivel comparar o valor do item de consignacao");
        }

        valor = valor.subtract(itemConsignacao.getLancamentoFP().getQuantificacao());
        if (itemConsignacao.getItemFichaFinanceiraFP() == null) {
            itemConsignacao.setNaoPago(true);
            return;
        }

        if (itemConsignacao.getItemFichaFinanceiraFP().getValor().compareTo(BigDecimal.ZERO) <= 0) {
            itemConsignacao.setNaoPago(true);
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            itemConsignacao.setNaoPago(true);
        }
    }

    private void verificarTipoLancamentoEAtribuirNaoPago(BigDecimal valor, ItemConsignacao itemConsignacao) {
        if (TipoLancamentoFP.VALOR.equals(itemConsignacao.getLancamentoFP().getTipoLancamentoFP())) {
            atribuirNaoPagoItemConsignacao(valor, itemConsignacao);
        } else {
            if (itemConsignacao.getItemFichaFinanceiraFP() != null) {
                valor = valor.subtract(itemConsignacao.getItemFichaFinanceiraFP().getValor());
                if (itemConsignacao.getItemFichaFinanceiraFP().getValor().compareTo(BigDecimal.ZERO) <= 0) {
                    itemConsignacao.setNaoPago(true);
                } else if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                    itemConsignacao.setNaoPago(true);
                }
            }
        }

    }

    public ConsignacaoServidorDTO buscarConsignacaoPorServidorMesAno(Mes mes, Integer ano, TipoFolhaDePagamento tipoFolhaDePagamento, VinculoFP vinculo) {
        ConsignacaoServidor consignacaoServidor = new ConsignacaoServidor();
        List<ItemFichaFinanceiraFP> itensFichaFinanceira = fichaFinanceiraFPFacade.recuperafichaFinanceiraFP(mes.getNumeroMes(), ano, vinculo, tipoFolhaDePagamento, null);
        for (TipoDeConsignacao tipoDeConsignacao : TipoDeConsignacao.values()) {
            List<LancamentoFP> lancamentosFp = listaLancamentosPorTipoConsignacao(vinculo, ano, mes.getNumeroMes(), tipoDeConsignacao);
            for (LancamentoFP lancamentoFP : lancamentosFp) {

                consignacaoServidor.getItemConsignacoes().add(new ItemConsignacao(null, lancamentoFP));
            }
        }
        for (ItemFichaFinanceiraFP itemFichaFinanceiraFP : itensFichaFinanceira) {
            ItemConsignacao itemConsignacao = eventoContemNoLancamento(itemFichaFinanceiraFP.getEventoFP(), consignacaoServidor.getItemConsignacoes());
            if (itemConsignacao != null) itemConsignacao.setItemFichaFinanceiraFP(itemFichaFinanceiraFP);
        }
        consignacaoServidor.setTotalBaseConsignacao(baseFPFacade.recuperarValoresBase(itensFichaFinanceira));
        consignacaoServidor.preencherValoresDoPercentualDaConsignacao();
        consignacaoServidor.preencherValoresParaDescontados();
        verificarVerbasNaoDescontadas(consignacaoServidor);

        ConfiguracaoFP configuracaoFP = baseFPFacade.buscarConfiguracaoFPVigente();
        consignacaoServidor.setTotalBaseEuConsigoMais(baseFPFacade.recurperarValoresEuConsigoMais(itensFichaFinanceira, configuracaoFP));
        consignacaoServidor.setPercentualMargemEuConsigoMais(configuracaoFP.getPercentualMargemEmprestimo());
        consignacaoServidor.setValorMargemEuConsigoMais(calcularMargemEuConsigoMais(consignacaoServidor.getTotalBaseEuConsigoMais(), consignacaoServidor.getPercentualMargemEuConsigoMais()));
        consignacaoServidor.setMinimoDiasDireitoEuConsigoMais(configuracaoFP.getQtdeMinimaDiasEuConsigMais());
        return consignacaoServidor.toConsignacaoServidorDTO();
    }

    private BigDecimal calcularMargemEuConsigoMais(BigDecimal totalBaseEuConsigoMais, BigDecimal percentualMargemEuConsigoMais) {
        return (totalBaseEuConsigoMais != null ? totalBaseEuConsigoMais : BigDecimal.ZERO).multiply(percentualMargemEuConsigoMais != null ?
            percentualMargemEuConsigoMais : BigDecimal.ZERO).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
    }

    private void verificarVerbasNaoDescontadas(ConsignacaoServidor consignacaoServidor) {
        consignacaoServidor.setCambioEntreConvenioEmprestimo(verificarConvenio(consignacaoServidor.getItemConsignacoes(), consignacaoServidor.getTotalBase20(), consignacaoServidor.getTotalBase10(), consignacaoServidor.getCambioEntreConvenioEmprestimo(), consignacaoServidor.getTotalConvenio()));
        verificarEmprestimo(consignacaoServidor.getItemConsignacoes(), consignacaoServidor.getCambioEntreConvenioEmprestimo());
        verificarCartao(consignacaoServidor.getItemConsignacoes(), consignacaoServidor.getTotalBase5());
    }


    public ItemConsignacao eventoContemNoLancamento(EventoFP eventoFP, List<ItemConsignacao> itemConsignacoes) {
        for (ItemConsignacao item : itemConsignacoes) {
            if (item.getLancamentoFP().getEventoFP().equals(eventoFP)) {
                return item;
            }
        }
        return null;
    }

    public Date getLancamentoFPPorCodigoEventoFP(VinculoFP vinculoFP, String codigo) {
        String sql = " SELECT lancamento.mesAnoInicial " +
            " FROM lancamentoFP lancamento INNER JOIN vinculoFP vinculo ON vinculo.id = lancamento.vinculofp_id " +
            "   INNER JOIN eventoFP evento ON evento.id = lancamento.eventofp_id " +
            " WHERE vinculo.id = :vinculo AND evento.codigo = :codigo " +
            "      AND lancamento.MESANOINICIAL = (SELECT MIN(LANC.MESANOINICIAL) " +
            "                                      FROM LANCAMENTOFP LANC " +
            "                                      WHERE LANC.VINCULOFP_ID = VINCULO.ID AND LANC.EVENTOFP_ID = evento.id) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("vinculo", vinculoFP.getId());
        q.setParameter("codigo", codigo);
        q.setMaxResults(1);
        try {

            return (Date) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<LancamentoFP> buscarLancamentosFoPorVinculo(VinculoFP vinculoFP) {
        String sql = " select lanc.* from LANCAMENTOFP lanc " +
            "where lanc.VINCULOFP_ID = :vinculo ";
        Query q = em.createNativeQuery(sql, LancamentoFP.class);
        q.setParameter("vinculo", vinculoFP.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

}
