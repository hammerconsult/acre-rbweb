package br.com.webpublico.controle;

import br.com.webpublico.entidades.OrdemDeServicoContrato;
import br.com.webpublico.entidadesauxiliares.OrdemCompraServicoVo;
import br.com.webpublico.entidadesauxiliares.administrativo.FiltroOrdemCompraServicoContratoVO;
import br.com.webpublico.negocios.OrdemCompraFacade;
import br.com.webpublico.util.FacesUtil;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@ManagedBean
@ViewScoped
public class OrdemCompraControlador implements Serializable {

    @EJB
    private OrdemCompraFacade facade;
    private List<OrdemCompraServicoVo> ordensCompra;
    private OrdemCompraServicoVo ordemCompraSelecionada;
    private FiltroOrdemCompraServicoContratoVO filtro;
    private List<OrdemDeServicoContrato> ordensServico;

    public void novo(FiltroOrdemCompraServicoContratoVO filtro) {
        if (filtro != null && filtro.getContrato() != null && filtro.getContrato().getId() != null) {
            this.filtro = filtro;
            if (ordensCompra == null) {
                preencherOrdensCompraServico();
            }
        }
    }

    public void preencherOrdensCompraServico() {
        ordensCompra = facade.buscarOrdemCompraAndServicoPorContrato(filtro.getContrato());
        ordensCompra.forEach(oc -> Collections.sort(oc.getExecucoes()));
        if (filtro.getOrdemServico()) {
            ordensServico = facade.buscarOrdemServicoContrato(filtro.getContrato());
        }
        FacesUtil.executaJavaScript("atualizarTabelaOC()");
    }

    public boolean hasOrdensCompra() {
        return ordensCompra != null && !ordensCompra.isEmpty();
    }

    public void selecionarOrdemCompra(OrdemCompraServicoVo ordemCompraServicoVo) {
        ordemCompraSelecionada = ordemCompraServicoVo;
    }

    public List<OrdemCompraServicoVo> getOrdensCompra() {
        return ordensCompra;
    }

    public void setOrdensCompra(List<OrdemCompraServicoVo> ordensCompra) {
        this.ordensCompra = ordensCompra;
    }

    public List<OrdemDeServicoContrato> getOrdensServico() {
        return ordensServico;
    }

    public void setOrdensServico(List<OrdemDeServicoContrato> ordensServico) {
        this.ordensServico = ordensServico;
    }

    public OrdemCompraServicoVo getOrdemCompraSelecionada() {
        return ordemCompraSelecionada;
    }

    public void setOrdemCompraSelecionada(OrdemCompraServicoVo ordemCompraSelecionada) {
        this.ordemCompraSelecionada = ordemCompraSelecionada;
    }

    public BigDecimal getValorTotalRequisicao() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasOrdensCompra()) {
            for (OrdemCompraServicoVo item : ordensCompra) {
                valor = valor.add(item.getValorRequisicao());
            }
        }
        return valor;
    }

    public BigDecimal getValorTotalRequisicaoEstornado() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasOrdensCompra()) {
            for (OrdemCompraServicoVo item : ordensCompra) {
                valor = valor.add(item.getValorRequisicaoEstornado());
            }
        }
        return valor;
    }

    public Integer getQuantidadeTotalDocumentos() {
        int valor = 0;
        if (hasOrdensCompra()) {
            for (OrdemCompraServicoVo item : ordensCompra) {
                valor = valor + item.getQtdDocumentos();
            }
        }
        return valor;
    }

    public BigDecimal getValorTotalAtesto() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasOrdensCompra()) {
            for (OrdemCompraServicoVo item : ordensCompra) {
                valor = valor.add(item.getValorAtesto());
            }
        }
        return valor;
    }

    public BigDecimal getValorTotalAtestoEstornado() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasOrdensCompra()) {
            for (OrdemCompraServicoVo item : ordensCompra) {
                valor = valor.add(item.getValorAtestoEstornado());
            }
        }
        return valor;
    }

    public BigDecimal getSaldoTotalOrdemCompra() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasOrdensCompra()) {
            for (OrdemCompraServicoVo item : ordensCompra) {
                valor = valor.add(item.getSaldo());
            }
        }
        return valor;
    }
}
