/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pessoa.dto.EconomicoCnaeDTO;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Terminal-2
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Tributario")
public class EconomicoCNAE implements Serializable, Comparable<EconomicoCNAE> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CNAE cnae;
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicio;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fim;
    private String funcSegSex;
    private String funcSabado;
    private String funcDomFeriado;
    @Enumerated(EnumType.STRING)
    private TipoCnae tipo;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataregistro;
    @Transient
    private Long criadoEm;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "economicoCNAE")
    private List<SituacaoEconomicoCNAE> situacoes;
    @ManyToOne
    private HorarioFuncionamento horarioFuncionamento;
    private Boolean exercidaNoLocal;

    public EconomicoCNAE() {
        criadoEm = System.nanoTime();
        situacoes = new ArrayList<SituacaoEconomicoCNAE>();
    }

    public EconomicoCNAE(Long id, CadastroEconomico cmc, CNAE cnae, HorarioFuncionamento horario, Date inicio) {
        this.id = id;
        this.cadastroEconomico = cmc;
        this.cnae = cnae;
        this.horarioFuncionamento = horario;
        this.inicio = inicio;
    }

    public Long getId() {
        return id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public String getFuncDomFeriado() {
        return funcDomFeriado;
    }

    public void setFuncDomFeriado(String funcDomFeriado) {
        this.funcDomFeriado = funcDomFeriado;
    }

    public String getFuncSabado() {
        return funcSabado;
    }

    public void setFuncSabado(String funcSabado) {
        this.funcSabado = funcSabado;
    }

    public String getFuncSegSex() {
        return funcSegSex;
    }

    public void setFuncSegSex(String funcSegSex) {
        this.funcSegSex = funcSegSex;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public TipoCnae getTipo() {
        return tipo;
    }

    public void setTipo(TipoCnae tipo) {
        this.tipo = tipo;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public Date getDataregistro() {
        return dataregistro;
    }

    public void setDataregistro(Date dataregistro) {
        this.dataregistro = dataregistro;
    }

    public List<SituacaoEconomicoCNAE> getSituacoes() {
        return situacoes;
    }

    public void setSituacoes(List<SituacaoEconomicoCNAE> situacoes) {
        this.situacoes = situacoes;
    }

    public HorarioFuncionamento getHorarioFuncionamento() {
        return horarioFuncionamento;
    }

    public void setHorarioFuncionamento(HorarioFuncionamento horarioFuncionamento) {
        this.horarioFuncionamento = horarioFuncionamento;
    }

    public Boolean getExercidaNoLocal() {
        return exercidaNoLocal != null ? exercidaNoLocal : Boolean.FALSE;
    }

    public void setExercidaNoLocal(Boolean exercidaNoLocal) {
        this.exercidaNoLocal = exercidaNoLocal;
    }

    public boolean isVigente(){
        return DataUtil.isVigente(inicio,fim);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }


    @Override
    public String toString() {
        return cnae.getCodigoCnae();
    }

    @Override
    public int compareTo(EconomicoCNAE o) {
        int i = this.getCnae().getSituacao().compareTo(o.getCnae().getSituacao());
        if (i == 0) {
            i = this.getTipo() != null && o.getTipo() != null ? this.getTipo().compareTo(o.getTipo()) : 0;
        }
        if (i == 0) {
            i = this.getCnae().getCodigoCnae().compareTo(o.getCnae().getCodigoCnae());
        }
        return i;
    }

    public EconomicoCnaeDTO toEconomicoCnaeDTO() {
        EconomicoCnaeDTO economicoCnaeDTO = new EconomicoCnaeDTO();
        economicoCnaeDTO.setDataRegistro(dataregistro);
        economicoCnaeDTO.setExercidaNoLocal(exercidaNoLocal);
        economicoCnaeDTO.setFim(fim);
        economicoCnaeDTO.setFuncDomFeriado(funcDomFeriado);
        economicoCnaeDTO.setFuncSabado(funcSabado);
        economicoCnaeDTO.setFuncSegSex(funcSegSex);
        economicoCnaeDTO.setInicio(inicio);
        economicoCnaeDTO.setTipo(tipo.tipoCnaeDTOPessoaDto);
        economicoCnaeDTO.setCnaeDTO(cnae.toCnaeDTO());
        return economicoCnaeDTO;
    }

    public enum TipoCnae {
        Primaria("Primária", br.com.webpublico.pessoa.enumeration.TipoCnae.PRIMARIA, 1),
        Secundaria("Secundária", br.com.webpublico.pessoa.enumeration.TipoCnae.SECUNDARIA, 2);
        private String descricao;
        private br.com.webpublico.pessoa.enumeration.TipoCnae tipoCnaeDTO;
        private br.com.webpublico.pessoa.enumeration.TipoCnae tipoCnaeDTOPessoaDto;
        private Integer ordenacao;

        private TipoCnae(String descricao, br.com.webpublico.pessoa.enumeration.TipoCnae tipoCnaeDTO, Integer ordenacao) {
            this.descricao = descricao;
            this.tipoCnaeDTOPessoaDto = tipoCnaeDTO;
            this.ordenacao = ordenacao;
            this.tipoCnaeDTO = tipoCnaeDTO;
        }

        public String getDescricao() {
            return descricao;
        }

        public br.com.webpublico.pessoa.enumeration.TipoCnae getTipoCnaeDTO() {
            return tipoCnaeDTO;
        }

        public br.com.webpublico.pessoa.enumeration.TipoCnae getTipoCnaeDTOPessoaDto() {
            return tipoCnaeDTOPessoaDto;
        }

        public Integer getOrdenacao() {
            return ordenacao;
        }

        public static TipoCnae getTipoCnaePorTipoCnaeDTO(br.com.webpublico.tributario.enumeration.TipoCnae tipoCnaeDTO) {
            for (TipoCnae tipoCnae : TipoCnae.values()) {
                if (tipoCnae.getTipoCnaeDTO().name().equals(tipoCnaeDTO.name())) {
                    return tipoCnae;
                }
            }
            return null;
        }

        public static TipoCnae getTipoCnaePorTipoCnaeDTOPessoaDto(br.com.webpublico.pessoa.enumeration.TipoCnae tipoCnaeDTO) {
            for (TipoCnae tipoCnae : TipoCnae.values()) {
                if (tipoCnae.getTipoCnaeDTOPessoaDto().equals(tipoCnaeDTO)) {
                    return tipoCnae;
                }
            }
            return null;
        }
    }

}
