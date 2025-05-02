package br.com.webpublico.negocios.administrativo.pesquisabem;

import br.com.webpublico.controle.ManipuladorDeBemImpl;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.BemFacade;
import br.com.webpublico.util.FacesUtil;
import org.primefaces.model.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 21/05/14
 * Time: 17:08
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class PesquisaBem {
    public BemFacade bemFacade;
    private Bem bem;
    private TipoBem tipoBem;
    private TipoGrupo tipoGrupo;
    private GrupoBem grupoBem;
    private EstadoConservacaoBem estadoConservacaoBem;
    private SituacaoConservacaoBem situacaoConservacaoBem;
    private TreeNode selectedNodeUnidadePesquisa;
    private HierarquiaOrganizacional hierarquiaOrganizacionalPesquisa;
    private HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria;
    public String nomeDaTabelaSubClasseEventoBem;
    public String nomeDaColunaDeDiferenciacao;
    public String valorColunaDeDiferenciacao;
    private ManipuladorDeBemImpl manipulador;
    private boolean pesquisou;

    public PesquisaBem() {
    }

    public PesquisaBem(BemFacade bemFacade, Class subClasse,
                       String nomeDaColunaDeDiferenciacao, String valorColunaDeDiferenciacao, ManipuladorDeBemImpl manipuladorDeBem) {
        this.pesquisou = false;
        this.bemFacade = bemFacade;
        this.manipulador = manipuladorDeBem;
        try {
            this.nomeDaTabelaSubClasseEventoBem = this.bemFacade.getSingletonWPEntities().getByClass(subClasse).tableName;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.nomeDaColunaDeDiferenciacao = nomeDaColunaDeDiferenciacao;
        this.valorColunaDeDiferenciacao = valorColunaDeDiferenciacao;
    }

    public void selecionarHierarquiaDaArvore() {
        if (selectedNodeUnidadePesquisa == null)
            return;

        HierarquiaOrganizacional ho = (HierarquiaOrganizacional) selectedNodeUnidadePesquisa.getData();
        UsuarioSistema usuarioCorrente = this.bemFacade.getSistemaFacade().getUsuarioCorrente();
        List<HierarquiaOrganizacional> hierarquias = this.bemFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPaiEFilhoOndeUsuarioEhGestorPatrimonio(ho.getSubordinada().getDescricao(), null, usuarioCorrente, bemFacade.getSistemaFacade().getDataOperacao());

        for (HierarquiaOrganizacional hierarquiaOrg : hierarquias) {
            if (hierarquiaOrg.equals(ho)) {
                hierarquiaOrganizacionalPesquisa = ho;
                return;
            }
        }

        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O usuário " + usuarioCorrente.getNome() + " não possui permissão de gestor patrimonial nesta secretaria.");
    }

    public List<Bem> pesquisar() throws ExcecaoNegocioGenerica {
        if (validarAtributosDaPesquisa()) {
            try {
                List<Bem> bens = bemFacade.recuperarBensParaPesquisa(this);
                pesquisou = true;
                if (bens == null || bens.isEmpty()) {
                    return new ArrayList();
                }
                return bens;
            } catch (ExcecaoNegocioGenerica ex) {
                FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            }
        }
        return new ArrayList<>();
    }

    public boolean validarAtributosDaPesquisa() {
        boolean retorno = true;

        if (hierarquiaOrganizacionalPesquisa == null) {
            retorno = false;
            exibirMensagemCampoUnidadeOrganizacionalObrigatorio();
        }

        if (tipoBem == null) {
            retorno = false;
            exibirMensagemCampoTipoBemObrigatorio();
        }

        return retorno;
    }

    public List<Bem> pesquisar(String valorColunaDeDiferenciacao) throws ExcecaoNegocioGenerica {
        this.valorColunaDeDiferenciacao = valorColunaDeDiferenciacao;
        return pesquisar();
    }

    private void exibirMensagemCampoUnidadeOrganizacionalObrigatorio() {
        FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O campo Unidade Organizacional é obrigatório para realizar a pesquisa.");
    }

    private void exibirMensagemCampoTipoBemObrigatorio() {
        FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O campo Tipo de Bem é obrigatório para realizar a pesquisa.");
    }

    public TreeNode getSelectedNodeUnidadePesquisa() {
        return selectedNodeUnidadePesquisa;
    }

    public void setSelectedNodeUnidadePesquisa(TreeNode selectedNodeUnidadePesquisa) {
        this.selectedNodeUnidadePesquisa = selectedNodeUnidadePesquisa;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalPesquisa() {
        return hierarquiaOrganizacionalPesquisa;
    }

    public void setHierarquiaOrganizacionalPesquisa(HierarquiaOrganizacional hierarquiaOrganizacionalPesquisa) {
        if (pesquisou &&
            (hierarquiaOrganizacionalPesquisa != null && !hierarquiaOrganizacionalPesquisa.equals(this.hierarquiaOrganizacionalPesquisa)) ||
            (hierarquiaOrganizacionalPesquisa == null && this.hierarquiaOrganizacionalPesquisa != null)) {
            manipulador.reinicializarManipulador();
        }
        this.hierarquiaOrganizacionalPesquisa = hierarquiaOrganizacionalPesquisa;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return hierarquiaOrganizacionalPesquisa.getSubordinada();
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public SituacaoConservacaoBem getSituacaoConservacaoBem() {
        return situacaoConservacaoBem;
    }

    public void setSituacaoConservacaoBem(SituacaoConservacaoBem situacaoConservacaoBem) {
        this.situacaoConservacaoBem = situacaoConservacaoBem;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalOrcamentaria() {
        return hierarquiaOrganizacionalOrcamentaria;
    }

    public void setHierarquiaOrganizacionalOrcamentaria(HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria) {
        this.hierarquiaOrganizacionalOrcamentaria = hierarquiaOrganizacionalOrcamentaria;
    }

    public EstadoConservacaoBem getEstadoConservacaoBem() {
        return estadoConservacaoBem;
    }

    public void setEstadoConservacaoBem(EstadoConservacaoBem estadoConservacaoBem) {
        this.estadoConservacaoBem = estadoConservacaoBem;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        if (pesquisou && tipoBem != this.tipoBem) {
            manipulador.reinicializarManipulador();
        }
        this.tipoBem = tipoBem;
    }
}
