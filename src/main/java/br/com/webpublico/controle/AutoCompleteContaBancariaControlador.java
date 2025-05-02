package br.com.webpublico.controle;

import br.com.webpublico.entidades.ContaBancariaEntidade;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoContaFinanceira;
import br.com.webpublico.enums.TipoRecursoSubConta;
import br.com.webpublico.negocios.ContaBancariaEntidadeFacade;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 24/07/14
 * Time: 17:10
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class AutoCompleteContaBancariaControlador implements Serializable {
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    private UnidadeOrganizacional unidadeOrganizacional;
    private FonteDeRecursos fonteDeRecursos;
    private List<TipoContaFinanceira> tiposContasFinanceira;
    private List<TipoRecursoSubConta> tipoRecursoSubContas;
    protected static final Logger logger = LoggerFactory.getLogger(AutoCompleteContaBancariaControlador.class);

    public List<TipoContaFinanceira> getTiposContasFinanceira() {
        return tiposContasFinanceira;
    }

    public void setTiposContasFinanceira(List<TipoContaFinanceira> tiposContasFinanceira) {
        this.tiposContasFinanceira = tiposContasFinanceira;
    }

    public List<TipoRecursoSubConta> getTipoRecursoSubContas() {
        return tipoRecursoSubContas;
    }

    public void setTipoRecursoSubContas(List<TipoRecursoSubConta> tipoRecursoSubContas) {
        this.tipoRecursoSubContas = tipoRecursoSubContas;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public void novo(UnidadeOrganizacional uo, FonteDeRecursos fr, List<TipoContaFinanceira> tipos, List<TipoRecursoSubConta> tipoRecursos) {
        this.unidadeOrganizacional = uo;
        this.fonteDeRecursos = fr;
        this.tipoRecursoSubContas = tipoRecursos;
        this.tiposContasFinanceira = tipos;
    }

    public List<ContaBancariaEntidade> completaContaBancariaEntidade(String parte) {
        try {
            return contaBancariaEntidadeFacade.listaFiltrandoAtivaPorUnidade(parte.trim(), unidadeOrganizacional, getSistemaControlador().getExercicioCorrente(), fonteDeRecursos, tiposContasFinanceira, tipoRecursoSubContas);
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
            return new ArrayList<>();
        }
    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public String montaTitleContaBancaria() {
        String retorno = "Informe uma Conta Bancária filtrando por ";
        if (unidadeOrganizacional != null) {
            retorno += " Unidade " + unidadeOrganizacional.getDescricao() + ",";
        }
        if (fonteDeRecursos != null) {
            retorno += " Fonte " + fonteDeRecursos + ",";
        }
        if (tiposContasFinanceira != null) {
            retorno += " Tipos " + contaBancariaEntidadeFacade.getSubContaFacade().getTiposContaFinanceiraAsString(tiposContasFinanceira) + ",";
        }
        if (tipoRecursoSubContas != null) {
            retorno += " Tipos de Recursos " + contaBancariaEntidadeFacade.getSubContaFacade().getTiposRecursosAsString(tipoRecursoSubContas) + ",";
        }
        retorno = retorno.substring(0, retorno.length() - 1) + ".";
        return retorno;
    }
}
