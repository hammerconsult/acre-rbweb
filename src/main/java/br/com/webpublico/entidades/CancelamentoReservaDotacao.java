package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 09/06/14
 * Time: 14:46
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Cancelamento de Reserva de Dotação")
public class CancelamentoReservaDotacao extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data")
    @Tabelavel
    @Pesquisavel
    private Date data;

    @Etiqueta("Número")
    @Pesquisavel
    @Tabelavel
    private Long numero;

    @OneToOne
    @Etiqueta("Usuário")
    @Tabelavel
    @Pesquisavel
    private UsuarioSistema usuarioSistema;

    @OneToOne
    @Etiqueta("Solicitação")
    @Tabelavel
    @Pesquisavel
    private SolicitacaoCancelamentoReservaDotacao solicitacao;

    public CancelamentoReservaDotacao() {
    }

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

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public SolicitacaoCancelamentoReservaDotacao getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoCancelamentoReservaDotacao solicitacao) {
        this.solicitacao = solicitacao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return numero + " Solicitação: " + solicitacao.toString() + "(" + solicitacao.getOrigemReservaFonte().getDescricao() + ")";
    }
}
