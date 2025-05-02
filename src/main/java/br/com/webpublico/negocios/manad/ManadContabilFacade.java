package br.com.webpublico.negocios.manad;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.manad.Manad;
import br.com.webpublico.entidadesauxiliares.manad.ManadRegistro;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.interfaces.IManadRegistro;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.ManadUtil;
import br.com.webpublico.util.UtilRH;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 10/06/14
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ManadContabilFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeGestoraFacade unidadeGestoraFacade;
    @EJB
    private PPAFacade ppaFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private LOAFacade loaFacade;
    @EJB
    private CLPFacade clpFacade;
    @EJB
    private PrestacaoDeContasFacade prestacaoDeContasFacade;
    @EJB
    private PessoaFacade pessoaFacade;


    public void recuperarInformacoesArquivos(Manad manad) {
        criarRegistroLancamentoConabil(manad);
        criarRegistroOrgaoUnidade(manad);
        criarRegistroFuncaoSubFuncaoProgramas(manad);
    }

    private void criarRegistroFuncaoSubFuncaoProgramas(Manad manad) {
        PPA ppa = prestacaoDeContasFacade.getPpaFacade().ultimoPpaDoExercicio(manad.getExercicioInicial());
        List<ProgramaPPA> programaPPAs = prestacaoDeContasFacade.getProgramaPPAFacade().recuperaProgramasPPAOrdenandoPorCodigoQuePossuiLancamento(ppa);

        HashSet<Funcao> funcoes = new HashSet<>();
        HashSet<SubFuncao> subFuncoes = new HashSet<>();

        for (ProgramaPPA programaPPA : programaPPAs) {
            manad.getRegistros().add(new ManadRegistro(programaPPA, ManadRegistro.ManadRegistroTipo.L550, ManadRegistro.ManadModulo.CONTABIL, manad));
            for (AcaoPrincipal acaoPrincipal : programaPPA.getAcoes()) {
                manad.getRegistros().add(new ManadRegistro(acaoPrincipal, ManadRegistro.ManadRegistroTipo.L600, ManadRegistro.ManadModulo.CONTABIL, manad));
                for (AcaoPPA acaoPPA : acaoPrincipal.getProjetosAtividades()) {
                    manad.getRegistros().add(new ManadRegistro(acaoPPA, ManadRegistro.ManadRegistroTipo.L650, ManadRegistro.ManadModulo.CONTABIL, manad));
                    funcoes.add(acaoPPA.getFuncao());
                    subFuncoes.add(acaoPPA.getSubFuncao());
                }
            }
        }

        for (Funcao funcao : funcoes) {
            manad.getRegistros().add(new ManadRegistro(funcao, ManadRegistro.ManadRegistroTipo.L450, ManadRegistro.ManadModulo.CONTABIL, manad));
        }

        for (SubFuncao subFuncao : subFuncoes) {
            manad.getRegistros().add(new ManadRegistro(subFuncao, ManadRegistro.ManadRegistroTipo.L500, ManadRegistro.ManadModulo.CONTABIL, manad));
        }
    }


    private void criarRegistroOrgaoUnidade(Manad manad) {
        List<HierarquiaOrganizacional> orgaos = prestacaoDeContasFacade.getHierarquiaOrganizacionalFacade().listaFiltrandoPorOrgaoAndTipoOrcamentaria("", new Date());
        for (HierarquiaOrganizacional orgao : orgaos) {
            manad.getRegistros().add(new ManadRegistro(orgao, ManadRegistro.ManadRegistroTipo.L350, ManadRegistro.ManadModulo.CONTABIL, manad));
        }
        List<HierarquiaOrganizacional> unidades = prestacaoDeContasFacade.getHierarquiaOrganizacionalFacade().listaTodasPorNivel("", "3", "ORCAMENTARIA", new Date());
        for (HierarquiaOrganizacional unidade : unidades) {
            manad.getRegistros().add(new ManadRegistro(unidade, ManadRegistro.ManadRegistroTipo.L400, ManadRegistro.ManadModulo.CONTABIL, manad));
        }
    }

    public void criarConteudoArquivo(Manad manad) {
        StringBuilder conteudo = new StringBuilder();

        Collections.sort(manad.getRegistros(), new Comparator<ManadRegistro>() {
            @Override
            public int compare(ManadRegistro o1, ManadRegistro o2) {
                return o1.getTipoRegistro().compareTo(o2.getTipoRegistro());
            }
        });
        HashSet<Pessoa> fornecedores = new HashSet<Pessoa>();


        for (ManadRegistro registro : manad.getRegistros()) {
            if (registro.getModulo().equals(ManadRegistro.ManadModulo.CONTABIL)) {
                if (registro.getObjeto() instanceof LancamentoContabil) {
                    IManadRegistro imanad = (IManadRegistro) registro.getObjeto();
                    imanad = recuperarObjetoDoLancamento(imanad, fornecedores, manad);
                    try {
                        imanad.getConteudoManad(this, conteudo);
                    } catch (Exception e) {
                        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), imanad.toString());
                    }
                }
            }
        }
        for (Pessoa pessoa : fornecedores) {
            manad.getRegistros().add(new ManadRegistro(pessoa, ManadRegistro.ManadRegistroTipo.L750, ManadRegistro.ManadModulo.CONTABIL, manad));
        }

        manad.setQuantidadeLinhaBlocoL(0);
        for (ManadRegistro registro : manad.getRegistros()) {
            if (registro.getModulo().equals(ManadRegistro.ManadModulo.CONTABIL)) {
                manad.setQuantidadeLinhaBlocoL(manad.getQuantidadeLinhaBlocoL() + 1);
            }
        }

        for (ManadRegistro registro : manad.getRegistros()) {
            if (registro.getModulo().equals(ManadRegistro.ManadModulo.CONTABIL)) {
                if (registro.getTipoRegistro().equals(ManadRegistro.ManadRegistroTipo.L350)) {
                    criarConteudoArquivoOrgao(registro.getObjeto(), conteudo);
                } else if (registro.getTipoRegistro().equals(ManadRegistro.ManadRegistroTipo.L400)) {
                    criarConteudoArquivoUnidade(registro.getObjeto(), conteudo);
                } else if (registro.getTipoRegistro().equals(ManadRegistro.ManadRegistroTipo.L450)) {
                    criarConteudoArquivoFuncao(registro.getObjeto(), conteudo, manad);
                } else if (registro.getTipoRegistro().equals(ManadRegistro.ManadRegistroTipo.L500)) {
                    criarConteudoArquivoSubFuncao(registro.getObjeto(), conteudo, manad);
                } else if (registro.getTipoRegistro().equals(ManadRegistro.ManadRegistroTipo.L550)) {
                    criarConteudoArquivoProgramas(registro.getObjeto(), conteudo);
                } else if (registro.getTipoRegistro().equals(ManadRegistro.ManadRegistroTipo.L600)) {
                    criarConteudoArquivoAcaoPrincipal(registro.getObjeto(), conteudo);
                } else if (registro.getTipoRegistro().equals(ManadRegistro.ManadRegistroTipo.L650)) {
                    criarConteudoArquivoAcaoProjetoAtividade(registro.getObjeto(), conteudo);
                } else if (registro.getTipoRegistro().equals(ManadRegistro.ManadRegistroTipo.L750)) {
                    criarConteudoArquivoFornecedores(registro.getObjeto(), conteudo, manad);
                }
            }
        }

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.L990.name(), conteudo);
        ManadUtil.criaLinha(2, manad.getQuantidadeLinhaBlocoL() + "", conteudo);
        ManadUtil.quebraLinha(conteudo);

        manad.setConteudo(manad.getConteudo() + conteudo.toString());
    }

    private void criarConteudoArquivoFornecedores(Object objeto, StringBuilder conteudo, Manad manad) {
        Pessoa pessoa = (Pessoa) objeto;
        pessoa = pessoaFacade.recuperar(pessoa.getId());
        EnderecoCorreio enderecoPessoa = ManadUtil.getEnderecoPessoa(pessoa);

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.L750.name(), conteudo);
        ManadUtil.criaLinha(2, manad.getExercicioInicial().getAno().toString(), conteudo);
        ManadUtil.criaLinha(3, pessoa.getId().toString(), conteudo);
        ManadUtil.criaLinha(4, pessoa.getNome(), conteudo);
        ManadUtil.criaLinha(5, pessoa instanceof PessoaFisica ? "1" : "2", conteudo);
        ManadUtil.criaLinha(6, pessoa instanceof PessoaFisica ? "" : pessoa.getCpf_Cnpj(), conteudo);
        ManadUtil.criaLinha(7, pessoa instanceof PessoaFisica ? pessoa.getCpf_Cnpj() : "", conteudo);
        ManadUtil.criaLinha(8, "", conteudo);
        ManadUtil.criaLinha(9, enderecoPessoa.getEnderecoCompleto(), conteudo);
        ManadUtil.criaLinha(10, enderecoPessoa.getLocalidade(), conteudo);
        ManadUtil.criaLinha(11, enderecoPessoa.getUf(), conteudo);
        ManadUtil.criaLinha(12, enderecoPessoa.getCep(), conteudo);
        ManadUtil.criaLinha(13, "", conteudo);
        ManadUtil.quebraLinha(conteudo);
    }

    private void criarConteudoArquivoAcaoProjetoAtividade(Object objeto, StringBuilder conteudo) {
        AcaoPPA acaoPPA = (AcaoPPA) objeto;

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.L650.name(), conteudo);
        ManadUtil.criaLinha(2, acaoPPA.getExercicio().getAno().toString(), conteudo);
        ManadUtil.criaLinha(3, acaoPPA.getCodigo(), conteudo);
        ManadUtil.criaLinha(4, acaoPPA.getDescricao(), conteudo);
        ManadUtil.criaLinha(5, "0" + acaoPPA.getAcaoPrincipal().getTipoAcaoPPA().getCodigo(), conteudo);

        ManadUtil.quebraLinha(conteudo);

    }

    private void criarConteudoArquivoAcaoPrincipal(Object objeto, StringBuilder conteudo) {
        AcaoPrincipal acaoPrincipal = (AcaoPrincipal) objeto;

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.L600.name(), conteudo);
        ManadUtil.criaLinha(2, acaoPrincipal.getPrograma().getExercicio().getAno().toString(), conteudo);
        ManadUtil.criaLinha(3, acaoPrincipal.getCodigo(), conteudo);
        ManadUtil.criaLinha(4, acaoPrincipal.getDescricao(), conteudo);

        ManadUtil.quebraLinha(conteudo);
    }

    private void criarConteudoArquivoProgramas(Object objeto, StringBuilder conteudo) {
        ProgramaPPA programaPPA = (ProgramaPPA) objeto;

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.L550.name(), conteudo);
        ManadUtil.criaLinha(2, programaPPA.getExercicio().getAno().toString(), conteudo);
        ManadUtil.criaLinha(3, programaPPA.getCodigo(), conteudo);
        ManadUtil.criaLinha(4, programaPPA.getObjetivo(), conteudo);

        ManadUtil.quebraLinha(conteudo);
    }

    private void criarConteudoArquivoSubFuncao(Object objeto, StringBuilder conteudo, Manad manad) {
        SubFuncao subFuncao = (SubFuncao) objeto;

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.L500.name(), conteudo);
        ManadUtil.criaLinha(2, manad.getExercicioInicial().getAno().toString(), conteudo);
        ManadUtil.criaLinha(3, subFuncao.getCodigo(), conteudo);
        ManadUtil.criaLinha(4, subFuncao.getDescricao(), conteudo);

        ManadUtil.quebraLinha(conteudo);
    }

    private void criarConteudoArquivoFuncao(Object objeto, StringBuilder conteudo, Manad manad) {
        Funcao funcao = (Funcao) objeto;

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.L450.name(), conteudo);
        ManadUtil.criaLinha(2, manad.getExercicioInicial().getAno().toString(), conteudo);
        ManadUtil.criaLinha(3, funcao.getCodigo(), conteudo);
        ManadUtil.criaLinha(4, funcao.getDescricao(), conteudo);

        ManadUtil.quebraLinha(conteudo);
    }

    private void criarConteudoArquivoUnidade(Object objeto, StringBuilder conteudo) {
        HierarquiaOrganizacional unidade = (HierarquiaOrganizacional) objeto;

        HierarquiaOrganizacional orgao = getHierarquiaDaUnidade(unidade.getSuperior(), UtilRH.getDataOperacao());

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.L400.name(), conteudo);
        ManadUtil.criaLinha(2, unidade.getExercicio().getAno().toString(), conteudo);
        ManadUtil.criaLinha(3, orgao.getCodigoNo(), conteudo);
        ManadUtil.criaLinha(4, unidade.getCodigoNo(), conteudo);
        ManadUtil.criaLinha(5, unidade.getSubordinada().getDescricao(), conteudo);
        ManadUtil.criaLinha(6, "", conteudo);
        try {
            ManadUtil.criaLinha(7, unidade.getSubordinada().getEntidade().getPessoaJuridica().getCnpjSemFormatacao(), conteudo);
        } catch (Exception e) {
            ManadUtil.criaLinha(7, "", conteudo);
        }

        ManadUtil.quebraLinha(conteudo);
    }

    private void criarConteudoArquivoOrgao(Object objeto, StringBuilder conteudo) {
        HierarquiaOrganizacional orgao = (HierarquiaOrganizacional) objeto;

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.L350.name(), conteudo);
        ManadUtil.criaLinha(2, orgao.getExercicio().getAno().toString(), conteudo);
        ManadUtil.criaLinha(3, orgao.getCodigoNo(), conteudo);
        ManadUtil.criaLinha(4, orgao.getSubordinada().getDescricao(), conteudo);
        ManadUtil.quebraLinha(conteudo);
    }

    public HierarquiaOrganizacional getHierarquiaDaUnidade(UnidadeOrganizacional uo, Date data) {
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(data, uo, TipoHierarquiaOrganizacional.ORCAMENTARIA);
    }

    private void criarRegistroLancamentoConabil(Manad manad) {
        List<LancamentoContabil> lancamentoContabils = recuperLancamentosContabeis(manad.getExercicioInicial(), manad.getExercicioFinal(), manad.getTipoEventoContabils());
        lancamentoContabils = prestacaoDeContasFacade.prepararLancamentoContabeisPorIdMovimentoOrigem(lancamentoContabils);
        for (LancamentoContabil lancamentoContabil : lancamentoContabils) {
            manad.getRegistros().add(new ManadRegistro(lancamentoContabil, manad));

        }
    }

    private IManadRegistro recuperarObjetoDoLancamento(IManadRegistro registro, HashSet<Pessoa> fornecedores, Manad manad) {
        if (registro instanceof LancamentoContabil) {
            LancamentoContabil lancamentoContabil = (LancamentoContabil) registro;
            if (lancamentoContabil.getItemParametroEvento().getParametroEvento() != null) {
                if (lancamentoContabil.getItemParametroEvento().getParametroEvento().getIdOrigem() != null) {
                    String idOrigem = lancamentoContabil.getItemParametroEvento().getParametroEvento().getIdOrigem();
                    String classeOrigem = lancamentoContabil.getItemParametroEvento().getParametroEvento().getClasseOrigem();

                    IManadRegistro objetoRetorno = (IManadRegistro) prestacaoDeContasFacade.buscar(Long.parseLong(idOrigem), classeOrigem);

                    if (lancamentoContabil.getTipoEventoContabil().equals(TipoEventoContabil.EMPENHO_DESPESA) && lancamentoContabil.isLancamentoNormal()) {
                        fornecedores.add(((Empenho) objetoRetorno).getFornecedor());
                    }
                    return objetoRetorno;
                }
            }
        }
        return registro;
    }

    private List<LancamentoContabil> recuperLancamentosContabeis(Exercicio exercicioInicial, Exercicio exercicioFinal, List<TipoEventoContabil> tipoEventoContabils) {

        String tiposEventos = getEventosAsString(tipoEventoContabils);

        String hql = " select l.* from lancamentocontabil l " +
                " inner join lcp on l.lcp_id = lcp.id" +
                " inner join itemparametroevento item on l.itemparametroevento_id = item.id " +
                " inner join parametroevento p on item.parametroevento_id = p.id " +
                " inner join eventocontabil evento on p.eventocontabil_id = evento.id " +
                " where to_date(to_char(l.dataLancamento,'yyyy'),'yyyy') between to_date(:di,'yyyy') and to_date(:df,'yyyy') " +
                " and (lcp.usoInterno = 0 or lcp.usoInterno is null) " +
                " and l.valor <> 0 " +
                " and evento.tipoEventoContabil in " + tiposEventos;
        hql += " order by p.idorigem ||''|| " +
                "case p.complementoid " +
                "when '" + ParametroEvento.ComplementoId.CONCEDIDO + "' then '1' " +
                "else '2' end";

        Query consulta = em.createNativeQuery(hql, LancamentoContabil.class);
        consulta.setParameter("di", exercicioInicial.getAno().toString());
        consulta.setParameter("df", exercicioFinal.getAno().toString());
        try {
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public String getEventosAsString(List<TipoEventoContabil> tipoEventoContabils) {
        String tiposEventos = "(";

        for (TipoEventoContabil tipoEventoContabil : tipoEventoContabils) {
            tiposEventos += "'" + tipoEventoContabil.name() + "',";
        }
        tiposEventos = tiposEventos.substring(0, tiposEventos.length() - 1) + ")";
        return tiposEventos;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
