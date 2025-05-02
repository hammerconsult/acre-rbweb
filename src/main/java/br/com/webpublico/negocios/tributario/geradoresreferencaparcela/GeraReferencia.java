package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.entidades.SituacaoParcelaValorDivida;
import br.com.webpublico.interfaces.GeradorReferenciaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.seguranca.service.UsuarioSistemaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;


public abstract class GeraReferencia<T> implements GeradorReferenciaParcela {

    private static final Logger logger = LoggerFactory.getLogger(GeraReferencia.class);

    @Autowired
    protected UsuarioSistemaService usuarioSistemaService;
    public T calculo;
    @Autowired
    protected JdbcDividaAtivaDAO dao;

    protected GeraReferencia() {
        dependenciasSpring();
    }

    public void dependenciasSpring() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    public EntityManager getEm() {
        return usuarioSistemaService.getEntityManager();
    }

    protected Calculo getCalculo(SituacaoParcelaValorDivida spvd) {
        Calculo c = getEm().find(Calculo.class, spvd.getParcela().getValorDivida().getCalculo().getId());
        if (c == null) {
            c = spvd.getParcela().getValorDivida().getCalculo();
        }
        return c;
    }

    @Transactional
    protected Calculo getCalculo(Long idParcela) {
        Query query = getEm().createQuery("select pvd.valorDivida.calculo from ParcelaValorDivida  pvd where pvd.id = :id")
            .setParameter("id", idParcela);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            logger.warn("Nenhum calculo encontrado para parcelavalordivida id: " + idParcela);
        }
        return (Calculo) resultList.get(0);
    }

    @Override
    public void geraReferencia(SituacaoParcelaValorDivida spvd) {
        calculo = (T) getCalculo(spvd);
        if (!(((Calculo) calculo).getTipoCalculo().isCancelamentoParcelamento() && spvd.getSituacaoParcela().isParcelado())) {
            spvd.setReferencia(getReferencia());
            if (spvd != null && spvd.getSituacaoParcela().isSoPago()) {
                spvd.setReferencia(spvd.getReferencia() + buscarDosParcelamentoPagos(spvd.getParcela().getId()));
            } else if (spvd != null && spvd.getSituacaoParcela().isPago()) {
                spvd.setReferencia(spvd.getReferencia() + buscarDeTodosParcelamentos(spvd.getParcela().getId()));
            }
        }
    }

    @Override
    public String geraReferencia(Long idParcela) {
        calculo = (T) getCalculo(idParcela);
        SituacaoParcelaValorDivida spvd = dao.findUltimaSituacaoParcelaValorDividaByIdParcela(idParcela);
        if (spvd != null && spvd.getSituacaoParcela().isSoPago()) {
            return getReferencia() + buscarDosParcelamentoPagos(idParcela);
        } else if (spvd != null && spvd.getSituacaoParcela().isPago()) {
            return getReferencia() + buscarDeTodosParcelamentos(idParcela);
        }
        return getReferencia();
    }

    protected abstract String getReferencia();

    protected String buscarDeTodosParcelamentos(Long idParcela) {
        return dao.buscarDeTodosParcelamentos(idParcela);
    }

    protected String buscarDosParcelamentoPagos(Long idParcela) {
        return dao.buscarDosParcelamentoPagos(idParcela);
    }
}
