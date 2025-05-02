    create or replace force view vwconsultadividaativa
    as
    select distinct
           c.cadastro_id id_cadastro,
           coalesce(ci.inscricaocadastral, coalesce(ce.inscricaocadastral, cr.numeroincra)) cadastro,
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
           case
              when ce.id is not null then 'ECONOMICO'
              when ci.id is not null then 'IMOBILIARIO'
              when cr.id is not null then 'RURAL'
              else 'PESSOA'
           end tipo_cadastro,
           coalesce(pf.cpf, pj.cnpj) cpfcnpj,
           coalesce(pf.nome, pj.razaosocial) nomerazaosocial,
           da.numero numero_inscricao,
           exinsc.ano ano_inscricao,
           cda.numero numero_cda,
           pcj.numeroprocessoforum numero_ajuizamento,
           excda.ano exercicio_cda
       from iteminscricaodividaativa ida
      inner join inscricaodividaativa da on ida.inscricaodividaativa_id = da.id
      inner join exercicio exinsc on exinsc.id = da.exercicio_id
      left join itemcertidaodividaativa icda on icda.iteminscricaodividaativa_id = ida.id
      left join certidaodividaativa cda on cda.id = icda.certidao_id
      left join exercicio excda on excda.id = cda.exercicio_id
      left join processojudicialcda pjcda on pjcda.certidaodividaativa_id = cda.id
      left join processojudicial pcj on pcj.id = pjcda.processojudicial_id
      inner join calculo c on c.id = ida.id
      inner join calculopessoa cp on cp.calculo_id = c.id
      left join pessoajuridica pj on pj.id = cp.pessoa_id
      left join pessoafisica pf on pf.id = cp.pessoa_id
      left join cadastroeconomico ce on c.cadastro_id = ce.id
      left join cadastroimobiliario ci on c.cadastro_id = ci.id
      left join cadastrorural cr on c.cadastro_id = cr.id
      inner join valordivida vd on vd.calculo_id = c.id
      inner join exercicio e on e.id = vd.exercicio_id
      inner join divida d on d.id = vd.divida_id
      inner join dividaacrescimo dva on dva.divida_id = d.id
      inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id
      inner join opcaopagamento op on op.id = pvd.opcaopagamento_id
      inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id
    where spvd.situacaoparcela <> 'ISOLAMENTO'
