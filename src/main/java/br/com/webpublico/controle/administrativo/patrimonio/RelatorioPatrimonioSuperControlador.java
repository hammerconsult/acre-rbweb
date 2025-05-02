package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.administrativo.relatorio.FiltroPatrimonio;
import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.PrettyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@ManagedBean
@ViewScoped
public class RelatorioPatrimonioSuperControlador implements Serializable {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    protected FiltroPatrimonio filtro;

    public String montaNomeSecretaria() {
        String nome = "";
        if (filtro.getHierarquiaAdm() != null) {
            HierarquiaOrganizacional secretaria = hierarquiaOrganizacionalFacade.recuperarSecretariaAdministrativaVigente(filtro.getHierarquiaAdm(), sistemaFacade.getDataOperacao());
            if (secretaria == null) {
                throw new ExcecaoNegocioGenerica("Secretaria não encontrada para hierarquia administrativa " + filtro.getHierarquiaAdm() + ".");
            }
            nome = secretaria.getDescricao().toUpperCase();
        }
        if (filtro.getHierarquiaAdm() == null && filtro.getHierarquiaOrc() != null) {
            HierarquiaOrganizacional hierararAdm = hierarquiaOrganizacionalFacade.recuperarAdministrativaDaOrcamentariaVigente(filtro.getHierarquiaOrc(), sistemaFacade.getDataOperacao());
            if (hierararAdm == null) {
                throw new ExcecaoNegocioGenerica("Hierarquia Administrativa não encontrada para hierarquia orçamentária " + filtro.getHierarquiaOrc() + ".");
            }
            HierarquiaOrganizacional secretaria = hierarquiaOrganizacionalFacade.recuperarSecretariaAdministrativaVigente(hierararAdm, sistemaFacade.getDataOperacao());
            if (secretaria == null) {
                throw new ExcecaoNegocioGenerica("Secretaria não encontrada para hierarquia administrativa " + hierararAdm + ".");
            }
            nome = secretaria.getDescricao().toUpperCase();
        }
        return nome;
    }

    protected void validarDataInicialPosteriorDataFinal() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if ((filtro.getInicioVigencia() != null && filtro.getFimVigencia() != null)
            && filtro.getInicioVigencia().after(filtro.getFimVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de inicio de vigência deve ser inferior a data de final de vigência");
        }
        ve.lancarException();
    }

    public String getUrlAtual() {
        return PrettyContext.getCurrentInstance().getRequestURL().toString();
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentaria(String parte) {
        if (filtro.getHierarquiaAdm() != null) {
            return hierarquiaOrganizacionalFacade.retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(filtro.getHierarquiaAdm().getSubordinada(), filtro.getDataReferencia());
        }
        return hierarquiaOrganizacionalFacade.filtraPorNivel(parte.trim(), "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), filtro.getDataReferencia());
    }

    public List<HierarquiaOrganizacional> completarHierarquiaAdministrativa(String parte) {
        return hierarquiaOrganizacionalFacade.buscarHierarquiaUsuarioPorTipoData(parte.trim(), sistemaFacade.getUsuarioCorrente(), filtro.getDataReferencia(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacionalAdministrativaOndeUsuarioLogadoEhGestorPatrimonio(String parte) {
        return hierarquiaOrganizacionalFacade.buscarHierarquiaOrganizacionalAdministrativaDoUsuario(parte, null,
            getSistemaFacade().getDataOperacao(), getSistemaFacade().getUsuarioCorrente(), filtro.getHierarquiaOrc(), Boolean.TRUE);
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacionalOrcamentariaOndeUsuarioLogadoEhGestorPatrimonio(String parte) {
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(parte, null,
            getSistemaFacade().getDataOperacao(), getSistemaFacade().getUsuarioCorrente(), filtro.getHierarquiaAdm());
    }

    public List<SelectItem> getEstadosDeConservacaoBensParaLevantamentosDeBensPatrimoniais() {
        return Util.getListSelectItem(Arrays.asList(EstadoConservacaoBem.getValoresPossiveisParaLevantamentoDeBemPatrimonial()));
    }

    public List<SelectItem> retornarHierarquiasOrcamentarias() {
        List<SelectItem> toReturn = Lists.newArrayList();
        if (filtro.getHierarquiaAdm() != null && filtro.getHierarquiaAdm().getSubordinada() != null) {
            toReturn.add(new SelectItem(null, ""));
            for (HierarquiaOrganizacional obj : hierarquiaOrganizacionalFacade.retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(filtro.getHierarquiaAdm().getSubordinada(), getSistemaFacade().getDataOperacao())) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
            return toReturn;
        }
        toReturn.add(new SelectItem(null, ""));
        for (HierarquiaOrganizacional obj : hierarquiaOrganizacionalFacade.retornaHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(getSistemaFacade().getUsuarioCorrente(), getSistemaFacade().getDataOperacao())) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public String montarNomeMunicipio() {
        return "MUNICÍPIO DE " + sistemaFacade.getMunicipio().toUpperCase();
    }

    public void novoFiltroRelatorio(){
        filtro = new FiltroPatrimonio();
    }

    public FiltroPatrimonio getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroPatrimonio filtro) {
        this.filtro = filtro;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
