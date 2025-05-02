package br.com.webpublico.controle.rh.cadastrofuncional;

import br.com.webpublico.controle.PesquisaGenericaRHControlador;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class AssentamentoFuncionaPesquisaGenericaControlador extends PesquisaGenericaRHControlador implements Serializable {

    public AssentamentoFuncionaPesquisaGenericaControlador() {
        setNomeVinculo("obj.vinculoFP");
    }

}
