package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paschualleto
 * Date: 26/08/14
 * Time: 14:07
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-verbas-por-orgao-geral", pattern = "/relatorio/relatorio-verbas-por-orgao-geral/", viewId = "/faces/rh/relatorios/relatorioverbaspororgaogeral.xhtml")
})
public class RelatorioVerbasPorOrgaoGeralControlador extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacadeNovo;
    private ConverterAutoComplete converterHierarquia;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private Integer mes;
    private Integer ano;
    private Boolean todosOrgaos;

    public RelatorioVerbasPorOrgaoGeralControlador() {
        geraNoDialog = Boolean.TRUE;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public Boolean getTodosOrgaos() {
        return todosOrgaos;
    }

    public void setTodosOrgaos(Boolean todosOrgaos) {
        this.todosOrgaos = todosOrgaos;
    }

    public ConverterAutoComplete getConverterHierarquia() {
        if (hierarquiaOrganizacionalSelecionada == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacadeNovo);
        }
        return converterHierarquia;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacadeNovo.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public List<SelectItem> getTipoDeFolhaPagamentos() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }


    public void gerarRelatorio() throws JRException, IOException {
        if (validaCampos()) {
            HashMap parameter = new HashMap();
            String imagem = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/img");
            imagem += "/";

            parameter.put("BRASAO", imagem);
            parameter.put("MODULO", "Recursos Humanos");
            parameter.put("MES", mes - 1);
            parameter.put("ANO", ano);
            parameter.put("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
            parameter.put("NOMERELATORIO", "RELATÓRIO DE VERBAS POR ÓRGÃO GERAL");
            parameter.put("DATAOPERACAO", UtilRH.getDataOperacao());
            parameter.put("TIPOFOLHA", tipoFolhaDePagamento.name());
            String arquivo = "";
            if (todosOrgaos) {
                arquivo = "RelatorioVerbasPorOrgaoGeralSemOrgao.jasper";
            } else {
                arquivo = "RelatorioVerbasPorOrgaoGeral.jasper";
                parameter.put("CODIGOHO", " and ho.codigo like '" + hierarquiaOrganizacionalSelecionada.getCodigoSemZerosFinais() + "%'");
                parameter.put("HO", hierarquiaOrganizacionalSelecionada.toString());
            }
            if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
                parameter.put("USUARIO", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
            } else {
                parameter.put("USUARIO", sistemaControlador.getUsuarioCorrente().getLogin());
            }
            //System.out.println(parameter);
            gerarRelatorio(arquivo, parameter);
        }
    }

    @URLAction(mappingId = "relatorio-verbas-por-orgao-geral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.mes = null;
        this.ano = null;
        this.hierarquiaOrganizacionalSelecionada = null;
        this.tipoFolhaDePagamento = null;
        todosOrgaos = Boolean.TRUE;
    }

    public Boolean validaCampos() {
        if (mes == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:mes", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "O campo Mês é obrigatório"));
            return false;
        }
        if (ano == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:ano", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "O campo Ano é obrigatório"));
            return false;
        }
        if (!todosOrgaos && hierarquiaOrganizacionalSelecionada == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:orgao", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "O campo Órgão é obrigatório"));
            return false;
        }
        if (tipoFolhaDePagamento == null){
            FacesContext.getCurrentInstance().addMessage("Formulario:tipo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "Informe um Tipo de Folha de Pagamento"));
            return false;
        }
        return true;
    }
}
