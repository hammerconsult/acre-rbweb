package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoPassageiro;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 11/12/13
 * Time: 14:15
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class OutorgaIPO implements Serializable, Comparable<OutorgaIPO> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Mes mes;
    private BigDecimal percentual;
    private BigDecimal ipo;
    private Integer alunos;
    private BigDecimal alunosTransportados;
    @Temporal(TemporalType.DATE)
    private Date dataCadastro;
    @OneToOne
    private UsuarioSistema usuarioCadastro;
    @Temporal(TemporalType.DATE)
    private Date dataAtualizacao;
    @OneToOne
    private UsuarioSistema usuarioAtualizacao;
    @ManyToOne
    private ContribuinteDebitoOutorga contribuinteDebitoOutorga;
    @Transient
    @Invisivel
    private Long criadoEm;
    @Enumerated(EnumType.STRING)
    private TipoPassageiro tipoPassageiro;
    @Temporal(TemporalType.DATE)
    private Date dataInicial;
    @Temporal(TemporalType.DATE)
    private Date dataFinal;

    public OutorgaIPO() {
        this.criadoEm = System.nanoTime();
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public TipoPassageiro getTipoPassageiro() {
        return tipoPassageiro;
    }

    public void setTipoPassageiro(TipoPassageiro tipoPassageiro) {
        this.tipoPassageiro = tipoPassageiro;
    }

    public ContribuinteDebitoOutorga getContribuinteDebitoOutorga() {
        return contribuinteDebitoOutorga;
    }

    public void setContribuinteDebitoOutorga(ContribuinteDebitoOutorga contribuinteDebitoOutorga) {
        this.contribuinteDebitoOutorga = contribuinteDebitoOutorga;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public UsuarioSistema getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(UsuarioSistema usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
    }

    public Date getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public UsuarioSistema getUsuarioAtualizacao() {
        return usuarioAtualizacao;
    }

    public void setUsuarioAtualizacao(UsuarioSistema usuarioAtualizacao) {
        this.usuarioAtualizacao = usuarioAtualizacao;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public BigDecimal getIpo() {
        return ipo;
    }

    public void setIpo(BigDecimal ipo) {
        this.ipo = ipo;
    }

    public Integer getAlunos() {
        return alunos;
    }

    public void setAlunos(Integer alunos) {
        this.alunos = alunos;
    }

    public BigDecimal getAlunosTransportados() {
        return alunosTransportados;
    }

    public void setAlunosTransportados(BigDecimal alunosTransportados) {
        this.alunosTransportados = alunosTransportados;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public int compareTo(OutorgaIPO o) {
        int i = o.getMes().getNumeroMes().compareTo(this.getMes().getNumeroMes());
        if (i == 0) {
            i = o.getMes().getNumeroMes().compareTo(this.getMes().getNumeroMes());
        }
        return i;
    }
}
