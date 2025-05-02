package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.MotivoInativacaoImovel;
import br.com.webpublico.enums.SituacaoSolicitacaoTransferenciaMovBCI;
import br.com.webpublico.enums.TipoArquivoTransferenciaMovBCI;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class ParecerTransferenciaMovBCIFacade extends AbstractFacade<ParecerTransferenciaMovBCI> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SolicitacaoTransfMovBCIFacade solicitacaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public ParecerTransferenciaMovBCIFacade() {
        super(ParecerTransferenciaMovBCI.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ParecerTransferenciaMovBCI recuperar(Object id) {
        ParecerTransferenciaMovBCI parecer = em.find(ParecerTransferenciaMovBCI.class, id);
        Hibernate.initialize(parecer.getArquivos());
        for (ArquivoTransferenciaMovBCI arquivo : parecer.getArquivos()) {
            Hibernate.initialize(arquivo.getDetentorArquivoComposicao());
        }
        return parecer;
    }

    public ParecerTransferenciaMovBCI salvaParecer(ParecerTransferenciaMovBCI parecer) throws Exception {
        return salvaParecer(parecer, Lists.<ArquivoTransferenciaMovBCI>newArrayList(), Lists.<ArquivoTransferenciaMovBCI>newArrayList());
    }

    public ParecerTransferenciaMovBCI salvaParecer(ParecerTransferenciaMovBCI parecer, List<ArquivoTransferenciaMovBCI> arquivos,
                                                   List<ArquivoTransferenciaMovBCI> removidos) throws Exception {
        removerArquivos(removidos);
        parecer = salvarArquivos(parecer, arquivos);
        adicionarArquivoDaSolicitacao(parecer);
        return em.merge(parecer);
    }

    private void removerArquivos(List<ArquivoTransferenciaMovBCI> removidos) {
        for (ArquivoTransferenciaMovBCI removido : removidos) {
            solicitacaoFacade.removerArquivos(removido.getId());
        }
    }

    private ParecerTransferenciaMovBCI salvarArquivos(ParecerTransferenciaMovBCI parecer, List<ArquivoTransferenciaMovBCI> arquivos) {
        if(arquivos != null && !arquivos.isEmpty()) {
            for (ArquivoTransferenciaMovBCI arquivo : arquivos) {
                arquivo.setParecer(parecer);
                arquivo = salvarArquivo(arquivo);
                parecer.getArquivos().add(arquivo);
            }
            return em.merge(parecer);
        }
        return parecer;
    }

    public ArquivoTransferenciaMovBCI salvarArquivo(ArquivoTransferenciaMovBCI arquivo) {
        return em.merge(arquivo);
    }

    private void adicionarArquivoDaSolicitacao(ParecerTransferenciaMovBCI parecer) throws Exception {
        SolicitacaoTransfMovBCI solicitacao = solicitacaoFacade.recuperar(parecer.getSolicitacao().getId());

        ArquivoTransferenciaMovBCI arquivo = null;
        for (ArquivoTransferenciaMovBCI arq : solicitacao.getArquivos()) {
            if (TipoArquivoTransferenciaMovBCI.SIMULACAO.equals(arq.getTipoArquivo())) {
                arquivo = arq;
                break;
            }
        }

        if (arquivo != null) {
            solicitacaoFacade.removerArquivoParecer(parecer);

            ArquivoTransferenciaMovBCI arquivoTransferencia = solicitacaoFacade.criarArquivoTransferencia(parecer);

            ArquivoComposicao arquivoSolicitacao = arquivo.getDetentorArquivoComposicao().getArquivoComposicao();
            ArquivoComposicao novoArquivoComposicao = solicitacaoFacade.criarArquivoComposicao(arquivoSolicitacao.getArquivo().getByteArrayDosDados(), arquivoTransferencia.
                getDetentorArquivoComposicao(), arquivoSolicitacao.getArquivo());
            arquivoTransferencia.getDetentorArquivoComposicao().setArquivoComposicao(novoArquivoComposicao);

            arquivoTransferencia = salvarArquivo(arquivoTransferencia);
            parecer.getArquivos().add(arquivoTransferencia);
        }
    }

    public List<SolicitacaoTransfMovBCI> buscarSolicitacoes(String parte) {
        String sql = " select sol.* from solicitacaotransfmovbci sol " +
            "left join cadastroimobiliario bci_origem on sol.cadastrodestino_id = bci_origem.id " +
            "left join cadastroimobiliario bci_destino on sol.cadastrodestino_id = bci_destino.id " +
            "where (sol.numerosolicitacao like :parte or bci_origem.inscricaocadastral like :parte " +
            "    or bci_destino.inscricaocadastral like :parte) " +
            "and sol.situacao = :situacao ";

        Query q = em.createNativeQuery(sql, SolicitacaoTransfMovBCI.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("situacao", SituacaoSolicitacaoTransferenciaMovBCI.ABERTA.name());

        List<SolicitacaoTransfMovBCI> solicitacoes = q.getResultList();
        return solicitacoes != null ? solicitacoes : Lists.<SolicitacaoTransfMovBCI>newArrayList();
    }

    public UsuarioSistema buscarUsuarioLogado() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public Date buscarDataOperacao() {
        return sistemaFacade.getDataOperacao();
    }

    public String buscarProprietarioBCI(Long idCadastro) {
        return solicitacaoFacade.buscarProprietarioBCI(idCadastro, buscarDataOperacao());
    }

    public ParecerTransferenciaMovBCI deferirTransferencia(ParecerTransferenciaMovBCI selecionado) {
        SolicitacaoTransfMovBCI solicitacao = solicitacaoFacade.recuperar(selecionado.getSolicitacao().getId());

        if (solicitacao.getTransferirDebitosDams()) {
            transferirDebitosAndDans(solicitacao.getTransferirDebitosDams(), solicitacao);
        } else {
            transferirDividaAtiva(solicitacao.getTransferirDividasAtivas(), solicitacao);
            transferirCalculosAndRevisoesIPTU(solicitacao.getTransferirCalculosRevisaoIPTU(), solicitacao);
            transferirIsencoesIPTU(solicitacao.getTransferirIsencoesIPTU(), solicitacao);
            transferirParcelamentos(solicitacao.getTransferirParcelamentos(), solicitacao);
            transferirLancamentosITBI(solicitacao.getTransferirLancamentosITBI(), solicitacao);
        }
        transferirCertidoes(solicitacao.getTransferirCertidoes(), solicitacao);
        transferirProcessosDebito(solicitacao.getTransferirProcessosDebitos(), solicitacao);

        CadastroImobiliario bci = inativarBCI(solicitacao.getInativarInscricaoImob(), solicitacao.getCadastroOrigem());
        solicitacao.setCadastroOrigem(bci);

        solicitacao.setSituacao(SituacaoSolicitacaoTransferenciaMovBCI.DEFERIDA);
        solicitacao = em.merge(solicitacao);
        selecionado.setSolicitacao(solicitacao);

        return em.merge(selecionado);
    }

    public ParecerTransferenciaMovBCI indeferirTransferencia(ParecerTransferenciaMovBCI selecionado) {
        SolicitacaoTransfMovBCI solicitacao = solicitacaoFacade.recuperar(selecionado.getSolicitacao().getId());

        solicitacao.setSituacao(SituacaoSolicitacaoTransferenciaMovBCI.INDEFERIDA);
        solicitacao = em.merge(solicitacao);
        selecionado.setSolicitacao(solicitacao);

        return em.merge(selecionado);
    }

    private void transferirDividaAtiva(boolean condicao, SolicitacaoTransfMovBCI solicitacao) {
        List<String> querys = Lists.newArrayList();

        querys.add("update calculo calc set calc.cadastro_id = :idBCIDestino where exists(select item.id from iteminscricaodividaativa item where item.id = calc.id) and calc.cadastro_id = :idBCIOrigem ");
        querys.add("update certidaodividaativa certidao set certidao.cadastro_id = :idBCIDestino where certidao.cadastro_id = :idBCIOrigem ");
        transferirMovimentos(condicao, querys, solicitacao);
    }

    private void transferirCalculosAndRevisoesIPTU(boolean condicao, SolicitacaoTransfMovBCI solicitacao) {
        List<String> querys = Lists.newArrayList();

        querys.add("update calculo calc set calc.cadastro_id = :idBCIDestino where exists(select iptu.id from calculoiptu iptu where iptu.id = calc.id) and calc.cadastro_id = :idBCIOrigem ");
        querys.add("update calculoiptu calciptu set calciptu.cadastroimobiliario_id = :idBCIDestino where calciptu.cadastroimobiliario_id = :idBCIOrigem ");
        querys.add("update revisaocalculoiptu revisao set revisao.cadastro_id = :idBCIDestino where revisao.cadastro_id = :idBCIOrigem ");
        transferirMovimentos(condicao, querys, solicitacao);
    }

    private void transferirIsencoesIPTU(boolean condicao, SolicitacaoTransfMovBCI solicitacao) {
        String query = "update isencaocadastroimobiliario isencaobci set isencaobci.cadastroimobiliario_id = :idBCIDestino where isencaobci.cadastroimobiliario_id = :idBCIOrigem ";
        transferirMovimentos(condicao, Lists.newArrayList(query), solicitacao);
    }

    private void transferirParcelamentos(boolean condicao, SolicitacaoTransfMovBCI solicitacao) {
        List<String> querys = Lists.newArrayList();

        querys.add("update calculo calc set calc.cadastro_id = :idBCIDestino where exists(select procparcela.id from processoparcelamento procparcela where procparcela.id = calc.id) and calc.cadastro_id = :idBCIOrigem ");
        transferirMovimentos(condicao, querys, solicitacao);
    }

    private void transferirDebitosAndDans(boolean condicao, SolicitacaoTransfMovBCI solicitacao) {
        String query = " update calculo calc set calc.cadastro_id = :idBCIDestino where calc.cadastro_id = :idBCIOrigem ";
        transferirMovimentos(condicao, Lists.newArrayList(query), solicitacao);

        transferirDividaAtiva(solicitacao.getTransferirDividasAtivas(), solicitacao);
        transferirCalculosAndRevisoesIPTU(solicitacao.getTransferirCalculosRevisaoIPTU(), solicitacao);
        transferirIsencoesIPTU(solicitacao.getTransferirIsencoesIPTU(), solicitacao);
        transferirParcelamentos(solicitacao.getTransferirParcelamentos(), solicitacao);
        transferirLancamentosITBI(solicitacao.getTransferirLancamentosITBI(), solicitacao);
    }

    private void transferirCertidoes(boolean condicao, SolicitacaoTransfMovBCI solicitacao) {
        String query = " update solicitacaodoctooficial docto set docto.cadastroimobiliario_id = :idBCIDestino where docto.cadastroimobiliario_id = :idBCIOrigem ";
        transferirMovimentos(condicao, Lists.newArrayList(query), solicitacao);
    }

    private void transferirLancamentosITBI(boolean condicao, SolicitacaoTransfMovBCI solicitacao) {
        List<String> querys = Lists.newArrayList();

        querys.add("update calculoitbi itbi set itbi.cadastroimobiliario_id = :idBCIDestino where itbi.cadastroimobiliario_id = :idBCIOrigem ");
        querys.add("update calculo calc set calc.cadastro_id = :idBCIDestino where exists(select itbi.id from calculoitbi itbi where itbi.id = calc.id) and calc.cadastro_id = :idBCIOrigem");
        transferirMovimentos(condicao, querys, solicitacao);
    }

    private void transferirProcessosDebito(boolean condicao, SolicitacaoTransfMovBCI solicitacao) {
        String query = " update processodebito proc set proc.cadastro_id = :idBCIDestino where proc.cadastro_id = :idBCIOrigem ";
        transferirMovimentos(condicao, Lists.newArrayList(query), solicitacao);
    }

    private void transferirMovimentos(boolean condicao, List<String> querys, SolicitacaoTransfMovBCI solicitacao) {
        if (condicao) {
            for (String query : querys) {
                em.createNativeQuery(query).setParameter("idBCIDestino", solicitacao.getCadastroDestino().getId()).
                    setParameter("idBCIOrigem", solicitacao.getCadastroOrigem().getId()).executeUpdate();
            }
        }
    }

    private CadastroImobiliario inativarBCI(boolean condicao, CadastroImobiliario bci) {
        if(condicao) {
            bci.setAtivo(Boolean.FALSE);
            bci.setDataInativacao(new Date());
            bci.setMotivoInativacao(MotivoInativacaoImovel.UNIFICACAO);

            return em.merge(bci);
        }
        return bci;
    }
}
