package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 23/01/14
 * Time: 18:33
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class OCCGrupoBem extends OrigemContaContabil implements Serializable {

    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Grupo Patrimonial")
    private GrupoBem grupoBem;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Grupo")
    @Enumerated(EnumType.STRING)
    private TipoGrupo tipoGrupo;


    public OCCGrupoBem() {
    }

    public OCCGrupoBem(TagOCC tagOCC, ContaContabil contaContabil, Date inicioVigencia, Date fimVigencia, Boolean reprocessar, OrigemContaContabil origem, TipoGrupo tipoGrupo, GrupoBem grupoBem) {
        super(tagOCC, contaContabil, inicioVigencia, fimVigencia, reprocessar, origem);
        this.tipoGrupo = tipoGrupo;
        this.grupoBem = grupoBem;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }
}
