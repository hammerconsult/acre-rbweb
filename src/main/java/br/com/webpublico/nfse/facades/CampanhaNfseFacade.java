package br.com.webpublico.nfse.facades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.tributario.UsuarioNotaPremiadaFacade;
import br.com.webpublico.nfse.domain.*;
import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.nfse.util.GeradorNumeroCupomEletronico;
import br.com.webpublico.util.AssistenteBarraProgresso;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class CampanhaNfseFacade extends AbstractFacade<CampanhaNfse> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private UsuarioNotaPremiadaFacade usuarioNotaPremiadaFacade;

    public CampanhaNfseFacade() {
        super(CampanhaNfse.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void validarCpfBloqueado(BloqueioCpfCampanhaNfse bloqueioCpf,
                                    CampanhaNfse campanha) throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (bloqueioCpf.getPessoaFisica() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Pessoa Física para realizar o bloqueio.");
        }
        for (BloqueioCpfCampanhaNfse bloqueioCpfRegistrado : campanha.getCpfsBloqueados()) {
            if (bloqueioCpfRegistrado.getPessoaFisica().getId().equals(bloqueioCpf.getPessoaFisica().getId())) {
                ve.adicionarMensagemDeCampoObrigatorio("Essa Pessoa Física já se encontra bloqueada.");
            }
        }
        ve.lancarException();
    }

    public List<CampanhaNfse> buscarCampanhasAbertas() {
        String sql = " select * from campanhanfse c " +
            " where current_date between c.inicio and c.fim ";
        Query q = em.createNativeQuery(sql, CampanhaNfse.class);
        return q.getResultList();
    }

    private void persistirCupom(CampanhaNfse campanha,
                                UsuarioNotaPremiada usuario,
                                DeclaracaoPrestacaoServico declaracao,
                                String numero) {
        CupomCampanhaNfse cupom = new CupomCampanhaNfse();
        cupom.setDataEmissao(new Date());
        cupom.setCampanha(campanha);
        cupom.setUsuario(usuario);
        cupom.setDeclaracao(declaracao);
        cupom.setNumero(numero);
        em.persist(cupom);
    }

    @Override
    public CampanhaNfse recuperar(Object id) {
        CampanhaNfse campanha = super.recuperar(id);
        Hibernate.initialize(campanha.getCpfsBloqueados());
        if (campanha.getDetentorArquivoComposicao() != null) {
            campanha.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return campanha;
    }

    public List<DeclaracaoPrestacaoServico> buscarDeclaracoesParticipanteSemCupom(CampanhaNfse campanha,
                                                                                  UsuarioNotaPremiada usuario) {
        String sql = " select dec.* " +
            "    from declaracaoprestacaoservico dec " +
            "   left join notafiscal nf on nf.declaracaoprestacaoservico_id = dec.id " +
            "   left join servicodeclarado sd on sd.declaracaoprestacaoservico_id = dec.id " +
            "   inner join dadospessoaisnfse dpt on dpt.id = dec.dadospessoaistomador_id " +
            " where coalesce(nf.emissao, sd.emissao) between :inicio and :fim " +
            "  and dec.situacao != :cancelada " +
            "  and dpt.cpfcnpj = :cpfcnpj " +
            "  and not exists(select 1 " +
            "                    from cupomcampanhanfse c " +
            "                 where c.declaracao_id = dec.id " +
            "                   and c.campanha_id = :id_campanha " +
            "                   and c.usuario_id = :id_usuario) ";

        Query q = em.createNativeQuery(sql, DeclaracaoPrestacaoServico.class);
        q.setParameter("inicio", campanha.getInicio());
        q.setParameter("fim", campanha.getFim());
        q.setParameter("cancelada", SituacaoNota.CANCELADA.name());
        q.setParameter("cpfcnpj", usuario.getLogin());
        q.setParameter("id_campanha", campanha.getId());
        q.setParameter("id_usuario", usuario.getId());
        return q.getResultList();
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future gerarCuponsEletronicos(CampanhaNfse campanha, AssistenteBarraProgresso assistente) throws ValidacaoException {
        assistente.adicionarLogHtml("Recuperando dados da campanha.");
        campanha = recuperar(campanha.getId());
        assistente.adicionarLogHtml("Buscando os participantes da campanha.");
        List<UsuarioNotaPremiada> participantes = usuarioNotaPremiadaFacade.buscarParticipantes();
        if (participantes != null) {
            GeradorNumeroCupomEletronico gerador = new GeradorNumeroCupomEletronico();
            assistente.setTotal(participantes.size());
            assistente.adicionarLogHtml("Buscando as declarações de cada participante e gerando seus cupons eletrônicos.");
            for (UsuarioNotaPremiada participante : participantes) {
                List<DeclaracaoPrestacaoServico> declaracao = buscarDeclaracoesParticipanteSemCupom(campanha, participante);
                for (DeclaracaoPrestacaoServico declaracaoPrestacaoServico : declaracao) {
                    Integer quantidadeCupom = 1;
                    if (campanha.getTipoCupom().isPorValor()) {
                        BigDecimal quantidadePorValor = declaracaoPrestacaoServico.getTotalServicos().divide(campanha.getValorPorCupom(), 2, RoundingMode.HALF_UP);
                        quantidadeCupom = quantidadePorValor.intValue();
                    }
                    while (quantidadeCupom > 0) {
                        persistirCupom(campanha, participante, declaracaoPrestacaoServico, gerador.gerarNumero());
                        quantidadeCupom--;
                    }
                }
                assistente.conta();
            }
        }
        assistente.adicionarLogHtml("Cupons eletrônicos gerados com sucesso");
        return new AsyncResult<>(null);
    }

}
