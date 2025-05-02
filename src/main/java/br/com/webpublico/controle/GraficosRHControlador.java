package br.com.webpublico.controle;


import br.com.webpublico.negocios.GraficosRHFacade;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.chart.PieChartModel;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;


/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 04/09/13
 * Time: 10:23
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "graficosRHControlador")
@SessionScoped
@URLMappings(mappings = {
        @URLMapping(id = "mostrarGraficos", pattern = "/graficosrh/mostrar/", viewId = "/faces/rh/administracaodepagamento/graficos/graficos.xhtml"),
//        @URLMapping(id = "verMedico", pattern = "/medico/ver/#{medicoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/medico/visualizar.xhtml")
})
public class GraficosRHControlador implements Serializable {

    public GraficosRHControlador() {

    }

    private PieChartModel pieModel;
    @EJB
    private GraficosRHFacade graficosRHFacade;

    public PieChartModel getPieModel() {
        return pieModel;
    }

    public void setPieModel(PieChartModel pieModel) {
        this.pieModel = pieModel;
    }

    public void createPieModelServidoresCalculados() {
        pieModel = new PieChartModel();
//        TotalServidoresCalculados total = graficosRHFacade.recuperaDadosCalculo(8, 2013);
//        //System.out.println(total);
//        pieModel.set("Servidores Calculados", total.getTotalServidoresCalculados());
//        pieModel.set("Servidores Não Calculados", total.getTotalNaoCalculados());
    }


}
