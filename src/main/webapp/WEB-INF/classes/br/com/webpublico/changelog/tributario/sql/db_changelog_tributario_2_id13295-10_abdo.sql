    create or replace force view vwconsultarendas
    as
      select distinct
        c.cadastro_id id_cadastro,
        null cadastro,
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
        coalesce(pf.nome, pj.razaosocial) nomerazao,
        contrato.id id_contrato,
        contrato.numerocontrato numerocontrato,
        (select loc.codigo||' '||loc.descricao
           from ptocomercialcontratorendas rel
          inner join pontocomercial ponto on rel.pontocomercial_id = ponto.id
          inner join localizacao loc on ponto.localizacao_id = loc.id
         where rel.contratorendaspatrimoniais_id = contrato.id
           and rownum = 1) localizacao,
        (select listagg(ponto.numerobox, '; ') within group (order by ponto.numerobox)
            from ptocomercialcontratorendas rel
           inner join pontocomercial ponto on rel.pontocomercial_id = ponto.id
         where rel.contratorendaspatrimoniais_id = contrato.id) box
       from calculorendas rendas
      inner join contratorendaspatrimoniais contrato on contrato.id = rendas.contrato_id
      inner join calculo c on c.id = rendas.id
      inner join calculopessoa cp on cp.calculo_id = rendas.id
      left join pessoajuridica pj on pj.id = cp.pessoa_id
      left join pessoafisica pf on pf.id = cp.pessoa_id
      inner join valordivida vd on vd.calculo_id = rendas.id
      inner join divida d on d.id = vd.divida_id
      inner join dividaacrescimo dva on dva.divida_id = d.id
      inner join exercicio e on e.id = vd.exercicio_id
      inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id
      inner join opcaopagamento op on op.id = pvd.opcaopagamento_id
      inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id
    where spvd.situacaoparcela <> 'ISOLAMENTO'
