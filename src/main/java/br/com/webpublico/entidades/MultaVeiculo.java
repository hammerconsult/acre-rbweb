/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Munif
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Frota")
@Etiqueta("Multa do Veículo")
public class MultaVeiculo extends SuperEntidade implements PossuidorArquivo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Numero do Auto de Infração")
    private String numeroAutoInfracao;

    @Obrigatorio
    @OneToOne
    @Tabelavel
    @Pesquisavel
    private Veiculo veiculo;

    @Tabelavel
    @Etiqueta("Data Emissão")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Pesquisavel
    private Date emitidaEm;

    @Etiqueta("Valor")
    @Tabelavel
    @Obrigatorio
    @Monetario
    private BigDecimal valor;

    @Etiqueta("Local")
    @Obrigatorio
    private String localMulta;

    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private Motorista motorista;

    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    private Cidade cidade;

    @ManyToOne
    @Etiqueta("Tipo da Multa")
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    private TipoMultaVeiculo tipoMulta;

    @Etiqueta("Observações")
    private String observacao;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;
    private Boolean teveContestacao;
    private Date dataContestacao;

    @Invisivel
    @OneToMany(mappedBy = "multaVeiculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JulgamentoMultaVeiculo> julgamentosMultaVeiculo;

    public MultaVeiculo() {
        teveContestacao = Boolean.FALSE;
    }

    public MultaVeiculo(MultaVeiculo multaVeiculo, HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.setId(multaVeiculo.getId());
        this.setNumeroAutoInfracao(multaVeiculo.getNumeroAutoInfracao());
        this.setVeiculo(multaVeiculo.getVeiculo());
        this.setMotorista(multaVeiculo.getMotorista());
        this.setEmitidaEm(multaVeiculo.getEmitidaEm());
        this.setCidade(multaVeiculo.getCidade());
        this.setValor(multaVeiculo.getValor());
        this.setTipoMulta(multaVeiculo.getTipoMulta());
        this.setHierarquiaOrganizacional(hierarquiaOrganizacional);
    }

    public Long getId() {
        return id;
    }

    public String getNumeroAutoInfracao() {
        return numeroAutoInfracao;
    }

    public void setNumeroAutoInfracao(String numeroAutoInfracao) {
        this.numeroAutoInfracao = numeroAutoInfracao;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public Date getEmitidaEm() {
        return emitidaEm;
    }

    public void setEmitidaEm(Date emitidaEm) {
        this.emitidaEm = emitidaEm;
    }

    public String getLocalMulta() {
        return localMulta;
    }

    public void setLocalMulta(String localMulta) {
        this.localMulta = localMulta;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Motorista getMotorista() {
        return motorista;
    }

    public void setMotorista(Motorista motorista) {
        this.motorista = motorista;
    }

    public TipoMultaVeiculo getTipoMulta() {
        return tipoMulta;
    }

    public void setTipoMulta(TipoMultaVeiculo tipoMulta) {
        this.tipoMulta = tipoMulta;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Boolean getTeveContestacao() {
        return teveContestacao;
    }

    public void setTeveContestacao(Boolean teveContestacao) {
        this.teveContestacao = teveContestacao;
    }

    public Date getDataContestacao() {
        return dataContestacao;
    }

    public void setDataContestacao(Date dataContestacao) {
        this.dataContestacao = dataContestacao;
    }

    public List<JulgamentoMultaVeiculo> getJulgamentosMultaVeiculo() {
        return julgamentosMultaVeiculo;
    }

    public void setJulgamentosMultaVeiculo(List<JulgamentoMultaVeiculo> julgamentosMultaVeiculo) {
        this.julgamentosMultaVeiculo = julgamentosMultaVeiculo;
    }

    @Override
    public String toString() {
        return this.tipoMulta.getDescricao() + " em " + new SimpleDateFormat("dd/MM/yyyy").format(this.emitidaEm);
    }

}
