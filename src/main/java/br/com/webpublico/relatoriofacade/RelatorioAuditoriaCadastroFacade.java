package br.com.webpublico.relatoriofacade;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidadesauxiliares.AuditoriaCadastro;
import br.com.webpublico.entidadesauxiliares.AuditoriaCadastroAssistente;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.AuditoriaFacade;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by HardRock on 11/04/2017.
 */
@Stateless
public class RelatorioAuditoriaCadastroFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private PropriedadeFacade propriedadeFacade;
    @EJB
    private CompromissarioFacade compromissarioFacade;
    @EJB
    private NaturezaJuridicaFacade naturezaJuridicaFacade;
    @EJB
    private TipoAutonomoFacade tipoAutonomoFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private AuditoriaFacade auditoriaFacade;


    public List<AuditoriaCadastro> buscarAuditoriaCadastroImobiliarioAnaliticoPaginado(String condicao, Integer maxResult, Integer firstResult) {
        StringBuilder sql = getSqlAuditoriaCadastroImobiliarioAnalitico(condicao);
        sql.append("order by aud.datahora, aud.usuario ");
        Query q = em.createNativeQuery(sql.toString());
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResult);
        List<Object[]> lista = (List<Object[]>) q.getResultList();
        List<AuditoriaCadastro> retorno = new ArrayList<>();
        if (!lista.isEmpty()) {
            for (Object[] obj : lista) {
                AuditoriaCadastro item = new AuditoriaCadastro();
                item.setUsuarioSistema(getUsuarioPorLogin((String) obj[0]));
                item.setDataRevisao((Date) obj[1]);
                item.setTipoMovimento((String) obj[2]);
                item.setInscricao((String) obj[3]);
                item.setDetalhe((String) obj[4]);
                item.setIdRevisao(((BigDecimal) obj[5]).longValue());
                item.setIdMovimento(((BigDecimal) obj[6]).longValue());
                item.setTipoCadastro(AuditoriaCadastro.TipoCadastro.IMOBILIARIO);
                retorno.add(item);
            }
        }
        return retorno;
    }

    public List<AuditoriaCadastro> buscarAuditoriaCadastroEconomicoAnaliticoPaginado(String condicao, Integer maxResult, Integer firstResult) {
        StringBuilder sql = getSqlAuditoriaCadastroEconomicoAnalitico(condicao);
        sql.append("order by aud.datahora, aud.usuario ");
        Query q = em.createNativeQuery(sql.toString());
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResult);
        List<Object[]> lista = (List<Object[]>) q.getResultList();
        List<AuditoriaCadastro> retorno = new ArrayList<>();
        if (!lista.isEmpty()) {
            for (Object[] obj : lista) {
                AuditoriaCadastro item = new AuditoriaCadastro();
                item.setUsuarioSistema(getUsuarioPorLogin((String) obj[0]));
                item.setDataRevisao((Date) obj[1]);
                item.setTipoMovimento((String) obj[2]);
                item.setInscricao((String) obj[3]);
                item.setDetalhe((String) obj[4]);
                item.setIdRevisao(((BigDecimal) obj[5]).longValue());
                item.setIdMovimento(((BigDecimal) obj[6]).longValue());
                item.setTipoCadastro(AuditoriaCadastro.TipoCadastro.ECONOMICO);
                retorno.add(item);
            }
        }
        return retorno;
    }

    public List<AuditoriaCadastro> buscarAuditoriaCadastroContribuinteAnaliticoPaginado(String condicao, Integer maxResult, Integer firstResult) {
        StringBuilder sql = getSqlAuditoriaCadastroContribuinteAnalitico(condicao);
        sql.append("order by aud.datahora, aud.usuario ");
        Query q = em.createNativeQuery(sql.toString());
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResult);
        List<Object[]> lista = (List<Object[]>) q.getResultList();
        List<AuditoriaCadastro> retorno = new ArrayList<>();
        if (!lista.isEmpty()) {
            for (Object[] obj : lista) {
                AuditoriaCadastro item = new AuditoriaCadastro();
                item.setUsuarioSistema(getUsuarioPorLogin((String) obj[0]));
                item.setDataRevisao((Date) obj[1]);
                item.setTipoMovimento((String) obj[2]);
                item.setInscricao((String) obj[3]);
                item.setDetalhe((String) obj[4]);
                item.setIdRevisao(((BigDecimal) obj[5]).longValue());
                item.setIdMovimento(((BigDecimal) obj[6]).longValue());
                definirTipoCadastroParaContribuinte(obj[7], item);
                retorno.add(item);
            }
        }
        return retorno;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    public Future<AuditoriaCadastroAssistente> buscarAuditoriaCadastroImobiliarioAnalitico(String condicao) {
        StringBuilder sql = getSqlAuditoriaCadastroImobiliarioAnalitico(condicao);
        sql.append("order by aud.usuario, aud.datahora ");
        Query q = em.createNativeQuery(sql.toString());
        List<Object[]> lista = (List<Object[]>) q.getResultList();
        List<AuditoriaCadastro> retorno = new ArrayList<>();
        if (!lista.isEmpty()) {
            for (Object[] obj : lista) {
                AuditoriaCadastro item = new AuditoriaCadastro();
                item.setUsuarioSistema(getUsuarioPorLogin((String) obj[0]));
                item.setDataRevisao((Date) obj[1]);
                item.setTipoMovimento((String) obj[2]);
                item.setInscricao((String) obj[3]);
                item.setDetalhe((String) obj[4]);
                item.setIdRevisao(((BigDecimal) obj[5]).longValue());
                item.setIdMovimento(((BigDecimal) obj[6]).longValue());
                item.setTipoCadastro(AuditoriaCadastro.TipoCadastro.IMOBILIARIO);
                retorno.add(item);
            }
        }
        return new AsyncResult<>(new AuditoriaCadastroAssistente(retorno));
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    public Future<AuditoriaCadastroAssistente> buscarAuditoriaCadastroEconomicoAnalitico(String condicao) {
        StringBuilder sql = getSqlAuditoriaCadastroEconomicoAnalitico(condicao);
        sql.append("order by aud.usuario, aud.datahora ");
        Query q = em.createNativeQuery(sql.toString());
        List<Object[]> lista = (List<Object[]>) q.getResultList();
        List<AuditoriaCadastro> retorno = new ArrayList<>();

        if (!lista.isEmpty()) {
            for (Object[] obj : lista) {
                AuditoriaCadastro item = new AuditoriaCadastro();
                item.setUsuarioSistema(getUsuarioPorLogin((String) obj[0]));
                item.setDataRevisao((Date) obj[1]);
                item.setTipoMovimento((String) obj[2]);
                item.setInscricao((String) obj[3]);
                item.setDetalhe((String) obj[4]);
                item.setIdRevisao(((BigDecimal) obj[5]).longValue());
                item.setIdMovimento(((BigDecimal) obj[6]).longValue());
                item.setTipoCadastro(AuditoriaCadastro.TipoCadastro.ECONOMICO);
                retorno.add(item);
            }
        }
        return new AsyncResult<>(new AuditoriaCadastroAssistente(retorno));
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    public Future<AuditoriaCadastroAssistente> buscarAuditoriaCadastroContribuinteAnalitico(String condicao) {
        StringBuilder sql = getSqlAuditoriaCadastroContribuinteAnalitico(condicao);
        sql.append("order by aud.usuario, aud.datahora ");
        Query q = em.createNativeQuery(sql.toString());
        List<Object[]> lista = (List<Object[]>) q.getResultList();
        List<AuditoriaCadastro> retorno = new ArrayList<>();
        if (!lista.isEmpty()) {
            for (Object[] obj : lista) {
                AuditoriaCadastro item = new AuditoriaCadastro();
                item.setUsuarioSistema(getUsuarioPorLogin((String) obj[0]));
                item.setDataRevisao((Date) obj[1]);
                item.setTipoMovimento((String) obj[2]);
                item.setInscricao((String) obj[3]);
                item.setDetalhe((String) obj[4]);
                item.setIdRevisao(((BigDecimal) obj[5]).longValue());
                item.setIdMovimento(((BigDecimal) obj[6]).longValue());
                definirTipoCadastroParaContribuinte(obj[7], item);
                retorno.add(item);
            }
        }
        return new AsyncResult<>(new AuditoriaCadastroAssistente(retorno));
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    public Future<AuditoriaCadastroAssistente> buscarAuditoriaCadastroContribuinteSintetico(String condicao) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
            .append("  tipoCadastro, ")
            .append("  tipoMovimento, ")
            .append("  quantidade ")
            .append(" FROM ( ")
            .append("       SELECT ")
            .append("         'Pessoa Física' AS tipoCadastro, ")
            .append("         CASE ")
            .append("           WHEN aud.REVTYPE = 0 THEN 'Inclusão' ")
            .append("           WHEN aud.REVTYPE = 1 THEN 'Alteração' ")
            .append("           WHEN aud.REVTYPE = 2 THEN 'Exclusão' ")
            .append("         END AS tipoMovimento, ")
            .append("         count(distinct rev.id)   AS quantidade, ")
            .append("         aud.REVTYPE ")
            .append("       FROM pessoa_aud aud ")
            .append("         INNER JOIN revisaoauditoria rev ON rev.id = aud.rev ")
            .append("         INNER JOIN pessoa p ON p.id = aud.id ")
            .append("         INNER JOIN pessoafisica pf ON pf.id = p.id ")
            .append(condicao)
            .append("       GROUP BY aud.REVTYPE ")
            .append(" ")
            .append("       UNION ALL ")
            .append(" ")
            .append("       SELECT ")
            .append("         'Pessoa Jurídica' AS tipoCadastro, ")
            .append("         CASE ")
            .append("           WHEN aud.REVTYPE = 0 THEN 'Inclusão' ")
            .append("           WHEN aud.REVTYPE = 1 THEN 'Alteração' ")
            .append("           WHEN aud.REVTYPE = 2 THEN 'Exclusão' ")
            .append("         END AS tipoMovimento, ")
            .append("         count(distinct rev.id)     AS quantidade, ")
            .append("         aud.REVTYPE ")
            .append("       FROM pessoa_aud aud ")
            .append("         INNER JOIN revisaoauditoria rev ON rev.id = aud.rev ")
            .append("         INNER JOIN pessoa p ON p.id = aud.id ")
            .append("         INNER JOIN pessoajuridica pj ON pj.id = p.id ")
            .append(condicao)
            .append("       GROUP BY aud.REVTYPE ")
            .append("     ) aud ")
            .append("ORDER BY aud.REVTYPE ");
        Query q = em.createNativeQuery(sql.toString());
        List<Object[]> lista = (List<Object[]>) q.getResultList();
        List<AuditoriaCadastro> retorno = new ArrayList<>();
        if (!lista.isEmpty()) {
            for (Object[] obj : lista) {
                AuditoriaCadastro item = new AuditoriaCadastro();
                definirTipoCadastroParaContribuinte(obj[0], item);
                item.setTipoMovimento((String) obj[1]);
                item.setQuantidade(((BigDecimal) obj[2]).intValue());
                retorno.add(item);
            }
        }
        return new AsyncResult<>(new AuditoriaCadastroAssistente(retorno));
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    public Future<AuditoriaCadastroAssistente> buscarAuditoriaCadastroEconomicoSintetico(String condicao) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ")
            .append("    'Econômico' as tipoCadastro, ")
            .append("     case when aud.REVTYPE = 0 then 'Inclusão' ")
            .append("          when aud.REVTYPE = 1 then 'Alteração' ")
            .append("          when aud.REVTYPE = 2 then 'Exclusão' ")
            .append("     end as tipoMovimento, ")
            .append("     count(distinct rev.id) as quantidade, ")
            .append("     aud.REVTYPE ")
            .append("     from cadastro_aud aud ")
            .append("       inner join cadastroeconomico_aud ce on ce.id = aud.id ")
            .append("       inner join revisaoauditoria rev on rev.id = aud.rev ")
            .append("       inner join cadastro c on aud.id = c.id ")
            .append("       inner join cadastroeconomico bce on bce.id = c.id ")
            .append("       inner join enquadramentofiscal enq on enq.cadastroEconomico_id = bce.id and enq.fimVigencia is null ")
            .append("       left join ECONOMICOCNAE eCnae on eCnae.CADASTROECONOMICO_ID = bce.id ")
            .append("       left join CNAE cnae on cnae.ID = eCnae.CNAE_ID ")
            .append(condicao)
            .append("  group by aud.REVTYPE ")
            .append("  order by aud.REVTYPE ");
        Query q = em.createNativeQuery(sql.toString());
        List<Object[]> lista = (List<Object[]>) q.getResultList();
        List<AuditoriaCadastro> retorno = new ArrayList<>();
        if (!lista.isEmpty()) {
            for (Object[] obj : lista) {
                AuditoriaCadastro item = new AuditoriaCadastro();
                item.setTipoCadastro(AuditoriaCadastro.TipoCadastro.ECONOMICO);
                item.setTipoMovimento((String) obj[1]);
                item.setQuantidade(((BigDecimal) obj[2]).intValue());
                retorno.add(item);
            }
        }
        return new AsyncResult<>(new AuditoriaCadastroAssistente(retorno));
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    public Future<AuditoriaCadastroAssistente> buscarAuditoriaCadastroImobiliarioSintetico(String condicao) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ")
            .append("    'Imobiliário' as tipoCadastro, ")
            .append("    case when aud.REVTYPE = 0 then 'Inclusão' ")
            .append("         when aud.REVTYPE = 1 then 'Alteração' ")
            .append("         when aud.REVTYPE = 2 then 'Exclusão' ")
            .append("     end as tipoMovimento, ")
            .append("     count(distinct rev.id) as quantidade, ")
            .append("     aud.REVTYPE ")
            .append("      from cadastro_aud aud ")
            .append("        inner join cadastroImobiliario_aud ci on ci.id = aud.id ")
            .append("        inner join revisaoauditoria rev on rev.id = aud.rev ")
            .append("        inner join cadastro c on aud.id = c.id ")
            .append("        inner join cadastroimobiliario bci on bci.id = c.id ")
            .append("        inner join lote lote on lote.id = bci.lote_id")
            .append("        inner join setor setor on setor.id = lote.setor_id")
            .append("        inner join quadra quadra on quadra.id = lote.quadra_id")
            .append("        inner join testada test on test.lote_id = lote.id and test.principal = 1")
            .append("        inner join face face on face.id = test.face_id")
            .append("        inner join logradourobairro logra on logra.id = face.logradourobairro_id")
            .append("        inner join logradouro log on log.id = logra.logradouro_id ")
            .append("        inner join bairro bairro on bairro.id = logra.bairro_id")
            .append("        left join propriedade prop on prop.imovel_id = bci.id")
            .append("        left join compromissario cp on cp.cadastroimobiliario_id = bci.id ")
            .append(condicao)
            .append(" group by aud.REVTYPE ")
            .append(" order by aud.REVTYPE ");
        Query q = em.createNativeQuery(sql.toString());
        List<Object[]> lista = (List<Object[]>) q.getResultList();
        List<AuditoriaCadastro> retorno = new ArrayList<>();
        if (!lista.isEmpty()) {
            for (Object[] obj : lista) {
                AuditoriaCadastro item = new AuditoriaCadastro();
                item.setTipoCadastro(AuditoriaCadastro.TipoCadastro.IMOBILIARIO);
                item.setTipoMovimento((String) obj[1]);
                item.setQuantidade(((BigDecimal) obj[2]).intValue());
                retorno.add(item);
            }
        }
        return new AsyncResult<>(new AuditoriaCadastroAssistente(retorno));
    }

    private StringBuilder getSqlAuditoriaCadastroContribuinteAnalitico(String condicao) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct ")
            .append("      aud.usuario, ")
            .append("      aud.datahora as dataRevisao, ")
            .append("      aud.tipoMovimento, ")
            .append("      coalesce(aud.inscricao, ' '), ")
            .append("      coalesce(aud.detalhe, ' '), ")
            .append("      aud.idRevisao, ")
            .append("      aud.idMovimento, ")
            .append("      aud.tipoCadastro ")
            .append("    from ( ")
            .append("          select ")
            .append("            rev.usuario as usuario, ")
            .append("            rev.id as idRevisao, ")
            .append("            rev.datahora as datahora, ")
            .append("            coalesce((select 'Pessoa Física' from pessoafisica_aud pf where pf.id = aud.id and id is not null and rownum = 1), 'Pessoa Jurídica') as tipoCadastro, ")
            .append("            case when aud.REVTYPE = 0 then 'Inclusão' ")
            .append("                 when aud.REVTYPE = 1 then 'Alteração' ")
            .append("                 when aud.REVTYPE = 2 then 'Exclusão' ")
            .append("            end as tipoMovimento, ")
            .append("            aud.REVTYPE, ")
            .append("            aud.id as idMovimento, ")
            .append("            coalesce((select FORMATACPFCNPJ(pf.CPF) from pessoafisica_aud pf where pf.id = aud.id and rownum = 1), (select FORMATACPFCNPJ(pj.CNPJ) from PESSOAJURIDICA_aud pj where pj.id = aud.id and rownum = 1)) as inscricao, ")
            .append("            coalesce((select pf.nome from pessoafisica_aud pf where pf.id = aud.id and rownum = 1), (select pj.RAZAOSOCIAL from PESSOAJURIDICA_aud pj where pj.id = aud.id and rownum = 1)) as detalhe ")
            .append("          from pessoa_aud aud ")
            .append("            inner join revisaoauditoria rev on rev.id = aud.rev ")
            .append("            inner join pessoa p on p.id = aud.id ")
            .append(condicao)
            .append(" ) aud ");
        return sql;
    }

    private StringBuilder getSqlAuditoriaCadastroImobiliarioAnalitico(String condicao) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct aud.usuario, ")
            .append("      aud.datahora as dataRevisao, ")
            .append("      aud.tipoMovimento, ")
            .append("      aud.inscricao, ")
            .append("      aud.detalhe, ")
            .append("      aud.idRevisao, ")
            .append("      aud.idMovimento, ")
            .append("      aud.tipoCadastro ")
            .append("    from ( ")
            .append("          select ")
            .append("            rev.usuario, ")
            .append("            rev.id as idRevisao, ")
            .append("            rev.datahora as datahora, ")
            .append("            'Imobiliário' as tipoCadastro, ")
            .append("            case when aud.REVTYPE = 0 then 'Inclusão' ")
            .append("                 when aud.REVTYPE = 1 then 'Alteração' ")
            .append("                 when aud.REVTYPE = 2 then 'Exclusão' ")
            .append("            end as tipoMovimento, ")
            .append("            aud.REVTYPE, ")
            .append("            aud.id as idMovimento, ")
            .append("            coalesce(ciaud.inscricaocadastral, '') as inscricao, ")
            .append("            'Proprietário: ' || coalesce(pf.nome, pj.razaosocial) as detalhe ")
            .append("          from cadastro_aud aud ")
            .append("            inner join cadastroimobiliario_aud ciaud on ciaud.id = aud.ID ")
            .append("            inner join revisaoauditoria rev on rev.id = aud.rev ")
            .append("            inner join cadastro c on aud.id = c.id ")
            .append("            inner join cadastroimobiliario bci on bci.id = c.id ")
            .append("            inner join lote lote on lote.id = bci.lote_id")
            .append("            inner join setor setor on setor.id = lote.setor_id")
            .append("            inner join quadra quadra on quadra.id = lote.quadra_id")
            .append("            inner join testada test on test.lote_id = lote.id and test.principal = 1")
            .append("            inner join face face on face.id = test.face_id")
            .append("            inner join logradourobairro logra on logra.id = face.logradourobairro_id")
            .append("            inner join logradouro log on log.id = logra.logradouro_id ")
            .append("            inner join bairro bairro on bairro.id = logra.bairro_id")
            .append("            left join propriedade prop on prop.imovel_id = bci.id and rev.datahora between prop.inicioVigencia and coalesce(prop.finalVigencia,rev.datahora) ")
            .append("            left join pessoafisica pf on pf.id = prop.pessoa_id ")
            .append("            left join pessoajuridica pj on pj.id = prop.pessoa_id ")
            .append("            left join compromissario cp on cp.cadastroimobiliario_id = bci.id ")
            .append(condicao)
            .append(" ) aud ");
        return sql;
    }

    private StringBuilder getSqlAuditoriaCadastroEconomicoAnalitico(String condicao) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct ")
            .append("      aud.usuario, ")
            .append("      aud.datahora as dataRevisao, ")
            .append("      aud.tipoMovimento, ")
            .append("      aud.inscricao, ")
            .append("      aud.detalhe, ")
            .append("      aud.idRevisao, ")
            .append("      aud.idMovimento, ")
            .append("      aud.tipoCadastro ")
            .append("    from ( ")
            .append("          select ")
            .append("            rev.usuario, ")
            .append("            rev.id as idRevisao, ")
            .append("            rev.datahora as datahora, ")
            .append("            'Econômico' as tipoCadastro, ")
            .append("            case when aud.REVTYPE = 0 then 'Inclusão' ")
            .append("                 when aud.REVTYPE = 1 then 'Alteração' ")
            .append("                 when aud.REVTYPE = 2 then 'Exclusão' ")
            .append("            end as tipoMovimento, ")
            .append("            aud.REVTYPE, ")
            .append("            aud.id as idMovimento, ")
            .append("            'CMC: ' || coalesce(ceaud.inscricaocadastral, '') as inscricao, ")
            .append("            (select ")
            .append("               case when pf.id is not null then 'Nome: ' else 'Razão Social: ' end || coalesce(pf.nome, pj.razaosocial) ")
            .append("               from pessoa ")
            .append("               left join pessoafisica pf on pf.id = pessoa.id ")
            .append("               left join pessoajuridica pj on pj.id = pessoa.id ")
            .append("               where pessoa.id = ceaud.pessoa_id ")
            .append("             ) as detalhe ")
            .append("          from cadastro_aud aud ")
            .append("            inner join cadastroeconomico_aud ceaud on ceaud.id = aud.ID ")
            .append("            inner join revisaoauditoria rev on rev.id = aud.rev ")
            .append("            inner join cadastro c on aud.id = c.id ")
            .append("            inner join cadastroeconomico bce on bce.id = c.id ")
            .append("            inner join enquadramentofiscal enq on enq.cadastroEconomico_id = bce.id and enq.fimVigencia is null ")
            .append("            left join ECONOMICOCNAE eCnae on eCnae.CADASTROECONOMICO_ID = bce.id ")
            .append("            left join CNAE cnae on cnae.ID = eCnae.CNAE_ID ")
            .append(condicao)
            .append(" ) aud ");
        return sql;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public Future<Integer> getTotalRegistroAuditoriaCadastroImobiliarioAnalitico(String condicao) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct aud.* ")
            .append("    from ( ")
            .append("          select ")
            .append("            rev.usuario, ")
            .append("            rev.id as idRevisao, ")
            .append("            rev.datahora as datahora, ")
            .append("            'Imobiliário' as tipoCadastro, ")
            .append("            case when aud.REVTYPE = 0 then 'Inclusão' ")
            .append("                 when aud.REVTYPE = 1 then 'Alteração' ")
            .append("                 when aud.REVTYPE = 2 then 'Exclusão' ")
            .append("            end as tipoMovimento, ")
            .append("            aud.REVTYPE, ")
            .append("            aud.id as idMovimento, ")
            .append("            coalesce(ciaud.inscricaocadastral, '') as inscricao, ")
            .append("            'Proprietário: ' || coalesce(pf.nome, pj.razaosocial) as detalhe ")
            .append("          from cadastro_aud aud ")
            .append("            inner join cadastroimobiliario_aud ciaud on ciaud.id = aud.ID ")
            .append("            inner join revisaoauditoria rev on rev.id = aud.rev ")
            .append("            inner join cadastro c on aud.id = c.id ")
            .append("            inner join cadastroimobiliario bci on bci.id = c.id ")
            .append("            inner join lote lote on lote.id = bci.lote_id")
            .append("            inner join setor setor on setor.id = lote.setor_id")
            .append("            inner join quadra quadra on quadra.id = lote.quadra_id")
            .append("            inner join testada test on test.lote_id = lote.id and test.principal = 1")
            .append("            inner join face face on face.id = test.face_id")
            .append("            inner join logradourobairro logra on logra.id = face.logradourobairro_id")
            .append("            inner join logradouro log on log.id = logra.logradouro_id ")
            .append("            inner join bairro bairro on bairro.id = logra.bairro_id")
            .append("            left join propriedade prop on prop.imovel_id = bci.id and rev.datahora between prop.inicioVigencia and coalesce(prop.finalVigencia,rev.datahora) ")
            .append("            left join pessoafisica pf on pf.id = prop.pessoa_id ")
            .append("            left join pessoajuridica pj on pj.id = prop.pessoa_id ")
            .append("            left join compromissario cp on cp.cadastroimobiliario_id = bci.id ")
            .append(condicao)
            .append(" ) aud ");
        Query q = em.createNativeQuery(sql.toString());
        return new AsyncResult<>(q.getResultList().size());
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public Future<Integer> getTotalRegistroAuditoriaCadastroEconomicoAnalitico(String condicao) {
        StringBuilder sql = new StringBuilder();
        sql.append("select  distinct ")
            .append("      aud.* ")
            .append("    from ( ")
            .append("          select ")
            .append("            rev.usuario, ")
            .append("            rev.id as idRevisao, ")
            .append("            rev.datahora as datahora, ")
            .append("            'Econômico' as tipoCadastro, ")
            .append("            case when aud.REVTYPE = 0 then 'Inclusão' ")
            .append("                 when aud.REVTYPE = 1 then 'Alteração' ")
            .append("                 when aud.REVTYPE = 2 then 'Exclusão' ")
            .append("            end as tipoMovimento, ")
            .append("            aud.REVTYPE, ")
            .append("            aud.id as idMovimento, ")
            .append("            'CMC: ' || coalesce(ceaud.inscricaocadastral, '') as inscricao, ")
            .append("            (select ")
            .append("               case when pf.id is not null then 'Nome: ' else 'Razão Social: ' end || coalesce(pf.nome, pj.razaosocial) ")
            .append("               from pessoa ")
            .append("               left join pessoafisica pf on pf.id = pessoa.id ")
            .append("               left join pessoajuridica pj on pj.id = pessoa.id ")
            .append("               where pessoa.id = ceaud.pessoa_id ")
            .append("             ) as detalhe ")
            .append("          from cadastro_aud aud ")
            .append("            inner join cadastroeconomico_aud ceaud on ceaud.id = aud.ID ")
            .append("            inner join revisaoauditoria rev on rev.id = aud.rev ")
            .append("            inner join cadastro c on aud.id = c.id ")
            .append("            inner join cadastroeconomico bce on bce.id = c.id ")
            .append("            inner join enquadramentofiscal enq on enq.cadastroEconomico_id = bce.id and enq.fimVigencia is null ")
            .append("            left join ECONOMICOCNAE eCnae on eCnae.CADASTROECONOMICO_ID = bce.id ")
            .append("            left join CNAE cnae on cnae.ID = eCnae.CNAE_ID ")
            .append(condicao)
            .append(" ) aud ");
        Query q = em.createNativeQuery(sql.toString());
        return new AsyncResult<>(q.getResultList().size());
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public Future<Integer> getTotalRegistroAuditoriaCadastroContribuinteAnalitico(String condicao) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct ")
            .append("      aud.* ")
            .append("    from ( ")
            .append("          select ")
            .append("            rev.usuario as usuario, ")
            .append("            rev.id as idRevisao, ")
            .append("            rev.datahora as datahora, ")
            .append("            coalesce((select 'Pessoa Física' from pessoafisica_aud pf where pf.id = aud.id and id is not null and rownum = 1), 'Pessoa Jurídica') as tipoCadastro, ")
            .append("            case when aud.REVTYPE = 0 then 'Inclusão' ")
            .append("                 when aud.REVTYPE = 1 then 'Alteração' ")
            .append("                 when aud.REVTYPE = 2 then 'Exclusão' ")
            .append("            end as tipoMovimento, ")
            .append("            aud.REVTYPE, ")
            .append("            aud.id as idMovimento, ")
            .append("            coalesce((select FORMATACPFCNPJ(pf.CPF) from pessoafisica_aud pf where pf.id = aud.id and rownum = 1), (select FORMATACPFCNPJ(pj.CNPJ) from PESSOAJURIDICA_aud pj where pj.id = aud.id and rownum = 1)) as inscricao, ")
            .append("            coalesce((select pf.nome from pessoafisica_aud pf where pf.id = aud.id and rownum = 1), (select pj.RAZAOSOCIAL from PESSOAJURIDICA_aud pj where pj.id = aud.id and rownum = 1)) as detalhe ")
            .append("          from pessoa_aud aud ")
            .append("            inner join revisaoauditoria rev on rev.id = aud.rev ")
            .append("            inner join pessoa p on p.id = aud.id ")
            .append(condicao)
            .append(" ) aud ");
        Query q = em.createNativeQuery(sql.toString());
        return new AsyncResult<>(q.getResultList().size());
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<ByteArrayOutputStream> gerarRelatorio(String usuario, String caminhoReport,
                                                        String caminhoImagem, String arquivoJasper,
                                                        String tipoRelatorio, List resultado) throws IOException, JRException {

        ByteArrayOutputStream byteArrayOutputStream;
        try {
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            HashMap parameters = new HashMap();
            parameters.put("USUARIO", usuario);
            parameters.put("BRASAO", caminhoImagem);
            parameters.put("SUBREPORT_DIR", caminhoReport);
            parameters.put("NOME_PREFEITURA", "Prefeitura Municipal de Rio Branco");
            parameters.put("NOME_RELATORIO", "HISTÓRICO E AUDITORIA DO CADASTRO ");
            parameters.put("TIPO_RELATORIO", tipoRelatorio);
            JasperPrint jasperPrint = abstractReport.gerarBytesDeRelatorioComDadosEmCollectionView(caminhoReport, arquivoJasper, parameters, new JRBeanCollectionDataSource(resultado));
            byteArrayOutputStream = abstractReport.exportarJasperParaPDF(jasperPrint);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new AsyncResult<>(byteArrayOutputStream);
    }

    private String getUsuarioPorLogin(String login) {
        UsuarioSistema usuarioSistema = usuarioSistemaFacade.findOneByLogin(login.trim());
        if (usuarioSistema != null) {
            return usuarioSistema.getPessoaFisica().getNome();
        }
        return login;
    }

    private void definirTipoCadastroParaContribuinte(Object obj, AuditoriaCadastro item) {
        if ("Pessoa Física".equals(obj)) {
            item.setTipoCadastro(AuditoriaCadastro.TipoCadastro.PESSOA_FISICA);
        }
        if ("Pessoa Jurídica".equals(obj)) {
            item.setTipoCadastro(AuditoriaCadastro.TipoCadastro.PESSOA_JURIDICA);
        }
    }


    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public LogradouroFacade getLogradouroFacade() {
        return logradouroFacade;
    }

    public BairroFacade getBairroFacade() {
        return bairroFacade;
    }

    public PropriedadeFacade getPropriedadeFacade() {
        return propriedadeFacade;
    }

    public CompromissarioFacade getCompromissarioFacade() {
        return compromissarioFacade;
    }

    public NaturezaJuridicaFacade getNaturezaJuridicaFacade() {
        return naturezaJuridicaFacade;
    }

    public TipoAutonomoFacade getTipoAutonomoFacade() {
        return tipoAutonomoFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public AuditoriaFacade getAuditoriaFacade() {
        return auditoriaFacade;
    }
}
