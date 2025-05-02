package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidadesauxiliares.BeneficioServidorSiprev;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import liquibase.util.SqlUtil;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 06/11/14
 * Time: 08:59
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class BeneficioServidorFacade extends AbstractFacade<BeneficioServidorSiprev> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public BeneficioServidorFacade() {
        super(BeneficioServidorSiprev.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<BeneficioServidorSiprev> recuperarBeneficios(int mes, int ano, Date dataOperacao) {
        String sql = " SELECT DEPV.INICIOVIGENCIA AS DTINICIOBENEFICIO, DEPV.FINALVIGENCIA AS DTFIMBENEFICIO," +
                " ITEM.VALOR as vlAtualBeneficio, cargo.descricao, depv.id as dependenteVinculoFPID, cargo.id as idCargo," +
                " contrato.id as idContrato, pessoa.id as idServidor" +
                " FROM ITEMFICHAFINANCEIRAFP ITEM " +
                " inner join FICHAFINANCEIRAFP FICHA ON ITEM.FICHAFINANCEIRAFP_ID = FICHA.ID " +
                " inner join FOLHADEPAGAMENTO folha on FOLHA.ID = FICHA.FOLHADEPAGAMENTO_ID " +
                " inner join EVENTOFP EVENTO ON ITEM.EVENTOFP_ID = EVENTO.ID " +
                " inner join VINCULOFP vinculo ON FICHA.VINCULOFP_ID = vinculo.ID " +
                " inner join contratofp contrato on contrato.id = vinculo.id " +
                " inner join cargo on contrato.cargo_id = cargo.id " +
                " inner join matriculafp mat on mat.id = vinculo.matriculafp_id  " +
                " inner join pessoafisica pessoa on pessoa.id = mat.pessoa_id " +
                " inner join dependente dep on dep.responsavel_id = pessoa.id " +
                " inner join dependentevinculofp depv on depv.dependente_id = dep.id " +
                " inner join TIPODEPENDENTE TIPO ON TIPO.ID = DEPV.TIPODEPENDENTE_ID " +
                " WHERE EVENTO.CODIGO = :codigoEvento AND TIPO.CODIGO IN (1,9) and folha.ano = :ano and FOLHA.MES = :mes " +
                " and :data between depv.iniciovigencia and coalesce(depv.finalvigencia,sysdate)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("mes", mes - 1);
        q.setParameter("ano", ano);
        q.setParameter("data", DataUtil.dataSemHorario(dataOperacao));
        q.setParameter("codigoEvento", 149);
        return listaDeBeneficioSiprev(q.getResultList());
    }

    private List<BeneficioServidorSiprev> listaDeBeneficioSiprev(List<Object[]> resultado) {
        List<BeneficioServidorSiprev> retorno = Lists.newArrayList();
        for (Object[] obj : resultado) {
            BeneficioServidorSiprev beneficio = new BeneficioServidorSiprev();
            beneficio.setDtInicioBeneficio((Date) obj[0]);
            beneficio.setDtFimBeneficio((Date) obj[1]);
            beneficio.setVlAtualBeneficio((BigDecimal) obj[2]);
            beneficio.setCargo((String) obj[3]);
            beneficio.setDependenteVinculoFPID((BigDecimal) obj[4]);
            beneficio.setIdCargo((BigDecimal) obj[5]);
            beneficio.setIdContratoFP((BigDecimal) obj[6]);
            beneficio.setIdServidor((BigDecimal) obj[7]);
            retorno.add(beneficio);
        }
        return retorno;
    }


    //Pegando a primeira data do inicio do beneficio salario familia
    public Date recuperaDataDeInicioDoBeneficio(BigDecimal idDependenreVinculo) {
        String sql = " SELECT MIN(INICIOVIGENCIA) FROM DEPENDENTEVINCULOFP DEPV " +
                "        INNER JOIN TIPODEPENDENTE TIPO ON TIPO.ID = DEPV.TIPODEPENDENTE_ID " +
                "        where depv.id = :idDependente AND TIPO.CODIGO IN (1,9) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDependente", idDependenreVinculo);
        if (!q.getResultList().isEmpty()) {
            return (Date) q.getResultList().get(0);
        }
        return null;
    }


    //Pegando a ultima data da atualizacao  do beneficio salario familia
    public Date recuperaDataAtualizacaoDoBeneficio(BigDecimal idDependenreVinculo) {
        String sql = " SELECT MAX(INICIOVIGENCIA) FROM DEPENDENTEVINCULOFP DEPV " +
                "        INNER JOIN TIPODEPENDENTE TIPO ON TIPO.ID = DEPV.TIPODEPENDENTE_ID " +
                "        where depv.id = :idDependente AND TIPO.CODIGO IN (1,9) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDependente", idDependenreVinculo);
        if (!q.getResultList().isEmpty()) {
            return (Date) q.getResultList().get(0);
        }
        return null;
    }

    public String recuperaDescricaoDoCargo(BigDecimal idCargo) {
        String sql = "select descricao from cargo where id = :idCargo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCargo", idCargo);
        if (!q.getResultList().isEmpty()) {
            return (String) q.getResultList().get(0);
        }
        return "";
    }

    public String recuperaCarreiraBeneficioServidor(BigDecimal idContratoFP, Date dataOperacao) {
        String sql = " SELECT distinct PCS.DESCRICAO FROM ENQUADRAMENTOFUNCIONAL ENQ " +
                " INNER JOIN CATEGORIAPCS CATEGORIA ON ENQ.CATEGORIAPCS_ID = CATEGORIA.ID " +
                " INNER JOIN PLANOCARGOSSALARIOS PCS ON PCS.ID = CATEGORIA.PLANOCARGOSSALARIOS_ID " +
                " WHERE ENQ.CONTRATOSERVIDOR_ID = :idContratoFP " +
                " and :dataOperacao between PCS.INICIOVIGENCIA and coalesce(PCS.FINALVIGENCIA,sysdate)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContratoFP", idContratoFP);
        q.setParameter("dataOperacao", DataUtil.dataSemHorario(dataOperacao));
        if (!q.getResultList().isEmpty()) {
            return (String) q.getResultList().get(0);
        }
        return "";
    }

//    public ContratoFP recuperarContrato() {
//        String hqlO = "";
//    }
}
