CREATE OR REPLACE PACKAGE BODY pacote_parcelamento AS

FUNCTION get_cdas_parcelamento(id_parcelamento number)
    RETURN cda_parcelamento_table
    PIPELINED IS
    rec cda_parcelamento_record;

    CURSOR CDAS IS
     with cteparcelamento(id_parcelamento, id_parcela_original) as (
      select parcelamento.id as id_parcelamento, pvdoriginal.id as id_parcela_original
         from processoparcelamento parcelamento
        inner join itemprocessoparcelamento itemparcelamento on itemparcelamento.PROCESSOPARCELAMENTO_ID = parcelamento.id
        inner join parcelavalordivida pvdoriginal on pvdoriginal.id = itemparcelamento.PARCELAVALORDIVIDA_ID
        inner join valordivida vdoriginal on vdoriginal.id = pvdoriginal.valordivida_id
        inner join calculo on calculo.id = vdoriginal.calculo_id
      where parcelamento.id = id_parcelamento
      union all
      select parcelamento.id as id_parcelamento, pvdoriginal.id as id_parcela_original
      from parcelavalordivida pvd
      inner join CTEPARCELAMENTO cte on cte.id_parcela_original = pvd.id
      left join valordivida vd on vd.id = pvd.valordivida_id
      left join processoparcelamento parcelamento on parcelamento.id = vd.CALCULO_ID
      left join ITEMPROCESSOPARCELAMENTO itemparcelamento on itemparcelamento.PROCESSOPARCELAMENTO_ID = parcelamento.id
      left join parcelavalordivida pvdoriginal on pvdoriginal.id = itemparcelamento.PARCELAVALORDIVIDA_ID
      left join valordivida vdoriginal on vdoriginal.id = pvdoriginal.valordivida_id
      inner join calculo on calculo.id = vdoriginal.calculo_id)
      select distinct cte.id_parcelamento, cda.numero numero_cda, e.ano ano_cda,
       pj.numeroprocessoforum numero_ajuizamento into rec
         from CTEPARCELAMENTO cte
      inner join parcelavalordivida pvd on pvd.id = cte.id_parcela_original
      inner join valordivida vd on pvd.valordivida_id = vd.id
      inner join ITEMINSCRICAODIVIDAATIVA itemda on itemda.id = vd.calculo_id
      inner join ITEMCERTIDAODIVIDAATIVA itemcda on itemcda.iteminscricaodividaativa_id = itemda.id
      inner join CERTIDAODIVIDAATIVA cda on cda.id = itemcda.certidao_id
      inner join exercicio e on e.id = cda.exercicio_id
      left join processojudicialcda pjcda on pjcda.certidaodividaativa_id = cda.id
      left join processojudicial pj on pj.id = pjcda.processojudicial_id
      where cda.id is not null;
BEGIN
    FOR CDA in CDAS
    LOOP
       rec.id_parcelamento := CDA.id_parcelamento;
       rec.numero_cda := cda.numero_cda;
       rec.ano_cda := cda.ano_cda;
       rec.numero_ajuizamento := cda.numero_ajuizamento;
       PIPE ROW (rec);
    END LOOP;

    RETURN;
END get_cdas_parcelamento;
END pacote_parcelamento;
