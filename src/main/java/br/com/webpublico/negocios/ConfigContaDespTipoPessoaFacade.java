package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SubTipoDespesa;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoViagem;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Preconditions;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: Edi Date: 13/01/15 Time: 09:49 To
 * change this template use File | Settings | File Templates.
 */
@Stateless
public class ConfigContaDespTipoPessoaFacade extends AbstractFacade<ConfigContaDespTipoPessoa> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;

    public ConfigContaDespTipoPessoaFacade() {
        super(ConfigContaDespTipoPessoa.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean verificarConfiguracaoVigente(ConfigContaDespTipoPessoa configuracao) {
        String sql = " select config.*  " +
            "           from configcontadesptipopessoa config " +
            "           inner join exercicio ex on ex.id = config.exercicio_id " +
            "           inner join conta c on c.id = config.contadespesa_id " +
            "          where config.tipopessoa = :tipoPessoa" +
            "          and config.exercicio_id = :idExercicio" +
            "          and config.contadespesa_id = :idContaDespesa ";
        if (configuracao.getId() != null) {
            sql += " and config.id <> :id";
        }
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("tipoPessoa", configuracao.getTipoPessoa().name());
        consulta.setParameter("idExercicio", configuracao.getExercicio().getId());
        consulta.setParameter("idContaDespesa", configuracao.getContaDespesa().getId());
        if (configuracao.getId() != null) {
            consulta.setParameter("id", configuracao.getId());
        }
        return !consulta.getResultList().isEmpty();
    }

    public ConfigContaDespTipoPessoa recuperarConfiguracaoContaDespesaTipoPessoa(Conta c, Exercicio exercicio) throws Exception {

        Preconditions.checkNotNull(c, " A conta de despesa não foi informada.");
        Preconditions.checkNotNull(exercicio, " O Exercício não foi informado.");

        String sql = " SELECT C.* FROM CONFIGCONTADESPTIPOPESSOA C " +
            " WHERE C.CONTADESPESA_ID = :conta " +
            " AND C.EXERCICIO_ID = :exerc ";
        Query q = em.createNativeQuery(sql, ConfigContaDespTipoPessoa.class);
        q.setParameter("conta", c.getId());
        q.setParameter("exerc", exercicio.getId());
        try {
            ConfigContaDespTipoPessoa configuracao = (ConfigContaDespTipoPessoa) q.getSingleResult();
            if (configuracao != null) {
                return configuracao;
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }
}
