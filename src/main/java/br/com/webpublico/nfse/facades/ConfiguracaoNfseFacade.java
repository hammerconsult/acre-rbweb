package br.com.webpublico.nfse.facades;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoTributario;
import br.com.webpublico.nfse.domain.ConfiguracaoNfse;
import br.com.webpublico.nfse.domain.ConfiguracaoNfseDivida;
import br.com.webpublico.nfse.enums.TipoDeclaracaoMensalServico;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ConfiguracaoNfseFacade extends AbstractFacade<ConfiguracaoNfse> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigoTributario singletonGeradorCodigoTributario;

    public ConfiguracaoNfseFacade() {
        super(ConfiguracaoNfse.class);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SingletonGeradorCodigoTributario getSingletonGeradorCodigoTributario() {
        return singletonGeradorCodigoTributario;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ConfiguracaoNfse recuperarConfiguracao() {
        String sql = " select * from configuracaonfse ";
        Query q = em.createNativeQuery(sql, ConfiguracaoNfse.class);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            ConfiguracaoNfse configuracaoNfse = (ConfiguracaoNfse) resultList.get(0);
            if (configuracaoNfse.getConfiguracaoNfseRelatorio() != null && configuracaoNfse.getConfiguracaoNfseRelatorio().getDetentorArquivoComposicao() != null)
                configuracaoNfse.getConfiguracaoNfseRelatorio().getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().getPartes().size();
            Hibernate.initialize(configuracaoNfse.getDividasNfse());
            if (configuracaoNfse.getConfiguracaoNotaPremiada() != null && configuracaoNfse.getConfiguracaoNotaPremiada().getDetentorArquivoComposicao() != null) {
                configuracaoNfse.getConfiguracaoNotaPremiada().getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().getPartes().size();
            }
            configuracaoNfse.setHomologacao(SistemaFacade.PerfilApp.DEV.equals(sistemaFacade.getPerfilAplicacao()));
            return configuracaoNfse;
        }
        throw new ExcecaoNegocioGenerica("Configuração da Nfse não definida.");
    }


    public Divida buscarDividaNfse(TipoMovimentoMensal tipoMovimentoMensal, TipoDeclaracaoMensalServico tipoDeclaracaoMensalServico) {
        ConfiguracaoNfse configuracaoNfse = recuperarConfiguracao();
        ConfiguracaoNfseDivida configuracaoNfseDivida = configuracaoNfse.buscarConfiguracaoDivida(tipoMovimentoMensal, tipoDeclaracaoMensalServico);
        if (configuracaoNfseDivida == null || configuracaoNfseDivida.getDividaNfse() == null)
            throw new ExcecaoNegocioGenerica("Dívida não configurada para o Tipo de Movimento " + tipoMovimentoMensal + " e Tipo de Declaração " + tipoDeclaracaoMensalServico);
        return configuracaoNfseDivida.getDividaNfse();
    }

    public Tributo buscarTributoNfse(TipoMovimentoMensal tipoMovimentoMensal, TipoDeclaracaoMensalServico tipoDeclaracaoMensalServico) {
        ConfiguracaoNfse configuracaoNfse = recuperarConfiguracao();
        ConfiguracaoNfseDivida configuracaoNfseDivida = configuracaoNfse.buscarConfiguracaoDivida(tipoMovimentoMensal, tipoDeclaracaoMensalServico);
        if (configuracaoNfseDivida == null || configuracaoNfseDivida.getTributo() == null)
            throw new ExcecaoNegocioGenerica("Tributo não configurado para o Tipo de Movimento " + tipoMovimentoMensal + " e Tipo de Declaração " + tipoDeclaracaoMensalServico);
        return configuracaoNfseDivida.getTributo();
    }
}
