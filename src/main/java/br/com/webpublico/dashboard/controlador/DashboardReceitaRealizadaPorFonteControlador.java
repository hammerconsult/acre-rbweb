package br.com.webpublico.dashboard.controlador;


import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquia;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.FonteDeRecursosFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import org.joda.time.LocalDate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "dashboardReceitaRealizadaPorFonteControlador")
@ViewScoped
public class DashboardReceitaRealizadaPorFonteControlador implements Serializable {

    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;


    public DashboardReceitaRealizadaPorFonteControlador() {
    }

    public List<Exercicio> getExercicios() {
        LocalDate anoInicial = LocalDate.now();
        LocalDate anoFinal = anoInicial.minusYears(10);
        return exercicioFacade.buscarPorIntervaloDeAno(anoFinal.getYear(), anoInicial.getYear(), true);
    }

    public List<FonteDeRecursos> getFontes() {
        LocalDate anoInicial = LocalDate.now();
        Exercicio exercicioPorAno = fonteDeRecursosFacade.getExercicioFacade().getExercicioPorAno(anoInicial.getYear());
        return fonteDeRecursosFacade.listaPorExercicio(exercicioPorAno);
    }

    public List<HierarquiaOrganizacional> getHierarquias() {
        Date dataOperacao = hierarquiaOrganizacionalFacade.getSistemaFacade().getDataOperacao();
        return hierarquiaOrganizacionalFacade.listaTodasPorNivel("", "2", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), dataOperacao);
    }
}
