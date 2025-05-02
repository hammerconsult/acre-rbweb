package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.Empenho;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by renato on 30/08/2016.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class UnidadePortal extends EntidadePortalTransparencia {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Unidade")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional unidade;

    public UnidadePortal() {

    }

    public UnidadePortal(UnidadeOrganizacional unidade) {
        this.unidade = unidade;
        super.setTipo(TipoObjetoPortalTransparencia.UNIDADE);
    }


    public UnidadeOrganizacional getUnidade() {
        return unidade;
    }

    public void setUnidade(UnidadeOrganizacional unidade) {
        this.unidade = unidade;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return unidade.toString();
    }
}
