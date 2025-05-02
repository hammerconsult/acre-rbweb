/*
 * Codigo gerado automaticamente em Mon Sep 12 10:22:37 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoPlanejamentoPublico;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoPlanejamentoPublicoFacade;
import br.com.webpublico.negocios.SistemaFacade;
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
public class ConfiguracaoPlanejamentoPublicoControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private ConfiguracaoPlanejamentoPublicoFacade configuracaoPlanejamentoPublicoFacade;
    private Date desde;

    public ConfiguracaoPlanejamentoPublicoControlador() {
        metadata = new EntidadeMetaData(ConfiguracaoPlanejamentoPublico.class);
        desde = new Date();
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public ConfiguracaoPlanejamentoPublicoFacade getFacade() {
        return configuracaoPlanejamentoPublicoFacade;
    }

    @Override
    public void novo() {
        super.novo();
        ConfiguracaoPlanejamentoPublico cpp = ((ConfiguracaoPlanejamentoPublico) selecionado);
        ConfiguracaoPlanejamentoPublico cppNew = configuracaoPlanejamentoPublicoFacade.retornaUltima();
        cpp.setMascaraCodigoPrograma(cppNew.getMascaraCodigoPrograma());
        cpp.setMascaraCodigoAcao(cppNew.getMascaraCodigoAcao());
        cpp.setMascaraCodigoSubAcao(cppNew.getMascaraCodigoSubAcao());
        cpp.setMascaraCodigoReceitaLOA(cppNew.getMascaraCodigoReceitaLOA());
        cpp.setNivelContaDespesaPPA(cppNew.getNivelContaDespesaPPA());
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoPlanejamentoPublicoFacade;
    }

    @Override
    public List getLista() {
        List<ConfiguracaoPlanejamentoPublico> l = new ArrayList<ConfiguracaoPlanejamentoPublico>();
        l.add(configuracaoPlanejamentoPublicoFacade.listaConfiguracaoPlanejamentoPublicoFiltrandoVigencia(desde));
        return l;
    }

    @Override
    public String salvar() {
        ((ConfiguracaoPlanejamentoPublico) selecionado).setDesde(SistemaFacade.getDataCorrente());
        return super.salvar();
    }

    public void setVigencia(DateSelectEvent event) {
        desde = event.getDate();
    }
}
