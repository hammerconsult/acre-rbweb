package br.com.webpublico.controle.rh.cadastrofuncional;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.cadastrofuncional.EnquadramentoFuncionalLote;
import br.com.webpublico.entidades.rh.cadastrofuncional.EnquadramentoFuncionalLoteItem;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.cadastrofuncional.EnquadramentoFuncionalLoteFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @Author peixe
 */
@ManagedBean(name = "enquadramanentoFuncionalPorLoteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoEnquadramentoFuncionalLote", pattern = "/enquadramento-funcional-lote/novo/", viewId = "/faces/rh/cadastrofuncional/enquadramento-funcional-lote/edita.xhtml"),
    @URLMapping(id = "editarEnquadramentoFuncionalLote", pattern = "/enquadramento-funcional-lote/editar/#{enquadramanentoFuncionalPorLoteControlador.id}/", viewId = "/faces/rh/cadastrofuncional/enquadramento-funcional-lote/edita.xhtml"),
    @URLMapping(id = "listarEnquadramentoFuncionalLote", pattern = "/enquadramento-funcional-lote/listar/", viewId = "/faces/rh/cadastrofuncional/enquadramento-funcional-lote/lista.xhtml"),
    @URLMapping(id = "verEnquadramentoFuncionalLote", pattern = "/enquadramento-funcional-lote/ver/#{enquadramanentoFuncionalPorLoteControlador.id}/", viewId = "/faces/rh/cadastrofuncional/enquadramento-funcional-lote/visualizar.xhtml")
})
public class EnquadramanentoFuncionalPorLoteControlador extends PrettyControlador<EnquadramentoFuncionalLote> implements Serializable, CRUD {

    protected static final Logger logger = LoggerFactory.getLogger(EnquadramanentoFuncionalPorLoteControlador.class);

    @EJB
    private EnquadramentoFuncionalLoteFacade facade;
    @EJB
    private PlanoCargosSalariosFacade planoCargosSalariosFacade;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;

    private PlanoCargosSalarios planoCargosSalariosDe;
    private CategoriaPCS categoriaPcsDe;
    private ProgressaoPCS progressaoPcsDe;
    private Cargo cargoDe;

    private PlanoCargosSalarios planoCargosSalariosPara;
    private CategoriaPCS categoriaPcsPara;
    private ProgressaoPCS progressaoPcsPara;

    private List<DeParaCategoriaPCS> deParaCategorias;
    private List<DeParaProgressaoPCS> deParaProgressoes;

    private boolean filtrarTodosServidoresCargo = false;
    private Boolean marcarTodosConsideraProgAut;


