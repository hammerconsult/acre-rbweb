package br.com.webpublico.negocios.rh.informativo;

import br.com.webpublico.entidadesauxiliares.BarraProgressoItens;
import br.com.webpublico.entidadesauxiliares.rh.VOSituacaoContratoFP;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author octavio
 */
@Stateless
public class PesquisaSituacaoContratoFacade implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(PesquisaSituacaoContratoFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public PesquisaSituacaoContratoFacade() {
    }

    public EntityManager getEntityManager() {
        return em;
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Future<Map<String, List<VOSituacaoContratoFP>>> buscarSituacoesAssincrono(BarraProgressoItens assistente) {
        assistente.inicializa();
        String sql = " select situacaocontrato.id                                    as id, " +
            "                 mfp.matricula || '/' || vfp.numero                     as matricula,      " +
            "                 pf.nome                                                as nome,           " +
            "                 vfp.iniciovigencia                                     as iniciocontrato, " +
            "                 vfp.finalvigencia                                      as finalcontrato,  " +
            "                 sit.codigo || ' - ' || sit.descricao                   as situacao,       " +
            "                 situacaocontrato.iniciovigencia                        as iniciosituacao, " +
            "                 situacaocontrato.finalvigencia                         as finalsituacao   " +
            " from situacaocontratofp situacaocontrato                                        " +
            " inner join situacaofuncional sit on situacaocontrato.situacaofuncional_id = sit.id " +
            " inner join contratofp cfp on situacaocontrato.contratofp_id = cfp.id               " +
            " inner join vinculofp vfp on vfp.id = cfp.id                                " +
            " inner join matriculafp mfp on vfp.matriculafp_id = mfp.id                  " +
            " inner join pessoafisica pf on mfp.pessoa_id = pf.id                        " +
            " where sysdate between vfp.iniciovigencia and coalesce(vfp.finalvigencia, sysdate) " +
            " order by nome, matricula, iniciosituacao ";

        Query q = em.createNativeQuery(sql);

        List<Object[]> resultados = q.getResultList();
        Map<String, List<VOSituacaoContratoFP>> mapa = Maps.newLinkedHashMap();

        for (Object[] obj : resultados) {
            VOSituacaoContratoFP situacao = new VOSituacaoContratoFP();
            situacao.setId(((BigDecimal) obj[0]).longValue());
            situacao.setMatricula((String) obj[1]);
            situacao.setNome((String) obj[2]);
            situacao.setDataInicioContrato((Date) obj[3]);
            situacao.setDataFinalContrato((Date) obj[4]);
            situacao.setSituacao((String) obj[5]);
            situacao.setDataInicioSituacao((Date) obj[6]);
            situacao.setDataFinalSituacao((Date) obj[7]);


            if (!mapa.containsKey(situacao.getMatricula())) {
                mapa.put(situacao.getMatricula(), Lists.<VOSituacaoContratoFP>newArrayList());
            }
            mapa.get(situacao.getMatricula()).add(situacao);
        }
        assistente.finaliza();
        return new AsyncResult<>(mapa);
    }
}
