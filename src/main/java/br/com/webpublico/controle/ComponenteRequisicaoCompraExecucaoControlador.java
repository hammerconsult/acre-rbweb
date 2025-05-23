package br.com.webpublico.controle;

import br.com.webpublico.entidades.Empenho;
import br.com.webpublico.entidades.ExecucaoContrato;
import br.com.webpublico.entidades.ExecucaoProcesso;
import br.com.webpublico.entidades.RequisicaoCompraExecucao;
import br.com.webpublico.entidadesauxiliares.EmpenhoVO;
import br.com.webpublico.entidadesauxiliares.FiltroResumoExecucaoVO;
import br.com.webpublico.entidadesauxiliares.RequisicaoExecucaoExecucaoVO;
import br.com.webpublico.enums.TipoRequisicaoCompra;
import br.com.webpublico.enums.TipoResumoExecucao;
import br.com.webpublico.negocios.EmpenhoFacade;
import br.com.webpublico.negocios.ExecucaoContratoFacade;
import br.com.webpublico.negocios.ExecucaoProcessoFacade;
import br.com.webpublico.negocios.RequisicaoDeCompraFacade;
import br.com.webpublico.util.FacesUtil;
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
    @EJB
    private EmpenhoFacade empenhoFacade;
    private RequisicaoExecucaoExecucaoVO requisicaoExecucaoExecucaoVO;
    private List<RequisicaoCompraExecucao> execucoes;
    private FiltroResumoExecucaoVO filtroResumoExecucaoVO;

    public void novo(Long idRequisicao) {
        if (idRequisicao == null) {
            execucoes = Lists.newArrayList();
            return;
        }
        execucoes = requisicaoDeCompraFacade.buscarRequisicaoExecucao(idRequisicao);
        Collections.sort(execucoes);
    }

    public void selecionarRequisicaoExecucaoVo(RequisicaoExecucaoExecucaoVO reqExec) {
        if (reqExec.getTipoExecucao().isContrato()) {
            ExecucaoContrato execucaoContrato = reqExec.getExecucaoContrato();
            requisicaoExecucaoExecucaoVO = new RequisicaoExecucaoExecucaoVO(execucaoContrato);
            requisicaoExecucaoExecucaoVO.setTipoExecucao(TipoRequisicaoCompra.CONTRATO);
            adicionarEmpenhoExecucaoContrato(execucaoContrato);
            novoFiltroResumoExecucaoContrato(execucaoContrato);
        } else {
            ExecucaoProcesso execucaoProcesso = reqExec.getExecucaoProcesso();
            requisicaoExecucaoExecucaoVO = new RequisicaoExecucaoExecucaoVO(execucaoProcesso);
            requisicaoExecucaoExecucaoVO.setTipoExecucao(execucaoProcesso.isExecucaoAta() ? TipoRequisicaoCompra.ATA_REGISTRO_PRECO : TipoRequisicaoCompra.DISPENSA_LICITACAO_INEXIGIBILIDADE);
            adicionarEmpenhoExecucaoProcesso(execucaoProcesso);
            novoFiltroResumoExecucaoProcesso(requisicaoExecucaoExecucaoVO.getExecucaoProcesso());
        }
        FacesUtil.executaJavaScript("$('#modalDetalhesExecucao').modal('show');");
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

    public void selecionarRequisicaoExecucaoComponente(RequisicaoCompraExecucao reqExec) {
        if (reqExec.getRequisicaoCompra().isTipoContrato()) {
            ExecucaoContrato execucaoContrato = reqExec.getExecucaoContrato();
            requisicaoExecucaoExecucaoVO = new RequisicaoExecucaoExecucaoVO(execucaoContrato);
            requisicaoExecucaoExecucaoVO.setTipoExecucao(TipoRequisicaoCompra.CONTRATO);
            adicionarEmpenhoExecucaoContrato(execucaoContrato);
            novoFiltroResumoExecucaoContrato(execucaoContrato);
        } else {
            requisicaoExecucaoExecucaoVO = new RequisicaoExecucaoExecucaoVO(reqExec.getExecucaoProcesso());
            ExecucaoProcesso execucaoProcesso = requisicaoExecucaoExecucaoVO.getExecucaoProcesso();
            requisicaoExecucaoExecucaoVO.setTipoExecucao(execucaoProcesso.isExecucaoAta() ? TipoRequisicaoCompra.ATA_REGISTRO_PRECO : TipoRequisicaoCompra.DISPENSA_LICITACAO_INEXIGIBILIDADE);
            adicionarEmpenhoExecucaoProcesso(execucaoProcesso);
            novoFiltroResumoExecucaoProcesso(execucaoProcesso);
        }
        FacesUtil.executaJavaScript("$('#modalDetalhesExecucao').modal('show');");
    }

    private void adicionarEmpenhoExecucaoProcesso(ExecucaoProcesso execucaoProc) {
        List<Empenho> empenhos = execucaoProcessoFacade.buscarEmpenhosExecucao(execucaoProc);
        criarEmpenhoVO(empenhos);
    }

    private void adicionarEmpenhoExecucaoContrato(ExecucaoContrato execucaoContrato) {
        List<Empenho> empenhos = execucaoContratoFacade.buscarEmpenhosExecucao(execucaoContrato);
        criarEmpenhoVO(empenhos);
    }

    private void criarEmpenhoVO(List<Empenho> empenhos) {
        for (Empenho emp : empenhos) {
            EmpenhoVO novoEmp = new EmpenhoVO();
            novoEmp.setEmpenho(emp);
            novoEmp.setDesdobramentoEmpenho(empenhoFacade.buscarDesdobramento(emp));
            requisicaoExecucaoExecucaoVO.getEmpenhos().add(novoEmp);
        }
    }

    public String getCaminhoExecucao() {
        if (requisicaoExecucaoExecucaoVO == null) {
            return "";
        }
        if (requisicaoExecucaoExecucaoVO.getTipoExecucao().isContrato()) {
            return "/execucao-contrato-adm/ver/" + requisicaoExecucaoExecucaoVO.getExecucaoContrato().getId() + "/";
        }
        return "/execucao-sem-contrato/ver/" + requisicaoExecucaoExecucaoVO.getExecucaoProcesso().getId() + "/";
    }

    public String getCaminhoEmpenho(Empenho empenho) {
        if (empenho.isEmpenhoNormal()) {
            return "/empenho/ver/" + empenho.getId() + "/";
        }
        return "/empenho/resto-a-pagar/ver/" + empenho.getId() + "/";
    }

    public RequisicaoExecucaoExecucaoVO getRequisicaoExecucao() {
        return requisicaoExecucaoExecucaoVO;
    }

    public void setRequisicaoExecucao(RequisicaoExecucaoExecucaoVO requisicaoExecucaoExecucaoVO) {
        this.requisicaoExecucaoExecucaoVO = requisicaoExecucaoExecucaoVO;
    }

    public List<RequisicaoCompraExecucao> getExecucoes() {
        return execucoes;
    }

    public void setExecucoes(List<RequisicaoCompraExecucao> execucoes) {
        this.execucoes = execucoes;
    }

    public FiltroResumoExecucaoVO getFiltroResumoExecucaoContrato() {
        return filtroResumoExecucaoVO;
    }

    public void setFiltroResumoExecucaoContrato(FiltroResumoExecucaoVO filtroResumoExecucaoVO) {
        this.filtroResumoExecucaoVO = filtroResumoExecucaoVO;
    }
}
