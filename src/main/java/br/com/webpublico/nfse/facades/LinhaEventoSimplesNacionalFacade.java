package br.com.webpublico.nfse.facades;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.LinhaEventoSimplesNacional;
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
public class LinhaEventoSimplesNacionalFacade extends AbstractFacade<LinhaEventoSimplesNacional> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public LinhaEventoSimplesNacionalFacade() {
        super(LinhaEventoSimplesNacional.class);
    }


    public List<LinhaEventoSimplesNacional> buscarLinhasEventoSimplesNacionalPorEmpresa(CadastroEconomico cadastroEconomico) throws Exception {
        List<ParametroQuery> parametros = Lists.newArrayList();
        parametros.add(new ParametroQuery(
            Lists.newArrayList(new ParametroQueryCampo("l.cadastroeconomico_id", Operador.IGUAL, cadastroEconomico.getId()))));
        return buscarLinhaEventoSimplesNacional(parametros);
    }

    public List<LinhaEventoSimplesNacional> buscarLinhasEventoSimplesNacionalPorEmpresaAndFiltro(Long prestadorId, String filtro) throws Exception {
        List<ParametroQuery> parametros = Lists.newArrayList();
        parametros.add(new ParametroQuery(Lists.newArrayList(new ParametroQueryCampo("l.cadastroeconomico_id", Operador.IGUAL, prestadorId))));
        parametros.add(new ParametroQuery(" or ",
            Lists.newArrayList(
                new ParametroQueryCampo("lower(trim(te.nome))", Operador.LIKE, "%" + filtro.trim().toLowerCase() + "%"),
                new ParametroQueryCampo("cast(te.codigo as varchar)", Operador.LIKE, filtro.trim().toLowerCase()))));
        return buscarLinhaEventoSimplesNacional(parametros);
    }

    public List<LinhaEventoSimplesNacional> buscarLinhaEventoSimplesNacional(List<ParametroQuery> parametros) throws Exception {
        String sql = " select l.* " +
            "   from linhaeventosimplesnacional l " +
            " inner join tipoeventosimplesnacional te on te.id = l.tipoeventosimplesnacional_id ";

        Query query = new GeradorQuery(em, LinhaEventoSimplesNacional.class, sql).parametros(parametros).build();

        return query.getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
