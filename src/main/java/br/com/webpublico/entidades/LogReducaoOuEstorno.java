package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 31/10/14
 * Time: 11:31
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Log de Redução de Valor do Bem ou de Estorno")
public class LogReducaoOuEstorno extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "logReducaoOuEstorno", fetch = FetchType.LAZY)
    private List<MsgLogReducaoOuEstorno> mensagens;

    public LogReducaoOuEstorno() {
        this.mensagens = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<MsgLogReducaoOuEstorno> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<MsgLogReducaoOuEstorno> mensagens) {
        this.mensagens = mensagens;
    }
}
