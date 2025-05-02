package br.com.webpublico.controle;

import br.com.webpublico.entidades.ColunaDicionarioDeDados;
import br.com.webpublico.entidades.DicionarioDeDados;
import br.com.webpublico.enums.TipoDicionarioDeDados;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DicionarioDeDadosFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-dicionario-de-dados", pattern = "/dicionario-de-dados/novo/", viewId = "/faces/comum/dicionario-dados/edita.xhtml"),
    @URLMapping(id = "listar-dicionario-de-dados", pattern = "/dicionario-de-dados/listar/", viewId = "/faces/comum/dicionario-dados/lista.xhtml"),
    @URLMapping(id = "editar-dicionario-de-dados", pattern = "/dicionario-de-dados/editar/#{dicionarioDeDadosControlador.id}/", viewId = "/faces/comum/dicionario-dados/edita.xhtml"),
    @URLMapping(id = "ver-dicionario-de-dados", pattern = "/dicionario-de-dados/ver/#{dicionarioDeDadosControlador.id}/", viewId = "/faces/comum/dicionario-dados/visualizar.xhtml")
})
public class DicionarioDeDadosControlador extends PrettyControlador<DicionarioDeDados> implements Serializable, CRUD {

    @EJB
    private DicionarioDeDadosFacade facade;
    private ColunaDicionarioDeDados colunaDicionarioDeDados;

    public DicionarioDeDadosControlador() {
        super(DicionarioDeDados.class);
    }

    @URLAction(mappingId = "novo-dicionario-de-dados", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        cancelarColuna();
    }

    @URLAction(mappingId = "ver-dicionario-de-dados", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-dicionario-de-dados", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        cancelarColuna();
    }

    public List<SelectItem> getTiposDeDicionarioDeDados() {
        return Util.getListSelectItemSemCampoVazio(TipoDicionarioDeDados.values());
    }

    public void cancelarColuna() {
        colunaDicionarioDeDados = null;
    }

    public void instanciarColuna() {
        colunaDicionarioDeDados = new ColunaDicionarioDeDados();
        colunaDicionarioDeDados.setDicionarioDeDados(selecionado);
    }

    public void adicionarColuna() {
        try {
            validarColuna();
            Util.adicionarObjetoEmLista(selecionado.getColunas(), colunaDicionarioDeDados);
            cancelarColuna();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarColuna() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(colunaDicionarioDeDados.getColuna())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Coluna deve ser informado.");
        }
        if (colunaDicionarioDeDados.getOrdem() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ordem deve ser informado.");
        }
        if (Strings.isNullOrEmpty(colunaDicionarioDeDados.getDescricao())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Descrição deve ser informado.");
        }
        ve.lancarException();
        for (ColunaDicionarioDeDados dicionarioDeDados : selecionado.getColunas()) {
            if (!colunaDicionarioDeDados.equals(dicionarioDeDados) && dicionarioDeDados.getOrdem().equals(colunaDicionarioDeDados.getOrdem())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Ordem informada já está em outra coluna.");
            }
        }
        ve.lancarException();
    }

    public void editarColuna(ColunaDicionarioDeDados colunaDicionarioDeDados) {
        this.colunaDicionarioDeDados = (ColunaDicionarioDeDados) Util.clonarObjeto(colunaDicionarioDeDados);
    }

    public void removerColuna(ColunaDicionarioDeDados colunaDicionarioDeDados) {
        selecionado.getColunas().remove(colunaDicionarioDeDados);
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        }catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }

    }

    private void validarCampos() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (facade.hasTipoCadastrado(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um cadastro com o tipo informado");
        }
        ve.lancarException();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/dicionario-de-dados/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ColunaDicionarioDeDados getColunaDicionarioDeDados() {
        return colunaDicionarioDeDados;
    }

    public void setColunaDicionarioDeDados(ColunaDicionarioDeDados colunaDicionarioDeDados) {
        this.colunaDicionarioDeDados = colunaDicionarioDeDados;
    }
}
