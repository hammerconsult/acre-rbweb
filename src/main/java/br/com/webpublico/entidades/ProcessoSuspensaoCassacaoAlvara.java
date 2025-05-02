package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoProcessoSuspensaoCassacaoAlvara;
import br.com.webpublico.enums.TipoProcessoSuspensaoCassacaoAlvara;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class ProcessoSuspensaoCassacaoAlvara implements Serializable, PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long codigo;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo do Processo")
    @Obrigatorio
    private TipoProcessoSuspensaoCassacaoAlvara tipoProcesso;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorArquivoComposicao detentorArquivoComposicao;
    @ManyToOne
    private Exercicio exercicio;
    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Alvarás")
    List<AlvaraProcessoSuspensaoCassacao> alvaras;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação do Processo")
    @Obrigatorio
    private SituacaoProcessoSuspensaoCassacaoAlvara situacao;
    @Temporal(TemporalType.TIMESTAMP)
    @Obrigatorio
    private Date dataLancamento;
    @ManyToOne
    @Obrigatorio
    private UsuarioSistema usuarioSistema;
    @Obrigatorio
    @Etiqueta("Motivo")
    private String motivo;

    public ProcessoSuspensaoCassacaoAlvara() {
        this.situacao = SituacaoProcessoSuspensaoCassacaoAlvara.EM_ABERTO;
        this.dataLancamento = new Date();
        this.alvaras = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoProcessoSuspensaoCassacaoAlvara getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoProcessoSuspensaoCassacaoAlvara tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<AlvaraProcessoSuspensaoCassacao> getAlvaras() {
        return alvaras;
    }

    public void setAlvaras(List<AlvaraProcessoSuspensaoCassacao> alvaras) {
        this.alvaras = alvaras;
    }

    public SituacaoProcessoSuspensaoCassacaoAlvara getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoProcessoSuspensaoCassacaoAlvara situacao) {
        this.situacao = situacao;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public boolean processoAberto() {
        return SituacaoProcessoSuspensaoCassacaoAlvara.EM_ABERTO.equals(situacao);
    }

    public boolean processoEfetivado() {
        return SituacaoProcessoSuspensaoCassacaoAlvara.EFETIVADO.equals(situacao);
    }

    public boolean processoEncerrado() {
        return SituacaoProcessoSuspensaoCassacaoAlvara.ENCERRADO.equals(situacao);
    }
}
