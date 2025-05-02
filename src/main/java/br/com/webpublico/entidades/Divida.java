/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.PermissaoEmissaoDAM;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.CacheableTributario;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author munif
 */
@GrupoDiagrama(nome = "Tributario")
@Entity
@Audited
@Etiqueta("Dívida")
public class Divida extends CacheableTributario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;
    @Pesquisavel
    @Etiqueta("Tipo de Cadastro")
    @Enumerated(EnumType.STRING)
    private TipoCadastroTributario tipoCadastro;
    @Pesquisavel
    @Etiqueta("Entidade")
    @ManyToOne
    private Entidade entidade;
    @Pesquisavel
    @Etiqueta("Início da Vigência")
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date inicioVigencia;
    @Pesquisavel
    @Etiqueta("Final da Vigência")
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Etiqueta("Forma de Cobrança da Dívida")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "divida", orphanRemoval = true)
    private List<FormaCobrancaDivida> formaCobrancaDividas;
    @Etiqueta("Opções de Pagamento da Dívida")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "divida", orphanRemoval = true)
    private List<OpcaoPagamentoDivida> opcaoPagamentosDivida;
    @OneToOne
    private Divida divida;
    private Boolean isDividaAtiva;
    private Boolean isParcelamento;
    private String nrLivroDividaAtiva;
    @Etiqueta("Multa Acessória")
    private BigDecimal multaAcessoria;
    @OneToMany(mappedBy = "divida", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DividaAcrescimo> acrescimos;
    @OneToOne
    @Etiqueta("Configuração do DAM")
    private ConfiguracaoDAM configuracaoDAM;
    private Integer codigo;
    private Integer ordemApresentacao;
    @Enumerated(EnumType.STRING)
    private PermissaoEmissaoDAM permissaoEmissaoDAM;
    private Boolean permiteRevisao;
    private Boolean gerarQrCodePix;
    private Integer anosPrescricao;
    @Enumerated(EnumType.STRING)
    private ConfiguracaoAcrescimos.DataBaseCalculo dataBasePrescricao;

    public Divida() {
        super();
        opcaoPagamentosDivida = Lists.newArrayList();
        formaCobrancaDividas = Lists.newArrayList();
        acrescimos = Lists.newArrayList();
        ordemApresentacao = 1;
        permissaoEmissaoDAM = PermissaoEmissaoDAM.HABILITA;
        permiteRevisao = Boolean.FALSE;
        gerarQrCodePix = Boolean.FALSE;
    }

    public Divida(Divida divida, Long id) {
        setId(id);
        this.divida = divida.getDivida();
        this.setDescricao(divida.descricao);
    }

    public Divida(Long id) {
        setId(id);
    }

    public Divida(BigDecimal id, String descricao) {
        setId(id.longValue());
        setDescricao(descricao);
    }

    public List<DividaAcrescimo> getAcrescimos() {
        return acrescimos;
    }

    public void setAcrescimos(List<DividaAcrescimo> acrescimos) {
        this.acrescimos = acrescimos;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Boolean getIsDividaAtiva() {
        return isDividaAtiva != null ? isDividaAtiva : false;
    }

    public void setIsDividaAtiva(Boolean isDividaAtiva) {
        this.isDividaAtiva = isDividaAtiva;
    }

    public Boolean getIsParcelamento() {
        return isParcelamento != null ? isParcelamento : false;
    }

    public void setIsParcelamento(Boolean isParcelamento) {
        isParcelamento = isParcelamento;
    }

    public String getNrLivroDividaAtiva() {
        return nrLivroDividaAtiva;
    }

    public void setNrLivroDividaAtiva(String nrLivroDividaAtiva) {
        this.nrLivroDividaAtiva = nrLivroDividaAtiva;
    }

    public BigDecimal getMultaAcessoria() {
        return multaAcessoria;
    }

    public void setMultaAcessoria(BigDecimal multaAcessoria) {
        this.multaAcessoria = multaAcessoria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public TipoCadastroTributario getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastroTributario tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public List<FormaCobrancaDivida> getFormaCobrancaDividas() {
        return formaCobrancaDividas;
    }

    public void setFormaCobrancaDividas(List<FormaCobrancaDivida> formaCobrancaDividas) {
        this.formaCobrancaDividas = formaCobrancaDividas;
    }

    public List<OpcaoPagamentoDivida> getOpcaoPagamentosDivida() {
        return opcaoPagamentosDivida;
    }

    public void setOpcaoPagamentosDivida(List<OpcaoPagamentoDivida> opcaoPagamentosDivida) {
        this.opcaoPagamentosDivida = opcaoPagamentosDivida;
    }

    public ConfiguracaoDAM getConfiguracaoDAM() {
        return configuracaoDAM;
    }

    public void setConfiguracaoDAM(ConfiguracaoDAM configuracaoDAM) {
        this.configuracaoDAM = configuracaoDAM;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getOrdemApresentacao() {
        return ordemApresentacao;
    }

    public void setOrdemApresentacao(Integer ordemApresentacao) {
        this.ordemApresentacao = ordemApresentacao;
    }

    public PermissaoEmissaoDAM getPermissaoEmissaoDAM() {
        return permissaoEmissaoDAM;
    }

    public void setPermissaoEmissaoDAM(PermissaoEmissaoDAM permissaoEmissaoDAM) {
        this.permissaoEmissaoDAM = permissaoEmissaoDAM;
    }

    public Boolean getPermiteRevisao() {
        return permiteRevisao != null ? permiteRevisao : Boolean.FALSE;
    }

    public void setPermiteRevisao(Boolean permiteRevisao) {
        this.permiteRevisao = permiteRevisao;
    }

    public Boolean getGerarQrCodePix() {
        return gerarQrCodePix != null ? gerarQrCodePix : Boolean.FALSE;
    }

    public void setGerarQrCodePix(Boolean gerarQrCodePix) {
        this.gerarQrCodePix = gerarQrCodePix;
    }

    public Integer getAnosPrescricao() {
        return anosPrescricao;
    }

    public void setAnosPrescricao(Integer anosPrescricao) {
        this.anosPrescricao = anosPrescricao;
    }

    public ConfiguracaoAcrescimos.DataBaseCalculo getDataBasePrescricao() {
        return dataBasePrescricao;
    }

    public void setDataBasePrescricao(ConfiguracaoAcrescimos.DataBaseCalculo dataBasePrescricao) {
        this.dataBasePrescricao = dataBasePrescricao;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Divida)) {
            return false;
        }
        Divida other = (Divida) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return descricao;
    }

    @Override
    public Object getIdentificacao() {
        return this.id;
    }

    public static void validarDividaParaAdicaoEmLista(Divida divida, List<Divida> dividas) {
        ValidacaoException ve = new ValidacaoException();
        if (divida == null || divida.getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione uma dívida para adicionar");
        } else if (dividas.contains(divida)) {
            ve.adicionarMensagemDeCampoObrigatorio("Essa Dívida já foi selecionada.");
        } else {
            for (Divida di : dividas) {
                if (!di.getConfiguracaoDAM().equals(di.getConfiguracaoDAM())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Essa Dívida tem a configuração de código de barras diferente das outras selecionadas. Apenas uma configuração de código de barras deve compor a Mala Direta.");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    public String toStringAutoComplete() {
        if (descricao != null) {
            return codigo + " - " + descricao;
        }
        return "";
    }
}
