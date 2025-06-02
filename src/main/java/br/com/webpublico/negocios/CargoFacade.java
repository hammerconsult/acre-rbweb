/*
 * Codigo gerado automaticamente em Fri Mar 04 09:37:47 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.CargoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidadesauxiliares.CargoSiprev;
import br.com.webpublico.entidadesauxiliares.ItemVaga;
import br.com.webpublico.enums.EsferaDoPoder;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoPCS;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.esocial.service.ESocialService;
import br.com.webpublico.esocial.service.S1030Service;
import br.com.webpublico.esocial.service.S1035Service;
import br.com.webpublico.esocial.service.S1040Service;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Stateless
public class CargoFacade extends AbstractFacade<Cargo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private AreaFormacaoFacade areaFormacaoFacade;
    @EJB
    private HabilidadeFacade habilidadeFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ClassificacaoCargoFacade classificacaoCargoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private ESocialService eSocialService;
    private S1030Service s1030Service;
    private S1035Service s1035Service;
    private S1040Service s1040Service;


    public CargoFacade() {
        super(Cargo.class);
    }

    @PostConstruct
    public void init() {
        eSocialService = (ESocialService) Util.getSpringBeanPeloNome("eSocialService");
        s1030Service = (S1030Service) Util.getSpringBeanPeloNome("s1030Service");
        s1035Service = (S1035Service) Util.getSpringBeanPeloNome("s1035Service");
        s1040Service = (S1040Service) Util.getSpringBeanPeloNome("s1040Service");
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }


    public List<EventoESocialDTO> getEventoPorIdentificador(Long id) {
        try {
            return eSocialService.getEventosPorIdEsocial(id.toString());
        } catch (Exception e) {
            logger.debug("Não foi possível buscar o histórico do esocial para o cargo");
            return null;
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void remover(Cargo entity) {
        super.remover(entity);
    }


    @Override
    public Cargo recuperar(Object id) {
        Cargo c = em.find(Cargo.class, id);
        c.getBaseCargos().size();
        c.getVagas().size();
        c.getCbos().size();
        c.getAreasFormacao().size();
        c.getHabilidades().size();
        c.getCargoEventoFP().size();
        c.getItensCargoSindicato().size();
        c.getItemConfiguracaoCargoEventoFP().size();
        Hibernate.initialize(c.getEmpregadores());
        Hibernate.initialize(c.getUnidadesCargo());
        return c;
    }

    public boolean verificaDependencia(BaseCargo base) {
        if (base.getId() == null) {
            return true;
        }

        Query q = em.createQuery("from PeriodoAquisitivoFL bp where bp.baseCargo = :base");
        q.setParameter("base", base);
        return q.getResultList().isEmpty();
    }

    public boolean verificarDuplicidadeCodigoCargo(Cargo cargo) {
        String hql = " from Cargo c where lower(trim(c.codigoDoCargo)) = :codigoParametro ";
        if (cargo.getId() != null) {
            hql += " and c.id <> :idCargo";
        }
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("codigoParametro", cargo.getCodigoDoCargo().trim().toLowerCase());
        if (cargo.getId() != null) {
            q.setParameter("idCargo", cargo.getId());
        }
        q.setMaxResults(1);

        return !q.getResultList().isEmpty();
    }

    public String retornaCodigoPorTipo(TipoPCS tipo) {
        Query q = em.createNativeQuery(" SELECT coalesce(max(to_number(c.codigodocargo)),0) + 1 FROM cargo c "
            + " WHERE c.tipopcs = :tipoPCS");
        q.setParameter("tipoPCS", tipo.name());
        Integer toReturn = ((BigDecimal) q.getSingleResult()).intValue();
        return toReturn.toString();
    }


    public List<CargoSiprev> retornarCargosSiprev(TipoPCS tipo, Date data) {
        String sql = " select cargo.descricao, min(enq.vencimentobase) as valorminimo, max(enq.vencimentobase) as valormaximo, " +
            " cargo.iniciovigencia, cargo.codigodocargo, cargo.permiteacumulo," +
            " cargo.finalvigencia, entidade.nome, entidade.esferadopoder, " +
            " cargo.codigocarreira, cargo.atolegal_id as atolegalid, ato.datapublicacao, ato.dataemissao as inicioVigenciaAto, " +
            " ato.numero as numeroato, exercicio.ano as anoAto, ato.fundamentacaolegal as ementa, cargo.aposentadoriaEspecialSIPREV, cargo.tipoCargoAcumulavelSIPREV," +
            " cargo.tipoContagemSIPREV, cargo.dedicacaoExclusivaSIPREV, cargo.tecnicoCientificoSIPREV, cargo.id, plano.id as planoId " +
            " from enquadramentopcs enq " +
            " inner join progressaopcs prog on prog.id = enq.progressaopcs_id " +
            " inner join planocargossalarios planoProg on prog.planocargossalarios_id = planoProg.id " +
            " inner join categoriapcs cat on cat.id = enq.categoriapcs_id " +
            " inner join planocargossalarios plano on cat.planocargossalarios_id = plano.id " +
            " left join EntidadePCS entidadepcs on entidadepcs.planocargossalarios_id = plano.id " +
            " left join entidade on entidadepcs.entidade_id = entidade.id " +
            " inner join cargocategoriapcs cargocat on cargocat.categoriapcs_id = cat.id " +
            " inner join cargo on cargo.id = cargocat.cargo_id " +
            " left join atolegal ato on cargo.atolegal_id = ato.id " +
            " left join exercicio on ato.exercicio_id = exercicio.id " +
            " where :dataAtual between cargo.iniciovigencia and coalesce(cargo.finalvigencia, :dataAtual) and cargo.ativo = :ativo" +
            " and :dataAtual between ENQ.INICIOVIGENCIA and coalesce(ENQ.FINALVIGENCIA, :dataAtual) " +
            " and :dataAtual BETWEEN CARGOCAT.INICIOVIGENCIA AND COALESCE(CARGOCAT.FINALVIGENCIA, :dataAtual) " +
            " and plano.tipoPCS = :tipoPCS " +
            " and planoProg.tipoPCS = :tipoPCS " +
            " group by cargo.descricao, cargo.iniciovigencia, cargo.codigodocargo, " +
            " cargo.permiteacumulo, cargo.finalvigencia, entidade.nome, entidade.esferadopoder, cargo.codigocarreira, " +
            " ato.numero, exercicio.ano, ato.datapublicacao, ato.dataemissao, ato.fundamentacaolegal, cargo.atolegal_id, cargo.aposentadoriaEspecialSIPREV," +
            " cargo.tipoCargoAcumulavelSIPREV, cargo.tipoContagemSIPREV, cargo.dedicacaoExclusivaSIPREV, cargo.tecnicoCientificoSIPREV, cargo.id, plano.id ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataAtual", data);
        q.setParameter("ativo", 1);
        q.setParameter("tipoPCS", TipoPCS.QUADRO_EFETIVO.name());
        return listaDeCargosSiprev(q.getResultList());
    }

    private List<CargoSiprev> listaDeCargosSiprev(List<Object[]> resultado) {
        List<CargoSiprev> retorno = Lists.newArrayList();
        for (Object[] obj : resultado) {
            CargoSiprev cargo = new CargoSiprev();
            cargo.setDescricao((String) obj[0]);    //descricao
            cargo.setValorMinimo((BigDecimal) obj[1]);  //valorminimo
            cargo.setValorMaximo((BigDecimal) obj[2]);  //valormaximo
            cargo.setInicioVigencia((Date) obj[3]); //iniciovigencia
            cargo.setCodigoCargo((String) obj[4]);  //codigodocargo
            cargo.setPermiteAcumulo(obj[5] != null && (obj[5].equals(1) ? true : false));
            cargo.setFinalVigencia(obj[6] != null ? (Date) obj[6] : null);
            cargo.setNomeEntidade((String) obj[7]);
            cargo.setEsferaDoPoder(obj[8] != null ? EsferaDoPoder.valueOf((String) obj[8]) : null);
            cargo.setCodigoCarreira((String) obj[9]);
            cargo.setAtoLegalId(obj[10] != null ? (BigDecimal) obj[10] : null);
            cargo.setAtoDataPublicacao((Date) obj[11]);
            cargo.setAtoDataEmissao((Date) obj[12]);
            cargo.setAtoNumero((String) obj[13]);
            cargo.setAtoAno((BigDecimal) obj[14]);
            cargo.setAtoEmenta((String) obj[15]);
            cargo.setAposentadoriaEspecial(obj[16] != null ? (BigDecimal) obj[16] : BigDecimal.ZERO);
            cargo.setTipoCargoAcumulavelSIPREV(obj[17] != null ? (String) obj[17] : "");
            cargo.setTipoContagemSIPREV(obj[18] != null ? (String) obj[18] : "");
            cargo.setDedicacaoExclusiva(obj[19] != null ? (BigDecimal) obj[19] : BigDecimal.ZERO);
            cargo.setTecnicoCientifico(obj[20] != null ? (BigDecimal) obj[20] : BigDecimal.ZERO);
            cargo.setId((BigDecimal) obj[21]);
            cargo.setPlanoId((BigDecimal) obj[22]);
            retorno.add(cargo);
        }
        return retorno;
    }

    public String retornarPlanoDecargosSalario(CargoSiprev cargo, Date data) {
        String sql = "SELECT DISTINCT CAT.descricao || ' ' || prog.descricao  FROM ENQUADRAMENTOPCS ENQ " +
            "INNER JOIN CATEGORIAPCS CAT ON ENQ.CATEGORIAPCS_ID = CAT.ID " +
            "inner join PLANOCARGOSSALARIOS plano on CAT.PLANOCARGOSSALARIOS_ID = plano.id " +
            "INNER JOIN CARGOCATEGORIAPCS CARGOCAT ON CARGOCAT.CATEGORIAPCS_ID = CAT.ID " +
            "INNER JOIN progressaopcs prog on prog.id  = enq.progressaopcs_id " +
            "where (:data between (ENQ.INICIOVIGENCIA) and coalesce(ENQ.FINALVIGENCIA, :data)) " +
            " and (:data BETWEEN (CARGOCAT.INICIOVIGENCIA) AND COALESCE(CARGOCAT.FINALVIGENCIA, :data)) " +
            " AND  CARGOCAT.CARGO_ID = :id";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id", cargo.getId());
        q.setParameter("data", DataUtil.dataSemHorario(data));
        if (!q.getResultList().isEmpty()) {
            return (String) q.getResultList().get(0);
        }
        return "";
    }

    public UnidadeOrganizacional recuperaUnidadeOrganizacaionalDaCarreira(PlanoCargosSalarios planoCargosSalarios) {
        String sql = " SELECT entidade.* FROM PLANOCARGOSSALARIOS PLANO " +
            " INNER JOIN ENTIDADEPCS ENTIDADEPCS ON ENTIDADEPCS.PLANOCARGOSSALARIOS_ID = PLANO.ID " +
            " inner join entidade on ENTIDADEPCS.ENTIDADE_ID = entidade.id " +
            " where plano.id = :id ";
        Query q = em.createNativeQuery(sql, Entidade.class);
        if (q.getResultList().isEmpty()) {
            return (UnidadeOrganizacional) q.getResultList().get(0);
        }
        return null;
    }

    public List<Cargo> recuperaCargosVigentes(TipoPCS tipoPCS) {
        String hql = " from Cargo c where :dataVigencia >= c.inicioVigencia and "
            + " :dataVigencia <= coalesce(c.finalVigencia,:dataVigencia) ";

        if (tipoPCS != null) {
            hql += " and c.tipoPCS = :tipoPCSParametro ";
        }

        hql += " order by c.descricao ";

        Query q = em.createQuery(hql);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));

        if (tipoPCS != null) {
            q.setParameter("tipoPCSParametro", tipoPCS);
        }

        return q.getResultList();
    }

    public List<ItemVaga> recuperaVagasOcupadasPorHierarquia(Cargo cargo) {
        Query q = em.createQuery(" select new br.com.webpublico.entidadesauxiliares.ItemVaga"
            + "(unidade.descricao,count(lotacao.id)) from LotacaoFuncional lotacao,ContratoFP contrato "
            + " inner join lotacao.vinculoFP vinculo "
            + " inner join contrato.cargo cargo"
            + " inner join lotacao.unidadeOrganizacional unidade "
            + " where vinculo = contrato and :dataVigencia >= contrato.inicioVigencia and "
            + " :dataVigencia <= coalesce(contrato.finalVigencia,:dataVigencia) "
            + " and :dataVigencia >= lotacao.inicioVigencia and "
            + " :dataVigencia <= coalesce(lotacao.finalVigencia,:dataVigencia) "
            + " and cargo = :parametroCargo and contrato not in (from Estagiario estagiario) "
            + " group by unidade.descricao ");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        q.setParameter("parametroCargo", cargo);
        return q.getResultList();
    }

    public Integer recuperaTotalVagasPorCargo(Cargo cargo) {
        Query q = em.createQuery(" select coalesce(sum(coalesce(vaga.numeroVagas,0)),0) from Vaga vaga"
            + " inner join vaga.cargo cargo "
            + " where cargo = :parametroCargo "
            + " and :dataVigencia >= cargo.inicioVigencia and "
            + " :dataVigencia <= coalesce(cargo.finalVigencia,:dataVigencia) ");
//                + " and cargo.tipoPCS = :parametroTipoPCS ");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        q.setParameter("parametroCargo", cargo);
//        q.setParameter("parametroTipoPCS", tipoPCS);

        q.setMaxResults(1);
        Long l = (Long) q.getSingleResult();
        return Integer.valueOf(l.toString());
    }

    /**
     * Método retorna as vagas ocupadas por cargo, contabilizando apenas as
     * lotações funcionais vigentes em cada contratofp, não contabiliza as vagas
     * ocupadas pelos estagiários
     *
     * @param cargo
     * @return
     */
    public Integer buscarTotalVagasOcupadasPorCargo(Cargo cargo) {
        String sql = " select coalesce(count(c.id),0) as resultado from contratofp c " +
            "      inner join vinculofp vfp on vfp.id = c.id" +
            "           where c.cargo_id = :cargo_id" +
            "             and :data_referencia >= vfp.iniciovigencia and :data_referencia <= coalesce(vfp.finalvigencia, :data_referencia)";

        Query q = em.createNativeQuery(sql);

        q.setParameter("data_referencia", UtilRH.getDataOperacao());
        q.setParameter("cargo_id", cargo.getId());

        try {
            return Integer.parseInt("" + q.getSingleResult());
        } catch (NoResultException nre) {
            return 0;
        }
    }

    public List<ItemVaga> recuperaTotalVagasPorHierarquia(Cargo cargo) {
        Query q = em.createQuery(" select new " + new ItemVaga().getClass().getCanonicalName()
            + "(unidade.descricao,count(lotacao.id)) from LotacaoFuncional lotacao,ContratoFP contrato "
            + " inner join lotacao.vinculoFP vinculo "
            + " inner join contrato.cargo cargo"
            + " inner join lotacao.unidadeOrganizacional unidade "
            + " where contrato = vinculo and :dataVigencia >= contrato.inicioVigencia and "
            + " :dataVigencia <= coalesce(contrato.finalVigencia,:dataVigencia) "
            + " and :dataVigencia >= lotacao.inicioVigencia and "
            + " :dataVigencia <= coalesce(lotacao.finalVigencia,:dataVigencia) "
            + " and cargo = :parametroCargo and contrato not in (from Estagiario estagiario) "
            + " group by unidade.descricao ");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        q.setParameter("parametroCargo", cargo);
        return q.getResultList();
    }

    public List<Cargo> lista(TipoPCS tipoPCS) {
        Query q = em.createQuery(" from Cargo c where c.tipoPCS = :parametroTipoPCS order by c.descricao,c.codigoDoCargo");
        q.setParameter("parametroTipoPCS", tipoPCS);
        return q.getResultList();
    }

    /**
     * Retorna os cargos ativos e vigentes com a data do sistema.
     *
     * @param parte
     * @param tipoPCS
     * @return
     */
    public List<Cargo> buscarCargosPorTipoPCSAndVigenteAndAtivoAndCodigoOrDescricao(String parte, TipoPCS tipoPCS) {
        Query q = em.createQuery(" from Cargo c where c.tipoPCS = :parametroTipoPCS "
            + " and :data between c.inicioVigencia and coalesce(c.finalVigencia,:data) "
            + " and c.ativo = true "
            + " and (lower(c.codigoDoCargo) like :parte or lower(c.descricao) like :parte) "
            + " order by c.descricao, c.codigoDoCargo");
        q.setParameter("parametroTipoPCS", tipoPCS);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("data", UtilRH.getDataOperacao());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Cargo> buscarCargosPorTipoPCSAndVigenteAndAtivoAndCodigoOrDescricaoAndUnidadeOrganizacionalUsuario(String parte, TipoPCS tipoPCS) {
        String sql = " select c.* from Cargo c where c.TIPOPCS = :parametroTipoPCS  " +
            " and to_date(:data,'dd/MM/yyyy') between trunc(c.inicioVigencia) and coalesce(trunc(c.finalVigencia), to_date(:data,'dd/MM/yyyy'))" +
            " and c.ativo = :ativo  " +
            " and (lower(c.codigoDoCargo) like :parte or lower(c.descricao) like :parte)  " +
            " and (exists(select 1 " +
            "     from UNIDADECARGO uni " +
            "       inner join hierarquiaorganizacional ho on ho.SUBORDINADA_ID = uni.UNIDADEORGANIZACIONAL_ID " +
            "       where uni.CARGO_ID = c.id " +
            "           and ho.tipohierarquiaorganizacional = :administrativa " +
            "           and to_date(:data,'dd/MM/yyyy') between trunc(ho.INICIOVIGENCIA) and coalesce(trunc(ho.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) " +
            "           and ho.INDICEDONO = 1 and substr(ho.codigo,1,3) in (select distinct substr(hierarquia.codigo,1,3) " +
            "                                 from hierarquiaorganizacional hierarquia " +
            "                                          inner join usuariounidadeorganizacio uu " +
            "                                                     on uu.unidadeorganizacional_id = hierarquia.subordinada_id " +
            "                                          inner join usuariosistema u on u.id = uu.usuariosistema_id " +
            "                                 where u.id = :usuario " +
            "                                   and hierarquia.tipohierarquiaorganizacional = :administrativa " +
            "                                   and hierarquia.NIVELNAENTIDADE = 2 " +
            "                                   and to_date(:data,'dd/MM/yyyy') between trunc(hierarquia.INICIOVIGENCIA) and coalesce(trunc(hierarquia.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')))) or " +
            " exists(select 1 " +
            "       from UNIDADECARGO uni " +
            "       where uni.CARGO_ID = c.id " +
            "         and uni.UNIDADEORGANIZACIONAL_ID in (select distinct ho.SUBORDINADA_ID " +
            "                                              from hierarquiaorganizacional ho " +
            "                                                       inner join usuariounidadeorganizacio uu " +
            "                                                                  on uu.unidadeorganizacional_id = ho.subordinada_id " +
            "                                                       inner join usuariosistema u on u.id = uu.usuariosistema_id " +
            "                                              where u.id = :usuario " +
            "                                                and ho.tipohierarquiaorganizacional = :administrativa " +
            "                                                and ho.NIVELNAENTIDADE = 2 " +
            "                                                and to_date(:data,'dd/MM/yyyy') between trunc(ho.INICIOVIGENCIA) and coalesce(trunc(ho.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy'))))) " +
            " order by c.descricao, c.codigoDoCargo";
        Query q = em.createNativeQuery(sql, Cargo.class);
        q.setParameter("parametroTipoPCS", tipoPCS.name());
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("data", DataUtil.getDataFormatada(UtilRH.getDataOperacao()));
        q.setParameter("administrativa", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("usuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("ativo", 1);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Cargo> listaFiltrando(TipoPCS tipoPCS, String s) {
        Query q = em.createQuery(" from Cargo c where c.tipoPCS = :parametroTipoPCS and (lower(c.descricao) like :filtro or lower(c.codigoDoCargo) like :filtro) order by c.descricao,c.codigoDoCargo");
        q.setParameter("parametroTipoPCS", tipoPCS);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<Cargo> listaCargosVigentes(String s) {
        Query q = em.createQuery(" from Cargo c where (lower(c.descricao) like :filtro or lower(c.codigoDoCargo) like :filtro)"
            + " and :dataVigencia >= c.inicioVigencia "
            + " and :dataVigencia <= coalesce(c.finalVigencia,:dataVigencia) "
            + " order by c.descricao,c.codigoDoCargo");

        q.setParameter("dataVigencia", new Date());
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<Cargo> buscarCargosVigentesPorUnidadeOrganizacionalAndUsuario(String s) {
        String sql = "select c.* " +
            " from CARGO c " +
            " where (lower(c.descricao) like :filtro or lower(c.codigoDoCargo) like :filtro) " +
            "  and to_date(:dataVigencia,'dd/MM/yyyy') between trunc(c.inicioVigencia) and coalesce(trunc(c.finalVigencia), to_date(:dataVigencia,'dd/MM/yyyy')) " +
            "  and (exists(select 1 " +
            "     from UNIDADECARGO uni " +
            "              inner join hierarquiaorganizacional ho on ho.SUBORDINADA_ID = uni.UNIDADEORGANIZACIONAL_ID " +
            "     where uni.CARGO_ID = c.id " +
            "       and ho.tipohierarquiaorganizacional = :administrativa " +
            "       and to_date(:dataVigencia,'dd/MM/yyyy') between trunc(ho.INICIOVIGENCIA) and coalesce(trunc(ho.FIMVIGENCIA), to_date(:dataVigencia,'dd/MM/yyyy')) " +
            "       and ho.INDICEDONO = 1 and substr(ho.codigo,1,3) in (select distinct substr(hierarquia.codigo,1,3) " +
            "                                 from hierarquiaorganizacional hierarquia " +
            "                                          inner join usuariounidadeorganizacio uu " +
            "                                                     on uu.unidadeorganizacional_id = hierarquia.subordinada_id " +
            "                                          inner join usuariosistema u on u.id = uu.usuariosistema_id " +
            "                                 where u.id = :usuario " +
            "                                   and hierarquia.tipohierarquiaorganizacional = :administrativa " +
            "                                   and hierarquia.NIVELNAENTIDADE = 2 " +
            "                                   and to_date(:dataVigencia,'dd/MM/yyyy') between trunc(hierarquia.INICIOVIGENCIA) and coalesce(trunc(hierarquia.FIMVIGENCIA), to_date(:dataVigencia,'dd/MM/yyyy')))) or " +
            " exists(select 1 " +
            "       from UNIDADECARGO uni " +
            "       where uni.CARGO_ID = c.id " +
            "         and uni.UNIDADEORGANIZACIONAL_ID in (select distinct ho.SUBORDINADA_ID " +
            "                                              from hierarquiaorganizacional ho " +
            "                                                       inner join usuariounidadeorganizacio uu " +
            "                                                                  on uu.unidadeorganizacional_id = ho.subordinada_id " +
            "                                                       inner join usuariosistema u on u.id = uu.usuariosistema_id " +
            "                                              where u.id = :usuario " +
            "                                                and ho.tipohierarquiaorganizacional = :administrativa " +
            "                                                and ho.NIVELNAENTIDADE = 2 " +
            "                                                and to_date(:dataVigencia,'dd/MM/yyyy') between trunc(ho.INICIOVIGENCIA) and coalesce(trunc(ho.FIMVIGENCIA), to_date(:dataVigencia,'dd/MM/yyyy')))))";
        Query q = em.createNativeQuery(sql, Cargo.class);
        q.setParameter("dataVigencia", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("administrativa", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("usuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<Cargo> buscarCargosVigentesPorUnidadesUsuarioAndUnidadeOrganizacional(String s, Long unidade, TipoPCS tipoPCS) {
        if (unidade == null) {
            return Lists.newArrayList();
        }

        String sql = "select c.* " +
            " from CARGO c " +
            " where (lower(c.descricao) like :filtro or lower(c.codigoDoCargo) like :filtro) " +
            "  and to_date(:dataVigencia,'dd/MM/yyyy') between trunc(c.inicioVigencia) and coalesce(trunc(c.finalVigencia), to_date(:dataVigencia,'dd/MM/yyyy')) " +
            " and (exists(select 1 " +
            "               from UNIDADECARGO uni " +
            "               where uni.CARGO_ID = c.id " +
            "                 and uni.UNIDADEORGANIZACIONAL_ID = :unidade) or " +
            "       exists(select 1 " +
            "              from UNIDADECARGO uni " +
            "                       inner join hierarquiaorganizacional ho on ho.SUBORDINADA_ID = uni.UNIDADEORGANIZACIONAL_ID " +
            "              where uni.CARGO_ID = c.id " +
            "                and ho.tipohierarquiaorganizacional = :administrativa " +
            "                and to_date(:dataVigencia,'dd/MM/yyyy') between trunc(ho.INICIOVIGENCIA) and coalesce(trunc(ho.FIMVIGENCIA), to_date(:dataVigencia,'dd/MM/yyyy')) " +
            "                and ho.INDICEDONO = 1 and substr(ho.codigo,1,3) in (select distinct substr(hierarquia.codigo,1,3) " +
            "                                                                    from hierarquiaorganizacional hierarquia " +
            "                                                                    where hierarquia.tipohierarquiaorganizacional = :administrativa " +
            "                                                                      and hierarquia.SUBORDINADA_ID = :unidade " +
            "                                                                      and to_date(:dataVigencia,'dd/MM/yyyy') between trunc(hierarquia.INICIOVIGENCIA) and coalesce(trunc(hierarquia.FIMVIGENCIA), to_date(:dataVigencia,'dd/MM/yyyy'))))) " +
            "  and (exists(select 1 " +
            "     from UNIDADECARGO uni " +
            "              inner join hierarquiaorganizacional ho on ho.SUBORDINADA_ID = uni.UNIDADEORGANIZACIONAL_ID " +
            "     where uni.CARGO_ID = c.id " +
            "       and ho.tipohierarquiaorganizacional = :administrativa " +
            "       and to_date(:dataVigencia,'dd/MM/yyyy') between trunc(ho.INICIOVIGENCIA) and coalesce(trunc(ho.FIMVIGENCIA), to_date(:dataVigencia,'dd/MM/yyyy')) " +
            "       and ho.INDICEDONO = 1 and substr(ho.codigo,1,3) in (select distinct substr(hierarquia.codigo,1,3) " +
            "                                 from hierarquiaorganizacional hierarquia " +
            "                                          inner join usuariounidadeorganizacio uu " +
            "                                                     on uu.unidadeorganizacional_id = hierarquia.subordinada_id " +
            "                                          inner join usuariosistema u on u.id = uu.usuariosistema_id " +
            "                                 where u.id = :usuario " +
            "                                   and hierarquia.tipohierarquiaorganizacional = :administrativa " +
            "                                   and hierarquia.NIVELNAENTIDADE = 2 " +
            "                                   and to_date(:dataVigencia,'dd/MM/yyyy') between trunc(hierarquia.INICIOVIGENCIA) and coalesce(trunc(hierarquia.FIMVIGENCIA), to_date(:dataVigencia,'dd/MM/yyyy')))) or " +
            " exists(select 1 " +
            "       from UNIDADECARGO uni " +
            "       where uni.CARGO_ID = c.id " +
            "         and uni.UNIDADEORGANIZACIONAL_ID in (select distinct ho.SUBORDINADA_ID " +
            "                                              from hierarquiaorganizacional ho " +
            "                                                       inner join usuariounidadeorganizacio uu " +
            "                                                                  on uu.unidadeorganizacional_id = ho.subordinada_id " +
            "                                                       inner join usuariosistema u on u.id = uu.usuariosistema_id " +
            "                                              where u.id = :usuario " +
            "                                                and ho.tipohierarquiaorganizacional = :administrativa " +
            "                                                and ho.NIVELNAENTIDADE = 2 " +
            "                                                and to_date(:dataVigencia,'dd/MM/yyyy') between trunc(ho.INICIOVIGENCIA) and coalesce(trunc(ho.FIMVIGENCIA), to_date(:dataVigencia,'dd/MM/yyyy')))))";
        if (tipoPCS != null) {
            sql += " and c.TIPOPCS = :tipoPCS ";
        }
        Query q = em.createNativeQuery(sql, Cargo.class);
        q.setParameter("dataVigencia", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("administrativa", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("usuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("unidade", unidade);
        if (tipoPCS != null) {
            q.setParameter("tipoPCS", tipoPCS.name());
        }
        q.setMaxResults(50);
        return q.getResultList();
    }

    public boolean cargoContemUnidadeOrganizacional(Long cargo, Long unidade) {
        String sql = "select hierarquia.* " +
            "from hierarquiaorganizacional hierarquia " +
            "where hierarquia.SUBORDINADA_ID = :unidade " +
            "  and to_date(:dataVigencia,'dd/MM/yyyy') between trunc(hierarquia.INICIOVIGENCIA) and coalesce(trunc(hierarquia.FIMVIGENCIA), to_date(:dataVigencia,'dd/MM/yyyy')) " +
            "  and (substr(hierarquia.codigo, 1, 3) in (select substr(ho.codigo, 1, 3) " +
            "                                           from UNIDADECARGO uni " +
            "                                                    inner join hierarquiaorganizacional ho " +
            "                                                               on ho.SUBORDINADA_ID = uni.UNIDADEORGANIZACIONAL_ID " +
            "                                           where uni.CARGO_ID = :cargo " +
            "                                             and ho.tipohierarquiaorganizacional = :administrativa " +
            "                                             and to_date(:dataVigencia,'dd/MM/yyyy') between trunc(ho.INICIOVIGENCIA) and coalesce(trunc(ho.FIMVIGENCIA), to_date(:dataVigencia,'dd/MM/yyyy')) " +
            "                                             and ho.INDICEDONO = 1) or " +
            "       substr(hierarquia.codigo, 1, 5) in (select substr(ho.codigo, 1, 5) " +
            "                                           from UNIDADECARGO uni " +
            "                                                    inner join hierarquiaorganizacional ho " +
            "                                                               on ho.SUBORDINADA_ID = uni.UNIDADEORGANIZACIONAL_ID " +
            "                                           where uni.CARGO_ID = :cargo " +
            "                                             and ho.tipohierarquiaorganizacional = :administrativa " +
            "                                             and to_date(:dataVigencia,'dd/MM/yyyy') between trunc(ho.INICIOVIGENCIA) and coalesce(trunc(ho.FIMVIGENCIA), to_date(:dataVigencia,'dd/MM/yyyy')) " +
            "                                             and ho.INDICEDONO = 2)) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("cargo", cargo);
        q.setParameter("unidade", unidade);
        q.setParameter("administrativa", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("dataVigencia", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        return !q.getResultList().isEmpty();
    }

    public List<Cargo> filtraCargosVigentes(Date dataInicial, Date dataFinal) {
        String sql = " select distinct cargo.* " +
            "  from  cargo cargo" +
            "  where  ((to_date(:dataInicial,'dd/MM/yyyy') BETWEEN cargo.INICIOVIGENCIA and coalesce(cargo.FINALVIGENCIA,to_date(:dataFinal,'dd/MM/yyyy'))) " +
            " or (to_date(:dataFinal,'dd/MM/yyyy') BETWEEN cargo.INICIOVIGENCIA and coalesce(cargo.FINALVIGENCIA,to_date(:dataFinal,'dd/MM/yyyy')))) " +
            "  order by cargo.descricao ";
        Query q = em.createNativeQuery(sql, Cargo.class);
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        q.setParameter("dataInicial", formato.format(dataInicial));
        q.setParameter("dataFinal", formato.format(dataFinal));
        return q.getResultList();
    }

    public List<Cargo> filtraCargosVigentes(Date data) {
        return filtraCargosVigentes(data, data);
    }

    public List<Cargo> filtraCargosVigentesPorUnidadeOrganizacionalAndUsuario(Date data) {
        return filtraCargosVigentesPorUnidadeOrganizacionalAndUsuario(data, data);
    }

    public List<Cargo> filtraCargosVigentesPorUnidadeOrganizacionalAndUsuario(Date dataInicial, Date dataFinal) {
        String sql = " select distinct cargo.* " +
            "  from  cargo cargo" +
            "  where  ((to_date(:dataInicial,'dd/MM/yyyy') BETWEEN cargo.INICIOVIGENCIA and coalesce(cargo.FINALVIGENCIA,to_date(:dataFinal,'dd/MM/yyyy'))) " +
            " or (to_date(:dataFinal,'dd/MM/yyyy') BETWEEN cargo.INICIOVIGENCIA and coalesce(cargo.FINALVIGENCIA,to_date(:dataFinal,'dd/MM/yyyy')))) " +
            "  and (exists(select 1 " +
            "     from UNIDADECARGO uni " +
            "              inner join hierarquiaorganizacional ho on ho.SUBORDINADA_ID = uni.UNIDADEORGANIZACIONAL_ID " +
            "     where uni.CARGO_ID = cargo.id " +
            "       and ho.tipohierarquiaorganizacional = :administrativa " +
            "       and to_date(:dataAtual,'dd/MM/yyyy') between trunc(ho.INICIOVIGENCIA) and coalesce(trunc(ho.FIMVIGENCIA), to_date(:dataAtual,'dd/MM/yyyy')) " +
            "       and ho.INDICEDONO = 1 and substr(ho.codigo,1,3) in (select distinct substr(hierarquia.codigo,1,3) " +
            "                                 from hierarquiaorganizacional hierarquia " +
            "                                          inner join usuariounidadeorganizacio uu " +
            "                                                     on uu.unidadeorganizacional_id = hierarquia.subordinada_id " +
            "                                          inner join usuariosistema u on u.id = uu.usuariosistema_id " +
            "                                 where u.id = :usuario " +
            "                                   and hierarquia.tipohierarquiaorganizacional = :administrativa " +
            "                                   and hierarquia.NIVELNAENTIDADE = 2 " +
            "                                   and to_date(:dataAtual,'dd/MM/yyyy') between trunc(hierarquia.INICIOVIGENCIA) and coalesce(trunc(hierarquia.FIMVIGENCIA), to_date(:dataAtual,'dd/MM/yyyy')))) or " +
            " exists(select 1 " +
            "       from UNIDADECARGO uni " +
            "       where uni.CARGO_ID = cargo.id " +
            "         and uni.UNIDADEORGANIZACIONAL_ID in (select distinct ho.SUBORDINADA_ID " +
            "                                              from hierarquiaorganizacional ho " +
            "                                                       inner join usuariounidadeorganizacio uu " +
            "                                                                  on uu.unidadeorganizacional_id = ho.subordinada_id " +
            "                                                       inner join usuariosistema u on u.id = uu.usuariosistema_id " +
            "                                              where u.id = :usuario " +
            "                                                and ho.tipohierarquiaorganizacional = :administrativa " +
            "                                                and ho.NIVELNAENTIDADE = 2 " +
            "                                                and to_date(:dataAtual,'dd/MM/yyyy') between trunc(ho.INICIOVIGENCIA) and coalesce(trunc(ho.FIMVIGENCIA), to_date(:dataAtual,'dd/MM/yyyy'))))) " +
            "  order by cargo.descricao ";
        Query q = em.createNativeQuery(sql, Cargo.class);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        q.setParameter("administrativa", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("usuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("dataAtual", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        return q.getResultList();
    }

    public List<Cargo> listaFiltrandoModalidade(ModalidadeContratoFP modalidade, Date dataInicial, Date dataFinal) {
        String sql = " select distinct cargo.* " +
            "  from contratofp contrato" +
            "  join modalidadecontratofp modalidade on modalidade.id = contrato.modalidadecontratofp_id " +
            "  join cargo cargo on cargo.id = contrato.cargo_id " +
            "  where  ((to_date(:dataInicial,'dd/MM/yyyy') BETWEEN cargo.INICIOVIGENCIA and coalesce(cargo.FINALVIGENCIA,to_date(:dataFinal,'dd/MM/yyyy'))) " +
            " or (to_date(:dataFinal,'dd/MM/yyyy') BETWEEN cargo.INICIOVIGENCIA and coalesce(cargo.FINALVIGENCIA,to_date(:dataFinal,'dd/MM/yyyy')))) " +
            "  and modalidade.id = :param " +
            "  and (exists(select 1 " +
            "     from UNIDADECARGO uni " +
            "              inner join hierarquiaorganizacional ho on ho.SUBORDINADA_ID = uni.UNIDADEORGANIZACIONAL_ID " +
            "     where uni.CARGO_ID = cargo.id " +
            "       and ho.tipohierarquiaorganizacional = :administrativa " +
            "       and to_date(:dataAtual,'dd/MM/yyyy') between trunc(ho.INICIOVIGENCIA) and coalesce(trunc(ho.FIMVIGENCIA), to_date(:dataAtual,'dd/MM/yyyy')) " +
            "       and ho.INDICEDONO = 1 and substr(ho.codigo,1,3) in (select distinct substr(hierarquia.codigo,1,3) " +
            "                                 from hierarquiaorganizacional hierarquia " +
            "                                          inner join usuariounidadeorganizacio uu " +
            "                                                     on uu.unidadeorganizacional_id = hierarquia.subordinada_id " +
            "                                          inner join usuariosistema u on u.id = uu.usuariosistema_id " +
            "                                 where u.id = :usuario " +
            "                                   and hierarquia.tipohierarquiaorganizacional = :administrativa " +
            "                                   and hierarquia.NIVELNAENTIDADE = 2 " +
            "                                   and to_date(:dataAtual,'dd/MM/yyyy') between trunc(hierarquia.INICIOVIGENCIA) and coalesce(trunc(hierarquia.FIMVIGENCIA), to_date(:dataAtual,'dd/MM/yyyy')))) or " +
            " exists(select 1 " +
            "       from UNIDADECARGO uni " +
            "       where uni.CARGO_ID = cargo.id " +
            "         and uni.UNIDADEORGANIZACIONAL_ID in (select distinct ho.SUBORDINADA_ID " +
            "                                              from hierarquiaorganizacional ho " +
            "                                                       inner join usuariounidadeorganizacio uu " +
            "                                                                  on uu.unidadeorganizacional_id = ho.subordinada_id " +
            "                                                       inner join usuariosistema u on u.id = uu.usuariosistema_id " +
            "                                              where u.id = :usuario " +
            "                                                and ho.tipohierarquiaorganizacional = :administrativa " +
            "                                                and ho.NIVELNAENTIDADE = 2 " +
            "                                                and to_date(:dataAtual,'dd/MM/yyyy') between trunc(ho.INICIOVIGENCIA) and coalesce(trunc(ho.FIMVIGENCIA), to_date(:dataAtual,'dd/MM/yyyy'))))) " +
            "  order by cargo.descricao ";
        Query q = em.createNativeQuery(sql, Cargo.class);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        q.setParameter("param", modalidade.getId());
        q.setParameter("administrativa", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("usuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("dataAtual", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        return q.getResultList();
    }

    public List<Cargo> listaFiltrandoModalidade(ModalidadeContratoFP modalidade, Date data) {
        return listaFiltrandoModalidade(modalidade, data, data);
    }

    public List<Cargo> listaFiltrando(TipoPCS tipoPCS, String s, String... atributos) {
        String hql = " from Cargo cargo where ";
        for (String atributo : atributos) {
            hql += "lower(cargo." + atributo + ") like :filtro OR ";
        }
        hql = hql.substring(0, hql.length() - 3);
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public Cargo recuperaCargoDoProcuradorVigente(PessoaFisica procurador) {
        String sql = "select cargo from ContratoFP v "
            + "inner join v.matriculaFP as m  "
            + "inner join m.pessoa as p "
            + "inner join v.cargo cargo "
            + "where p = :procurador  "
            + " and :dataVigencia >= cargo.inicioVigencia and "
            + " :dataVigencia <= coalesce(cargo.finalVigencia,:dataVigencia) ";
        Query q = em.createQuery(sql);
        q.setMaxResults(1);
        q.setParameter("procurador", procurador);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));

        if (!q.getResultList().isEmpty()) {
            return (Cargo) q.getSingleResult();
        }

        return null;
    }

    public List<Cargo> completaCargo(String filtro) {
        String hql = "from Cargo where (lower(trim(descricao)) like :filtro or codigoDoCargo like :filtro) and ativo = :ativo order by descricao,  to_number(codigoDoCargo)";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q.setParameter("ativo", true);
        return q.getResultList();
    }

    @Override
    public void salvar(Cargo entity) {
        super.salvar(entity);
        em.flush();
    }


    @Override
    public void salvarNovo(Cargo entity) {
        if (entity.getCbos() != null) {
            for (CBO cbo : entity.getCbos()) {
                cbo = em.merge(cbo);
            }
        }
        super.salvarNovo(entity);
    }

    public AreaFormacaoFacade getAreaFormacaoFacade() {
        return areaFormacaoFacade;
    }

    public HabilidadeFacade getHabilidadeFacade() {
        return habilidadeFacade;
    }

    public BigDecimal getRemuneracaoInicialDoCargo(Cargo c) {
        String sql = "     select enq.vencimentobase                                                                                       " +
            "       from CargoCategoriaPCS cargocategoria                                                                         " +
            " inner join cargo             cargo          on cargocategoria.cargo_id = cargo.id                                   " +
            " inner join categoriapcs      cat            on cat.id                  = cargocategoria.categoriapcs_id             " +
            " inner join enquadramentopcs  enq            on enq.categoriapcs_id     = cat.id                                     " +
            " inner join progressaopcs     prog           on prog.id                 = enq.progressaopcs_id                       " +
            "      where cargo.id = :cargo_id                                                                                     " +
            "        and :data_atual between cargocategoria.iniciovigencia and coalesce(cargocategoria.finalvigencia, :data_atual)" +
            "        and :data_atual between            enq.iniciovigencia and coalesce(enq.finalvigencia           , :data_atual)" +
            "   order by prog.descricao,prog.ordem                                                                                ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("cargo_id", c.getId());
        q.setParameter("data_atual", UtilRH.getDataOperacao());
        q.setMaxResults(1);
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    public boolean isServidorJaVinculadoAoCargo(ContratoFP contratoFP, Cargo cargo) {
        String sql = "select c.id from contratofp c where c.id = :contratofp_id and c.cargo_id = :cargo_id";
        Query q = em.createNativeQuery(sql);
        q.setMaxResults(1);
        q.setParameter("contratofp_id", contratoFP.getId());
        q.setParameter("cargo_id", cargo.getId());
        try {
            return q.getSingleResult() != null;
        } catch (NoResultException nre) {
            return false;
        }
    }

    public Cargo recuperarComHabilidades(Long id) {
        Cargo cargo = super.recuperar(id);
        if (!CollectionUtils.isEmpty(cargo.getHabilidades())) {
            cargo.getHabilidades().size();
        }
        return cargo;
    }

    public Cargo recuperarComAreasFormacao(Long id) {
        Cargo cargo = super.recuperar(id);
        if (!CollectionUtils.isEmpty(cargo.getAreasFormacao())) {
            cargo.getAreasFormacao().size();
        }
        return cargo;
    }

    public List<Cargo> recuperarCargoPorCapacitacao(String filtro, Capacitacao capacitacao) {
        String sql = " SELECT DISTINCT CARGO.* FROM CARGO "
            + " INNER JOIN CONTRATOFP CONTRATO ON CONTRATO.CARGO_ID = CARGO.ID "
            + " INNER JOIN VINCULOFP VINCULO ON VINCULO.ID = CONTRATO.ID "
            + " INNER JOIN INSCRICAOCAPACITACAO INSC ON INSC.MATRICULAFP_ID = VINCULO.MATRICULAFP_ID "
            + " INNER JOIN CAPACITACAO CAPACITACAO ON CAPACITACAO.ID = INSC.CAPACITACAO_ID"
            + " WHERE (lower(cargo.descricao) like :filtro "
            + "         or lower(cargo.codigodocargo) like :filtro) ";
        if (capacitacao != null) {
            sql += " and capacitacao.id = :capacitacao ";
        }
        Query q = em.createNativeQuery(sql, Cargo.class);
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        if (capacitacao != null) {
            q.setParameter("capacitacao", capacitacao.getId());
        }
        return q.getResultList();
    }

    public List<Cargo> buscarCargosParaESocial(ConfiguracaoEmpregadorESocial empregador, TipoPCS... tipos) {
        String sql = " SELECT distinct cargo.* " +
            " FROM cargo cargo INNER JOIN CARGOEMPREGADORESOCIAL cargoEmpregador ON cargo.ID = cargoEmpregador.CARGO_ID " +
            " where cargoEmpregador.EMPREGADOR_ID = :empregador " +
            " and cargo.tipoPcs in(:tipos) ";
        Query q = em.createNativeQuery(sql, Cargo.class);
        q.setParameter("empregador", empregador.getId());
        q.setParameter("tipos", Util.getListOfEnumName(tipos));
        return q.getResultList();
    }

    public Boolean cboVinculadoCargoContratoVigente(CBO cbo, Date dataOperacao) {
        String sql = " select contr.* " +
            " from contratofp contr " +
            "         inner join contratofpcargo cargCont on contr.id = cargCont.contratofp_id " +
            "         inner join vinculofp vinc on contr.id = vinc.id " +
            " where :dataOperacao between vinc.iniciovigencia and coalesce(vinc.finalvigencia, :dataOperacao) " +
            "  and :dataOperacao between cargCont.iniciovigencia and coalesce(cargCont.fimvigencia, :dataOperacao) " +
            "  and cargCont.cbo_id = :idCbo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCbo", cbo.getId());
        q.setParameter("dataOperacao", dataOperacao);
        List resultList = q.getResultList();
        return resultList != null && !resultList.isEmpty();
    }


    public void enviarCargoParaESocial(Cargo cargo) throws ValidacaoException {
        cargo = recuperar(cargo.getId());
        for (CargoEmpregadorESocial cargoEmpregadorESocial : cargo.getEmpregadores()) {
            s1030Service.enviarS1030(cargoEmpregadorESocial.getEmpregador(), cargo);
        }
    }

    public List<ClassificacaoCargo> buscarClassificacoesCargo(String filtro) {
        return classificacaoCargoFacade.listaFiltrando(filtro.trim(), "codigo", "descricao");
    }

    public void enviarCargoESocial(ConfiguracaoEmpregadorESocial empregador, Cargo cargo) throws ValidacaoException {
        s1030Service.enviarS1030(empregador, cargo);
    }

    public void enviarCarreiraPublicaESocial(ConfiguracaoEmpregadorESocial empregador, Cargo cargo) throws ValidacaoException {
        s1035Service.enviarS1035(empregador, cargo);
    }

    public void enviarS1040ESocial(ConfiguracaoEmpregadorESocial empregador, Cargo cargo) throws ValidacaoException {
        s1040Service.enviarS1040(empregador, cargo);
    }

    public void salvarCargoInativo(Cargo cargo) {
        em.merge(cargo);
    }
}
