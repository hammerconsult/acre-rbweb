package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by israeleriston on 11/11/15.
 */
@Entity
@Audited
@Etiqueta("Acidente de Trabalho")
public class AcidenteTrabalho extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Servidor")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private ContratoFP contratoFP;
    @Etiqueta("Local do Acidente")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private String local;
    @Etiqueta("Parte do Corpo Atingido")
    @Pesquisavel
    @Obrigatorio
    private String parteCorpoAtingido;
    @Etiqueta("Agente Causador")
    @Pesquisavel
    @Obrigatorio
    private String agenteCausador;
    @Etiqueta("Conhecimento Policial")
    @Obrigatorio
    @Column(name = "conhecimentoPolicial")
    private Boolean isConhecimentoPolicial;
    @Etiqueta("Falecimento")
    @Obrigatorio
    @Column(name = "falecimento")
    private Boolean isFalecimento;
    @Etiqueta("Unidade de Saúde")
    @Obrigatorio
    private String unidadeSaude;
    @Etiqueta("Data de Atendimento")
    @Obrigatorio
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date atendidoEm;
    @Etiqueta("Horário do Atendimento")
    @Obrigatorio
    @Temporal(TemporalType.TIME)
    private Date horario;
    @Obrigatorio
    @Etiqueta("Internado")
    @Column(name = "internado")
    private Boolean isInternado;
    @Etiqueta("Duração Provável de Tratamento")
    private Integer duracaoTratamento;
    @Etiqueta("Afastamento")
    @Column(name = "afastado")
    @Obrigatorio
    private Boolean isAfastamentoTrabalho;
    @OneToOne(cascade = CascadeType.ALL)
    private Acidente acidente;
    @Etiqueta("Primeira Testemunha")
    @Obrigatorio
    private String primeiraTestemunha;
    @Etiqueta("Segunda Testemunha")
    @Obrigatorio
    private String segundaTestemunha;
    @Etiqueta("Ultimo Dia Trabalhado")
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    private Date ultimoDiaTrabalhado;
    @Etiqueta("Data da Ocorrência")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date ocorridoEm;

    public AcidenteTrabalho(ContratoFP contratoFP, String local,
                            String parteCorpoAtingido, String agenteCausador,
                            Boolean isConhecimentoPolicial, Boolean isFalecimento,
                            String unidadeSaude, Date atendidoEm, Date horario,
                            Boolean isInternado, Integer duracaoTratamento,
                            Boolean isAfastamentoTrabalho, Acidente acidente,
                            String primeiraTestemunha, String segundaTestemunha,
                            Date ultimoDiaTrabalhado, Date ocorridoEm) {
        this.contratoFP = contratoFP;
        this.local = local;
        this.parteCorpoAtingido = parteCorpoAtingido;
        this.agenteCausador = agenteCausador;
        this.isConhecimentoPolicial = isConhecimentoPolicial;
        this.isFalecimento = isFalecimento;
        this.unidadeSaude = unidadeSaude;
        this.atendidoEm = atendidoEm;
        this.horario = horario;
        this.isInternado = isInternado;
        this.duracaoTratamento = duracaoTratamento;
        this.isAfastamentoTrabalho = isAfastamentoTrabalho;
        this.acidente = acidente;
        this.primeiraTestemunha = primeiraTestemunha;
        this.segundaTestemunha = segundaTestemunha;
        this.ultimoDiaTrabalhado = ultimoDiaTrabalhado;
        this.ocorridoEm = ocorridoEm;
    }

    public AcidenteTrabalho() {
        this.acidente = new Acidente();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getParteCorpoAtingido() {
        return parteCorpoAtingido;
    }

    public void setParteCorpoAtingido(String parteCorpoAtingido) {
        this.parteCorpoAtingido = parteCorpoAtingido;
    }

    public String getAgenteCausador() {
        return agenteCausador;
    }

    public void setAgenteCausador(String agenteCausador) {
        this.agenteCausador = agenteCausador;
    }

    public Boolean getIsConhecimentoPolicial() {
        return isConhecimentoPolicial;
    }

    public void setIsConhecimentoPolicial(Boolean isConhecimentoPolicial) {
        this.isConhecimentoPolicial = isConhecimentoPolicial;
    }

    public Boolean getIsFalecimento() {
        return isFalecimento;
    }

    public void setIsFalecimento(Boolean isFalecimento) {
        this.isFalecimento = isFalecimento;
    }

    public String getUnidadeSaude() {
        return unidadeSaude;
    }

    public void setUnidadeSaude(String unidadeSaude) {
        this.unidadeSaude = unidadeSaude;
    }

    public Date getAtendidoEm() {
        return atendidoEm;
    }

    public void setAtendidoEm(Date atendidoEm) {
        this.atendidoEm = atendidoEm;
    }

    public Date getHorario() {
        return horario;
    }

    public void setHorario(Date horario) {
        this.horario = horario;
    }

    public Boolean getIsInternado() {
        return isInternado;
    }

    public void setIsInternado(Boolean isInternado) {
        this.isInternado = isInternado;
    }

    public Integer getDuracaoTratamento() {
        return duracaoTratamento;
    }

    public void setDuracaoTratamento(Integer duracaoTratamento) {
        this.duracaoTratamento = duracaoTratamento;
    }

    public Boolean getIsAfastamentoTrabalho() {
        return isAfastamentoTrabalho;
    }

    public void setIsAfastamentoTrabalho(Boolean isAfastamentoTrabalho) {
        this.isAfastamentoTrabalho = isAfastamentoTrabalho;
    }

    public Acidente getAcidente() {
        return acidente;
    }

    public void setAcidente(Acidente acidente) {
        this.acidente = acidente;
    }

    public String getPrimeiraTestemunha() {
        return primeiraTestemunha;
    }

    public void setPrimeiraTestemunha(String primeiraTestemunha) {
        this.primeiraTestemunha = primeiraTestemunha;
    }

    public String getSegundaTestemunha() {
        return segundaTestemunha;
    }

    public void setSegundaTestemunha(String segundaTestemunha) {
        this.segundaTestemunha = segundaTestemunha;
    }

    public Date getUltimoDiaTrabalhado() {
        return ultimoDiaTrabalhado;
    }

    public void setUltimoDiaTrabalhado(Date ultimoDiaTrabalhado) {
        this.ultimoDiaTrabalhado = ultimoDiaTrabalhado;
    }

    public Date getOcorridoEm() {
        return ocorridoEm;
    }

    public void setOcorridoEm(Date ocorridoEm) {
        this.ocorridoEm = ocorridoEm;
    }

    @Override
    public String toString() {
        return contratoFP.getMatriculaFP().getPessoa().getNomeCpfCnpj();
    }
}

