/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.*;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Munif
 */
public abstract class SuperControladorCRUD implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(SuperControladorCRUD.class);

    @EJB
    private SistemaFacade sistemaFacade;
    protected static final int REGISTROS_POR_PAGINA = 4;
    protected List lista;
    protected Object selecionado;
    protected EntidadeMetaData metadata;
    protected Operacoes operacao;
    private String caminho;
    private String filtro = "";
    private int inicio = 0;
    private int maximoRegistrosTabela;
    private String sessao;

    public abstract AbstractFacade getFacede();

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public List getLista() {
        if (lista == null) {
            lista = getFacede().lista();
        }
        return lista;
    }

    public List getListaDecrescente() {
        if (lista == null) {
            lista = getFacede().listaDecrescente();
        }
        return lista;
    }

    public int getMaximoRegistrosTabela() {
        return maximoRegistrosTabela;
    }

    public void setMaximoRegistrosTabela(int maximoRegistrosTabela) {
        this.maximoRegistrosTabela = maximoRegistrosTabela;
    }

    public void selecionar(ActionEvent evento) {
        operacao = Operacoes.EDITAR;
        selecionado = evento.getComponent().getAttributes().get("objeto");
    }

    public void listar() {
        lista = null;
    }

    public String getSessao() {
        return sessao;
    }

    public void setSessao(String sessao) {
        this.sessao = sessao;
    }

    public void novo() {
        sessao = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("sessao");
        lista = null;
        operacao = Operacoes.NOVO;
        try {
            selecionado = metadata.getEntidade().newInstance();
        } catch (Exception ex) {
            logger.error("Erro ao instanciar nova entidade", ex);
        }
    }

    public void setSelecionado(Object selecionado) {
        this.selecionado = selecionado;
    }

    public EntidadeMetaData getMetadata() {
        return metadata;
    }

    /**
     * @return
     */
    public String salvar() {
        if (validaCampos() == true) {
            try {

                if (operacao == Operacoes.NOVO) {
                    this.getFacede().salvarNovo(selecionado);
                } else {
                    this.getFacede().salvar(selecionado);
                }
                lista = null;
                if (isCadastroDerivado()) {
                    return "";
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com Sucesso", ""));
                return caminho;

            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage()));
                return "edita.xhtml";
            }
        } else {
            return "edita.xhtml";
        }
    }

    public Boolean validaCampos() {
        return Util.validaCampos(selecionado);
    }

    public String caminho() {
        return caminho;
    }

    public void excluir(ActionEvent evento) {
        selecionado = evento.getComponent().getAttributes().get("objeto");
        getFacede().remover(selecionado);
        lista = null;
    }

    public void excluirSelecionado() {
        try {
            getFacede().remover(selecionado);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Excluído com sucesso!", "O registro selecionado foi excluído com sucesso!"));
            lista = null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage()));
        }
    }

    public Object getSelecionado() {
        return selecionado;
    }

    public String vaiVizualizar() {
        return "vizualizar";
    }

    public Exercicio getExercicioCorrente() {
        return sistemaFacade.getExercicioCorrente();
    }

    public void cancelar() {
        //TODO Apenas fazer isso se o objeto foi alterado, por exemplo, foi tentado gravar e na validação não passou, depois cancelou.
        Object obj = getFacede().recarregar(selecionado);
        selecionado = obj;
        if (selecionado != null) {
            if (Persistencia.getId(selecionado) != null) {
                if (lista != null) {
                    lista.set(lista.indexOf(selecionado) + 1, selecionado);
                }
            }
        }
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    public int getMaxRegistrosNaLista() {
        return AbstractFacade.MAX_RESULTADOS_NA_CONSULTA - 1;
    }

    public boolean isTemMaisResultados() {
        if (lista == null) {
            lista = listaFiltrandoX();
        }
        return lista.size() >= maximoRegistrosTabela;
    }

    public boolean isTemAnterior() {
        return inicio > 0;
    }

    public List acaoBotaoFiltrar() {
        inicio = 0;
        return listaFiltrandoX();
    }

    public List listaFiltrandoX() {
        List<AtributoMetadata> atributosTabelaveis = getMetadata().getAtributosTabelaveis();
        Field atributos[] = new Field[atributosTabelaveis.size()];
        for (int i = 0; i < atributos.length; i++) {
            atributos[i] = atributosTabelaveis.get(i).getAtributo();
        }

        lista = getFacede().listaFiltrandoX(filtro, inicio, maximoRegistrosTabela, atributos);
        return lista;
    }

    public void proximos() {
        inicio += maximoRegistrosTabela;
        listaFiltrandoX();
    }

    public void anteriores() {
        inicio -= maximoRegistrosTabela;
        if (inicio < 0) {
            inicio = 0;
        }
        listaFiltrandoX();
    }

    public int getInicioBuscaTabela() {
        return inicio;
    }

    public void setInicio(int inicio) {
        this.inicio = inicio;
    }

    // MENSAGENS
    public void addInfo(String summary, String detail) {
        FacesUtil.addInfo(summary, detail);
    }

    public void addError(String summary, String detail) {
        FacesUtil.addError(summary, detail);
    }

    public void addWarn(String summary, String detail) {
        FacesUtil.addWarn(summary, detail);
    }

    public void addFatal(String summary, String detail) {
        FacesUtil.addFatal(summary, detail);
    }

    public void addErrorPadrao(Throwable t) {
        FacesUtil.addErrorPadrao(t);
    }

    public void addErrorGenerico(Throwable t) {
        FacesUtil.addErrorGenerico(t);
    }

    public boolean isCadastroDerivado() {
        if (sessao != null && sessao.trim().length() > 0) {
            Web.poeNaSessao(selecionado);
            RequestContext.getCurrentInstance().execute("window.close()");
            return true;
        }
        return false;
    }
}
