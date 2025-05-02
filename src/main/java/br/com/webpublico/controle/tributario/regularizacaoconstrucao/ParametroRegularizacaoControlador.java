package br.com.webpublico.controle.tributario.regularizacaoconstrucao;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.TipoDoctoOficial;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ParametroRegularizacao;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ServicoConstrucao;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.VinculoServicoTributo;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.ParametroRegularizacaoFacade;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.ServicoConstrucaoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "parametroRegularizacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParametroRegularizacao", pattern = "/regularizacao-construcao/parametro-regularizacao/novo/", viewId = "/faces/tributario/regularizacaoconstrucao/parametroregularizacao/edita.xhtml"),
    @URLMapping(id = "editarParametroRegularizacao", pattern = "/regularizacao-construcao/parametro-regularizacao/editar/#{parametroRegularizacaoControlador.id}/", viewId = "/faces/tributario/regularizacaoconstrucao/parametroregularizacao/edita.xhtml"),
    @URLMapping(id = "listarParametroRegularizacao", pattern = "/regularizacao-construcao/parametro-regularizacao/listar/", viewId = "/faces/tributario/regularizacaoconstrucao/parametroregularizacao/lista.xhtml"),
    @URLMapping(id = "verParametroRegularizacao", pattern = "/regularizacao-construcao/parametro-regularizacao/ver/#{parametroRegularizacaoControlador.id}/", viewId = "/faces/tributario/regularizacaoconstrucao/parametroregularizacao/visualizar.xhtml")
})

public class ParametroRegularizacaoControlador extends PrettyControlador<ParametroRegularizacao> implements Serializable, CRUD {

    @EJB
    private ParametroRegularizacaoFacade parametroRegularizacaoFacade;
    @EJB
    private ServicoConstrucaoFacade servicoConstrucaoFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    private VinculoServicoTributo vinculoAtual;
    private ParametroRegularizacao parametro;
    private String assinaturaDe;
    private Arquivo assinatura;

    @Override
    public AbstractFacade getFacede() {
        return parametroRegularizacaoFacade;
    }

