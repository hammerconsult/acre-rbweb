package br.com.webpublico.controle;

import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean(name = "filtrarVariosRegistrosControlador")
@ViewScoped
public class FiltrarVariosRegistrosControlador {

    private Object registro;

    public FiltrarVariosRegistrosControlador() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    public Object getRegistro() {
        return registro;
    }

    public void setRegistro(Object registro) {
        this.registro = registro;
    }

    public void add(List registros) {
        registros.add(registro);
        registro = null;
    }

    public void del(List registros, Object registro) {
        registros.remove(registro);
    }
}
