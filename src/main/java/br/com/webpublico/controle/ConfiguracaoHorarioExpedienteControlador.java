/*
 * Codigo gerado automaticamente em Thu Oct 06 16:26:42 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoHorarioExpediente;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoHorarioExpedienteFacade;
import br.com.webpublico.util.EntidadeMetaData;
import org.primefaces.event.DateSelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.Date;

@ManagedBean
@SessionScoped
public class ConfiguracaoHorarioExpedienteControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private ConfiguracaoHorarioExpedienteFacade configuracaoHorarioExpedienteFacade;
    private Date desde;

    public ConfiguracaoHorarioExpedienteControlador() {
        metadata = new EntidadeMetaData(ConfiguracaoHorarioExpediente.class);
        desde = new Date();
    }

    public ConfiguracaoHorarioExpedienteFacade getFacade() {
        return configuracaoHorarioExpedienteFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoHorarioExpedienteFacade;
    }

    @Override
    public void novo() {
        super.novo();
        ConfiguracaoHorarioExpediente che = (ConfiguracaoHorarioExpediente) selecionado;
        che.setDesde(new Date());
    }

    public ConfiguracaoHorarioExpediente getListar() {
        return configuracaoHorarioExpedienteFacade.listaFiltrandoVigencia(desde);
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public void setVigencia(DateSelectEvent event) {
        desde = event.getDate();
    }
}
