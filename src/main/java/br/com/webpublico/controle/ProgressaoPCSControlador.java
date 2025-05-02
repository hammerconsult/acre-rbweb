/*
 * Codigo gerado automaticamente em Sat Jul 02 09:46:00 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CategoriaPCS;
import br.com.webpublico.entidades.PlanoCargosSalarios;
import br.com.webpublico.entidades.ProgressaoPCS;
import br.com.webpublico.entidades.rh.pccr.MesesProgressaoProgressaoPCS;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CategoriaPCSFacade;
import br.com.webpublico.negocios.PlanoCargosSalariosFacade;
import br.com.webpublico.negocios.ProgressaoPCSFacade;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ManagedBean(name = "progressaoPCSControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoProgressaoPCS", pattern = "/progressao-pccr/novo/", viewId = "/faces/rh/administracaodepagamento/progressaopcs/edita.xhtml"),
    @URLMapping(id = "editarProgressaoPCS", pattern = "/progressao-pccr/editar/#{progressaoPCSControlador.id}/", viewId = "/faces/rh/administracaodepagamento/progressaopcs/edita.xhtml"),
    @URLMapping(id = "listarProgressaoPCS", pattern = "/progressao-pccr/listar/", viewId = "/faces/rh/administracaodepagamento/progressaopcs/lista.xhtml"),
    @URLMapping(id = "verProgressaoPCS", pattern = "/progressao-pccr/ver/#{progressaoPCSControlador.id}/", viewId = "/faces/rh/administracaodepagamento/progressaopcs/visualizar.xhtml")
})
public class ProgressaoPCSControlador extends PrettyControlador<ProgressaoPCS> implements Serializable, CRUD {

    protected final static String[] letras = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;
    @EJB
    private PlanoCargosSalariosFacade planoCargosSalariosFacade;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    private ConverterGenerico converterCategoriaPCSg;
    private ConverterGenerico converterPlanoCargosSalarios;
    private ConverterAutoComplete converterSuperior;
    private transient TreeNode root;
    private transient TreeNode noSelecionado;
    private ProgressaoPCS superior;
    private ProgressaoPCS filha;
    private Integer ordem;
    private boolean iniciouNaLetraA = false;
    private MesesProgressaoProgressaoPCS mesesProgressaoSelecionado;

    public ProgressaoPCSControlador() {
        super(ProgressaoPCS.class);
    }

    public ConverterGenerico getConverterCategoriaPCSg() {
        if (converterCategoriaPCSg == null) {
            converterCategoriaPCSg = new ConverterGenerico(CategoriaPCS.class, categoriaPCSFacade);
        }
        return converterCategoriaPCSg;
    }

    public List<SelectItem> getCategoriasSelect() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado.getPlanoCargosSalarios() != null) {
            for (CategoriaPCS object : categoriaPCSFacade.getRaizPorPlano(selecionado.getPlanoCargosSalarios())) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public TreeNode getNoSelecionado() {
        return noSelecionado;
    }

    public void setNoSelecionado(TreeNode noSelecionado) {
        this.noSelecionado = noSelecionado;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public ProgressaoPCS getSuperior() {
        return superior;
    }

    public void setSuperior(ProgressaoPCS superior) {
        this.superior = superior;
    }

    public ProgressaoPCSFacade getFacade() {
        return progressaoPCSFacade;
    }

    public Converter getConverterSuperior() {
        if (converterSuperior == null) {
            converterSuperior = new ConverterAutoComplete(ProgressaoPCS.class, progressaoPCSFacade);
        }
        return converterSuperior;
    }

    public void setConverterSuperior(ConverterAutoComplete converterSuperior) {
        if (converterSuperior == null) {
            converterSuperior = new ConverterAutoComplete(ProgressaoPCS.class, progressaoPCSFacade);
        }
        this.converterSuperior = converterSuperior;
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            for (ProgressaoPCS progressaoPCS : selecionado.getFilhos()) {
                progressaoPCS.setCodigo(selecionado.getCodigo());
            }
            super.salvar();
        } catch (ValidacaoException va) {
            FacesUtil.printAllFacesMessages(va.getMensagens());
        } catch (Exception va) {
            logger.error("Erro ao tentar salvar uma Progressao PCS.", va);
            FacesUtil.addError("Erro desconhecido", va.getMessage());
        }
    }

    @Override
    public AbstractFacade getFacede() {
        return progressaoPCSFacade;
    }

    public List<SelectItem> getPlanoCargosSalariosSelectItem() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (PlanoCargosSalarios object : planoCargosSalariosFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterPlanoCargosSalarios() {
        if (converterPlanoCargosSalarios == null) {
            converterPlanoCargosSalarios = new ConverterGenerico(PlanoCargosSalarios.class, planoCargosSalariosFacade);
        }
        return converterPlanoCargosSalarios;
    }

    public void montaRaiz() {
        root = new DefaultTreeNode("root", null);
        List<ProgressaoPCS> raiz = progressaoPCSFacade.getRaizFiltrandoPCS(((ProgressaoPCS) selecionado).getPlanoCargosSalarios());

        if (raiz != null) {
            for (ProgressaoPCS pcs : raiz) {
                TreeNode raizVisual = new DefaultTreeNode(pcs, root);
                montaArvore(raizVisual);
            }
        }
    }

    public void montaArvore(TreeNode raizArvore) {
        ProgressaoPCS raiz = (ProgressaoPCS) raizArvore.getData();
        acharNoSelecionado(raizArvore);
        for (ProgressaoPCS pro : progressaoPCSFacade.getFilhosDe(raiz, ((ProgressaoPCS) selecionado).getPlanoCargosSalarios())) {
            TreeNode filhoArvore = new DefaultTreeNode(pro, raizArvore);
            acharNoSelecionado(filhoArvore);
            montaArvore(filhoArvore);

        }
    }

    public TreeNode getArvore() {
        return root;
    }

    public TreeNode acharNoSelecionado(TreeNode treeNode) {
        if (selecionado != null) {
            if (treeNode.getData().equals(((ProgressaoPCS) selecionado).getSuperior())) {
                treeNode.setSelected(true);
                treeNode.setExpanded(true);
            }
        }
        return treeNode;
    }

    public List<ProgressaoPCS> completaUnidade(String parte) {
        return progressaoPCSFacade.listaFiltrandoDescricaoPCS(parte.trim(), ((ProgressaoPCS) selecionado).getPlanoCargosSalarios());
    }

    public void setaUnidade() {
        if (superior != null) {
            ((ProgressaoPCS) selecionado).setSuperior(superior);
        } else {
            FacesContext.getCurrentInstance().addMessage("Formulario:unidadeOrg", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "Selecione para filtrar!"));
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/progressao-pccr/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoProgressaoPCS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        filha = new ProgressaoPCS();
        filha.setOrdem(1);
        ordem = 0;
        iniciouNaLetraA = false;
    }

    @URLAction(mappingId = "verProgressaoPCS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
    }

    @URLAction(mappingId = "editarProgressaoPCS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        filha = new ProgressaoPCS();
        if (selecionado.getSuperior() != null) {
            FacesUtil.addInfo("Atenção", "Só é possível editar um nó pai");
            redireciona();
        }
        if (!selecionado.getFilhos().isEmpty()) {
            ordem = selecionado.getFilhos().size();
            Collections.sort(selecionado.getFilhos());
        } else {
            ordem = 0;
        }
        iniciouNaLetraA = false;
    }

    public void validarCampos() {
        ValidacaoException validacaoException = new ValidacaoException();
        if (selecionado.getPlanoCargosSalarios() == null) {
            validacaoException.adicionarMensagemDeOperacaoNaoPermitida("Selecione um Plano de Cargos e Salários.");
        }

        if (selecionado.getCategoriaPCS() == null) {
            validacaoException.adicionarMensagemDeOperacaoNaoPermitida("Selecione uma Categoria de Plano de Cargos e Salários.");
        }

        if ("".equals(selecionado.getCodigo().trim())) {
            validacaoException.adicionarMensagemDeCampoObrigatorio("O Código deve ser preenchido.");
        }

        if ("".equals(selecionado.getDescricao().trim())) {
            validacaoException.adicionarMensagemDeCampoObrigatorio("A Descrição deve ser preenchida.");
        }

        if (noSelecionado != null) {
            if (progressaoPCSFacade.recuperaProgressaoNoEnquadramentoFuncionalVigente((ProgressaoPCS) noSelecionado.getData()) != null) {
                validacaoException.adicionarMensagemDeCampoObrigatorio("A progressão superior já tem valor informado no Enquadramento PCCR. Não é possível informar a nova progressão como filha.");
            }
            selecionado.setSuperior((ProgressaoPCS) noSelecionado.getData());
        }

        if (selecionado.getId() != null
            && selecionado.getSuperior() != null
            && selecionado.getId().equals(selecionado.getSuperior().getId())) {
            selecionado.setSuperior(null);
            validacaoException.adicionarMensagemDeCampoObrigatorio("A ProgressaoPCS não pode ser pai dela mesma.");
        }
        if (validacaoException.temMensagens()) {
            throw validacaoException;
        }
    }

    public ProgressaoPCS getFilha() {
        return filha;
    }

    public void setFilha(ProgressaoPCS filha) {
        this.filha = filha;
    }

    public String getLetra(int index) {
        try {
            return letras[index];
        } catch (Exception e) {
            return "";
        }
    }


    public void adicionarFilho() {
        try {
            validarFilha();
            String letra = filha.getDescricao();
            Integer ultimaOrdem = null;
            if (letra.equals("A")) {
                iniciouNaLetraA = true;
            }
            filha.setSuperior(selecionado);
            filha.setPlanoCargosSalarios(selecionado.getPlanoCargosSalarios());
            if (validaLetra()) {
                selecionado.getFilhos().add(filha);
            }
            ultimaOrdem = filha.getOrdem();
            filha = new ProgressaoPCS();
            filha.setOrdem(++ultimaOrdem);
            if (iniciouNaLetraA) {
                filha.setDescricao(getLetra(selecionado.getFilhos().size()));
            }
            ordenarProgressoes();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
        }
    }

    public void ordenarProgressoes() {
        if (!CollectionUtils.isEmpty(selecionado.getFilhos())) {
            Collections.sort(selecionado.getFilhos(), new Comparator<ProgressaoPCS>() {
                @Override
                public int compare(ProgressaoPCS o1, ProgressaoPCS o2) {
                    return o1.getOrdem().compareTo(o2.getOrdem());
                }
            });
        }
    }

    public boolean validaLetra() {
        boolean valida = true;
        for (ProgressaoPCS progressaoPCS : selecionado.getFilhos()) {
            if (progressaoPCS.getDescricao().equals(filha.getDescricao())) {
                FacesUtil.addOperacaoNaoPermitida("A Letra '" + filha.getDescricao() + "' já foi inserida.");
                valida = false;
            }
        }
        return valida;
    }

    public void removerFilho(ProgressaoPCS fi) {
        if (isPodeRemoverFilho(fi)) {
            selecionado.getFilhos().remove(fi);
            ordenarProgressoes();
        }
    }

    public void editarFilho(ProgressaoPCS filho) {
        this.filha = (ProgressaoPCS) Util.clonarObjeto(filho);
        this.selecionado.getFilhos().remove(filho);
    }

    public boolean isPodeRemoverFilho(ProgressaoPCS pcs) {
        if (pcs.getId() == null) {
            return true;
        }
        if (!progressaoPCSFacade.buscarProgressoesNoEnquadramento(pcs).isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("O registro já está sendo utilizado no Enquadramento PCCR.");
            return false;
        }
        return true;
    }

    private void validarFilha() {
        ValidacaoException ve = new ValidacaoException();
        if (filha != null) {
            if (filha.getDescricao().equals("")) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Nome é obrigatório.");
            }

            if (filha.getOrdem() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Ordem é obrigatório.");
            }

            if (!CollectionUtils.isEmpty(selecionado.getFilhos())) {
                for (ProgressaoPCS node : selecionado.getFilhos()) {
                    if (node.getOrdem().equals(this.filha.getOrdem())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A ordem de nº " + filha.getOrdem() + " já foi informada. Por favor informe outra ordem para esta subprogressão.");
                        break;
                    }
                }

            }
        }
        ve.lancarException();
    }

    public void criarMesesProgressao() {
        this.mesesProgressaoSelecionado = new MesesProgressaoProgressaoPCS();
    }

    public MesesProgressaoProgressaoPCS getMesesProgressaoSelecionado() {
        return mesesProgressaoSelecionado;
    }

    public void setMesesProgressaoSelecionado(MesesProgressaoProgressaoPCS mesesProgressaoSelecionado) {
        this.mesesProgressaoSelecionado = mesesProgressaoSelecionado;
    }

    public void selecionarMesesProgressao(MesesProgressaoProgressaoPCS mesesProgressao) {
        mesesProgressaoSelecionado = (MesesProgressaoProgressaoPCS) Util.clonarObjeto(mesesProgressao);
    }

    public void confirmarMesesProgressao() {
        try {
            validarCamposMesesProgressao();

            atribuirFinalDeVigenciaParaUltimoRegistroAdicionado();
            mesesProgressaoSelecionado.setProgressaoPCS(selecionado);
            selecionado.setMesesProgressao(Util.adicionarObjetoEmLista(selecionado.getMesesProgressao(), mesesProgressaoSelecionado));
            cancelarMesesProgressao();
        } catch (ValidacaoException val) {
            FacesUtil.printAllFacesMessages(val.getMensagens());
        }
    }

    private void validarCamposMesesProgressao() {
        Util.validarCampos(mesesProgressaoSelecionado);
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(mesesProgressaoSelecionado.getInicioVigencia(), mesesProgressaoSelecionado.getFinalVigencia())) {
            throw new ValidacaoException("A data de inicio de vigência deve ser anterior a data de final de vigência.");
        }

        if (!isVigenciaDoNovoMesValida()) {
            throw new ValidacaoException("A data de inicio de vigência deve ser posterior à do último registro adicionado");
        }
    }

    public void cancelarMesesProgressao() {
        this.mesesProgressaoSelecionado = null;
    }

    public void removerMesesProgressao(MesesProgressaoProgressaoPCS mesesProgressao) {
        ordenarMesesProgressoes();
        int indexMesRemovido = selecionado.getMesesProgressao().indexOf(mesesProgressao);
        selecionado.getMesesProgressao().remove(mesesProgressao);
        try {
            mesesProgressaoSelecionado = selecionado.getMesesProgressao().get(indexMesRemovido);
            atribuirFinalDeVigenciaParaUltimoRegistroAdicionado();
            return;
        } catch (IndexOutOfBoundsException ioe) {
        }

        try {
            MesesProgressaoProgressaoPCS ultimoMes = selecionado.getMesesProgressao().get(indexMesRemovido - 1);
            ultimoMes.setFinalVigencia(null);
            selecionado.setMesesProgressao(Util.adicionarObjetoEmLista(selecionado.getMesesProgressao(), ultimoMes));
        } catch (IndexOutOfBoundsException ioe) {
        }

    }


    private boolean isVigenciaDoNovoMesValida() {
        if (selecionado.getMesesProgressao() == null || selecionado.getMesesProgressao().isEmpty()) {
            return true;
        }

        MesesProgressaoProgressaoPCS ultimoMesAdicionado;

        ordenarMesesProgressoes();

        if (selecionado.getMesesProgressao().get(0).equals(mesesProgressaoSelecionado)) {
            return true;
        }

        try {
            ultimoMesAdicionado = selecionado.getMesesProgressao().get(selecionado.getMesesProgressao().indexOf(mesesProgressaoSelecionado) - 1);
        } catch (IndexOutOfBoundsException ie) {
            ultimoMesAdicionado = selecionado.getMesesProgressao().get(selecionado.getMesesProgressao().size() - 1);
        }

        if (ultimoMesAdicionado == null)
            return true;

        if (mesesProgressaoSelecionado.getInicioVigencia().compareTo(ultimoMesAdicionado.getInicioVigencia()) <= 0) {
            return false;
        }

        return true;
    }

    private void ordenarMesesProgressoes() {
        Collections.sort(selecionado.getMesesProgressao(), new Comparator<MesesProgressaoProgressaoPCS>() {
            @Override
            public int compare(MesesProgressaoProgressaoPCS o1, MesesProgressaoProgressaoPCS o2) {
                return o1.getInicioVigencia().compareTo(o2.getInicioVigencia());
            }
        });
    }


    private void atribuirFinalDeVigenciaParaUltimoRegistroAdicionado() {
        if (selecionado.getMesesProgressao() == null || selecionado.getMesesProgressao().isEmpty())
            return;
        MesesProgressaoProgressaoPCS ultimoMesAdicionado;
        ordenarMesesProgressoes();

        if (selecionado.getMesesProgressao().get(0).equals(mesesProgressaoSelecionado)) {
            return;
        }

        try {
            ultimoMesAdicionado = selecionado.getMesesProgressao().get(selecionado.getMesesProgressao().indexOf(mesesProgressaoSelecionado) - 1);
        } catch (IndexOutOfBoundsException ie) {
            ultimoMesAdicionado = selecionado.getMesesProgressao().get(selecionado.getMesesProgressao().size() - 1);
        }


        if (ultimoMesAdicionado == null)
            return;

        ultimoMesAdicionado.setFinalVigencia(DataUtil.adicionaDias(mesesProgressaoSelecionado.getInicioVigencia(), -1));
        selecionado.setMesesProgressao(Util.adicionarObjetoEmLista(selecionado.getMesesProgressao(), ultimoMesAdicionado));
    }

    public void redirecionaEditar(BigDecimal identificador) {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + identificador.longValue() + "/");
    }

    public boolean renderizarEditar(BigDecimal identificador) {
        ProgressaoPCS progressaoPCS = progressaoPCSFacade.recuperar(identificador.longValue());
        return progressaoPCS != null && progressaoPCS.getSuperior() == null;
    }
}
