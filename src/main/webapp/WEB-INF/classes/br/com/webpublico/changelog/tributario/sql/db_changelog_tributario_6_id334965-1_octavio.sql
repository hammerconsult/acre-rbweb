create or replace view vwconsultaalvara as
select distinct
    c.cadastro_id id_cadastro,
    ce.inscricaocadastral cadastro,
    e.ano exercicio,
    cp.pessoa_id id_pessoa,
    c.id id_calculo,
    c.subdivida sd,
    vd.id id_valordivida,
    c.datacalculo emissao,
    pvd.id id_parcela,
    pvd.opcaopagamento_id id_opcaopagamento,
    op.promocional cotaunica,
    pvd.vencimento,
    spvd.situacaoparcela situacaoparcela,
    spvd.referencia,
    pacote_consulta_de_debitos.getnumeroparcela(vd.id, op.id, op.promocional, pvd.sequenciaparcela) parcela,
    case
        when spvd.situacaoparcela <> 'EM_ABERTO'
            then pvd.valor
        else spvd.saldo
        end valororiginal,
    d.descricao divida,
    d.id id_divida,
    coalesce(pvd.dividaativa,0) dividaativa,
    coalesce(pvd.dividaativaajuizada,0) dividaativaajuizada,
    null pagamento,
    dva.acrescimo_id id_configuracaoacrescimo,
    coalesce(pf.cpf, pj.cnpj) cpfcnpj,
    coalesce(pf.nome, pj.razaosocial) nomerazaosocial,
    alv.tipoalvara,
    alv.provisorio provisorio,
    ce.classificacaoatividade,
    nj.id naturezajuridica_id,
    nj.descricao naturezajuridica_descricao,
    ta.id tipoautonomo_id,
    ta.descricao tipoautonomo_descricao,
    ce.mei,
    endbce.tipologradouro,
    endbce.logradouro,
    endbce.numero,
    endbce.complemento,
    endbce.bairro,
    endbce.cep
from calculo c
         inner join cadastroeconomico ce on c.cadastro_id = ce.id
         inner join vwenderecobce endbce on endbce.cadastroeconomico_id = ce.id
         inner join naturezajuridica nj on nj.id = ce.naturezajuridica_id
         left join tipoautonomo ta on ta.id = ce.tipoautonomo_id
         left join calculoalvarafunc cf on cf.id = c.id
         left join calculoalvaralocalizacao cl on cl.id = c.id
         left join calculoalvarasanitario cs on cs.id = c.id
         left join calculoalvara calc_alvara on calc_alvara.id = c.id
         left join processocalculoalvara proc_alvara on calc_alvara.processocalculoalvara_id = proc_alvara.id
         inner join alvara alv on alv.id = coalesce(cf.alvara_id, cl.alvara_id , cs.alvara_id, proc_alvara.alvara_id)
         inner join calculopessoa cp on cp.calculo_id = c.id
         left join pessoajuridica pj on pj.id = cp.pessoa_id
         left join pessoafisica pf on pf.id = cp.pessoa_id
         inner join valordivida vd on vd.calculo_id = c.id
         inner join divida d on d.id = vd.divida_id
         inner join dividaacrescimo dva on dva.divida_id = d.id
         inner join exercicio e on vd.exercicio_id = e.id
         inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id
         inner join opcaopagamento op on op.id = pvd.opcaopagamento_id
         inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id
where spvd.situacaoparcela <> 'ISOLAMENTO';
