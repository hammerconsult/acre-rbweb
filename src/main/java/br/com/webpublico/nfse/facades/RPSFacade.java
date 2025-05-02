package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.ConfiguracaoNfse;
import br.com.webpublico.nfse.domain.NotaFiscal;
import br.com.webpublico.nfse.domain.Rps;
import br.com.webpublico.nfse.domain.dtos.DeclaracaoMensalServicoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NotaFiscalSearchDTO;
import br.com.webpublico.nfse.domain.dtos.PrestadorServicoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.RpsNfseDTO;
import br.com.webpublico.nfse.domain.dtos.enums.TipoDeclaracaoMensalServicoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.enums.TipoMovimentoMensalNfseDTO;
import br.com.webpublico.nfse.enums.TipoParametroNfse;
import br.com.webpublico.nfse.util.Competencia;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Stateless
public class RPSFacade extends AbstractFacade<Rps> {

    private final String SELECT_RPS = "select a.id, a.numero, a.serie, a.dataEmissao, a.totalServicos, a.issPagar," +
        " nota.numero as numeroNota, nota.id as notaId, lote.id as lote_id, lote.numero as numeroLote ";

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoNfseFacade configuracaoNfseFacade;
    @EJB
    private DeclaracaoMensalServicoFacade declaracaoMensalServicoFacade;


    public RPSFacade() {
        super(Rps.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    private RpsNfseDTO popularDtoResultadoQuery(Object[] obj) {
        RpsNfseDTO dto = new RpsNfseDTO();
        dto.setId(((Number) obj[0]).longValue());
        dto.setNumero((String) obj[1]);
        dto.setSerie((String) obj[2]);
        dto.setDataEmissao((Date) obj[3]);
        dto.setTotalServicos((BigDecimal) obj[4]);
        dto.setIssPagar((BigDecimal) obj[5]);
        if (obj[6] != null)
            dto.setNumeroNotaFiscal(obj[6].toString());
        if (obj[7] != null)
            dto.setIdNotaFiscal(((Number) obj[7]).longValue());
        if (obj[8] != null)
            dto.setIdLote(((Number) obj[8]).longValue());
        if (obj[9] != null)
            dto.setNumeroLote(obj[9].toString());

        return dto;
    }

    public List<RpsNfseDTO> buscarRpsPorLote(Long loteId) {
        String from = "  from Rps a" +
            " left join notafiscal nota on nota.rps_id = a.id " +
            " left join loterps lote on lote.id = a.loterps_id " +
            " where  a.loterps_id = :loteId";
        Query q = em.createNativeQuery(SELECT_RPS + from);
        q.setParameter("loteId", loteId);
        List<RpsNfseDTO> dtos = Lists.newArrayList();
        List<Object[]> resultado = q.getResultList();
        for (Object[] obj : resultado) {
            dtos.add(popularDtoResultadoQuery(obj));
        }
        return dtos;
    }

    @Override
    public Rps recuperar(Object id) {
        Rps recuperar = super.recuperar(id);
        Hibernate.initialize(recuperar.getItens());
        return recuperar;
    }

    public List<NotaFiscal> getNotasDeRPSDaCompetenciaAnterior() {
        Query q = em.createNativeQuery("select nfse.* " +
            " from NOTAFISCAL nfse " +
            "         inner join DECLARACAOPRESTACAOSERVICO D on nfse.DECLARACAOPRESTACAOSERVICO_ID = D.ID " +
            "         inner join rps on rps.id = nfse.RPS_ID " +
            " where extract(year from d.COMPETENCIA) <= extract(year from current_date)" +
            "  and extract(month from d.COMPETENCIA) < extract(month from current_date)" +
            "  and d.NATUREZAOPERACAO = 'TRIBUTACAO_MUNICIPAL'" +
            "  and coalesce(nfse.MIGRADO,'0') = '0' " +
            "  and not exists(" +
            "        select nd.id " +
            "        from NOTADECLARADA nd" +
            "        where nd.DECLARACAOPRESTACAOSERVICO_ID = nfse.DECLARACAOPRESTACAOSERVICO_ID" +
            "    )" +
            " order by d.COMPETENCIA desc", NotaFiscal.class);
        return q.getResultList();
    }

    public void encerrarcCompetenciaAnterior() {
        ConfiguracaoNfse configuracaoNfse = configuracaoNfseFacade.recuperarConfiguracao();
        List<NotaFiscal> processadas = getNotasDeRPSDaCompetenciaAnterior();
        if (configuracaoNfse.getParametroBoolean(TipoParametroNfse.ENCERRA_COMPETENCIA_ANTERIOR_RPS)) {
            Date competenciaAtual = new Date();
            Map<Competencia, List<NotaFiscal>> notas = Maps.newHashMap();
            List<DeclaracaoMensalServicoNfseDTO> declaracoes = Lists.newArrayList();
            for (NotaFiscal nota : processadas) {
                Date competencia1 = nota.getDeclaracaoPrestacaoServico().getCompetencia();
                if (DataUtil.getMes(competenciaAtual) > DataUtil.getMes(competencia1)) {
                    Competencia competencia = new Competencia(DataUtil.getMes(competencia1), DataUtil.getAno(competencia1), nota.getPrestador().getId());
                    if (notas.get(competencia) == null) {
                        notas.put(competencia, Lists.<NotaFiscal>newArrayList());
                    }
                    notas.get(competencia).add(nota);
                }
            }
            if (!notas.isEmpty()) {
                for (Competencia competencia : notas.keySet()) {
                    DeclaracaoMensalServicoNfseDTO declaracao = new DeclaracaoMensalServicoNfseDTO();
                    declaracao.setMes(competencia.getMes());
                    declaracao.setExercicio(competencia.getAno());
                    declaracao.setTipo(TipoDeclaracaoMensalServicoNfseDTO.PRINCIPAL);
                    declaracao.setTipoMovimento(TipoMovimentoMensalNfseDTO.NORMAL);
                    declaracao.setNotas(Lists.<NotaFiscalSearchDTO>newArrayList());
                    PrestadorServicoNfseDTO prestador = new PrestadorServicoNfseDTO();
                    for (NotaFiscal nota : notas.get(competencia)) {
                        NotaFiscalSearchDTO notaDeclarar = new NotaFiscalSearchDTO();
                        notaDeclarar.setId(nota.getDeclaracaoPrestacaoServico().getId());
                        declaracao.getNotas().add(notaDeclarar);
                        declaracao.setTotalServicos(declaracao.getTotalServicos().add(nota.getDeclaracaoPrestacaoServico().getTotalServicos()));
                        declaracao.setTotalIss(declaracao.getTotalIss().add(nota.getDeclaracaoPrestacaoServico().getIssPagar()));
                        prestador.setId(nota.getPrestador().getId());
                    }
                    declaracao.setQtdNotas(notas.get(competencia).size());
                    declaracao.setAbertura(new Date());
                    declaracao.setEncerramento(new Date());
                    declaracao.setPrestador(prestador);
                    declaracoes.add(declaracao);
                }
                declaracaoMensalServicoFacade.declarar(declaracoes);
            }
        }
    }
}
