package br.com.webpublico.entidades;

import br.com.webpublico.enums.PercursoAcidente;
import br.com.webpublico.enums.TipoTransporte;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by carlos on 05/10/15.
 */
@Entity
@Audited
@Etiqueta("Acidente de Trajeto")
public class AcidenteTrajeto extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Unidade organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;
    @Etiqueta("Servidor")
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private ContratoFP contratoFP;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private PercursoAcidente percursoAcidente;
    @Etiqueta("Data da Ocorrência")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Column(name = "ocorridoEm")
    private Date ocorrencia;
    @Pesquisavel
    @Obrigatorio
    @Temporal(TemporalType.TIME)
    private Date horario;
    @Pesquisavel
    @Obrigatorio
    @Temporal(TemporalType.TIMESTAMP)
    private Date saidaLocal;
    @Pesquisavel
    @Obrigatorio
    private Double cargaHoraria;
    @Etiqueta("Trajeto do Segurado")
    @Obrigatorio
    private String trajeto;
    @Etiqueta("Meio de Locomoção do Servidor")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoTransporte tipoVeiculo;
    @Etiqueta("Local do Acidente")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String local;
    @Column(name = "mudancaTrajeto")
    @Etiqueta("Mudança de Trajeto")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private Boolean isMudancaTrajeto;
    @Etiqueta("Conhecimento Policial")
    @Obrigatorio
    @Column(name = "conhecimentoPolicial")
    private Boolean isConhecimentoPolicial;
    @Etiqueta("Dispositivo Legal")
    @Obrigatorio
    @Column(name = "dispositivoLegal")
    private Boolean isDispositivoLegal;
    @Etiqueta("Considerações")
    @Obrigatorio
    private String consideracao;

    public AcidenteTrajeto(UnidadeOrganizacional unidadeOrganizacional, ContratoFP contratoFP, Date ocorrencia, Date horario, Date saidaLocal) {
        this.unidadeOrganizacional = new UnidadeOrganizacional();
        this.contratoFP = new ContratoFP();
        this.ocorrencia = new Date();
        this.horario = new Date();
        this.saidaLocal = new Date();
    }

    public AcidenteTrajeto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public PercursoAcidente getPercursoAcidente() {
        return percursoAcidente;
    }

    public void setPercursoAcidente(PercursoAcidente percursoAcidente) {
        this.percursoAcidente = percursoAcidente;
    }

    public Date getOcorrencia() {
        return ocorrencia;
    }

    public void setOcorrencia(Date ocorrencia) {
        this.ocorrencia = ocorrencia;
    }

    public Date getHorario() {
        return horario;
    }

    public void setHorario(Date horario) {
        this.horario = horario;
    }

    public Date getSaidaLocal() {
        return saidaLocal;
    }

    public void setSaidaLocal(Date saidaLocal) {
        this.saidaLocal = saidaLocal;
    }

    public Double getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(Double cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public String getTrajeto() {
        return trajeto;
    }

    public void setTrajeto(String trajeto) {
        this.trajeto = trajeto;
    }

    public TipoTransporte getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(TipoTransporte tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Boolean getIsMudancaTrajeto() {
        return isMudancaTrajeto;
    }

    public void setIsMudancaTrajeto(Boolean isMudancaTrajeto) {
        this.isMudancaTrajeto = isMudancaTrajeto;
    }

    public Boolean getIsConhecimentoPolicial() {
        return isConhecimentoPolicial;
    }

    public void setIsConhecimentoPolicial(Boolean isConhecimentoPolicial) {
        this.isConhecimentoPolicial = isConhecimentoPolicial;
    }

    public Boolean getIsDispositivoLegal() {
        return isDispositivoLegal;
    }

    public void setIsDispositivoLegal(Boolean isDispositivoLegal) {
        this.isDispositivoLegal = isDispositivoLegal;
    }

    public String getConsideracao() {
        return consideracao;
    }

    public void setConsideracao(String consideracao) {
        this.consideracao = consideracao;
    }

    @Override
    public String toString() {
        return contratoFP.getMatriculaFP().getPessoa().getNomeCpfCnpj();
    }
}
