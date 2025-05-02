package br.com.webpublico.controle;

import br.com.webpublico.entidades.ServicoObra;
import br.com.webpublico.entidades.TipoServicoObra;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ServicoObraFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by venom on 10/09/14.
 */
@ManagedBean(name = "obraServicoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-servico", pattern = "/servicoobra/novo/", viewId = "/faces/administrativo/obras/servico/edita.xhtml"),
        @URLMapping(id = "lista-servico", pattern = "/servicoobra/listar/", viewId = "/faces/administrativo/obras/servico/lista.xhtml"),
        @URLMapping(id = "ver-servico", pattern = "/servicoobra/ver/#{obraServicoControlador.id}/", viewId = "/faces/administrativo/obras/servico/visualizar.xhtml"),
        @URLMapping(id = "editar-servico", pattern = "/servicoobra/editar/#{obraServicoControlador.id}/", viewId = "/faces/administrativo/obras/servico/edita.xhtml")
})
public class ObraServicoControlador extends PrettyControlador<ServicoObra> implements Serializable, CRUD {

    @EJB
    private ServicoObraFacade servicoObraFacade;
    private ConverterGenerico converterSuperiores;
    private TreeNode root;

    public ObraServicoControlador() {
        super(ServicoObra.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return servicoObraFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/servicoobra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-servico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
//        populaTreeNode();
        montaTreeNode();
    }

    @Override
    public void salvar() {
        if (selecionado.getTipoServicoObra() == null) {
            selecionado.setTipoServicoObra(TipoServicoObra.REFERENCIA);
        }
        if (selecionado.getSuperior() != null && selecionado.getOrdem() == 0) {
            selecionado.setOrdem(getOrdem(selecionado.getSuperior()));
        }
        if (Util.validaCampos(selecionado)) {
            if (operacao.equals(Operacoes.NOVO)) {
                if (isServicoUnico()) {
                    servicoObraFacade.salvarNovo(selecionado);
                } else {
                    FacesUtil.addOperacaoNaoPermitida("Já existe um serviço com esse nome!");
                }
            }
            if (operacao.equals(Operacoes.EDITAR)) {
                servicoObraFacade.salvar(selecionado);
            }
//                redireciona();
            novo();
        }
    }

    @URLAction(mappingId = "ver-servico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-servico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public void novoServico() {
        operacao = Operacoes.EDITAR;
    }

    public void cancelarServico() {
        novo();
    }

    public void selecionarServico(ServicoObra so) {
        selecionado = so;
        operacao = Operacoes.EDITAR;
    }

    public void removerServico(ServicoObra so) {
        servicoObraFacade.remover(so);
        novo();
    }

    public boolean liberaRemover(ServicoObra so) {
        if (so.getSuperior() == null && so.getFilhos().isEmpty()) {
            return true;
        }
        if (so.getSuperior() != null && so.getOrdem().equals(so.getSuperior().getFilhos().size())) {
            return true;
        }
        return false;
    }

    public Integer getOrdem(ServicoObra so) {
        List<ServicoObra> filhos = new ArrayList<>();
        filhos.addAll(servicoObraFacade.getFilhosDoSuperior(so));
        return filhos.size() + 1;
    }

    public boolean isServicoUnico() {
        List<ServicoObra> servicos = servicoObraFacade.listaFiltrando(selecionado.getNome(), "nome");
        if (servicos.isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean liberaSuperior() {
        return selecionado.getFilhos().isEmpty();
    }

    public List<SelectItem> getSuperiores() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (ServicoObra obj : servicoObraFacade.lista()) {
            if (obj.getSuperior() == null) {
                lista.add(new SelectItem(obj, obj.getNome()));
            }
        }
        return Util.ordenaSelectItem(lista);
    }

    public ConverterGenerico getConverterSuperiores() {
        if (converterSuperiores == null) {
            converterSuperiores = new ConverterGenerico(ServicoObra.class, servicoObraFacade);
        }
        return converterSuperiores;
    }

    public TreeNode getRoot() {
        if (root == null) {
//            populaTreeNode();
            montaTreeNode();
        }
        return root;
    }

    public void montaTreeNode() {
        root = new DefaultTreeNode(" ", null);
        for (ServicoObra servico : servicoObraFacade.listaRecuperados()) {
            if (servico.getSuperior() == null) {
                carregaArvore(root, servico);
            }
        }
    }

    private void carregaArvore(TreeNode raiz, ServicoObra so) {
        TreeNode primeiroNivel = new DefaultTreeNode(so, raiz);
        for (ServicoObra filho : so.getFilhos()) {
            montaArvore(primeiroNivel, filho);
        }
    }

    public void populaTreeNode() {
        root = new DefaultTreeNode(" ", null);
        for (ServicoObra servico : servicoObraFacade.getServicosSuperiores()) {
            montaArvore(root, servico);
        }
    }

    private void montaArvore(TreeNode raiz, ServicoObra so) {
        TreeNode primeiroNivel = new DefaultTreeNode(so, raiz);
        for (ServicoObra filho : servicoObraFacade.getFilhosDoSuperior(so)) {
            montaArvore(primeiroNivel, filho);
        }
    }
}
