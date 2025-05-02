package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOAlvara;
import br.com.webpublico.entidadesauxiliares.VOAlvaraItens;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.VistoriaFacade;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.hibernate.LazyInitializationException;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoVistoria", pattern = "/vistoria-de-alvara/novo/",
        viewId = "/faces/tributario/alvara/vistoria/edita.xhtml"),
    @URLMapping(id = "editarVistoria", pattern = "/vistoria-de-alvara/editar/#{vistoriaControlador.id}/",
        viewId = "/faces/tributario/alvara/vistoria/edita.xhtml"),
    @URLMapping(id = "finalizarVistoria", pattern = "/vistoria-de-alvara/finalizar/#{vistoriaControlador.id}/",
        viewId = "/faces/tributario/alvara/vistoria/edita.xhtml"),
    @URLMapping(id = "listarVistoria", pattern = "/vistoria-de-alvara/listar/",
        viewId = "/faces/tributario/alvara/vistoria/lista.xhtml"),
    @URLMapping(id = "verVistoria", pattern = "/vistoria-de-alvara/ver/#{vistoriaControlador.id}/",
        viewId = "/faces/tributario/alvara/vistoria/visualizar.xhtml")})
public class VistoriaControlador extends PrettyControlador<Vistoria> implements Serializable, CRUD {

    @EJB
    private VistoriaFacade vistoriaFacade;
    private CadastroEconomico cadastroEconomico;
    private transient Converter converterCNAE;
    private ConverterAutoComplete converterCadastroEconomico;
    private IrregularidadeDaVistoria irregularidadeDaVistoria;
    private List<Alvara> alvarasCmc;
    private Boolean finalizar;
    private Arquivo arquivoSelecionado;
    private UploadedFile file;
    private ParecerVistoria parecerVistoria;

    public VistoriaControlador() {
        super(Vistoria.class);
    }

    public List<Alvara> getAlvarasCmc() {
        return alvarasCmc;
    }

    public void setAlvarasCmc(List<Alvara> alvarasCmc) {
        this.alvarasCmc = alvarasCmc;
    }

    public IrregularidadeDaVistoria getIrregularidadeDaVistoria() {
        return irregularidadeDaVistoria;
    }

    public void setIrregularidadeDaVistoria(IrregularidadeDaVistoria irregularidadeDaVistoria) {
        this.irregularidadeDaVistoria = irregularidadeDaVistoria;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public Boolean getFinalizar() {
        return finalizar;
    }

    public void setFinalizar(Boolean finalizar) {
        this.finalizar = finalizar;
    }

    public ParecerVistoria getParecerVistoria() {
        return parecerVistoria;
    }

    public void setParecerVistoria(ParecerVistoria parecerVistoria) {
        this.parecerVistoria = parecerVistoria;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/vistoria-de-alvara/";
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @URLAction(mappingId = "novoVistoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setData(new Date());
        selecionado.setStatusVistoria(StatusVistoria.ABERTA);
        irregularidadeDaVistoria = new IrregularidadeDaVistoria();
        cadastroEconomico = null;
        finalizar = false;
    }

    @Override
    @URLAction(mappingId = "editarVistoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        irregularidadeDaVistoria = new IrregularidadeDaVistoria();
        recuperarCadastroEconomico(selecionado.getAlvara().getCadastroEconomico());
        finalizar = false;
    }

    @Override
    @URLAction(mappingId = "verVistoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "finalizarVistoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void finalizarVistoria() {
        ver();
        finalizar = true;
        parecerVistoria = new ParecerVistoria();
        parecerVistoria.setVistoria(selecionado);
        parecerVistoria.setData(new Date());
        parecerVistoria.setUsuarioSistema(getSistemaControlador().getUsuarioCorrente());
    }


    public void atualizaForm() {
        listarAlvara();
        FacesUtil.atualizarComponente("Formulario");
        recuperarCadastroEconomico(getCadastroEconomico());
    }

    public List<SituacaoCadastralCadastroEconomico> getSituacoesDisponiveis() {
        List<SituacaoCadastralCadastroEconomico> situacoes = new ArrayList<>();
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
        return situacoes;
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        this.setCadastroEconomico((CadastroEconomico) obj);
        listarAlvara();
    }

    public ComponentePesquisaGenerico getComponentePesquisa() {
        PesquisaCadastroEconomicoControlador componente = (PesquisaCadastroEconomicoControlador) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
        List<SituacaoCadastralCadastroEconomico> situacao = Lists.newArrayList();
        situacao.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        situacao.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
        componente.setSituacao(situacao);
        return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return vistoriaFacade.getCadastroEconomicoFacade().listaCadastroEconomicoPorPessoa(parte.trim());
    }

    public Converter getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, vistoriaFacade.getCadastroEconomicoFacade());
        }
        return converterCadastroEconomico;
    }

