package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoContratoRendasPatrimoniais;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: scarpini
 * Date: 11/07/14
 * Time: 14:19
 * To change this template use File | Settings | File Templates.
 */
public class RenovadorContradoRendas {

    ContratoRendasPatrimoniaisFacade facade;
    Date dataOperacao;
    UsuarioSistema usuarioSistema;
    ParametroRendas parametroRendas;
    Exercicio exercicio;
    BigDecimal ufm;

    public RenovadorContradoRendas(ContratoRendasPatrimoniaisFacade facade, Date dataOperacao, UsuarioSistema usuarioSistema, ParametroRendas parametroRendas, Exercicio exercicio) {
        this.facade = facade;
        this.dataOperacao = dataOperacao;
        this.usuarioSistema = usuarioSistema;
        this.parametroRendas = parametroRendas;
        this.exercicio = exercicio;
        ufm = facade.getMoedaFacade().recuperaValorUFMParaData(dataOperacao);
    }

    public ContratoRendasPatrimoniais criarRenovacaoAutomaticaDosContratos(ContratoRendasPatrimoniais contrato) {
        ContratoRendasPatrimoniais contratoRenovado = new ContratoRendasPatrimoniais();
        String numeroSequencia = facade.getSequenciaRenovacaoContrato(contrato);
        String[] numeroContrato = contrato.getNumeroContrato().split("-");

        contratoRenovado.setNumeroContrato(numeroContrato[0] + "-" + (numeroSequencia));
        contratoRenovado.setSequenciaContrato(facade.getSequenciaContratoPorLocatario(contrato));
        contratoRenovado.setDataInicio(parametroRendas.getDataInicioContrato());
        contratoRenovado.setDiaVencimento(parametroRendas.getDiaVencimentoParcelas().intValue());
        contratoRenovado.setLocatario(contrato.getLocatario());
        contratoRenovado.setPeriodoVigencia(facade.mesVigente(parametroRendas.getDataInicioContrato(), parametroRendas));
        contratoRenovado.setLotacaoVistoriadora(contrato.getLotacaoVistoriadora());
        contratoRenovado.setAtividadePonto(contrato.getAtividadePonto());
        contratoRenovado.setIndiceEconomico(contrato.getIndiceEconomico());
        contratoRenovado.setDataFim(parametroRendas.getDataFimContrato());
        contratoRenovado.setValorUfmDoContrato(ufm);
        contratoRenovado.setDataOperacao(dataOperacao);

        copiarListaDeCnaeRenovacaoAutomatica(contrato, contratoRenovado);

        contrato.setUsuarioOperacao(usuarioSistema);
        contrato.setDataOperacao(new Date());
        contrato.setMotivoOperacao(contrato.getMotivoOperacao());
        copiarPontosComerciaisRenovacaoAutomatica(contrato, contratoRenovado);
        contratoRenovado.setSituacaoContrato(SituacaoContratoRendasPatrimoniais.ATIVO);
        contratoRenovado.setTipoUtilizacao(contrato.getTipoUtilizacao());
        contratoRenovado.setUsuarioInclusao(usuarioSistema);
        contratoRenovado.setQuantidadeParcelas(facade.mesVigente(parametroRendas.getDataInicioContrato(), parametroRendas));
        contrato.setSituacaoContrato(SituacaoContratoRendasPatrimoniais.RENOVADO);
//        if (podeRenovarContrato(contratoRenovado)) {
        //System.out.println("contrato numero " + contrato.getNumeroContrato());
        //System.out.println("contrato numero renovado " + contratoRenovado.getNumeroContrato());
//        }
        return contratoRenovado;
    }

    private void copiarListaDeCnaeRenovacaoAutomatica(ContratoRendasPatrimoniais contrato, ContratoRendasPatrimoniais contratoRenovado) {
        List<ContratoRendaCNAE> cnaes = facade.recuperarCnaeContrato(contrato);
        if (!cnaes.isEmpty()) {
            for (ContratoRendaCNAE cnae : cnaes) {
                ContratoRendaCNAE contratoCnae = new ContratoRendaCNAE();
                contratoCnae.setCnae(cnae.getCnae());
                contratoCnae.setContratoRendasPatrimoniais(contratoRenovado);
                contratoRenovado.getContratoRendaCNAEs().add(contratoCnae);
            }
        }
    }

    private void copiarPontosComerciaisRenovacaoAutomatica(ContratoRendasPatrimoniais contrato, ContratoRendasPatrimoniais contratoRenovado) {
        List<PontoComercialContratoRendasPatrimoniais> listaDePontosComercialRendasPatrimoniais = facade.recuperarPontoDoContrato(contrato);
        for (PontoComercialContratoRendasPatrimoniais p : listaDePontosComercialRendasPatrimoniais) {
            PontoComercialContratoRendasPatrimoniais pontoNovo = new PontoComercialContratoRendasPatrimoniais();
            pontoNovo.setPontoComercial(p.getPontoComercial());
            pontoNovo.setValorMetroQuadrado(p.getPontoComercial().getValorMetroQuadrado());
            pontoNovo.setArea(p.getPontoComercial().getArea());
            pontoNovo.setValorTotalContrato(p.getValorTotalContrato());
            pontoNovo.setContratoRendasPatrimoniais(contratoRenovado);
            BigDecimal valorCalculadoMes = p.getPontoComercial().getArea().multiply(p.getPontoComercial().getValorMetroQuadrado());
            pontoNovo.setValorCalculadoMes(valorCalculadoMes);
            BigDecimal valor = valorCalculadoMes.multiply(BigDecimal.valueOf(contratoRenovado.getPeriodoVigencia()));
            pontoNovo.setValorTotalContrato(valor);
            contratoRenovado.setValorPorMesUFM(contratoRenovado.getValorPorMesUFM().add(pontoNovo.getValorCalculadoMes()));
            contratoRenovado.getPontoComercialContratoRendasPatrimoniais().add(pontoNovo);
        }
    }
}
