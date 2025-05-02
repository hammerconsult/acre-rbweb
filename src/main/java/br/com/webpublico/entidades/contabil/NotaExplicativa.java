package br.com.webpublico.entidades.contabil;

import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Audited
@Entity
public class NotaExplicativa extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Fim de Vigência")
    private Date fimVigencia;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Relatório")
    private RelatoriosItemDemonst relatoriosItemDemonst;
    @Etiqueta("Conteúdo")
    private String conteudo;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "notaExplicativa")
    private List<NotaExplicativaItem> itens;

    public NotaExplicativa() {
        itens = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public RelatoriosItemDemonst getRelatoriosItemDemonst() {
        return relatoriosItemDemonst;
    }

    public void setRelatoriosItemDemonst(RelatoriosItemDemonst relatoriosItemDemonst) {
        this.relatoriosItemDemonst = relatoriosItemDemonst;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public List<NotaExplicativaItem> getItens() {
        return itens;
    }

    public void setItens(List<NotaExplicativaItem> itens) {
        this.itens = itens;
    }

    @Override
    public String toString() {
        return relatoriosItemDemonst.toString();
    }
}
