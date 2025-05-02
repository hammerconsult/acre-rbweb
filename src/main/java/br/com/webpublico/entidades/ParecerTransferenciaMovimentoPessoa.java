package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 02/10/15
 * Time: 08:28
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Table(name = "PARECERTRANSFMOVPESSOA")
@Etiqueta("Parecer da Solicitação de Transferência de Movimentos da Pessoa")
public class ParecerTransferenciaMovimentoPessoa extends SuperEntidade{

    @Id
    @GeneratedValue
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data/Hora do Parecer")
    @Temporal(TemporalType.TIMESTAMP)
    private Date realizadoEm;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Responsável do Parecer")
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Solicitação de Transferência Movimentos da Pessoa")
    private TransferenciaMovPessoa transferenciaMovPessoa;
    @Obrigatorio
    @Etiqueta("Justificativa")
    private String justificativa;

    public ParecerTransferenciaMovimentoPessoa() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getRealizadoEm() {
        return realizadoEm;
    }

    public void setRealizadoEm(Date realizadoEm) {
        this.realizadoEm = realizadoEm;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public TransferenciaMovPessoa getTransferenciaMovPessoa() {
        return transferenciaMovPessoa;
    }

    public void setTransferenciaMovPessoa(TransferenciaMovPessoa transferenciaMovPessoa) {
        this.transferenciaMovPessoa = transferenciaMovPessoa;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }
}
