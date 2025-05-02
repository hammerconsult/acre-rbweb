package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Audited
@Entity
@Etiqueta("Extinção do Processo Judicial")
public class ProcessoJudicialExtincao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Processo Judicial")
    @Tabelavel
    @Pesquisavel
    private ProcessoJudicial processoJudicial;
    private String motivoCancelamento;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cancelamento;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Usuário")
    @ManyToOne
    private UsuarioSistema usuarioSistemaCancelamento;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    @Tabelavel
    @Pesquisavel
    private Situacao situacao;
    @Transient
    private Long criadoEm;

    public ProcessoJudicialExtincao() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessoJudicial getProcessoJudicial() {
        return processoJudicial;
    }

    public void setProcessoJudicial(ProcessoJudicial processoJudicial) {
        this.processoJudicial = processoJudicial;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public Date getCancelamento() {
        return cancelamento;
    }

    public void setCancelamento(Date cancelamento) {
        this.cancelamento = cancelamento;
    }

    public UsuarioSistema getUsuarioSistemaCancelamento() {
        return usuarioSistemaCancelamento;
    }

    public void setUsuarioSistemaCancelamento(UsuarioSistema usuarioSistemaCancelamento) {
        this.usuarioSistemaCancelamento = usuarioSistemaCancelamento;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    public enum Situacao implements EnumComDescricao {
        EFETIVADO("Efetivado"),
        CANCELADO("Cancelado");

        private String descricao;

        Situacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
