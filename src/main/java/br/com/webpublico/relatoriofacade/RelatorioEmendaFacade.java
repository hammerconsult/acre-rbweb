package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.EmendaItem;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.negocios.BeneficiarioEmendaFacade;
import br.com.webpublico.negocios.EmendaFacade;
import br.com.webpublico.negocios.VereadorFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edi on 10/11/2015.
 */
@Stateless
public class RelatorioEmendaFacade extends RelatorioContabilSuperFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private VereadorFacade vereadorFacade;
    @EJB
    private BeneficiarioEmendaFacade beneficiarioEmendaFacade;
    @EJB
    private EmendaFacade emendaFacade;

    public List<EmendaItem> montarConsultaSql(List<ParametrosRelatorios> parametros) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select e.id as id_emenda, ")
            .append("       pf.cpf || ' - ' || pf.nome as vereador ")
            .append("       from emenda e ")
            .append("       inner join vereador v on v.id = e.vereador_id ")
            .append("       inner join pessoa p on p.id = v.pessoa_id ")
            .append("       inner join pessoafisica pf on pf.id = p.id ")
            .append("       where e.dataemenda between to_date(:DataInicial, 'dd/MM/yyyy') and to_date(:DataFinal, 'dd/MM/yyyy')")
            .append(concatenaParametros(parametros, 2, "and"));
        Query q = em.createNativeQuery(sql.toString());
        q = adicionaParametrosMenosLocal(parametros, q, 1);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<EmendaItem> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                EmendaItem item = new EmendaItem();
                item.setIdEmenda(((BigDecimal) obj[0]).longValue());
                item.setVereador((String) obj[1]);
                item.setSubReport(subReport(parametros, item.getIdEmenda()));
                if (!item.getSubReport().isEmpty()) {
                    retorno.add(item);
                }
            }
            return retorno;
        }
    }

    public List<EmendaItem> montarConsultaSqlBeneficiario(List<ParametrosRelatorios> parametros) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select e.id as id_emenda,  ")
            .append("       pf.cpf || ' - ' || pf.nome as vereador, ")
            .append("       pj.cnpj || ' - ' || pj.razaosocial as beneficiario ")
            .append("       from emenda e  ")
            .append("       inner join vereador v on v.id = e.vereador_id  ")
            .append("       inner join beneficiarioemenda bf on bf.emenda_id = e.id ")
            .append("       inner join pessoa pb on pb.id = bf.pessoa_id ")
            .append("       inner join pessoajuridica pj on pj.id = pb.id ")
            .append("       inner join pessoa p on p.id = v.pessoa_id  ")
            .append("       inner join pessoafisica pf on pf.id = p.id ")
            .append("         where e.dataemenda between to_date(:DataInicial, 'dd/MM/yyyy') and to_date(:DataFinal, 'dd/MM/yyyy')")
            .append(concatenaParametros(parametros, 2, "and"));
        Query q = em.createNativeQuery(sql.toString());
        q = adicionaParametrosMenosLocal(parametros, q, 1);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<EmendaItem> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                EmendaItem item = new EmendaItem();
                item.setIdEmenda(((BigDecimal) obj[0]).longValue());
                item.setVereador((String) obj[1]);
                item.setBeneficiario((String) obj[2]);
                item.setListaDeAcrescimos(buscarAcrescimoEmendaPorBeneficiario(parametros, item.getIdEmenda()));
                retorno.add(item);
            }
            return retorno;
        }
    }


    public List<EmendaItem> subReport(List<ParametrosRelatorios> parametros, Long idEmenda) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select  ")
            .append(" funcional, ")
            .append(" conta, ")
            .append(" fonte_recurso, ")
            .append(" acrescimo, ")
            .append(" cancelamento ")
            .append("from(  ")
            .append(" select  ")
            .append("  substr(vw.codigo,4,7)  ")
            .append("  || '.' || f.codigo  ")
            .append("  || '.' || sf.codigo  ")
            .append("  || '.' || p.codigo  ")
            .append("  || '.' || t.codigo  ")
            .append("  ||  a.codigo  ")
            .append("  || '.' || s.codigo  ")
            .append("  || ' - ' || a.descricao as funcional, ")
            .append("  c.codigo || ' - ' || c.descricao as conta, ")
            .append("  fr.codigo || ' - ' || fr.descricao as fonte_recurso, ")
            .append("  se.valor as acrescimo, ")
            .append("  0 as cancelamento ")
            .append("  from suplementacaoemenda se ")
            .append("  inner join emenda e on e.id = se.emenda_id ")
            .append("  inner join vereador v on v.id = e.vereador_id ")
            .append("  inner join conta c on c.id = se.conta_id ")
            .append("  inner join acaoppa a on a.id = se.acaoppa_id  ")
            .append("  inner join subacaoppa s on s.id = se.subacaoppa_id  ")
            .append("  inner join tipoacaoppa t on t.id = a.tipoacaoppa_id ")
            .append("  inner join programappa p on p.id = a.programa_id ")
            .append("  inner join funcao f on f.id = a.funcao_id ")
            .append("  inner join subfuncao sf on sf.id = a.subfuncao_id ")
            .append("  inner join contadedestinacao cd on cd.id = se.destinacaoderecursos_id ")
            .append("  inner join fontederecursos fr on cd.fontederecursos_id = fr.id ")
            .append("  inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = se.unidadeorganizacional_id ")
            .append("  inner join vwhierarquiaorcamentaria vworg on vworg.subordinada_id = vw.superior_id ")
            .append("    where e.dataemenda between vw.iniciovigencia and coalesce(vw.fimvigencia, e.dataemenda) ")
            .append("    and e.dataemenda between vworg.iniciovigencia and coalesce(vworg.fimvigencia, e.dataemenda) ")
            .append("    and e.dataemenda between to_date(:DataInicial, 'dd/MM/yyyy') and to_date(:DataFinal, 'dd/MM/yyyy')")
            .append("    and e.id = :idEmenda ")
            .append(concatenaParametros(parametros, 1, "and"))
            .append(concatenaParametros(parametros, 2, "and"))
            .append(" ")
            .append("union all ")
            .append("select  ")
            .append("  substr(vw.codigo,4,7)  ")
            .append("  || '.' || f.codigo  ")
            .append("  || '.' || sf.codigo  ")
            .append("  || '.' || p.codigo  ")
            .append("  || '.' || t.codigo  ")
            .append("  ||  a.codigo  ")
            .append("  || '.' || s.codigo  ")
            .append("  || ' - ' || a.descricao as funcional, ")
            .append("  c.codigo || ' - ' || c.descricao as conta, ")
            .append("  fr.codigo || ' - ' || fr.descricao as fonte_recurso, ")
            .append("  0 as acrescimo, ")
            .append("  ae.valor as cancelamento ")
            .append("  from anulacaoemenda ae ")
            .append("  inner join emenda e on e.id = ae.emenda_id ")
            .append("  inner join vereador v on v.id = e.vereador_id ")
            .append("  inner join conta c on c.id = ae.conta_id ")
            .append("  inner join acaoppa a on a.id = ae.acaoppa_id  ")
            .append("  inner join subacaoppa s on s.id = ae.subacaoppa_id  ")
            .append("  inner join tipoacaoppa t on t.id = a.tipoacaoppa_id ")
            .append("  inner join programappa p on p.id = a.programa_id ")
            .append("  inner join funcao f on f.id = a.funcao_id ")
            .append("  inner join subfuncao sf on sf.id = a.subfuncao_id ")
            .append("  inner join contadedestinacao cd on cd.id = ae.destinacaoderecursos_id ")
            .append("  inner join fontederecursos fr on cd.fontederecursos_id = fr.id ")
            .append("  inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = ae.unidadeorganizacional_id ")
            .append("  inner join vwhierarquiaorcamentaria vworg on vworg.subordinada_id = vw.superior_id ")
            .append("    where e.dataemenda between vw.iniciovigencia and coalesce(vw.fimvigencia, e.dataemenda) ")
            .append("    and e.dataemenda between vworg.iniciovigencia and coalesce(vworg.fimvigencia, e.dataemenda) ")
            .append("    and e.dataemenda between to_date(:DataInicial, 'dd/MM/yyyy') and to_date(:DataFinal, 'dd/MM/yyyy')")
            .append("    and e.id = :idEmenda ")
            .append(concatenaParametros(parametros, 1, "and"))
            .append(concatenaParametros(parametros, 2, "and"))
            .append("  ) ")
            .append(" order by funcional, conta, fonte_recurso ");

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idEmenda", idEmenda);
        q = adicionaParametros(parametros, q);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<EmendaItem> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                EmendaItem item = new EmendaItem();
                item.setFuncional((String) obj[0]);
                item.setContaDespesa((String) obj[1]);
                item.setFonteRecurso((String) obj[2]);
                item.setValorAcrescimo((BigDecimal) obj[3]);
                item.setValorCancelamento((BigDecimal) obj[4]);
                retorno.add(item);
            }
            return retorno;
        }
    }

    public List<EmendaItem> buscarAcrescimoEmendaPorBeneficiario(List<ParametrosRelatorios> parametros, Long idEmenda) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select  distinct ")
            .append("  substr(vw.codigo,4,7)  ")
            .append("  || '.' || f.codigo  ")
            .append("  || '.' || sf.codigo  ")
            .append("  || '.' || p.codigo  ")
            .append("  || '.' || t.codigo  ")
            .append("  ||  a.codigo  ")
            .append("  || '.' || s.codigo  ")
            .append("  || ' - ' || a.descricao as funcional, ")
            .append("  c.codigo || ' - ' || c.descricao as conta, ")
            .append("  fr.codigo || ' - ' || fr.descricao as fonte_recurso, ")
            .append("  se.valor as acrescimo, ")
            .append("  vworg.codigo || ' - ' || vworg.descricao as orgao ")
            .append("  from suplementacaoemenda se ")
            .append("  inner join emenda e on e.id = se.emenda_id ")
            .append("  inner join beneficiarioemenda bf on bf.emenda_id = e.id ")
            .append("  inner join vereador v on v.id = e.vereador_id ")
            .append("  inner join conta c on c.id = se.conta_id ")
            .append("  inner join acaoppa a on a.id = se.acaoppa_id  ")
            .append("  inner join subacaoppa s on s.id = se.subacaoppa_id  ")
            .append("  inner join tipoacaoppa t on t.id = a.tipoacaoppa_id ")
            .append("  inner join programappa p on p.id = a.programa_id ")
            .append("  inner join funcao f on f.id = a.funcao_id ")
            .append("  inner join subfuncao sf on sf.id = a.subfuncao_id ")
            .append("  inner join contadedestinacao cd on cd.id = se.destinacaoderecursos_id ")
            .append("  inner join fontederecursos fr on cd.fontederecursos_id = fr.id ")
            .append("  inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = se.unidadeorganizacional_id ")
            .append("  inner join vwhierarquiaorcamentaria vworg on vworg.subordinada_id = vw.superior_id ")
            .append("    where e.dataemenda between vw.iniciovigencia and coalesce(vw.fimvigencia, e.dataemenda) ")
            .append("    and e.dataemenda between vworg.iniciovigencia and coalesce(vworg.fimvigencia, e.dataemenda) ")
            .append("    and e.dataemenda between to_date(:DataInicial, 'dd/MM/yyyy') and to_date(:DataFinal, 'dd/MM/yyyy')")
            .append("    and e.id = :idEmenda ")
            .append(concatenaParametros(parametros, 1, "and"))
            .append(concatenaParametros(parametros, 2, "and"))
            .append(" order by orgao, funcional, conta, fonte_recurso ");

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idEmenda", idEmenda);
        q = adicionaParametros(parametros, q);

        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<EmendaItem> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                EmendaItem item = new EmendaItem();
                item.setFuncional((String) obj[0]);
                item.setContaDespesa((String) obj[1]);
                item.setFonteRecurso((String) obj[2]);
                item.setValorAcrescimo((BigDecimal) obj[3]);
                item.setOrgao((String) obj[4]);
                retorno.add(item);
            }
            return retorno;
        }
    }

    public VereadorFacade getVereadorFacade() {
        return vereadorFacade;
    }

    public void setVereadorFacade(VereadorFacade vereadorFacade) {
        this.vereadorFacade = vereadorFacade;
    }

    public BeneficiarioEmendaFacade getBeneficiarioEmendaFacade() {
        return beneficiarioEmendaFacade;
    }

    public void setBeneficiarioEmendaFacade(BeneficiarioEmendaFacade beneficiarioEmendaFacade) {
        this.beneficiarioEmendaFacade = beneficiarioEmendaFacade;
    }

    public EmendaFacade getEmendaFacade() {
        return emendaFacade;
    }
}