    public List<SelectItem> getTipoAlvara() {
        return Util.getListSelectItem(Arrays.asList(TipoAlvara.values()));
    }

    public List<SelectItem> getListSelectItemStatusParecerVistoria() {
        return Util.getListSelectItem(Arrays.asList(StatusParecerVistoria.values()));
    }

    @Override
    public void salvar() {
        if (!validaCampos()) {
            return;
        }

        if (!finalizar) {
            salvarSemFinalizar();
        } else {
            try {
                selecionado.getPareceres().add(parecerVistoria);
                if (selecionado.getSequencia() == null) {
                    selecionado.setSequencia(vistoriaFacade.getSequencia());
                }
            } catch (LazyInitializationException ex) {
                selecionado.setPareceres(vistoriaFacade.recuperarPareceres(selecionado));
                selecionado.getPareceres().add(parecerVistoria);
            }

            atribuirStatusDaVistoria();
            salvarFinalizando();
        }

        redireciona();
    }

    private void atribuirStatusDaVistoria() {
        if (parecerVistoria.getStatus().equals(StatusParecerVistoria.FAVORAVEL)) {
            selecionado.setStatusVistoria(StatusVistoria.FINALIZADA);
        } else {
            selecionado.setStatusVistoria(StatusVistoria.TRAMITANDO);
        }
    }

