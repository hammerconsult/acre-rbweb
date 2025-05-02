package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FileUploadPrimeFaces;
import br.com.webpublico.entidadesauxiliares.FiltroRefeicaoMaterial;
import br.com.webpublico.entidadesauxiliares.RefeicaoMaterialVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.RefeicaoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-refeicao", pattern = "/refeicao/novo/", viewId = "/faces/administrativo/materiais/alimentacao-escolar/refeicao/edita.xhtml"),
    @URLMapping(id = "editar-refeicao", pattern = "/refeicao/editar/#{refeicaoControlador.id}/", viewId = "/faces/administrativo/materiais/alimentacao-escolar/refeicao/edita.xhtml"),
    @URLMapping(id = "listar-refeicao", pattern = "/refeicao/listar/", viewId = "/faces/administrativo/materiais/alimentacao-escolar/refeicao/lista.xhtml"),
    @URLMapping(id = "ver-refeicao", pattern = "/refeicao/ver/#{refeicaoControlador.id}/", viewId = "/faces/administrativo/materiais/alimentacao-escolar/refeicao/visualizar.xhtml"),
})
public class RefeicaoControlador extends PrettyControlador<Refeicao> implements Serializable, CRUD {

    @EJB
    private RefeicaoFacade facade;
    @EJB
    private SistemaFacade sistemaFacade;
    private FiltroRefeicaoMaterial filtroRefeicaoMaterial;
    private List<RefeicaoMaterialVO> materiais;
    private RefeicaoEspecial novaRefeicaoEspecial;
    private StreamedContent imagemFoto;
    private Arquivo arquivo;
    private FileUploadPrimeFaces fileUploadPrimeFaces;


    public RefeicaoControlador() {
        super(Refeicao.class);
    }

    @Override
    @URLAction(mappingId = "nova-refeicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataCadastro(sistemaFacade.getDataOperacao());
        filtroRefeicaoMaterial = new FiltroRefeicaoMaterial(TipoFiltroMaterial.LICITACAO);

        if (selecionado.getComposicaoNutricional() == null) {
            selecionado.setComposicaoNutricional(new ComposicaoNutricional());
        }
        novaRefeicaoEspecial = new RefeicaoEspecial();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", imagemFoto);
    }

    @Override
    @URLAction(mappingId = "ver-refeicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        carregarFoto((selecionado).getArquivo());
    }

    @Override
    @URLAction(mappingId = "editar-refeicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        filtroRefeicaoMaterial = new FiltroRefeicaoMaterial(TipoFiltroMaterial.LICITACAO);

        if (selecionado.getComposicaoNutricional() == null) {
            selecionado.setComposicaoNutricional(new ComposicaoNutricional());
        }
        novaRefeicaoEspecial = new RefeicaoEspecial();
        carregarFoto((selecionado).getArquivo());
    }

    @Override
    public String getCaminhoPadrao() {
        return "/refeicao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public boolean validaRegrasEspecificas() {
        validarCampos();
        return super.validaRegrasEspecificas();
    }

    public void limparFiltrosPesquisaMateriais() {
        filtroRefeicaoMaterial.setLicitacao(null);
        filtroRefeicaoMaterial.setDispensa(null);
        filtroRefeicaoMaterial.setContrato(null);
        filtroRefeicaoMaterial.setFornecedor(null);
    }

    public void limparFiltroContrato() {
        filtroRefeicaoMaterial.setContrato(null);
        filtroRefeicaoMaterial.setFornecedor(null);
    }

