package br.com.webpublico.controle.rh.administracaodepagamento.analiseconformidadefp;


import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.administracaodepagamento.AnaliseConformidadeFP;
import br.com.webpublico.entidades.rh.administracaodepagamento.VinculoAnaliseConformidadeFP;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.administracaodepagamento.AnaliseConformidadeFPFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.beust.jcommander.internal.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ManagedBean(name = "analiseConformidadeFPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoAnaliseConformidadeFP", pattern = "/analise-conformidade-fp/novo/", viewId = "/faces/rh/administracaodepagamento/analiseconformidadefp/edita.xhtml"),
    @URLMapping(id = "verAnaliseConformidadeFP", pattern = "/analise-conformidade-fp/ver/#{analiseConformidadeFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/analiseconformidadefp/visualizar.xhtml"),
    @URLMapping(id = "listaAnaliseConformidadeFP", pattern = "/analise-conformidade-fp/listar/", viewId = "/faces/rh/administracaodepagamento/analiseconformidadefp/lista.xhtml")
})
public class AnaliseConformidadeFPControlador extends PrettyControlador<AnaliseConformidadeFP> implements Serializable, CRUD {
    private static final Logger logger = LoggerFactory.getLogger(AnaliseConformidadeFPControlador.class);
    @EJB
    private AnaliseConformidadeFPFacade analiseConformidadeFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    private VinculoAnaliseConformidadeFP vinculoAnaliseConformidadeFP;

    public AnaliseConformidadeFPControlador() {
        super(AnaliseConformidadeFP.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return analiseConformidadeFPFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/analise-conformidade-fp/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @Override
    @URLAction(mappingId = "novoAnaliseConformidadeFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setPercentualAmostra(new BigDecimal(10));
    }

    @Override
    @URLAction(mappingId = "verAnaliseConformidadeFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        analiseConformidadeFPFacade.recuperar(selecionado.getId());
        ordenar();
    }

    public void buscarVinculos() {
        try {
            validarCamposBusca();
            selecionado.setVinculosAnalise(analiseConformidadeFPFacade.buscarVinculos(selecionado));
            ordenar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao buscar os vínculos para a Analise de Conformidade FP. ", ex);
            descobrirETratarException(ex);
        }
    }

    private void validarCamposBusca() {
        ValidacaoException ve = new ValidacaoException();
        validarMesAno(ve);
        if (selecionado.getMes() != null && selecionado.getAno() != null && analiseConformidadeFPFacade.buscarAnaliseConformidadeFPPorMesAndAno(selecionado.getMes(), selecionado.getAno()) != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um sorteio realizado para a competência selecionada.");
        }
        ve.lancarException();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        validarMesAno(ve);
        if (operacao.equals(Operacoes.NOVO)) {
            if (selecionado.getVinculosAnalise().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não contem vínculos na Analise de Conformidade FP.");
            }
        }
        ve.lancarException();
    }

    private void validarMesAno(ValidacaoException ve) {
        if (selecionado.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês é deve ser informado.");
        }
        if (!isMesValido()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser entre 1 e 12.");
        }
        if (selecionado.getAno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano é deve ser informado.");
        }
    }

    private boolean isMesValido() {
        return selecionado.getMes() != null && selecionado.getMes().compareTo(1) >= 0 && selecionado.getMes().compareTo(12) <= 0;
    }


    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.setApi("rh/analise-conformidade-fp/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar Relatório. ", e);
        }
    }

    public RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
        dto.setNomeRelatorio("ANÁLISE DE CONFORMIDADE FP");
        dto.adicionarParametro("NOMERELATORIO", "Relatório Análise de Conformidade FP");
        dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        if (operacao.equals(Operacoes.VER)) {
            dto.adicionarParametro("BUSCA-ANALISE", true);
            dto.adicionarParametro("ID-ANALISE", selecionado.getId());
        } else {
            dto.adicionarParametro("VINCULOS", buscarIdsVinculos());
        }
        dto.adicionarParametro("MES", selecionado.getMes() - 1);
        dto.adicionarParametro("ANO", selecionado.getAno());
        return dto;
    }

    private List<Long> buscarIdsVinculos() {
        List<Long> retorno = Lists.newArrayList();
        for (VinculoAnaliseConformidadeFP vinculoAnalise : selecionado.getVinculosAnalise()) {
            retorno.add(vinculoAnalise.getVinculoFP().getId());
        }
        return retorno;
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            analiseConformidadeFPFacade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao salvar a Analise de Conformidade FP. ", ex);
            descobrirETratarException(ex);
        }
    }

    public void ordenar() {
        Collections.sort(selecionado.getVinculosAnalise(), new Comparator<VinculoAnaliseConformidadeFP>() {
            @Override
            public int compare(VinculoAnaliseConformidadeFP o1, VinculoAnaliseConformidadeFP o2) {
                return o1.getTipoVinculo().compareTo(o2.getTipoVinculo());
            }
        });
    }

    public void assinarAuditoria(VinculoAnaliseConformidadeFP vinc) {
        this.vinculoAnaliseConformidadeFP = vinc;
        if (Util.isNull(getVinculoAnaliseConformidadeFP().getresponsavelAuditoria())) {
            this.vinculoAnaliseConformidadeFP.setresponsavelAuditoria(sistemaFacade.getUsuarioCorrente());
            FacesUtil.executaJavaScript("dialogAssinarRevisado.show()");
        }
    }

    public void confirmarComoRevisado() {
        if (Util.isNotNull(vinculoAnaliseConformidadeFP)) {
            analiseConformidadeFPFacade.atualizarVinculoAnaliseConformidadeFP(vinculoAnaliseConformidadeFP);
        }
    }

    public void atribuirVinculoRevisadoNull() {
        this.vinculoAnaliseConformidadeFP.setresponsavelAuditoria(null);
    }

    public VinculoAnaliseConformidadeFP getVinculoAnaliseConformidadeFP() {
        return vinculoAnaliseConformidadeFP;
    }

    public void setVinculoAnaliseConformidadeFP(VinculoAnaliseConformidadeFP vinculoAnaliseConformidadeFP) {
        this.vinculoAnaliseConformidadeFP = vinculoAnaliseConformidadeFP;
    }
}
