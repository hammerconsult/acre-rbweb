package br.com.webpublico.nfse.facades;

import br.com.webpublico.enums.Sistema;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.nfse.domain.CupomCampanhaNfse;
import br.com.webpublico.nfse.domain.PremioSorteioNfse;
import br.com.webpublico.nfse.domain.SorteioNfse;
import br.com.webpublico.nfse.domain.dtos.DetalheCupomSorteioNfseDTO;
import br.com.webpublico.nfse.domain.dtos.FiltroCupomSorteioNfse;
import br.com.webpublico.nfse.domain.dtos.RelatorioResultadoSorteioNfse;
import br.com.webpublico.nfse.domain.dtos.ResultadoSorteioDTO;
import br.com.webpublico.nfse.enums.SituacaoSorteioNfse;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import com.beust.jcommander.internal.Maps;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;

@Stateless
public class SorteioNfseFacade extends AbstractFacade<SorteioNfse> {

    @EJB
    private CampanhaNfseFacade campanhaNfseFacade;


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public SorteioNfseFacade() {
        super(SorteioNfse.class);
    }

    public CampanhaNfseFacade getCampanhaNfseFacade() {
        return campanhaNfseFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void validarPremio(SorteioNfse sorteio, PremioSorteioNfse premio) throws ValidacaoException {
        premio.validarCamposObrigatorios();
        if (sorteio.getPremios() != null && !sorteio.getPremios().isEmpty()) {
            for (PremioSorteioNfse premioRegistrado : sorteio.getPremios()) {
                if (!premio.equals(premioRegistrado) && premio.getSequencia().equals(premioRegistrado.getSequencia())) {
                    throw new ValidacaoException("Sequência do prêmio já registrada.");
                }
            }
        }
    }

    public List<SorteioNfse> buscarSorteiosAbertos() {
        String sql = " select * from sorteionfse " +
            " where situacao = :situacao ";
        Query q = em.createNativeQuery(sql, SorteioNfse.class);
        q.setParameter("situacao", SituacaoSorteioNfse.ABERTO.name());
        return q.getResultList();
    }

    @Override
    public SorteioNfse recuperar(Object id) {
        SorteioNfse campanha = super.recuperar(id);
        campanha.getPremios().size();
        return campanha;
    }


    public List<CupomCampanhaNfse> recuperarCupons(SorteioNfse sorteio) {
        String sql = " select * from cupomsorteionfse " +
            " where sorteio_id = :sorteio_id ";
        Query q = em.createNativeQuery(sql, CupomCampanhaNfse.class);
        q.setParameter("sorteio_id", sorteio.getId());
        return q.getResultList();
    }

    public List<RelatorioResultadoSorteioNfse> buscarDadosRelatorioResultadoSorteio(Long idSorteio) {
        String sql = " select " +
            "    s.id as id_sorteio, " +
            "    s.numero as numero_sorteio, " +
            "    s.descricao as descricao_sorteio, " +
            "    cp.inicio as inicio_campanha, " +
            "    cp.fim as fim_campanha, " +
            "    s.datadivulgacaocupom as data_divulgacao_cupom, " +
            "    s.datasorteio as data_sorteio, " +
            "    p.id as id_premio, " +
            "    p.descricao as descricao_premio, " +
            "    p.quantidade as quantidade_premio, " +
            "    p.valor as valor_premio, " +
            "    c.numero as numero_cupom, " +
            "    dpt.cpfcnpj as cpfcnpj_tomador, " +
            "    dpt.nomerazaosocial as nomerazaosocial_tomador, " +
            "    dpt.logradouro logradouro_tomador, " +
            "    dpt.bairro as bairro_tomador, " +
            "    dpt.numero as numero_tomador, " +
            "    dpt.complemento as complemento_tomador, " +
            "    dpt.municipio as cidade_tomador, " +
            "    dpt.uf as uf_tomador, " +
            "    dpt.telefone as telefone_tomador, " +
            "    dpt.celular as celular_tomador, " +
            "    dpt.email as email_tomador, " +
            "    dpp.cpfcnpj as cpfcnpj_prestador, " +
            "    dpp.nomerazaosocial as nomerazaosocial_prestador, " +
            "    coalesce(nf.numero, sd.numero) as numero_nota_fiscal, " +
            "    coalesce(nf.emissao, sd.emissao) as emissao_nota_fiscal, " +
            "    (select max(s.nome) " +
            "     from itemdeclaracaoservico ids " +
            "              inner join servico s on s.id = ids.servico_id " +
            "     where ids.declaracaoprestacaoservico_id = dec.id) as servico, " +
            "    dec.totalservicos as total_servico " +
            "   from sorteionfse s " +
            "  inner join campanhanfse cp on cp.id = s.campanha_id " +
            "  inner join premiosorteionfse p on p.sorteio_id = s.id " +
            "  inner join cupomcampanhanfse c on c.premio_id = p.id " +
            "  inner join usuarionotapremiada unp on unp.id = c.usuario_id " +
            "  inner join declaracaoprestacaoservico dec on dec.id = c.declaracao_id " +
            "  left join notafiscal nf on nf.declaracaoprestacaoservico_id = dec.id " +
            "  left join servicodeclarado sd on sd.declaracaoprestacaoservico_id = dec.id " +
            "  inner join dadospessoaisnfse dpp on dpp.id = dec.dadospessoaisprestador_id " +
            "  inner join dadospessoaisnfse dpt on dpt.id = unp.dadospessoais_id " +
            "where s.id = :id_sorteio " +
            "order by s.id, p.sequencia, case when c.numero >= s.numerosorteado then 0 else 1 end, c.numero  ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id_sorteio", idSorteio);
        return RelatorioResultadoSorteioNfse.fromObjects(q.getResultList());
    }

