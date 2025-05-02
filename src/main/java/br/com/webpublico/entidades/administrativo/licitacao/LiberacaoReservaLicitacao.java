package br.com.webpublico.entidades.administrativo.licitacao;

import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by wellington on 24/10/17.
 */
@Etiqueta("Liberação de Reserva da Licitação")
@Entity
public class LiberacaoReservaLicitacao extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Liberado em")
    @Temporal(TemporalType.TIMESTAMP)
    private Date liberadoEm;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Responsável")
    @ManyToOne
    private UsuarioSistema responsavel;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Licitação")
    @ManyToOne
    private Licitacao licitacao;
    @OneToMany(mappedBy = "liberacaoReservaLicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LiberacaoReservaLicitacaoItem> itens;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getLiberadoEm() {
        return liberadoEm;
    }

    public void setLiberadoEm(Date liberadoEm) {
        this.liberadoEm = liberadoEm;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public List<LiberacaoReservaLicitacaoItem> getItens() {
        return itens;
    }

    public void setItens(List<LiberacaoReservaLicitacaoItem> itens) {
        this.itens = itens;
    }
}
