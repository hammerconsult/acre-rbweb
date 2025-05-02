/*
 * Codigo gerado automaticamente em Wed Nov 23 09:59:01 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.GrauDeParentesco;
import br.com.webpublico.entidades.GrauParentTipoDepend;
import br.com.webpublico.entidades.TipoDependente;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoDependenteFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ManagedBean(name = "tipoDependenteControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoTipoDependencia", pattern = "/tipo-de-dependencia/novo/", viewId = "/faces/rh/administracaodepagamento/tipodependente/edita.xhtml"),
        @URLMapping(id = "listaTipoDependencia", pattern = "/tipo-de-dependencia/listar/", viewId = "/faces/rh/administracaodepagamento/tipodependente/lista.xhtml"),
        @URLMapping(id = "verTipoDependencia", pattern = "/tipo-de-dependencia/ver/#{tipoDependenteControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipodependente/visualizar.xhtml"),
        @URLMapping(id = "editarTipoDependencia", pattern = "/tipo-de-dependencia/editar/#{tipoDependenteControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipodependente/edita.xhtml"),
})
public class TipoDependenteControlador extends PrettyControlador<TipoDependente> implements Serializable, CRUD {

    @EJB
    private TipoDependenteFacade tipoDependenteFacade;
    private GrauParentTipoDepend grauParentTipoDepend;
    private List<GrauParentTipoDepend> grausDeParentescoRemovidos;

    public TipoDependenteControlador() {
        super(TipoDependente.class);
    }

    public TipoDependenteFacade getFacade() {
        return tipoDependenteFacade;
    }

    public List<GrauParentTipoDepend> getGrausDeParentescoRemovidos() {
        return grausDeParentescoRemovidos;
    }

    public void setGrausDeParentescoRemovidos(List<GrauParentTipoDepend> grausDeParentescoRemovidos) {
        this.grausDeParentescoRemovidos = grausDeParentescoRemovidos;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-de-dependencia/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoTipoDependencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        Long novoCodigo = tipoDependenteFacade.retornaUltimoCodigoLong();
        selecionado.setCodigo(novoCodigo+"");
    }

    @URLAction(mappingId = "editarTipoDependencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verTipoDependencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        if (temGrauDeParentescoRemovido()) {
            if (podeSalvar(selecionado)) {
                if (operacao == Operacoes.NOVO) {
                    Long novoCodigo = tipoDependenteFacade.retornaUltimoCodigoLong();
                    if (!novoCodigo.equals(selecionado.getCodigo())) {
                        FacesUtil.addInfo("Atenção!", "O Código " + getSelecionado().getCodigo() + " já está sendo usado e foi gerado o novo código " + novoCodigo + " !");
                        selecionado.setCodigo(novoCodigo+"");
                    }
                }
                try {
                    tipoDependenteFacade.salvar(selecionado, grausDeParentescoRemovidos);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
                } catch (ValidacaoException ex) {
                    FacesUtil.printAllFacesMessages(ex.getMensagens());
                    return;
                } catch (Exception e) {
                    descobrirETratarException(e);
                }
                redireciona();
            }
        } else {
            super.salvar();
        }
    }

    private boolean temGrauDeParentescoRemovido() {
        if (isEditar()) {
            if (grausDeParentescoRemovidos != null && !grausDeParentescoRemovidos.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean isEditar() {
        return Operacoes.EDITAR.equals(operacao);
    }

    private boolean podeSalvar(TipoDependente selecionado) {
        return Util.validaCampos(selecionado);
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoDependenteFacade;
    }

    public void novoGrauDeParentesco() {
        grauParentTipoDepend = new GrauParentTipoDepend();
        grauParentTipoDepend.setTipoDependente(selecionado);
    }

    public void adicionaGrauParentTipoDepend() {
        if (podeAdicionarGrauParentTipoDepend(grauParentTipoDepend)) {
            grauParentTipoDepend.setDataRegistro(tipoDependenteFacade.getSistemaFacade().getDataOperacao());
            selecionado.setGrauParentTipoDepends(Util.adicionarObjetoEmLista(selecionado.getGrauParentTipoDepends(), grauParentTipoDepend));
            cancelarGrauParentTipoDepend();
        }
    }

    private boolean podeAdicionarGrauParentTipoDepend(GrauParentTipoDepend grauParentTipoDepend) {
        if (!isGrauParentescoInformado(grauParentTipoDepend)) {
            return false;
        }
        if (!isGrauParentescoRepetido(grauParentTipoDepend)) {
            return false;
        }
        return true;
    }

    private boolean isGrauParentescoRepetido(GrauParentTipoDepend grauParentTipoDepend) {
        for (GrauParentTipoDepend grauParentescoAdicionado : selecionado.getGrauParentTipoDepends()) {
            if (grauParentescoAdicionado.getGrauDeParentesco().equals(grauParentTipoDepend.getGrauDeParentesco())) {
                FacesUtil.addOperacaoNaoPermitida("O grau de parentesco <b>" + grauParentTipoDepend.getGrauDeParentesco().getDescricao() + "</b> já está adicionado.");
                grauParentTipoDepend.setGrauDeParentesco(null);
                return false;
            }
        }
        return true;
    }

    private boolean isGrauParentescoInformado(GrauParentTipoDepend grauParentTipoDepend) {
        if (grauParentTipoDepend.getGrauDeParentesco() == null) {
            FacesUtil.addCampoObrigatorio("O campo grau de parentesco é obrigatório.");
            return false;
        }
        return true;
    }

    public void cancelarGrauParentTipoDepend() {
        grauParentTipoDepend = null;
    }

    public void removeGrauParentTipoDepend(GrauParentTipoDepend grauParentesco) {
        selecionado.getGrauParentTipoDepends().remove(grauParentesco);
        if (!isAdicionadoNaListaParaRemocao(grauParentesco.getGrauDeParentesco())) {
            setGrausDeParentescoRemovidos(Util.adicionarObjetoEmLista(getGrausDeParentescoRemovidos(), grauParentesco));
        }
    }

    private boolean isAdicionadoNaListaParaRemocao(GrauDeParentesco grauDeParentesco) {
        return getGrausDeParentescoJaAdicionadosNaListaDeRemocao().contains(grauDeParentesco);
    }

    private List<GrauDeParentesco> getGrausDeParentescoJaAdicionadosNaListaDeRemocao() {
        List<GrauDeParentesco> grausDeParentescoJaAdicionadoNaListaDeRemocao = new ArrayList<>();
        if (grausDeParentescoRemovidos != null && !grausDeParentescoRemovidos.isEmpty()) {
            for (GrauParentTipoDepend gptd : grausDeParentescoRemovidos) {
                grausDeParentescoJaAdicionadoNaListaDeRemocao.add(gptd.getGrauDeParentesco());
            }
        }
        return grausDeParentescoJaAdicionadoNaListaDeRemocao;
    }

    public List<SelectItem> getGrausDeParentesco() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (GrauDeParentesco object : getGrausDeParentescoOrdenados()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    private List<GrauDeParentesco> getGrausDeParentescoOrdenados() {
        List<GrauDeParentesco> grausDeParentesco = tipoDependenteFacade.getGrauDeParentescoFacade().lista();
        Collections.sort(grausDeParentesco, new Comparator<GrauDeParentesco>() {
            @Override
            public int compare(GrauDeParentesco o1, GrauDeParentesco o2) {
                return o1.getCodigoRais().compareTo(o2.getCodigoRais());
            }
        });
        return grausDeParentesco;
    }

    public GrauParentTipoDepend getGrauParentTipoDepend() {
        return grauParentTipoDepend;
    }

    public void setGrauParentTipoDepend(GrauParentTipoDepend grauParentTipoDepend) {
        this.grauParentTipoDepend = grauParentTipoDepend;
    }

    public boolean isVisualizar() {
        return Operacoes.VER.equals(operacao);
    }
}