    public FiltroCupomSorteioNfse buscarCupons(FiltroCupomSorteioNfse filtroCupons) throws Exception {
        String select = "   select " +
            "      c.id as id, " +
            "      c.numero, " +
            "      case when c.premio_id is not null and sorteio.numero = :numero_sorteio then 1 else 0 end as premiado, " +
            "      coalesce(nf.numero, sd.numero) as numero_nota, " +
            "      coalesce(nf.emissao, sd.emissao) as emissao_nota, " +
            "      coalesce(dpp.cpfcnpj, '')||' - '||coalesce(dpp.nomerazaosocial, '') prestador_nota, " +
            "      coalesce(dpt.cpfcnpj, '')||' - '||coalesce(dpt.nomerazaosocial, '') tomador_nota ";
        String from = "   from cupomcampanhanfse c " +
            "  left join premiosorteionfse premio on premio.id = c.premio_id " +
            "  left join sorteionfse sorteio on sorteio.id = premio.sorteio_id " +
            "  inner join campanhanfse cm on cm.id = c.campanha_id " +
            "  inner join declaracaoprestacaoservico dec on dec.id = c.declaracao_id " +
            "  inner join dadospessoaisnfse dpp on dpp.id = dec.dadospessoaisprestador_id " +
            "  inner join dadospessoaisnfse dpt on dpt.id = dec.dadospessoaistomador_id " +
            "  left join notafiscal nf on nf.declaracaoprestacaoservico_id = dec.id " +
            "  left join servicodeclarado sd on sd.declaracaoprestacaoservico_id = dec.id " +
            "  inner join usuarionotapremiada un on un.id = c.usuario_id " +
            "  inner join dadospessoaisnfse dpun on dpun.id = un.dadospessoais_id " +
            "  inner join termousonotapremiada tunp on tunp.usuario_id = un.id" +
            "  inner join termouso t on t.id = tunp.termouso_id ";
        String where = " where t.id = (select max(ult_t.id) " +
            "                  from termouso ult_t " +
            "               where ult_t.sistema = :sistema " +
            "                 and ult_t.iniciovigencia <= :fim_emissao) " +
            "   and tunp.dataaceite <= :fim_emissao " +
            "   and un.participandoprograma = 1 " +
            "   and un.ativo = 1 " +
            "   and c.campanha_id = :id_campanha " +
            "    and coalesce(nf.emissao, sd.emissao) between :inicio_emissao and :fim_emissao " +
            "    and (c.premio_id is null or sorteio.numero >= :numero_sorteio) ";

        Map<String, Object> parametros = Maps.newHashMap();
        parametros.put("sistema", Sistema.NOTA_PREMIADA.name());
        parametros.put("id_campanha", filtroCupons.getIdCampanha());
        parametros.put("inicio_emissao", DataUtil.dataSemHorario(filtroCupons.getInicioEmissaoNotaFiscal()));
        parametros.put("fim_emissao", DataUtil.dataSemHorario(filtroCupons.getFimEmissaoNotaFiscal()));
        parametros.put("numero_sorteio", filtroCupons.getNumeroSorteio());
        if (!Strings.isNullOrEmpty(filtroCupons.getNumeroCupom())) {
            where += " and c.numero = :numero_cupom ";
            parametros.put("numero_cupom", filtroCupons.getNumeroCupom());
        }
        if (filtroCupons.getEmissaoNfse() != null) {
            where += " and trunc(coalesce(nf.emissao, sd.emissao)) = :emissao_nota ";
            parametros.put("emissao_nota", DataUtil.dataSemHorario(filtroCupons.getEmissaoNfse()));
        }
        if (!Strings.isNullOrEmpty(filtroCupons.getNumeroNfse())) {
            where += " and coalesce(nf.numero, sd.numero) = :numero_nota ";
            parametros.put("numero_nota", filtroCupons.getNumeroNfse());
        }
        if (!Strings.isNullOrEmpty(filtroCupons.getCpfCnpjPrestador())) {
            where += " and dpp.cpfcnpj = :cpfcnpj_prestador ";
            parametros.put("cpfcnpj_prestador", StringUtil.retornaApenasNumeros(filtroCupons.getCpfCnpjPrestador()));
        }
        if (!Strings.isNullOrEmpty(filtroCupons.getNomePrestador())) {
            where += " and trim(lower(dpp.nomerazaosocial)) like :nome_prestador ";
            parametros.put("nome_prestador", "%" + filtroCupons.getNomePrestador().trim().toLowerCase() + "%");
        }
        if (!Strings.isNullOrEmpty(filtroCupons.getCpfCnpjTomador())) {
            where += " and dpt.cpfcnpj = :cpfcnpj_tomador ";
            parametros.put("cpfcnpj_tomador", StringUtil.retornaApenasNumeros(filtroCupons.getCpfCnpjTomador()));
        }
        if (!Strings.isNullOrEmpty(filtroCupons.getNomeTomador())) {
            where += " and trim(lower(dpt.nomerazaosocial)) like :nome_tomador ";
            parametros.put("nome_tomador", "%" + filtroCupons.getNomeTomador().trim().toLowerCase() + "%");
        }
        if (Boolean.TRUE.equals(filtroCupons.getPremiado())) {
            where += " and premio.id is not null ";
        }
        Query query = em.createNativeQuery(select + from + where);
        for (String parametro : parametros.keySet()) {
            query.setParameter(parametro, parametros.get(parametro));
        }
        query.setFirstResult(filtroCupons.getIndexInicialDaPesquisa());
        query.setMaxResults(filtroCupons.getQuantidadePorPagina());

        filtroCupons.setCupons(new ArrayList());
        List resultList = query.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : (List<Object[]>) resultList) {
                DetalheCupomSorteioNfseDTO dto = new DetalheCupomSorteioNfseDTO();
                dto.setId(((Number) obj[0]).longValue());
                dto.setNumeroCupom((String) obj[1]);
                dto.setPremiado(obj[2] != null && ((BigDecimal) obj[2]).intValue() == 1);
                dto.setNumeroNotaFiscal(((Number) obj[3]).intValue());
                dto.setEmissaoNotaFiscal((Date) obj[4]);
                dto.setPrestador((String) obj[5]);
                dto.setTomador((String) obj[6]);

                filtroCupons.getCupons().add(dto);
            }
        }

        filtroCupons.setQuantidadeTotal(0);
        Query queryCount = em.createNativeQuery("select count(1) " + from + where);
        for (String parametro : parametros.keySet()) {
            queryCount.setParameter(parametro, parametros.get(parametro));
        }
        List resultListCount = queryCount.getResultList();
        if (resultListCount != null && !resultListCount.isEmpty()) {
            filtroCupons.setQuantidadeTotal(((Number) resultListCount.get(0)).intValue());
        }

        return filtroCupons;
    }

    @Override
    public SorteioNfse salvarRetornando(SorteioNfse sorteio) {
        if (sorteio.getNumero() == null) {
            sorteio.setNumero(atribuirNovoNumeroSorteio(sorteio.getCampanha().getId()));
        }
        return super.salvarRetornando(sorteio);
    }

    private int atribuirNovoNumeroSorteio(Long idCampanha) {
        String sql = " select max(numero) from sorteionfse where campanha_id = :idCampanha ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCampanha", idCampanha);
        BigDecimal result = (BigDecimal) q.getResultList().get(0);
        if (result != null) {
            return result.intValue() + 1;
        } else {
            return 1;
        }
    }

    private List<CupomCampanhaNfse> buscarCuponsPremiados(SorteioNfse sorteio, String numeroSorteado) {
        List<CupomCampanhaNfse> cuponsPremiados = Lists.newArrayList();
        String sqlPrincipal = " select cupom.* " +
            "   from cupomcampanhanfse cupom " +
            "  inner join usuarionotapremiada unp on unp.id = cupom.usuario_id " +
            "  inner join termousonotapremiada tunp on tunp.usuario_id = unp.id " +
            "  inner join termouso t on t.id = tunp.termouso_id " +
            "  inner join declaracaoprestacaoservico dec on dec.id = cupom.declaracao_id " +
            "  inner join dadospessoaisnfse dpt on dpt.id = dec.dadospessoaistomador_id " +
            "  left join notafiscal nf on nf.declaracaoprestacaoservico_id = dec.id " +
            "  left join servicodeclarado sd on sd.declaracaoprestacaoservico_id = dec.id " +
            "where t.id = (select max(ult_t.id) " +
            "                 from termouso ult_t " +
            "              where ult_t.sistema = :sistema" +
            "                and ult_t.iniciovigencia <= :fim_emissao) " +
            "  and tunp.dataaceite <= :fim_emissao " +
            "  and unp.participandoprograma = 1 " +
            "  and unp.ativo = 1 " +
            "  and cupom.campanha_id = :id_campanha " +
            "  and not exists (select 1 " +
            "                     from bloqueiocpfcampanhanfse b " +
            "                    inner join pessoafisica pf on pf.id = b.pessoafisica_id " +
            "                  where b.campanha_id = cupom.campanha_id " +
            "                    and regexp_replace(pf.cpf, '[^0-9]', '') = regexp_replace(dpt.cpfcnpj, '[^0-9]', '')) " +
            "  and coalesce(nf.emissao, sd.emissao) between :inicio_emissao and :fim_emissao " +
            "  and cupom.premio_id is null ";
        String complemento = "  and to_number(cupom.numero) >= :numero_sorteado " +
            " order by cupom.numero asc ";

        Query query = em.createNativeQuery(sqlPrincipal + complemento, CupomCampanhaNfse.class);
        query.setParameter("id_campanha", sorteio.getCampanha().getId());
        query.setParameter("inicio_emissao", DataUtil.dataSemHorario(sorteio.getInicioEmissaoNotaFiscal()));
        query.setParameter("fim_emissao", DataUtil.dataSemHorario(sorteio.getFimEmissaoNotaFiscal()));
        query.setParameter("numero_sorteado", new Integer(numeroSorteado));
        query.setParameter("sistema", Sistema.NOTA_PREMIADA.name());
        query.setMaxResults(sorteio.getQuantidadeCuponsPremiacao());
        List<CupomCampanhaNfse> cuponsMaioresNumeroSorteado = query.getResultList();
        if (cuponsMaioresNumeroSorteado != null && !cuponsMaioresNumeroSorteado.isEmpty()) {
            for (CupomCampanhaNfse cupom : cuponsMaioresNumeroSorteado) {
                cuponsPremiados.add(cupom);
            }
        }
        if (cuponsPremiados.size() < sorteio.getQuantidadeCuponsPremiacao()) {
            complemento = "  and to_number(cupom.numero) < :numero_sorteado " +
                "order by cupom.numero asc ";
            query = em.createNativeQuery(sqlPrincipal + complemento, CupomCampanhaNfse.class);
            query.setParameter("id_campanha", sorteio.getCampanha().getId());
            query.setParameter("inicio_emissao", DataUtil.dataSemHorario(sorteio.getInicioEmissaoNotaFiscal()));
            query.setParameter("fim_emissao", DataUtil.dataSemHorario(sorteio.getFimEmissaoNotaFiscal()));
            query.setParameter("numero_sorteado", new Integer(numeroSorteado));
            query.setParameter("sistema", Sistema.NOTA_PREMIADA.name());
            query.setMaxResults(sorteio.getQuantidadeCuponsPremiacao() - cuponsPremiados.size());
            List<CupomCampanhaNfse> cuponsDesdePrimeiroCupom = query.getResultList();
            if (cuponsDesdePrimeiroCupom != null && !cuponsDesdePrimeiroCupom.isEmpty()) {
                for (CupomCampanhaNfse cupom : cuponsDesdePrimeiroCupom) {
                    cuponsPremiados.add(cupom);
                }
            }
        }
        if (cuponsPremiados.size() < sorteio.getQuantidadeCuponsPremiacao()) {
            throw new ValidacaoException("Cupons insuficiêntes para a quantidade de prêmios.");
        }
        return cuponsPremiados;
    }

    public List<ResultadoSorteioDTO> buscarResultadosSorteio(SorteioNfse sorteio,
                                                             String numeroSorteado) {
        List<ResultadoSorteioDTO> resultadosSorteio = Lists.newArrayList();
        sorteio = recuperar(sorteio.getId());
        Iterator<CupomCampanhaNfse> iteratorCuponsPremiados = buscarCuponsPremiados(sorteio, numeroSorteado).iterator();
        Collections.sort(sorteio.getPremios(), new Comparator<PremioSorteioNfse>() {
            @Override
            public int compare(PremioSorteioNfse o1, PremioSorteioNfse o2) {
                return o1.getSequencia().compareTo(o2.getSequencia());
            }
        });
        for (PremioSorteioNfse premio : sorteio.getPremios()) {
            Integer quantidade = premio.getQuantidade();
            while (quantidade > 0) {
                if (iteratorCuponsPremiados.hasNext()) {
                    CupomCampanhaNfse cupomPremiado = iteratorCuponsPremiados.next();
                    ResultadoSorteioDTO resultadoSorteio = new ResultadoSorteioDTO();
                    resultadoSorteio.setIdPremio(premio.getId());
                    resultadoSorteio.setSequenciaPremio(premio.getSequencia());
                    resultadoSorteio.setDescricaoPremio(premio.getDescricao());
                    resultadoSorteio.setIdCupom(cupomPremiado.getId());
                    resultadoSorteio.setNumeroCupom(cupomPremiado.getNumero());
                    resultadosSorteio.add(resultadoSorteio);
                    iteratorCuponsPremiados.remove();
                }
                quantidade--;
            }
        }
        return resultadosSorteio;
    }

    public SorteioNfse confirmarSorteio(SorteioNfse sorteio, List<ResultadoSorteioDTO> resultadosSorteio) {
        for (ResultadoSorteioDTO resultadoSorteio : resultadosSorteio) {
            PremioSorteioNfse premioSorteioNfse = em.find(PremioSorteioNfse.class, resultadoSorteio.getIdPremio());
            CupomCampanhaNfse cupomCampanhaNfse = em.find(CupomCampanhaNfse.class, resultadoSorteio.getIdCupom());
            cupomCampanhaNfse.setPremio(premioSorteioNfse);
            em.merge(cupomCampanhaNfse);
        }
        sorteio.setSituacao(SituacaoSorteioNfse.REALIZADO);
        return salvarRetornando(sorteio);
    }
}
