/*
 * Codigo gerado automaticamente em Wed Jun 29 14:01:43 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.EventoFPEmpregador;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.enums.TipoEventoFP;
import br.com.webpublico.enums.TipoExecucaoEP;
import br.com.webpublico.enums.rh.administracaopagamento.IdentificadorBaseFP;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.esocial.service.ESocialService;
import br.com.webpublico.esocial.service.ESocialUtilService;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.rh.esocial.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;

@Stateless
public class EventoFPFacade extends AbstractFacade<EventoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private IncidenciaTributariaPrevidenciaFacade incidenciaTributariaPrevidenciaFacade;
    @EJB
    private IncidenciaTributariaIRRFFacade incidenciaTributariaIRRFFacade;
    @EJB
    private IncidenciaTributariaFGTSFacade incidenciaTributariaFGTSFacade;
    @EJB
    private IncidenciaTributariaRPPSFacade incidenciaTributariaRPPSFacade;
    @EJB
    private NaturezaRubricaFacade naturezaRubricaFacade;
    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ClassificacaoVerbaFacade classificacaoVerbaFacade;

    private ESocialUtilService eSocialUtilService;

    private ESocialService eSocialService;


    @PostConstruct
    public void init() {
        eSocialUtilService = (ESocialUtilService) Util.getSpringBeanPeloNome("eSocialUtilService");
        eSocialService = (ESocialService) Util.getSpringBeanPeloNome("eSocialService");
    }

    public EventoFPFacade() {
        super(EventoFP.class);
    }

    public void enviarEventoS1010(ConfiguracaoEmpregadorESocial configuracao, EventoFP evento) throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        eSocialUtilService.getS1010Service().enviar1010(configuracao, ve, evento);
    }

    public IncidenciaTributariaPrevidenciaFacade getIncidenciaTributariaPrevidenciaFacade() {
        return incidenciaTributariaPrevidenciaFacade;
    }

    public IncidenciaTributariaIRRFFacade getIncidenciaTributariaIRRFFacade() {
        return incidenciaTributariaIRRFFacade;
    }

    public IncidenciaTributariaFGTSFacade getIncidenciaTributariaFGTSFacade() {
        return incidenciaTributariaFGTSFacade;
    }

    public ConfiguracaoEmpregadorESocialFacade getConfiguracaoEmpregadorESocialFacade() {
        return configuracaoEmpregadorESocialFacade;
    }

    public IncidenciaTributariaRPPSFacade getIncidenciaTributariaRPPSFacade() {
        return incidenciaTributariaRPPSFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public NaturezaRubricaFacade getNaturezaRubricaFacade() {
        return naturezaRubricaFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvar(EventoFP entity) {
        super.salvar(entity);
    }

    @Override
    public void salvarNovo(EventoFP entity) {
        super.salvarNovo(entity);
        EventoFP evento = recarregar(entity);
        if (entity.getConsignacao()) {
            if (entity.getEventoFPAgrupador() == null) {
                evento.setEventoFPAgrupador(evento);
            }
        } else {
            entity.setEventoFPAgrupador(null);
        }
        em.merge(evento);
    }

    @Override
    public EventoFP recuperar(Object id) {
        EventoFP efp = em.find(EventoFP.class, id);
        efp.getItensBasesFPs().size();
        efp.getEventoFPUnidade().size();
        Set<BaseFP> bases = new LinkedHashSet<>();
        for (ItemBaseFP itemBaseFP : efp.getItensBasesFPs()) {
            itemBaseFP.getBaseFP().getItensBasesFPs().size();
        }

        efp.setBaseFPs(new ArrayList<>(bases));
        Hibernate.initialize(efp.getTiposFolha());
        Hibernate.initialize(efp.getBloqueiosFuncionalidade());
        Hibernate.initialize(efp.getItensEventoFPEmpregador());
        return efp;
    }

    public EventoFP recuperarComUnidade(Object id) {
        EventoFP evento = em.find(EventoFP.class, id);
        evento.getEventoFPUnidade().size();
        return evento;
    }

    public boolean verificaCodigoSalvo(String codigo) {
        Query q = em.createQuery("from EventoFP e where e.codigo = :codigo");
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }

    /**
     * Método utilizando para editar, casa o codigo ja esteja em utilização não
     * salva.
     *
     * @param evento
     * @return
     */
    public boolean verificaCodigoEditar(EventoFP evento) {
        Query q = em.createQuery("from EventoFP e where (e.codigo = :codigo and e = :evento)");
        q.setParameter("evento", evento);
        q.setParameter("codigo", evento.getCodigo());
        return !q.getResultList().isEmpty();
    }

    public List<EventoFP> listaEventosAtivosPorTipoExecucao(TipoExecucaoEP tipo, boolean automatico) {
        Query q = em.createQuery("select distinct e from EventoFP e left join fetch e.tiposFolha tipoFolha where e.automatico = :automatico and e.tipoExecucaoEP = :tipo and e.ativo is true order by e.codigo");
        q.setParameter("tipo", tipo);
        q.setParameter("automatico", automatico);
        List<EventoFP> resultList = q.getResultList();
        for (EventoFP eventoFP : resultList) {
            Hibernate.initialize(eventoFP.getTiposFolha());
        }
        return resultList;
    }

    public List<EventoFP> listaTodosEventosAtivosPorTipoExecucao(TipoExecucaoEP tipo, boolean automatico) {
        Query q = em.createQuery("select distinct e from EventoFP e left join fetch e.tiposFolha tipoFolha where  e.tipoExecucaoEP = :tipo and e.ativo is true order by e.codigo");
        q.setParameter("tipo", tipo);
        return q.getResultList();
    }

    public List<EventoFP> findEventosPorIdentificacao(IdentificacaoEventoFP identifiacao) {
        Query q = em.createQuery("select distinct e from EventoFP e left join fetch e.tiposFolha where e.identificacaoEventoFP = :tipo and e.ativo is true");
        q.setParameter("tipo", identifiacao);
        return q.getResultList();
    }

    public List<EventoFP> listaEventosLancados(VinculoFP vinculo, Integer mes, Integer ano) {
        Query q = em.createQuery(" select evento from LancamentoFP lancamento inner join "
            + " lancamento.eventoFP evento where lancamento.vinculoFP = :vinculo "
            + " and :dataParam >= lancamento.mesAnoInicial "
            + " and :dataParam <= coalesce(lancamento.mesAnoFinal,:dataParam) "
            //+ " and evento.automatico is not true"
            + " and evento.ativo is true");
        try {
            q.setParameter("vinculo", vinculo);
            q.setParameter("dataParam", Util.criaDataPrimeiroDiaDoMesFP(mes, ano));
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public List<EventoFP> listaEventosRH(TipoExecucaoEP tipoExecucaoEP) {
        Query q = em.createQuery("from EventoFP e where e.tipoExecucaoEP = :tipo order by e.codigo ");
        q.setParameter("tipo", tipoExecucaoEP);
        return q.getResultList();
    }

    public List<EventoFP> recuperarEventosAtivosRHPorTipoExecucao(TipoExecucaoEP tipoExecucaoEP) {
        Query q = em.createQuery("from EventoFP e where e.tipoExecucaoEP = :tipo and e.ativo = true order by e.codigo ");
        q.setParameter("tipo", tipoExecucaoEP);
        return q.getResultList();
    }

    public List<EventoFP> listaEventosLike(String parte, TipoExecucaoEP tipoExecucaoEP) {
        Query q = em.createQuery("from EventoFP e where e.tipoExecucaoEP = :tipo and (e.descricao like :filtro or e.codigo like :filtro)");
        q.setParameter("filtro", "%" + parte.trim() + "%");
        q.setParameter("tipo", tipoExecucaoEP);
        return q.getResultList();
    }

    public List<EventoFP> listaEventoFPNaoVigentesNoConsignatario(String s) {
        Date d = Util.getDataHoraMinutoSegundoZerado(new Date());

        Query q = em.createQuery(" select eventoFP from EventoFP eventoFP where eventoFP not in("
            + " select e from ItemEntidadeConsignataria item "
            + " inner join item.eventoFP e "
            + " where :dataVigencia >= item.inicioVigencia and "
            + " :dataVigencia <= coalesce(item.finalVigencia,:dataVigencia)) "
            + " and eventoFP.tipoEventoFP = 'DESCONTO' and (lower(eventoFP.codigo) like :filtro or lower(eventoFP.descricao) like :filtro)");
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataVigencia", d);

        return q.getResultList();
    }

    public List<EventoFP> listaEventoFPDesconto(String s) {
        Query q = em.createQuery(" select eventoFP from EventoFP eventoFP where (lower(eventoFP.codigo) like :filtro or lower(eventoFP.descricao) like :filtro)");
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        return q.getResultList();
    }

    public EventoFP recuperaEvento(String codigo, TipoExecucaoEP tipo) {
        Query q = em.createQuery("from EventoFP e where e.codigo = :codigo and e.tipoExecucaoEP = :tipo");
        q.setParameter("codigo", codigo);
        q.setParameter("tipo", tipo);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        EventoFP e = (EventoFP) q.getSingleResult();
        Hibernate.initialize(e.getTiposFolha());
        return e;
    }

    public EventoFP recuperaEventoAtivo(String codigo, TipoExecucaoEP tipo) {
        Query q = em.createQuery("from EventoFP e where e.codigo = :codigo and e.tipoExecucaoEP = :tipo and e.ativo = true");
        q.setParameter("codigo", codigo);
        q.setParameter("tipo", tipo);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (EventoFP) q.getSingleResult();
    }

    public EventoFP recuperaPorCodigo(String codigo) {
        Query q = em.createQuery("from EventoFP e where e.codigo = :codigo ");
        q.setParameter("codigo", codigo);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (EventoFP) q.getResultList().get(0);
    }

    public List<EventoFP> recuperaEventoDuplicados(String codigo) {
        Query q = em.createQuery("select e from EventoFP e where e.codigo = :codigo and e.tipoExecucaoEP = :tipo");
        q.setParameter("codigo", codigo);
        q.setParameter("tipo", TipoExecucaoEP.FOLHA);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return q.getResultList();
    }


    public EventoFP recuperaEventoEstornoParaFerias() {
        Query q = em.createQuery("from EventoFP e where e.estornoFerias is true");
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return recuperaEvento(151 + "", TipoExecucaoEP.FOLHA);
        }
        return (EventoFP) q.getSingleResult();
    }

    public List<EventoFP> listaEventosAtivosFolha() {
        return em.createQuery("from EventoFP e where e.tipoExecucaoEP = :tipo and e.ativo is true order by cast(e.codigo as integer)").setParameter("tipo", TipoExecucaoEP.FOLHA).getResultList();
    }

    public List<EventoFP> buscarEventosAtivosPorTipoEvento(TipoEventoFP tipo) {
        Query query = em.createQuery("select e from EventoFP e where " +
            "               e.tipoExecucaoEP = :tipo " +
            "               and e.tipoEventoFP = :tipoEvento " +
            "               and e.ativo is true " +
            "               order by cast(e.codigo as integer)");
        query.setParameter("tipo", TipoExecucaoEP.FOLHA);
        query.setParameter("tipoEvento", tipo);
        return query.getResultList();
    }

    public List<EventoFP> listaFiltrandoEventosAtivos(String s) {
        Query q = em.createQuery(" from EventoFP e where e.ativo is true "
            + " and (lower(codigo) like :filtro or lower(descricao) like :filtro) ");
        q.setParameter("filtro", "%" + s.trim().toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<EventoFP> buscarFiltrandoEventosAtivosOrdenadoPorCodigo(String s) {
        Query q = em.createQuery(" from EventoFP e where e.ativo is true "
            + " and (lower(codigo) like :filtro or lower(descricao) like :filtro) "
            + " order by to_number(e.codigo) ");
        q.setParameter("filtro", "%" + s.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public void backupValoresEventoFPRescisao() {
        List<EventoFP> eventos = new ArrayList<>();
//        evento 119;
//        return (calculador.recuperaTipoFolha() == 'RESCISAO');

//        if(calculador.totalDiasDireitoProporcional() == 0 || calculador.calculaBase('1004') == 0){
//            return 0;
//        }
//        return ((calculador.calculaBaseSemRetroativo('1004') * calculador.totalDiasDireitoProporcional()) / 12);
//            refe
//        return calculador.totalDiasDireitoProporcional();

//        return calculador.calculaBase('1004');


        //        evento 120- Indenização de férias;
//        return (calculador.recuperaTipoFolha() == 'RESCISAO');
        //Formula
//        if(calculador.totalMesesIndenizacaoFerias() < 12){
//            println('retornou antes');
//            return 0;
//        }
//        var meses = calculador.totalMesesIndenizacaoFerias();
//        if(  (meses/12>>0) >= 1 ){
//            return ((meses/12>>0) * calculador.calculaBaseSemRetroativo('1004')) * 2;
//        } else {
//            return 0;
//        }
//
//        return (Math.floor(calculador.totalDiasDireitoProporcional()) /12);
//        return calculador.calculaBaseSemRetroativo('1004');

//        evento 248 - 13 PROPORCIONAL
//        return (calculador.recuperaTipoFolha() == 'RESCISAO');

//        return (calculador.calculaBase('1080') / 12)  * calculador.quantidadeMesesTrabalhadosAno() ;
//        return calculador.quantidadeMesesTrabalhadosAno() ;
//        return calculador.calculaBase('1080');

//          170 Media Hora Extra Décimo
//            Regra
//        return calculador.recuperaTipoFolha() == 'RESCISAO' || calculador.recuperaTipoFolha() == 'SALARIO_13';
//        formula
//        if (calculador.recuperaTipoFolha() == 'RESCISAO'){
//            var totalHoraAno100 = calculador.totalHorasExtrasAnual(145);
//            var totalHoraAno50 = calculador.totalHorasExtrasAnual(144);
//            if(totalHoraAno100 > 0){
//                totalHoraAno100 = totalHoraAno100 * 2;
//            }
//            if(totalHoraAno50 > 0){
//                totalHoraAno50 = totalHoraAno50 * 1.5;
//            }
//            if(totalHoraAno50 > 0 || totalHoraAno100 > 0){
//                var vlr_hor_serv = calculador.calculaBase(1006) / calculador.obterHoraMensal() * ( (totalHoraAno100 + totalHoraAno50) /12 );
//                return vlr_hor_serv;
//            }
//        }
//        return 0;
//        referencia
//        return calculador.totalHorasExtrasAnual(144,145);
//        Base
//        return 0;
    }


    public List<EventoFP> listaFiltrandoEventosAtivosEPermiteLancamento(String s) {
        Query q = em.createQuery(" from EventoFP e where e.ativo is true and e.naoPermiteLancamento is false"
            + " and (lower(codigo) like :filtro or lower(descricao) like :filtro) ");
        q.setParameter("filtro", "%" + s.trim().toLowerCase() + "%");
        q.setMaxResults(7);
        return q.getResultList();
    }

    public List<EventoFP> buscarEventosAtivosAndPermiteLancamento(String s) {
        Query q = em.createQuery("select new br.com.webpublico.entidades.EventoFP(e.id,e.codigo, e.descricao, e.tipoEventoFP) from EventoFP e where e.ativo is true and e.naoPermiteLancamento is false"
            + " and (lower(codigo) like :filtro or lower(descricao) like :filtro) ");
        q.setParameter("filtro", "%" + s.trim().toLowerCase() + "%");
        q.setMaxResults(7);
        return q.getResultList();
    }

    public List<EventoFP> listarEventosConsignados() {
        Query q = em.createQuery(" from EventoFP e where e.ativo = true and e.tipoDeConsignacao is not null");
        return q.getResultList();
    }

    public List<EventoFP> listaFiltrandoAutoComplete(String s, String... atributos) {
        String hql = " select new EventoFP(evento.id, evento.codigo, evento.descricao, evento.tipoEventoFP) "
            + " from EventoFP evento where rownum < 11 and ( ";
        for (String atributo : atributos) {
            hql += "lower(evento." + atributo + ") like :filtro OR ";
        }

        hql = hql.substring(0, hql.length() - 3) + ")";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public BigDecimal getSomaDosEventosPorAno(ContratoFP c, Integer ano, String codigoModuloExportacao, String codigoGrupoExportacao) {
        String sql = " select (select coalesce(sum(iff.valor),0) from itemfichafinanceirafp iff                    "
            + "inner join fichafinanceirafp ff on ff.id = iff.fichafinanceirafp_id                             "
            + "inner join folhadepagamento  fp on fp.id = ff.folhadepagamento_id                               "
            + "     where fp.ano = :ano                                                                        "
            + "       and ff.vinculofp_id = :vinculo_id                                                        "
            + "       and iff.eventofp_id in (select ige.eventofp_id from itemgrupoexportacao ige              "
            + "                           inner join grupoexportacao ge on ige.grupoexportacao_id = ge.id      "
            + "                           inner join moduloexportacao me on ge.moduloexportacao_id = me.id     "
            + "                                where me.codigo = :codigoModuloExportacao                       "
            + "                                  and ige.operacaoformula = :adicao                             "
            + "                                  and ge.codigo = :codigoGrupoExportacao))                      "
            + " -                                                                                              "
            + "    (select coalesce(sum(iff.valor),0) from itemfichafinanceirafp iff                           "
            + " inner join fichafinanceirafp ff on ff.id = iff.fichafinanceirafp_id                            "
            + " inner join folhadepagamento fp on fp.id = ff.folhadepagamento_id                               "
            + "      where fp.ano = :ano                                                                       "
            + "        and ff.vinculofp_id = :vinculo_id                                                       "
            + "        and iff.eventofp_id in (select ige.eventofp_id from itemgrupoexportacao ige             "
            + "                            inner join grupoexportacao ge on ige.grupoexportacao_id = ge.id     "
            + "                            inner join moduloexportacao me on ge.moduloexportacao_id = me.id    "
            + "                                 where me.codigo = :codigoModuloExportacao                          "
            + "                                   and ige.operacaoformula = :subtracao                         "
            + "                                   and ge.codigo = :codigoGrupoExportacao)) as resultado from dual ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", ano);
        q.setParameter("vinculo_id", c.getId());
        q.setParameter("codigoModuloExportacao", codigoModuloExportacao);
        q.setParameter("adicao", OperacaoFormula.ADICAO.toString());
        q.setParameter("subtracao", OperacaoFormula.SUBTRACAO.toString());
        q.setParameter("codigoGrupoExportacao", codigoGrupoExportacao);

        try {
            BigDecimal resultado = (BigDecimal) q.getSingleResult();
            if (resultado == null) {
                return BigDecimal.ZERO;
            }
            return resultado;
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    public Boolean verificaEventoCadastrados(String codigo) {
        Query q = em.createQuery("from EventoFP e inner join e.tipoPrevidenciaFP where e.codigo = :codigo ");
        q.setParameter("codigo", codigo);
        return q.getResultList() != null && !q.getResultList().isEmpty();
    }

    public List<EventoFP> buscarEventoPorTipoPrevidencia(TipoPrevidenciaFP tipoPrevidencia) {
        Query q = em.createQuery("select e from EventoFP e where e.tipoPrevidenciaFP = :tipo");
        q.setParameter("tipo", tipoPrevidencia);
        if (q.getResultList().isEmpty()) {
            return Lists.newArrayList();
        }
        return q.getResultList();
    }

    public Boolean incidenciaPorCodigo(String codigo, EventoFP evento) {
        String sql = "SELECT" +
            "            ev.id" +
            "  FROM " +
            "  EVENTOFP EV " +
            "INNER JOIN ITEMBASEFP ITEM ON  EV.ID = ITEM.EVENTOFP_ID " +
            "INNER JOIN basefp base ON  base.id = item.basefp_id " +
            "WHERE" +
            "  base.codigo  = :codigo" +
            " AND ev.id     = :evento ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", codigo);
        q.setParameter("evento", evento.getId());

        return !q.getResultList().isEmpty();
    }

    public Boolean incidenciaPorIdentificador(EventoFP evento, IdentificadorBaseFP... tipos) {
        String sql = "SELECT" +
            "            ev.id" +
            "  FROM " +
            "  EVENTOFP EV " +
            "INNER JOIN ITEMBASEFP ITEM ON  EV.ID = ITEM.EVENTOFP_ID " +
            "INNER JOIN basefp base ON  base.id = item.basefp_id " +
            "WHERE" +
            "  base.tipoBaseFP in(:tipo) " +
            " AND ev.id  = :evento ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("tipo", Util.getListOfEnumName(tipos));
        q.setParameter("evento", evento.getId());

        return !q.getResultList().isEmpty();
    }

    public Boolean incidenciaPorCodigo(String codigo, Long eventoID) {
        String sql = "SELECT" +
            "            ev.id" +
            "  FROM " +
            "  EVENTOFP EV " +
            "INNER JOIN ITEMBASEFP ITEM ON  EV.ID = ITEM.EVENTOFP_ID " +
            "INNER JOIN basefp base ON  base.id = item.basefp_id " +
            "WHERE" +
            "  base.codigo  = :codigo" +
            " AND ev.id     = :evento ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", codigo);
        q.setParameter("evento", eventoID);

        return !q.getResultList().isEmpty();
    }

    public List<EventoFP> getEventoFPPorEmpregador(Entidade empregador) {
        String sql = "select * from eventofp where ENTIDADE_ID = :idEntidade";
        Query q = em.createNativeQuery(sql, EventoFP.class);
        q.setParameter("idEntidade", empregador.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }

    public Boolean incidenciaIRRF(EventoFP evento) {
        return incidenciaPorCodigo("1003", evento);
    }

    public Boolean incidenciaINSS(EventoFP evento) {
        return incidenciaPorCodigo("1002", evento);
    }

    public Boolean incidenciaRPPS(EventoFP evento) {
        return incidenciaPorCodigo("1001", evento);
    }

    public boolean hasIncidenciaIRRF(Long eventoID) {
        return incidenciaPorCodigo("1003", eventoID);
    }

    public boolean hasIncidenciaINSS(Long eventoID) {
        return incidenciaPorCodigo("1002", eventoID);
    }

    public boolean hasIncidenciaRPPS(Long eventoID) {
        return incidenciaPorCodigo("1001", eventoID);
    }


    public boolean hasIncidenciaCodigosIRRF(List<String> codigos, Long idEvento) {
        String sql = " select evento.* from eventofp evento " +
            " inner join incidenciatributariairrf incidencia on evento.incidenciatributariairrf_id = incidencia.id " +
            " where incidencia.codigo in :codigos" +
            " and evento.id = :idEvento ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("codigos", codigos);
        q.setParameter("idEvento", idEvento);

        return !q.getResultList().isEmpty();
    }

    public List<ClassificacaoVerba> buscarClassificacoesVerbas(String filtro) {
        return classificacaoVerbaFacade.listaFiltrandoOrdenada(filtro.trim(), null, "obj.codigo asc ", "codigo", "descricao");
    }

    public List<EventoESocialDTO> getEventoPorIdentificador(Long id) {
        try {
            return eSocialService.getEventosPorIdEsocial(id.toString());
        } catch (Exception e) {
            logger.debug("Não foi possível buscar o histórico do esocial para o contrato");
            return null;
        }
    }

    public EventoFPEmpregador getEventoFPEmpregador(EventoFP evento, Date dataReferencia, Entidade entidade) {
        String sql = " select eventoEmpregador.* from EVENTOFPEMPREGADOR eventoEmpregador" +
            " where EVENTOFP_ID = :eventoFP and ENTIDADE_ID = :entidade and :dataReferencia between INICIOVIGENCIA and coalesce(FIMVIGENCIA, :dataReferencia)";
        Query q = em.createNativeQuery(sql, EventoFPEmpregador.class);
        q.setParameter("eventoFP", evento.getId());
        q.setParameter("entidade", entidade.getId());
        q.setParameter("dataReferencia", dataReferencia);
        List result = q.getResultList();
        if (!result.isEmpty()) {
            return (EventoFPEmpregador) result.get(0);
        }
        return null;

    }

    public List<EventoFP> buscarEventoFPs(List<Long> eventoFPIds) {
        String sql = "select * from eventofp e where e.id in :eventoFPIds";
        Query q = em.createNativeQuery(sql, EventoFP.class);
        q.setParameter("eventoFPIds", eventoFPIds);
        List resultado = q.getResultList();
        if (resultado != null) {
            return resultado;
        }
        return Lists.newArrayList();
    }

    public List<Object[]> buscarEventosNaoConfigurados(Mes mes, Integer ano, List<Long> idsRecursos, List<String> codigosEventos) {
        String sql = "select distinct rec.codigo as recursoFP, " +
            "                cast(evento.codigo as int) as evento, " +
            "                evento.descricao as descricao, " +
            "                evento.id as event, " +
            "                rec.id as rec " +
            "from fichafinanceirafp ficha " +
            "  inner join vinculofp vinc on ficha.vinculofp_id = vinc.id " +
            "  inner join matriculafp mat on vinc.matriculafp_id = mat.id " +
            "  inner join pessoafisica pes on mat.pessoa_id = pes.id " +
            "  inner join itemfichafinanceirafp item on ficha.id = item.fichafinanceirafp_id " +
            "  inner join folhadepagamento folha on ficha.folhadepagamento_id = folha.id " +
            "  inner join eventofp evento on evento.id = item.eventofp_id " +
            "  inner join recursofp rec on rec.id = ficha.recursofp_id " +
            "  left join fontesrecursofp fonteR on rec.id = fonteR.recursofp_id and " +
            "                                      extract(year from fonteR.iniciovigencia) = folha.ano and " +
            "                                      extract(month from fonteR.iniciovigencia) = (folha.mes + 1) " +
            "  left join fonteeventofp fonteEvento on fonteR.id = fonteEvento.fontesrecursofp_id and " +
            "                                         item.eventofp_id = fonteEvento.eventofp_id " +
            " where folha.mes = :mes " +
            "   and folha.ano = :ano " +
            "   and fonteEvento.eventofp_id is null " +
            ((idsRecursos != null && !idsRecursos.isEmpty()) ? " and rec.id in :idsRecursos " : "") +
            ((codigosEventos != null && !codigosEventos.isEmpty()) ? " and evento.codigo in :codigosEventos " : "") +
            " order by 1, 2 ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", ano);
        q.setParameter("mes", mes.getNumeroMesIniciandoEmZero());
        if (idsRecursos != null && !idsRecursos.isEmpty()) {
            q.setParameter("idsRecursos", idsRecursos);
        }
        if (codigosEventos != null && !codigosEventos.isEmpty()) {
            q.setParameter("codigosEventos", codigosEventos);
        }
        return q.getResultList();
    }

    public List<EventoFP> buscarEventos(String parte, Mes mes, Integer ano, List<Long> idsRecursos) {
        if (parte == null || mes == null || ano == null) return Lists.newArrayList();
        String sql = "select distinct evento.id, evento.codigo, evento.descricao " +
            " from fichafinanceirafp ficha " +
            "  inner join vinculofp vinc on ficha.vinculofp_id = vinc.id " +
            "  inner join matriculafp mat on vinc.matriculafp_id = mat.id " +
            "  inner join pessoafisica pes on mat.pessoa_id = pes.id " +
            "  inner join itemfichafinanceirafp item on ficha.id = item.fichafinanceirafp_id " +
            "  inner join folhadepagamento folha on ficha.folhadepagamento_id = folha.id " +
            "  inner join eventofp evento on evento.id = item.eventofp_id " +
            " where folha.mes = :mes " +
            "   and folha.ano = :ano " +
            "   and (evento.codigo like :parte or lower(evento.descricao) like :parte)" +
            ((idsRecursos != null && !idsRecursos.isEmpty()) ? " and ficha.recursofp_id in :idsRecursos " : "") +
            " order by cast(evento.codigo as int) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", ano);
        q.setParameter("mes", mes.getNumeroMesIniciandoEmZero());
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        if (idsRecursos != null && !idsRecursos.isEmpty()) {
            q.setParameter("idsRecursos", idsRecursos);
        }
        List<EventoFP> retorno = Lists.newArrayList();
        List<Object[]> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                EventoFP evento = new EventoFP();
                evento.setId(((BigDecimal) obj[0]).longValue());
                evento.setCodigo((String) obj[1]);
                evento.setDescricao((String) obj[2]);
                retorno.add(evento);
            }
        }
        return retorno;
    }
}
