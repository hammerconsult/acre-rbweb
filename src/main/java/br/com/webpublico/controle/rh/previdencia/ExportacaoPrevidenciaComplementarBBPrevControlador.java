package br.com.webpublico.controle.rh.previdencia;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.previdencia.ExportacaoPrevidenciaComplementarBBPrev;
import br.com.webpublico.enums.CategoriaDeclaracaoPrestacaoContas;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.rh.previdencia.TipoEnvioBBPrev;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.previdencia.ExportacaoPrevidenciaComplementarBBPrevFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@ManagedBean(name = "exportacaoPrevidenciaComplementarBBPrevControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoExportacaoPrevidenciaComplementarBBPrev", pattern = "/exportacao-previdencia-complementar-bbprev/novo/", viewId = "/faces/rh/previdencia/exportacaoprevidenciacomplementarbbprev/edita.xhtml"),
    @URLMapping(id = "listarExportacaoPrevidenciaComplementarBBPrev", pattern = "/exportacao-previdencia-complementar-bbprev/listar/", viewId = "/faces/rh/previdencia/exportacaoprevidenciacomplementarbbprev/lista.xhtml"),
    @URLMapping(id = "verExportacaoPrevidenciaComplementarBBPrev", pattern = "/exportacao-previdencia-complementar-bbprev/ver/#{exportacaoPrevidenciaComplementarBBPrevControlador.id}/", viewId = "/faces/rh/previdencia/exportacaoprevidenciacomplementarbbprev/visualizar.xhtml")
})
public class ExportacaoPrevidenciaComplementarBBPrevControlador extends PrettyControlador<ExportacaoPrevidenciaComplementarBBPrev> implements Serializable, CRUD {

    @EJB
    private ExportacaoPrevidenciaComplementarBBPrevFacade facade;
    @EJB
    private EntidadeDPContasFacade entidadeDPContasFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ItemEntidadeDPContasFacade itemEntidadeDPContasFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private ContratoFP contratoFPSelecionado;
    private List<ContratoFP> contratos;
    private List<ContratoFP> contratosParaGeracao;
    private ConverterAutoComplete converterItemEntidadeDPContas;
    private CompletableFuture<ExportacaoPrevidenciaComplementarBBPrev> future;
    private AssistenteBarraProgresso assistenteBarraProgresso;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public ExportacaoPrevidenciaComplementarBBPrevControlador() {
        super(ExportacaoPrevidenciaComplementarBBPrev.class);
    }


    @Override
    public String getCaminhoPadrao() {
        return "/exportacao-previdencia-complementar-bbprev/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoExportacaoPrevidenciaComplementarBBPrev", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataGeracao(new Date());
        contratos = Lists.newArrayList();
        contratosParaGeracao = Lists.newArrayList();
    }

