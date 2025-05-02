package br.com.webpublico.dashboard.controlador;


import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.ContaFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import com.google.common.collect.Lists;
import org.joda.time.LocalDate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;


@ManagedBean(name = "dashboardContaPorCategoriaControlador")
@ViewScoped
public class DashboardContaPorCategoriaControlador implements Serializable {


    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    @EJB
    private ContaFacade contaFacade;

    @EJB
    private ExercicioFacade exercicioFacade;

    private List<Long> idContas;
    private List<Long> idNaturezaDaDespesa;

    private List<Conta> contasDeNatureza;
    private List<Conta> contasDeElemento;


    public DashboardContaPorCategoriaControlador() {

    }

    public List<Conta> getCategoriaDespesa() {
        LocalDate ano = LocalDate.now();
        Exercicio exercicio = exercicioFacade.recuperarExercicioPeloAno(ano.getYear());
        return contaFacade.buscarContasDespesaAteNivelPorExercicio("", exercicio, 1);
    }


    public void recuperarNaturezaDaDespesa() {
        if (idContas != null) {
            contasDeNatureza = contaFacade.buscarContasDespesaFilhas(idContas);
        } else {
            contasDeNatureza = Lists.newArrayList();
        }
    }

    public void recuperarElementoDaDespesa() {
        if (idContas != null) {
            contasDeElemento = contaFacade.buscarContasDespesaAteNivel(idNaturezaDaDespesa, 4);
        } else {
            contasDeElemento = Lists.newArrayList();
        }
    }

    public List<Exercicio> getExercicios() {
        LocalDate anoInicial = LocalDate.now();
        LocalDate anoFinal = anoInicial.minusYears(10);
        return exercicioFacade.buscarPorIntervaloDeAno(anoFinal.getYear(), anoInicial.getYear(), true);
    }

    public List<HierarquiaOrganizacional> getHierarquias() {
        Date dataOperacao = hierarquiaOrganizacionalFacade.getSistemaFacade().getDataOperacao();
        return hierarquiaOrganizacionalFacade.listaTodasPorNivel("", "2", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), dataOperacao);
    }

    public void atribuirIdConta() {
        Map<String, String> requestParameterMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String conta = requestParameterMap.get("conta");

        if (idContas == null) {
            idContas = Lists.newArrayList();
        }
        idContas.add(Long.valueOf(conta));

        recuperarNaturezaDaDespesa();
    }

    public void removerIdConta() {
        Map<String, String> requestParameterMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String conta = requestParameterMap.get("conta");

        if (idContas == null) {
            idContas = Lists.newArrayList();
        }
        idContas.remove(Long.valueOf(conta));

        recuperarNaturezaDaDespesa();
    }

    public void atribuirIdNatureza() {
        Map<String, String> requestParameterMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String conta = requestParameterMap.get("conta");

        if (idNaturezaDaDespesa == null) {
            idNaturezaDaDespesa = Lists.newArrayList();
        }
        idNaturezaDaDespesa.add(Long.valueOf(conta));

        recuperarElementoDaDespesa();
    }

    public void removerIdNatureza() {
        Map<String, String> requestParameterMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String conta = requestParameterMap.get("conta");

        if (idNaturezaDaDespesa == null) {
            idNaturezaDaDespesa = Lists.newArrayList();
        }
        idNaturezaDaDespesa.remove(Long.valueOf(conta));

        recuperarElementoDaDespesa();
    }


    public List<Conta> getContasDeNatureza() {
        return contasDeNatureza;
    }

    public void setContasDeNatureza(List<Conta> contasDeNatureza) {
        this.contasDeNatureza = contasDeNatureza;
    }

    public List<Conta> getContasDeElemento() {
        return contasDeElemento;
    }

    public void setContasDeElemento(List<Conta> contasDeElemento) {
        this.contasDeElemento = contasDeElemento;
    }
}
