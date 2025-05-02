package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.RedutorValorBem;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 27/10/14
 * Time: 09:57
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Mensagem de Log de Redução de Valor do Bem ou de Estorno")
public class MsgLogReducaoOuEstorno extends SuperEntidade implements Comparable<MsgLogReducaoOuEstorno> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private LogReducaoOuEstorno logReducaoOuEstorno;

    private BigDecimal valor;

    @ManyToOne
    private Bem bem;

    private String mensagem;

    public MsgLogReducaoOuEstorno() {
        super();
    }

    public MsgLogReducaoOuEstorno(RedutorValorBem redutor, String mensagem) {
        super();
        this.logReducaoOuEstorno = redutor.getLogReducaoOuEstorno();
        this.valor = redutor.getValorDoLancamento();
        this.bem = redutor.getBem();
        this.mensagem = mensagem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LogReducaoOuEstorno getLogReducaoOuEstorno() {
        return logReducaoOuEstorno;
    }

    public void setLogReducaoOuEstorno(LogReducaoOuEstorno logReducaoOuEstorno) {
        this.logReducaoOuEstorno = logReducaoOuEstorno;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }


    @Override
    public int compareTo(MsgLogReducaoOuEstorno o) {
        if (o.getBem() != null && getBem() != null) {
            return Long.valueOf(getBem().getIdentificacao()).compareTo(Long.valueOf(o.getBem().getIdentificacao()));
        }
        return 0;
    }
}
