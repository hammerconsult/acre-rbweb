/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.FinalidadeCedenciaContratoFP;
import br.com.webpublico.enums.TipoCedenciaContratoFP;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.IHistoricoEsocial;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author andre
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Cedência de Contrato FP")
@Audited
@Entity

public class CedenciaContratoFP implements Serializable, IHistoricoEsocial {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @ColunaAuditoriaRH(posicao = 2)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data Cedência")
    @Obrigatorio
    private Date dataCessao;
    @ColunaAuditoriaRH(posicao = 3)
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Retorno Prevista")
    private Date dataRetorno;
    @Tabelavel
    @Etiqueta("Ato Legal")
    @ManyToOne
    @Obrigatorio
    private AtoLegal atoLegal;
    @ColunaAuditoriaRH
    @ManyToOne
    @Tabelavel
    @Etiqueta("Servidor")
    @Obrigatorio
    private ContratoFP contratoFP;
    @ColunaAuditoriaRH(posicao = 4)
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta("Tipo da Cedência do Contrato FP")
    private TipoCedenciaContratoFP tipoContratoCedenciaFP;
    @Etiqueta("Cedente")
    @ManyToOne
    private UnidadeExterna cedente;
    @Tabelavel
    @Etiqueta("Cessionário")
    @ManyToOne
    private UnidadeExterna cessionario;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Unidade Organizacional")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta("Finalidade da Cedência do Contrato FP")
    private FinalidadeCedenciaContratoFP finalidadeCedenciaContratoFP;
    @Temporal(TemporalType.DATE)
    private Date dataCadastro;
    @Etiqueta("Fundamentação Legal")
    private String fundamentacaoLegal;
    @Etiqueta("Descrição Conforme D.O.")
    @Pesquisavel
    private String descricao;
    @ManyToOne
    private Cargo cargo;
    @Invisivel
    @OneToOne
    private Arquivo anexo;
    @Invisivel
    @Transient
    private HierarquiaOrganizacional ho;
    private String cargoExterno;
    @Transient
    private Integer cedido;
    @Transient
    private LotacaoFuncional localTrabalhoAtual;
    private Boolean enviadoIniCessaoESocial;
    private Boolean enviadoFimCessaoESocial;

    public HierarquiaOrganizacional getHo() {
        return ho;
    }

    public String getCargoExterno() {
        return cargoExterno;
    }

    public void setCargoExterno(String cargoExterno) {
        this.cargoExterno = cargoExterno;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public String getFundamentacaoLegal() {
        return fundamentacaoLegal;
    }

    public void setFundamentacaoLegal(String fundamentacaoLegal) {
        this.fundamentacaoLegal = fundamentacaoLegal;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Arquivo getAnexo() {
        return anexo;
    }

    public void setAnexo(Arquivo anexo) {
        this.anexo = anexo;
    }

    public void setHo(HierarquiaOrganizacional ho) {
        if (ho != null && ho.getSubordinada() != null) {
            setUnidadeOrganizacional(ho.getSubordinada());
        }
        this.ho = ho;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public UnidadeExterna getCedente() {
        return cedente;
    }

    public void setCedente(UnidadeExterna cedente) {
        this.cedente = cedente;
    }

    public UnidadeExterna getCessionario() {
        return cessionario;
    }

    public void setCessionario(UnidadeExterna cessionario) {
        this.cessionario = cessionario;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Date getDataCessao() {
        return dataCessao;
    }

    public void setDataCessao(Date dataCessao) {
        this.dataCessao = dataCessao;
    }

    public Date getDataRetorno() {
        return dataRetorno;
    }

    public void setDataRetorno(Date dataRetorno) {
        this.dataRetorno = dataRetorno;
    }

    public TipoCedenciaContratoFP getTipoContratoCedenciaFP() {
        return tipoContratoCedenciaFP;
    }

    public void setTipoContratoCedenciaFP(TipoCedenciaContratoFP tipoContratoCedenciaFP) {
        this.tipoContratoCedenciaFP = tipoContratoCedenciaFP;
    }

    public FinalidadeCedenciaContratoFP getFinalidadeCedenciaContratoFP() {
        return finalidadeCedenciaContratoFP;
    }

    public void setFinalidadeCedenciaContratoFP(FinalidadeCedenciaContratoFP finalidadeCedenciaContratoFP) {
        this.finalidadeCedenciaContratoFP = finalidadeCedenciaContratoFP;
    }

    public Integer getCedido() {
        return cedido;
    }

    public void setCedido(Integer cedido) {
        this.cedido = cedido;
    }

    public LotacaoFuncional getLocalTrabalhoAtual() {
        return localTrabalhoAtual;
    }

    public void setLocalTrabalhoAtual(LotacaoFuncional localTrabalhoAtual) {
        this.localTrabalhoAtual = localTrabalhoAtual;
    }

    public Boolean getEnviadoIniCessaoESocial() {
        return enviadoIniCessaoESocial;
    }

    public void setEnviadoIniCessaoESocial(Boolean enviadoIniCessaoESocial) {
        this.enviadoIniCessaoESocial = enviadoIniCessaoESocial;
    }

    public Boolean getEnviadoFimCessaoESocial() {
        return enviadoFimCessaoESocial;
    }

    public void setEnviadoFimCessaoESocial(Boolean enviadoFimCessaoESocial) {
        this.enviadoFimCessaoESocial = enviadoFimCessaoESocial;
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
        if (!(object instanceof CedenciaContratoFP)) {
            return false;
        }
        CedenciaContratoFP other = (CedenciaContratoFP) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Data da cessão : " + dataCessao + " - Data de Retorno Prevista : " + dataRetorno == null ? " " : DataUtil.getDataFormatada(dataRetorno) +" - Servidor : " + contratoFP + " - Ato Legal : " + atoLegal;
    }

    public String servidorCessao() {
        return contratoFP +
            ("Data da cessão: "+ (dataCessao == null ? " ": DataUtil.getDataFormatada(dataCessao) +
            " - Data de Retorno Prevista : " + (dataRetorno== null ? " ": DataUtil.getDataFormatada(dataRetorno))));
    }

    @Override
    public String getDescricaoCompleta() {
        return contratoFP.toString();
    }

    @Override
    public String getIdentificador() {
        return this.getId().toString();
    }
}
