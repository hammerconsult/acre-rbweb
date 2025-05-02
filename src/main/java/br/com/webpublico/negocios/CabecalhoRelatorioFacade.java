/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author Romanini
 */
@Stateless
public class CabecalhoRelatorioFacade extends AbstractFacade<CabecalhoRelatorio> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CabecalhoRelatorioFacade() {
        super(CabecalhoRelatorio.class);
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public String mesclaTags(String conteudo) {
        CharSequence string1 = "$NUMERO[if !mso]";
        CharSequence string2 = "$NUMERO";
        if (conteudo == null) {
            conteudo = "";
        }
        if (conteudo.contains(string1)) {
            conteudo = conteudo.replace(string1, string2);
        }

        Properties p = new Properties();
        p.setProperty("resource.loader", "string");
        p.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        VelocityEngine ve = new VelocityEngine();
        ve.init(p);

        StringResourceRepository repo = StringResourceLoader.getRepository();
        repo.putStringResource("myTemplate", conteudo);
        repo.setEncoding("UTF-8");
        VelocityContext context = new VelocityContext();
        tagsDataHoje(context, conteudo);
        tagsConfiguracaoDocumento(context);
        Template template = ve.getTemplate("myTemplate", "UTF-8");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    public void tagsDataHoje(VelocityContext context, String conteudo) {
        addTag(context, TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_ANO.name(), DataUtil.getAno(new Date()));
        addTag(context, TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_MES.name(), StringUtil.preencheString(DataUtil.getMes(new Date()) + "", 2, '0'));
        addTag(context, TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_DIA.name(), StringUtil.preencheString(DataUtil.getDia(new Date()) + "", 2, '0'));
        addTag(context, TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_MES_EXTENSO.name(), Mes.getMesToInt(DataUtil.getMes(new Date())).getDescricao());

        DateFormat dfmt = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy");
        addTag(context, TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_POR_EXTENSO.name(), dfmt.format(new Date()));
    }

    public void tagsConfiguracaoDocumento(VelocityContext context) {
        String quebra = "<div class=\"break\"></div>";
        addTag(context, TipoModeloDoctoOficial.TagsConfiguracaoDocumento.QUEBRA_PAGINA.name(), quebra);
        SistemaService sistemaService = (SistemaService) Util.getSpringBeanPeloNome("sistemaService");
        addTag(context, TipoModeloDoctoOficial.TagsConfiguracaoDocumento.USUARIO.name(), sistemaService.getUsuarioCorrente().toString());
        addTag(context, TipoModeloDoctoOficial.TagsConfiguracaoDocumento.UNIDADE_LOGADA_ADM.name(), sistemaService.getHierarquiAdministrativaCorrente().getDescricao().toString());
        addTag(context, TipoModeloDoctoOficial.TagsConfiguracaoDocumento.UNIDADE_LOGADA_ORC.name(), sistemaService.getHierarquiOrcamentariaCorrente().getDescricao().toString());
        addTag(context, TipoModeloDoctoOficial.TagsConfiguracaoDocumento.DATA_LOGADA.name(), new SimpleDateFormat("dd/MM/yyyy").format(sistemaService.getDataOperacao()));
        addTag(context, TipoModeloDoctoOficial.TagsConfiguracaoDocumento.EXERCICIO_LOGADO.name(), sistemaService.getExercicioCorrente().getAno().toString());
    }

    private void addTag(VelocityContext contexto, String chave, Object valor) {
        if (valor != null) {
            contexto.put(chave, valor);
        } else {
            contexto.put(chave, "");
        }
    }

    public String alteraURLImagens(String conteudo) {
        return FacesUtil.alteraURLImagens(conteudo);
    }

    public CabecalhoRelatorio validaPadraoPorUnidade(CabecalhoRelatorio cr) {
        String hql = "select c from CabecalhoRelatorio c where c.unidadeOrganizacional = :unidade and c.padrao is true";
        if (cr.getId() != null) {
            hql += " and c.id <> :id";
        }
        Query consulta = em.createQuery(hql);
        consulta.setParameter("unidade", cr.getUnidadeOrganizacional());
        consulta.setMaxResults(1);
        if (cr.getId() != null) {
            consulta.setParameter("id", cr.getId());
        }

        try {
            return (CabecalhoRelatorio) consulta.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<CabecalhoRelatorio> completaCabecalhoPorUnidade(String parte, UnidadeOrganizacional unidade, TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional) {
        String hql = "select c.* from cabecalhorelatorio c "
                + " where lower(c.nome) like :parte "
                + "   and unidadeorganizacional_id in ("
                + " select uo.id from unidadeorganizacional uo "
                + " inner join hierarquiaorganizacional ho on uo.id = ho.subordinada_id and ho.tipohierarquiaorganizacional = :tipo"
                + " and :data between ho.iniciovigencia and coalesce(ho.fimvigencia, :data)"
                + " start with ho.subordinada_id = :unidade"
                + " connect by prior ho.superior_id =ho.subordinada_id)" +
                " order by c.padrao desc";
        Query consulta = em.createNativeQuery(hql, CabecalhoRelatorio.class);
        consulta.setParameter("unidade", unidade.getId());
        consulta.setParameter("tipo", tipoHierarquiaOrganizacional.name());
        consulta.setParameter("data", new Date(), TemporalType.DATE);
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        return (ArrayList<CabecalhoRelatorio>) consulta.getResultList();
    }
}
