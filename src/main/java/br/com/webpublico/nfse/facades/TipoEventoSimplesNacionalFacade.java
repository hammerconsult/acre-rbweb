package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.TipoEventoSimplesNacional;
import br.com.webpublico.nfse.domain.dtos.Operador;
import br.com.webpublico.nfse.domain.dtos.ParametroQuery;
import br.com.webpublico.nfse.domain.dtos.ParametroQueryCampo;
import br.com.webpublico.nfse.util.GeradorQuery;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class TipoEventoSimplesNacionalFacade extends AbstractFacade<TipoEventoSimplesNacional> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public TipoEventoSimplesNacionalFacade() {
        super(TipoEventoSimplesNacional.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoEventoSimplesNacional buscarTipoEventoSimplesNacionalPorCodigo(Integer codigoEvento) throws Exception {
        List<ParametroQuery> parametros = Lists.newArrayList();
        parametros.add(new ParametroQuery(Lists.newArrayList(new ParametroQueryCampo("codigo", Operador.IGUAL, codigoEvento))));
        List<TipoEventoSimplesNacional> tipos = buscarTipoEventoSimplesNacional(parametros);
        if (tipos != null && !tipos.isEmpty()) {
            return tipos.get(0);
        }
        return null;
    }

    public List<TipoEventoSimplesNacional> buscarTipoEventoSimplesNacional(List<ParametroQuery> parametros) throws Exception {
        Query query = new GeradorQuery(em, TipoEventoSimplesNacional.class, " select * from tipoeventosimplesnacional ").parametros(parametros).build();
        return query.getResultList();
    }
}
