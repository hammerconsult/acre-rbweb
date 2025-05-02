/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import net.sf.jasperreports.engine.JRException;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author
 */
@ManagedBean
public class RelatorioHierarquiaOrganizacionalControle extends RelatorioGenerico implements Serializable {


    private Date dataDeReferencia;

    private TipoHierarquiaOrganizacional tipoTemp;


    public TipoHierarquiaOrganizacional getTipoTemp() {
        return tipoTemp;
    }

    public void setTipoTemp(TipoHierarquiaOrganizacional tipoTemp) {
        this.tipoTemp = tipoTemp;
    }

//   public List<SelectItem> getTipos() {
//        List<SelectItem> toReturn = new ArrayList<SelectItem>();
//        toReturn.add(new SelectItem("", ""));
//        for(TipoHierarquiaOrganizacional t: TipoHierarquiaOrganizacional.values()){
//            toReturn.add(new SelectItem(t));
//        }
//        return toReturn;
//    }

    public List<SelectItem> getTipos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem("", ""));
        List t = Arrays.asList(TipoHierarquiaOrganizacional.values());
        toReturn.addAll(t);
        return toReturn;
    }

    public Date getDataDeReferencia() {
        return dataDeReferencia;
    }

    public void setDataDeReferencia(Date dataDeReferencia) {
        this.dataDeReferencia = dataDeReferencia;
    }


    public void limpaCampos() {
        dataDeReferencia = null;

    }

    public String formataData(Date data) {
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        return formatador.format(data);
    }


    public void validaCampos() throws JRException, IOException {
        if (dataDeReferencia != null) {
            gerarRelatorioHierarquia();
        } else {
            //System.out.println("Data não pode ser nula");
        }
    }


    public void gerarRelatorioHierarquia() throws JRException, IOException {
        String arquivoJasper = "RelatorioHierarquia.jasper";

        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.responseComplete();
//        ServletContext scontext = (ServletContext) facesContext.getExternalContext().getContext();
//        String subReport = scontext.getRealPath("/WEB-INF/report/Cabecalho_subreport1.jasper");

        //String imgdef = scontext.getRealPath("/img/Brasao_de_Rio_Branco.gif");
        //System.out.println("...:" + tipoTemp);
        //System.out.println("...:" + formataData(dataDeReferencia));


        HashMap parameters = new HashMap();
        String retorno;
        if (TipoHierarquiaOrganizacional.ADMINISTRATIVA.equals(tipoTemp)) {
            retorno = "ADMINISTRATIVA";
        } else {
            retorno = "ORCAMENTARIA";
        }
        parameters.put("TIPO", retorno);
//        parameters.put("UNIDADE_ORG", getUnidadeOrganizacionalRaiz().getId());
        parameters.put("DATA", dataDeReferencia);


        gerarRelatorio(arquivoJasper, parameters);

    }
}
