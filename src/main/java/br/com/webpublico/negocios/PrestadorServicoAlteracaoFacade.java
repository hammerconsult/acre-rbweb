/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.PrestadorServicoAlteracao;
import br.com.webpublico.entidades.PrestadorServicos;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.ItemConfiguracaoEmpregadorESocial;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.esocial.service.S2300Service;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * @author peixe
 */
@Stateless
public class PrestadorServicoAlteracaoFacade extends AbstractFacade<PrestadorServicoAlteracao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    private S2300Service s2300Service;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PrestadorServicoAlteracaoFacade() {
        super(PrestadorServicoAlteracao.class);
    }

    public List<PrestadorServicos> buscarPrestadorServicoAlteracaoPorEmpregador(ConfiguracaoEmpregadorESocial empregadorESocial,
                                                                                Boolean somenteNaoEnviados, PrestadorServicos prestador,
                                                                                Date inicio, Date fim) {
        System.out.println("prestador " + prestador);
        String sql = " select distinct prestadorservicos.* from PRESTADORSERVICOALTERACAO alteracao " +
            " inner join prestadorservicos on alteracao.PRESTADORSERVICO_ID = prestadorservicos.ID  " +
            " where prestadorservicos.lotacao_id in (:unidades)";
        if (prestador != null) {
            sql += " and prestadorservicos.id = :idPrestador ";
        }
        if (inicio != null) {
            sql += " and prestadorservicos.INICIOVIGENCIACONTRATO >= :inicioContrato ";
        }
        if (fim != null) {
            sql += " and prestadorservicos.FINALVIGENCIACONTRATO <= :fimContrato ";
        }
        if (somenteNaoEnviados) {

            sql += " and not exists (" +
                "    select * from REGISTROESOCIAL registro" +
                "    where registro.IDESOCIAL = prestadorservicos.ID and" +
                "          registro.TIPOARQUIVOESOCIAL = :tipoArquivoESocial " +
                " and registro.SITUACAO in (:situacoes) " +
                " and registro.EMPREGADOR_ID = :config )";
        }
        Query q = em.createNativeQuery(sql, PrestadorServicos.class);
        q.setParameter("unidades", montarIdOrgaoEmpregador(empregadorESocial));
        if (prestador != null) {
            q.setParameter("idPrestador", prestador.getId());
        }
        if (inicio != null) {
            q.setParameter("inicioContrato", inicio);
        }
        if (fim != null) {
            q.setParameter("fimContrato", fim);
        }
        if (somenteNaoEnviados) {
            q.setParameter("situacoes", Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
            q.setParameter("tipoArquivoESocial", TipoArquivoESocial.S2306.name());
            q.setParameter("config", empregadorESocial.getId());
        }

        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }

    private List<Long> montarIdOrgaoEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        List<Long> ids = Lists.newArrayList();
        for (ItemConfiguracaoEmpregadorESocial item : empregador.getItemConfiguracaoEmpregadorESocial()) {
            ids.add(item.getUnidadeOrganizacional().getId());
        }
        return ids;
    }
}
