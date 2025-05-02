package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoDocPagto;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.interfaces.IGuiaArquivoOBN600;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.util.FacesUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 07/08/15
 * Time: 12:30
 * To change this template use File | Settings | File Templates.
 */
public class GuiaPagamentoItem {

    private String pessoaPagamento;
    private String agencia;
    private String contaCorrente;
    private String codigoBarrasOrPessoa;
    private String numeroPagamento;
    private String autenticacao;
    private String codigoIndentificador;
    private String tipoCodigoIdentificador;
    private String imagem;
    private String situacao;
    private String codigoReceita;
    private String codigoRecolhimento;
    private String codigoIdentificacaoTributo;
    private String numeroReferencia;
    private Date competencia;
    private Date dataPagamentoGuia;
    private Date periodoApuracao;
    private Date dataVencimento;
    private BigDecimal valorOutra;
    private BigDecimal valorPago;
    private BigDecimal valorGuia;
    private BigDecimal valorMulta;
    private BigDecimal valorJuros;
    private BigDecimal valorReceitaBruta;
    private BigDecimal percentualReceitaBruta;
    private BigDecimal atualizacaoMonetaria;
    private BigDecimal total;
    private String tipoDocPagto;
    private String banco;
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;


    private GuiaPagamentoItem() {
    }

