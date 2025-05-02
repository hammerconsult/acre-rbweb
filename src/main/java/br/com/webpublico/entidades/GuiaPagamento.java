package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoGuiaPagamento;
import br.com.webpublico.enums.TipoDocPagto;
import br.com.webpublico.enums.TipoIdentificacaoGuia;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.IGuiaArquivoOBN600;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by RenatoRomanini on 11/05/2015.
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
public class GuiaPagamento extends SuperEntidade implements Serializable, IGuiaArquivoOBN600 {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Pagamento pagamento;
    @ManyToOne(cascade = CascadeType.ALL)
    private GuiaFatura guiaFatura;
    @ManyToOne(cascade = CascadeType.ALL)
    private GuiaConvenio guiaConvenio;
    @ManyToOne(cascade = CascadeType.ALL)
    private GuiaGPS guiaGPS;
    @ManyToOne(cascade = CascadeType.ALL)
    private GuiaDARF guiaDARF;
    @ManyToOne(cascade = CascadeType.ALL)
    private GuiaDARFSimples guiaDARFSimples;
    @ManyToOne(cascade = CascadeType.ALL)
    private GuiaGRU guiaGRU;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoGuiaPagamento situacaoGuiaPagamento;
    private String numeroAutenticacao;
    @Obrigatorio
    @Etiqueta("Pessoa")
    @ManyToOne
    private Pessoa pessoa;
    @Obrigatorio
    @Etiqueta("Tipo de Identificação")
    @Enumerated(EnumType.STRING)
    private TipoIdentificacaoGuia tipoIdentificacaoGuia;
    @Obrigatorio
    @Etiqueta("Código de Indentificação")
    private String codigoIdentificacao;
    @Obrigatorio
    @Etiqueta("Data de Pagamento")
    @Temporal(TemporalType.DATE)
    private Date dataPagamento;


    public GuiaPagamento() {
        super();
    }

