/*
 * Codigo gerado automaticamente em Fri Feb 11 08:09:57 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Historico;
import br.com.webpublico.entidades.PessoaTransferenciaProprietario;
import br.com.webpublico.entidades.Propriedade;
import br.com.webpublico.entidades.TransferenciaProprietario;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.AssistenteBarraProgresso;
import org.hibernate.Hibernate;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Future;

@Stateless
public class TransferenciaProprietarioFacade extends AbstractFacade<TransferenciaProprietario> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private BloqueioTransferenciaProprietarioFacade bloqueioTransferenciaProprietarioFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CalculaITBIFacade calculaITBIFacade;

    public TransferenciaProprietarioFacade() {
        super(TransferenciaProprietario.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BloqueioTransferenciaProprietarioFacade getBloqueioTransferenciaProprietarioFacade() {
        return bloqueioTransferenciaProprietarioFacade;
    }

    @Override
    public TransferenciaProprietario recuperar(Object id) {
        TransferenciaProprietario t = em.find(TransferenciaProprietario.class, id);
        Hibernate.initialize(t.getProprietarios());
        Hibernate.initialize(t.getProprietariosAnteriores());
        if (t.getDetentorArquivoComposicao() != null && t.getDetentorArquivoComposicao().getArquivosComposicao() != null &&
            !t.getDetentorArquivoComposicao().getArquivosComposicao().isEmpty()) {
            Hibernate.initialize(t.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return t;
    }

    @Asynchronous
    public Future<?> efetivarTransferencia(AssistenteBarraProgresso assistenteBarraProgresso,
                                           TransferenciaProprietario transferencia,
                                           Historico historico) {
        try {
            assistenteBarraProgresso.setTotal(3);
            transferencia = salvarTransferencia(transferencia);
            transferencia.setCadastroImobiliario(cadastroImobiliarioFacade.recuperar(transferencia.getCadastroImobiliario().getId()));
            assistenteBarraProgresso.setDescricaoProcesso("Transferindo débitos do(s) antigo(s) proprietário(s) para o(s) novo(s) proprietário(s).");
            calculaITBIFacade.transfereDebitosCadastroImobiliario(transferencia.getCadastroImobiliario());
            assistenteBarraProgresso.conta();
            assistenteBarraProgresso.setDescricaoProcesso("Transferindo CDA's do(s) antigo(s) proprietário(s) para o(s) novo(s) proprietário(s).");
            calculaITBIFacade.transferirCDAs(transferencia.getCadastroImobiliario());
            assistenteBarraProgresso.conta();
            assistenteBarraProgresso.setDescricaoProcesso("Registrando o(s) novo(s) proprietário(s) no cadastro imobiliário.");
            transferirProprietarios(transferencia, historico);
            assistenteBarraProgresso.conta();
        } catch (Exception e) {
            logger.error("Erro ao efetivar transferência de proprietário. {}", e.getMessage());
            logger.debug("Detalhe do erro ao efetivar transferência de proprietário.", e);
            return new AsyncResult<>(e.getMessage());
        }
        return new AsyncResult<>(transferencia);
    }

    public TransferenciaProprietario salvarTransferencia(TransferenciaProprietario transferencia) {
        if (transferencia.getId() == null) {
            transferencia.setCodigo(singletonGeradorCodigo.getProximoCodigo(TransferenciaProprietario.class, "codigo"));
        }
        return em.merge(transferencia);
    }

    private void transferirProprietarios(TransferenciaProprietario transferencia, Historico historico) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date ontem = cal.getTime();
        Date hoje = new Date();
        for (Propriedade p : transferencia.getCadastroImobiliario().getPropriedadeVigente()) {
            if (ontem.before(hoje)) {
                p.setFinalVigencia(ontem);
            } else {
                p.setFinalVigencia(hoje);
            }
        }
        for (PessoaTransferenciaProprietario proprietario : transferencia.getProprietarios()) {
            Propriedade p = new Propriedade();
            p.setImovel(transferencia.getCadastroImobiliario());
            p.setPessoa(proprietario.getPessoa());
            p.setInicioVigencia(hoje);
            p.setProporcao(proprietario.getProporcao());
            p.setTipoProprietario(proprietario.getTipoProprietario());
            p.setVeioPorITBI(true);
            transferencia.getCadastroImobiliario().getPropriedade().add(p);
        }
        historico.setCadastro(transferencia.getCadastroImobiliario());
        historico.setSequencia(cadastroImobiliarioFacade.recuperarSequenciaHistorico(transferencia.getCadastroImobiliario()));
        transferencia.getCadastroImobiliario().getHistoricos().add(historico);
        cadastroImobiliarioFacade.salvar(transferencia.getCadastroImobiliario());
    }
}
