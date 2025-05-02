package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.PPAFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 08/10/13
 * Time: 11:52
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "relatorioDemonstrativoDespesaOrgaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-despesa-orgao-recursos", pattern = "/relatorio/demonstrativo-despesa-orgao-recursos/", viewId = "/faces/financeiro/relatorio/relatorio-demonstrativo-despesa-orgao.xhtml")
})
public class RelatorioDemonstrativoDespesaOrgaoControlador extends AbstractReport implements Serializable {

    @EJB
    private PPAFacade pPAFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private HierarquiaOrganizacional orgaoInicial;
    private HierarquiaOrganizacional orgaoFinal;
    private ConverterAutoComplete converterOrgaoInicial;
    private ConverterAutoComplete converterOrgaoFinal;

    @URLAction(mappingId = "relatorio-despesa-orgao-recursos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.orgaoInicial = null;
        this.orgaoFinal = null;
    }

    public PPAFacade getpPAFacade() {
        return pPAFacade;
    }

    public void setpPAFacade(PPAFacade pPAFacade) {
        this.pPAFacade = pPAFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public void setHierarquiaOrganizacionalFacade(HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade) {
        this.hierarquiaOrganizacionalFacade = hierarquiaOrganizacionalFacade;
    }

    public HierarquiaOrganizacional getOrgaoInicial() {
        return orgaoInicial;
    }

    public void setOrgaoInicial(HierarquiaOrganizacional orgaoInicial) {
        this.orgaoInicial = orgaoInicial;
    }

    public HierarquiaOrganizacional getOrgaoFinal() {
        return orgaoFinal;
    }

    public void setOrgaoFinal(HierarquiaOrganizacional orgaoFinal) {
        this.orgaoFinal = orgaoFinal;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public ConverterAutoComplete getConverterOrgaoInicial() {
        if (converterOrgaoInicial == null) {
            converterOrgaoInicial = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterOrgaoInicial;
    }

    public ConverterAutoComplete getConverterOrgaoFinal() {
        if (converterOrgaoInicial == null) {
            converterOrgaoInicial = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterOrgaoInicial;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        List<HierarquiaOrganizacional> lista = hierarquiaOrganizacionalFacade.filtraPorNivel(parte, "2", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaControlador.getDataOperacao());
        return lista;
    }

    public void gerarRelatorio() {
        try {

            Exercicio e = sistemaControlador.getExercicioCorrente();
            String arquivoJasper = "RelatorioDemonstrativoDespesaPorOrgao.jasper";
            HashMap parameters = new HashMap();
            parameters.put("MUNICIPIO", "Prefeitura Municipal de Rio Branco - AC");
            parameters.put("EXERCICIO", e.getId());
            parameters.put("ANO", e.getAno().toString());
            parameters.put("IMAGEM", getCaminhoImagem());
            parameters.put("DATA", DataUtil.getDataFormatada(sistemaControlador.getDataOperacao()));
            parameters.put("USUARIO", getUsuario());
            String sql = getSql();
            if (!sql.equals("")) {
                parameters.put("SQL", sql);
            } else {
                parameters.put("SQL", " 1 = 1 ");
            }
            gerarRelatorio(arquivoJasper, parameters);
        } catch (ExcecaoNegocioGenerica e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " Não foi possível gerar o Relatório.", e.getMessage()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, " Não foi possível gerar o Relatório.", ex.getMessage()));
        }
    }

    public String getSql() {
        if (this.orgaoInicial != null && this.orgaoFinal == null) {
            throw new ExcecaoNegocioGenerica("Selecione um Órgão Final para gerar o relatório");
        }
        if (this.orgaoInicial == null && this.orgaoFinal != null) {
            throw new ExcecaoNegocioGenerica("Selecione um Órgão Inicial para gerar o relatório");
        }

        StringBuilder stb = new StringBuilder();

        if (this.orgaoInicial != null) {
            stb.append(" vw_orgao.codigo  between ");
            stb.append("'" + orgaoInicial.getCodigo() + "'");
        }

        if (this.orgaoFinal != null) {
            stb.append(" AND ");
            stb.append("'" + orgaoFinal.getCodigo() + "'");
        }

        return stb.toString();
    }

    public String getUsuario() {
        if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
            return sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome();
        } else {
            return sistemaControlador.getUsuarioCorrente().getUsername();
        }
    }
}
