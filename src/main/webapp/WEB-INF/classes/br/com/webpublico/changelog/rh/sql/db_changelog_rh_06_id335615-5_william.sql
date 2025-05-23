update contratofp
set segmentoatuacao = 'PRE_ESCOLA'
where id in (
    select distinct vinculo.id
    from vinculofp vinculo
             inner join contratofp contrato on vinculo.ID = contrato.ID
             inner join LOTACAOFUNCIONAL lotacao on vinculo.ID = lotacao.VINCULOFP_ID
    where to_date('01/2021', 'MM/yyyy') between lotacao.INICIOVIGENCIA and coalesce (lotacao.FINALVIGENCIA, to_date('01/2021', 'MM/yyyy'))
      and lotacao.UNIDADEORGANIZACIONAL_ID in
          (58758540
              )
);

update contratofp
set segmentoatuacao = 'FUNDAMENTAL_1'
where id in (
    select distinct vinculo.id
    from vinculofp vinculo
             inner join contratofp contrato on vinculo.ID = contrato.ID
             inner join LOTACAOFUNCIONAL lotacao on vinculo.ID = lotacao.VINCULOFP_ID
    where to_date('01/2021', 'MM/yyyy') between lotacao.INICIOVIGENCIA and coalesce (lotacao.FINALVIGENCIA, to_date('01/2021', 'MM/yyyy'))
      and lotacao.UNIDADEORGANIZACIONAL_ID in
          (58758578)
);
