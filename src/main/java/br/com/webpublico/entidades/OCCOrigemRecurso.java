package br.com.webpublico.entidades;

import br.com.webpublico.enums.OrigemSuplementacaoORC;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 09/01/14
 * Time: 17:02
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class OCCOrigemRecurso extends OrigemContaContabil implements Serializable {


    @Tabelavel
    @Pesquisavel
    @Etiqueta("Origem do Recurso")
    @Enumerated(EnumType.STRING)
    private OrigemSuplementacaoORC origemSuplementacaoORC;

    public OCCOrigemRecurso() {
    }

    public OCCOrigemRecurso(OrigemSuplementacaoORC origemSuplementacaoORC, TagOCC tagOCC, ContaContabil contaContabil, Date inicioVigencia, Date fimVigencia, Boolean reprocessar, OrigemContaContabil origemContaContabil) {
        super(tagOCC, contaContabil, inicioVigencia, fimVigencia, reprocessar, origemContaContabil);
        this.origemSuplementacaoORC = origemSuplementacaoORC;
    }


    public OrigemSuplementacaoORC getOrigemSuplementacaoORC() {
        return origemSuplementacaoORC;
    }

    public void setOrigemSuplementacaoORC(OrigemSuplementacaoORC origemSuplementacaoORC) {
        this.origemSuplementacaoORC = origemSuplementacaoORC;
    }
}
