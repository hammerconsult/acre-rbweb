package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoPassageiro;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 18/01/15
 * Time: 19:42
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class ParametroOutorgaSubvencao extends SuperEntidade implements Comparable<ParametroOutorgaSubvencao> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Mes mes;
    private Integer qtdeAlunosTransportados;
    private BigDecimal percentualSubvencao;
    private BigDecimal valorPassagem;
    @ManyToOne
    private ParametrosOutorgaRBTrans parametroOutorga;
    @Enumerated(EnumType.STRING)
    private TipoPassageiro tipoPassageiro;

    @Temporal(TemporalType.DATE)
    private Date dataCadastro;
    @OneToOne
    private UsuarioSistema usuarioCadastro;
    @Temporal(TemporalType.DATE)
    private Date dataAtualizacao;
    @OneToOne
    private UsuarioSistema usuarioAtualizacao;
    @Temporal(TemporalType.DATE)
    private Date dataInicial;
    @Temporal(TemporalType.DATE)
    private Date dataFinal;

    public ParametroOutorgaSubvencao() {
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Integer getQtdeAlunosTransportados() {
        return qtdeAlunosTransportados;
    }

    public void setQtdeAlunosTransportados(Integer qtdeAlunosTransportados) {
        this.qtdeAlunosTransportados = qtdeAlunosTransportados;
    }

    public TipoPassageiro getTipoPassageiro() {
        return tipoPassageiro;
    }

    public void setTipoPassageiro(TipoPassageiro tipoPassageiro) {
        this.tipoPassageiro = tipoPassageiro;
    }

    public BigDecimal getPercentualSubvencao() {
        return percentualSubvencao;
    }

    public void setPercentualSubvencao(BigDecimal percentualSubvencao) {
        this.percentualSubvencao = percentualSubvencao;
    }

    public BigDecimal getValorPassagem() {
        return valorPassagem;
    }

    public void setValorPassagem(BigDecimal valorPassagem) {
        this.valorPassagem = valorPassagem;
    }

    public ParametrosOutorgaRBTrans getParametroOutorga() {
        return parametroOutorga;
    }

    public void setParametroOutorga(ParametrosOutorgaRBTrans parametroOutorga) {
        this.parametroOutorga = parametroOutorga;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int compareTo(ParametroOutorgaSubvencao o) {
        int i = o.getMes().getNumeroMes().compareTo(this.getMes().getNumeroMes());
        if (i == 0) {
            i = o.getMes().getNumeroMes().compareTo(this.getMes().getNumeroMes());
        }
        return i;
    }
}
