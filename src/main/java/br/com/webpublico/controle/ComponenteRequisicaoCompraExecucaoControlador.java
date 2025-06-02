package br.com.webpublico.controle;

import br.com.webpublico.entidades.Empenho;
import br.com.webpublico.entidades.ExecucaoContrato;
import br.com.webpublico.entidades.ExecucaoProcesso;
import br.com.webpublico.entidadesauxiliares.FiltroResumoExecucaoVO;
import br.com.webpublico.entidadesauxiliares.RequisicaoCompraExecucaoVO;
import br.com.webpublico.enums.TipoResumoExecucao;
import br.com.webpublico.negocios.ExecucaoContratoFacade;
import br.com.webpublico.negocios.ExecucaoProcessoFacade;
import br.com.webpublico.negocios.RequisicaoDeCompraFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@ManagedBean
@ViewScoped
public class ComponenteRequisicaoCompraExecucaoControlador implements Serializable {

    @EJB
    private RequisicaoDeCompraFacade requisicaoDeCompraFacade;
    @EJB
    private ExecucaoProcessoFacade execucaoProcessoFacade;
    @EJB
    private ExecucaoContratoFacade execucaoContratoFacade;
    private RequisicaoCompraExecucaoVO requisicaoCompraExecucaoVO;
    private FiltroResumoExecucaoVO filtroResumoExecucaoVO;
    private List<RequisicaoCompraExecucaoVO> execucoes;

    public void novo(Long idRequisicao) {
        if (idRequisicao == null) {
            execucoes = Lists.newArrayList();
            return;
        }
        execucoes = requisicaoDeCompraFacade.buscarRequisicaoExecucaoComponente(idRequisicao);
        Collections.sort(execucoes);
    }

    public void selecionarRequisicaoExecucaoVo(RequisicaoCompraExecucaoVO reqExecVO) {
        requisicaoCompraExecucaoVO =  (RequisicaoCompraExecucaoVO) Util.clonarObjeto(reqExecVO);
        requisicaoCompraExecucaoVO.setEmpenhosVO(Lists.newArrayList());
        if (reqExecVO.getTipoProcesso().isContrato()) {
            ExecucaoContrato execucaoContrato = execucaoContratoFacade.recuperarSemDependencias(reqExecVO.getId());
            novoFiltroResumoExecucaoContrato(execucaoContrato);
        } else if (reqExecVO.getTipoProcesso().isExecucaoProcesso()) {
            ExecucaoProcesso execucaoProcesso = execucaoProcessoFacade.recuperarSemDependencias(reqExecVO.getId());
            novoFiltroResumoExecucaoProcesso(execucaoProcesso);
        }
        requisicaoDeCompraFacade.buscarEmpenhoExecucao(requisicaoCompraExecucaoVO, requisicaoCompraExecucaoVO.getTipoObjetoCompra());
        FacesUtil.executaJavaScript("$('#modalDetalhesRequicaoExec').modal('show')");
    }

    private void novoFiltroResumoExecucaoContrato(ExecucaoContrato execucaoContrato) {
        filtroResumoExecucaoVO = new FiltroResumoExecucaoVO(TipoResumoExecucao.CONTRATO);
        filtroResumoExecucaoVO.setContrato(execucaoContrato.getContrato());
        filtroResumoExecucaoVO.setExecucaoContrato(execucaoContrato);
        filtroResumoExecucaoVO.setIdProcesso(execucaoContrato.getContrato().getId());
    }

    private void novoFiltroResumoExecucaoProcesso(ExecucaoProcesso execucaoProcesso) {
        filtroResumoExecucaoVO = new FiltroResumoExecucaoVO(TipoResumoExecucao.PROCESSO);
        filtroResumoExecucaoVO.setExecucaoProcesso(execucaoProcesso);
        filtroResumoExecucaoVO.setIdProcesso(execucaoProcesso.getIdProcesso());
    }

    public String getCaminhoExecucao() {
        if (requisicaoCompraExecucaoVO == null) {
            return "";
        }
        if (requisicaoCompraExecucaoVO.getTipoProcesso().isContrato()) {
            return "/execucao-contrato-adm/ver/" + requisicaoCompraExecucaoVO.getId() + "/";
        } else if (requisicaoCompraExecucaoVO.getTipoProcesso().isReconhecimentoDivida()) {
            return "/reconhecimento-divida/ver/" + requisicaoCompraExecucaoVO.getId() + "/";
        }
        return "/execucao-sem-contrato/ver/" + requisicaoCompraExecucaoVO.getId() + "/";
    }

    public String getCaminhoEmpenho(Empenho empenho) {
        if (empenho.isEmpenhoNormal()) {
            return "/empenho/ver/" + empenho.getId() + "/";
        }
        return "/empenho/resto-a-pagar/ver/" + empenho.getId() + "/";
    }

    public RequisicaoCompraExecucaoVO getRequisicaoCompraExecucaoVO() {
        return requisicaoCompraExecucaoVO;
    }

    public void setRequisicaoCompraExecucaoVO(RequisicaoCompraExecucaoVO requisicaoCompraExecucaoVO) {
        this.requisicaoCompraExecucaoVO = requisicaoCompraExecucaoVO;
    }

    public List<RequisicaoCompraExecucaoVO> getExecucoes() {
        return execucoes;
    }

    public void setExecucoes(List<RequisicaoCompraExecucaoVO> execucoes) {
        this.execucoes = execucoes;
    }

    public FiltroResumoExecucaoVO getFiltroResumoExecucaoContrato() {
        return filtroResumoExecucaoVO;
    }

    public void setFiltroResumoExecucaoContrato(FiltroResumoExecucaoVO filtroResumoExecucaoVO) {
        this.filtroResumoExecucaoVO = filtroResumoExecucaoVO;
    }
}
