/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoAutoInfracao;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.webreportdto.dto.tributario.SituacaoRegistroLancamentoContabilDTO;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * @author Terminal-2
 */
@Entity
@Audited
public class RegistroLancamentoContabil implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "registroLancamentoContabil", orphanRemoval = true)
    private List<LancamentoContabilFiscal> lancamentosContabeis;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "registroLancamentoContabil", orphanRemoval = true)
    private List<LancamentoMultaFiscal> multas;
    @ManyToOne
    private AcaoFiscal acaoFiscal;
    private Long numeroRegistro;
    private Integer ano;
    private String numeroProcessoAdministrativo;
    @Transient
    private Long criadoEm;
    @Enumerated(EnumType.STRING)
    private Situacao situacao;
    @Transient
    private Map<Integer, List<LancamentoContabilFiscal>> lancamentosPorAno;
    @Transient
    private List<LancamentoContabilFiscal> lancamentoSelecionado;
    @Transient
    private Integer anoSelecionado;
    @OneToMany(mappedBy = "registro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AutoInfracaoFiscal> autosInfracao;
    private String migracaochave;

    public RegistroLancamentoContabil() {
        lancamentosContabeis = new ArrayList<>();
        autosInfracao = new ArrayList<>();
        multas = new ArrayList<>();
        criadoEm = System.nanoTime();
        ano = Calendar.getInstance().get(Calendar.YEAR);
        situacao = Situacao.AGUARDANDO;
        numeroRegistro = 1l;
    }

    public RegistroLancamentoContabil(AcaoFiscal acaoFiscal) {
        this();
        this.acaoFiscal = acaoFiscal;
        numeroRegistro = new Long(acaoFiscal.getLancamentosContabeis().size() + 1);
    }

    public Map<Integer, List<LancamentoContabilFiscal>> getLancamentosPorAno() {
        if (lancamentosPorAno == null || lancamentosPorAno.isEmpty()) {
            lancamentosPorAno = new TreeMap<>();
            Collections.sort(lancamentosContabeis);
            for (LancamentoContabilFiscal lancamento : lancamentosContabeis) {
                if (lancamentosPorAno.containsKey(lancamento.getAno())) {
                    lancamentosPorAno.get(lancamento.getAno()).add(lancamento);
                } else {
                    lancamentosPorAno.put(lancamento.getAno(), new ArrayList<LancamentoContabilFiscal>());
                    lancamentosPorAno.get(lancamento.getAno()).add(lancamento);
                }
            }
            if (lancamentosPorAno != null && !lancamentosPorAno.isEmpty()) {
                if (anoSelecionado == null) {
                    anoSelecionado = Lists.newArrayList(lancamentosPorAno.keySet()).get(0);
                }
                selecionarLancamento(anoSelecionado);
            }
        }

        return lancamentosPorAno;
    }

    public void selecionarLancamento(Integer anoLancamento) {
        anoSelecionado = anoLancamento;
        if (getLancamentosPorAno() != null && !getLancamentosPorAno().isEmpty()) {
            lancamentoSelecionado = getLancamentosPorAno().get(anoLancamento);
        }
    }

    public List<LancamentoContabilFiscal> getLancamentoSelecionado() {
        return lancamentoSelecionado;
    }

    public void setLancamentoSelecionado(List<LancamentoContabilFiscal> lancamentoSelecionado) {
        this.lancamentoSelecionado = lancamentoSelecionado;
    }

    public Integer getAnoSelecionado() {
        return anoSelecionado;
    }

    public void setAnoSelecionado(Integer anoSelecionado) {
        this.anoSelecionado = anoSelecionado;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<LancamentoContabilFiscal> getLancamentosContabeis() {
        return lancamentosContabeis;
    }

    public void setLancamentosContabeis(List<LancamentoContabilFiscal> lancamentosContabeis) {
        this.lancamentosContabeis = lancamentosContabeis;
    }

    public List<LancamentoMultaFiscal> getMultas() {
        return multas;
    }

    public void setMultas(List<LancamentoMultaFiscal> multas) {
        this.multas = multas;
    }

    public AcaoFiscal getAcaoFiscal() {
        return acaoFiscal;
    }

    public void setAcaoFiscal(AcaoFiscal acaoFiscal) {
        this.acaoFiscal = acaoFiscal;
    }

    public Long getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(Long numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getNumeroProcessoAdministrativo() {
        return numeroProcessoAdministrativo;
    }

    public void setNumeroProcessoAdministrativo(String numeroProcessoAdministrativo) {
        this.numeroProcessoAdministrativo = numeroProcessoAdministrativo;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return String.format("%d/%d", numeroRegistro, ano);
    }

    public String getNumeroAno() {
        return String.format("%d/%d", numeroRegistro, ano);
    }

    public List<AutoInfracaoFiscal> getAutosInfracao() {
        return autosInfracao;
    }

    public void setAutosInfracao(List<AutoInfracaoFiscal> autosInfracao) {
        this.autosInfracao = autosInfracao;
    }

    public AutoInfracaoFiscal getAutoInfracaoValido() {
        if (!autosInfracao.isEmpty()) {
            for (AutoInfracaoFiscal auto : autosInfracao) {
                if (auto.getSituacaoAutoInfracao() != null && !auto.getSituacaoAutoInfracao().equals(SituacaoAutoInfracao.CANCELADO)
                    && !auto.getSituacaoAutoInfracao().equals(SituacaoAutoInfracao.ESTORNADO)
                    && !auto.getSituacaoAutoInfracao().equals(SituacaoAutoInfracao.RETIFICADO)) {
                    return auto;
                }
            }
        }
        return null;
    }

    public AutoInfracaoFiscal getAutoInfracaoEntregue() {
        if (!autosInfracao.isEmpty()) {
            for (AutoInfracaoFiscal auto : autosInfracao) {
                if (auto.getSituacaoAutoInfracao() != null && (auto.getSituacaoAutoInfracao().equals(SituacaoAutoInfracao.CIENCIA)
                    || auto.getSituacaoAutoInfracao().equals(SituacaoAutoInfracao.REVELIA))) {
                    return auto;
                }
            }
        }
        return null;
    }

    public AutoInfracaoFiscal getAutoInfracaoRecebido() {
        if (!autosInfracao.isEmpty()) {
            for (AutoInfracaoFiscal auto : autosInfracao) {
                if (auto.getSituacaoAutoInfracao() != null && (auto.getSituacaoAutoInfracao().equals(SituacaoAutoInfracao.CIENCIA)
                    || auto.getSituacaoAutoInfracao().equals(SituacaoAutoInfracao.REVELIA))) {
                    return auto;
                }
            }
        }
        return null;
    }

    public String getMigracaochave() {
        return migracaochave;
    }

    public void setMigracaochave(String migracaochave) {
        this.migracaochave = migracaochave;
    }

    public Boolean isCancelado() {
        return Situacao.CANCELADO.equals(this.situacao);
    }

    public Boolean isEstornado() {
        return Situacao.ESTORNADO.equals(this.situacao);
    }

    public enum Situacao implements EnumComDescricao {
        AGUARDANDO("Aguardando", SituacaoRegistroLancamentoContabilDTO.AGUARDANDO),
        PAGO("Pago", SituacaoRegistroLancamentoContabilDTO.PAGO),
        AUTO_INFRACAO("Auto de Infração", SituacaoRegistroLancamentoContabilDTO.AUTO_INFRACAO),
        CIENCIA("Ciência", SituacaoRegistroLancamentoContabilDTO.CIENCIA),
        REVELIA("Revelia", SituacaoRegistroLancamentoContabilDTO.REVELIA),
        DIVIDA_ATIVA("Dívida Ativa", SituacaoRegistroLancamentoContabilDTO.DIVIDA_ATIVA),
        ESTORNADO("Estornado", SituacaoRegistroLancamentoContabilDTO.ESTORNADO),
        DESISTENCIA("Desistencia", SituacaoRegistroLancamentoContabilDTO.DESISTENCIA),
        BAIXADO("Baixado", SituacaoRegistroLancamentoContabilDTO.BAIXADO),
        HOMOLOGADO("Homologado", SituacaoRegistroLancamentoContabilDTO.HOMOLOGADO),
        CANCELADO("Cancelado", SituacaoRegistroLancamentoContabilDTO.CANCELADO);
        private String descricao;
        private SituacaoRegistroLancamentoContabilDTO toDto;

        Situacao(String descricao, SituacaoRegistroLancamentoContabilDTO toDto) {
            this.descricao = descricao;
            this.toDto = toDto;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }

        public SituacaoRegistroLancamentoContabilDTO getToDto() {
            return toDto;
        }
    }
}
