package br.com.webpublico.controle;

import br.com.webpublico.enums.TipoRelatorio;
import br.com.webpublico.entidadesauxiliares.AtributoRelatorioGenerico;
import br.com.webpublico.entidadesauxiliares.RelatorioPesquisaGenerico;
import br.com.webpublico.negocios.RecuperadorFacade;
import br.com.webpublico.negocios.RelatorioPesquisaGenericoFacade;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.Persistencia;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 06/11/13
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class VisualizarCamposControlador implements Serializable {

    @EJB
    private RelatorioPesquisaGenericoFacade relatorioPesquisaGenericoFacade;
    @EJB
    private RecuperadorFacade recuperadorFacade;
    private Object objeto;
    private RelatorioPesquisaGenerico relatorioPesquisaGenerico;

    public void novo(Object objeto) {
        this.objeto = objeto;
        if (objeto != null) {
            novoRelatorioGenericoTabela();
        }
    }

    public List<RelatorioPesquisaGenerico> listaRelatorioPesquisaGenericoTabela() {
        return relatorioPesquisaGenericoFacade.listaFiltrandoPorClasse("", objeto.getClass().getName(), TipoRelatorio.TABELA);
    }

    public String preencherCampo(Object objeto, AtributoRelatorioGenerico atributo) {
        return recuperadorFacade.preencherCampo(objeto, atributo);
    }

    public void novoRelatorioGenericoTabela() {
        try {
            Object o = objeto.getClass().newInstance();
            EntidadeMetaData entidadeMetaData = new EntidadeMetaData(objeto.getClass());
            relatorioPesquisaGenerico = relatorioPesquisaGenericoFacade.recuperaRelatorioPorClasseTipoPadrao(objeto.getClass().getName(), TipoRelatorio.TABELA);
            if (relatorioPesquisaGenerico == null) {
                relatorioPesquisaGenerico = new RelatorioPesquisaGenerico(o, entidadeMetaData);
                relatorioPesquisaGenerico.setEntidadeMetaData(entidadeMetaData);
            } else {
                relatorioPesquisaGenerico.setObjetoSelecionadoRelatorio(o);
                relatorioPesquisaGenerico.setEntidadeMetaData(entidadeMetaData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNomeDaClasse() {
        if (objeto != null) {
            return Persistencia.getNomeDaClasse(objeto.getClass());
        }
        return " - ";
    }

    public RelatorioPesquisaGenerico getRelatorioPesquisaGenerico() {
        return relatorioPesquisaGenerico;
    }

    public void setRelatorioPesquisaGenerico(RelatorioPesquisaGenerico relatorioPesquisaGenerico) {
        this.relatorioPesquisaGenerico = relatorioPesquisaGenerico;
    }

    public Object getObjeto() {
        return objeto;
    }

    public void setObjeto(Object objeto) {
        this.objeto = objeto;
    }
}
