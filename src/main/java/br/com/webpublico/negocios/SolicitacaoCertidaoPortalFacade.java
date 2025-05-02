package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DadosSolicitacaoCertidaoPortal;
import br.com.webpublico.entidadesauxiliares.ValidacaoCertidaoDoctoOficial;
import br.com.webpublico.entidadesauxiliares.ValidacaoSolicitacaoDoctoOficial;
import br.com.webpublico.enums.SituacaoSolicitacao;
import br.com.webpublico.enums.TipoCadastroDoctoOficial;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.ws.model.WSImovel;
import br.com.webpublico.ws.model.WSPessoa;
import br.com.webpublico.ws.model.WsDadosPessoaisSolicitacaoCertidao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class SolicitacaoCertidaoPortalFacade implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(SolicitacaoCertidaoPortalFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SolicitacaoDoctoOficialFacade solicitacaoDoctoOficialFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    public WSImovel buscarWsImovel(WsDadosPessoaisSolicitacaoCertidao dados) {
        String sql = " select distinct ci.id, ci.inscricaocadastral from cadastroimobiliario ci " +
            " inner join propriedade prop on ci.id = prop.imovel_id " +
            " inner join pessoa pes on prop.pessoa_id = pes.id " +
            " left join pessoafisica pf on pes.id = pf.id " +
            " left join pessoajuridica pj on pes.id = pj.id " +
            " where to_date(:dataAtual, 'dd/MM/yyyy') between trunc(coalesce(prop.iniciovigencia, to_date(:dataAtual, 'dd/MM/yyyy'))) and " +
            " trunc(coalesce(prop.finalvigencia, to_date(:dataAtual, 'dd/MM/yyyy'))) " +
            " and ci.inscricaocadastral = :inscricao " +
            " and replace(replace(replace(coalesce(pf.cpf, pj.cnpj),'.',''),'-',''),'/','') = :cpfcnpj ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("dataAtual", DataUtil.getDataFormatada(new Date()));
        q.setParameter("inscricao", dados.getInscricaoCadastral().trim());
        q.setParameter("cpfcnpj", StringUtil.retornaApenasNumeros(dados.getCpfCnpj()));

        List<Object[]> resultados = q.getResultList();

        if (resultados != null && !resultados.isEmpty()) {
            Object[] obj = resultados.get(0);
            WSImovel imovel = new WSImovel();
            imovel.setId(((BigDecimal) obj[0]).longValue());
            imovel.setInscricao((String) obj[1]);

            return imovel;
        }

        return null;
    }

    public WSPessoa buscarWsPessoa(WsDadosPessoaisSolicitacaoCertidao dados) {
        String sql = dados.isPessoaFisica() ? montarSelectPessoaFisica() : montarSelectPessoaJuridica();
        Query q = em.createNativeQuery(sql);
        q.setParameter("cpfCnpj", StringUtil.retornaApenasNumeros(dados.getCpfCnpj()));

        List<Object[]> resultados = q.getResultList();

        if (resultados != null && !resultados.isEmpty()) {
            Object[] obj = resultados.get(0);
            WSPessoa pessoa = new WSPessoa();
            pessoa.setId(((BigDecimal) obj[0]).longValue());
            pessoa.setNome((String) obj[1]);
            pessoa.setCpfCnpj((String) obj[2]);

            return pessoa;
        }

        return null;
    }

    private String montarSelectPessoaFisica() {
        return " select pf.id, pf.nome, pf.cpf from pessoa pes " +
            " inner join pessoafisica pf on pes.id = pf.id" +
            " where replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') = :cpfCnpj ";
    }

    private String montarSelectPessoaJuridica() {
        return " select pj.id, pj.razaosocial, pj.cnpj from Pessoa pes " +
            " inner join PessoaJuridica pj on pj.id = pes.id " +
            " where replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') = :cpfCnpj ";
    }

    public ValidacaoSolicitacaoDoctoOficial imprimirCertidao(Long id, DadosSolicitacaoCertidaoPortal dados) throws Exception {
        ValidacaoSolicitacaoDoctoOficial validacao = buscarOrGerarSolicitacao(id, dados);

        if (validacao != null && validacao.getSolicitacaoDoctoOficial() != null) {
            validacao.setSolicitacaoDoctoOficial(salvarSolicitacaoComoEmitida(validacao.getSolicitacaoDoctoOficial()));
            validacao.setCertidao(documentoOficialFacade.geraDocumentoSolicitacaoPortalWeb(validacao.getSolicitacaoDoctoOficial()).getBytes());
        }

        return validacao;
    }

    public boolean certidaoLiberadaParaEmissaoPortal(WsDadosPessoaisSolicitacaoCertidao dados) {
        TipoCadastroDoctoOficial tipoCadastroDoctoOficial = dados.getTipoDocumentoOficialPortal().getTipoCadastroDoctoOficial();
        if (TipoCadastroDoctoOficial.NENHUM.equals(tipoCadastroDoctoOficial)) {
            tipoCadastroDoctoOficial = dados.isPessoaFisica() ? TipoCadastroDoctoOficial.PESSOAFISICA : TipoCadastroDoctoOficial.PESSOAJURIDICA;
        }
        TipoDoctoOficial tipoDoctoOficial = solicitacaoDoctoOficialFacade.getTipoDoctoOficialFacade()
            .buscarTipoDoctoOficial(dados.getTipoDocumentoOficialPortal(), tipoCadastroDoctoOficial);
        ModeloDoctoOficial mod = documentoOficialFacade.getTipoDoctoOficialFacade().recuperaModeloVigente(tipoDoctoOficial);
        if (mod != null) {
            return mod.getTipoDoctoOficial().getDisponivelSolicitacaoWeb();
        }
        return false;
    }

    private ValidacaoSolicitacaoDoctoOficial buscarOrGerarSolicitacao(Long id, DadosSolicitacaoCertidaoPortal dados) {
        Pessoa pessoa = null;
        Cadastro cadastro = null;
        if (dados.isCadastroImobiliario()) {
            cadastro = em.find(CadastroImobiliario.class, id);
        } else if (dados.isPessoaFisica() || dados.isPessoaJuridica()) {
            pessoa = em.find(Pessoa.class, id);
        }

        TipoCadastroDoctoOficial tipoCadastroDoctoOficial = dados.getTipoDocumentoOficialPortal().getTipoCadastroDoctoOficial();
        if (TipoCadastroDoctoOficial.NENHUM.equals(tipoCadastroDoctoOficial)) {
            tipoCadastroDoctoOficial = dados.isPessoaFisica() ? TipoCadastroDoctoOficial.PESSOAFISICA : TipoCadastroDoctoOficial.PESSOAJURIDICA;
        }

        TipoDoctoOficial tipoDoctoOficial = solicitacaoDoctoOficialFacade.getTipoDoctoOficialFacade()
            .buscarTipoDoctoOficial(dados.getTipoDocumentoOficialPortal(), tipoCadastroDoctoOficial);


        if (tipoDoctoOficial != null) {
            Long idCadastro = pessoa != null ? pessoa.getId() : (cadastro != null ? cadastro.getId() : null);

            if (idCadastro != null) {
                SolicitacaoDoctoOficial solicitacao = solicitacaoDoctoOficialFacade.buscarSolicitacaoCertidaoVigente(tipoDoctoOficial.getId(),
                    tipoCadastroDoctoOficial, idCadastro);

                ValidacaoSolicitacaoDoctoOficial validacao = new ValidacaoSolicitacaoDoctoOficial();

                if (solicitacao == null) {
                    solicitacao = new SolicitacaoDoctoOficial();

                    try {
                        if (dados.isPessoaFisica()) {
                            solicitacao.setPessoaFisica((PessoaFisica) pessoa);
                        } else if (dados.isPessoaJuridica()) {
                            solicitacao.setPessoaJuridica((PessoaJuridica) pessoa);
                        } else if (dados.isCadastroImobiliario()) {
                            solicitacao.setCadastroImobiliario((CadastroImobiliario) cadastro);
                        }

                        solicitacao.setDataSolicitacao(new Date());
                        solicitacao.setTipoDoctoOficial(tipoDoctoOficial);
                        solicitacao.setDocumentoOficial(documentoOficialFacade.geraDocumentoEmBranco(cadastro, pessoa, tipoDoctoOficial));

                        ValidacaoCertidaoDoctoOficial validacaoDocto = solicitacaoDoctoOficialFacade.permitirSolicitarDocumento(solicitacao);

                        if (!validacaoDocto.getValido()) {
                            validacao.setMensagemValidacao(validacaoDocto.getMensagem());
                        } else {
                            solicitacao.setCodigo(singletonGeradorCodigo.getProximoCodigo(SolicitacaoDoctoOficial.class, "codigo"));
                            solicitacao = em.merge(solicitacao);
                            solicitacaoDoctoOficialFacade.calculaValorCertidao(solicitacao);
                            validacao.setSolicitacaoDoctoOficial(solicitacao);
                        }
                    } catch (Exception e) {
                        logger.error("Erro ao buscar/gerar solicitacao. ", e);
                    }
                } else {
                    ValidacaoCertidaoDoctoOficial validacaoDocto = solicitacaoDoctoOficialFacade.permitirSolicitarDocumento(solicitacao);
                    if (!validacaoDocto.getValido()) {
                        validacao.setMensagemValidacao(validacaoDocto.getMensagem());
                    } else {
                        validacao.setSolicitacaoDoctoOficial(solicitacao);
                    }
                }
                return validacao;
            }
        }

        return null;
    }

    private SolicitacaoDoctoOficial salvarSolicitacaoComoEmitida(SolicitacaoDoctoOficial sol) {
        sol.setSituacaoSolicitacao(SituacaoSolicitacao.EMITIDO);
        sol = em.merge(sol);
        return sol;
    }
}
