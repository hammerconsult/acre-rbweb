package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.TipoIssqn;
import br.com.webpublico.enums.TipoMotivoTransferenciaPermissao;
import br.com.webpublico.enums.TipoPermissaoRBTrans;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
public class TransferenciaPermissaoTransporteFacade extends AbstractFacade<TransferenciaPermissaoTransporte> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private ParametrosTransitoTransporte parametrosTransitoTransporte;
    @EJB
    private ParametrosTransitoTransporteFacade parametrosTransitoTransporteFacade;
    @EJB
    private CalculoRBTransFacade calculoRBTransFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PermissaoTransporteFacade permissaoTransporteFacade;
    @EJB
    private CredencialRBTransFacade credencialRBTransFacade;

    public TransferenciaPermissaoTransporteFacade() {
        super(TransferenciaPermissaoTransporte.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ParametrosTransitoTransporteFacade getParametrosTransitoTransporteFacade() {
        return parametrosTransitoTransporteFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public PermissaoTransporteFacade getPermissaoTransporteFacade() {
        return permissaoTransporteFacade;
    }

    public ParametrosTransitoTransporte getParametrosTransitoTransporte() {
        return parametrosTransitoTransporte;
    }

    @Override
    public TransferenciaPermissaoTransporte recuperar(Object id) {
        TransferenciaPermissaoTransporte transferencia = super.recuperar(id);
        if (transferencia.getCertidaoObitoRBTrans() != null) {
            if (transferencia.getCertidaoObitoRBTrans().getDetentorArquivoComposicao() != null) {
                transferencia.getCertidaoObitoRBTrans().getDetentorArquivoComposicao().getArquivosComposicao().size();
            }

        }
        return transferencia;
    }

    public List<PermissaoTransporte> listaPermissoesPorCmc(String parte) {
        StringBuilder hql = new StringBuilder("select perm from PermissaoTransporte perm join fetch perm.permissionarios permissionarios join permissionarios.cadastroEconomico ce ")
            .append(" join ce.situacaoCadastroEconomico situacao ")
            .append(" left join ce.enquadramentos enquadramento with enquadramento.fimVigencia is null ")
            .append(" where enquadramento.tipoIssqn = :tipoIss ")
            .append(" and perm.tipoPermissaoRBTrans != :tipoPermissao")
            .append(" and (ce.inscricaoCadastral like :parte ")
            .append(" or ce.pessoa in(select pf from PessoaFisica pf where lower(pf.nome) like :parte or pf.cpf like :parte) ")
            .append(" or ce.pessoa in(select pj from PessoaJuridica pj where lower(pj.razaoSocial) like :parte or pj.cnpj like :parte)")
            .append(" or to_char(perm.numero) like :parte )")
            .append(" and situacao.id = (select max(s.id) from SituacaoCadastroEconomico s where s in elements (ce.situacaoCadastroEconomico))")
            .append(" and (situacao.situacaoCadastral = :situacaoCadastral1 or situacao.situacaoCadastral = :situacaoCadastral2)")
            .append(" order by perm.numero ");

        Query q = em.createQuery(hql.toString());
        q.setMaxResults(10);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("tipoIss", TipoIssqn.FIXO);
        q.setParameter("tipoPermissao", TipoPermissaoRBTrans.FRETE);
        q.setParameter("situacaoCadastral1", SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        q.setParameter("situacaoCadastral2", SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
        return q.getResultList();
    }

    public TransferenciaPermissaoTransporte transferePermissao(TransferenciaPermissaoTransporte transferencia,
                                                               PermissaoTransporte antiga,
                                                               CadastroEconomico cadastro) throws Exception {
        parametrosTransitoTransporte = getParametrosTransitoTransporteFacade().recuperarParametroVigentePeloTipo(
            antiga.getTipoPermissaoRBTrans());

        antiga = em.find(PermissaoTransporte.class, antiga.getId());
        transferencia.setPermissaoNova(cadastro);
        transferencia.setPermissaoAntiga(antiga);
        transferencia.setEfetuadaEm(sistemaFacade.getDataOperacao());

        VeiculoPermissionario veiculoVelho = antiga.getVeiculoVigente();
        if (veiculoVelho != null) {
            veiculoVelho = baixaVeiculoAntigo(antiga, veiculoVelho, transferencia);
            credencialRBTransFacade.encerrarCredenciailVeiculoPermissionario(veiculoVelho);

        }

        CadastroEconomico cmcPermissionarioAntigo = antiga.getPermissionarioVigente().getCadastroEconomico();
        credencialRBTransFacade.encerrarCredenciailPermissionario(antiga.getPermissionarioVigente());
        antiga.getPermissionarioVigente().encerraVigencia();
        Permissionario permissionario = new Permissionario();
        permissionario.setCadastroEconomico(transferencia.getPermissaoNova());
        permissionario.setInicioVigencia(new Date());
        permissionario.setPermissaoTransporte(antiga);
        antiga.getPermissionarios().add(permissionario);
        antiga = em.merge(antiga);

        if (transferencia.getGeraCredencialPermissionario()) {
            credencialRBTransFacade.criarCredencialTransporte(sistemaFacade.getDataOperacao(), parametrosTransitoTransporte,
                transferencia.getPermissaoAntiga(), transferencia.getPermissaoNova());
        }

        if (veiculoVelho != null) {
            if (transferencia.getTransfereVeiculo()) {
                VeiculoPermissionario novoVeiculo = geraNovoVeiculo(veiculoVelho, transferencia);
                if (transferencia.getGeraCredencialVeiculo()) {
                    credencialRBTransFacade.criarCredencialTrafego(sistemaFacade.getDataOperacao(), parametrosTransitoTransporte,
                        transferencia.getPermissaoAntiga(), novoVeiculo);
                }
            }
        }

        if (!TipoMotivoTransferenciaPermissao.FALECIMENTO.equals(transferencia.getMotivo())) {
            transferencia.setCalculoRBTrans(calculoRBTransFacade.calculaTransferenciaPermissao(cadastro, antiga.getTipoPermissaoRBTrans()));

            if (transferencia.getPermissaoAntiga().isMotoTaxi()) {
                transferencia.setCalculoMotoTaxi(calculoRBTransFacade.calculaTransferenciaPermissaoMotoTaxi(cmcPermissionarioAntigo, antiga));
            }
        }

        permissaoTransporteFacade.gerarTermoDePermissao(antiga, parametrosTransitoTransporte);

        transferencia = em.merge(transferencia);


        return transferencia;
    }

    private VeiculoPermissionario geraNovoVeiculo(VeiculoPermissionario veiculoVelho, TransferenciaPermissaoTransporte transferencia) throws Exception {
        VeiculoPermissionario novoVeiculo = new VeiculoPermissionario();
        novoVeiculo.setInicioVigencia(new Date());
        novoVeiculo.setStatusLancamento(veiculoVelho.getStatusLancamento());
        novoVeiculo.setCadastradoPor(veiculoVelho.getCadastradoPor());
        novoVeiculo.setCategoriaVeiculo(veiculoVelho.getCategoriaVeiculo());
        novoVeiculo.setVeiculoTransporte(veiculoVelho.getVeiculoTransporte());
        novoVeiculo.setPermissaoTransporte(transferencia.getPermissaoAntiga());
        if (!TipoMotivoTransferenciaPermissao.FALECIMENTO.equals(transferencia.getMotivo())) {
            novoVeiculo.setCalculoRBTrans(calculoRBTransFacade.calculaInsercaoVeiculo(transferencia.getPermissaoNova(), transferencia.getPermissaoAntiga().getTipoPermissaoRBTrans()));

            if (transferencia.getPermissaoAntiga().isTaxi()) {
                VistoriaVeiculo vistoriaVeiculo = new VistoriaVeiculo();
                vistoriaVeiculo.setVeiculoPermissionario(novoVeiculo);
                vistoriaVeiculo.setRealizadaEm(Calendar.getInstance().getTime());
                vistoriaVeiculo.setUsuarioQueLancou(sistemaFacade.getUsuarioCorrente().getLogin());
                vistoriaVeiculo.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
                vistoriaVeiculo.setCalculoRBTrans(calculoRBTransFacade.calculaVistoriaVeiculo(transferencia.getPermissaoNova()));
                novoVeiculo.getVistoriasVeiculo().add(vistoriaVeiculo);
            }

        }
        permissaoTransporteFacade.gerarTermoAutorizacaoVeiculo(transferencia.getPermissaoAntiga(), parametrosTransitoTransporte, novoVeiculo.getVeiculoTransporte());
        return em.merge(novoVeiculo);
    }

    private VeiculoPermissionario baixaVeiculoAntigo(PermissaoTransporte antiga, VeiculoPermissionario veiculoVelho, TransferenciaPermissaoTransporte transferencia) throws Exception {
        veiculoVelho.setFinalVigencia(new Date());
        BaixaVeiculoPermissionario baixaVeiculo = new BaixaVeiculoPermissionario();
        baixaVeiculo.setMotivo("Transferência de Permissão de Transporte");
        baixaVeiculo.setRealizadaEm(new Date());
        baixaVeiculo.setVeiculoPermissionario(veiculoVelho);
        baixaVeiculo.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());

        if (!TipoMotivoTransferenciaPermissao.FALECIMENTO.equals(transferencia.getMotivo())) {
            CalculoRBTrans calculoBaixaVeiculo = calculoRBTransFacade.calculaBaixaVeiculo(antiga.getPermissionarioVigente().getCadastroEconomico(), antiga.getTipoPermissaoRBTrans());
            baixaVeiculo.setCalculoRBTrans(calculoBaixaVeiculo);
            permissaoTransporteFacade.gerarTermoBaixaVeiculo(antiga, parametrosTransitoTransporte, veiculoVelho.getVeiculoTransporte());
        }

        baixaVeiculo = em.merge(baixaVeiculo);
        veiculoVelho.setBaixaVeiculoPermissionario(baixaVeiculo);
        veiculoVelho = em.merge(veiculoVelho);
        return veiculoVelho;
    }

    public VeiculoPermissionario atualizaVeiculoPermissionario(VeiculoPermissionario vp) {
        return em.merge(vp);
    }
}
