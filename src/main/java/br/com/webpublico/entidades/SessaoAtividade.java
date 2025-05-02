/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.beust.jcommander.internal.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Camila
 */
@Entity

@Audited
@Etiqueta("Sessão de Atividade")
public class SessaoAtividade extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Etiqueta("Sessão")
    @Pesquisavel
    @Obrigatorio
    private String sessao;

    @Tabelavel
    @Etiqueta("Denominação")
    @Pesquisavel
    @Obrigatorio
    private String denominacao;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sessaoAtividade", orphanRemoval = true)
    private List<SessaoAtividadeCnae> sessaoAtividadeCnaes;

    public SessaoAtividade() {
        sessaoAtividadeCnaes = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessao() {
        return sessao;
    }

    public void setSessao(String sessao) {
        this.sessao = sessao;
    }

    public String getDenominacao() {
        return denominacao;
    }

    public void setDenominacao(String denominacao) {
        this.denominacao = denominacao;
    }

    public List<SessaoAtividadeCnae> getSessaoAtividadeCnaes() {
        return sessaoAtividadeCnaes;
    }

    public void setSessaoAtividadeCnaes(List<SessaoAtividadeCnae> sessaoAtividadeCnaes) {
        this.sessaoAtividadeCnaes = sessaoAtividadeCnaes;
    }
}
