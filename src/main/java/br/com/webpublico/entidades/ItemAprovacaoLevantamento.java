package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Suporte Contabil
 * Date: 23/04/14
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class ItemAprovacaoLevantamento extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private LevantamentoBemPatrimonial levantamentoBemPatrimonial;

    @ManyToOne
    private AprovacaoLevantamentoBem aprovacaoLevantamentoBem;

    @Enumerated(EnumType.STRING)
    private SituacaoEventoBem situacao;

    public ItemAprovacaoLevantamento() {
        super();
        this.situacao = SituacaoEventoBem.FINALIZADO;
    }

    public ItemAprovacaoLevantamento(AprovacaoLevantamentoBem aprovacao, LevantamentoBemPatrimonial levantamento) {
        this.aprovacaoLevantamentoBem = aprovacao;
        this.levantamentoBemPatrimonial = levantamento;
        this.situacao = SituacaoEventoBem.FINALIZADO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LevantamentoBemPatrimonial getLevantamentoBemPatrimonial() {
        return levantamentoBemPatrimonial;
    }

    public void setLevantamentoBemPatrimonial(LevantamentoBemPatrimonial levantamentoBemPatrimonial) {
        this.levantamentoBemPatrimonial = levantamentoBemPatrimonial;
    }

    public AprovacaoLevantamentoBem getAprovacaoLevantamentoBem() {
        return aprovacaoLevantamentoBem;
    }

    public void setAprovacaoLevantamentoBem(AprovacaoLevantamentoBem aprovacaoLevantamentoBem) {
        this.aprovacaoLevantamentoBem = aprovacaoLevantamentoBem;
    }

    public SituacaoEventoBem getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoEventoBem situacao) {
        this.situacao = situacao;
    }
}
