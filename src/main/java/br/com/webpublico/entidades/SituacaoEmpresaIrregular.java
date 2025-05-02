package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Etiqueta("Situação Empresa Irregular")
@Entity
@Audited
public class SituacaoEmpresaIrregular extends SuperEntidade {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EmpresaIrregular empresaIrregular;
    @ManyToOne
    private MonitoramentoFiscal monitoramentoFiscal;
    @Temporal(TemporalType.TIMESTAMP)
    private Date data;
    @Enumerated(EnumType.STRING)
    private Situacao situacao;
    @ManyToOne
    private Irregularidade irregularidade;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    private String observacao;
    private String anoProtocolo;
    private String numeroProtocolo;

    public SituacaoEmpresaIrregular() {
        situacao = Situacao.INSERIDA;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public Irregularidade getIrregularidade() {
        return irregularidade;
    }

    public void setIrregularidade(Irregularidade irregularidade) {
        this.irregularidade = irregularidade;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public EmpresaIrregular getEmpresaIrregular() {
        return empresaIrregular;
    }

    public void setEmpresaIrregular(EmpresaIrregular empresaIrregular) {
        this.empresaIrregular = empresaIrregular;
    }

    public MonitoramentoFiscal getMonitoramentoFiscal() {
        return monitoramentoFiscal;
    }

    public void setMonitoramentoFiscal(MonitoramentoFiscal monitoramentoFiscal) {
        this.monitoramentoFiscal = monitoramentoFiscal;
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

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public enum Situacao implements EnumComDescricao {
        INSERIDA("Inserida"),
        RETIRADA("Retirada");

        private String descricao;

        Situacao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }
    }
}
