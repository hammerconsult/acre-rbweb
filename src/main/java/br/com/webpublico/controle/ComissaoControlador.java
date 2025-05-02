/*
 * Codigo gerado automaticamente em Wed Nov 09 08:54:07 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Comissao;
import br.com.webpublico.entidades.MembroComissao;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.enums.AtribuicaoComissao;
import br.com.webpublico.enums.NaturezaDoCargo;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoComissao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ComissaoFacade;
import br.com.webpublico.negocios.LicitacaoFacade;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "comissaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novocomissao", pattern = "/comissao/novo/", viewId = "/faces/administrativo/licitacao/comissao/edita.xhtml"),
    @URLMapping(id = "editarcomissao", pattern = "/comissao/editar/#{comissaoControlador.id}/", viewId = "/faces/administrativo/licitacao/comissao/edita.xhtml"),
    @URLMapping(id = "vercomissao", pattern = "/comissao/ver/#{comissaoControlador.id}/", viewId = "/faces/administrativo/licitacao/comissao/visualizar.xhtml"),
    @URLMapping(id = "listarcomissao", pattern = "/comissao/listar/", viewId = "/faces/administrativo/licitacao/comissao/lista.xhtml")
})
public class ComissaoControlador extends PrettyControlador<Comissao> implements Serializable, CRUD {

    @EJB
    private ComissaoFacade comissaoFacade;
    private MembroComissao membroComissao;
    private MembroComissao membroExoneracao;
    private ConverterAutoComplete converterPessoaFisica;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    private Integer codigo;
    @EJB
    private LicitacaoFacade licitacaoFacade;

    public ComissaoControlador() {
        super(Comissao.class);
    }

    public MembroComissao getMembroComissao() {
        return membroComissao;
    }

    public MembroComissao getMembroExoneracao() {
        return membroExoneracao;
    }

    public void setMembroExoneracao(MembroComissao membroExoneracao) {
        this.membroExoneracao = membroExoneracao;
    }

    public void setMembroComissao(MembroComissao membroComissao) {
        this.membroComissao = membroComissao;
    }

    public void anularMembroComissao() {
        membroComissao = null;
    }

    public void inicializarMembro() {
        membroComissao = new MembroComissao();
    }

    public void novoMembro() {
        this.membroComissao = new MembroComissao();
    }

    private boolean membroJaAdicionado() {
        for (MembroComissao membroDaVez : selecionado.getMembroComissao()) {
            if (membroDaVez.getPessoaFisica().equals(membroComissao.getPessoaFisica()) && membroDaVez.getExoneradoEm() == null) {
                return true;
            }
        }

        return false;
    }

    private boolean podeAdicionarMembro() {
        if (membroComissao.getPessoaFisica() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Informe uma Pessoa", ""));
            return false;
        }

        if (!selecionado.getMembroComissao().contains(membroComissao)) {
            if (membroJaAdicionado()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Membro já adicionado", ""));
                return false;
            }
        }

        if (membroComissao.getExoneradoEm() != null) {
            if (membroComissao.getExoneradoEm().compareTo(new Date()) > 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "A data/hora da exoneração não pode ser superior a atual.", ""));
                return false;
            }
        }

        return true;
    }

    public void adicionarMembro() {
        if (!podeAdicionarMembro()) {
            return;
        }

        selecionado.setMembroComissao(Util.adicionarObjetoEmLista(selecionado.getMembroComissao(), membroComissao));
        membroComissao = new MembroComissao();
    }

    public void removerMembro(ActionEvent event) {
        MembroComissao membro = (MembroComissao) event.getComponent().getAttributes().get("removeMembro");
        selecionado.getMembroComissao().remove(membro);
    }

    public ComissaoFacade getFacade() {
        return comissaoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return comissaoFacade;
    }

    public List<SelectItem> getTipoDeComissao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoComissao object : TipoComissao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getNaturezaDoCargo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (NaturezaDoCargo object : NaturezaDoCargo.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getAtruibuicaoComissao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (AtribuicaoComissao object : AtribuicaoComissao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterPessoaFisica() {
        if (converterPessoaFisica == null) {
            converterPessoaFisica = new ConverterAutoComplete(PessoaFisica.class, pessoaFisicaFacade);
        }
        return converterPessoaFisica;
    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        return pessoaFisicaFacade.listaFiltrando(parte.trim(), "nome", "cpf");
    }

    public boolean possuiMembros() {
        return !selecionado.getMembroComissao().isEmpty();
    }

    public void atribuiComissaoAoMembro() {
        for (MembroComissao membro : selecionado.getMembroComissao()) {
            membro.setComissao((Comissao) selecionado);
//            ((Comissao) selecionado).getMembroComissao().set(((Comissao) selecionado).getMembroComissao().indexOf(membro), membro);
        }
    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado) && validaComissao()) {
            super.salvar();
        }
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public void padraoAoEditar() {
        this.codigo = selecionado.getCodigo();
        this.membroComissao = new MembroComissao();
    }

    public boolean validaCodigoNegativo() {
        if (selecionado.getCodigo() < 0) {
            FacesContext.getCurrentInstance().addMessage("Formulario:codigo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "O código informado não pode ser negativo", null));
            return false;
        }
        return true;
    }

    public boolean validaComissao() {
        if (possuiMembros()) {
            atribuiComissaoAoMembro();
        } else {
            FacesContext.getCurrentInstance().addMessage("Formulario:pessoa", new FacesMessage(FacesMessage.SEVERITY_ERROR, "A comissão deve possuir pelo menos um membro.", null));
            return false;
        }
        if (!validaCodigoNegativo()) {
            return false;
        }
        if (operacao == Operacoes.NOVO) {
            if (comissaoFacade.existeCodigoDeComissaoRepetido(selecionado.getCodigo())) {
                FacesContext.getCurrentInstance().addMessage("Formulario:codigo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Código de Comissão duplicado.", null));
                return false;
            }
        }
        if (operacao == Operacoes.EDITAR) {
            if (comissaoFacade.existeCodigoDeComissaoRepetido(selecionado.getCodigo()) && selecionado.getCodigo() != this.codigo) {
                FacesContext.getCurrentInstance().addMessage("Formulario:codigo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Código de Comissão duplicado.", null));
                return false;
            }
        }
        if (selecionado.getInicioVigencia() == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:inicioVigencia", new FacesMessage(FacesMessage.SEVERITY_ERROR, "O campo data inicio é obrigatório.", null));
            return false;
        }
        if (selecionado.getFinalVigencia() != null) {
            if (selecionado.getInicioVigencia().after(selecionado.getFinalVigencia())) {
                FacesContext.getCurrentInstance().addMessage("Formulario:inicioVigencia", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data de Inicio não pode ser superior a data Final.", null));
                return false;
            }
        }
        return true;
    }

    public boolean estaEditando() {
        return operacao == Operacoes.EDITAR ? true : false;
    }

    public void alterarMembro(MembroComissao membro) {
        this.membroComissao = (MembroComissao) Util.clonarObjeto(membro);
    }

    public void setarMembroExoneracao(MembroComissao membro) {
        this.membroExoneracao = membro;
        this.membroExoneracao.setExoneradoEm(new Date());
    }

    public void cancelarExoneracao() {
        this.membroExoneracao.setExoneradoEm(null);
    }

    @URLAction(mappingId = "novocomissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setCodigo(comissaoFacade.buscarProximoCodigoDeComissao());
        selecionado.setMembroComissao(new ArrayList<MembroComissao>());
        inicializarMembro();
    }

    @URLAction(mappingId = "vercomissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        padraoAoEditar();
    }

    @URLAction(mappingId = "editarcomissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        padraoAoEditar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/comissao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void excluir() {
        super.excluir();
    }
}
