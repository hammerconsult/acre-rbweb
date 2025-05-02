/*
 * Codigo gerado automaticamente em Tue Mar 01 14:19:07 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.Loteamento;
import br.com.webpublico.entidades.Quadra;
import br.com.webpublico.entidades.Setor;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.QuadraFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "quadraControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "cadastrarQuadraTributario",
                pattern = "/tributario/quadra/novo/",
                viewId = "/faces/tributario/cadastromunicipal/quadra/edita.xhtml"),
        @URLMapping(id = "alterarQuadraTributario",
                pattern = "/tributario/quadra/editar/#{quadraControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/quadra/edita.xhtml"),
        @URLMapping(id = "listarQuadraTributario",
                pattern = "/tributario/quadra/listar/",
                viewId = "/faces/tributario/cadastromunicipal/quadra/lista.xhtml"),
        @URLMapping(id = "visualizarQuadraTributario",
                pattern = "/tributario/quadra/ver/#{quadraControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/quadra/visualizar.xhtml")
})
public class QuadraControlador extends PrettyControlador<Quadra> implements Serializable, CRUD {

    @EJB
    private QuadraFacade quadraFacade;
    protected ConverterGenerico converterSetor;
    protected ConverterGenerico converterLoteamento;
    private String caminho;
    private List<Quadra> quadras;
    private ConfiguracaoTributario conf;

    public QuadraControlador() {
        super(Quadra.class);
        this.quadras = new ArrayList<Quadra>();
        metadata = new EntidadeMetaData(Quadra.class);
    }

    public ConfiguracaoTributario getConf() {
        return conf;
    }

    public QuadraFacade getFacade() {
        return quadraFacade;
    }

    public AbstractFacade getFacede() {
        return quadraFacade;
    }

    public Quadra getSelecionado() {
        return (Quadra) selecionado;
    }

    public void setSelecionado(Quadra selecionado) {
        this.selecionado = selecionado;
    }

    public List<Quadra> getQuadras() {
        return this.quadraFacade.lista();
    }

    public void setQuadras(List<Quadra> quadras) {
        this.quadras = quadras;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public String caminho() {
        return this.caminho;
    }

    public List<SelectItem> getLoteamento() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (Loteamento object : quadraFacade.getLoteamentoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public List<SelectItem> getSetor() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (Setor object : quadraFacade.getSetorFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterLoteamento() {
        if (converterLoteamento == null) {
            converterLoteamento = new ConverterGenerico(Loteamento.class, quadraFacade.getLoteamentoFacade());
        }
        return converterLoteamento;
    }

    public ConverterGenerico getConverterSetor() {
        if (converterSetor == null) {
            converterSetor = new ConverterGenerico(Setor.class, quadraFacade.getSetorFacade());
        }
        return converterSetor;
    }

    @Override
    public void salvar() {
        if (validarCampos()) {
            if (!quadraFacade.existeNumeroQuadraSetor(this.getSelecionado())) {
                if (quadraFacade.validaDescricao(getSelecionado())) {
                    if (quadraFacade.validaDescricaoLoteamento(getSelecionado())) {
                        try {
                            if (this.getSelecionado().getId() == null) {
                                this.getFacede().salvarNovo(this.selecionado);
                            } else {
                                this.getFacede().salvar(this.selecionado);
                            }
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com Sucesso", ""));
                        } catch (Exception e) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage().toString()));
                        }

                    } else {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção.", "Descrição (Loteamento) da quadra já existente."));
                    }
                }
                redireciona();
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção.", "Já existe este número de quadra para o setor selecionado!"));
            }

        }
    }

    @Override
    @URLAction(mappingId = "cadastrarQuadraTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        conf = quadraFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        if (conf != null) {
            super.novo();
        } else {
            cancelar();
            FacesUtil.addWarn("Configuração não encontrada!", "É necessário cadastrar uma Configuração do Tributário.");
        }
    }

    @URLAction(mappingId = "alterarQuadraTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        conf = quadraFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        if (conf != null) {
            super.editar();
        } else {
            cancelar();
            FacesUtil.addWarn("Configuração não encontrada!", "É necessário cadastrar uma Configuração do Tributário.");
        }
    }

    @URLAction(mappingId = "visualizarQuadraTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void excluirSelecionado() {
        this.quadraFacade.remover(getSelecionado());
    }

    private boolean validarCampos() {
        boolean retorno = true;
        if (selecionado.getCodigo() == null || selecionado.getCodigo().trim().equals("")) {
            retorno = false;
            FacesUtil.addWarn("Atenção!", "O Código é um campo obrigatório.");
        } else if (selecionado.getCodigo().replaceAll(" ","").length() < conf.getNumDigitosQuadra() ) {
            retorno = false;
            FacesUtil.addWarn("Código inválido.", "A configuração vigente exige um código com " + conf.getNumDigitosQuadra() + " dígitos.");
        } else if (selecionado.getSetor() != null) {
            if (quadraFacade.existeNumeroQuadraSetor(selecionado)) {
                retorno = false;
                FacesUtil.addWarn("Atenção!", "A Quadra de número " + selecionado.getCodigo() + " já tem cadastro no setor selecionado!");
            }
        }

        if (selecionado.getDescricao() == null || selecionado.getDescricao().trim().equals("")) {
            retorno = false;
            FacesUtil.addWarn("Atenção!", "A Descrição Municipal é um campo obrigatório.");
        }

        if (selecionado.getSetor() == null) {
            retorno = false;
            FacesUtil.addWarn("Atenção!", "O Setor é um campo obrigatório.");
        }

        return retorno;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/quadra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

}