    private void salvarFinalizando() {
        VOAlvara voAlvara = vistoriaFacade.getCalculoAlvaraFacade().preencherVOAlvaraPorIdAlvara(selecionado.getAlvara().getId());

        List<ValorDivida> valoresDivida = Lists.newArrayList();

        for (VOAlvaraItens item : voAlvara.getItens()) {
            valoresDivida.add(vistoriaFacade.getCalculoAlvaraFacade().buscarValorDividaPorIdCalculo(item.getId()));
        }

        if (TipoAlvara.SANITARIO.equals(selecionado.getTipoAlvara()) &&
            StatusVistoria.FINALIZADA.equals(selecionado.getStatusVistoria()) &&
            valoresDivida.isEmpty()) {
            try {
                ProcessoCalculoAlvaraSan processo = vistoriaFacade.getCalculoAlvaraFacade().recuperaProcessoCalculoSan(voAlvara.getId());
                if (processo != null) {
                    vistoriaFacade.getCalculoAlvaraFacade().getGeraValorDividaAlvaraSanitario().geraDebito(processo);
                    vistoriaFacade.getCalculoAlvaraFacade().getGeraValorDividaAlvaraSanitario().getDamFacade().geraDAM(processo.getCalculos().get(0));
                } else {
                    FacesUtil.addError("Erro", "Não é possível finalizar essa vistória, os dados estão inconsistentes!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        vistoriaFacade.salvar(selecionado);
        FacesUtil.addInfo("Finalizado com Sucesso!", "");
    }

    private void salvarSemFinalizar() {
        if (selecionado.getId() != null) {
            vistoriaFacade.salvar(selecionado);
        } else {
            selecionado.setSequencia(vistoriaFacade.getSequencia());
            if (vistoriaFacade.verificaSequenciaExistente(selecionado)) {
                long sequenciaAnterior = selecionado.getSequencia();
                selecionado.setSequencia(vistoriaFacade.getSequencia());
                FacesUtil.addWarn("Atenção!", "A sequência " + sequenciaAnterior + "foi já foi incluída. Portanto a sequência para esta Vistoria foi alterada para " + selecionado.getSequencia());
            }
            vistoriaFacade.salvarNovo(selecionado);
        }

        FacesUtil.addInfo("Salvo com Sucesso!", "");
    }

    public Boolean validaCampos() {
        boolean toReturn = Util.validaCampos(selecionado);

        if (selecionado.getListaIrregularidade().isEmpty()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Adicione ao menos uma irregularidade.");
            toReturn = false;
        }

        if (selecionado.getCnaes().isEmpty()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Adicione ao menos um CNAE.");
            toReturn = false;
        }

        if (finalizar && !Util.validaCampos(parecerVistoria)) {
            toReturn = false;
        }
        return toReturn;
    }

    public void addLista() {
        if (validaCamposLista()) {
            irregularidadeDaVistoria.setVistoria(selecionado);
            selecionado.getListaIrregularidade().add(irregularidadeDaVistoria);
            irregularidadeDaVistoria = new IrregularidadeDaVistoria();
        }
    }

    public void listarAlvara() {
        selecionado.setCnaes(new ArrayList<VistoriaCnae>());
        alvarasCmc = vistoriaFacade.getAlvarasPorCMC(cadastroEconomico);
    }

    private void validarAlvaraSelecionado(Alvara alvara) {
        ValidacaoException ve = new ValidacaoException();

        VOAlvara voAlvara = vistoriaFacade.getCalculoAlvaraFacade().preencherVOAlvaraPorIdAlvara(alvara.getId());

        if (voAlvara == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi possível localizar o calculo do alvará selecionado, efetue o calculo novamente!");
        } else if (vistoriaFacade.temVistoriaNaoFinalizada(voAlvara.getId(), false)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Somente um processo de vistoria pode ser realizado para cada alvará!");
        }
        ve.lancarException();
    }

    public void selecionarAlvara(Alvara a) {
        try {
            validarAlvaraSelecionado(a);

            alvarasCmc.clear();
            alvarasCmc.add(a);

            selecionado.setAlvara(a);
            selecionado.setCnaes(new ArrayList<VistoriaCnae>());
            for (CNAEAlvara ca : vistoriaFacade.getCnaePorAlvara(a)) {
                if (ca.getCnae() != null) {
                    VistoriaCnae vc = new VistoriaCnae();
                    vc.setVistoria(selecionado);
                    vc.setCnae(ca.getCnae());
                    if (ca.getCnae().getGrauDeRisco() != null && ca.getCnae().getGrauDeRisco().equals(GrauDeRiscoDTO.ALTO)) {
                        vc.setEmbargado(true);
                    } else {
                        vc.setEmbargado(false);
                    }
                    selecionado.getCnaes().add(vc);
                }
            }
            selecionado.setTipoAlvara(a.getTipoAlvara());

            List<ConfiguracaoIrregularidadesDoAlvara> irregularidadesDoAlvaras = vistoriaFacade.irregularidadesDaConfiguracao(a.getTipoAlvara());
            if (irregularidadesDoAlvaras != null) {
                if (!irregularidadesDoAlvaras.isEmpty()) {
                    for (ConfiguracaoIrregularidadesDoAlvara c : irregularidadesDoAlvaras) {
                        IrregularidadeDaVistoria irregularidadeDaVistoria = new IrregularidadeDaVistoria();
                        irregularidadeDaVistoria.setVistoria(selecionado);
                        irregularidadeDaVistoria.setIrregularidade(c.getIrregularidade());
                        irregularidadeDaVistoria.setObservacao("");
                        selecionado.getListaIrregularidade().add(irregularidadeDaVistoria);
                    }
                }
            }

            FacesUtil.atualizarComponente("Formulario");
            recuperarCadastroEconomico(getCadastroEconomico());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private boolean validaCamposLista() {
        boolean toReturn = true;
        if (irregularidadeDaVistoria.getIrregularidade() == null || irregularidadeDaVistoria.getIrregularidade().getId() == null) {
            FacesUtil.addError("Atenção!", "Selecione o Tipo de Irregularidade.");
            toReturn = false;
        }
        return toReturn;
    }

    public void removeIrregularidade(IrregularidadeDaVistoria irregularidade) {
        selecionado.getListaIrregularidade().remove(irregularidade);
        irregularidadeDaVistoria = new IrregularidadeDaVistoria();
    }

    @Override
    public AbstractFacade getFacede() {
        return vistoriaFacade;
    }

    public boolean getPessoaFisica() {
        if (cadastroEconomico != null && cadastroEconomico.getPessoa() != null) {
            return cadastroEconomico.getPessoa() instanceof PessoaFisica;
        }
        return false;
    }

    public boolean getPessoaJuridica() {
        if (cadastroEconomico != null && cadastroEconomico.getPessoa() != null) {
            return cadastroEconomico.getPessoa() instanceof PessoaJuridica;
        }
        return false;
    }

    public void handleFileUpload(FileUploadEvent event) {
        file = event.getFile();
        arquivoSelecionado = new Arquivo();
        arquivoSelecionado.setDescricao(file.getFileName());
        adicionarArquivo();
    }

    public void adicionarArquivo() {
        ArquivoVistoria arquivoVistoria = new ArquivoVistoria();
        arquivoSelecionado.setMimeType(file.getContentType());
        arquivoSelecionado.setNome(file.getFileName());
        arquivoVistoria.setArquivo(arquivoSelecionado);
        arquivoVistoria.setVistoria(selecionado);
        arquivoVistoria.setFile(file);
        selecionado.getArquivos().add(arquivoVistoria);
    }

    public StreamedContent getArquivoStream(ArquivoVistoria arquivoVistoria) throws IOException {
        UploadedFile download = (UploadedFile) arquivoVistoria.getFile();
        return new DefaultStreamedContent(download.getInputstream(), download.getContentType(), download.getFileName());
    }

    public void removeArquivo(ArquivoVistoria arquivoVistoria) {
        selecionado.getArquivos().remove(arquivoVistoria);
    }

    public void recuperarCadastroEconomico(CadastroEconomico cadastroEconomico) {
        if (cadastroEconomico != null) {
            setCadastroEconomico(vistoriaFacade.getCadastroEconomicoFacade().recuperar(cadastroEconomico.getId()));
        }
    }
}
