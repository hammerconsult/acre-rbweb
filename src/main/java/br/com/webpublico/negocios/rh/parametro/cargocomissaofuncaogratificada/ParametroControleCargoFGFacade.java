package br.com.webpublico.negocios.rh.parametro.cargocomissaofuncaogratificada;

import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.entidades.rh.parametro.cargocomissaofuncaogratificada.ParametroControleCargoFG;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by William on 27/03/2019.
 */
@Stateless
public class ParametroControleCargoFGFacade extends AbstractFacade<ParametroControleCargoFG> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ParametroControleCargoFGFacade() {
        super(ParametroControleCargoFG.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ParametroControleCargoFG recuperarPorEntidadeAndVigencia(String codigoHO, Date dataOperacao) {
        String sql = " select distinct parametro.* from ParametroControleCargoFG parametro " +
            "   INNER JOIN VWHIERARQUIAADMINISTRATIVA vw on vw.ENTIDADE_ID = parametro.ENTIDADE_ID " +
            "   inner join vinculofp vinculo on vinculo.UNIDADEORGANIZACIONAL_ID = vw.SUBORDINADA_ID " +
            "   where :dataOperacao between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, :dataOperacao) " +
            "   and vw.codigo like :codigoHO";
        Query q = em.createNativeQuery(sql, ParametroControleCargoFG.class);
        q.setParameter("codigoHO", codigoHO + "%");
        q.setParameter("dataOperacao", dataOperacao);
        if (!q.getResultList().isEmpty()) {
            return (ParametroControleCargoFG) q.getResultList().get(0);
        }
        return null;
    }

    public Integer recuperarQuantidadeServidoresVigentesFuncaoGratificadaPorEntidade(String codigoHO, Date dataOperacao, boolean modalidadeCoordenacao) {
        String sql = " select count(vinculo.id) from vinculofp vinculo " +
            "   INNER JOIN VWHIERARQUIAADMINISTRATIVA vw on vw.SUBORDINADA_ID = vinculo.UNIDADEORGANIZACIONAL_ID " +
            "   inner join contratofp contrato on vinculo.id = contrato.id " +
            "   inner join FUNCAOGRATIFICADA fg on fg.CONTRATOFP_ID = vinculo.id " +
            "   inner join UNIDADEORGANIZACIONAL unidade on vinculo.UNIDADEORGANIZACIONAL_ID = unidade.id " +
            "   inner join modalidadecontratofp modalidade on contrato.MODALIDADECONTRATOFP_ID = modalidade.id " +
            "   where vw.codigo like :codigoHO and :dataOperacao between vinculo.iniciovigencia and coalesce (vinculo.finalvigencia, :dataOperacao) " +
            "   and :dataOperacao between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, :dataOperacao) ";
        if(modalidadeCoordenacao){
            sql += " and modalidade.codigo = :modalidade";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigoHO", codigoHO + "%");
        q.setParameter("dataOperacao", dataOperacao);
        if(modalidadeCoordenacao){
            q.setParameter("modalidade", ModalidadeContratoFP.FUNCAO_COORDENACAO);
        }
        return Integer.parseInt(q.getResultList().get(0).toString());

    }

    public Integer recuperarQuantidadeServidoresVigentesCargoComissaoPorEntidade(String codigoHO, Date dataOperacao) {
        String sql = " select count(vinculo.id) from vinculofp vinculo " +
            "   INNER JOIN VWHIERARQUIAADMINISTRATIVA vw on vw.SUBORDINADA_ID = vinculo.UNIDADEORGANIZACIONAL_ID " +
            "   inner join contratofp contrato on vinculo.id = contrato.id " +
            "   inner join UNIDADEORGANIZACIONAL unidade on vinculo.UNIDADEORGANIZACIONAL_ID = unidade.id " +
            "   inner join modalidadecontratofp modalidade on contrato.MODALIDADECONTRATOFP_ID = modalidade.id " +
            "   where vw.codigo like :codigoHO and :dataOperacao between vinculo.iniciovigencia and coalesce (vinculo.finalvigencia, :dataOperacao) " +
            "   and :dataOperacao between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, :dataOperacao) " +
            "   and modalidade.codigo in (2, 9) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigoHO", codigoHO + "%");
        q.setParameter("dataOperacao", dataOperacao);
        return Integer.parseInt(q.getResultList().get(0).toString());

    }

    public BigDecimal recuperarRemuneracaoAcessoCargoComissaoPorEntidade(String codigoHO, Date dataOperacao) {
        String sql = "SELECT sum(enquadramentopcs.VENCIMENTOBASE) " +
            " FROM vinculofp vinculo " +
            "       INNER JOIN VWHIERARQUIAADMINISTRATIVA vw on vw.SUBORDINADA_ID = vinculo.UNIDADEORGANIZACIONAL_ID " +
            "       inner join cargoconfianca cargo on cargo.CONTRATOFP_ID = vinculo.id " +
            "       inner join EnquadramentoCC enquadramento on enquadramento.CARGOCONFIANCA_ID = cargo.id " +
            "       inner join categoriapcs categoria on enquadramento.CATEGORIAPCS_ID = categoria.id " +
            "       inner join PROGRESSAOPCS prog on enquadramento.PROGRESSAOPCS_ID = prog.id " +
            "       inner join enquadramentopcs on enquadramentopcs.CATEGORIAPCS_ID = categoria.id and enquadramento.PROGRESSAOPCS_ID = prog.id " +
            "  WHERE vw.codigo like :codigoHO " +
            "  AND :dataOperacao BETWEEN vw.iniciovigencia AND COALESCE (vw.FIMVIGENCIA, :dataOperacao)" +
            "  AND :dataOperacao BETWEEN cargo.iniciovigencia AND COALESCE (cargo.finalvigencia, :dataOperacao)" +
            "  AND :dataOperacao BETWEEN vinculo.iniciovigencia AND COALESCE (vinculo.finalvigencia, :dataOperacao)" +
            "  AND :dataOperacao BETWEEN enquadramento.iniciovigencia AND COALESCE (enquadramento.finalvigencia, :dataOperacao)" +
            "  and :dataOperacao BETWEEN enquadramentopcs.iniciovigencia AND COALESCE (enquadramentopcs.finalvigencia, :dataOperacao)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigoHO", codigoHO + "%");
        q.setParameter("dataOperacao", dataOperacao);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        }
        return null;
    }

    public BigDecimal recuperarRemuneracaFuncaoGratificadaPorEntidade(String codigoHO, Date dataOperacao) {
        String sql = " SELECT SUM(enquadramentopcs.VENCIMENTOBASE)" +
            "   FROM vinculofp vinculo  " +
            "   INNER JOIN VWHIERARQUIAADMINISTRATIVA vw ON vw.SUBORDINADA_ID = vinculo.UNIDADEORGANIZACIONAL_ID  " +
            "   INNER JOIN FUNCAOGRATIFICADA funcao ON funcao.CONTRATOFP_ID = vinculo.id  " +
            "   INNER JOIN EnquadramentoFG enquadramento ON enquadramento.FUNCAOGRATIFICADA_ID = funcao.id  " +
            "   INNER JOIN categoriapcs categoria ON enquadramento.CATEGORIAPCS_ID = categoria.id  " +
            "   INNER JOIN PROGRESSAOPCS prog ON enquadramento.PROGRESSAOPCS_ID = prog.id  " +
            "   INNER JOIN enquadramentopcs ON enquadramentopcs.CATEGORIAPCS_ID = categoria.id  " +
            "   AND enquadramento.PROGRESSAOPCS_ID  = prog.id  " +
            "   WHERE vw.codigo like :codigoHO  " +
            "   AND :dataOperacao BETWEEN vw.iniciovigencia AND COALESCE (vw.FIMVIGENCIA, :dataOperacao)  " +
            "   AND :dataOperacao BETWEEN funcao.iniciovigencia AND COALESCE (funcao.finalvigencia, :dataOperacao)  " +
            "   AND :dataOperacao BETWEEN vinculo.iniciovigencia AND COALESCE (vinculo.finalvigencia, :dataOperacao)  " +
            "   AND :dataOperacao BETWEEN enquadramento.iniciovigencia AND COALESCE (enquadramento.finalvigencia, :dataOperacao)  " +
            "   AND :dataOperacao BETWEEN enquadramentopcs.iniciovigencia AND COALESCE (enquadramentopcs.finalvigencia, :dataOperacao)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigoHO", codigoHO + "%");
        q.setParameter("dataOperacao", dataOperacao);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        }
        return null;
    }
}

