package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoAquisicaoObjetoFrota;
import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 05/09/14
 * Time: 10:46
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Frota")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ObjetoFrota extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue
    private Long id;

    @Pesquisavel
    @Tabelavel(ordemApresentacao = 0)
    @Etiqueta("Órgão/Entidade/Fundo")
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;

    @Pesquisavel
    @Etiqueta("Unidade Organizacional Responsável")
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacionalResponsavel;

    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacionalResp;

    @Pesquisavel
    @Tabelavel(ordemApresentacao = 1)
    @Etiqueta("Identificação")
    @Obrigatorio
    private String identificacao;

    @Obrigatorio
    @Etiqueta("Data de Aquisição")
    @Tabelavel(ordemApresentacao = 2)
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date dataAquisicao;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Cedido")
    private Boolean cedido;

    @Pesquisavel
    @Etiqueta("Cedido Por")
    private String cedidoPor;

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Frota")
    @Tabelavel(ordemApresentacao = 3)
    @Pesquisavel
    private TipoAquisicaoObjetoFrota tipoAquisicaoObjetoFrota;

    @Pesquisavel
    @Etiqueta("Bem Patrimonial")
    @Tabelavel(ordemApresentacao = 4)
    @OneToOne
    private Bem bem;

    @Pesquisavel
    @Etiqueta("Contrato")
    @Tabelavel(ordemApresentacao = 5)
    @ManyToOne
    private Contrato contrato;

    @Pesquisavel
    @Etiqueta("Descrição")
    @Tabelavel(ordemApresentacao = 6)
    private String descricao;

    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Etiqueta("Data Final da Garantia")
    private Date dataFinalGarantia;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Objeto Frota")
    private TipoObjetoFrota tipoObjetoFrota;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Etiqueta("Observações")
    @Length(maximo = 255)
    private String observacao;

    @OneToMany(mappedBy = "objetoFrota", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UnidadeObjetoFrota> unidades;

    @Transient
    private BaixaObjetoFrota baixaObjetoFrota;

    protected ObjetoFrota() {
        this.unidades = Lists.newArrayList();
        this.cedido = Boolean.FALSE;
    }

    public List<UnidadeObjetoFrota> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<UnidadeObjetoFrota> unidades) {
        this.unidades = unidades;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public Date getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(Date dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataFinalGarantia() {
        return dataFinalGarantia;
    }

    public void setDataFinalGarantia(Date dataFinalGarantia) {
        this.dataFinalGarantia = dataFinalGarantia;
    }

    public TipoObjetoFrota getTipoObjetoFrota() {
        return tipoObjetoFrota;
    }

    public void setTipoObjetoFrota(TipoObjetoFrota tipoObjetoFrota) {
        this.tipoObjetoFrota = tipoObjetoFrota;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public TipoAquisicaoObjetoFrota getTipoAquisicaoObjetoFrota() {
        return tipoAquisicaoObjetoFrota;
    }

    public void setTipoAquisicaoObjetoFrota(TipoAquisicaoObjetoFrota tipoAquisicaoObjetoFrota) {
        this.tipoAquisicaoObjetoFrota = tipoAquisicaoObjetoFrota;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        if (hierarquiaOrganizacional != null) {
            unidadeOrganizacional = hierarquiaOrganizacional.getSubordinada();
        } else {
            unidadeOrganizacional = null;
        }
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Boolean getCedido() {
        return cedido;
    }

    public void setCedido(Boolean cedido) {
        this.cedido = cedido;
    }

    public String getCedidoPor() {
        return cedidoPor;
    }

    public void setCedidoPor(String cedidoPor) {
        this.cedidoPor = cedidoPor;
    }

    public BaixaObjetoFrota getBaixaObjetoFrota() {
        return baixaObjetoFrota;
    }

    public void setBaixaObjetoFrota(BaixaObjetoFrota baixaObjetoFrota) {
        this.baixaObjetoFrota = baixaObjetoFrota;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalResponsavel() {
        if (hierarquiaOrganizacionalResponsavel != null) {
            unidadeOrganizacionalResp = hierarquiaOrganizacionalResponsavel.getSubordinada();
        }
        return hierarquiaOrganizacionalResponsavel;
    }

    public void setHierarquiaOrganizacionalResponsavel(HierarquiaOrganizacional hierarquiaOrganizacionalResponsavel) {
        if (hierarquiaOrganizacionalResponsavel != null) {
            unidadeOrganizacionalResp = hierarquiaOrganizacionalResponsavel.getSubordinada();
        } else {
            unidadeOrganizacionalResp = null;
        }

        this.hierarquiaOrganizacionalResponsavel = hierarquiaOrganizacionalResponsavel;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalResp() {
        return unidadeOrganizacionalResp;
    }

    public void setUnidadeOrganizacionalResp(UnidadeOrganizacional unidadeOrganizacionalResp) {
        this.unidadeOrganizacionalResp = unidadeOrganizacionalResp;
    }

    public Boolean isCedido() {
        return this.getCedido() == null ? false : this.getCedido();
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ObjetoFrota)) {
            return false;
        }
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        if (tipoAquisicaoObjetoFrota != null) {
            if (tipoAquisicaoObjetoFrota.equals(TipoAquisicaoObjetoFrota.ALUGADO)) {
                return descricao;
            } else if (tipoAquisicaoObjetoFrota.equals(TipoAquisicaoObjetoFrota.PROPRIO)) {
                return bem.toString();
            }
        }
        return "";
    }

    public BigDecimal getHoraUsoAtualEquipamento() {
        BigDecimal kmAtual = BigDecimal.ZERO;
        if (getTipoObjetoFrota().isEquipamento()) {
            if (this instanceof Equipamento) {
                Equipamento equipamento = (Equipamento) this;
                if (equipamento.getHoraPercorrida() != null) {
                    kmAtual = equipamento.getHoraPercorrida().getHoraAtual();
                }
            }
        }
        return kmAtual;
    }

    public BigDecimal getKmAtualVeiculo() {
        BigDecimal kmAtual = BigDecimal.ZERO;
        if (getTipoObjetoFrota().isVeiculo()) {
            if (this instanceof Veiculo) {
                Veiculo veiculo = (Veiculo) this;
                if (veiculo.getKmPercorrido() != null) {
                    kmAtual = veiculo.getKmPercorrido().getKmAtual();
                }
            }
        }
        return kmAtual;
    }

    public Veiculo getVeiculo() {
        return (Veiculo) this;
    }

    public Equipamento getEquipamento() {
        return (Equipamento) this;
    }

    public void validarVigencia(){

    }
}
