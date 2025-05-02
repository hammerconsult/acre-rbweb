package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.entidades.BemPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CaracterizadorDeBem;
import br.com.webpublico.interfaces.CaracterizadorDeBemImovel;
import br.com.webpublico.interfaces.CaracterizadorDeBemMovel;
import br.com.webpublico.negocios.administrativo.pesquisabem.PesquisaBem;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.CodigoGeradoAoConcluir;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.persistence.*;
import javax.persistence.metamodel.EntityType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 02/01/14
 * Time: 14:13
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class BemFacade extends AbstractFacade<Bem> {

    private static final BigDecimal CEM = new BigDecimal("100");

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private GrupoObjetoCompraGrupoBemFacade grupoObjetoCompraGrupoBemFacade;
    @EJB
    private GrupoObjetoCompraFacade grupoObjetoCompraFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private RecuperadorFacade recuperadorFacade;
    @EJB
    private InventarioBensMoveisFacade inventarioBensMoveisFacade;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    @EJB
    private EfetivacaoAquisicaoFacade efetivacaoAquisicaoFacade;

    public BemFacade() {
        super(Bem.class);
    }

    @Override
    public Bem recuperar(Object id) {
        Bem bem = super.recuperar(id);
        recuperarDependenciasEstadoBem(bem);
        bem.getEventosBem().size();
        recuperarBemComDependenciasNotasFicaisAndOrigemRecurso(id);
        if (bem.getEventosBem() != null && !bem.getEventosBem().isEmpty()) {
            for (EventoBem eventoBem : bem.getEventosBem()) {
                if (eventoBem.getDetentorArquivoComposicao() != null) {
                    eventoBem.getDetentorArquivoComposicao().getArquivosComposicao().size();
                }
            }
        }
        return bem;
    }

    //Utilizado autocomplete, não recuperar outras dependências aqui
    public Bem recuperarSemDependencias(Object id) {
        return super.recuperar(id);
    }


    public Bem recuperarBemDoEventoBem(EventoBem eventoBem) {
        Bem bem = eventoBem.getBem();
        bem.setValorOriginal(eventoBem.getEstadoInicial().getValorOriginal());
        bem.setValorAcumuladoDeAjuste(eventoBem.getEstadoInicial().getValorAcumuladoDeAjuste());
        bem.setValorAcumuladoDaAmortizacao(eventoBem.getEstadoInicial().getValorAcumuladoDaAmortizacao());
        bem.setValorAcumuladoDaDepreciacao(eventoBem.getEstadoInicial().getValorAcumuladoDaDepreciacao());
        bem.setValorAcumuladoDaExaustao(eventoBem.getEstadoInicial().getValorAcumuladoDaExaustao());
        return bem;
    }

    public Bem recuperarHistoricoBem(Object id) {
        Bem bem = super.recuperar(id);
        recuperarDependenciasEstadoBem(bem);
        bem.getEventosBem().size();
        return bem;
    }

    public Bem recuperarComDependenciasEventoBem(Object id) {
        Bem bem = super.recuperar(id);
        bem.getEventosBem().size();
        return bem;
    }

    public Bem recuperarBemComDependenciasNotasFicaisAndOrigemRecurso(Object id) {
        Bem bem = super.recuperar(id);
        recuperarDependenciasNotaFiscal(bem);
        recuperarDepemdenciasOrigemRecurso(bem);
        return bem;
    }

    private void recuperarDependenciasNotaFiscal(Bem bem) {
        if (bem.getNotasFiscais() != null) {
            for (BemNotaFiscal bemNotaFiscal : bem.getNotasFiscais()) {
                if (bemNotaFiscal.getEmpenhos() != null) {
                    bemNotaFiscal.getEmpenhos().size();
                }
            }
        }
    }

    private void recuperarDependenciasEstadoBem(Bem bem) {
        EstadoBem estadoBem = recuperarUltimoEstadoDoBem(bem);
        if (estadoBem != null) {
            bem.setValorOriginal(estadoBem.getValorOriginal());
            bem.setValorAcumuladoDaAmortizacao(estadoBem.getValorAcumuladoDaAmortizacao());
            bem.setValorAcumuladoDaDepreciacao(estadoBem.getValorAcumuladoDaDepreciacao());
            bem.setValorAcumuladoDaExaustao(estadoBem.getValorAcumuladoDaExaustao());
            bem.setValorAcumuladoDeAjuste(estadoBem.getValorAcumuladoDeAjuste());
        }
    }

    private void recuperarDepemdenciasOrigemRecurso(Bem bem) {
        if (bem.getDetentorOrigemRecurso() != null) {
            bem.getDetentorOrigemRecurso().getListaDeOriemRecursoBem().size();
        }
    }

    public static String situacoesParaMovimentacoes() {
        String situacoes = "";
        List<SituacaoEventoBem> situacaoEventoBem = Lists.newArrayList(SituacaoEventoBem.RECUSADO, SituacaoEventoBem.FINALIZADO, SituacaoEventoBem.CONCLUIDO);
        for (SituacaoEventoBem situacao : situacaoEventoBem) {
            situacoes += "'" + situacao.name() + "', ";
        }
        return situacoes.substring(0, situacoes.length() - 2);
    }

    @Override
    protected EntityManager getEntityManager() {
        Set<EntityType<?>> entities = em.getMetamodel().getEntities();

        return this.em;
    }

    public EstadoBem criarNovoEstadoResultanteAPartirDoCaracterizador(CaracterizadorDeBemMovel cdbm) {
        cdbm.setGrupoBem(grupoObjetoCompraGrupoBemFacade.recuperarAssociacaoDoGrupoObjetoCompra(cdbm.getGrupoObjetoCompra(), cdbm.getDataDaOperacao()).getGrupoBem());
        return new EstadoBem(cdbm);
    }

    public EstadoBem criarNovoEstadoResultanteAPartirDoCaracterizadorDeBemImovel(CaracterizadorDeBemImovel cdbm) {
        return new EstadoBem(cdbm);
    }

    public EstadoBem criarNovoEstadoResultanteAPartirDoEstadoInicial(EstadoBem estadoInicial) {
        return estadoInicial.clone();
    }

    public EstadoBem recuperarUltimoEstadoDoBem(Bem bem) {
        return recuperarUltimoEstadoDoBem(bem.getId());
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public EstadoBem recuperarUltimoEstadoDoBem(Long idBem) {
        String sql = " select et.* from estadobem et " +
            "          inner join eventobem eb on eb.estadoresultante_id = et.id " +
            "          where eb.bem_id = :bem " +
            "          and eb.dataoperacao = (select max(eb2.dataoperacao) from eventobem eb2 where eb2.bem_id = eb.bem_id)";
        Query q = em.createNativeQuery(sql, EstadoBem.class);
        q.setParameter("bem", idBem);
        q.setMaxResults(1);
        try {
            return (EstadoBem) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List buscarBensPorTipoBem(String parte, TipoBem tipoBem) {
        StringBuilder sql = new StringBuilder();
        sql.append("  select bem.* from bem ")
            .append("   inner join eventobem ev on ev.bem_id = bem.id ")
            .append("   inner join estadobem est on est.id = ev.estadoresultante_id ")
            .append("   inner join grupobem gb on gb.id = est.grupobem_id ")
            .append(" where ev.dataoperacao = (select max(ev2.dataoperacao) from eventobem ev2 where ev2.bem_id = bem.id) ")
            .append("   and gb.tipobem = :tipoBem ")
            .append("   and est.estadobem <> :diferenteBaixado ")
            .append("   and (bem.identificacao like :parte or lower(bem.descricao) like :parte ) ")
            .append(" order by to_number(bem.identificacao) ");
        Query q = em.createNativeQuery(sql.toString(), Bem.class);
        q.setParameter("diferenteBaixado", EstadoConservacaoBem.BAIXADO.name());
        q.setParameter("tipoBem", tipoBem.name());
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public BigDecimal getValorOriginalUltimoEventoBem(Long idBem) {
        String sql = "select est.valororiginal from bem  " +
            "     inner join eventobem ev on ev.bem_id = bem.id  " +
            "     inner join estadobem est on est.id = ev.estadoresultante_id  " +
            "     inner join grupobem gb on gb.id = est.grupobem_id  " +
            "   where ev.dataoperacao = (select max(ev2.dataoperacao) from eventobem ev2 where ev2.bem_id = bem.id)  " +
            "     and bem.id = :idBem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idBem", idBem);
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal valorDepreciadoEfetivacaoBem(EfetivacaoLevantamentoBem ef) {

        String sql = "select" +
            "              est.VALORACUMULADODADEPRECIACAO " +
            "           from EFETIVACAODEPRECIACAOBEM depreciacao " +
            "         inner join eventobem ev on ev.id = depreciacao.id" +
            "         inner join estadobem est on est.id = ev.estadoresultante_id " +
            "           where depreciacao.efetivacaoLevantamentoBem_id = :ef";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ef", ef.getId());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public boolean validarEstornoDaEfetivacaoDeLevantamentoMovelPorBem(Bem bem) {
        String sql = "select count(ev.id) " +
            "       from eventobem ev " +
            "     where ev.bem_id = :bem_id" +
            "           and  ev.tipoeventobem <> :efetivacaodepreciacaobem " +
            "           and  ev.tipoeventobem <> :efetivacaolevantamentobem " +
            "           and  ev.tipoeventobem <> :estornoefetivacaolevmovel ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("bem_id", bem.getId());
        q.setParameter("efetivacaodepreciacaobem", TipoEventoBem.EFETIVACAODEPRECIACAOBEM.name());
        q.setParameter("efetivacaolevantamentobem", TipoEventoBem.EFETIVACAOLEVANTAMENTOBEM.name());
        q.setParameter("estornoefetivacaolevmovel", TipoEventoBem.ESTORNOEFETIVACAOLEVMOVEL.name());
        return ((BigDecimal) q.getSingleResult()).intValue() > 0;
    }

    public boolean usuarioIsGestorUnidade(UsuarioSistema usuarioSistema, UnidadeOrganizacional unidadeDestino, Date dataOperacao) {
        String sql = "select count(1)" +
            "       from usuariosistema us" +
            " inner join usuariounidadeorganizacio uuo on uuo.usuariosistema_id = us.id" +
            " inner join vwhierarquiaadministrativa vw on vw.subordinada_id = uuo.unidadeorganizacional_id" +
            "      where us.id = :usuario_id" +
            "        and uuo.gestorpatrimonio = 1" +
            "        and :data_operacao between vw.iniciovigencia and coalesce(vw.fimvigencia, :data_operacao)" +
            "        and vw.subordinada_id = :unidade_id";
        Query q = em.createNativeQuery(sql);
        q.setParameter("usuario_id", usuarioSistema.getId());
        q.setParameter("data_operacao", dataOperacao, TemporalType.DATE);
        q.setParameter("unidade_id", unidadeDestino.getId());

        return ((BigDecimal) q.getSingleResult()).intValue() > 0;
    }


    public boolean bemMovelTemCodigoRepetido(CaracterizadorDeBem cdb) {
        ValidacaoException ve = new ValidacaoException();
        Entidade entidade = entidadeFacade.recuperarEntidadePorUnidadeOrganizacional(cdb.getUnidadeAdministrativa());

        if (parametroPatrimonioFacade.naoExisteParametro() || parametroPatrimonioFacade.recuperarEntidadeGeradoraPorEntidade(entidade, cdb.getGrupoBem().getTipoBem()) == null) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Não existe uma sequência de geração do código de identificação patrimonial definida para a entidade " + entidade.getNome() + ", nos parâmetros do patrimônio.");
            throw ve;
        }

        String sql = "    WITH ENTIDADEGERADORA_QUERY (ENTIDADEGERADORA_ID) AS " +
            "      (SELECT " +
            "        CASE  " +
            "          WHEN ESA.ID IS NOT NULL THEN ESA.ENTIDADESEQUENCIAPROPRIA_ID " +
            "          WHEN ESP.ID IS NOT NULL THEN ESP.ID " +
            "        END AS ENTIDADEGERADORA_ID " +
            "      FROM ENTIDADEGERADORA EG " +
            "      LEFT JOIN ENTIDADESEQUENCIAAGREGADA ESA ON ESA.ID = EG.ID" +
            "      LEFT JOIN ENTIDADESEQUENCIAPROPRIA ESP ON ESP.ID  = EG.ID" +
            "      WHERE EG.ENTIDADE_ID = :ID_ENTIDADE)," +
            "      ENTIDADES_DA_SEQUENCIA (ENTIDADE_ID) AS" +
            "      (SELECT EG.ENTIDADE_ID " +
            "         FROM ENTIDADEGERADORA EG" +
            "   INNER JOIN ENTIDADEGERADORA_QUERY EGQ ON EGQ.ENTIDADEGERADORA_ID = EG.ID" +
            "        UNION " +
            "       SELECT EG.ENTIDADE_ID" +
            "         FROM ENTIDADEGERADORA EG" +
            "   INNER JOIN ENTIDADESEQUENCIAAGREGADA ESA ON EG.ID = ESA.ID" +
            "   INNER JOIN ENTIDADEGERADORA_QUERY EGQ ON EGQ.ENTIDADEGERADORA_ID = ESA.ENTIDADESEQUENCIAPROPRIA_ID)" +
            "       SELECT COUNT(1) " +
            "         FROM VWHIERARQUIAADMINISTRATIVA VW" +
            "   INNER JOIN ENTIDADES_DA_SEQUENCIA EDS ON EDS.ENTIDADE_ID = VW.ENTIDADE_ID" +
            "   INNER JOIN LEVANTAMENTOBEMPATRIMONIAL LEV ON LEV.UNIDADEADMINISTRATIVA_ID = VW.SUBORDINADA_ID" +
            "        WHERE SYSDATE BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, SYSDATE)" +
            "             AND LTRIM(TRIM(UPPER(LEV.CODIGOPATRIMONIO))) = LTRIM(TRIM(:CODIGO))";

        if (cdb.getId() != null) {
            sql += " AND LEV.ID <> :id_caracterizador";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("CODIGO", cdb.getCodigoPatrimonio().toUpperCase());

        if (cdb.getId() != null) {
            q.setParameter("id_caracterizador", cdb.getId());
        }

        q.setParameter("ID_ENTIDADE", entidade.getId());

        if (((BigDecimal) q.getSingleResult()).intValue() > 0) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public Boolean bemImovelTemCodigoRepetido(CaracterizadorDeBem cdb) throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        Entidade entidade = entidadeFacade.recuperarEntidadePorUnidadeOrganizacional(cdb.getUnidadeAdministrativa());

        if (parametroPatrimonioFacade.naoExisteParametro() || parametroPatrimonioFacade.recuperarEntidadeGeradoraPorEntidade(entidade, cdb.getGrupoBem().getTipoBem()) == null) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Não existe uma sequência de geração do código de identificação patrimonial definida para a entidade " + entidade.getNome() + ", nos parâmetros do patrimônio.");
            throw ve;
        }

        String sql = "    WITH ENTIDADEGERADORA_QUERY (ENTIDADEGERADORA_ID) AS " +
            "      (SELECT " +
            "        CASE  " +
            "          WHEN ESA.ID IS NOT NULL THEN ESA.ENTIDADESEQUENCIAPROPRIA_ID " +
            "          WHEN ESP.ID IS NOT NULL THEN ESP.ID " +
            "        END AS ENTIDADEGERADORA_ID " +
            "      FROM ENTIDADEGERADORA EG " +
            "      LEFT JOIN ENTIDADESEQUENCIAAGREGADA ESA ON ESA.ID = EG.ID" +
            "      LEFT JOIN ENTIDADESEQUENCIAPROPRIA ESP ON ESP.ID  = EG.ID" +
            "      WHERE EG.ENTIDADE_ID = :ID_ENTIDADE)," +
            "      ENTIDADES_DA_SEQUENCIA (ENTIDADE_ID) AS" +
            "      (SELECT EG.ENTIDADE_ID " +
            "         FROM ENTIDADEGERADORA EG" +
            "   INNER JOIN ENTIDADEGERADORA_QUERY EGQ ON EGQ.ENTIDADEGERADORA_ID = EG.ID" +
            "        UNION " +
            "       SELECT EG.ENTIDADE_ID" +
            "         FROM ENTIDADEGERADORA EG" +
            "   INNER JOIN ENTIDADESEQUENCIAAGREGADA ESA ON EG.ID = ESA.ID" +
            "   INNER JOIN ENTIDADEGERADORA_QUERY EGQ ON EGQ.ENTIDADEGERADORA_ID = ESA.ENTIDADESEQUENCIAPROPRIA_ID)" +
            "       SELECT COUNT(1) " +
            "         FROM VWHIERARQUIAADMINISTRATIVA VW" +
            "   INNER JOIN ENTIDADES_DA_SEQUENCIA EDS ON EDS.ENTIDADE_ID = VW.ENTIDADE_ID" +
            "   INNER JOIN LEVANTAMENTOBEMIMOVEL LEV ON LEV.UNIDADEADMINISTRATIVA_ID = VW.SUBORDINADA_ID" +
            "        WHERE SYSDATE BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, SYSDATE)" +
            "             AND UPPER(LEV.CODIGOPATRIMONIO) = :CODIGO";

        if (cdb.getId() != null) {
            sql += " AND LEV.ID <> :ID_CARACTERIZADOR";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("CODIGO", cdb.getCodigoPatrimonio().toUpperCase());

        if (cdb.getId() != null) {
            q.setParameter("ID_CARACTERIZADOR", cdb.getId());
        }

        q.setParameter("ID_ENTIDADE", entidade.getId());

        if (((BigDecimal) q.getSingleResult()).intValue() > 0) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    @Deprecated //O componente PesquisaBem foi descontinuado
    public List<Bem> recuperarBensParaPesquisa(PesquisaBem pesquisa) {
        if (pesquisa.getUnidadeOrganizacional() == null) {
            throw new ExcecaoNegocioGenerica("Impossível pesquisar sem unidade administrativa");
        }
        String sql = " select vw.*" +
            "        from vwbem vw  " +
            "  inner join eventobem eb    on eb.id     = vw.ID_ULTIMO_EVENTO_BEM  " +
            "  inner join estadobem etb   on etb.id    = vw.ID_ULTIMO_ESTADO_BEM  " +
            "  inner join grupoBem  grupo on grupo.id  = etb.grupoBem_id  " +
            "  left join " + pesquisa.nomeDaTabelaSubClasseEventoBem + " tabela on tabela.id = eb.id";

        if (pesquisa.getGrupoBem() != null) {
            sql = "with pai (ID, CODIGO, DESCRICAO, SUPERIOR_ID, ATIVOINATIVO) " +
                "as (select grupopai.id, " +
                "           grupopai.codigo, " +
                "           grupopai.descricao, " +
                "           grupopai.superior_id, " +
                "           grupopai.ativoinativo " +
                "      from grupobem grupopai " +
                "     where grupopai.id = :grupo_id " +
                "     union all " +
                "    select grupofilho.id, " +
                "           grupofilho.codigo, " +
                "           grupofilho.descricao, " +
                "           grupofilho.superior_id, " +
                "           grupofilho.ativoinativo " +
                "      from grupobem grupofilho " +
                "inner join pai p on p.id = grupofilho.superior_id) " + sql + " inner join pai pai on pai.id = etb.grupobem_id";
        }


        sql += "        where (eb.situacaoeventobem in (" + situacoesParaMovimentacoes() + ")" +
            "              or (tabela.id is not null and tabela." + pesquisa.nomeDaColunaDeDiferenciacao + " = " + pesquisa.valorColunaDeDiferenciacao + "))" +
            "         and etb.detentoraAdministrativa_id = :unidade_id";

        if (pesquisa.getBem() != null) {
            sql += " and vw.BEM_ID  = :bem_id";
        }

        if (pesquisa.getTipoGrupo() != null) {
            sql += " and etb.tipoGrupo = :tipoGrupo";
        }

        if (pesquisa.getEstadoConservacaoBem() != null) {
            sql += " and etb.estadoBem = :estadoBem";
        }

        if (pesquisa.getSituacaoConservacaoBem() != null) {
            sql += " and etb.situacaoConservacaoBem = :situacaoConservacao";
        }

        if (pesquisa.getTipoBem() != null) {
            sql += " and grupo.tipoBem = :tipoBem";
        }

        if (pesquisa.getHierarquiaOrganizacionalOrcamentaria() != null) {
            sql += " and etb.detentoraOrcamentaria_id = :orcamentaria";
        }

        sql += " order by 2";

        Query q = em.createNativeQuery(sql);
        q.setParameter("unidade_id", pesquisa.getUnidadeOrganizacional().getId());

        if (pesquisa.getTipoGrupo() != null) {
            q.setParameter("tipoGrupo", pesquisa.getTipoGrupo().name());
        }

        if (pesquisa.getGrupoBem() != null) {
            q.setParameter("grupo_id", pesquisa.getGrupoBem().getId());
        }

        if (pesquisa.getBem() != null) {
            q.setParameter("bem_id", pesquisa.getBem().getId());
        }

        if (pesquisa.getEstadoConservacaoBem() != null) {
            q.setParameter("estadoBem", pesquisa.getEstadoConservacaoBem().name());
        }

        if (pesquisa.getSituacaoConservacaoBem() != null) {
            q.setParameter("situacaoConservacao", pesquisa.getSituacaoConservacaoBem().name());
        }

        if (pesquisa.getTipoBem() != null) {
            q.setParameter("tipoBem", pesquisa.getTipoBem().name());
        }

        if (pesquisa.getHierarquiaOrganizacionalOrcamentaria() != null) {
            q.setParameter("orcamentaria", pesquisa.getHierarquiaOrganizacionalOrcamentaria().getSubordinada().getId());
        }
        return Bem.preencherListaDeBensApartirArrayObject(q.getResultList());
    }

    public List<Bem> preencherListaDeBensApartirArrayObject(List<Object[]> objetosDaConsulta) {
        List<Bem> retornaBens = new ArrayList<>();
        for (Object[] bem : objetosDaConsulta) {
            BigDecimal id = (BigDecimal) bem[0];
            Bem b = new Bem();
            b.setId(id.longValue());
            b.setIdentificacao((String) bem[1]);
            b.setDescricao((String) bem[2]);
            b.setIdUltimoEstado(((BigDecimal) bem[3]).longValue());
            b.setDataAquisicao(((Date) bem[4]));
            b.setOrcamentaria((String) bem[5]);
            b.setRegistroAnterior((String) bem[6]);
            retornaBens.add(b);
        }
        for (Bem bem : retornaBens) {
            try {
                Bem.preencherDadosTrasientsDoBem(bem, recuperarEstadoBemPorId(bem.getIdUltimoEstado()));
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return retornaBens;
    }

    public SingletonWPEntities getSingletonWPEntities() {
        return SingletonWPEntities.getINSTANCE(getEntityManager().getMetamodel().getEntities());
    }

    public EventoBem recuperarUltimoEventoBem(Bem bem) {
        return recuperarUltimoEventoBem(bem.getId());
    }

    public EventoBem recuperarUltimoEventoBem(Long idBem) {
        try {
            String hql = "select ev " +
                " from EventoBem ev " +
                " where ev.dataOperacao = (select max(evMax.dataOperacao) " +
                "                 from EventoBem evMax  " +
                "                 where evMax.bem.id = :bem_id)" +
                " and ev.bem.id = :bem_id";
            Query q = em.createQuery(hql, EventoBem.class);
            q.setParameter("bem_id", idBem);
            q.setMaxResults(1);
            return (EventoBem) q.getSingleResult();
        } catch (javax.persistence.NoResultException ex) {
            return null;
        }
    }

    //A Secretaria Administrativa Corrente - Todos do Nível 2 da Hierarquia
    public List<Bem> buscarBensUnidadeAdministrativaPorGrupoPatrimonialVigenteNoParametroFrota(String parte, HierarquiaOrganizacional hierarquiaOrganizacional, TipoObjetoFrota tipoObjetoFrota) {
        String sql = " " +
            " select " +
            "  distinct vwbem.* " +
            " from vwbem " +
            "  inner join estadobem est on est.id = vwbem.id_ultimo_estado_bem " +
            "  inner join grupobem grupo on grupo.id = est.grupobem_id " +
            "  inner join tipoobjetofrotagrupobem config on config.grupopatrimonial_id = grupo.id " +
            "  inner join parametrofrotas pr on pr.id = config.parametrofrotas_id " +
            "  inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = est.detentoraadministrativa_id " +
            "  inner join usuariounidadeorganizacio usu_unid_adm on usu_unid_adm.unidadeorganizacional_id = est.detentoraadministrativa_id " +
            " where vwadm.codigo like :secretaria " +
            "   and usu_unid_adm.usuariosistema_id = :usuario " +
            "   and config.tipoobjetofrota = :tipoObjetoFrota " +
            "   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(vwadm.inicioVigencia) and coalesce(trunc(vwadm.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(config.inicioVigencia) and coalesce(trunc(config.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "   and (trim(lower(vwbem.identificacao_patrimonial)) like :parte or trim(lower(vwbem.descricao)) like :parte)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("usuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("tipoObjetoFrota", tipoObjetoFrota.name());
        q.setParameter("secretaria", hierarquiaOrganizacional.getCodigo().substring(0, 5) + "%");
        q.setParameter("parte", parte.trim().toLowerCase() + "%");
        return Bem.preencherListaDeBensApartirArrayObject(q.getResultList());
    }

    public List<Bem> buscarBensPorAdministrativaAndGrupoAndTipoNotAntEstadoBem(String parte, UnidadeOrganizacional unidadeOrganizacional,
                                                                               GrupoBem grupoBem, String tipoBem, String estadoDoBem) {
        String sql =
            " select  " +
                "  distinct bem.* " +
                "from bem  " +
                "  inner join eventobem ev on ev.bem_id = bem.id " +
                "  inner join estadobem estado on estado.id = ev.estadoresultante_id " +
                "  inner join grupobem gb on gb.id = estado.grupobem_id " +
                "  inner join hierarquiaorganizacional vwadm on vwadm.subordinada_id = estado.detentoraadministrativa_id " +
                "  inner join usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = estado.detentoraadministrativa_id " +
                "where (trim(lower(bem.identificacao)) like :PARTE " +
                "  or trim(lower(bem.descricao)) like :PARTE " +
                "  or trim(lower(bem.registroanterior)) like :PARTE) " +
                "  and gb.tipobem = :TIPOBEM " +
                "  and vwadm.tipohierarquiaorganizacional = :tipoHo " +
                "  and ev.dataoperacao = (select max(evMax.dataoperacao) from eventobem evmax where evmax.bem_id = ev.bem_id) " +
                "  and uu.gestorpatrimonio  = :GESTOR " +
                "  and uu.usuariosistema_id = :USUARIO_ID ";

        if (grupoBem != null && grupoBem.getId() != null) {
            sql += " and gb.id = :IDGRUPOBEM";
        }
        if (estadoDoBem != null) {
            sql += " and estado.estadobem <> :ESTADOBEM ";
        }
        if (unidadeOrganizacional != null && unidadeOrganizacional.getId() != null) {
            sql += " and vwadm.codigo like :CODIGO_HIERARQUIA ";
        }
        sql += " order by to_number(bem.identificacao) ";

        Query q = em.createNativeQuery(sql, Bem.class);
        q.setParameter("PARTE", parte.trim().toLowerCase() + "%");
        q.setParameter("TIPOBEM", tipoBem);
        q.setParameter("tipoHo", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("GESTOR", Boolean.TRUE);
        q.setParameter("USUARIO_ID", sistemaFacade.getUsuarioCorrente().getId());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        if (grupoBem != null && grupoBem.getId() != null) {
            q.setParameter("IDGRUPOBEM", grupoBem.getId());
        }
        if (estadoDoBem != null) {
            q.setParameter("ESTADOBEM", EstadoConservacaoBem.valueOf(estadoDoBem).name());
        }
        if (unidadeOrganizacional != null && unidadeOrganizacional.getId() != null) {
            q.setParameter("CODIGO_HIERARQUIA", getHierarquiaDaUnidade(unidadeOrganizacional, TipoHierarquiaOrganizacional.ADMINISTRATIVA).getCodigoSemZerosFinais() + "%");
        }
        return q.getResultList();
    }

    public List<Bem> buscarBensPorAdministrativaEstadoBemEstornadoAndBaixado(String parte, UnidadeOrganizacional unidadeOrganizacional) {
        String sql =
            " select  " +
                "  distinct bem.* " +
                "from bem  " +
                "  inner join eventobem ev on ev.bem_id = bem.id " +
                "  inner join estadobem estado on estado.id = ev.estadoresultante_id " +
                "  inner join grupobem gb on gb.id = estado.grupobem_id " +
                "  inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = estado.detentoraadministrativa_id " +
                "  inner join usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = estado.detentoraadministrativa_id " +
                "where (trim(lower(bem.identificacao)) like :PARTE " +
                "  or trim(lower(bem.descricao)) like :PARTE " +
                "  or trim(lower(bem.registroanterior)) like :PARTE) " +
                "  and gb.tipobem = :TIPOBEM " +
                " and ev.dataoperacao = (select max(evMax.dataoperacao) from eventobem evmax where evmax.bem_id = ev.bem_id) " +
                " and uu.gestorpatrimonio = :GESTOR and uu.usuariosistema_id = :USUARIO_ID" +
                " and estado.estadobem <> :ESTADO " +
                " and ev.situacaoeventobem not in (:SITUACAOESTORNADO, :SITUACAOESTORNOAQUISICAO) " +
                " and vwadm.codigo like :CODIGO_HIERARQUIA " +
                " order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql, Bem.class);
        q.setParameter("PARTE", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("TIPOBEM", TipoBem.MOVEIS.name());
        q.setParameter("GESTOR", Boolean.TRUE);
        q.setParameter("USUARIO_ID", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("CODIGO_HIERARQUIA", getHierarquiaDaUnidade(unidadeOrganizacional, TipoHierarquiaOrganizacional.ADMINISTRATIVA).getCodigoSemZerosFinais() + "%");
        q.setParameter("ESTADO", EstadoConservacaoBem.BAIXADO.name());
        q.setParameter("SITUACAOESTORNADO", SituacaoEventoBem.ESTORNADO.name());
        q.setParameter("SITUACAOESTORNOAQUISICAO", SituacaoEventoBem.BLOQUEADO.name());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);

        return q.getResultList();
    }

    public HierarquiaOrganizacional getHierarquiaDaUnidade(UnidadeOrganizacional unidadeOrganizacional, TipoHierarquiaOrganizacional tipoHierarquia) {
        return hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
            tipoHierarquia.name(),
            unidadeOrganizacional,
            sistemaFacade.getDataOperacao());
    }

    public void verificarDisponibilidadeParaMoviventacoes(Bem bem) {
        String msg = buscarLocalizacaoDoBem(bem);
        if (msg != null) {
            throw new ExcecaoNegocioGenerica(msg);
        }
    }

    @Deprecated // componente pesquisa bem foi descontinuado
    public String buscarLocalizacaoDoBem(Bem bem) {
        String sql = " select   " +
            "case when TR.id is not null then            'O bem '|| B.IDENTIFICACAO ||' está '|| replace(replace(EVTB.SITUACAOEVENTOBEM,'AGUARDANDO_EFETIVACAO',' aguardando efetivação '),'EM_ELABORACAO','em elaboração')  ||' na transferência n° '                           || LT.CODIGO             || ' e não pode ser movimentado.'   " +
            "    when CS.id is not null then             'O bem '|| B.IDENTIFICACAO ||' está '|| replace(replace(EVTB.SITUACAOEVENTOBEM,'AGUARDANDO_EFETIVACAO',' aguardando efetivação '),'EM_ELABORACAO','em elaboração')  ||' na cessão de bem n° '                           || LC.CODIGO             || ' e não pode ser movimentado.'   " +
            "    WHEN SOLICITABAIXA.ID IS NOT NULL THEN  'O bem '|| B.IDENTIFICACAO ||' está '|| replace(replace(EVTB.SITUACAOEVENTOBEM,'AGUARDANDO_EFETIVACAO',' aguardando efetivação '),'EM_ELABORACAO','em elaboração')  ||' na solicitação de baixa n° '                    || SOLICITABAIXA.CODIGO  || ' e não pode ser movimentado.'   " +
            "    when SOL_EST_TRASF.id is not null then  'O bem '|| B.IDENTIFICACAO ||' está '|| replace(replace(EVTB.SITUACAOEVENTOBEM,'AGUARDANDO_EFETIVACAO',' aguardando efetivação '),'EM_ELABORACAO','em elaboração')  ||' na solicitação de estorno de transferência n° ' || SOL_EST_TRASF.CODIGO  || ' e não pode ser movimentado.'   " +
            "    when ACB.id is not null then            'O bem '|| B.IDENTIFICACAO ||' está '|| replace(replace(EVTB.SITUACAOEVENTOBEM,'AGUARDANDO_EFETIVACAO',' aguardando efetivação '),'EM_ELABORACAO','em elaboração')  ||' na alteração de conservação n° '                || LACB.CODIGO           || ' e não pode ser movimentado.'   " +
            "    when IQ.id is not null then             'O bem '|| B.IDENTIFICACAO ||' está '|| replace(replace(EVTB.SITUACAOEVENTOBEM,'AGUARDANDO_EFETIVACAO',' aguardando efetivação '),'EM_ELABORACAO','em elaboração')  ||' na aquisição de bens n° '                       || AQ.NUMERO             || ' e não pode ser movimentado.'   " +
            "    WHEN ITEM_LOTE_ALIE.ID IS NOT NULL THEN 'O bem '|| B.IDENTIFICACAO ||' está '|| replace(replace(EVTB.SITUACAOEVENTOBEM,'AGUARDANDO_EFETIVACAO',' aguardando efetivação '),'EM_ELABORACAO','em elaboração') ||' na solicitação de alienação de bens n° '         || LOT_SOL_ALIE.NUMERO   || ' e não pode ser movimentado.'   " +
            "    WHEN LOTEREAV.ID       IS NOT NULL THEN 'O bem '|| B.IDENTIFICACAO ||' está '|| replace(replace(EVTB.SITUACAOEVENTOBEM,'AGUARDANDO_EFETIVACAO',' aguardando efetivação '),'EM_ELABORACAO','em elaboração') ||' na solicitação de reavaliação de bens n° '       || LOTEREAV.CODIGO       || ' e não pode ser movimentado.'   " +
            "ELSE 'Operação não definida' END AS OPERACAO    " +
            "FROM EVENTOBEM EVTB      " +
            "INNER JOIN BEM B ON B.ID = EVTB.BEM_ID    " +
            "LEFT JOIN TRANSFERENCIABEM TR ON TR.ID = EVTB.ID    " +
            "LEFT JOIN LOTETRANSFERENCIABEM LT ON LT.ID = TR.LOTETRANSFERENCIABEM_ID    " +
            "LEFT JOIN CESSAO CS ON CS.ID = EVTB.ID    " +
            "LEFT JOIN LOTECESSAO LC ON LC.ID = CS.LOTECESSAO_ID  " +
            "LEFT JOIN ITEMSOLICITBAIXAPATRIMONIO ITEMBAIXA         ON ITEMBAIXA.ID = EVTB.ID   " +
            "LEFT JOIN SOLICITABAIXAPATRIMONIAL   SOLICITABAIXA     ON SOLICITABAIXA.ID = ITEMBAIXA.SOLICITACAOBAIXA_ID   " +
            "LEFT JOIN ITEMESTORNOTRANSFERENCIA   ITEM_ESTOR_TRANSF ON ITEM_ESTOR_TRANSF.ID = EVTB.ID    " +
            "LEFT JOIN SOLICITESTORNOTRANSFER     SOL_EST_TRASF     ON SOL_EST_TRASF.ID = ITEM_ESTOR_TRANSF.SOLICITACAOESTORNO_ID   " +
            "left join ALTERACAOCONSERVACAOBEM    ACB               on ACB.id = EVTB.id    " +
            "left join EFETIVACAOALTERACAOCONSBEM      LACB         on ACB.EFETIVACAOALTERACAOCONSBEM_ID = LACB.id   " +
            "left join ITEMAQUISICAO IQ                             on IQ.id = EVTB.id  " +
            "left join AQUISICAO AQ                                 on AQ.id = IQ.AQUISICAO_ID  " +
            "left join ITEMSOLICITACAOALIENACAO ITEM_LOTE_ALIE      on ITEM_LOTE_ALIE.id = EVTB.id  " +
            "left join LOTEAVALIACAOALIENACAO  LOT_SOL_ALIE         ON LOT_SOL_ALIE.ID = ITEM_LOTE_ALIE.LOTEAVALIACAOALIENACAO_ID " +
            "left join REAVALIACAOBEM REAVALIACAO                   ON REAVALIACAO.ID = EVTB.ID   " +
            "left join LOTEREAVALIACAOBEM LOTEREAV                  ON LOTEREAV.ID = REAVALIACAO.LOTEREAVALIACAOBEM_ID " +
            " WHERE  EVTB.DATAOPERACAO = (SELECT MAX(EV.DATAOPERACAO)   " +
            "                    FROM EVENTOBEM EV     " +
            "                   WHERE EV.BEM_ID = B.ID) " +
            "      AND EVTB.SITUACAOEVENTOBEM not in ( " + situacoesParaMovimentacoes() + ")" +
            "      AND B.ID = :ID_BEM";

        Query q = em.createNativeQuery(sql);
        q.setParameter("ID_BEM", bem.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (String) q.getSingleResult();
        }
        return null;
    }

    public Long recuperarUltimoCodigoDoBemPorEntidade(Entidade entidade, ParametroPatrimonio parametroPatrimonio, TipoBem tipoBem) {
        String sql = " WITH ENTIDADEGERADORA_QUERY (ENTIDADEGERADORA_ID) AS  " +
            "  (SELECT  " +
            "    CASE   " +
            "      WHEN ESA.ID IS NOT NULL THEN ESA.ENTIDADESEQUENCIAPROPRIA_ID  " +
            "      WHEN ESP.ID IS NOT NULL THEN ESP.ID  " +
            "    END AS ENTIDADEGERADORA_ID  " +
            "   FROM ENTIDADEGERADORA EG      " +
            "     LEFT JOIN ENTIDADESEQUENCIAAGREGADA ESA ON ESA.ID  = EG.ID  " +
            "     LEFT JOIN ENTIDADESEQUENCIAPROPRIA  ESP ON ESP.ID  = EG.ID  " +
            "   WHERE EG.ENTIDADE_ID = :entidade                          " +
            "    and eg.tipoBem = :tipoGeracao), " +
            "  ENTIDADES_DA_SEQUENCIA (ENTIDADE_ID) AS " +
            "  (SELECT EG.ENTIDADE_ID  " +
            "     FROM ENTIDADEGERADORA EG " +
            "   INNER JOIN ENTIDADEGERADORA_QUERY EGQ ON EGQ.ENTIDADEGERADORA_ID = EG.ID " +
            "    where eg.tipoBem = :tipoGeracao " +
            "     UNION  " +
            "   SELECT EG.ENTIDADE_ID " +
            "     FROM ENTIDADEGERADORA EG " +
            "   INNER JOIN ENTIDADESEQUENCIAAGREGADA ESA ON EG.ID = ESA.ID " +
            "   INNER JOIN ENTIDADEGERADORA_QUERY EGQ ON EGQ.ENTIDADEGERADORA_ID = ESA.ENTIDADESEQUENCIAPROPRIA_ID " +
            "    where eg.tipoBem = :tipoGeracao) " +
            "   SELECT " +
            "        MAX(TO_NUMBER(regexp_replace(bem.IDENTIFICACAO, '[^0-9]'))) " +
            "     FROM EVENTOBEM EV " +
            "    inner join bem on ev.bem_id = bem.id " +
            "   INNER JOIN ESTADOBEM ESTADO ON ESTADO.ID = EV.ESTADORESULTANTE_ID " +
            "   inner join grupobem grupo on estado.grupobem_id = grupo.id " +
            "   INNER JOIN VWHIERARQUIAADMINISTRATIVA VW ON VW.SUBORDINADA_ID = ESTADO.DETENTORAADMINISTRATIVA_ID " +
            "   INNER JOIN ENTIDADES_DA_SEQUENCIA EDS ON EDS.ENTIDADE_ID = VW.ENTIDADE_ID  " +
            "    WHERE " +
            "          SYSDATE BETWEEN VW.INICIOVIGENCIA    AND COALESCE(VW.FIMVIGENCIA, SYSDATE)  " +
            "    AND EV.ESTADOINICIAL_ID IS NULL " +
            "    and grupo.tipobem = :tipoGeracao ";

        try {
            Query q = em.createNativeQuery(sql);
            q.setParameter("entidade", entidade.getId());
            q.setParameter("tipoGeracao", tipoBem.name());
            Object obj = q.getSingleResult();
            return ((BigDecimal) obj).longValue();
        } catch (NoResultException | NullPointerException ex) {
            if (TipoBem.IMOVEIS.equals(tipoBem)) {
                return Long.valueOf("0");
            } else {
                return parametroPatrimonio.getFaixaFinalParaInsevivel();
            }
        }
    }

    public GrupoObjetoCompraGrupoBemFacade getGrupoObjetoCompraGrupoBemFacade() {
        return grupoObjetoCompraGrupoBemFacade;
    }

    public GrupoObjetoCompraFacade getGrupoObjetoCompraFacade() {
        return grupoObjetoCompraFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public ParametroPatrimonioFacade getParametroPatrimonioFacade() {
        return parametroPatrimonioFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public List recuperarBem(List<? extends EventoBem> lista) {
        if (lista.size() > 0) {
            for (EventoBem eventoBem : lista) {
                eventoBem.setBem(recuperarBemDoEventoBem(eventoBem));
            }
        }
        return lista;
    }

    public TipoReducao recuperarTipoReducaoBem(Bem bem) {
        String sql = " select tr.* " +
            "    from eventoBem evb" +
            "   inner join estadoBem estb on evb.estadoResultante_id = estb.id " +
            "   inner join GrupoBem gb on gb.id = estb.grupobem_id " +
            "   inner join TipoReducao tr on tr.grupobem_id = gb.id " +
            " where evb.id = (select max(id) from eventoBem s_evb where s_evb.bem_id =:bem_id) ";
        Query q = em.createNativeQuery(sql, TipoReducao.class);
        q.setParameter("bem_id", bem.getId());
        if (q.getResultList().size() > 0) {
            return (TipoReducao) q.getResultList().get(0);
        }
        return null;
    }

    public List<Bem> completaBemMovel(String parte) {
        return completarBem(parte, TipoBem.MOVEIS, null, null);
    }

    public List<Bem> completaBemImovel(String parte) {
        return completarBem(parte, TipoBem.IMOVEIS, null, null);
    }

    public List<Bem> completaBemIntangivel(String parte) {
        return completarBem(parte, TipoBem.INTANGIVEIS, null, null);
    }

    public List<Bem> completarBem(String parte, TipoBem tipoBem, HierarquiaOrganizacional administrativa, HierarquiaOrganizacional orcamentaria) {
        return completarBem(parte, tipoBem, administrativa, orcamentaria, true);
    }

    public List<Bem> completarBem(String parte, TipoBem tipoBem, HierarquiaOrganizacional administrativa, HierarquiaOrganizacional orcamentaria, Boolean considerarBensEstornados) {

        String sql =
            "      select " +
                "   distinct bem.* " +
                " from bem " +
                "   inner join eventobem ev on ev.bem_id = bem.id " +
                "   inner join estadobem est on est.id = ev.estadoresultante_id " +
                "   inner join grupobem grupo on grupo.id = est.grupobem_id " +
                " where ev.dataoperacao = (select max(ev2.dataoperacao) from eventobem ev2 where ev2.bem_id = bem.id) " +
                "   and (lower(bem.descricao) LIKE :PARTE OR lower(bem.identificacao) LIKE :PARTE) " +
                (considerarBensEstornados ? "   and ev.situacaoeventobem <> :SITUACAO_ESTORNADO " : "") +
                (tipoBem != null ? " and grupo.tipobem = :TIPOBEM " : "") +
                (administrativa != null ? " and est.detentoraadministrativa_id = :ID_ADMINISTRATIVA " : "") +
                (orcamentaria != null ? " and est.detentoraorcamentaria_id = :ID_ORCAMENTARIA " : "") +
                " order by to_number(regexp_replace(bem.identificacao, '[^[:digit:]]')), bem.descricao ";
        Query q = em.createNativeQuery(sql, Bem.class);
        q.setParameter("PARTE", "%" + parte.trim().toLowerCase() + "%");
        if (considerarBensEstornados) {
            q.setParameter("SITUACAO_ESTORNADO", SituacaoEventoBem.ESTORNADO.name());
        }
        if (tipoBem != null) {
            q.setParameter("TIPOBEM", tipoBem.name());
        }
        if (administrativa != null) {
            q.setParameter("ID_ADMINISTRATIVA", administrativa.getSubordinada().getId());
        }
        if (orcamentaria != null) {
            q.setParameter("ID_ORCAMENTARIA", orcamentaria.getSubordinada().getId());
        }
        q.setMaxResults(50);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<Bem> buscarBemAjustePerdas(String parte, TipoBem tipoBem, HierarquiaOrganizacional administrativa) {
        String sql =
            "      select " +
                "   distinct bem.* " +
                " from bem " +
                "   inner join eventobem ev on ev.bem_id = bem.id " +
                "   inner join estadobem est on est.id = ev.estadoresultante_id " +
                "   inner join grupobem grupo on grupo.id = est.grupobem_id " +
                " where ev.dataoperacao = (select max(ev2.dataoperacao) from eventobem ev2 where ev2.bem_id = bem.id) " +
                "   and (lower(bem.descricao) LIKE :PARTE OR lower(bem.identificacao) LIKE :PARTE) " +
                "   and est.estadobem <> :BAIXADO " +
                (tipoBem != null ? " and grupo.tipobem = :TIPOBEM " : "") +
                (administrativa != null ? " and est.detentoraadministrativa_id = :ID_ADMINISTRATIVA " : "") +
                " order by to_number(regexp_replace(bem.identificacao, '[^[:digit:]]')), bem.descricao ";
        Query q = em.createNativeQuery(sql, Bem.class);
        q.setParameter("PARTE", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("BAIXADO", EstadoConservacaoBem.BAIXADO.name());
        if (tipoBem != null) {
            q.setParameter("TIPOBEM", tipoBem.name());
        }
        if (administrativa != null) {
            q.setParameter("ID_ADMINISTRATIVA", administrativa.getSubordinada().getId());
        }
        q.setMaxResults(50);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public EstadoBem salvarEstadoBem(EstadoBem estadoBem) {
        return em.merge(estadoBem);
    }

    public Bem salvarBem(Bem bem, EstadoBem estadoBem) {
        bem = em.merge(bem);
        salvarEstadoBem(estadoBem);
        return bem;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<List<Bem>> buscarBensNoProcessoAlienacao(HierarquiaOrganizacional hierarquiaOrganizacional, AssistenteBarraProgresso assistente) {
        String sql = " select distinct bem.*  " +
            "  from bem " +
            "  inner join eventobem ev on ev.bem_id = bem.id " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id " +
            "  inner join vwhierarquiaadministrativa vw on vw.subordinada_id = est.detentoraadministrativa_id " +
            "  inner join itemsolicitacaoalienacao item on item.id = ev.id " +
            "  inner join solicitacaoalienacao sol on sol.id = item.solicitacaoalienacao_id " +
            "  where vw.codigo like :codigoHo " +
            "  and sol.situacao <> 'REJEITADA' ";
        Query q = em.createNativeQuery(sql, Bem.class);
        q.setParameter("codigoHo", hierarquiaOrganizacional.getCodigoSemZerosFinais() + "%");
        List<Object> resultado = q.getResultList();
        assistente.setTotal(resultado.size());
        for (Object bem : resultado) {
            ((Bem) bem).getEventosBem().size();
            assistente.conta();
        }
        return new AsyncResult<List<Bem>>(q.getResultList());
    }

    public String preencherCampo(Object objeto, AtributoRelatorioGenerico atributo) {
        try {
            if (atributo.getField() != null) {
                atributo.getField().setAccessible(true);
                if (atributo.getField().isAnnotationPresent(CodigoGeradoAoConcluir.class)) {
                    if (atributo.getField().get(objeto) == null) {
                        return "Código gerado ao concluir.";
                    }
                }
            }
            return recuperadorFacade.preencherCampo(objeto, atributo);
        } catch (Exception e) {
            return "Erro ao preencher campo " + atributo.getField().getName() + ".";
        }
    }

    public EstadoBem buscarEstadoDoBemPorData(Bem bem, Date dataReferencia) {
        String sql = "Select est.* " +
            "           from eventobem ev " +
            "         inner join estadobem est on est.id = ev.estadoResultante_id" +
            "         where ev.dataoperacao = ( select max(evt.dataoperacao)" +
            "                                     from eventobem evt" +
            "                                   where evt.bem_id = ev.bem_id" +
            "                                   and evt.dataLancamento <= :dataTempo" +
            "                                  )" +
            "         and ev.bem_id = :id_bem";
        Query q = em.createNativeQuery(sql, EstadoBem.class);
        q.setParameter("id_bem", bem.getId());
        q.setParameter("dataTempo", DataUtil.ultimaHoraDoDia(dataReferencia), TemporalType.TIMESTAMP);
        try {
            return (EstadoBem) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public EventoBem recuperarUltimoEventoBemComHierarquiaVigente(Bem bem) {
        try {
            String sql = " " +
                "   select ev.id from EventoBem ev " +
                "           inner join estadobem est on est.id = ev.estadoresultante_id " +
                "       where exists(select 1 " +
                "              from HierarquiaOrganizacional hoAdm " +
                "              where ev.dataLancamento between hoAdm.inicioVigencia and coalesce(hoAdm.fimVigencia, ev.dataLancamento) " +
                "              and hoAdm.tipoHierarquiaOrganizacional = :tipoHoAdm " +
                "              and hoAdm.subordinada_id = est.detentoraAdministrativa_id) " +
                "       and ev.bem_id = :bem_id " +
                "   order by ev.datalancamento desc ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("bem_id", bem.getId());
            q.setParameter("tipoHoAdm", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
            q.setMaxResults(1);
            EventoBem eventoBem = em.find(EventoBem.class, ((BigDecimal) q.getSingleResult()).longValue());
            return eventoBem;
        } catch (javax.persistence.NoResultException ex) {
            return null;
        }
    }

    public List<EstadoBem> buscarEstadoBemComHierarquiaEncerrada(Bem bem, Date dataReferencia) {
        try {
            String sql = " " +
                "   select est.* from EventoBem ev " +
                "           inner join estadobem est on est.id = ev.estadoresultante_id " +
                "       where not exists(select 1 " +
                "              from HierarquiaOrganizacional hoAdm " +
                "              where ev.dataLancamento between hoAdm.inicioVigencia and coalesce(hoAdm.fimVigencia, ev.dataLancamento) " +
                "              and hoAdm.tipoHierarquiaOrganizacional = :tipoHoAdm " +
                "              and hoAdm.subordinada_id = est.detentoraAdministrativa_id) " +
                "       and ev.bem_id = :bem_id " +
                "       and trunc(ev.dataLancamento) > to_date(:dataLancamento, 'dd/MM/yyyy') " +
                "   order by ev.datalancamento desc ";
            Query q = em.createNativeQuery(sql, EstadoBem.class);
            q.setParameter("bem_id", bem.getId());
            q.setParameter("tipoHoAdm", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
            q.setParameter("dataLancamento", DataUtil.getDataFormatada(dataReferencia));
            return q.getResultList();
        } catch (javax.persistence.NoResultException ex) {
            return null;
        }
    }


    public EstadoBem buscarEstadoBemPorDataAndHierarquia(Bem bem, Date dataReferencia) {
        String sql = "Select est.* " +
            "           from eventobem ev " +
            "         inner join estadobem est on est.id = ev.estadoResultante_id" +
            "         where ev.dataoperacao = ( select max(evt.dataoperacao)" +
            "                                     from eventobem evt" +
            "                                   where evt.bem_id = ev.bem_id" +
            "                                   and evt.dataLancamento <= :dataTempo" +
            "                                  )" +
            "            and exists(select 1 " +
            "                        from hierarquiaorganizacional hoAdm " +
            "                        where ev.datalancamento between hoAdm.iniciovigencia and coalesce(hoAdm.fimvigencia, ev.datalancamento) " +
            "                         and hoAdm.tipohierarquiaorganizacional = :tipoHoAdm " +
            "                         and hoAdm.subordinada_id = est.detentoraadministrativa_id) " +
            "         and ev.bem_id = :id_bem ";
        Query q = em.createNativeQuery(sql, EstadoBem.class);
        q.setParameter("id_bem", bem.getId());
        q.setParameter("dataTempo", DataUtil.ultimaHoraDoDia(dataReferencia), TemporalType.TIMESTAMP);
        try {
            return (EstadoBem) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<EstadoBem> buscarEstadoResultanteBemPosteriorAData(Bem bem, Date dataReferencia) {
        String sql = " select est.* from eventobem ev " +
            "           inner join estadobem est on est.id = ev.estadoResultante_id" +
            "          where trunc(ev.dataoperacao) > to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "           and ev.bem_id = :idBem ";
        Query q = em.createNativeQuery(sql, EstadoBem.class);
        q.setParameter("idBem", bem.getId());
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(dataReferencia));
        return q.getResultList();
    }

    public EstadoBem recuperarEstadoBemPorId(Long id) {
        return em.find(EstadoBem.class, id);
    }

    public EventoBem buscarEventoBemPorTipoEventoAndBem(Bem bem, TipoEventoBem tipoEventoBem, Date dataLancamento) {
        String hql = " select ev            " +
            "            from EventoBem ev  " +
            "           where ev.bem = :bem " +
            "             and ev.tipoEventoBem = :tipoEvento  " +
            "                 and ev.dataOperacao >= :dataLancamento " +
            "           order by ev.dataOperacao asc ";
        Query q = em.createQuery(hql);
        q.setParameter("bem", bem);
        q.setParameter("tipoEvento", tipoEventoBem);
        q.setParameter("dataLancamento", dataLancamento, TemporalType.TIMESTAMP);
        q.setMaxResults(1);
        try {
            return (EventoBem) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public EventoBem buscarEventoBemAnterirorAoUltimo(Bem bem, Date dataLancamento) {
        String hql = " select ev            " +
            "            from EventoBem ev  " +
            "           where ev.bem = :bem " +
            "            and ev.dataOperacao < :dataLancamento " +
            "           order by ev.dataOperacao desc ";
        Query q = em.createQuery(hql);
        q.setParameter("bem", bem);
        q.setParameter("dataLancamento", dataLancamento, TemporalType.TIMESTAMP);
        q.setMaxResults(1);
        try {
            return (EventoBem) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<EventoBem> buscarEventosBemPorBemMaiorQueDataLacamaneto(Bem bem, Date dataLancamento) {
        String hql = " select ev            " +
            "            from EventoBem ev  " +
            "           where ev.bem = :bem " +
            "             and ev.dataOperacao > :dataLancamento " +
            "           order by ev.dataOperacao asc ";
        Query q = em.createQuery(hql);
        q.setParameter("bem", bem);
        q.setParameter("dataLancamento", dataLancamento, TemporalType.TIMESTAMP);
        try {
            return q.getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<EventoBem> buscarEventosBemPorTipoEventoDataLacamanetoAndBem(Bem bem, TipoEventoBem tipoEventoBem, Date dataLancamento) {
        String hql = " select ev            " +
            "            from EventoBem ev  " +
            "           where ev.bem = :bem " +
            "             and ev.tipoEventoBem = :tipoEvento  " +
            "             and ev.dataOperacao >= :dataLancamento " +
            "           order by ev.dataOperacao asc ";
        Query q = em.createQuery(hql);
        q.setParameter("bem", bem);
        q.setParameter("tipoEvento", tipoEventoBem);
        q.setParameter("dataLancamento", dataLancamento, TemporalType.TIMESTAMP);
        try {
            return q.getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public EventoBem recuperarEventosBemPorTipoEventoDataLacamaentoBem(Bem bem, TipoEventoBem tipoEventoBem, Date dataLancamento) {
        String hql = " select ev            " +
            "            from EventoBem ev  " +
            "           where ev.bem = :bem " +
            "             and ev.tipoEventoBem = :tipoEvento  " +
            "             and ev.dataOperacao >= :dataLancamento " +
            "           order by ev.dataOperacao ";
        Query q = em.createQuery(hql);
        q.setParameter("bem", bem);
        q.setParameter("tipoEvento", tipoEventoBem);
        q.setParameter("dataLancamento", dataLancamento, TemporalType.TIMESTAMP);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (EventoBem) q.getResultList().get(0);
        }
        return null;
    }

    public String montarNomeSecretaria(HierarquiaOrganizacional hierarquiaAdministrativa, HierarquiaOrganizacional hierarquiaOrcamentaria) {
        String nome = "";
        if (hierarquiaAdministrativa != null) {
            HierarquiaOrganizacional secretaria = hierarquiaOrganizacionalFacade.recuperarSecretariaAdministrativaVigente(hierarquiaAdministrativa, getSistemaFacade().getDataOperacao());
            if (secretaria != null) {
                nome = secretaria.getDescricao().toUpperCase();
            }
        }
        if (hierarquiaAdministrativa == null && hierarquiaOrcamentaria != null) {
            HierarquiaOrganizacional hierarquiaAdministrativaDaOrcamentaria = hierarquiaOrganizacionalFacade.recuperarAdministrativaDaOrcamentariaVigente(hierarquiaOrcamentaria, getSistemaFacade().getDataOperacao());
            HierarquiaOrganizacional secretaria = hierarquiaOrganizacionalFacade.recuperarSecretariaAdministrativaVigente(hierarquiaAdministrativaDaOrcamentaria, getSistemaFacade().getDataOperacao());
            if (secretaria != null) {
                nome = secretaria.getDescricao().toUpperCase();
            }
        }
        return nome;
    }

    public Bem recuperarBemPorUnidadeAdministrativa(String registroPatrimonial, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = "select b.* from BEM B " +
            "           inner join EVENTOBEM EVB on B.ID = EVB.BEM_ID " +
            "           inner join ESTADOBEM EB on EB.ID = EVB.ESTADORESULTANTE_ID " +
            "           inner join UNIDADEORGANIZACIONAL UO on  UO.ID = eb.DETENTORAADMINISTRATIVA_ID " +
            "           inner join movimentobloqueiobem mov on mov.bem_id = bem.id " +
            "         where B.IDENTIFICACAO = :registroPatrimonial " +
            "           AND UO.ID = :unidadeOrganizacional  " +
            "           and mov.movimentoum = :bloqueado " +
            "           and mov.movimentodois = :bloqueado " +
            "           AND EVB.DATAOPERACAO = (select max(ev.dataoperacao) " +
            "                                                   from eventobem ev " +
            "                                                   where ev.bem_id = evb.bem_id)" +
            "          and mov.datamovimento = (select max(mov2.datamovimento) from movimentobloqueiobem mov2 where  b.id = mov2.bem_id) ";
        Query q = em.createNativeQuery(sql, Bem.class);
        q.setParameter("registroPatrimonial", registroPatrimonial);
        q.setParameter("unidadeOrganizacional", unidadeOrganizacional.getId());
        q.setParameter("bloqueado", Boolean.FALSE);
        try {
            return (Bem) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public String verificarUnidadeDoBemNoControleSetorial(Bem bem, List<Long> unidadesDoControleSetorial) {
        String sql = " select  " +
            "  vw.subordinada_id as unidadeId,  " +
            "  vw.codigo || ' - ' || vw.descricao as unidade " +
            "from bem bem " +
            "  inner join eventobem ev on bem.id = ev.bem_id " +
            "  inner join estadobem est on ev.estadoresultante_id = est.id " +
            "  inner join vwhierarquiaadministrativa vw on est.detentoraadministrativa_id = vw.subordinada_id " +
            "where ev.dataoperacao between vw.iniciovigencia and coalesce(vw.fimvigencia, ev.dataoperacao) " +
            "  and ev.dataoperacao = (select max(evbem.dataoperacao) from eventobem evbem where evbem.bem_id = bem.id)" +
            "  and bem.id = :idBem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idBem", bem.getId());
        q.setMaxResults(1);
        try {
            String toReturn = "";
            Object[] obj = (Object[]) q.getSingleResult();
            if (obj != null) {
                BigDecimal unidadeId = (BigDecimal) obj[0];
                if (unidadesDoControleSetorial.contains(unidadeId)) {
                    toReturn = "O bem " + bem.getIdentificacao() + " não pode ser alienado, pois está em uma Unidade do Controle Setorial: " + ((String) obj[1]) + ".";
                }
            }
            return toReturn;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void deletarItemEventoBem(Long idEvento, String tabela) {
        String sql = " delete from " + tabela + " item  where item.id = :idEvento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEvento", idEvento);
        q.executeUpdate();
    }

    public void deletarEventoBem(Long idEvento) {
        String sql = "delete from eventobem e where e.id = :idEvento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEvento", idEvento);
        q.executeUpdate();
    }

    public void atualizarSituacaoEventoBem(Long idEvento, SituacaoEventoBem situacao) {
        String sql = " update eventobem set situacaoeventobem = :situacao where id = :idEvento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEvento", idEvento);
        q.setParameter("situacao", situacao.name());
        q.executeUpdate();
    }

    public void deletarEstadoBem(Long idEstado) {
        String sql = " delete from estadobem where id = :idEstado ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEstado", idEstado);
        q.executeUpdate();
    }

    public List<Bem> buscarFiltrandoBem(String parte, FiltroPesquisaBem filtroPesquisaBem) {
        String sql =
            " select  " +
                "  distinct bem.* " +
                "from bem  " +
                "  inner join eventobem ev on ev.bem_id = bem.id " +
                "  inner join estadobem estado on estado.id = ev.estadoresultante_id " +
                "  inner join grupobem gb on gb.id = estado.grupobem_id " +
                "  inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = estado.detentoraadministrativa_id " +
                "  inner join usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = estado.detentoraadministrativa_id " +
                "where (trim(lower(bem.identificacao)) like :parte " +
                "  or trim(lower(bem.descricao)) like :parte " +
                "  or trim(lower(bem.registroanterior)) like :parte) " +
                "  and gb.tipobem = :tipoBem " +
                "  and ev.dataoperacao = (select max(evMax.dataoperacao) from eventobem evmax where evmax.bem_id = ev.bem_id) " +
                "  and uu.gestorpatrimonio = :gestor " +
                "  and uu.usuariosistema_id = :idUsuario " +
                "  and estado.estadoBem <> :estadoConservacao ";
        if (filtroPesquisaBem.getGrupoBem() != null && filtroPesquisaBem.getGrupoBem().getId() != null) {
            sql += " and gb.id = :idGrupoBem";
        }
        if (filtroPesquisaBem.getHierarquiaAdministrativa() != null && filtroPesquisaBem.getHierarquiaAdministrativa().getId() != null) {
            sql += " and vwadm.codigo like :codigoHierarquia ";
        }
        sql += " order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql, Bem.class);
        q.setParameter("parte", parte.trim().toLowerCase() + "%");
        q.setParameter("tipoBem", filtroPesquisaBem.getTipoBem().name());
        q.setParameter("gestor", Boolean.TRUE);
        q.setParameter("estadoConservacao", EstadoConservacaoBem.BAIXADO.name());
        q.setParameter("idUsuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        if (filtroPesquisaBem.getGrupoBem() != null && filtroPesquisaBem.getGrupoBem().getId() != null) {
            q.setParameter("idGrupoBem", filtroPesquisaBem.getGrupoBem().getId());
        }
        if (filtroPesquisaBem.getHierarquiaAdministrativa() != null && filtroPesquisaBem.getHierarquiaAdministrativa().getId() != null) {
            q.setParameter("codigoHierarquia", filtroPesquisaBem.getHierarquiaAdministrativa().getCodigoSemZerosFinais() + "%");
        }
        return q.getResultList();
    }

    public Bem recuperarBemPorIdBem(Number idBem) {
        try {
            String sql = "select * from bem where id = :idBem ";
            Query q = em.createNativeQuery(sql, Bem.class);
            q.setParameter("idBem", idBem);
            q.setMaxResults(1);
            return (Bem) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public VODadosBem recuperarDadosBem(Bem bem) {
        String sql = " " +
            " select  " +
            "  b.identificacao, " +
            "  b.registroanterior, " +
            "  b.dataaquisicao, " +
            "  b.descricao, " +
            "  b.marca, " +
            "  b.modelo, " +
            "  gb.codigo || ' - ' ||gb.descricao as grupoBem, " +
            "  grupo.codigo || ' - ' || grupo.descricao as grupoObjetoCompra, " +
            "  vwadm.codigo || ' - ' || vwadm.descricao as unidadeAdm, " +
            "  vworc.codigo || ' - ' || vworc.descricao as unidadeOrc, " +
            "  est.estadobem as estadoConservacao, " +
            "  est.situacaoconservacaobem as situacaoConservacao, " +
            "  est.tipogrupo, " +
            "  est.valororiginal, " +
            "  est.valoracumuladodadepreciacao, " +
            "  est.valoracumuladodaamortizacao, " +
            "  est.valoracumuladodaexaustao, " +
            "  est.valoracumuladodeajuste " +
            "from bem b " +
            "  inner join eventobem ev on ev.bem_id = b.id " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id " +
            "  inner join grupobem gb on gb.id = est.grupobem_id " +
            "  inner join grupoobjetocompra grupo on grupo.id = est.grupoobjetocompra_id " +
            "  inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = est.detentoraadministrativa_id " +
            "  inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = est.detentoraorcamentaria_id " +
            "where ev.dataoperacao = (select max(ev2.dataoperacao) from eventobem ev2 where ev2.bem_id = b.id) " +
            "  and trunc(ev.dataoperacao) between vwadm.iniciovigencia and coalesce(vwadm.fimvigencia, trunc(ev.dataoperacao)) " +
            "  and trunc(ev.dataoperacao) between vworc.iniciovigencia and coalesce(vworc.fimvigencia, trunc(ev.dataoperacao)) " +
            "  and b.id = :idBem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idBem", bem.getId());
        try {
            VODadosBem item = new VODadosBem();
            Object[] obj = (Object[]) q.getResultList().get(0);
            item.setRegistroPatimonial((String) obj[0]);
            item.setRegistroAnterior((String) obj[1]);
            item.setDataAquisicao((Date) obj[2]);
            item.setDescricao((String) obj[3]);
            item.setMarca((String) obj[4]);
            item.setModelo((String) obj[5]);
            item.setGrupoPatrimonial((String) obj[6]);
            item.setGrupoObjetoCompra((String) obj[7]);
            item.setUnidadeAdministrativa((String) obj[8]);
            item.setUnidadeOrcamentaria((String) obj[9]);
            item.setEstadoConservacao(obj[10] != null ? EstadoConservacaoBem.valueOf((String) obj[10]).getDescricao() : "");
            item.setSituacaoConservacao(obj[11] != null ? SituacaoConservacaoBem.valueOf((String) obj[11]).getDescricao() : "");
            item.setTipoGrupo(obj[12] != null ? TipoGrupo.valueOf((String) obj[12]).getDescricao() : "");
            item.setValorOriginal((BigDecimal) obj[13]);
            item.setValorDepreciacao((BigDecimal) obj[14]);
            item.setValorAmortizacao((BigDecimal) obj[15]);
            item.setValorExaustao((BigDecimal) obj[16]);
            item.setValorAjuste((BigDecimal) obj[17]);
            item.setValorLiquido(item.getValorOriginal().subtract((item.getValorDepreciacao().add(item.getValorAmortizacao().add(item.getValorExaustao().add(item.getValorAjuste()))))));
            return item;
        } catch (NoResultException nre) {
            return null;
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Object[] filtarComContadorDeRegistros(String sql, String sqlCount, int inicio, int max, TipoBem tipoBem, List<DataTablePesquisaGenerico> camposPesquisa) throws Exception {
        Query consulta = em.createNativeQuery(sql);
        Query consultaCount = em.createNativeQuery(sqlCount);
        getParemeter(tipoBem, consulta, camposPesquisa);
        getParemeter(tipoBem, consultaCount, camposPesquisa);
        Long count = 0L;
        List<Bem> bens = new ArrayList<>();
        try {
            count = ((BigDecimal) consultaCount.getSingleResult()).longValue();
            if (max != 0) {
                consulta.setMaxResults(max);
                consulta.setFirstResult(inicio);
            }
            List<Object[]> lista = consulta.getResultList();
            for (Object[] object : lista) {
                Bem bem = new Bem();
                bem.setId(object[0] != null ? ((BigDecimal) object[0]).longValue() : null);
                bem.setIdUltimoEstado(object[1] != null ? ((BigDecimal) object[1]).longValue() : null);
                bem.setIdentificacao(object[2] != null ? (String) object[2] : "");
                bem.setRegistroAnterior(object[3] != null ? (String) object[3] : "");
                bem.setDataAquisicao(object[4] != null ? (Date) object[4] : null);
                bem.setDescricao(object[5] != null ? (String) object[5] : "");
                bem.setMarca(object[6] != null ? (String) object[6] : "");
                bem.setModelo(object[7] != null ? (String) object[7] : "");
                bem.setAdministrativa((String) object[8]);
                bem.setOrcamentaria((String) object[9]);
                EstadoBem estadoBem = em.find(EstadoBem.class, bem.getIdUltimoEstado());
                Bem.preencherDadosTrasientsDoBem(bem, estadoBem);
                bens.add(bem);
            }
        } catch (NoResultException nre) {
            logger.error("Erro ao montar a pesquisa genérica do bem{} ", nre);
        }
        Object[] retorno = new Object[2];
        retorno[0] = bens;
        retorno[1] = count;
        return retorno;
    }

    private void getParemeter(TipoBem tipoBem, Query query, List<DataTablePesquisaGenerico> camposPesquisa) {
        query.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        query.setParameter("dataFiltroEvento", DataUtil.getDataFormatadaDiaHora(new Date()));
        query.setParameter("tipoBem", tipoBem.name());
        if (camposPesquisa != null) {
            for (DataTablePesquisaGenerico campo : camposPesquisa) {
                if ("Ingressado por Incorporação".equals(campo.getItemPesquisaGenerica().getLabel().trim())) {
                    query.setParameter("ingressadoPorIncorporacao", TipoEventoBem.INCORPORACAOBEM.name());
                    break;
                }
                if ("Ingressado por Aquisição".equals(campo.getItemPesquisaGenerica().getLabel().trim())) {
                    query.setParameter("ingressadoPorAquisicao", TipoEventoBem.ITEMAQUISICAO.name());
                    break;
                }
                if ("Ingressado por Levantamento".equals(campo.getItemPesquisaGenerica().getLabel().trim())) {
                    if (tipoBem.isMovel()) {
                        query.setParameter("ingressadoPorLevantamento", TipoEventoBem.EFETIVACAOLEVANTAMENTOBEM.name());
                    } else {
                        query.setParameter("ingressadoPorLevantamento", TipoEventoBem.EFETIVACAOLEVANTAMENTOIMOVEL.name());
                    }
                    break;
                }
            }
        }
    }

    public Long count(String sql, Boolean considerarParametro, TipoBem tipoBem) {
        try {
            Query query = em.createNativeQuery(sql);
            if (considerarParametro) {
                getParemeter(tipoBem, query, null);
            }
            return ((BigDecimal) query.getSingleResult()).longValue();
        } catch (Exception nre) {
            return 0L;
        }
    }

    private String getSqlContultaBens(ConsultaBemMovelFiltro filtro) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct ")
            .append("  obj.id               AS bemId, ")
            .append("  obj.identificacao    AS registroPatrimonial, ")
            .append("  obj.registroanterior AS registroAnterior, ")
            .append("  obj.dataaquisicao    AS dataAquisicao, ")
            .append("  obj.descricao        AS descricao, ")
            .append("  obj.marca            AS marca, ")
            .append("  obj.modelo           AS modelo, ")
            .append("  gb.codigo || ' - ' || gb.descricao  AS grupoPatrimonial, ")
            .append("  grupoobj.codigo || ' - ' || grupoobj.descricao  AS grupoObjetoCompra, ")
            .append("  hoadm.codigo  || ' - ' || hoadm.descricao AS unidadeAdm, ")
            .append("  hoorc.codigo || ' - ' || hoorc.descricao AS unidadeOrc, ")
            .append("  est.tipoaquisicaobem as tipoAquisicao, ")
            .append("  est.estadobem estadoConservacao, ")
            .append("  est.situacaoconservacaobem as situacaoConservacao, ")
            .append("  est.tipogrupo as tipoGrupo, ")
            .append("  coalesce(est.valororiginal,0) as valorOriginal, ")
            .append("  coalesce(est.valoracumuladodadepreciacao,0) as valorDepreciacao, ")
            .append("  coalesce(est.valoracumuladodaamortizacao,0) as valorAmortizacao, ")
            .append("  coalesce(est.valoracumuladodeajuste,0) as valorAjuste, ")
            .append("  coalesce(est.valororiginal,0) - (coalesce(est.valoracumuladodadepreciacao,0) + coalesce(est.valoracumuladodaamortizacao,0) + coalesce(est.valoracumuladodaexaustao,0) + coalesce(est.valoracumuladodeajuste,0)) as valorLiquido ")
            .append(getCorpoSqlConsultaBens(filtro, true));
        return sql.toString();
    }

    private String getSqlContultaBensContador(ConsultaBemMovelFiltro filtro) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select count(obj.id) ").append(getCorpoSqlConsultaBens(filtro, false));
        return sql.toString();
    }

    private StringBuilder getCorpoSqlConsultaBens(ConsultaBemMovelFiltro filtro, Boolean ordenacao) {
        StringBuilder sql = new StringBuilder();
        sql.append("  from bem obj ")
            .append("  inner join eventobem ev on ev.bem_id = obj.id ")
            .append("  inner join estadobem est on est.id = ev.estadoresultante_id ")
            .append("  inner join grupobem gb on gb.id = est.grupobem_id ")
            .append("  inner join grupoObjetoCompra grupoobj on grupoobj.id = est.grupoobjetocompra_id ")
            .append(filtro.hasFiltrosNotaOrEmpenhoUtilizados() ? "left join bemnotafiscal nota on nota.bem_id = obj.id left join bemnotafiscalempenho notaEmp on notaemp.bemnotafiscal_id = nota.id " : "")
            .append("  inner join hierarquiaorganizacional hoorc on hoorc.subordinada_id = est.detentoraorcamentaria_id ")
            .append("     and to_date(:dataOperacao, 'dd/MM/yyyy') between hoorc.iniciovigencia and coalesce(hoorc.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy'))")
            .append("     and hoorc.tipohierarquiaorganizacional = :tipoHierarquiaOrcamentaria ")
            .append("  inner join hierarquiaorganizacional hoadm on hoadm.subordinada_id = est.detentoraadministrativa_id ")
            .append("     and to_date(:dataOperacao, 'dd/MM/yyyy') between hoadm.iniciovigencia and coalesce(hoadm.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy'))")
            .append("     and hoadm.tipohierarquiaorganizacional = :tipoHierarquiaAdministrativa ")
            .append(" where ev.dataOperacao = (select max(evento.dataOperacao) ")
            .append("   from eventobem evento ")
            .append("   where evento.bem_id = obj.id ")
            .append("   and evento.dataLancamento <= :dataFiltroEvento) ")
            .append("   and gb.tipoBem = :tipoBem ")
            .append("   and exists (select 1 from usuariounidadeorganizacio usuunid ")
            .append("       where usuunid.gestorpatrimonio =  :gestorPatrimonio ")
            .append("       and est.detentoraadministrativa_id = usuunid.unidadeorganizacional_id ")
            .append("       and usuunid.usuariosistema_id = :idUsuario ")
            .append("  )")
            .append(filtro.montarCondicao())
            .append(ordenacao ? filtro.montarOrdenacao() : "");
        return sql;
    }

    public Object[] consultarBensComContadorDeRegistros(ConsultaBemMovelFiltro filtro, Integer maximoRegistrosTabele, Integer inicio) throws Exception {
        Query consulta = em.createNativeQuery(getSqlContultaBens(filtro));
        Query consultaCount = em.createNativeQuery(getSqlContultaBensContador(filtro));
        getParameterConsultaBem(filtro, consulta);
        getParameterConsultaBem(filtro, consultaCount);
        Long count;
        List<VOBem> bens;
        try {
            count = ((BigDecimal) consultaCount.getSingleResult()).longValue();
            if (maximoRegistrosTabele != 0) {
                consulta.setMaxResults(maximoRegistrosTabele);
                consulta.setFirstResult(inicio);
            }
            List list = consulta.getResultList();
            bens = ConsultaBemMovelFiltro.preencherListaVOBensApartirArrayObject(list);
        } catch (NoResultException nre) {
            logger.error("Erro ao montar a pesquisa genérica do bem{} ", nre);
            throw new ExcecaoNegocioGenerica("Não foi possível recuperar os bens para a consulta");
        }
        Object[] retorno = new Object[2];
        retorno[0] = bens;
        retorno[1] = count;
        return retorno;
    }

    private void getParameterConsultaBem(ConsultaBemMovelFiltro filtro, Query query) {
        query.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        query.setParameter("dataFiltroEvento", DataUtil.getDataFormatadaDiaHora(new Date()));
        query.setParameter("tipoBem", TipoBem.MOVEIS.name());
        query.setParameter("idUsuario", filtro.getUsuarioSistema().getId());
        query.setParameter("gestorPatrimonio", Boolean.TRUE);
        query.setParameter("tipoHierarquiaOrcamentaria", TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
        query.setParameter("tipoHierarquiaAdministrativa", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
    }

    public Long recuperarQuantidadeRegistroExistentes() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select count(distinct obj.id) from bem obj ");
        Query query = em.createNativeQuery(sql.toString());
        return ((BigDecimal) query.getSingleResult()).longValue();
    }

    public List<VOEventoBem> buscarEventosBem(Bem bem) {
        String sql = " " +
            " select  " +
            "  ev.id, " +
            "  ev.datalancamento, " +
            "  ev.dataoperacao, " +
            "  ev.tipoeventobem, " +
            "  ev.valordolancamento, " +
            "  ev.situacaoeventobem, " +
            "  (select hoadm.codigo || ' - ' || hoadm.descricao from hierarquiaorganizacional hoadm " +
            "        where ev.datalancamento between hoadm.iniciovigencia and coalesce(hoadm.fimvigencia, ev.datalancamento) " +
            "          and hoadm.subordinada_id = est.detentoraadministrativa_id " +
            "          and hoadm.tipohierarquiaorganizacional = :tipoHierarquiaAdministrativa) as unidade_adm, " +
            "  hoorc.codigo || ' - ' || hoorc.descricao   as unidadeorc, " +
            "  gb.codigo || ' - ' || gb.descricao as grupopatrimonial, " +
            "  est.localizacao, " +
            "  est.tipoaquisicaobem, " +
            "  est.estadobem, " +
            "  est.situacaoconservacaobem, " +
            "  ev.detentorarquivocomposicao_id as idDetentor, " +
            "  ev.estadoinicial_id as estadoInicial, " +
            "  ev.estadoresultante_id as estadoResultante " +
            " from eventobem ev " +
            " inner join estadobem est on est.id = ev.estadoresultante_id " +
            " inner join grupobem gb on gb.id = est.grupobem_id " +
            " inner join hierarquiaorganizacional hoorc on hoorc.subordinada_id = est.detentoraorcamentaria_id " +
            " where ev.datalancamento between hoorc.iniciovigencia and coalesce(hoorc.fimvigencia, ev.datalancamento) " +
            "   and hoorc.tipohierarquiaorganizacional = :tipoHierarquiaOrcamentaria " +
            "   and gb.tipobem = :tipoBem " +
            "   and ev.bem_id = :idBem " +
            " order by ev.dataoperacao asc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idBem", bem.getId());
        q.setParameter("tipoBem", TipoBem.MOVEIS.name());
        q.setParameter("tipoHierarquiaOrcamentaria", TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
        q.setParameter("tipoHierarquiaAdministrativa", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());

        List<Object[]> eventos = q.getResultList();
        List<VOEventoBem> retorno = Lists.newArrayList();

        if (!eventos.isEmpty()) {
            for (Object[] obj : eventos) {
                VOEventoBem evento = new VOEventoBem();
                evento.setIdEvento(((BigDecimal) obj[0]).longValue());
                evento.setDataLancamento((Date) obj[1]);
                evento.setDataOperacao((Date) obj[2]);
                evento.setOperacao(TipoEventoBem.valueOf((String) obj[3]).getDescricao());
                evento.setValorLancamento((BigDecimal) obj[4]);
                evento.setSituacao(SituacaoEventoBem.valueOf((String) obj[5]).getDescricao());
                evento.setUnidadeAdministrativa((String) obj[6]);
                evento.setUnidadeOrcamentaria((String) obj[7]);
                evento.setGrupoPatrimonial((String) obj[8]);
                evento.setLocalizacao((String) obj[9]);
                evento.setTipoAquisicao(TipoAquisicaoBem.valueOf((String) obj[10]).getDescricao());
                evento.setEstadoConservacao(EstadoConservacaoBem.valueOf((String) obj[11]).getDescricao());
                evento.setSituacaoConservacao(SituacaoConservacaoBem.valueOf((String) obj[12]).getDescricao());
                evento.setIdDetentor(obj[13] != null ? ((BigDecimal) obj[13]).longValue() : null);
                evento.setEstadoInicial(obj[14] != null ? recuperarEstadoBemPorId(((BigDecimal) obj[14]).longValue()) : null);
                EstadoBem estadoResultante = recuperarEstadoBemPorId(((BigDecimal) obj[15]).longValue());
                evento.setEstadoResultante(estadoResultante);

                if (Util.isStringNulaOuVazia(evento.getUnidadeAdministrativa())) {
                    HierarquiaOrganizacional hoAdmAtual = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), estadoResultante.getDetentoraAdministrativa(), sistemaFacade.getDataOperacao());
                    evento.setUnidadeAdministrativa(hoAdmAtual !=null ? hoAdmAtual.getCodigo() + " - " + hoAdmAtual.getDescricao() : "");
                }
                retorno.add(evento);
            }
        }
        return retorno;
    }

    public List<VOEventoBem> buscarEventosBemCorrecaoDados(TipoEventoBem tipoEventoBem) {
        StringBuilder sql = new StringBuilder();
        sql.append("select  ")
            .append("  ev.id, ")
            .append("  ev.bem_id as idBem, ")
            .append("  ev.dataoperacao, ")
            .append("  ev.tipoeventobem, ")
            .append("  ev.situacaoeventobem, ")
            .append("  bem.identificacao, ")
            .append("  bem.descricao ")
            .append("from eventobem ev ")
            .append("inner join bem on bem.id = ev.bem_id ")
            .append("inner join estadobem est on est.id = ev.estadoresultante_id ")
            .append("inner join grupobem gb on gb.id = est.grupobem_id ")
            .append("   where gb.tipobem = :tipoBem ")
            .append("   and ev.tipoeventobem = :tipoEvento ")
            .append("   and ev.situacaoeventobem <> :eventoRecusado ")
            .append(" order by ev.dataoperacao asc ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("tipoBem", TipoBem.MOVEIS.name());
        q.setParameter("tipoEvento", tipoEventoBem.name());
        q.setParameter("eventoRecusado", SituacaoEventoBem.RECUSADO.name());
        List<Object[]> eventos = q.getResultList();
        List<VOEventoBem> retorno = Lists.newArrayList();

        if (!eventos.isEmpty()) {
            for (Object[] obj : eventos) {
                VOEventoBem evento = new VOEventoBem();
                evento.setIdEvento(((BigDecimal) obj[0]).longValue());
                evento.setIdBem(((BigDecimal) obj[1]).longValue());
                evento.setDataOperacao((Date) obj[2]);
                evento.setTipoEventoBem(TipoEventoBem.valueOf((String) obj[3]));
                evento.setSituacaoEventoBem(SituacaoEventoBem.valueOf((String) obj[4]));
                evento.setIdentificacaoBem((String) obj[5]);
                evento.setDescricaoBem((String) obj[6]);
                retorno.add(evento);
            }
        }
        return retorno;
    }

    public DetentorArquivoComposicao recuperarDependenciasArquivoComposicao(Long idDetentor) {
        DetentorArquivoComposicao detentor = em.find(DetentorArquivoComposicao.class, idDetentor);
        if (detentor != null) {
            detentor.getArquivosComposicao().size();
        }
        return detentor;
    }

    public List<VOBemPortal> buscarVOBensPortal(String condicao, Date dataOperacao) {
        String sql = " select distinct " +
            "       bem.IDENTIFICACAO as registroPatrimonial, " +
            "       bem.DATAAQUISICAO as DATAAQUISICAO, " +
            "       estado.SITUACAOCONSERVACAOBEM as situacaoConservacaoBem, " +
            "       estado.ESTADOBEM as estadoBem, " +
            "       grupo.TIPOBEM as tipoBem, " +
            "       estado.TIPOAQUISICAOBEM as tipoAquisicaoBem, " +
            "       coalesce(estado.VALORORIGINAL, 0) as valorAquisicao, " +
            "       bem.DESCRICAO as descricaoBem, " +
            "       estado.DETENTORAADMINISTRATIVA_ID as idUnidadeAdm, " +
            "       estado.DETENTORAORCAMENTARIA_ID as idUnidadeOrc, " +
            "       pfResponsavel.NOME as responsavel," +
            "       tipoVinculo.DESCRICAO as tipoPropriedade," +
            "       coalesce(estado.valorAcumuladoDaAmortizacao, 0) + coalesce(estado.valorAcumuladoDaDepreciacao, 0) + " +
            "       coalesce(estado.valorAcumuladoDaExaustao, 0) + coalesce(estado.valorAcumuladoDeAjuste, 0) as valorAjustes " +
            "  from BEM bem " +
            "  inner join EVENTOBEM evento on bem.ID = evento.BEM_ID " +
            "  inner join ESTADOBEM estado on evento.ESTADORESULTANTE_ID = estado.ID " +
            "  inner join GRUPOBEM grupo on estado.GRUPOBEM_ID = grupo.ID " +
            "  left join ResponsavelPatrimonio rp on rp.UNIDADEORGANIZACIONAL_ID = estado.DETENTORAADMINISTRATIVA_ID " +
            "        and to_date(:dataOperacao, 'dd/MM/yyyy') between rp.INICIOVIGENCIA and coalesce(rp.FIMVIGENCIA, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "  left join PESSOAFISICA pfResponsavel on rp.RESPONSAVEL_ID = pfResponsavel.ID " +
            "  left join BEMMOVELPROPRIEDADETERCEIR propriedadeTerceiro on propriedadeTerceiro.GRUPOBEM_ID = grupo.ID " +
            "  left join TIPOVINCULOBEM tipoVinculo on propriedadeTerceiro.TIPOVINCULOBEM_ID = tipoVinculo.ID " +
            "     where evento.DATAOPERACAO = (select max(evMax.dataoperacao) from eventobem evMax where evMax.BEM_ID = bem.id) and " +
            condicao +
            " order by bem.DATAAQUISICAO, bem.IDENTIFICACAO ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        List<Object[]> resultado = q.getResultList();
        List<VOBemPortal> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                VOBemPortal bem = new VOBemPortal();
                bem.setIdentificacao(obj[0] != null ? (String) obj[0] : "");
                bem.setDataAquisicao((Date) obj[1]);
                bem.setSituacaoDeConservacao(obj[2] != null ? (String) obj[2] : "");
                bem.setEstadoDoBem(obj[3] != null ? (String) obj[3] : "");
                bem.setTipoDeBem(obj[4] != null ? (String) obj[4] : "");
                bem.setTipoDeAquisicaoDoBem(obj[5] != null ? (String) obj[5] : "");
                bem.setValorDeAquisicao((BigDecimal) obj[6]);
                bem.setDescricao(obj[7] != null ? (String) obj[7] : "");
                bem.setIdUnidadeAdm(((BigDecimal) obj[8]).longValue());
                bem.setIdunidadeOrc(((BigDecimal) obj[9]).longValue());
                bem.setResponsavel(obj[10] != null ? (String) obj[10] : "");
                bem.setTipoPropriedade(obj[11] != null ? (String) obj[11] : null);
                bem.setValorAjustes((BigDecimal) obj[12]);
                bem.setValorLiquido(bem.getValorDeAquisicao().subtract(bem.getValorAjustes()));
                retorno.add(bem);
            }
        }
        return retorno;
    }

    public Date getDataPrimeiraAquisicao() {
        String sql = " select MIN(bem.dataaquisicao) from bem ";
        Query q = em.createNativeQuery(sql);
        try {
            return (Date) q.getSingleResult();
        } catch (NullPointerException | NoResultException e) {
            return DataUtil.montaData(1, 1, 2000).getTime();
        }
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }

    public ConfiguracaoDeRelatorioFacade getConfiguracaoDeRelatorioFacade() {
        return configuracaoDeRelatorioFacade;
    }

    public void salvarPortal(Bem bem) {
        if (bem != null && bem.getId() != null) {
            em.persist(new BemPortal(bem));
        }
    }

    public List<BensMoveisBloqueio> buscarBensBloqueadoParaMovimentacao(FiltroPesquisaBem filtro) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select  ")
            .append("  vwadm.codigo as codigoadministrativa,  ")
            .append("  vwadm.descricao as descricaoadministrativa,  ")
            .append("  vworc.codigo as codigoorcamentaria,  ")
            .append("  vworc.descricao as descricaoorcamentaria,  ")
            .append("  bem.identificacao,  ")
            .append("  bem.descricao,  ")
            .append("  mov.situacaoeventobem,  ")
            .append("  mov.operacaoMovimento,  ")
            .append("  mov.datamovimento, ")
            .append("  mov.movimentoUm, ")
            .append("  mov.movimentoDois, ")
            .append("  mov.movimentoTres, ")
            .append("  bem.id ")
            .append("from movimentobloqueiobem mov ")
            .append("  inner join bem bem on mov.bem_id = bem.id ")
            .append("  inner join eventobem ev on ev.bem_id = bem.id ")
            .append("  inner join estadobem est on ev.estadoresultante_id = est.id ")
            .append("  inner join grupobem gm on gm.id = est.grupobem_id  ")
            .append("  inner join vwhierarquiaadministrativa vwadm on est.detentoraadministrativa_id = vwadm.subordinada_id  ")
            .append("  inner join vwhierarquiaorcamentaria vworc on est.detentoraorcamentaria_id = vworc.subordinada_id  ")
            .append("where to_date(:dataOperacao, 'dd/mm/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy'))  ")
            .append("  and to_date(:dataOperacao, 'dd/mm/yyyy') between vwadm.iniciovigencia and coalesce(vwadm.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy'))  ")
            .append("  and mov.datamovimento = (select max(mov2.datamovimento) from movimentobloqueiobem mov2 where bem.id = mov2.bem_id)  ")
            .append("  and ev.dataoperacao = (select max(evbem.dataoperacao)  from eventobem evbem where evbem.bem_id = bem.id) ")
            .append("  and ev.tipoeventobem <> :eventoEstornoAquisicao ")
            .append(filtro.getCondicaoConsultaRelatorioBloqueioBens())
            .append(" order by vwadm.codigo, vworc.codigo, bem.identificacao  ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(filtro.getDataOperacao()));
        q.setParameter("bloqueado", Boolean.TRUE);
        q.setParameter("eventoEstornoAquisicao", TipoEventoBem.ITEMAQUISICAO_ESTORNO.name());
        if (!filtro.getIdsBem().isEmpty()) {
            q.setParameter("idsBem", filtro.getIdsBem());
        }
        List<Object[]> resultado = q.getResultList();
        List<BensMoveisBloqueio> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                BensMoveisBloqueio bensMoveisBloqueio = new BensMoveisBloqueio();
                bensMoveisBloqueio.setCodigoAdministrativa((String) obj[0]);
                bensMoveisBloqueio.setUnidadeAdministrativa((String) obj[1]);
                bensMoveisBloqueio.setCodigoOrcamentaria((String) obj[2]);
                bensMoveisBloqueio.setUnidadeOrcamentaria((String) obj[3]);
                bensMoveisBloqueio.setRegistroPatrimonial((String) obj[4]);
                bensMoveisBloqueio.setDescricao((String) obj[5]);
                bensMoveisBloqueio.setSituacao(SituacaoEventoBem.valueOf((String) obj[6]).getDescricao());
                bensMoveisBloqueio.setBloqueadoPor(OperacaoMovimentacaoBem.valueOf((String) obj[7]).getDescricao());
                bensMoveisBloqueio.setDataBloqueio((Date) obj[8]);
                String bloqueioUm = ((BigDecimal) obj[9]).intValue() == 1 ? "Bloqueado" : "Liberado";
                String bloqueioDois = ((BigDecimal) obj[10]).intValue() == 1 ? "Bloqueado" : "Liberado";
                String bloqueioTres = ((BigDecimal) obj[11]).intValue() == 1 ? "Bloqueado" : "Liberado";
                bensMoveisBloqueio.setMovimentoTipoUm(bloqueioUm);
                bensMoveisBloqueio.setMovimentoTipoDois(bloqueioDois);
                bensMoveisBloqueio.setMovimentoTipoTres(bloqueioTres);
                bensMoveisBloqueio.setIdBem(((BigDecimal) obj[12]).longValue());
                retorno.add(bensMoveisBloqueio);
            }
        }
        return retorno;
    }

    public Boolean verificarBemBloqueado(ConfigMovimentacaoBem configuracao, FiltroPesquisaBem filtroPesquisaBem) {
        try {
            String sql = " select bem.* from bem " +
                "          inner join movimentobloqueiobem mov on mov.bem_id = bem.id " +
                "          where bem.id = :idBem " +
                "          and mov.datamovimento = (select max(mov2.datamovimento) from movimentobloqueiobem mov2 where  bem.id = mov2.bem_id) ";
            sql += FiltroPesquisaBem.getCondicaoBloqueioPesquisaBem(configuracao);
            Query q = em.createNativeQuery(sql);
            q.setParameter("idBem", filtroPesquisaBem.getBem().getId());
            FiltroPesquisaBem.adicionarParametrosConfigMovimentacaoBem(q, configuracao);
            return q.getResultList() != null && !q.getResultList().isEmpty();
        } catch (Exception ex) {
            throw new ValidacaoException(ex.getMessage());
        }
    }
}

