package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCalculoRBTRans;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.enums.TipoTermoRBTrans;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.ParametrosTransitoTermosException;
import br.com.webpublico.exception.TipoDoctoOficialRBTransException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.negocios.tributario.arrecadacao.CalculoExecutorDepoisDePagar;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;

import static java.util.Calendar.*;

@Stateless
public class PermissaoTransporteFacade extends CalculoExecutorDepoisDePagar<PermissaoTransporte> {

    private static final Logger logger = LoggerFactory.getLogger(PermissaoTransporteFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ParametrosTransitoTransporteFacade parametrosTransitoTransporteFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private CalculoRBTransFacade calculoRBTransFacade;
    @EJB
    private CredencialRBTransFacade credencialRBTransFacade;
    @EJB
    private PontoTaxiFacade pontoTaxiFacade;
    @EJB
    private EnderecoCorreioFacade enderecoCorreioFacade;
    @EJB
    private DocumentoPessoalFacade documentoPessoalFacade;
    @EJB
    private VeiculoTransporteFacade veiculoTransporteFacade;
    @EJB
    private MarcaFacade marcaFacade;
    @EJB
    private TipoVeiculoFacade tipoVeiculoFacade;
    @EJB
    private CorFacade corFacade;
    @EJB
    private ModeloFacade modeloFacade;
    @EJB
    private CombustivelFacade combustivelFacade;
    @EJB
    private MoedaFacade moedaFacade;

    public PermissaoTransporteFacade() {
        super(PermissaoTransporte.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExercicioFacade getExercicioFacade() {
        return parametrosTransitoTransporteFacade.getExercicioFacade();
    }

    public SistemaFacade getSistemaFacade() {
        return credencialRBTransFacade.getSistemaFacade();
    }

    public ArquivoFacade getArquivoFacade() {
        return cadastroEconomicoFacade.getArquivoFacade();
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return cadastroEconomicoFacade.getConfiguracaoTributarioFacade();
    }

    public ParametrosTransitoTransporteFacade getParametrosTransitoTransporteFacade() {
        return parametrosTransitoTransporteFacade;
    }

    public PontoTaxiFacade getPontoTaxiFacade() {
        return pontoTaxiFacade;
    }

    public CredencialRBTransFacade getCredencialRBTransFacade() {
        return credencialRBTransFacade;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return getFiscalizacaoRBTransFacade().getPessoaFisicaFacade();
    }

    public PessoaFacade getPessoaFacade() {
        return getFiscalizacaoRBTransFacade().getPessoaFacade();
    }

    public EnderecoCorreioFacade getEnderecoCorreioFacade() {
        return enderecoCorreioFacade;
    }

    public DocumentoPessoalFacade getDocumentoPessoalFacade() {
        return documentoPessoalFacade;
    }

    public CalculoRBTransFacade getCalculoRBTransFacade() {
        return calculoRBTransFacade;
    }

    public FiscalizacaoRBTransFacade getFiscalizacaoRBTransFacade() {
        return documentoOficialFacade.getFiscalizacaoRBTransFacade();
    }

    public DAMFacade getDamFacade() {
        return getFiscalizacaoRBTransFacade().getDamFacade();
    }

    public CidadeFacade getCidadeFacade() {
        return getFiscalizacaoRBTransFacade().getCidadeFacade();
    }

    public VeiculoTransporteFacade getVeiculoTransporteFacade() {
        return veiculoTransporteFacade;
    }

    public MarcaFacade getMarcaFacade() {
        return marcaFacade;
    }

    public TipoVeiculoFacade getTipoVeiculoFacade() {
        return tipoVeiculoFacade;
    }

    public CorFacade getCorFacade() {
        return corFacade;
    }

    public ModeloFacade getModeloFacade() {
        return modeloFacade;
    }

    public CombustivelFacade getCombustivelFacade() {
        return combustivelFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return getPessoaFacade().getUsuarioSistemaFacade();
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }

    //-----------------------------------------------------CONSULTAS--------------------------------------------------------
    public VeiculoPermissionario retornaUltimoVeiculoBaixado(PermissaoTransporte pt) {
        String sql = "select vp.* " +
            " from veiculopermissionario vp " +
            " where vp.id in ( " +
            " select max(veicperm.id) from veiculopermissionario veicperm " +
            " inner join baixaveiculoperm baixa on baixa.veiculopermissionario_id = veicperm.id " +
            " where veicperm.permissaotransporte_id = :idPermissao )";

        Query q = em.createNativeQuery(sql, VeiculoPermissionario.class);
        q.setParameter("idPermissao", pt.getId());
        List<VeiculoPermissionario> veiculos = q.getResultList();
        if (!veiculos.isEmpty()) return veiculos.get(0);

        return null;
    }

    public VeiculoPermissionario retornaUltimoVeiculo(boolean vigente, PermissaoTransporte pt) {
        String sql = " select vp.* " +
            " from veiculopermissionario vp " +
            " where vp.id in (select max(veicperm.id) from veiculopermissionario veicperm " +
            " where veicperm.permissaotransporte_id = :idPermissao ";
        if (vigente) {
            sql += " and (veicperm.finalvigencia is null or trunc(veicperm.finalvigencia) > trunc(sysdate))";
        }
        sql += ")";

        Query q = em.createNativeQuery(sql, VeiculoPermissionario.class);
        q.setParameter("idPermissao", pt.getId());
        List<VeiculoPermissionario> veiculos = q.getResultList();
        if (!veiculos.isEmpty()) return veiculos.get(0);
        return null;
    }

    public Integer sugereNumeroPermissaoPorTipo(TipoPermissaoRBTrans tipo) {
        String sql = "select " +
            "coalesce(max(pt.numero),0) + 1 as numero " +
            "from permissaotransporte pt where pt.tipopermissaorbtrans = :tipo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipo", tipo.name());
        return ((BigDecimal) q.getSingleResult()).intValue();
    }

    public ValorDivida recuperaValorDividaDoCalculo(Calculo calculo) {
        try {
            String sql = "select vd.*"
                + "     from valordivida vd"
                + "    where vd.calculo_id = :calculo_id";

            Query q = em.createNativeQuery(sql, ValorDivida.class);
            q.setParameter(
                "calculo_id", calculo.getId());
            try {
                return recarregarValorDivida((ValorDivida) q.getSingleResult());
            } catch (NoResultException nre) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private PermissaoTransporte recuperaPermissaoAtivaDeUmTipoPeloNumero(Integer numero, TipoPermissaoRBTrans tipo) {
        String hql = "        from PermissaoTransporte pt"
            + "           where pt.numero = :numero"
            + "           and pt.tipoPermissaoRBTrans = :tipo"
            + "           and pt.finalVigencia is null";

        Query q = em.createQuery(hql);
        q.setParameter("numero", numero);
        q.setParameter("tipo", tipo);
        q.setMaxResults(1);
        List<PermissaoTransporte> result = q.getResultList();
        if (!result.isEmpty()) return result.get(0);
        return null;
    }

    public List<PermissaoTransporte> recuperaPermissoesVigentesDoResponsavelDoCMC(Pessoa pessoa) {
        String sb = " select pt from PermissaoTransporte pt " +
            " inner join fetch  pt.permissionarios permissionario " +
            " inner join permissionario.cadastroEconomico ce " +
            " inner join ce.pessoa p " +
            " where pt.finalVigencia is null " +
            " and permissionario.finalVigencia is null " +
            " and p = :pessoa ";

        Query q = em.createQuery(sb, PermissaoTransporte.class);
        q.setParameter("pessoa", pessoa);
        return q.getResultList();
    }

    public List<PermissaoTransporte> recuperaPermissoesAtivasDoPermissionario(CadastroEconomico ce) {
        String sql = " select distinct pt.id from permissaotransporte pt " +
            " inner join permissionario perm on perm.permissaotransporte_id = pt.id " +
            " inner join cadastroeconomico ce on ce.id = perm.cadastroeconomico_id " +
            " where coalesce(pt.finalvigencia, :dataAtual) >= :dataAtual " +
            " and perm.finalvigencia is null " +
            " and ce.id = :cadastroEconomico";
        Query q = em.createNativeQuery(sql);
        q.setParameter("cadastroEconomico", ce.getId());
        q.setParameter("dataAtual", DataUtil.dataSemHorario(getSistemaFacade().getDataOperacao()));
        List<BigDecimal> result = q.getResultList();
        List<PermissaoTransporte> retorno = Lists.newArrayList();
        for (BigDecimal idPermissao : result) {
            retorno.add(recuperar(idPermissao.longValue()));
        }
        return retorno;
    }

    public PermissaoTransporte recuperaPermissaoPorCMC(CadastroEconomico ce) {
        String sql = "select pt.id from permissaotransporte pt " +
            " inner join permissionario perm on perm.permissaotransporte_id = pt.id " +
            " inner join cadastroeconomico ce on ce.id = perm.cadastroeconomico_id " +
            " where perm.finalvigencia is null " +
            " and ce.id = :cadastroEconomico";
        Query q = em.createNativeQuery(sql);
        q.setParameter("cadastroEconomico", ce.getId());
        List<BigDecimal> result = q.getResultList();
        if (!result.isEmpty()) return recuperar(result.get(0).longValue());
        return null;
    }

    public List<ResultadoParcela> recuperarDebitoVistoriaAndRenovacao(CadastroEconomico cadastro) {
        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, cadastro.getId());
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        consulta.addComplementoJoin(" inner join calculorbtrans calculorb on calculorb.id = VW.CALCULO_ID");
        consulta.addComplementoDoWhere(" and (CALCULORB.TIPOCALCULORBTRANS IN ('VISTORIA_VEICULO', 'RENOVACAO_PERMISSAO'))");
        consulta.executaConsulta();
        return consulta.getResultados();
    }

    public List<ResultadoParcela> recuperaDebitosVencidosCMC(CadastroEconomico cadastro) {
        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, cadastro.getId());
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR, new Date());
        consulta.addOrdem(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Ordem.TipoOrdem.ASC);
        List<Divida> dividas = parametrosTransitoTransporteFacade.listaTodasDividasInformadasNosParametros();
        List<Long> idDividas = Lists.newArrayList();
        ConfiguracaoTributario configuracaoTributario = getConfiguracaoTributarioFacade().retornaUltimo();
        if (configuracaoTributario != null) {
            if (configuracaoTributario.getDividaISSFixo() != null) {
                dividas.add(configuracaoTributario.getDividaISSFixo());
            }
        }

        for (Divida divida : dividas) {
            if (!idDividas.contains(divida.getId())) {
                idDividas.add(divida.getId());
            }
        }

        consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IN, idDividas);
        consulta.executaConsulta();
        return consulta.getResultados();
    }

