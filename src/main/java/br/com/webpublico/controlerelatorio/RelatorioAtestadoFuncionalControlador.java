package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hudson on 06/11/15.
 */

@ManagedBean(name = "relatorioAtestadoFuncionalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-atestado-funcional", pattern = "/relatorio/relatorio-atestado-funcional/", viewId = "/faces/rh/relatorios/relatorioatestadofuncional.xhtml")
})
public class RelatorioAtestadoFuncionalControlador extends AbstractReport implements Serializable {

    private ContratoFP contratoFP;
    private HierarquiaOrganizacional orgao;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade orgaoFacade;
    private ConverterAutoComplete converterServidor;
    private ConverterAutoComplete converterOrgao;
    private String filtros;
    public String opcaoRelatorio;

    public RelatorioAtestadoFuncionalControlador() {
        geraNoDialog = Boolean.TRUE;
    }

    public List<ContratoFP> completarServidor(String parte) {
        return contratoFPFacade.recuperaContratoMatricula(parte.trim());
    }

    public List<HierarquiaOrganizacional> completarOrgao(String parte) {
        return orgaoFacade.filtraPorNivel(parte.trim(), "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public ConverterAutoComplete getConverterServidor() {
        if (converterServidor == null) {
            converterServidor = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterServidor;
    }

    public ConverterAutoComplete getConverterOrgao() {
        if (converterOrgao == null) {
            converterOrgao = new ConverterAutoComplete(HierarquiaOrganizacional.class, orgaoFacade);
        }
        return converterOrgao;
    }

    public boolean validarFiltros() {
        boolean valida = true;
        if (opcaoRelatorio.equals("S")) {
            if (contratoFP == null) {
                FacesUtil.addCampoObrigatorio("Informe a matricula.");
                valida = false;
            }
        }

        if (opcaoRelatorio.equals("O")) {
            if (orgao == null) {
                FacesUtil.addCampoObrigatorio("Informe o órgão");
                valida = false;
            }
        }

        return valida;
    }

    public void limparCampos() {
        contratoFP = null;
        orgao = null;
    }


    public String isCriterio() {
        StringBuilder sb = new StringBuilder(" WHERE 1 = 1 ");
        String juncao = " and ";
        filtros = "";

        if (contratoFP != null) {
            sb.append(juncao).append(" vfp.id = ").append(contratoFP.getId()).append("");
            filtros += contratoFP;
        }

        if (contratoFP == null && orgao != null) {
            sb.append(juncao).append(" ho.codigo = '").append(orgao.getCodigo()).append("' ");
            filtros += orgao;
        }
        return sb.toString();
    }

    public void gerarRelatorio() throws JRException, IOException {
        if (validarFiltros()) {

            String arquivoJasper = "RelatorioDeAtestadoFuncional.jasper";
            HashMap parameters = new HashMap();

            parameters.put("IMAGEM", super.getCaminhoImagem());
            parameters.put("USUARIO", usuarioLogado().getNome());
            parameters.put("MODULO", "Recursos Humanos");
            parameters.put("WHERE", isCriterio());
            gerarRelatorio(arquivoJasper, parameters);
            limparCampos();
        }
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public void setContratoFPFacade(ContratoFPFacade contratoFPFacade) {
        this.contratoFPFacade = contratoFPFacade;
    }

    public HierarquiaOrganizacional getOrgao() {
        return orgao;
    }

    public void setOrgao(HierarquiaOrganizacional orgao) {
        this.orgao = orgao;
    }

    public HierarquiaOrganizacionalFacade getOrgaoFacade() {
        return orgaoFacade;
    }

    public void setOrgaoFacade(HierarquiaOrganizacionalFacade orgaoFacade) {
        this.orgaoFacade = orgaoFacade;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public String getOpcaoRelatorio() {
        return opcaoRelatorio;
    }

    public void setOpcaoRelatorio(String opcaoRelatorio) {
        this.opcaoRelatorio = opcaoRelatorio;
    }
}
