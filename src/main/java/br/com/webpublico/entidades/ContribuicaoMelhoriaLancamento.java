package br.com.webpublico.entidades;


import br.com.webpublico.enums.SituacaoContribuicaoMelhoria;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * Created by William on 30/06/2016.
 */
@Entity
@Audited
@Etiqueta("Lançamento de Contribuição de Melhoria")
@Table(name = "CONTRIBUICAOMELHORIALANC")
public class ContribuicaoMelhoriaLancamento extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Edital")
    private ContribuicaoMelhoriaEdital edital;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contribuicaoMelhoriaLanc", orphanRemoval = true)
    private List<ContribuicaoMelhoriaItem> itens;

    private String observacao;

    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    private SituacaoContribuicaoMelhoria situacao;

    public ContribuicaoMelhoriaLancamento() {
        itens = Lists.newArrayList();
    }

    public ContribuicaoMelhoriaEdital getEdital() {
        return edital;
    }

    public void setEdital(ContribuicaoMelhoriaEdital edital) {
        this.edital = edital;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ContribuicaoMelhoriaItem> getItens() {
        return itens;
    }

    public void setItens(List<ContribuicaoMelhoriaItem> itens) {
        this.itens = itens;
    }

    public SituacaoContribuicaoMelhoria getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoContribuicaoMelhoria situacao) {
        this.situacao = situacao;
    }
}