    @Override
    @URLAction(mappingId = "verExportacaoPrevidenciaComplementarBBPrev", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        recuperarArquivo();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (TipoEnvioBBPrev.PROPOSTA_ADESAO.equals(selecionado.getTipoEnvio())) {
                contratosParaGeracao = facade.buscarContratosParaProposta(selecionado.getMes(), selecionado.getAno(), montarIdOrgaoItemEntidadeDPContas(selecionado.getPatrocinador()));
            } else {
                Map<Long, ContratoFP> mapContratos = new HashMap<>();
                List<ContratoFP> contratosManutencao = facade.buscarContratosParaManutencao(selecionado.getMes(), selecionado.getAno(), montarIdOrgaoItemEntidadeDPContas(selecionado.getPatrocinador()));
                contratosManutencao.forEach(c -> {
                    mapContratos.put(c.getId(), c);
                });

                contratos.forEach(c -> {
                    mapContratos.put(c.getId(), c);
                });

                contratosParaGeracao = Lists.newArrayList(mapContratos.values());
            }
            validarContratos();
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setDescricaoProcesso("Geração de arquivo da Previdência Complementar BBPrev");
            assistenteBarraProgresso.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            assistenteBarraProgresso.setTotal(contratosParaGeracao.size());

            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> {
                    try {
                        return facade.gerarArquivo(selecionado, contratosParaGeracao, assistenteBarraProgresso);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            FacesUtil.executaJavaScript("gerarArquivoPrevidenciaComplementar()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public List<Long> montarIdOrgaoItemEntidadeDPContas(ItemEntidadeDPContas itemEntidadeDPContas) {
        List<Long> ids = Lists.newArrayList();
        itemEntidadeDPContas = itemEntidadeDPContasFacade.recuperar(itemEntidadeDPContas.getId());
        for (ItemEntidadeDPContasUnidadeOrganizacional item : itemEntidadeDPContas.getItemEntidadeUnidadeOrganizacional()) {
            ids.add(item.getHierarquiaOrganizacional().getSubordinada().getId());
        }
        return ids;
    }

    public void verificarTermino() {
        if (future != null && future.isDone()) {
            try {
                selecionado = future.get();
            } catch (Exception e) {
                logger.error("erro", e);
                FacesUtil.addOperacaoNaoRealizada(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao() + " Erro ao gerar arquivo da Previdência Complementar BBPrev.");
            }
            FacesUtil.executaJavaScript("termina()");
            future = null;
            FacesUtil.executaJavaScript("dialogo.hide()");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
    }

    public void validarCampos() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getPatrocinador() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Patrocinador deve ser informado.");
        }
        if (selecionado.getAno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        if (selecionado.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        } else if (selecionado.getMes() < 1 || selecionado.getMes() > 12) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Favor informar um mês entre 01 (Janeiro) e 12 (Dezembro).");
        }
        if (selecionado.getTipoEnvio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Envio deve ser informado.");
        }
        ve.lancarException();
    }

    public void validarContratos() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (contratosParaGeracao == null || contratosParaGeracao.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foram encontrados servidores com os filtros selecionados.");
        }
        ve.lancarException();
    }

    private void recuperarArquivo() {
        selecionado.setArquivo(arquivoFacade.recuperaDependencias(selecionado.getArquivo().getId()));
    }

    public DefaultStreamedContent getFileDownload() {
        selecionado.getArquivo().montarImputStream();
        return new DefaultStreamedContent(selecionado.getArquivo().getInputStream(), "text/plain", selecionado.getArquivo().getNome());
    }

    public void adicionarContrato() {
        contratos.add(contratoFPSelecionado);
        contratoFPSelecionado = null;
    }

    public void removerContrato(ContratoFP contrato) {
        contratos.remove(contrato);
    }

    public List<ContratoFP> completarContratoFP(String parte) {
        if (selecionado.getMes() != null && selecionado.getAno() != null && selecionado.getPatrocinador() != null) {
            return facade.buscarContratosComPrevidenciaComplementarVigenteFiltrandoMatriculaOrNomeOrCPF(parte.trim(), selecionado.getMes(), selecionado.getAno(), montarIdOrgaoItemEntidadeDPContas(selecionado.getPatrocinador()));
        }
        return Lists.newArrayList();
    }

    public List<SelectItem> getTiposEnvioBBPrev() {
        return Util.getListSelectItem(TipoEnvioBBPrev.values());
    }

    public List<SelectItem> getEntidades() {
        return Util.getListSelectItem(entidadeDPContasFacade.recuperarItensEntidadeDPContasVigenteParaCategoria(CategoriaDeclaracaoPrestacaoContas.BBPREV, UtilRH.getDataOperacao()));
    }

    public Boolean isHabilitaContrato() {
        return TipoEnvioBBPrev.MANUTENCAO.equals(selecionado.getTipoEnvio()) && selecionado.getMes() != null && selecionado.getAno() != null;
    }

    public ContratoFP getContratoFPSelecionado() {
        return contratoFPSelecionado;
    }

    public void setContratoFPSelecionado(ContratoFP contratoFPSelecionado) {
        this.contratoFPSelecionado = contratoFPSelecionado;
    }

    public List<ContratoFP> getContratos() {
        return contratos;
    }

    public void setContratos(List<ContratoFP> contratos) {
        this.contratos = contratos;
    }

    public ConverterAutoComplete getConverterItemEntidadeDPContas() {
        if (converterItemEntidadeDPContas == null) {
            converterItemEntidadeDPContas = new ConverterAutoComplete(ItemEntidadeDPContas.class, entidadeDPContasFacade);
        }
        return converterItemEntidadeDPContas;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }
}
