package br.com.webpublico.singletons;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ProcessoDeCompra;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.ChaveGeradorCodigoAdministrativo;
import br.com.webpublico.enums.FuncionalidadeAdministrativo;
import br.com.webpublico.enums.ModalidadeLicitacao;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Maps;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by carlos on 18/06/15.
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class SingletonGeradorCodigoAdministrativo implements Serializable {

    private Map<ChaveGeradorCodigoAdministrativo, Integer> mapUltimoCodigoPorFuncionalidade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Lock(LockType.READ)
    public void buscarUltimosCodigosFormularioCotacao() {
        String sql = " select exercicio_id, unidadeorganizacional_id, max(numero) " +
            "  from formulariocotacao fc " +
            " group by exercicio_id, unidadeorganizacional_id ";
        Query q = em.createNativeQuery(sql);
        if (q.getResultList() != null) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                mapUltimoCodigoPorFuncionalidade.put(ChaveGeradorCodigoAdministrativo.newChave(
                    ((BigDecimal) obj[0]).longValue(), ((BigDecimal) obj[1]).longValue(),
                    FuncionalidadeAdministrativo.FORMULARIO_COTACAO), ((BigDecimal) obj[2]).intValue());
            }
        }
    }

    @Lock(LockType.READ)
    public void buscarUltimosCodigosLicitacao() {
        String sql = " select l.exercicio_id, max(l.numerolicitacao) " +
            "   from licitacao l " +
            "   where l.modalidadelicitacao in (:modalidades) " +
            " group by l.exercicio_id ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("modalidades", Util.getListOfEnumName(ModalidadeLicitacao.getModalidadesLicitacao()));
        if (q.getResultList() != null) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                mapUltimoCodigoPorFuncionalidade.put(ChaveGeradorCodigoAdministrativo.newChave(
                    null, ((BigDecimal) obj[0]).longValue(),
                    FuncionalidadeAdministrativo.LICITACAO), ((BigDecimal) obj[1]).intValue());
            }
        }
    }

    @Lock(LockType.READ)
    public Integer gerarNumeroCredenciamento(Exercicio exercicio, UnidadeOrganizacional unidade, Date dataOperacao) {
        String sql = " " +
            " select coalesce(max(lic.numerolicitacao), 0) from licitacao lic  " +
            "   inner join processodecompra pc on lic.processodecompra_id = pc.id  " +
            "   inner join unidadeorganizacional uo on pc.unidadeorganizacional_id = uo.id  " +
            "   inner join exercicio ex on lic.exercicio_id = ex.id  " +
            "   inner join hierarquiaorganizacional ho on ho.superior_id = uo.id  " +
            " where ex.id = :idExercicio  " +
            "   and substr(ho.codigo, 0, 6) = (select distinct substr(codigo, 0, 6)  " +
            "                                 from hierarquiaorganizacional  " +
            "                                 where (superior_id = :idUnidade or subordinada_id = :idUnidade)  " +
            "                                   and tipohierarquiaorganizacional = :tipo  " +
            "                                   and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(iniciovigencia) and coalesce(trunc(fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy'))  " +
            "                                   and regexp_like(codigo, '^[[:digit:]]{2}\\.[[:digit:]]{2}\\.'))  " +
            "  and ho.tipohierarquiaorganizacional = :tipo  " +
            "  and lic.modalidadelicitacao = :credenciamento " +
            "  and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy'))  ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("idUnidade", unidade.getId());
        q.setParameter("tipo", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("credenciamento", ModalidadeLicitacao.CREDENCIAMENTO.name());
        return (((BigDecimal) q.getSingleResult()).intValue() + 1);
    }

    @Lock(LockType.READ)
    public Integer gerarNumeroDispensa(Exercicio exercicio, UnidadeOrganizacional unidade, Date dataOperacao) {
        String sql = "  " +
            " select coalesce(max(dl.numerodadispensa), 0) from dispensadelicitacao dl " +
            "   inner join processodecompra pc on dl.processodecompra_id = pc.id " +
            "   inner join unidadeorganizacional uo on pc.unidadeorganizacional_id = uo.id " +
            "   inner join exercicio ex on dl.exerciciodadispensa_id = ex.id " +
            "   inner join hierarquiaorganizacional ho on ho.superior_id = uo.id " +
            " where ex.ano = :ano " +
            "   and substr(ho.codigo,0,6) = (select distinct substr(codigo,0,6) from hierarquiaorganizacional" +
            "                                where (superior_id = :idUnidade or subordinada_id = :idUnidade )" +
            "                                and tipohierarquiaorganizacional = :tipo " +
            "                                and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(iniciovigencia) and coalesce(trunc(fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            " and regexp_like(codigo, '^[[:digit:]]{2}\\.[[:digit:]]{2}\\.')) " +
            " and ho.tipohierarquiaorganizacional = :tipo " +
            " and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy')) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", exercicio.getAno());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("idUnidade", unidade.getId());
        q.setParameter("tipo", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        return (((BigDecimal) q.getSingleResult()).intValue() + 1);
    }

    @PostConstruct
    public void init() {
        mapUltimoCodigoPorFuncionalidade = Maps.newHashMap();
        buscarUltimosCodigosFormularioCotacao();
        buscarUltimosCodigosLicitacao();
    }

    @Lock(LockType.READ)
    public Integer getProximoCodigoPorFuncionalidade(Long idUnidade, Long idExercicio, FuncionalidadeAdministrativo funcionalidadeAdministrativo) {
        ChaveGeradorCodigoAdministrativo chave = ChaveGeradorCodigoAdministrativo.newChave(idExercicio, idUnidade, funcionalidadeAdministrativo);
        Integer ultimoCodigo = mapUltimoCodigoPorFuncionalidade.get(chave);
        if (ultimoCodigo == null) {
            ultimoCodigo = 0;
        }
        mapUltimoCodigoPorFuncionalidade.put(chave, ultimoCodigo + 1);
        return ultimoCodigo + 1;
    }
}

