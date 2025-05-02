/*
 * Codigo gerado automaticamente em Fri Feb 11 09:06:37 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class LocalizacaoFacade extends AbstractFacade<Localizacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private AtividadePontoFacade atividadePontoFacade;
    @EJB
    private PontoComercialFacade pontoComercialFacade;
    @EJB
    private TipoPontoComercialFacade tipoPontoComercialFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    ContratoRendasPatrimoniaisFacade contratoRendasPatrimoniaisFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private LotacaoVistoriadoraFacade lotacaoVistoriadoraFacade;
    @EJB
    private IndiceEconomicoFacade indiceEconomicoFacade;
    @EJB
    private ParametroDescontoRendaPatrimonialFacade parametroDescontoRendaPatrimonialFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LocalizacaoFacade() {
        super(Localizacao.class);
    }

    public AtividadePontoFacade getAtividadePontoFacade() {
        return atividadePontoFacade;
    }

    public PontoComercialFacade getPontoComercialFacade() {
        return pontoComercialFacade;
    }

    public TipoPontoComercialFacade getTipoPontoComercialFacade() {
        return tipoPontoComercialFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public BairroFacade getBairroFacade() {
        return bairroFacade;
    }

    public LogradouroFacade getLogradouroFacade() {
        return logradouroFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ContratoRendasPatrimoniaisFacade getContratoRendasPatrimoniaisFacade() {
        return contratoRendasPatrimoniaisFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public LotacaoVistoriadoraFacade getLotacaoVistoriadoraFacade() {
        return lotacaoVistoriadoraFacade;
    }

    public IndiceEconomicoFacade getIndiceEconomicoFacade() {
        return indiceEconomicoFacade;
    }

    public ParametroDescontoRendaPatrimonialFacade getParametroDescontoRendaPatrimonialFacade() {
        return parametroDescontoRendaPatrimonialFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    @Override
    public Localizacao recuperar(Object id) {
        Localizacao l = em.find(Localizacao.class, id);
        l.getPontosComerciais().size();
        return l;
    }

    public boolean jaExiste(Localizacao selecionado) {
        if (selecionado == null || selecionado.getCodigo() == null) {
            return false;
        }

        if (selecionado.getLotacaoVistoriadora() == null || selecionado.getLotacaoVistoriadora().getId() == null) {
            return false;
        }

        String hql = "from Localizacao l where l.codigo = :codigo and l.lotacaoVistoriadora = :lotacao";
        if (selecionado.getId() != null) {
            hql += " and l <> :parametro";
        }
        Query q = em.createQuery(hql);
        q.setParameter("codigo", selecionado.getCodigo());
        q.setParameter("lotacao", selecionado.getLotacaoVistoriadora());
        if (selecionado.getId() != null) {
            q.setParameter("parametro", selecionado);
        }

        return !q.getResultList().isEmpty();
    }

    public boolean jaExisteCodigo(Localizacao selecionado) {
        if (selecionado == null || selecionado.getCodigo() == null) {
            return false;
        }

        String hql = "from Localizacao l where l.codigo = :codigo ";
        if (selecionado.getId() != null) {
            hql += " and l <> :parametro";
        }
        Query q = em.createQuery(hql);
        q.setParameter("codigo", selecionado.getCodigo());
        if (selecionado.getId() != null) {
            q.setParameter("parametro", selecionado);
        }

        return !q.getResultList().isEmpty();
    }

    public List<Localizacao> listaFiltrandoPorLotacao(LotacaoVistoriadora lotacaoVistoriadora, String s, String... atributos) {
        StringBuilder hql = new StringBuilder("from Localizacao obj where obj.lotacaoVistoriadora.id =:lotacao and ( ");
        for (String atributo : atributos) {
            hql.append("lower(obj.").append(atributo).append(") like :filtro OR ");
        }
        hql = new StringBuilder(hql.substring(0, hql.length() - 3) + ")");
        hql.append(" order by obj.descricao");
        Query q = em.createQuery(hql.toString());
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("lotacao", lotacaoVistoriadora.getId());
        return q.getResultList();
    }

    public List<Localizacao> listaLotacaoPorUsuarioCorrente(String s, String... atributos) {

        List<LotacaoVistoriadora> lotacaoVistoriadoras = lotacaoVistoriadoraFacade.listaPorUsuarioSistemaVigente(sistemaFacade.getUsuarioCorrente(), sistemaFacade.getDataOperacao());

        String hql = "from Localizacao obj where ( ";
        for (String atributo : atributos) {
            hql += "lower(obj." + atributo + ") like :filtro OR ";
        }
        hql = hql.substring(0, hql.length() - 3) + ")";
        if (lotacaoVistoriadoras != null && !lotacaoVistoriadoras.isEmpty()) {
            hql += " and obj.lotacaoVistoriadora  in :lotacao ";
        }
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        if (lotacaoVistoriadoras != null && !lotacaoVistoriadoras.isEmpty()) {
            q.setParameter("lotacao", lotacaoVistoriadoras);
        }
        return lotacaoVistoriadoras.isEmpty() ? null : q.getResultList();
    }

    public void carregarDisponibilidadePontosComerciais(Localizacao localizacao, String RENDAS_PATRIMONIAIS) {
        for (PontoComercial pontoComercial : localizacao.getPontosComerciais()) {
            if (pontoComercial.getId() != null) {
                if (localizacao.getLotacaoVistoriadora().getDescricao().equals(RENDAS_PATRIMONIAIS)) {
                    pontoComercial.setDisponivel(!getContratoRendasPatrimoniaisFacade().hasPontosEmOutroContratoVigenteRendasPatrimoniais(pontoComercial, null));
                } else {
                    pontoComercial.setDisponivel(!getContratoRendasPatrimoniaisFacade().existePontosEmOutroContratoVigenteCEASA(pontoComercial));
                }
            } else {
                pontoComercial.setDisponivel(true);
            }
        }
    }

    public List<Localizacao> buscarLocalizacoesRendasPatrimoniaisComAoMenosUmPontoAtivo() {
        String sql = " select l.* " +
            " from localizacao l " +
            " where exists(select ponto.ID from PONTOCOMERCIAL ponto where coalesce(ATIVO, 0) = 1 and ponto.LOCALIZACAO_ID = l.ID) " +
            "  and l.LOTACAOVISTORIADORA_ID in (:idLotacaoVistoriadora) ";

        Query q = getEntityManager().createNativeQuery(sql, Localizacao.class);
        ConfiguracaoTributario configuracao = configuracaoTributarioFacade.retornaUltimo();
        q.setParameter("idLotacaoVistoriadora", configuracao.getRendasLotacoesVistoriadoras().stream().map(LotacaoVistoriadoraConfigTribRendas::getId).collect(Collectors.toList()));
        return q.getResultList();
    }
}
