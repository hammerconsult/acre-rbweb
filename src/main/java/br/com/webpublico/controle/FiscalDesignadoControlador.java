package br.com.webpublico.controle;

import br.com.webpublico.entidades.FiscalDesignado;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FiscalDesignadoFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
public class FiscalDesignadoControlador extends PrettyControlador<FiscalDesignado> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(FiscalDesignadoControlador.class);

    @EJB
    private FiscalDesignadoFacade fiscalDesignadoFacade;

    @Override
    public String getCaminhoPadrao() {
        return null;
    }

    @Override
    public Object getUrlKeyValue() {
        return null;
    }

    @Override
    public AbstractFacade getFacede() {
        return fiscalDesignadoFacade;
    }

    public FiscalDesignadoControlador() {
        super(FiscalDesignado.class);
    }

    public List<UsuarioSistema> completarFiscalDesignado(String filtro) {
        return fiscalDesignadoFacade.completarFiscalDesignado(filtro);
    }
}
