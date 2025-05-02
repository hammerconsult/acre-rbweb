package br.com.webpublico.negocios;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltrosConsultaFiscalizacaoRBTrans;
import br.com.webpublico.entidadesauxiliares.VOCalculo;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoDocumentoFiscalizacaoRBTrans;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.negocios.tributario.arrecadacao.CalculoExecutorDepoisDePagar;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class FiscalizacaoRBTransFacade extends CalculoExecutorDepoisDePagar<FiscalizacaoRBTrans>  {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ParametrosTransitoRBTransFacade parametrosTransitoRBTransFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private GeraValorDividaFiscalizacaoRBTrans geraDebito;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private EnderecoCorreioFacade enderecoCorreioFacade;
    @EJB
    private CidadeFacade cidadeFacade;
    @EJB
    private OcorrenciaFiscalizacaoRBTransFacade ocorrenciaFiscalizacaoRBTransFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private PermissaoTransporteFacade permissaoTransporteFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private AgenteAutuadorFacade agenteAutuadorFacade;

    public FiscalizacaoRBTransFacade() {
        super(FiscalizacaoRBTrans.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ParametrosTransitoRBTransFacade getParametrosTransitoRBTransFacade() {
        return parametrosTransitoRBTransFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public DAMFacade getDamFacade() {
        return damFacade;
    }

    public EnderecoCorreioFacade getEnderecoCorreioFacade() {
        return enderecoCorreioFacade;
    }

    public CidadeFacade getCidadeFacade() {
        return cidadeFacade;
    }

    public OcorrenciaFiscalizacaoRBTransFacade getOcorrenciaFiscalizacaoRBTransFacade() {
        return ocorrenciaFiscalizacaoRBTransFacade;
    }

    public FeriadoFacade getFeriadoFacade() {
        return feriadoFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }

    public PermissaoTransporteFacade getPermissaoTransporteFacade() {
        return permissaoTransporteFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public GeraValorDividaFiscalizacaoRBTrans getGeraDebito() {
        return geraDebito;
    }

    public void setGeraDebito(GeraValorDividaFiscalizacaoRBTrans geraDebito) {
        this.geraDebito = geraDebito;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public AgenteAutuadorFacade getAgenteAutuadorFacade() {
        return agenteAutuadorFacade;
    }

    public void setAgenteAutuadorFacade(AgenteAutuadorFacade agenteAutuadorFacade) {
        this.agenteAutuadorFacade = agenteAutuadorFacade;
    }

    public List<FiscalizacaoRBTrans> pesquisaFiscalizacao(FiltrosConsultaFiscalizacaoRBTrans filtro) {
        Query q = em.createQuery(filtro.obtemQueryConsultaFiscalizacaoRBTrans());

        if (filtro.getCodigoAutuacao() != null) {
            q.setParameter("codigo", filtro.getCodigoAutuacao());
        }
        if (filtro.getDataAutuacao() != null) {
            q.setParameter("dataAutuacao", filtro.getDataAutuacao());
        }
        if (filtro.getOrgaoAutuadorRBTrans() != null) {
            q.setParameter("orgaoAutuador", filtro.getOrgaoAutuadorRBTrans());
        }
        if (filtro.getSituacaoFiscalizacao() != null) {
            q.setParameter("statusFiscalizacao", filtro.getSituacaoFiscalizacao());
        }
        if (filtro.getCadastroEconomicoPermissionario() != null) {
            q.setParameter("permissionario", filtro.getCadastroEconomicoPermissionario());
        }

        q.setMaxResults(filtro.getQuantidadeMaximaRegistros());

        List<FiscalizacaoRBTrans> toReturn = (List<FiscalizacaoRBTrans>) q.getResultList();

        for (FiscalizacaoRBTrans fisc : toReturn) {
            for (SituacaoFiscalizacaoRBTrans situacao : fisc.getSituacoesFiscalizacao()) {
                if (situacao.isVigente()) {
                    fisc.setSituacaoAtual(situacao);
                }
            }
        }

        return toReturn;
    }

    public VeiculoPermissionario recuperaVeiculoPermissionarioPorPlacaAutuacaoSQL(String placa, Date dataAutuacao) {
        String sql = "select vp.* from VeiculoPermissionario vp"
                + " inner join permissaotransporte permissao on vp.permissaotransporte_id = permissao.id"
                + " inner join veiculotransporte vt on vt.id = vp.veiculotransporte_id"
                + " where vt.placa = :placa"
                + " and permissao.iniciovigencia <= to_date(:dataAutuacao,'dd/MM/yyyy')"
                + " and coalesce(permissao.finalvigencia,to_date(:dataAutuacao,'dd/MM/yyyy')) >= to_date(:dataAutuacao,'dd/MM/yyyy')";
        Query q = em.createNativeQuery(sql, VeiculoPermissionario.class);
        q.setParameter("placa", placa);
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        q.setParameter("dataAutuacao", formatador.format(dataAutuacao));

        Object veiculo = q.getSingleResult();

        if (veiculo != null) {
            return (VeiculoPermissionario) veiculo;
        }
        return null;
    }

    public VeiculoPermissionario recuperaVeiculoPermissionarioPorPlacaAutuacaoHQL(String placa, Date dataAutuacao) {
        String hql = "select vp from VeiculoPermissionario vp"
                + " inner join vp.permissaoTransporte permissao"
                + " inner join vp.veiculoTransporte veiculo"
                + " where LOWER(veiculo.placa) = :placa"
                + " and permissao.inicioVigencia <= :dataAutuacao"
                + " and coalesce(permissao.finalVigencia,:dataAutuacao) >= :dataAutuacao";

        Query q = em.createQuery(hql);
        q.setParameter("placa", placa.toLowerCase());
        q.setParameter("dataAutuacao", Util.getDataHoraMinutoSegundoZerado(dataAutuacao));
        List<VeiculoPermissionario> toReturn = q.getResultList();
        Object veiculo = null;
        if (!toReturn.isEmpty()) {
            veiculo = (VeiculoPermissionario) toReturn.get(0);
        }
        if (veiculo != null) {
            ((VeiculoPermissionario) veiculo).getVeiculoTransporte().getModelo().getMarca();
            ((VeiculoPermissionario) veiculo).getVeiculoTransporte();
            ((VeiculoPermissionario) veiculo).getPermissaoTransporte();
            ((VeiculoPermissionario) veiculo).getPermissaoTransporte().getPermissionarioVigente().getCadastroEconomico();
            ((VeiculoPermissionario) veiculo).getPermissaoTransporte().getPermissionarioVigente().getCadastroEconomico().getPessoa();

            return (VeiculoPermissionario) veiculo;
        }
        return null;
    }

    public Long sugereCodigo() {
        Query q = em.createNativeQuery("select lpad(coalesce(max(codigo), 0) + 1,8,'0') as codigo from AUTUACAOFISCALIZACAO");
        return Long.parseLong((String) q.getSingleResult());
    }

    public Long sugereCodigoNotificacao(Integer ano) {
        Query q = em.createNativeQuery("select lpad(coalesce(max(numero), 0) + 1,8,'0') as codigo from NOTIFICACAORBTRANS where ano = :ano ");
        q.setParameter("ano", ano);
        return Long.parseLong((String) q.getSingleResult());
    }

    public SituacaoFiscalizacaoRBTrans recuperaSituacaoAtual(FiscalizacaoRBTrans fisc) {
        String hql = "from SituacaoFiscalizacaoRBTrans situacao"
                + " where situacao.dataInicial <= :dataAtual"
                + " and coalesce(situacao.dataFinal, :dataAtual) <= :dataAtual"
                + " and situacao.fiscalizacao = :fisc";

        Query q = em.createQuery(hql);
        q.setParameter("dataAtual", new Date());
        q.setParameter("fisc", fisc);

        SituacaoFiscalizacaoRBTrans toReturn = (SituacaoFiscalizacaoRBTrans) q.getSingleResult();
        if (toReturn != null) {
            return toReturn;
        }
        return null;
    }

    public CadastroEconomico recuperaCadastroEconomicoFiscalizacaoRBTrans(FiscalizacaoRBTrans fisc) {
        Query q = em.createQuery("select ce from FiscalizacaoRBTrans fisc"
            + " inner join fisc.autuacaoFiscalizacao autuacao"
            + " inner join autuacao.veiculoPermissionario vp"
            + " inner join vp.permissaoTransporte pt"
            + " inner join pt.cadastroEconomico ce"
            + " where fisc =:fiscParam");
        q.setParameter("fiscParam", fisc);
        q.setMaxResults(1);
        CadastroEconomico toReturn = (CadastroEconomico) q.getResultList();
        if (toReturn != null) {
            return toReturn;
        }
        return null;
    }

    public List<CadastroEconomico> listaCadastrosEconomicosPermissionario(String parte) {
        Query q = em.createQuery("select c from PermissaoTransporte pt"
            + " inner join pt.cadastroEconomico c"
            + " where c.pessoa in (select pf from PessoaFisica pf "
            + "                    where lower(pf.nome) like :parte "
            + "                       or lower(pf.cpf) like :parte "
            + "                       or (replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') like :parte)) "
            + "    or c.pessoa in (select pj from PessoaJuridica pj "
            + "                    where lower(pj.razaoSocial) like :parte "
            + "                       or lower(pj.nomeFantasia) like :parte "
            + "                       or lower(pj.cnpj) like :parte "
            + "                       or (replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :parte)) "
            + " or c.inscricaoCadastral like :parte "
            + " or pt.numero like :parte");
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setMaxResults(10);

        return q.getResultList();
    }

    public List<CadastroEconomico> listaCadastrosEconomicosPermissionarioEOutorgas(String parte) {
        Query q = em.createNativeQuery("SELECT cadastro.migracaochave, cadastro.responsavelPeloCadastro_id, ce.*"
            + " FROM cadastroeconomico ce"
            + " INNER JOIN cadastro cadastro ON cadastro.id = ce.id"
            + " WHERE "
            + "      ((ce.id IN"
            + "      (SELECT permissionario.cadastroeconomico_id"
            + "       FROM permissaotransporte pt"
            + "         inner join permissionario  on permissionario.permissaotransporte_id = pt.id"
            + "       inner join cadastroeconomico cadec on cadec.id = permissionario.cadastroeconomico_id"
            + "       inner join pessoa p on p.id = cadec.pessoa_id"
            + "       left join pessoajuridica pj on pj.id = p.id"
            + "       left join pessoafisica pf on pf.id = p.id"
            + "       WHERE (permissionario.iniciovigencia <= current_date and coalesce(permissionario.finalvigencia,current_date) >= current_date) "
            + "       AND (pt.numero LIKE :parte"
            + "       OR upper(pf.nome) LIKE :parte"
            + "       OR pf.cpf LIKE :parte"
            + "       OR (REPLACE(REPLACE(REPLACE(pf.cpf,'.',''),'-',''),'/','') LIKE :parte)"
            + "       OR upper(pj.nomefantasia) LIKE :parte"
            + "       OR pj.cnpj LIKE :parte"
            + "       OR (REPLACE(REPLACE(REPLACE(pj.cnpj,'.',''),'-',''),'/','') LIKE :parte)"
            + "       OR ce.inscricaocadastral LIKE :parte"
            + "       )))"
            + " OR "
            + "      (ce.id IN"
            + "      (SELECT cadeco.id"
            + "       FROM lancamentooutorga lo"
            + "       inner join cadastroeconomico cadeco on cadeco.id = lo.cmc_id"
            + "       inner join pessoa p on p.id = cadeco.pessoa_id"
            + "       left join pessoajuridica pj on pj.id = p.id"
            + "       left join pessoafisica pf on pf.id = p.id"
            + "       WHERE upper(pf.nome) LIKE :parte"
            + "       OR pf.cpf LIKE :parte"
            + "       OR (REPLACE(REPLACE(REPLACE(pf.cpf,'.',''),'-',''),'/','') LIKE :parte)"
            + "       OR upper(pj.nomefantasia) LIKE :parte"
            + "       OR pj.cnpj LIKE :parte"
            + "       OR (REPLACE(REPLACE(REPLACE(pj.cnpj,'.',''),'-',''),'/','') LIKE :parte)"
            + "       OR ce.inscricaocadastral LIKE :parte"
            + "      )))", CadastroEconomico.class);
        q.setParameter("parte", "%" + parte.trim().toUpperCase() + "%");
        q.setMaxResults(10);

        return q.getResultList();
    }

    public ValorDivida retornaValorDividaCalculo(CalculoFiscalizacaoRBTrans calculo) {
        Query q = em.createQuery("from ValorDivida vd where vd.calculo = :calculo");
        q.setParameter("calculo", calculo);
        List<ValorDivida> resultado = q.getResultList();
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            ValorDivida vd = resultado.get(0);
            vd.getParcelaValorDividas().size();
            return vd;
        }
    }

    public DocumentoOficial geraDocumento(TipoDocumentoFiscalizacaoRBTrans tipoDocumento, FiscalizacaoRBTrans fisc, CadastroEconomico ce, Pessoa p, SistemaControlador sistemaControlador) {
        ParametrosFiscalizacaoRBTrans param = parametrosTransitoRBTransFacade.getParametrosFiscalizacaoRBTransVigente();
        try {
            if (tipoDocumento.equals(TipoDocumentoFiscalizacaoRBTrans.NOTIFICACAO_INFRATOR)) {
                return documentoOficialFacade.geraDocumentoFiscalizacaoRBTrans(fisc, null, ce, p, param.getNotificacaoDeAutuacao(), sistemaControlador);
            }
            if (tipoDocumento.equals(TipoDocumentoFiscalizacaoRBTrans.PARECER_RECURSO)) {
                return documentoOficialFacade.geraDocumentoFiscalizacaoRBTrans(fisc, null, ce, p, param.getCienciaDaDecisaoDoRecursoAoInfrator(), sistemaControlador);
            }
            if (tipoDocumento.equals(TipoDocumentoFiscalizacaoRBTrans.PARECER_RECURSO_JARI)) {
                return documentoOficialFacade.geraDocumentoFiscalizacaoRBTrans(fisc, null, ce, p, param.getCienciaDecisaoRecursoJARI(), sistemaControlador);
            }
            if (tipoDocumento.equals(TipoDocumentoFiscalizacaoRBTrans.NOTIFICACAO_PENALIDADE)) {
                return documentoOficialFacade.geraDocumentoFiscalizacaoRBTrans(fisc, null, ce, p, param.getNotificacaoDePenalidades(), sistemaControlador);
            }
            if (tipoDocumento.equals(TipoDocumentoFiscalizacaoRBTrans.NOTIFICACAO_PRECLUSAO_PROCESSO)) {
                return documentoOficialFacade.geraDocumentoFiscalizacaoRBTrans(fisc, null, ce, p, param.getNotificacaoDePreclusaoDoProcesso(), sistemaControlador);
            }
            if (tipoDocumento.equals(TipoDocumentoFiscalizacaoRBTrans.SOLICITACAO_CANCELAMENTO_DIVIDA)) {
                return documentoOficialFacade.geraDocumentoFiscalizacaoRBTrans(fisc, null, ce, p, param.getCancelamentoMulta(), sistemaControlador);
            }
            if (tipoDocumento.equals(TipoDocumentoFiscalizacaoRBTrans.SOLICITACAO_RESTITUICAO_PAGAMENTO)) {
                return documentoOficialFacade.geraDocumentoFiscalizacaoRBTrans(fisc, null, ce, p, param.getNotificacaoRestituicao(), sistemaControlador);
            }
            if (tipoDocumento.equals(TipoDocumentoFiscalizacaoRBTrans.SOLICITACAO_ARQUIVAMENTO)) {
                return documentoOficialFacade.geraDocumentoFiscalizacaoRBTrans(fisc, null, ce, p, param.getSolicitacaoArquivamento(), sistemaControlador);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public FiscalizacaoRBTrans salvarAutuacao(FiscalizacaoRBTrans fiscalizacao) {
        AutuacaoFiscalizacaoRBTrans autuacao = fiscalizacao.getAutuacaoFiscalizacao();
        verificaArquivos(fiscalizacao);
        em.persist(autuacao);
        fiscalizacao = em.merge(fiscalizacao);
        em.flush();
        return fiscalizacao;
    }

    public void salvaAnaliseAutuacao(FiscalizacaoRBTrans fiscalizacao) {
        AnaliseAutuacaoRBTrans analise = fiscalizacao.getAutuacaoFiscalizacao().getAnaliseAutuacaoRBTrans();
        AutuacaoFiscalizacaoRBTrans autuacao = fiscalizacao.getAutuacaoFiscalizacao();
        verificaArquivos(fiscalizacao);
        em.persist(analise);
        em.merge(autuacao);
        em.merge(fiscalizacao);
    }

    public void salvaNotificacaoAutuacao(FiscalizacaoRBTrans fiscalizacao) {
        AutuacaoFiscalizacaoRBTrans autuacao = fiscalizacao.getAutuacaoFiscalizacao();
        verificaArquivos(fiscalizacao);
        em.merge(autuacao);
        em.merge(fiscalizacao);
    }

    public void salvaCienciaNotificacao(FiscalizacaoRBTrans fiscalizacao) {
        AutuacaoFiscalizacaoRBTrans autuacao = fiscalizacao.getAutuacaoFiscalizacao();
        verificaArquivos(fiscalizacao);
        em.merge(autuacao);
        em.merge(fiscalizacao);
    }

    public void salvarRecurso(FiscalizacaoRBTrans fiscalizacao) {
        AutuacaoFiscalizacaoRBTrans autuacao = fiscalizacao.getAutuacaoFiscalizacao();
        verificaArquivos(fiscalizacao);
        em.merge(autuacao);
        em.merge(fiscalizacao);
    }

    public void salvaAnaliseRecurso(FiscalizacaoRBTrans fiscalizacao) {
        AutuacaoFiscalizacaoRBTrans autuacao = fiscalizacao.getAutuacaoFiscalizacao();
        verificaArquivos(fiscalizacao);
        for (RecursoRBTrans obj : autuacao.getRecursosRBTrans()) {
            if (obj.getAnaliseRecursoRBTrans().getId() == null) {
                em.persist(obj.getAnaliseRecursoRBTrans());
            }
        }
        em.merge(autuacao);
        em.merge(fiscalizacao);
    }

    @Override
    public void salvar(FiscalizacaoRBTrans fiscalizacao) {
        verificaArquivos(fiscalizacao);
        em.merge(fiscalizacao);
    }

    @Override
    public void depoisDePagar(Calculo calculo) {
        CalculoFiscalizacaoRBTrans calcFiscalizacao = em.find(CalculoFiscalizacaoRBTrans.class, calculo.getId());
        FiscalizacaoRBTrans fisc = calcFiscalizacao.getProcessoCalculoFiscalizacaoRBTrans().getFiscalizacaoRBTrans();
        fisc.executaQuandoPagarGuia();
        salvar(fisc);
    }

    public FiscalizacaoRBTrans recuperar(FiscalizacaoRBTrans obj) {
        if (obj.getId() == null) {
            return null;
        }
        Query q = em.createQuery("from FiscalizacaoRBTrans f where f =:param");
        q.setParameter("param", obj);
        q.setMaxResults(1);

        if (!q.getResultList().isEmpty()) {
            FiscalizacaoRBTrans fisc = (FiscalizacaoRBTrans) q.getResultList().get(0);
            if (fisc != null) {
                fisc.getOrgaoAutuador();
                if (fisc.getAutuacaoFiscalizacao().getMotoristaInfrator() != null) {
                    fisc.getAutuacaoFiscalizacao().getMotoristaInfrator().getEnderecoscorreio().size();
                }
                fisc.getSituacoesFiscalizacao().size();
                fisc.getFiscalizacaoArquivo().size();
                fisc.getAutuacaoFiscalizacao().getNotificacoes().size();
                fisc.getAutuacaoFiscalizacao().getRecursosRBTrans().size();
                fisc.getAutuacaoFiscalizacao().getMotoristaInfrator();
                fisc.getAutuacaoFiscalizacao().getAnaliseAutuacaoRBTrans();

                if (fisc.getAutuacaoFiscalizacao().getAnaliseAutuacaoRBTrans() != null) {
                    fisc.getAutuacaoFiscalizacao().getAnaliseAutuacaoRBTrans().getCamposInconsistentes().size();
                }

                if (fisc.getAutuacaoFiscalizacao().getCadastroEconomico() != null) {
                    fisc.getAutuacaoFiscalizacao().getCadastroEconomico().getPessoa();
                    fisc.getAutuacaoFiscalizacao().getCadastroEconomico().getPessoa().getEnderecoscorreio().size();
                }
                fisc.getAutuacaoFiscalizacao().getOcorrenciasAutuacao().size();

                return fisc;
            }
        }
        return null;
    }

    public FiscalizacaoRBTrans recuperar(Long id) {
        Query q = em.createQuery("from FiscalizacaoRBTrans f where f.id =:param");
        q.setParameter("param", id);
        q.setMaxResults(1);

        FiscalizacaoRBTrans fisc = (FiscalizacaoRBTrans) q.getResultList().get(0);
        if (fisc != null) {
            fisc.getOrgaoAutuador();
            if (fisc.getAutuacaoFiscalizacao().getMotoristaInfrator() != null) {
                fisc.getAutuacaoFiscalizacao().getMotoristaInfrator().getEnderecoscorreio().size();
            }
            fisc.getSituacoesFiscalizacao().size();
            fisc.getFiscalizacaoArquivo().size();
            fisc.getAutuacaoFiscalizacao().getNotificacoes().size();
            fisc.getAutuacaoFiscalizacao().getRecursosRBTrans().size();
            fisc.getAutuacaoFiscalizacao().getMotoristaInfrator();
            fisc.getAutuacaoFiscalizacao().getAnaliseAutuacaoRBTrans();

            if (fisc.getAutuacaoFiscalizacao().getAnaliseAutuacaoRBTrans() != null) {
                fisc.getAutuacaoFiscalizacao().getAnaliseAutuacaoRBTrans().getCamposInconsistentes().size();
            }

            if (fisc.getAutuacaoFiscalizacao().getCadastroEconomico() != null) {
                fisc.getAutuacaoFiscalizacao().getCadastroEconomico().getPessoa();
                fisc.getAutuacaoFiscalizacao().getCadastroEconomico().getPessoa().getEnderecoscorreio().size();
            }
            fisc.getAutuacaoFiscalizacao().getOcorrenciasAutuacao().size();

            return fisc;
        }
        return null;
    }

    public void verificaArquivos(FiscalizacaoRBTrans fisc) {
        for (FiscalizacaoArquivoRBTrans obj : fisc.getFiscalizacaoArquivo()) {
            if (obj.getArquivo().getId() == null) {
                if (!obj.getExcluido()) {
                    salvarArquivo((UploadedFile) obj.getFile(), obj.getArquivo());
                }
            }
        }
    }

    public void salvarArquivo(UploadedFile uploadedFile, Arquivo arq) {
        try {
            UploadedFile arquivoRecebido = uploadedFile;
            arq.setNome(arquivoRecebido.getFileName());
            arq.setMimeType(arquivoRecebido.getContentType());
            arq.setTamanho(arquivoRecebido.getSize());
            arq.setInputStream(arquivoRecebido.getInputstream());
            arquivoFacade.novoArquivo(arq, arquivoRecebido.getInputstream());
            arquivoRecebido.getInputstream().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ProcessoCalculoFiscalizacaoRBTrans geraProcessoCalculoMulta(FiscalizacaoRBTrans fiscalizacao) throws UFMException {
        ParametrosFiscalizacaoRBTrans parametro = parametrosTransitoRBTransFacade.getParametrosFiscalizacaoRBTransVigente();
        Divida divida = parametro.getDivida();

        CalculoFiscalizacaoRBTrans calculo = new CalculoFiscalizacaoRBTrans();
        calculo.setCadastro(fiscalizacao.getAutuacaoFiscalizacao().getCadastroEconomico());

        CalculoPessoa cp = new CalculoPessoa();
        cp.setCalculo(calculo);
        if (fiscalizacao.getAutuacaoFiscalizacao().getCadastroEconomico() != null) {
            cp.setPessoa(fiscalizacao.getAutuacaoFiscalizacao().getCadastroEconomico().getPessoa());
        } else {
            cp.setPessoa(fiscalizacao.getAutuacaoFiscalizacao().getPessoaClandestina());
        }

        calculo.getPessoas().add(cp);
        calculo.setItensCalculoFiscalizacaoRBTrans(new ArrayList<ItemCalculoFiscalizacaoRBTrans>());

        ProcessoCalculoFiscalizacaoRBTrans processoCalculo = new ProcessoCalculoFiscalizacaoRBTrans();
        processoCalculo.setDivida(divida);
        processoCalculo.setFiscalizacaoRBTrans(fiscalizacao);
        processoCalculo.setExercicio(sistemaFacade.getExercicioCorrente());
        processoCalculo.setListaDeCalculos(new ArrayList<CalculoFiscalizacaoRBTrans>());
        processoCalculo.getListaDeCalculos().add(calculo);

        BigDecimal valorTotalMulta = BigDecimal.ZERO;
        for (OcorrenciaAutuacaoRBTrans obj : fiscalizacao.getAutuacaoFiscalizacao().getOcorrenciasAutuacao()) {
            valorTotalMulta = valorTotalMulta.add((obj.getOcorrenciaFiscalizacao().getValor().
                multiply(new BigDecimal(obj.getOcorrenciaFiscalizacao().getFatorMultReincidencia()))));
            ItemCalculoFiscalizacaoRBTrans item = new ItemCalculoFiscalizacaoRBTrans();
            item.setTributo(obj.getOcorrenciaFiscalizacao().getTributo());
            item.setValor(moedaFacade.recuperaValorUFMParaData(fiscalizacao.getAutuacaoFiscalizacao().getDataAutuacao()).
                multiply(obj.getOcorrenciaFiscalizacao().getValor()));
            item.setCalculoFiscalizacaoRBTrans(calculo);
            calculo.getItensCalculoFiscalizacaoRBTrans().add(item);
        }

        calculo.setValorReal(moedaFacade.recuperaValorUFMParaData(fiscalizacao.getAutuacaoFiscalizacao().getDataAutuacao()).multiply(valorTotalMulta));
        calculo.setValorEfetivo(calculo.getValorReal());
        calculo.setProcessoCalculoFiscalizacaoRBTrans(processoCalculo);
        calculo.setSimulacao(false);
        calculo.setDataCalculo(sistemaFacade.getDataCorrente());
        return processoCalculo;
    }

    public void gerarDadosParaPagamento(FiscalizacaoRBTrans fiscalizacao) {
        try {
            ProcessoCalculoFiscalizacaoRBTrans processo = geraProcessoCalculoMulta(fiscalizacao);
            em.persist(processo);
            geraDebito.geraDebito(processo);
            damFacade.geraDAM(processo.getCalculos().get(0));
        } catch (Exception e) {
            FacesUtil.addError("Ocorreu um erro", e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean hasNumeroAutuacao(Long idFiscalizacao, Long numero) {
        String sql = "select fisc.id "
            + " from fiscalizacaorbtrans fisc"
            + " inner join autuacaofiscalizacao aut on aut.id = fisc.autuacaofiscalizacao_id"
            + " where aut.codigo = :codigo ";
        if (idFiscalizacao != null) {
            sql += " and fisc.id <> :idFiscalizacao";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", numero);
        if (idFiscalizacao != null) {
            q.setParameter("idFiscalizacao", idFiscalizacao);
        }
        if (q.getResultList().isEmpty()) {
            return false;
        }
        return true;

    }

    public List<AutuacaoFiscalizacaoRBTrans> recuperarAutuacoesDoPermissionario(CadastroEconomico ce) {
        String hql = "  from AutuacaoFiscalizacaoRBTrans "
                + "    where cadastroEconomico = :permissionario "
                + " order by dataAutuacao desc";

        List<AutuacaoFiscalizacaoRBTrans> resultList = em.createQuery(hql).setParameter("permissionario", ce).getResultList();
        for (AutuacaoFiscalizacaoRBTrans autuacao : resultList) {
            autuacao.getOcorrenciasAutuacao().size();
        }

        return resultList;
    }

    public List<ParcelaValorDivida> recuperaParcelasDividaFiscalizacao(FiscalizacaoRBTrans fisc) {
        List<ParcelaValorDivida> toReturn = new ArrayList<>();
        String sql = "select parcela.* from PARCELAVALORDIVIDA parcela"
                + " inner join VALORDIVIDA vd on parcela.valordivida_id = vd.id"
                + " inner join CALCULOFISCRBTRANS calc on calc.id = vd.calculo_id"
                + " inner join PROCESSOCALCFISCRBTRANS processo on processo.id = calc.proccalcfiscrbtrans_id"
                + " where processo.fiscalizacaorbtrans_id = :fisc";

        Query q = em.createNativeQuery(sql, ParcelaValorDivida.class);
        q.setParameter("fisc", fisc);

        toReturn = q.getResultList();

        if (!toReturn.isEmpty()) {
            for (ParcelaValorDivida obj : toReturn) {
                obj.getSituacoes().size();
            }
        }

        return toReturn;
    }

    public void cancelaDivida(FiscalizacaoRBTrans fisc) {
        List<ParcelaValorDivida> parcelas = recuperaParcelasDividaFiscalizacao(fisc);
        for (ParcelaValorDivida obj : parcelas) {
            SituacaoParcelaValorDivida situacao = new SituacaoParcelaValorDivida();
            situacao.setParcela(obj);
            situacao.setDataLancamento(new Date());
            situacao.setSaldo(BigDecimal.ZERO);
            situacao.setSituacaoParcela(SituacaoParcela.CANCELAMENTO);

            obj.getSituacoes().add(situacao);
            damFacade.cancelarDamsDaParcela(obj, sistemaFacade.getUsuarioCorrente());

            em.merge(obj);
        }
    }

    public VOCalculo buscarCalculoDaFiscalizacao(FiscalizacaoRBTrans fiscalizacaoRBTrans) {
        String sql = "select calculorb.id, calculo.tipocalculo from fiscalizacaorbtrans fisc " +
            " inner join PROCESSOCALCFISCRBTRANS processo on processo.fiscalizacaorbtrans_id = fisc.id " +
            " inner join CALCULOFISCRBTRANS calculorb on calculorb.proccalcfiscrbtrans_id = processo.id " +
            " inner join calculo calculo on calculo.id = calculorb.id " +
            " where fisc.id = :idFiscalizacao " +
            " order by calculorb.id desc";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idFiscalizacao", fiscalizacaoRBTrans.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            Object[] obj = (Object[]) q.getResultList().get(0);
            VOCalculo voCalculo = new VOCalculo();
            voCalculo.setIdCalculo(((BigDecimal) obj[0]).longValue());
            voCalculo.setTipoCalculo(Calculo.TipoCalculo.valueOf((String) obj[1]));
            return voCalculo;
        }
        return null;
    }
}