    public ParametroRegularizacaoControlador() {
        super(ParametroRegularizacao.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/regularizacao-construcao/parametro-regularizacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Arquivo getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(Arquivo assinatura) {
        this.assinatura = assinatura;
    }

    @URLAction(mappingId = "novoParametroRegularizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(parametroRegularizacaoFacade.getSistemaFacade().getExercicioCorrente());
    }

    @URLAction(mappingId = "verParametroRegularizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarParametroRegularizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        parametro = (ParametroRegularizacao) Util.clonarObjeto(selecionado);
    }

    public void abrirDialogAdicionarVinculo() {
        vinculoAtual = new VinculoServicoTributo();
        vinculoAtual.setParametroRegularizacao(selecionado);
        FacesUtil.executaJavaScript("adicionarVinculoServico.show()");
    }

    public void adicionarVinculoAtual() {
        try {
            validarAdicionarVinculoAtual();
            Util.adicionarObjetoEmLista(selecionado.getVinculosServicosTributos(), vinculoAtual);
            vinculoAtual = null;
            FacesUtil.executaJavaScript("adicionarVinculoServico.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarAdicionarVinculoAtual() {
        ValidacaoException ve = new ValidacaoException();
        if (vinculoAtual.getServicoConstrucao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo serviço deve ser informado.");
        }
        if (vinculoAtual.getTributo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tributo deve ser informado.");
        } else if ((selecionado.getTributoHabitese() != null && vinculoAtual.getTributo().equals(selecionado.getTributoHabitese()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O tributo selecionado já foi adicionado ao Tributo do Habite-se.");
            vinculoAtual.setTributo(null);
        } else if (selecionado.getTributoTaxaVistoria() != null && vinculoAtual.getTributo().equals(selecionado.getTributoTaxaVistoria())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O tributo selecionado já foi adicionado ao Tributo da Taxa de Vistoria.");
            vinculoAtual.setTributo(null);
        }
        if (selecionado != null) {
            if (selecionado.getVinculosServicosTributos() != null && vinculoAtual.getTributo() != null) {
                for (VinculoServicoTributo vinculo : selecionado.getVinculosServicosTributos()) {
                    if (vinculo.getTributo().equals(vinculoAtual.getTributo())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O tributo selecionado já está vinculado a outro serviço da lista.");
                        vinculoAtual.setTributo(null);
                        break;
                    }
                }
            }
            if (selecionado.getServicoConstrucao() != null) {
                if (selecionado.getServicoConstrucao().equals(vinculoAtual.getServicoConstrucao())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O serviço selecionado já foi adicionado ao Serviço de Construção da Taxa de Vistoria.");
                    vinculoAtual.setServicoConstrucao(null);
                }
            }
        }
        ve.lancarException();
    }

    public List<TipoDoctoOficial> completarTipoDocumento(String parte) {
        return parametroRegularizacaoFacade.getTipoDoctoOficialFacade().tipoDoctoPorModulo(parte.trim(), ModuloTipoDoctoOficial.ALVARA_CONSTRUCAO);
    }

    public List<TipoDoctoOficial> completarTipoDocumentoAlvaraImediato(String parte) {
        return parametroRegularizacaoFacade.getTipoDoctoOficialFacade().tipoDoctoPorModulo(parte.trim(), ModuloTipoDoctoOficial.ALVARA_IMEDIATO);
    }

    public List<TipoDoctoOficial> completarTipoDocumentoHabitese(String parte) {
        return parametroRegularizacaoFacade.getTipoDoctoOficialFacade().tipoDoctoPorModulo(parte.trim(), ModuloTipoDoctoOficial.HABITESE_CONSTRUCAO);
    }

    public List<ServicoConstrucao> completarServicos(String parte) {
        List<ServicoConstrucao> resultado = servicoConstrucaoFacade.listarFiltrando(parte);
        List<ServicoConstrucao> filtrado = Lists.newArrayList();
        for (ServicoConstrucao servicoConstrucao : resultado) {
            boolean podeAdd = true;
            for (VinculoServicoTributo vinculosServicosTributo : selecionado.getVinculosServicosTributos()) {
                if (servicoConstrucao.equals(vinculosServicosTributo.getServicoConstrucao())) {
                    podeAdd = false;
                    break;
                }
            }
            if (podeAdd) {
                filtrado.add(servicoConstrucao);
            }
        }
        return filtrado;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Exercício!");
        } else if (parametroRegularizacaoFacade.buscarParametroRegularizacaoPorExercicio(selecionado.getExercicio()) != null && !parametroRegularizacaoFacade.buscarParametroRegularizacaoPorExercicio(selecionado.getExercicio()).equals(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um parâmetro cadastrado para o exercício " + selecionado.getExercicio() + "!");
        }
        if (selecionado.getSecretaria() == null || "".equals(selecionado.getSecretaria().trim())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Secretaria!");
        }
        if (selecionado.getSecretario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o nome do Secretário!");
        }
        if (selecionado.getDiretor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Diretor do Departamento!");
        }
        if (selecionado.getDiaDoMesWebService() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Dia do Mês Para Arquivo de Saída WebSevice");
        } else if (selecionado.getDiaDoMesWebService() <= 0 || selecionado.getDiaDoMesWebService() >= 32) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O dia do mês não pode ser menor que 1, e nem maior que 31");
        }
        if (selecionado.getPrazoCartaz() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Prazo em dias para o vencimento do cartaz de alvará de construção!");
        } else if (selecionado.getPrazoCartaz() < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Prazo em dias para o vencimento do cartaz de alvará de construção não pode ser menor que zero!");
        }
        if (selecionado.getPrazoTaxaAlvara() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Prazo em dias para o vencimento da taxa de alvará de construção!");
        } else if (selecionado.getPrazoTaxaAlvara() < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Prazo em dias para o vencimento da taxa de alvará de construção não pode ser menor que zero!");
        }
        if (selecionado.getPrazoTaxaVistoria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Prazo em dias para o vencimento da taxa de vistoria de conclusão de obra!");
        } else if (selecionado.getPrazoTaxaVistoria() < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Prazo em dias para o vencimento da taxa de vistoria de conclusão de obra não pode ser menor que zero!");
        }
        if (selecionado.getPrazoComunicacaoContribuinte() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Prazo em dias para comunicação automática do contribuinte de vencimento do alvará de construção!");
        } else if (selecionado.getPrazoComunicacaoContribuinte() < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Prazo em dias para comunicação automática do contribuinte de vencimento do alvará de construção não pode ser menor que zero!");
        }
        ve.lancarException();
    }

    public VinculoServicoTributo getVinculoAtual() {
        return vinculoAtual;
    }

    public void setVinculoAtual(VinculoServicoTributo vinculoAtual) {
        this.vinculoAtual = vinculoAtual;
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        }
    }

    public void validarServicoVistoria(SelectEvent event) {
        ServicoConstrucao servico = (ServicoConstrucao) event.getObject();
        if(servico != null && selecionado != null && selecionado.getVinculosServicosTributos() != null) {
            for (VinculoServicoTributo vinculo : selecionado.getVinculosServicosTributos()) {
                if(vinculo.getServicoConstrucao().equals(servico)) {
                    FacesUtil.addOperacaoNaoPermitida("O serviço selecionado já foi adicionado a lista de Vínculo de Serviços com Tributo.");
                    selecionado.setServicoConstrucao(isOperacaoNovo() ? null : (parametro != null ? parametro.getServicoConstrucao() : null));
                    break;
                }
            }
        }
    }

    public void validarTributoHabitese(SelectEvent event) {
        Tributo tributo = (Tributo) event.getObject();
        if(tributo != null) {
            if(selecionado.getTributoTaxaVistoria() != null && tributo.equals(selecionado.getTributoTaxaVistoria())) {
                FacesUtil.addOperacaoNaoPermitida("O tributo selecionado já foi adicionado ao Tributo da Taxa de Vistoria.");
                selecionado.setTributoHabitese(isOperacaoNovo() ? null : (parametro != null ? parametro.getTributoHabitese() : null));
            }
            validarListaVinculoServicoTributo(tributo, true);
        }
    }

    public void validarTributoVistoria(SelectEvent event) {
        Tributo tributo = (Tributo) event.getObject();
        if(tributo != null) {
            if(selecionado.getTributoHabitese() != null && tributo.equals(selecionado.getTributoHabitese())) {
                FacesUtil.addOperacaoNaoPermitida("O tributo selecionado já foi adicionado ao Tributo do Habite-se.");
                selecionado.setTributoTaxaVistoria(isOperacaoNovo() ? null : (parametro != null ? parametro.getTributoTaxaVistoria() : null));
            }
            validarListaVinculoServicoTributo(tributo, false);
        }
    }

    private void validarListaVinculoServicoTributo(Tributo tributo, boolean habitese) {
        for (VinculoServicoTributo vinculo : selecionado.getVinculosServicosTributos()) {
            if (tributo.equals(vinculo.getTributo())) {
                FacesUtil.addOperacaoNaoPermitida("O tributo selecionado já foi adicionado a lista de Vínculo de Serviços com Tributo.");
                if (habitese) {
                    selecionado.setTributoHabitese(isOperacaoNovo() ? null : (parametro != null ? parametro.getTributoHabitese() : null));
                } else {
                    selecionado.setTributoTaxaVistoria(isOperacaoNovo() ? null : (parametro != null ? parametro.getTributoTaxaVistoria() : null));
                }
                break;
            }
        }
    }

    public void tratarAssinaturaSecretario() {
        tratarAssinatura("S");
        assinatura = selecionado.getAssinaturaSecretario();
    }

    public void tratarAssinaturaDiretor() {
        tratarAssinatura("D");
        assinatura = selecionado.getAssinaturaDiretor();
    }

    public void tratarAssinatura(String assinaturaDe) {
        this.assinaturaDe = assinaturaDe;
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            assinatura = arquivoFacade.criarArquivo(event.getFile());
        } catch (IOException e) {
            logger.error("Erro ao inserir assinatura. {}", e.getMessage());
            logger.debug("Detalhes do erro ao inserir assinatura. ", e);
        }
    }

    public void removerAssinatura() {
        assinatura = null;
    }

    public StreamedContent getStreamedContent() {
        return arquivoFacade.montarArquivoParaDownloadPorArquivo(assinatura);
    }

    public void confirmarAssinatura() {
        if (assinatura == null) {
            FacesUtil.addCampoObrigatorio("A assinatura deve ser informada.");
            return;
        }
        switch (assinaturaDe) {
            case "S": {
                selecionado.setAssinaturaSecretario(assinatura);
                break;
            }
            case "D": {
                selecionado.setAssinaturaDiretor(assinatura);
                break;
            }
        }
        FacesUtil.executaJavaScript("dlgAssinatura.hide()");
    }

}