    public EnquadramanentoFuncionalPorLoteControlador() {
        super(EnquadramentoFuncionalLote.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/enquadramento-funcional-lote/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }


    @Override
    @URLAction(mappingId = "novoEnquadramentoFuncionalLote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        deParaCategorias = Lists.newLinkedList();
        deParaProgressoes = Lists.newLinkedList();
        planoCargosSalariosDe = null;
        categoriaPcsDe = null;
        progressaoPcsDe = null;
        planoCargosSalariosPara = null;
        categoriaPcsPara = null;
        progressaoPcsPara = null;
        cargoDe = null;
    }

    @Override
    @URLAction(mappingId = "verEnquadramentoFuncionalLote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        preencherObjetos();
        carregarValores();
    }

    @URLAction(mappingId = "editarEnquadramentoFuncionalLote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void pesquisar() {
        operacao = Operacoes.EDITAR;
        super.editar();
        preencherObjetos();
        carregarValores();

    }

    public void carregarValores() {
        for (EnquadramentoFuncionalLoteItem item : selecionado.getItens()) {
            definirVencimebtoBase(item.getEnquadramentoFuncAntigo());
            definirVencimebtoBase(item.getEnquadramentoFuncNovo());
        }

    }

    public boolean isFiltrarTodosServidoresCargo() {
        return filtrarTodosServidoresCargo;
    }

    public void setFiltrarTodosServidoresCargo(boolean filtrarTodosServidoresCargo) {
        this.filtrarTodosServidoresCargo = filtrarTodosServidoresCargo;
    }

    void preencherObjetos() {
        deParaCategorias = Lists.newLinkedList();
        deParaProgressoes = Lists.newLinkedList();

        planoCargosSalariosDe = selecionado.getPlanoCargosSalariosOrigem();
        planoCargosSalariosPara = selecionado.getPlanoCargosSalariosDestino();

        categoriaPcsDe = selecionado.getCategoriaPCSOrigem();
        categoriaPcsPara = selecionado.getCategoriaPCSDestino();

        progressaoPcsDe = selecionado.getProgressaoPCSOrigem();
        progressaoPcsPara = selecionado.getProgressaoPCSDestino();

        cargoDe = selecionado.getCargo();
    }


    @Override
    public void cancelar() {
        super.cancelar();
    }


    @Override
    public void salvar() {
        try {
            validarCampos();
            copiarRecursos();
            selecionado.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            selecionado.setDataRegistro(new Date());
            super.salvar();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            return;
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void copiarRecursos() {
        selecionado.setPlanoCargosSalariosOrigem(categoriaPcsDe.getPlanoCargosSalarios());
        selecionado.setCategoriaPCSOrigem(categoriaPcsDe);
        selecionado.setProgressaoPCSOrigem(progressaoPcsDe);
        selecionado.setCargo(cargoDe);

        selecionado.setPlanoCargosSalariosDestino(categoriaPcsPara.getPlanoCargosSalarios());
        selecionado.setCategoriaPCSDestino(categoriaPcsPara);
        selecionado.setProgressaoPCSDestino(progressaoPcsPara);
    }

    public boolean isEdicaoOrVisualizacao() {
        return isOperacaoEditar() || isOperacaoVer();
    }


    public void validarCampos() {
        ValidacaoException val = new ValidacaoException();
        if (selecionado.getFinalVigencia() == null) {
            val.adicionarMensagemDeCampoObrigatorio("Preencha corretamente o final de vigência do registro anterior");
        }
        if (selecionado.getFinalVigencia() == null) {
            val.adicionarMensagemDeCampoObrigatorio("Preencha corretamente o início de vigência do novo registro");
        }
        validarPlanosSelecionados();
        val.lancarException();
    }

    public void validarPlanosSelecionados() {
        ValidacaoException val = new ValidacaoException();
        if (categoriaPcsDe == null) {
            val.adicionarMensagemDeCampoObrigatorio("Selecione corretamente a Categoria Origem no De/Para");
        }
        if (categoriaPcsPara == null) {
            val.adicionarMensagemDeCampoObrigatorio("Selecione corretamente a Categoria Destino no De/Para");
        }

        if (progressaoPcsDe == null) {
            val.adicionarMensagemDeCampoObrigatorio("Selecione corretamente a Progressão Origem no De/Para");
        }

        if (progressaoPcsPara == null) {
            val.adicionarMensagemDeCampoObrigatorio("Selecione corretamente a Progressão Destino no De/Para");
        }

        if (deParaCategorias != null && deParaCategorias.isEmpty()) {
            val.adicionarMensagemDeCampoObrigatorio("Selecione corretamente o De/Para das Categorias");
        }

        if (deParaProgressoes != null && deParaProgressoes.isEmpty()) {
            val.adicionarMensagemDeCampoObrigatorio("Selecione corretamente o De/Para das Progressões");
        }
        if (deParaProgressoes != null) {
            for (DeParaProgressaoPCS deParaProgressoe : deParaProgressoes) {
                if (deParaProgressoe.getPara() == null) {
                    val.adicionarMensagemDeCampoObrigatorio("Selecione corretamente TODAS as progressões no De/Para das Progressões");
                }
            }
        }
        if (deParaCategorias != null) {
            for (DeParaCategoriaPCS deParaCat : deParaCategorias) {
                if (deParaCat.getPara() == null) {
                    val.adicionarMensagemDeCampoObrigatorio("Selecione corretamente TODAS as categorias no De/Para das Categorias");
                }
            }
        }


        val.lancarException();
    }

    public PlanoCargosSalarios getPlanoCargosSalariosDe() {
        return planoCargosSalariosDe;
    }

    public void setPlanoCargosSalariosDe(PlanoCargosSalarios planoCargosSalariosDe) {
        this.planoCargosSalariosDe = planoCargosSalariosDe;
    }

    public CategoriaPCS getCategoriaPcsDe() {
        return categoriaPcsDe;
    }

    public void setCategoriaPcsDe(CategoriaPCS categoriaPcsDe) {
        this.categoriaPcsDe = categoriaPcsDe;
    }

    public ProgressaoPCS getProgressaoPcsDe() {
        return progressaoPcsDe;
    }

    public void setProgressaoPcsDe(ProgressaoPCS progressaoPcsDe) {
        this.progressaoPcsDe = progressaoPcsDe;
    }

    public PlanoCargosSalarios getPlanoCargosSalariosPara() {
        return planoCargosSalariosPara;
    }

    public void setPlanoCargosSalariosPara(PlanoCargosSalarios planoCargosSalariosPara) {
        this.planoCargosSalariosPara = planoCargosSalariosPara;
    }

    public CategoriaPCS getCategoriaPcsPara() {
        return categoriaPcsPara;
    }

    public void setCategoriaPcsPara(CategoriaPCS categoriaPcsPara) {
        this.categoriaPcsPara = categoriaPcsPara;
    }

    public ProgressaoPCS getProgressaoPcsPara() {
        return progressaoPcsPara;
    }

    public void setProgressaoPcsPara(ProgressaoPCS progressaoPcsPara) {
        this.progressaoPcsPara = progressaoPcsPara;
    }


    public List<DeParaCategoriaPCS> getDeParaCategorias() {
        return deParaCategorias;
    }

    public void setDeParaCategorias(List<DeParaCategoriaPCS> deParaCategorias) {
        this.deParaCategorias = deParaCategorias;
    }

    public List<DeParaProgressaoPCS> getDeParaProgressoes() {
        return deParaProgressoes;
    }

    public void setDeParaProgressoes(List<DeParaProgressaoPCS> deParaProgressoes) {
        this.deParaProgressoes = deParaProgressoes;
    }

    public List<SelectItem> getProgressoesDe() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (planoCargosSalariosDe != null && categoriaPcsDe != null) {
            for (ProgressaoPCS object : progressaoPCSFacade.buscaProgressoesSuperioresEnquadramentoPCSPorCategoriaSuperior(categoriaPcsDe, sistemaFacade.getDataOperacao())) {
                toReturn.add(new SelectItem(object, (object.getCodigo() != null ? object.getCodigo() + " - " : "") + "" + object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getProgressoesPara() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (planoCargosSalariosPara != null) {
            for (ProgressaoPCS object : progressaoPCSFacade.buscaProgressoesSuperioresEnquadramentoPCSPorCategoriaSuperior(categoriaPcsPara, sistemaFacade.getDataOperacao())) {
                toReturn.add(new SelectItem(object, (object.getCodigo() != null ? object.getCodigo() + " - " : "") + "" + object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getProgressoesFilhas() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (progressaoPcsPara != null) {
            for (ProgressaoPCS object : progressaoPCSFacade.recuperar(progressaoPcsPara.getId()).getFilhos()) {
                toReturn.add(new SelectItem(object, (object.getCodigo() != null ? object.getCodigo() + " - " : "") + "" + object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getCategoriasFilha() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (categoriaPcsPara != null) {
            for (CategoriaPCS object : categoriaPCSFacade.recuperar(categoriaPcsPara.getId()).getFilhos()) {
                toReturn.add(new SelectItem(object, (object.getCodigo() != null ? object.getCodigo() + " - " : "") + "" + object.getDescricao()));
            }
        }
        return toReturn;
    }


    public List<SelectItem> getCategoriasDe() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (planoCargosSalariosDe != null) {
            for (CategoriaPCS object : categoriaPCSFacade.getRaizPorPlano(planoCargosSalariosDe)) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getCategoriasPara() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (planoCargosSalariosPara != null) {
            for (CategoriaPCS object : categoriaPCSFacade.getRaizPorPlano(planoCargosSalariosPara)) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getPlanos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (PlanoCargosSalarios object : planoCargosSalariosFacade.listaFiltrandoVigencia(UtilRH.getDataOperacao())) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public void preencherCategorias() {
        deParaCategorias = Lists.newLinkedList();
        if (categoriaPcsDe != null && categoriaPcsPara != null) {
            List<CategoriaPCS> correspondentes = categoriaPCSFacade.recuperar(categoriaPcsPara.getId()).getFilhos();
            for (CategoriaPCS categoriaPCS : categoriaPCSFacade.recuperar(categoriaPcsDe.getId()).getFilhos()) {
                List<EnquadramentoPCS> enquadramentos = enquadramentoPCSFacade.buscarEnquadramentosPorCategoria(progressaoPcsDe, categoriaPCS, sistemaFacade.getDataOperacao());

                if (!enquadramentos.isEmpty()) {
                    deParaCategorias.add(new DeParaCategoriaPCS(categoriaPCS, encontrarCorrespondenteCategoria(categoriaPCS, correspondentes)));
                }
            }
        }

    }

    private CategoriaPCS encontrarCorrespondenteCategoria(CategoriaPCS categoriaPCS, List<CategoriaPCS> correspondentes) {
        for (CategoriaPCS correspondente : correspondentes) {
            if (correspondente.getDescricao().trim().equals(categoriaPCS.getDescricao().trim())) {
                return correspondente;
            }
        }
        return null;

    }

    public void preencherProgressoes() {
        deParaProgressoes = Lists.newLinkedList();
        if (progressaoPcsDe != null && progressaoPcsPara != null) {
            List<ProgressaoPCS> filhos = progressaoPCSFacade.recuperar(progressaoPcsDe.getId()).getFilhos();
            List<ProgressaoPCS> correspondentes = progressaoPCSFacade.recuperar(progressaoPcsPara.getId()).getFilhos();
            Collections.sort(filhos);
            for (ProgressaoPCS progressao : filhos) {
                deParaProgressoes.add(new DeParaProgressaoPCS(progressao, encontrarCorrespondente(progressao, correspondentes)));
            }
        }
    }

    private ProgressaoPCS encontrarCorrespondente(ProgressaoPCS progressao, List<ProgressaoPCS> correspondentes) {
        for (ProgressaoPCS correspondente : correspondentes) {
            if (correspondente.getDescricao().trim().equals(progressao.getDescricao().trim())) {
                return correspondente;
            }
        }
        return null;
    }

    public void buscarServidores() {
        logger.debug("buscando servidores Enquadramento em Lote...");

        try {
            validarCampos();
            selecionado.setItens(Lists.<EnquadramentoFuncionalLoteItem>newLinkedList());
            List<EnquadramentoFuncional> enquadramentosDe;

            if (cargoDe != null && filtrarTodosServidoresCargo) {
                enquadramentosDe = enquadramentoFuncionalFacade.buscarEnquadramentosPorCargo(cargoDe, sistemaFacade.getDataOperacao());
            } else {
                enquadramentosDe = enquadramentoFuncionalFacade.buscarEnquadramentosPorCategoriaAndProgressaoSuperior(categoriaPcsDe, progressaoPcsDe, cargoDe, sistemaFacade.getDataOperacao());
            }

            for (EnquadramentoFuncional enquadramentoFuncional : enquadramentosDe) {
                criarItem(enquadramentoFuncional);
            }
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            logger.error("Erro: ", e);
            FacesUtil.addError("Atenção: ", e.getMessage());
        }


    }

    private void criarItem(EnquadramentoFuncional enquadramentoFuncional) {
        definirVencimebtoBase(enquadramentoFuncional);
        enquadramentoFuncional.setFinalVigencia(selecionado.getFinalVigencia());
        EnquadramentoFuncionalLoteItem item = new EnquadramentoFuncionalLoteItem(selecionado, enquadramentoFuncional.getContratoServidor(), enquadramentoFuncional, criarNovoEnquadramentoFuncional(enquadramentoFuncional));
        selecionado.getItens().add(item);
    }

    private void definirVencimebtoBase(EnquadramentoFuncional ef) {
        EnquadramentoPCS pcs = enquadramentoPCSFacade.recuperaValor(ef.getCategoriaPCS(), ef.getProgressaoPCS(), sistemaFacade.getDataOperacao());
        if (pcs != null && pcs.getId() != null) {
            ef.setVencimentoBase(pcs.getVencimentoBase());
        } else {
            ef.setVencimentoBase(BigDecimal.ZERO);
        }
    }

    private EnquadramentoFuncional criarNovoEnquadramentoFuncional(EnquadramentoFuncional enquadramentoFuncional) {
        EnquadramentoFuncional novo = new EnquadramentoFuncional();
        novo.setInicioVigencia(selecionado.getInicioVigencia());
        novo.setContratoServidor(enquadramentoFuncional.getContratoServidor());
        novo.setCategoriaPCS(getCategoriaPCSDoDePara(enquadramentoFuncional.getCategoriaPCS()));
        novo.setProgressaoPCS(getProgressaoPCSDoDePara(enquadramentoFuncional.getProgressaoPCS()));
        novo.setDataCadastro(new Date());
        novo.setConsideraParaProgAutomat(Boolean.TRUE);
        definirVencimebtoBase(novo);
        return novo;
    }

    private CategoriaPCS getCategoriaPCSDoDePara(CategoriaPCS categoriaPCS) {
        ValidacaoException ve = new ValidacaoException();
        for (DeParaCategoriaPCS deParaCategoria : deParaCategorias) {
            if (selecionado.getProgressoesCruzadas()) {
                if (deParaCategoria.getDe().equals(categoriaPCS)) {
                    return deParaCategoria.getPara();
                } else if (deParaCategoria.getDe().getDescricao().equals(categoriaPCS.getDescricao())) {
                    return deParaCategoria.getPara();
                }
            } else {
                if (deParaCategoria.getDe().equals(categoriaPCS)) {
                    return deParaCategoria.getPara();
                }
                ve.adicionarMensagemDeOperacaoNaoPermitida("A categoria " + deParaCategoria.getDe().getSuperior() + " é diferente da categoria " + categoriaPCS.getSuperior());
                ve.lancarException();
            }
        }
        return null;
    }

    private ProgressaoPCS getProgressaoPCSDoDePara(ProgressaoPCS progressaoPCS) {
        ValidacaoException ve = new ValidacaoException();
        for (DeParaProgressaoPCS deParaProgressao : deParaProgressoes) {
            if (selecionado.getProgressoesCruzadas()) {
                if (deParaProgressao.getDe().equals(progressaoPCS)) {
                    return deParaProgressao.getPara();
                } else if (deParaProgressao.getDe().getDescricao().equals(progressaoPCS.getDescricao())) {
                    return deParaProgressao.getPara();
                }
            } else {
                if (deParaProgressao.getDe().equals(progressaoPCS)) {
                    return deParaProgressao.getPara();
                }
                ve.adicionarMensagemDeOperacaoNaoPermitida("A progressão " + deParaProgressao.getDe().getSuperior() + " é diferenta da progressão " + progressaoPCS.getSuperior());
                ve.lancarException();
            }
            ;
        }
        return null;
    }

    public List<Cargo> completarCargos(String filtro) {
        return facade.getCargoFacade().buscarCargosVigentesPorUnidadeOrganizacionalAndUsuario(filtro.trim());
    }

    public Cargo getCargoDe() {
        return cargoDe;
    }

    public void setCargoDe(Cargo cargoDe) {
        this.cargoDe = cargoDe;
    }

    public class DeParaCategoriaPCS {

        private CategoriaPCS de;
        private CategoriaPCS para;

        public DeParaCategoriaPCS() {
        }

        public DeParaCategoriaPCS(CategoriaPCS de, CategoriaPCS para) {
            this.de = de;
            this.para = para;
        }

        public CategoriaPCS getDe() {
            return de;
        }

        public void setDe(CategoriaPCS de) {
            this.de = de;
        }

        public CategoriaPCS getPara() {
            return para;
        }

        public void setPara(CategoriaPCS para) {
            this.para = para;
        }
    }

    public class DeParaProgressaoPCS {
        private ProgressaoPCS de;
        private ProgressaoPCS para;

        public DeParaProgressaoPCS() {
        }

        public DeParaProgressaoPCS(ProgressaoPCS de, ProgressaoPCS para) {
            this.de = de;
            this.para = para;
        }

        public ProgressaoPCS getDe() {
            return de;
        }

        public void setDe(ProgressaoPCS de) {
            this.de = de;
        }

        public ProgressaoPCS getPara() {
            return para;
        }

        public void setPara(ProgressaoPCS para) {
            this.para = para;
        }
    }

    public Boolean getMarcarTodosConsideraProgAut() {
        return marcarTodosConsideraProgAut;
    }

    public void setMarcarTodosConsideraProgAut(Boolean marcarTodosConsideraProgAut) {
        this.marcarTodosConsideraProgAut = marcarTodosConsideraProgAut;
    }

    public void marcarTodosParaConsiderarProgAut() {
        for (EnquadramentoFuncionalLoteItem item : selecionado.getItens()) {
            item.getEnquadramentoFuncNovo().setConsideraParaProgAutomat(marcarTodosConsideraProgAut);
        }
    }
}

