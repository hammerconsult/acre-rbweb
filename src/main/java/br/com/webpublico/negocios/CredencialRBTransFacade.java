/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.CredencialSimples;
import br.com.webpublico.entidadesauxiliares.FiltroGeracaoCredencialRBTrans;
import br.com.webpublico.enums.TipoCredencialRBTrans;
import br.com.webpublico.enums.TipoPagamentoCredencialRBTrans;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.enums.TipoRequerenteCredencialRBTrans;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;

@Stateless
public class CredencialRBTransFacade extends AbstractFacade<CredencialRBTrans> {

    @EJB
    private PermissaoTransporteFacade permissaoTransporteFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ParametrosTransitoRBTransFacade parametrosTransitoRBTransFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private ParametroInformacaoRBTransFacade parametroInformacaoRBTransFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private List<CredencialRBTrans> lista;


    public CredencialRBTransFacade() {
        super(CredencialRBTrans.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PermissaoTransporteFacade getPermissaoTransporteFacade() {
        return permissaoTransporteFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public ParametroInformacaoRBTransFacade getParametroInformacaoRBTransFacade() {
        return parametroInformacaoRBTransFacade;
    }

    public ParametrosTransitoRBTransFacade getParametrosTransitoRBTransFacade() {
        return parametrosTransitoRBTransFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public Integer gerarNumero() {
        String sql = " SELECT nvl(max(cre.numero),0) "
            + "      FROM credencialrbtrans cre"
            + "     WHERE extract(YEAR FROM cre.datageracao) = :ano";

        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", sistemaFacade.getExercicioCorrente().toString());

        return new Integer(((BigDecimal) q.getSingleResult()).toString()) + 1;
    }

    public Habilitacao recuperarHabilitacao(PessoaFisica pessoaFisica) {
        for (DocumentoPessoal dp : pessoaFisica.getDocumentosPessoais()) {
            if (dp instanceof Habilitacao) {
                return (Habilitacao) dp;
            }
        }

        return null;
    }

    public void baixarTodasCredenciaisPorPermissao(PermissaoTransporte permissaoTransporte) {
        List<CredencialRBTrans> credenciais = buscarTodasCredenciaisPorPermissao(permissaoTransporte);
        for (CredencialRBTrans credencialRBTrans : credenciais) {
            credencialRBTrans.setDataValidade(DataUtil.getDataDiaAnterior(sistemaFacade.getDataOperacao()));
            salvar(credencialRBTrans);
        }
    }

    private List<CredencialRBTrans> buscarTodasCredenciaisPorPermissao(PermissaoTransporte permissaoTransporte) {
        StringBuilder sb = new StringBuilder();
        sb.append(" from CredencialRBTrans crbt ");
        sb.append(" where crbt.permissaoTransporte = :permissao ");
        sb.append(" and crbt.dataValidade  > :dataAtual");
        Query q = em.createQuery(sb.toString(), CredencialRBTrans.class);
        q.setParameter("permissao", permissaoTransporte);
        q.setParameter("dataAtual", SistemaFacade.getDataCorrente());
        if (!q.getResultList().isEmpty()) {
            return (List<CredencialRBTrans>) q.getResultList();
        }
        return new ArrayList<>();
    }

    @Asynchronous
    public Future<List<CredencialRBTrans>> recuperarCredenciaisPorFiltro(FiltrosConsultaCredenciaisRBTrans filtros) {
        String classePermissao = filtros.obterNomeClassePermissao();
        String classeCredencial = filtros.obterNomeClasseCredencial();
        String parteDaQueryComCmc = "";

        if (filtros.isFiltroCmc()) {
            parteDaQueryComCmc = ", CadastroEconomico ce";
        }
        String hql = null;
        hql = "         select c "
            + "       from " + classeCredencial + " c, " + classePermissao + " perm" + parteDaQueryComCmc
            + "      where c.dataGeracao >= :dataGeracaoInicial"
            + "        and c.dataGeracao <= :dataGeracaoFinal"
            + "        and c.tipoRequerente = :tipoRequerente";

        hql = obterFiltroValidade(hql, filtros);
        hql = obterFiltroStatusEmissao(hql, filtros);
        hql = obterFiltroStatusPagamento(hql, filtros);
        hql = obterFiltroTipoFiltroETipoCredencial(hql, filtros);
        hql = hql + " order by c.numero";

        Query q = em.createQuery(hql);
        setarParametrosComFiltro(q, filtros);

        for (Field field : FiltrosConsultaCredenciaisRBTrans.class.getDeclaredFields()) {
            field.setAccessible(true);
        }
        return new AsyncResult<List<CredencialRBTrans>>(q.getResultList());
    }

    private String obterFiltroValidade(String hql, FiltrosConsultaCredenciaisRBTrans filtros) {
        if (filtros.isValidadeEmDia()) {
            return hql + " and c.dataValidade > :dataAtual";
        }

        if (filtros.isValidadeVencida()) {
            return hql + " and c.dataValidade <= :dataAtual";
        }

        return hql;
    }

    private String obterFiltroStatusEmissao(String hql, FiltrosConsultaCredenciaisRBTrans filtros) {
        if (filtros.isStatusEmissaoEmitida()) {
            return hql + " and c.foiEmitida = true";
        }

        if (filtros.isStatusEmissaoNaoEmitida()) {
            return hql + " and c.foiEmitida = false";
        }

        return hql;
    }

    private String obterFiltroStatusPagamento(String hql, FiltrosConsultaCredenciaisRBTrans filtros) {
        if (!filtros.getStatusPagamento().equals(TipoPagamentoCredencialRBTrans.TODAS)) {
            hql = hql + " and c.statusPagamento = :statusPagamento";
        }

        return hql;
    }

    private String obterFiltroTipoFiltroETipoCredencial(String hql, FiltrosConsultaCredenciaisRBTrans filtros) {
        if (filtros.isFiltroPermissao()) {
            if (filtros.isTransporte()) {
                hql = hql + " and c.permissaoTransporte = perm";
            } else {
                hql = hql + " and c.veiculoPermissionario.permissaoTransporte = perm";
            }

            hql = hql + " and perm.numero >= :numeroPermissaoInicial"
                + "   and perm.numero <= :numeroPermissaoFinal";
        } else {
            if (filtros.isTransporte()) {
                hql = hql + " and c.permissaoTransporte = perm";
            } else {
                hql = hql + " and c.veiculoPermissionario.permissaoTransporte = perm";
            }

            hql = hql + " and ce.pessoa = perm.cadastroEconomico.pessoa"
                + "   and to_number(ce.inscricaoCadastral) >= :inscricaoCadastralInicial"
                + "   and to_number(ce.inscricaoCadastral) <= :inscricaoCadastralFinal";
        }

        return hql;
    }

    private void setarParametrosComFiltro(Query q, FiltrosConsultaCredenciaisRBTrans filtros) {
        if (filtros.isFiltroCmc()) {
            q.setParameter("inscricaoCadastralInicial", filtros.getNumeroCmcInicial());
            q.setParameter("inscricaoCadastralFinal", filtros.getNumeroCmcFinal());
        } else {
            q.setParameter("numeroPermissaoInicial", filtros.getNumeroPermissaoInicial());
            q.setParameter("numeroPermissaoFinal", filtros.getNumeroPermissaoFinal());
        }

        q.setParameter("dataGeracaoInicial", filtros.getDataGeracaoInicial());
        q.setParameter("dataGeracaoFinal", filtros.getDataGeracaoFinal());
        q.setParameter("tipoRequerente", filtros.getTipoRequerente());

        if (!filtros.isValidadeTodas()) {
            q.setParameter("dataAtual", SistemaFacade.getDataCorrente());
        }

        if (!filtros.getStatusPagamento().equals(TipoPagamentoCredencialRBTrans.TODAS)) {
            q.setParameter("statusPagamento", filtros.getStatusPagamento());
        }
    }

    public List<CredencialRBTrans> recuperarTodasCredenciais() {
        List<CredencialRBTrans> retorno = new ArrayList<>();

        String hql1 = " from CredencialTrafego";

        String hql2 = " from CredencialTransporte";

        Query q1 = em.createQuery(hql1);
        Query q2 = em.createQuery(hql2);

        if (!q1.getResultList().isEmpty()) {
            retorno.addAll(q1.getResultList());
        }

        if (!q2.getResultList().isEmpty()) {
            retorno.addAll(q2.getResultList());
        }

        return retorno;
    }


    public String recuperarCredencial(CredencialRBTrans cred) {
        cred = em.find(CredencialRBTrans.class, cred.getId());
        return cred.getCadastroEconomico().getPessoa().getNome();
    }

    public StringBuilder getSqlCredenciasPorTipoCredencialAndTipoRequerente(TipoCredencialRBTrans tipoCredencial, TipoRequerenteCredencialRBTrans tipoRequerente) {
        StringBuilder sql = new StringBuilder();
        if (TipoCredencialRBTrans.TRANSPORTE.equals(tipoCredencial)) {
            if (TipoRequerenteCredencialRBTrans.MOTORISTA.equals(tipoRequerente)) {
                sql.append(" select distinct c.*, ce.id as cadastroEconomico_id");
                sql.append(" from credencialrbtrans c");
                sql.append(" inner join credencialtransporte ct on ct.id = c.id");
                sql.append(" inner join cadastroeconomico ce on ce.id = ct.cadastroeconomico_id");
                sql.append(" left join pessoafisica pf on pf.id = ce.pessoa_id");
                sql.append(" left join pessoajuridica pj on pj.id = ce.pessoa_id");
                sql.append(" inner join permissaotransporte p on p.id = c.permissaotransporte_id");
                sql.append(" inner join motoristaauxiliar ma on ma.permissaotransporte_id = p.id");
                sql.append("                                and ma.cadastroeconomico_id = ct.cadastroeconomico_id");
            } else if ((TipoRequerenteCredencialRBTrans.PERMISSIONARIO.equals(tipoRequerente))) {
                sql.append(" select distinct c.*, ce.id as cadastroEconomico_id ");
                sql.append(" from credencialrbtrans c ");
                sql.append(" inner join credencialtransporte ct on ct.id = c.id ");
                sql.append(" inner join cadastroeconomico ce on ce.id = ct.cadastroeconomico_id ");
                sql.append(" left join pessoafisica pf on pf.id = ce.pessoa_id ");
                sql.append(" left join pessoajuridica pj on pj.id = ce.pessoa_id ");
                sql.append(" inner join permissaotransporte p on p.id = c.permissaotransporte_id ");
                sql.append(" inner join permissionario pm on pm.permissaotransporte_id = p.id ");
                sql.append("                          and pm.cadastroeconomico_id = ct.cadastroeconomico_id ");
            }
        } else if (TipoCredencialRBTrans.TRAFEGO.equals(tipoCredencial)) {
            if (TipoRequerenteCredencialRBTrans.PERMISSIONARIO.equals(tipoRequerente)) {
                sql.append("select distinct c.*, v.id as veiculoPermissionario_id from credencialrbtrans c ");
                sql.append(" inner join credencialtrafego ct on ct.id = c.id ");
                sql.append(" inner join VeiculoPermissionario v on v.id = ct.veiculopermissionario_id ");
                sql.append(" inner join permissaotransporte p on p.id = v.permissaotransporte_id ");
                sql.append(" inner join permissionario pm on pm.permissaotransporte_id = p.id ");
                sql.append(" inner join cadastroeconomico ce on ce.id = pm.cadastroeconomico_id ");
                sql.append(" left join pessoafisica pf on pf.id = ce.pessoa_id ");
                sql.append(" left join pessoajuridica pj on pj.id = ce.pessoa_id ");
            }
        }
        return sql;
    }

    public List<CredencialRBTrans> buscarCredenciaisPorFiltroDeGeracao(TipoCredencialRBTrans tipoCredencial, TipoRequerenteCredencialRBTrans tipoRequerente,
                                                                       FiltroGeracaoCredencialRBTrans filtro) {
        List<CredencialRBTrans> toReturn = Lists.newArrayList();
        Map<String, Object> parametros = Maps.newHashMap();
        StringBuilder sql = getSqlCredenciasPorTipoCredencialAndTipoRequerente(tipoCredencial, tipoRequerente);
        if (sql.toString().isEmpty()) {
            return toReturn;
        }
        sql.append(" where c.tiporequerente = :tipoRequerente ");
        parametros.put("tipoRequerente", tipoRequerente.name());

        if (filtro.getAnoValidade() != null) {
            sql.append(" and extract(year from c.datavalidade) = :anoValidade ");
            parametros.put("anoValidade", filtro.getAnoValidade());
        }
        if (filtro.getNome() != null && !filtro.getNome().trim().equals("")) {
            sql.append(" and (upper(coalesce(pf.nome, pj.razaosocial)) like :nome) ");
            parametros.put("nome", new StringBuilder("%").append(filtro.getNome().toUpperCase()).append("%").toString());
        }
        if (!(filtro.getCpf() == null || Util.removeMascaras(filtro.getCpf()).trim().equals(""))) {
            sql.append(" and ((coalesce(pf.cpf, pj.cnpj) = :cpf) or (replace(replace(replace(coalesce(pf.cpf,pj.cnpj),'.',''),'-',''),'/','') = :cpf)) ");
            parametros.put("cpf", Util.removeMascaras(filtro.getCpf()));
        }
        if (filtro.getCmc() != null && !filtro.getCmc().trim().equals("")) {
            sql.append(" and ce.inscricaocadastral = :cmc ");
            parametros.put("cmc", filtro.getCmc());
        }
        if (filtro.getTipoPermissaoRBTrans() != null) {
            sql.append(" and p.tipopermissaorbtrans = :tipoPermissao ");
            parametros.put("tipoPermissao", filtro.getTipoPermissaoRBTrans().name());
        }
        if (filtro.getNumeroPermissaoInicial() != null) {
            sql.append(" and p.numero >= :numeroPermissaoInicial ");
            parametros.put("numeroPermissaoInicial", filtro.getNumeroPermissaoInicial());
        }
        if (filtro.getNumeroPermissaoFinal() != null) {
            sql.append(" and p.numero <= :numeroPermissaoFinal ");
            parametros.put("numeroPermissaoFinal", filtro.getNumeroPermissaoFinal());
        }
        if (filtro.getFinalPermissaoInicial() != null) {
            sql.append(" and to_number(substr(p.numero, length(p.numero), 1)) >= :finalPermissaoInicial ");
            parametros.put("finalPermissaoInicial", filtro.getFinalPermissaoInicial());
        }
        if (filtro.getFinalPermissaoFinal() != null) {
            sql.append(" and to_number(substr(p.numero, length(p.numero), 1)) <= :finalPermissaoFinal");
            parametros.put("finalPermissaoFinal", filtro.getFinalPermissaoFinal());
        }

        Query q = em.createNativeQuery(sql.toString(),
            TipoCredencialRBTrans.TRANSPORTE.equals(tipoCredencial) ? CredencialTransporte.class : CredencialTrafego.class);
        for (String parametro : parametros.keySet()) {
            q.setParameter(parametro, parametros.get(parametro));
        }
        return q.getResultList();
    }

    public List<CredencialRBTrans> buscarCredenciaisPorFiltroDeGeracao(FiltroGeracaoCredencialRBTrans filtro) {
        List<TipoCredencialRBTrans> tiposDeCredencial = Lists.newArrayList();
        List<TipoRequerenteCredencialRBTrans> tiposDeRequerente = Lists.newArrayList();

        if (filtro.getTipoCredencialRBTrans() == null) {
            tiposDeCredencial.addAll(Lists.newArrayList(TipoCredencialRBTrans.values()));
        } else {
            tiposDeCredencial.add(filtro.getTipoCredencialRBTrans());
        }
        if (filtro.getTipoRequerenteCredencialRBTrans() == null) {
            tiposDeRequerente.addAll(Lists.newArrayList(TipoRequerenteCredencialRBTrans.values()));
        } else {
            tiposDeRequerente.add(filtro.getTipoRequerenteCredencialRBTrans());
        }

        List<CredencialRBTrans> credenciais = Lists.newArrayList();
        for (TipoCredencialRBTrans tipoCredencial : tiposDeCredencial) {
            for (TipoRequerenteCredencialRBTrans tipoRequerente : tiposDeRequerente) {
                credenciais.addAll(buscarCredenciaisPorFiltroDeGeracao(tipoCredencial, tipoRequerente, filtro));
            }
        }
        return credenciais;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @Asynchronous
    public Future<List<CredencialRBTrans>> geraCredenciais(AssistenteBarraProgresso assistente) {
        FiltroGeracaoCredencialRBTrans filtroGeracao = (FiltroGeracaoCredencialRBTrans) assistente.getFiltro();
        List<PermissaoTransporte> permissoes = buscarPermissoes(filtroGeracao, assistente.getDataAtual());
        return new AsyncResult<>(criarCredenciais(permissoes, assistente));
    }

    public List<PermissaoTransporte> buscarPermissoes(FiltroGeracaoCredencialRBTrans filtroGeracao, Date dataGeracao) {
        return permissaoTransporteFacade.buscarPermissoesDeTransportePorFiltroGeracaoCredencial(filtroGeracao, dataGeracao);
    }

    public Date retornaDataValidadeCredencial(ParametrosTransitoTransporte parametro, PermissaoTransporte pt, Integer anoValidade) {
        DigitoVencimento digitoVencimento = parametro.recuperarVencimentoPorDigitoTipo(permissaoTransporteFacade.retornaUltimoDigitoNumeroPermissao(pt.getNumero()), DigitoVencimento.TipoDigitoVencimento.CREDENCIAL);
        return permissaoTransporteFacade.retornaDataFinalVigenciaPermissao(digitoVencimento.getDia(), digitoVencimento.getMes(), anoValidade);
    }


    private List<CredencialRBTrans> criarCredenciais(List<PermissaoTransporte> listaDePermissoes, AssistenteBarraProgresso assistente) {
        FiltroGeracaoCredencialRBTrans filtroGeracao = (FiltroGeracaoCredencialRBTrans) assistente.getFiltro();
        ParametrosTransitoTransporte parametro = permissaoTransporteFacade.getParametrosTransitoTransporteFacade().recuperarParametroVigentePeloTipo(filtroGeracao.getTipoPermissaoRBTrans());
        List<CredencialRBTrans> credenciais = Lists.newArrayList();

        if (parametro != null) {
            List<TipoCredencialRBTrans> tipos = organizarTiposCredenciaisSelecionadas(filtroGeracao);
            List<TipoRequerenteCredencialRBTrans> requerentes = organizarRequerentesSelecionados(filtroGeracao);

            assistente.setTotal(tipos.size() * requerentes.size() * listaDePermissoes.size());

            for (TipoCredencialRBTrans tipoCredencial : tipos) {
                for (TipoRequerenteCredencialRBTrans requerente : requerentes) {
                    if (TipoCredencialRBTrans.TRANSPORTE.equals(tipoCredencial)) {
                        for (PermissaoTransporte permissao : listaDePermissoes) {
                            if (TipoRequerenteCredencialRBTrans.PERMISSIONARIO.equals(requerente)) {
                                    credenciais.add(criarCredencialTransporte(filtroGeracao, tipoCredencial, permissao,
                                        assistente.getDataAtual(), parametro, requerente, permissao.getPermissionarioVigente().getCadastroEconomico()));
                            } else {
                                for (MotoristaAuxiliar mot : permissaoTransporteFacade.recuperar(permissao.getId()).getMotoristasAuxiliaresVigentes()) {
                                    credenciais.add(criarCredencialTransporte(filtroGeracao, tipoCredencial, permissao,
                                        assistente.getDataAtual(), parametro, requerente, mot.getCadastroEconomico()));
                                }
                            }
                            assistente.conta();
                        }
                    } else if (TipoCredencialRBTrans.TRAFEGO.equals(tipoCredencial)) {
                        for (PermissaoTransporte permissao : listaDePermissoes) {
                            if (TipoRequerenteCredencialRBTrans.PERMISSIONARIO.equals(requerente)) {
                                permissao = getPermissaoTransporteFacade().carregarVeiculosDaPermissaoTransporte(permissao);
                                VeiculoPermissionario veiculo = permissao.getVeiculoVigente();
                                if (veiculo != null) {
                                    credenciais.add(criarCredencialTrafego(filtroGeracao, tipoCredencial, permissao,
                                        assistente.getDataAtual(), requerente, parametro));
                                }
                            }
                            assistente.conta();
                        }
                    }
                }
            }
        }
        return credenciais;
    }

    private List<CredencialSimples> getCredenciaisDeTransporte(List<CredencialSimples> credenciais) {
        List<CredencialSimples> toReturn = Lists.newArrayList();
        if (credenciais != null && !credenciais.isEmpty()) {
            for (CredencialSimples credencial : credenciais) {
                if (TipoCredencialRBTrans.TRANSPORTE.equals(credencial.getTipoCredencialRBTrans())) {
                    toReturn.add(credencial);
                }
            }
        }
        return toReturn;
    }

    private List<CredencialSimples> getCredenciaisDeTrafego(List<CredencialSimples> credenciais) {
        List<CredencialSimples> toReturn = Lists.newArrayList();
        if (credenciais != null && !credenciais.isEmpty()) {
            for (CredencialSimples credencial : credenciais) {
                if (TipoCredencialRBTrans.TRAFEGO.equals(credencial.getTipoCredencialRBTrans())) {
                    toReturn.add(credencial);
                }
            }
        }
        return toReturn;
    }

    private boolean existeCredencialDeTransporteValida(List<CredencialSimples> credenciais, TipoCredencialRBTrans tipoCredencialRBTrans,
                                                       PermissaoTransporte permissaoTransporte,
                                                       CadastroEconomico cadastroEconomico) {
        return existeCredencialValida(credenciais, tipoCredencialRBTrans, permissaoTransporte, cadastroEconomico, null);
    }

    private boolean existeCredencialDeTrafegoValida(List<CredencialSimples> credenciais, TipoCredencialRBTrans tipoCredencialRBTrans,
                                                    PermissaoTransporte permissaoTransporte,
                                                    VeiculoPermissionario veiculoPermissionario) {
        return existeCredencialValida(credenciais, tipoCredencialRBTrans, permissaoTransporte, null, veiculoPermissionario);
    }

    private boolean existeCredencialValida(List<CredencialSimples> credenciais, TipoCredencialRBTrans tipoCredencialRBTrans,
                                           PermissaoTransporte permissaoTransporte,
                                           CadastroEconomico cadastroEconomico, VeiculoPermissionario veiculoPermissionario) {
        if (credenciais != null && !credenciais.isEmpty()) {
            if (tipoCredencialRBTrans.equals(TipoCredencialRBTrans.TRANSPORTE)) {
                for (CredencialSimples credencialTransporte : getCredenciaisDeTransporte(credenciais)) {
                    if (credencialTransporte.getIdPermissaoTransporte().equals(permissaoTransporte.getId()) &&
                        credencialTransporte.getIdCadastroEconomico().equals(cadastroEconomico.getId())) {
                        return true;
                    }
                }
            } else if (tipoCredencialRBTrans.equals(TipoCredencialRBTrans.TRAFEGO)) {
                for (CredencialSimples credencialTrafego : getCredenciaisDeTrafego(credenciais)) {
                    if (credencialTrafego.getIdPermissaoTransporte().equals(permissaoTransporte.getId()) &&
                        credencialTrafego.getIdVeiculoPermissionario().equals(veiculoPermissionario.getId())) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    private List<CredencialSimples> buscarCredenciasValidasNoAno(Integer ano, Date validaEm) {
        List<CredencialSimples> toReturn = Lists.newArrayList();
        String sql = " select case when ct.cadastroeconomico_id is not null then 'TRANSPORTE' else 'TRAFEGO' end, c.permissaotransporte_id, ct.cadastroeconomico_id, cf.veiculopermissionario_id " +
            "   from credencialrbtrans c " +
            "  left join credencialtransporte ct on ct.id = c.id" +
            "  left join credencialtrafego cf on cf.id = c.id  " +
            "where extract(year from c.datavalidade) = :ano " +
            "  and (c.datavalidade is null or c.datavalidade >= :valida_em) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", ano);
        q.setParameter("valida_em", validaEm);
        if (q.getResultList() != null) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                CredencialSimples credencialSimples = new CredencialSimples();
                credencialSimples.setTipoCredencialRBTrans(obj[0] != null ? TipoCredencialRBTrans.valueOf((String) obj[0]) : null);
                credencialSimples.setIdPermissaoTransporte(obj[1] != null ? ((BigDecimal) obj[1]).longValue() : null);
                credencialSimples.setIdCadastroEconomico(obj[2] != null ? ((BigDecimal) obj[2]).longValue() : null);
                credencialSimples.setIdVeiculoPermissionario(obj[3] != null ? ((BigDecimal) obj[3]).longValue() : null);
                toReturn.add(credencialSimples);
            }
        }
        return toReturn;
    }

    private List<TipoRequerenteCredencialRBTrans> organizarRequerentesSelecionados(FiltroGeracaoCredencialRBTrans filtroGeracao) {
        List<TipoRequerenteCredencialRBTrans> requerentes = Lists.newArrayList();
        if (filtroGeracao.getTipoRequerenteCredencialRBTrans() == null) {
            if (TipoPermissaoRBTrans.FRETE.equals(filtroGeracao.getTipoPermissaoRBTrans())) {
                requerentes.add(TipoRequerenteCredencialRBTrans.PERMISSIONARIO);
            } else {
                requerentes.addAll(Lists.newArrayList(TipoRequerenteCredencialRBTrans.values()));
            }
        } else {
            requerentes.add(filtroGeracao.getTipoRequerenteCredencialRBTrans());
        }
        return requerentes;
    }

    private List<TipoCredencialRBTrans> organizarTiposCredenciaisSelecionadas(FiltroGeracaoCredencialRBTrans filtroGeracao) {
        List<TipoCredencialRBTrans> tipos = Lists.newArrayList();
        if (filtroGeracao.getTipoCredencialRBTrans() == null) {
            tipos.addAll(Lists.newArrayList(TipoCredencialRBTrans.values()));
        } else {
            tipos.add(filtroGeracao.getTipoCredencialRBTrans());
        }
        return tipos;
    }

    private CredencialTrafego criarCredencialTrafego(FiltroGeracaoCredencialRBTrans filtroGeracao, TipoCredencialRBTrans tipoCredencial, PermissaoTransporte permissao, Date dataGeracao, TipoRequerenteCredencialRBTrans requerente, ParametrosTransitoTransporte parametro) {
        return new CredencialTrafego(dataGeracao,
            retornaDataValidadeCredencial(parametro, permissao, filtroGeracao.getAnoValidade()),
            tipoCredencial,
            requerente,
            TipoPagamentoCredencialRBTrans.NAO_PAGO,
            false,
            permissao.getVeiculoVigente(),
            permissao);
    }

    private CredencialTransporte criarCredencialTransporte(FiltroGeracaoCredencialRBTrans filtroGeracao, TipoCredencialRBTrans tipoCredencial, PermissaoTransporte permissao, Date dataGeracao, ParametrosTransitoTransporte parametro, TipoRequerenteCredencialRBTrans requerente, CadastroEconomico cadastroEconomico) {
        return new CredencialTransporte(dataGeracao,
            retornaDataValidadeCredencial(parametro, permissao, filtroGeracao.getAnoValidade()),
            tipoCredencial,
            requerente,
            TipoPagamentoCredencialRBTrans.NAO_PAGO,
            false,
            permissao,
            cadastroEconomico);
    }

    @Override
    public CredencialRBTrans recuperar(Object id) {
        CredencialRBTrans credencial = em.find(CredencialRBTrans.class, id);
        credencial.getCalculosCredencial().size();
        return credencial;
    }

    public void encerrarCredenciailVeiculoPermissionario(VeiculoPermissionario veiculoPermissionario) {
        String hql = " select ct from CredencialTrafego ct " +
            " where ct.permissaoTransporte.id = :permissao_id " +
            "   and ct.veiculoPermissionario.id = :veiculo_id " +
            "   and (ct.dataValidade is null or ct.dataValidade >= :data_atual) ";
        Query q = em.createQuery(hql);
        q.setParameter("permissao_id", veiculoPermissionario.getPermissaoTransporte().getId());
        q.setParameter("veiculo_id", veiculoPermissionario.getId());
        q.setParameter("data_atual", Util.getDataHoraMinutoSegundoZerado(Calendar.getInstance().getTime()));
        List<CredencialTrafego> retorno = q.getResultList();
        if (retorno != null && !retorno.isEmpty()) {
            for (CredencialTrafego credencialTrafego : retorno) {
                credencialTrafego.setDataValidade(DataUtil.adicionaDias(Calendar.getInstance().getTime(), -1));
                em.merge(credencialTrafego);
            }
        }
    }

    public void encerrarCredenciailPermissionario(Permissionario permissionario) {
        String hql = " select ct from CredencialTransporte ct " +
            " where ct.permissaoTransporte.id = :permissao_id " +
            "   and ct.cadastroEconomico.id = :cadastroeconomico_id " +
            "   and ct.tipoRequerente =:tipoRequerente " +
            "   and (ct.dataValidade is null or ct.dataValidade >= :data_atual) ";
        Query q = em.createQuery(hql);
        q.setParameter("tipoRequerente", TipoRequerenteCredencialRBTrans.PERMISSIONARIO);
        q.setParameter("permissao_id", permissionario.getPermissaoTransporte().getId());
        q.setParameter("cadastroeconomico_id", permissionario.getCadastroEconomico().getId());
        q.setParameter("data_atual", Util.getDataHoraMinutoSegundoZerado(Calendar.getInstance().getTime()));
        List<CredencialTransporte> retorno = q.getResultList();
        if (retorno != null && !retorno.isEmpty()) {
            for (CredencialTransporte credencialTransporte : retorno) {
                credencialTransporte.setDataValidade(DataUtil.adicionaDias(Calendar.getInstance().getTime(), -1));
                em.merge(credencialTransporte);
            }
        }
    }

    public void criarCredencialTransporte(Date dataGeracao, ParametrosTransitoTransporte parametro, PermissaoTransporte permissao, CadastroEconomico cadastroEconomico) {
        CredencialTransporte credencialTransporte = new CredencialTransporte(dataGeracao,
            retornaDataValidadeCredencial(parametro, permissao, DataUtil.getAno(dataGeracao)),
            TipoCredencialRBTrans.TRANSPORTE,
            TipoRequerenteCredencialRBTrans.PERMISSIONARIO,
            TipoPagamentoCredencialRBTrans.NAO_PAGO,
            false,
            permissao,
            cadastroEconomico);
        credencialTransporte.setNumero(gerarNumero());
        em.persist(credencialTransporte);

    }

    public void criarCredencialTrafego(Date dataGeracao, ParametrosTransitoTransporte parametro, PermissaoTransporte permissao, VeiculoPermissionario veiculoPermissionario) {
        CredencialTrafego credencialTrafego = new CredencialTrafego(dataGeracao,
            retornaDataValidadeCredencial(parametro, permissao, DataUtil.getAno(dataGeracao)),
            TipoCredencialRBTrans.TRAFEGO,
            TipoRequerenteCredencialRBTrans.PERMISSIONARIO,
            TipoPagamentoCredencialRBTrans.NAO_PAGO,
            false,
            veiculoPermissionario,
            permissao);
        credencialTrafego.setNumero(gerarNumero());
        em.persist(credencialTrafego);
    }
}
