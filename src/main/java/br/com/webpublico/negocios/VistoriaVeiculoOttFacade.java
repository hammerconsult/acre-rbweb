package br.com.webpublico.negocios;

import br.com.webpublico.DateUtils;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoVistoriaOtt;
import br.com.webpublico.util.DataUtil;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

@Stateless
public class VistoriaVeiculoOttFacade extends AbstractFacade<VistoriaVeiculoOtt> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ParametrosOttFacade parametrosOttFacade;
    @EJB
    private VeiculoOperadoraTecnologiaTransporteFacade veiculoOperadoraTecnologiaTransporteFacade;
    @EJB
    private OperadoraTecnologiaTransporteFacade operadoraTecnologiaTransporteFacade;

    public VistoriaVeiculoOttFacade() {
        super(VistoriaVeiculoOtt.class);
    }

    @Override
    public VistoriaVeiculoOtt recuperar(Object id) {
        VistoriaVeiculoOtt vistoriaVeiculoOtt = super.recuperar(id);
        if (vistoriaVeiculoOtt.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(vistoriaVeiculoOtt.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return vistoriaVeiculoOtt;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ParametrosOttFacade getParametrosOttFacade() {
        return parametrosOttFacade;
    }

    public VeiculoOperadoraTecnologiaTransporteFacade getVeiculoOperadoraTecnologiaTransporteFacade() {
        return veiculoOperadoraTecnologiaTransporteFacade;
    }

    public OperadoraTecnologiaTransporteFacade getOperadoraTecnologiaTransporteFacade() {
        return operadoraTecnologiaTransporteFacade;
    }

    public VistoriaVeiculoOtt salvarRetornando(VistoriaVeiculoOtt vistoriaVeiculoOtt) {
        return em.merge(vistoriaVeiculoOtt);
    }

    public boolean verificarVistoriaNaoVencidaPorVeiculo(VistoriaVeiculoOtt vistoriaVeiculoOtt) {

        String sql = "select V.id from VISTORIAVEICULOOTT V " +
            "where V.vencimento > to_date(:vencimento,'dd/MM/yyyy')" +
            " and v.veiculoOtTransporte_id = :idVeiculo";
        if (vistoriaVeiculoOtt.getId() != null) {
            sql += " and v.id <> :idVistoria";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("vencimento", DataUtil.getDataFormatada(new Date()));
        q.setParameter("idVeiculo", vistoriaVeiculoOtt.getVeiculoOtTransporte().getId());
        if (vistoriaVeiculoOtt.getId() != null) {
            q.setParameter("idVistoria", vistoriaVeiculoOtt.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public void calcularDataVencimentoVistoria(String placaVeiculo, VistoriaVeiculoOtt vistoria) {
        Integer ultimoDigito = new Integer(placaVeiculo.substring(placaVeiculo.trim().length() - 1));
        ParametrosOtt parametrosOtt = parametrosOttFacade.retornarParametroOtt();
        DigitoVencimento digitoVencimento = parametrosOtt.getVencimentos()
            .stream().filter(venc -> venc.getDigito().equals(ultimoDigito)
                && DigitoVencimento.TipoDigitoVencimento.VISTORIA_VEICULO_OTT.equals(venc.getTipoDigitoVencimento()))
            .findFirst()
            .orElse(null);
        if (digitoVencimento != null) {
            Date vencimento = DateUtils.getData(digitoVencimento.getDia(), digitoVencimento.getMes(), DateUtils.getAno(new Date()));
            if (vencimento.before(new Date())) {
                vencimento = DataUtil.acrescentarUmAno(vencimento);
            }
            vistoria.setVencimento(vencimento);
        }
    }

    public void gerarVistoriaParaVeiculo(VeiculoOperadoraTecnologiaTransporte veiculo, UsuarioSistema usuarioSistema) {
        VistoriaVeiculoOtt vistoria = new VistoriaVeiculoOtt();
        vistoria.setOperadoraTecTransporte(veiculo.getOperadoraTransporte());
        vistoria.setVeiculoOtTransporte(veiculo);
        vistoria.setDataVistoria(new Date());
        vistoria.setSituacao(SituacaoVistoriaOtt.ABERTA);
        vistoria.setResponsavel(usuarioSistema.getPessoaFisica());
        calcularDataVencimentoVistoria(veiculo.getPlacaVeiculo(), vistoria);
        em.merge(vistoria);
    }
}
