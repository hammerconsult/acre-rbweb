package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.UnidadeOrganizacional;
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
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
public class UnidadePrefeituraPortal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Prefeitura")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    private PrefeituraPortal prefeitura;
    @Etiqueta("Unidade")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional unidade;

    public UnidadePrefeituraPortal() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PrefeituraPortal getPrefeitura() {
        return prefeitura;
    }

    public void setPrefeitura(PrefeituraPortal prefeitura) {
        this.prefeitura = prefeitura;
    }

    public UnidadeOrganizacional getUnidade() {
        return unidade;
    }

    public void setUnidade(UnidadeOrganizacional unidade) {
        this.unidade = unidade;
    }

    @Override
    public String toString() {
        return unidade.toString();
    }
}
