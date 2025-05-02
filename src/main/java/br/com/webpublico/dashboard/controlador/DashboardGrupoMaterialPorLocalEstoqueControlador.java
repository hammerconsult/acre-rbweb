package br.com.webpublico.dashboard.controlador;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.entidades.LocalEstoque;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.GrupoMaterialFacade;
import br.com.webpublico.negocios.LocalEstoqueFacade;
import org.joda.time.LocalDate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

/**
 * jorge created on 05/02/20.
 */
@ManagedBean(name = "dashboardGrupoMaterialPorLocalEstoque")
@ViewScoped
public class DashboardGrupoMaterialPorLocalEstoqueControlador {

    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;


    public DashboardGrupoMaterialPorLocalEstoqueControlador() {
    }

    public List<Exercicio> getExercicios() {
        LocalDate anoInicial = LocalDate.now();
        LocalDate anoFinal = anoInicial.minusYears(10);
        return exercicioFacade.buscarPorIntervaloDeAno(anoFinal.getYear(), anoInicial.getYear(), true);
    }

    public List<GrupoMaterial> getGrupoMaterial() {
        return grupoMaterialFacade.lista();
    }

    public List<LocalEstoque> getLocalEstoque() {
        return localEstoqueFacade.lista();
    }

}