    public List<VoDebitosRBTrans> recuperaDebitosDeRBTransPorCMC(CadastroEconomico cadastro) {
        return recuperaDebitosDeRBTransPorCMC(cadastro, Boolean.FALSE);
    }

    public List<VoDebitosRBTrans> recuperaDebitosVencidosDeRBTransPorCMC(CadastroEconomico cadastro) {
        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, cadastro.getId());
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR, DataUtil.dataSemHorario(getSistemaFacade().getDataOperacao()));

        List<Divida> dividas = parametrosTransitoTransporteFacade.listaTodasDividasInformadasNosParametros();

        ConfiguracaoTributario configuracaoTributario = getConfiguracaoTributarioFacade().retornaUltimo();
        if (configuracaoTributario != null) {
            if (configuracaoTributario.getDividaISSFixo() != null) {
                dividas.add(configuracaoTributario.getDividaISSFixo());
            }
        }
        List<Long> idDividas = Lists.newArrayList();
        for (Divida divida : dividas) {
            if (!idDividas.contains(divida.getId())) {
                idDividas.add(divida.getId());
            }
        }
        consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IN, idDividas);
        consulta.executaConsulta();
        List<VoDebitosRBTrans> debitos = new ArrayList<>();
        for (ResultadoParcela resultado : consulta.getResultados()) {
            VoDebitosRBTrans debito = new VoDebitosRBTrans();
            debito.setResultadoParcela(resultado);
            VOCalculoRBTrans voCalculoRBTrans = buscarInformacoesDoCalculo(resultado.getIdCalculo());
            debito.setDescricao(montarDescricaoDebito(voCalculoRBTrans));
            debitos.add(debito);
        }
        return debitos;
    }

    public List<VoDebitosRBTrans> recuperaDebitosDeRBTransPorCMC(CadastroEconomico cadastro, Boolean antesDeHoje) {
        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, cadastro.getId());
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        consulta.addParameter(ConsultaParcela.Campo.TIPO_CALCULO, ConsultaParcela.Operador.IN, Lists.newArrayList(Calculo.TipoCalculo.ISS, Calculo.TipoCalculo.RB_TRANS));
        if (antesDeHoje) {
            consulta.addParameter(ConsultaParcela.Campo.VALOR_DIVIDA_EMISSAO, ConsultaParcela.Operador.MENOR, DataUtil.dataSemHorario(getSistemaFacade().getDataOperacao()));
        }
        List<ResultadoParcela> parcelas = consulta.executaConsulta().getResultados();
        List<VoDebitosRBTrans> debitos = Lists.newArrayList();

        for (ResultadoParcela resultado : parcelas) {
            VoDebitosRBTrans debito = new VoDebitosRBTrans();
            debito.setResultadoParcela(resultado);
            VOCalculoRBTrans voCalculoRBTrans = buscarInformacoesDoCalculo(resultado.getIdCalculo());
            debito.setDescricao(montarDescricaoDebito(voCalculoRBTrans));
            debitos.add(debito);
        }
        return debitos;
    }

    public VOCalculoRBTrans buscarInformacoesDoCalculo(Long idCalculo) {
        String sql = " select c.id as idcalculo, ciss.id as idiss, crbt.tipocalculorbtrans from calculo c " +
            " left join calculorbtrans crbt on c.id = crbt.id" +
            " left join calculoiss ciss on c.id = ciss.id " +
            " where c.id = :idCalculo ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCalculo", idCalculo);

        List<Object[]> calculos = q.getResultList();

        if (calculos != null && !calculos.isEmpty()) {
            Object[] obj = calculos.get(0);

            VOCalculoRBTrans voCalculoRBTrans = new VOCalculoRBTrans();
            voCalculoRBTrans.setIdCalculo(obj[0] != null ? ((BigDecimal) obj[0]).longValue() : null);
            voCalculoRBTrans.setIdCalculoISS(obj[1] != null ? ((BigDecimal) obj[1]).longValue() : null);
            if (obj[2] != null) {
                voCalculoRBTrans.setTipoCalculoRBTRans(TipoCalculoRBTRans.valueOf((String) obj[2]));
            }

            return voCalculoRBTrans;
        }
        return null;
    }

    public String montarDescricaoDebito(VOCalculoRBTrans voCalculoRBTrans) {
        String descricao = "";

        if (voCalculoRBTrans.getTipoCalculoRBTRans() != null) {
            switch (voCalculoRBTrans.getTipoCalculoRBTRans()) {
                case INSERCAO_MOTORISTA_AUXILIAR:
                    descricao = "Inserção de Motorista Auxiliar";
                    descricao += montarDescricaoMotorista(voCalculoRBTrans.getIdCalculo(), false);
                    break;
                case BAIXA_MOTORISTA_AUXILIAR:
                    descricao = "Baixa de Motorista Auxiliar";
                    descricao += montarDescricaoMotorista(voCalculoRBTrans.getIdCalculo(), true);
                    break;
                case INSERCAO_VEICULO:
                    descricao = "Inserção de Veículo";
                    descricao += montarDescricaoVeiculo(voCalculoRBTrans.getIdCalculo(), false);
                    break;
                case BAIXA_VEICULO:
                    descricao = "Baixa de Veículo";
                    descricao += montarDescricaoVeiculo(voCalculoRBTrans.getIdCalculo(), true);
                    break;
                case CADASTRO_AUTONOMO:
                    descricao = "Cadastro de Autônomo";
                    descricao += montarDescricaoCadastroAutonomo(voCalculoRBTrans.getIdCalculo());
                    break;
                case VISTORIA_VEICULO:
                    descricao = "Vistoria de Veículo";
                    descricao += montarDescricaoVistoria(voCalculoRBTrans.getIdCalculo());
                    break;
                case OUTORGA:
                    descricao = "Outorga de Moto-Táxi";
                    descricao += montarDescricaoOutorga(voCalculoRBTrans.getIdCalculo());
                    break;
                case TRANSFERENCIA_PERMISSAO:
                    descricao = "Transferência de Permissão";
                    break;
                case AUTORIZACAO_FUNCIONAMENTO:
                    descricao = "Autorização para Funcionamento";
                    break;
                case REQUERIMENTO:
                    descricao = "Requerimento";
                    break;
                case RENOVACAO_PERMISSAO:
                    descricao = "Renovação de Permissão";
                    break;
            }
        } else if (voCalculoRBTrans.getIdCalculoISS() != null) {
            descricao = "ISS FIXO";
        }
        return descricao;
    }

    private String montarDescricaoMotorista(Long idCalculo, boolean isBaixa) {
        String descricao = "";
        VOMotoristaAuxiliar motoristaAuxiliar = buscarInformacoesMotoristaAuxiliar(idCalculo, isBaixa);
        if (motoristaAuxiliar != null) {
            descricao += " - " + motoristaAuxiliar.getInscricaoCadastral() + " " + motoristaAuxiliar.getNomePessoa();
        }
        return descricao;
    }

    private String montarDescricaoVeiculo(Long idCalculo, boolean isBaixa) {
        String descricao = "";
        VOVeiculo veiculo = buscarInformacoesVeiculo(idCalculo, isBaixa);
        if (veiculo != null) {
            descricao += " - " + veiculo.getMarca()
                + "/" + veiculo.getModelo();
            if (veiculo.getPlaca() != null) {
                descricao += " Placa: " + veiculo.getPlaca();
            } else {
                descricao += " Placa: não Informada";
            }
        }
        return descricao;
    }

    private String montarDescricaoCadastroAutonomo(Long idCalculo) {
        String descricao = "";
        VOMotoristaAuxiliar permissionario = buscarInformacoesCadastroAutonomo(idCalculo);
        if (permissionario != null) {
            descricao += " - " + permissionario.getInscricaoCadastral() + " " + permissionario.getNomePessoa();
        }
        return descricao;
    }

    private String montarDescricaoVistoria(Long idCalculo) {
        String descricao = "";
        VOVeiculo vistoria = buscarInformacoesVistoriaVeiculo(idCalculo);
        if (vistoria != null) {
            descricao += " - " + vistoria.getMarca()
                + "/" + vistoria.getModelo();
            if (vistoria.getPlaca() != null) {
                descricao += " Placa: " + vistoria.getPlaca();
            } else {
                descricao += " Placa: não Informada";
            }
        }
        return descricao;
    }

    private String montarDescricaoOutorga(Long idCalculo) {
        String descricao = "";
        VOMotoristaAuxiliar outorga = buscarInformacoesOutorga(idCalculo);
        if (outorga != null) {
            descricao += " - " + outorga.getInscricaoCadastral() + " " + outorga.getNomePessoa();
        }
        return descricao;
    }

    public VOMotoristaAuxiliar buscarInformacoesMotoristaAuxiliar(Long idCalculo, boolean isBaixa) {
        String sql = isBaixa ? montarSqlBaixaMotorista() : montarSqlMotoristaAuxiliar();
        return montarVOMotorista(idCalculo, sql);
    }

    public VOVeiculo buscarInformacoesVeiculo(Long idCalculo, boolean isBaixa) {
        String sql = isBaixa ? montarSqlBaixaVeiculo() : montarSqlInsercaoVeiculo();
        return montarVOVeiculo(idCalculo, sql);
    }

    private VOMotoristaAuxiliar buscarInformacoesCadastroAutonomo(Long idCalculo) {
        String sql = " select cmc.inscricaocadastral, coalesce(pf.nome, pj.razaosocial) " +
            " from calculorbtrans calc" +
            " inner join calculo c on calc.id = c.id" +
            " inner join cadastroeconomico cmc on c.cadastro_id = cmc.id" +
            " inner join pessoa pes on cmc.pessoa_id = pes.id" +
            " left join pessoafisica pf on pes.id = pf.id" +
            " left join pessoajuridica pj on pes.id = pj.id" +
            " where calc.id = :idCalculo ";

        return montarVOMotorista(idCalculo, sql);
    }

    private VOVeiculo buscarInformacoesVistoriaVeiculo(Long idCalculo) {
        String sql = " select marca.descricao as marca, modelo.descricao as descricao, vt.placa " +
            " from vistoriaveiculo vv " +
            " inner join veiculopermissionario vp on vv.veiculopermissionario_id = vp.id " +
            " inner join veiculotransporte vt on vp.veiculotransporte_id = vt.id " +
            " inner join modelo on vt.modelo_id = modelo.id " +
            " inner join marca on modelo.marca_id = marca.id " +
            " where vv.calculorbtrans_id = :idCalculo ";

        return montarVOVeiculo(idCalculo, sql);
    }

    private VOMotoristaAuxiliar buscarInformacoesOutorga(Long idCalculo) {
        String sql = " select cmc.inscricaocadastral, coalesce(pf.nome, pj.razaosocial) " +
            " from calculopermissao cp " +
            " inner join permissaotransporte pt on cp.permissaotransporte_id = pt.id " +
            " inner join permissionario p on pt.id = p.permissaotransporte_id " +
            " inner join cadastroeconomico cmc on p.cadastroeconomico_id = cmc.id " +
            " inner join pessoa pes on cmc.pessoa_id = pes.id " +
            " left join pessoafisica pf on pes.id = pf.id " +
            " left join pessoajuridica pj on pes.id = pj.id " +
            " where cp.calculorbtrans_id = :idCalculo " +
            " and p.finalvigencia is null ";

        return montarVOMotorista(idCalculo, sql);
    }

    private VOMotoristaAuxiliar montarVOMotorista(Long idCalculo, String sql) {
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCalculo", idCalculo);
        List<Object[]> motoristas = q.getResultList();
        if (motoristas != null && !motoristas.isEmpty()) {
            Object[] obj = motoristas.get(0);
            VOMotoristaAuxiliar voPermissionario = new VOMotoristaAuxiliar();
            voPermissionario.setInscricaoCadastral(obj[0] != null ? (String) obj[0] : null);
            voPermissionario.setNomePessoa(obj[1] != null ? (String) obj[1] : null);
            return voPermissionario;
        }
        return null;
    }

    private VOVeiculo montarVOVeiculo(Long idCalculo, String sql) {
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCalculo", idCalculo);
        List<Object[]> insercoes = q.getResultList();
        if (insercoes != null && !insercoes.isEmpty()) {
            Object[] obj = insercoes.get(0);
            VOVeiculo voVeiculo = new VOVeiculo();
            voVeiculo.setMarca(obj[0] != null ? (String) obj[0] : null);
            voVeiculo.setModelo(obj[1] != null ? (String) obj[1] : null);
            voVeiculo.setPlaca(obj[2] != null ? (String) obj[2] : null);
            return voVeiculo;
        }
        return null;
    }

    private String montarSqlMotoristaAuxiliar() {
        return " select cmc.inscricaocadastral, coalesce(pf.nome, pj.razaosocial) " +
            " from motoristaauxiliar ma " +
            " inner join cadastroeconomico cmc on ma.cadastroeconomico_id = cmc.id " +
            " inner join pessoa pes on cmc.pessoa_id = pes.id " +
            " left join pessoafisica pf on pes.id = pf.id " +
            " left join pessoajuridica pj on pes.id = pj.id " +
            " where ma.calculorbtrans_id = :idCalculo ";
    }

    private String montarSqlBaixaMotorista() {
        return " select cmc.inscricaocadastral, coalesce(pf.nome, pj.razaosocial) " +
            " from baixamotoristaauxiliar baixa " +
            " inner join motoristaauxiliar ma on baixa.motoristaauxiliar_id = ma.id " +
            " inner join cadastroeconomico cmc on ma.cadastroeconomico_id = cmc.id " +
            " inner join pessoa pes on cmc.pessoa_id = pes.id " +
            " left join pessoafisica pf on pes.id = pf.id " +
            " left join pessoajuridica pj on pes.id = pj.id " +
            " where baixa.calculorbtrans_id = :idCalculo ";
    }

    private String montarSqlInsercaoVeiculo() {
        return " select marca.descricao as marca, modelo.descricao as modelo, vt.placa " +
            " from veiculopermissionario vp " +
            " inner join veiculotransporte vt on vp.veiculotransporte_id = vt.id " +
            " inner join modelo on vt.modelo_id = modelo.id " +
            " inner join marca on modelo.marca_id = marca.id " +
            " where vp.calculorbtrans_id = :idCalculo ";
    }

    private String montarSqlBaixaVeiculo() {
        return " select marca.descricao as marca, modelo.descricao as modelo, vt.placa " +
            " from baixaveiculoperm baixa " +
            " inner join veiculopermissionario vp on baixa.veiculopermissionario_id = vp.id " +
            " inner join veiculotransporte vt on vp.veiculotransporte_id = vt.id " +
            " inner join modelo on vt.modelo_id = modelo.id " +
            " inner join marca on modelo.marca_id = marca.id " +
            " where baixa.calculorbtrans_id = :idCalculo ";
    }

    public PermissaoTransporte recuperaPermissaoVigentePorNumeroTipo(Integer numero, TipoPermissaoRBTrans tipo) {
        String sql = " select pt.id from PermissaoTransporte pt " +
            " where pt.numero = :numero " +
            " and pt.tipoPermissaoRBTrans = :tipo " +
            " and pt.finalVigencia is null ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("numero", numero);
        q.setParameter("tipo", tipo.name());
        List<BigDecimal> result = q.getResultList();
        if (!result.isEmpty()) return recuperar(result.get(0).longValue());
        return null;

    }

    public void imprimirTermo(TermoRBTrans termo, boolean novoConteudo) {
        if (novoConteudo) termo.getDocumentoOficial().setConteudo(null);
        documentoOficialFacade.emitirDocumentoRBTrans(termo.getDocumentoOficial(), termo);
    }


