/*
 * Codigo gerado automaticamente em Thu Jun 16 13:52:10 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoPatrimonial;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoPatrimonialFacade;
import br.com.webpublico.util.EntidadeMetaData;
import org.primefaces.event.DateSelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@SessionScoped
public class ConfiguracaoPatrimonialControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private ConfiguracaoPatrimonialFacade configuracaoPatrimonialFacade;
    private Date desde;

    public ConfiguracaoPatrimonialControlador() {
        metadata = new EntidadeMetaData(ConfiguracaoPatrimonial.class);
        desde = new Date();
    }

    public ConfiguracaoPatrimonialFacade getFacade() {
        return configuracaoPatrimonialFacade;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoPatrimonialFacade;
    }

    @Override
    public List getLista() {
        List<ConfiguracaoPatrimonial> l = new ArrayList<ConfiguracaoPatrimonial>();
        l.add(configuracaoPatrimonialFacade.listaConfiguracaoPatrimonialFiltrandoVigencia(desde));
        return l;
    }

    public void setVigencia(DateSelectEvent event) {
        desde = event.getDate();
    }
}
