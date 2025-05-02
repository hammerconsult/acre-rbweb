/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OrgaoEmpresa;
import br.com.webpublico.enums.rh.estudoatuarial.TipoRegimePrevidenciarioEstudoAtuarial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author peixe
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecusosHumanos")
@Etiqueta("Averbação de Tempo de Contribuição")
public class AverbacaoTempoServico extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //contrato vigente
    @Obrigatorio
    @Etiqueta("Contrato Servidor")
    @Tabelavel
    @ManyToOne
    private ContratoFP contratoFP;
    //confirmar numero sequencial...
    @Obrigatorio
    @Etiqueta("Número Sequencial")
    @Tabelavel
    private Long numero;
    @Obrigatorio
    @Etiqueta("Empregador")
    @Tabelavel
    private String empregado;
    //requerido - o user tem que digitar.
    @Obrigatorio
    @Etiqueta("Cargo")
    @Tabelavel
    private String cargo;
    @Obrigatorio
    @Etiqueta("Tipo de Documento")
    @Tabelavel
    @ManyToOne
    private TipoDocumento tipoDocumento;
    @Obrigatorio
    @Etiqueta("Número do Documento")
    private Long numeroDocumento;
    @Etiqueta("Data Emissão Documento")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataEmissaoDocumento;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data Início da Averbação")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Data Fim Averbação")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;
    @Etiqueta("Anos")
    private Integer anos;
    private Integer meses;
    private Integer dias;
    @Etiqueta("Sexta parte")
    private Boolean sextaParte;
    @Etiqueta("Adicional Tempo de Serviço")
    private Boolean adicionalTempoServico;
    @Etiqueta("Aposentado")
    private Boolean aposentado;
    //proposito ato legal ato de pessoal
    @Etiqueta("Ato Legal")
    @ManyToOne
    private AtoLegal atoLegal;
    @Etiqueta("Observações")
    @Length(max = 3000)
    private String observacao;
    @Etiqueta("Calcular Automático")
    private Boolean calcularAutomatico;
    @Etiqueta("Data do Cadastro")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataCadastro;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Órgão/Empresa")
    @Enumerated(EnumType.STRING)
    private OrgaoEmpresa orgaoEmpresa;
    @Etiqueta("Tipo de Regime Previdenciário")
    @Enumerated(EnumType.STRING)
    private TipoRegimePrevidenciarioEstudoAtuarial tipoRegPrevidenciarioEstAtua;

    public AverbacaoTempoServico() {
    }

    public Boolean getAdicionalTempoServico() {
        return adicionalTempoServico;
    }

    public void setAdicionalTempoServico(Boolean adicionalTempoServico) {
        this.adicionalTempoServico = adicionalTempoServico;
    }

    public Integer getAnos() {
        return anos;
    }

    public void setAnos(Integer anos) {
        this.anos = anos;
    }

    public Boolean getAposentado() {
        return aposentado;
    }

    public void setAposentado(Boolean aposentado) {
        this.aposentado = aposentado;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Boolean getCalcularAutomatico() {
        return calcularAutomatico;
    }

    public void setCalcularAutomatico(Boolean calcularAutomatico) {
        this.calcularAutomatico = calcularAutomatico;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Date getDataEmissaoDocumento() {
        return dataEmissaoDocumento;
    }

    public void setDataEmissaoDocumento(Date dataEmissaoDocumento) {
        this.dataEmissaoDocumento = dataEmissaoDocumento;
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

    public Integer getDias() {
        return dias;
    }

    public void setDias(Integer dias) {
        this.dias = dias;
    }

    public String getEmpregado() {
        return empregado;
    }

    public void setEmpregado(String empregado) {
        this.empregado = empregado;
    }

    public Integer getMeses() {
        return meses;
    }

    public void setMeses(Integer meses) {
        this.meses = meses;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Long getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(Long numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public Boolean getSextaParte() {
        return sextaParte;
    }

    public void setSextaParte(Boolean sextaParte) {
        this.sextaParte = sextaParte;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public OrgaoEmpresa getOrgaoEmpresa() {
        return orgaoEmpresa;
    }

    public void setOrgaoEmpresa(OrgaoEmpresa orgaoEmpresa) {
        this.orgaoEmpresa = orgaoEmpresa;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public TipoRegimePrevidenciarioEstudoAtuarial getTipoRegPrevidenciarioEstAtua() {
        return tipoRegPrevidenciarioEstAtua;
    }

    public void setTipoRegPrevidenciarioEstAtua(TipoRegimePrevidenciarioEstudoAtuarial tipoRegPrevidenciarioEstAtua) {
        this.tipoRegPrevidenciarioEstAtua = tipoRegPrevidenciarioEstAtua;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.AverbacaoTempoServico[ id=" + id + " ]";
    }
}
