/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CabecalhoRelatorio;
import br.com.webpublico.entidades.Cor;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.entidades.TipoModeloDoctoOficial;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CabecalhoRelatorioFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Romanini
 */
@ManagedBean(name = "cabecalhoRelatorioControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-cabecalho-relatorio", pattern = "/cabecalho-relatorio/novo/", viewId = "/faces/admin/cabecalhorelatorio/edita.xhtml"),
        @URLMapping(id = "editar-cabecalho-relatorio", pattern = "/cabecalho-relatorio/editar/#{cabecalhoRelatorioControlador.id}/", viewId = "/faces/admin/cabecalhorelatorio/edita.xhtml"),
        @URLMapping(id = "ver-cabecalho-relatorio", pattern = "/cabecalho-relatorio/ver/#{cabecalhoRelatorioControlador.id}/", viewId = "/faces/admin/cabecalhorelatorio/visualizar.xhtml"),
        @URLMapping(id = "listar-cabecalho-relatorio", pattern = "/cabecalho-relatorio/listar/", viewId = "/faces/admin/cabecalhorelatorio/lista.xhtml")
})
public class CabecalhoRelatorioControlador extends PrettyControlador<CabecalhoRelatorio> implements Serializable, CRUD {

    @EJB
    private CabecalhoRelatorioFacade cabecalhoRelatorioFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private CabecalhoRelatorio cabecalhoRelatorioPadrao;

    public CabecalhoRelatorioControlador() {
        super(CabecalhoRelatorio.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return cabecalhoRelatorioFacade;
    }

    @URLAction(mappingId = "novo-cabecalho-relatorio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ADMINISTRATIVA);
    }

    @URLAction(mappingId = "ver-cabecalho-relatorio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        hierarquiaOrganizacional = cabecalhoRelatorioFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(new Date(), selecionado.getUnidadeOrganizacional(), selecionado.getTipoHierarquiaOrganizacional());
        if (selecionado.getConteudo().contains("src=\"../../img/Brasao_de_Rio_Branco.gif\"")) {
            selecionado.setConteudo(selecionado.getConteudo().replace("src=\"../../img/Brasao_de_Rio_Branco.gif\"", "src=\"../../../img/Brasao_de_Rio_Branco.gif\""));
        }
    }

    @URLAction(mappingId = "editar-cabecalho-relatorio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        hierarquiaOrganizacional = cabecalhoRelatorioFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(new Date(), selecionado.getUnidadeOrganizacional(), selecionado.getTipoHierarquiaOrganizacional());
        if (selecionado.getConteudo().contains("src=\"../../img/Brasao_de_Rio_Branco.gif\"")) {
            selecionado.setConteudo(selecionado.getConteudo().replace("src=\"../../img/Brasao_de_Rio_Branco.gif\"", "src=\"../../../img/Brasao_de_Rio_Branco.gif\""));
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cabecalho-relatorio/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    //GETTERS E SETTERS
    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public CabecalhoRelatorio getCabecalhoRelatorioPadrao() {
        return cabecalhoRelatorioPadrao;
    }

    public void setCabecalhoRelatorioPadrao(CabecalhoRelatorio cabecalhoRelatorioPadrao) {
        this.cabecalhoRelatorioPadrao = cabecalhoRelatorioPadrao;
    }

    public List<SelectItem> recuperaTiposHierarquia() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        for (TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional1 : TipoHierarquiaOrganizacional.values()) {
            retorno.add(new SelectItem(tipoHierarquiaOrganizacional1, tipoHierarquiaOrganizacional1.getDescricao()));
        }
        return retorno;
    }
    //METODOS COMPLETA

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return cabecalhoRelatorioFacade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaHorganizacionalTipo(parte.trim(), selecionado.getTipoHierarquiaOrganizacional().name(), new Date());
    }

    //CONVERTERS
    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, cabecalhoRelatorioFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterHierarquiaOrganizacional;
    }

