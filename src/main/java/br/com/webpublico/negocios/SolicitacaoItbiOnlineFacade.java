package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.TemplateEmail;
import br.com.webpublico.entidades.comum.TipoTemplateEmail;
import br.com.webpublico.entidades.comum.trocatag.TrocaTagAvaliacaoFiscalSolicitacaoItbiOnline;
import br.com.webpublico.entidades.comum.trocatag.TrocaTagRejeicaoSolicitacaoItbiOnline;
import br.com.webpublico.entidadesauxiliares.VODadosCadastroITBI;
import br.com.webpublico.entidadesauxiliares.VOProprietarioITBI;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.negocios.comum.TemplateEmailFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.EmailService;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
public class SolicitacaoItbiOnlineFacade extends AbstractFacade<SolicitacaoItbiOnline> {

    private static final BigDecimal CEM = new BigDecimal(100);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroFacade cadastroFacade;
    @EJB
    private TipoIsencaoITBIFacade tipoIsencaoITBIFacade;
    @EJB
    private TipoNegociacaoFacade tipoNegociacaoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ParametroITBIFacade parametroITBIFacade;
    @EJB
    private BloqueioTransferenciaProprietarioFacade bloqueioTransferenciaProprietarioFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private TramiteSolicitacaoItbiOnlineFacade tramiteSolicitacaoItbiOnlineFacade;
    @EJB
    private TemplateEmailFacade templateEmailFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;
    @EJB
    private CalculoITBIFacade calculoITBIFacade;
    @EJB
    private GeraValorDividaITBI geraValorDividaITBI;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;

