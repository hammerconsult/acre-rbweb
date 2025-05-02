package br.com.webpublico.singletons;

import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;
import com.google.common.collect.Maps;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by hudson on 15/09/15.
 */

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class SingletonAdministrativo implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Map<UnidadeOrganizacional, List<UsuarioSistema>> gestoresProtocoloPorUnidadeOrganizacional = Maps.newHashMap();

    @Lock(LockType.READ)
    public Integer gerarNumeroInscricaoSicaRb() {
        String sql = "select COALESCE (max(f.inscricaonosicarb), 0) from fornecedor f";
        Query q = em.createNativeQuery(sql);

        return ((BigDecimal) q.getSingleResult()).intValue() + 1;
    }

    @Lock(LockType.READ)
    public Integer gerarNumeroCertificado() {
        String sql = "select COALESCE (max(f.certificado), 0) from fornecedor f";
        Query q = em.createNativeQuery(sql);

        return ((BigDecimal) q.getSingleResult()).intValue() + 1;
    }

    public List<UsuarioSistema> getGestoresProtocoloPorUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        return gestoresProtocoloPorUnidadeOrganizacional.get(unidadeOrganizacional);
    }

    public void addGestoresProtocoloPorUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional, List<UsuarioSistema> usuarios) {
        gestoresProtocoloPorUnidadeOrganizacional.put(unidadeOrganizacional, usuarios);
    }

    public void clearGestoresProtocoloPorUnidadeOrganizacional() {
        gestoresProtocoloPorUnidadeOrganizacional = Maps.newHashMap();
    }
}
