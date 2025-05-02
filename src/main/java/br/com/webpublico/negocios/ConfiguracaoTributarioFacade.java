/*
 * Codigo gerado automaticamente em Tue Nov 08 14:43:58 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.bi.TipoExportacaoArquivoBI;
import br.com.webpublico.singletons.CacheTributario;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ConfiguracaoTributarioFacade extends AbstractFacade<ConfiguracaoTributario> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CidadeFacade cidadeFacade;
    @EJB
    private UFFacade estadoFacade;
    @EJB
    private TributoFacade tributoFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private LotacaoVistoriadoraFacade lotacaoVistoriadoraFacade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private MultaFiscalizacaoFacade multaFiscalizacaoFacade;
    @EJB
    private EventoCalculoFacade eventoCalculoFacade;
    @EJB
    private IrregularidadeFacade irregularidadeFacade;
    @EJB
    private AtributoFacade atributoFacade;
    @EJB
    private CacheTributario cacheTributario;

    public ConfiguracaoTributarioFacade() {
        super(ConfiguracaoTributario.class);
    }

    public CidadeFacade getCidadeFacade() {
        return cidadeFacade;
    }

    public UFFacade getEstadoFacade() {
        return estadoFacade;
    }

    public TributoFacade getTributoFacade() {
        return tributoFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public LotacaoVistoriadoraFacade getLotacaoVistoriadoraFacade() {
        return lotacaoVistoriadoraFacade;
    }

    public TipoDoctoOficialFacade getTipoDoctoOficialFacade() {
        return tipoDoctoOficialFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public MultaFiscalizacaoFacade getMultaFiscalizacaoFacade() {
        return multaFiscalizacaoFacade;
    }

    public EventoCalculoFacade getEventoCalculoFacade() {
        return eventoCalculoFacade;
    }

    public IrregularidadeFacade getIrregularidadeFacade() {
        return irregularidadeFacade;
    }

    public AtributoFacade getAtributoFacade() {
        return atributoFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<ConfiguracaoTributario> lista() {
        List<ConfiguracaoTributario> lista = new ArrayList<ConfiguracaoTributario>();
        String hql = "from ConfiguracaoTributario c where c.vigencia = (select max(a.vigencia) from ConfiguracaoTributario a)";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        if (q.getResultList().size() > 0) {
            lista = q.getResultList();
        }
        return lista;
    }

    public ConfiguracaoTributario retornaUltimo() {
        if (cacheTributario != null && cacheTributario.getConfiguracaoTributario() == null) {
            String hql = "from ConfiguracaoTributario c where c.vigencia = (select max(a.vigencia) from ConfiguracaoTributario a)";
            Query q = em.createQuery(hql);
            q.setMaxResults(1);
            if (!q.getResultList().isEmpty()) {
                ConfiguracaoTributario configuracaoTributario = (ConfiguracaoTributario) q.getSingleResult();
                inicializarListas(configuracaoTributario);
                cacheTributario.setConfiguracaoTributario(configuracaoTributario);
            }
        }
        return cacheTributario.getConfiguracaoTributario();
    }

    public int tamanhoInscricaoCadastralCompleta() {
        int retorno = 0;
        ConfiguracaoTributario cfg = retornaUltimo();
        if (cfg != null) {
            if (cfg.getNumDigitosDistrito() != null) {
                retorno += cfg.getNumDigitosDistrito().intValue();
            }
            if (cfg.getNumDigitosLote() != null) {
                retorno += cfg.getNumDigitosLote().intValue();
            }
            if (cfg.getNumDigitosQuadra() != null) {
                retorno += cfg.getNumDigitosQuadra().intValue();
            }
            if (cfg.getNumDigitosSetor() != null) {
                retorno += cfg.getNumDigitosSetor().intValue();
            }
            if (cfg.getNumDigitosUnidade() != null) {
                retorno += cfg.getNumDigitosUnidade().intValue();
            }
        }
        return retorno;
    }

    public int tamanhoInscricaoCadastralSemUnidade() {
        int retorno = 0;
        ConfiguracaoTributario cfg = retornaUltimo();
        if (cfg != null) {
            if (cfg.getNumDigitosDistrito() != null) {
                retorno += cfg.getNumDigitosDistrito().intValue();
            }
            if (cfg.getNumDigitosLote() != null) {
                retorno += cfg.getNumDigitosLote().intValue();
            }
            if (cfg.getNumDigitosQuadra() != null) {
                retorno += cfg.getNumDigitosQuadra().intValue();
            }
            if (cfg.getNumDigitosSetor() != null) {
                retorno += cfg.getNumDigitosSetor().intValue();
            }
        }
        return retorno;
    }

    public boolean permiteEmitirCarneIPTUPortal() {
        String sql = " select ct.canemitircarneiptuportal from configuracaotributario ct " +
            " order by ct.vigencia desc fetch first 1 rows only ";

        Query q = em.createNativeQuery(sql);
        List<BigDecimal> permissoes = q.getResultList();

        if (permissoes != null && !permissoes.isEmpty()) {
            return permissoes.get(0).intValue() == 1;
        }
        return false;
    }

    public String recuperarUrlPortal() {
        ConfiguracaoTributario configuracaoTributario = retornaUltimo();
        String urlPortal = "";
        if (configuracaoTributario != null && !Strings.isNullOrEmpty(configuracaoTributario.getUrlPortalContribuinte()) &&
            configuracaoTributario.getUrlPortalContribuinte().endsWith("/")) {
            urlPortal = StringUtils.chop(configuracaoTributario.getUrlPortalContribuinte());
        }
        return urlPortal;
    }

    @Override
    public ConfiguracaoTributario recuperar(Object id) {
        ConfiguracaoTributario c = em.find(ConfiguracaoTributario.class, id);
        inicializarListas(c);
        return c;
    }

    private static void inicializarListas(ConfiguracaoTributario c) {
        Hibernate.initialize(c.getBancosArrecadacao());
        Hibernate.initialize(c.getEventosBCI());
        Hibernate.initialize(c.getIrregularidadesDoAlvara());
        Hibernate.initialize(c.getItemConfiguracaoWebService());
        Hibernate.initialize(c.getConfiguracaoAlvara());
        Hibernate.initialize(c.getParametrosValorAlvaraAmbiental());
        Hibernate.initialize(c.getRendasLotacoesVistoriadoras());
        if (c.getConfiguracaoAlvara() == null) {
            c.setConfiguracaoAlvara(new ArrayList<ConfiguracaoAlvara>());
        }
        if (c.getConfiguracaoPatrimonioLote() == null) {
            c.setConfiguracaoPatrimonioLote(new ConfiguracaoPatrimonioLote());
            c.getConfiguracaoPatrimonioLote().setConfiguracaoTributario(c);
        }
        Hibernate.initialize(c.getConfiguracaoPatrimonioLote().getItensConfiguracaoPatrimonioLote());
        if (c.getCertificadoNfse() != null) {
            Hibernate.initialize(c.getCertificadoNfse().getArquivosComposicao());
        }
    }

    public String recuperarRodapeEmailPortal(boolean isRodapeRbTrans) {
        String sql = " select " + (isRodapeRbTrans ? "rodaperbtransemail" : "rodapepadraoemail") + " from configuracaotributario ct " +
            " order by ct.vigencia desc fetch  first 1 rows only ";

        Query q = em.createNativeQuery(sql);
        List<String> rodapes = q.getResultList();
        return (rodapes != null && !rodapes.isEmpty()) ? rodapes.get(0) : "";
    }

    public void limparCache() {
        cacheTributario.setConfiguracaoTributario(null);
    }

    public List<ConfiguracaoTributarioBIDivida> buscarTodasDividasTipoExportacaoBI(ConfiguracaoTributarioBI configuracaoTributarioBI) {
        return em.createQuery("from ConfiguracaoTributarioBIDivida cd " +
            " where cd.configuracaoTributarioBI = :configuracaoTributarioBI ")
            .setParameter("configuracaoTributarioBI", configuracaoTributarioBI)
            .getResultList();
    }

    public List<Divida> buscarDividasTipoExportacaoBI(ConfiguracaoTributarioBI configuracaoTributarioBI,
                                                      TipoExportacaoArquivoBI tipoExportacaoArquivoBI) {
        return em.createQuery("select cd.divida from ConfiguracaoTributarioBIDivida cd " +
                " where cd.configuracaoTributarioBI = :configuracaoTributarioBI " +
                "   and cd.tipoExportacaoArquivoBI = :tipoExportacaoArquivoBI ")
            .setParameter("configuracaoTributarioBI", configuracaoTributarioBI)
            .setParameter("tipoExportacaoArquivoBI", tipoExportacaoArquivoBI)
            .getResultList();
    }

    public void salvar(ConfiguracaoTributario configuracaoTributario, List<ConfiguracaoTributarioBIDivida> dividasPorTipoExportacao) {
        configuracaoTributario = salvarRetornando(configuracaoTributario);
        if (dividasPorTipoExportacao != null) {
            salvarDividasPorTipoExportacaoBI(configuracaoTributario.getConfiguracaoTributarioBI(), dividasPorTipoExportacao);
        }
    }

    private void salvarDividasPorTipoExportacaoBI(ConfiguracaoTributarioBI configuracaoTributarioBI,
                                                  List<ConfiguracaoTributarioBIDivida> dividasPorTipoExportacao) {
        for (ConfiguracaoTributarioBIDivida configuracaoTributarioBIDivida : dividasPorTipoExportacao) {
            if (configuracaoTributarioBIDivida.getExcluido()) {
                if (configuracaoTributarioBIDivida.getId() != null) {
                    em.remove(em.find(ConfiguracaoTributarioBIDivida.class, configuracaoTributarioBIDivida.getId()));
                }
            } else {
                configuracaoTributarioBIDivida.setConfiguracaoTributarioBI(configuracaoTributarioBI);
                em.merge(configuracaoTributarioBIDivida);
            }
        }
    }
}
