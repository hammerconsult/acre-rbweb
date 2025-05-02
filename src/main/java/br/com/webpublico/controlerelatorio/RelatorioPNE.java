package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
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
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by carlos on 17/11/15.
 */
@ManagedBean(name = "relatorioPNEControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioPNE", pattern = "/rh/servidores-pne", viewId = "/faces/rh/relatorios/relatorioservidorespne.xhtml")
})
public class RelatorioPNE extends AbstractReport implements Serializable {
    private Date dataInicial;
    private Date dataFinal;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private String filtros;

    public RelatorioPNE() {
        geraNoDialog = true;
    }

    @URLAction(mappingId = "relatorioPNE", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        hierarquiaOrganizacional = null;
        dataInicial = null;
        dataFinal = null;
    }


    public void gerarRelatorioServidoresPNE() throws JRException, IOException {
        if (isValidaCampos()) {
            String arquivo = "relatorioPNE.jasper";
            HashMap parameter = new HashMap();
            parameter.put("IMAGEM", super.getCaminhoImagem());
            parameter.put("DATA_INICIAL", dataInicial);
            parameter.put("DATA_FINAL", dataFinal);
            parameter.put("WHERE", isCriterio());

            gerarRelatorio(arquivo, parameter);
        }
    }

    public boolean isValidaCampos() {
        boolean valida = true;
        if (hierarquiaOrganizacional == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("O campo Local de Trabalho é obrigatório.");
        }
        if (dataInicial == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("O campo Data Inicial é obrigatório.");
        }

        if (dataFinal == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("O campo Data Final é obrigatório.");
        }
        return valida;
    }

    public String isCriterio() {
        StringBuilder sb = new StringBuilder(" WHERE 1 = 1 ");
        String juncao = " and ";
        filtros = "";

        sb.append(juncao).append("HO.ID = " + hierarquiaOrganizacional.getId()).append("").append(" ");
        sb.append(juncao).append("PF.DEFICIENTEFISICO = " + 1).append("").append(" ");
        sb.append(juncao).append("TO_DATE('" + DataUtil.getDataFormatada(dataInicial) + "','dd/mm/yyyy') " +
            "BETWEEN CARGO.INICIOVIGENCIA AND COALESCE(CARGO.FINALVIGENCIA, TO_DATE('" + DataUtil.getDataFormatada(dataFinal)
            + "', 'dd/mm/yyyy')").append(" )").append(" ");
        sb.append(juncao).append("TO_DATE('" + DataUtil.getDataFormatada(dataInicial) + "','dd/mm/yyyy') " +
            "BETWEEN V.INICIOVIGENCIA AND COALESCE(CARGO.FINALVIGENCIA, TO_DATE('" + DataUtil.getDataFormatada(dataFinal)
            + "', 'dd/mm/yyyy')").append(" )").append(" ");

        return sb.toString();
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaOrganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public void setHierarquiaOrganizacionalSelecionada(SelectEvent evt) {
        hierarquiaOrganizacional = (HierarquiaOrganizacional) evt.getObject();
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }
}