    public GuiaPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
        if (this.pagamento.getTipoDocPagto() != null) {
            switch (this.pagamento.getTipoDocPagto()) {
                case FATURA:
                    guiaFatura = new GuiaFatura();
                    break;
                case CONVENIO:
                    guiaConvenio = new GuiaConvenio();
                    break;
                case GPS:
                    guiaGPS = new GuiaGPS();
                    break;
                case DARF:
                    guiaDARF = new GuiaDARF();
                    break;
                case DARF_SIMPLES:
                    guiaDARFSimples = new GuiaDARFSimples();
                    break;
                case GRU:
                    guiaGRU = new GuiaGRU();
                    break;
            }
        }
        situacaoGuiaPagamento = SituacaoGuiaPagamento.ABERTA;
    }

    public String getNumeroAutenticacao() {
        return numeroAutenticacao;
    }

    public void setNumeroAutenticacao(String numeroAutenticacao) {
        this.numeroAutenticacao = numeroAutenticacao;
    }

    public SituacaoGuiaPagamento getSituacaoGuiaPagamento() {
        return situacaoGuiaPagamento;
    }

    public void setSituacaoGuiaPagamento(SituacaoGuiaPagamento situacaoGuiaPagamento) {
        this.situacaoGuiaPagamento = situacaoGuiaPagamento;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public GuiaFatura getGuiaFatura() {
        return guiaFatura;
    }

    public void setGuiaFatura(GuiaFatura guiaFatura) {
        this.guiaFatura = guiaFatura;
    }

    @Override
    public GuiaConvenio getGuiaConvenio() {
        return guiaConvenio;
    }

    public void setGuiaConvenio(GuiaConvenio guiaConvenio) {
        this.guiaConvenio = guiaConvenio;
    }

    @Override
    public GuiaGPS getGuiaGPS() {
        return guiaGPS;
    }

    public void setGuiaGPS(GuiaGPS guiaGPS) {
        this.guiaGPS = guiaGPS;
    }

    @Override
    public GuiaDARF getGuiaDARF() {
        return guiaDARF;
    }

    public void setGuiaDARF(GuiaDARF guiaDARF) {
        this.guiaDARF = guiaDARF;
    }

    @Override
    public GuiaDARFSimples getGuiaDARFSimples() {
        return guiaDARFSimples;
    }

    public void setGuiaDARFSimples(GuiaDARFSimples guiaDARFSimples) {
        this.guiaDARFSimples = guiaDARFSimples;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    //    Guia GPS
    public BigDecimal getSomaGuiaGPS() {
        BigDecimal valor = BigDecimal.ZERO;
        if (this.pagamento.getTipoDocPagto().equals(TipoDocPagto.GPS)) {
            if (!this.pagamento.getGuiaPagamento().isEmpty()) {
                for (GuiaPagamento guia : this.pagamento.getGuiaPagamento()) {
                    valor = valor.add(guia.getGuiaGPS().getValorPrevistoINSS());
                }
            }
        }
        return valor;
    }

    //    Guia Darf
    public BigDecimal getSomaGuiaDARF() {
        BigDecimal valor = BigDecimal.ZERO;
        if (this.pagamento.getTipoDocPagto().equals(TipoDocPagto.DARF)) {
            if (this.pagamento.getGuiaPagamento() != null) {
                for (GuiaPagamento guia : this.pagamento.getGuiaPagamento()) {
                    valor = valor.add(guia.getGuiaDARF().getValorPrincipal());
                }
            }
        }
        return valor;
    }


    //    Guia Darf Simples
    public BigDecimal getSomaGuiaDARFSimples() {
        BigDecimal valor = BigDecimal.ZERO;
        if (this.pagamento.getTipoDocPagto().equals(TipoDocPagto.DARF_SIMPLES)) {
            if (!this.pagamento.getGuiaPagamento().isEmpty()) {
                for (GuiaPagamento guia : this.pagamento.getGuiaPagamento()) {
                    valor = valor.add(guia.getGuiaDARFSimples().getValorPrincipal());
                }
            }
        }
        return valor;
    }

    //    Guia Fatura
    public BigDecimal getSomaGuiaFaturas() {
        BigDecimal valor = BigDecimal.ZERO;
        if (this.pagamento.getTipoDocPagto().equals(TipoDocPagto.FATURA)) {
            if (!this.pagamento.getGuiaPagamento().isEmpty()) {
                for (GuiaPagamento guiaPag : this.pagamento.getGuiaPagamento()) {
                    valor = valor.add(guiaPag.getGuiaFatura().getValorNominal());
                }
            }
        }
        return valor;
    }


    @Override
    public String getNumero() {
        Integer i = pagamento.getGuiaPagamento().indexOf(this) + 1;
        return i.toString();
    }

    @Override
    public TipoDocPagto getTipoDocumento() {
        return pagamento.getTipoDocPagto();
    }

    @Override
    public BigDecimal getValorTotalPorGuia() {
        BigDecimal total = BigDecimal.ZERO;
        if (pagamento.getTipoDocPagto() != null) {
            switch (pagamento.getTipoDocPagto()) {
                case FATURA:
                    total = guiaFatura.getValorNominal().add(guiaFatura.getValorJuros().subtract(guiaFatura.getValorDescontos()));
                    break;
                case CONVENIO:
                    total = guiaConvenio.getValor();
                    break;
                case GPS:
                    total = guiaGPS.getValorPrevistoINSS().add(guiaGPS.getValorOutrasEntidades().add(guiaGPS.getAtualizacaoMonetaria()));
                    break;
                case DARF:
                    total = guiaDARF.getValorPrincipal().add(guiaDARF.getValorMulta().add(guiaDARF.getValorJuros()));
                    break;
                case DARF_SIMPLES:
                    total = guiaDARFSimples.getValorPrincipal().add(guiaDARFSimples.getValorMulta().add(guiaDARFSimples.getValorJuros()));
                    break;
                case GRU:
                    total = guiaGRU.getValorPrincipal();
                    break;
            }
        }
        return total;
    }


    public BigDecimal getValorTotalGuiasPagamento() {
        BigDecimal valor = BigDecimal.ZERO;
        for (GuiaPagamento guiaPag : this.pagamento.getGuiaPagamento()) {
            valor = valor.add(guiaPag.getValorTotalPorGuia());
        }
        return valor;
    }

    public BigDecimal getCalcularDiferencaPagamentoComGuia() {
        return pagamento.getValorFinal().subtract(getValorTotalGuiasPagamento());
    }

    public String getTipoDeGuia(Pagamento pagamento) {
        if (pagamento.getTipoDocPagto() != null) {
            switch (pagamento.getTipoDocPagto()) {
                case FATURA:
                    return "Fatura";
                case CONVENIO:
                    return "Convênio";
                case GPS:
                    return "GPS";
                case DARF:
                    return "Darf";
                case DARF_SIMPLES:
                    return "Darf Simples";
                case GRU:
                    return "GRU";
            }
        }
        return "";
    }

    public Boolean isGuiaTipoQuatro(Pagamento pagamento) {
        Boolean retorno = false;
        if (pagamento.getTipoDocPagto() != null) {
            if (TipoDocPagto.FATURA.equals(pagamento.getTipoDocPagto())
                || TipoDocPagto.CONVENIO.equals(pagamento.getTipoDocPagto())) {
                retorno = true;
            }
        }
        return retorno;
    }

    public Boolean isGuiaTipoCinco(Pagamento pagamento) {
        Boolean retorno = false;
        if (pagamento.getTipoDocPagto() != null) {
            if (TipoDocPagto.GPS.equals(pagamento.getTipoDocPagto())
                || TipoDocPagto.DARF.equals(pagamento.getTipoDocPagto())
                || TipoDocPagto.DARF_SIMPLES.equals(pagamento.getTipoDocPagto())) {
                retorno = true;
            }
        }
        return retorno;
    }


    public Boolean possuiDiferencaValorPagamentoComGuia() {
        if (this.pagamento.getValorFinal().compareTo(getValorTotalGuiasPagamento()) == 0) {
            return false;
        }
        return true;
    }

    @Override
    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    @Override
    public TipoIdentificacaoGuia getTipoIdentificacaoGuia() {
        return tipoIdentificacaoGuia;
    }

    @Override
    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setTipoIdentificacaoGuia(TipoIdentificacaoGuia tipoIdentificacaoGuia) {
        this.tipoIdentificacaoGuia = tipoIdentificacaoGuia;
    }

    @Override
    public String getCodigoIdentificacao() {
        return codigoIdentificacao;
    }

    public void setCodigoIdentificacao(String codigoIdentificacao) {
        this.codigoIdentificacao = codigoIdentificacao;
    }


    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    @Override
    public GuiaGRU getGuiaGRU() {
        return guiaGRU;
    }

    public void setGuiaGRU(GuiaGRU guiaGRU) {
        this.guiaGRU = guiaGRU;
    }
}