    public List<SelectItem> getTiposRefeicao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoRefeicao object : TipoRefeicao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getPublicos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (PublicoAlvoPreferencial object : PublicoAlvoPreferencial.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposRefeicaoEspecial() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoRefeicaoEspecial object : TipoRefeicaoEspecial.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposFiltroMaterial() {
        return Util.getListSelectItemSemCampoVazio(TipoFiltroMaterial.values(), false);
    }

    public FiltroRefeicaoMaterial getFiltroRefeicaoMaterial() {
        return filtroRefeicaoMaterial;
    }

    public void setFiltroRefeicaoMaterial(FiltroRefeicaoMaterial filtroRefeicaoMaterial) {
        this.filtroRefeicaoMaterial = filtroRefeicaoMaterial;
    }

    public List<RefeicaoMaterialVO> getMateriais() {
        return materiais;
    }

    public void setMateriais(List<RefeicaoMaterialVO> materiais) {
        this.materiais = materiais;
    }


    public List<Contrato> completarContratos(String parte) {
        if (filtroRefeicaoMaterial != null) {
            if (filtroRefeicaoMaterial.getTipoFiltroMaterial().isLicitacao() && filtroRefeicaoMaterial.getLicitacao() != null) {
                return facade.getContratoFacade().buscarContratoLicitacao(filtroRefeicaoMaterial.getLicitacao());
            } else if (filtroRefeicaoMaterial.getTipoFiltroMaterial().isDispensaInexigibilidade() && filtroRefeicaoMaterial.getDispensa() != null) {
                return facade.getContratoFacade().buscarContratoDispensa(filtroRefeicaoMaterial.getDispensa());
            }
        }
        return facade.getContratoFacade().buscarFiltrandoPorSituacao(parte.trim(), SituacaoContrato.DEFERIDO);
    }

    public List<Pessoa> completarFornecedores(String parte) {
        return facade.buscarFornecedores(parte.trim());
    }

    public List<Licitacao> completarLicitacoes(String parte) {
        return facade.getLicitacaoFacade().buscarLicitacoesUsuarioGestorUnidade(parte);
    }

    public List<DispensaDeLicitacao> completarDispensaDeLicitacao(String parte) {
        return facade.getDispensaDeLicitacaoFacade().buscarDispensaDeLicitacaoPorNumeroOrExercicioOrResumoAndUsuarioAndGestor(parte.trim(), facade.getSistemaFacade().getUsuarioCorrente(), Boolean.TRUE);
    }

    public void pesquisarMateriais() {
        try {
            validarFiltroObrigatorio();
            materiais = facade.buscarMateriais(filtroRefeicaoMaterial.getCondicaoSql());
            FacesUtil.atualizarComponente("formDlgMateriais");
            FacesUtil.executaJavaScript("dlgMateriais.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void setFornecedorContrato() {
        if (filtroRefeicaoMaterial.getContrato() != null) {
            filtroRefeicaoMaterial.setFornecedor(filtroRefeicaoMaterial.getContrato().getContratado());
        }
    }

    public boolean todosItensSelcionados() {
        boolean todosSelecionados = true;
        if (materiais != null && !materiais.isEmpty()) {
            for (RefeicaoMaterialVO item : materiais) {
                if (!item.getSelecionado()) {
                    todosSelecionados = false;
                    break;
                }
            }
        }
        return todosSelecionados;
    }

    public void selecionarTodosItens(boolean selecinado) {
        for (RefeicaoMaterialVO item : materiais) {
            item.setSelecionado(selecinado);
        }
    }

    public boolean hasItemSelecionado() {
        boolean selecionado = false;
        if (materiais != null) {
            for (RefeicaoMaterialVO item : materiais) {
                if (item.getSelecionado()) {
                    selecionado = true;
                    break;
                }
            }
        }
        return selecionado;
    }

    public void confirmarMaterialSelecionado() {
        try {
            validarMaterialSelecionado();
            selecionado.setMateriais(new ArrayList<>());
            for (RefeicaoMaterialVO mat : materiais) {
                if (mat.getSelecionado()) {
                    novoMaterial(mat.getMaterial());
                }
            }
            FacesUtil.atualizarComponente("Formulario:tab-view-geral:tabelaMateriais");
            cancelarMaterial();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarMaterial() {
        try {
            validarMaterialJaAdicionado();
            if (filtroRefeicaoMaterial.getMaterial() != null) {
                novoMaterial(filtroRefeicaoMaterial.getMaterial());
            }
            filtroRefeicaoMaterial.setMaterial(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void novoMaterial(Material material) {
        RefeicaoMaterial refeicaoMaterial = new RefeicaoMaterial();
        refeicaoMaterial.setRefeicao(selecionado);
        refeicaoMaterial.setMaterial(material);
        Util.adicionarObjetoEmLista(selecionado.getMateriais(), refeicaoMaterial);
    }


    public void cancelarMaterial() {
        materiais.clear();
        FacesUtil.executaJavaScript("dlgMateriais.hide();");
    }

    public void removerMaterial(RefeicaoMaterial material) {
        selecionado.getMateriais().remove(material);
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getComposicaoNutricional() != null) {
            if (selecionado.getComposicaoNutricional().getEnergiaKCAL() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Energia (KCAL) é obrigatório.");
            }
            if (selecionado.getComposicaoNutricional().getCHOg() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo CHO (g) é obrigatório.");
            }
            if (selecionado.getComposicaoNutricional().getPTNg() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo  PTN (g) é obrigatório.");
            }
            if (selecionado.getComposicaoNutricional().getLPDg() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo LPD (g) é obrigatório.");
            }
            if (selecionado.getComposicaoNutricional().getFIBRASg() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo FIBRAS (g) é obrigatório.");
            }
            if (selecionado.getComposicaoNutricional().getVITAmcg() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo VIT. A (mcg) é obrigatório.");
            }
            if (selecionado.getComposicaoNutricional().getVITCmcg() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo VIT. C (mg) é obrigatório.");
            }
            if (selecionado.getComposicaoNutricional().getCAmg() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Ca (mg) é obrigatório.");
            }
            if (selecionado.getComposicaoNutricional().getFEmg() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Fe (mg) é obrigatório.");
            }
            if (selecionado.getComposicaoNutricional().getZNmg() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Zn (mg) é obrigatório.");
            }
            if (selecionado.getComposicaoNutricional().getNAmg() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Na (mg) é obrigatório.");
            }
            if (selecionado.getComposicaoNutricional().getEnergiaKCAL() != null) {
                if (selecionado.getComposicaoNutricional().getEnergiaKCAL().compareTo(BigDecimal.ZERO) == 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Energia(KCAL) deve ser maior que zero.");
                }
            }
        }
        ve.lancarException();
    }

    public void validarMaterialSelecionado() {
        ValidacaoException ve = new ValidacaoException();
        if (!hasItemSelecionado()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione um material para continuar.");
        }
        ve.lancarException();
    }

    public void validarFiltroObrigatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroRefeicaoMaterial.getLicitacao() == null && filtroRefeicaoMaterial.getDispensa() == null && filtroRefeicaoMaterial.getContrato() == null && filtroRefeicaoMaterial.getFornecedor() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione ao menos um filtro.");
        }
        ve.lancarException();
    }

    public void validarMaterialJaAdicionado() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroRefeicaoMaterial.getMaterial() != null) {
            if (materialJaAdicionado()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Material já adicionado na lista.");
            }
        }
        ve.lancarException();
    }

    private boolean materialJaAdicionado() {
        for (RefeicaoMaterial matNaLista : selecionado.getMateriais()) {
            if (filtroRefeicaoMaterial.getMaterial().equals(matNaLista.getMaterial())) {
                return true;
            }
        }
        return false;
    }

    private void validarTipoRefeicaoEspecialJaAdicionada() {
        ValidacaoException ve = new ValidacaoException();
        for (RefeicaoEspecial refeicaoEsp : selecionado.getRefeicoesEspeciais()) {
            if (refeicaoEsp.getTipoRefeicaoEspecial() != null && refeicaoEsp.getTipoRefeicaoEspecial().equals(novaRefeicaoEspecial.getTipoRefeicaoEspecial())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Tipo de refeição especial já adicionado.");
                break;
            }
        }
        ve.lancarException();
    }

    public void removeRefeicaoEspecial(RefeicaoEspecial refeicaoEspecial) {
        selecionado.getRefeicoesEspeciais().remove(refeicaoEspecial);

        if (selecionado.getRefeicoesEspeciais().isEmpty()) {
            selecionado.setObservacaoRefeicaoEspecial(null);
        }
    }

    public void addRefeicaoEspecial() {
        try {
            if (novaRefeicaoEspecial.getTipoRefeicaoEspecial() != null) {
                validarTipoRefeicaoEspecialJaAdicionada();
                novaRefeicaoEspecial.setRefeicao(selecionado);
                selecionado.getRefeicoesEspeciais().add(novaRefeicaoEspecial);
                novaRefeicaoEspecial = new RefeicaoEspecial();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }


    public void uploadArquivo(FileUploadEvent event) {
        try {
            fileUploadPrimeFaces = new FileUploadPrimeFaces(event);
            imagemFoto = new DefaultStreamedContent(event.getFile().getInputstream(), "image/jpg", event.getFile().getFileName());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", imagemFoto);
            arquivo = facade.getArquivoFacade().criarArquivo(event.getFile());
            selecionado.setArquivo(arquivo);
        } catch (IOException ex) {
            logger.error("Erro: ", ex);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao adicionar arquivo. Detalhes do erro: " + e.getMessage());
        }
    }

    public Boolean hasImagemRefeicao() {
        try {
            StreamedContent sc = ((StreamedContent) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("imagem-foto"));
            return sc.getName().trim().length() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void carregarFoto(Arquivo arq) {
        if (arq != null) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                for (ArquivoParte a :  facade.getArquivoFacade().recuperaDependencias(arq.getId()).getPartes()) {
                    buffer.write(a.getDados());
                }
                InputStream is = new ByteArrayInputStream(buffer.toByteArray());
                imagemFoto = new DefaultStreamedContent(is, arq.getMimeType(), arq.getNome());
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", imagemFoto);
                selecionado.setArquivo(arq);
            } catch (Exception ex) {
                logger.error("Erro: ", ex);
            }
        }
    }

    public TipoMascaraUnidadeMedida getTipoMascaraDefault() {
        return TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
    }

    public RefeicaoEspecial getNovaRefeicaoEspecial() {
        return novaRefeicaoEspecial;
    }

    public void setNovaRefeicaoEspecial(RefeicaoEspecial novaRefeicaoEspecial) {
        this.novaRefeicaoEspecial = novaRefeicaoEspecial;
    }

    public FileUploadPrimeFaces getFileUploadPrimeFaces() {
        return fileUploadPrimeFaces;
    }

    public void setFileUploadPrimeFaces(FileUploadPrimeFaces fileUploadPrimeFaces) {
        this.fileUploadPrimeFaces = fileUploadPrimeFaces;
    }

    public enum TipoFiltroMaterial {
        LICITACAO("Licitação"),
        DISPENSA_INEXIBILIDADE("Dispensa/Inexigibilidade");

        private String descricao;

        TipoFiltroMaterial(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public Boolean isLicitacao() {
            return LICITACAO.equals(this);
        }

        public Boolean isDispensaInexigibilidade() {
            return DISPENSA_INEXIBILIDADE.equals(this);
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

}
