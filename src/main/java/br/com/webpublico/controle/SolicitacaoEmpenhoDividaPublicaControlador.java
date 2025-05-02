package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 25/02/14
 * Time: 17:28
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "solicitacaoEmpenhoDividaPublicaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "gerar-solicitacao-empenho", pattern = "/divida-publica/solicitacao-empenho/", viewId = "/faces/financeiro/orcamentario/dividapublica/solicitacaoempenho/edita.xhtml")
})

public class SolicitacaoEmpenhoDividaPublicaControlador extends DividaPublicaSuperControlador implements Serializable {

    private ConverterAutoComplete converterDividaPublica;
    private DividaPublica dividaPublica;

    @URLAction(mappingId = "gerar-solicitacao-empenho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void gerarSolicitacao() {
    }

    public Converter getConverterDividaPublica() {
        if (converterDividaPublica == null) {
            converterDividaPublica = new ConverterAutoComplete(DividaPublica.class, dividaPublicaFacade);
        }
        return converterDividaPublica;
    }

    public List<DividaPublica> completaDividaPublica(String parte) {
        try {
            return dividaPublicaFacade.buscarDividasPublicasPorUsuario(parte.trim(), sistemaControlador.getUsuarioCorrente(), sistemaControlador.getDataOperacao(), null);
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
        }
        return new ArrayList<>();
    }

    public List<UnidadeOrganizacional> completaUnidadeDaDividaPublica() {
        List<UnidadeOrganizacional> listaUnidade = new ArrayList<>();
        if (dividaPublica != null) {
            try {
                listaUnidade = dividaPublicaFacade.recuperaUnidadesDaDividaPublica(dividaPublica);
                if (listaUnidade.size() != 0) {
                    return listaUnidade;
                } else {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " A dívida pública selecionada não possui unidades organizacionais cadastradas.");
                }
            } catch (Exception ex) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
            }
        }
        return new ArrayList<>();
    }

    public void recuperaDividaPublica() {
        dividaPublica = dividaPublicaFacade.recuperar(dividaPublica.getId());
    }

    public BigDecimal getTotalJurosParcelamentos() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        if (dividaPublica != null) {
            for (ParcelaDividaPublica pdp : dividaPublica.getParcelas()) {
                total = total.add(pdp.getValorJuros());
            }
        }
        return total;
    }

    public BigDecimal getTotalOutrosEncargosParcelamento() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        if (dividaPublica != null) {
            for (ParcelaDividaPublica pdp : dividaPublica.getParcelas()) {
                if (pdp.getOutrosEncargos() != null) {
                    total = total.add(pdp.getOutrosEncargos());
                }
            }
        }
        return total;
    }

    public BigDecimal getTotalAmortizacao() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        if (dividaPublica != null) {
            for (ParcelaDividaPublica pdp : dividaPublica.getParcelas()) {
                if (pdp.getValorAmortizado() != null) {
                    total = total.add(pdp.getValorAmortizado());
                }
            }
        }
        return total;
    }

    public BigDecimal getTotalPrestacao() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        if (dividaPublica != null) {
            for (ParcelaDividaPublica pdp : dividaPublica.getParcelas()) {
                if (pdp.getValorPrestacao() != null) {
                    total = total.add(pdp.getValorPrestacao());
                }
            }
        }
        return total;
    }

    public void selecionaParcelaDaDivida(ParcelaDividaPublica parcela) {
        if (dividaPublica != null) {
            parcelaDividaPublica = parcela;
        }
    }

    public boolean validaCampoUnidade() {
        if (unidadeOrganizacional == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Unidade Organizacional deve ser informado.");
            return false;
        }
        return true;
    }


    public void gerarSolicitacaoEmpenho() {
        try {
            if (validaCampoUnidade()) {
                if (dividaPublicaFacade.verificaSolicitacaoEmpenhoParaDividaPublica(parcelaDividaPublica)) {
                    if (parcelaDividaPublica.getValorPrestacao().compareTo(BigDecimal.ZERO) > 0) {
                        dividaPublicaFacade.gerarSolicitacaoEmpenhoValorDaParcela(dividaPublica, parcelaDividaPublica, unidadeOrganizacional);
                    }
                    if (parcelaDividaPublica.getValorJuros().compareTo(BigDecimal.ZERO) > 0) {
                        dividaPublicaFacade.geraSolicitacaoEmpenhoJuros(dividaPublica, parcelaDividaPublica, unidadeOrganizacional);
                    }
                    if (parcelaDividaPublica.getOutrosEncargos().compareTo(BigDecimal.ZERO) > 0) {
                        dividaPublicaFacade.gerarSolicitacaoEmpenhoOutrosEncargos(dividaPublica, parcelaDividaPublica, unidadeOrganizacional);
                    }
                } else {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " A parcela: <b>" + parcelaDividaPublica + "</b> possui uma solicitação de empenho pendente.");
                    return;
                }
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Solicitação de empenho gerada com sucesso.");
                FacesUtil.redirecionamentoInterno("/divida-publica/listar/");
            }
        } catch (Exception ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
        }
    }


    public DividaPublica getDividaPublica() {
        return dividaPublica;
    }

    public void setDividaPublica(DividaPublica dividaPublica) {
        this.dividaPublica = dividaPublica;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public ParcelaDividaPublica getParcelaDividaPublica() {
        return parcelaDividaPublica;
    }

    public void setParcelaDividaPublica(ParcelaDividaPublica parcelaDividaPublica) {
        this.parcelaDividaPublica = parcelaDividaPublica;
    }
}
