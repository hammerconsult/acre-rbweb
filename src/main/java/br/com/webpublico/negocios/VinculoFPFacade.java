/*
 * Codigo gerado automaticamente em Thu Aug 04 09:41:08 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.BarCode;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.ItemConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidadesauxiliares.BeneficiarioProvaDeVidaDTO;
import br.com.webpublico.entidadesauxiliares.ConsultaBeneficiarioProvaDeVidaVO;
import br.com.webpublico.entidadesauxiliares.HoleriteBBAuxiliar;
import br.com.webpublico.entidadesauxiliares.rh.VwFalta;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.TipoAutorizacaoRH;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.esocial.service.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.rh.esocial.LogErroEnvioEventoFacade;
import br.com.webpublico.relatoriofacade.rh.RelatorioTempoDeServicoFacade;
import br.com.webpublico.util.*;
import br.com.webpublico.ws.model.*;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.faces.application.FacesMessage;
import javax.persistence.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class VinculoFPFacade extends AbstractFacade<VinculoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    private ESocialService eSocialService;
    private S2300Service s2300Service;
    private S2306Service s2306Service;
    private S2400Service s2400Service;
    private S2405Service s2405Service;
    private S2410Service s2410Service;

    private S2416Service s2416Service;
    private S2420Service s2420Service;
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;
    @EJB
    private ReintegracaoFacade reintegracaoFacade;
    @EJB
    private ProvimentoReversaoFacade provimentoReversaoFacade;
    @EJB
    PessoaFacade pessoaFacade;
    @EJB
    private RelatorioTempoDeServicoFacade relatorioTempoDeServicoFacade;
    @EJB
    private AverbacaoTempoServicoFacade averbacaoTempoServicoFacade;
    @EJB
    private FaltasFacade faltasFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    @EJB
    private LogErroEnvioEventoFacade logErroEnvioEventoFacade;


    public VinculoFPFacade() {
        super(VinculoFP.class);
    }

    @PostConstruct
    private void init() {
        eSocialService = (ESocialService) Util.getSpringBeanPeloNome("eSocialService");
        s2300Service = (S2300Service) Util.getSpringBeanPeloNome("s2300Service");
        s2306Service = (S2306Service) Util.getSpringBeanPeloNome("s2306Service");
        s2410Service = (S2410Service) Util.getSpringBeanPeloNome("s2410Service");
        s2416Service = (S2416Service) Util.getSpringBeanPeloNome("s2416Service");
        s2400Service = (S2400Service) Util.getSpringBeanPeloNome("s2400Service");
        s2405Service = (S2405Service) Util.getSpringBeanPeloNome("s2405Service");
        s2420Service = (S2420Service) Util.getSpringBeanPeloNome("s2420Service");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public VinculoFP recuperar(Object id) {
        VinculoFP vinculoFP = em.find(VinculoFP.class, id);
        vinculoFP.getLotacaoFuncionals().size();
        vinculoFP.getAssociacaosVinculosFPs().size();
        vinculoFP.getItensValorPrevidencia().size();
        vinculoFP.getFichasFinanceiraFP().size();
        vinculoFP.getLotacaoOutrosVinculos().size();
        vinculoFP.getOpcaoSalarialVinculoFP().size();
        vinculoFP.getRecursosDoVinculoFP().size();
        vinculoFP.getSindicatosVinculosFPs().size();
        return vinculoFP;
    }

    public VinculoFP recuperarComSindicato(Object id) {
        VinculoFP vinculoFP = em.find(VinculoFP.class, id);
        Hibernate.initialize(vinculoFP.getSindicatosVinculosFPs());
        return vinculoFP;
    }

    public VinculoFP recuperarVinculoFPComDependenciaLotacaoFuncional(Object id) {
        VinculoFP vinculoFP = em.find(VinculoFP.class, id);
        vinculoFP.getLotacaoFuncionals().size();
        vinculoFP.getRecursosDoVinculoFP().size();
        if (vinculoFP.getLotacaoFuncionalVigente() != null && vinculoFP.getLotacaoFuncionalVigente().getHorarioContratoFP() != null) {
            vinculoFP.getLotacaoFuncionalVigente().getHorarioContratoFP().getLocalTrabalho().size();
        }

        return vinculoFP;
    }

    public VinculoFP recuperarSimples(Object id) {
        return em.find(VinculoFP.class, id);
    }

    public List<VinculoFPDTOHistorico> buscarHistoricoLotacao(Long id, String matricula, String cpf) {
        if (id == null && matricula == null && cpf == null) {
            return null;
        }
        String sql = getQueryBaseViculoFPPortal();
        if (id != null) {
            sql += " where obj.id = :id ";
        } else if (matricula != null) {
            sql += " where mat.matricula = :matricula ";
        } else if (cpf != null) {
            sql += " where replace(replace(pf.cpf,'.',''),'-','') = :cpf ";
        }

        Query q = em.createNativeQuery(sql);
        if (id != null) {
            q.setParameter("id", id);
        } else if (matricula != null) {
            q.setParameter("matricula", matricula);
        } else if (cpf != null) {
            q.setParameter("cpf", StringUtil.retornaApenasNumeros(cpf));
        }

        List<Object[]> registros = q.getResultList();
        List<VinculoFPDTOHistorico> vinculos = Lists.newLinkedList();

        if (registros != null && !registros.isEmpty()) {
            for (Object[] registro : registros) {
                WSVinculoFP wsVinculoFP = convertObjectToWsVinculoFPHistorico(registro);
                VinculoFPDTOHistorico vinculoHistorico = (VinculoFPDTOHistorico) wsVinculoFP;
                vinculoHistorico.setHistoricoLotacao(buscarHistoricoFuncionalServidor(wsVinculoFP.getId()));
                vinculos.add(vinculoHistorico);
            }
        }
        return vinculos;
    }

    private List<HistoricoLotacaoDTO> buscarHistoricoFuncionalServidor(Long id) {
        List<HistoricoLotacaoDTO> historicos = Lists.newLinkedList();
        String sql = "select " +
            "coalesce(vw.codigo, vwAntiga.codigo)                 as codigo, " +
            "coalesce(vw.descricao, vwAntiga.descricao)           as descricao, " +
            "lotacao.INICIOVIGENCIA                               as inicioVigencia, " +
            "lotacao.FINALVIGENCIA                                as finalVigencia, " +
            "coalesce(vw.id, vwAntiga.id)                         as id, " +
            "coalesce(vw.SUBORDINADA_ID, vwAntiga.SUBORDINADA_ID) as SUBORDINADA_ID, " +
            "coalesce(vw.SUPERIOR_ID, vwAntiga.SUPERIOR_ID)       as SUPERIOR_ID, " +
            "coalesce(vw.inicioVigencia, vwAntiga.inicioVigencia) as inicioHierarquia, " +
            "case " +
            "    when lotacao.FINALVIGENCIA is not null then coalesce(vw.fimVigencia, vwAntiga.fimVigencia) " +
            "    else vw.fimVigencia end                          as fimVigencia, " +
            "    coalesce(ho.NIVELNAENTIDADE, hoAntiga.NIVELNAENTIDADE) as NIVELNAENTIDADE, " +
            "    (select vwOrgao.id from VWHIERARQUIAADMINISTRATIVA vwOrgao inner join hierarquiaOrganizacional hoOrgao on hoOrgao.id = vwOrgao.id  where substr(vwOrgao.codigo, 0, 6) = substr(vw.codigo, 0, 6) and hoOrgao.NIVELNAENTIDADE = 2 and current_date between vwOrgao.INICIOVIGENCIA and coalesce(vwOrgao.fimVigencia, current_date)), " +
            "    (select vwOrgao.CODIGO from VWHIERARQUIAADMINISTRATIVA vwOrgao inner join hierarquiaOrganizacional hoOrgao on hoOrgao.id = vwOrgao.id  where substr(vwOrgao.codigo, 0, 6) = substr(vw.codigo, 0, 6) and hoOrgao.NIVELNAENTIDADE = 2 and current_date between vwOrgao.INICIOVIGENCIA and coalesce(vwOrgao.fimVigencia, current_date)), " +
            "    (select vwOrgao.descricao from VWHIERARQUIAADMINISTRATIVA vwOrgao inner join hierarquiaOrganizacional hoOrgao on hoOrgao.id = vwOrgao.id  where substr(vwOrgao.codigo, 0, 6) = substr(vw.codigo, 0, 6) and hoOrgao.NIVELNAENTIDADE = 2 and current_date between vwOrgao.INICIOVIGENCIA and coalesce(vwOrgao.fimVigencia, current_date)) " +
            " from lotacaofuncional lotacao " +
            "         inner join unidadeorganizacional un on lotacao.UNIDADEORGANIZACIONAL_ID = un.ID " +
            "         left join VWHIERARQUIAADMINISTRATIVA vw on vw.SUBORDINADA_ID = un.id " +
            "    and current_date between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, current_date) " +
            "    inner join HIERARQUIAORGANIZACIONAL ho on vw.id = ho.id " +
            "         left join VWHIERARQUIAADMINISTRATIVA vwAntiga on vwAntiga.SUBORDINADA_ID = un.id " +
            "    and " +
            "                                                          lotacao.INICIOVIGENCIA between vwAntiga.INICIOVIGENCIA and coalesce(vwAntiga.FIMVIGENCIA, lotacao.INICIOVIGENCIA)  " +
            "                                                            " +
            "    inner join HIERARQUIAORGANIZACIONAL hoAntiga on vwAntiga.id = hoAntiga.id " +
            "where lotacao.VINCULOFP_ID = :id " +
            "order by lotacao.INICIOVIGENCIA desc ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("id", id);
        List<Object[]> registros = q.getResultList();

        if (registros != null && !registros.isEmpty()) {
            for (Object[] registro : registros) {
                HistoricoLotacaoDTO detail = new HistoricoLotacaoDTO();
                detail.setCodigo((String) registro[0]);
                detail.setDescricao((String) registro[1]);
                detail.setInicioVigencia((Date) registro[2]);
                detail.setFinalVigencia((Date) registro[3]);
                detail.setHierarquiaId(((BigDecimal) registro[4]).longValue());
                BigDecimal subordinadaId = (BigDecimal) registro[5];
                detail.setSubordinadaId(subordinadaId != null ? subordinadaId.longValue() : null);
                BigDecimal superiorId = (BigDecimal) registro[6];
                detail.setSuperiorId(superiorId != null ? superiorId.longValue() : null);
                detail.setInicioVigenciaHierarquia((Date) registro[7]);
                detail.setFinalVigenciaHierarquia((Date) registro[8]);
                BigDecimal nivelEntidade = (BigDecimal) registro[9];
                detail.setNivelEntidade(nivelEntidade != null ? nivelEntidade.intValue() : null);
                detail.setOrgao(convertObjectToOrgaoDto(registro));
                historicos.add(detail);
            }
        }
        return historicos;

    }

    private OrgaoDTO convertObjectToOrgaoDto(Object[] registro) {
        OrgaoDTO orgaoDTO = new OrgaoDTO();
        BigDecimal idOrgao = (BigDecimal) registro[10];
        if (idOrgao != null) {
            orgaoDTO.setId(idOrgao.longValue());
        }
        orgaoDTO.setCodigo((String) registro[11]);
        orgaoDTO.setNome((String) registro[12]);
        return orgaoDTO;
    }


    public VinculoFPDTODetalhe buscarDetalhesServidor(Object id, Boolean filtrarUsuarioAtivo) {
        String sql =
            "select v.id, " +
                "       mat.MATRICULA, " +
                "       v.NUMERO, " +
                "       pf.NOME, " +
                "       coalesce(vw.DESCRICAO, un.descricao)             as orgao, " +
                "       (select coalesce(vho.DESCRICAO, u.descricao) " +
                "        from LOTACAOFUNCIONAL lot " +
                "                 inner join unidadeorganizacional u on lot.UNIDADEORGANIZACIONAL_ID = u.ID " +
                "                 inner join VWHIERARQUIAADMINISTRATIVA vho on vho.subordinada_id = u.id and current_date between vho.inicioVigencia and coalesce(vho.fimVigencia, current_date) " +
                "        where lot.VINCULOFP_ID = v.id " +
                "          and lot.INICIOVIGENCIA = " +
                "              (select max(l.INICIOVIGENCIA) from lotacaofuncional l where l.VINCULOFP_ID = lot.VINCULOFP_ID) " +
                "          and rownum = 1)       as lotacao, " +
                "       FORMATACPFCNPJ(pf.CPF)   as cpf, " +
                "       c.NOME || ' - ' || uf.UF as naturalidade, " +
                "       pf.DATANASCIMENTO, " +
                "       (select rg.NUMERO " +
                "        from DOCUMENTOPESSOAL docp " +
                "                 inner join rg rg on docp.ID = rg.ID " +
                "        where docp.PESSOAFISICA_ID = pf.id " +
                "          and ROWNUM = 1)       as rg, " +
                "       (select titulo.NUMERO " +
                "        from DOCUMENTOPESSOAL docp " +
                "                 inner join TITULOELEITOR titulo on docp.ID = titulo.ID " +
                "        where docp.PESSOAFISICA_ID = pf.id " +
                "          and ROWNUM = 1)       as tituloEleitor, " +
                "       pf.MAE, " +
                "       pf.PAI, " +
                "       (select config.URLPORTALSERVIDORHOMOLOGACAO " +
                "        from configuracaorh config " +
                "        where current_date between config.INICIOVIGENCIA and coalesce(config.FINALVIGENCIA, current_date)) as urlHomologacao, " +
                "       (select config.URLPORTALSERVIDORPRODUCAO " +
                "        from configuracaorh config " +
                "        where current_date between config.INICIOVIGENCIA and coalesce(config.FINALVIGENCIA, current_date)) as urlProducao, " +
                "        v.inicioVigencia as inicioVigencia, " +
                "        pf.id as idPessoa, " +
                "        cargo.id as idCargo, " +
                "        cargo.codigoDoCargo as codigoDoCargo, " +
                "        cargo.descricao as descricaoCargo, " +
                "        (select vho.id " +
                "        from LOTACAOFUNCIONAL lot " +
                "                 inner join unidadeorganizacional u on lot.UNIDADEORGANIZACIONAL_ID = u.ID " +
                "                 inner join VWHIERARQUIAADMINISTRATIVA vho on vho.subordinada_id = u.id and current_date between vho.inicioVigencia and coalesce(vho.fimVigencia, current_date) " +
                "        where lot.VINCULOFP_ID = v.id " +
                "          and lot.INICIOVIGENCIA = " +
                "              (select max(l.INICIOVIGENCIA) from lotacaofuncional l where l.VINCULOFP_ID = lot.VINCULOFP_ID) " +
                "          and rownum = 1) as idLotacao, " +
                "       (select vho.codigo " +
                "        from LOTACAOFUNCIONAL lot " +
                "                 inner join unidadeorganizacional u on lot.UNIDADEORGANIZACIONAL_ID = u.ID " +
                "                 inner join VWHIERARQUIAADMINISTRATIVA vho on vho.subordinada_id = u.id and current_date between vho.inicioVigencia and coalesce(vho.fimVigencia, current_date) " +
                "        where lot.VINCULOFP_ID = v.id " +
                "          and lot.INICIOVIGENCIA = " +
                "              (select max(l.INICIOVIGENCIA) from lotacaofuncional l where l.VINCULOFP_ID = lot.VINCULOFP_ID) " +
                "          and rownum = 1) as codigoLotacao, " +
                "        moda.descricao as situacaoVinculo, " +
                "        coalesce((select sc.DESCRICAO " +
                "        from situacaocontratofp scfp " +
                "                inner join situacaofuncional sc on scfp.SITUACAOFUNCIONAL_ID = sc.ID " +
                "       where :dataAtual between trunc(scfp.INICIOVIGENCIA) and coalesce(trunc(scfp.FINALVIGENCIA), :dataAtual) " +
                "         and scfp.INICIOVIGENCIA = " +
                "             (select max(stfp.inicioVigencia) from SITUACAOCONTRATOFP stfp where stfp.CONTRATOFP_ID = v.id) " +
                "         and scfp.CONTRATOFP_ID = v.id " +
                "         and rownum <= 1), (select sc.DESCRICAO " +
                "                            from situacaocontratofp scfp " +
                "                                     inner join situacaofuncional sc on scfp.SITUACAOFUNCIONAL_ID = sc.ID " +
                "                            where :dataAtual between trunc(scfp.INICIOVIGENCIA) and coalesce(trunc(scfp.FINALVIGENCIA), :dataAtual) " +
                "                              and scfp.CONTRATOFP_ID = v.id " +
                "                              and rownum <= 1))                                                  as situacaoFuncional, " +
                "       jornada.HORASSEMANAL, " +
                "       jornada.HORASMENSAL " +
                "from vinculofp v " +
                "         inner join matriculafp mat on v.MATRICULAFP_ID = mat.ID " +
                "         inner join pessoafisica pf on mat.PESSOA_ID = pf.ID " +
                "         left join contratofp c on c.id = v.id " +
                "         left join modalidadecontratofp moda on moda.id = c.modalidadeContratoFP_id" +
                "         left join cargo cargo on cargo.id = c.cargo_id " +
                "         left join JORNADADETRABALHO jornada on c.JORNADADETRABALHO_ID = jornada.ID " +
                "         left join cidade c on pf.NATURALIDADE_ID = c.ID " +
                "         left join uf uf on c.UF_ID = uf.ID " +
                "         inner join UNIDADEORGANIZACIONAL un on v.UNIDADEORGANIZACIONAL_ID = un.ID " +
                "         inner join VWHIERARQUIAADMINISTRATIVA vw on vw.subordinada_id = un.id and current_date between vw.inicioVigencia and coalesce(vw.fimVigencia, current_date)  " +
                "where v.id = :id ";
        if (filtrarUsuarioAtivo) {
            sql += " and pf.id not in(select us.pessoafisica_id from usuariosistema us where us.pessoafisica_id = pf.id and coalesce(us.expira,0) = 1) ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("id", id);
        q.setParameter("dataAtual", Util.getDataHoraMinutoSegundoZerado(new Date()));
        List<Object[]> registros = q.getResultList();
        if (registros != null && !registros.isEmpty()) {
            for (Object[] registro : registros) {
                VinculoFPDTODetalhe detail = new VinculoFPDTODetalhe();
                BigDecimal vinduloId = (BigDecimal) registro[0];
                detail.setId(vinduloId.longValue());
                detail.setMatricula((String) registro[1]);
                detail.setNumero((String) registro[2]);
                detail.setNome((String) registro[3]);
                detail.setOrgao((String) registro[4]);
                detail.setLotacao((String) registro[5]);
                detail.setCpf((String) registro[6]);
                detail.setNaturalidade((String) registro[7]);
                detail.setDataNascimeto((Date) registro[8]);
                detail.setRg((String) registro[9]);
                detail.setTituloEleitor((String) registro[10]);
                detail.setMae((String) registro[11]);
                detail.setPai((String) registro[12]);
                String urlHomologacao = (String) registro[13];
                String urlProducao = (String) registro[14];
                detail.setQrCode(getQrCodeCracha(detail, urlHomologacao, urlProducao));
                Date inivioVigencia = (Date) registro[15];
                detail.setAdmissao(inivioVigencia);
                detail.setIdPessoa((BigDecimal) registro[16]);
                Arquivo arquivo = pessoaFacade.recuperaFotoPessoaFisica(((BigDecimal) registro[16]).longValue());
                if (arquivo != null) {
                    String fotoBase64 = arquivo.montarConteudoBase64();
                    detail.setFoto(fotoBase64);
                }

                detail.setIdCargo((BigDecimal) registro[17]);
                detail.setCodigoCargo((String) registro[18]);
                detail.setCargo((String) registro[19]);

                detail.setIdLotacao((BigDecimal) registro[20]);
                detail.setCodigoLotacao((String) registro[21]);
                detail.setSituacaoVinculo((String) registro[22]);
                detail.setSituacaoFuncional((String) registro[23]);

                detail.setHorasSemanais((BigDecimal) registro[24]);
                detail.setHorasMensais((BigDecimal) registro[25]);
                detail.setHorarioTrabalho(buscarHorarioTrabalhoServidor(id));

                return detail;
            }
        }
        return null;
    }

    public HorarioTrabalhoDTO buscarHorarioTrabalhoServidor(Object id) {
        String sql =
            " select horarioTrabalho.id, " +
                "  to_char(horarioTrabalho.ENTRADA,'HH24:MI'), " +
                "  to_char(horarioTrabalho.INTERVALO,'HH24:MI'), " +
                "  to_char(horarioTrabalho.RETORNOINTERVALO,'HH24:MI'), " +
                "  to_char(horarioTrabalho.SAIDA,'HH24:MI')  " +
                "        from LOTACAOFUNCIONAL lot " +
                "                 inner join HORARIOCONTRATOFP HorarioContrato on lot.HORARIOCONTRATOFP_ID = HorarioContrato.ID " +
                "                 inner join HORARIODETRABALHO horarioTrabalho on HorarioContrato.HORARIODETRABALHO_ID = horarioTrabalho.ID " +
                "        where lot.VINCULOFP_ID = :id " +
                "          and lot.INICIOVIGENCIA = " +
                "              (select max(l.INICIOVIGENCIA) from lotacaofuncional l where l.VINCULOFP_ID = lot.VINCULOFP_ID) " +
                "          and rownum = 1 ";


        Query q = em.createNativeQuery(sql);
        q.setParameter("id", id);
        List<Object[]> registros = q.getResultList();
        if (registros != null && !registros.isEmpty()) {
            for (Object[] registro : registros) {
                HorarioTrabalhoDTO detail = new HorarioTrabalhoDTO();
                detail.setIdHorarioTrabalho((BigDecimal) registro[0]);

                detail.setHorarioEntrada((String) registro[1]);
                detail.setHorarioSaidaIntervalo((String) registro[2]);
                detail.setHorarioRetornoIntervalo((String) registro[3]);
                detail.setHorarioSaida((String) registro[4]);
                return detail;
            }
        }
        return null;
    }

    private String getQrCodeCracha(VinculoFPDTODetalhe detail, String urlHomologacao, String urlProducao) {
        String urlValidacaoQrCode = "";
        if (SistemaFacade.PerfilApp.PROD.equals(sistemaFacade.getPerfilAplicacao())) {
            urlValidacaoQrCode = urlProducao != null && urlProducao.endsWith("/") ? urlProducao.substring(0, urlProducao.length() - 1) : urlProducao;
        } else {
            urlValidacaoQrCode = urlHomologacao != null && urlHomologacao.endsWith("/") ? urlHomologacao.substring(0, urlHomologacao.length() - 1) : urlHomologacao;
        }
        try {
            String urlPortal = urlValidacaoQrCode + "/autenticidade-de-documentos/cracha/" + detail.getId() + "/";
            return BarCode.generateBase64QRCodePng(urlPortal, 350, 350);
        } catch (IOException e) {
            logger.error("Erro ao gerar gr code em buscarDetalhesServidor: ", e);
        }
        return "NÃO FOI POSSIVEL GERAR O QR PARA O VINCULO";

    }

    public VinculoFP recuperarComConfiguracaoCargoAndLotacaoFuncional(Object id) {
        VinculoFP vinculo = em.find(VinculoFP.class, id);
        vinculo.getLotacaoFuncionals().size();
        if (vinculo.getCargo() != null) {
            vinculo.getCargo().getItemConfiguracaoCargoEventoFP().size();
        }
        return vinculo;
    }

    public String retornaCodigo(MatriculaFP matricula) {
        String num;
        String hql = "select a from VinculoFP a "
            + "where a.numero = (select max(b.numero) from VinculoFP b where b.matriculaFP = :matricula)";
        Query query = em.createQuery(hql);
        query.setMaxResults(1);
        query.setParameter("matricula", matricula);
        if (!query.getResultList().isEmpty()) {
            Long l = Long.parseLong(((VinculoFP) query.getSingleResult()).getNumero()) + 1;
            num = String.valueOf(l);
        } else {
            return "1";
        }
        return num;
    }

    public List<VinculoFP> listaTodosVinculos(String filtro) {
        ArrayList<VinculoFP> retorno = new ArrayList<>();

        String hql = "from BeneficioFP bfp where lower(bfp.modalidadeBeneficioFP.descricao) like :filtro";
        Query q;
        q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        q.setMaxResults(50);
        retorno.addAll(q.getResultList());
        q = getEntityManager().createQuery("from VinculoFP obj where lower(obj.matriculaFP.matricula) like :filtro "
            + " and obj.id not in (" + hql + ")");
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        q.setMaxResults(50);
        retorno.addAll(q.getResultList());

        return retorno;
    }

    public List<VinculoFP> listaTodosVinculos1(String filtro) {
        Query q = em.createQuery("from VinculoFP obj "
            + " where (lower(obj.matriculaFP.pessoa.nome) like :filtro)"
            + "or (lower(obj.matriculaFP.matricula) like :filtro)");
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<String> buscarContratosParaMatricula(MatriculaFP matriculaFP) {
        String sql = "select case " +
            "            when (select apo.id from APOSENTADORIA apo where apo.ID = v.id) is not null then 'APOSENTADO(A)' " +
            "        when (select pen.id from PENSIONISTA pen where pen.ID = v.id) is not null then 'PENSIONISTA' " +
            "           else v.NUMERO end vinculo " +
            "        from Vinculofp v " +
            "        inner join MatriculaFP mat on v.MATRICULAFP_ID = mat.ID " +
            "        where mat.id = :matricula " +
            "        order by vinculo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("matricula", matriculaFP.getId());
        return q.getResultList();
    }

    public List<VinculoFP> buscarVinculosParaMatricula(String matriculaFP) {
        Query q = em.createQuery("from VinculoFP obj "
            + " where obj.matriculaFP.matricula = :matriculaFP " +
            " order by obj.inicioVigencia ");
        q.setParameter("matriculaFP", matriculaFP);
        return q.getResultList();
    }

    public VinculoFP recuperarVinculoPorMatriculaETipoOrNumero(String matricula, String vinculo) {
        VinculoFP vinculoFP = null;
        if ("APOSENTADO(A)".equals(vinculo)) {
            Query q = em.createQuery("select obj from VinculoFP obj, Aposentadoria c where c.id = obj.id and obj.matriculaFP.matricula = :mat ");
            q.setParameter("mat", matricula);
            List resultList = q.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                vinculoFP = (VinculoFP) resultList.get(0);
            }
        } else if ("PENSIONISTA".equals(vinculo)) {
            Query q = em.createQuery("select obj from VinculoFP obj, Pensionista c where c.id = obj.id and obj.matriculaFP.matricula = :mat ");
            q.setParameter("mat", matricula);
            List resultList = q.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                vinculoFP = (VinculoFP) resultList.get(0);
            }
        } else {
            Query q = em.createQuery("select obj from VinculoFP obj, ContratoFP c where c.id = obj.id and obj.matriculaFP.matricula = :mat and obj.numero = :numero");
            q.setParameter("mat", matricula);
            q.setParameter("numero", vinculo);
            List resultList = q.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                vinculoFP = (VinculoFP) resultList.get(0);
            }
        }
        return vinculoFP;
    }

    public List<VinculoFP> listaTodosVinculosVigentes(String filtro) {
        StringBuilder hql = new StringBuilder();
        hql.append(" select obj from VinculoFP obj");
        hql.append(" where ((lower(obj.matriculaFP.pessoa.nome) like :filtro) or (lower(obj.matriculaFP.matricula) like :filtro))");
        hql.append("   and obj.inicioVigencia <= :data");
        hql.append("   and coalesce(obj.finalVigencia, :data) >= :data");

        Query q = em.createQuery(hql.toString());
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        q.setParameter("data", sistemaFacade.getDataOperacao());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<VinculoFP> listaTodosVinculosQueSaoContratoVigentes(String filtro) {
        StringBuilder hql = new StringBuilder();
        hql.append(" select obj from ContratoFP obj");
        hql.append(" where ((lower(obj.matriculaFP.pessoa.nome) like :filtro) or (lower(obj.matriculaFP.matricula) like :filtro))");
        hql.append("   and obj.inicioVigencia <= :data");
        hql.append("   and coalesce(obj.finalVigencia, :data) >= :data");

        Query q = em.createQuery(hql.toString());
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        q.setParameter("data", UtilRH.getDataOperacao());
        q.setMaxResults(10);
        return q.getResultList();
    }


    public List<VinculoFP> buscarContratosQueNaoEstaoRescisos(String filtro) {
        StringBuilder hql = new StringBuilder();
        hql.append(" select obj from ContratoFP obj");
        hql.append(" where ((lower(obj.matriculaFP.pessoa.nome) like :filtro) or (lower(obj.matriculaFP.matricula) like :filtro))");
        Query q = em.createQuery(hql.toString());
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        q.setMaxResults(50);
        List resultList = q.getResultList();
        List<VinculoFP> retorno = Lists.newArrayList();

        if (!resultList.isEmpty()) {
            for (Object o : resultList) {
                ExoneracaoRescisao exoneracao = exoneracaoRescisaoFacade.recuperarExoneracaoRescisaoPorContratoFP((ContratoFP) o);
                if (exoneracao == null) {
                    retorno.add((VinculoFP) o);
                } else {
                    Reintegracao reintegracao = reintegracaoFacade.recuperarUltimaReintegracao((VinculoFP) o);
                    ProvimentoReversao provimentoReversao = provimentoReversaoFacade.recuperarUltimoProvimentoReversaoDoContratoFP((ContratoFP) o);
                    Aposentadoria aposentadoria = null;
                    if (provimentoReversao != null){
                        aposentadoria = aposentadoriaFacade.recuperarUltimaAposentadoriaPorContratoFP(provimentoReversao.getContratoFP());
                    }
                    if (reintegracao != null && reintegracao.getDataReintegracao().compareTo(exoneracao.getDataRescisao()) >= 0 ||
                        ((provimentoReversao != null && provimentoReversao.getInicioVigencia().compareTo(exoneracao.getDataRescisao()) >= 0) &&
                            (aposentadoria == null || (((ContratoFP) o).getFinalVigencia() != null && aposentadoria.getInicioVigencia().compareTo(((ContratoFP) o).getFinalVigencia()) <0)))) {
                        retorno.add((VinculoFP) o);
                    }

                }

            }
        }
        return retorno;
    }


    public List<VinculoFP> listaTodosVinculosPorPessoa(PessoaFisica pessoa) {
        Query q = em.createQuery("from VinculoFP obj where obj.matriculaFP.pessoa = :pessoa");
        q.setParameter("pessoa", pessoa);
        return q.getResultList();
    }

    public Date recuperarDataMaximaFinalVigenciaContrato(PessoaFisica pessoa) {
        Query q = em.createQuery("select max(obj.finalVigencia) from VinculoFP obj where obj.matriculaFP.pessoa = :pessoa");
        q.setParameter("pessoa", pessoa);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (Date) q.getResultList().get(0);
    }

    public VinculoFP buscarVinculoVigentePorPessoaMesAndAno(PessoaFisica pessoa, Date dataOperacao) {
        Query q = em.createQuery("from VinculoFP obj where obj.matriculaFP.pessoa = :pessoa " +
            " AND TO_CHAR(:dataOperacao,'MM/yyyy') BETWEEN TO_CHAR(obj.inicioVigencia,'MM/yyyy') " +
            "AND coalesce(TO_CHAR(obj.finalVigencia,'MM/yyyy'), TO_CHAR(:dataOperacao,'MM/yyyy'))");
        q.setParameter("pessoa", pessoa);
        q.setParameter("dataOperacao", dataOperacao);
        if (!q.getResultList().isEmpty()) {
            return (VinculoFP) q.getResultList().get(0);
        }
        return null;
    }

    public VinculoFP buscarVinculoVigentePorPessoa(PessoaFisica pessoa, Date dataOperacao) {
        Query q = em.createQuery("from VinculoFP obj where obj.matriculaFP.pessoa = :pessoa and :dataOperacao between obj.inicioVigencia and coalesce(obj.finalVigencia,:dataOperacao)");
        q.setParameter("pessoa", pessoa);
        q.setParameter("dataOperacao", dataOperacao);
        if (!q.getResultList().isEmpty()) {
            return (VinculoFP) q.getResultList().get(0);
        }
        return null;
    }

    public List<VinculoFP> buscarVinculosVigentesPorPessoa(PessoaFisica pessoa, Date dataOperacao) {
        Query q = em.createQuery("from VinculoFP obj where obj.matriculaFP.pessoa = :pessoa and :dataOperacao between obj.inicioVigencia and coalesce(obj.finalVigencia,:dataOperacao)");
        q.setParameter("pessoa", pessoa);
        q.setParameter("dataOperacao", dataOperacao);
        if (!q.getResultList().isEmpty()) {
            return (List<VinculoFP>) q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<VinculoFP> buscarTodosVinculosPorPessoaVigentesNoAno(Long pessoaId, Integer ano) {
        Query q = em.createQuery("from VinculoFP obj where obj.matriculaFP.pessoa.id = :pessoa and :ano between extract(year from obj.inicioVigencia) and coalesce(extract(year from obj.finalVigencia),:ano)   ");
        q.setParameter("pessoa", pessoaId);
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    public VinculoFP buscarVinculoPorPessoaVigenteAno(Long pessoaId, Integer ano) {
        Query q = em.createQuery("from VinculoFP obj where obj.matriculaFP.pessoa.id = :pessoa and :ano between extract(year from obj.inicioVigencia) and coalesce(extract(year from obj.finalVigencia),:ano)   ");
        q.setParameter("pessoa", pessoaId);
        q.setParameter("ano", ano);
        if (!q.getResultList().isEmpty()) {
            return (VinculoFP) q.getResultList().get(0);
        }
        return null;
    }

    public VinculoFP buscarVinculoPorId(Long idVinculo) {
        Query q = em.createQuery("from VinculoFP obj where obj.id = :idVinculo ");
        q.setParameter("idVinculo", idVinculo);
        if (!q.getResultList().isEmpty()) {
            return (VinculoFP) q.getResultList().get(0);
        }
        return null;
    }


    public List<WSVinculoFP> buscarVinculosPorPessoalPortal(Long idPessoa) {
        List<WSVinculoFP> vinculos = Lists.newArrayList();
        Query q = em.createNativeQuery(getQueryBaseViculoFPPortal() +
            "where mat.pessoa_id = :pessoa order by obj.inicioVigencia desc ");
        q.setParameter("pessoa", idPessoa);

        List<Object[]> resultados = q.getResultList();
        for (Object[] resultado : resultados) {
            WSVinculoFP wsVinculoFP = convertObjectToWsVinculoFP(resultado);
            vinculos.add(wsVinculoFP);
        }
        return vinculos;
    }

    private VinculoFPDTOHistorico convertObjectToWsVinculoFPHistorico(Object[] resultado) {
        VinculoFPDTOHistorico wsVinculoFP = new VinculoFPDTOHistorico();
        wsVinculoFP.setId(((BigDecimal) resultado[0]).longValue());
        wsVinculoFP.setMatricula((String) resultado[1]);
        wsVinculoFP.setNumero((String) resultado[2]);
        wsVinculoFP.setTipoVinculo((String) resultado[3]);
        wsVinculoFP.setSituacao((String) resultado[4]);
        return wsVinculoFP;
    }

    private WSVinculoFP convertObjectToWsVinculoFP(Object[] resultado) {
        WSVinculoFP wsVinculoFP = new WSVinculoFP();
        wsVinculoFP.setId(((BigDecimal) resultado[0]).longValue());
        wsVinculoFP.setMatricula((String) resultado[1]);
        wsVinculoFP.setNumero((String) resultado[2]);
        wsVinculoFP.setTipoVinculo((String) resultado[3]);
        wsVinculoFP.setSituacao((String) resultado[4]);
        return wsVinculoFP;
    }

    private String getQueryBaseViculoFPPortal() {
        return " select obj.id, " +
            "       mat.MATRICULA, " +
            "       obj.numero, " +
            "       case " +
            "           when apo.ID is not null then 'APOSENTADORIA' " +
            "           when ben.ID is not null then 'BENEFICIARIO' " +
            "           when pen.ID is not null then 'PENSIONISTA' " +
            "           ELSE 'SERVIDOR' END AS tipoVinculo, " +
            "       case " +
            "           when (select id from EXONERACAORESCISAO ex where ex.VINCULOFP_ID = obj.id and ex.VINCULOFP_ID not in(select rei.CONTRATOFP_ID from REINTEGRACAO rei) and rownum <= 1) is not null or " +
            "                (current_date not between obj.INICIOVIGENCIA and coalesce(obj.FINALVIGENCIA, current_date)) " +
            "               then 'INATIVO' " +
            "           ELSE 'ATIVO' end    as situacao " +
            " from vinculoFP obj " +
            "         inner join matriculafp mat on mat.id = obj.matriculafp_id " +
            "         inner join pessoafisica pf on pf.id = mat.pessoa_id " +
            "         left join aposentadoria apo on apo.id = obj.id " +
            "         left join pensionista pen on pen.id = obj.id " +
            "         left join BENEFICIARIO ben on ben.id = obj.id ";
    }


    public List<VinculoFP> listaTodosVinculosPorPessoa(Long idPessoa) {
        Query q = em.createQuery("from VinculoFP obj where obj.matriculaFP.pessoa.id = :pessoa");
        q.setParameter("pessoa", idPessoa);
        return q.getResultList();
    }

    public VinculoFP recuperarVinculoContratoPorMatriculaENumero(String matricula, String numero) {
        Query q = em.createQuery("select obj from VinculoFP obj, ContratoFP c where c.id = obj.id and obj.matriculaFP.matricula = :mat and obj.numero = :numero");
        q.setParameter("mat", matricula);
        q.setParameter("numero", numero);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (VinculoFP) q.getResultList().get(0);
    }

    public VinculoFP recuperarVinculoContratoPorMatriculaENumeroImportacaoLancFP(String matricula, String numero) {
        Query q = em.createQuery("select new ContratoFP(c.id, c.matriculaFP.matricula||'/'||c.numero||' - '||c.matriculaFP.pessoa.nome)  from VinculoFP obj, ContratoFP c where c.id = obj.id and obj.matriculaFP.matricula = :mat and obj.numero = :numero");
        q.setParameter("mat", matricula);
        q.setParameter("numero", numero);
        q.setMaxResults(1);
        try {
            return (VinculoFP) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public Date buscarDataInicioVigenciaUltimoVinculoPorPessoa(PessoaFisica pessoaFisica, Date dataOperacao) {
        Query q = em.createQuery("select obj.inicioVigencia from VinculoFP obj where obj.matriculaFP.pessoa = :pessoa and :dataOperacao between obj.inicioVigencia and coalesce(obj.finalVigencia,:dataOperacao) order by obj.inicioVigencia desc");
        q.setParameter("pessoa", pessoaFisica);
        q.setParameter("dataOperacao", dataOperacao);
        if (!q.getResultList().isEmpty()) {
            return (Date) q.getResultList().get(0);
        }
        return null;
    }

    public List<VinculoFP> listaVinculosExonerados(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append("select contrato from ExoneracaoRescisao r inner join r.vinculoFP contrato ");
        sb.append("where lower(contrato.matriculaFP.matricula) like :filtro ");
        sb.append("or  lower(contrato.matriculaFP.pessoa.nome) like :filtro ");
        sb.append("or  lower(contrato.numero) like :filtro ");
        sb.append("or  lower(contrato.numero) like :filtro ");
        sb.append("or  lower(r.motivoExoneracaoRescisao.descricao) like :filtro ");
        sb.append("or  lower(r.tipoAvisoPrevio) like :filtro ");
        sb.append("or  (to_char(r.dataRescisao,'dd/MM/yyyy') like :filtro) ");
        sb.append("order by  contrato.inicioVigencia desc, contrato.matriculaFP.matricula desc, contrato.numero desc ");
        Query q = em.createQuery(sb.toString());
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(20);
        return q.getResultList();
    }

    public VinculoFP recuperarVinculoPorMatriculaENumero(String matricula, String numero) {
        ContratoFP c = null;
        Aposentadoria apo = null;
        Pensionista pens = null;
        Query q = em.createQuery("select obj from VinculoFP obj, ContratoFP c where c.id = obj.id and obj.matriculaFP.matricula = :mat and obj.numero = :numero");
        q.setParameter("mat", matricula);
        q.setParameter("numero", numero);
        if (!q.getResultList().isEmpty()) {
            c = (ContratoFP) q.getResultList().get(0);
        }
        Query q1 = em.createQuery("select obj from VinculoFP obj, Aposentadoria c where c.id = obj.id and obj.matriculaFP.matricula = :mat and obj.numero = :numero");
        q1.setParameter("mat", matricula);
        q1.setParameter("numero", numero);
        if (!q1.getResultList().isEmpty()) {
            apo = (Aposentadoria) q1.getResultList().get(0);
        }
        Query q2 = em.createQuery("select obj from VinculoFP obj, Pensionista c where c.id = obj.id and obj.matriculaFP.matricula = :mat and obj.numero = :numero");
        q2.setParameter("mat", matricula);
        q2.setParameter("numero", numero);
        if (!q2.getResultList().isEmpty()) {
            pens = (Pensionista) q2.getResultList().get(0);
        }
        return pens != null ? pens : (apo != null ? apo : c);
    }

    public List<VinculoFP> findVinculosByConsultDinamica(String consulta) {
        Query q = em.createQuery(consulta);
        if (consulta.contains(":data")) {
            q.setParameter("data", UtilRH.getDataOperacao());
        }
        return q.getResultList();
    }

    public List<Long> findVinculosByConsultDinamicaSql(String consulta) {
        Query q = em.createNativeQuery(consulta);
        if (consulta.contains(":data")) {
            q.setParameter("data", UtilRH.getDataOperacao());
        }
        List<Long> ids = Lists.newArrayList();
        for (Object o : q.getResultList()) {
            ids.add(((BigDecimal) o).longValue());
        }
        return ids;
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public VinculoFP recuperarMultiplosVinculosParaFolha(VinculoFP v, Mes mes, Integer ano) {
        Query q = em.createQuery("select v "
            + "  from VinculoFP v, RecursoDoVinculoFP recurso " +
            "   "
            + "  join v.matriculaFP m "
            + " where to_date(to_char(:dataHoje,'mm/yyyy'),'mm/yyyy') between to_date(to_char(v.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(v.finalVigencia,:dataHoje),'mm/yyyy'),'mm/yyyy') " +
            " and to_date(to_char(:dataHoje,'mm/yyyy'),'mm/yyyy') between to_date(to_char(recurso.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(recurso.finalVigencia,:dataHoje),'mm/yyyy'),'mm/yyyy') " +
            " and m.matricula is not null and m.pessoa is not null and v.inicioVigencia is not null and v.id = recurso.vinculoFP.id "
            + " and :data between v.inicioVigencia and coalesce(v.finalVigencia,:data) and " +
            "  v.id <> :id and m.pessoa = :pessoa and v.id not in (select contratoFP.id "
            + "                  from CedenciaContratoFP cedencia"
            + "                   inner join cedencia.contratoFP contratoFP "
            + "                 where cedencia.tipoContratoCedenciaFP = :parametroTipoCedencia "
            + " and to_date(to_char(:dataHoje,'mm/yyyy'),'mm/yyyy') between to_date(to_char(cedencia.dataCessao,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(cedencia.dataRetorno,:dataHoje),'mm/yyyy'),'mm/yyyy')) "


            + "   and v not in(select contratoFP"
            + "                  from RetornoCedenciaContratoFP retorno"
            + "                 inner join retorno.cedenciaContratoFP cedencia "
            + "                   inner join cedencia.contratoFP contratoFP "
            + "                 where cedencia.tipoContratoCedenciaFP = :parametroTipoCedencia "
            + "                   and retorno.oficioRetorno = false "
            + "   and to_date(to_char(:dataHoje,'mm/yyyy'),'mm/yyyy') between to_date(to_char(cedencia.dataCessao,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(retorno.dataRetorno,:dataHoje),'mm/yyyy'),'mm/yyyy')) and v.id not in (select e.id from Estagiario e) "

            + "   and v not in(select c from ContratoFP c join c.situacaoFuncionals situacao where situacao.situacaoFuncional in (:elementos) "
            + " and to_date(to_char(:dataHoje,'mm/yyyy'),'mm/yyyy') between to_date(to_char(situacao.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(situacao.finalVigencia,:dataHoje),'mm/yyyy'),'mm/yyyy')) order by v.inicioVigencia ");
        q.setParameter("dataHoje", Util.criaDataPrimeiroDiaMesJoda(mes.getNumeroMes(), ano).toDate());
        q.setParameter("data", UtilRH.getDataOperacao(), TemporalType.DATE);
        q.setParameter("pessoa", v.getMatriculaFP().getPessoa());
        q.setParameter("id", v.getId());
        q.setParameter("elementos", getSituacaoFuncionaisQueNaoDevemIrParaFolha());
        q.setParameter("parametroTipoCedencia", TipoCedenciaContratoFP.SEM_ONUS);
        List<VinculoFP> vinculoFPs = q.getResultList();
        if (vinculoFPs.isEmpty()) {
            return null;
        }
        return vinculoFPs.get(0);


    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long recuperarMultiplosVinculosParaFolhaSQL(VinculoFP v, Mes mes, Integer ano, Date dataReferencia) {
        String sql = "select v.id " +
            " from VinculoFP v inner join RecursoDoVinculoFP recurso on recurso.VINCULOFP_ID = v.id " +
            " inner join matriculaFP m on m.id = v.MATRICULAFP_ID " +
            " where to_date(to_char(:dataHoje,'mm/yyyy'),'mm/yyyy') between to_date(to_char(v.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(v.finalVigencia,:dataHoje),'mm/yyyy'),'mm/yyyy') " +
            "      and to_date(to_char(:dataHoje,'mm/yyyy'),'mm/yyyy') between to_date(to_char(recurso.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(recurso.finalVigencia,:dataHoje),'mm/yyyy'),'mm/yyyy') " +
            "      and m.matricula is not null and m.PESSOA_ID is not null and v.inicioVigencia is not null " +
            "      and :data between v.inicioVigencia and coalesce(v.finalVigencia,:data) and " +
            "      v.id <> :id and m.PESSOA_ID = :pessoa and v.id not in (select contratoFP.id " +
            "                                                          from CedenciaContratoFP cedencia " +
            "                                                            inner join contratoFP contratoFP on contratoFP.id = cedencia.CONTRATOFP_ID " +
            "                                                          where cedencia.tipoContratoCedenciaFP = :parametroTipoCedencia " +
            "                                                                and to_date(to_char(:dataHoje,'mm/yyyy'),'mm/yyyy') between to_date(to_char(cedencia.dataCessao,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(cedencia.dataRetorno,:dataHoje),'mm/yyyy'),'mm/yyyy')) " +
            "      and v.id not in(select contratoFP.id " +
            "                      from RetornoCedenciaContratoFP retorno " +
            "                        inner join cedenciaContratoFP cedencia on retorno.CEDENCIACONTRATOFP_ID = cedencia.ID " +
            "                        inner join contratoFP contratoFP on contratofp.id = cedencia.CONTRATOFP_ID " +
            "                      where cedencia.tipoContratoCedenciaFP = :parametroTipoCedencia " +
            "                            and retorno.oficioRetorno = 0 " +
            "                            and to_date(to_char(:dataHoje,'mm/yyyy'),'mm/yyyy') between to_date(to_char(cedencia.dataCessao,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(retorno.dataRetorno,:dataHoje),'mm/yyyy'),'mm/yyyy')) and v.id not in (select e.id from Estagiario e) " +
            "      and v.id not in(select c.id from ContratoFP c inner join SITUACAOCONTRATOFP st on st.CONTRATOFP_ID = c.id inner join situacaoFuncional situacao on situacao.id= st.SITUACAOFUNCIONAL_ID " +
            "                                                    where situacao.codigo in (:elementos) " +
            "                                                                                             and to_date(to_char(:dataHoje,'mm/yyyy'),'mm/yyyy') between to_date(to_char(st.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(st.finalVigencia,:dataHoje),'mm/yyyy'),'mm/yyyy')) order by v.inicioVigencia ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataHoje", Util.criaDataPrimeiroDiaMesJoda(mes.getNumeroMes(), ano).toDate());
        q.setParameter("data", dataReferencia, TemporalType.DATE);
        q.setParameter("pessoa", v.getMatriculaFP().getPessoa().getId());
        q.setParameter("id", v.getId());
        q.setParameter("elementos", getSituacaoFuncionaisQueNaoDevemIrParaFolha());
        q.setParameter("parametroTipoCedencia", TipoCedenciaContratoFP.SEM_ONUS.name());
        List<Long> ids = Lists.newArrayList();
        for (Object o : q.getResultList()) {
            ids.add(((BigDecimal) o).longValue());
        }
        if (ids.isEmpty()) {
            return null;
        }
        return ids.get(0);
    }

    public List<Long> getSituacaoFuncionaisQueNaoDevemIrParaFolha() {
        List<Long> st = new LinkedList<>();
        st.add(SituacaoFuncional.AFASTADO_LICENCIADO);
        st.add(SituacaoFuncional.EXONERADO_RESCISO);
        st.add(SituacaoFuncional.INATIVO_PARA_FOLHA);
        st.add(SituacaoFuncional.INSTITUIDOR);
        return st;
    }

    public List<VinculoFP> recuperarContratoMatriculaVigente(String matricula, String numero, Date data) {
        Query createQuery = em.createNativeQuery("select contrato.id from VinculoFP contrato inner join matriculaFP matricula on matricula.id = contrato.matriculafp_id "
            + " where matricula.matricula = :matricula and contrato.numero = :numero  "
            + "       and  to_date(to_char(:dataOperacao,'mm/yyyy'),'mm/yyyy') between "
            + "                 to_date(to_char(contrato.inicioVigencia,'mm/yyyy'),'mm/yyyy') "
            + "                 and to_date(to_char(coalesce(contrato.finalVigencia,:dataOperacao),'mm/yyyy'),'mm/yyyy')  "
            + " order by  contrato.inicioVigencia desc");
        createQuery.setParameter("numero", numero);
        createQuery.setParameter("matricula", matricula);
        createQuery.setParameter("dataOperacao", data);
        if (createQuery.getResultList().isEmpty()) {
            return null;
        }
        List<BigDecimal> resultados = createQuery.getResultList();
        List<VinculoFP> vinculos = Lists.newLinkedList();
        for (BigDecimal resultado : resultados) {
            vinculos.add(em.find(VinculoFP.class, resultado.longValue()));
        }
        return vinculos;
    }

    public List<VinculoFP> buscarVinculosVigentePorMatricula(String matricula, Date dataOperacao) {
        Query createQuery = em.createNativeQuery("select contrato.id from VinculoFP contrato inner join matriculaFP matricula on matricula.id = contrato.matriculafp_id "
            + "            where matricula.matricula = :matricula "
            + "            and  to_date(to_char(:dataOperacao,'mm/yyyy'),'mm/yyyy') between "
            + "             to_date(to_char(contrato.inicioVigencia,'mm/yyyy'),'mm/yyyy') "
            + "             and to_date(to_char(coalesce(contrato.finalVigencia,:dataOperacao),'mm/yyyy'),'mm/yyyy')  ");
        createQuery.setParameter("matricula", matricula);
        createQuery.setParameter("dataOperacao", dataOperacao);
        if (createQuery.getResultList().isEmpty()) {
            return null;
        }
        List<BigDecimal> ids = (List<BigDecimal>) createQuery.getResultList();
        List<VinculoFP> vinculos = Lists.newLinkedList();
        for (BigDecimal id : ids) {
            vinculos.add(em.find(VinculoFP.class, id.longValue()));
            ;
        }
        return vinculos;
    }

    public Boolean temVinculosComMultiploVinculos(VinculoFP vinculo, Date data) {
        Query q = em.createQuery("from VinculoFP vinculo inner join vinculo.matriculaFP mat " +
            " where mat = :mat and :data between vinculo.inicioVigencia and coalesce(vinculo.finalVigencia,:data)" +
            " and vinculo.id <> :vinculo ");
        q.setParameter("mat", vinculo.getMatriculaFP());
        q.setParameter("vinculo", vinculo.getId());
        q.setParameter("data", data);
        return !q.getResultList().isEmpty();
    }

    public List<VinculoFP> recuperarTodosVinculosVigentes(Date dataOperacao) {
        Query q = em.createQuery(" from VinculoFP vinculo where :data between vinculo.inicioVigencia and coalesce(vinculo.finalVigencia, :data) ");
        q.setParameter("data", DataUtil.dataSemHorario(dataOperacao));
        return q.getResultList();
    }


    public List<VinculoFP> recuperarTodosVinculosVigentesComFicha(Date dataOperacao, Mes mes, Integer ano) {
        Query q = em.createQuery("select vinculo from FichaFinanceiraFP ficha" +
            " join ficha.folhaDePagamento folha" +
            " join ficha.vinculoFP vinculo" +
            " where :data between vinculo.inicioVigencia and coalesce(vinculo.finalVigencia, :data)" +
            " and folha.ano = :ano and folha.mes = :mes ");
        q.setParameter("data", DataUtil.dataSemHorario(dataOperacao));
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    public boolean isVinculoFPVivo(Long id) {
        String sql = "      select v.id                                                        " +
            "        from vinculofp        v                                          " +
            "  inner join matriculafp      m on m.id               = v.matriculafp_id " +
            "  inner join pessoafisica    pf on pf.id              = m.pessoa_id      " +
            "  inner join registrodeobito ro on ro.pessoafisica_id = pf.id            " +
            "       where v.id = :id";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id", id);
        try {
            return q.getSingleResult() == null;
        } catch (NoResultException nre) {
            return true;
        }
    }

    public List<HoleriteBBAuxiliar> buscarVinculosVigentesComFichaFinanceiraFP(Integer ano, Integer mes, TipoFolhaDePagamento tipoFolhaDePagamento, UnidadeOrganizacional unidadeOrganizacional) {
        String hql = "select new br.com.webpublico.entidadesauxiliares.HoleriteBBAuxiliar(vinculo, enq, lot) from VinculoFP vinculo, EnquadramentoFuncional enq, LotacaoFuncional lot" +
            "                      inner join enq.progressaoPCS progressao " +
            "                      inner join enq.categoriaPCS cat" +
            "                      inner join vinculo.fichasFinanceiraFP ff " +
            "                     where enq.contratoServidor.id = vinculo.id " +
            "                        and lot.vinculoFP.id = vinculo.id" +
            "                        and ff.folhaDePagamento.ano = :ano " +
            "                        and ff.folhaDePagamento.mes = :mes " +
            "                        AND enq.inicioVigencia = ( select max(enqua.inicioVigencia) from EnquadramentoFuncional enqua " +
            "                                                   where enqua.contratoServidor.id = enq.contratoServidor.id) " +
            "                        and lot.inicioVigencia = (select max(l.inicioVigencia) from LotacaoFuncional l" +
            "                                                   where l.vinculoFP.id = lot.vinculoFP.id) ";
        if (unidadeOrganizacional != null) {
            hql += "                        and ff.vinculoFP.unidadeOrganizacional.id = :unidadeOrganizacional";
        }

        hql += "                        and ff.folhaDePagamento.tipoFolhaDePagamento = :tipoFolha ";
        Query q = em.createQuery(hql);
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("tipoFolha", tipoFolhaDePagamento);
        if (unidadeOrganizacional != null) {
            q.setParameter("unidadeOrganizacional", unidadeOrganizacional.getId());
        }

        List<HoleriteBBAuxiliar> resultList = new ArrayList<>(new HashSet(q.getResultList()));
        return resultList;
    }

    public List<VinculoFP> buscarVinculoPorMesAndAnoCalculados(Mes mes, Integer ano) {
        Query q = em.createQuery("select vinculo from FichaFinanceiraFP ficha join ficha.folhaDePagamento folha " +
            " join ficha.vinculoFP vinculo " +
            " where folha.mes = :mes" +
            " and folha.ano = :ano ");
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);

        return q.getResultList();
    }

    public List<VinculoFP> buscaVinculoVigenteFiltrandoAtributosMatriculaFP(String s) {
        String hql = "select new VinculoFP(obj.id, obj.matriculaFP.matricula||'/'||obj.numero||' - '||obj.matriculaFP.pessoa.nome|| " +
            "' ('||obj.matriculaFP.pessoa.nomeTratamento||')' || (select ' - APOSENTADO(A)' from Aposentadoria a where a.id = obj.id) ) " +
            "       from VinculoFP obj, VwHierarquiaAdministrativa vw " +
            " inner join obj.lotacaoFuncionals lotacao " +
            " inner join lotacao.unidadeOrganizacional un " +
            "      where un.id = vw.subordinadaId " +
            "        and :dataVigencia between obj.inicioVigencia and coalesce(obj.finalVigencia,:dataVigencia) " +
            "        and to_date('" + Util.dateToString(sistemaFacade.getDataOperacao()) + "','dd/mm/yyyy') between lotacao.inicioVigencia and coalesce(lotacao.finalVigencia,to_date('" + Util.dateToString(sistemaFacade.getDataOperacao()) + "','dd/mm/yyyy')) " +
            "        and to_date('" + Util.dateToString(sistemaFacade.getDataOperacao()) + "','dd/mm/yyyy') between to_date(vw.inicioVigencia,'dd/mm/yyyy') and coalesce(to_date(vw.fimVigencia,'dd/mm/yyyy'),to_date('" + Util.dateToString(sistemaFacade.getDataOperacao()) + "','dd/mm/yyyy')) " +
            "        and substring(vw.codigo,1,5) in " +
            "            (select substring(orgao.codigo,1,5) from UsuarioSistema usu, HierarquiaOrganizacional orgao " +
            "            inner join usu.usuarioUnidadeOrganizacional uno " +
            "            where orgao.tipoHierarquiaOrganizacional = 'ADMINISTRATIVA' " +
            "               and orgao.indiceDoNo = 2 " +
            "               and uno.unidadeOrganizacional.id = orgao.subordinada.id " +
            "               and usu.id =  " + sistemaFacade.getUsuarioCorrente().getId() +
            "               and to_date('" + Util.dateToString(sistemaFacade.getDataOperacao()) + "','dd/mm/yyyy') " +
            "                           between to_date(orgao.inicioVigencia,'dd/mm/yyyy') " +
            "                           and coalesce(to_date(orgao.fimVigencia,'dd/mm/yyyy'), " +
            "                               to_date('" + Util.dateToString(sistemaFacade.getDataOperacao()) + "','dd/mm/yyyy'))) " +
            "        and ((lower(obj.matriculaFP.pessoa.nome) like :filtro) " +
            "         or (lower(obj.matriculaFP.unidadeMatriculado.descricao) like :filtro) " +
            "         or (lower(obj.numero) like :filtro) " +
            "         or (lower(obj.matriculaFP.matricula) like :filtro)) ";

        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        return q.getResultList();
    }

    public List<Estagiario> buscarEstagiarioPorEmpregador(ConfiguracaoEmpregadorESocial empregadorESocial) {
        List<Estagiario> estagiarios = Lists.newArrayList();
        String sql = " select estagiario.id from vinculofp vinculo " +
            " inner join estagiario estagiario on vinculo.id = estagiario.id " +
            " where :dataAtual between vinculo.INICIOVIGENCIA and coalesce(vinculo.FINALVIGENCIA, :dataAtual) " +
            " and vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataAtual", sistemaFacade.getDataOperacao());
        q.setParameter("unidades", montarIdOrgaoEmpregador(empregadorESocial));
        if (!q.getResultList().isEmpty()) {
            for (BigDecimal id : (List<BigDecimal>) q.getResultList()) {
                estagiarios.add(em.find(Estagiario.class, id.longValue()));
            }
            return estagiarios;
        }
        return null;
    }

    private List<Long> montarIdOrgaoEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        List<Long> ids = Lists.newArrayList();
        for (ItemConfiguracaoEmpregadorESocial item : empregador.getItemConfiguracaoEmpregadorESocial()) {
            ids.add(item.getUnidadeOrganizacional().getId());
        }
        return ids;
    }

    public void removerContaCorrenteBancariaVinculoFP(List<VinculoFP> vinculos, List<ContaCorrenteBancPessoa> contas) {
        for (VinculoFP vinculo : vinculos) {
            for (ContaCorrenteBancPessoa conta : contas) {
                if (vinculo.getContaCorrente().equals(conta.getContaCorrenteBancaria())) {
                    vinculo.setContaCorrente(null);
                }
            }
            em.merge(vinculo);
        }
    }

    public Integer getTotalDiasTempoServico(VinculoFP v, Boolean considerarAverbacoes, Boolean considerarAlteracaoVinculo, Date dataAtual) {
        return (getTotalDiasTempoContrato(v, considerarAlteracaoVinculo, dataAtual) + (considerarAverbacoes ? getTotalDiasTempoAverbado(v) : 0)) - (getTotalDiasFaltas(v) + getTotalDiasAfastamentos(v));
    }

    public int getTotalDiasTempoContrato(VinculoFP v, Boolean considerarAlteracaoVinculo, Date dataAtual) {
        if (v == null) {
            return 0;
        }
        Date inicioVIgencia = considerarAlteracaoVinculo ? v.getAlteracaoVinculo() : null;
        Date finalVigencia = v.getFinalVigencia();
        if (inicioVIgencia == null) {
            inicioVIgencia = v.getInicioVigencia();
        }
        if (finalVigencia != null && finalVigencia.compareTo(dataAtual) > 0) {
            finalVigencia = dataAtual;
        }
        if (v != null) {
            return DataUtil.getDias(inicioVIgencia, finalVigencia != null ? finalVigencia : dataAtual);
        }
        return 0;
    }

    public int getTotalDiasTempoAverbado(VinculoFP v) {
        try {
            return relatorioTempoDeServicoFacade.buscarDadosRelatorioTempoServico(v.getId(), false).get(0).getSubreport5().get(0).getTotalAverbado().intValue();
        } catch (Exception e) {
            Integer dias = 0;
            List<AverbacaoTempoServico> averbacaoTempoServicoList = averbacaoTempoServicoFacade.buscarAverbacaoPorContratoFPAndData(v, v.getInicioVigencia(), Boolean.TRUE);
            for (AverbacaoTempoServico averbacao : averbacaoTempoServicoList) {
                dias = dias + DataUtil.getDias(averbacao.getInicioVigencia(), averbacao.getFinalVigencia());
            }
            return dias;
        }
    }

    public int getTotalDiasFaltas(VinculoFP v) {
        Integer dias = 0;
        List<VwFalta> faltasList = faltasFacade.buscarFaltasPorVinculoFPAndTipoFaltaAndData(v, TipoFalta.FALTA_INJUSTIFICADA, v.getInicioVigencia());
        for (VwFalta falta : faltasList) {
            dias = dias + falta.getTotalFaltas();
        }
        return dias;
    }

    public int getTotalDiasAfastamentos(VinculoFP v) {
        Integer dias = 0;
        List<Afastamento> afastamentoList = afastamentoFacade.buscarAfastamentosPorVinculoFPAndData(v, v.getInicioVigencia(), Boolean.TRUE);
        for (Afastamento afastamento : afastamentoList) {
            dias = dias + DataUtil.getDias(afastamento.getInicio(), afastamento.getTermino());
        }
        return dias;
    }

    public void enviarS2420ESocial(ConfiguracaoEmpregadorESocial empregador, VinculoFP vinculoFP, Boolean isObito) throws ValidacaoException {
        s2420Service.enviarS2420(empregador, vinculoFP, sistemaFacade.getDataOperacao(), isObito);
    }

    //Atender a familia de eventos 2400...ate 2410
    public List<VinculoFP> buscarBeneficiariosParaEnvioEsocial(ConfiguracaoEmpregadorESocial empregadorESocial, boolean somenteNaoEnviados,
                                                               Date dataOperacao, Date inicio, Date fim, TipoArquivoESocial tipo) {
        List<Long> unidades = montarIdOrgaoEmpregador(empregadorESocial);
        if (unidades.isEmpty()) {
            return Collections.emptyList();
        }


        String sql = "select v.id " +
            " from vinculofp v " +
                "         left join aposentadoria apo on apo.id = v.ID " +
                "         left join beneficiario ben on ben.id = v.ID " +
                "         left join pensionista pen on pen.id = v.id " +

            " where (ben.id is not null " +
            "   or pen.id is not null " +
            "   or apo.id is not null)  " +
            "  and v.unidadeorganizacional_id in (:unidades) ";
        if (inicio != null) {
            sql += " and v.iniciovigencia>= :dataInicial ";
        }

        if (fim != null) {
            sql += " and v.iniciovigencia <= :dataFinal ";
        }

        if (somenteNaoEnviados) {
            sql += " and not exists(select registro.* " +
                "                 from registroesocial registro" +
                "                 where registro.IDENTIFICADORWP = v.ID" +
                "                   and registro.TIPOARQUIVOESOCIAL = :tipo" +
                "                   and registro.SITUACAO in (:situacoes) " +
                "    ) ";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("unidades", montarIdOrgaoEmpregador(empregadorESocial));

        if (somenteNaoEnviados) {
            q.setParameter("tipo", tipo.name());
            q.setParameter("situacoes", Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
        }
        if (inicio != null) {
            q.setParameter("dataInicial", inicio);
        }
        if (fim != null) {
            q.setParameter("dataFinal", fim);
        }

        List resultList = q.getResultList();
        List<VinculoFP> vinculos = Lists.newArrayList();

        if (!resultList.isEmpty()) {
            for (Object o : resultList) {
                vinculos.add(recuperar(((BigDecimal) o).longValue()));
            }
            return vinculos;
        }
        return null;
    }


    public void enviarS2405ESocial(ConfiguracaoEmpregadorESocial empregador, VinculoFP vinculo) throws
        ValidacaoException {
        s2405Service.enviarS2405(empregador, vinculo);
    }


    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteBarraProgresso> enviarS2410ESocial(ConfiguracaoEmpregadorESocial empregador, List<VinculoFP> vinculos,
                                                               AssistenteBarraProgresso assistenteBarraProgresso) throws ValidacaoException {

        int quantidadeErros = 0;
        for (VinculoFP vinculo : vinculos) {
            try {
                s2410Service.enviarS2410(empregador, vinculo);
            } catch (ValidacaoException e) {
                quantidadeErros++;
                logErroEnvioEventoFacade.criarLogErro("VinculoFP", vinculo.getId(), empregador, TipoArquivoESocial.S2410, e);
            } catch (Exception e) {
                assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(new FacesMessage(e.getMessage()));
            }
            assistenteBarraProgresso.conta();
        }
        if (quantidadeErros > 0) {
            String mensagem = "Ocorreu <b>" + quantidadeErros + "</b> erro(s) de <b>" + vinculos.size() + "</b> evento(s) enviado(s), consulte o Log no painel do e-social para mais detalhes.";
            assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(new FacesMessage(mensagem));
        }
        return new AsyncResult<>(assistenteBarraProgresso);
    }

    public void enviarS2416ESocial(ConfiguracaoEmpregadorESocial empregador, VinculoFP vinculoFP) throws ValidacaoException {
        s2416Service.enviarS2416(empregador, vinculoFP);
    }

    public List<VinculoFP> buscarBeneficiariosVigentePorEntidade(ConfiguracaoEmpregadorESocial empregadorESocial, Date dataOperacao, String parte) {
        String hql = "from VinculoFP v " +
            " where ((lower(v.matriculaFP.pessoa.nome) like :filtro) " +
            "    or (lower(v.matriculaFP.pessoa.cpf) like :filtro) " +
            "    or (lower(v.matriculaFP.matricula) like :filtro)) " +
            " and :dataOperacao between v.inicioVigencia and coalesce(v.finalVigencia, :dataOperacao) " +
            " and v.unidadeOrganizacional.id in (:unidades) " +
            " and (v.id in (select a.id from Aposentadoria a )" +
            " or v.id in (select b.id from Beneficiario b )" +
            " or v.id in (select p.id from Pensionista p ))";

        Query q = em.createQuery(hql);
        List<Long> unidades = montarIdOrgaoEmpregador(empregadorESocial);
        if (unidades.isEmpty()) {
            return Lists.newArrayList();
        }
        q.setParameter("unidades", montarIdOrgaoEmpregador(empregadorESocial));
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setParameter("dataOperacao", dataOperacao);
        q.setMaxResults(50);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<VinculoFP> buscarBeneficiariosPorEntidade(ConfiguracaoEmpregadorESocial empregadorESocial, String parte) {
        String hql = "from VinculoFP v " +
            " where ((lower(v.matriculaFP.pessoa.nome) like :filtro) " +
            "    or (lower(v.matriculaFP.pessoa.cpf) like :filtro) " +
            "    or (lower(v.matriculaFP.matricula) like :filtro)) " +
            " and v.unidadeOrganizacional.id in (:unidades) " +
            " and v.finalVigencia is not null " +
            " and (v.id in (select a.id from Aposentadoria a )" +
            " or v.id in (select b.id from Beneficiario b )" +
            " or v.id in (select p.id from Pensionista p ))";

        Query q = em.createQuery(hql);
        List<Long> unidades = montarIdOrgaoEmpregador(empregadorESocial);
        if (unidades.isEmpty()) {
            return Lists.newArrayList();
        }
        q.setParameter("unidades", montarIdOrgaoEmpregador(empregadorESocial));
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(50);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<VinculoFP> buscarBeneficiariosComTerminoParaEnvioEsocial(ConfiguracaoEmpregadorESocial empregadorESocial, boolean somenteNaoEnviados, TipoArquivoESocial tipo) {
        String hql = "from VinculoFP v "
            + " where v.unidadeOrganizacional.id in (:unidades) " +
            " and v.finalVigencia is not null " +
            " and (v.id in (select a.id from Aposentadoria a )" +
            " or v.id in (select b.id from Beneficiario b )" +
            " or v.id in (select p.id from Pensionista p ))";
        if (somenteNaoEnviados) {
            hql += " and not exists (from RegistroESocial registro " +
                "                 where registro.idESocial = v.id" +
                "                   and registro.tipoArquivoESocial = :tipo" +
                "                   and registro.situacao = :situacao )";
        }
        Query q = em.createQuery(hql);
        q.setParameter("unidades", montarIdOrgaoEmpregador(empregadorESocial));
        if (somenteNaoEnviados) {
            q.setParameter("situacao", SituacaoESocial.PROCESSADO_COM_SUCESSO);
            q.setParameter("tipo", tipo);
        }
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public boolean hasAutorizacaoEspecialRH(UsuarioSistema usuarioSistema, TipoAutorizacaoRH tipo) {
        String sql = "select * from AUTORIZACAOUSUARIORH where tipoautorizacaorh = :tipo and usuariosistema_id = :usuario";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipo", tipo.name());
        q.setParameter("usuario", usuarioSistema.getId());
        return !q.getResultList().isEmpty();
    }

    public boolean existeVinculoFPVigenteNaData(Date data, HierarquiaOrganizacional hierarquiaOrganizacional) {
        String sql = " select v.id from vinculofp v " +
            " inner join hierarquiaorganizacional ho on ho.subordinada_id = v.unidadeorganizacional_id " +
            " where to_date(:data, 'dd/MM/yyyy') between ho.iniciovigencia and coalesce(ho.fimvigencia, to_date(:data, 'dd/MM/yyyy')) " +
            " and to_date(:data, 'dd/MM/yyyy') between v.iniciovigencia and coalesce(v.finalvigencia, to_date(:data, 'dd/MM/yyyy')) " +
            " and ho.tipohierarquiaorganizacional = :tipoHierarquia" +
            " and ho.id = :hierarquiaId ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("hierarquiaId", hierarquiaOrganizacional.getId());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setMaxResults(1);
        List resultado = q.getResultList();
        if (resultado != null) {
            return !resultado.isEmpty();
        }
        return false;
    }

    public List<BeneficiarioProvaDeVidaDTO> buscarBeneficiariosProvaDeVida(TipoFolhaDePagamento tipoFolha, String tipoBeneficiario, int ano, int mes, boolean apenasAniversariantes) {
        String sql = "select distinct " +
            "result.cpf, " +
            "result.nome, " +
            "result.data_nascimento " +
            "from (" +
            "select " +
            "replace(replace(pf.cpf, '.', ''), '-', '') as cpf, " +
            "pf.nome as nome, " +
            "to_char(pf.datanascimento, 'dd/mm/yyyy') as data_nascimento, " +
            "'Aposentados' as tipo_beneficiario, " +
            "folha.tipofolhadepagamento as tipofolha, " +
            "pf.datanascimento as data_nascimento_date " +
            "from aposentadoria ap " +
            "inner join vinculofp vinculo on ap.id = vinculo.id " +
            "inner join contratofp cont on cont.id = ap.contratofp_id " +
            "inner join matriculafp mat on vinculo.matriculafp_id = mat.id " +
            "inner join pessoafisica pf on mat.pessoa_id = pf.id " +
            "left join registrodeobito ro on ro.pessoafisica_id = pf.id " +
            "inner join fichafinanceirafp ficha on ficha.vinculofp_id = vinculo.id " +
            "inner join folhadepagamento folha on folha.id = ficha.folhadepagamento_id " +
            "inner join previdenciavinculofp prev on prev.contratofp_id = cont.id " +
            "inner join tipoprevidenciafp tipo on prev.tipoprevidenciafp_id = tipo.id " +
            "where tipo.tiporegimeprevidenciario = 'RPPS'  " +
            "and (ro.datafalecimento is null or " +
            "not exists (select 1 from registrodeobito reo where reo.pessoafisica_id = pf.id and reo.datafalecimento <= :dataCompetencia)) " +
            "and (:tipofolha is null or (folha.tipofolhadepagamento = :tipofolha and folha.ano = :ano and folha.mes = :mesFolha)) " +
            "union all " +
            "select " +
            "replace(replace(pf.cpf, '.', ''), '-', '') as cpf, " +
            "pf.nome as nome, " +
            "to_char(pf.datanascimento, 'dd/mm/yyyy') as data_nascimento, " +
            "'Pensionistas' as tipo_beneficiario, " +
            "folha.tipofolhadepagamento as tipofolha, " +
            "pf.datanascimento as data_nascimento_date " +
            "from pensionista pen " +
            "inner join vinculofp vinculo on pen.id = vinculo.id " +
            "inner join pensao pensao on pensao.ID = PEN.PENSAO_ID " +
            "inner join contratofp cont on cont.id = pensao.CONTRATOFP_ID " +
            "inner join matriculafp mat on vinculo.matriculafp_id = mat.id " +
            "inner join pessoafisica pf on mat.pessoa_id = pf.id " +
            "left join registrodeobito ro on ro.pessoafisica_id = pf.id " +
            "inner join fichafinanceirafp ficha on ficha.vinculofp_id = vinculo.id " +
            "inner join folhadepagamento folha on folha.id = ficha.folhadepagamento_id " +
            "inner join previdenciavinculofp prev on prev.contratofp_id = cont.id " +
            "inner join tipoprevidenciafp tipo on prev.tipoprevidenciafp_id = tipo.id " +
            "where tipo.tiporegimeprevidenciario = 'RPPS' " +
            "and (ro.datafalecimento is null or " +
            "not exists (select 1 from REGISTRODEOBITO reo where reo.pessoafisica_id = pf.id and reo.datafalecimento <= :dataCompetencia))" +
            "and (:tipofolha is null or (folha.tipofolhadepagamento = :tipofolha and folha.ano = :ano and folha.mes = :mesFolha))) result " +
            "where (:tipobeneficiario is null or result.tipo_beneficiario = :tipobeneficiario or :tipobeneficiario = 'Aposentados e Pensionistas') " +
            "and (:apenasaniversariantes = 'false' or (extract(month from result.data_nascimento_date) = :mes)) " +
            "order by result.data_nascimento";

        Query q = em.createNativeQuery(sql);
        q.setParameter("tipofolha", tipoFolha != null ? tipoFolha.name() : null);
        q.setParameter("tipobeneficiario", tipoBeneficiario);
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        q.setParameter("mesFolha", (mes - 1));
        q.setParameter("dataCompetencia", DataUtil.getDataFormatada(DataUtil.getUltimoDiaMes(mes, ano)));
        q.setParameter("apenasaniversariantes", apenasAniversariantes ? "true" : "false");

        List<Object[]> resultList = q.getResultList();
        List<BeneficiarioProvaDeVidaDTO> beneficiarios = new ArrayList<>();
        for (Object[] result : resultList) {
            BeneficiarioProvaDeVidaDTO beneficiario = new BeneficiarioProvaDeVidaDTO();
            beneficiario.setCpf((String) result[0]);
            beneficiario.setNome((String) result[1]);
            beneficiario.setDataNascimento((String) result[2]);
            beneficiarios.add(beneficiario);
        }

        return beneficiarios;
    }

    public List<ConsultaBeneficiarioProvaDeVidaVO> buscarDadosBeneficiariosProvaDeVida(TipoFolhaDePagamento tipoFolha, String tipoBeneficiario, int ano, int mes, boolean apenasAniversariantes) {
        String sql = "select servidor, cpf, data_nascimento, tipo_beneficiario from ( " +
            "    select distinct  " +
            "        m.matricula || '/' || v.numero || ' - ' || pf.nome AS servidor, " +
            "        FORMATACPFCNPJ(pf.CPF) as cpf, " +
            "        to_char(pf.datanascimento, 'dd/mm/yyyy') as data_nascimento, " +
            "        case  " +
            "            when exists (select 1 from pensionista pen where pen.id = p.id) then 'Pensionista'  " +
            "            when exists (select 1 from aposentadoria apose where apose.id = apo.id) then 'Aposentado' " +
            "        end AS tipo_beneficiario, " +
            "        pf.nome" +
            "    from vinculofp v " +
            "    left join pensionista p on p.id = v.id  " +
            "    left join aposentadoria apo on apo.id = v.id " +
            "    join matriculafp m on m.id = v.matriculafp_id " +
            "    join pessoafisica pf on pf.id = m.pessoa_id " +
            "    left join registrodeobito ro on ro.pessoafisica_id = pf.id  " +
            "    left join fichafinanceirafp ficha on ficha.vinculofp_id = v.id  " +
            "    left join folhadepagamento folha on folha.id = ficha.folhadepagamento_id  " +
            "    where (ro.datafalecimento is null or not exists (select 1 from registrodeobito reo " +
            "                                                      where reo.pessoafisica_id = pf.id and reo.datafalecimento <= :dataOperacao)) " +
            "    and not exists(select 1 " +
            "                 from vinculofp vinculo " +
            "                 inner join matriculafp matricula on vinculo.MATRICULAFP_ID = matricula.ID " +
            "                 where matricula.PESSOA_ID = m.PESSOA_ID " +
            "                 and (apo.id = vinculo.id or p.id = vinculo.id) " +
            "                 and :dataOperacao between vinculo.INICIOVIGENCIA and coalesce(vinculo.FINALVIGENCIA, :dataOperacao)) " +
            "    and (:tipofolha is null or (folha.tipofolhadepagamento = :tipofolha and folha.ano = :ano and folha.mes = :mesFolha)) " +
            "    and (apo.id = v.id or p.id = v.id)" +
            "    order by pf.nome " +
            ") subquery " +
            "where (:tipobeneficiario is null or tipo_beneficiario = :tipobeneficiario or :tipobeneficiario = 'Aposentados e Pensionistas') " +
            "and (:apenasaniversariantes = 'false' or (extract(month from TO_DATE(data_nascimento, 'dd/mm/yyyy')) = :mesAniversario))";

        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        q.setParameter("tipofolha", tipoFolha != null ? tipoFolha.name() : null);
        q.setParameter("tipobeneficiario", tipoBeneficiario);
        q.setParameter("ano", ano);
        q.setParameter("mesAniversario", mes);
        q.setParameter("mesFolha", mes - 1);
        q.setParameter("apenasaniversariantes", apenasAniversariantes ? "true" : "false");

        List<Object[]> resultList = q.getResultList();
        List<ConsultaBeneficiarioProvaDeVidaVO> beneficiarios = new ArrayList<>();
        for (Object[] result : resultList) {
            ConsultaBeneficiarioProvaDeVidaVO beneficiario = new ConsultaBeneficiarioProvaDeVidaVO();
            beneficiario.setMatContNome((String) result[0]);
            beneficiario.setCpf((String) result[1]);
            beneficiario.setDataNascimento((String) result[2]);
            beneficiario.setTipoBeneficiario((String) result[3]);
            beneficiarios.add(beneficiario);
        }
        return beneficiarios;
    }

    public Optional<VinculoFP> buscarVinculoFPPorCPF(String cpf) {
        String sql = "select v from VinculoFP  v " +
            " join v.matriculaFP mat " +
            " join mat.pessoa pf " +
            " WHERE REGEXP_REPLACE(pf.cpf, '[^0-9]', '') = :cpf ";
        Query q = em.createQuery(sql);
        q.setParameter("cpf", StringUtil.retornaApenasNumeros(cpf).trim());
        List<VinculoFP> result = q.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }
}
