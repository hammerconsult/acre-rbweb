package br.com.webpublico.negocios.comum;

import br.com.webpublico.DateUtils;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.SolicitacaoCadastroPessoa;
import br.com.webpublico.entidades.comum.TemplateEmail;
import br.com.webpublico.entidades.comum.TipoTemplateEmail;
import br.com.webpublico.entidades.comum.trocatag.TrocaTagSolicitacaoCadastroCredor;
import br.com.webpublico.entidades.comum.trocatag.TrocaTagSolicitacaoFormularioCadastro;
import br.com.webpublico.entidadesauxiliares.VOSolicitacaoCadastroPessoa;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoSolicitacaoCadastroPessoa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoTributarioFacade;
import br.com.webpublico.negocios.ConsultaDebitoFacade;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.util.EmailService;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.apache.commons.lang.RandomStringUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class SolicitacaoCadastroPessoaFacade extends AbstractFacade<SolicitacaoCadastroPessoa> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;
    @EJB
    private TemplateEmailFacade templateEmailFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;

    public SolicitacaoCadastroPessoaFacade() {
        super(SolicitacaoCadastroPessoa.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void registrarSolicitacaoCadastroPessoa(String cpf, String email) {
        SolicitacaoCadastroPessoa solicitacao = salvarNovaSolicitacaoCadastroPessoa(cpf, email);
        solicitacao.setTipo(TipoSolicitacaoCadastroPessoa.TRIBUTARIO);
        enviarEmail(solicitacao, TipoTemplateEmail.SOLICITACAO_FORMULARIO_CADASTRO);
    }

    public void registrarSolicitacaoCadastroCredor(String cpf, String email) {
        SolicitacaoCadastroPessoa solicitacao = salvarNovaSolicitacaoCadastroPessoa(cpf, email);
        solicitacao.setTipo(TipoSolicitacaoCadastroPessoa.CONTABIL);
        enviarEmail(solicitacao, TipoTemplateEmail.SOLICITACAO_CADASTRO_CREDOR);
    }

    private SolicitacaoCadastroPessoa salvarNovaSolicitacaoCadastroPessoa(String cpf, String email) {
        SolicitacaoCadastroPessoa solicitacao = new SolicitacaoCadastroPessoa();
        solicitacao.setCpfCnpj(cpf);
        solicitacao.setEmail(email);
        solicitacao.setDataExpiracao(DateUtils.adicionarDias(new Date(), 2));
        solicitacao.setKey(RandomStringUtils.randomAlphanumeric(20));
        return em.merge(solicitacao);
    }

    private void enviarEmail(SolicitacaoCadastroPessoa solicitacao, TipoTemplateEmail tipoTemplateEmail) {
        TemplateEmail templateEmail = templateEmailFacade.findByTipoTemplateEmail(tipoTemplateEmail);
        if (templateEmail == null) {
            logger.error("Template de e-mail não configurado. {}", tipoTemplateEmail);
            return;
        }
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        if (tipoTemplateEmail.isCadastroCredor()) {
            EmailService.getInstance().enviarEmail(solicitacao.getEmail(),
                templateEmail.getAssunto(),
                new TrocaTagSolicitacaoCadastroCredor(configuracaoTributario, solicitacao)
                    .trocarTags(templateEmail.getConteudo()));
        } else {
            EmailService.getInstance().enviarEmail(solicitacao.getEmail(),
                templateEmail.getAssunto(),
                new TrocaTagSolicitacaoFormularioCadastro(configuracaoTributario, solicitacao)
                    .trocarTags(templateEmail.getConteudo()));
        }
    }

    public void concluirSolicitacaoCadastro(String key) {
        SolicitacaoCadastroPessoa solicitacao = findByKey(key);
        solicitacao.setDataConclusao(new Date());
        em.merge(solicitacao);
    }

    public SolicitacaoCadastroPessoa findByKey(String key) {
        try {
            Query q = em.createNativeQuery("select * from SolicitacaoCadastroPessoa s where upper(s.key) = :key",
                SolicitacaoCadastroPessoa.class);
            q.setParameter("key", key.toUpperCase());
            List resultList = q.getResultList();
            if (!resultList.isEmpty()) {
                return (SolicitacaoCadastroPessoa) resultList.get(0);
            }
        } catch (Exception ex) {
            logger.error("Erro findByKey:", ex);
        }
        return null;
    }

    public SolicitacaoCadastroPessoa buscarPorKeyAndTipo(String key, TipoSolicitacaoCadastroPessoa tipo) {
        try {
            Query q = em.createNativeQuery("select * from SolicitacaoCadastroPessoa s where upper(s.key) = :key and s.tipo = :tipo ",
                SolicitacaoCadastroPessoa.class);
            q.setParameter("key", key.toUpperCase());
            q.setParameter("tipo", tipo.name());
            List resultList = q.getResultList();
            if (!resultList.isEmpty()) {
                return (SolicitacaoCadastroPessoa) resultList.get(0);
            }
        } catch (Exception ex) {
            logger.error("Erro findByKey:", ex);
        }
        return null;
    }

    public void enviarEmailDaRejeicaoDoCadastro(SolicitacaoCadastroPessoa solicitacao, TipoTemplateEmail tipoTemplateEmail) {
        TemplateEmail templateEmail = templateEmailFacade.findByTipoTemplateEmail(tipoTemplateEmail);
        if (templateEmail == null) {
            logger.error("Template de e-mail não configurado. {}", tipoTemplateEmail);
            return;
        }
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        ConfiguracaoEmail configuracaoEmail = configuracaoEmailFacade.recuperarUtilmo();
        if (tipoTemplateEmail.isCadastroCredor()) {
            EmailService.getInstance().enviarEmail(solicitacao.getEmail(),
                templateEmail.getAssunto(),
                new TrocaTagSolicitacaoCadastroCredor(configuracaoTributario, solicitacao)
                    .trocarTags(templateEmail.getConteudo()));
        } else {
            EmailService.getInstance().enviarEmail(solicitacao.getEmail(),
                templateEmail.getAssunto(),
                new TrocaTagSolicitacaoFormularioCadastro(configuracaoTributario, solicitacao)
                    .trocarTags(templateEmail.getConteudo()));
        }
    }

    public void confirmarRejeicaoSolicitacaoCadastroPessoa(SolicitacaoCadastroPessoa solicitacaoCadastroPessoa, TipoTemplateEmail tipoTemplateEmail) {
        solicitacaoCadastroPessoa = salvarRetornando(solicitacaoCadastroPessoa);
        enviarEmailDaRejeicaoDoCadastro(solicitacaoCadastroPessoa, tipoTemplateEmail);
    }

    public List<VOSolicitacaoCadastroPessoa> buscarPessoasFisicasComSolicitacaoCadastroPessoaPorTipoESituacao(TipoSolicitacaoCadastroPessoa tipo, SituacaoCadastralPessoa situacao) {
        String sql = "SELECT pf.id, scp.key, scp.DATAEXPIRACAO, pf.NOME, pf.CPF, " +
            "                e.LOGRADOURO || ', ' || e.NUMERO || ', ' || e.BAIRRO || ', ' || e.LOCALIDADE || ', ' || e.UF || ', ' || e.CEP as endereco " +
            " FROM SolicitacaoCadastroPessoa scp " +
            "    inner join pessoafisica pf on regexp_replace(trim(pf.CPF), '[^0-9]', '')  = regexp_replace(trim(scp.CPFCNPJ), '[^0-9]', '') " +
            "    inner join pessoa p on p.id = pf.id " +
            "    inner join ENDERECOCORREIO e on e.id = p.ENDERECOPRINCIPAL_ID " +
            " where p.SITUACAOCADASTRALPESSOA = :situacao " +
            "   and scp.TIPO = :tipo " +
            " order by scp.id desc";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipo", tipo.name());
        q.setParameter("situacao", situacao.name());
        List<Object[]> resultado = q.getResultList();
        List<VOSolicitacaoCadastroPessoa> retorno = Lists.newArrayList();
        if (resultado != null && !resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                retorno.add(
                    new VOSolicitacaoCadastroPessoa(
                        ((BigDecimal) obj[0]).longValue(),
                        (String) obj[1],
                        (Date) obj[2],
                        (String) obj[3],
                        (String) obj[4],
                        (String) obj[5]
                    ));
            }
        }
        return retorno;
    }

    public List<VOSolicitacaoCadastroPessoa> buscarPessoasJuridicasComSolicitacaoCadastroPessoaPorTipoESituacao(TipoSolicitacaoCadastroPessoa tipo, SituacaoCadastralPessoa situacao) {
        String sql = "SELECT pj.id, scp.key, scp.DATAEXPIRACAO, pj.RAZAOSOCIAL, pj.CNPJ, " +
            "                e.LOGRADOURO || ', ' || e.NUMERO || ', ' || e.BAIRRO || ', ' || e.LOCALIDADE || ', ' || e.UF || ', ' || e.CEP as endereco " +
            " FROM SolicitacaoCadastroPessoa scp " +
            "    inner join pessoajuridica pj on regexp_replace(trim(pj.CNPJ), '[^0-9]', '')  = regexp_replace(trim(scp.CPFCNPJ), '[^0-9]', '') " +
            "    inner join pessoa p on p.id = pj.id " +
            "    inner join ENDERECOCORREIO e on e.id = p.ENDERECOPRINCIPAL_ID " +
            " where p.SITUACAOCADASTRALPESSOA = :situacao " +
            "   and scp.TIPO = :tipo " +
            " order by scp.id desc";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipo", tipo.name());
        q.setParameter("situacao", situacao.name());
        List<Object[]> resultado = q.getResultList();
        List<VOSolicitacaoCadastroPessoa> retorno = Lists.newArrayList();
        if (resultado != null && !resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                retorno.add(
                    new VOSolicitacaoCadastroPessoa(
                        ((BigDecimal) obj[0]).longValue(),
                        (String) obj[1],
                        (Date) obj[2],
                        (String) obj[3],
                        (String) obj[4],
                        (String) obj[5]
                    ));
            }
        }
        return retorno;
    }

    public byte[] gerarBytesDAM(Calculo calculo, String key) {
        ImprimeDAM imprimeDAM = new ImprimeDAM();
        return imprimeDAM.gerarByteImpressaoDamUnicoViaApi(recuperarDAM(calculo, key));
    }

    private DAM recuperarDAM(Calculo calculo, String key) throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        DAM dam = buscarDamPorKeySolicitacaoESituacoesDaParcela(key, Lists.newArrayList(SituacaoParcela.EM_ABERTO));
        if (dam != null) {
            return dam;
        } else if (calculo != null) {
            List<ValorDivida> valoresDivida = consultaDebitoFacade.getDamFacade().recuperaValorDividaDoCalculo(calculo);
            try {
                return consultaDebitoFacade.getDamFacade().geraDAM(valoresDivida.get(0)).get(0);
            } catch (Exception e) {
                logger.error("{}", e);
                ve.adicionarMensagemDeOperacaoNaoPermitida("Ocorreu um erro ao gerar o DAM!: " + e.getMessage());
                ve.lancarException();
            }
        }
        return null;
    }

    public DAM buscarDamPorKeySolicitacaoESituacoesDaParcela(String key, List<SituacaoParcela> situacoes) {
        String sql = " select dam.* " +
            " from SolicitacaoCadastroPessoa scp " +
            "         inner join CALCULOSOLICITACAOCADASTC calculoSol on calculoSol.solicitacaoCadastroPessoa_id = scp.ID " +
            "         inner join valordivida vd on vd.calculo_id = calculoSol.id " +
            "         inner join parcelavalordivida parcela ON parcela.valordivida_id = vd.id " +
            "         inner join itemdam item ON item.parcela_id = parcela.id " +
            "         inner join dam on item.dam_id = dam.id " +
            "         inner join SituacaoParcelaValorDivida situacao on situacao.ID = parcela.situacaoAtual_ID " +
            " where scp.key = :key " +
            "  and situacao.SITUACAOPARCELA in (:situacoes) " +
            " ORDER BY dam.id desc ";
        Query q = em.createNativeQuery(sql, DAM.class);
        q.setParameter("key", key);
        q.setParameter("situacoes", Util.getListOfEnumName(situacoes));
        List<DAM> resultado = q.getResultList();
        if (resultado != null && !resultado.isEmpty()) {
            return resultado.get(0);
        }
        return null;
    }

    public boolean hasSolicitacaoNaoConcluidaPorCPFAndEmail(String cpf, String email) {
        String sql = "SELECT scp.* " +
            " FROM SolicitacaoCadastroPessoa scp " +
            " where regexp_replace(trim(scp.cpfCnpj), '[^0-9]', '') = regexp_replace(trim(:cpf), '[^0-9]', '') " +
            "   and scp.email = :email " +
            "   and scp.dataconclusao is null " +
            "   and scp.dataExpiracao >= :dataHoraAtual ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("cpf", cpf);
        q.setParameter("email", email);
        q.setParameter("dataHoraAtual", new Date());
        List<Object[]> resultado = q.getResultList();
        return resultado != null && !resultado.isEmpty();
    }
}
