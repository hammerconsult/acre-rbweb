/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
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
@Etiqueta("Sessão de Atividade CNAE")
public class SessaoAtividadeCnae extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Etiqueta("CNAE")
    @Pesquisavel
    @ManyToOne
    private CNAE cnae;

    @Tabelavel
    @Etiqueta("Sessão Atividade")
    @Pesquisavel
    @ManyToOne
    private SessaoAtividade sessaoAtividade;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }

    public SessaoAtividade getSessaoAtividade() {
        return sessaoAtividade;
    }

    public void setSessaoAtividade(SessaoAtividade sessaoAtividade) {
        this.sessaoAtividade = sessaoAtividade;
    }
}
