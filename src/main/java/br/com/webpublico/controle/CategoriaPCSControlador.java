/*
 * Codigo gerado automaticamente em Sat Jul 02 09:02:20 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.CargoCategoriaPCS;
import br.com.webpublico.entidades.CategoriaPCS;
import br.com.webpublico.entidades.PlanoCargosSalarios;
import br.com.webpublico.entidades.rh.pccr.MesesProgressaoCategoria;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CargoFacade;
import br.com.webpublico.negocios.CategoriaPCSFacade;
import br.com.webpublico.negocios.PlanoCargosSalariosFacade;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ManagedBean(name = "categoriaPCSControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoCategoriaPCS", pattern = "/categoria-pccr/novo/", viewId = "/faces/rh/administracaodepagamento/categoriapcs/edita.xhtml"),
    @URLMapping(id = "editarCategoriaPCS", pattern = "/categoria-pccr/editar/#{categoriaPCSControlador.id}/", viewId = "/faces/rh/administracaodepagamento/categoriapcs/edita.xhtml"),
    @URLMapping(id = "listarCategoriaPCS", pattern = "/categoria-pccr/listar/", viewId = "/faces/rh/administracaodepagamento/categoriapcs/lista.xhtml"),
    @URLMapping(id = "verCategoriaPCS", pattern = "/categoria-pccr/ver/#{categoriaPCSControlador.id}/", viewId = "/faces/rh/administracaodepagamento/categoriapcs/visualizar.xhtml")
})
public class CategoriaPCSControlador extends PrettyControlador<CategoriaPCS> implements Serializable, CRUD {

    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    @EJB
    private PlanoCargosSalariosFacade planoCargosSalariosFacade;
    @EJB
    private CargoFacade cargoFacade;
    private ConverterGenerico converterPlanoCargosSalarios;
    private ConverterAutoComplete converterCargo;
    private TreeNode root;
    private TreeNode noSelecionado;
    private CargoCategoriaPCS cargoCategoriaPCS;
    @ManagedProperty(name = "cargoControlador", value = "#{cargoControlador}")
    private CargoControlador cargoControlador;
    private ConverterAutoComplete converterCategoria;
    private Integer ordem;

    //Novo
    private CategoriaPCS filha;

    private MesesProgressaoCategoria mesesPromocaoSelecionado;


    public TreeNode getNoSelecionado() {
        return noSelecionado;
    }

    private List<CargoCategoriaPCS> lista;

    public void carregaLista(CategoriaPCS cat) {
//        if (cat.getId() != null) {
//            cat = categoriaPCSFacade.recuperar(cat.getId());
//            lista = cat.getCargosCategoriaPCS();
        filha = cat;
//        }
        cargoCategoriaPCS = null;
    }

    public List<CargoCategoriaPCS> getCargosCategorias() {
        return lista;
    }

    public void setNoSelecionado(TreeNode noSelecionado) {
        this.noSelecionado = noSelecionado;
    }

    public CategoriaPCSControlador() {
        super(CategoriaPCS.class);
    }

    public CategoriaPCSFacade getFacade() {
        return categoriaPCSFacade;
    }

    public CargoCategoriaPCS getCargoCategoriaPCS() {
        return cargoCategoriaPCS;
    }

    public void setCargoCategoriaPCS(CargoCategoriaPCS cargoCategoriaPCS) {
        this.cargoCategoriaPCS = cargoCategoriaPCS;
    }

    public void novoCargoCategoria() {
        this.cargoCategoriaPCS = new CargoCategoriaPCS();
        inicializaVigencia();
    }

    public CategoriaPCS getFilha() {
        return filha;
    }

    public void setFilha(CategoriaPCS filha) {
        this.filha = filha;
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado != null) {
            if (selecionado.getPlanoCargosSalarios() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione o Plano de Cargos e Salários.");
            }
            if (selecionado.getCodigo() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Código deve ser preenchido.");
            }
            if (Strings.isNullOrEmpty(selecionado.getDescricao())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Descrição deve ser preenchido.");
            }
            if (selecionado.getFilhos().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Categoria deve ter pelo menos um nível.");
            }
            for (CategoriaPCS nivel : selecionado.getFilhos()) {
                validarCategoriaFilha(nivel);
            }
        }
        ve.lancarException();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/categoria-pccr/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "novoCategoriaPCS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        //cargoCategoriaPCS = new CargoCategoriaPCS();
        filha = new CategoriaPCS();
        ordem = 0;
        inicializaVigencia();

    }

    @URLAction(mappingId = "editarCategoriaPCS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        //cargoCategoriaPCS = new CargoCategoriaPCS();
        filha = new CategoriaPCS();
//        cargoControlador.setTipoPCSSelecionado(selecionado.getPlanoCargosSalarios().getTipoPCS());
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
    }

    @URLAction(mappingId = "verCategoriaPCS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public AbstractFacade getFacede() {
        return categoriaPCSFacade;
    }

    public List<SelectItem> getPlanoCargosSalariosSelectItem() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (PlanoCargosSalarios object : planoCargosSalariosFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<Cargo> completaCargos(String parte) {
        if (selecionado == null || selecionado.getPlanoCargosSalarios() == null) {
            return null;
        }
        return cargoFacade.buscarCargosPorTipoPCSAndVigenteAndAtivoAndCodigoOrDescricao(parte.trim(), selecionado.getPlanoCargosSalarios().getTipoPCS());
    }

    public List<CategoriaPCS> completaCategoriaPCSs(String parte) {
//        CategoriaPCS categoria = (CategoriaPCS) selecionado;
//        if (categoria == null || categoria.getPlanoCargosSalarios() == null) {
//            return null;
//        }
        return categoriaPCSFacade.listaFiltrandoVigente(parte.trim());
    }

    public Converter getConverterCargo() {
        if (converterCargo == null) {
            converterCargo = new ConverterAutoComplete(Cargo.class, cargoFacade);
        }
        return converterCargo;
    }

    public ConverterGenerico getConverterPlanoCargosSalarios() {
        if (converterPlanoCargosSalarios == null) {
            converterPlanoCargosSalarios = new ConverterGenerico(PlanoCargosSalarios.class, planoCargosSalariosFacade);
        }
        return converterPlanoCargosSalarios;
    }

    public void montaArvore(TreeNode raizArvore) {
        CategoriaPCS raiz = (CategoriaPCS) raizArvore.getData();
        acharNoSelecionado(raizArvore);
        for (CategoriaPCS c : categoriaPCSFacade.getFilhosDe(raiz)) {
            TreeNode filhoArvore = new DefaultTreeNode(c, raizArvore);
            acharNoSelecionado(filhoArvore);
            montaArvore(filhoArvore);
        }
    }

    public TreeNode getArvore() {
        root = new DefaultTreeNode("root", null);
        List<CategoriaPCS> raiz = categoriaPCSFacade.getRaizPorPlano(selecionado.getPlanoCargosSalarios());
        for (CategoriaPCS cp : raiz) {
            TreeNode raizVisual = new DefaultTreeNode(cp, root);
            if (raiz != null) {
                montaArvore(raizVisual);
            }
        }
        return root;
    }

    public TreeNode acharNoSelecionado(TreeNode treeNode) {
        if (selecionado != null) {
            if (treeNode.getData().equals(((CategoriaPCS) selecionado).getSuperior())) {
                treeNode.setSelected(true);
                treeNode.setExpanded(true);
            }

        }
        return treeNode;
    }

    public void removeCargo(CargoCategoriaPCS e) {
        if (filha.getCargosCategoriaPCS().contains(e)) {
            filha.getCargosCategoriaPCS().remove(e);
        }

    }

    public void editarCargo(CargoCategoriaPCS cargo) {
        cargoCategoriaPCS = cargo;
        if (filha.getCargosCategoriaPCS().contains(cargo)) {
            filha.getCargosCategoriaPCS().remove(cargo);
        }
    }

    public void validarEFecharDialog() {
        try {
            validarCategoriaFilha(filha);
            FacesUtil.executaJavaScript("dialog.hide();");
            filha = new CategoriaPCS();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }


    public void validarCategoriaFilha(CategoriaPCS cat) {
        ValidacaoException ve = new ValidacaoException();
        if (cat != null) {
            if (Strings.isNullOrEmpty(cat.getRequisito())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Requisito do nível deve ser preenchido.");
            }
            if (Strings.isNullOrEmpty(cat.getDescricao())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Nome do nível deve ser preenchido.");
            }
            if (cat.getCodigo() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Código do nível deve ser preenchido.");
            }
            if (cat.getCargosCategoriaPCS().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O nível deve ter pelo menos um Cargo adicionado.");
            }
        }
        ve.lancarException();
    }


    public void adicionarCargo() {
        CategoriaPCS cPCS = ((CategoriaPCS) selecionado);
        if (validaCargoCategoria()) {
            cargoCategoriaPCS.setCategoriaPCS(cPCS);
            cPCS.getCargosCategoriaPCS().add(cargoCategoriaPCS);
            cargoCategoriaPCS = new CargoCategoriaPCS();
        }
    }

    public void adicionarCargoCategoria() {
        if (validaCargoCategoria()) {
            cargoCategoriaPCS.setCategoriaPCS(filha);
            filha.getCargosCategoriaPCS().add(cargoCategoriaPCS);
            cargoCategoriaPCS = null;
        }
    }

    public CategoriaPCS getCategoriaPCS() {
        return ((CategoriaPCS) selecionado);
    }

    public boolean validaCargoCategoria() {
        boolean retorno = true;
        if (cargoCategoriaPCS.getInicioVigencia() == null) {
            FacesUtil.addWarn("Atenção", "Campo início vigência é carater obrigatório o preenchimento.");
            retorno = false;
        }
        if (cargoCategoriaPCS.getCargo() == null) {
            FacesUtil.addWarn("Atenção", "Campo cargo é obrigatório o preenchimento.");
            retorno = false;
        }
        if (cargoCategoriaPCS.getFinalVigencia() != null && cargoCategoriaPCS.getInicioVigencia() != null) {
            if (cargoCategoriaPCS.getInicioVigencia().after(cargoCategoriaPCS.getFinalVigencia())) {
                FacesUtil.addWarn("Atenção", "A data inicial é maior que a data final");
                retorno = false;
            }
        }
        return retorno;
    }

    public void inicializaVigencia() {
        CategoriaPCS cPCS = ((CategoriaPCS) selecionado);
        if (selecionado.getPlanoCargosSalarios() != null && cargoCategoriaPCS != null) {
            cargoCategoriaPCS.setInicioVigencia(selecionado.getPlanoCargosSalarios().getInicioVigencia());
            cargoCategoriaPCS.setFinalVigencia(selecionado.getPlanoCargosSalarios().getFinalVigencia());
        }
    }

    public CargoControlador getCargoControlador() {
        return cargoControlador;
    }

    public void setCargoControlador(CargoControlador cargoControlador) {
        this.cargoControlador = cargoControlador;
    }

    public Converter getConverterCategoria() {
        if (converterCategoria == null) {
            converterCategoria = new ConverterAutoComplete(CategoriaPCS.class, categoriaPCSFacade);
        }
        return converterCategoria;
    }

    public void adicionarFilho() {
        try {
            validarFilha();
            ordem++;
            filha.setOrdem(ordem);
            filha.setSuperior(selecionado);
            filha.setPlanoCargosSalarios(selecionado.getPlanoCargosSalarios());
            selecionado.getFilhos().add(filha);
            filha = new CategoriaPCS();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removeFilho(CategoriaPCS fi) {
        selecionado.getFilhos().remove(fi);
    }

    private void validarFilha() {
        ValidacaoException ve = new ValidacaoException();
        if (filha != null) {
            if (filha.getCodigo() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Código é obrigatório.");
            }
            if (Strings.isNullOrEmpty(filha.getDescricao())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Nome é obrigatório.");
            }
            if (selecionado.getPlanoCargosSalarios() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Selecione um Plano de Cargos e Salários.");
            }
        }
        ve.lancarException();
    }


    public void criarMesesProgressao() {
        this.mesesPromocaoSelecionado = new MesesProgressaoCategoria();
    }

    public MesesProgressaoCategoria getMesesPromocaoSelecionado() {
        return mesesPromocaoSelecionado;
    }

    public void setMesesPromocaoSelecionado(MesesProgressaoCategoria mesesPromocaoSelecionado) {
        this.mesesPromocaoSelecionado = mesesPromocaoSelecionado;
    }

    public void selecionarMesesProgressao(MesesProgressaoCategoria mesesProgressao) {
        mesesPromocaoSelecionado = (MesesProgressaoCategoria) Util.clonarObjeto(mesesProgressao);
    }

    public void confirmarMesesProgressao() {
        try {
            validarCamposMesesProgressao();

            atribuirFinalDeVigenciaParaUltimoRegistroAdicionado();
            mesesPromocaoSelecionado.setCategoriaPCS(selecionado);
            selecionado.setMesesPromocao(Util.adicionarObjetoEmLista(selecionado.getMesesPromocao(), mesesPromocaoSelecionado));
            cancelarMesesProgressao();
        } catch (ValidacaoException val) {
            FacesUtil.printAllFacesMessages(val.getMensagens());
        }
    }

    private void validarCamposMesesProgressao() {
        Util.validarCampos(mesesPromocaoSelecionado);
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(mesesPromocaoSelecionado.getInicioVigencia(), mesesPromocaoSelecionado.getFinalVigencia())) {
            throw new ValidacaoException("A data de inicio de vigência deve ser anterior a data de final de vigência.");
        }

        if (!isVigenciaDoNovoMesValida()) {
            throw new ValidacaoException("A data de inicio de vigência deve ser posterior à do último registro adicionado");
        }
    }

    public void cancelarMesesProgressao() {
        this.mesesPromocaoSelecionado = null;
    }

    public void removerMesesProgressao(MesesProgressaoCategoria mesesProgressao) {
        ordenarMesesProgressoes();
        int indexMesRemovido = selecionado.getMesesPromocao().indexOf(mesesProgressao);
        selecionado.getMesesPromocao().remove(mesesProgressao);
        try {
            mesesPromocaoSelecionado = selecionado.getMesesPromocao().get(indexMesRemovido);
            atribuirFinalDeVigenciaParaUltimoRegistroAdicionado();
            return;
        } catch (IndexOutOfBoundsException ioe) {
        }

        try {
            MesesProgressaoCategoria ultimoMes = selecionado.getMesesPromocao().get(indexMesRemovido - 1);
            ultimoMes.setFinalVigencia(null);
            selecionado.setMesesPromocao(Util.adicionarObjetoEmLista(selecionado.getMesesPromocao(), ultimoMes));
        } catch (IndexOutOfBoundsException ioe) {
        }

    }


    private boolean isVigenciaDoNovoMesValida() {
        if (selecionado.getMesesPromocao() == null || selecionado.getMesesPromocao().isEmpty()) {
            return true;
        }

        MesesProgressaoCategoria ultimoMesAdicionado;

        ordenarMesesProgressoes();

        if (selecionado.getMesesPromocao().get(0).equals(mesesPromocaoSelecionado)) {
            return true;
        }

        try {
            ultimoMesAdicionado = selecionado.getMesesPromocao().get(selecionado.getMesesPromocao().indexOf(mesesPromocaoSelecionado) - 1);
        } catch (IndexOutOfBoundsException ie) {
            ultimoMesAdicionado = selecionado.getMesesPromocao().get(selecionado.getMesesPromocao().size() - 1);
        }

        if (ultimoMesAdicionado == null)
            return true;

        if (mesesPromocaoSelecionado.getInicioVigencia().compareTo(ultimoMesAdicionado.getInicioVigencia()) <= 0) {
            return false;
        }

        return true;
    }

    private void ordenarMesesProgressoes() {
        Collections.sort(selecionado.getMesesPromocao(), new Comparator<MesesProgressaoCategoria>() {
            @Override
            public int compare(MesesProgressaoCategoria o1, MesesProgressaoCategoria o2) {
                return o1.getInicioVigencia().compareTo(o2.getInicioVigencia());
            }
        });
    }


    private void atribuirFinalDeVigenciaParaUltimoRegistroAdicionado() {
        if (selecionado.getMesesPromocao() == null || selecionado.getMesesPromocao().isEmpty())
            return;
        MesesProgressaoCategoria ultimoMesAdicionado;
        ordenarMesesProgressoes();

        if (selecionado.getMesesPromocao().get(0).equals(mesesPromocaoSelecionado)) {
            return;
        }

        try {
            ultimoMesAdicionado = selecionado.getMesesPromocao().get(selecionado.getMesesPromocao().indexOf(mesesPromocaoSelecionado) - 1);
        } catch (IndexOutOfBoundsException ie) {
            ultimoMesAdicionado = selecionado.getMesesPromocao().get(selecionado.getMesesPromocao().size() - 1);
        }


        if (ultimoMesAdicionado == null)
            return;

        ultimoMesAdicionado.setFinalVigencia(DataUtil.adicionaDias(mesesPromocaoSelecionado.getInicioVigencia(), -1));
        selecionado.setMesesPromocao(Util.adicionarObjetoEmLista(selecionado.getMesesPromocao(), ultimoMesAdicionado));
    }
}
