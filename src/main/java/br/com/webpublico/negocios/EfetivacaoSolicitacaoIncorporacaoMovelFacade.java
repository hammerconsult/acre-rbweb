package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoPatrimonio;
import br.com.webpublico.util.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by Desenvolvimento on 03/02/2016.
 */
@Stateless
public class EfetivacaoSolicitacaoIncorporacaoMovelFacade extends AbstractFacade<EfetivacaoSolicitacaoIncorporacaoMovel> {


    private static Logger logger = LoggerFactory.getLogger(EfetivacaoSolicitacaoIncorporacaoMovelFacade.class);
    @EJB
    private BemFacade bemFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private SingletonGeradorCodigoPatrimonio singletonGeradorCodigoPatrimonio;
    @EJB
    private IntegradorPatrimonialContabilFacade integradorPatrimonialContabilFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SolicitacaoIncorporacaoMovelFacade solicitacaoIncorporacaoMovelFacade;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private InventarioBensMoveisFacade inventarioBensMoveisFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public EfetivacaoSolicitacaoIncorporacaoMovelFacade() {
        super(EfetivacaoSolicitacaoIncorporacaoMovel.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    @Override
    public EfetivacaoSolicitacaoIncorporacaoMovel recuperar(Object id) {
        EfetivacaoSolicitacaoIncorporacaoMovel ef = super.recuperar(id);
        ef.getItensEfetivacao().size();
        if (ef.getDetentorArquivoComposicao() != null) {
            ef.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return ef;
    }

    public void alterarSituacaoDaSolicitacaoIncorporacao(EfetivacaoSolicitacaoIncorporacaoMovel selecionado) throws Exception {
        SolicitacaoIncorporacaoMovel solicitacao = solicitacaoIncorporacaoMovelFacade.recuperar(selecionado.getSolicitacaoIncorporacao().getId());
        solicitacao.setSituacao(SituacaoEventoBem.FINALIZADO);
        em.merge(solicitacao);
    }

    public void criarItensEfetivacaoIncorporacaoMovel(EfetivacaoSolicitacaoIncorporacaoMovel entity) {
        entity.getItensEfetivacao().clear();
        Entidade entidade = entidadeFacade.recuperarEntidadePorUnidadeOrganizacional(entity.getSolicitacaoIncorporacao().getUnidadeAdministrativa());
        SolicitacaoIncorporacaoMovel solicitacao = solicitacaoIncorporacaoMovelFacade.recuperar(entity.getSolicitacaoIncorporacao().getId());
        ParametroPatrimonio parametroPatrimonio = parametroPatrimonioFacade.recuperarParametroComDependenciasEntidadeGeradoCodigo();
        if (parametroPatrimonio == null) {
            throw new ExcecaoNegocioGenerica("Parâmetro do patrimônio não encontrado para gerar a incorporação de bem.");
        }
        for (ItemSolicitacaoIncorporacaoMovel item : solicitacao.getItensSolicitacaoIncorporacaoMovel()) {
            for (int x = 0; x < item.getQuantidade().intValue(); x++) {
                Bem bem = new Bem(item);
                if (item.getSeguradora() != null) {
                    bem.setSeguradora(item.getSeguradora());
                }
                if (item.getGarantia() != null) {
                    bem.setGarantia(item.getGarantia());
                }

                EstadoBem resultante = bemFacade.criarNovoEstadoResultanteAPartirDoCaracterizador(item);
                resultante.setTipoGrupo(TipoGrupo.BEM_MOVEL_PRINCIPAL);
                ItemEfetivacaoIncorporacaoMovel incorporacaoBem = new ItemEfetivacaoIncorporacaoMovel(bem, null, resultante, entity, item);
                incorporacaoBem.setDataLancamento(sistemaFacade.getDataOperacao());
                geradorDeCodigosDoPatrimonio(incorporacaoBem, entidade, parametroPatrimonio);
                entity.getItensEfetivacao().add(incorporacaoBem);
                em.persist(bem);
                em.persist(resultante);
            }
        }
    }

    private void geradorDeCodigosDoPatrimonio(ItemEfetivacaoIncorporacaoMovel incorporacaoBem, Entidade entidade, ParametroPatrimonio parametroPatrimonio) throws ExcecaoNegocioGenerica {
        if (entidade != null) {
            String codigo = singletonGeradorCodigoPatrimonio.getProximoCodigo(entidade, TipoBem.MOVEIS, parametroPatrimonio).toString();
            incorporacaoBem.getBem().setIdentificacao(codigo);
            incorporacaoBem.getEstadoResultante().setIdentificacao(codigo);
        } else {
            throw new ExcecaoNegocioGenerica("A unidade organizacional " + incorporacaoBem.getItemSolicitacao().getUnidadeAdministrativa() + " deve estar associada a uma entidade.");
        }
    }

    public EfetivacaoSolicitacaoIncorporacaoMovel salvarIncorporacao(EfetivacaoSolicitacaoIncorporacaoMovel entity, ConfigMovimentacaoBem configuracao) throws Exception {
        try {
            singletonGeradorCodigoPatrimonio.inicializarReset();
            singletonGeradorCodigoPatrimonio.bloquearMovimento(EfetivacaoSolicitacaoIncorporacaoMovel.class);
            entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(EfetivacaoSolicitacaoIncorporacaoMovel.class, "codigo"));
            alterarSituacaoDaSolicitacaoIncorporacao(entity);
            criarItensEfetivacaoIncorporacaoMovel(entity);
            entity = em.merge(entity);
            integradorPatrimonialContabilFacade.incorporarBens(entity.getItensEfetivacao(), "Efetivação de Incorporação de bem móvel código: " + entity.getCodigo());


            for (ItemEfetivacaoIncorporacaoMovel item : entity.getItensEfetivacao()) {

                configMovimentacaoBemFacade.inserirMovimentoBloqueioInicial(item.getBem());
            }

        } catch (ExcecaoNegocioGenerica exg) {
            singletonGeradorCodigoPatrimonio.reset();
            singletonGeradorCodigoPatrimonio.desbloquear(EfetivacaoSolicitacaoIncorporacaoMovel.class);
            throw new ExcecaoNegocioGenerica(exg.getMessage());
        } catch (ValidacaoException ve) {
            throw ve;
        } catch (Exception ex) {
            singletonGeradorCodigoPatrimonio.desbloquear(EfetivacaoSolicitacaoIncorporacaoMovel.class);
            singletonGeradorCodigoPatrimonio.reset();
            throw new Exception(ex.getMessage());
        } finally {
            singletonGeradorCodigoPatrimonio.finalizarReset();
        }
        singletonGeradorCodigoPatrimonio.desbloquear(EfetivacaoSolicitacaoIncorporacaoMovel.class);
        return entity;
    }

    public List<EfetivacaoSolicitacaoIncorporacaoMovel> buscarEfetivacao(String parte) {
        String sql = "" +
            " select ef.* from efetsoliciincorpomovel ef " +
            "         inner join solicitacaoincorporacaomov sol on sol.id = ef.solicitacaoincorporacao_id " +
            "         inner join vwhierarquiaadministrativa vw on vw.subordinada_id = sol.unidadeadministrativa_id " +
            "  where sol.situacao = :finalizado " +
            "   and to_date(:dataoperacao, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:dataoperacao, 'dd/mm/yyyy')) " +
            "   and (lower(translate(vw.descricao, 'âàãááâàãéêéêííóôõóôõüúüúçç', 'aaaaaaaaeeeeiioooooouuuucc')) like translate(:parte, 'âàãááâàãéêéêííóôõóôõüúüúçç', 'aaaaaaaaeeeeiioooooouuuucc') " +
            "    or lower(vw.descricao) like :parte" +
            "    or vw.codigo like :parte " +
            "    or replace(vw.codigo, '.', '') like :parte " +
            "    or ef.codigo like :parte " +
            "    or sol.codigo like :parte  " +
            "    or lower(sol.observacao) like :parte) " +
            "  order by ef.codigo ";
        Query q = em.createNativeQuery(sql, EfetivacaoSolicitacaoIncorporacaoMovel.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("finalizado", SituacaoEventoBem.FINALIZADO.name());
        q.setParameter("dataoperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();

    }

    public List<Long> buscarIdsBens(EfetivacaoSolicitacaoIncorporacaoMovel efetivacao) {
        String sql = " select ev.bem_id from ITEMEFETIVACAOINCORPMOVEL item" +
            "          inner join eventobem ev on ev.id = item.id " +
            "          where item.efetivacao_id = :idEfetivacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEfetivacao", efetivacao.getId());
        return q.getResultList();
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public IntegradorPatrimonialContabilFacade getIntegradorPatrimonialContabilFacade() {
        return integradorPatrimonialContabilFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SolicitacaoIncorporacaoMovelFacade getSolicitacaoIncorporacaoMovelFacade() {
        return solicitacaoIncorporacaoMovelFacade;
    }

    public ParametroPatrimonioFacade getParametroPatrimonioFacade() {
        return parametroPatrimonioFacade;
    }

    public InventarioBensMoveisFacade getInventarioBensMoveisFacade() {
        return inventarioBensMoveisFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }
}
