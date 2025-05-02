/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.EstornoMovimentoDividaPublica;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.MovimentoDividaPublica;
import br.com.webpublico.enums.OperacaoMovimentoDividaPublica;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EstornoMovimentoDividaPublicaFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.EntidadeMetaData;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Renato
 */
@ManagedBean
@SessionScoped
public class EstornoMovimentoDividaPublicaControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private EstornoMovimentoDividaPublicaFacade estornoMovimentoDividaPublicaFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ConverterAutoComplete converterMovimentoDividaPublica;
    private List<MovimentoDividaPublica> listaMovimentosFiltros;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public EstornoMovimentoDividaPublicaControlador() {
        metadata = new EntidadeMetaData(EstornoMovimentoDividaPublica.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return estornoMovimentoDividaPublicaFacade;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<MovimentoDividaPublica> getListaMovimentosFiltros() {
        return listaMovimentosFiltros;
    }

    public void setListaMovimentosFiltros(List<MovimentoDividaPublica> listaMovimentosFiltros) {
        this.listaMovimentosFiltros = listaMovimentosFiltros;
    }

    public ConverterAutoComplete getConverterMovimentoDividaPublica() {
        if (converterMovimentoDividaPublica == null) {
            converterMovimentoDividaPublica = new ConverterAutoComplete(MovimentoDividaPublica.class, estornoMovimentoDividaPublicaFacade.getMovimentoDividaPublicaFacade());
        }
        return converterMovimentoDividaPublica;
    }

    public List<MovimentoDividaPublica> completaMovimentoDividaPublica(String parte) {
        return listaMovimentosFiltros = estornoMovimentoDividaPublicaFacade.getMovimentoDividaPublicaFacade().filtraPorUnidadeOrganizacionalOperacao(parte.trim(), hierarquiaOrganizacional.getSubordinada(), ((EstornoMovimentoDividaPublica) selecionado).getOperacaoMovimentoDividaPublica(), ((EstornoMovimentoDividaPublica) selecionado).getData());
    }

    public List<SelectItem> getOperacoes() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));
        for (OperacaoMovimentoDividaPublica operacao : OperacaoMovimentoDividaPublica.values()) {
            retorno.add(new SelectItem(operacao, operacao.getDescricao()));
        }
        return retorno;
    }

    public void copiaValorProSaldo() {
        ((EstornoMovimentoDividaPublica) selecionado).setSaldo(((EstornoMovimentoDividaPublica) selecionado).getValor());
    }

    @Override
    public void novo() {
        super.novo();
        hierarquiaOrganizacional = null;
        limpaFiltros();
        ((EstornoMovimentoDividaPublica) selecionado).setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
    }

    @Override
    public String salvar() {
        validaHierarquiaOrganizacional();
        if (!validaNumeroNegativo()) {
            return "edita";
        }
        EstornoMovimentoDividaPublica estorno = (EstornoMovimentoDividaPublica) selecionado;

        estorno.setNumero(estornoMovimentoDividaPublicaFacade.retornaUltimoCodigoLong());
        if (validaCampos()) {
            if (((EstornoMovimentoDividaPublica) selecionado).getValor().compareTo(((EstornoMovimentoDividaPublica) selecionado).getMovimentoDividaPublica().getValor()) > 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar salvar!", "Valor do estorno não pode ser maior que do movimento: " + new DecimalFormat("#,###,##0.00").format(((EstornoMovimentoDividaPublica) selecionado).getMovimentoDividaPublica().getValor())));
                return "edita";
            }
            try {
                estornoMovimentoDividaPublicaFacade.getHierarquiaOrganizacionalFacade().validaVigenciaHIerarquiaAdministrativaOrcamentaria(estorno.getUnidadeOrganizacionalAdm(), estorno.getUnidadeOrganizacional(), estorno.getData());
                estornoMovimentoDividaPublicaFacade.gerarSaldo(((EstornoMovimentoDividaPublica) selecionado));
                lista = null;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com Sucesso", ""));
                return caminho();
            } catch (ExcecaoNegocioGenerica e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exceção do sistema!", e.getMessage()));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exceção do sistema!", e.getMessage()));
            }

        }
        return "edita";
    }

    private boolean validaNumeroNegativo() {
        if (((EstornoMovimentoDividaPublica) selecionado).getValor().compareTo(BigDecimal.ZERO) <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar salvar!", "O campo Valor não pode ser negativo ou igual a 0 (ZERO)."));
            return false;
        }
        return true;
    }

    private void validaHierarquiaOrganizacional() {
        if (hierarquiaOrganizacional != null) {
            ((EstornoMovimentoDividaPublica) selecionado).setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar salvar!", "O campo Unidade Organizacional é obrigatório."));
        }
    }

    public void filtrarMovimentoDividaPublica() {
        if (validaFiltros()) {
            listaMovimentosFiltros = estornoMovimentoDividaPublicaFacade.getMovimentoDividaPublicaFacade().filtraPorUnidadeOrganizacionalOperacao("", hierarquiaOrganizacional.getSubordinada(), ((EstornoMovimentoDividaPublica) selecionado).getOperacaoMovimentoDividaPublica(), ((EstornoMovimentoDividaPublica) selecionado).getData());
        }
    }

    public void limpaFiltros() {
        listaMovimentosFiltros = null;
    }

    private boolean validaFiltros() {
        boolean deuCerto = true;
        if (hierarquiaOrganizacional == null) {
            deuCerto = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar filtrar!", "Informe a Unidade Organizacional para filtrar os movimentos."));
        }
        if (((EstornoMovimentoDividaPublica) selecionado).getOperacaoMovimentoDividaPublica() == null) {
            deuCerto = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar filtrar!", "Informe a Operação do Movimento para filtrar os movimentos."));
        }
        return deuCerto;
    }

    public void limpaMovimentoDividaPublicaSelecionado() {
        ((EstornoMovimentoDividaPublica) selecionado).setMovimentoDividaPublica(null);
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
