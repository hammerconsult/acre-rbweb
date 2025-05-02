/*
 * Codigo gerado automaticamente em Thu May 10 14:10:07 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ContadorAcessosPortal;
import br.com.webpublico.entidades.Cor;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContadorAcessosPortalFacade;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.LocalDate;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.LineChartSeries;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.*;

@ManagedBean(name = "contadorAcessoPortalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "verContadorAcessos", pattern = "/tributario/contador-acessos-portal/", viewId = "/faces/tributario/configuracao/contadoracessoportal/visualizar.xhtml")
})
public class ContadorAcessoPortalControlador extends PrettyControlador<ContadorAcessosPortal> implements Serializable, CRUD {

    @EJB
    private ContadorAcessosPortalFacade contadorAcessosPortalFacade;
    private Date inicio, fim;
    private Map<ContadorAcessosPortal.TipoAcesso, List<ContadorAcessosPortal>> acessos;
    private CartesianChartModel cartesianChartModel;
    private Long totalAcessos, totalUsuarios;

    public ContadorAcessoPortalControlador() {
        super(Cor.class);
    }

    @URLAction(mappingId = "verContadorAcessos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        acessos = Maps.newHashMap();
        LocalDate hoje = LocalDate.now();
        fim = hoje.toDate();
        inicio = hoje.minusMonths(1).toDate();
        totalAcessos = contadorAcessosPortalFacade.countTotalAcessos(ContadorAcessosPortal.TipoAcesso.LOGIN);
        totalUsuarios = contadorAcessosPortalFacade.countTotalContribuintesPortal();
        filtrarAcessos();
    }

    public void filtrarAcessos() {
        acessos = Maps.newHashMap();
        List<ContadorAcessosPortal> consulta = contadorAcessosPortalFacade.findByDataInicialAndFinal(inicio, fim);
        Collections.sort(consulta);
        for (ContadorAcessosPortal contadorAcessosPortal : consulta) {
            if (!acessos.containsKey(contadorAcessosPortal.getTipoAcesso())) {
                acessos.put(contadorAcessosPortal.getTipoAcesso(), new ArrayList<ContadorAcessosPortal>());
            }
            acessos.get(contadorAcessosPortal.getTipoAcesso()).add(contadorAcessosPortal);
        }
        popularGrafico();
    }

    public List<ContadorAcessosPortal.TipoAcesso> getTiposAcessos() {
        return Lists.newArrayList(acessos.keySet());
    }

    public Map<ContadorAcessosPortal.TipoAcesso, List<ContadorAcessosPortal>> getAcessos() {
        return acessos;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public CartesianChartModel getCartesianChartModel() {
        return cartesianChartModel;
    }

    public Long getTotalAcessos() {
        return totalAcessos;
    }

    public Long getTotalUsuarios() {
        return totalUsuarios;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/contador-acessos-portal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return contadorAcessosPortalFacade;
    }

    private void popularGrafico() {
        cartesianChartModel = new CartesianChartModel();
        for (ContadorAcessosPortal.TipoAcesso tipoAcesso : acessos.keySet()) {
            LineChartSeries serie = new LineChartSeries(tipoAcesso.getDescricao());
            Map<String, List<ContadorAcessosPortal>> agrupadoPorData = Maps.newHashMap();
            List<ContadorAcessosPortal> contadorAcessosPortals = acessos.get(tipoAcesso);
            Collections.sort(contadorAcessosPortals);
            for (ContadorAcessosPortal contadorAcessosPortal : contadorAcessosPortals) {
                String dataFormatada = DataUtil.getDataFormatada(contadorAcessosPortal.getAcessoEm(), "dd/MM/yy");
                if (!agrupadoPorData.containsKey(dataFormatada)) {
                    agrupadoPorData.put(dataFormatada, Lists.<ContadorAcessosPortal>newArrayList());
                }
                agrupadoPorData.get(dataFormatada).add(contadorAcessosPortal);
            }
            for (String s : agrupadoPorData.keySet()) {
                serie.set(s, agrupadoPorData.get(s).size());
            }
            cartesianChartModel.addSeries(serie);
        }
    }

}
