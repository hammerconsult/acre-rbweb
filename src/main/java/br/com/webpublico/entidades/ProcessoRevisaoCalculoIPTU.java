package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoVencimentoRevisaoIPTU;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 02/07/15
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
public class ProcessoRevisaoCalculoIPTU extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Número do processo")
    private Long codigo;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data do Processo")
    private Date dataProcesso;
    @Etiqueta("Usuário Responsável")
    @ManyToOne
    private UsuarioSistema usuario;
    @OneToMany(mappedBy = "processoRevisaoCalculoIPTU", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcessoRevisaoCalculoIPTUExercicio> exercicios;
    @Etiqueta("Inscrição Inicial")
    private String inscricaoInicial;
    @Etiqueta("Inscrição Final")
    private String inscricaoFinal;
    private String observacao;
    @OneToMany(mappedBy = "processoRevisaoCalculoIPTU", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ProcessoRevisao> processoRevisao;
    @Enumerated(EnumType.STRING)
    private TipoVencimentoRevisaoIPTU tipoVencimentoRevisaoIPTU;
    @Transient
    private String anoProtocolo;
    @Transient
    private String numeroProtocolo;

    public ProcessoRevisaoCalculoIPTU() {
        exercicios = Lists.newArrayList();
        processoRevisao = Lists.newArrayList();
        tipoVencimentoRevisaoIPTU = TipoVencimentoRevisaoIPTU.VENCIMENTO_ATUAL;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getDataProcesso() {
        return dataProcesso;
    }

    public void setDataProcesso(Date dataProcesso) {
        this.dataProcesso = dataProcesso;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public List<ProcessoRevisaoCalculoIPTUExercicio> getExercicios() {
        return exercicios;
    }

    public void setExercicios(List<ProcessoRevisaoCalculoIPTUExercicio> exercicios) {
        this.exercicios = exercicios;
    }

    public String getInscricaoInicial() {
        return inscricaoInicial;
    }

    public void setInscricaoInicial(String inscricaoInicial) {
        this.inscricaoInicial = inscricaoInicial;
    }

    public String getInscricaoFinal() {
        return inscricaoFinal;
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.inscricaoFinal = inscricaoFinal;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<ProcessoRevisao> getProcessoRevisao() {
        return processoRevisao;
    }

    public void setProcessoRevisao(List<ProcessoRevisao> processoRevisao) {
        this.processoRevisao = processoRevisao;
    }

    public TipoVencimentoRevisaoIPTU getTipoVencimentoRevisaoIPTU() {
        return tipoVencimentoRevisaoIPTU;
    }

    public void setTipoVencimentoRevisaoIPTU(TipoVencimentoRevisaoIPTU tipoVencimentoRevisaoIPTU) {
        this.tipoVencimentoRevisaoIPTU = tipoVencimentoRevisaoIPTU;
    }
}
