package br.com.webpublico.controle;

import br.com.webpublico.entidades.CertidaoDividaAtiva;
import br.com.webpublico.entidades.ProcessoJudicial;
import br.com.webpublico.entidades.ProcessoJudicialExtincao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CertidaoDividaAtivaFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ProcessoJudicialFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "processoJudicialExtincaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarExtincaoProcessoJudicial",
        pattern = "/extincao-processo-judicial/listar/",
        viewId = "/faces/tributario/dividaativa/extincaoprocessojudicial/lista.xhtml"),
    @URLMapping(id = "novaExtincaoProcessoJudicial",
        pattern = "/extincao-processo-judicial/novo/",
        viewId = "/faces/tributario/dividaativa/extincaoprocessojudicial/edita.xhtml"),
    @URLMapping(id = "editaExtincaProcessoJudicial",
        pattern = "/extincao-processo-judicial/editar/#{processoJudicialExtincaoControlador.id}/",
        viewId = "/faces/tributario/dividaativa/extincaoprocessojudicial/edita.xhtml"),
    @URLMapping(id = "verExtincaoProcessoJudicial",
        pattern = "/extincao-processo-judicial/ver/#{processoJudicialExtincaoControlador.id}/",
        viewId = "/faces/tributario/dividaativa/extincaoprocessojudicial/visualizar.xhtml")
})
public class ProcessoJudicialExtincaoControlador extends PrettyControlador<ProcessoJudicialExtincao> implements Serializable, CRUD {

    @EJB
    private ProcessoJudicialFacade processoJudicialFacade;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    private ConverterAutoComplete converterProcessoJudicial;
    private List<CertidaoDividaAtiva> certidoesDoProcesso;

    public ProcessoJudicialExtincaoControlador() {
        super(ProcessoJudicialExtincao.class);
    }

    public Converter getConverterProcessoJudicial() {
        if (converterProcessoJudicial == null) {
            converterProcessoJudicial = new ConverterAutoComplete(ProcessoJudicial.class, processoJudicialFacade);
        }
        return converterProcessoJudicial;
    }

    public List<CertidaoDividaAtiva> getCertidoesDoProcesso() {
        return certidoesDoProcesso;
    }

    public void setCertidoesDoProcesso(List<CertidaoDividaAtiva> certidoesDoProcesso) {
        this.certidoesDoProcesso = certidoesDoProcesso;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/extincao-processo-judicial/";
    }

    @Override
    public AbstractFacade getFacede() {
        return processoJudicialFacade;
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "verExtincaoProcessoJudicial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        selecionado = processoJudicialFacade.recuperarExtincao(getId());
        carregarCertidoesDoProcessoJudicial(ProcessoJudicialExtincao.Situacao.EFETIVADO.equals(selecionado.getSituacao()) ? ProcessoJudicial.Situacao.INATIVO : ProcessoJudicial.Situacao.ATIVO);
    }

    public void carregarCertidoesDoProcessoJudicial() {
        carregarCertidoesDoProcessoJudicial(ProcessoJudicial.Situacao.ATIVO);
    }

    private void carregarCertidoesDoProcessoJudicial(ProcessoJudicial.Situacao situacaoProcessoJudicial) {
        certidoesDoProcesso = Lists.newArrayList();
        List<ProcessoJudicial> processos = processoJudicialFacade.recuperarProcessosPorNumeroForum(selecionado.getProcessoJudicial().getNumeroProcessoForum(), situacaoProcessoJudicial);
        for (ProcessoJudicial processo : processos) {
            certidoesDoProcesso.addAll(certidaoDividaAtivaFacade.certidoesDoProcessoJudicialParcelamento(processo));
        }
    }

    @URLAction(mappingId = "editaExtincaProcessoJudicial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        selecionado = processoJudicialFacade.recuperarExtincao(getId());
    }

    @URLAction(mappingId = "novaExtincaoProcessoJudicial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setCancelamento(getSistemaControlador().getDataOperacao());
        selecionado.setUsuarioSistemaCancelamento(getSistemaControlador().getUsuarioCorrente());
        selecionado.setMotivoCancelamento("");
        selecionado.setSituacao(ProcessoJudicialExtincao.Situacao.EFETIVADO);
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    private boolean validarCampos() {
        boolean retorno = true;
        if (selecionado.getProcessoJudicial() == null) {
            FacesUtil.addCampoObrigatorio("Informe o Processo Judicial para Extinção!");
            retorno = false;
        }

        if (selecionado.getCancelamento() == null) {
            FacesUtil.addCampoObrigatorio("Informe a Data da Extinção!");
            retorno = false;
        }

        if (selecionado.getMotivoCancelamento() == null || "".equals(selecionado.getMotivoCancelamento().trim())) {
            FacesUtil.addCampoObrigatorio("Informe o Motivo da Extinção!");
            retorno = false;
        }

        if (selecionado.getUsuarioSistemaCancelamento() == null) {
            FacesUtil.addCampoObrigatorio("Informe o Usuário Responsável pela Extinção!");
            retorno = false;
        }

        return retorno;
    }

    @Override
    public void salvar() {
        if (validarCampos()) {
            try {
                processoJudicialFacade.inativarProcessoJudicial(selecionado);
                FacesUtil.addInfo("Processo Judicial extinto com sucesso!", "");
                super.salvar();
            } catch (ExcecaoNegocioGenerica ex) {
                FacesUtil.addError("Não é possível extinguir o Processo Judicial!", ex.getMessage());
            } catch (Exception e) {
                FacesUtil.addError("Não é possível extinguir o Processo Judicial!", e.getMessage());
                logger.error("{}", e);
            }
        }
    }

    public List<ProcessoJudicial> completaProcessoJudicial(String parte) {
        List<ProcessoJudicial> processos = processoJudicialFacade.listarFiltrandoProcessoJudicialAtivo(parte.trim());
        List<ProcessoJudicial> retorno = Lists.newArrayList();
        for (ProcessoJudicial processo : processos) {
            boolean jaExiste = false;
            for (ProcessoJudicial ret : retorno) {
                if (ret.getNumeroProcessoForum().equals(processo.getNumeroProcessoForum())) {
                    jaExiste = true;
                }
            }
            if (!jaExiste) {
                retorno.add(processo);
            }
        }
        return retorno;
    }

    public void estornarExtincaoProcessoJudicial() {
        try {
            processoJudicialFacade.estornarExtincaoProcessoJudicial(selecionado);
            selecionado.setSituacao(ProcessoJudicialExtincao.Situacao.CANCELADO);
            FacesUtil.addInfo("Extinção do Processo Judicial estornado com sucesso!", "");
            super.salvar();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addError("Não é possível estornar a extinção do Processo Judicial!", ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addError("Não é possível estornar a extinção do Processo Judicial!", e.getMessage());
            logger.error("{}", e);
        }
    }

}
