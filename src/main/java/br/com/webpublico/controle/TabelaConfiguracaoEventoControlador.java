/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigCreditoReceber;
import br.com.webpublico.entidades.ConfiguracaoEvento;
import br.com.webpublico.entidades.ConfiguracaoEvento.ModoListagem;
import br.com.webpublico.negocios.TabelaConfiguracaoEventoFacade;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.primefaces.component.selectoneradio.SelectOneRadio;
import org.primefaces.event.DateSelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Renato
 */
@ManagedBean
@ViewScoped
public class TabelaConfiguracaoEventoControlador implements Serializable {

    @EJB
    private TabelaConfiguracaoEventoFacade facade;
    protected ConfiguracaoEvento configuracaoEvento;
    protected EntidadeMetaData metadata;
    protected List lista = new ArrayList<>();
    private Date dataVigencia;
    private Date dataInicioVigencia;
    private Date dataFimVigencia;
    private String nomeDaClasse;

    public Date getDataInicioVigencia() {
        return dataInicioVigencia;
    }

    public void setDataInicioVigencia(Date dataInicioVigencia) {
        this.dataInicioVigencia = dataInicioVigencia;
    }

    public Date getDataFimVigencia() {
        return dataFimVigencia;
    }

    public void setDataFimVigencia(Date dataFimVigencia) {
        this.dataFimVigencia = dataFimVigencia;
    }

    public String getNomeDaClasse() {
        return nomeDaClasse;
    }

    public void setNomeDaClasse(String nomeDaClasse) {
        this.nomeDaClasse = nomeDaClasse;
    }

    public Date getDataVigencia() {
        return dataVigencia;
    }

    public void setDataVigencia(Date dataVigencia) {
        this.dataVigencia = dataVigencia;
    }

    public ConfiguracaoEvento getConfiguracaoEvento() {
        return configuracaoEvento;
    }

    public void setConfiguracaoEvento(ConfiguracaoEvento configuracaoEvento) {
        this.configuracaoEvento = configuracaoEvento;
    }

    public List getLista() {
        return lista;
    }

    public void setLista(List lista) {
        this.lista = lista;
    }

    public EntidadeMetaData getMetadata() {
        return metadata;
    }

    public void setMetadata(EntidadeMetaData metadata) {
        this.metadata = metadata;
    }

    public List<ConfiguracaoEvento.ModoListagem> getListaModoListagem() {
        List<ConfiguracaoEvento.ModoListagem> lista = new ArrayList<ConfiguracaoEvento.ModoListagem>();
        lista.addAll(Arrays.asList(ConfiguracaoEvento.ModoListagem.values()));
        return lista;
    }

    public boolean atribuirClasse(String nomeDaClasse) {
        if (configuracaoEvento == null) {
            this.nomeDaClasse = "br.com.webpublico.entidades." + nomeDaClasse;
            configuracaoEvento = new ConfiguracaoEvento();
            configuracaoEvento.setModoListagem(ModoListagem.VIGENTE);
            dataVigencia = null;
            dataFimVigencia = null;
            dataInicioVigencia = null;

        }
        listagem();
        return true;
    }

    public void listagem() {

        Class classe = null;
        try {
            classe = Class.forName(nomeDaClasse);
        } catch (ClassNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro !", "Classe não encontrada : " + nomeDaClasse));
        }
        metadata = new EntidadeMetaData(classe);

        lista = new ArrayList<ConfigCreditoReceber>();
        ModoListagem modoListagem = configuracaoEvento.getModoListagem();

        if (modoListagem.equals(ConfiguracaoEvento.ModoListagem.VIGENTE)) {
            lista = facade.getListaVigentesDecrescente(configuracaoEvento, classe);
        } else if (modoListagem.equals(ConfiguracaoEvento.ModoListagem.ENCERRADO)) {
            lista = facade.getListaEncerradosDecrescente(configuracaoEvento, classe, dataVigencia);
        } else if (modoListagem.equals(ConfiguracaoEvento.ModoListagem.TODOS)) {
            lista = facade.getListaEncerradosDecrescente(configuracaoEvento, classe, dataInicioVigencia, dataFimVigencia);
        }
    }

    public void setaDataParaListagem(DateSelectEvent evento) {
        dataVigencia = evento.getDate();
        listagem();
    }

    public void setaDataInicioParaListagem(DateSelectEvent evento) {
        dataVigencia = evento.getDate();
        listagem();
    }

    public void setaDataFimParaListagem(DateSelectEvent evento) {
        dataVigencia = evento.getDate();
        listagem();
    }

    public String getNomeEntidade() {
        Class entidade = this.configuracaoEvento.getClass();
        String nomeEntidade = "";
        nomeEntidade = entidade.getSimpleName();
        if (entidade.isAnnotationPresent(br.com.webpublico.util.anotacoes.Etiqueta.class)) {
            br.com.webpublico.util.anotacoes.Etiqueta e = (Etiqueta) entidade.getAnnotation(br.com.webpublico.util.anotacoes.Etiqueta.class);
            nomeEntidade = e.value();
        }
        return nomeEntidade;
    }

    public void setaModoListagem(javax.faces.event.AjaxBehaviorEvent event) {
        SelectOneRadio selectOneRadio = (SelectOneRadio) event.getSource();
        ModoListagem value = (ModoListagem) selectOneRadio.getValue();
        configuracaoEvento.setModoListagem(value);
    }

    public boolean podeEditar(ConfiguracaoEvento ev) {

        if (ev.getFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(ev.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(new Date())) >= 0) {
            return true;
        } else {
            return false;
        }
    }
}