    //METODOS UTILIZADOS PELA VIEW
    public void setaUnidade(SelectEvent event) {
        hierarquiaOrganizacional = (HierarquiaOrganizacional) event.getObject();
        if (hierarquiaOrganizacional != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        }
    }

    public void setaNullHierarquia() {
        hierarquiaOrganizacional = null;
    }

    public void adicionaCabecalho() {
        String caminhoDaImagem = cabecalhoRelatorioFacade.getDocumentoOficialFacade().geraUrlImagemDir() + "img/Brasao_de_Rio_Branco.gif";
        String secretaria = selecionado.getUnidadeOrganizacional() != null ? selecionado.getUnidadeOrganizacional().getDescricao() : "Secretaria Municipal de Finanças";

        String conteudo =
                "<table style=\"width: 100%;\">"
                        + "<tbody>"
                        + "<tr>"
                        + "<td style=\"text-align: left;\" align=\"right\"><img src=\"" + caminhoDaImagem + "\" alt=\"PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO\" /></td>"
                        + "<td><span style=\"font-size: small;\">PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO</span><br /><span style=\"font-size: small;\">" + secretaria + "</span> <strong><br /></strong></td>"
                        + "</tr>"
                        + "</tbody>"
                        + "</table>";
        RequestContext.getCurrentInstance().execute("insertAtCursor('" + conteudo + "')");
    }

    public void adicionaDtaHojeAno() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_ANO.name() + "')");
    }

    public void adicionaDtaHojeMes() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_MES.name() + "')");
    }

    public void adicionaDtaHojeMesExtenso() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_MES_EXTENSO.name() + "')");
    }

    public void adicionaDtaHojeDia() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_DIA.name() + "')");
    }

    public void adicionaDtaHojePorExtenso() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_POR_EXTENSO.name() + "')");
    }

    public void adicionaQuebraPagina() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsConfiguracaoDocumento.QUEBRA_PAGINA.name() + "')");
    }

    public void adicionaUsuario() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsConfiguracaoDocumento.USUARIO.name() + "')");
    }

    public void adicionaUnidadeLogadaADM() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsConfiguracaoDocumento.UNIDADE_LOGADA_ADM.name() + "')");
    }

    public void adicionaUnidadeLogadaORC() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsConfiguracaoDocumento.UNIDADE_LOGADA_ORC.name() + "')");
    }

    public void adicionaExercicioLogado() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsConfiguracaoDocumento.EXERCICIO_LOGADO.name() + "')");
    }

    public void adicionaDataLogada() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsConfiguracaoDocumento.DATA_LOGADA.name() + "')");
    }

    public void adicionarStringNoEditor(String valor) {
        RequestContext.getCurrentInstance().execute("insertAtCursor('" + valor + "')");
    }

    public String mesclarTags() {
        return mesclarTags(selecionado.getConteudo());
    }

    public String mesclarTagsCabecalhoPadraoRecuperado() {
        if (cabecalhoRelatorioPadrao != null) {
            return mesclarTags(cabecalhoRelatorioPadrao.getConteudo());
        }
        return "";
    }

    public String mesclarTags(String conteudo) {
        return cabecalhoRelatorioFacade.mesclaTags(conteudo);
    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado)) {
            cabecalhoRelatorioPadrao = cabecalhoRelatorioFacade.validaPadraoPorUnidade(selecionado);
            if (cabecalhoRelatorioPadrao != null) {
                RequestContext.getCurrentInstance().update("FormularioCabecalhoPadrao");
                RequestContext.getCurrentInstance().execute("dialogCabecalhoPadrao.show()");
            } else {
                salvarAlternativo();
            }
        }
    }

    public void alteraCabecalhoPadrao() {
        selecionado.setPadrao(Boolean.TRUE);
        cabecalhoRelatorioPadrao.setPadrao(Boolean.FALSE);
        cabecalhoRelatorioFacade.salvar(cabecalhoRelatorioPadrao);
        cabecalhoRelatorioFacade.salvar(selecionado);
        redireciona();
    }

    public void salvarAlternativo() {
        selecionado.setPadrao(Boolean.FALSE);
        super.salvar();
    }
}
