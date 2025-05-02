/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CategoriaServico;
import br.com.webpublico.entidades.Servico;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CategoriaServicoFacade;
import br.com.webpublico.negocios.ServicoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
import br.com.webpublico.util.Util;
import com.beust.jcommander.internal.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author terminal1
 */
@ManagedBean(name = "servicoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoServico", pattern = "/servico/novo/", viewId = "/faces/tributario/cadastromunicipal/servico/edita.xhtml"),
    @URLMapping(id = "editarServico", pattern = "/servico/editar/#{servicoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/servico/edita.xhtml"),
    @URLMapping(id = "listarServico", pattern = "/servico/listar/", viewId = "/faces/tributario/cadastromunicipal/servico/lista.xhtml"),
    @URLMapping(id = "verServico", pattern = "/servico/ver/#{servicoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/servico/visualizar.xhtml")
})
public class ServicoControlador extends PrettyControlador<Servico> implements Serializable, CRUD {

    @EJB
    private ServicoFacade servicoFacade;
    @EJB
    private CategoriaServicoFacade categoriaServicoFacade;
    protected ConverterAutoComplete converterCategoriaServico;
    private MoneyConverter moneyConverter;
    private Servico servico;
    private List<Servico> removidos;

    public ServicoControlador() {
        super(Servico.class);
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    @Override
    public AbstractFacade getFacede() {
        return servicoFacade;
    }

    public List<SelectItem> getServicos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Servico object : servicoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public List<SelectItem> getCategoriasServico() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (CategoriaServico object : categoriaServicoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public Converter getConverterCategoriaServico() {
        if (converterCategoriaServico == null) {
            converterCategoriaServico = new ConverterAutoComplete(CategoriaServico.class, categoriaServicoFacade);
        }
        return converterCategoriaServico;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public List<CategoriaServico> completaCategoriaServico(String parte) {
        return categoriaServicoFacade.listaFiltrando(parte.trim(), "nome");
    }

    public boolean validaCampos() {
        if (!Util.validaCampos(selecionado)) {
            return false;
        }
        if (!validarAliquotaDoISS()) {
            return false;
        }
        if (!validarPercentualDeducao()) {
            return false;
        }
        if (servicoFacade.jaExisteServicoComCodigo(selecionado)) {
            FacesUtil.addError("Atenção!", "Já existe um serviço cadastrado com esse código!");
            return false;
        }
        return true;
    }

    private boolean validarPercentualDeducao() {
        if (selecionado.getPermiteDeducao() && selecionado.getPermiteDeducao() != null) {
            if (selecionado.getPercentualDeducao().compareTo(BigDecimal.ZERO) <= 0) {
                FacesUtil.addError("Atenção!", "O percentual de dedução deve ser maior que zero.");
                return false;
            }
            if (selecionado.getPercentualDeducao().compareTo(new BigDecimal(100)) > 0) {
                FacesUtil.addError("Atenção!", "O percentual de dedução deve ser menor ou igual a 100%.");
                return false;
            }
        }
        return true;
    }

    private boolean validarAliquotaDoISS() {
        if (selecionado.getAliquotaISSHomologado() != null) {
            if (selecionado.getAliquotaISSHomologado().compareTo(new BigDecimal(100)) > 0) {
                FacesUtil.addAtencao("O campo Alíquota do ISS Homologado deve ser menor ou igual a 100%.");
                return false;
            }
        }
        if (selecionado.getAliquotaISSFixo() != null) {
            if (selecionado.getAliquotaISSFixo().compareTo(new BigDecimal(100)) > 0) {
                FacesUtil.addAtencao("O campo Alíquota do ISS Fixo deve ser menor ou igual a 100%.");
                return false;
            }
        }
        return true;
    }

    @URLAction(mappingId = "verServico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "novoServico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setAliquotaISSHomologado(BigDecimal.ZERO);
    }

    @URLAction(mappingId = "editarServico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        removidos = Lists.newArrayList();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            if (!selecionado.getPermiteDeducao()) {
                selecionado.setPercentualDeducao(BigDecimal.ZERO);
            }
            super.salvar();
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/servico/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<Servico> completarServico(String parte) {
        return servicoFacade.completaServico(parte);
    }

    public List<Servico> completarServicoInstituicaoFinanceira(String parte) {
        return servicoFacade.completaServicoInstituicaoFinanceira(parte);
    }

    @Override
    public void salvar(Redirecionar redirecionar) {
        try {
            if (validaRegrasParaSalvar()) {
                servicoFacade.salvar(selecionado);
                for (Servico removido : removidos) {
                    removido.setServico(null);
                    servicoFacade.salvar(removido);
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
                String caminhoPadrao = ((CRUD) this).getCaminhoPadrao();
                Object key = ((CRUD) this).getUrlKeyValue();
                switch (redirecionar) {
                    case VER:
                        redireciona(caminhoPadrao + "ver/" + key);
                        break;
                    case EDITAR:
                        redireciona(caminhoPadrao + "editar/" + key);
                        break;
                    case LISTAR:
                        redireciona();
                        break;
                }
            }
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void adicionarServico() {
        try {
            validarServico();
            if (selecionado.getServicos() == null) {
                selecionado.setServicos(new ArrayList());
            }
            servico.setServico(selecionado);
            selecionado.getServicos().add(servico);
            servico = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    private void validarServico() {
        ValidacaoException ve = new ValidacaoException();
        if (servico == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Serviço deve ser informado.");
        } else if (selecionado.hasServico(servico)) {
            ve.adicionarMensagemDeCampoObrigatorio("O Serviço ja esta adicionado.");
        }
        ve.lancarException();
    }

    public void removerServico(Servico servico) {
        removidos.add(servico);
        selecionado.getServicos().remove(servico);
    }
}
