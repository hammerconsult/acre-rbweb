package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.saudeservidor.RiscoOcupacionalASO;
import br.com.webpublico.enums.Lesoes;
import br.com.webpublico.enums.SituacaoASO;
import br.com.webpublico.enums.TipoExame;
import br.com.webpublico.enums.TipoSanguineo;
import br.com.webpublico.interfaces.IHistoricoEsocial;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by carlos on 20/06/15.
 */
@Entity
@Audited
@Etiqueta("ASO - Atestado de Saúde Ocupacional")
public class ASO extends SuperEntidade implements Serializable, IHistoricoEsocial {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
//===================================================DADOS DO SERVIDOR==================================================
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Emissão ASO")
    private Date dataEmissaoASO;

    @Obrigatorio
    @Etiqueta("Tipo de exame")
    @Enumerated(EnumType.STRING)
    private TipoExame tipoExame;
    @Obrigatorio
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoASO situacao;
    @Etiqueta("Apto a outra função")
    private String aptoOutraFuncao;
    @ManyToOne
    @Etiqueta("Unidade organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;
    @Etiqueta("Servidor")
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private ContratoFP contratoFP;
//====================================================DADOS DO EXAME====================================================
    @Etiqueta("Peso em Kg")
    private String peso;
    @Etiqueta("Estatura em M")
    private String estatura;
    @Etiqueta("Tipo sanguineo")
    @Enumerated(EnumType.STRING)
    private TipoSanguineo tipoSanguineo;
    @Etiqueta("Pressão arterial")
    private String pressao;
    @Etiqueta("Fumante")
    private String fumante;
    @Etiqueta("Número de cigarros (caso seja fumante)")
    private Integer numeroCigarro;
    @Etiqueta("Já fumou")
    private String jaFumou;
    @Etiqueta("Parou à")
    private Integer parouA;
    @Etiqueta("Faz uso de bebidas alcoólicas")
    private String bebidaAlcoolica;
    @Etiqueta("Alergias")
    private String alergias;
    @Etiqueta("Fator desencadeante")
    private String fatorDesencadeante;
    @Etiqueta("Doenças anteriores")
    private String doencaAnterior;
    @Etiqueta("Doenças familiares")
    private String doencaFamiliar;
    @Etiqueta("Cirurgia e internações")
    private String cirurgiaInternacao;
    @Etiqueta("Lesões anteriores")
    @Enumerated(EnumType.STRING)
    private Lesoes lesao;
    @Etiqueta("Em caso de emergência, avisar")
    private String avisar;
    @Etiqueta("Telefone")
    private String telefone;
    @Etiqueta("Médico")
    @Obrigatorio
    @ManyToOne
    private Medico medico;
    @Etiqueta("Telefone do médico")
    private String telefoneMedico;
    @Etiqueta("Data do retorno")
    @Temporal(TemporalType.DATE)
    private Date dataRetorno;
    @OneToMany(mappedBy = "aso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExameComplementar> exameComplementares;

//========================================DADOS DO RISCO OCUPACIONAL====================================================
    @OneToMany(mappedBy = "aso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RiscoOcupacionalASO> riscos;

//================================================DADOS DO EQUIPAMENTO==================================================
    @OneToMany(mappedBy = "aso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EquipamentoPCMSO> equipamentosPCMSO;

//================================================CONSTRUTOR============================================================

    public ASO() {
        exameComplementares = new ArrayList<ExameComplementar>();
        riscos = Lists.newArrayList();
        equipamentosPCMSO = Lists.newArrayList();

    }

//====================================================GETTERS E SETTERS DADOS SERVIDOR==================================


    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public TipoExame getTipoExame() {
        return tipoExame;
    }

    public void setTipoExame(TipoExame tipoExame) {
        this.tipoExame = tipoExame;
    }

    public SituacaoASO getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoASO situacao) {
        this.situacao = situacao;
    }

    public String getAptoOutraFuncao() {
        return aptoOutraFuncao;
    }

    public void setAptoOutraFuncao(String aptoOutraFuncao) {
        this.aptoOutraFuncao = aptoOutraFuncao;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    ===================================================GETTERS E SETTERS EXAME========================================


    public TipoSanguineo getTipoSanguineo() {
        return tipoSanguineo;
    }

    public void setTipoSanguineo(TipoSanguineo tipoSanguineo) {
        this.tipoSanguineo = tipoSanguineo;
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    public String getAvisar() {
        return avisar;
    }

    public void setAvisar(String avisar) {
        this.avisar = avisar;
    }

    public String getBebidaAlcoolica() {
        return bebidaAlcoolica;
    }

    public void setBebidaAlcoolica(String bebidaAlcoolica) {
        this.bebidaAlcoolica = bebidaAlcoolica;
    }

    public String getCirurgiaInternacao() {
        return cirurgiaInternacao;
    }

    public void setCirurgiaInternacao(String cirurgiaInternacao) {
        this.cirurgiaInternacao = cirurgiaInternacao;
    }

    public Date getDataRetorno() {
        return dataRetorno;
    }

    public void setDataRetorno(Date dataRetorno) {
        this.dataRetorno = dataRetorno;
    }

    public String getDoencaAnterior() {
        return doencaAnterior;
    }

    public void setDoencaAnterior(String doencaAnterior) {
        this.doencaAnterior = doencaAnterior;
    }

    public String getDoencaFamiliar() {
        return doencaFamiliar;
    }

    public void setDoencaFamiliar(String doencaFamiliar) {
        this.doencaFamiliar = doencaFamiliar;
    }

    public String getEstatura() {
        return estatura;
    }

    public void setEstatura(String estatura) {
        this.estatura = estatura;
    }

    public String getFatorDesencadeante() {
        return fatorDesencadeante;
    }

    public void setFatorDesencadeante(String fatorDesencadeante) {
        this.fatorDesencadeante = fatorDesencadeante;
    }

    public String getFumante() {
        return fumante;
    }

    public void setFumante(String fumante) {
        this.fumante = fumante;
    }

    public String getJaFumou() {
        return jaFumou;
    }

    public void setJaFumou(String jaFumou) {
        this.jaFumou = jaFumou;
    }

    public Lesoes getLesao() {
        return lesao;
    }

    public void setLesao(Lesoes lesao) {
        this.lesao = lesao;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Integer getNumeroCigarro() {
        return numeroCigarro;
    }

    public void setNumeroCigarro(Integer numeroCigarro) {
        this.numeroCigarro = numeroCigarro;
    }

    public Integer getParouA() {
        return parouA;
    }

    public void setParouA(Integer parouA) {
        this.parouA = parouA;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getPressao() {
        return pressao;
    }

    public void setPressao(String pressao) {
        this.pressao = pressao;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTelefoneMedico() {
        return telefoneMedico;
    }

    public void setTelefoneMedico(String telefoneMedico) {
        this.telefoneMedico = telefoneMedico;
    }

    public List<ExameComplementar> getExameComplementares() {
        return exameComplementares;
    }

    public void setExameComplementares(List<ExameComplementar> exameComplementares) {
        this.exameComplementares = exameComplementares;
    }

//==============================================GETTERS E SETTERS RISCO OCUPACIONAL=====================================

    public List<RiscoOcupacionalASO> getRiscos() {
        return riscos;
    }

    public void setRiscos(List<RiscoOcupacionalASO> riscos) {
        this.riscos = riscos;
    }


//==============================================GETTERS E SETTERS EQUIPAMENTOS==========================================


    public List<EquipamentoPCMSO> getEquipamentosPCMSO() {
        return equipamentosPCMSO;
    }

    public void setEquipamentosPCMSO(List<EquipamentoPCMSO> equipamentosPCMSO) {
        this.equipamentosPCMSO = equipamentosPCMSO;
    }

//===============================================TOSTRING===============================================================


    public Date getDataEmissaoASO() {
        return dataEmissaoASO;
    }

    public void setDataEmissaoASO(Date dataEmissaoASO) {
        this.dataEmissaoASO = dataEmissaoASO;
    }

    @Override
    public String toString() {
        return contratoFP.toString();
    }

    @Override
    public String getDescricaoCompleta() {
        return this.contratoFP.toString();
    }

    @Override
    public String getIdentificador() {
        return this.getId().toString();
    }
}