    public SolicitacaoItbiOnlineFacade() {
        super(SolicitacaoItbiOnline.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public Date buscarDataOperacao() {
        return sistemaFacade.getDataOperacao();
    }

    public Exercicio buscarExercicioCorrente() {
        return sistemaFacade.getExercicioCorrente();
    }

    public UsuarioSistema buscarUsuarioSistema() {
        return Util.recuperarUsuarioCorrente();
    }

    public BloqueioTransferenciaProprietarioFacade getBloqueioTransferenciaProprietarioFacade() {
        return bloqueioTransferenciaProprietarioFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public FeriadoFacade getFeriadoFacade() {
        return feriadoFacade;
    }

    public TramiteSolicitacaoItbiOnlineFacade getTramiteSolicitacaoItbiOnlineFacade() {
        return tramiteSolicitacaoItbiOnlineFacade;
    }

    @Override
    public SolicitacaoItbiOnline recuperar(Object id) {
        SolicitacaoItbiOnline processoCalculoITBI = em.find(SolicitacaoItbiOnline.class, id);
        Hibernate.initialize(processoCalculoITBI.getCalculos());
        for (CalculoSolicitacaoItbiOnline calculo : processoCalculoITBI.getCalculos()) {
            Hibernate.initialize(calculo.getAdquirentesCalculo());
            Hibernate.initialize(calculo.getTransmitentesCalculo());
            Hibernate.initialize(calculo.getItensCalculo());
            Hibernate.initialize(calculo.getProprietariosSimulacao());
            calculo.setIsentoSub(calculo.getTipoIsencaoITBI() != null);
        }
        Hibernate.initialize(processoCalculoITBI.getDocumentos());
        return processoCalculoITBI;
    }

    public Map<Integer, List<PropriedadeRural>> simulacaoDasProporcoesRural(CadastroRural cadastro, List<CalculoSolicitacaoItbiOnline> calculos) {

        Map<Integer, List<PropriedadeRural>> novasPropriedadesDoCalculoSimulacao = new HashMap<>();
        ordenarCalculos(calculos);
        for (CalculoSolicitacaoItbiOnline calculo : calculos) {
            novasPropriedadesDoCalculoSimulacao.put(calculo.getOrdemCalculo(), Lists.newArrayList());
            List<PropriedadeRural> propriedadesQueNaoSaoTransmitentes = Lists.newArrayList();
            List<PropriedadeRural> propriedadesRural = cadastroRuralFacade.recuperarPropriedadesVigentes(cadastro);
            List<PropriedadeRural> propriedadesImovelParaSimulacao = Lists.newArrayList();
            List<PropriedadeRural> adquirentesCalculoAnterior = Lists.newArrayList();

            if (calculo.getOrdemCalculo() > 1) {
                Integer key = calculo.getOrdemCalculo() - 1;
                if (novasPropriedadesDoCalculoSimulacao.containsKey(key)) {
                    adquirentesCalculoAnterior = novasPropriedadesDoCalculoSimulacao.get(key);
                }
            }

            if (!adquirentesCalculoAnterior.isEmpty()) {
                propriedadesImovelParaSimulacao.addAll(adquirentesCalculoAnterior);
            } else {
                for (PropriedadeRural p : propriedadesRural) {
                    PropriedadeRural propriedade = new PropriedadeRural();
                    propriedade.setDataRegistro(p.getDataRegistro());
                    propriedade.setImovel(p.getImovel());
                    propriedade.setInicioVigencia(p.getInicioVigencia());
                    propriedade.setPessoa(p.getPessoa());
                    propriedade.setProporcao(p.getProporcao());
                    propriedade.setTipoProprietario(p.getTipoProprietario());

                    propriedadesImovelParaSimulacao.add(propriedade);
                }
            }

            List<PropriedadeRural> propriedadesQueSaoTransmitentes = Lists.newArrayList();

            for (PropriedadeRural propriedade : propriedadesImovelParaSimulacao) {
                for (TransmitentesSolicitacaoItbiOnline transmitenteCaculo : calculo.getTransmitentesCalculo()) {
                    if (propriedade.getPessoa().equals(transmitenteCaculo.getTransmitente())) {
                        propriedadesQueSaoTransmitentes.add(propriedade);
                    }
                }
                if (!propriedadesQueSaoTransmitentes.contains(propriedade)) {
                    propriedadesQueNaoSaoTransmitentes.add(propriedade);
                }
            }

            BigDecimal proporcaoAjustada = calculo.getTotalPercentualAdquirentes().setScale(2, RoundingMode.DOWN)
                .divide(BigDecimal.valueOf(propriedadesQueSaoTransmitentes.isEmpty() ? calculo.getTransmitentesCalculo().size() : propriedadesQueSaoTransmitentes.size()), 2, RoundingMode.DOWN);

            for (PropriedadeRural propriedadeAjustada : propriedadesQueSaoTransmitentes) {

                PropriedadeRural propriedadeSimulacao = new PropriedadeRural();
                propriedadeSimulacao.setDataRegistro(propriedadeAjustada.getDataRegistro());
                propriedadeSimulacao.setImovel(propriedadeAjustada.getImovel());
                propriedadeSimulacao.setInicioVigencia(propriedadeAjustada.getInicioVigencia());
                propriedadeSimulacao.setPessoa(propriedadeAjustada.getPessoa());
                propriedadeSimulacao.setTipoProprietario(propriedadeAjustada.getTipoProprietario());
                propriedadeSimulacao.setFinalVigencia(propriedadeAjustada.getFinalVigencia());

                if (proporcaoAjustada.doubleValue() > propriedadeAjustada.getProporcao() ||
                    propriedadeAjustada.getProporcao().equals(proporcaoAjustada.doubleValue()) ||
                    calculo.getTotalPercentualAdquirentes().compareTo(buscarProporcaoDosProprietarioJaVigentesRural(propriedadesQueSaoTransmitentes)) == 0) {
                    propriedadeSimulacao.setFinalVigencia(new Date());
                    propriedadeSimulacao.setProporcao(0.0);
                } else {
                    propriedadeSimulacao.setProporcao(propriedadeAjustada.getProporcao() - proporcaoAjustada.doubleValue());
                    novasPropriedadesDoCalculoSimulacao.get(calculo.getOrdemCalculo()).add(propriedadeSimulacao);
                }
            }


            for (AdquirentesSolicitacaoITBIOnline ad : calculo.getAdquirentesCalculo()) {
                BigDecimal proprocaoJaExisteAdquirente = BigDecimal.ZERO;
                for (PropriedadeRural propriedadesQueNaoSaoTransmitente : propriedadesQueNaoSaoTransmitentes) {
                    if (ad.getAdquirente().equals(propriedadesQueNaoSaoTransmitente.getPessoa())) {
                        proprocaoJaExisteAdquirente = proprocaoJaExisteAdquirente.add(BigDecimal.valueOf(propriedadesQueNaoSaoTransmitente.getProporcao()));
                        propriedadesQueNaoSaoTransmitente.setFinalVigencia(new Date());
                    }
                }
                PropriedadeRural propriedade = new PropriedadeRural();
                propriedade.setDataRegistro(new Date());
                propriedade.setImovel(cadastro);
                propriedade.setInicioVigencia(new Date());
                propriedade.setPessoa(ad.getAdquirente());
                propriedade.setProporcao(ad.getPercentual().add(proprocaoJaExisteAdquirente).doubleValue());
                propriedade.setTipoProprietario(TipoProprietario.COMPRAVENDA);
                novasPropriedadesDoCalculoSimulacao.get(calculo.getOrdemCalculo()).add(propriedade);
            }

            for (PropriedadeRural propriedadesQueNaoSaoTransmitente : propriedadesQueNaoSaoTransmitentes) {
                boolean naoEhAdquirente = true;
                for (AdquirentesSolicitacaoITBIOnline adquirentesCalculoITBI : calculo.getAdquirentesCalculo()) {
                    if (adquirentesCalculoITBI.getAdquirente().equals(propriedadesQueNaoSaoTransmitente.getPessoa())) {
                        naoEhAdquirente = false;
                    }
                }
                if (naoEhAdquirente) {
                    novasPropriedadesDoCalculoSimulacao.get(calculo.getOrdemCalculo()).add(propriedadesQueNaoSaoTransmitente);
                }
            }
        }
        return novasPropriedadesDoCalculoSimulacao;
    }

    public Map<Integer, List<Propriedade>> simulacaoDasProporcoesImobiliario(CadastroImobiliario cadastro, List<CalculoSolicitacaoItbiOnline> calculos) {

        Map<Integer, List<Propriedade>> novasPropriedadesDoCalculoSimulacao = new HashMap<>();
        ordenarCalculos(calculos);
        for (CalculoSolicitacaoItbiOnline calculo : calculos) {
            novasPropriedadesDoCalculoSimulacao.put(calculo.getOrdemCalculo(), Lists.newArrayList());
            List<Propriedade> propriedadesQueNaoSaoTransmitentes = Lists.newArrayList();
            List<Propriedade> propriedadesImovel = cadastroImobiliarioFacade.recuperarProprietariosVigentes(cadastro);
            List<Propriedade> propriedadesImovelParaSimulacao = Lists.newArrayList();
            List<Propriedade> adquirentesCalculoAnterior = Lists.newArrayList();

            if (calculo.getOrdemCalculo() > 1) {
                Integer key = calculo.getOrdemCalculo() - 1;
                if (novasPropriedadesDoCalculoSimulacao.containsKey(key)) {
                    adquirentesCalculoAnterior = novasPropriedadesDoCalculoSimulacao.get(key);
                }
            }

            if (!adquirentesCalculoAnterior.isEmpty()) {
                propriedadesImovelParaSimulacao.addAll(adquirentesCalculoAnterior);
            } else {
                for (Propriedade p : propriedadesImovel) {
                    Propriedade propriedade = new Propriedade();
                    propriedade.setDataRegistro(p.getDataRegistro());
                    propriedade.setImovel(p.getImovel());
                    propriedade.setInicioVigencia(p.getInicioVigencia());
                    propriedade.setPessoa(p.getPessoa());
                    propriedade.setProporcao(p.getProporcao());
                    propriedade.setTipoProprietario(p.getTipoProprietario());
                    propriedade.setVeioPorITBI(p.getVeioPorITBI());

                    propriedadesImovelParaSimulacao.add(propriedade);
                }
            }

            List<Propriedade> propriedadesQueSaoTransmitentes = Lists.newArrayList();

            for (Propriedade propriedade : propriedadesImovelParaSimulacao) {
                for (TransmitentesSolicitacaoItbiOnline transmitenteCaculo : calculo.getTransmitentesCalculo()) {
                    if (propriedade.getPessoa().equals(transmitenteCaculo.getTransmitente())) {
                        propriedadesQueSaoTransmitentes.add(propriedade);
                    }
                }
                if (!propriedadesQueSaoTransmitentes.contains(propriedade)) {
                    propriedadesQueNaoSaoTransmitentes.add(propriedade);
                }
            }

            BigDecimal proporcaoAjustada = calculo.getTotalPercentualAdquirentes().setScale(2, RoundingMode.DOWN)
                .divide(BigDecimal.valueOf(propriedadesQueSaoTransmitentes.isEmpty() ? calculo.getTransmitentesCalculo().size() : propriedadesQueSaoTransmitentes.size()), 2, RoundingMode.DOWN);

            for (Propriedade propriedadeAjustada : propriedadesQueSaoTransmitentes) {

                Propriedade propriedadeSimulacao = new Propriedade();
                propriedadeSimulacao.setDataRegistro(propriedadeAjustada.getDataRegistro());
                propriedadeSimulacao.setImovel(propriedadeAjustada.getImovel());
                propriedadeSimulacao.setInicioVigencia(propriedadeAjustada.getInicioVigencia());
                propriedadeSimulacao.setPessoa(propriedadeAjustada.getPessoa());
                propriedadeSimulacao.setTipoProprietario(propriedadeAjustada.getTipoProprietario());
                propriedadeSimulacao.setVeioPorITBI(propriedadeAjustada.getVeioPorITBI());
                propriedadeSimulacao.setFinalVigencia(propriedadeAjustada.getFinalVigencia());

                if (propriedadeAjustada.getProporcao().equals(proporcaoAjustada.doubleValue()) ||
                    calculo.getTotalPercentualAdquirentes().compareTo(buscarProporcaoDosProprietarioJaVigentesImovel(propriedadesQueSaoTransmitentes)) == 0) {
                    propriedadeSimulacao.setFinalVigencia(new Date());
                    propriedadeSimulacao.setProporcao(0.0);
                } else {
                    if (proporcaoAjustada.doubleValue() > propriedadeAjustada.getProporcao()) {
                        propriedadeSimulacao.setProporcao(0.0);
                    } else {
                        propriedadeSimulacao.setProporcao(propriedadeAjustada.getProporcao() - proporcaoAjustada.doubleValue());
                    }
                    novasPropriedadesDoCalculoSimulacao.get(calculo.getOrdemCalculo()).add(propriedadeSimulacao);
                }
            }

            for (AdquirentesSolicitacaoITBIOnline ad : calculo.getAdquirentesCalculo()) {
                BigDecimal proprocaoJaExisteAdquirente = BigDecimal.ZERO;
                for (Propriedade propriedadesQueNaoSaoTransmitente : propriedadesQueNaoSaoTransmitentes) {
                    if (ad.getAdquirente().equals(propriedadesQueNaoSaoTransmitente.getPessoa())) {
                        proprocaoJaExisteAdquirente = proprocaoJaExisteAdquirente.add(BigDecimal.valueOf(propriedadesQueNaoSaoTransmitente.getProporcao()));
                        propriedadesQueNaoSaoTransmitente.setFinalVigencia(new Date());
                    }
                }
                Propriedade propriedade = new Propriedade();
                propriedade.setDataRegistro(new Date());
                propriedade.setImovel(cadastro);
                propriedade.setInicioVigencia(new Date());
                propriedade.setPessoa(ad.getAdquirente());
                propriedade.setProporcao(ad.getPercentual().add(proprocaoJaExisteAdquirente).doubleValue());
                propriedade.setTipoProprietario(TipoProprietario.COMPRAVENDA);
                propriedade.setVeioPorITBI(true);
                novasPropriedadesDoCalculoSimulacao.get(calculo.getOrdemCalculo()).add(propriedade);
            }

            for (Propriedade propriedadesQueNaoSaoTransmitente : propriedadesQueNaoSaoTransmitentes) {
                boolean naoEhAdquirente = true;
                for (AdquirentesSolicitacaoITBIOnline adquirentesCalculoITBI : calculo.getAdquirentesCalculo()) {
                    if (adquirentesCalculoITBI.getAdquirente().equals(propriedadesQueNaoSaoTransmitente.getPessoa())) {
                        naoEhAdquirente = false;
                    }
                }
                if (naoEhAdquirente) {
                    novasPropriedadesDoCalculoSimulacao.get(calculo.getOrdemCalculo()).add(propriedadesQueNaoSaoTransmitente);
                }
            }
        }
        return novasPropriedadesDoCalculoSimulacao;
    }

    private void ordenarCalculos(List<CalculoSolicitacaoItbiOnline> calculos) {
        Collections.sort(calculos, new Comparator<CalculoSolicitacaoItbiOnline>() {
            @Override
            public int compare(CalculoSolicitacaoItbiOnline o1, CalculoSolicitacaoItbiOnline o2) {
                return ComparisonChain.start().compare(o1.getOrdemCalculo(), o2.getOrdemCalculo()).result();
            }
        });
    }

    public Long buscarSequeciaCodigoSolicitacaoITBI(SolicitacaoItbiOnline solicitacao) {
        try {
            String sql = " select coalesce(max(s.codigo), 0) from solicitacaoitbionline s " +
                " where s.exercicio_id = :exercicio ";

            Query q = em.createNativeQuery(sql);
            q.setParameter("exercicio", solicitacao.getExercicio().getId());

            List<BigDecimal> codigos = q.getResultList();

            if (codigos != null && !codigos.isEmpty()) {
                BigDecimal codigo = codigos.get(0);
                if (codigo != null) {
                    return codigo.longValue() + 1;
                }
            }
            return 1L;
        } catch (Exception e) {
            logger.error("Erro ao buscar codigo sequencial do processo de calculo de ITBI. ", e);
        }
        return 1L;
    }

    public List<TipoIsencaoITBI> buscarTiposDeIsencaoITBI(String parte) {
        return tipoIsencaoITBIFacade.listaFiltrando(parte.trim(), "nome", "descricao");
    }

    public List<? extends Cadastro> buscarCadastroPorTipo(String parte, boolean isImobiliario) {
        return isImobiliario ? cadastroFacade.getCadastroImobiliarioFacade().listaFiltrando(parte.trim(), "inscricaoCadastral") :
            cadastroFacade.getCadastroRuralFacade().listaFiltrando(parte.trim(), "codigo", "nomePropriedade");
    }

    public List<TipoNegociacao> buscarTiposNegociacaoAtivas() {
        return tipoNegociacaoFacade.buscarTiposNegociacaoAtivas();
    }

    public List<VOProprietarioITBI> buscarProprietarios(boolean isImobiliario, Long idCadastro) {
        String sql = " select p.id as idpessoa, prop.imovel_id, coalesce(pf.nome, pj.razaosocial), coalesce(pf.cpf, pj.cnpj) " +
            " from " + (isImobiliario ? "propriedade" : "propriedaderural") + " prop " +
            " inner join pessoa p on prop.pessoa_id = p.id " +
            " left join pessoajuridica pj on p.id = pj.id " +
            " left join pessoafisica pf on p.id = pf.id " +
            " where prop.imovel_id = :idCadastro " +
            " and (prop.finalvigencia is null or trunc(prop.finalvigencia) > trunc(current_date))  " +
            " order by prop.proporcao ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", idCadastro);

        List<Object[]> dados = q.getResultList();
        List<VOProprietarioITBI> proprietarios = Lists.newArrayList();

        if (dados != null && !dados.isEmpty()) {
            for (Object[] obj : dados) {
                VOProprietarioITBI proprietario = new VOProprietarioITBI();
                proprietario.setIdPessoa(obj[0] != null ? ((BigDecimal) obj[0]).longValue() : null);
                proprietario.setIdCadastro(obj[1] != null ? ((BigDecimal) obj[1]).longValue() : null);
                proprietario.setNomeRazaoSocial(obj[2] != null ? (String) obj[2] : "");
                proprietario.setCpfCnpj(obj[3] != null ? formatarCpfCnpj((String) obj[3]) : "");

                proprietarios.add(proprietario);
            }
        }
        return proprietarios;
    }

    public VODadosCadastroITBI montarDadosProprietarioImobiliario(Long idCadastro) {
        String sql = " select setor.codigo as codigo_setor, quadra.codigo as codigo_quadra, lote.codigolote as codigo_lote, " +
            "       lote.descricaoloteamento as descricao_lote, tl.descricao as tipo_logradouro, log.nome as logradouro, " +
            "       ci.numero, lote.complemento as complemento_lote, ci.complementoendereco as complemento, " +
            "       bairro.descricao as bairro, lote.arealote as area_terreno, ci.areatotalconstruida as area_construida, " +
            "       (select ev.valor from eventocalculobci ev " +
            "        inner join eventoconfiguradobci evconfig on evconfig.id = ev.eventocalculo_id " +
            "        inner join eventocalculo calc on evconfig.eventocalculo_id = calc.id " +
            "        where ev.cadastroimobiliario_id = ci.id " +
            "        and calc.identificacao = :eventoCalculo " +
            "        order by ev.id desc fetch first 1 rows only)  as valor_venal " +
            " from cadastroimobiliario ci " +
            " left join lote on ci.lote_id = lote.id " +
            " left join setor on lote.setor_id = setor.id " +
            " left join quadra on lote.quadra_id = quadra.id " +
            " left join testada on lote.id = testada.lote_id " +
            " left join face on testada.face_id = face.id " +
            " left join logradourobairro lb on face.logradourobairro_id = lb.id " +
            " left join logradouro log on lb.logradouro_id = log.id " +
            " left join tipologradouro tl on log.tipologradouro_id = tl.id " +
            " left join bairro on lb.bairro_id = bairro.id " +
            " where ci.id = :idCadastro " +
            " and testada.id = (select t.id from testada t " +
            "                   where t.lote_id = lote.id " +
            "                   order by t.principal desc fetch first 1 rows only) ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", idCadastro);
        q.setParameter("eventoCalculo", "valorVenalImovelBCI");

        List<Object[]> dados = q.getResultList();

        if (dados != null && !dados.isEmpty()) {
            Object[] obj = dados.get(0);
            VODadosCadastroITBI proprietario = new VODadosCadastroITBI();

            proprietario.setCodigoSetor(obj[0] != null ? obj[0].toString() : "");
            proprietario.setCodigoQuadra(obj[1] != null ? obj[1].toString() : "");
            proprietario.setCodigoLote(obj[2] != null ? obj[2].toString() : "");
            proprietario.setDescricaoLote(obj[3] != null ? obj[3].toString() : "");
            proprietario.setTipoLogradouro(obj[4] != null ? obj[4].toString() : "");
            proprietario.setLogradouro(obj[5] != null ? obj[5].toString() : "");
            proprietario.setNumero(obj[6] != null ? obj[6].toString() : "");
            proprietario.setComplementoLote(obj[7] != null ? obj[7].toString() : "");
            proprietario.setComplemento(obj[8] != null ? obj[8].toString() : "");
            proprietario.setBairro(obj[9] != null ? obj[9].toString() : "");
            proprietario.setAreaTerreno(obj[10] != null ? (BigDecimal) obj[10] : BigDecimal.ZERO);
            proprietario.setAreaConstruida(obj[11] != null ? (BigDecimal) obj[11] : BigDecimal.ZERO);
            proprietario.setValorVenal(obj[12] != null ? (BigDecimal) obj[12] : BigDecimal.ZERO);
            proprietario.setCodigoPropriedade("");
            proprietario.setNomePropriedade("");

            return proprietario;
        }
        return null;
    }

    public VODadosCadastroITBI montarDadosProprietarioRural(Long idCadastro) {
        String sql = " SELECT CR.CODIGO as codigo, " +
            " CR.NOMEPROPRIEDADE as nome_propriedade, " +
            " CR.LOCALIZACAOLOTE as localizacao, " +
            " CR.AREALOTE as area_lote, " +
            " CR.VALORVENALLOTE as valor_venal " +
            " FROM CADASTRORURAL CR " +
            " WHERE CR.ID = :idCadastro ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", idCadastro);

        List<Object[]> dados = q.getResultList();

        if (dados != null && !dados.isEmpty()) {
            Object[] obj = dados.get(0);
            VODadosCadastroITBI proprietario = new VODadosCadastroITBI();

            proprietario.setCodigoPropriedade(obj[0] != null ? obj[0].toString() : "");
            proprietario.setNomePropriedade(obj[1] != null ? (String) obj[1] : "");
            proprietario.setLogradouro(obj[2] != null ? (String) obj[2] : "");
            proprietario.setAreaTerreno(obj[3] != null ? new BigDecimal(StringUtil.retornaApenasNumerosComPontoVirgula(obj[3].toString().replace(",", "."))) : BigDecimal.ZERO);
            proprietario.setValorVenal(obj[4] != null ? (BigDecimal) obj[4] : BigDecimal.ZERO);

            proprietario.setCodigoSetor("");
            proprietario.setCodigoQuadra("");
            proprietario.setCodigoLote("");
            proprietario.setDescricaoLote("");
            proprietario.setTipoLogradouro("");
            proprietario.setNumero("");
            proprietario.setComplementoLote("");
            proprietario.setComplemento("");
            proprietario.setBairro("");
            proprietario.setAreaConstruida(BigDecimal.ZERO);


            return proprietario;
        }
        return null;
    }

