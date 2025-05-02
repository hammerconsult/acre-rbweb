package br.com.webpublico.negocios;

import br.com.webpublico.entidades.FinalidadePagamento;
import br.com.webpublico.enums.SituacaoCadastralContabil;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 16/05/14
 * Time: 11:51
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class FinalidadePagamentoFacade extends AbstractFacade<FinalidadePagamento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FinalidadePagamentoFacade() {
        super(FinalidadePagamento.class);
    }


    @Override
    public void salvarNovo(FinalidadePagamento entity) {
        entity.setCodigo(recuperarUltimoCodigo());
        super.salvarNovo(entity);
    }

    private String recuperarUltimoCodigo() {
        Query consulta = em.createNativeQuery("SELECT max(cast(OBJ.codigo as number))+1 FROM FinalidadePagamento obj");
        BigDecimal resultado = (BigDecimal) consulta.getSingleResult();
        if (resultado != null) {
            return String.valueOf(resultado.intValue());
        } else {
            return "1";
        }
    }

    public List<FinalidadePagamento> completaFinalidadesPorSituacao(String parte, SituacaoCadastralContabil situacaoCadastralContabil) {
        String sql = "select f.* from finalidadepagamento f" +
                " where f.situacao = :situacao" +
                " and(f.codigo like :codigo " +
                " or f.codigoob like :codigo" +
                " or lower(f.descricao) like :descricao)" +
                " order by to_number(f.codigo) asc";
        Query consulta = em.createNativeQuery(sql, FinalidadePagamento.class);
        consulta.setParameter("codigo", "%" + parte.trim() + "%");
        consulta.setParameter("descricao", "%" + parte.toLowerCase().trim() + "%");
        consulta.setParameter("situacao", situacaoCadastralContabil.name());
        return (ArrayList<FinalidadePagamento>) consulta.getResultList();
    }

}
