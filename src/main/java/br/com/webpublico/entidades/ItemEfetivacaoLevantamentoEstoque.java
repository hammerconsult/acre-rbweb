package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by Desenvolvimento on 02/02/2017.
 */

@Entity
@Audited
@Table(name = "ITEMEFETIVLEVANTESTOQUE")
public class ItemEfetivacaoLevantamentoEstoque extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;

    @Obrigatorio
    @ManyToOne
    private EfetivacaoLevantamentoEstoque efetivacao;

    @Obrigatorio
    @OneToOne
    private LevantamentoEstoque levantamentoEstoque;

    public ItemEfetivacaoLevantamentoEstoque() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EfetivacaoLevantamentoEstoque getEfetivacao() {
        return efetivacao;
    }

    public void setEfetivacao(EfetivacaoLevantamentoEstoque efetivacao) {
        this.efetivacao = efetivacao;
    }

    public LevantamentoEstoque getLevantamentoEstoque() {
        return levantamentoEstoque;
    }

    public void setLevantamentoEstoque(LevantamentoEstoque levantamentoEstoque) {
        this.levantamentoEstoque = levantamentoEstoque;
    }
}