    private String formatarCpfCnpj(String cpfCnpj) {
        if (!Strings.isNullOrEmpty(cpfCnpj)) {
            if (StringUtil.retornaApenasNumeros(cpfCnpj).length() == 11) {
                return Util.formatarCpf(cpfCnpj);
            } else {
                return Util.formatarCnpj(cpfCnpj);
            }
        }
        return "";
    }

    public List<PessoaFisica> buscarAuditorFiscal(String parte) {
        return pessoaFacade.getPessoaFisicaFacade().listaFiscalTributario(parte);
    }

    public boolean hasOutraPessoaComMesmoDocto(Pessoa pessoa) {
        return pessoa.isPessoaFisica() ? pessoaFacade.hasOutraPessoaComMesmoCpf((PessoaFisica) pessoa, true) :
            pessoaFacade.hasOutraPessoaComMesmoCnpj((PessoaJuridica) pessoa, true);
    }

    public ParametrosITBI recuperarParametroITBIVigente(Exercicio exercicio, TipoITBI tipoITBI) {
        return parametroITBIFacade.getParametroVigente(exercicio, tipoITBI);
    }

    private BigDecimal buscarProporcaoDosProprietarioJaVigentesImovel(List<Propriedade> propriedadesImovel) {
        BigDecimal proporcaoDosProprietarioJaVigentes = BigDecimal.ZERO;
        for (Propriedade propriedade : propriedadesImovel) {
            proporcaoDosProprietarioJaVigentes = proporcaoDosProprietarioJaVigentes.add(BigDecimal.valueOf(propriedade.getProporcao()));
        }
        return proporcaoDosProprietarioJaVigentes;
    }

