/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Munif
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Frota")
@Etiqueta("Abastecimento")
public class AbastecimentoObjetoFrota extends SuperEntidade implements PossuidorArquivo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer ano;
    private Integer numero;

    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @Transient
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número do Abastecimento")
    private String numeroAbastecimento;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número do Abastecimento Manual")
    private String numeroAbastecimentoManual;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Cota")
    @ManyToOne
    private CotaAbastecimento cotaAbastecimento;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo")
    @Enumerated(EnumType.STRING)
    private TipoObjetoFrota tipoObjetoFrota;

    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Veículo/Equipamento")
    private ObjetoFrota objetoFrota;

    @Etiqueta("Data do Abastecimento")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    private Date dataAbastecimento;

    @Etiqueta("Quilometragem Atual")
    private BigDecimal km;

    @ManyToOne
    @Etiqueta("Motorista")
    @Tabelavel
    @Pesquisavel
    private Motorista motorista;

    @ManyToOne
    @Etiqueta("Operador")
    @Tabelavel
    @Pesquisavel
    private PessoaFisica operador;

    @Obrigatorio
    @Etiqueta("Hora(s) de Uso Atual")
    private BigDecimal horasUso;

    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "abastecimentoObjetoFrota")
    private List<ItemAbastecObjetoFrota> itensAbastecimentoObjetoFrota;

    @Etiqueta("Data de Emissão")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Pesquisavel
    private Date dataEmissao;

    @Etiqueta("Data de Validade")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Pesquisavel
    private Date dataValidade;
    private String trecho;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    @Etiqueta("Arquivo")
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public AbastecimentoObjetoFrota() {
    }

    public AbastecimentoObjetoFrota(AbastecimentoObjetoFrota abastecimento, HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.setId(abastecimento.getId());
        this.setAno(abastecimento.getAno());
        this.setNumero(abastecimento.getNumero());
        this.setNumeroAbastecimento(abastecimento.getNumeroAbastecimento());
        this.setNumeroAbastecimentoManual(abastecimento.getNumeroAbastecimentoManual());
        this.setTipoObjetoFrota(abastecimento.getTipoObjetoFrota());
        this.setObjetoFrota(abastecimento.getObjetoFrota());
        this.setDataAbastecimento(abastecimento.getDataAbastecimento());
        this.setMotorista(abastecimento.getMotorista());
        this.setOperador(abastecimento.getOperador());
        this.setHierarquiaOrganizacional(hierarquiaOrganizacional);
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CotaAbastecimento getCotaAbastecimento() {
        return cotaAbastecimento;
    }

    public void setCotaAbastecimento(CotaAbastecimento cotaAbastecimento) {
        this.cotaAbastecimento = cotaAbastecimento;
    }

    public ObjetoFrota getObjetoFrota() {
        return objetoFrota;
    }

    public void setObjetoFrota(ObjetoFrota objetoFrota) {
        this.objetoFrota = objetoFrota;
    }

    public BigDecimal getKm() {
        return km;
    }

    public void setKm(BigDecimal km) {
        this.km = km;
    }

    public Motorista getMotorista() {
        return motorista;
    }

    public void setMotorista(Motorista motorista) {
        this.motorista = motorista;
    }

    public PessoaFisica getOperador() {
        return operador;
    }

    public void setOperador(PessoaFisica operador) {
        this.operador = operador;
    }

    public Date getDataAbastecimento() {
        return dataAbastecimento;
    }

    public void setDataAbastecimento(Date dataAbastecimento) {
        this.dataAbastecimento = dataAbastecimento;
    }

    public BigDecimal getHorasUso() {
        return horasUso;
    }

    public void setHorasUso(BigDecimal horasUso) {
        this.horasUso = horasUso;
    }

    public TipoObjetoFrota getTipoObjetoFrota() {
        return tipoObjetoFrota;
    }

    public void setTipoObjetoFrota(TipoObjetoFrota tipoObjetoFrota) {
        this.tipoObjetoFrota = tipoObjetoFrota;
    }

    public List<ItemAbastecObjetoFrota> getItensAbastecimentoObjetoFrota() {
        return itensAbastecimentoObjetoFrota;
    }

    public void setItensAbastecimentoObjetoFrota(List<ItemAbastecObjetoFrota> itensAbastecimentoObjetoFrota) {
        this.itensAbastecimentoObjetoFrota = itensAbastecimentoObjetoFrota;
    }

    public String getTrecho() {
        return trecho;
    }

    public void setTrecho(String trecho) {
        this.trecho = trecho;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getNumeroAbastecimento() {
        if (ano != null && numero != null) {
            numeroAbastecimento = numero + "/" + ano;
        }
        return numeroAbastecimento;
    }

    public void setNumeroAbastecimento(String numeroAbastecimento) {
        this.numeroAbastecimento = numeroAbastecimento;
    }

    public String getNumeroAbastecimentoManual() {
        return numeroAbastecimentoManual;
    }

    public void setNumeroAbastecimentoManual(String numeroAbastecimentoManual) {
        this.numeroAbastecimentoManual = numeroAbastecimentoManual;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Date getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(Date dataValidade) {
        this.dataValidade = dataValidade;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    @Override
    public String toString() {
        return numero == null ? "Abastecimento ainda não gravado" : "Abastecimento código " + numero;
    }

}
