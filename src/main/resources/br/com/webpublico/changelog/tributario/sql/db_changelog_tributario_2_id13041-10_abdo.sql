    create or replace force view vwconsultanotafiscal
    as
    select distinct c.cadastro_id id_cadastro,
        coalesce(ce_nfsa_prest.inscricaocadastral, ce_nfsa_tomad.inscricaocadastral)  cadastro,
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
        pacote_consulta_de_debitos.getdatapagamentoparcela(pvd.id, spvd.situacaoparcela) pagamento,
        dva.acrescimo_id id_configuracaoacrescimo,
        case
           when c.cadastro_id is not null then 'ECONOMICO'
           else 'PESSOA'
        end tipocadastro,
        coalesce(pf.cpf, pj.cnpj) cpfcnpj,
        coalesce(pf.nome, pj.razaosocial) nomerazaosocial,
        nfsa.id nota_id,
        nfsa.numero nota_numero,
        nfsa.emissao nota_emissao,
        ex_nota.ano nota_exercicio,
        nfsa.valoriss nota_percentual_iss,
        nfsa.valorservicos nota_total_servicos,
        nfsa.valortotaliss nota_total_iss,
        nfsa.situacao nota_situacao,
        nfsa.usuario_id nota_id_usuario,
        us.login nota_usuario,
        ce_nfsa_tomad.inscricaocadastral cmc_tomador,
        coalesce(pf_nfsa_tomad.id, pj_nfsa_tomad.id) id_tomador,
        coalesce(pf_nfsa_tomad.cpf, pj_nfsa_tomad.cnpj) cpfcnpj_tomador,
        coalesce(pf_nfsa_tomad.nome, pj_nfsa_tomad.razaosocial) nomerazao_tomador,
        ce_nfsa_prest.inscricaocadastral cmc_prestador,
        coalesce(pf_nfsa_prest.id, pj_nfsa_prest.id) id_prestador,
        coalesce(pf_nfsa_prest.cpf, pj_nfsa_prest.cnpj) cpfcnpj_prestador,
        coalesce(pf_nfsa_prest.nome, pj_nfsa_prest.razaosocial) nomerazao_prestador,
        (select hist.id
               from historicoimpressaodam hist
            where hist.id = (select max(h_dam.id)
                               from dam dam
                              inner join itemdam idam on idam.dam_id = dam.id
                              inner join historicoimpressaodam h_dam on h_dam.dam_id = dam.id
                             where idam.parcela_id = pvd.id)) id_historico
       from calculonfsavulsa c_nfsa
      inner join calculo c on c.id = c_nfsa.id
      inner join nfsavulsa nfsa on nfsa.id = c_nfsa.nfsavulsa_id
      inner join exercicio ex_nota on ex_nota.id = nfsa.exercicio_id
      left join usuariosistema us on nfsa.usuario_id = us.id
      left join cadastroeconomico ce_nfsa_prest on c.cadastro_id = ce_nfsa_prest.id
      left join pessoafisica pf_nfsa_prest on pf_nfsa_prest.id = coalesce(nfsa.prestador_id, ce_nfsa_prest.pessoa_id)
      left join pessoajuridica pj_nfsa_prest on pj_nfsa_prest.id = coalesce(nfsa.prestador_id, ce_nfsa_prest.pessoa_id)
      left join cadastroeconomico ce_nfsa_tomad on ce_nfsa_tomad.id = c.cadastro_id
      left join pessoafisica pf_nfsa_tomad on pf_nfsa_tomad.id = coalesce(nfsa.tomador_id, ce_nfsa_tomad.pessoa_id)
      left join pessoajuridica pj_nfsa_tomad on pj_nfsa_tomad.id = coalesce(nfsa.tomador_id, ce_nfsa_tomad.pessoa_id)
      inner join valordivida vd on vd.calculo_id = c.id
      inner join divida d on d.id = vd.divida_id
      inner join dividaacrescimo dva on dva.divida_id = d.id
      inner join calculopessoa cp on cp.calculo_id = c.id
      left join pessoajuridica pj on pj.id = cp.pessoa_id
      left join pessoafisica pf on pf.id = cp.pessoa_id
      inner join exercicio e on vd.exercicio_id = e.id
      inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id
      inner join opcaopagamento op on op.id = pvd.opcaopagamento_id
      inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id
    where spvd.situacaoparcela <> 'ISOLAMENTO'