//-----------------------------------------------------FIM CONSULTAS----------------------------------------------------


    public Integer getIdadeVeiculo(VeiculoTransporte veiculo) {
        Calendar c = Calendar.getInstance();
        c.setTime(SistemaFacade.getDataCorrente());
        Integer anoCorrente = c.get(Calendar.YEAR);
        return anoCorrente - veiculo.getAnoFabricacao();
    }

    public Integer retornaUltimoDigitoNumeroPermissao(Integer numero) {
        String numeroString = numero.toString();
        String ultimoDigito = numeroString.substring(numeroString.length() - 1, numeroString.length());
        return Integer.parseInt(ultimoDigito);
    }

    private ValorDivida recarregarValorDivida(ValorDivida valorDivida) {
        valorDivida.getItemValorDividas().size();
        valorDivida.getOcorrenciaValorDivida().size();
        valorDivida.getParcelaValorDividas().size();

        for (ParcelaValorDivida parcelaValorDivida : valorDivida.getParcelaValorDividas()) {
            parcelaValorDivida.getSituacoes().size();
        }

        return valorDivida;
    }

    public void salva(PermissaoTransporte permissao) {
        try {
            permissao = em.merge(permissao);
//            gerarTaxas(permissao, false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public PermissaoTransporte retornaPermissaoPersistida(PermissaoTransporte pt) {
        return em.merge(pt);
    }

    public VeiculoPermissionario atualizaVeiculo(VeiculoPermissionario vp) {
        return em.merge(vp);
    }

    public MotoristaAuxiliar atualizarMotorista(MotoristaAuxiliar ma) {
        return em.merge(ma);
    }

    public Object atualizaEntidade(Object obj) {
        return em.merge(obj);
    }

    public void salvarArquivo(FileUploadEvent fileUploadEvent,
                              Arquivo arq) {
        try {
            UploadedFile arquivoRecebido = fileUploadEvent.getFile();
            arq.setNome(arquivoRecebido.getFileName());
            arq.setMimeType(arquivoRecebido.getContentType()); //No prime 2 não funciona
            arq.setTamanho(arquivoRecebido.getSize());
            getArquivoFacade().novoArquivo(arq, arquivoRecebido.getInputstream());
            arquivoRecebido.getInputstream().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void salvarVistoriaVeiculo(VistoriaVeiculo vistoria) {
        if (vistoria.getId() != null) {
            vistoria = em.merge(vistoria);
        }

        em.persist(vistoria);
    }

    public boolean numeroPermissaoRepetido(PermissaoTransporte permissao) {
        PermissaoTransporte permissaoTransporte = recuperaPermissaoAtivaDeUmTipoPeloNumero(permissao.getNumero(), permissao.getTipoPermissaoRBTrans());

        if (permissaoTransporte == null) {
            return false;
        }

        if (permissao.getId() != null) {
            if (permissao.getId().equals(permissaoTransporte.getId())) {
                return false;
            }
        }

        if (!permissaoTransporte.getClass().equals(permissao.getClass())) {
            return false;
        }

        return true;
    }

    public ParametrosTransitoTransporte recuperarParametrosTransitoTransporte(TipoPermissaoRBTrans tipoPermissaoRBTrans) {
        return parametrosTransitoTransporteFacade.recuperarParametroVigentePeloTipo(tipoPermissaoRBTrans);
    }

    @Override
    public PermissaoTransporte recuperar(Object id) {
        PermissaoTransporte pt = em.find(PermissaoTransporte.class, id);

        Hibernate.initialize(pt.getPermissionarios());
        Hibernate.initialize(pt.getCalculosPermissao());
        if (pt.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(pt.getDetentorArquivoComposicao().getArquivosComposicao());
        }

        try {
            if (pt.getPermissionarioVigente() != null) {
                CadastroEconomico permissionario = recuperarPermissionario(pt.getPermissionarioVigente().getCadastroEconomico());
                if (permissionario.getPessoa() != null) {
                    Hibernate.initialize(permissionario.getPessoa().getEnderecos());
                    Hibernate.initialize(permissionario.getPessoa().getTelefones());
                    if (permissionario.getPessoa() instanceof PessoaFisica) {
                        Hibernate.initialize(((PessoaFisica) permissionario.getPessoa()).getDocumentosPessoais());
                    }
                }
            }
            Hibernate.initialize(pt.getListaTermos());
            Collections.sort(pt.getListaTermos());
        } catch (Exception ex) {
            logger.error("Erro:", ex);
        }

        Hibernate.initialize(pt.getListaDeVeiculos());
        Collections.sort(pt.getListaDeVeiculos());
        Hibernate.initialize(pt.getListaBloqueioPermissaoFiscalizacao());
        for (VeiculoPermissionario vp : pt.getListaDeVeiculos()) {
            if (vp != null && vp.getVeiculoTransporte() != null && vp.getVeiculoTransporte().getModelo() != null
                && vp.getVeiculoTransporte().getModelo().getMarca() != null) {
                Hibernate.initialize(vp.getVeiculoTransporte().getModelo().getMarca());
            }

            if (vp != null && vp.getVistoriasVeiculo() != null) {
                Hibernate.initialize(vp.getVistoriasVeiculo());
            }
        }

        if (pt.getMotoristasAuxiliares() != null) {
            Hibernate.initialize(pt.getMotoristasAuxiliares());
            Collections.sort(pt.getMotoristasAuxiliares());
            for (MotoristaAuxiliar motorista : pt.getMotoristasAuxiliares()) {
                Hibernate.initialize(motorista.getCadastroEconomico());
                if (motorista.getCadastroEconomico() != null) {
                    Hibernate.initialize(motorista.getCadastroEconomico().getPessoa());
                    if (motorista.getCadastroEconomico().getPessoa() != null) {
                        Hibernate.initialize(motorista.getCadastroEconomico().getPessoa().getTelefones());
                    }
                    if (motorista.getPessoaFisica() != null) {
                        Hibernate.initialize(motorista.getPessoaFisica().getDocumentosPessoais());
                    }
                }
            }
        }
        return pt;
    }

    public RG recuperarRg(PessoaFisica pf) {
        try {
            for (DocumentoPessoal dp : pf.getDocumentosPessoais()) {
                if (dp instanceof RG) {
                    return (RG) dp;
                }
            }
        } catch (LazyInitializationException | NullPointerException e) {
            pf.setDocumentosPessoais(recuperarDocumentosPessoais(pf));
            return recuperarRg(pf);
        }

        return null;
    }

    public Habilitacao recuperarCNH(PessoaFisica pf) {
        try {
            for (DocumentoPessoal dp : pf.getDocumentosPessoais()) {
                if (dp instanceof Habilitacao) {
                    return (Habilitacao) dp;
                }
            }
        } catch (LazyInitializationException | NullPointerException e) {
            pf.setDocumentosPessoais(recuperarDocumentosPessoais(pf));
            return recuperarCNH(pf);
        }

        return null;
    }

    public CadastroEconomico recuperarPermissionario(CadastroEconomico ce) throws Exception {
        String hql = " select ce from CadastroEconomico ce where ce = :param";

        Query q = em.createQuery(hql);
        q.setParameter("param", ce);

        if (q.getResultList().isEmpty()) {
            throw new Exception("Nenhum Cadastro Econômico foi encontrado com o id " + ce.getId());
        } else {
            return (CadastroEconomico) q.getSingleResult();
        }
    }

    private List<DocumentoPessoal> recuperarDocumentosPessoais(PessoaFisica pf) {
        String hql = " select dp "
            + " from DocumentoPessoal dp "
            + " where dp.pessoaFisica = :pessoa ";

        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pf);

        return q.getResultList();
    }


    public PermissaoTransporte recuperaPermissoesMotoristaAuxiliar(CadastroEconomico ce) {
        if (ce != null) {
            String sql = "select p.* from permissaotransporte p "
                + " where p.id = (select perm.permissaotransporte_id from permissionario perm"
                + "                inner join cadastroeconomico ce on ce.id = perm.cadastroeconomico_id and perm.finalvigencia is null"
                + "                where perm.cadastroeconomico_id = :cadastroEconomico)";
            Query q = em.createNativeQuery(sql, PermissaoTransporte.class);
            q.setParameter("cadastroEconomico", ce.getId());
            List<PermissaoTransporte> resultado = q.getResultList();
            if (!q.getResultList().isEmpty()) {
                PermissaoTransporte permissao = resultado.get(0);
                permissao.getMotoristasAuxiliares().size();
                return permissao;
            }
        }
        return null;
    }

    public PermissaoTransporte recuperaPermissaoTransporteCompleta(PermissaoTransporte permissao) {
        permissao = recuperar(permissao.getId());

        permissao.getMotoristasAuxiliares().size();
        for (MotoristaAuxiliar motorista : permissao.getMotoristasAuxiliares()) {
            if (motorista.getCadastroEconomico() != null) {
                motorista.getCadastroEconomico().getPessoa();
            }
        }
        permissao.getListaDeVeiculos().size();

        return permissao;
    }

    public Date recuperarUltimoDiaUtilDoMesCorrente() {
        Date ultimoDiaMes = Util.recuperaDataUltimoDiaDoMes(SistemaFacade.getDataCorrente());
        Calendar data = Calendar.getInstance();
        data.setTime(ultimoDiaMes);
        Calendar ultimoDiaUtil = DataUtil.ultimoDiaUtil(
            new GregorianCalendar(data.get(Calendar.YEAR), data.get(Calendar.MONTH), data.get(Calendar.DATE)), feriadoFacade);

        return new Date(ultimoDiaUtil.getTimeInMillis());
    }


//------------------------------------------------- GERAÇÃO DE TERMOS---------------------------------------------------

    public void imprimeTermoRBTrans(TermoRBTrans termoRBTrans) {
        documentoOficialFacade.emiteDocumentoOficial(termoRBTrans.getDocumentoOficial());
    }

    public void gerarTermoDePermissao(PermissaoTransporte pt,
                                      ParametrosTransitoTransporte parametroVigente) throws UFMException, TipoDoctoOficialRBTransException, ParametrosTransitoTermosException, AtributosNulosException {
        TermoRBTrans termo = new TermoRBTrans();
        termo.setTipoTermoRBTrans(TipoTermoRBTrans.TERMO_DE_PERMISSAO);
        termo.setDescricao(TipoTermoRBTrans.TERMO_DE_PERMISSAO.getDescricao());
        termo.setPermissaoTransporte(pt);
        termo.setDocumentoOficial(gerarTermo(termo,
            parametroVigente.recuperarParametroTermos(TipoTermoRBTrans.TERMO_DE_PERMISSAO),
            pt.getPermissionarioVigente().getCadastroEconomico(),
            pt.getPermissionarioVigente().getCadastroEconomico().getPessoa()));
        Hibernate.initialize(pt.getListaTermos());
        pt.getListaTermos().add(termo);
    }

    public void gerarTermoPermissaoMotoristaAuxiliar(PermissaoTransporte pt,
                                                     ParametrosTransitoTransporte parametroVigente,
                                                     MotoristaAuxiliar motorista) {
            TermoRBTrans termo = new TermoRBTrans();
            termo.setTipoTermoRBTrans(TipoTermoRBTrans.TERMO_AUTORIZA_MOTORISTA);
            termo.setDescricao(TipoTermoRBTrans.TERMO_AUTORIZA_MOTORISTA.getDescricao() +
                " - " + motorista.getCadastroEconomico().getDescricao());
            termo.setPermissaoTransporte(pt);
            try {
                termo.setDocumentoOficial(gerarTermo(termo,
                    parametroVigente.recuperarParametroTermos(TipoTermoRBTrans.TERMO_AUTORIZA_MOTORISTA), pt.getPermissionarioVigente().getCadastroEconomico(),
                    pt.getPermissionarioVigente().getCadastroEconomico().getPessoa()));
                Hibernate.initialize(pt.getListaTermos());
                pt.getListaTermos().add(termo);
            } catch (UFMException | TipoDoctoOficialRBTransException | ParametrosTransitoTermosException |
                     AtributosNulosException ex) {
                logger.error(ex.getMessage());
            }
    }

    public void gerarTermoAutorizacaoVeiculo(PermissaoTransporte pt,
                                             ParametrosTransitoTransporte parametroVigente,
                                             VeiculoTransporte veiculo) {
        TermoRBTrans termo = new TermoRBTrans();
        termo.setTipoTermoRBTrans(TipoTermoRBTrans.TERMO_AUTORIZA_VEICULO);
        termo.setDescricao(TipoTermoRBTrans.TERMO_AUTORIZA_VEICULO.getDescricao() +
            " - " + (veiculo.getPlaca() != null ? veiculo.getPlaca() : ""));
        termo.setPermissaoTransporte(pt);

        try {
            termo.setDocumentoOficial(gerarTermo(termo,
                parametroVigente.recuperarParametroTermos(TipoTermoRBTrans.TERMO_AUTORIZA_VEICULO),
                cadastroEconomicoFacade.recuperar(pt.getPermissionarioVigente().getCadastroEconomico().getId()),
                pt.getPermissionarioVigente().getCadastroEconomico().getPessoa()));
            Hibernate.initialize(pt.getListaTermos());
            pt.getListaTermos().add(termo);
        } catch (UFMException | TipoDoctoOficialRBTransException | ParametrosTransitoTermosException |
                 AtributosNulosException ex) {
            logger.error(ex.getMessage());
        }
    }

    public void gerarTermoBaixaVeiculo(PermissaoTransporte pt,
                                       ParametrosTransitoTransporte parametroVigente,
                                       VeiculoTransporte veiculo) {
        TermoRBTrans termo = new TermoRBTrans();
        termo.setTipoTermoRBTrans(TipoTermoRBTrans.TERMO_AUTORIZA_BAIXA_VEICULO);
        termo.setDescricao(TipoTermoRBTrans.TERMO_AUTORIZA_BAIXA_VEICULO.getDescricao() +
            " - " + (veiculo.getPlaca() != null ? veiculo.getPlaca() : ""));
        termo.setPermissaoTransporte(pt);
        try {
            termo.setDocumentoOficial(gerarTermo(termo,
                parametroVigente.recuperarParametroTermos(TipoTermoRBTrans.TERMO_AUTORIZA_BAIXA_VEICULO),
                pt.getPermissionarioVigente().getCadastroEconomico(),
                pt.getPermissionarioVigente().getCadastroEconomico().getPessoa()));
            Hibernate.initialize(pt.getListaTermos());
            pt.getListaTermos().add(termo);
        } catch (UFMException | TipoDoctoOficialRBTransException | ParametrosTransitoTermosException |
                 AtributosNulosException ex) {
            logger.error(ex.getMessage());
        }
    }

    public void gerarTermoBaixaInsercaoVeiculo(PermissaoTransporte pt,
                                               ParametrosTransitoTransporte parametroVigente) {
        TermoRBTrans termo = new TermoRBTrans();
        termo.setTipoTermoRBTrans(TipoTermoRBTrans.TERMO_AUTORIZA_BAIXA_INSERCAO_VEICULO);
        termo.setDescricao(TipoTermoRBTrans.TERMO_AUTORIZA_BAIXA_INSERCAO_VEICULO.getDescricao());
        termo.setPermissaoTransporte(pt);
        try {
            termo.setDocumentoOficial(gerarTermo(termo,
                parametroVigente.recuperarParametroTermos(TipoTermoRBTrans.TERMO_AUTORIZA_BAIXA_INSERCAO_VEICULO),
                pt.getPermissionarioVigente().getCadastroEconomico(),
                pt.getPermissionarioVigente().getCadastroEconomico().getPessoa()));
            Hibernate.initialize(pt.getListaTermos());
            pt.getListaTermos().add(termo);
        } catch (UFMException | TipoDoctoOficialRBTransException | ParametrosTransitoTermosException |
                 AtributosNulosException ex) {
            logger.error(ex.getMessage());
        }
    }

    public DocumentoOficial gerarTermo(TermoRBTrans termo,
                                       ParametrosTransitoTermos parametroTermo,
                                       Cadastro cadastro, Pessoa pessoa) throws UFMException, TipoDoctoOficialRBTransException, ParametrosTransitoTermosException, AtributosNulosException {

        TipoDoctoOficial tipoDocto = parametroTermo.getTipoDoctoOficial();

        if (tipoDocto == null) {
            throw new TipoDoctoOficialRBTransException("Não existe nenhum tipo de documento oficial cadastrado para "
                + parametroTermo.getTipoTermoRBTrans().getDescricao().toLowerCase() + ".");
        }
        return documentoOficialFacade.geraDocumentoRBTrans(termo, null, cadastro, pessoa, tipoDocto);
    }
//------------------------------------------------- FIM GERAÇÃO DE TERMOS-----------------------------------------------


    public MotoristaAuxiliar recuperaMotorista(TermoRBTrans termo) {
        PermissaoTransporte permissao = recuperar(termo.getPermissaoTransporte().getId());
        if (!permissao.getMotoristasAuxiliares().isEmpty()) {
            //  Collections.sort(permissao.getMotoristasAuxiliares());
            for (MotoristaAuxiliar mt : permissao.getMotoristasAuxiliares()) {
                if (mt.getFinalVigencia() == null) {
                    mt = em.find(MotoristaAuxiliar.class, mt.getId());
                    mt.getPessoaFisica();
                    mt.getPessoaFisica().getEnderecoscorreio().size();
                    mt.getPessoaFisica().getTelefones().size();
                    return mt;
                }
            }
        }
        return null;
    }

    public VeiculoTransporte recuperaVeiculosVigente(PermissaoTransporte permissao) {
        String sql = " select v.* from veiculopermissionario v " +
            " where v.permissaotransporte_id = :permissao " +
            " and (v.iniciovigencia is not null and v.finalvigencia is null)";
        Query q = em.createNativeQuery(sql, VeiculoPermissionario.class);
        q.setParameter("permissao", permissao.getId());
        if (!q.getResultList().isEmpty()) {
            VeiculoTransporte veiculoTransporte = ((VeiculoPermissionario) q.getResultList().get(0)).getVeiculoTransporte();
            Hibernate.initialize(veiculoTransporte);
            return veiculoTransporte;
        }
        return null;
    }

    public Date retornaDataFinalVigenciaPermissao(Integer dia, Integer mes, Integer ano) {
        Calendar c = Calendar.getInstance();
        c.set(DAY_OF_MONTH, dia);
        c.set(MONTH, mes - 1);
        c.set(YEAR, ano);
        return c.getTime();
    }

    public int quantidadeTransferencias(PermissaoTransporte permissaoTransporte) {
        String hql = "select count(trans.id) from TransferenciaPermissaoTransporte trans where trans.permissaoAntiga = :permissao and extract(YEAR from trans.efetuadaEm) = :ano";
        Query q = em.createQuery(hql);
        q.setParameter("permissao", permissaoTransporte);
        q.setParameter("ano", getSistemaFacade().getExercicioCorrente().getAno());
        if (q.getResultList().isEmpty()) return 0;
        return ((Long) q.getSingleResult()).intValue();
    }

    public ParcelaValorDivida recuperaParcela(Long id) {
        return em.find(ParcelaValorDivida.class, id);
    }

    public boolean recuperarDebitoRenovacao(CadastroEconomico cadastro) {
        List<SituacaoParcela> situacoes = Lists.newArrayList(SituacaoParcela.BAIXADO, SituacaoParcela.PAGO);
        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, cadastro.getId());
        consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.IGUAL, getSistemaFacade().getExercicioCorrente().getAno());
        consulta.addComplementoJoin(" inner join calculorbtrans calculorb on calculorb.id = VW.CALCULO_ID");
        consulta.addComplementoDoWhere(" and (CALCULORB.TIPOCALCULORBTRANS =  ('" + TipoCalculoRBTRans.RENOVACAO_PERMISSAO.name() + "'))");
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.NOT_IN, situacoes);
        consulta.executaConsulta();
        return consulta.getResultados().isEmpty();
    }

    public Date recuperarCredencialDeMaiorVencimento(PermissaoTransporte permissao) {
        String sql = " select digito.* from paramtransitotransporte param " +
            "inner join digitovencimento digito on digito.parametro_id = param.id " +
            "where digito.tipodigitovencimento = :tipoDigitoVencimento " +
            "and param.tipopermissao = :tipoPermissao and digito.digito = :ultimoDigito " +
            " order by digito.id desc  ";

        Query q = em.createNativeQuery(sql, DigitoVencimento.class);
        Integer ultimoDigito = retornaUltimoDigitoNumeroPermissao(permissao.getNumero());

        q.setParameter("tipoDigitoVencimento", DigitoVencimento.TipoDigitoVencimento.CREDENCIAL.name());
        q.setParameter("tipoPermissao", permissao.getTipoPermissaoRBTrans().name());
        q.setParameter("ultimoDigito", ultimoDigito);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            DigitoVencimento digito = (DigitoVencimento) q.getSingleResult();
            return definirValidadePermissaoPorParametro(digito);
        }
        return null;
    }

    private Date definirValidadePermissaoPorParametro(DigitoVencimento digito) {
        Calendar c = Calendar.getInstance();
        c.setTime(Util.getDataHoraMinutoSegundoZerado(c.getTime()));
        c.set(Calendar.DAY_OF_MONTH, digito.getDia());
        c.set(Calendar.MONTH, digito.getMes() - 1);
        c.set(Calendar.YEAR, getSistemaFacade().getExercicioCorrente().getAno() + 1);
        return c.getTime();
    }

    public List<PermissaoTransporte> buscarPermissoesDeTransportePorFiltroGeracaoCredencial(FiltroGeracaoCredencialRBTrans filtro, Date dataGeracao) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct pt.* ");
        sql.append("   from permissaotransporte pt");
        sql.append("  inner join permissionario pm on pm.permissaotransporte_id = pt.id and to_date(:dataSistema, 'dd/MM/yyyy') between trunc(pm.iniciovigencia) and trunc(coalesce(pm.finalvigencia, to_date(:dataSistema, 'dd/MM/yyyy')))");
        sql.append("  inner join cadastroeconomico pm_ce on pm_ce.id = pm.cadastroeconomico_id");
        sql.append("  left join pessoajuridica pm_ce_pj on pm_ce_pj.id = pm_ce.pessoa_id");
        sql.append("  left join pessoafisica pm_ce_pf on pm_ce_pf.id = pm_ce.pessoa_id");
        sql.append("  left join motoristaauxiliar ma on ma.permissaotransporte_id = pt.id and to_date(:dataSistema, 'dd/MM/yyyy') between trunc(ma.iniciovigencia) and trunc(coalesce(ma.finalvigencia, to_date(:dataSistema, 'dd/MM/yyyy')))");
        sql.append("  left join cadastroeconomico ma_ce on ma_ce.id = ma.cadastroeconomico_id");
        sql.append("  left join pessoajuridica ma_ce_pj on ma_ce_pj.id = ma_ce.pessoa_id");
        sql.append("  left join pessoafisica ma_ce_pf on ma_ce_pf.id = ma_ce.pessoa_id ");
        sql.append(" where (pt.tipopermissaorbtrans = :tipoPermissaoRBTrans) ");

        Map<String, Object> parametros = Maps.newHashMap();
        parametros.put("tipoPermissaoRBTrans", filtro.getTipoPermissaoRBTrans().name());
        parametros.put("dataSistema", DataUtil.getDataFormatada(dataGeracao));

        if (filtro.getNumeroPermissaoInicial() != null) {
            sql.append(" and pt.numero >= :numeroPermissaoInicial ");
            parametros.put("numeroPermissaoInicial", filtro.getNumeroPermissaoInicial());
        }

        if (filtro.getNumeroPermissaoFinal() != null) {
            sql.append(" and pt.numero <= :numeroPermissaoFinal ");
            parametros.put("numeroPermissaoFinal", filtro.getNumeroPermissaoFinal());
        }

        if (filtro.getFinalPermissaoInicial() != null) {
            sql.append(" and to_number(substr(pt.numero,length(pt.numero), 1)) >= :finalPermissaoInicial ");
            parametros.put("finalPermissaoInicial", filtro.getFinalPermissaoInicial());
        }

        if (filtro.getFinalPermissaoFinal() != null) {
            sql.append(" and to_number(substr(pt.numero,length(pt.numero), 1)) <= :finalPermissaoFinal ");
            parametros.put("finalPermissaoFinal", filtro.getFinalPermissaoFinal());
        }

        if (filtro.getNome() != null && !filtro.getNome().trim().isEmpty()) {
            sql.append(" AND ( (upper(coalesce(pm_ce_pf.nome, pm_ce_pj.razaosocial)) LIKE :nome) OR (upper(coalesce(ma_ce_pf.nome, ma_ce_pj.razaosocial)) LIKE :nome) ) ");
            parametros.put("nome", "%" + filtro.getNome().toUpperCase() + "%");
        }

        if (filtro.getCpf() != null && !filtro.getCpf().trim().isEmpty()) {
            sql.append(" AND ( (REPLACE(REPLACE(REPLACE(pm_ce_pf.cpf,'.',''),'-',''),'/','') = :cpf)  ");
            sql.append(" OR ");
            sql.append(" (REPLACE(REPLACE(REPLACE(ma_ce_pf.cpf,'.',''),'-',''),'/','') = :cpf) ) ");
            parametros.put("cpf", StringUtil.retornaApenasNumeros(filtro.getCpf()));
        }

        if (filtro.getCnpj() != null && !filtro.getCnpj().trim().isEmpty()) {
            sql.append(" AND ( (REPLACE(REPLACE(REPLACE(pm_ce_pj.cnpj,'.',''),'-',''),'/','') = :cnpj)  ");
            sql.append(" OR ");
            sql.append(" (REPLACE(REPLACE(REPLACE(ma_ce_pj.cnpj,'.',''),'-',''),'/','') = :cnpj) ) ");
            parametros.put("cnpj", StringUtil.retornaApenasNumeros(filtro.getCnpj()));
        }

        if (filtro.getCmc() != null && !filtro.getCmc().trim().isEmpty()) {
            sql.append(" AND ( pm_ce.inscricaocadastral = :cmc  ");
            sql.append(" OR ");
            sql.append(" ma_ce.inscricaocadastral = :cmc ) ");
            parametros.put("cmc", filtro.getCmc().trim());
        }
        sql.append(" order by pt.numero asc ");

        Query q = em.createNativeQuery(sql.toString(), PermissaoTransporte.class);
        for (String parametro : parametros.keySet()) {
            q.setParameter(parametro, parametros.get(parametro));
        }
        return q.getResultList();
    }

    public PermissaoTransporte carregarVeiculosDaPermissaoTransporte(PermissaoTransporte permissaoTransporte) {
        permissaoTransporte = em.find(PermissaoTransporte.class, permissaoTransporte.getId());
        Hibernate.initialize(permissaoTransporte.getListaDeVeiculos());
        return permissaoTransporte;
    }

    public PermissaoTransporte baixarVeiculo(AssistentePermissaoTransporte assistente) {
        PermissaoTransporte selecionado = assistente.getSelecionado();
        VeiculoPermissionario veiculoPermissionario = assistente.getVeiculoPermissionario();
        try {
            BaixaVeiculoPermissionario baixa = new BaixaVeiculoPermissionario();
            baixa.setUsuarioSistema(Util.recuperarUsuarioCorrente());
            baixa.setVeiculoPermissionario(veiculoPermissionario);
            baixa.setRealizadaEm(new Date());
            baixa.setCalculoRBTrans(calculoRBTransFacade.calculaBaixaVeiculo(assistente.getPermissionario().getCadastroEconomico(), selecionado.getTipoPermissaoRBTrans()));

            calculoRBTransFacade.gerarDebito(baixa.getCalculoRBTrans());

            baixa = em.merge(baixa);

            veiculoPermissionario.setBaixaVeiculoPermissionario(baixa);
            veiculoPermissionario.setFinalVigencia(new Date());
            selecionado = retornaPermissaoPersistida(selecionado);
            gerarTermoBaixaVeiculo(selecionado, assistente.getParametro(), veiculoPermissionario.getVeiculoTransporte());
            return retornaPermissaoPersistida(selecionado);

        } catch (Exception e) {
            logger.error("Erro ao baixar veiculo. ", e);
        }
        return selecionado;
    }

    public PermissaoTransporte baixarAndInserirVeiculo(AssistentePermissaoTransporte assistente) {
        PermissaoTransporte selecionado = assistente.getSelecionado();
        VeiculoPermissionario veiculoPermissionarioBaixaInsercao = assistente.getVeiculoPermissionarioBaixaInsercao();
        VeiculoPermissionario veiculoPermissionario = assistente.getVeiculoPermissionario();
        try {
            BaixaVeiculoPermissionario baixa = new BaixaVeiculoPermissionario();
            baixa.setUsuarioSistema(Util.recuperarUsuarioCorrente());
            baixa.setVeiculoPermissionario(veiculoPermissionarioBaixaInsercao);
            baixa.setRealizadaEm(new Date());
            baixa.setCalculoRBTrans(calculoRBTransFacade.calculaBaixaVeiculo(assistente.getPermissionario().getCadastroEconomico(),
                assistente.getSelecionado().getTipoPermissaoRBTrans()));

            baixa = em.merge(baixa);

            gerarDebitosAndDams(baixa.getCalculoRBTrans());

            veiculoPermissionarioBaixaInsercao.setBaixaVeiculoPermissionario(baixa);
            veiculoPermissionarioBaixaInsercao.setFinalVigencia(new Date());
            selecionado = retornaPermissaoPersistida(selecionado);

            gerarTermoBaixaVeiculo(selecionado, assistente.getParametro(), veiculoPermissionarioBaixaInsercao.getVeiculoTransporte());

            veiculoPermissionario.setPermissaoTransporte(selecionado);
            veiculoPermissionario.setCadastradoPor(Util.recuperarUsuarioCorrente());
            VistoriaVeiculo vistoria = criarNovaVistoriaVeiculo(veiculoPermissionario);

            if (selecionado.isTaxi()) {
                if (recuperarDebitoVistoriaAndRenovacao(assistente.getPermissionario().getCadastroEconomico()).isEmpty()) {
                    vistoria.setCalculoRBTrans(calculoRBTransFacade.calculaVistoriaVeiculo(assistente.getPermissionario().getCadastroEconomico()));
                    gerarDebitosAndDams(vistoria.getCalculoRBTrans());
                }
            }

            veiculoPermissionario.getVistoriasVeiculo().add(vistoria);
            veiculoPermissionario.setCalculoRBTrans(calculoRBTransFacade.calculaInsercaoVeiculo(assistente.getPermissionario().getCadastroEconomico(), selecionado.getTipoPermissaoRBTrans()));
            gerarDebitosAndDams(veiculoPermissionario.getCalculoRBTrans());

            selecionado.getListaDeVeiculos().add(veiculoPermissionario);

            selecionado = retornaPermissaoPersistida(selecionado);
            credencialRBTransFacade.baixarTodasCredenciaisPorPermissao(selecionado);

            gerarTermoBaixaInsercaoVeiculo(selecionado, assistente.getParametro());

            return retornaPermissaoPersistida(selecionado);
        } catch (Exception e) {
            logger.error("Erro ao baixar e inserir veiculo. ", e);
        }
        return selecionado;
    }

    public PermissaoTransporte adicionarVeiculo(AssistentePermissaoTransporte assistente) {
        PermissaoTransporte selecionado = assistente.getSelecionado();
        VeiculoPermissionario veiculoPermissionario = assistente.getVeiculoPermissionario();

        try {
            veiculoPermissionario.setPermissaoTransporte(selecionado);
            veiculoPermissionario.setCadastradoPor(Util.recuperarUsuarioCorrente());
            VistoriaVeiculo vistoria = criarNovaVistoriaVeiculo(veiculoPermissionario);
            if (selecionado.isTaxi()) {
                if (recuperarDebitoVistoriaAndRenovacao(assistente.getPermissionario().getCadastroEconomico()).isEmpty()) {
                    vistoria.setCalculoRBTrans(calculoRBTransFacade.calculaVistoriaVeiculo(assistente.getPermissionario().getCadastroEconomico()));
                    gerarDebitosAndDams(vistoria.getCalculoRBTrans());
                }
            }
            veiculoPermissionario.getVistoriasVeiculo().add(vistoria);
            veiculoPermissionario.setCalculoRBTrans(calculoRBTransFacade.calculaInsercaoVeiculo(assistente.getPermissionario().getCadastroEconomico(), selecionado.getTipoPermissaoRBTrans()));

            gerarDebitosAndDams(veiculoPermissionario.getCalculoRBTrans());

            selecionado.getListaDeVeiculos().add(veiculoPermissionario);
            selecionado = retornaPermissaoPersistida(selecionado);
            credencialRBTransFacade.baixarTodasCredenciaisPorPermissao(selecionado);
            gerarTermoAutorizacaoVeiculo(selecionado, assistente.getParametro(), veiculoPermissionario.getVeiculoTransporte());
            return retornaPermissaoPersistida(selecionado);
        } catch (Exception e) {
            logger.error("Erro ao adicionar veiculo. ", e);
        }
        return selecionado;
    }

    public PermissaoTransporte adicionarVeiculoGestor(AssistentePermissaoTransporte assistente) {
        PermissaoTransporte selecionado = assistente.getSelecionado();
        VeiculoPermissionario veiculoPermissionario = assistente.getVeiculoPermissionario();
        try {
            veiculoPermissionario.setPermissaoTransporte(selecionado);
            veiculoPermissionario.setCadastradoPor(Util.recuperarUsuarioCorrente());
            veiculoPermissionario.getVistoriasVeiculo().add(criarNovaVistoriaVeiculo(veiculoPermissionario));
            selecionado.getListaDeVeiculos().add(veiculoPermissionario);

            selecionado = retornaPermissaoPersistida(selecionado);
            credencialRBTransFacade.baixarTodasCredenciaisPorPermissao(selecionado);
            return selecionado;
        } catch (Exception e) {
            logger.error("Erro ao adicionar veiculo gestor. ", e);
        }
        return selecionado;
    }

    private void gerarDebitosAndDams(CalculoRBTrans calculo) {
        calculoRBTransFacade.gerarDebito(calculo);
    }

    private VistoriaVeiculo criarNovaVistoriaVeiculo(VeiculoPermissionario veiculoPermissionario) {
        VistoriaVeiculo vistoria = new VistoriaVeiculo();
        vistoria.setVeiculoPermissionario(veiculoPermissionario);
        return vistoria;
    }

    @Override
    public void depoisDePagar(Calculo calculo) {
        CalculoRBTrans calculoRBTrans = em.find(CalculoRBTrans.class, calculo.getId());
        if (TipoCalculoRBTRans.RENOVACAO_PERMISSAO.equals(calculoRBTrans.getTipoCalculoRBTRans())) {
            PermissaoTransporteFacade permissaoTransporteFacade = (PermissaoTransporteFacade) Util.getFacadeViaLookup("java:module/PermissaoTransporteFacade");
            PermissaoTransporte permissao = permissaoTransporteFacade.recuperaPermissaoPorCMC(calculoRBTrans.getCadastro());
            permissao.setHabilitarVerificacaoDocumentos(true);
            permissao.setDocumentosApresentados(false);
            permissaoTransporteFacade.salva(permissao);
        }
    }

    public List<PermissaoTransporte> buscarPermissoesFiltrandoPorPermissionarioETipoPermissao(TipoPermissaoRBTrans tipoPermissao, String parte) {
        List<PermissaoTransporte> retorno = Lists.newArrayList();
        Query q = em.createNativeQuery("select permissao.id " +
            " from permissaotransporte permissao " +
            "         left join permissionario perm on permissao.id = perm.permissaotransporte_id and " +
            "                                           (perm.finalvigencia is null or trunc(perm.finalvigencia) >= trunc(sysdate)) " +
            "         left join cadastroeconomico cmc on perm.cadastroeconomico_id = cmc.id " +
            "         left join pessoafisica pfcmc on cmc.pessoa_id = pfcmc.id " +
            "         left join pessoajuridica pjcmc on cmc.pessoa_id = pjcmc.id " +
            " where permissao.tipopermissaorbtrans = :tipoPermissao " +
            "  and (lower(coalesce(pfcmc.nome, pjcmc.razaosocial)) like lower(:parte) or " +
            "       coalesce(pfcmc.cpf, pjcmc.cnpj) like :parte or " +
            "       cmc.inscricaocadastral like :parte) fetch first 15 rows only");
        q.setParameter("tipoPermissao", tipoPermissao.name());
        q.setParameter("parte", "%" + parte.trim().replaceAll("\\.", "").replaceAll("-", "").replaceAll("/", "") + "%");
        List<BigDecimal> result = q.getResultList();
        for (BigDecimal idPermissao : result) {
            retorno.add(recuperar(idPermissao.longValue()));
        }
        return retorno;
    }
}
