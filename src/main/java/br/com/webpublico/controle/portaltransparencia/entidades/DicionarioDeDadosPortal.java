package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.DicionarioDeDados;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class DicionarioDeDadosPortal extends EntidadePortalTransparencia {

    @ManyToOne
    @Etiqueta("Dicionário de Dados")
    private DicionarioDeDados dicionarioDeDados;

    public DicionarioDeDadosPortal() {
    }

    public DicionarioDeDadosPortal(DicionarioDeDados dicionarioDeDados) {
        this.dicionarioDeDados = dicionarioDeDados;
        super.setTipo(TipoObjetoPortalTransparencia.DICIONARIO_DE_DADOS);
    }

    public DicionarioDeDados getDicionarioDeDados() {
        return dicionarioDeDados;
    }

    public void setDicionarioDeDados(DicionarioDeDados dicionarioDeDados) {
        this.dicionarioDeDados = dicionarioDeDados;
    }

    @Override
    public String toString() {
        return dicionarioDeDados.toString();
    }
}
