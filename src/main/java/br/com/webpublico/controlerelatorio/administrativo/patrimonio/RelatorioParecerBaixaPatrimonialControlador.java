package br.com.webpublico.controlerelatorio.administrativo.patrimonio;

import br.com.webpublico.controle.RelatorioPatrimonioControlador;
import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ParecerBaixaPatrimonial;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.administrativo.relatorio.patrimonio.ParecerBaixaPatrimonialVO;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.EntidadeFacade;
import br.com.webpublico.relatoriofacade.administrativo.patrimonio.RelatorioParecerBaixaPatrimonialFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zaca on 30/03/17.
 */
@ManagedBean
@ViewScoped
public class RelatorioParecerBaixaPatrimonialControlador extends RelatorioPatrimonioControlador {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioPatrimonioControlador.class);

    private static final String MODULO = "Patrimônio";
    private static final String MUNICIPIO = "MUNICIPIO DE  RIO BRANCO - AC";
    private static final String ARQUIVO_JASPER = "ParecerBaixaPatrimonial.jasper";

    @EJB
    private RelatorioParecerBaixaPatrimonialFacade parecerBaixaPatrimonialFacade;
    @EJB
    private EntidadeFacade entidadeFacade;

    private HashMap parameters;
    private JRBeanCollectionDataSource jrBeanCollection;
    private List<ParametrosRelatorios> filters;

    public RelatorioParecerBaixaPatrimonialFacade getParecerBaixaPatrimonialFacade() {
        return parecerBaixaPatrimonialFacade;
    }

    public void emitirParecerBaixaBemMovel(ParecerBaixaPatrimonial parecerBaixaPatrimonial) {
        emitirParecerBaixaPatrimonial(TipoBem.MOVEIS, parecerBaixaPatrimonial);
    }

    private void emitirParecerBaixaPatrimonial(TipoBem tipoBem, ParecerBaixaPatrimonial selecionado) {
        try {
            validarParecerBaixaPatrimonial(selecionado);
            adicionarParameters(selecionado);
            adicionarFilters(selecionado);
            carregarBeanCollections();
            gerarRelatorioComDadosEmCollection(ARQUIVO_JASPER, getParameters(), getJrBeanCollection());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void adicionarFilters(ParecerBaixaPatrimonial selecionado) {
        setFilters(Lists.<ParametrosRelatorios>newArrayList());
        getFilters().add(new ParametrosRelatorios(" parecer.id ", ":parecer", null, OperacaoRelatorio.IGUAL, selecionado.getId(), null, 1, Boolean.FALSE));
    }

    private void carregarBeanCollections() {
        List<ParecerBaixaPatrimonialVO> parecerBaixaPatrimonialVOs = getParecerBaixaPatrimonialFacade().emitirParecerBaixaPatrimonial(getFilters());
        setJrBeanCollection(new JRBeanCollectionDataSource(parecerBaixaPatrimonialVOs));
    }

    private void adicionarParameters(ParecerBaixaPatrimonial selecionado) {
        setParameters(Maps.newHashMap());
        adicionarParameterBrasao();
        adicionarParameterMunicipio();
        adicionarParameterModulo();
        adicionarParameterUsuario();
        adicionarParameterNomeRelatorio();
        adicionarParameterEntidade(selecionado);
    }

    private void adicionarParameterMunicipio() {
        getParameters().put("MUNICIPIO", MUNICIPIO);
    }

    private void adicionarParameterEntidade(ParecerBaixaPatrimonial selecionado) {
        String descricaoEntidade = " ";
        HierarquiaOrganizacional hierarquiaAdministrativa = selecionado.getSolicitacaoBaixa().getHierarquiaAdministrativa();
        if (hierarquiaAdministrativa != null) {
            Entidade entidade = getEntidadeFacade().recuperarEntidadePorUnidadeOrganizacional(hierarquiaAdministrativa.getSubordinada());
            descricaoEntidade = entidade != null ? entidade.getNome().toUpperCase() : " ";
        }
        getParameters().put("ENTIDADE", descricaoEntidade);
    }

    private void adicionarParameterNomeRelatorio() {
        getParameters().put("NOMERELATORIO", "RELATÓRIO DE PARECER DE BAIXA DE BENS MÓVEIS");
    }

    private void adicionarParameterBrasao() {
        getParameters().put("BRASAO", getCaminhoImagem());
    }

    private void adicionarParameterModulo() {
        getParameters().put("MODULO", MODULO);
    }

    private void adicionarParameterUsuario() {
        if (getSistemaControlador().getUsuarioCorrente().getPessoaFisica() != null) {
            getParameters().put("USUARIO", getSistemaControlador().getUsuarioCorrente().getPessoaFisica().getNome());
        } else {
            getParameters().put("USUARIO", getSistemaControlador().getUsuarioCorrente().getLogin());
        }
    }

    private void validarParecerBaixaPatrimonial(ParecerBaixaPatrimonial selecionado) {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado == null || selecionado.getId() == null) {
            ve.adicionarMensagemErroGerarRelatorio();
        }

        ve.lancarException();
    }


    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public HashMap getParameters() {
        return parameters;
    }

    public void setParameters(HashMap parameters) {
        this.parameters = parameters;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public JRBeanCollectionDataSource getJrBeanCollection() {
        return jrBeanCollection;
    }

    public void setJrBeanCollection(JRBeanCollectionDataSource jrBeanCollection) {
        this.jrBeanCollection = jrBeanCollection;
    }

    public List<ParametrosRelatorios> getFilters() {
        return filters;
    }

    public void setFilters(List<ParametrosRelatorios> filters) {
        this.filters = filters;
    }
}