    public GuiaPagamentoItem(IGuiaArquivoOBN600 iGuiaArquivoOBN600, HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade) {
        this.hierarquiaOrganizacionalFacade = hierarquiaOrganizacionalFacade;
        String imagem = FacesUtil.geraUrlImagemDir() + "img/Brasao_de_Rio_Branco.gif";
        if (iGuiaArquivoOBN600 instanceof GuiaPagamento) {
            GuiaPagamento guiaPagamento = (GuiaPagamento) iGuiaArquivoOBN600;
            Pagamento pagamento = guiaPagamento.getPagamento();
            this.imagem = imagem;
            this.numeroPagamento = pagamento.getNumeroPagamento();
            this.dataPagamentoGuia = pagamento.getDataConciliacao() != null ? pagamento.getDataConciliacao() : pagamento.getDataPagamento();
            this.valorPago = iGuiaArquivoOBN600.getValorTotalPorGuia();
            this.tipoDocPagto = pagamento.getTipoDocPagto().name();
            this.autenticacao = guiaPagamento.getNumeroAutenticacao();
            this.banco = pagamento.getSubConta().getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco();

            for (SubContaUniOrg subContaUniOrg : pagamento.getSubConta().getUnidadesOrganizacionais()) {
                if (subContaUniOrg.getExercicio().equals(pagamento.getExercicio())) {
                    if (subContaUniOrg.getUnidadeOrganizacional() != null) {
                        this.pessoaPagamento = getHierarquiaOrgazanicionalFacade().buscarCodigoDescricaoHierarquia(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(),
                            subContaUniOrg.getUnidadeOrganizacional(), dataPagamentoGuia);
                    }
                    this.agencia = subContaUniOrg.getSubConta().getContaBancariaEntidade().getAgencia().getNumeroAgenciaComDigitoVerificador();
                    this.contaCorrente = subContaUniOrg.getSubConta().getContaBancariaEntidade().getNumeroContaComDigitoVerificador();
                }
            }
            switch (guiaPagamento.getPagamento().getTipoDocPagto()) {
                case CONVENIO:
                    codigoBarrasOrPessoa = iGuiaArquivoOBN600.getGuiaConvenio().getCodigoBarra();
                    break;
                case FATURA:
                    codigoBarrasOrPessoa = iGuiaArquivoOBN600.getGuiaFatura().getCodigoBarra();
                    valorGuia = iGuiaArquivoOBN600.getGuiaFatura().getValorNominal();
                    break;
                case GPS:
                    situacao = guiaPagamento.getSituacaoGuiaPagamento().getDescricao();
                    codigoBarrasOrPessoa = iGuiaArquivoOBN600.getPessoa().getNome();
                    tipoCodigoIdentificador = iGuiaArquivoOBN600.getTipoIdentificacaoGuia().getDescricao();
                    codigoIndentificador = iGuiaArquivoOBN600.getCodigoIdentificacao();
                    codigoReceita = guiaPagamento.getGuiaGPS().getCodigoReceitaTributo();
                    codigoIdentificacaoTributo = guiaPagamento.getGuiaGPS().getCodigoIdentificacaoTributo();
                    competencia = guiaPagamento.getGuiaGPS().getPeriodoCompetencia();
                    valorGuia = guiaPagamento.getGuiaGPS().getValorPrevistoINSS();
                    valorOutra = guiaPagamento.getGuiaGPS().getValorOutrasEntidades();
                    atualizacaoMonetaria = guiaPagamento.getGuiaGPS().getAtualizacaoMonetaria();
                    break;
                case DARF:
                    situacao = guiaPagamento.getSituacaoGuiaPagamento().getDescricao();
                    codigoBarrasOrPessoa = iGuiaArquivoOBN600.getPessoa().getNome();
                    tipoCodigoIdentificador = iGuiaArquivoOBN600.getTipoIdentificacaoGuia().getDescricao();
                    codigoIndentificador = iGuiaArquivoOBN600.getCodigoIdentificacao();
                    codigoReceita = guiaPagamento.getGuiaDARF().getCodigoReceitaTributo();
                    codigoIdentificacaoTributo = guiaPagamento.getGuiaDARF().getCodigoIdentificacaoTributo();
                    numeroReferencia = guiaPagamento.getGuiaDARF().getNumeroReferencia();
                    periodoApuracao = guiaPagamento.getGuiaDARF().getPeriodoApuracao();
                    dataVencimento = guiaPagamento.getGuiaDARF().getDataVencimento();
                    valorGuia = guiaPagamento.getGuiaDARF().getValorPrincipal();
                    valorMulta = guiaPagamento.getGuiaDARF().getValorMulta();
                    valorJuros = guiaPagamento.getGuiaDARF().getValorJuros();
                    break;
                case DARF_SIMPLES:
                    situacao = guiaPagamento.getSituacaoGuiaPagamento().getDescricao();
                    codigoBarrasOrPessoa = iGuiaArquivoOBN600.getPessoa().getNome();
                    tipoCodigoIdentificador = iGuiaArquivoOBN600.getTipoIdentificacaoGuia().getDescricao();
                    codigoIndentificador = iGuiaArquivoOBN600.getCodigoIdentificacao();
                    codigoReceita = guiaPagamento.getGuiaDARFSimples().getCodigoReceitaTributo();
                    codigoIdentificacaoTributo = guiaPagamento.getGuiaDARFSimples().getCodigoIdentificacaoTributo();
                    periodoApuracao = guiaPagamento.getGuiaDARFSimples().getPeriodoApuracao();
                    valorReceitaBruta = guiaPagamento.getGuiaDARFSimples().getValorReceitaBruta();
                    percentualReceitaBruta = guiaPagamento.getGuiaDARFSimples().getPercentualReceitaBruta();
                    valorGuia = iGuiaArquivoOBN600.getGuiaDARFSimples().getValorPrincipal();
                    valorMulta = guiaPagamento.getGuiaDARFSimples().getValorMulta();
                    valorJuros = guiaPagamento.getGuiaDARFSimples().getValorJuros();
                    break;
                case GRU:
                    situacao = guiaPagamento.getSituacaoGuiaPagamento().getDescricao();
                    codigoIndentificador = iGuiaArquivoOBN600.getCodigoIdentificacao();
                    codigoRecolhimento = ((GuiaPagamento) iGuiaArquivoOBN600).getGuiaGRU().getCodigoRecolhimento();
                    codigoBarrasOrPessoa = iGuiaArquivoOBN600.getPessoa().getNome();
                    valorGuia = ((GuiaPagamento) iGuiaArquivoOBN600).getGuiaGRU().getValorPrincipal();
                    valorPago = iGuiaArquivoOBN600.getValorTotalPorGuia();
                    break;
            }
        } else {
            GuiaPagamentoExtra guiaPagamento = (GuiaPagamentoExtra) iGuiaArquivoOBN600;
            PagamentoExtra pagamento = guiaPagamento.getPagamentoExtra();

            this.imagem = imagem;
            this.numeroPagamento = (pagamento.getNumeroPagamento() != null ? pagamento.getNumeroPagamento() : "") + "/" + pagamento.getExercicio().getAno();
            this.dataPagamentoGuia = guiaPagamento.getDataPagamento() != null ? guiaPagamento.getDataPagamento() : pagamento.getDataPagto();
            this.tipoDocPagto = pagamento.getTipoDocumento().name();
            this.valorPago = iGuiaArquivoOBN600.getValorTotalPorGuia();
            this.autenticacao = guiaPagamento.getNumeroAutenticacao();
            this.banco = pagamento.getSubConta().getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco();

            for (SubContaUniOrg subContaUniOrg : pagamento.getSubConta().getUnidadesOrganizacionais()) {
                if (subContaUniOrg.getExercicio().equals(pagamento.getExercicio())) {
                    if (subContaUniOrg.getUnidadeOrganizacional() != null) {
                        this.pessoaPagamento = getHierarquiaOrgazanicionalFacade().buscarCodigoDescricaoHierarquia(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(),
                            subContaUniOrg.getUnidadeOrganizacional(), dataPagamentoGuia);
                    }
                    this.agencia = subContaUniOrg.getSubConta().getContaBancariaEntidade().getAgencia().getNumeroAgenciaComDigitoVerificador();
                    this.contaCorrente = subContaUniOrg.getSubConta().getContaBancariaEntidade().getNumeroContaComDigitoVerificador();
                }
            }
            switch (guiaPagamento.getPagamentoExtra().getTipoDocumento()) {
                case CONVENIO:
                    codigoBarrasOrPessoa = iGuiaArquivoOBN600.getGuiaConvenio().getCodigoBarra();
                    autenticacao = guiaPagamento.getNumeroAutenticacao();
                    break;
                case FATURA:
                    codigoBarrasOrPessoa = iGuiaArquivoOBN600.getGuiaFatura().getCodigoBarra();
                    valorGuia = iGuiaArquivoOBN600.getGuiaFatura().getValorNominal();
                    autenticacao = guiaPagamento.getNumeroAutenticacao();
                    break;
                case GPS:
                    situacao = guiaPagamento.getSituacaoGuiaPagamento().getDescricao();
                    codigoBarrasOrPessoa = iGuiaArquivoOBN600.getPessoa().getNome();
                    tipoCodigoIdentificador = iGuiaArquivoOBN600.getTipoIdentificacaoGuia().getDescricao();
                    codigoIndentificador = iGuiaArquivoOBN600.getCodigoIdentificacao();
                    codigoReceita = guiaPagamento.getGuiaGPS().getCodigoReceitaTributo();
                    codigoIdentificacaoTributo = guiaPagamento.getGuiaGPS().getCodigoIdentificacaoTributo();
                    competencia = guiaPagamento.getGuiaGPS().getPeriodoCompetencia();
                    valorGuia = iGuiaArquivoOBN600.getGuiaGPS().getValorPrevistoINSS();
                    valorOutra = guiaPagamento.getGuiaGPS().getValorOutrasEntidades();
                    atualizacaoMonetaria = guiaPagamento.getGuiaGPS().getAtualizacaoMonetaria();
                    total = this.valorGuia.add(this.valorOutra).add(this.atualizacaoMonetaria);
                    autenticacao = guiaPagamento.getNumeroAutenticacao();
                    break;
                case DARF:
                    situacao = guiaPagamento.getSituacaoGuiaPagamento().getDescricao();
                    codigoBarrasOrPessoa = iGuiaArquivoOBN600.getPessoa().getNome();
                    tipoCodigoIdentificador = iGuiaArquivoOBN600.getTipoIdentificacaoGuia().getDescricao();
                    codigoIndentificador = iGuiaArquivoOBN600.getCodigoIdentificacao();
                    codigoReceita = guiaPagamento.getGuiaDARF().getCodigoReceitaTributo();
                    codigoIdentificacaoTributo = guiaPagamento.getGuiaDARF().getCodigoIdentificacaoTributo();
                    numeroReferencia = guiaPagamento.getGuiaDARF().getNumeroReferencia();
                    periodoApuracao = guiaPagamento.getGuiaDARF().getPeriodoApuracao();
                    dataVencimento = guiaPagamento.getGuiaDARF().getDataVencimento();
                    valorGuia = guiaPagamento.getGuiaDARF().getValorPrincipal();
                    valorMulta = guiaPagamento.getGuiaDARF().getValorMulta();
                    valorJuros = guiaPagamento.getGuiaDARF().getValorJuros();
                    autenticacao = guiaPagamento.getNumeroAutenticacao();
                    break;
                case DARF_SIMPLES:
                    situacao = guiaPagamento.getSituacaoGuiaPagamento().getDescricao();
                    codigoBarrasOrPessoa = iGuiaArquivoOBN600.getPessoa().getNome();
                    tipoCodigoIdentificador = iGuiaArquivoOBN600.getTipoIdentificacaoGuia().getDescricao();
                    codigoIndentificador = iGuiaArquivoOBN600.getCodigoIdentificacao();
                    codigoReceita = guiaPagamento.getGuiaDARFSimples().getCodigoReceitaTributo();
                    codigoIdentificacaoTributo = guiaPagamento.getGuiaDARFSimples().getCodigoIdentificacaoTributo();
                    periodoApuracao = guiaPagamento.getGuiaDARFSimples().getPeriodoApuracao();
                    valorReceitaBruta = guiaPagamento.getGuiaDARFSimples().getValorReceitaBruta();
                    percentualReceitaBruta = guiaPagamento.getGuiaDARFSimples().getPercentualReceitaBruta();
                    valorGuia = iGuiaArquivoOBN600.getGuiaDARFSimples().getValorPrincipal();
                    valorMulta = guiaPagamento.getGuiaDARFSimples().getValorMulta();
                    valorJuros = guiaPagamento.getGuiaDARFSimples().getValorJuros();
                    autenticacao = guiaPagamento.getNumeroAutenticacao();
                    break;
                case GRU:
                    situacao = guiaPagamento.getSituacaoGuiaPagamento().getDescricao();
                    codigoIndentificador = iGuiaArquivoOBN600.getCodigoIdentificacao();
                    codigoRecolhimento = ((GuiaPagamentoExtra) iGuiaArquivoOBN600).getGuiaGRU().getCodigoRecolhimento();
                    codigoBarrasOrPessoa = iGuiaArquivoOBN600.getPessoa().getNome();
                    valorGuia = ((GuiaPagamentoExtra) iGuiaArquivoOBN600).getGuiaGRU().getValorPrincipal();
                    valorPago = iGuiaArquivoOBN600.getValorTotalPorGuia();
                    break;
            }

        }
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getCodigoBarrasOrPessoa() {
        return codigoBarrasOrPessoa;
    }

    public void setCodigoBarrasOrPessoa(String codigoBarrasOrPessoa) {
        this.codigoBarrasOrPessoa = codigoBarrasOrPessoa;
    }

    public String getPessoaPagamento() {
        return pessoaPagamento;
    }

    public void setPessoaPagamento(String pessoaPagamento) {
        this.pessoaPagamento = pessoaPagamento;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getContaCorrente() {
        return contaCorrente;
    }

    public void setContaCorrente(String contaCorrente) {
        this.contaCorrente = contaCorrente;
    }

    public String getNumeroPagamento() {
        return numeroPagamento;
    }

    public void setNumeroPagamento(String numeroPagamento) {
        this.numeroPagamento = numeroPagamento;
    }

    public String getAutenticacao() {
        return autenticacao;
    }

    public void setAutenticacao(String autenticacao) {
        this.autenticacao = autenticacao;
    }

    public Date getDataPagamentoGuia() {
        return dataPagamentoGuia;
    }

    public void setDataPagamentoGuia(Date dataPagamentoGuia) {
        this.dataPagamentoGuia = dataPagamentoGuia;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public BigDecimal getValorGuia() {
        return valorGuia;
    }

    public void setValorGuia(BigDecimal valorGuia) {
        this.valorGuia = valorGuia;
    }

    public String getCodigoIndentificador() {
        return codigoIndentificador;
    }

    public void setCodigoIndentificador(String codigoIndentificador) {
        this.codigoIndentificador = codigoIndentificador;
    }

    public String getTipoCodigoIdentificador() {
        return tipoCodigoIdentificador;
    }

    public void setTipoCodigoIdentificador(String tipoCodigoIdentificador) {
        this.tipoCodigoIdentificador = tipoCodigoIdentificador;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getCodigoReceita() {
        return codigoReceita;
    }

    public void setCodigoReceita(String codigoReceita) {
        this.codigoReceita = codigoReceita;
    }

    public String getCodigoIdentificacaoTributo() {
        return codigoIdentificacaoTributo;
    }

    public void setCodigoIdentificacaoTributo(String codigoIdentificacaoTributo) {
        this.codigoIdentificacaoTributo = codigoIdentificacaoTributo;
    }

    public Date getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Date competencia) {
        this.competencia = competencia;
    }

    public BigDecimal getValorOutra() {
        return valorOutra;
    }

    public void setValorOutra(BigDecimal valorOutra) {
        this.valorOutra = valorOutra;
    }

    public BigDecimal getAtualizacaoMonetaria() {
        return atualizacaoMonetaria;
    }

    public void setAtualizacaoMonetaria(BigDecimal atualizacaoMonetaria) {
        this.atualizacaoMonetaria = atualizacaoMonetaria;
    }

    public String getNumeroReferencia() {
        return numeroReferencia;
    }

    public void setNumeroReferencia(String numeroReferencia) {
        this.numeroReferencia = numeroReferencia;
    }

    public Date getPeriodoApuracao() {
        return periodoApuracao;
    }

    public void setPeriodoApuracao(Date periodoApuracao) {
        this.periodoApuracao = periodoApuracao;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public BigDecimal getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public BigDecimal getValorJuros() {
        return valorJuros;
    }

    public void setValorJuros(BigDecimal valorJuros) {
        this.valorJuros = valorJuros;
    }

    public BigDecimal getValorReceitaBruta() {
        return valorReceitaBruta;
    }

    public void setValorReceitaBruta(BigDecimal valorReceitaBruta) {
        this.valorReceitaBruta = valorReceitaBruta;
    }

    public BigDecimal getPercentualReceitaBruta() {
        return percentualReceitaBruta;
    }

    public void setPercentualReceitaBruta(BigDecimal percentualReceitaBruta) {
        this.percentualReceitaBruta = percentualReceitaBruta;
    }

    public String getTipoDocPagto() {
        return tipoDocPagto;
    }

    public void setTipoDocPagto(String tipoDocPagto) {
        this.tipoDocPagto = tipoDocPagto;
    }

    private HierarquiaOrganizacionalFacade getHierarquiaOrgazanicionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