    private BigDecimal buscarProporcaoDosProprietarioJaVigentesRural(List<PropriedadeRural> propriedadesRurais) {
        BigDecimal proporcaoDosProprietarioJaVigentes = BigDecimal.ZERO;
        for (PropriedadeRural propriedade : propriedadesRurais) {
            proporcaoDosProprietarioJaVigentes = proporcaoDosProprietarioJaVigentes.add(BigDecimal.valueOf(propriedade.getProporcao()));
        }
        return proporcaoDosProprietarioJaVigentes;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public void popularDocumentosProcurador(ParametrosITBI parametrosITBI,
                                            SolicitacaoItbiOnline solicitacaoItbiOnline) {
        if (parametrosITBI == null) return;
        List<ParametrosITBIDocumento> documentos = parametrosITBI.getDocumentosPorNatureza(ParametrosITBIDocumento.NaturezaDocumento.PROCURADOR);
        if (documentos == null) return;
        for (ParametrosITBIDocumento parametrosITBIDocumento : documentos) {
            String descricao = parametrosITBIDocumento.getDescricao() + " (Procurador)";
            if (solicitacaoItbiOnline.getDocumentos().stream().noneMatch(sd -> sd.getDescricao().equals(descricao))) {
                solicitacaoItbiOnline.getDocumentos().add(new SolicitacaoItbiOnlineDocumento(solicitacaoItbiOnline,
                    parametrosITBIDocumento, descricao));
            }
        }
    }

    public void popularDocumentosSolicitacao(ParametrosITBI parametrosITBI,
                                             SolicitacaoItbiOnline solicitacaoItbiOnline) {
        if (parametrosITBI == null) return;
        List<ParametrosITBIDocumento> documentos = parametrosITBI.getDocumentosPorNatureza(ParametrosITBIDocumento.NaturezaDocumento.SOLICITACAO);
        if (documentos == null) return;
        for (ParametrosITBIDocumento parametrosITBIDocumento : documentos) {
            String descricao = parametrosITBIDocumento.getDescricao();
            if (solicitacaoItbiOnline.getDocumentos().stream().noneMatch(sd -> sd.getDescricao().equals(descricao))) {
                solicitacaoItbiOnline.getDocumentos().add(new SolicitacaoItbiOnlineDocumento(solicitacaoItbiOnline,
                    parametrosITBIDocumento, descricao));
            }
        }
    }

    public void popularDocumentosTransmitente(ParametrosITBI parametrosITBI,
                                              SolicitacaoItbiOnline solicitacaoItbiOnline,
                                              TransmitentesSolicitacaoItbiOnline transmitente) {
        if (parametrosITBI == null) return;
        List<ParametrosITBIDocumento> documentos = parametrosITBI.getDocumentosPorNatureza(ParametrosITBIDocumento.NaturezaDocumento.TRANSMITENTE);
        if (documentos == null) return;
        for (ParametrosITBIDocumento parametrosITBIDocumento : documentos) {
            String descricao = parametrosITBIDocumento.getDescricao() + " (" + transmitente.getTransmitente().getNome() + ")";
            if (solicitacaoItbiOnline.getDocumentos().stream().noneMatch(sd -> sd.getDescricao().equals(descricao))) {
                solicitacaoItbiOnline.getDocumentos().add(new SolicitacaoItbiOnlineDocumento(solicitacaoItbiOnline,
                    parametrosITBIDocumento, descricao));
            }
        }
    }

    public void popularDocumentosAdquirente(ParametrosITBI parametrosITBI,
                                            SolicitacaoItbiOnline solicitacaoItbiOnline,
                                            AdquirentesSolicitacaoITBIOnline adquirente) {
        if (parametrosITBI == null) return;
        List<ParametrosITBIDocumento> documentos = parametrosITBI.getDocumentosPorNatureza(ParametrosITBIDocumento.NaturezaDocumento.ADQUIRENTE);
        if (documentos == null) return;
        for (ParametrosITBIDocumento parametrosITBIDocumento : documentos) {
            String descricao = parametrosITBIDocumento.getDescricao() + " (" + adquirente.getAdquirente().getNome() + ")";
            if (solicitacaoItbiOnline.getDocumentos().stream().noneMatch(sd -> sd.getDescricao().equals(descricao))) {
                solicitacaoItbiOnline.getDocumentos().add(new SolicitacaoItbiOnlineDocumento(solicitacaoItbiOnline,
                    parametrosITBIDocumento, descricao));
            }
        }
    }

    public void removerDocumentos(SolicitacaoItbiOnline solicitacaoItbiOnline,
                                  String parteDescricao) {
        List<SolicitacaoItbiOnlineDocumento> documentos = Lists.newArrayList();
        solicitacaoItbiOnline.getDocumentos()
            .stream()
            .filter(sd -> sd.getDescricao().endsWith(parteDescricao))
            .forEach(documentos::add);
        if (!documentos.isEmpty()) {
            solicitacaoItbiOnline.getDocumentos().removeAll(documentos);
        }
    }

    @Override
    public SolicitacaoItbiOnline salvarRetornando(SolicitacaoItbiOnline solicitacaoItbiOnline) {
        Boolean registrarTramite = solicitacaoItbiOnline.getId() == null;
        if (solicitacaoItbiOnline.getCodigo() == null) {
            solicitacaoItbiOnline.setCodigo(buscarSequeciaCodigoSolicitacaoITBI(solicitacaoItbiOnline));
        }
        solicitacaoItbiOnline = super.salvarRetornando(solicitacaoItbiOnline);
        if (registrarTramite) {
            adicionarTramite(solicitacaoItbiOnline, sistemaFacade.getUsuarioCorrente().getNome(),
                SituacaoSolicitacaoITBI.EM_ANALISE, "");
        }
        return solicitacaoItbiOnline;
    }

    public void aprovarSolicitacao(SolicitacaoItbiOnline solicitacaoItbiOnline) {
        solicitacaoItbiOnline.setSituacao(SituacaoSolicitacaoITBI.APROVADA);
        salvar(solicitacaoItbiOnline);
        adicionarTramite(solicitacaoItbiOnline, sistemaFacade.getUsuarioCorrente().getNome(),
            SituacaoSolicitacaoITBI.APROVADA, "");
    }

    public void rejeitarSolicitacao(SolicitacaoItbiOnline solicitacaoItbiOnline,
                                    String motivoRejeicao) {
        if (Strings.isNullOrEmpty(motivoRejeicao)) {
            throw new ValidacaoException("O campo Motivo da Rejeição deve ser informado.");
        }
        solicitacaoItbiOnline.setSituacao(SituacaoSolicitacaoITBI.REJEITADA);
        salvar(solicitacaoItbiOnline);
        adicionarTramite(solicitacaoItbiOnline, sistemaFacade.getUsuarioCorrente().getNome(),
            SituacaoSolicitacaoITBI.REJEITADA, motivoRejeicao);
    }

    private void adicionarTramite(SolicitacaoItbiOnline solicitacaoItbiOnline,
                                  String usuario,
                                  SituacaoSolicitacaoITBI situacaoSolicitacaoITBI,
                                  String observacao) {
        adicionarTramite(solicitacaoItbiOnline, usuario, situacaoSolicitacaoITBI, observacao,
            null, null, null);
    }

    private void adicionarTramite(SolicitacaoItbiOnline solicitacaoItbiOnline,
                                  String usuario,
                                  SituacaoSolicitacaoITBI situacaoSolicitacaoITBI,
                                  String observacao,
                                  UsuarioSistema auditorFiscal) {
        adicionarTramite(solicitacaoItbiOnline, usuario, situacaoSolicitacaoITBI, observacao,
            auditorFiscal, null, null);
    }

    private void adicionarTramite(SolicitacaoItbiOnline solicitacaoItbiOnline,
                                  String usuario,
                                  SituacaoSolicitacaoITBI situacaoSolicitacaoITBI,
                                  String observacao,
                                  UsuarioSistema auditorFiscal,
                                  Arquivo arquivo,
                                  BigDecimal valorAvaliado) {
        TramiteSolicitacaoItbiOnline tramite = new TramiteSolicitacaoItbiOnline();
        tramite.setSolicitacaoItbiOnline(solicitacaoItbiOnline);
        tramite.setUsuario(usuario);
        tramite.setSituacaoSolicitacaoITBI(situacaoSolicitacaoITBI);
        tramite.setObservacao(observacao);
        tramite.setAuditorFiscal(auditorFiscal);
        tramite.setArquivo(arquivo);
        tramite.setValorAvaliado(valorAvaliado);
        tramiteSolicitacaoItbiOnlineFacade.salvar(tramite);
    }

    public void enviarEmailRejeicaoSolicitacao(SolicitacaoItbiOnline solicitacaoItbiOnline) {
        solicitacaoItbiOnline = recuperar(solicitacaoItbiOnline.getId());
        TramiteSolicitacaoItbiOnline tramite = tramiteSolicitacaoItbiOnlineFacade.buscarUltimoTramiteDaSolicitacao(solicitacaoItbiOnline);
        TemplateEmail templateEmail = templateEmailFacade.findByTipoTemplateEmail(TipoTemplateEmail.REJEICAO_SOLICITACAO_ITBI_ONLINE);
        if (templateEmail == null) {
            logger.error("Template de e-mail não configurado. {}", TipoTemplateEmail.REJEICAO_SOLICITACAO_ITBI_ONLINE);
            return;
        }
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        EmailService.getInstance().enviarEmail(solicitacaoItbiOnline.getUsuarioWeb().getEmail(),
            templateEmail.getAssunto(),
            new TrocaTagRejeicaoSolicitacaoItbiOnline(configuracaoTributario, solicitacaoItbiOnline, tramite.getObservacao())
                .trocarTags(templateEmail.getConteudo()));
    }

    public void designarSolicitacao(SolicitacaoItbiOnline solicitacaoItbiOnline,
                                    UsuarioSistema auditorFiscal) {
        if (auditorFiscal == null) {
            throw new ValidacaoException("O campo Auditor Fiscal deve ser informado.");
        }
        solicitacaoItbiOnline.setDataDesignacao(new Date());
        solicitacaoItbiOnline.setAuditorFiscal(auditorFiscal);
        solicitacaoItbiOnline.setSituacao(SituacaoSolicitacaoITBI.DESIGNADA);
        salvar(solicitacaoItbiOnline);

        adicionarTramite(solicitacaoItbiOnline, sistemaFacade.getUsuarioCorrente().getNome(),
            SituacaoSolicitacaoITBI.DESIGNADA, "", auditorFiscal);
    }

    private void validarAvaliacaoSolicitacao(Arquivo planilhaAvaliacao,
                                             BigDecimal valorAvaliado,
                                             String parecer) {
        ValidacaoException ve = new ValidacaoException();
        if (planilhaAvaliacao == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Planilha de Avaliação deve ser informado.");
        }
        if (valorAvaliado == null || valorAvaliado.compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor Avaliado deve ser informado.");
        }
        if (Strings.isNullOrEmpty(parecer)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Parecer deve ser informado.");
        }
        ve.lancarException();
    }

    public void salvarAvaliacaoSolicitacao(SolicitacaoItbiOnline solicitacaoItbiOnline,
                                           String usuario,
                                           Arquivo planilhaAvaliacao,
                                           BigDecimal valorAvaliado,
                                           String parecer,
                                           SituacaoSolicitacaoITBI situacaoSolicitacaoITBI) {
        planilhaAvaliacao = planilhaAvaliacao != null ? em.merge(planilhaAvaliacao) : null;

        if (situacaoSolicitacaoITBI.equals(SituacaoSolicitacaoITBI.INDEFERIDA)) {
            validarAvaliacaoSolicitacao(planilhaAvaliacao, valorAvaliado, parecer);
        }

        solicitacaoItbiOnline.setPlanilhaAvaliacao(planilhaAvaliacao);
        solicitacaoItbiOnline.setValorAvaliado(valorAvaliado);
        solicitacaoItbiOnline.setSituacao(situacaoSolicitacaoITBI);
        salvar(solicitacaoItbiOnline);

        adicionarTramite(solicitacaoItbiOnline, usuario,
            situacaoSolicitacaoITBI, parecer, null, planilhaAvaliacao, valorAvaliado);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public AssistenteBarraProgresso homologarSolicitacao(AssistenteBarraProgresso assistente,
                                                         String usuario) throws Exception {
        SolicitacaoItbiOnline solicitacaoItbiOnline = (SolicitacaoItbiOnline) assistente.getSelecionado();
        solicitacaoItbiOnline.setSituacao(SituacaoSolicitacaoITBI.HOMOLOGADA);
        solicitacaoItbiOnline = salvarRetornando(solicitacaoItbiOnline);
        adicionarTramite(solicitacaoItbiOnline, usuario, SituacaoSolicitacaoITBI.HOMOLOGADA, "",
            null, solicitacaoItbiOnline.getPlanilhaAvaliacao(), solicitacaoItbiOnline.getValorAvaliado());
        ProcessoCalculoITBI processoCalculoITBI = criarProcessoCalculoITBI(solicitacaoItbiOnline,
            assistente.getUsuarioSistema());
        processoCalculoITBI = calculoITBIFacade.salvarRetornando(processoCalculoITBI);
        criarLaudoJaAssinadoPeloChefeITBI(processoCalculoITBI);
        geraValorDividaITBI.gerarDebito(processoCalculoITBI, false);
        transferirProprietarios(assistente, processoCalculoITBI);
        return assistente;
    }

    private void transferirProprietarios(AssistenteBarraProgresso assistente, ProcessoCalculoITBI processoCalculoITBI) {
        boolean todosCalculosIsentos = true;
        for (CalculoITBI calculo : processoCalculoITBI.getCalculos()) {
            if (!calculo.getIsento()) {
                todosCalculosIsentos = false;
                break;
            }
        }
        if (todosCalculosIsentos) {
            calculoITBIFacade.transferirProprietariosCadastro(processoCalculoITBI, assistente);
        }
    }

    private void criarLaudoJaAssinadoPeloChefeITBI(ProcessoCalculoITBI processoCalculoITBI) {
        LaudoAvaliacaoITBI laudoAvaliacaoITBI = calculoITBIFacade.recuperarLaudo(processoCalculoITBI);
        if (laudoAvaliacaoITBI == null) {
            ParametrosITBI parametroItbi = parametroITBIFacade.getParametroVigente(processoCalculoITBI.getExercicio(), processoCalculoITBI.getTipoITBI());
            laudoAvaliacaoITBI = new LaudoAvaliacaoITBI();
            laudoAvaliacaoITBI.setProcessoCalculoITBI(processoCalculoITBI);
            laudoAvaliacaoITBI.setDiretorChefeDeparTributo(parametroItbi.getFuncionarioPorFuncao(TipoFuncaoParametrosITBI.DIRETOR_CHEFE_DEPARTAMENTO_TRIBUTO));
            calculoITBIFacade.salvarLaudoAvaliacao(laudoAvaliacaoITBI);
        }
    }

    private ProcessoCalculoITBI criarProcessoCalculoITBI(SolicitacaoItbiOnline solicitacaoItbiOnline,
                                                         UsuarioSistema usuarioSistema) {
        ParametrosITBI parametro = calculoITBIFacade.recuperarParametroITBIVigente(solicitacaoItbiOnline.getExercicio(),
            solicitacaoItbiOnline.getTipoITBI());

        ProcessoCalculoITBI processoCalculoITBI = new ProcessoCalculoITBI();
        processoCalculoITBI.setExercicio(solicitacaoItbiOnline.getExercicio());
        processoCalculoITBI.setCompleto(Boolean.TRUE);
        processoCalculoITBI.setDivida(parametro.getDivida());
        processoCalculoITBI.setDataLancamento(new Date());
        processoCalculoITBI.setDescricao("ITBI / " + processoCalculoITBI.getExercicio().getAno());
        processoCalculoITBI.setUsuarioSistema(usuarioSistema);
        processoCalculoITBI.setCodigo(calculoITBIFacade.buscarSequeciaCodigoITBI(processoCalculoITBI));
        processoCalculoITBI.setIsentoImune(solicitacaoItbiOnline.getCalculos().get(0).getTipoIsencaoITBI() != null);
        processoCalculoITBI.setTipoITBI(solicitacaoItbiOnline.getTipoITBI());
        processoCalculoITBI.setSituacao(SituacaoITBI.PROCESSADO);
        processoCalculoITBI.setAuditorFiscal(solicitacaoItbiOnline.getAuditorFiscal().getPessoaFisica());
        processoCalculoITBI.setCadastroImobiliario(solicitacaoItbiOnline.getCadastroImobiliario());
        processoCalculoITBI.setCadastroRural(solicitacaoItbiOnline.getCadastroRural());
        processoCalculoITBI.setCodigoVerificacao(calculoITBIFacade.gerarAssinaturaDigital(processoCalculoITBI));
        processoCalculoITBI.setSolicitacaoItbiOnline(solicitacaoItbiOnline);
        criarCalculoITBI(solicitacaoItbiOnline, processoCalculoITBI);
        return processoCalculoITBI;
    }

    private void criarCalculoITBI(SolicitacaoItbiOnline solicitacaoItbiOnline, ProcessoCalculoITBI processoCalculoITBI) {
        CalculoSolicitacaoItbiOnline calculoSolicitacaoItbiOnline = solicitacaoItbiOnline.getCalculos().get(0);
        CalculoITBI calculoITBI = new CalculoITBI();
        calculoITBI.setProcessoCalculoITBI(processoCalculoITBI);
        calculoITBI.setValorReal(calculoSolicitacaoItbiOnline.getValorReal());
        calculoITBI.setValorEfetivo(calculoSolicitacaoItbiOnline.getValorCalculado());
        calculoITBI.setCadastro(solicitacaoItbiOnline.getCadastro());
        calculoITBI.setSequencia(1);
        calculoITBI.setExercicio(processoCalculoITBI.getExercicio());
        calculoITBI.setDataLancamento(processoCalculoITBI.getDataLancamento());
        calculoITBI.setTipoItbi(processoCalculoITBI.getTipoITBI());
        calculoITBI.setCadastroImobiliario(calculoSolicitacaoItbiOnline.getSolicitacaoItbiOnline().getCadastroImobiliario());
        calculoITBI.setCadastroRural(calculoSolicitacaoItbiOnline.getSolicitacaoItbiOnline().getCadastroRural());
        calculoITBI.setTipoBaseCalculoITBI(calculoSolicitacaoItbiOnline.getTipoBaseCalculoITBI());
        calculoITBI.setValorReajuste(calculoSolicitacaoItbiOnline.getValorReajuste());
        calculoITBI.setValorVenal(calculoSolicitacaoItbiOnline.getValorVenal());
        calculoITBI.setSituacao(SituacaoITBI.PROCESSADO);
        calculoITBI.setBaseCalculo(calculoSolicitacaoItbiOnline.getBaseCalculo());
        calculoITBI.setTotalBaseCalculo(calculoSolicitacaoItbiOnline.getTotalBaseCalculo());
        calculoITBI.setPercentual(calculoSolicitacaoItbiOnline.getPercentual());
        calculoITBI.setOrdemCalculo(calculoSolicitacaoItbiOnline.getOrdemCalculo());
        calculoITBI.setIsento(calculoSolicitacaoItbiOnline.getTipoIsencaoITBI() != null);
        if (calculoITBI.getIsento()) calculoITBI.setTipoIsencaoITBI(calculoSolicitacaoItbiOnline.getTipoIsencaoITBI());
        calculoITBI.setNumeroParcelas(calculoSolicitacaoItbiOnline.getQuantidadeParcelas());
        criarTransmitentesCalculoITBI(calculoSolicitacaoItbiOnline, calculoITBI);
        criarAdquirentesCalculoITBI(calculoSolicitacaoItbiOnline, calculoITBI);
        criarItensCalculoITBI(calculoSolicitacaoItbiOnline, calculoITBI);
        criarCalculoPessoa(calculoSolicitacaoItbiOnline, calculoITBI);
        processoCalculoITBI.getCalculos().add(calculoITBI);
        novosProprietariosSimulacao(calculoITBI, processoCalculoITBI);
    }

    private void novosProprietariosSimulacao(CalculoITBI calculoITBI, ProcessoCalculoITBI processoCalculoITBI) {
        if (calculoITBI.getProprietariosSimulacao() != null) {
            calculoITBI.getProprietariosSimulacao().clear();
        } else {
            calculoITBI.setProprietariosSimulacao(Lists.newArrayList());
        }
        if (processoCalculoITBI.getTipoITBI().equals(TipoITBI.IMOBILIARIO)) {
            for (Propriedade novoProprietario : calculoITBIFacade.simulacaoDasProporcoesImobiliario(processoCalculoITBI.getCadastroImobiliario(), processoCalculoITBI.getCalculos()).get((calculoITBI.getOrdemCalculo()))) {
                PropriedadeSimulacaoITBI propriedadeSimulacao = new PropriedadeSimulacaoITBI();
                propriedadeSimulacao.setPessoa(novoProprietario.getPessoa());
                propriedadeSimulacao.setCalculoITBI(calculoITBI);
                propriedadeSimulacao.setProporcao(novoProprietario.getProporcao());
                calculoITBI.getProprietariosSimulacao().add(propriedadeSimulacao);
            }
        } else {
            for (PropriedadeRural novoProprietario : calculoITBIFacade.simulacaoDasProporcoesRural(processoCalculoITBI.getCadastroRural(), processoCalculoITBI.getCalculos()).get(calculoITBI.getOrdemCalculo())) {
                PropriedadeSimulacaoITBI propriedadeSimulacao = new PropriedadeSimulacaoITBI();
                propriedadeSimulacao.setPessoa(novoProprietario.getPessoa());
                propriedadeSimulacao.setCalculoITBI(calculoITBI);
                propriedadeSimulacao.setProporcao(novoProprietario.getProporcao());
                calculoITBI.getProprietariosSimulacao().add(propriedadeSimulacao);
            }
        }
    }

    private static void criarTransmitentesCalculoITBI(CalculoSolicitacaoItbiOnline calculoSolicitacaoItbiOnline, CalculoITBI calculoITBI) {
        for (TransmitentesSolicitacaoItbiOnline transmitentesSolicitacaoItbiOnline : calculoSolicitacaoItbiOnline.getTransmitentesCalculo()) {
            TransmitentesCalculoITBI transmitentesCalculoITBI = new TransmitentesCalculoITBI();
            transmitentesCalculoITBI.setCalculoITBI(calculoITBI);
            transmitentesCalculoITBI.setPessoa(transmitentesSolicitacaoItbiOnline.getTransmitente());
            transmitentesCalculoITBI.setPercentual(transmitentesSolicitacaoItbiOnline.getPercentual());
            calculoITBI.getTransmitentesCalculo().add(transmitentesCalculoITBI);
        }
    }

    private static void criarAdquirentesCalculoITBI(CalculoSolicitacaoItbiOnline calculoSolicitacaoItbiOnline, CalculoITBI calculoITBI) {
        for (AdquirentesSolicitacaoITBIOnline adquirentesSolicitacaoITBIOnline : calculoSolicitacaoItbiOnline.getAdquirentesCalculo()) {
            AdquirentesCalculoITBI adquirentesCalculoITBI = new AdquirentesCalculoITBI();
            adquirentesCalculoITBI.setCalculoITBI(calculoITBI);
            adquirentesCalculoITBI.setAdquirente(adquirentesSolicitacaoITBIOnline.getAdquirente());
            adquirentesCalculoITBI.setPercentual(adquirentesSolicitacaoITBIOnline.getPercentual());
            calculoITBI.getAdquirentesCalculo().add(adquirentesCalculoITBI);
        }
    }

    private static void criarItensCalculoITBI(CalculoSolicitacaoItbiOnline calculoSolicitacaoItbiOnline, CalculoITBI calculoITBI) {
        for (ItemCalculoITBIOnline itemCalculoITBIOnline : calculoSolicitacaoItbiOnline.getItensCalculo()) {
            ItemCalculoITBI itemCalculoITBI = new ItemCalculoITBI();
            itemCalculoITBI.setCalculoITBI(calculoITBI);
            itemCalculoITBI.setTipoNegociacao(itemCalculoITBIOnline.getTipoNegociacao());
            itemCalculoITBI.setPercentual(itemCalculoITBIOnline.getPercentual());
            itemCalculoITBI.setValorInformado(itemCalculoITBIOnline.getValorInformado());
            itemCalculoITBI.setValorCalculado(itemCalculoITBIOnline.getValorCalculado());
            calculoITBI.getItensCalculo().add(itemCalculoITBI);
        }
    }

    private static void criarCalculoPessoa(CalculoSolicitacaoItbiOnline calculoSolicitacaoItbiOnline, CalculoITBI calculoITBI) {
        for (AdquirentesSolicitacaoITBIOnline adquirente : calculoSolicitacaoItbiOnline.getAdquirentesCalculo()) {
            CalculoPessoa calculoPessoa = new CalculoPessoa();
            calculoPessoa.setCalculo(calculoITBI);
            calculoPessoa.setPessoa(adquirente.getAdquirente());
            calculoITBI.getPessoas().add(calculoPessoa);
        }
    }

    public ProcessoCalculoITBI buscarProcessoCalculoITBIDaSolicitacao(SolicitacaoItbiOnline solicitacaoItbiOnline) {
        List<ProcessoCalculoITBI> processos = em.createQuery("from ProcessoCalculoITBI p where p.solicitacaoItbiOnline = :solicitacaoItbiOnline ")
            .setParameter("solicitacaoItbiOnline", solicitacaoItbiOnline)
            .getResultList();
        if (!processos.isEmpty()) {
            processos.sort(Comparator.comparing(ProcessoCalculoITBI::getId).reversed());
            ProcessoCalculoITBI processoCalculoITBI = processos.get(0);
            Hibernate.initialize(processoCalculoITBI.getCalculos());
            return processoCalculoITBI;
        }
        return null;
    }

    public void enviarEmailAvaliacaoSolicitacao(SolicitacaoItbiOnline solicitacaoItbiOnline) {
        solicitacaoItbiOnline = recuperar(solicitacaoItbiOnline.getId());
        TramiteSolicitacaoItbiOnline tramite = tramiteSolicitacaoItbiOnlineFacade.buscarUltimoTramiteDaSolicitacao(solicitacaoItbiOnline);
        TemplateEmail templateEmail = templateEmailFacade.findByTipoTemplateEmail(TipoTemplateEmail.AVALIACAO_FISCAL_SOLICITACAO_ITBI_ONLINE);
        if (templateEmail == null) {
            logger.error("Template de e-mail não configurado. {}", TipoTemplateEmail.AVALIACAO_FISCAL_SOLICITACAO_ITBI_ONLINE);
            return;
        }
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        EmailService.getInstance().enviarEmail(solicitacaoItbiOnline.getUsuarioWeb().getEmail(),
            templateEmail.getAssunto(),
            new TrocaTagAvaliacaoFiscalSolicitacaoItbiOnline(configuracaoTributario, solicitacaoItbiOnline, tramite.getObservacao())
                .trocarTags(templateEmail.getConteudo()));
    }

    public byte[] imprimirLaudoItbi(Long idSolicitacao) {
        SolicitacaoItbiOnline solicitacao = recuperar(idSolicitacao);
        ProcessoCalculoITBI processoCalculoITBI = buscarProcessoCalculoITBIDaSolicitacao(solicitacao);
        if (processoCalculoITBI == null) {
            throw new ValidacaoException("Não é possivel emitir o Laudo no momento.");
        }
        LaudoAvaliacaoITBI laudo = calculoITBIFacade.recuperarLaudo(processoCalculoITBI);
        if (laudo == null) {
            calculoITBIFacade.gerarLaudo(processoCalculoITBI);
        }
        return calculoITBIFacade.imprimirLaudo(processoCalculoITBI.getId(), Lists.newArrayList()).toByteArray();
    }

}
