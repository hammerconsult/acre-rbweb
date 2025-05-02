package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.CalamidadePublica;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class CalamidadePublicaPortal extends EntidadePortalTransparencia {

    @ManyToOne
    @Etiqueta("Calamidade Pública")
    private CalamidadePublica calamidadePublica;

    public CalamidadePublicaPortal() {
    }

    public CalamidadePublicaPortal(CalamidadePublica calamidadePublica) {
        this.calamidadePublica = calamidadePublica;
        super.setTipo(TipoObjetoPortalTransparencia.CALAMIDADE_PUBLICA);
    }

    public CalamidadePublica getCalamidadePublica() {
        return calamidadePublica;
    }

    public void setCalamidadePublica(CalamidadePublica calamidadePublica) {
        this.calamidadePublica = calamidadePublica;
    }

    @Override
    public String toString() {
        return calamidadePublica.toString();
    }
}
