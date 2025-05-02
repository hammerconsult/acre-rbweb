package br.com.webpublico.negocios;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteRenovacaoPermissaoTransporte;
import br.com.webpublico.entidadesauxiliares.FiltroRenovacaoPermissao;
import br.com.webpublico.entidadesauxiliares.VOPermissaoRenovacao;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.util.DetailProcessAsync;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by AndreGustavo on 10/09/2014.
 */
@Stateless
public class RenovacaoPermissaoFacade extends AbstractFacade<RenovacaoPermissao> {

    private Logger logger = LoggerFactory.getLogger(RenovacaoPermissaoFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private PermissaoTransporteFacade permissaoTransporteFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public RenovacaoPermissaoFacade() {
        super(RenovacaoPermissao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<VOPermissaoRenovacao> consultaPermissoesParaRenovacao(FiltroRenovacaoPermissao filtro) {
        List<VOPermissaoRenovacao> toReturn = new ArrayList<>();
        Query q = em.createNativeQuery(filtro.getQueryConsultaPermissoes());

        q.setParameter("situacaoCadastral", Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name(), SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name()));
        if (filtro.getNumeroPermissao() != null) {
            q.setParameter("numero", filtro.getNumeroPermissao());
        }

        if (filtro.getTipoPermissaoRBTrans() != null) {
            q.setParameter("tipoPermissaoRBTrans", filtro.getTipoPermissaoRBTrans().name());
        }

        if (filtro.getInicioDigitoFinalPermissao() != null) {
            q.setParameter("inicioDigitoFinalPermissao", filtro.getInicioDigitoFinalPermissao());
        }

        if (filtro.getFimDigitoFinalPermissao() != null) {
            q.setParameter("fimDigitoFinalPermissao", filtro.getFimDigitoFinalPermissao());
        }


        List<Object[]> elementos = q.getResultList();
        for (Object[] elem : elementos) {
            VOPermissaoRenovacao vo = new VOPermissaoRenovacao();
            vo.setNumero(((BigDecimal) elem[0]).intValue());
            vo.setTipoPermissaoRBTrans(TipoPermissaoRBTrans.valueOf((String) elem[1]));
            vo.setNomePermissionario((String) elem[2]);
            vo.setFinalVigencia((Date) elem[3]);
            vo.setIdPermissao(((BigDecimal) elem[4]).longValue());
            toReturn.add(vo);
        }


        return toReturn;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    private Integer getIdadeVeiculo(VeiculoPermissionario veiculo) {
        return getPermissaoTransporteFacade().getIdadeVeiculo(veiculo.getVeiculoTransporte());
    }

    private CalculoRenovacao geraTaxaRenovacao(Date dataOperacao, Exercicio exercicio, PermissaoTransporte pt, RenovacaoPermissao renovacao) throws ExcecaoNegocioGenerica {
        CalculoRenovacao calculo = new CalculoRenovacao();
        calculo.setCalculoRBTrans(getPermissaoTransporteFacade().getCalculoRBTransFacade().calculaRenovacaoPermissao(dataOperacao, exercicio, pt.getPermissionarioVigente().getCadastroEconomico(),
            pt.getTipoPermissaoRBTrans(), pt));
        calculo.setRenovacaoPermissao(renovacao);
        return calculo;
    }

    public CalculoRBTrans gerarTaxaVistoria(Date dataOperacao, Exercicio exercicio, PermissaoTransporte pt, Date vencimento) throws ExcecaoNegocioGenerica {
        return getPermissaoTransporteFacade().getCalculoRBTransFacade().calculaVistoriaVeiculo(dataOperacao, exercicio, pt.getPermissionarioVigente().getCadastroEconomico(), vencimento);
    }

    public CalculoRBTrans gerarTaxaOutorga(Date dataOperacao, Exercicio exercicio, PermissaoTransporte pt, Date vencimento) throws ExcecaoNegocioGenerica {
        return getPermissaoTransporteFacade().getCalculoRBTransFacade().calculaOutorga(dataOperacao, exercicio, pt.getPermissionarioVigente().getCadastroEconomico(), vencimento);
    }

    public CalculoRBTrans gerarTaxaOutorga(PermissaoTransporte pt) throws ExcecaoNegocioGenerica {
        return getPermissaoTransporteFacade().getCalculoRBTransFacade().calculaOutorga(pt.getPermissionarioVigente().getCadastroEconomico());
    }

    public void novaVistoria(PermissaoTransporte pt) {
        SistemaControlador sistemaControlador = ((SistemaControlador) Util.getControladorPeloNome("sistemaControlador"));
        novaVistoria(sistemaControlador.getDataOperacao(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getUsuarioCorrente(), pt, null);
    }

    public VistoriaVeiculo novaVistoria(Date dataOperacao, Exercicio exercicio, UsuarioSistema usuarioSistema, PermissaoTransporte pt, Date vencimento) throws ExcecaoNegocioGenerica {
        VistoriaVeiculo vistoria = new VistoriaVeiculo();
        vistoria.setVeiculoPermissionario(pt.getVeiculoVigente());
        vistoria.setUsuarioSistema(usuarioSistema);
        vistoria.setRealizadaEm(dataOperacao);
        vistoria.setCalculoRBTrans(gerarTaxaVistoria(dataOperacao, exercicio, pt, vencimento));
        return vistoria;
    }

    public CalculoPermissao novaOutorga(PermissaoTransporte pt) {
        CalculoPermissao c = new CalculoPermissao();
        c.setCalculoRBTrans(gerarTaxaOutorga(pt));
        c.setPermissaoTransporte(pt);
        return c;
    }

    public CalculoPermissao novaOutorga(Date dataOperacao, Exercicio exercicio, PermissaoTransporte pt, Date vencimento) {
        CalculoPermissao c = new CalculoPermissao();
        c.setCalculoRBTrans(gerarTaxaOutorga(dataOperacao, exercicio, pt, vencimento));
        c.setPermissaoTransporte(pt);
        return c;
    }

    private AssistenteRenovacaoPermissaoTransporte gerarDebitosParaRenovacaoDeTaxi(AssistenteRenovacaoPermissaoTransporte assistente) throws ExcecaoNegocioGenerica {
        CalculoRenovacao calculoRenovacao = geraTaxaRenovacao(assistente.getDataOperacao(), assistente.getExercicio(), assistente.getPermissaoTransporte(), assistente.getRenovacaoPermissao());
        assistente.getRenovacaoPermissao().getCalculosRenovacao().add(calculoRenovacao);
        permissaoTransporteFacade.getCalculoRBTransFacade().gerarDebito(calculoRenovacao.getCalculoRBTrans());
        assistente.getCalculosGerados().add(calculoRenovacao.getCalculoRBTrans());

        if ((assistente.getPermissaoTransporte().getVeiculoVigente() != null) &&
            (getIdadeVeiculo(assistente.getPermissaoTransporte().getVeiculoVigente()) > 3)) {
            VistoriaVeiculo vistoriaVeiculo = novaVistoria(assistente.getDataOperacao(), assistente.getExercicio(),
                assistente.getUsuarioSistema(), assistente.getPermissaoTransporte(), assistente.getPermissaoTransporte().getFinalVigencia());
            assistente.getPermissaoTransporte().getVeiculoVigente().getVistoriasVeiculo().add(vistoriaVeiculo);
            permissaoTransporteFacade.getCalculoRBTransFacade().gerarDebito(vistoriaVeiculo.getCalculoRBTrans());
            assistente.getCalculosGerados().add(vistoriaVeiculo.getCalculoRBTrans());
        }

        return assistente;
    }

    private AssistenteRenovacaoPermissaoTransporte gerarDebitosParaRenovacaoDeMotoTaxi(AssistenteRenovacaoPermissaoTransporte assistente, ParametrosTransitoTransporte parametro) throws ExcecaoNegocioGenerica {
        CalculoRenovacao calculoRenovacao = geraTaxaRenovacao(assistente.getDataOperacao(), assistente.getExercicio(),
            assistente.getPermissaoTransporte(), assistente.getRenovacaoPermissao());
        assistente.getRenovacaoPermissao().getCalculosRenovacao().add(calculoRenovacao);
        permissaoTransporteFacade.getCalculoRBTransFacade().gerarDebito(calculoRenovacao.getCalculoRBTrans());
        assistente.getCalculosGerados().add(calculoRenovacao.getCalculoRBTrans());

        if (parametro.getGerarOutorga()) {
            CalculoPermissao calculoPermissao = novaOutorga(assistente.getDataOperacao(), assistente.getExercicio(), assistente.getPermissaoTransporte(),
                assistente.getPermissaoTransporte().getFinalVigencia());
            assistente.getPermissaoTransporte().getCalculosPermissao().add(calculoPermissao);
            permissaoTransporteFacade.getCalculoRBTransFacade().gerarDebito(calculoPermissao.getCalculoRBTrans());
            assistente.getCalculosGerados().add(calculoPermissao.getCalculoRBTrans());
        }
        return assistente;
    }

    private AssistenteRenovacaoPermissaoTransporte gerarDebitosParaRenovacaoDeFrete(AssistenteRenovacaoPermissaoTransporte assistente) throws ExcecaoNegocioGenerica {
        CalculoRenovacao calculoRenovacao = geraTaxaRenovacao(assistente.getDataOperacao(), assistente.getExercicio(),
            assistente.getPermissaoTransporte(), assistente.getRenovacaoPermissao());
        assistente.getRenovacaoPermissao().getCalculosRenovacao().add(calculoRenovacao);
        permissaoTransporteFacade.getCalculoRBTransFacade().gerarDebito(calculoRenovacao.getCalculoRBTrans());
        assistente.getCalculosGerados().add(calculoRenovacao.getCalculoRBTrans());
        return assistente;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 12)
    public List<AssistenteRenovacaoPermissaoTransporte> renovar(AssistenteCalculoRenovacao assistenteCalculoRenovacao,
                                                                Date dataOperacao, List<VOPermissaoRenovacao> permissoes,
                                                                Integer anoRenovacao,
                                                                ParametrosTransitoTransporte parametro) throws ExcecaoNegocioGenerica {
        List<AssistenteRenovacaoPermissaoTransporte> listAssistenteRenovacaoPermissaoTransporte = Lists.newArrayList();
        Exercicio exercicio = getPermissaoTransporteFacade().getExercicioFacade().recuperarExercicioPeloAno(anoRenovacao);
        for (VOPermissaoRenovacao vo : permissoes) {
            PermissaoTransporte pt = getPermissaoTransporteFacade().recuperar(vo.getIdPermissao());
            AssistenteRenovacaoPermissaoTransporte assistenteRenovacaoPermissaoTransporte = new AssistenteRenovacaoPermissaoTransporte();

            RenovacaoPermissao renovacao = new RenovacaoPermissao();
            renovacao.setPermissaoTransporte(pt);
            renovacao.setUsuarioSistema(assistenteCalculoRenovacao.getUsuarioSistema());
            renovacao.setDataRenovacao(dataOperacao);

            DigitoVencimento digitoVencimento = parametro.recuperarVencimentoPorDigitoTipo(getPermissaoTransporteFacade().retornaUltimoDigitoNumeroPermissao(pt.getNumero()), DigitoVencimento.TipoDigitoVencimento.LICENCIAMENTO);
            pt.setFinalVigencia(getPermissaoTransporteFacade().retornaDataFinalVigenciaPermissao(digitoVencimento.getDia(), digitoVencimento.getMes(), anoRenovacao));

            assistenteRenovacaoPermissaoTransporte.setDataOperacao(dataOperacao);
            assistenteRenovacaoPermissaoTransporte.setExercicio(exercicio);
            assistenteRenovacaoPermissaoTransporte.setUsuarioSistema(assistenteCalculoRenovacao.getUsuarioSistema());
            assistenteRenovacaoPermissaoTransporte.setPermissaoTransporte(pt);
            assistenteRenovacaoPermissaoTransporte.setRenovacaoPermissao(renovacao);

            switch (pt.getTipoPermissaoRBTrans()) {
                case TAXI:
                    listAssistenteRenovacaoPermissaoTransporte.add(gerarDebitosParaRenovacaoDeTaxi(assistenteRenovacaoPermissaoTransporte));
                    break;
                case MOTO_TAXI:
                    listAssistenteRenovacaoPermissaoTransporte.add(gerarDebitosParaRenovacaoDeMotoTaxi(assistenteRenovacaoPermissaoTransporte, parametro));
                    break;
                case FRETE:
                    listAssistenteRenovacaoPermissaoTransporte.add(gerarDebitosParaRenovacaoDeFrete(assistenteRenovacaoPermissaoTransporte));
                    break;
            }

            pt.getRenovacoes().add(renovacao);
            getPermissaoTransporteFacade().salvar(pt);
            assistenteCalculoRenovacao.conta();
            logger.debug("JA RENOVOU : " + assistenteCalculoRenovacao.getCalculados());
        }
        return listAssistenteRenovacaoPermissaoTransporte;
    }

    public static class AssistenteCalculoRenovacao implements DetailProcessAsync {
        private static final String formatoDataHora = "%d:%2$TM:%2$TS%n";
        private Integer calculados, total;
        private Long decorrido, tempo;
        private Double qntoFalta;
        private UsuarioSistema usuarioSistema;
        private String descricao;
        private Date inicio, fim;
        private String log;

        public AssistenteCalculoRenovacao(Integer total) {
            this.total = total;
            calculados = 0;
        }

        public AssistenteCalculoRenovacao(Integer total, UsuarioSistema usuarioSistema, String descricao) {
            this.total = total;
            calculados = 0;
            this.usuarioSistema = usuarioSistema;
            this.descricao = descricao;
        }

        public AssistenteCalculoRenovacao(Date inicio, Date fim) {
            this.inicio = inicio;
            this.fim = fim;
            calculados = 0;
        }

        public String getLog() {
            return log;
        }

        public void setLog(String log) {
            this.log = log;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public synchronized void conta() {
            calculados++;
        }

        public Integer getCalculados() {
            return calculados;
        }

        @Override
        public String getUsuario() {
            return usuarioSistema != null ? usuarioSistema.getLogin() : "";
        }

        @Override
        public String getDescricao() {
            return descricao;
        }

        public Integer getTotal() {
            return total;
        }

        @Override
        public Double getPorcentagemExecucao() {
            return 0.0;
        }

        public Date getInicio() {
            return inicio;
        }

        public Date getFim() {
            return fim;
        }

        public Double getPorcentagemDoCalculo() {
            if (calculados == null || total == null) {
                return 0d;
            }
            return (calculados.doubleValue() / total.doubleValue()) * 100;
        }

        public String getTempoDecorrido() {
            long HOUR = TimeUnit.HOURS.toMillis(1);

            decorrido = (System.currentTimeMillis() - tempo);

            return String.format(formatoDataHora, decorrido / HOUR, decorrido % HOUR);
        }

        public String getTempoEstimado() {
            long HOUR = TimeUnit.HOURS.toMillis(1);
            long unitario = (System.currentTimeMillis() - tempo) / (calculados + 1);
            qntoFalta = (unitario * (total - calculados.doubleValue()));
            return String.format(formatoDataHora, qntoFalta.longValue() / HOUR, qntoFalta.longValue() % HOUR);
        }

        public UsuarioSistema getUsuarioSistema() {
            return usuarioSistema;
        }

        public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
            this.usuarioSistema = usuarioSistema;
        }
    }

    public PermissaoTransporteFacade getPermissaoTransporteFacade() {
        return permissaoTransporteFacade;
    }
}
