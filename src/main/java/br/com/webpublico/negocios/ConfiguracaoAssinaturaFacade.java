package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class ConfiguracaoAssinaturaFacade extends AbstractFacade<ConfiguracaoAssinatura> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    public ConfiguracaoAssinaturaFacade() {
        super(ConfiguracaoAssinatura.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ConfiguracaoAssinatura recuperar(Object id) {
        ConfiguracaoAssinatura entity = super.recuperar(id);
        Hibernate.initialize(entity.getUnidades());
        return entity;
    }

    public AssinaturaDocumentoOficial salvarNotificacaoAssinatura(AssinaturaDocumentoOficial assinaturaDocumentoOficial) {
        return em.merge(assinaturaDocumentoOficial);
    }

    public boolean hasConfiguracaoVigenteMesmoModuloEUnidade(ConfiguracaoAssinatura configuracaoAssinatura, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = " select ca.* " +
            " from CONFIGURACAOASSINATURA ca " +
            "    inner join CONFIGASSINATURAUNIDADE cau on cau.CONFIGURACAOASSINATURA_ID = ca.ID " +
            " where cau.UNIDADEORGANIZACIONAL_ID = :idUnidade " +
            "  and ca.MODULO = :modulo " +
            "  and ca.PESSOA_ID <> :idPessoa" +
            "  and to_date(:dataReferencia, 'dd/MM/yyyy') between ca.INICIOVIGENCIA and coalesce(ca.FIMVIGENCIA, to_date(:dataReferencia, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(configuracaoAssinatura.getInicioVigencia()));
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        q.setParameter("modulo", configuracaoAssinatura.getModulo().name());
        q.setParameter("idPessoa", configuracaoAssinatura.getPessoa().getId());
        return !q.getResultList().isEmpty();
    }

    public List<AssinaturaDocumentoOficial> buscarAssinaturaPorDoctoOficial(DocumentoOficial documentoOficial) {
        String sql = " select assina.* " +
            " from AssinaturaDocumentoOficial assina " +
            " where assina.documentoOficial_id = :idDocto ";
        Query q = em.createNativeQuery(sql, AssinaturaDocumentoOficial.class);
        q.setParameter("idDocto", documentoOficial.getId());
        try {
            return q.getResultList();
        } catch (NoResultException nr) {
            return Lists.newArrayList();
        }
    }

    public List<ConfiguracaoAssinatura> buscarConfiguracaoVigentePorModuloEUnidade(ModuloTipoDoctoOficial modulo, Long idUnidadeOrganizacional, Date dataReferencia) {
        String sql = " select ca.* " +
            " from configuracaoAssinatura ca " +
            "   inner join CONFIGASSINATURAUNIDADE cau on cau.configuracaoAssinatura_id = ca.id " +
            " where ca.modulo = :modulo " +
            "   and cau.unidadeOrganizacional_id = :idUnidadeOrganizacional " +
            "   and to_date(:dataReferencia, 'dd/MM/yyyy') between ca.iniciovigencia and coalesce(ca.fimvigencia, to_date(:dataReferencia, 'dd/MM/yyyy'))";
        Query q = em.createNativeQuery(sql, ConfiguracaoAssinatura.class);
        q.setParameter("modulo", modulo.name());
        q.setParameter("idUnidadeOrganizacional", idUnidadeOrganizacional);
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(dataReferencia));
        try {
            return q.getResultList();
        } catch (NoResultException nr) {
            return Lists.newArrayList();
        }
    }

    public AssinaturaDocumentoOficial buscarAssinaturaPorDoctoOficialUsuario(DocumentoOficial documentoOficial, UsuarioSistema usuarioSistema) {
        String sql = " select assina.* " +
            " from assinaturaDocumentoOficial assina " +
            " where assina.usuariosistema_id = :usuario " +
            " and assina.documentoOficial_id = :documento" +
            " order by assina.id";
        Query q = em.createNativeQuery(sql, AssinaturaDocumentoOficial.class);
        q.setParameter("documento", documentoOficial.getId());
        q.setParameter("usuario", usuarioSistema.getId());
        q.setMaxResults(1);
        try {
            List<AssinaturaDocumentoOficial> resultado = q.getResultList();
            if (!resultado.isEmpty()) {
                AssinaturaDocumentoOficial ado = resultado.get(0);
                Hibernate.initialize(ado.getUsuarioSistema().getAssinaturas());
                if (ado.getUsuarioSistema().getAssinaturas().get(0).getDetentorArquivoComposicao().getArquivosComposicao() != null &&
                    !ado.getUsuarioSistema().getAssinaturas().get(0).getDetentorArquivoComposicao().getArquivosComposicao().isEmpty()) {
                    Hibernate.initialize(ado.getUsuarioSistema().getAssinaturas().get(0).getDetentorArquivoComposicao().getArquivosComposicao());
                }
                return ado;
            }
            return null;
        } catch (NoResultException nr) {
            return null;
        }
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
