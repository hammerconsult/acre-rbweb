package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.portal.RHStatusSolicitacaoAfastamentoPortal;
import br.com.webpublico.entidades.rh.portal.SolicitacaoAfastamentoPortal;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.portal.SolicitacaoAfastamentoPortalFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.*;

@ManagedBean(name = "solicitacaoAfastamentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarSolicitacaoAfastamento", pattern = "/solicitacao-afastamento/listar/", viewId = "/faces/rh/administracaodepagamento/solicitacao-afastamento/lista.xhtml"),
    @URLMapping(id = "verSolicitacaoAfastamento", pattern = "/solicitacao-afastamento/ver/#{solicitacaoAfastamentoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/solicitacao-afastamento/visualizar.xhtml"),
    @URLMapping(id = "aprovarSolicitacaoAfastamento", pattern = "/solicitacao-afastamento/aprovar/#{solicitacaoAfastamentoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/solicitacao-afastamento/aprovar.xhtml"),
    @URLMapping(id = "reprovarSolicitacaoAfastamento", pattern = "/solicitacao-afastamento/reprovar/#{solicitacaoAfastamentoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/solicitacao-afastamento/reprovar.xhtml")
})
public class SolicitacaoAfastamentoControlador extends PrettyControlador<SolicitacaoAfastamentoPortal> implements Serializable, CRUD {

    @EJB
    private SolicitacaoAfastamentoPortalFacade facade;

    public SolicitacaoAfastamentoControlador() {
        super(SolicitacaoAfastamentoPortal.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-afastamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "verSolicitacaoAfastamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "aprovarSolicitacaoAfastamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void telaAprovar() {
        super.ver();
        if (!selecionado.getStatus().isEmAnalise()) {
            FacesUtil.addAtencao("Não é possível aprovar esta solicitação de afastamento, já está " + selecionado.getStatus().getDescricao());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
        Afastamento afastamento = new Afastamento();
        afastamento.setContratoFP(selecionado.getContratoFP());
        afastamento.setTipoAfastamento(selecionado.getTipoAfastamento());
        afastamento.setDataCadastro(UtilRH.getDataOperacao());
        afastamento.setInicio(selecionado.getDataInicio());
        afastamento.setTermino(selecionado.getDataFim());
        afastamento.setQuantidadeDias(Long.valueOf(Util.diferencaDeDiasEntreData(selecionado.getDataInicio(), selecionado.getDataFim())).intValue() + 1);

        DetentorArquivoComposicao detentorArquivoComposicao = clonarAnexosParaAfastamento();
        afastamento.setDetentorArquivoComposicao(detentorArquivoComposicao);

        selecionado.setAfastamento(afastamento);
        preencherCarencia();
    }

    @URLAction(mappingId = "reprovarSolicitacaoAfastamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void telaReprovar() {
        super.ver();
        if (!selecionado.getStatus().isEmAnalise()) {
            FacesUtil.addAtencao("Não é possível reprovar esta solicitação de afastamento, já está " + selecionado.getStatus().getDescricao());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
    }

    public void redirecionarTelaAprovar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "aprovar/" + selecionado.getId() + "/");
    }

    public void redirecionarTelaReprovar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "reprovar/" + selecionado.getId() + "/");
    }

    public void redirecionarParaVisualizarAfastamento() {
        FacesUtil.redirecionamentoInterno("/afastamento/ver/" + selecionado.getAfastamento().getId() + "/");
    }

    private DetentorArquivoComposicao clonarAnexosParaAfastamento() {
        if (selecionado.getDetentorArquivoComposicao() != null
            && selecionado.getDetentorArquivoComposicao().getArquivosComposicao() != null) {

            DetentorArquivoComposicao newDetentor = new DetentorArquivoComposicao();
            for (ArquivoComposicao arquivoComposicao : selecionado.getDetentorArquivoComposicao().getArquivosComposicao()) {
                Arquivo cloneArquivo = clonarArquivo(arquivoComposicao.getArquivo());
                ArquivoComposicao newArquivoComposicao = criarArquivoComposicao(newDetentor);
                newArquivoComposicao.setArquivo(cloneArquivo);
                newDetentor.getArquivosComposicao().add(newArquivoComposicao);
            }
            return newDetentor;
        }
        return null;
    }

    private ArquivoComposicao criarArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
        arquivoComposicao.setDataUpload(UtilRH.getDataOperacao());
        arquivoComposicao.setDetentorArquivoComposicao(detentorArquivoComposicao);
        return arquivoComposicao;
    }

    private Arquivo clonarArquivo(Arquivo arquivo) {
        Arquivo newArquivo = new Arquivo();
        newArquivo.setDescricao(arquivo.getDescricao());
        newArquivo.setNome(arquivo.getNome());
        newArquivo.setTamanho(arquivo.getTamanho());
        newArquivo.setInputStream(arquivo.getInputStream());
        newArquivo.setMimeType(arquivo.getMimeType());

        for (ArquivoParte parte : arquivo.getPartes()) {
            ArquivoParte newParte = new ArquivoParte();
            newParte.setArquivo(newArquivo);
            newParte.setDados(parte.getDados());
            newArquivo.getPartes().add(newParte);
        }
        return newArquivo;
    }

    public void aprovar() {
        try {
            Util.validarCampos(selecionado);
            Util.validarCampos(selecionado.getAfastamento());
            validarDatas();
            facade.getAfastamentoFacade().validarCampos(selecionado.getAfastamento());
            selecionado.setStatus(RHStatusSolicitacaoAfastamentoPortal.APROVADO);
            facade.salvarSolicitacaoEAfastamento(selecionado);
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void reprovar() {
        selecionado.setStatus(RHStatusSolicitacaoAfastamentoPortal.REPROVADO);
        salvar();
        redireciona();
    }

    public StreamedContent fazerDownloadAnexo(Arquivo arquivo) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte a : arquivo.getPartes()) {
            try {
                buffer.write(a.getDados());
            } catch (IOException ex) {
                logger.error("Erro: ", ex);
            }
        }
        InputStream is = new ByteArrayInputStream(buffer.toByteArray());
        return new DefaultStreamedContent(is, arquivo.getMimeType(), arquivo.getNome());
    }

    public void preencherCarencia() {
        TipoAfastamento t = selecionado.getAfastamento().getTipoAfastamento();
        selecionado.getAfastamento().setDiasMaximoPermitido(t.getDiasMaximoPermitido());
        if (t.getCarencia() != null) {
            selecionado.getAfastamento().setCarencia(t.getCarencia().intValue());
        } else {
            selecionado.getAfastamento().setCarencia(null);
        }
    }

    public void validarDatas() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getAfastamento().getInicio().after(selecionado.getAfastamento().getTermino())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Término deve ser posterior à Data de Início.");
        }
        if (quantidadeDiasEntreDatas().compareTo(selecionado.getAfastamento().getDiasMaximoPermitido() != null ? selecionado.getAfastamento().getDiasMaximoPermitido() : 0) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O intervalo entre a data de início e término ultrapassa o número máximo de " + selecionado.getAfastamento().getDiasMaximoPermitido() + " dias permitidos.");
        }
        ve.lancarException();
    }

    private Integer quantidadeDiasEntreDatas() {
        DateTime inicio = new DateTime(selecionado.getAfastamento().getInicio());
        DateTime fim = new DateTime(selecionado.getAfastamento().getTermino());
        return Days.daysBetween(inicio, fim).getDays();
    }
}
