package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoContratoCEASA;
import br.com.webpublico.negocios.ContratoCEASAFacade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * Created by William on 07/01/2016.
 */
public class RenovadorContratoCeasa {

    ContratoCEASAFacade facade;
    Date dataOperacao;
    UsuarioSistema usuarioSistema;
    ParametroRendas parametroRendas;
    Exercicio exercicio;
    BigDecimal ufm;

    public RenovadorContratoCeasa(ContratoCEASAFacade facade, Date dataOperacao, UsuarioSistema usuarioSistema, ParametroRendas parametroRendas, Exercicio exercicio) {
        this.facade = facade;
        this.dataOperacao = dataOperacao;
        this.usuarioSistema = usuarioSistema;
        this.parametroRendas = parametroRendas;
        this.exercicio = exercicio;
        ufm = facade.getMoedaFacade().recuperaValorUFMParaData(dataOperacao);
    }

    public ContratoCEASA criarRenovacaoAutomaticaDosContratos(ContratoCEASA contrato) {
        ContratoCEASA contratoRenovado = new ContratoCEASA();
        BigDecimal totalServico = BigDecimal.ZERO;
        contratoRenovado.setOriginario(contrato);
        String numeroSequencia = facade.getSequenciaRenovacaoContrato(contrato);
        String[] numeroContrato = contrato.getNumeroContrato().split("-");
        contratoRenovado.setNumeroContrato(numeroContrato[0] + "-" + (numeroSequencia));
        contratoRenovado.setDataInicio(parametroRendas.getDataInicioContrato());
        contratoRenovado.setDiaVencimento(parametroRendas.getDiaVencimentoParcelas().intValue());
        contratoRenovado.setLocatario(contrato.getLocatario());
        contratoRenovado.setPeriodoVigencia(facade.mesVigente(parametroRendas.getDataInicioContrato(), parametroRendas));
        contratoRenovado.setQuantidadeParcelas(facade.mesVigente(parametroRendas.getDataInicioContrato(), parametroRendas));
        contratoRenovado.setLotacaoVistoriadora(contrato.getLotacaoVistoriadora());
        contratoRenovado.setAtividadePonto(contrato.getAtividadePonto());
        contratoRenovado.setIndiceEconomico((contrato.getIndiceEconomico()));
        contratoRenovado.setDataFim(parametroRendas.getDataFimContrato());
        contratoRenovado.setValorUFMAtual(facade.getMoedaFacade().recuperaValorUFMParaData(dataOperacao));
        contrato.setUsuarioOperacao(usuarioSistema);
        contrato.setDataOperacao(dataOperacao);
        contrato.setSituacaoContrato(SituacaoContratoCEASA.RENOVADO);
        copiarPontosComerciais(contrato, contratoRenovado);
        contratoRenovado.setAreaTotalRateio(parametroRendas.getAreaTotal());
        if (contratoRenovado.getPontoComercialContratoCEASAs().get(0).getPontoComercial().getLocalizacao().getCalculaRateio() == null || contratoRenovado.getPontoComercialContratoCEASAs().get(0).getPontoComercial().getLocalizacao().getCalculaRateio()) {
            for (ServicoRateioCeasa servico : parametroRendas.getListaServicoRateioCeasa()) {
                totalServico = totalServico.add(servico.getValor());
            }
        }
        BigDecimal qtdeParcelas = new BigDecimal(contratoRenovado.getQuantidadeParcelas());
        contratoRenovado.setValorServicosRateio(totalServico);
        contratoRenovado.setValorMensalRateio((contratoRenovado.getValorServicosRateio().divide(contratoRenovado.getAreaTotalRateio())).multiply(contratoRenovado.getSomaTotalArea()).setScale(2, RoundingMode.UP));
        contratoRenovado.setValorTotalRateio(contratoRenovado.getValorMensalRateio().multiply(qtdeParcelas));
        contratoRenovado.setSituacaoContrato(SituacaoContratoCEASA.ATIVO);
        contratoRenovado.setTipoUtilizacao(contrato.getTipoUtilizacao());
        contratoRenovado.setUsuarioInclusao(contrato.getUsuarioInclusao());
        return contratoRenovado;
    }


    private void copiarPontosComerciais(ContratoCEASA selecionado, ContratoCEASA contratoRenovado) {
        List<PontoComercialContratoCEASA> listaDePontosComercialCEASA;
        listaDePontosComercialCEASA = facade.recuperarPontoDoContrato(selecionado);
        BigDecimal total = BigDecimal.ZERO;
        for (PontoComercialContratoCEASA p : listaDePontosComercialCEASA) {
            PontoComercialContratoCEASA pontoNovo = new PontoComercialContratoCEASA();
            pontoNovo.setPontoComercial(p.getPontoComercial());
            pontoNovo.setValorMetroQuadrado(p.getPontoComercial().getValorMetroQuadrado());
            pontoNovo.setArea(p.getPontoComercial().getArea());
            pontoNovo.setValorTotalContrato(p.getValorTotalContrato());
            pontoNovo.setContratoCEASA(contratoRenovado);
            BigDecimal valorCalculadoMes = p.getPontoComercial().getArea().multiply(p.getPontoComercial().getValorMetroQuadrado());
            pontoNovo.setValorCalculadoMes(valorCalculadoMes);

            BigDecimal valor = valorCalculadoMes.multiply(BigDecimal.valueOf(contratoRenovado.getPeriodoVigencia()));
            pontoNovo.setValorTotalContrato(valor);

            BigDecimal valorPorMes = pontoNovo.getValorCalculadoMes().multiply(facade.getMoedaFacade().recuperaValorVigenteUFM());
            pontoNovo.setValorTotalMes(valorPorMes.setScale(2, RoundingMode.HALF_UP));

            BigDecimal valorEmReais = pontoNovo.getValorTotalContrato().multiply(facade.getMoedaFacade().recuperaValorVigenteUFM());
            pontoNovo.setValorReal(valorEmReais.setScale(2, RoundingMode.HALF_UP));
            total = total.add(pontoNovo.getValorReal());
            contratoRenovado.getPontoComercialContratoCEASAs().add(pontoNovo);
        }
        contratoRenovado.setValorTotalContrato(total);
    }
}
